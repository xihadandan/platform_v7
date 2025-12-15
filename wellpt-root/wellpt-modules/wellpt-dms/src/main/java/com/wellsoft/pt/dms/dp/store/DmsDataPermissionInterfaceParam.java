package com.wellsoft.pt.dms.dp.store;

import com.wellsoft.pt.basicdata.datastore.support.DataStoreInterfaceField;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreInterfaceFieldElement;
import com.wellsoft.pt.jpa.criteria.InterfaceParam;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/23    chenq		2019/10/23		Create
 * </pre>
 */
public class DmsDataPermissionInterfaceParam implements InterfaceParam {

    @DataStoreInterfaceField(name = "数据权限", service = "dmsDataPermissionQueryService.queryAll4SelectOptions", domType = DataStoreInterfaceFieldElement.SELECT)
    private String dmsDataPermissionId;

    public String getDmsDataPermissionId() {
        return dmsDataPermissionId;
    }

    public void setDmsDataPermissionId(String dmsDataPermissionId) {
        this.dmsDataPermissionId = dmsDataPermissionId;
    }

}
