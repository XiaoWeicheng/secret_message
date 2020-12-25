package org.example.secret_message;

/**
 * @author weicheng.zhao
 * @date 2020/12/24
 */
public interface Command {
	
	String name();
	
	String desc();
	
	void invoke(String param);
	
}
