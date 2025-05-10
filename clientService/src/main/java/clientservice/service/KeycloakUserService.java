package clientservice.service;

import clientservice.dto.AuthDTO;
import clientservice.dto.TokenResponseDTO;
import clientservice.dto.UserDTO;
import clientservice.exception.UserAuthenticationException;
import clientservice.properties.KeycloakProperties;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {
    private final KeycloakProperties keycloakProperties;
    private Keycloak keycloak;

    private static final Logger log = LoggerFactory.getLogger(KeycloakUserService.class);

    private static final String CLIENT_ROLE = "ROLE_CLIENT";

    @PostConstruct
    public void init() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getServerUrl())
                .realm(keycloakProperties.getRealm())
                .clientId(keycloakProperties.getClientId())
                .clientSecret(keycloakProperties.getClientSecret())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    public String registerUser(UserDTO userDTO)
    {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDTO.getUsername());
        user.setEnabled(true);
        user.setRequiredActions(Collections.emptyList());
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("phone", Collections.singletonList(userDTO.getPhone()));
        user.setAttributes(attributes);
        user.setEmailVerified(true);

        Response response = keycloak.realm(keycloakProperties.getRealm())
                .users()
                .create(user);

        if (response.getStatus() != 201) {
            String body = response.readEntity(String.class);
            log.error("Failed to create user. Status: {}, Body: {}", response.getStatus(), body);
            throw new RuntimeException("Failed to create user: " + response.getStatus());
        }


        String userId = CreatedResponseUtil.getCreatedId(response);

        CredentialRepresentation password = new CredentialRepresentation();
        password.setType(CredentialRepresentation.PASSWORD);
        password.setValue(userDTO.getPassword());
        password.setTemporary(false);

        keycloak.realm(keycloakProperties.getRealm())
                .users()
                .get(userId)
                .resetPassword(password);

        RoleRepresentation role = keycloak.realm(keycloakProperties.getRealm())
                .roles()
                .get(CLIENT_ROLE)
                .toRepresentation();

        keycloak.realm(keycloakProperties.getRealm())
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(Collections.singletonList(role));

        return userId;
    }

    public TokenResponseDTO loginUser(AuthDTO authDTO) {
        try {
            Keycloak keycloakUser = KeycloakBuilder.builder()
                    .serverUrl(keycloakProperties.getServerUrl())
                    .realm(keycloakProperties.getRealm())
                    .clientId(keycloakProperties.getClientId())
                    .clientSecret(keycloakProperties.getClientSecret())
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(authDTO.getUsername())
                    .password(authDTO.getPassword())
                    .build();

            var tokenResponse = keycloakUser.tokenManager().getAccessToken();

            return new TokenResponseDTO(
                    tokenResponse.getToken(),
                    tokenResponse.getRefreshToken(),
                    tokenResponse.getExpiresIn(),
                    tokenResponse.getRefreshExpiresIn(),
                    tokenResponse.getTokenType()
            );
        } catch (Exception e) {
            log.error("Login failed for User {}", authDTO.getUsername(), e);
            throw new UserAuthenticationException("Login failed: " + e.getMessage());
        }
    }
}
