package jastaddBridge.interop;

public enum Severity {
    Error(1), Warning(2), Information(3), Hint(4);

    private final int value;

    Severity(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}