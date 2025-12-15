package com.wellsoft.pt.ei.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.PinyinUtil;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dto.ImportEntity;
import com.wellsoft.pt.ei.dto.org.OrgNodeInfoData;
import com.wellsoft.pt.ei.dto.org.OrgUserData;
import com.wellsoft.pt.ei.entity.DataImportTaskLog;
import com.wellsoft.pt.ei.facade.DataImportApi;
import com.wellsoft.pt.ei.service.DataImportTaskLogService;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgTreeDialogService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.multi.org.facade.service.impl.MultiOrgUserAccountFacadeServiceImpl;
import com.wellsoft.pt.multi.org.service.*;
import com.wellsoft.pt.multi.org.util.PwdUtils;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Date;
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
@Transactional
public class OrgUserExpImpServiceImpl extends AbstractOrgExportService<OrgUserData, MultiOrgUserAccount> {

    @Autowired
    private MultiOrgService multiOrgService;

    @Autowired
    private MultiOrgUserAccountService multiOrgUserAccountService;

    @Autowired
    private DataImportTaskLogService dataImportTaskLogService;

    @Autowired
    private MultiOrgUserInfoService multiOrgUserInfoService;

    @Autowired
    private MultiOrgTreeNodeService multiOrgTreeNodeService;

    @Autowired
    private MultiOrgElementService multiOrgElementService;

    @Autowired
    private MultiOrgUserService multiOrgUserService;

    @Autowired
    private MultiOrgTreeDialogService multiOrgTreeDialogService;

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private MultiOrgVersionService multiOrgVersionService;

    @Autowired
    private MultiOrgUserTreeNodeService multiOrgUserTreeNodeService;

    private List<OrgNodeInfoData> orgNodeInfoDataList;

    @Override
    public int order() {
        return 4;
    }

    @Override
    public Class dataClass() {
        return OrgUserData.class;
    }

    @Override
    public String fileName() {
        return "组织数据_用户";
    }

    @Override
    public String dataChildType() {
        return DataExportConstants.DATA_TYPE_ORG_USER;
    }

    @Override
    public long total(String systemUnitId) {
        MultiOrgUserAccount multiOrgUserAccount = new MultiOrgUserAccount();
        multiOrgUserAccount.setSystemUnitId(systemUnitId);
        return multiOrgUserAccountService.getDao().countByEntity(multiOrgUserAccount);
    }

    @Override
    public List<MultiOrgUserAccount> queryAll(String systemUnitId) {
        return multiOrgUserAccountService.queryAllAccountOfUnit(systemUnitId);
    }

    @Override
    public List<MultiOrgUserAccount> queryByPage(String systemUnitId, Integer currentPage, Integer pageSize) {
        MultiOrgUserAccount multiOrgUserAccount = new MultiOrgUserAccount();
        multiOrgUserAccount.setSystemUnitId(systemUnitId);
        return multiOrgUserAccountService.listAllByPage(multiOrgUserAccount, new PagingInfo(currentPage, pageSize, true), null);
    }

