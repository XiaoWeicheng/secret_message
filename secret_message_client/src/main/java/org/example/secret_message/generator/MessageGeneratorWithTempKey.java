package org.example.secret_message.generator;

import org.example.secret_message.ServerDelegate;
import org.example.secret_message.WaitUtils;
import org.example.secret_message.data.TempKeyRequest;
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
public class MessageGeneratorWithTempKey implements MessageGenerator {
	
	@Resource
    private ServerDelegate serverDelegate;
    @Resource
    private MessageGenerators messageGenerators;

    @Override
    public EncryptType encryptType() {
        return EncryptType.TEMP_KEY;
    }

    @Override
    public byte[] generate(String msgId, MessageType type, Object data) {
        return AesUtils.encrypt(JsonUtils.toJsonBytes(data), serverDelegate.keyMap.get(msgId));
    }

    @Override
    public byte[] parseMessage(Message message) {
        if (!serverDelegate.sendToServer(
                messageGenerators.generate(null, MessageType.TEMP_KEY, new TempKeyRequest(message.getId())))) {
            return new byte[0];
        }
        WaitUtils.wait(() -> serverDelegate.keyMap.containsKey(message.getId()), 1000);
        return AesUtils.decrypt(message.getData(), serverDelegate.keyMap.get(message.getId()));
    }

}
