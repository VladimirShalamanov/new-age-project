package app.web.dto;

import app.user.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static EditUserProfileRequest fromUser(User user) {

        return EditUserProfileRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .city(user.getCity())
                .address(user.getAddress())
                .build();
    }
}