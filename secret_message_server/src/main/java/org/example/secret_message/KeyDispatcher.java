package org.example.secret_message;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.example.secret_message.data.RootKeyResponse;
import org.example.secret_message.data.TempKeyResponse;
import org.example.secret_message.generator.MessageGenerators;
import org.example.secret_message.message.MessageType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author weicheng.zhao
 * @date 2020/12/24
 */
@Slf4j
@Component
public class KeyDispatcher {

    private final KeyGenerator keyGenerator;
    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(4);
    private final Cache<String, byte[]> keyCache = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS)
            .expireAfterAccess(60, TimeUnit.SECONDS).build();

    @Resource
    private Server server;
    @Resource
    private MessageGenerators messageGenerators;

    public KeyDispatcher() throws NoSuchAlgorithmException {
        keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
    }

    public void dispatchRootKey(ClientDelegate clientDelegate) {
        byte[] key = generateKey();
        log.info("RootKey派发 首次生成 {} {}", clientDelegate.getUser().getUserName(), key);
        if (server.sendToClient(clientDelegate, messageGenerators.generate(UUID.randomUUID().toString(),
                MessageType.SET_ROOT_KEY, new RootKeyResponse(null, key), clientDelegate))) {
            clientDelegate.setRootKey(key);
            scheduledExecutorService.schedule(() -> updateRootKey(clientDelegate), 1, TimeUnit.MINUTES);
            log.info("RootKey派发 派发成功 {} {}", clientDelegate.getUser().getUserName(), key);
            return;
        }
        log.error("RootKey派发 派发失败 {} {}", clientDelegate.getUser().getUserName(), key);
    }

    private void updateRootKey(ClientDelegate clientDelegate) {
        byte[] key = generateKey();
        log.info("RootKey派发 更新生成 {} {}", clientDelegate.getUser().getUserName(), key);
        if (server.sendToClient(clientDelegate, messageGenerators.generate(UUID.randomUUID().toString(),
                MessageType.CHANGE_ROOT_KEY, new RootKeyResponse(null, key), clientDelegate))) {
            clientDelegate.setRootKey(key);
            scheduledExecutorService.schedule(() -> updateRootKey(clientDelegate), 1, TimeUnit.MINUTES);
            log.info("RootKey派发 更新成功 {} {}", clientDelegate.getUser().getUserName(), key);
            return;
        }
        log.error("RootKey派发 更新失败 {} {}", clientDelegate.getUser().getUserName(), key);
    }

    public void dispatchTempKey(ClientDelegate clientDelegate, String msgId) {
        byte[] key = new byte[0];
        try {
            key = keyCache.get(msgId, this::generateKey);
            if (server.sendToClient(clientDelegate, messageGenerators.generate(null, MessageType.TEMP_KEY,
                    new TempKeyResponse(msgId, key), clientDelegate))) {
                scheduledExecutorService.schedule(() -> updateRootKey(clientDelegate), 1, TimeUnit.MINUTES);
                log.info("TempKey派发 成功 {} {} {}", clientDelegate.getUser().getUserName(), msgId, key);
                return;
            }
            log.error("TempKey派发 失败 {} {} {}", clientDelegate.getUser().getUserName(), msgId, key);
        } catch (ExecutionException e) {
            log.info("TempKey派发 异常 {} {} {}", clientDelegate.getUser().getUserName(), msgId, key);
        }
    }

    private byte[] generateKey() {
        return keyGenerator.generateKey().getEncoded();
    }
}
