package com.github.nderwin.rag.control;

import ai.docling.serve.api.convert.request.options.OutputFormat;
import io.quarkiverse.docling.runtime.client.DoclingService;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ProcessingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@ApplicationScoped
public class DoclingConverter {
    
    @Inject
    DoclingService docling;
    
    public String toMarkdown(final File sourceFile) throws IOException {
        final Path filePath = sourceFile.toPath();
        
        try {
            var response = docling.convertFile(filePath, OutputFormat.MARKDOWN);
            var document = response.getDocument();
            
            if (null == document) {
                throw new IllegalStateException("Document conversion returned null document for file: " + sourceFile);
            }
            
            return document.getMarkdownContent();
        } catch (final ProcessingException ex) {
            healthCheck(sourceFile);
            
            final Throwable cause = ex.getCause();
            final String message = cause != null ? cause.getMessage() : ex.getMessage();
            throw new IOException("Failed to convert file: " + sourceFile + ". Cause: " + message, ex.getCause());
        } catch (final Exception ex) {
            healthCheck(sourceFile);
            
            throw new IOException("Failed to convert file: " + sourceFile, ex);
        }
    }

    private void healthCheck(final File sourceFile) {
        try {
            final boolean isHealthy = docling.isHealthy();
            Log.warnf("Docling service health check: %s (file %s)",
                    isHealthy ? "HEALTHY" : "UNHEALTHY",
                    sourceFile.getName()
            );
        } catch (final Exception hcEx) {
            Log.warnf("Failed to check Docling service health status: %s (file: %s)",
                    hcEx.getMessage(),
                    sourceFile.getName()
            );
        }
    }

}
