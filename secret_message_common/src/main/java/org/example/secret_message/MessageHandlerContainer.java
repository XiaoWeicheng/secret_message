package org.example.secret_message;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.example.secret_message.message.Message;
import org.example.secret_message.message.MessageType;
import org.example.secret_message.util.JsonUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author weicheng.zhao
 * @date 2020/12/23
 */
@Slf4j
@Component
public class MessageHandlerContainer {

    private final Map<MessageType, MessageHandler> handlerMap = Maps.newHashMap();

    @Resource
    private List<MessageHandler> messageHandlers;

    @PostConstruct
    public void postConstruct() {
        handlerMap.putAll(messageHandlers.stream()
                .flatMap(messageHandler -> messageHandler.handledType().stream()
                        .map(messageType -> Pair.of(messageType, messageHandler)))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight)));
    }

    /**
     * 处理消息
     * 
     * @param message 消息
     */
    public void handle(Message message, Socket socket) {
        try {
            handlerMap.get(message.getType()).handle(message, socket);
        } catch (Exception e) {
            log.info("无法处理此类型消息 {}", JsonUtils.toJsonString(message), e);
        }
    }

}