    @Override
    public OrgUserData toData(MultiOrgUserAccount multiOrgUserAccount) {
        OrgUserVo orgUserVo = multiOrgService.getUser(multiOrgUserAccount.getUuid());
        OrgUserData orgUserData = new OrgUserData();
        BeanUtils.copyProperties(orgUserVo, orgUserData);
        if (StringUtils.isNotBlank(orgUserVo.getPhotoUuid())) {
            MongoFileEntity mongoFileEntity = mongoFileService.getFile(orgUserVo.getPhotoUuid());
            if (null != mongoFileEntity) {
                orgUserData.setPhotoUuid(Arrays.asList(orgUserVo.getPhotoUuid().split(Separator.COMMA.getValue())));
            }
        }

        if (StringUtils.isNotBlank(orgUserVo.getDirectLeaderIds()) && StringUtils.isNotBlank(orgUserVo.getDirectLeaderNamePaths())) {
            String[] directLeaderIdPaths = orgUserVo.getDirectLeaderIds().split(Separator.SEMICOLON.getValue());
            String[] directLeaderNamePaths = orgUserVo.getDirectLeaderNamePaths().split(Separator.SEMICOLON.getValue());
            for (int i = 0, len = directLeaderIdPaths.length; i < len; i++) {
                directLeaderIdPaths[i] = directLeaderIdPaths[i].substring(directLeaderIdPaths[i].lastIndexOf(MultiOrgService.PATH_SPLIT_SYSMBOL) + 1, directLeaderIdPaths[i].length());
                directLeaderNamePaths[i] = directLeaderNamePaths[i].substring(directLeaderNamePaths[i].lastIndexOf(MultiOrgService.PATH_SPLIT_SYSMBOL) + 1, directLeaderNamePaths[i].length());
            }
            Map<String, OrgNode> result = multiOrgTreeDialogService.smartName(1, Arrays.asList(directLeaderIdPaths), Arrays.asList(directLeaderNamePaths));
            List<String> idPaths = Lists.newArrayList(), namePaths = Lists.newArrayList();
            OrgNode orgNode = null;
            for (int i = 0, len = directLeaderIdPaths.length; i < len; i++) {
                orgNode = result.get(directLeaderIdPaths[i]);
                if (null == orgNode || StringUtils.isBlank(orgNode.getId())) {
                    // 直属上级领导可能没了。。或者在其它组织版本，但是被删除了。。也就是脏数据。。
                    continue;
                }
                if (orgNode.getId().startsWith(IdPrefix.USER.getValue())) {
                    idPaths.add(orgNode.getIdPath() + MultiOrgService.PATH_SPLIT_SYSMBOL + orgNode.getId());
                    namePaths.add(orgNode.getNamePath() + MultiOrgService.PATH_SPLIT_SYSMBOL + orgNode.getName());
                } else {
                    idPaths.add(orgNode.getIdPath());
                    namePaths.add(orgNode.getNamePath());
                }
            }
            orgUserData.setDirectLeaderIdPaths(StringUtils.join(idPaths, Separator.SEMICOLON.getValue()));
            orgUserData.setDirectLeaderNamePaths(StringUtils.join(namePaths, Separator.SEMICOLON.getValue()));
        }

        return orgUserData;
    }

    @Override
    public String getUuid(MultiOrgUserAccount multiOrgUserAccount) {
        return multiOrgUserAccount.getUuid();
    }

    @Override
    public String getDataUuid(OrgUserData orgUserData) {
        return orgUserData.getUuid();
    }

    @Override
    public String getId(MultiOrgUserAccount multiOrgUserAccount) {
        return multiOrgUserAccount.getId();
    }

    @Override
    public String getDataId(OrgUserData orgUserData) {
        return orgUserData.getId();
    }

