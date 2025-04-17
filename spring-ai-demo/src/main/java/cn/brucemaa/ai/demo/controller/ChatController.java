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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/v1")
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

    @GetMapping
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Hello, World!");
    }

    @PostMapping(path = "/chat/completions", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ChatResponseVO chatCompletions(@RequestBody ChatRequestVO chatRequestVO) {
        var respContent = dashScopeChatClient.prompt(chatRequestVO.getContent()).call().content();
        var chatResponseVO = new ChatResponseVO();
        chatResponseVO.setStreamResp(chatRequestVO.isStreamResp());
        if (respContent == null || respContent.isBlank()) {
            respContent = "没有响应信息";
        }
        chatResponseVO.setContent(respContent);
        return chatResponseVO;
    }

    @PostMapping(path = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ChatResponseVO> chatStream(HttpServletResponse response, @RequestBody ChatRequestVO chatRequestVO) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        var chatResponseFlux = dashScopeChatClient.prompt(chatRequestVO.getContent()).stream().chatResponse();
        return chatResponseFlux.map(chatResponse -> {
            var chatResponseVO = new ChatResponseVO();
            chatResponseVO.setStreamResp(chatRequestVO.isStreamResp());
            chatResponseVO.setContent(chatResponse.getResult().getOutput().getText());
            return chatResponseVO;
        });
    }
}
