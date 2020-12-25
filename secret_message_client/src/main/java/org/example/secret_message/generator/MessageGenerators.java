package org.example.secret_message.generator;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.example.secret_message.message.EncryptType;
import org.example.secret_message.message.Message;
import org.example.secret_message.message.MessageType;
import org.example.secret_message.util.JsonUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author weicheng.zhao
 * @date 2020/12/24
 */
@Slf4j
@Component
public class MessageGenerators {

    private final Map<EncryptType, MessageGenerator> messageGeneratorMap = Maps.newHashMap();

    @Resource
    private List<MessageGenerator> messageGeneratorList;

    @PostConstruct
    public void postConstruct() {
        messageGeneratorMap.putAll(messageGeneratorList.stream()
                .collect(Collectors.toMap(MessageGenerator::encryptType, messageGenerator -> messageGenerator)));
    }

    public Message generate(String msgId, MessageType type, Object data) {
        try {
	        byte[] encryptedData = messageGeneratorMap.get(type.getEncryptType()).generate(msgId, type, data);
            return ArrayUtils.isEmpty(encryptedData) ? null : new Message(msgId, type, encryptedData);
        } catch (Throwable throwable) {
            log.info("加密消息 异常 {} {} {}", msgId, type, JsonUtils.toJsonString(data), throwable);
            return null;
        }
    }

    public <T> T parseMessage(Message message, Class<T> tClass) {
        try {
	        byte[] originData = messageGeneratorMap.get(message.getType().getEncryptType()).parseMessage(message);
            return JsonUtils.parseJson(originData, tClass);
        } catch (Throwable throwable) {
            log.info("解密消息 异常 {}", JsonUtils.toJsonString(message), throwable);
            return null;
        }
    }

}
