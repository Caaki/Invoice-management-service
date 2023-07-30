package com.app.ares.repository.Implementation;

import com.app.ares.domain.Role;
import com.app.ares.domain.User;
import com.app.ares.domain.UserPrincipal;
import com.app.ares.dto.UserDTO;
import com.app.ares.exception.ApiException;
import com.app.ares.repository.RoleRepository;
import com.app.ares.repository.UserRepository;
import com.app.ares.rowmapper.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
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

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static com.app.ares.enumeration.RoleType.ROLE_USER;
import static com.app.ares.enumeration.VerificationType.ACCOUNT;
import static com.app.ares.query.UserQuery.*;
import static com.app.ares.utils.SmsUtils.sendSMS;
import static java.util.Map.of;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.addMinutes;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {
    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
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
        return null;
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
            return new UserPrincipal(user,roleRepository.getRoleByUserId(user.getId()).getPermission());
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

        String expirationDate = DateFormatUtils.format(addMinutes(new Date(),15), DATE_FORMAT);
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

    private String getVerificationUrl(String key, String type){
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/user/verify/" + type + "/" + key).toUriString();
    }

    private boolean isVerificationCodeExpired(String code) {
        try{
            return jdbcTemplate.queryForObject(SELECT_CODE_EXPIRATION_QUERY, of("code", code), Boolean.class);
        }catch (EmptyResultDataAccessException e){
            throw new ApiException("Code is not valid. Please log in again");

        }catch (Exception e) {
            log.error("Error in verifyCode() at UserRepositoryImpl");
            throw new ApiException("An error occurred. Please try again");
    }
    }

}
