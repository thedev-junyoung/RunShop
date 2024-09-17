package com.example.runshop.utils.mapper;

import com.example.runshop.model.dto.user.UserDTO;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.vo.user.Address;
import com.example.runshop.model.vo.user.Email;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "address", target = "address")
    UserDTO userToUserDTO(User user);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(source = "email", target = "email")
    @Mapping(source = "address", target = "address")
    User userDTOToUser(UserDTO userDTO);


    default String emailToString(Email email) {
        return email.getEmailValue();
    }

    default Email stringToEmail(String email) {
        return new Email(email);
    }

    default String addressToString(Address address) {
        return address.toString();
    }

    default Address stringToAddress(String address) {
        String[] parts = address.split(", ");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid address format");
        }
        return Address.builder()
                .street(parts[0])
                .detailedAddress(parts[1])
                .city(parts[2])
                .region(parts[3])
                .zipCode(parts[4])
                .build();
    }
}
