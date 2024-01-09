package jastaddBridge.interop;

public class Range {
    private final int startLine, startColumn, endLine, endColumn;
    public Range(int startLine, int startColumn, int endLine, int endColumn) {
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }

    public static Range of(int start, int end) {
        var startCol = start & ((1 << 12) - 1);
        var startLine = start >> 12;
        var endCol = end & ((1 << 12) - 1);
        var endLine = end >> 12;
        return new Range(startLine - 1, startCol-1, endLine - 1, endCol);
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
