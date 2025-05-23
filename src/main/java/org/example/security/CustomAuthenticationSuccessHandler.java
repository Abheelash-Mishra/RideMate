package org.example.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.models.User;
import org.example.repository.UserRepository;
import org.example.services.impl.UserDetailsImpl;
import org.example.utilities.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepo;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication auth) throws IOException {
        log.info("Authentication was successful");
        DefaultOAuth2User oauthUser = (DefaultOAuth2User) auth.getPrincipal();
        String email = oauthUser.getAttribute("email");

        User user = userRepo.findByEmail(email).orElseThrow();

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        String token = jwtUtil.generateToken(userDetails);

        boolean isFirstLogin = (user.getRole() == null);
        String redirectPath = isFirstLogin
                ? "/profile"
                : "/oauth2/redirect";

        String targetUrl = UriComponentsBuilder
                .fromUriString("http://localhost:5173" + redirectPath)
                .queryParam("token", token)
                .queryParam("id", user.getId())
                .build()
                .toUriString();

        redirectStrategy.sendRedirect(req, resp, targetUrl);
    }
}
