package com.demo.rental_system_api.service.impl;

import com.demo.rental_system_api.model.Building;
import com.demo.rental_system_api.model.Room;
import com.demo.rental_system_api.model.constants.RoomStatus;
import com.demo.rental_system_api.repository.BuildingRepository;
import com.demo.rental_system_api.repository.RoomRepository;
import com.demo.rental_system_api.service.RoomService;
import com.demo.rental_system_api.service.utils.MappingHelper;
import com.demo.rental_system_api.web.dto.BuildingDto;
import com.demo.rental_system_api.web.dto.RoomDto;
import com.demo.rental_system_api.web.dto.request.CreateRoomRequest;
import com.demo.rental_system_api.web.dto.request.UpdateRoomRequest;
import com.demo.rental_system_api.web.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final BuildingRepository buildingRepository;
    private final MappingHelper mappingHelper;

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public List<BuildingDto> getAllBuildings() {
        return buildingRepository
                .findAll()
                .stream()
                .map(this::mapToBuildingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomDto> getRoomByBuilding(Integer buildingId) {
        Optional<Building> building = buildingRepository.findById(buildingId);

        if (building.isPresent()) {
            List<Room> rooms = roomRepository.getRoomByBuilding(building.get());

            return rooms.stream()
                    .map(room -> mappingHelper.map(room, RoomDto.class))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>(); // Return an empty list if building is not found
    }


    @Override
    public Optional<Room> getRoomById(Integer roomId) {
        return roomRepository.findById(roomId);
    }

    @Override
    public RoomDto createRoom(CreateRoomRequest createRoomRequest) throws Exception {
        Optional<Building> building = buildingRepository.findById(createRoomRequest.getBuildingRequest().getId());

        var room = mappingHelper.map(createRoomRequest, Room.class);
        if(building.isPresent()){
            room.setBuilding(building);
        }else {
            throw new Exception("tòa nhà không tồn tại");
        }

        room.setRoomStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        return mapToRoomDto(room);
    }


    @Override
    @Transactional
    public RoomDto updateRoom(Integer roomId, UpdateRoomRequest updateRoomRequest) {
        var room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException(
                        Room.class.getName(),
                        roomId.toString()
                ));

        Optional<Building> building = buildingRepository.findById(updateRoomRequest.getBuildingRequest().getId());
        if (building.isPresent()) {
            room.setBuilding(building);
        } else {
            throw new IllegalArgumentException("Invalid building ID: " + updateRoomRequest.getBuildingRequest().getId());
        }
        mappingHelper.mapIfSourceNotNullAndStringNotBlank(updateRoomRequest, room);
       // room.setBuilding(building);
        roomRepository.save(room);

        return mapToRoomDto(room);
    }

    @Override
    @Transactional
    public Integer deleteRoom(Integer roomId) {
        var room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException(
                        Room.class.getName(),
                        roomId.toString()
                ));
        roomRepository.delete(room);
        return roomId;
    }

    private RoomDto mapToRoomDto(Room room) {
        var roomDto = mappingHelper.map(room, RoomDto.class);
//        roomDto.setBuildingDto(mapToBuildingDto(room.getBuilding()));
        return roomDto;
    }

    private BuildingDto mapToBuildingDto(Building building) {
        var buildingDto = mappingHelper.map(building, BuildingDto.class);
        return buildingDto;
    }
}
