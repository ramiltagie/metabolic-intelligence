package com.metabolicintelligence.external.openai.chat;

import java.util.List;

public interface ChatClient {
    String chat(List<ChatMessage> messages);
}