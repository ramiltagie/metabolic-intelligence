package com.metabolicintelligence.external.openai.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.metabolicintelligence.config.openai.OpenAiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatClientImpl implements ChatClient {
    private final OpenAiProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public String chat(List<ChatMessage> messages) {
        try {
            var arrayNode = objectMapper.createArrayNode();
            for (ChatMessage msg : messages) {
                ObjectNode node = objectMapper.createObjectNode();
                node.put("role", msg.role());
                node.put("content", msg.content());
                arrayNode.add(node);
            }

            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("model", properties.getModel());
            payload.set("messages", arrayNode);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(properties.getApiKey());

            HttpEntity<String> entity = new HttpEntity<>(payload.toString(), headers);

            String response = restTemplate.postForObject(
                properties.getBaseUrl() + "/chat/completions",
                entity,
                String.class
            );

            return parseResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send chat request to OpenAI", e);
        }
    }

    private String parseResponse(String response) throws IOException {
        JsonNode jsonResponse = objectMapper.readTree(response);
        return jsonResponse
            .path("choices")
            .get(0)
            .path("message")
            .path("content")
            .asText();
    }
}