    @Override
    @Transactional
    public ImportEntity<MultiOrgUserAccount, OrgUserData> save(OrgUserData orgUserData, String systemUnitId, boolean replace, Map<String, String> dependentDataMap) {
        DataImportTaskLog dataImportTaskLog = dataImportTaskLogService.getBySourceUuid(orgUserData.getUuid());
        DataImportApi dataImportApi = ApplicationContextHolder.getBean(DataImportApi.class);

        MultiOrgUserAccount multiOrgUserAccount = null;
        if (null != dataImportTaskLog) {
            multiOrgUserAccount = multiOrgUserAccountService.getOne(dataImportTaskLog.getAfterImportUuid());
        }
        if (null == multiOrgUserAccount) {
            multiOrgUserAccount = new MultiOrgUserAccount();
            // ID重新生成
            multiOrgUserAccount.setId(dataImportApi.generateElementId(DataExportConstants.DATA_TYPE_ORG_USER));
        }

        // 账号信息处理
        BeanUtils.copyProperties(orgUserData, multiOrgUserAccount, new String[]{"uuid", "id"});
        if (StringUtils.isBlank(multiOrgUserAccount.getLoginNameZh())) {
            multiOrgUserAccount.setLoginNameZh(orgUserData.getUserName());
        }
        multiOrgUserAccount.setUserNamePy(PinyinUtil.getPinYin(multiOrgUserAccount.getUserName()));
        multiOrgUserAccount.setUserNameJp(PinyinUtil.getPinYinHeadChar(multiOrgUserAccount.getUserName()));
        // 登录名转小写
        multiOrgUserAccount.setLoginNameLowerCase(multiOrgUserAccount.getLoginName().toLowerCase());
        // 加密密码，采用登录名的小写作为salt
        multiOrgUserAccount.setPassword(PwdUtils.createSm3Password(multiOrgUserAccount.getLoginName(), dependentDataMap.get("defaultPwd")));
        // 对登录名也进行MD5加密，也是对登录名小写后MD5
        String md5LoginName = MultiOrgUserAccountFacadeServiceImpl.createMd5LoginName(multiOrgUserAccount.getLoginName());
        multiOrgUserAccount.setMd5LoginName(md5LoginName);
        multiOrgUserAccount.setPwdCreateTime(new Date());
        multiOrgUserAccount.setIsForbidden(0);
        multiOrgUserAccount.setSystemUnitId(systemUnitId);
        multiOrgUserAccount.setType(0);

        if (replace) {
            multiOrgUserAccountService.update(multiOrgUserAccount);
        } else {
            MultiOrgUserAccount account = this.multiOrgUserAccountService.getAccountByLoignName(orgUserData.getLoginName());
            Assert.isNull(account, "该账号已存在，无法新建");

            multiOrgUserAccountService.save(multiOrgUserAccount);
        }

        // 个人信息处理
        MultiOrgUserInfo multiOrgUserInfo = multiOrgUserInfoService.getByUserId(multiOrgUserAccount.getId());
        if (null == multiOrgUserInfo) {
            multiOrgUserInfo = new MultiOrgUserInfo();
        }

        // 头像附件处理
        List<String> photos = orgUserData.getPhotoUuid();
        if (!Collections.isEmpty(photos)) {
            multiOrgUserInfo.setPhotoUuid(dependentDataMap.get("fileId").replaceAll("\\[", "").replaceAll("\\]", ""));
        }

        BeanUtils.copyProperties(orgUserData, multiOrgUserInfo, "uuid");
        multiOrgUserInfo.setUserId(multiOrgUserAccount.getId());
        if (replace) {
            multiOrgUserInfoService.update(multiOrgUserInfo);
        } else {
            multiOrgUserInfoService.save(multiOrgUserInfo);
        }

        // 工作信息处理
        OrgUserVo orgUserVo = multiOrgUserService.getUser(multiOrgUserAccount.getUuid());

        // 清除用户职位信息
        multiOrgUserTreeNodeService.deleteUser(multiOrgUserAccount.getId());
        /*List<MultiOrgVersion> versionList = this.multiOrgVersionService
                .queryCurrentActiveVersionListOfSystemUnit(systemUnitId);
        if (!CollectionUtils.isEmpty(versionList)) {
            for (MultiOrgVersion version : versionList) {
                this.multiOrgUserTreeNodeService.deleteUserJobByOrgVersion(multiOrgUserAccount.getId(), version.getId());
            }
        }*/

        // 主职
        String versionId = dependentDataMap.get("versionId");
        String mainJobIdPath = orgUserData.getMainJobIdPath();
        String mainJobNamePath = orgUserData.getMainJobNamePath();

        if (CollectionUtils.isEmpty(orgNodeInfoDataList)) {
            orgNodeInfoDataList = dataImportApi.getAllNodeInfo(versionId);
        }

        if (StringUtils.isNotBlank(mainJobIdPath) && StringUtils.isNotBlank(mainJobNamePath)) {
            // 判断职位路径的节点是否存在，没有则新建，且路径的起点是本次导入选择的组织版本ID
            handlerMainJob(orgUserVo, versionId, systemUnitId, mainJobIdPath, mainJobNamePath);
        }

        // 其他职位
        if (StringUtils.isNotBlank(orgUserData.getOtherJobIdPaths()) && StringUtils.isNotBlank(orgUserData.getOtherJobNamePaths())) {
            String[] otherJobIdPaths = orgUserData.getOtherJobIdPaths().split(Separator.SEMICOLON.getValue());
            String[] otherJobNamePaths = orgUserData.getOtherJobNamePaths().split(Separator.SEMICOLON.getValue());
            handleOtherJob(orgUserVo, versionId, systemUnitId, otherJobIdPaths, otherJobNamePaths);
        }

        boolean postProcess = false;
        // 直属上级领导
        if (StringUtils.isNotBlank(orgUserData.getDirectLeaderIdPaths()) && StringUtils.isNotBlank(orgUserData.getDirectLeaderNamePaths())) {
            // 可能是人，可能是职位。。
            handlerDirectLeader(orgUserVo, orgUserData, versionId);
        }

        orgUserVo.setPassword(null);
        multiOrgService.modifyUser(orgUserVo);

        ImportEntity importEntity = new ImportEntity();
        importEntity.setPostProcess(postProcess);
        importEntity.setSorce(orgUserData);
        importEntity.setObj(multiOrgUserAccount);
        return importEntity;
    }

