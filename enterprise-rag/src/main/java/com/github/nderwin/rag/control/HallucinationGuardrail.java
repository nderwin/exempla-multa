package com.github.nderwin.rag.control;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HallucinationGuardrail implements OutputGuardrail {
    
    private static final String[] UNCERTAINTY_PHRASES = {
        "i don't have that information",
        "i don't know",
        "i'm not sure",
        "i cannot find",
        "i don't have access to",
        "i'm unable to provide",
        "i don't have specific information",
        "i cannot confirm",
        "i'm not aware of",
        "i don't have details about"
    };

    @Override
    public OutputGuardrailResult validate(final AiMessage responseFromLLM) {
        Log.info("Validating LLM response");
        
        final String content = responseFromLLM.text();
        final String contentLower = content.toLowerCase();
        Log.debug("Response content length: " + content.length() + " characters");
        
        final String uncertaintyPhrase = detectUncertaintyPhrase(contentLower);
        if (null != uncertaintyPhrase) {
            Log.warn("Detected uncertainty phrase: '" + uncertaintyPhrase + "'");
            
            return reprompt("The response contains uncertainty phrases.",
                    "Please provide a confident answer based strictly on the CloudX sales enablement materials. "
                    + "If the information is not available in the provided documents, clearly state that the information is not in the available materials rather than expressing uncertainty.");
        }
        
        // TODO - other types of checks
        
        Log.info("Response validated successfully - no hallucination indicators detected");
        return success();
    }
    
    private String detectUncertaintyPhrase(final String content) {
        for (final String phrase : UNCERTAINTY_PHRASES) {
            if (content.contains(phrase)) {
                return phrase;
            }
        }
        
        return null;
    }
}
