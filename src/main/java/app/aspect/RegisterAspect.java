package app.aspect;

import app.web.dto.RegisterRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class RegisterAspect {

    private static final List<String> BAD_WORDS = List.of("anonymous", "illegal", "thief");

    @Before("execution(* register(app.web.dto.RegisterRequest)) && args(dto)")
    public void validateRegisterRequest(RegisterRequest dto) {

        String username = dto.getUsername();

        if (BAD_WORDS.contains(username)) {
            throw new IllegalArgumentException("Inappropriate username!");
        }
    }
}