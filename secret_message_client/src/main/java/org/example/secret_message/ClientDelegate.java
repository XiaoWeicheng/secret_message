package org.example.secret_message;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.secret_message.command.ClientCommands;
import org.example.secret_message.message.Message;
import org.example.secret_message.util.JsonUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;

/**
 * @author weicheng.zhao
 */
@Slf4j
@Component
public class ClientDelegate {
	
	public final Map<String, Socket> clientMap = Maps.newConcurrentMap();
	
    @Resource
    private ServerDelegate serverDelegate;
    @Resource
    private ClientServer clientServer;
    @Resource
    private ClientCommands clientCommands;

    public boolean sendToClient(String to, Message message){
	    if (message == null) {
		    log.error("客户端 向其他客户端发送消息 消息为空 {}",to);
		    return false;
	    }
	    String content = JsonUtils.toJsonString(message);
	    if (StringUtils.isBlank(content)) {
		    log.error("客户端 向其他客户端发送消息 消息为空 {}",to);
		    return false;
	    }
	    try {
		    Socket socket = clientMap.get(to);
		    if(socket == null){
			    log.info("客户端 向其他客户端发送消息 与 {} 未建立连接 {}",to, content);
			    return false;
		    }
		    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		    writer.write(content);
		    writer.newLine();
		    writer.flush();
		    return true;
	    } catch (Exception e) {
		    log.error("客户端 向其他客户端发送消息 异常 {}", content, e);
		    return false;
	    }
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
