package org.example.secret_message.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.secret_message.ClientDelegate;
import org.example.secret_message.KeyDispatcher;
import org.example.secret_message.MessageHandler;
import org.example.secret_message.Server;
import org.example.secret_message.data.TempKeyRequest;
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
    private Server server;
    @Resource
    private KeyDispatcher keyDispatcher;
	@Resource
	private MessageGenerators messageGenerators;

    @Override
    public Set<MessageType> handledType() {
        return Set.of(MessageType.TEMP_KEY);
    }

    @Override
    public void handle(Message message, Socket socket) {
        ClientDelegate clientDelegate = server.clientMap.get(socket);
	    TempKeyRequest tempKeyRequest = messageGenerators.parseMessage(message, TempKeyRequest.class,clientDelegate);
        if (tempKeyRequest == null || StringUtils.isBlank(tempKeyRequest.getMessageId())) {
            log.info("处理临时密钥请求 参数错误 {}", message.getData());
            return;
        }
        keyDispatcher.dispatchTempKey(clientDelegate,tempKeyRequest.getMessageId());
    }
    
}
