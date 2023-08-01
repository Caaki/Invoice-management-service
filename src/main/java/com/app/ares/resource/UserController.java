package com.app.ares.resource;

import com.app.ares.configuration.security.provider.TokenProvider;
import com.app.ares.domain.HttpResponse;
import com.app.ares.domain.User;
import com.app.ares.domain.UserPrincipal;
import com.app.ares.dto.UserDTO;
import com.app.ares.dtomapper.UserDTOMapper;
import com.app.ares.exception.ApiException;
import com.app.ares.form.LoginForm;
import com.app.ares.service.RoleService;
import com.app.ares.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import static com.app.ares.utils.ExceptionUtils.processError;
import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RoleService roleService;



    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm){
        Authentication authentication = authenticate(loginForm.getEmail(),loginForm.getPassword());
        UserDTO userDTO = getAuthenticatedUser(authentication);
        return userDTO.isUsingMfa() ? sendVerificationCode(userDTO): sendResponse(userDTO);
    }

    private UserDTO getAuthenticatedUser(Authentication authentication){
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }




    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user){
        UserDTO userDto = userService.createUser(user);
        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userDto))
                        .message("User created")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build());
    }


    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code){
        UserDTO userDTO = userService.verifyCode(email,code);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", userDTO,
                                "access_token", tokenProvider.createAccessToken(getUserPrincipal(userDTO)),
                                "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(userDTO))))
                        .message("Login Success")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/profile")
    public ResponseEntity<HttpResponse> profile(Authentication authentication){
        UserDTO userDTO = userService.getUserByEmail(authentication.getName());
        System.out.println(authentication);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", userDTO))
                        .message("Profile page opened")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword (@PathVariable("email") String email){
        userService.resetPassword(email);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("Email sent. Please check your email to reset your password.")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/verify/password/{key}")
    public ResponseEntity<HttpResponse> verifyPasswordUrlNotLoggedIn (@PathVariable("key") String key){
        UserDTO userDTO = userService.verifyPasswordKey(key);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(Map.of("user",userDTO))
                        .message("Please enter a new password")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }


    @PostMapping("/resetpassword/{key}/{password}/{confirmation}")
    public ResponseEntity<HttpResponse> resetPasswordWithKeyNotLoggedIn(
            @PathVariable("key") String key,
            @PathVariable("password") String password,
            @PathVariable("confirmation") String confirmation){
        userService.renewPassword(key, password, confirmation);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("Password has been reset successfully.")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }


    @RequestMapping("/error")
    public ResponseEntity<HttpResponse> handleError(HttpServletRequest request){
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .reason("There is no mapping for the given rout with the method of type" + " [" + request.getMethod() +"]")
                        .status(NOT_FOUND)
                        .statusCode(NOT_FOUND.value())
                        .build(), NOT_FOUND);
    }





    private URI getUri() {
        return URI.create(
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/user/get/<userId>").toUriString());
    }


    private ResponseEntity<HttpResponse> sendResponse(UserDTO userDTO) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", userDTO,
                            "access_token", tokenProvider.createAccessToken(getUserPrincipal(userDTO)),
                                "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(userDTO))))
                        .message("Login Success")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());

    }

    private UserPrincipal getUserPrincipal(UserDTO userDTO) {
        return new UserPrincipal(UserDTOMapper.toUser(userService.getUserByEmail(userDTO.getEmail())),
                roleService.getRoleByUserId(userDTO.getId()));
    }

    private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO userDTO) {

        userService.sendVerificationCode(userDTO);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", userDTO))
                        .message("Verification code sent")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    private Authentication authenticate(String email, String password) {
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
            return authentication;
        }catch (Exception e ){
            processError(request,response,e);
            throw new ApiException(e.getMessage());
        }
    }


}
