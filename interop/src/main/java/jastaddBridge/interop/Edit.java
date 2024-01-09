package jastaddBridge.interop;

public class Edit {

    private final String replacement;
    private final int startLine, startColumn, endLine, endColumn;

    private Edit(String replacement,
                 int startLine,
                 int startColumn,
                 int endLine,
                 int endColumn) {
        this.replacement = replacement;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }

    public static Edit of(String replacement, int startLine, int startColumn, int endLine, int endColumn) {
        return new Edit(replacement, startLine, startColumn, endLine, endColumn);
    }

    public static Edit of(String replacement, Range r) {
        return new Edit(replacement, r.startLine(), r.startColumn(), r.endLine(), r.endColumn());
    }

    public String replacement() {
        return replacement;
    }

    public int startLine() {
        return startLine;
    }

    public int startColumn() {
        return startColumn;
    }

    public int endLine() {
        return endLine;
    }

    public int endColumn() {
        return endColumn;
    }
}
