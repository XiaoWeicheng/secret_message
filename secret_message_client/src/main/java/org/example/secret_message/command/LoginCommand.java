package org.example.secret_message.command;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.example.secret_message.ClientServer;
import org.example.secret_message.LoginStatus;
import org.example.secret_message.ServerDelegate;
import org.example.secret_message.data.LoginRequest;
import org.example.secret_message.generator.MessageGenerators;
import org.example.secret_message.message.MessageType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author weicheng.zhao
 * @date 2020/12/24
 */
@Slf4j
@Component
public class LoginCommand implements ClientCommand {
	
	@Resource
	private ServerDelegate serverDelegate;
	@Resource
	private ClientServer clientServer;
    @Resource
    private MessageGenerators messageGenerators;

    @Override
    public String name() {
        return "login";
    }

    @Override
    public String desc() {
        return "登录 -> login userName password";
    }

    @Override
    public void invoke(String param) {
        List<String> params = Splitter.on(" ").limit(2).splitToList(param);
        if(params.size()<2){
	        log.info("参数缺失 {}", param);
	        return;
        }
        String userName = params.get(0);
        String password = params.get(1);
	    serverDelegate.setUserName(userName);
	    serverDelegate.sendToServer(messageGenerators.generate(UUID.randomUUID().toString(), MessageType.LOGIN,
                new LoginRequest(userName, password, clientServer.localPort())));
    }

    @Override
    public Set<LoginStatus> canUseStatus() {
        return Set.of(LoginStatus.NOT_LOGIN);
    }
}
