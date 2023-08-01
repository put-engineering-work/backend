package work.tutor.domain;

import org.springframework.security.core.GrantedAuthority;

public enum AppUserRole implements GrantedAuthority {
    ROLE_ADMIN,
    ROLE_CLIENT,
    ROLE_TUTOR;

    public String getAuthority() {
        return name();
    }

}