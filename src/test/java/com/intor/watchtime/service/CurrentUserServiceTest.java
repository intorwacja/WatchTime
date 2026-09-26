package com.intor.watchtime.service;

import com.intor.watchtime.entity.User;
import com.intor.watchtime.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    private static final String ISSUER = "https://issuer.example.com/";
    private static final String SUBJECT = "subject-1";

    @Mock
    private UserRepository userRepository;

    @Mock
    private OidcUser oidcUser;

    @InjectMocks
    private CurrentUserService currentUserService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void givenSignedIn() {
        when(oidcUser.getClaimAsString(IdTokenClaimNames.ISS)).thenReturn(ISSUER);
        when(oidcUser.getSubject()).thenReturn(SUBJECT);
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken(oidcUser, null, "ROLE_USER"));
    }

    @Test
    @DisplayName("resolves the stored account matching the identity from the token")
    void findsProvisionedUser() {
        User stored = User.builder().id(UUID.randomUUID()).issuer(ISSUER).subject(SUBJECT).build();
        givenSignedIn();
        when(userRepository.findByIssuerAndSubject(ISSUER, SUBJECT)).thenReturn(Optional.of(stored));

        assertThat(currentUserService.find()).contains(stored);
        assertThat(currentUserService.get()).isSameAs(stored);
    }

    @Test
    @DisplayName("treats an anonymous request as nobody signed in, without touching the database")
    void reportsNobodyForAnonymousRequest() {
        SecurityContextHolder.getContext().setAuthentication(
                new AnonymousAuthenticationToken("key", "anonymousUser",
                        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));

        assertThat(currentUserService.find()).isEmpty();
        assertThatThrownBy(() -> currentUserService.get())
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }

    @Test
    @DisplayName("a signed-in identity with no stored account is a system fault, not an anonymous request")
    void failsLoudlyWhenIdentityWasNeverProvisioned() {
        givenSignedIn();
        when(userRepository.findByIssuerAndSubject(ISSUER, SUBJECT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> currentUserService.find())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(SUBJECT);
    }
}
