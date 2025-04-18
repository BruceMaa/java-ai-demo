package cn.brucemaa.ai.demo.vo;

import cn.brucemaa.ai.demo.constant.ModelType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ModelResponseVO {

    String modelId;

    String modelName;

    ModelType modelType;
}
