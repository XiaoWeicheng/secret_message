package org.example.secret_message.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author weicheng.zhao
 * @date 2020/12/24
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserInfoResponse {
	
	private String userName;
	private String ip;
	private int port;
	
}
