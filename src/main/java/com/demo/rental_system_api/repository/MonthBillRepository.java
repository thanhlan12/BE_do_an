package com.demo.rental_system_api.repository;

import com.demo.rental_system_api.model.MonthBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface MonthBillRepository extends JpaRepository<MonthBill, Integer> {
    List<MonthBill> findByContract_Id(Integer contractId);
    @Query("SELECT b FROM MonthBill b WHERE b.dueDate >= :startDate AND b.dueDate < :endDate")
    List<MonthBill> findBillsByTime(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
