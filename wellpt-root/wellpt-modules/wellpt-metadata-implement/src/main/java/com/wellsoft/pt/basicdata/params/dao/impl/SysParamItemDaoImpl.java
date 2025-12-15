package com.wellsoft.pt.basicdata.params.dao.impl;

import com.wellsoft.pt.basicdata.params.dao.SysParamItemDao;
import com.wellsoft.pt.basicdata.params.entity.SysParamItem;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 *
 * @author Lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-07-20.1	Lmw		2015-07-20		Create
 * </pre>
 * @date 2015-07-20
 */

@Repository
public class SysParamItemDaoImpl extends AbstractJpaDaoImpl<SysParamItem, String> implements SysParamItemDao {

    public int size(SysParamItem sysParamItem) {
        StringBuilder stringBuilder = new StringBuilder(" from SysParamItem u where (1=1)");
        Map<String, Object> rMap = new HashMap<String, Object>();
        if (sysParamItem != null) {
            if (sysParamItem.getUuid() != null) {
                stringBuilder.append(" and u.uuid=:uuid");
                rMap.put("uuid", sysParamItem.getUuid());
            }
            if (sysParamItem.getKey() != null) {
                stringBuilder.append(" and u.key=:key");
                rMap.put("key", sysParamItem.getKey());
            }
        }
        return (int) getNumberByHQL(stringBuilder.toString(), rMap);
    }

    /**
     * 删除不是指定类型的记录
     *
     * @param type
     */
    public void deleteIsNotType(int type) {
        StringBuilder stringBuilder = new StringBuilder(" delete SysParamItem u where u.sourcetype=:sourcetype");
        Map<String, Object> rMap = new HashMap<String, Object>();
        rMap.put("sourcetype", type);

        this.deleteByHQL(stringBuilder.toString(), rMap);
    }

    /**
     * 查询指定key的记录数
     *
     * @param key
     * @return
     */
    public int isExistKey(String key) {
        StringBuilder stringBuilder = new StringBuilder("from SysParamItem u  where u.key=:key");
        Map<String, Object> rMap = new HashMap<String, Object>();
        rMap.put("key", key);
        return (int) this.getNumberByHQL(stringBuilder.toString(), rMap);
    }

    /**
     * 跟新指定键的值
     *
     * @param key
     * @param value
     */
    public void updateValue(String key, String value) {
        StringBuilder stringBuilder = new StringBuilder(" update SysParamItem u set u.value=:value where u.key=:key");
        Map<String, Object> rMap = new HashMap<String, Object>();
        rMap.put("key", key);
        rMap.put("value", value);
        this.updateByHQL(stringBuilder.toString(), rMap);
    }
}
