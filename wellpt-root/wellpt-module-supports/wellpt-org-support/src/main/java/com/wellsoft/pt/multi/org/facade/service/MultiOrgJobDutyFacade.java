package com.wellsoft.pt.multi.org.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.multi.org.entity.MultiOrgJobDuty;

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
public interface MultiOrgJobDutyFacade extends BaseService {

    void deleteJobDutyByJobId(String jobId);

    MultiOrgJobDuty getJobDutyByJobId(String eleId);

    List<MultiOrgJobDuty> getJobDutyByDutyId(String dutyId);

    boolean isMemberOfSelectedJobDuty(String userId, Set<String> dutyIds, Set<String> jobId);

    boolean isMemberOfAllJobDuty(String userId, Set<String> dutyIds);

    boolean isMemberOfMainJobDuty(String userId, Set<String> dutyIds);
}
