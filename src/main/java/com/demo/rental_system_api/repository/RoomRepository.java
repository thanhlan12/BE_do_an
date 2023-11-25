package com.demo.rental_system_api.repository;

import com.demo.rental_system_api.model.Building;
import com.demo.rental_system_api.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> getRoomByBuilding(Building building);
}
