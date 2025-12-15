package com.wellsoft.pt.org.iexport.provider;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IExportTable;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.org.entity.OrgElementEntity;
import com.wellsoft.pt.org.entity.OrgUserEntity;
import com.wellsoft.pt.org.service.OrgElementService;
import com.wellsoft.pt.org.service.OrgUserService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年04月19日   chenq	 Create
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class UserIexportDataProvider extends AbstractIexportDataProvider<UserInfoEntity, String> {

    @Autowired
    private OrgUserService orgUserService;
    @Autowired
    private OrgElementService orgElementService;

    @Override
    public String getType() {
        return IexportType.User;
    }

    @Override
    public String getTreeName(UserInfoEntity userInfoEntity) {
        return userInfoEntity.getUserName();
    }

    @Override
    public TreeNode treeNode(String uuid) {
        TreeNode node = super.treeNode(uuid);
        node.setName("用户" + Separator.COLON.getValue() + node.getName());
        if (node != null) {
            // 查询用户的组织身份
            UserInfoEntity userInfoEntity = getEntity(uuid);
            List<OrgUserEntity> orgUserEntities = orgUserService.getAllOrgUserUnderPublishedOrgVersion(userInfoEntity.getUserId()
                    , RequestSystemContextPathResolver.system(), SpringSecurityUtils.getCurrentTenantId());
            if (CollectionUtils.isNotEmpty(orgUserEntities)) {
                for (OrgUserEntity orgUserEntity : orgUserEntities) {
                    if (StringUtils.isNotBlank(orgUserEntity.getOrgElementId())) {
                        OrgElementEntity orgElementEntity = orgElementService.getByIdAndOrgVersionUuid(orgUserEntity.getOrgElementId(), orgUserEntity.getOrgVersionUuid());
                        if (orgElementEntity != null) {
                            node.appendChild(this.exportTreeNodeByDataProvider(orgElementEntity.getUuid(), IexportType.OrgElement));
                        }
                    }
                }
            }
        }
        return node;
    }

    @Override
    protected List<IExportTable> childTableStream() {
        return Lists.newArrayList(new IExportTable("SELECT * FROM USER_ACCOUNT A WHERE A.UUID =:accountUuid "),
                new IExportTable("SELECT * FROM USER_INFO_EXT A WHERE A.USER_UUID =:uuid "),
                new IExportTable("SELECT * FROM USER_CREDENTIAL A WHERE A.LOGIN_NAME = (SELECT B.LOGIN_NAME WHERE USER_ACCOUNT B WHERE B.UUID=:accountUuid) ")
        );
    }
}
