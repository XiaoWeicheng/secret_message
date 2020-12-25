package org.example.secret_message.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.secret_message.MessageHandler;
import org.example.secret_message.ServerDelegate;
import org.example.secret_message.command.SendCommand;
import org.example.secret_message.data.TempKeyResponse;
import org.example.secret_message.generator.MessageGeneratorWithTempKey;
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
public class TempKeyHandler implements MessageHandler {

    @Resource
    private ServerDelegate serverDelegate;
    @Resource
    private SendCommand sendCommand;
    @Resource
    private MessageGenerators messageGenerators;
    @Resource
    private MessageGeneratorWithTempKey messageGeneratorWithTempKey;

    @Override
    public Set<MessageType> handledType() {
        return Set.of(MessageType.TEMP_KEY);
    }

    @Override
    public void handle(Message message, Socket socket) {
        TempKeyResponse tempKeyResponse = messageGenerators.parseMessage(message, TempKeyResponse.class);
        if (tempKeyResponse == null) {
            log.info("客户端 处理临时密钥回复 response为空 {}", message.getData());
            return;
        }
        if (tempKeyResponse.getKey() == null) {
            log.info("客户端 处理临时密钥回复 key为空 {}", tempKeyResponse.getId());
        } else {
            serverDelegate.keyMap.put(tempKeyResponse.getId(), tempKeyResponse.getKey());
        }
    }

}
