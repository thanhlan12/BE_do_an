package com.demo.rental_system_api.service;

import com.demo.rental_system_api.web.dto.MonthBillDto;
import com.demo.rental_system_api.web.dto.liquidationDto;
import com.demo.rental_system_api.web.dto.request.CreateMonthBillRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface MonthBillService {



    List<MonthBillDto> getBillOfContract(Integer contractId);

    boolean PaymentBill(Integer BillId) throws Exception;

    MonthBillDto getBillById(Integer billId);

    liquidationDto createBill(CreateMonthBillRequest createMonthBillRequest);

    @Transactional
    MonthBillDto createMonthlyBill(CreateMonthBillRequest createMonthBillRequest) throws Exception;
}
