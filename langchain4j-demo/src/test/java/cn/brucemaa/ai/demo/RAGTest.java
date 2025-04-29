package cn.brucemaa.ai.demo;

import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.FileSystems;

@SpringBootTest
public class RAGTest {

    @Test
    public void testReadDocument() {

        // 加载单个文档
        var documents1 = FileSystemDocumentLoader.loadDocuments("/opt/test.txt");

        var documents2 = FileSystemDocumentLoader.loadDocuments("/opt/test.txt", new TextDocumentParser());

        // 从一个目录加载所有文档
        var documents3 = FileSystemDocumentLoader.loadDocuments("/opt/", new TextDocumentParser());

        // 从一个目录加载所有txt文档
        var pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.txt");
        var documents4 = FileSystemDocumentLoader.loadDocuments("/opt/", pathMatcher, new TextDocumentParser());

        // 从一个目录及其子目录加载所有文档
        var documents5 = FileSystemDocumentLoader.loadDocumentsRecursively("/opt/", new TextDocumentParser());
    }

    @Test
    public void testReadDocumentAndStore() {

        var documents1 = FileSystemDocumentLoader.loadDocuments("/opt/test.txt");

        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        EmbeddingStoreIngestor.ingest(documents1, embeddingStore);
    }
}
