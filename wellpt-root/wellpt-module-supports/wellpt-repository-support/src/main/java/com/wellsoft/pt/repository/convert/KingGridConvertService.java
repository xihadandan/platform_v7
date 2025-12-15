package com.wellsoft.pt.repository.convert;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

public interface KingGridConvertService {

    /**
     * 方法描述
     *
     * @param metas
     * @return
     * @author baozh
     * @date 2021/11/15 11:25
     */
    public String kingGridConvert(File srcFile, OutputStream out, Map<String, String> metas);
}
