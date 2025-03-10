package shandong.zheng.redis.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shandong.zheng.redis.common.enums.ResponseEnum;

import java.io.Serializable;

/**
 * @author zhangzheng481@163.com
 * @since 2025/3/7 11:38
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ServiceResponse<T> implements Serializable {
    private static final long serialVersionUID = 2488663702267110932L;
    private String code;
    private String msg;
    private Object detail;
    private T result;
    private String responseId;
    private Integer status;

    /**
     * 构造器
     * @param responseEnum 响应枚举
     */
    public ServiceResponse(ResponseEnum responseEnum){
        this.code = responseEnum.getCode();
        this.msg = responseEnum.getMsg();
    }

    /**
     * 构造器
     * @param responseEnum
     * @param detail
     */
    public ServiceResponse(ResponseEnum responseEnum,Object detail) {
        this.code = responseEnum.getCode();
        this.msg = responseEnum.getMsg();
        this.detail = detail;
    }
}
