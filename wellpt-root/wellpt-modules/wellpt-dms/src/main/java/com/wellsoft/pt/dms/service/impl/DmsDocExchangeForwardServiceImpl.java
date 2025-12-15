package com.wellsoft.pt.dms.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.dms.bean.DmsDocExchangeForwardDto;
import com.wellsoft.pt.dms.core.support.DocExchangeActionData;
import com.wellsoft.pt.dms.dao.impl.DmsDocExchangeForwardDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDocExchangeForwardEntity;
import com.wellsoft.pt.dms.enums.DocExchangeNotifyWayEnum;
import com.wellsoft.pt.dms.enums.DocExchangeRecordStatusEnum;
import com.wellsoft.pt.dms.service.DmsDocExchangeForwardService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgApiFacade;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialClob;
import java.util.Collections;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/5/21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/21    chenq		2018/5/21		Create
 * </pre>
 */
@Service
public class DmsDocExchangeForwardServiceImpl extends
        AbstractJpaServiceImpl<DmsDocExchangeForwardEntity, DmsDocExchangeForwardDaoImpl, String> implements
        DmsDocExchangeForwardService {

    @Autowired
    private MultiOrgApiFacade multiOrgApiFacade;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Override
    @Transactional
    public String saveForwardDetail(String docExchangeRecordUuid,
                                    DocExchangeActionData.DocExcOperateData forwardData,
                                    DocExchangeRecordStatusEnum status,
                                    String fromUserId, String fromUnitId) {
        try {
            DocExchangeActionData.ToUserData userData = forwardData.getToUserData().get(0);
            DmsDocExchangeForwardEntity forwardEntity = new DmsDocExchangeForwardEntity(
                    docExchangeRecordUuid, new SerialClob(userData.getToUserId().toCharArray()),
                    new SerialClob(userData.getToUserName().toCharArray()),
                    userData.getFeedbackTimeLimit(), userData.getSignTimeLimit(),
                    userData.getNotifyWays().contains(DocExchangeNotifyWayEnum.IM),
                    userData.getNotifyWays().contains(DocExchangeNotifyWayEnum.SMS),
                    userData.getNotifyWays().contains(DocExchangeNotifyWayEnum.MAIL),
                    status,
                    forwardData.getContent(),
                    forwardData.getFileUuids(), forwardData.getFileNames(), fromUserId, fromUnitId
            );
            this.save(forwardEntity);
            return forwardEntity.getUuid();
        } catch (Exception e) {
            logger.error("保存转发详情异常：", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public DmsDocExchangeForwardDto getSignedForwardByRecordUuid(String uuid) {
        List<DmsDocExchangeForwardEntity> forwardEntities = this.dao.listForwardByRecordUuidAndStatus(
                uuid,
                DocExchangeRecordStatusEnum.SIGNED);
        if (CollectionUtils.isEmpty(forwardEntities)) {
            return null;
        }
        DmsDocExchangeForwardEntity forwardEntity = forwardEntities.get(0);
        return copyForwardEntity2Dto(forwardEntity);
    }

    private DmsDocExchangeForwardDto copyForwardEntity2Dto(
            DmsDocExchangeForwardEntity forwardEntity) {
        DmsDocExchangeForwardDto dto = new DmsDocExchangeForwardDto();
        BeanUtils.copyProperties(forwardEntity, dto, "toUserIds", "toUserNames");
        OrgUserVo user = multiOrgApiFacade.getUserById(dto.getFromUserId());
        dto.setFromUserName(user.getUserName());
        dto.setFromUserUnitName(orgApiFacade.getSystemUnitById(user.getSystemUnitId()).getName());
        try {
            dto.setToUserIds(IOUtils.toString(forwardEntity.getToUserIds().getCharacterStream()));
            dto.setToUserNames(
                    IOUtils.toString(forwardEntity.getToUserNames().getCharacterStream()));
        } catch (Exception e) {
            logger.error("查询文档交换转发详情，大字段拷贝异常：", e);
        }
        return dto;
    }

    @Override
    public List<DmsDocExchangeForwardDto> listSendedForwardByRecordUuid(String uuid) {
        List<DmsDocExchangeForwardEntity> forwardEntities = this.dao.listForwardByRecordUuidAndStatus(
                uuid,
                DocExchangeRecordStatusEnum.SENDED);
        if (CollectionUtils.isEmpty(forwardEntities)) {
            return Collections.EMPTY_LIST;
        }
        List<DmsDocExchangeForwardDto> forwardDtos = Lists.newArrayList();
        for (DmsDocExchangeForwardEntity forwardEntity : forwardEntities) {
            forwardDtos.add(this.copyForwardEntity2Dto(forwardEntity));
        }
        return forwardDtos;
    }

}
