package cn.brucemaa.ai.demo.controller;

import cn.brucemaa.ai.demo.vo.StoreRequestVO;
import cn.brucemaa.ai.demo.vo.StoreResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@DependsOn({"simpleVectorStore"})
@RequestMapping(value = "/v1/store", consumes = MediaType.APPLICATION_JSON_VALUE)
public class StoreController {

    private final VectorStore simpleVectorStore;
    private final VectorStore vectorStore;

    public StoreController(SimpleVectorStore simpleVectorStore, VectorStore vectorStore) {
        this.simpleVectorStore = simpleVectorStore;
        this.vectorStore = vectorStore;
    }

    private VectorStore getVectorStore(StoreRequestVO storeRequestVO) {
        return switch (storeRequestVO.getType()) {
            case PG_VECTOR -> vectorStore;
            case null, default -> simpleVectorStore;
        };
    }

    @PostMapping
    public ResponseEntity<StoreResponseVO> store(@RequestBody StoreRequestVO storeRequestVO) {
        if (storeRequestVO.getMetadata() == null) {
            storeRequestVO.setMetadata(new HashMap<>());
        }
        storeRequestVO.getMetadata().put("innerMeta", "test");
        var document = Document.builder()
                .text(storeRequestVO.getText())
                .metadata(storeRequestVO.getMetadata())
                .build();
        getVectorStore(storeRequestVO).add(List.of(document));
        return ResponseEntity.ok(new StoreResponseVO(document.getId()));
    }

    @DeleteMapping(path = "/{documentId}")
    public ResponseEntity<String> delete(@PathVariable String documentId, @RequestBody StoreRequestVO storeRequestVO) {
        getVectorStore(storeRequestVO).delete(List.of(documentId));
        return ResponseEntity.ok("success");
    }

    @PostMapping(path = "/search")
    public ResponseEntity<List<Document>> search(@RequestBody StoreRequestVO storeRequestVO) {
        var filterExpressionBuilder = new FilterExpressionBuilder();
        var metadata = storeRequestVO.getMetadata();
        var expression = filterExpressionBuilder.eq("innerMeta", "test");
        if (metadata != null && !metadata.isEmpty()) {
            for (Map.Entry<String, Object> entry : metadata.entrySet()) {
                String k = entry.getKey();
                Object v = entry.getValue();
                expression = filterExpressionBuilder.and(expression, filterExpressionBuilder.eq(k, v));
            }
        }
        var searchRequest = SearchRequest.builder()
                .query(storeRequestVO.getText())
                .filterExpression(expression.build())
                .topK(5)
                .build();
        return ResponseEntity.ok(getVectorStore(storeRequestVO).similaritySearch(searchRequest));
    }
}
