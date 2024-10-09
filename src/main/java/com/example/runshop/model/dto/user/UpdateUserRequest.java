package com.example.runshop.model.dto.user;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateUserRequest extends BaseUserDTO {
}
