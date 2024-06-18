package com.authentication.mapper;

import com.authentication.model.dto.UserDto;
import com.authentication.model.dto.UserEventDto;
import com.authentication.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User mapToUserEntity(UserDto userDto);
    UserEventDto mapToUserEventDto(User user);
    UserDto mapToUserDto(User user);
}
