package app.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(min = 6, max = 20, message = "Username length must be between 6 and 20 symbols.")
    private String username;

    @NotBlank
    @Size(min = 4, max = 10, message = "Password length must be between 4 and 10 symbols.")
    private String password;

    @NotBlank
    private String repeatPassword;

    @NotBlank
    @Email
    private String email;
}