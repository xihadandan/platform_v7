package com.wellsoft.pt.dms.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.dms.bean.DmsDocExcContactBookDto;
import com.wellsoft.pt.dms.dao.impl.DmsDocExcContactBookDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDocExcContactBookEntity;
import com.wellsoft.pt.dms.entity.DmsDocExcContactBookUnitEntity;
import com.wellsoft.pt.dms.enums.DocExcContactBookIdPrefixEnum;
import com.wellsoft.pt.dms.service.DmsDocExcContactBookService;
import com.wellsoft.pt.dms.service.DmsDocExcContactBookUnitService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Description: 文档交换-通讯录服务实现类
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
@Service
public class DmsDocExcContactBookServiceImpl extends
        AbstractJpaServiceImpl<DmsDocExcContactBookEntity, DmsDocExcContactBookDaoImpl, String> implements
        DmsDocExcContactBookService {

    @Autowired
    DmsDocExcContactBookUnitService dmsDocExcContactBookUnitService;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Override
    @Transactional
    public void saveContactBook(DmsDocExcContactBookDto contactBookDto) {
        DmsDocExcContactBookEntity contactBookEntity = new DmsDocExcContactBookEntity();
        if (StringUtils.isNotBlank(contactBookDto.getUuid())) {
            contactBookEntity = getOne(contactBookDto.getUuid());
            BeanUtils.copyProperties(contactBookDto, contactBookEntity,
                    ArrayUtils.addAll(IdEntity.BASE_FIELDS, "contactId",
                            "systemUnitId", "moduleId"));
        } else {
            BeanUtils.copyProperties(contactBookDto, contactBookEntity);
            contactBookEntity.setContactId(
                    DocExcContactBookIdPrefixEnum.CONTACT_ID.getId() + DateFormatUtils.format(
                            new Date(),
                            "yyMMddHHmmssSSS"));
        }
        save(contactBookEntity);
    }

    @Override
    public List<DmsDocExcContactBookEntity> listByUnitUuid(String unitUuid) {
        return dao.listByUnitUuid(unitUuid);
    }

    @Override
    public List<DmsDocExcContactBookEntity> listByUserId(String currentUserId) {
        return dao.listByUserId(currentUserId);
    }

    @Override
    public List<DmsDocExcContactBookEntity> listByUserIdAndModule(String currentUserId,
                                                                  String moduleId) {
        return dao.listByUserIdAndModule(currentUserId, moduleId);
    }

    @Override
    public List<DmsDocExcContactBookEntity> listByUnitId(String unitId) {
        DmsDocExcContactBookUnitEntity unitEntity = dmsDocExcContactBookUnitService.getByUnitId(
                unitId);
        if (unitEntity != null) {
            return this.listByUnitUuid(unitEntity.getUuid());
        }
        return Lists.newArrayList();
    }

    @Override
    public DmsDocExcContactBookEntity getByContactId(String contactId) {
        List<DmsDocExcContactBookEntity> bookEntities = this.dao.listByFieldEqValue("contactId",
                contactId);
        return CollectionUtils.isNotEmpty(bookEntities) ? bookEntities.get(0) : null;
    }

    @Override
    public List<String> explainUserIdsByContactBookId(String ui) {
        if (ui.startsWith(DocExcContactBookIdPrefixEnum.CONTACT_ID.getId())) {
            DmsDocExcContactBookEntity bookEntity = this.getByContactId(ui);
            if (bookEntity != null) {
                return StringUtils.isNotBlank(
                        bookEntity.getRelaUserId()) ? Lists.newArrayList(
                        bookEntity.getRelaUserId()) : Lists.newArrayList(bookEntity.getContactId());
            }
        } else if (ui.startsWith(DocExcContactBookIdPrefixEnum.CONTACT_UNIT_ID.getId())) {
            List<DmsDocExcContactBookEntity> bookEntities = this.listByUnitId(ui);
            if (CollectionUtils.isEmpty(bookEntities)) {
                return Lists.newArrayList(ui);
            }
            List<String> userIds = Lists.newArrayList();
            for (DmsDocExcContactBookEntity bookEntity : bookEntities) {
                if (StringUtils.isNotBlank(bookEntity.getRelaUserId())) {
                    userIds.add(bookEntity.getRelaUserId());
                } else {
                    userIds.add(bookEntity.getContactId());
                }
            }
            return userIds;
        }

        return Lists.newArrayList();
    }

    @Override
    public Set<String> explainUserIdsBySelectIds(String ids) {
        Set<String> allUserIds = Sets.newLinkedHashSet();
        if (StringUtils.isBlank(ids)) {
            return allUserIds;
        }
        String[] toUserIdArr = ids.split(Separator.SEMICOLON.getValue());
        for (String userId : toUserIdArr) {
            if (userId.startsWith(
                    DocExcContactBookIdPrefixEnum.CONTACT_UNIT_ID.getId())
                    /*|| userId.startsWith(
                    DocExcContactBookIdPrefixEnum.CONTACT_ID.getId())*/) {//文档交换通讯录的
                /*allUserIds.addAll(
                        this.explainUserIdsByContactBookId(userId));*/
                //发文到通讯录的单位级别，不能发到其下联系人
                allUserIds.add(userId);
                continue;
            }
            if (userId.startsWith(
                    IdPrefix.EXTERNAL.getValue())) { //外部单位
                allUserIds.add(userId);
                continue;
            }
            if (userId.startsWith(IdPrefix.USER.getValue())) {
                allUserIds.add(userId);
                continue;
            }
            Set<String> getUserIds = orgApiFacade.queryUserIdListByOrgId(userId, true);
            if (CollectionUtils.isNotEmpty(getUserIds)) {
                allUserIds.addAll(getUserIds);
            }
        }
        return allUserIds;
    }

    @Override
    public List<DmsDocExcContactBookEntity> listBySysUnitIdAndModule(String unitId,
                                                                     String moduleId) {
        return this.dao.listBySysUnitIdAndModule(unitId, moduleId);
    }
}
