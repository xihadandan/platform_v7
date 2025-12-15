package com.wellsoft.pt.app.dingtalk.service;

import com.wellsoft.pt.app.dingtalk.dao.MultiOrgDingUserDao;
import com.wellsoft.pt.app.dingtalk.entity.MultiOrgDingUser;
import com.wellsoft.pt.jpa.service.JpaService;
import net.sf.json.JSONObject;

import java.util.Collection;
import java.util.List;

/**
 * Description: 钉钉用戶信息service
 *
 * @author bryanlin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月18日.1	bryanlin		2020年5月18日  	Create
 *          </pre>
 * @date 2020年5月18日
 */
@Deprecated
public interface MultiOrgDingUserService extends JpaService<MultiOrgDingUser, MultiOrgDingUserDao, String> {

    /**
     * 新增用户
     *
     * @param userId
     */
    public void saveAndUpdateUserFromDingtalk(JSONObject userobj);

    /**
     * 组织同步：新增用户
     *
     * @param userobj
     */
    public void saveAndUpdateUserFromDingtalk(JSONObject userobj, String logId, boolean syncUserWork);

    /**
     * 钉钉用户id删除用户
     *
     * @param dingUserId
     */
    public void deleteUserFromDingtalk(String dingUserId);

    /**
     * 根据平台用户ID获取钉钉用户信息
     *
     * @param ptUserId
     * @return
     */
    public MultiOrgDingUser getByPtUserId(String ptUserId);

    /**
     * 根据钉钉唯一标识获取数据
     *
     * @param unionid    钉钉用户唯一标识
     * @param dingUserId 钉钉用户id
     * @return
     */
    public MultiOrgDingUser getUser(String unionId, String dingUserId);

    public MultiOrgDingUser getByDingUserId(String dingUserId);

    public MultiOrgDingUser getByUnionId(String unionId);

    public abstract void confirmUsers(Collection<String> uuids);

    public abstract List<String> getDingIdsByPtIds(Collection<String> userIds);

    /**
     * 根据钉钉部门ID获取钉钉用户ID列表
     *
     * @param deptId
     * @return
     */
    public List<String> getDingIdsByDeptId(String deptId);

    /**
     * 组织同步：同步某个人员的职位关系（仅同步指定人员，指定部门的指定职位，同时不设置主职）
     *
     * @param userobj
     * @param logId
     */
    public void saveAndUpdateUserWork(JSONObject userobj, String logId, String deptId, String position);

}
