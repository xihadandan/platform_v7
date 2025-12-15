package com.wellsoft.pt.basicdata.serialnumber.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.basicdata.serialnumber.dao.SerialNumberMaintainDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberMaintain;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description: 流水号定义数据层访问类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-6.1	zhouyq		2013-3-6		Create
 * </pre>
 * @date 2013-3-6
 */
@Repository
public class SerialNumberMaintainDaoImpl extends AbstractJpaDaoImpl<SerialNumberMaintain, String> implements
        SerialNumberMaintainDao {

    @Override
    public SerialNumberMaintain getById(String id) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("id", id);
        return this.getOneByHQL("from SerialNumberMaintain where id=:id", param);
    }

    @Override
    public SerialNumberMaintain getByIdAndKeyPart(String id, String keyPart) {
        List<SerialNumberMaintain> serialNumberMaintainList = this.getByIdAndKeyParts(id, keyPart);
        if (serialNumberMaintainList.size() > 0) {
            return serialNumberMaintainList.get(0);
        }
        return null;
    }

    @Override
    public List<SerialNumberMaintain> getByIdAndKeyParts(String id, String keyPart) {
        String hql = null;
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", id);
        if (StringUtils.isBlank(keyPart)) {
            hql = "from SerialNumberMaintain a where a.id=:id";
        } else {
            map.put("keyPart", keyPart);
            hql = "from SerialNumberMaintain a where a.id=:id and a.keyPart=:keyPart";
        }
        List<SerialNumberMaintain> serialNumberMaintainList = this.listByHQL(hql, map);
        return serialNumberMaintainList;
    }
}
