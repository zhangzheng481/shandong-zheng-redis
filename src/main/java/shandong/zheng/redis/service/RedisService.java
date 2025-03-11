package shandong.zheng.redis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import shandong.zheng.redis.entity.User;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangzheng74@jd.com
 * @since 2025/3/10 10:51
 */
@Slf4j
@Service
public class RedisService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 字符串操作
    public void setString(String key, String value) {
        log.info("set string key: {}, value: {}", key, value);
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    // Hash操作（用户存储）
    public void saveUser(User user) {
        stringRedisTemplate.opsForHash().put("users", user.getId().toString(), user);
    }

    public User getUser(Long id) {
        return (User) stringRedisTemplate.opsForHash().get("users", id.toString());
    }

    // 自增ID生成
    public Long generateId(String key) {
        return stringRedisTemplate.opsForValue().increment(key, 1);
    }

    // 分布式锁
    public boolean tryLock(String lockKey, String requestId, long expireSeconds) {
        return Boolean.TRUE.equals(
                stringRedisTemplate.opsForValue().setIfAbsent(
                        lockKey,
                        requestId,
                        expireSeconds,
                        TimeUnit.SECONDS
                )
        );
    }

    /**
     * 安全释放分布式锁
     * @param lockKey 锁的key
     * @param requestId 加锁时的唯一标识
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String requestId) {
        try {
            // Lua脚本（原子性验证并删除）
            String script =
                    "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                            "    return redis.call('del', KEYS[1]) " +
                            "else " +
                            "    return 0 " +
                            "end";

            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            redisScript.setResultType(Long.class);

            // 执行脚本
            Object result = (Long) stringRedisTemplate.execute(
                    redisScript,
                    Collections.singletonList(lockKey), // KEYS[1]
                    requestId                           // ARGV[1]
            );

            return result != null && "1".equals(result);
        } catch (Exception e) {
            // 添加异常日志
            log.error("释放锁异常，lockKey: {}, requestId: {}", lockKey, requestId, e);
            return false;
        }
    }

    // List操作
    public void addToList(String key, String value) {
        stringRedisTemplate.opsForList().rightPush(key, value);
    }

    public List<String> getList(String key) {
        return stringRedisTemplate.opsForList().range(key, 0, -1);
    }
}
