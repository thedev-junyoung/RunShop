package com.example.runshop.utils.mapper;

import com.example.runshop.model.dto.user.UserDTO;
import com.example.runshop.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // User -> UserDTO 변환
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    UserDTO userToUserDTO(User user);

    // UserDTO -> User 변환
    @Mapping(target = "createdAt", ignore = true) // 생성 시점은 엔티티에서 처리
    @Mapping(target = "updatedAt", ignore = true) // 업데이트 시점은 엔티티에서 처리
    User userDTOToUser(UserDTO userDTO);
}
