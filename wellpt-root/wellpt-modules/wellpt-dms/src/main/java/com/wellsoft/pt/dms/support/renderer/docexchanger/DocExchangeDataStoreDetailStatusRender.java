package com.wellsoft.pt.dms.support.renderer.docexchanger;

import com.wellsoft.pt.dms.entity.DmsDocExchangeRecordDetailEntity;
import com.wellsoft.pt.dms.entity.DmsDocExchangeRecordEntity;
import com.wellsoft.pt.dms.enums.DocExchangeRecordStatusEnum;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Description: 文档交换-发送者查看所有接收用户的处理情况状态渲染器
 * 如果存在接收用户未签收完，则显示未签收完
 * 如果存在接收用户未反馈完，则显示未反馈完
 * 如果用户都签收都反馈完，则展示已办结
 *
 * @author chenq
 * @date 2018/5/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/26    chenq		2018/5/26		Create
 * </pre>
 */
@Component
public class DocExchangeDataStoreDetailStatusRender extends DocExchangeDataStoreRender {
    @Override
    public String doRenderData(String columnIndex, Object docExchangeRecordUuid,
                               Map<String, Object> rowData,
                               String param) {

        if (docExchangeRecordUuid != null) {
            DmsDocExchangeRecordEntity recordEntity = dmsDocExchangeRecordService.getOne(
                    docExchangeRecordUuid.toString());
            //文档交换状态已完结，则直接反馈已完结状态
            if (DocExchangeRecordStatusEnum.FINISH.ordinal() == recordEntity.getRecordStatus()) {
                return DocExchangeRecordStatusEnum.FINISH.getName();
            }
            List<DmsDocExchangeRecordDetailEntity> detailEntityList = dmsDocExchangeRecordDetailService.listByDocExchangeRecordUuid(
                    docExchangeRecordUuid.toString());
            int signCnt = 0;
            int feedbackCnt = 0;
            Iterator<DmsDocExchangeRecordDetailEntity> detailEntityIterator = detailEntityList.iterator();
            while (detailEntityIterator.hasNext()) {
                DmsDocExchangeRecordDetailEntity detailEntity = detailEntityIterator.next();
                if (detailEntity.getIsRevoked()) {
                    detailEntityIterator.remove();
                    continue;
                }
                /**
                 * 2020-2-1 modify by linxr:产品确认，需要反馈的公文，有用户退回且其他用户已签收时，发件状态显示为“未反馈完”
                 */
                //存在有待签收的或者已退回的用户的
                if (DocExchangeRecordStatusEnum.WAIT_SIGN.equals(detailEntity.getSignStatus())) {
//                        || DocExchangeRecordStatusEnum.RETURNED.equals(
//                        detailEntity.getSignStatus())) {
                    return "未签收完";
                }

                if (DocExchangeRecordStatusEnum.SIGNED.equals(detailEntity.getSignStatus()) || DocExchangeRecordStatusEnum.RETURNED.equals(
                        detailEntity.getSignStatus())) {
                    signCnt++;
                }
                if (detailEntity.getIsFeedback()) {
                    feedbackCnt++;
                }


            }

            if (recordEntity.getIsNeedSign() && signCnt != detailEntityList.size()) {
                return "未签收完";
            }
            if (recordEntity.getIsNeedFeedback() && feedbackCnt != detailEntityList.size()) {
                return "未反馈完";
            }

            return DocExchangeRecordStatusEnum.FINISH.getName();

        }

        return "";
    }

    @Override
    public String getType() {
        return "docExchangeDataStoreDetailStatusRender";
    }

    @Override
    public String getName() {
        return "数据管理_文档交换_文档明细状态渲染器";
    }
}
