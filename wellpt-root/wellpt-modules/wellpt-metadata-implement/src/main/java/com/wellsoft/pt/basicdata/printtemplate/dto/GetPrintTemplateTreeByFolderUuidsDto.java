package com.wellsoft.pt.basicdata.printtemplate.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description:
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2022/3/7.1	    zenghw		2022/3/7		    Create
 * </pre>
 * @date 2022/3/7
 */
@ApiModel("根据文件夹Uuid获取对应的套打模板输入对象")
public class GetPrintTemplateTreeByFolderUuidsDto {

    @ApiModelProperty("文件夹Uuid集合")
    private List<String> folderUuids;

    public List<String> getFolderUuids() {
        return this.folderUuids;
    }

    public void setFolderUuids(final List<String> folderUuids) {
        this.folderUuids = folderUuids;
    }
}
