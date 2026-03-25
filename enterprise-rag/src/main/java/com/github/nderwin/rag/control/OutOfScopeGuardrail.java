package com.github.nderwin.rag.control;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OutOfScopeGuardrail implements OutputGuardrail {
    
    private static final String[] COMPETITOR_INTERNAL_KEYWORDS = {
        "competecloud's internal",
        "competecloud roadmap",
        "competecloud strategy",
        "skyplatform's internal",
        "skyplatform roadmap",
        "skyplatform strategy",
        "techgiant's internal",
        "techgiant roadmap",
        "techgiant strategy",
        "competitor's source code",
        "competitor's architecture"
    };

    @Override
    public OutputGuardrailResult validate(final AiMessage responseFromLLM) {
        Log.info("Validating LLM response");
        
        final String content = responseFromLLM.text().toLowerCase();
        Log.debug("Response content length: " + content.length() + " character(s)");
        
        final String detectedIssue = detectOutOfScopeContent(content);
        
        if (null != detectedIssue) {
            Log.warn("Detected out-of-scope content - Issue type: " + detectedIssue);
            
            return buildOutOfScopeResponse(detectedIssue);
        }
        
        Log.info("Response validated successfully - content is in scope");
        return success();
    }
    
    private String detectOutOfScopeContent(final String content) {
        // TODO other checks
        
        for (final String keyword : COMPETITOR_INTERNAL_KEYWORDS) {
            if (content.contains(keyword)) {
                return "competitor_internal";
            }
        }
        
        return null;
    }

    private OutputGuardrailResult buildOutOfScopeResponse(final String issueType) {
        Log.info("Building reprompt response for issue type: " + issueType);
        
        String userMessage;
        String repromptMessage;
        
        switch (issueType) {
            // TODO - other types
            case "competitor_internal" -> {
                userMessage = "The response discusses competitors' internal strategies or confidential information.";
                repromptMessage = "Please limit the response to publicly available competitive comparisons based on the CloudX sales enablement materials. "
                        + "Focus on how CloudX compares to competitors using public information and customer feedback.";
            }
            default -> {
                userMessage = "The response appears to be outside the scope of CloudX sales enablement. ";
                repromptMessage = "Please provide a response focused on CloudX Enterprise Platform features, pricing, competitive analysis, "
                        + "sales methodology, or customer success stories based on the available sales enablement materials.";
            }
        }
        
        Log.debug("Reprompting with user message: " + userMessage);
        return reprompt(userMessage, repromptMessage);
    }
    
}
