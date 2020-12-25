package org.example.secret_message;

import lombok.extern.slf4j.Slf4j;
import org.example.secret_message.command.ClientCommands;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Scanner;

/**
 * @author weicheng.zhao
 */
@Slf4j
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@Component
public class Client {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static volatile boolean running = true;

    @Resource
    private ServerDelegate serverDelegate;
    @Resource
    private ClientServer clientServer;
    @Resource
    private ClientCommands clientCommands;

    public static void main(String[] args) {
        SpringApplication.run(Client.class, args);
        while (running) {
        }
    }

    @PostConstruct
    public void postConstruct() {
        serverDelegate.connectToServer();
	    clientServer.listenLocalPort();
        new Thread(this::handleUserInput, "input-handler").start();
	    log.info("客户端 启动 成功");
    }

	private void handleUserInput() {
		clientCommands.commandMap.values().forEach(command ->log.info(command.desc()));
        while (running) {
            String next = SCANNER.nextLine();
            clientCommands.invoke(serverDelegate.getLoginStatus(), next);
        }
    }

    public static void close() {
        running = false;
    }

    @PreDestroy
    public void destroy() {
        try {
	        log.info("客户端 关闭 成功");
        } catch (Exception e) {
	        log.info("客户端 关闭 异常",e);
        }
    }

}
