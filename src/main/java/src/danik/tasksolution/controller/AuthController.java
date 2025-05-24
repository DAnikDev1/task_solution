package src.danik.tasksolution.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import src.danik.tasksolution.entity.TelegramUser;
import src.danik.tasksolution.service.AuthService;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(path = "/auth", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> initTelegramData(
            @RequestBody String initData,
            HttpServletResponse response
    ) {
        TelegramUser user = authService.validate(initData);
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Telegram initData");
        }
        String authToken = authService.signUser(user);

        Cookie cookie = new Cookie("auth_token", authToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }
}
