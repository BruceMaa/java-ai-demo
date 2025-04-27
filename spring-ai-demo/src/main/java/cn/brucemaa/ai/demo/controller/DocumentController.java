package cn.brucemaa.ai.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/documents", consumes = MediaType.APPLICATION_JSON_VALUE)
public class DocumentController {

    private final VectorStore vectorStore;

    @GetMapping(path = "/test")
    public List<Document> documents(@RequestParam(value = "message", defaultValue = "钢铁时怎样炼成的") String message) {
        var documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .filterExpression(new FilterExpressionBuilder().eq("innerMeta", "test").build())
                .build();
        return documentRetriever.retrieve(new Query(message));
    }
}
