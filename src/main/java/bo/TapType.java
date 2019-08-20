package bo;

public enum TapType {
    ON, OFF;

    public static TapType fromValue(final String testValue) {
        for (final TapType value: values()) {
            if (value.name().equalsIgnoreCase(testValue)) {
                return value;
            }
        }
        return null;
    }
}