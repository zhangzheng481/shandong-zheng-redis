package shandong.zheng.redis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shandong.zheng.redis.common.enums.ResponseEnum;
import shandong.zheng.redis.common.response.ServiceResponse;
import shandong.zheng.redis.entity.User;
import shandong.zheng.redis.service.RedisService;

import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;

/**
 * @author zhangzheng74@jd.com
 * @since 2025/3/10 10:53
 */
@RestController
@RequestMapping("/api/redis")
public class RedisController {
    private static Integer index = 0;
    @Resource
    private RedisService redisService;

    // 字符串操作接口
    @PostMapping("/set/string")
    public ServiceResponse<String> setString(@RequestBody Map<String, String> data) {
        ServiceResponse<String> response=new ServiceResponse<>(ResponseEnum.SUCCESS);
        redisService.setString(data.get("key")+index++, data.get("value"));
        response.setResult("String stored successfully");
        return response;
    }

    // 字符串操作接口
    @PostMapping("/get/string")
    public ServiceResponse<String> getString(@RequestBody Map<String, String> data) {
        ServiceResponse<String> response=new ServiceResponse<>(ResponseEnum.SUCCESS);
        response.setResult(redisService.getString(data.get("key")));
        return response;
    }

    // 用户操作接口
    @PostMapping("/user")
    public ServiceResponse<User> createUser(@RequestBody User user) {
        ServiceResponse<User> response=new ServiceResponse<>(ResponseEnum.SUCCESS);
        Long id = redisService.generateId("user_id");
        user.setId(id);
        redisService.saveUser(user);
        response.setResult(user);
        return response;
    }

    // 获取用户
    @GetMapping("/user/{id}")
    public ServiceResponse<User> getUser(@PathVariable Long id) {
        ServiceResponse<User> response=new ServiceResponse<>(ResponseEnum.SUCCESS);
        User user = redisService.getUser(id);
        response.setResult(user);
        return response;
    }

    // List操作接口
    @PostMapping("/list")
    public ServiceResponse<String> addToList(@RequestBody Map<String, Object> data) {
        ServiceResponse<String> response=new ServiceResponse<>(ResponseEnum.SUCCESS);
        redisService.addToList((String) data.get("key"), String.valueOf(data.get("value")));
        response.setResult("List added successfully");
        return response;
    }

    // 分布式锁测试接口
    @PostMapping("/lock")
    public ServiceResponse<String> testLock() {
        ServiceResponse<String> response=new ServiceResponse<>(ResponseEnum.SUCCESS);
        String lockKey = "resource_lock";
        String requestId = UUID.randomUUID().toString();

        if (redisService.tryLock(lockKey, requestId, 30)) {
            try {
                // 模拟业务操作
                Thread.sleep(5000);
                response.setResult("Lock acquired and operation completed");
                return response;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                response.setResult("Operation interrupted");
                return response;
            } finally {
                // 实际生产环境需实现锁释放校验
                redisService.releaseLock(lockKey,requestId);
            }
        }
        response.setResult("Failed to acquire lock");
        return response;
    }
}
