package com.wellsoft.pt.ei.service.impl;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dto.ImportEntity;
import com.wellsoft.pt.ei.dto.org.OrgGroupData;
import com.wellsoft.pt.ei.dto.org.OrgGroupMember;
import com.wellsoft.pt.ei.entity.DataImportTaskLog;
import com.wellsoft.pt.ei.facade.DataImportApi;
import com.wellsoft.pt.ei.service.DataImportTaskLogService;
import com.wellsoft.pt.multi.org.bean.OrgGroupVo;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroup;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroupMember;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgGroupFacade;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.service.MultiOrgElementService;
import com.wellsoft.pt.multi.org.service.MultiOrgGroupService;
import com.wellsoft.pt.multi.org.service.MultiOrgUserAccountService;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/9/29.1	liuyz		2021/9/29		Create
 * </pre>
 * @date 2021/9/29
 */
@Service
@Transactional(readOnly = true)
public class OrgGroupExpImpServiceImpl extends AbstractOrgExportService<OrgGroupData, MultiOrgGroup> {

    @Autowired
    private MultiOrgService multiOrgService;

    @Autowired
    private MultiOrgGroupService multiOrgGroupService;

    @Autowired
    private MultiOrgGroupFacade multiOrgGroupFacade;

    @Autowired
    private MultiOrgUserAccountService multiOrgUserAccountService;

    @Autowired
    private MultiOrgElementService multiOrgElementService;

    @Autowired
    private DataImportTaskLogService dataImportTaskLogService;

    @Override
    public int order() {
        return 5;
    }

    @Override
    public Class dataClass() {
        return OrgGroupData.class;
    }

    @Override
    public String fileName() {
        return "组织数据_群组";
    }

    @Override
    public String dataChildType() {
        return DataExportConstants.DATA_TYPE_ORG_GROUP;
    }

    @Override
    public long total(String systemUnitId) {
        MultiOrgGroup multiOrgGroup = new MultiOrgGroup();
        multiOrgGroup.setSystemUnitId(systemUnitId);
        return multiOrgGroupService.getDao().countByEntity(multiOrgGroup);
    }

    @Override
    public List<MultiOrgGroup> queryAll(String systemUnitId) {
        MultiOrgGroup multiOrgGroup = new MultiOrgGroup();
        multiOrgGroup.setSystemUnitId(systemUnitId);
        return multiOrgGroupService.listByEntity(multiOrgGroup);
    }

    @Override
    public List<MultiOrgGroup> queryByPage(String systemUnitId, Integer currentPage, Integer pageSize) {
        MultiOrgGroup multiOrgGroup = new MultiOrgGroup();
        multiOrgGroup.setSystemUnitId(systemUnitId);
        return multiOrgGroupService.listAllByPage(multiOrgGroup, new PagingInfo(currentPage, pageSize, true), null);
    }

    @Override
    public OrgGroupData toData(MultiOrgGroup multiOrgGroup) {
        OrgGroupVo orgGroupVo = multiOrgGroupFacade.getGroupVo(multiOrgGroup.getUuid());
        OrgGroupData orgGroupData = new OrgGroupData();
        BeanUtils.copyProperties(orgGroupVo, orgGroupData);

        List<OrgGroupMember> orgGroupMembers = Lists.newArrayList();
        OrgGroupMember orgGroupMember;

        List<MultiOrgGroupMember> multiOrgGroupMembers = orgGroupVo.getMemberList();
        if (null != multiOrgGroupMembers && !multiOrgGroupMembers.isEmpty()) {
            for (MultiOrgGroupMember member : multiOrgGroupMembers) {
                orgGroupMember = new OrgGroupMember();
                BeanUtils.copyProperties(member, orgGroupMember);

                // 成员可能是用户、可能是职位、组织节点、部门、业务单位等
                if (IdPrefix.USER.getValue().equals(member.getMemberObjType())) {
                    MultiOrgUserAccount multiOrgUserAccount = multiOrgUserAccountService.getAccountById(member.getMemberObjId());
                    if (null != multiOrgUserAccount) {
                        orgGroupMember.setMemberObjUuid(multiOrgUserAccount.getUuid());
                    } else {
                        throw new BusinessException("群组【" + multiOrgGroup.getName() + "】的群组成员【" + member.getMemberObjName() + "】不存在。");
                    }
                } else {
                    MultiOrgElement multiOrgElement = new MultiOrgElement();
                    multiOrgElement.setSystemUnitId(multiOrgGroup.getSystemUnitId());
                    multiOrgElement.setId(member.getMemberObjId());
                    List<MultiOrgElement> multiOrgElements = multiOrgElementService.listByEntity(multiOrgElement);
                    if (null != multiOrgElements && !multiOrgElements.isEmpty()) {
                        orgGroupMember.setMemberObjUuid(multiOrgElements.get(0).getUuid());
                    }
                }
                orgGroupMembers.add(orgGroupMember);
            }
        }
        orgGroupData.setOrgGroupMembers(orgGroupMembers);
        return orgGroupData;
    }

