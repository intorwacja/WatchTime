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

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof OidcUser oidcUser)) {
            throw new AuthenticationCredentialsNotFoundException("No authenticated user in security context");
        }

        String issuer = oidcUser.getClaimAsString(IdTokenClaimNames.ISS);

        return userRepository.findByIssuerAndSubject(issuer, oidcUser.getSubject())
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated identity has no provisioned user: " + issuer + " / " + oidcUser.getSubject()));
    }
}
