package com.solosw.codelab.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class InitEnvDataVo {

    String name,password,email;
    Integer role;

}
