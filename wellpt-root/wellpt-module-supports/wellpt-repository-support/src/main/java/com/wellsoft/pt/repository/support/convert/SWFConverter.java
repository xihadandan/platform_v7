package com.wellsoft.pt.repository.support.convert;

import com.wellsoft.pt.repository.entity.FileEntity;

/**
 * Description: 文件上传转换成swf的接口类
 *
 * @author jackCheng
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-17.1	jackCheng		2013-4-17		Create
 * </pre>
 * @date 2013-4-17
 */
public interface SWFConverter {
    public FileEntity convert2SWF(String inputFile, String swfFile, String fileName);

    public FileEntity convert2SWF(String inputFile, String fileName);
}
