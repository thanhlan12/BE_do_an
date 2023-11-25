package com.demo.rental_system_api.web.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class liquidationDto {
    private Integer id;
    private Date dueDate;
    private Date paymentDate;
    private Float totalPrice;
    private String note;
    private boolean statusPayment;

    private ContractDto contractDto;
    private List<MonthBillDto.UsedServiceDto> usedServiceDtoList;

    @Data
    public static class UsedServiceDto {
        private ServiceDto serviceDto;
        private Integer quantity;
        private Float totalPrice;
    }

    public boolean getStatusPayment() {
        return statusPayment;
    }

    public void setStatusPayment(boolean statusPayment) {
        this.statusPayment = statusPayment;
    }


}
