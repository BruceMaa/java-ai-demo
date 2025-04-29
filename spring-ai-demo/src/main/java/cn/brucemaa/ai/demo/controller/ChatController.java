package cn.brucemaa.ai.demo.controller;

import cn.brucemaa.ai.demo.vo.ChatRequestVO;
import cn.brucemaa.ai.demo.vo.ChatResponseVO;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.memory.jdbc.JdbcChatMemory;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.join.DocumentJoiner;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Slf4j
@RestController
@RequestMapping(value = "/v1/chat", consumes = MediaType.APPLICATION_JSON_VALUE)
public class ChatController {

    private static final String DEFAULT_PROMPT = "你是一个博学的智能聊天助手，请根据用户提问回答！";

    private final ChatClient dashScopeChatClient;
    private final JdbcChatMemory jdbcChatMemory;
    private final ChatClient.Builder chatClientBuilder;
    private final VectorStore vectorStore;

    public ChatController(ChatClient.Builder chatClientBuilder, JdbcChatMemory jdbcChatMemory, VectorStore vectorStore) {
        this.chatClientBuilder = chatClientBuilder;
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
        this.jdbcChatMemory = jdbcChatMemory;
        this.vectorStore = vectorStore;
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

        // 构建查询扩展器
        // 用于生成多个相关的查询变体，以获得更全面的搜索结果
        var queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(this.chatClientBuilder)
                .includeOriginal(false)
                .numberOfQueries(3)
                .build();

        // 创建查询重写转换器
        QueryTransformer rewriteQueryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(this.chatClientBuilder)
                .build();

        // 创建查询翻译转换器，设置目标语言为中文
        QueryTransformer translationQueryTransformer = TranslationQueryTransformer.builder()
                .chatClientBuilder(this.chatClientBuilder)
                .targetLanguage("chinese")  // 设置目标语言为中文
                .build();

        // 创建查询压缩转换器
        QueryTransformer compressionQueryTransformer = CompressionQueryTransformer.builder()
                .chatClientBuilder(this.chatClientBuilder)
                .build();

        // 创建文档合并器实例
        DocumentJoiner documentJoiner = new ConcatenationDocumentJoiner();

        // 3. 创建检索增强顾问
        Advisor advisor = RetrievalAugmentationAdvisor.builder()
                .queryExpander(queryExpander)
                .queryTransformers(rewriteQueryTransformer, translationQueryTransformer, compressionQueryTransformer)
                .documentJoiner(documentJoiner)
                // 配置查询增强器
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(true)
                        .build())
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .build())
                .build();

        var chatResponse = dashScopeChatClient.prompt()
                .options(getOptions(chatRequestVO))
                .advisors(new MessageChatMemoryAdvisor(jdbcChatMemory))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatRequestVO.getConversationId())
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, chatRequestVO.getChatHistorySize()))
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
                .advisors(new MessageChatMemoryAdvisor(jdbcChatMemory))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatRequestVO.getConversationId())
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, chatRequestVO.getChatHistorySize()))
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
