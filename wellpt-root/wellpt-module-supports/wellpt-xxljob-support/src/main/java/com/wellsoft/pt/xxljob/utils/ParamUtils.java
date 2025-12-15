package com.wellsoft.pt.xxljob.utils;

import com.wellsoft.pt.xxljob.model.ExecutionParam;
import com.xxl.job.core.util.GsonTool;

public class ParamUtils {

    public static ExecutionParam toExecutionParam(String param) {
        ExecutionParam executionParam = GsonTool.fromJson(param, ExecutionParam.class);
        if (executionParam == null) {
            throw new RuntimeException("参数转换错误");
        }
        return executionParam;
    }

}