    @Override
    public void update(MultiOrgUserAccount multiOrgUserAccount, OrgUserData orgUserData, Map<String, String> dependentDataMap) {

    }

    private void handlerDirectLeader(OrgUserVo orgUserVo, OrgUserData orgUserData, String versionId) {
        String[] directLeaderIdPaths = orgUserData.getDirectLeaderIdPaths().split(Separator.SEMICOLON.getValue());

        String directLeaderIdPath;
        DataImportTaskLog dataImportTaskLog;
        MultiOrgUserAccount leader = null;
        MultiOrgElement ele = null;
        List<String> newLeaderIdPaths = Lists.newArrayList();
        List<String> newLeaderNamePaths = Lists.newArrayList();
        for (int i = 0, len = directLeaderIdPaths.length; i < len; i++) {
            directLeaderIdPath = directLeaderIdPaths[i];

            String leaderId = directLeaderIdPath.substring(directLeaderIdPath.lastIndexOf(MultiOrgService.PATH_SPLIT_SYSMBOL) + 1, directLeaderIdPath.length());
            dataImportTaskLog = dataImportTaskLogService.getBySourceId(leaderId);

            // 更新直属上级领导
            if (leaderId.startsWith(IdPrefix.USER.getValue())) {
                if (null != dataImportTaskLog) {
                    leader = multiOrgUserAccountService.getAccountById(dataImportTaskLog.getAfterImportId());
                    // 如果用户不存在的话，不关联。。信息有限，不去创建用户
                    if (null != leader) {
                        newLeaderIdPaths.add(versionId + MultiOrgService.PATH_SPLIT_SYSMBOL + leader.getId());
                        newLeaderNamePaths.add(leader.getUserName());
                    }
                }
            } else {
                if (null != dataImportTaskLog) {
                    ele = multiOrgElementService.getById(dataImportTaskLog.getAfterImportId());
                    if (null != ele) {
                        newLeaderIdPaths.add(versionId + MultiOrgService.PATH_SPLIT_SYSMBOL + ele.getId());

                        Map<String, OrgNode> map = multiOrgTreeDialogService.smartName(1, Lists.newArrayList(ele.getId()), Lists.newArrayList(ele.getName()));
                        newLeaderNamePaths.add(map.get(ele.getId()).getNamePath());

                        /*List<MultiOrgTreeNode> nodes = multiOrgTreeNodeService.queryNodeByEleId(ele.getId());
                        if (CollectionUtils.isNotEmpty(nodes)) {
                            multiOrgTreeNode = nodes.get(0);
                            newLeaderIdPaths.add(multiOrgTreeNode.getEleIdPath());
                            newLeaderNamePaths.add(ele.getName());
                        }*/
                    }
                }
            }
        }
        orgUserVo.setDirectLeaderIds(StringUtils.join(newLeaderIdPaths, Separator.SEMICOLON.getValue()));
        orgUserVo.setDirectLeaderNamePaths(StringUtils.join(newLeaderNamePaths, Separator.SEMICOLON.getValue()));
//        multiOrgService.modifyUser(orgUserVo);
    }

