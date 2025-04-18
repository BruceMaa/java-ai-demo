package cn.brucemaa.ai.demo.vo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * 对话请求对象
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatRequestVO {

    /**
     * 模型
     */
    String modelId;

    /**
     * 用于控制随机性和多样性的程度。
     * 具体来说，temperature值控制了生成文本时对每个候选词的概率分布进行平滑的程度。
     * 较高的temperature值会降低概率分布的峰值，使得更多的低概率词被选择，生成结果更加多样化；
     * 而较低的temperature值则会增强概率分布的峰值，使得高概率词更容易被选择，生成结果更加确定。
     * 取值范围：[0, 2)，系统默认值0.85。
     * 不建议取值为0，无意义。
     */
    Double temperature;

    /**
     * 生成时使用的随机数种子，用户控制模型生成内容的随机性。
     * seed支持无符号64位整数。
     * 在使用seed时，模型将尽可能生成相同或相似的结果，但目前不保证每次生成的结果完全相同。
     */
    Integer seed;

    /**
     * 生成时，核采样方法的概率阈值。
     * 例如，取值为0.8时，仅保留累计概率之和大于等于0.8的概率分布中的token，作为随机采样的候选集。
     * 取值范围为（0,1.0)，
     * 取值越大，生成的随机性越高；
     * 取值越低，生成的随机性越低。
     * 默认值为0.8。
     * 注意，取值不要大于等于1
     */
    Double topP;

    /**
     * 生成时，采样候选集的大小。
     * 例如，取值为50时，仅将单次生成中得分最高的50个token组成随机采样的候选集。
     * 取值越大，生成的随机性越高；
     * 取值越小，生成的确定性越高。
     * 注意：如果top_k参数为空或者top_k的值大于100，表示不启用top_k策略，此时仅有top_p策略生效，默认是空。
     */
    Integer topK;

    /**
     * <ul>
     *   <li>stop参数用于实现内容生成过程的精确控制，在生成内容即将包含指定的字符串或token_ids时自动停止，生成内容不包含指定的内容。
     *       <p>例如，如果指定stop为"你好"，表示将要生成"你好"时停止；如果指定stop为[37763, 367]，表示将要生成"Observation"时停止。
     *   <li>stop参数支持以list方式传入字符串数组或者token_ids数组，支持使用多个stop的场景。
     * </ul>
     *
     * <q>说明 list模式下不支持字符串和token_ids混用，list模式下元素类型要相同。</q>
     */
    List<Object> stop;

    /**
     * 模型内置了互联网搜索服务，该参数控制模型在生成文本时是否参考使用互联网搜索结果。取值如下：
     * true：启用互联网搜索，模型会将搜索结果作为文本生成过程中的参考信息，但模型会基于其内部逻辑"自行判断"是否使用互联网搜索结果。
     * false（默认）：关闭互联网搜索。
     */
    Boolean enableSearch = false;

    Integer maxTokens;

    /**
     * 控制在流式输出模式下是否开启增量输出，即后续输出内容是否包含已输出的内容。
     * 设置为True时，将开启增量输出模式，后面输出不会包含已经输出的内容，您需要自行拼接整体输出；
     * 设置为False则会包含已输出的内容。
     */
    Boolean incrementalOutput = true;

    /**
     * 用于控制模型生成时的重复度。
     * 提高repetition_penalty时可以降低模型生成的重复度。
     * 1.0表示不做惩罚。
     * 默认为1.1。
     */
    Double repetitionPenalty;

    /**
     * 用户输入的文字内容
     */
    String content;

    /**
     * 是否流式响应
     */
    boolean streamResp = false;
}
