package org.example.secret_message;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.secret_message.message.Message;
import org.example.secret_message.util.JsonUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author weicheng.zhao
 */
@Slf4j
@Component
public class ServerDelegate {

    private static final Scanner SCANNER = new Scanner(System.in);
    
	public final Map<String, byte[]> keyMap = Maps.newConcurrentMap();
    
    private Socket socket;
    @Getter
    private String userName;
    @Getter
    private byte[] rootKey;
    @Getter
    private LoginStatus loginStatus = LoginStatus.NOT_LOGIN;
	
	@Resource
    private MessageHandlerContainer messageHandlerContainer;

    public void connectToServer() {
        while (true) {
            log.info("客户端 连接服务器 开始 请输入服务器IP、服务器端口，例：127.0.0.1 8888");
            String ipPort = SCANNER.nextLine();
            List<String> ipPortList = Splitter.on(" ").trimResults().omitEmptyStrings().splitToList(ipPort);
            if (!CollectionUtils.isEmpty(ipPortList) && ipPortList.size() == 2) {
                try {
                    socket = new Socket(ipPortList.get(0), Integer.parseInt(ipPortList.get(1)));
                    break;
                } catch (IOException e) {
                    log.error("客户端 连接服务器 失败 请重试",e);
                }
            } else {
                log.info("客户端 连接服务器 服务器IP、服务器端口 输入错误 请重试");

            }
        }
        log.info("客户端 连接服务器 成功");
	    new Thread(this::handleServerSocket, "server-listener").start();
    }

    private void handleServerSocket() {
        try (Socket serverDelegateSocket = socket) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(serverDelegateSocket.getInputStream()));
            log.info("客户端 监听服务器消息 开始");
            String messageContent;
            while ((messageContent = bufferedReader.readLine()) != null) {
	            Message message = JsonUtils.parseJson(messageContent, Message.class);
	            if (message == null) {
		            log.error("客户端 监听服务器消息 消息错误");
		            continue;
	            }
	            messageHandlerContainer.handle(JsonUtils.parseJson(messageContent, Message.class), serverDelegateSocket);
            }
            log.info("客户端 监听服务器消息 连接断开");
        } catch (Exception e) {
            log.error("客户端 监听服务器消息 异常",e);
            Client.close();
        }
    }
	
	public boolean sendToServer(Message message) {
            String content = JsonUtils.toJsonString(message);
            if (StringUtils.isBlank(content)) {
                log.error("客户端 向服务器发送消息 消息为空");
                return false;
            }
	        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(content);
            writer.newLine();
            writer.flush();
            return true;
        } catch (Exception e) {
            log.error("客户端 向服务器发送消息 异常 {}", content, e);
            return false;
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void removeUserName() {
        this.userName = null;
    }

    public void setRootKey(byte[] rootKey) {
        this.rootKey = rootKey;
        loginStatus = LoginStatus.LOGIN;
    }

    public void logout() {
        this.userName = null;
        this.rootKey = null;
        loginStatus = LoginStatus.NOT_LOGIN;
    }

    @PreDestroy
    public void destroy() {
        try {
            socket.close();
	        log.info("客户端 关闭 成功");
        } catch (Exception e) {
	        log.info("客户端 关闭 异常",e);
        }
    }

}
