package org.example.secret_message.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author weicheng.zhao
 * @date 2020/12/22
 */
@RequiredArgsConstructor
@Getter
public enum MessageType {
	
	/**
	 * 登录
	 */
	LOGIN(EncryptType.NONE),
	/**
	 * 登录失败
	 */
	LOGIN_FAILED(EncryptType.NONE),
	/**
	 * 登录
	 */
	LOGOUT(EncryptType.ROOT_KEY),
	/**
	 * 用户信息
	 */
	USER_INFO(EncryptType.ROOT_KEY),
	/**
	 * 设置ROOT_KEY
	 */
	SET_ROOT_KEY(EncryptType.NONE),
	/**
	 * 替换ROOT_KEY
	 */
	CHANGE_ROOT_KEY(EncryptType.ROOT_KEY),
	/**
	 * 临时KEY
	 */
	TEMP_KEY(EncryptType.ROOT_KEY),
	/**
	 * 客户端通讯
	 */
	P_2_P_MSG(EncryptType.TEMP_KEY),
	
	;
	
	private final EncryptType encryptType;
}
