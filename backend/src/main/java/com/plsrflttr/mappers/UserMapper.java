package com.plsrflttr.mappers;

import com.plsrflttr.dto.UserDto;
import com.plsrflttr.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(UserDto dto);
}

