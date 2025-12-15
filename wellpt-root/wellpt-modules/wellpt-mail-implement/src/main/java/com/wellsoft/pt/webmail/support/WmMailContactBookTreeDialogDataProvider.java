package com.wellsoft.pt.webmail.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.multi.org.bean.OrgTreeDialLogAsynParms;
import com.wellsoft.pt.multi.org.bean.OrgTreeDialogParams;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgTreeDialogProvider;
import com.wellsoft.pt.multi.org.support.utils.ConvertOrgNode;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.constant.MailConstant;
import com.wellsoft.pt.webmail.entity.WmMailContactBookEntity;
import com.wellsoft.pt.webmail.entity.WmMailContactBookGroupEntity;
import com.wellsoft.pt.webmail.service.WmMailContactBookGrpService;
import com.wellsoft.pt.webmail.service.WmMailContactBookService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Description: 邮件个人通讯录组织树
 *
 * @author chenq
 * @date 2018/6/6
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/6    chenq		2018/6/6		Create
 * </pre>
 */
@Component
public class WmMailContactBookTreeDialogDataProvider extends
        AbstractMailContackBookTreeDialogDataProvider implements OrgTreeDialogProvider {

    private final static String CONTACTBOOK_TREE_TYPE = "MailContactBook";

    @Autowired
    WmMailContactBookGrpService wmMailContactBookGrpService;

    @Autowired
    WmMailContactBookService wmMailContactBookService;

    @Override
    public String getType() {
        return CONTACTBOOK_TREE_TYPE;
    }

    @Override
    public List<TreeNode> provideData(OrgTreeDialogParams p) {
        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        List<WmMailContactBookGroupEntity> groupEntityList = wmMailContactBookGrpService.listByUserId(
                currentUserId);
        List<WmMailContactBookEntity> bookEntities = wmMailContactBookService.listByUserId(
                currentUserId);
        List<TreeNode> treeNodes = Lists.newArrayList();
        for (WmMailContactBookGroupEntity grp : groupEntityList) {
            TreeNode grpNode = new TreeNode();
            grpNode.setId(grp.getGroupId());
            grpNode.setType(IdPrefix.GROUP.getValue());
            grpNode.setName(grp.getGroupName());
            List<WmMailContactBookEntity> grpContactBookEntities = wmMailContactBookService.listByGrpId(grp.getGroupId());
            for (WmMailContactBookEntity book : grpContactBookEntities) {
                TreeNode bookNode = new TreeNode();
                bookNode.setId(book.getContactId());
                bookNode.setName(book.getContactName());
                bookNode.setType(IdPrefix.USER.getValue());
                grpNode.getChildren().add(bookNode);
            }
            treeNodes.add(grpNode);
        }

        for (WmMailContactBookEntity bookEntity : bookEntities) {
            if (StringUtils.isBlank(bookEntity.getGroupUuid())) {
                TreeNode bookNode = new TreeNode();
                bookNode.setId(bookEntity.getContactId());
                bookNode.setName(bookEntity.getContactName());
                bookNode.setType(IdPrefix.USER.getValue());
                treeNodes.add(bookNode);
            }
        }

        return treeNodes;
    }

    @Override
    public List<javax.mail.Address> explainMailAddress(String id) throws Exception {
        if (StringUtils.isNotBlank(id)) {
            if (id.startsWith(MailConstant.CONTACT_BOOK_ID_PREFIX)) {// 通讯录ID
                WmMailContactBookEntity bookEntity = wmMailContactBookService.getByContactId(
                        id);
                if (bookEntity != null) {
                    return Lists.newArrayList(
                            WmMailUtils.getMailAddress(bookEntity.getContactName(),
                                    bookEntity.getPersonalEmail()));
                }
            } else if (id.startsWith(MailConstant.CONTACT_BOOK_GRP_ID_PREFIX)) {// 通讯录分组ID
                List<String> contactIds = wmMailContactBookService.listContactIdByGrpId(id);
                if (CollectionUtils.isNotEmpty(contactIds)) {
                    List<javax.mail.Address> addressList = Lists.newArrayList();
                    for (String addr : contactIds) {
                        addressList.addAll(explainMailAddress(addr));
                    }
                    return addressList;
                }
            }
        }
        return null;
    }

    @Override
    public boolean match(String id) {
        return id != null && (id.startsWith(MailConstant.CONTACT_BOOK_ID_PREFIX)
                || id.startsWith(
                MailConstant.CONTACT_BOOK_GRP_ID_PREFIX));
    }


    @Override
    public List<OrgNode> children(OrgTreeDialLogAsynParms parms) {
        List<OrgNode> orgNodes = new ArrayList<>();
        if (StringUtils.isEmpty(parms.getTreeNodeId())) {
            String currentUserId = SpringSecurityUtils.getCurrentUserId();
            List<WmMailContactBookGroupEntity> groupEntityList = wmMailContactBookGrpService.listByUserId(
                    currentUserId);
            List<WmMailContactBookEntity> bookEntities = wmMailContactBookService.listByUserId(
                    currentUserId);
            for (WmMailContactBookGroupEntity grp : groupEntityList) {
                OrgNode grpNode = this.convert(grp);
                WmMailContactBookEntity bookEntity = new WmMailContactBookEntity();
                bookEntity.setGroupUuid(grp.getUuid());
                long count = wmMailContactBookService.getDao().countByEntity(bookEntity);
                if (count > 0l) {
                    grpNode.setIsParent(true);
                }
                orgNodes.add(grpNode);
            }
            for (WmMailContactBookEntity bookEntity : bookEntities) {
                if (StringUtils.isBlank(bookEntity.getGroupUuid())) {
                    OrgNode bookNode = this.convert(bookEntity);
                    orgNodes.add(bookNode);
                }
            }
            return orgNodes;
        }
        List<WmMailContactBookEntity> grpContactBookEntities = wmMailContactBookService.listByGrpId(parms.getTreeNodeId());
        for (WmMailContactBookEntity book : grpContactBookEntities) {
            OrgNode bookNode = this.convert(book);
            orgNodes.add(bookNode);
        }
        return orgNodes;
    }

    private OrgNode convert(WmMailContactBookGroupEntity grp) {
        OrgNode grpNode = new OrgNode();
        grpNode.setId(grp.getGroupId());
        grpNode.setType(IdPrefix.GROUP.getValue());
        grpNode.setName(grp.getGroupName());
        return grpNode;
    }

    private OrgNode convert(WmMailContactBookEntity bookEntity) {
        OrgNode bookNode = new OrgNode();
        bookNode.setId(bookEntity.getContactId());
        bookNode.setName(bookEntity.getContactName());
        bookNode.setType(IdPrefix.USER.getValue());
        return bookNode;
    }

    @Override
    public List<OrgNode> full(OrgTreeDialLogAsynParms parms) {
        List<OrgNode> orgNodes = new ArrayList<>();
        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        List<WmMailContactBookGroupEntity> groupEntityList = wmMailContactBookGrpService.listByUserId(
                currentUserId);
        List<WmMailContactBookEntity> bookEntities = wmMailContactBookService.listByUserId(
                currentUserId);
        for (WmMailContactBookGroupEntity grp : groupEntityList) {
            OrgNode grpNode = this.convert(grp);
            ConvertOrgNode.checked(grpNode, parms.getCheckedIds(), null);
            List<WmMailContactBookEntity> grpContactBookEntities = wmMailContactBookService.listByGrpId(grp.getGroupId());
            if (grpContactBookEntities.size() > 0) {
                grpNode.setChildren(new ArrayList<OrgNode>());
                for (WmMailContactBookEntity book : grpContactBookEntities) {
                    OrgNode bookNode = this.convert(book);
                    ConvertOrgNode.checked(bookNode, parms.getCheckedIds(), null);
                    grpNode.getChildren().add(bookNode);
                }
            }
            orgNodes.add(grpNode);
        }
        for (WmMailContactBookEntity bookEntity : bookEntities) {
            if (StringUtils.isBlank(bookEntity.getGroupUuid())) {
                OrgNode bookNode = this.convert(bookEntity);
                ConvertOrgNode.checked(bookNode, parms.getCheckedIds(), null);
                orgNodes.add(bookNode);
            }
        }
        return orgNodes;
    }

    @Override
    public List<OrgNode> search(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getKeyword(), "keyword 不能为空");
        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("creator", currentUserId);
        query.put("contactName", "%" + parms.getKeyword() + "%");
        List<WmMailContactBookGroupEntity> groupEntityList = wmMailContactBookGrpService.listByUserId(currentUserId);
        Set<String> groupUuidSet = new HashSet<>();
        List<OrgNode> orgNodes = new ArrayList<>();
        for (WmMailContactBookGroupEntity wmMailContactBookGroupEntity : groupEntityList) {
            groupUuidSet.add(wmMailContactBookGroupEntity.getUuid());
            if (wmMailContactBookGroupEntity.getGroupName().contains(parms.getKeyword())) {
                OrgNode grpNode = this.convert(wmMailContactBookGroupEntity);
                orgNodes.add(grpNode);
            }
        }
        StringBuilder hql = new StringBuilder("from WmMailContactBookEntity where contactName like :contactName ");
        if (groupUuidSet.size() > 0) {
            query.put("groupUuids", groupUuidSet);
            hql.append(" and (creator = :creator or groupUuid in (:groupUuids) ) ");
        } else {
            hql.append(" and creator = :creator ");
        }
        List<WmMailContactBookEntity> bookEntities = wmMailContactBookService.listByHQL(hql.toString(), query);
        List<OrgNode> userNodes = new ArrayList<>();
        for (WmMailContactBookEntity bookEntity : bookEntities) {
            OrgNode bookNode = this.convert(bookEntity);
            userNodes.add(bookNode);
        }
        userNodes.addAll(orgNodes);
        return userNodes;
    }
}
