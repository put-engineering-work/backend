package work.user.domain;

import org.springframework.security.core.GrantedAuthority;

public enum AppUserRole implements GrantedAuthority {
    ROLE_ADMIN,
    ROLE_USER;

    public String getAuthority() {
        return name();
    }

}