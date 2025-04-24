package cn.brucemaa.ai.demo.vo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreRequestVO {

    /**
     * 文字内容
     */
    String text;

    /**
     * 元数据
     */
    Map<String, Object> metadata;
}
