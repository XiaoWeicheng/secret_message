package org.example.secret_message.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.secret_message.ClientDelegate;
import org.example.secret_message.MessageHandler;
import org.example.secret_message.Server;
import org.example.secret_message.data.UserInfoRequest;
import org.example.secret_message.data.UserInfoResponse;
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
public class UserInfoHandler implements MessageHandler {

    @Resource
    private Server server;
    @Resource
    private MessageGenerators messageGenerators;

    @Override
    public Set<MessageType> handledType() {
        return Set.of(MessageType.USER_INFO);
    }

    @Override
    public void handle(Message message, Socket socket) {
        ClientDelegate clientDelegate = server.clientMap.get(socket);
        UserInfoRequest userInfoRequest = messageGenerators.parseMessage(message, UserInfoRequest.class,
                clientDelegate);
        if (userInfoRequest == null || !ClientDelegate.judgeLogin(userInfoRequest.getUserName())) {
            log.info("获取用户信息 参数错误 {}", message.getData());
            return;
        }
        String userName = userInfoRequest.getUserName();
        if (!ClientDelegate.judgeLogin(userInfoRequest.getUserName())) {
            log.info("获取用户信息 对方不存在或未登录 {}", message.getData());
            server.sendToClient(clientDelegate, messageGenerators.generate("", MessageType.USER_INFO,
                    new UserInfoResponse(userName, null, 0), clientDelegate));
            return;
        }
        ClientDelegate target = ClientDelegate.CLIENT_MAP.get(userName);
        server.sendToClient(clientDelegate,
                messageGenerators.generate(
                        "", MessageType.USER_INFO, new UserInfoResponse(userName,
                                target.getSocket().getInetAddress().getHostAddress(), target.getPort()),
                        clientDelegate));
    }

}
