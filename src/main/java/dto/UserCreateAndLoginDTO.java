package dto;

import lombok.Builder;

@Builder
public record UserCreateAndLoginDTO(
        String email,
        String password
){}
