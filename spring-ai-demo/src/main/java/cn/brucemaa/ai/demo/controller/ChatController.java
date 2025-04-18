package cn.brucemaa.ai.demo.controller;

import cn.brucemaa.ai.demo.vo.ChatRequestVO;
import cn.brucemaa.ai.demo.vo.ChatResponseVO;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping(value = "/v1/chat", consumes = MediaType.APPLICATION_JSON_VALUE)
public class ChatController {

    private static final String DEFAULT_PROMPT = "你是一个博学的智能聊天助手，请根据用户提问回答！";

    private final ChatClient dashScopeChatClient;

    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.dashScopeChatClient = chatClientBuilder
                .defaultSystem(DEFAULT_PROMPT)
                // 实现 Chat Memory 的 Advisor
                // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory())
                )
                // 实现 Logger 的 Advisor
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withTopP(0.7)
                                .build()
                )
                .build();
    }

    private static DashScopeChatOptions getOptions(ChatRequestVO chatRequestVO) {
        return DashScopeChatOptions.builder()
                .withModel(chatRequestVO.getModelId())
                .withTemperature(chatRequestVO.getTemperature())
                .withSeed(chatRequestVO.getSeed())
                .withTopP(chatRequestVO.getTopP())
                .withTopK(chatRequestVO.getTopK())
                .withStop(chatRequestVO.getStop())
                .withEnableSearch(chatRequestVO.getEnableSearch())
                .withMaxToken(chatRequestVO.getMaxTokens())
                .withIncrementalOutput(chatRequestVO.getIncrementalOutput())
                .withRepetitionPenalty(chatRequestVO.getRepetitionPenalty())
                .build();
    }

    @PostMapping(path = "/completions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ChatResponseVO chatCompletions(@RequestBody ChatRequestVO chatRequestVO) {
        var chatResponse = dashScopeChatClient.prompt()
                .options(getOptions(chatRequestVO))
                .user(chatRequestVO.getContent())
                .call().chatResponse();
        String respContent;
        if (chatResponse != null) {
            respContent = chatResponse.getResult().getOutput().getText();
        } else {
            respContent = "没有响应信息";
        }
        var chatResponseVO = new ChatResponseVO();
        chatResponseVO.setStreamResp(chatRequestVO.isStreamResp());
        chatResponseVO.setModelId(chatRequestVO.getModelId());
        chatResponseVO.setContent(respContent);
        return chatResponseVO;
    }

    @PostMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponseVO> chatStream(HttpServletResponse response, @RequestBody ChatRequestVO chatRequestVO) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        var chatResponseFlux = dashScopeChatClient.prompt()
                .options(getOptions(chatRequestVO))
                .user(chatRequestVO.getContent())
                .stream().chatResponse();
        return chatResponseFlux.map(chatResponse -> {
            var chatResponseVO = new ChatResponseVO();
            chatResponseVO.setStreamResp(chatRequestVO.isStreamResp());
            chatResponseVO.setContent(chatResponse.getResult().getOutput().getText());
            return chatResponseVO;
        });
    }
}
