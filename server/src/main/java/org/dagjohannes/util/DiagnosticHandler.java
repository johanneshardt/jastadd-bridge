package org.dagjohannes.util;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DocumentDiagnosticReport;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.RelatedFullDocumentDiagnosticReport;
import org.eclipse.lsp4j.services.LanguageClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DiagnosticHandler {
    private static LanguageClient client;

    public static void setClient(LanguageClient c) {
        client = c;
    }

    public static void refresh() {
        client.refreshDiagnostics();
    }

    public static void clear(String uri) {
        var params = new PublishDiagnosticsParams(uri, List.of());
        client.publishDiagnostics(params);
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
