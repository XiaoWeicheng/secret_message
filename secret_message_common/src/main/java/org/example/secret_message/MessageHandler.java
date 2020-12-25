package org.example.secret_message;

import org.example.secret_message.message.Message;
import org.example.secret_message.message.MessageType;

import java.net.Socket;
import java.util.Set;

/**
 * @author weicheng.zhao
 * @date 2020/12/23
 */
public interface MessageHandler {
	
	/**
	 * 消息处理类型
	 * @return MessageType
	 */
	Set<MessageType> handledType();
	
	/**
	 * 处理消息
	 * @param message 消息
	 */
	void handle(Message message, Socket socket);
	
}
