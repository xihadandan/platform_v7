package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.impl.MultiOrgSystemUnitAttrDaoImpl;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnitAttr;

/**
 * Description:
 *
 * @author chenq
 * @date 2020/4/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2020/4/18    chenq		2020/4/18		Create
 * </pre>
 */
public interface MultiOrgSystemUntAttrService extends
        JpaService<MultiOrgSystemUnitAttr, MultiOrgSystemUnitAttrDaoImpl, String> {

    /**
     * 获取单位属性值
     *
     * @param attrCode
     * @param unitId
     * @return
     */
    String getAttrValue(String attrCode, String unitId);

    String getCurrentSystemUnitAttrValue(String attrCode);


    void saveUpdateCurrentUnitAttr(MultiOrgSystemUnitAttr attr);

    MultiOrgSystemUnitAttr getCurrentUnitOneAttr(String attrCode);
}
