package work.domain;

public enum AppMemberType {

    ROLE_HOST,

    ROLE_GUEST,

    ROLE_ADMIN;

    public String getType() {
        return name();
    }

}

