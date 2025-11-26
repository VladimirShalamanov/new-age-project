package app.product.model;

public enum ProductGender {
    MALE("Male"),
    FEMALE("Female");

    private final String displayName;

    ProductGender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}