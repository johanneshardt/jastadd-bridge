package jastaddBridge.interop;

import java.util.HashSet;
import java.util.Set;

public class Diagnostic {
    private final String message;
    private String codeActionTitle = "";
    private final int severity, startLine, startColumn, endLine, endColumn;
    private final Set<Edit> fixes = new HashSet<>();

    private Diagnostic(String message, int severity, int startLine, int startColumn, int endLine, int endColumn) {
        this.message = message;
        this.severity = severity;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }

    public static Diagnostic of(String message, int severity, int startLine, int startColumn, int endLine, int endColumn) {
        return new Diagnostic(message, severity, startLine, startColumn, endLine, endColumn);
    }

    public static Diagnostic of(String message, Severity s, Range r) {
        return new Diagnostic(message, s.getValue(), r.startLine(), r.startColumn(), r.endLine(), r.endColumn());
    }

    public Diagnostic withFix(Set<Edit> fixes, String codeActionTitle) {
        this.fixes.addAll(fixes);
        this.codeActionTitle = codeActionTitle;
        return this;
    }

    public String message() {
        return message;
    }


    public int severity() {
        return severity;
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

    public Set<Edit> fixes() {
        return fixes;
    }

    public String codeActionTitle() {
        return codeActionTitle;
    }
}
