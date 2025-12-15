package com.wellsoft.pt.log.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.web.converter.MappingCodehausJacksonHttpMessageConverter;
import com.wellsoft.context.web.converter.UTF8StringHttpMessageConverter;
import com.wellsoft.distributedlog.dto.LogDTO;
import com.wellsoft.distributedlog.request.LogRequestParams;
import com.wellsoft.distributedlog.response.DefaultLogResponse;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.log.service.ElasticSearchLogService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/12/21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/12/21    chenq		2019/12/21		Create
 * </pre>
 */
@Component
public class DistributedlogDataStoreRestQuery extends AbstractDataStoreQueryInterface implements InitializingBean {


    @Autowired
    ElasticSearchLogService elasticSearchLogService;

    RestTemplate restTemplate;

    @Value("${distributedlog.server.url:'}")
    private String distributedlogServerUrl;

    private String queryUrl;

    private String countUrl;

    public DistributedlogDataStoreRestQuery() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
        objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        MappingCodehausJacksonHttpMessageConverter jackson2HttpMessageConverter = new MappingCodehausJacksonHttpMessageConverter(
                objectMapper);
        jackson2HttpMessageConverter.setSupportedMediaTypes(Lists.newArrayList(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON));
        List<HttpMessageConverter<?>> converters = Lists.newArrayListWithCapacity(1);
        converters.add(new UTF8StringHttpMessageConverter());
        converters.add(jackson2HttpMessageConverter);
        restTemplate = new RestTemplate(converters);
    }

    @Override
    public String getQueryName() {
        return "平台管理_日志管理_分布式日志";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "uuid", "uuid", String.class);
        criteriaMetadata.add("loglevel", "loglevel", "日志等级", String.class);
        criteriaMetadata.add("message", "message", "日志内容", String.class);
        criteriaMetadata.add("timestamp", "timestamp", "创建时间", String.class);
        criteriaMetadata.add("ip", "ip", "应用IP", String.class);
        criteriaMetadata.add("preIp", "preIp", "前置应用IP", String.class);
        criteriaMetadata.add("traceId", "traceId", "请求追踪ID", String.class);
        criteriaMetadata.add("spanId", "spanId", "调用链ID", String.class);
        criteriaMetadata.add("app", "app", "应用名称", String.class);
        criteriaMetadata.add("preApp", "preApp", "前置应用名称", String.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractDataStoreQueryInterface#query(QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        HttpEntity<LogRequestParams> entity = defaultHttpEntity(queryContext);
        if (queryContext.getPagingInfo() != null) {
            entity.getBody().setPageIndex(queryContext.getPagingInfo().getCurrentPage());
            entity.getBody().setPageSize(queryContext.getPagingInfo().getPageSize());
        }
        ResponseEntity<DefaultLogResponse> logResponse = null;
        try {
            logResponse = restTemplate.postForEntity(this.queryUrl, entity, DefaultLogResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("分布式日志服务异常");
        }
        DefaultLogResponse response = logResponse.getBody();
        if (response.getCode() != 0) {
            return Collections.EMPTY_LIST;
        }
        List<QueryItem> queryItemList = Lists.newArrayListWithCapacity(response.getData().getLogDTOs().size());
        for (LogDTO dto : response.getData().getLogDTOs()) {
            QueryItem item = new QueryItem();
            item.put("uuid", dto.getId());
            item.put("message", dto.getContent());
            item.put("logLevel", dto.getLogLevel());
            item.put("timestamp", dto.getLogTime());
            item.put("ip", dto.getIp());
            item.put("preIp", dto.getPreIp());
            item.put("preApp", dto.getPreApp());
            item.put("app", dto.getApp());
            item.put("traceId", dto.getTraceId());
            item.put("spanId", dto.getSpanId());
            queryItemList.add(item);
        }
        return queryItemList;
    }

    private LogRequestParams setRequestParams(QueryContext queryContext) {
        LogRequestParams params = new LogRequestParams();
        Map<String, Object> queryParams = queryContext.getQueryParams();
        if (StringUtils.isNotBlank((String) queryParams.get("message"))) {
            params.setContent((String) queryParams.get("message"));
        }
        if (StringUtils.isNotBlank((String) queryParams.get("loglevel"))) {
            params.setLogLevel(((String) queryParams.get("loglevel")).split(",|;"));
        }


        String startStr = (String) queryParams.get("timestamp");
        if (StringUtils.isNotBlank(startStr)) {
            // 如果开始时间大于当前时间，抛出异常
            Date startDate = timeParser(startStr);
            if (startDate != null && startDate.getTime() > System.currentTimeMillis()) {
                throw new RuntimeException("开始时间大于当前时间，参数不符合要求");
            } else {
                params.setBeginTime(startDate);
            }
        }

        String endStr = (String) queryParams.get("timestamp_END");
        Date endDate = null;
        if (StringUtils.isNotBlank(endStr)) {
            endDate = timeParser(endStr);
        }
        // 结束时间大于当前时间。则为当前时间
        if (endDate != null && endDate.getTime() > System.currentTimeMillis()) {
            params.setEndTime(new Date());
        } else {
            params.setEndTime(endDate);
        }

        return params;
    }


    private Date timeParser(String timeStr) {
        try {
            return DateUtils.parseDate(timeStr, "yyyy-MM-dd HH:mm:ss:SSS", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
                    "yyyy-MM-dd HH", "yyyy-MM-dd", "yyyy年MM月dd日 HH时mm分ss秒SSS毫秒", "yyyy年MM月dd日 HH时mm分ss秒",
                    "yyyy年MM月dd日 HH时mm分", "yyyy年MM月dd日 HH时", "yyyy年MM月dd日");
        } catch (Exception e) {
            LOGGER.error("系统日志时间转换异常，无法转换时间字符串：" + timeStr);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        ResponseEntity<Long> logResponse = null;
        try {
            logResponse = restTemplate.postForEntity(this.countUrl, defaultHttpEntity(queryContext), Long.class);
        } catch (Exception e) {
            throw new RuntimeException("分布式日志服务异常");
        }


        return logResponse != null ? logResponse.getBody() : 0L;
    }

    private HttpEntity<LogRequestParams> defaultHttpEntity(QueryContext queryContext) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new HttpEntity<LogRequestParams>(setRequestParams(queryContext), requestHeaders);

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isNotBlank(distributedlogServerUrl)) {
            this.queryUrl = distributedlogServerUrl + "/query";
            this.countUrl = distributedlogServerUrl + "/count";
        }
    }
}
