package daraFactory;

import dto.UserCreateAndLoginDTO;
import dto.UserDTO;

public class ReqresDataFactory {

    public UserDTO setUser(String name, String job) {
        return UserDTO.builder()
                .name(name)
                .job(job)
                .build();
    }

    public UserCreateAndLoginDTO setRegister(String email, String password) {
        return UserCreateAndLoginDTO.builder()
                .email(email)
                .password(password)
                .build();
    }
}