    private void handlerMainJob(OrgUserVo orgUserVo, String versionId, String systemUnitId, String mainJobIdPath, String mainJobNamePath) {
        DataImportApi dataImportApi = ApplicationContextHolder.getBean(DataImportApi.class);

        String eleId = mainJobIdPath.substring(mainJobIdPath.lastIndexOf(MultiOrgService.PATH_SPLIT_SYSMBOL) + 1, mainJobIdPath.length());
        String eleName;
        DataImportTaskLog dataImportTaskLog = dataImportTaskLogService.getBySourceId(eleId);
        MultiOrgTreeNode multiOrgTreeNode = null;
        String newJobIdPath = "";
        if (null != dataImportTaskLog) {
            // 之前已导过这个职位的数据
            eleId = dataImportTaskLog.getAfterImportId();
            multiOrgTreeNode = multiOrgTreeNodeService.getNodeByEleIdAndOrgVersion(eleId, versionId);
            if (null != multiOrgTreeNode) {
                newJobIdPath = multiOrgTreeNode.getEleIdPath();
            }
        }

        MultiOrgVersion version = multiOrgVersionService.getById(versionId);
        String versionName = version.getName();
        mainJobIdPath = versionId + mainJobIdPath.replace(mainJobIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL)[0], "");
        mainJobNamePath = versionName + mainJobNamePath.replace(mainJobNamePath.split(MultiOrgService.PATH_SPLIT_SYSMBOL)[0], "");

        MultiOrgElement ele = null;
        if (StringUtils.isBlank(newJobIdPath)) {
            String[] idPaths = mainJobIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            String[] namePaths = mainJobNamePath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            StringBuffer parentId = new StringBuffer(versionId);
            StringBuffer parentName = new StringBuffer(versionName);
//            List<OrgNodeInfoData> list = dataImportApi.getAllNodeInfo(versionId);
            OrgNodeInfoData tmp = null;
            for (int j = 1, pathLen = idPaths.length; j < pathLen; j++) {
                eleId = idPaths[j];
                eleName = namePaths[j];

                if (StringUtils.isNotBlank(eleName)) {
                    // 判断每个节点是否存在
                    tmp = existSameNode(orgNodeInfoDataList, eleId.substring(0, 1), eleName, parentName.toString() + MultiOrgService.PATH_SPLIT_SYSMBOL + eleName);
                    if (null == tmp) {
                        // 节点不存在
                        ele = new MultiOrgElement();
                        ele.setName(namePaths[j]);
                        ele.setSystemUnitId(systemUnitId);
                        ele.setType(eleId.substring(0, 1));
                        ele.setId(dataImportApi.generateElementId(eleId.substring(0, 1)));
                        multiOrgElementService.save(ele);
                        // 同步数据到组织树中去，添加子节点数据
                        MultiOrgTreeNode node = new MultiOrgTreeNode();
                        node.setEleId(ele.getId());
                        node.setEleIdPath(parentId + MultiOrgService.PATH_SPLIT_SYSMBOL + ele.getId());
                        node.setOrgVersionId(versionId);
                        this.multiOrgTreeNodeService.save(node);
                        // 不存在，新建节点，此时的id已发生变化
                        eleId = ele.getId();

                        addOrgNodeInfoData(ele.getId(), node.getEleIdPath(), eleName, parentName.toString() + MultiOrgService.PATH_SPLIT_SYSMBOL + eleName, eleId.substring(0, 1));
                    } else {
                        eleId = tmp.getEleId();
                    }
                    parentId.append(MultiOrgService.PATH_SPLIT_SYSMBOL + eleId);
                    parentName.append(MultiOrgService.PATH_SPLIT_SYSMBOL + eleName);
                }
            }
            newJobIdPath = parentId.toString();
        }

