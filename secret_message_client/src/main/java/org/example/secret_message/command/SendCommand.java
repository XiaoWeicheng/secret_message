package org.example.secret_message.command;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.example.secret_message.ClientDelegate;
import org.example.secret_message.LoginStatus;
import org.example.secret_message.ServerDelegate;
import org.example.secret_message.WaitSendMessage;
import org.example.secret_message.WaitUtils;
import org.example.secret_message.data.P2pMessage;
import org.example.secret_message.data.TempKeyRequest;
import org.example.secret_message.data.UserInfoRequest;
import org.example.secret_message.generator.MessageGenerators;
import org.example.secret_message.message.MessageType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author weicheng.zhao
 * @date 2020/12/24
 */
@Slf4j
@Component
public class SendCommand implements ClientCommand {

    @Resource
    private ServerDelegate serverDelegate;
    @Resource
    private ClientDelegate clientDelegate;
    @Resource
    private MessageGenerators messageGenerators;

    @Override
    public String name() {
        return "send";
    }

    @Override
    public String desc() {
        return "发送消息 -> send userName message";
    }

    @Override
    public void invoke(String param) {
        List<String> params = Splitter.on(" ").limit(2).splitToList(param);
        if (params.size() < 2) {
            log.info("参数缺失 {}", param);
            return;
        }
        String to = params.get(0);
        String message = params.get(1);
        WaitSendMessage waitSendMessage = new WaitSendMessage(UUID.randomUUID().toString(), to, message);
        if (!clientDelegate.clientMap.containsKey(to)) {
            serverDelegate.sendToServer(messageGenerators.generate(UUID.randomUUID().toString(), MessageType.USER_INFO,
                    new UserInfoRequest(to)));
            WaitUtils.wait(() -> clientDelegate.clientMap.containsKey(to), 1000);
        }
        serverDelegate.sendToServer(messageGenerators.generate(UUID.randomUUID().toString(), MessageType.TEMP_KEY,
                new TempKeyRequest(waitSendMessage.getMsgId())));
        WaitUtils.wait(() -> serverDelegate.keyMap.containsKey(waitSendMessage.getMsgId()), 10000);
        if (clientDelegate.sendToClient(to, messageGenerators.generate(waitSendMessage.getMsgId(),
                MessageType.P_2_P_MSG, new P2pMessage(serverDelegate.getUserName(), waitSendMessage.getMessage())))) {
        } else {
            log.info("客户端 向 {} 发送消息 失败 {}", to, message);
        }
    }

    @Override
    public Set<LoginStatus> canUseStatus() {
        return Set.of(LoginStatus.LOGIN);
    }

}
