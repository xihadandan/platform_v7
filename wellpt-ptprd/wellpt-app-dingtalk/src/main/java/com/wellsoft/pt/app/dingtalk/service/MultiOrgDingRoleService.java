package com.wellsoft.pt.app.dingtalk.service;

import com.wellsoft.pt.app.dingtalk.dao.MultiOrgDingRoleDao;
import com.wellsoft.pt.app.dingtalk.entity.MultiOrgDingRole;
import com.wellsoft.pt.jpa.service.JpaService;
import net.sf.json.JSONObject;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年7月3日.1	zhongzh		2020年7月3日		Create
 * </pre>
 * @date 2020年7月3日
 */
@Deprecated
public interface MultiOrgDingRoleService extends JpaService<MultiOrgDingRole, MultiOrgDingRoleDao, String> {

    /**
     * @param roleObj
     */
    public void saveAndUpdateRoleFromDingtalk(JSONObject roleObj);

    /**
     * @param dingRoleId
     */
    public void deleteRoleFromDingtalk(String dingRoleId);

    public MultiOrgDingRole getByDingRoleId(String dingRoleId);

    public MultiOrgDingRole getByRoleId(String roleId);

}