        orgUserVo.setMainJobId(versionId + MultiOrgService.PATH_SPLIT_SYSMBOL + eleId);
        orgUserVo.setMainJobIdPath(newJobIdPath);
    }

    private void handleOtherJob(OrgUserVo orgUserVo, String versionId, String systemUnitId, String[] otherJobIdPaths, String[] otherJobNamePaths) {
        DataImportApi dataImportApi = ApplicationContextHolder.getBean(DataImportApi.class);
//        List<OrgNodeInfoData> list= dataImportApi.getAllNodeInfo(versionId);

        MultiOrgVersion version = multiOrgVersionService.getById(versionId);
        String versionName = version.getName();

        MultiOrgElement ele = null;
        List<String> fullPaths = Lists.newArrayList();
        List<String> shortPaths = Lists.newArrayList();
        DataImportTaskLog dataImportTaskLog;
        MultiOrgTreeNode multiOrgTreeNode = null;
        for (int i = 0, idLen = otherJobIdPaths.length; i < idLen; i++) {
            String otherJobIdPath = otherJobIdPaths[i];
            String otherJobNamePath = otherJobNamePaths[i];
            String eleId = otherJobIdPath.substring(otherJobIdPath.lastIndexOf(MultiOrgService.PATH_SPLIT_SYSMBOL) + 1, otherJobIdPath.length());
            String eleName;
            dataImportTaskLog = dataImportTaskLogService.getBySourceId(eleId);
            String newJobIdPath = "";
            if (null != dataImportTaskLog) {
                // 之前已导过这个职位的数据
                eleId = dataImportTaskLog.getAfterImportId();
                multiOrgTreeNode = multiOrgTreeNodeService.getNodeByEleIdAndOrgVersion(eleId, versionId);
                if (null != multiOrgTreeNode) {
                    newJobIdPath = multiOrgTreeNode.getEleIdPath();
                }
            }

            if (StringUtils.isBlank(newJobIdPath)) {
                otherJobIdPath = versionId + otherJobIdPath.replace(otherJobIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL)[0], "");
                String[] idPaths = otherJobIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                String[] namePaths = otherJobNamePath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                StringBuffer parentId = new StringBuffer(versionId);
                StringBuffer parentName = new StringBuffer(versionName);
                OrgNodeInfoData tmp = null;
                for (int j = 1, pathLen = idPaths.length; j < pathLen; j++) {
                    eleId = idPaths[j];
                    eleName = namePaths[j];

                    if (StringUtils.isNotBlank(eleName)) {
                        // 判断每个节点是否存在
                        tmp = existSameNode(orgNodeInfoDataList, eleId.substring(0, 1), eleName, parentName.toString() + MultiOrgService.PATH_SPLIT_SYSMBOL + eleName);
                        if (null == tmp) {
                            // 节点不存在
                            ele = new MultiOrgElement();
                            ele.setName(namePaths[j]);
                            ele.setSystemUnitId(systemUnitId);
                            ele.setType(eleId.substring(0, 1));
                            ele.setId(dataImportApi.generateElementId(eleId.substring(0, 1)));
                            multiOrgElementService.save(ele);
                            // 同步数据到组织树中去，添加子节点数据
                            MultiOrgTreeNode node = new MultiOrgTreeNode();
                            node.setEleId(ele.getId());
                            node.setEleIdPath(parentId + MultiOrgService.PATH_SPLIT_SYSMBOL + ele.getId());
                            node.setOrgVersionId(versionId);
                            this.multiOrgTreeNodeService.save(node);
                            // 不存在，新建节点，此时的id已发生变化
                            eleId = ele.getId();

                            addOrgNodeInfoData(eleId, node.getEleIdPath(), eleName, parentName.toString() + MultiOrgService.PATH_SPLIT_SYSMBOL + eleName, eleId.substring(0, 1));
                        } else {
                            eleId = tmp.getEleId();
                        }
                        parentId.append(MultiOrgService.PATH_SPLIT_SYSMBOL + eleId);
                        parentName.append(MultiOrgService.PATH_SPLIT_SYSMBOL + eleName);
                    }
                }
                newJobIdPath = parentId.toString();
            }

            shortPaths.add(versionId + MultiOrgService.PATH_SPLIT_SYSMBOL + eleId);
            fullPaths.add(newJobIdPath);
        }
        orgUserVo.setOtherJobIdPaths(StringUtils.join(fullPaths, Separator.SEMICOLON.getValue()));
        orgUserVo.setOtherJobIds(StringUtils.join(shortPaths, Separator.SEMICOLON.getValue()));
    }

    private void addOrgNodeInfoData(String eleId, String eleIdPath, String name, String eleNamePath, String type) {
        OrgNodeInfoData orgNodeInfoData = new OrgNodeInfoData();
        orgNodeInfoData.setEleId(eleId);
        orgNodeInfoData.setEleIdPath(eleIdPath);
        orgNodeInfoData.setName(name);
        orgNodeInfoData.setEleNamePath(eleNamePath);
        orgNodeInfoData.setType(type);
        orgNodeInfoDataList.add(orgNodeInfoData);
    }

}
