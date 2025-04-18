package cn.brucemaa.ai.demo.vo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseChatOptions {
    String model;

    Double frequencyPenalty;

    Integer maxTokens;

    Double presencePenalty;

    List<String> stopSequences;

    Double temperature;

    Integer topK;

    Double topP;
}
