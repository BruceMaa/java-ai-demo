package cn.brucemaa.ai.demo.vo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * 对话返回对象
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatResponseVO {
    /**
     * AI返回的文字内容
     */
    String content;

    /**
     * 是否流式响应
     */
    boolean streamResp = false;
}
