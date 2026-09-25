package com.intor.watchtime.service;

import com.intor.watchtime.entity.User;
import com.intor.watchtime.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public Optional<User> find() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof OidcUser oidcUser)) {
            return Optional.empty();
        }

        String issuer = oidcUser.getClaimAsString(IdTokenClaimNames.ISS);

        return Optional.of(userRepository.findByIssuerAndSubject(issuer, oidcUser.getSubject())
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated identity has no provisioned user: " + issuer + " / " + oidcUser.getSubject())));
    }

    public User get() {
        return find().orElseThrow(
                () -> new AuthenticationCredentialsNotFoundException("No authenticated user in security context"));
    }
}
