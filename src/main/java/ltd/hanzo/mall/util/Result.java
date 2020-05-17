package ltd.hanzo.mall.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author 皓宇QAQ
 * @qq交流群 951485783
 * @email 2469653218@qq.com
 * @link https://github.com/Tianhaoy/hanzomall
 * 实现了Serializable接口 序列化
 */
@ApiModel(description = "响应对象")
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "响应码", name = "resultCode", required = true,example="200")
    private int resultCode;
    @ApiModelProperty(value = "响应消息", name = "message", required = true,example="SUCCESS")
    private String message;
    @ApiModelProperty(value = "响应数据", name = "data",example="json串")
    private T data;

    public Result() {
    }

    public Result(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "resultCode=" + resultCode +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
