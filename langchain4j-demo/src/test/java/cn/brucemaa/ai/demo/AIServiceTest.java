package cn.brucemaa.ai.demo;

import cn.brucemaa.ai.demo.assistant.Assistant;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AIServiceTest {

    @Autowired
    private OpenAiChatModel openAiChatModel;

    @Test
    public void testChat() {
        var assistant = AiServices.create(Assistant.class, openAiChatModel);
        var answer = assistant.chat("你是谁呀？");
        System.out.println("answer = " + answer);
    }

    @Autowired
    private Assistant assistant;

    @Test
    public void testAssistant() {
        var answer = assistant.chat("我是谁呀？");
        System.out.println("answer = " + answer);
    }
}
