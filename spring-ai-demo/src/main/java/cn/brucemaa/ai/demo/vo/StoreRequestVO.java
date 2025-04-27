package cn.brucemaa.ai.demo.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreRequestVO {

    VectorStoreType type;

    /**
     * 文档id
     */
    String documentId;

    /**
     * 文字内容
     */
    String text;

    /**
     * 元数据
     */
    Map<String, Object> metadata;

    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public enum VectorStoreType {
        SIMPLE,
        DASH_SCOPE,
        PG_VECTOR,
        ;
    }
}
