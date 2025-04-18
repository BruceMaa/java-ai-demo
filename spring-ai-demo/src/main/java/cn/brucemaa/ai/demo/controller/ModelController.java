package cn.brucemaa.ai.demo.controller;

import cn.brucemaa.ai.demo.constant.ModelType;
import cn.brucemaa.ai.demo.vo.ModelResponseVO;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/v1/models", produces = MediaType.APPLICATION_JSON_VALUE)
public class ModelController {

    @GetMapping
    public ResponseEntity<List<ModelResponseVO>> list() {
        List<ModelResponseVO> models = new ArrayList<>();
        var chatModel1 = ModelResponseVO.builder()
                .modelId(DashScopeApi.ChatModel.QWEN_PLUS.getModel())
                .modelName(DashScopeApi.ChatModel.QWEN_PLUS.getModel())
                .modelType(ModelType.CHAT)
                .build();
        models.add(chatModel1);
        var chatModel2 = ModelResponseVO.builder()
                .modelId(DashScopeApi.ChatModel.QWEN_TURBO.getModel())
                .modelName(DashScopeApi.ChatModel.QWEN_TURBO.getModel())
                .modelType(ModelType.CHAT)
                .build();
        models.add(chatModel2);
        var chatModel3 = ModelResponseVO.builder()
                .modelId(DashScopeApi.ChatModel.QWEN_MAX.getModel())
                .modelName(DashScopeApi.ChatModel.QWEN_MAX.getModel())
                .modelType(ModelType.CHAT)
                .build();
        models.add(chatModel3);
        var chatModel4 = ModelResponseVO.builder()
                .modelId(DashScopeApi.ChatModel.QWEN_MAX_LONGCONTEXT.getModel())
                .modelName(DashScopeApi.ChatModel.QWEN_MAX_LONGCONTEXT.getModel())
                .modelType(ModelType.CHAT)
                .build();
        models.add(chatModel4);


        var embeddingModel1 = ModelResponseVO.builder()
                .modelId(DashScopeApi.EmbeddingModel.EMBEDDING_V1.getValue())
                .modelName(DashScopeApi.EmbeddingModel.EMBEDDING_V1.getValue())
                .modelType(ModelType.EMBEDDING)
                .build();
        models.add(embeddingModel1);
        var embeddingModel2 = ModelResponseVO.builder()
                .modelId(DashScopeApi.EmbeddingModel.EMBEDDING_V2.getValue())
                .modelName(DashScopeApi.EmbeddingModel.EMBEDDING_V2.getValue())
                .modelType(ModelType.EMBEDDING)
                .build();
        models.add(embeddingModel2);
        var embeddingModel3 = ModelResponseVO.builder()
                .modelId(DashScopeApi.EmbeddingModel.EMBEDDING_V3.getValue())
                .modelName(DashScopeApi.EmbeddingModel.EMBEDDING_V3.getValue())
                .modelType(ModelType.EMBEDDING)
                .build();
        models.add(embeddingModel3);
        return ResponseEntity.ok(models);
    }
}
