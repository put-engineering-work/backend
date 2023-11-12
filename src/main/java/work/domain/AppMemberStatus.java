package work.domain;

public enum AppMemberStatus {

    STATUS_ACTIVE,

    STATUS_INACTIVE,

    STATUS_PENDING,

    STATUS_BANNED;

    public String getStatus() {
        return name();
    }
}
