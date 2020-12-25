package org.example.secret_message.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.secret_message.MessageHandler;
import org.example.secret_message.ServerDelegate;
import org.example.secret_message.generator.MessageGenerators;
import org.example.secret_message.message.Message;
import org.example.secret_message.message.MessageType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.Socket;
import java.util.Set;

/**
 * @author weicheng.zhao
 * @date 2020/12/24
 */
@Slf4j
@Component
public class LoginFailedHandler implements MessageHandler {
	
	@Resource
	private ServerDelegate serverDelegate;
    @Resource
	private MessageGenerators messageGenerators;

    @Override
    public Set<MessageType> handledType() {
        return Set.of(MessageType.LOGIN_FAILED);
    }

    @Override
	public void handle(Message message, Socket socket) {
		serverDelegate.removeUserName();
		log.error("客户端 登录 失败 {}", messageGenerators.parseMessage(message,String.class));
	}
}
