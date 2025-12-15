package com.wellsoft.pt.dms.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.dms.entity.DmsDocExcContactBookEntity;
import com.wellsoft.pt.dms.entity.DmsDocExcContactBookUnitEntity;
import com.wellsoft.pt.dms.enums.DocExcContactBookIdPrefixEnum;
import com.wellsoft.pt.dms.service.DmsDocExcContactBookService;
import com.wellsoft.pt.dms.service.DmsDocExcContactBookUnitService;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.multi.org.bean.OrgTreeDialLogAsynParms;
import com.wellsoft.pt.multi.org.bean.OrgTreeDialogParams;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgTreeDialogDataProvider;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgTreeDialogProvider;
import com.wellsoft.pt.multi.org.support.utils.ConvertOrgNode;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/5/31
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/31    chenq		2018/5/31		Create
 * </pre>
 */
@Component
public class DocExcContactBookTreeDialogDataProvider implements OrgTreeDialogDataProvider, OrgTreeDialogProvider {

    public static final String CONTACTBOOK_TREE_TYPE = "DocExchangeContactBook";

    @Autowired
    DmsDocExcContactBookService dmsDocExcContactBookService;

    @Autowired
    DmsDocExcContactBookUnitService dmsDocExcContactBookUnitService;

    @Override
    public String getType() {
        return CONTACTBOOK_TREE_TYPE;
    }

    @Override
    public List<TreeNode> provideData(OrgTreeDialogParams p) {
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        String moduleId = p.getOtherParams().get("moduleId");
        boolean isShowUserNode = p.getOtherParams().containsKey(
                "isShowUserNode") && BooleanUtils.toBoolean(
                p.getOtherParams().get("isShowUserNode"));
        List<DmsDocExcContactBookUnitEntity> unitEntities = dmsDocExcContactBookUnitService.listBySysUnitIdAndModule(
                unitId, moduleId);

        List<TreeNode> treeNodes = Lists.newArrayList();
        for (DmsDocExcContactBookUnitEntity unit : unitEntities) {
            TreeNode unitNode = new TreeNode();
            unitNode.setId(unit.getUnitId());
            unitNode.setType(IdPrefix.ORG.getValue());
            unitNode.setName(unit.getUnitName());
            if (isShowUserNode) {
                List<DmsDocExcContactBookEntity> unitContacterEntities = dmsDocExcContactBookService.listByUnitUuid(
                        unit.getUuid());
                for (DmsDocExcContactBookEntity book : unitContacterEntities) {
                    TreeNode bookNode = new TreeNode();
                    bookNode.setId(book.getContactId());
                    bookNode.setName(book.getContactName());
                    bookNode.setType(IdPrefix.USER.getValue());
                    unitNode.getChildren().add(bookNode);

                }
            }
            treeNodes.add(unitNode);
        }

        if (isShowUserNode) {
            List<DmsDocExcContactBookEntity> dmsDocExcContactBookEntities = dmsDocExcContactBookService.listBySysUnitIdAndModule(
                    unitId, moduleId);
            for (DmsDocExcContactBookEntity bookEntity : dmsDocExcContactBookEntities) {
                if (StringUtils.isBlank(bookEntity.getContactUnitUuid())) {
                    TreeNode bookNode = new TreeNode();
                    bookNode.setId(bookEntity.getContactId());
                    bookNode.setName(bookEntity.getContactName());
                    bookNode.setType(IdPrefix.USER.getValue());
                    treeNodes.add(bookNode);
                }
            }

        }


        return treeNodes;
    }

