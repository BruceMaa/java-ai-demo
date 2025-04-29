package cn.brucemaa.ai.demo;

import cn.brucemaa.ai.demo.assistant.Assistant;
import cn.brucemaa.ai.demo.assistant.SeparateChatAssistant;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ChatMemoryTest {

    @Autowired
    private Assistant assistant;

    @Test
    public void testChatMemory() {
        var answer1 = assistant.chat("我是张三");
        System.out.println("answer1 = " + answer1);

        var answer2 = assistant.chat("我是谁");
        System.out.println("answer2 = " + answer2);
    }

    @Autowired
    private OpenAiChatModel openAiChatModel;

    @Test
    public void testChatMemory2() {
        var userMessage1 = UserMessage.userMessage("我是张三");
        var chatResponse1 = openAiChatModel.chat(userMessage1);
        var aiMessage1 = chatResponse1.aiMessage();
        System.out.println("aiMessage1 = " + aiMessage1.text());

        var userMessage2 = UserMessage.userMessage("你知道我是谁吗");
        var chatResponse2 = openAiChatModel.chat(userMessage1, aiMessage1, userMessage2);
        var aiMessage2 = chatResponse2.aiMessage();
        System.out.println("aiMessage2 = " + aiMessage2.text());
    }

    @Test
    public void testChatMemory3() {
        var messageWindowChatMemory = MessageWindowChatMemory.withMaxMessages(10);
        var assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(openAiChatModel)
                .chatMemory(messageWindowChatMemory)
                .build();

        var answer1 = assistant.chat("我是张三");
        System.out.println("answer1 = " + answer1);
        var answer2 = assistant.chat("你知道我是谁吗");
        System.out.println("answer2 = " + answer2);
    }

    @Autowired
    private SeparateChatAssistant separateChatAssistant;

    @Test
    public void testChatMemory4() {

        var answer1 = separateChatAssistant.chat(1, "我是张三");
        System.out.println("answer1 = " + answer1);
        var answer2 = separateChatAssistant.chat(1, "你知道我是谁吗");
        System.out.println("answer2 = " + answer2);
        var answer3 = separateChatAssistant.chat(2, "你知道我是谁吗");
        System.out.println("answer3 = " + answer3);
    }
}
