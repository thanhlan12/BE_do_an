package com.demo.rental_system_api.web.rest;

import com.demo.rental_system_api.service.RoomService;
import com.demo.rental_system_api.web.dto.request.CreateRoomRequest;
import com.demo.rental_system_api.web.dto.request.UpdateRoomRequest;
import com.demo.rental_system_api.web.dto.response.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/rooms")
@CrossOrigin("*")
public class RoomResource {
    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<?> getAllRooms() {
        return ResponseUtils.ok(roomService.getAllRooms());
    }


    @GetMapping("/getAllBuilding")
    public ResponseEntity<?> getAllBuildings() {
        return ResponseUtils.ok(roomService.getAllBuildings());
    }



    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoomById(@PathVariable Integer roomId) {
        return ResponseUtils.ok(roomService.getRoomById(roomId));
    }

    @GetMapping("/findRoom/{buildingId}")
    public ResponseEntity<?> getRoomByBuildingId(@PathVariable Integer buildingId) {
        return ResponseUtils.ok(roomService.getRoomByBuilding(buildingId));
    }

    @PostMapping
    public ResponseEntity<?> createRoom(@Valid @RequestBody CreateRoomRequest createRoomRequest) throws Exception {
        return ResponseUtils.ok(roomService.createRoom(createRoomRequest));
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<?> updateRoom(@Valid @RequestBody UpdateRoomRequest updateRoomRequest, @PathVariable Integer roomId) {
        return ResponseUtils.ok(roomService.updateRoom(roomId, updateRoomRequest));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable Integer roomId) {
        return ResponseUtils.ok(roomService.deleteRoom(roomId));
    }
}
