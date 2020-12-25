package org.example.secret_message.generator;

import org.example.secret_message.message.EncryptType;
import org.example.secret_message.message.Message;
import org.example.secret_message.message.MessageType;

/**
 * @author weicheng.zhao
 * @date 2020/12/24
 */
public interface MessageGenerator {
	
	EncryptType encryptType();
	
	byte[] generate(String msgId, MessageType type, Object data) throws Throwable;
	
	byte[] parseMessage(Message message) throws Throwable;
	
}
