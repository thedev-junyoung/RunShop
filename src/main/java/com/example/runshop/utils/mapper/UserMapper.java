package com.example.runshop.utils.mapper;

import com.example.runshop.model.dto.user.UserDTO;
import com.example.runshop.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") // 스프링 빈으로 등록되도록 설정
public interface UserMapper {
    UserDTO userToUserDTO(User user);
    User userDTOToUser(UserDTO userDTO);
}