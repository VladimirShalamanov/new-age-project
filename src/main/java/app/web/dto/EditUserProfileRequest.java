package app.web.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditUserProfileRequest {

    @Size(min = 2, max = 24)
    private String firstName;

    @Size(min = 2, max = 24)
    private String lastName;

    @Size(min = 4, max = 30)
    private String city;

    @Size(min = 10, max = 40)
    private String address;
}