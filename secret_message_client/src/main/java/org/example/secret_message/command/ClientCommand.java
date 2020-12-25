package org.example.secret_message.command;

import org.example.secret_message.Command;
import org.example.secret_message.LoginStatus;

import java.util.Set;

/**
 * @author weicheng.zhao
 * @date 2020/12/24
 */
public interface ClientCommand extends Command {
	
	Set<LoginStatus> canUseStatus();
}
