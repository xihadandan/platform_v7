package com.wellsoft.pt.di.component.datastore;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreData;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreOrder;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService;
import com.wellsoft.pt.basicdata.datastore.support.Condition;
import com.wellsoft.pt.di.component.AbstractScheduledPollConsumer;
import com.wellsoft.pt.di.constant.DiConstant;
import com.wellsoft.pt.di.entity.DiDataInterationLogEntity;
import com.wellsoft.pt.di.service.DiDataInterationLogService;
import org.apache.camel.Processor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.activation.DataHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Description:数据交换组件-消费数据仓库数据
 *
 * @author chenq
 * @date 2019/7/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/15    chenq		2019/7/15		Create
 * </pre>
 */
public class DataStoreConsumer extends AbstractScheduledPollConsumer<DataStoreEndpoint> {


    public DataStoreConsumer(
            DataStoreEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected DataStoreData body() {
        String dataStoreId = super.endpoint.getDataStoreId();
        if (StringUtils.isBlank(dataStoreId)) {
            return null;
        }

        CdDataStoreService cdDataStoreService = ApplicationContextHolder.getBean(
                CdDataStoreService.class);
        DataStoreParams params = new DataStoreParams();
        params.setDataStoreId(dataStoreId);
        boolean isTimeRange = StringUtils.isNotBlank(super.endpoint.getTimeColumn());
        Date fromTime = null;
        Date endTime = new Date();
        int pageIndex = 1;//当前数据页
        int totalPage = 1;//总的数据页
        int pageLimit = super.endpoint.getLimit();
        setProperty(DiConstant.DI_CONSUMER_DATA_PAGE_INDEX, pageIndex);
        setProperty(DiConstant.DI_CONSUMER_DATA_TOTAL_PAGE, totalPage);
        setProperty(DiConstant.DI_CONSUMER_DATA_PAGE_LIMIT, pageLimit);
        boolean unfinishPage = false;
        try {
            DiDataInterationLogEntity logEntity = ApplicationContextHolder.getBean(
                    DiDataInterationLogService.class).getLatestDataLogByDiConfigUuid(
                    super.endpoint.getDiUuid());
            if (logEntity != null) {
                unfinishPage = logEntity.getPageIndex() < logEntity.getTotalPage();//是否未完成分页数据交换
                if (unfinishPage) {
                    pageIndex = logEntity.getPageIndex() + 1;//获取“下一页”
                    pageLimit = logEntity.getPageLimit();
                    fromTime = logEntity.getDataBeginTime();
                    endTime = logEntity.getDataEndTime();
                    endTime = endTime == null ? new Date() : endTime;

                    //下一个分页批次的属性值，保存到批次日志内
                    setProperty(DiConstant.DI_CONSUMER_DATA_PAGE_INDEX, pageIndex);
                    setProperty(DiConstant.DI_CONSUMER_DATA_TOTAL_PAGE, logEntity.getTotalPage());
                    setProperty(DiConstant.DI_CONSUMER_DATA_PAGE_LIMIT, pageLimit);
                } else {
                    fromTime = logEntity.getDataEndTime();//上一次数据交换的结束时间作为本次交换的开始时间
                }
            }

            //设置数据时间
            setProperty(DiConstant.DI_CONSUMER_DATA_BEGIN_TIME, fromTime);
            setProperty(DiConstant.DI_CONSUMER_DATA_END_TIME, endTime);

        } catch (Exception e) {
        }


        if (isTimeRange) {
            //按时间范围导出
            Condition timeCondition = new Condition();
            timeCondition.setType("sql");
            params.getParams().put("maxConsumeDataTime", endTime);
            String timeRangeSql = super.endpoint.getTimeColumn() + "<=:maxConsumeDataTime";
            if (fromTime != null) {
                params.getParams().put("minConsumeDataTime", fromTime);
                timeRangeSql += " and " + super.endpoint.getTimeColumn() + ">:minConsumeDataTime ";
            }
            timeCondition.setSql(timeRangeSql);
            params.getCriterions().add(timeCondition);
            params.setPagingInfo(null);
            params.getParams().put("maxConsumeDataTime", endTime);

            if (unfinishPage) {//继续下一页
                params.setPagingInfo(new PagingInfo(pageIndex - 1, pageLimit));
            } else {
                //非继续分页数据的情况下，判断当前批次的数据量是否过大，如果是则分页输出
                long totalCount = cdDataStoreService.loadCount(params);
                if (totalCount == 0) {
                    return null;
                }
                if (totalCount > this.endpoint.getLimit()) {
                    params.setPagingInfo(new PagingInfo(0, pageLimit));

                    //下一个分页批次的属性值，保存到批次日志内
                    setProperty(DiConstant.DI_CONSUMER_DATA_PAGE_INDEX, 1);
                    setProperty(DiConstant.DI_CONSUMER_DATA_TOTAL_PAGE,
                            Math.ceil((double) totalCount / pageLimit));
                    setProperty(DiConstant.DI_CONSUMER_DATA_PAGE_LIMIT, pageLimit);

                }
            }

            params.getOrders().add(
                    new DataStoreOrder(super.endpoint.getTimeColumn(), true));//按时间升序
        }
        ArrayList<Map<String, Object>> dataList = Lists.newArrayList();

        DataStoreData dataStoreData = null;
        dataStoreData = cdDataStoreService.loadData(params);
        if (CollectionUtils.isEmpty(dataStoreData.getData())) {
            return null;
        }
        dataList.addAll(dataStoreData.getData());
        return dataStoreData;
    }


    @Override
    protected Map<String, Object> headers() {
        return null;
    }

    @Override
    public Map<String, Object> properties() {
        Map<String, Object> dataStoreMap = Maps.newHashMap();
        dataStoreMap.put("dataStoreId", super.endpoint.getDataStoreId());
        dataStoreMap.put("dataUuidColumn", super.endpoint.getUuidColumn());
        return dataStoreMap;
    }

    @Override
    protected Map<String, DataHandler> attachments() {
        return null;
    }
}
