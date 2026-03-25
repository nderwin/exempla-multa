package com.github.nderwin.rag.control;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.AugmentationRequest;
import dev.langchain4j.rag.AugmentationResult;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Naive or frozen RAG
 */
@ApplicationScoped
public class DocumentRetriever implements RetrievalAugmentor {

    private final RetrievalAugmentor augmentor;

    public DocumentRetriever(final EmbeddingStore store, final EmbeddingModel model) {
        final EmbeddingStoreContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(model)
                .embeddingStore(store)
                .maxResults(3)
                .build();
        
        this.augmentor = DefaultRetrievalAugmentor.builder()
                .contentRetriever(retriever)
                .build();
    }
    
    @Override
    public AugmentationResult augment(final AugmentationRequest ar) {
        return augmentor.augment(ar);
    }
    
}
