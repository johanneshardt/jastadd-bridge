package org.dagjohannes.util;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.dagjohannes.Server;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DocumentDiagnosticReport;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.RelatedFullDocumentDiagnosticReport;

public class DiagnosticHandler {
    private static Server server;

    public static void setServer(Server s) {
        server = s;
    }

    public static void refresh() {
        server.getClient().refreshDiagnostics();
    }

    public static void clear(String uri) {
        var params = new PublishDiagnosticsParams(uri, List.of());
        server.getClient().publishDiagnostics(params);
    }

    public static CompletableFuture<DocumentDiagnosticReport> report(List<Diagnostic> list) {
        var related = new RelatedFullDocumentDiagnosticReport(list);
        var report = new DocumentDiagnosticReport(related);
        return CompletableFuture.completedFuture(report);
    }

    public static CompletableFuture<DocumentDiagnosticReport> emptyReport() {
        return report(List.of());
    }
}
