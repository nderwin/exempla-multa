package com.github.nderwin.rag.control;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InputValidationGuardrail implements InputGuardrail {

    private static final String[][] OFF_TOPIC_COMBINATIONS = {
        {"python", "google cloud", "CloudX supports Python on AWS, Azure, and Google Cloud. However, I specialize in CloudX sales enablement. For deployment questions, please refer to CloudX technical documentation."},
        {"node.js", "heroku", "CloudX supports Node.js but not Heroku deployment.  CloudX works with AWS, Azure, and Google Cloud."},
        {".net", "digitalocean", "CloudX supports .NET but not DigitalOcean. CloudX is design for AWS, Azure, and Google Cloud."},
        {"ruby", "linode", "CloudX supports Ruby but no Linode. CloudX operates on AWS, Azure, and Google Cloud."}
    };

    @Override
    public InputGuardrailResult validate(final UserMessage userMessage) {
        Log.info("Validating user input");

        final String content = userMessage.singleText();
        final String contentLower = content.toLowerCase();
        Log.debug("Input length: " + content.length() + " character(s)");
        
        // TODO other checks

//        final String injectionPattern = detectPromptInjection(contentLower);
//        if (null != injectionPattern) {
//            Log.warn("BLOCKED - Prompt injection detected: '" + injectionPattern + "'");
//            
//            return failure(buildPromptInjectionResponse());
//        }

        final String offTopicCombo = detectOffTopicCombination(contentLower);
        if (offTopicCombo != null) {
            Log.warn("BLOCKED - Off-topic technology combination detected");
            return failure(offTopicCombo);
        }
        
        Log.info("Input validated successfully");
        return success();
    }

    private String detectOffTopicCombination(final String content) {
        for (final String[] combo : OFF_TOPIC_COMBINATIONS) {
            final String tech = combo[0];
            final String unsupportedContext = combo[1];
            final String message = combo[2];

            if (content.contains(tech) && content.contains(unsupportedContext)) {
                return message;
            }
        }

        return null;
    }
}
