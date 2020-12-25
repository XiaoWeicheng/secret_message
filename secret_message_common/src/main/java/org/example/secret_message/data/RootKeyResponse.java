package org.example.secret_message.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author weicheng.zhao
 * @date 2020/12/23
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RootKeyResponse {
	
	private String id;
	private byte[] key;
	
}
