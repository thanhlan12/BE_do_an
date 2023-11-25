package com.demo.rental_system_api.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Data
@Entity
@Table(name = "liquidationBill")
public class liquidation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Date dueDate;
    private Date paymentDate;
    private Float totalPrice;
    private String note;
    private boolean statusPayment;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;
    public boolean getStatusPayment() {
        return statusPayment;
    }

    public void setStatusPayment(boolean statusPayment) {
        this.statusPayment = statusPayment;
    }
}
