package org.example.secret_message.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author weicheng.zhao
 * @date 2020/12/25
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class P2pMessage {
	
	private String from;
	private String content;
	
}
