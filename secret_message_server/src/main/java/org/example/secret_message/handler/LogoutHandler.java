package org.example.secret_message.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.secret_message.ClientDelegate;
import org.example.secret_message.MessageHandler;
import org.example.secret_message.Server;
import org.example.secret_message.user.User;
import org.example.secret_message.message.Message;
import org.example.secret_message.message.MessageType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.Socket;
import java.util.Optional;
import java.util.Set;

/**
 * @author weicheng.zhao
 * @date 2020/12/23
 */
@Slf4j
@Component
public class LogoutHandler implements MessageHandler {

    @Resource
    private Server server;

    @Override
    public Set<MessageType> handledType() {
        return Set.of(MessageType.LOGOUT);
    }

    @Override
    public void handle(Message message, Socket socket) {
        ClientDelegate clientDelegate = server.clientMap.get(socket);
	    String userName = Optional.ofNullable(clientDelegate.getUser()).map(User::getUserName).orElse("");
        clientDelegate.logout();
	    log.info("注销 成功 {}", userName);
    }

}