    @Override
    public List<OrgNode> children(OrgTreeDialLogAsynParms parms) {
        String sysUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        String moduleId = parms.getOtherParams().get("moduleId");
        boolean isShowUserNode = parms.getOtherParams().containsKey("isShowUserNode") && BooleanUtils.toBoolean(parms.getOtherParams().get("isShowUserNode"));
        List<OrgNode> treeNodes = new ArrayList<>();
        //节点Id null 根节点
        if (org.apache.commons.lang.StringUtils.isBlank(parms.getTreeNodeId())) {
            List<DmsDocExcContactBookUnitEntity> unitEntities = dmsDocExcContactBookUnitService.listBySysUnitIdAndModule(sysUnitId, moduleId);
            Set<String> halfCheckSet = this.halfCheckIds(parms.getCheckedIds());
            for (DmsDocExcContactBookUnitEntity unitEntity : unitEntities) {
                OrgNode unitNode = new OrgNode();
                unitNode.setId(unitEntity.getUnitId());
                unitNode.setType(IdPrefix.ORG.getValue());
                unitNode.setName(unitEntity.getUnitName());
                unitNode.setIconSkin(IdPrefix.ORG.getValue());
                Map<String, Object> values = new HashMap<>();
                values.put("unitUuid", unitEntity.getUuid());
                long count = dmsDocExcContactBookService.getDao().countByHQL("select count(*) from DmsDocExcContactBookEntity where contactUnitUuid=:unitUuid ", values);
                unitNode.setIsParent(count > 0 ? true : null);
                //勾选状态处理
                ConvertOrgNode.checked(unitNode, parms.getCheckedIds(), halfCheckSet);
                treeNodes.add(unitNode);
            }
            if (isShowUserNode) {
                List<DmsDocExcContactBookEntity> dmsDocExcContactBookEntities = dmsDocExcContactBookService.listBySysUnitIdAndModule(sysUnitId, moduleId);
                for (DmsDocExcContactBookEntity bookEntity : dmsDocExcContactBookEntities) {
                    if (StringUtils.isBlank(bookEntity.getContactUnitUuid())) {
                        OrgNode bookNode = new OrgNode();
                        bookNode.setId(bookEntity.getContactId());
                        bookNode.setName(bookEntity.getContactName());
                        bookNode.setType(IdPrefix.USER.getValue());
                        bookNode.setIconSkin(IdPrefix.USER.getValue());
                        //勾选状态处理
                        ConvertOrgNode.checked(bookNode, parms.getCheckedIds(), null);
                        treeNodes.add(bookNode);
                    }
                }
            }
            return treeNodes;
        }


        //用户节点 直接返回
        if (parms.getTreeNodeId().startsWith(DocExcContactBookIdPrefixEnum.CONTACT_ID.getId())) {
            return treeNodes;
        }
        Map<String, Object> param = Maps.newHashMap();
        param.put("systemUnitId", sysUnitId);
        param.put("moduleId", moduleId);
        param.put("unitId", parms.getTreeNodeId());
        List<DmsDocExcContactBookUnitEntity> bookUnitEntityList = dmsDocExcContactBookUnitService.listByHQL("from DmsDocExcContactBookUnitEntity where systemUnitId=:systemUnitId and moduleId=:moduleId and unitId=:unitId ", param);
        if (bookUnitEntityList.isEmpty()) {
            return treeNodes;
        }
        DmsDocExcContactBookUnitEntity bookUnitEntity = bookUnitEntityList.get(0);
        List<DmsDocExcContactBookEntity> unitContacterEntities = dmsDocExcContactBookService.listByUnitUuid(bookUnitEntity.getUuid());
        for (DmsDocExcContactBookEntity book : unitContacterEntities) {
            OrgNode bookNode = new OrgNode();
            bookNode.setId(book.getContactId());
            bookNode.setName(book.getContactName());
            bookNode.setType(IdPrefix.USER.getValue());
            bookNode.setIconSkin(IdPrefix.USER.getValue());
            //勾选状态处理
            ConvertOrgNode.checked(bookNode, parms.getCheckedIds(), null);
            treeNodes.add(bookNode);
        }
        return treeNodes;
    }

    private Set<String> halfCheckIds(List<String> checkedIds) {
        Set<String> halfCheckSet = new HashSet<>();
        if (checkedIds == null || checkedIds.isEmpty()) {
            return halfCheckSet;
        }
        HashMap<String, Object> query = new HashMap<String, Object>();
        StringBuilder hqlSb = new StringBuilder("from DmsDocExcContactBookEntity a where a.contactUnitUuid is not null and ");
        HqlUtils.appendSql("a.contactId", query, hqlSb, Sets.<Serializable>newHashSet(checkedIds));
        List<DmsDocExcContactBookEntity> bookEntityList = dmsDocExcContactBookService.listByHQL(hqlSb.toString(), query);
        List<String> contactUnitUuidList = new ArrayList<>();
        for (DmsDocExcContactBookEntity bookEntity : bookEntityList) {
            contactUnitUuidList.add(bookEntity.getContactUnitUuid());
        }
        if (contactUnitUuidList.isEmpty()) {
            return halfCheckSet;
        }
        List<DmsDocExcContactBookUnitEntity> bookUnitEntityList = dmsDocExcContactBookUnitService.listByUuids(contactUnitUuidList);
        for (DmsDocExcContactBookUnitEntity bookUnitEntity : bookUnitEntityList) {
            halfCheckSet.add(bookUnitEntity.getUnitId());
        }
        return halfCheckSet;
    }

