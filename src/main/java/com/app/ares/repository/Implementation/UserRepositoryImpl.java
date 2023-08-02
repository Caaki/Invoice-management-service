package com.app.ares.repository.Implementation;

import com.app.ares.domain.Role;
import com.app.ares.domain.User;
import com.app.ares.domain.UserPrincipal;
import com.app.ares.dto.UserDTO;
import com.app.ares.enumeration.VerificationType;
import com.app.ares.exception.ApiException;
import com.app.ares.form.UpdateForm;
import com.app.ares.repository.RoleRepository;
import com.app.ares.repository.UserRepository;
import com.app.ares.rowmapper.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static com.app.ares.enumeration.RoleType.ROLE_USER;
import static com.app.ares.enumeration.VerificationType.ACCOUNT;
import static com.app.ares.enumeration.VerificationType.PASSWORD;
import static com.app.ares.query.UserQuery.*;
import static com.app.ares.utils.SmsUtils.sendSMS;
import static java.util.Map.of;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public User create(User user) {
        // Check the if the email is unique
        if (getEmailCount(user.getEmail().trim().toLowerCase()) > 0)
            throw new ApiException("Email already in use. Please use a different email");
        // Save new user
        try{
            KeyHolder holder = new GeneratedKeyHolder();
            SqlParameterSource parameters = getSqlParameterSource(user);
            jdbcTemplate.update(INSERT_USER_QUERY,parameters,holder);
            user.setId(requireNonNull(holder.getKey()).longValue());
        // Add a role to the user
            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());

        // Save verification Url
            jdbcTemplate.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY, Map.of("userId",user.getId(),"url",verificationUrl));

        //Send verification url
        //emailService.sendVerificationUrl(user.getFirstName(), user.getEmail(), verificationUrl, ACCOUNT);
            user.setEnabled(false);
            user.setNotLocked(true);

        // Return the new user
            return user;

       }catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }
    }




    @Override
    public Collection<User> list(int page, int pageSize) {
        return null;
    }

    @Override
    public User get(Long id) {
        try{
            return   jdbcTemplate.queryForObject(SELECT_USER_BY_ID_QUERY, Map.of("id", id), new UserRowMapper());

        }catch (EmptyResultDataAccessException e){
            log.error(e.getMessage());
            throw new ApiException("No user found by id: "+ id);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        if(user == null){
            log.error("User is not found in the database");
            throw new UsernameNotFoundException("User is not found in the database");
        }else{
            log.info("User found in the database: {}",email);
            return new UserPrincipal(user,roleRepository.getRoleByUserId(user.getId()));
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try{
            User user =  jdbcTemplate.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email",email), new UserRowMapper());
            return user;

        }catch (EmptyResultDataAccessException e){
            log.error(e.getMessage());
            throw new ApiException("No user found by email: "+ email);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }
    }

    @Override
    public void sendVerificationCode(UserDTO user) {

        LocalDateTime expirationDateTime = LocalDateTime.now().plusMinutes(15);
        String expirationDate = expirationDateTime.format(DATE_FORMAT);

        String verificationCode = randomAlphabetic(8).toUpperCase();
        try{
            jdbcTemplate.update(DELETE_VERIFICATION_CODE_BY_USER_ID, Map.of("id",user.getId()));
            jdbcTemplate.update(INSERT_VERIFICATION_CODE_QUERY, Map.of("userId",user.getId(),"code",verificationCode, "expirationDate", expirationDate));
            //sendSMS(user.getPhone(), "From: Ares \nVerification code\n"+verificationCode);
            log.info("Verification code: {}",verificationCode);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }

    }

    @Override
    public User verifyCode(String email, String code) {
        if (isVerificationCodeExpired(code))
            throw new ApiException("This code expired. Please log in again");
        try{
            log.info("Na pocetku je metode je userByCode");
            User userByCode = jdbcTemplate.queryForObject(
                    SELECT_USER_BY_USER_CODE_QUERY,
                    Map.of("code", code), new UserRowMapper());


            User userByEmail = jdbcTemplate.queryForObject(
                    SELECT_USER_BY_EMAIL_QUERY,
                    Map.of("email",email), new UserRowMapper());
            if (requireNonNull(userByEmail).getEmail().equalsIgnoreCase(requireNonNull(userByCode).getEmail())){
                log.info("U ifu je");
                jdbcTemplate.update(DELETE_CODE, Map.of("code", code));

                return userByCode;
            }
            else{
                throw new ApiException("Code is invalid. Please try again.");
            }
        }catch (EmptyResultDataAccessException e){
            throw new ApiException("Could not find record");
        }catch (Exception e) {
            log.error("Error in verifyCode() at UserRepositoryImpl");
            throw new ApiException("An error occurred. Please try again");
        }
    }

    private Integer getEmailCount(String email) {
        return jdbcTemplate.queryForObject(
                COUNT_USER_EMAIL_QUERY,
                of("email", email), Integer.class);
    }

    private SqlParameterSource getSqlParameterSource(User user) {

        return new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", passwordEncoder.encode(user.getPassword()));
    }

    private SqlParameterSource getSqlParameterSource(UpdateForm user) {

        return new MapSqlParameterSource()
                .addValue("id",user.getId())
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                //.addValue("password", passwordEncoder.encode(user.getPassword()))
                .addValue("address",user.getAddress())
                .addValue("phone",user.getPhone())
                .addValue("title",user.getTitle())
                .addValue("bio",user.getBio())
                ;

    }

    private String getVerificationUrl(String key, String type){
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/user/verify/" + type + "/" + key).toUriString();
    }
    

    @Override
    public void resetPassword(String email) {
        if (getEmailCount(email.trim().trim().toLowerCase()) <= 0){
            throw new ApiException("There is no account for this email address!");
        }
        try {
                LocalDateTime expirationDateTime = LocalDateTime.now().plusDays(1);
                String expirationDate = expirationDateTime.format(DATE_FORMAT);
                User user = getUserByEmail(email);
                String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), PASSWORD.getType());

//                jdbcTemplate.update(DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY,
//                        Map.of("userId", user.getId())
//                );

                jdbcTemplate.update(INSERT_PASSWORD_VERIFICATION_QUERY,
                        Map.of(
                                "userId", user.getId(),
                                "url", verificationUrl,
                                "expirationDate",expirationDate)
                );

                log.info("Verification URL: {}",verificationUrl);


        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User verifyPasswordKey(String key) {
        if(isLinkExpired(key,PASSWORD)){
            throw new ApiException("This link has expired. Please reset your password again.");
        }
        try {
            User user = jdbcTemplate.queryForObject(
                    SELECT_USER_BY_PASSWORD_URL_QUERY,
                    Map.of("url", getVerificationUrl(key,PASSWORD.getType())), new UserRowMapper());
            return user;
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("This url is not valid. Please reset password again.");
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }

    }

    @Override
    public void renewPassword(String key, String password, String confirmation) {
        if (!password.equals(confirmation)){
            throw new ApiException("Passwords do not match, Please try again");
        }
        try {
            jdbcTemplate.update(UPDATE_USER_PASSWORD_BY_URL_QUERY, Map.of(
                    "password",passwordEncoder.encode(password),
                    "url", getVerificationUrl(key,PASSWORD.getType())));

            jdbcTemplate.update(
                    DELETE_VERIFICATION_BY_URL_QUERY,
                    Map.of("url", getVerificationUrl(key,PASSWORD.getType())));
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }

    }

    @Override
    public User verifyAccountCode(String code) {
        try {
            User user = jdbcTemplate.queryForObject(
                    SELECT_USER_BY_ACCOUNT_URL_QUERY,
                    Map.of("url", getVerificationUrl(code,ACCOUNT.getType())), new UserRowMapper());
            jdbcTemplate.update(UPDATE_USER_ENABLED_QUERY, Map.of("enabled",true, "userId", user.getId()));
            return user;
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("This link is not valid.");
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User updateUserDetails(UpdateForm user) {
        try {
            jdbcTemplate.update(
                    UPDATE_USER_DETAILS_QUERY, getSqlParameterSource(user));
            return get(user.getId());
        }catch (EmptyResultDataAccessException e){
            throw new ApiException("No user found by id: "+ user.getId());


        }
        catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("This url is not valid. Please reset password again again.");
        }
    }

    private boolean isLinkExpired(String key, VerificationType password) {

        try {
            return jdbcTemplate.queryForObject(
                    SELECT_EXPIRATION_BY_URL,
                    Map.of("url", getVerificationUrl(key,password.getType())), Boolean.class);
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("This url is not valid. Please reset password again again.");
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    private Boolean isVerificationCodeExpired(String code) {
        try {
            log.info(jdbcTemplate.queryForObject(SELECT_CODE_EXPIRATION_QUERY, Map.of("code", code),String.class));
            log.info(String.valueOf(LocalDateTime.now()));
            return jdbcTemplate.queryForObject(SELECT_CODE_EXPIRATION_QUERY, Map.of("code", code), Boolean.class);
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("This code is not valid. Please try again.");
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }
    }
    
    
}
