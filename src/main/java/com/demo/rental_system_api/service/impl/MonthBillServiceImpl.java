package com.demo.rental_system_api.service.impl;

import com.demo.rental_system_api.model.*;
import com.demo.rental_system_api.model.constants.RoomStatus;
import com.demo.rental_system_api.repository.*;
import com.demo.rental_system_api.service.MonthBillService;
import com.demo.rental_system_api.service.utils.MappingHelper;
import com.demo.rental_system_api.web.dto.*;
import com.demo.rental_system_api.web.dto.request.CreateMonthBillRequest;
import com.demo.rental_system_api.web.exception.EntityNotFoundException;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class MonthBillServiceImpl implements MonthBillService {
    private final MonthBillRepository monthBillRepository;
    private final RoomRepository roomRepository;
    private final ContractRepository contractRepository;
    private final UsedServiceRepository usedServiceRepository;
    private final ServiceRepository serviceRepository;
    private final liquidationRepository liquidationRepo;
    private final MappingHelper mappingHelper;

    private boolean checkMonthBillByTime(Date timeDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeDate);
        calendar.add(Calendar.MONTH, -1);
        Date previousMonthDate = calendar.getTime();
        System.out.println("start_date: "+previousMonthDate);
        System.out.println("end_date: "+timeDate);
        List<MonthBill> listBill = monthBillRepository.findBillsByTime(previousMonthDate,timeDate);
        if(listBill.isEmpty()){
            return true;
        }
        return false;
    }

    @Override
    public List<MonthBillDto> getBillOfContract(Integer contractId) {
        return monthBillRepository
                .findByContract_Id(contractId)
                .stream()
                .map(this::mapToMonthBillDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean PaymentBill(Integer BillId) throws Exception {
        Optional<MonthBill> bill= monthBillRepository.findById(BillId);
        if (bill.isPresent()){
            bill.get().setStatusPayment(true);
            monthBillRepository.save(bill.get());
            return true;
        }
        else {
//            throw new Exception("Bill not found");
            return false;
        }

    }

    @Override
    public MonthBillDto getBillById(Integer billId) {
        return monthBillRepository
                .findById(billId)
                .map(this::mapToMonthBillDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        MonthBill.class.getName(),
                        billId.toString()
                ));
    }




    @Override
    public liquidationDto createBill(CreateMonthBillRequest createMonthBillRequest) {
        var contract = contractRepository
                .findById(createMonthBillRequest.getContractId())
                .orElseThrow(() -> new EntityNotFoundException(
                        Contract.class.getName(),
                        createMonthBillRequest.getContractId().toString()
                ));
        List<liquidation> liquiList = liquidationRepo.findByContract_Id(contract.getId());
        if (!liquiList.isEmpty()) {
            throw new RuntimeException("Hợp đồng đã được thanh lý, hãy tạo hợp đồng mới để thanh toán");
        }


        var monthBill = mappingHelper.map(createMonthBillRequest, MonthBill.class);
        monthBill.setContract(contract);

        var usedServices = serviceRepository
                .findAllById(
                        createMonthBillRequest.getUsedServiceRequests()
                                .stream()
                                .map(CreateMonthBillRequest.UsedServiceRequest::getServiceId)
                                .collect(Collectors.toList()))
                .stream()
                .map(service -> {
                    var usedServiceReq = createMonthBillRequest
                            .getUsedServiceRequests()
                            .stream().filter(e -> e.getServiceId().equals(service.getId()))
                            .findFirst();
                    if (usedServiceReq.isPresent()) {
                        var usedService = mappingHelper.map(usedServiceReq.get(), UsedService.class);
                        usedService.setService(service);
                        usedService.setMonthBill(monthBill);
                        usedService.setTotalPrice(service.getPrice() * usedServiceReq.get().getQuantity());
                        return usedService;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        usedServiceRepository.saveAll(usedServices);

        var totalPriceBill = contract.getRoom().getPrice()
                - contract.getRoomDeposit()
                + usedServices.stream().flatMapToDouble(e -> DoubleStream.of(e.getTotalPrice())).sum();

        monthBill.setTotalPrice((float) totalPriceBill);
//        monthBillRepository.save(monthBill);
        liquidation liqui = new liquidation();
        liqui.setContract(monthBill.getContract());
        liqui.setDueDate(monthBill.getDueDate());
        liqui.setPaymentDate(monthBill.getPaymentDate());
        liqui.setNote(monthBill.getNote());
        liqui.setTotalPrice(monthBill.getTotalPrice());
        liquidationRepo.save(liqui);
        Room room= contract.getRoom();
        room.setRoomStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);
        return mappingHelper.map(liqui,liquidationDto.class);
    }


    @Override
    public MonthBillDto createMonthlyBill(CreateMonthBillRequest createMonthBillRequest) throws Exception  {

        var contract = contractRepository
                .findById(createMonthBillRequest.getContractId())
                .orElseThrow(() -> new EntityNotFoundException(
                        Contract.class.getName(),
                        createMonthBillRequest.getContractId().toString()
                ));
        List<liquidation> liquiList = liquidationRepo.findByContract_Id(contract.getId());
        if (!liquiList.isEmpty()) {
            throw new RuntimeException("Hợp đồng đã được thanh lý, hãy tạo hợp đồng mới để thanh toán");
        }

        if (contract.getDueDate().before(new Date())) {
            throw new RuntimeException("Hợp đồng đã hết hạn, vui lòng thanh lý");
        }

        var monthBill = mappingHelper.map(createMonthBillRequest, MonthBill.class);
        monthBill.setContract(contract);

        var usedServices = serviceRepository
                .findAllById(
                        createMonthBillRequest.getUsedServiceRequests()
                                .stream()
                                .map(CreateMonthBillRequest.UsedServiceRequest::getServiceId)
                                .collect(Collectors.toList()))
                .stream()
                .map(service -> {
                    var usedServiceReq = createMonthBillRequest
                            .getUsedServiceRequests()
                            .stream().filter(e -> e.getServiceId().equals(service.getId()))
                            .findFirst();
                    if (usedServiceReq.isPresent()) {
                        var usedService = mappingHelper.map(usedServiceReq.get(), UsedService.class);
                        usedService.setService(service);
                        usedService.setMonthBill(monthBill);
                        usedService.setTotalPrice(service.getPrice() * usedServiceReq.get().getQuantity());
                        return usedService;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        usedServiceRepository.saveAll(usedServices);

        var totalPriceBill = contract.getRoom().getPrice()+ usedServices.stream().flatMapToDouble(e -> DoubleStream.of(e.getTotalPrice())).sum();

        monthBill.setTotalPrice((float) totalPriceBill);
        monthBillRepository.save(monthBill);

        return mapToMonthBillDto(monthBill);
    }





    private MonthBillDto mapToMonthBillDto(MonthBill monthBill) {
//        System.out.println(monthBill.getStatusPayment());
        var monthBillDto = mappingHelper.map(monthBill, MonthBillDto.class);
        monthBillDto.setContractDto(mapToContractDto(monthBill.getContract()));

        var usedServiceDtoList = usedServiceRepository
                .findByMonthBill_Id(monthBill.getId())
                .stream()
                .map(this::mapDataToUsedServiceDto)
                .collect(Collectors.toList());

        monthBillDto.setUsedServiceDtoList(usedServiceDtoList);

        return monthBillDto;
    }

    private ContractDto mapToContractDto(Contract contract) {
        var contractDto = mappingHelper.map(contract, ContractDto.class);
        var roomDto = mappingHelper.map(contract.getRoom(), RoomDto.class);
        roomDto.setBuildingDto(mappingHelper.map(contract.getRoom().getBuilding(), BuildingDto.class));
        contractDto.setRoomDto(roomDto);
        contractDto.setClientDto(mappingHelper.map(contract.getClient(), ClientDto.class));
        return contractDto;
    }

    private MonthBillDto.UsedServiceDto mapDataToUsedServiceDto(UsedService usedService) {
        var usedServiceDto = mappingHelper.map(usedService, MonthBillDto.UsedServiceDto.class);
        usedServiceDto.setServiceDto(mappingHelper.map(usedService.getService(), ServiceDto.class));
        return usedServiceDto;
    }


}
