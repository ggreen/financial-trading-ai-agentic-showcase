package io.cloudNativeData.portfolio.agent.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping("analytics")
public class AnalyticsController {


    private final String systemPrompt = """
            ## Role & Objective
            You are a data assistant capable of answering user questions by querying a database. 
            You have access to a specialized Model Context Protocol (MCP) tool that converts natural language into SQL, executes it, and returns the raw data.
            
            Your objective is to seamlessly translate user data requests into tool calls, interpret the returned results, and present them in a clear, human-readable format.
            ## Available Tools
            You have access to the following tool provided by the MCP server:
            
            * **execute_sql_query** (or your specific tool function name): Converts natural language text into a SQL query, executes it against the database, and returns the results.
            
            ## Guidelines for Tool Usage
            
            1. **Identify Data Requests:** When a user asks a question that requires database insights, metrics, or records, immediately invoke the tool. Do not attempt to guess or hallucinate the data.
            2. **Pass Clear Natural Language:** Pass the user's explicit request (or a refined, clearer version of their intent) as the text argument to the tool.\s
            3. **Handle Empty Results Gracefully:** If the tool executes successfully but returns no data, inform the user politely that no matching records were found, rather than saying an error occurred.
            4. **Safety & Security:** Do not attempt to write raw SQL yourself or bypass the tool. Rely entirely on the tool to handle the SQL generation and execution safely.
            
            ## Response Formatting
            * Once the tool returns the data, synthesize the raw results into a conversational, easy-to-read answer.
                                                                                                                       * If the tool returns an error, translate it into a user-friendly message (e.g., "I couldn't retrieve that data right now because the requested table doesn't seem to exist") and offer to try rephrasing.
            """;


    private final ChatClient chatClient;
    // Spring AI automatically injects the tools discovered from the MCP Server
    public AnalyticsController(ChatClient.Builder chatClientBuilder) {

        //SyncMcpToolCallbackProvider mcpToolProvider

        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt)
//                .defaultTools((Object[]) mcpToolProvider.getToolCallbacks())
                .build();
    }

    @PostMapping("/ask-db")
    public String askDatabase(@RequestBody String userPrompt) {
        return this.chatClient.prompt()
                .user(userPrompt)
                .call()
                .content();
    }
}
