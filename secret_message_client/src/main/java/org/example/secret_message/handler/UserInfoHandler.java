package org.example.secret_message.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.secret_message.ClientDelegate;
import org.example.secret_message.MessageHandler;
import org.example.secret_message.command.SendCommand;
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
    private ClientDelegate clientDelegate;
    @Resource
    private SendCommand sendCommand;
    @Resource
    private MessageGenerators messageGenerators;

    @Override
    public Set<MessageType> handledType() {
        return Set.of(MessageType.USER_INFO);
    }

    @Override
    public void handle(Message message, Socket socket) {
        UserInfoResponse userInfoResponse = messageGenerators.parseMessage(message, UserInfoResponse.class);
        if (userInfoResponse == null) {
            log.info("获取用户信息 返回错误 {}", message.getData());
            return;
        }
        String userName = userInfoResponse.getUserName();
        if (!StringUtils.isBlank(userInfoResponse.getIp()) && userInfoResponse.getPort() > 0) {
            try {
                clientDelegate.clientMap.put(userName,
                        new Socket(userInfoResponse.getIp(), userInfoResponse.getPort()));
            } catch (Exception e) {
                log.info("获取用户信息 建立连接失败 {}", userName, e);
            }
        } else {
            log.info("获取用户信息 对方不存在或未登录 {}", userName);
        }
    }

}
