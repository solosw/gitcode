package com.solosw.codelab.exception;

import lombok.Data;
import lombok.NoArgsConstructor;


public class EnvExceptiopn extends  Exception{
    public String reason;
    public Integer code;

    public EnvExceptiopn(String message, Integer code) {
        this.reason = message;
        this.code = code;
    }
    public EnvExceptiopn(){}
}
