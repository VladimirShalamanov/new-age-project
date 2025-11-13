package app.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank
    @Size(min = 6, max = 26, message = "Username length must be between 6 and 24 symbols.")
    String username;

    @NotBlank
    @Size(min = 4, max = 10, message = "Password length must be between 4 and 10 symbols.")
    String password;
}
