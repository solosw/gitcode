package com.solosw.codelab.entity.bo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ResponseBo {
    Object data;
    Integer status;
    String message;
    public static ResponseBo getSuccess(Object data)
    {
        return new ResponseBo().setData(data).setMessage("success").setStatus(200);
    }
    public static ResponseBo getFail(Object data,String message,Integer status)
    {
        return new ResponseBo().setData(data).setMessage(message).setStatus(status);
    }
}
