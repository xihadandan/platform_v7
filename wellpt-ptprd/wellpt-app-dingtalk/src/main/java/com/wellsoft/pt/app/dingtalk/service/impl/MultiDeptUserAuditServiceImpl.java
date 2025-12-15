package com.wellsoft.pt.app.dingtalk.service.impl;

import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.dingtalk.dao.MultiDeptUserAuditDao;
import com.wellsoft.pt.app.dingtalk.entity.MultiDeptUserAudit;
import com.wellsoft.pt.app.dingtalk.entity.MultiDeptUserAuditDetail;
import com.wellsoft.pt.app.dingtalk.entity.MultiOrgDingDept;
import com.wellsoft.pt.app.dingtalk.service.MultiDeptUserAuditDetailService;
import com.wellsoft.pt.app.dingtalk.service.MultiDeptUserAuditService;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgDingDeptService;
import com.wellsoft.pt.app.dingtalk.support.DingtalkConfig;
import com.wellsoft.pt.app.dingtalk.vo.DingJobVo;
import com.wellsoft.pt.app.dingtalk.vo.MultiDeptUserAuditVo;
import com.wellsoft.pt.app.dingtalk.vo.OaJobVo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeVo;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgTreeDialogService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserAccountFacadeService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 多部门人员审核service实现类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年8月13日.1	liuyz		2021年8月13日		Create
 *          </pre>
 * @date 2021年8月13日
 */
