package com.wellsoft.pt.basicdata.serialnumber.dao;

import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberMaintain;
import com.wellsoft.pt.jpa.dao.JpaDao;

import java.util.List;

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
public interface SerialNumberMaintainDao extends JpaDao<SerialNumberMaintain, String> {

    SerialNumberMaintain getById(String id);

    SerialNumberMaintain getByIdAndKeyPart(String id, String keyPart);

    List<SerialNumberMaintain> getByIdAndKeyParts(String id, String keyPart);
}
