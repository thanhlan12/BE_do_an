package com.demo.rental_system_api.service;

import com.demo.rental_system_api.model.Room;
import com.demo.rental_system_api.web.dto.BuildingDto;
import com.demo.rental_system_api.web.dto.RoomDto;
import com.demo.rental_system_api.web.dto.request.CreateRoomRequest;
import com.demo.rental_system_api.web.dto.request.UpdateRoomRequest;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    List<Room> getAllRooms();

    List<BuildingDto> getAllBuildings();

    List<RoomDto> getRoomByBuilding(Integer buildingId);

    Optional<Room> getRoomById(Integer roomId);

    RoomDto createRoom(CreateRoomRequest createRoomRequest) throws Exception;

    RoomDto updateRoom(Integer roomId, UpdateRoomRequest updateRoomRequest);

    Integer deleteRoom(Integer roomId);
}