    @Override
    public List<OrgNode> full(OrgTreeDialLogAsynParms parms) {
        String sysUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        String moduleId = parms.getOtherParams().get("moduleId");
        boolean isShowUserNode = parms.getOtherParams().containsKey("isShowUserNode") && BooleanUtils.toBoolean(parms.getOtherParams().get("isShowUserNode"));
        List<OrgNode> treeNodes = new ArrayList<>();
        List<DmsDocExcContactBookUnitEntity> unitEntities = dmsDocExcContactBookUnitService.listBySysUnitIdAndModule(sysUnitId, moduleId);
        for (DmsDocExcContactBookUnitEntity unitEntity : unitEntities) {
            OrgNode unitNode = new OrgNode();
            unitNode.setId(unitEntity.getUnitId());
            unitNode.setType(IdPrefix.ORG.getValue());
            unitNode.setName(unitEntity.getUnitName());
            unitNode.setIconSkin(IdPrefix.ORG.getValue());
            //勾选状态处理
            ConvertOrgNode.checked(unitNode, parms.getCheckedIds(), null);
            if (isShowUserNode) {
                List<DmsDocExcContactBookEntity> unitContacterEntities = dmsDocExcContactBookService.listByUnitUuid(unitEntity.getUuid());
                for (DmsDocExcContactBookEntity book : unitContacterEntities) {
                    OrgNode bookNode = new OrgNode();
                    bookNode.setId(book.getContactId());
                    bookNode.setName(book.getContactName());
                    bookNode.setType(IdPrefix.USER.getValue());
                    bookNode.setIconSkin(IdPrefix.USER.getValue());
                    //勾选状态处理
                    ConvertOrgNode.checked(bookNode, parms.getCheckedIds(), null);
                    unitNode.getChildren().add(bookNode);
                }
            }
            treeNodes.add(unitNode);
        }
        if (isShowUserNode) {
            List<DmsDocExcContactBookEntity> dmsDocExcContactBookEntities = dmsDocExcContactBookService.listBySysUnitIdAndModule(sysUnitId, moduleId);
            for (DmsDocExcContactBookEntity bookEntity : dmsDocExcContactBookEntities) {
                if (StringUtils.isBlank(bookEntity.getContactUnitUuid())) {
                    OrgNode bookNode = new OrgNode();
                    bookNode.setId(bookEntity.getContactId());
                    bookNode.setName(bookEntity.getContactName());
                    bookNode.setType(IdPrefix.USER.getValue());
                    bookNode.setIconSkin(IdPrefix.USER.getValue());
                    //勾选状态处理
                    ConvertOrgNode.checked(bookNode, parms.getCheckedIds(), null);
                    treeNodes.add(bookNode);
                }
            }
        }
        return treeNodes;

    }

    @Override
    public List<OrgNode> search(OrgTreeDialLogAsynParms parms) {
        String sysUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        String moduleId = parms.getOtherParams().get("moduleId");
        boolean isShowUserNode = parms.getOtherParams().containsKey("isShowUserNode") && BooleanUtils.toBoolean(parms.getOtherParams().get("isShowUserNode"));
        List<OrgNode> treeNodes = new ArrayList<>();
        Map<String, Object> param = Maps.newHashMap();
        param.put("systemUnitId", sysUnitId);
        param.put("moduleId", moduleId);
        param.put("key_like", "%" + parms.getKeyword() + "%");
        List<DmsDocExcContactBookUnitEntity> bookUnitEntityList = dmsDocExcContactBookUnitService.listByHQL("from DmsDocExcContactBookUnitEntity where " +
                "systemUnitId=:systemUnitId " +
                "and moduleId=:moduleId " +
                "and (unitName like :key_like " +
                "or fullUnitName like :key_like)", param);
        for (DmsDocExcContactBookUnitEntity bookUnitEntity : bookUnitEntityList) {
            OrgNode unitNode = new OrgNode();
            unitNode.setId(bookUnitEntity.getUnitId());
            unitNode.setType(IdPrefix.ORG.getValue());
            unitNode.setName(bookUnitEntity.getUnitName());
            unitNode.setIconSkin(IdPrefix.ORG.getValue());
            treeNodes.add(unitNode);
        }
        if (isShowUserNode) {
            List<DmsDocExcContactBookEntity> bookEntityList = dmsDocExcContactBookService.listByHQL("from DmsDocExcContactBookEntity where " +
                    "systemUnitId=:systemUnitId " +
                    "and moduleId=:moduleId " +
                    "and contactName like :key_like ", param);
            for (DmsDocExcContactBookEntity book : bookEntityList) {
                OrgNode bookNode = new OrgNode();
                bookNode.setId(book.getContactId());
                bookNode.setName(book.getContactName());
                bookNode.setType(IdPrefix.USER.getValue());
                bookNode.setIconSkin(IdPrefix.USER.getValue());
                treeNodes.add(bookNode);
            }
        }
        return treeNodes;
    }

}
