package shandong.zheng.redis.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangzheng481@163.com
 * @since 2025/3/7 13:29
 */
@Getter
@AllArgsConstructor
public enum ResponseEnum {
    //枚举
    SUCCESS("0", "成功"),
    FAIL("-1", "失败"),
    ;
    //属性
    private final String code;
    private final String msg;
    //缓存map
    private static final Map<String, ResponseEnum> map=new HashMap<>();
    //静态初始化
    static {
        for (ResponseEnum responseEnum : ResponseEnum.values()) {
            map.put(responseEnum.getCode(), responseEnum);
        }
    }
    //存在
    public static boolean exist(String code) {
        return map.containsKey(code);
    }
    //获取
    public static ResponseEnum get(String code) {
        return map.get(code);
    }
    //匹配
    public boolean match(String code) {
        return this.code.equals(code);
    }

}
