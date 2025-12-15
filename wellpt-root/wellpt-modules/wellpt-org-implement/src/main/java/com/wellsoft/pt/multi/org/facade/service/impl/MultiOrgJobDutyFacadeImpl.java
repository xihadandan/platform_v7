package com.wellsoft.pt.multi.org.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.multi.org.entity.MultiOrgJobDuty;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgJobDutyFacade;
import com.wellsoft.pt.multi.org.service.MultiOrgJobDutyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Description: 职务群组对外接口，供其它模块调用(调用链：client请求->facade服务->service->dao)
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022/5/25.1	liuyz		2022/5/25		Create
 * </pre>
 * @date 2022/5/25
 */
@Service
public class MultiOrgJobDutyFacadeImpl extends AbstractApiFacade implements MultiOrgJobDutyFacade {

    @Autowired
    private MultiOrgJobDutyService multiOrgJobDutyService;

    @Override
    public void deleteJobDutyByJobId(String jobId) {
        multiOrgJobDutyService.deleteJobDutyByJobId(jobId);
    }

    @Override
    public MultiOrgJobDuty getJobDutyByJobId(String eleId) {
        return multiOrgJobDutyService.getJobDutyByJobId(eleId);
    }

    public List<MultiOrgJobDuty> getJobDutyByDutyId(String dutyId) {
        MultiOrgJobDuty multiOrgJobDuty = new MultiOrgJobDuty();
        multiOrgJobDuty.setDutyId(dutyId);
        return multiOrgJobDutyService.getDao().listByEntity(multiOrgJobDuty);
    }

    @Override
    public boolean isMemberOfSelectedJobDuty(String userId, Set<String> dutyIds, Set<String> jobId) {
        return isMemberOfSelectedJobDuty(userId, dutyIds, jobId);
    }

    @Override
    public boolean isMemberOfAllJobDuty(String userId, Set<String> dutyIds) {
        return isMemberOfAllJobDuty(userId, dutyIds);
    }

    @Override
    public boolean isMemberOfMainJobDuty(String userId, Set<String> dutyIds) {
        return isMemberOfMainJobDuty(userId, dutyIds);
    }
}
