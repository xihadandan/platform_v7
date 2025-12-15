package com.wellsoft.distributedlog.es.service;

import com.wellsoft.distributedlog.dto.LogDTO;
import com.wellsoft.distributedlog.es.entity.LogEntity;
import com.wellsoft.distributedlog.request.LogRequestParams;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 日志数据服务类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年06月30日   chenq	 Create
 * </pre>
 */
public interface LogService {


    /**
     * 批量索引文档
     *
     * @param indexMap key: 索引名称  value: 数据集
     */
    void bulkIndex(Map<String, List<IndexQuery>> indexMap);

    void bulkIndex(ArrayList<LogDTO> logDTOs);

    boolean ping();

    Long count(LogRequestParams params);

    SearchHits<LogEntity> query(LogRequestParams params, PageRequest of);
}
