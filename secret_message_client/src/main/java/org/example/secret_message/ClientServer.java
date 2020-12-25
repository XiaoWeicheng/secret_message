package org.example.secret_message;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.example.secret_message.message.Message;
import org.example.secret_message.util.JsonUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;

/**
 * @author weicheng.zhao
 */
@Slf4j
@Component
public class ClientServer{

    private static final Scanner SCANNER = new Scanner(System.in);

    public final Set<Socket> otherClientSet = Sets.newConcurrentHashSet();

    private ServerSocket serverSocket;

    @Resource
    private MessageHandlerContainer messageHandlerContainer;

    public void listenLocalPort() {
        while (true) {
            log.info("客户端 绑定本地端口 开始 请输入本地端口，例：2222");
            try {
                int localPort = Integer.parseInt(SCANNER.nextLine());
                serverSocket = new ServerSocket(localPort);
                break;
            } catch (IOException e) {
                log.error("客户端 绑定本地端口 失败 请重试",e);
            }
        }
        log.info("客户端 绑定本地端口 成功");
	    new Thread(this::listen, "listener").start();
    }

    private void listen() {
        while (Client.running) {
            try {
                log.info("客户端 监听本地端口 开始");
                Socket otherClientSocket = serverSocket.accept();
	            otherClientSet.add(otherClientSocket);
	            new Thread(() -> handleClientSocket(otherClientSocket)).start();
            } catch (IOException e) {
	            Client.close();
                log.error("客户端 监听本地端口 异常",e);
                return;
            }
        }
    }

    private void handleClientSocket(Socket otherClientSocket) {
        try (otherClientSocket) {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(otherClientSocket.getInputStream()));
            log.info("客户端 监听其他客户端消息 开始");
            String messageContent;
            while ((messageContent = bufferedReader.readLine()) != null) {
	            Message message = JsonUtils.parseJson(messageContent, Message.class);
	            if (message == null) {
		            log.error("客户端 监听其他客户端消息 消息错误");
		            continue;
	            }
                messageHandlerContainer.handle(JsonUtils.parseJson(messageContent, Message.class), otherClientSocket);
            }
            log.info("客户端 监听其他客户端消息 连接断开");
        } catch (Exception e) {
            otherClientSet.remove(otherClientSocket);
            log.error("客户端 监听其他客户端消息 异常", e);
        }
    }

    public int localPort() {
        return serverSocket.getLocalPort();
    }

    @PreDestroy
    public void destroy() {
	    for (Socket socket : otherClientSet) {
		    try {
		    socket.close();
			    log.info("客户端 断开其他客户端与本机连接 成功");
		    } catch (Exception e) {
			    log.info("客户端 断开其他客户端与本机连接 异常",e);
		    }
	    }
        try {
            serverSocket.close();
	        log.info("客户端 关闭本地端口监听 成功");
        } catch (Exception e) {
	        log.info("客户端 关闭本地端口监听 异常",e);
        }
    }

}
