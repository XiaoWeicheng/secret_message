package org.example.secret_message;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.secret_message.message.Message;
import org.example.secret_message.util.JsonUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

/**
 * @author weicheng.zhao
 * @date 2020/12/22
 */
@Slf4j
@SpringBootApplication
@MapperScan("org.example.secret_message.user")
@Component
public class Server {
	
	private static final Scanner SCANNER = new Scanner(System.in);
	
    private static volatile boolean running = true;

    private ServerSocket serverSocket;
    public final Map<Socket, ClientDelegate> clientMap = Maps.newConcurrentMap();
    private final Map<Socket, BufferedWriter> writerMap = Maps.newConcurrentMap();

    @Resource
    private MessageHandlerContainer messageHandlerContainer;

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
        while (running) {
        }
    }

    @PostConstruct
    public void postConstruct() {
	    listenLocalPort();
        new Thread(this::listen, "listener").start();
	    log.info("服务器 启动 成功");
    }
	
	private void listenLocalPort() {
		while (true) {
			log.info("服务器 绑定本地端口 开始 请输入本地端口，例：8888");
			try {
				int localPort = Integer.parseInt(SCANNER.nextLine());
				serverSocket = new ServerSocket(localPort);
				break;
			} catch (IOException e) {
				log.error("服务器 绑定本地端口 失败 请重试",e);
			}
		}
		log.info("服务器 绑定本地端口 成功");
	}
	
	private void listen() {
        while (running) {
            try {
                log.info("服务器 监听本地端口 开始");
                Socket socket = serverSocket.accept();
                ClientDelegate clientDelegate = new ClientDelegate(socket);
                clientMap.put(socket, clientDelegate);
                clientDelegate.createHandlerThread(() -> handleSocket(clientDelegate)).start();
            } catch (IOException e) {
                log.error("服务器 监听本地端口 异常", e);
                return;
            }
        }
    }

    private void handleSocket(ClientDelegate clientDelegate) {
        Socket socket = clientDelegate.getSocket();
        try (socket) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writerMap.put(socket, new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            log.info("服务器 监听客户端消息 开始");
            String messageContent;
            while ((messageContent = bufferedReader.readLine()) != null) {
                log.info("服务器 监听客户端消息 收到消息 {} {}", clientDelegate.clientName(), messageContent);
                Message message = JsonUtils.parseJson(messageContent, Message.class);
                if (message == null) {
	                log.error("服务器 监听客户端消息 消息错误 {}", clientDelegate.clientName());
                    continue;
                }
                messageHandlerContainer.handle(message, socket);
            }
            log.error("服务器 监听客户端消息 连接断开");
        } catch (Exception e) {
            writerMap.remove(socket);
            clientMap.remove(socket);
            log.error("服务器 监听客户端消息 异常", e);
        }
    }

    public boolean sendToClient(ClientDelegate clientDelegate, Message message) {
        try {
            if (message == null) {
                log.error("服务器 向客户端发送消息 消息为空");
                return false;
            }
            String content = JsonUtils.toJsonString(message);
            if (StringUtils.isBlank(content)) {
                log.error("服务器 向客户端发送消息 消息为空");
                return false;
            }
            BufferedWriter writer = writerMap.get(clientDelegate.getSocket());
            writer.write(content);
            writer.newLine();
            writer.flush();
	        log.info("服务器 向客户端发送消息 成功 {}", content);
            return true;
        } catch (Exception e) {
            log.error("服务器 向客户端发送消息 异常", e);
            return false;
        }
    }

    @PreDestroy
    public void preDestroy() {
        try {
            serverSocket.close();
	        log.info("服务器 关闭 成功");
        } catch (Exception e) {
	        log.info("服务器 关闭 异常",e);
        }
    }
}
