package com.thacbao.codeSphere.configurations;

import com.thacbao.codeSphere.data.dao.AuthorizationDao;
import com.thacbao.codeSphere.data.repository.user.RoleRepository;
import com.thacbao.codeSphere.data.repository.user.UserRepository;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.entities.reference.Role;
import com.thacbao.codeSphere.enums.RoleEnum;
import com.thacbao.codeSphere.exceptions.common.AlreadyException;
import com.thacbao.codeSphere.exceptions.common.AppException;
import com.thacbao.codeSphere.exceptions.common.InvalidException;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.security.OAuth2UserInfo;
import com.thacbao.codeSphere.security.UserPrincipal;
import com.thacbao.codeSphere.security.factory.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.SQLDataException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    private final AuthorizationDao authorizationDao;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (OAuth2AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

//        if (registrationId.equalsIgnoreCase("github")) {
//            oAuth2User.getAttributes().put("email", oAuth2User.getAttributes().get("login") + "@codeSphere.com");
//        }
        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new NotFoundException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if(userOptional.isPresent()) {
            user = userOptional.get();

            if (user.getProvider() == null){
                throw new AlreadyException("Your account has been registered by this email");
            }
            // check người dùng đăng nhập với provider khác
            if( !user.getProvider().equals(registrationId)) {
                throw new InvalidException("You're signed up with " +
                        user.getProvider() + ". Please use your " + user.getProvider() + " account to login.");
            }

            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        try {
            User user = new User();

            user.setProvider(oAuth2UserRequest.getClientRegistration().getRegistrationId());
            user.setProviderId(oAuth2UserInfo.getId());
            user.setFullName(oAuth2UserInfo.getName());
            user.setEmail(oAuth2UserInfo.getEmail());
            user.setAvatar(oAuth2UserInfo.getImageUrl());
            user.setCreatedAt(LocalDate.now());
            user.setUpdatedAt(LocalDate.now());
            user.setIsActive(true);
            user.setIsBlocked(false);

            // Tạo username từ email
            String username = oAuth2UserInfo.getEmail().split("@")[0];
            if (userRepository.existsByUsername(username)) {
                username = username + "-" + System.currentTimeMillis();
            }
            user.setUsername(username);

            // Gán default role la user
            User savedUser = userRepository.save(user);
            authorizationDao.insertIntoAuthorization(savedUser.getId(), RoleEnum.USER.getId());
            return savedUser;
        }
        catch (SQLDataException ex){
            throw new AppException(ex.getMessage());
        }
        catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }


    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setFullName(oAuth2UserInfo.getName());
        existingUser.setAvatar(oAuth2UserInfo.getImageUrl());
        return userRepository.save(existingUser);
    }

}
