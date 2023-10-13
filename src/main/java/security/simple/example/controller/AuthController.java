package security.simple.example.controller;

import security.simple.example.config.utils.TokenPair;
import security.simple.example.controller.dto.AppError;
import security.simple.example.controller.dto.CreatedUserDto;
import security.simple.example.controller.dto.token.JwtRequest;
import security.simple.example.controller.dto.token.TokenPairResponse;
import security.simple.example.controller.dto.token.TokenRefreshRequest;
import security.simple.example.controller.dto.token.TokenRefreshResponse;
import security.simple.example.controller.dto.UserRegistrationDto;
import security.simple.example.model.RefreshToken;
import security.simple.example.model.User;
import security.simple.example.service.RefreshTokenService;
import security.simple.example.service.UserService;
import security.simple.example.config.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService tokenService;
    private final UserService userService;
    private final JwtUtils jwtUtils;


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return tokenService
                .findByToken(requestRefreshToken)
                .map(tokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(jwtUtils::generateAuthToken)
                .map(refreshToken -> new TokenRefreshResponse(refreshToken, requestRefreshToken))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RefreshTokenService.TokenRefreshException(requestRefreshToken, "Refresh token not found"));
    }

    @PostMapping("/auth")
    public ResponseEntity<?> createToken(@RequestBody JwtRequest authRequest) {
        String username = authRequest.getUsername();
        String password = authRequest.getPassword();

        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authenticate = authenticationManager.authenticate(token);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Bad credentials"),
                    HttpStatus.UNAUTHORIZED);
        }

        User user = userService
                .findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        TokenPair pair = jwtUtils.generateTokenPair(user);
        try {
            tokenService.updateOrSaveRefreshToken(user.getId(), pair.getRefreshToken());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new TokenPairResponse(pair), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDto regDto) {
        if (!regDto.getPassword().equals(regDto.getPasswordConfirmed())) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(), "Bad req"),
                    HttpStatus.BAD_REQUEST);
        }

        if (userService.findByUserName(regDto.getUsername()).isPresent()) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(), "Name already exists"),
                    HttpStatus.BAD_REQUEST);
        }

        CreatedUserDto newUser = userService.createNewUser(regDto);
        TokenPair pair = jwtUtils.generateTokenPair(newUser);

        try {
            tokenService.updateOrSaveRefreshToken(newUser.getId(), pair.getRefreshToken());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new TokenPairResponse(pair), HttpStatus.OK);
    }
}
