package com.solosw.codelab.entity.bo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class CodeBo {
    String name;
    String path;
    boolean dir;
    List<CodeBo> children =new ArrayList<>();
    String updateTime;
    String submitHash;
    String fileHash;
    String message;
    String username;
    boolean show=false;
}
