package com.demo.rental_system_api.web.dto;

import com.demo.rental_system_api.model.Room;
import lombok.Data;

import java.util.Date;

@Data
public class ContractDto {
    private Integer id;
    private Date createdAt;
    private String note;
    private Date startDate;
    private Date dueDate;
    private Float roomDeposit;

    private ClientDto clientDto;
    private Room room;
}
