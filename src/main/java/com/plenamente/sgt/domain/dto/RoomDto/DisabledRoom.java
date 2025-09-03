package com.plenamente.sgt.domain.dto.RoomDto;

public record DisabledRoom(
        Long id,
        String name,
        String address,
        Boolean isTherapeutic
){
}
