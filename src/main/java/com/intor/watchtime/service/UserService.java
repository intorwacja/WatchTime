package com.intor.watchtime.service;

import com.intor.watchtime.entity.User;
import com.intor.watchtime.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ChangelogService changelogService;

    @Transactional
    public void provision(OidcUser oidcUser) {
        String issuer = oidcUser.getClaimAsString(IdTokenClaimNames.ISS);
        String subject = oidcUser.getSubject();

        User user = userRepository.findByIssuerAndSubject(issuer, subject)
                .orElseGet(() -> User.builder()
                        .issuer(issuer)
                        .subject(subject)
                        .lastSeenChangelogId(changelogService.getLatestId() - 1)
                        .build());

        user.setEmail(oidcUser.getEmail());
        user.setDisplayName(oidcUser.getFullName());
        user.setPictureUrl(oidcUser.getPicture());

        userRepository.save(user);
    }
}