    @Override
    public String getUuid(MultiOrgGroup multiOrgGroup) {
        return multiOrgGroup.getUuid();
    }

    @Override
    public String getDataUuid(OrgGroupData orgGroupData) {
        return orgGroupData.getUuid();
    }

    @Override
    public String getId(MultiOrgGroup multiOrgGroup) {
        return multiOrgGroup.getId();
    }

    @Override
    public String getDataId(OrgGroupData orgGroupData) {
        return orgGroupData.getId();
    }

    @Override
    @Transactional
    public ImportEntity<MultiOrgGroup, OrgGroupData> save(OrgGroupData orgGroupData, String systemUnitId, boolean replace, Map<String, String> dependentDataMap) {
        DataImportTaskLog dataImportTaskLog = dataImportTaskLogService.getBySourceUuid(orgGroupData.getUuid());
        DataImportApi dataImportApi = ApplicationContextHolder.getBean(DataImportApi.class);
        MultiOrgGroup multiOrgGroup = null;

        if (null != dataImportTaskLog) {
            if (StringUtils.isNotBlank(dataImportTaskLog.getAfterImportUuid())) {
                multiOrgGroup = multiOrgGroupService.getOne(dataImportTaskLog.getAfterImportUuid());
            }
        }
        if (null == multiOrgGroup) {
            multiOrgGroup = new MultiOrgGroup();
            multiOrgGroup.setId(dataImportApi.generateElementId(DataExportConstants.DATA_TYPE_ORG_GROUP));
        }

        multiOrgGroup.setCode(orgGroupData.getCode());
        multiOrgGroup.setName(orgGroupData.getName());
        multiOrgGroup.setType(orgGroupData.getType());
        multiOrgGroup.setSystemUnitId(systemUnitId);
        multiOrgGroup.setRemark(orgGroupData.getRemark());
        multiOrgGroupService.save(multiOrgGroup);

        OrgGroupVo orgGroupVo = new OrgGroupVo();
        BeanUtils.copyProperties(multiOrgGroup, orgGroupVo);

        // 将旧的群组成员ID，替换成导入后的ID
        String[] memberIdPath = orgGroupData.getMemberIdPaths().split(Separator.SEMICOLON.getValue());
        String[] memberNamePath = orgGroupData.getMemberNames().split(Separator.SEMICOLON.getValue());
        List<String> idPaths = Lists.newArrayList(), namePaths = Lists.newArrayList();
        for (int i = 0, len = memberIdPath.length; i < len; i++) {
            dataImportTaskLog = dataImportTaskLogService.getBySourceId(memberIdPath[i]);
            if (null != dataImportTaskLog) {
                idPaths.add(dataImportTaskLog.getAfterImportId());
                namePaths.add(memberNamePath[i]);
            }
        }

        orgGroupVo.setMemberIdPaths(StringUtils.join(idPaths, Separator.SEMICOLON.getValue()));
        orgGroupVo.setMemberNames(StringUtils.join(namePaths, Separator.SEMICOLON.getValue()));

        if (StringUtils.isNotBlank(orgGroupVo.getUuid())) {
            multiOrgGroupFacade.modifyGroup(orgGroupVo);
        } else {
            multiOrgGroupFacade.addGroup(orgGroupVo);
        }

        ImportEntity importEntity = new ImportEntity();
        importEntity.setSorce(orgGroupData);
        importEntity.setObj(multiOrgGroup);
        return importEntity;
    }

    @Override
    public void update(MultiOrgGroup multiOrgGroup, OrgGroupData orgGroupData, Map<String, String> dependentDataMap) {

    }

}
