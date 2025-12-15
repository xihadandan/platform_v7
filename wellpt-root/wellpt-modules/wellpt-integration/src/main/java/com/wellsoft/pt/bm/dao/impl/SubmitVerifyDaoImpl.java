package com.wellsoft.pt.bm.dao.impl;

import com.wellsoft.pt.bm.dao.SubmitVerifyDao;
import com.wellsoft.pt.bm.entity.SubmitVerify;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author wangbx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-6.1	wangbx		2013-12-6		Create
 * </pre>
 * @date 2013-12-6
 */
@Repository
public class SubmitVerifyDaoImpl extends AbstractJpaDaoImpl<SubmitVerify, String> implements SubmitVerifyDao {

    public List<SubmitVerify> getObjByPuuidAndVerifyId(String puuid, String id) {
        String hql = "from SubmitVerify s where s.selfPublicityApply.uuid=:puuid and s.verifyId =:verifyId order by s.submitTime desc";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("puuid", puuid);
        values.put("verifyId", id);
        return listByHQL(hql, values);
    }
}
