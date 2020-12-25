package org.example.secret_message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author weicheng.zhao
 * @date 2020/12/25
 */
@RequiredArgsConstructor
@Setter
@Getter
public final class WaitSendMessage {
	private final String msgId;
	private final String to;
	private final String message;
}