@Service
@Deprecated
public class MultiDeptUserAuditServiceImpl extends AbstractJpaServiceImpl<MultiDeptUserAudit, MultiDeptUserAuditDao, String>
        implements MultiDeptUserAuditService {

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private DingtalkConfig dingtalkConfig;

    @Autowired
    private MultiOrgService multiOrgService;

    @Autowired
    private MultiOrgUserAccountFacadeService multiOrgUserAccountFacadeService;

    @Autowired
    private MultiDeptUserAuditDetailService multiDeptUserAuditDetailService;

    @Autowired
    private MultiOrgDingDeptService multiOrgDingDeptService;

    @Autowired
    private MultiDeptUserAuditService multiDeptUserAuditService;

    @Autowired
    private MultiOrgTreeDialogService multiOrgTreeDialogService;

    @Override
    public MultiDeptUserAudit getByUuid(String uuid) {
        return this.getOne(uuid);
    }

    @Override
    public List<MultiDeptUserAudit> query(MultiDeptUserAudit multiDeptUserAudit) {
        return this.listByEntity(multiDeptUserAudit);
    }

    @Override
    @Transactional(readOnly = false)
    public void auditRecord(MultiDeptUserAuditVo multiDeptUserAuditVo) {
        List<DingJobVo> dingJobVos = multiDeptUserAuditVo.getDingJobVos();
        List<OaJobVo> oaJobVos = multiDeptUserAuditVo.getOaJobVos();

        MultiDeptUserAudit multiDeptUserAudit = getByUuid(multiDeptUserAuditVo.getUuid());
        MultiOrgUserAccount multiOrgUserAccount = multiOrgUserAccountFacadeService.getAccountByUserId(multiDeptUserAudit.getUserId());
        OrgUserVo orgUserVo;
        String mainJobId = null, mainJobName = null, mainJobIdPath = null, mainJobNamePath = null;
        if (null != multiOrgUserAccount) {
            orgUserVo = multiOrgService.getUser(multiOrgUserAccount.getUuid());
            String eleId;
            String jobName;
            List<String> jobIds = Lists.newArrayList(), jobNames = Lists.newArrayList(), jobIdPaths = Lists.newArrayList();
            MultiDeptUserAuditDetail detail;
            List<MultiDeptUserAuditDetail> details = Lists.newArrayList();
            for (DingJobVo vo : dingJobVos) {
                MultiOrgDingDept multiOrgDingDept = multiOrgDingDeptService.getByPtDeptId(vo.getDeptId());
                eleId = multiOrgDingDept.getEleId();
                jobName = vo.getJobName();
                if (null == multiOrgDingDept || StringUtils.isBlank(multiOrgDingDept.getEleId())) {
                    continue;// 部门未同步，忽略
                }
                OrgTreeNodeDto orgTreeNodeDto = multiOrgDingDeptService.getNodeDtoByEleId(eleId, multiOrgDingDept.getOrgVersionId());
                if (orgTreeNodeDto == null) {
                    continue;// 部门未挂载到组织树中
                }

                OrgTreeNodeDto mainJobNode = null;
                List<OrgTreeNodeDto> list = orgApiFacade.queryAllNodeOfOrgVersionByEleIdPath(
                        multiOrgDingDept.getOrgVersionId(), orgTreeNodeDto.getEleIdPath(), IdPrefix.JOB.getValue());
                // 过滤部门下一级职位
                for (OrgTreeNodeDto jobNode : list) {
                    if (StringUtils.equals(jobNode.getSystemUnitId(), dingtalkConfig.getSystemUnitId())
                            && StringUtils.equals(jobNode.getParentIdPath(), orgTreeNodeDto.getEleIdPath())
                            && StringUtils.equals(jobName, jobNode.getName())) {
                        mainJobNode = jobNode;
                        break;
                    }
                }
                if (mainJobNode == null) {
                    // 构造职位VO
                    OrgTreeNodeVo nodevo = new OrgTreeNodeVo();
                    nodevo.setName(jobName);
                    nodevo.setShortName(jobName);
                    nodevo.setCode("JOB_DINGTALK_" + RandomStringUtils.randomNumeric(3));
                    nodevo.setType(IdPrefix.JOB.getValue()); // 类型
                    nodevo.setParentEleIdPath(orgTreeNodeDto.getEleIdPath());
                    nodevo.setParentEleNamePath(orgTreeNodeDto.getEleNamePath());
                    nodevo.setOrgVersionId(dingtalkConfig.getOrgVersionId());
                    nodevo.setSystemUnitId(dingtalkConfig.getSystemUnitId());
                    MultiOrgTreeNode node = multiOrgService.addOrgChildNode(nodevo);

                    Map<String, OrgNode> result = multiOrgTreeDialogService.smartName(1, Arrays.asList(new String[]{nodevo.getEleId()}), Arrays.asList(new String[]{nodevo.getName()}));
                    mainJobNamePath = result.get(nodevo.getEleId()).getNamePath();

                    jobIds.add(dingtalkConfig.getOrgVersionId() + MultiOrgService.PATH_SPLIT_SYSMBOL + node.getEleId());
                    jobNames.add(jobName);
                    jobIdPaths.add(node.getEleIdPath());
                    if (vo.getIsMain().intValue() == 1) {
                        mainJobId = dingtalkConfig.getOrgVersionId() + MultiOrgService.PATH_SPLIT_SYSMBOL + node.getEleId();
                        mainJobName = jobName;
                        mainJobIdPath = node.getEleIdPath();
                    }
                    detail = new MultiDeptUserAuditDetail(multiDeptUserAuditVo.getUuid(), vo.getIsMain(), vo.getDeptId(), vo.getDeptName(), jobName);
                    details.add(detail);
                } else {
                    // 可能修改了职位的名称
                    OrgTreeNodeVo nodeVo = multiOrgService.getOrgNodeByTreeUuid(mainJobNode.getUuid());
                    nodeVo.setName(jobName);
                    multiOrgService.modifyOrgChildNode(nodeVo, false);

                    mainJobNamePath = nodeVo.getEleNamePath();

                    jobIds.add(mainJobNode.getOrgVersionId() + MultiOrgService.PATH_SPLIT_SYSMBOL + mainJobNode.getEleId());
                    jobNames.add(mainJobNode.getName());
                    jobIdPaths.add(mainJobNode.getEleIdPath());
                    if (vo.getIsMain().intValue() == 1) {
                        mainJobId = mainJobNode.getOrgVersionId() + MultiOrgService.PATH_SPLIT_SYSMBOL + mainJobNode.getEleId();
                        mainJobName = mainJobNode.getName();
                        mainJobIdPath = mainJobNode.getEleIdPath();
                    }
                    detail = new MultiDeptUserAuditDetail(multiDeptUserAuditVo.getUuid(), vo.getIsMain(), vo.getDeptId(), vo.getDeptName(), jobName);
                    details.add(detail);
                }
            }

            for (OaJobVo vo : oaJobVos) {
                jobIds.add(vo.getJobId());
                jobNames.add(vo.getJobName());
                jobIdPaths.add(vo.getJobIdPath());
                if (vo.getIsMain().intValue() == 1) {
                    mainJobId = vo.getJobId();
                    mainJobName = vo.getJobName();
                    mainJobIdPath = vo.getJobIdPath();
                    mainJobNamePath = vo.getJobNamePath();
                }
                detail = new MultiDeptUserAuditDetail(multiDeptUserAuditVo.getUuid(), vo.getIsMain(), vo.getJobId(), vo.getJobIdPath(), vo.getJobName(), vo.getJobNamePath());
                details.add(detail);
            }

            if (StringUtils.isNotBlank(mainJobId) && StringUtils.isNotBlank(mainJobIdPath) && StringUtils.isNotBlank(mainJobName)) {
                orgUserVo.setMainJobId(mainJobId);
                orgUserVo.setMainJobName(mainJobName);
                orgUserVo.setMainJobIdPath(mainJobIdPath);
            }

            if (CollectionUtils.isNotEmpty(jobIds) && CollectionUtils.isNotEmpty(jobNames) && CollectionUtils.isNotEmpty(jobIdPaths)) {
                jobIds.remove(mainJobId);
                jobNames.remove(mainJobName);
                jobIdPaths.remove(mainJobIdPath);
                orgUserVo.setOtherJobIds(StringUtils.join(jobIds, Separator.SEMICOLON.getValue()));
                orgUserVo.setOtherJobNames(StringUtils.join(jobNames, Separator.SEMICOLON.getValue()));
                orgUserVo.setOtherJobIdPaths(StringUtils.join(jobIdPaths, Separator.SEMICOLON.getValue()));
            }

            orgUserVo.setPassword(null);// 不修改密码
            multiOrgService.modifyUser(orgUserVo);

            multiDeptUserAuditDetailService.saveAll(details);

            multiDeptUserAudit.setAuditStatus(1);
            multiDeptUserAudit.setAuditTime(new Date());
            multiDeptUserAudit.setAuditUser(SpringSecurityUtils.getCurrentUserName());
            multiDeptUserAudit.setAfterAuditMainJob(mainJobNamePath.replace(mainJobNamePath.split(MultiOrgService.PATH_SPLIT_SYSMBOL)[0] + MultiOrgService.PATH_SPLIT_SYSMBOL, ""));
            dao.save(multiDeptUserAudit);
        }

        // 审核步骤
        // 检查组织树上对应的部门下是否有相应的职位，否则创建职位
        // 将职位设置为用户的主职位/其它职位
        // 生成相应的多部门人员审核详情，将审核的时候每个职位的信息做一个保存，用于在查看详情的时候查看审核时设置的数据。
    }

    @Override
    public MultiDeptUserAudit getByEntity(MultiDeptUserAudit multiDeptUserAudit) {
        List<MultiDeptUserAudit> list = this.listByEntity(multiDeptUserAudit);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public MultiDeptUserAuditVo getMultiDeptUserAuditDetail(String uuid) {
        MultiDeptUserAudit entity = getOne(uuid);
        MultiDeptUserAuditVo vo = new MultiDeptUserAuditVo();
        vo.setUuid(uuid);

        String jobName = entity.getJobName();
        // 待审核
        if (entity.getAuditStatus().intValue() == 0) {
            if (StringUtils.isNotBlank(entity.getDeptIds()) && StringUtils.isNotBlank(entity.getDeptNames())) {
                String[] deptIds = entity.getDeptIds().split(Separator.COMMA.getValue());
                String[] deptNames = entity.getDeptNames().split(Separator.COMMA.getValue());

                // 钉钉职位，待审核的情况下，主职还没有设置，isMainJob为false
                DingJobVo dingJobVo;
                List<DingJobVo> dingJobVos = Lists.newArrayList();
                for (int i = 0, len = deptIds.length; i < len; i++) {
                    dingJobVo = new DingJobVo(deptIds[i], deptNames[i], jobName, 0);
                    dingJobVos.add(dingJobVo);
                }

                // OA职位，以OA那边的情况为主
                MultiOrgUserAccount multiOrgUserAccount = multiOrgUserAccountFacadeService.getAccountByUserId(entity.getUserId());
                OaJobVo oaJobVo;
                List<OaJobVo> oaJobVos = Lists.newArrayList();
                OrgUserVo orgUserVo = multiOrgService.getUser(multiOrgUserAccount.getUuid());
                if (StringUtils.isNotBlank(orgUserVo.getMainJobId())) {
                    oaJobVo = new OaJobVo(orgUserVo.getMainJobId(), orgUserVo.getMainJobIdPath(), orgUserVo.getMainJobName(), orgUserVo.getMainJobNamePath(), 1);
                    oaJobVos.add(oaJobVo);
                }

                if (StringUtils.isNotBlank(orgUserVo.getOtherJobIds())) {
                    String[] otherJobIds = orgUserVo.getOtherJobIds().split(Separator.SEMICOLON.getValue());
                    String[] otherJobIdPaths = orgUserVo.getOtherJobIdPaths().split(Separator.SEMICOLON.getValue());
                    String[] otherJobNames = orgUserVo.getOtherJobNames().split(Separator.SEMICOLON.getValue());
                    String[] otherJobNamePaths = orgUserVo.getOtherJobNamePaths().split(Separator.SEMICOLON.getValue());
                    for (int i = 0, len = otherJobIds.length; i < len; i++) {
                        oaJobVo = new OaJobVo(otherJobIds[i], otherJobIdPaths[i], otherJobNames[i], otherJobNamePaths[i], 0);
                        oaJobVos.add(oaJobVo);
                    }
                }
                vo.setDingJobVos(dingJobVos);
                vo.setOaJobVos(oaJobVos);
            }
        } else {
            // 已审核
            MultiDeptUserAuditDetail detail = new MultiDeptUserAuditDetail();
            detail.setAuditUuid(uuid);
            List<MultiDeptUserAuditDetail> details = multiDeptUserAuditDetailService.listByEntity(detail);

            DingJobVo dingJobVo;
            List<DingJobVo> dingJobVos = Lists.newArrayList();
            OaJobVo oaJobVo;
            List<OaJobVo> oaJobVos = Lists.newArrayList();
            for (MultiDeptUserAuditDetail tmp : details) {
                if (tmp.getJobType().intValue() == 1) {// 钉钉职位
                    dingJobVo = new DingJobVo(tmp.getDeptId(), tmp.getDeptName(), tmp.getJobName(), tmp.getIsMain());
                    dingJobVos.add(dingJobVo);
                } else if (tmp.getJobType().intValue() == 2) {// OA职位
                    oaJobVo = new OaJobVo(tmp.getJobId(), tmp.getJobIdPath(), tmp.getJobName(), tmp.getJobNamePath(), tmp.getIsMain());
                    oaJobVos.add(oaJobVo);
                }
            }
            vo.setDingJobVos(dingJobVos);
            vo.setOaJobVos(oaJobVos);
        }

        return vo;
    }

    @Override
    public List<String> getDingJobList() {
        List<MultiDeptUserAudit> multiDeptUserAudits = dao.listBySQL("select distinct JOB_NAME from multi_dept_user_audit order by length(job_name) ", new HashMap<>());
        return multiDeptUserAudits.stream().map(MultiDeptUserAudit::getJobName).collect(Collectors.toList());
    }

}
