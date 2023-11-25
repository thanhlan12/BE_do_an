package com.demo.rental_system_api.repository;

import com.demo.rental_system_api.model.MonthBill;
import com.demo.rental_system_api.model.liquidation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface liquidationRepository extends JpaRepository<liquidation,Integer> {
    List<liquidation> findByContract_Id(Integer contractId);
}
