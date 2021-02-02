package org.verwandlung.voj.web.util;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@ApiModel
@Data
public class ResponseData {
//    @ApiModelProperty(value = "是否成功")
    private Boolean success;

//    @ApiModelProperty(value = "返回码")
    private Integer code;

//    @ApiModelProperty(value = "返回消息")
    private String message;

//    @ApiModelProperty(value = "返回数据")
    private Map<String, Object> data = new HashMap<String, Object>();

    private ResponseData(){}

    public static ResponseData ok(){
        ResponseData r = new ResponseData();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS);
        r.setMessage("成功");
        return r;
    }

    public static ResponseData error(){
        ResponseData r = new ResponseData();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR);
        r.setMessage("失败");
        return r;
    }

    public ResponseData success(Boolean success){
        this.setSuccess(success);
        return this;
    }

    public ResponseData message(String message){
        this.setMessage(message);
        return this;
    }

    public ResponseData code(Integer code){
        this.setCode(code);
        return this;
    }

    public ResponseData data(String key, Object value){
        this.data.put(key, value);
        return this;
    }

    public ResponseData data(Map<String, Object> map){
        this.setData(map);
        return this;
    }
}
