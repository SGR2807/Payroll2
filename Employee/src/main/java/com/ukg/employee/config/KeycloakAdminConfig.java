package com.ukg.employee.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak.admin")
@Getter
@Setter
public class KeycloakAdminConfig {

    private String serverUrl;
    private String realm;
    private String username;
    private String password;
    private String targetRealm;
    private String defaultPassword;
}
