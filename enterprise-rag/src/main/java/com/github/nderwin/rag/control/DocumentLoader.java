package com.github.nderwin.rag.control;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.CreationException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Singleton
@Startup
public class DocumentLoader {

    @Inject
    EmbeddingStore<TextSegment> store;

    @Inject
    EmbeddingModel model;

    @Inject
    DoclingConverter converter;

    @PostConstruct
    public void loadDocument() throws Exception {
        Log.infof("Starting document loading...");

        final Path documentsPath = Path.of("src/main/resources/documents");
        final List<Document> docs = new ArrayList<>();

        if (Files.exists(documentsPath) && Files.isDirectory(documentsPath)) {
            int success = 0;
            int failure = 0;
            int skipped = 0;

            final SortedSet<String> extensions = new TreeSet<>(Set.of(
                    "txt", "csv", "json",
                    "pdf",
                    "pptx", "ppt",
                    "doc", "docx",
                    "xlsx", "xls",
                    "xml", "html"
            ));

            try (var stream = Files.list(documentsPath)) {
                for (final Path filePath : stream.filter(Files::isRegularFile).toList()) {
                    final File file = filePath.toFile();
                    final String fileName = file.getName();

                    final int lastDotIndex = fileName.lastIndexOf('.');
                    final String extension = (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1)
                            ? fileName.substring(lastDotIndex + 1).toLowerCase()
                            : "";

                    if (extension.isBlank() || !extensions.contains(extension)) {
                        skipped++;
                        Log.debugf("Skipping file '%s' - extension '%s' is not in allowed list: %s",
                                fileName,
                                extension.isEmpty() ? "(no extension)" : extension,
                                extensions
                        );

                        continue;
                    }

                    try {
                        Log.infof("Processing file: %s", file.getName());

                        final String markdown = converter.toMarkdown(file);

                        final Map<String, String> meta = new HashMap<>();
                        meta.put("file", file.getName());
                        meta.put("format", extension);

                        Document doc = Document.document(markdown, new Metadata(meta));
                        docs.add(doc);
                        success++;
                        Log.infof("Successfully processed file: %s", file.getName());
                    } catch (final Exception ex) {
                        failure++;

                        Log.errorf(ex, "Failed to process file: %s.  Error: %s",
                                filePath,
                                ex.getMessage()
                        );
                    }
                }
            }

            Log.infof("Document loading completed.  Success: %d, Failures: %d, Skipped: %d",
                    success, failure, skipped
            );
            
            if (docs.isEmpty()) {
                Log.warn("No documents to process.  Skipping embedding generation.");
                return;
            }
            
            final DocumentBySentenceSplitter splitter = new DocumentBySentenceSplitter(200, 20);
            final List<TextSegment> segments = splitter.splitAll(docs);
            
            if (segments.isEmpty()) {
                Log.warn("No text segments generated from documents.  Skipping embedding storage.");
                return;
            }
            
            Log.infof("Generating embeddings for %d text segment(s)...", segments.size());
            
            int embedded = 0;
            try {
                if (!segments.isEmpty()) {
                    final TextSegment test = segments.getFirst();
                    var testEmbedding = model.embed(test).content();
                    store.add(testEmbedding, test);
                    Log.infof("Store text successful.  Proceeding with bulk embedding...");
                    embedded++;
                }
            } catch (final CreationException ex) {
                final Throwable cause = ex.getCause();
                if (cause instanceof IllegalArgumentException
                        && cause.getMessage() != null
                        && cause.getMessage().contains("indexListSize")
                        && cause.getMessage().contains("zero")) {
                    Log.errorf("PgVector dimension configuration error detected during store initialization.");
                    Log.errorf("The dimension property 'quarkus.langchain4j.pgvector.dimension' is being read as 0.");
                    Log.errorf("Please verify:");
                    Log.errorf("1. The property is set correctly in application.properties");
                    Log.errorf("2. PostgreSQL database is running an accessible");
                    Log.errorf("3. The pgvector extension is installed:  CREATE EXTENSION IF NOT EXISTS vector;");
                    Log.errorf("4. Try setting 'quarkus.langchain4j.pgvector.use-index=false' temporarily");
                    
                    throw new RuntimeException("PgVector store initialization failed.  "
                            + "The dimension configuration is not being read correctly.  "
                            + "This usually means the dimension property is 0.  "
                            + "Check application.properties and database configuration.", ex);
                }
                
                throw ex;
            } catch (final IllegalArgumentException ex) {
                if (null != ex.getMessage() 
                        && ex.getMessage().contains("indexListSize")
                        && ex.getMessage().contains("zero")) {
                    Log.errorf("PgVector dimension configuration error.  The dimension is being read as 0.");
                    Log.errorf("Please verify 'quarkus.langchain4j.pgvector.dimension' in application.properties");
                    
                    throw new RuntimeException("PgVector dimension misconfiguration.  "
                            + "Dimension must be > 0.  "
                            + "Check application.properties.", ex);
                }
            } catch (final Exception ex) {
                Log.errorf(ex, "Failed to test embedding store.  This might indicate a configuration issue.");
                
                throw new RuntimeException("Embedding store test failed.  "
                        + "Please check your database and pgvector configuration.", ex);
            }
            
            final int startIndex = embedded > 0 ? 1 : 0;
            int errors = 0;
            for (int i = startIndex; i < segments.size(); i++) {
                final TextSegment segment = segments.get(i);
                try {
                    var embedding = model.embed(segment).content();
                    store.add(embedding, segment);
                    embedded++;
                    
                    if (embedded % 10 == 0) {
                        Log.infof("Progress: embedded %d/%d segments", embedded, segments.size());
                    }
                } catch (final Exception ex) {
                    errors++;
                    Log.errorf(ex, "Failed to embed and store segment: %s",
                            segment.text().substring(0, Math.min(50, segment.text().length()))
                    );
                }
            }
            
            Log.infof("Successfully embedded and stored %d out of %d segments (errors: %d)",
                    embedded, segments.size(), errors
            );
        }
    }

}
