package org.example.secret_message.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.secret_message.MessageHandler;
import org.example.secret_message.ServerDelegate;
import org.example.secret_message.data.RootKeyResponse;
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
public class RootKeyHandler implements MessageHandler {
	
	@Resource
	private ServerDelegate serverDelegate;
	@Resource
	private MessageGenerators messageGenerators;
	
	@Override
	public Set<MessageType> handledType() {
		return Set.of(MessageType.SET_ROOT_KEY,MessageType.CHANGE_ROOT_KEY);
	}
	
	@Override
	public void handle(Message message, Socket socket) {
		RootKeyResponse rootKeyResponse = messageGenerators.parseMessage(message, RootKeyResponse.class);
		if(rootKeyResponse !=null){
			serverDelegate.setRootKey(rootKeyResponse.getKey());
		}else{
			log.info("客户端 设置RootKey 失败");
		}
	}
}
