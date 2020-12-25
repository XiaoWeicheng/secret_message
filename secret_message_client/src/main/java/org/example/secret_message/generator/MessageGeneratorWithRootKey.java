package org.example.secret_message.generator;

import org.example.secret_message.ServerDelegate;
import org.example.secret_message.message.EncryptType;
import org.example.secret_message.message.Message;
import org.example.secret_message.message.MessageType;
import org.example.secret_message.util.AesUtils;
import org.example.secret_message.util.JsonUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author weicheng.zhao
 * @date 2020/12/24
 */
@Component
public class MessageGeneratorWithRootKey implements MessageGenerator{
	
	@Resource
	private ServerDelegate serverDelegate;
	
	@Override
	public EncryptType encryptType() {
		return EncryptType.ROOT_KEY;
	}
	
	@Override
	public byte[] generate(String msgId, MessageType type, Object data) {
		return AesUtils.encrypt(JsonUtils.toJsonBytes(data), serverDelegate.getRootKey());
	}
	
	@Override
	public byte[] parseMessage(Message message) {
		return AesUtils.decrypt(message.getData(), serverDelegate.getRootKey());
	}
	
}
