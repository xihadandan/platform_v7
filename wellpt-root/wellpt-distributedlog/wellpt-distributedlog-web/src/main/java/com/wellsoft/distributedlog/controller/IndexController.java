package com.wellsoft.distributedlog.controller;

import com.google.common.collect.Lists;
import com.wellsoft.distributedlog.dto.LogDTO;
import com.wellsoft.distributedlog.es.entity.LogEntity;
import com.wellsoft.distributedlog.es.service.LogService;
import com.wellsoft.distributedlog.request.LogRequestParams;
import com.wellsoft.distributedlog.response.DefaultLogResponse;
import com.wellsoft.distributedlog.response.LogResponseDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Description: 日志控制层
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年07月01日   chenq	 Create
 * </pre>
 */
@Controller
@RequestMapping("/")
public class IndexController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LogService logService;

    @GetMapping("/")
    public @ResponseBody
    ResponseEntity<String> index() {
        logger.info("app is up !");
        return ResponseEntity.ok("UP");
    }

    @PostMapping("/log")
    public @ResponseBody
    ResponseEntity<String> log(@RequestBody ArrayList<LogDTO> logDTOs) {
        if (!CollectionUtils.isEmpty(logDTOs)) {
            try {
                logService.bulkIndex(logDTOs);
            } catch (Exception e) {
                return ResponseEntity.ok("fail");
            }
        }
        return ResponseEntity.ok("success");
    }


    @PostMapping("/query")
    public @ResponseBody
    DefaultLogResponse query(@RequestBody LogRequestParams params) {
        DefaultLogResponse response = new DefaultLogResponse();
        LogResponseDetails details = new LogResponseDetails();
        SearchHits<LogEntity> searchHit = logService.query(params, PageRequest.of(params.getPageIndex() <= 0 ? 0 : params.getPageIndex() - 1, params.getPageSize()));
        if (searchHit == null) {
            details.setLogDTOs(Collections.EMPTY_LIST);
            response.setData(details);
            return response;
        }

        details.setTotal(searchHit.getSearchHits().size());
        List<LogDTO> dtoList = Lists.newArrayListWithCapacity((int) details.getTotal());
        Iterator<SearchHit<LogEntity>> iterator = searchHit.iterator();
        while (iterator.hasNext()) {
            SearchHit<LogEntity> hit = iterator.next();
            LogDTO dto = new LogDTO();
            BeanUtils.copyProperties(hit.getContent(), dto);
            dtoList.add(dto);
        }
        details.setLogDTOs(dtoList);
        response.setData(details);
        return response;
    }


    @PostMapping("/count")
    public @ResponseBody
    Long count(@RequestBody LogRequestParams params) {
        return logService.count(params);
    }
}
