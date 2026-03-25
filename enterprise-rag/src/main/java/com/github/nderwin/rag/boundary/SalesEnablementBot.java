package com.github.nderwin.rag.boundary;

import com.github.nderwin.rag.control.HallucinationGuardrail;
import com.github.nderwin.rag.control.OutOfScopeGuardrail;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.guardrail.OutputGuardrails;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface SalesEnablementBot {
    
    @SystemMessage("""
                   # ROLE AND SCOPE
                   You are a Sales Enablement Copilot for CloudX Enterprise Platform.
                   """)
    @OutputGuardrails({ OutOfScopeGuardrail.class, HallucinationGuardrail.class })
    String chat(@UserMessage String userQuestion);
    
}
