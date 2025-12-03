package app.web.dto;

import app.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DtoMapperUTest {

    @Test
    void fromUserToEditProfileRequest_whenUserWithDetailsIsPassed_thenDtoIsReturnedWithSameDetails() {

        User user = User.builder()
                .firstName("Vladimir")
                .lastName("Shalamanov")
                .city("Sofia")
                .address("ul. Vladimir Shalamanov 38")
                .build();

        EditUserProfileRequest result = DtoMapper.fromUser(user);

        assertEquals("Vladimir", result.getFirstName());
        assertEquals("Shalamanov", result.getLastName());
        assertEquals("Sofia", result.getCity());
        assertEquals("ul. Vladimir Shalamanov 38", result.getAddress());
    }
}