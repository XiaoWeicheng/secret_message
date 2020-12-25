package org.example.secret_message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author weicheng.zhao
 * @date 2020/12/24
 */
@RequiredArgsConstructor
@Getter
public class User {
	
	private final String userName;
	private final String password;
	
}
