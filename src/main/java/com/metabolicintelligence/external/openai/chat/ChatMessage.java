package com.metabolicintelligence.external.openai.chat;

public record ChatMessage(
    String role,
    String content
) {
}