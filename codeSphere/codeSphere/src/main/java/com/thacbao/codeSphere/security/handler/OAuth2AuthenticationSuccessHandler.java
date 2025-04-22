package com.thacbao.codeSphere.security.handler;

import com.thacbao.codeSphere.configurations.JwtUtils;
import com.thacbao.codeSphere.data.dao.UserDao;
import com.thacbao.codeSphere.data.repository.user.UserRepository;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.exceptions.common.AppException;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final UserDao userDao;
    private final UserRepository userRepository;

    @Value("${app.oauth2.redirectUrl}")
    private String authorizedRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            log.info("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findByEmail(principal.getEmail()).orElseThrow(
                () -> new NotFoundException("Cannot find user " + principal.getEmail())
        );
        try {
            List<String> roles = userDao.getRolesUser(principal.getId());
            Map<String, Object> claim = new HashMap<>();
            claim.put("role", roles);
            claim.put("email", principal.getEmail());

            String token = jwtUtils.generateToken(user.getUsername(), claim);

            return UriComponentsBuilder.fromUriString(authorizedRedirectUri)
                    .queryParam("token", token)
                    .build().toUriString();
        }
        catch (Exception e) {
            throw new AppException(e.getMessage());
        }
    }
}

