package org.example.secret_message;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.secret_message.user.User;

import java.net.Socket;
import java.util.Map;
import java.util.Optional;

/**
 * @author weicheng.zhao
 * @date 2020/12/24
 */
@RequiredArgsConstructor
@Getter
public class ClientDelegate {

    public static final Map<String, ClientDelegate> CLIENT_MAP = Maps.newConcurrentMap();

    private final Socket socket;
    private User user;
    private int port;
    @Setter
    private byte[] rootKey;
    private Thread handlerThread;

    public String clientName() {
        return socket.getInetAddress().getHostAddress() + "-" + socket.getPort()
                + Optional.ofNullable(user).map(User::getUserName).map(userName -> "-" + userName).orElse("");
    }

    public Thread createHandlerThread(Runnable runnable) {
        handlerThread = new Thread(runnable, clientName());
        return handlerThread;
    }

    public void login(User user, int port) {
        this.user = user;
        this.port = port;
        handlerThread.setName(clientName());
        CLIENT_MAP.put(user.getUserName(), this);
    }

    public void logout() {
        CLIENT_MAP.remove(user.getUserName());
        user = null;
        port = 0;
        handlerThread.setName(clientName());
    }

    public static boolean judgeLogin(String userName) {
        return CLIENT_MAP.containsKey(userName);
    }
}
