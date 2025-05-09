package cn.brucemaa.ai.demo.assistant;

import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "openAiChatModel",
        chatMemory = "chatMemory")
public interface Assistant {
    String chat(String userMessage);
}
