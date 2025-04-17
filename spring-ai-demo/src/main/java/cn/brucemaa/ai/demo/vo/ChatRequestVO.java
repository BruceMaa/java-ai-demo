package cn.brucemaa.ai.demo.vo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * 对话请求对象
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatRequestVO {

    /**
     * 用户输入的文字内容
     */
    String content;

    /**
     * 是否流式响应
     */
    boolean streamResp = false;
}
