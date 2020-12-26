package org.example.secret_message.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.secret_message.ClientDelegate;
import org.example.secret_message.KeyDispatcher;
import org.example.secret_message.MessageHandler;
import org.example.secret_message.Server;
import org.example.secret_message.data.LoginRequest;
import org.example.secret_message.generator.MessageGenerators;
import org.example.secret_message.message.Message;
import org.example.secret_message.message.MessageType;
import org.example.secret_message.user.User;
import org.example.secret_message.user.UserMapper;
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
public class LoginHandler implements MessageHandler {

    @Resource
    private UserMapper userMapper;
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
        User user = userMapper.selectByUserNameAndPassword(loginRequest.getUserName(), loginRequest.getPassword());
        if (user == null) {
            log.info("登录失败 用户名或密码错误 {}", message.getData());
            server.sendToClient(clientDelegate,
                    messageGenerators.generate(null, MessageType.LOGIN_FAILED, "用户名或密码错误", clientDelegate));
            return;
        }
        clientDelegate.login(user, loginRequest.getPort());
        keyDispatcher.dispatchRootKey(clientDelegate);
    }

}
