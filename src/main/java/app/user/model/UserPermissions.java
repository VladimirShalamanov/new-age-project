package app.user.model;

public enum UserPermissions {
    ACCOUNTANT("Accountant");

    private final String displayName;

    UserPermissions(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
