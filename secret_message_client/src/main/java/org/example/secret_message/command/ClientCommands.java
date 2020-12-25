package org.example.secret_message.command;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.secret_message.Command;
import org.example.secret_message.LoginStatus;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author weicheng.zhao
 * @date 2020/12/24
 */
@Slf4j
@Component
public class ClientCommands {

    private final Map<LoginStatus, List<String>> loginStatusMap = Maps.newHashMap();
    public final Map<String, Command> commandMap = Maps.newHashMap();

    @Resource
    private List<ClientCommand> clientCommandList;

    @PostConstruct
    public void postConstruct() {
        commandMap.putAll(clientCommandList.stream().collect(Collectors.toMap(Command::name, command -> command)));
        loginStatusMap.putAll(Arrays.stream(LoginStatus.values())
                .collect(Collectors.toMap(loginStatus -> loginStatus,
                        loginStatus -> clientCommandList.stream()
                                .filter(clientCommand -> clientCommand.canUseStatus().contains(loginStatus))
                                .map(Command::name).collect(Collectors.toList()))));
    }

    public void invoke(LoginStatus loginStatus, String command) {
        try {
            if (StringUtils.isBlank(command)) {
                return;
            }
            List<String> commandList = Splitter.on(" ").trimResults().omitEmptyStrings().limit(2).splitToList(command);
            if (CollectionUtils.isEmpty(commandList)) {
                return;
            }
            String commandName = commandList.get(0);
            if (!loginStatusMap.get(loginStatus).contains(commandName)) {
                log.error("当前状态下此命令不能使用或命令不存在，请检查");
                return;
            }
            commandMap.get(commandName).invoke(commandList.size() > 1 ? commandList.get(1) : "");
        } catch (Exception e) {
            log.error("执行命令 异常 {}", command);
        }
    }
}
