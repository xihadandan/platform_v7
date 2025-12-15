package com.wellsoft.pt.app.dingtalk.vo;

import java.util.List;

/**
 * Description: 多部门人员审核详情类vo
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/11/5.1	liuyz		2021/11/5		Create
 * </pre>
 * @date 2021/11/5
 */
@Deprecated
public class MultiDeptUserAuditVo {
    private String uuid;
    private List<DingJobVo> dingJobVos;
    private List<OaJobVo> oaJobVos;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<DingJobVo> getDingJobVos() {
        return dingJobVos;
    }

    public void setDingJobVos(List<DingJobVo> dingJobVos) {
        this.dingJobVos = dingJobVos;
    }

    public List<OaJobVo> getOaJobVos() {
        return oaJobVos;
    }

    public void setOaJobVos(List<OaJobVo> oaJobVos) {
        this.oaJobVos = oaJobVos;
    }

}
