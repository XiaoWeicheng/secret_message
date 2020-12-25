package org.example.secret_message.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

/**
 * @author weicheng.zhao
 * @date 2020/12/22
 */
@Getter
public final class Message {

    private final String id;
    private final MessageType type;
    private final byte[] data;

    @JsonCreator
    public Message(@JsonProperty("id") String id, @JsonProperty("type") MessageType type,
            @JsonProperty("data") byte[] data) {
        this.id = id;
        this.type = type;
        this.data = data;
    }
}
