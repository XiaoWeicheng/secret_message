package org.example.secret_message.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.secret_message.MessageHandler;
import org.example.secret_message.data.P2pMessage;
import org.example.secret_message.generator.MessageGenerators;
import org.example.secret_message.message.Message;
import org.example.secret_message.message.MessageType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.Socket;
import java.util.Set;

/**
 * @author weicheng.zhao
 * @date 2020/12/23
 */
@Slf4j
@Component
public class P2pMessageHandler implements MessageHandler {
	
    @Resource
    private MessageGenerators messageGenerators;

    @Override
    public Set<MessageType> handledType() {
        return Set.of(MessageType.P_2_P_MSG);
    }

    @Override
    public void handle(Message message, Socket socket) {
	    P2pMessage p2pMessage = messageGenerators.parseMessage(message, P2pMessage.class);
        if (p2pMessage == null) {
            log.info("客户端 处理其他客户端消息 消息为空 {}", message.getData());
            return;
        }
        log.info("收到消息 {} ===> {}", p2pMessage.getFrom(),p2pMessage.getContent());
    }

}
