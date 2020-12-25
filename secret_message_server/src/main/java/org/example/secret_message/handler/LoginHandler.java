package org.example.secret_message.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.secret_message.ClientDelegate;
import org.example.secret_message.KeyDispatcher;
import org.example.secret_message.MessageHandler;
import org.example.secret_message.Server;
import org.example.secret_message.User;
import org.example.secret_message.data.LoginRequest;
import org.example.secret_message.generator.MessageGenerators;
import org.example.secret_message.message.Message;
import org.example.secret_message.message.MessageType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.Socket;
import java.util.Objects;
import java.util.Set;

/**
 * @author weicheng.zhao
 * @date 2020/12/23
 */
@Slf4j
@Component
public class LoginHandler implements MessageHandler {

    @Resource
    private Server server;
    @Resource
    private KeyDispatcher keyDispatcher;
    @Resource
    private MessageGenerators messageGenerators;

    @Override
    public Set<MessageType> handledType() {
        return Set.of(MessageType.LOGIN);
    }

    @Override
    public void handle(Message message, Socket socket) {
        ClientDelegate clientDelegate = server.clientMap.get(socket);
        LoginRequest loginRequest = messageGenerators.parseMessage(message, LoginRequest.class, clientDelegate);
        if (loginRequest == null) {
            log.info("登录失败 参数错误 {}", message.getData());
            server.sendToClient(clientDelegate,
                    messageGenerators.generate(null, MessageType.LOGIN_FAILED, "参数错误", clientDelegate));
            return;
        }
        if (!validateUser(loginRequest) || !validatePassword(loginRequest)) {
            log.info("登录失败 用户名或错误 {}", message.getData());
            server.sendToClient(clientDelegate,
                    messageGenerators.generate(null, MessageType.LOGIN_FAILED, "用户名或密码错误", clientDelegate));
            return;
        }
        clientDelegate.login(new User(loginRequest.getUserName(), loginRequest.getPassword()), loginRequest.getPort());
        keyDispatcher.dispatchRootKey(clientDelegate);
    }

    private boolean validateUser(LoginRequest loginRequest) {
        return StringUtils.isNotBlank(loginRequest.getUserName()) && loginRequest.getUserName().startsWith("test");
    }

    private boolean validatePassword(LoginRequest loginRequest) {
        return Objects.equals(loginRequest.getPassword(), "1");
    }
}
