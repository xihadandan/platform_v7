/*
 * @(#)2020年12月8日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.web;

import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.bpm.engine.FlowEngine;
import com.wellsoft.pt.bpm.engine.query.api.FlowCategoryQuery;
import com.wellsoft.pt.bpm.engine.query.api.FlowCategoryQueryItem;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.workflow.bean.FlowCategoryBean;
import com.wellsoft.pt.workflow.entity.FlowCategory;
import com.wellsoft.pt.workflow.service.FlowCategoryService;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Description: 流程分类API接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年12月8日.1	zhulh		2020年12月8日		Create
 * </pre>
 * @date 2020年12月8日
 */
@Api(tags = "流程分类")
@Controller
@RequestMapping("/api/workflow/category")
public class ApiFlowCategoryController extends BaseController {

    @Autowired
    private FlowCategoryService flowCategoryService;

    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    /**
     * 流程分类查询
     *
     * @param keyword
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getAllBySystemUnitIdsLikeName", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "流程分类按系统单位及名称查询", notes = "流程分类按系统单位及名称查询")
    public ApiResult<List<FlowCategory>> getAllBySystemUnitIdsLikeName(@RequestBody FlowCategoryGetRequest request) {
        return ApiResult.success(flowCategoryService.getAllBySystemUnitIdsLikeName(request.getName()));
    }

    /**
     * 流程分类查询
     *
     * @param keyword
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/query", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "流程分类查询", notes = "流程分类查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "查询关键字", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "uuids", value = "流程分类UUID，多个以逗号隔开", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "pageNo", value = "页码", paramType = "query", dataType = "int", required = false),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", paramType = "query", dataType = "int", required = false)})
    public ApiResult<List<FlowCategoryQueryItem>> query(@RequestParam(name = "keyword") String keyword,
                                                        @RequestParam(name = "uuids", required = false) List<String> uuids,
                                                        @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
                                                        @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        FlowCategoryQuery flowCategoryQuery = FlowEngine.getInstance().createQuery(FlowCategoryQuery.class);
        if (StringUtils.isNotBlank(keyword)) {
            flowCategoryQuery.nameLike(keyword);
        }
        if (CollectionUtils.isNotEmpty(uuids)) {
            flowCategoryQuery.uuids(uuids);
        }
        if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            flowCategoryQuery.systemId(RequestSystemContextPathResolver.system());
        }
//        flowCategoryQuery.systemUnitIds(
//                Lists.newArrayList(MultiOrgSystemUnit.PT_ID, SpringSecurityUtils.getCurrentUserUnitId()));
        PagingInfo pagingInfo = new PagingInfo(pageNo, pageSize);
        flowCategoryQuery.setFirstResult(pagingInfo.getFirst());
        flowCategoryQuery.setMaxResults(pageSize);
        flowCategoryQuery.orderByCodeAsc();
        List<FlowCategoryQueryItem> flowCategoryQueryItems = flowCategoryQuery.list();
        if (CollectionUtils.isNotEmpty(flowCategoryQueryItems) && !LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            Map<String, FlowCategoryQueryItem> itemMap = Maps.newHashMap();
            for (FlowCategoryQueryItem item : flowCategoryQueryItems) {
                itemMap.put(item.getUuid(), item);
            }
            List<AppDefElementI18nEntity> i18nEntities = appDefElementI18nService.getI18ns(itemMap.keySet(), IexportType.FlowCategory, "name", LocaleContextHolder.getLocale().toString());
            if (CollectionUtils.isNotEmpty(i18nEntities)) {
                for (AppDefElementI18nEntity i : i18nEntities) {
                    if (StringUtils.isNotBlank(i.getContent()) && itemMap.containsKey(i.getDefId())) {
                        itemMap.get(i.getDefId()).setName(i.getContent());
                    }
                }
            }
        }
        return ApiResult.success(flowCategoryQueryItems);
    }

    /**
     * 获取流程分类
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程分类", notes = "获取流程分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "流程分类UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<FlowCategoryBean> get(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(flowCategoryService.getBean(uuid));
    }

    @ResponseBody
    @GetMapping("/getModuleFlowCategory")
    public ApiResult<List<FlowCategory>> getModuleFlowCategory(@RequestParam(name = "moduleId") String moduleId) {
        FlowCategory example = new FlowCategory();
        example.setModuleId(moduleId);
        return ApiResult.success(flowCategoryService.listByEntity(example));
    }

    /**
     * 保存流程分类
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存流程分类", notes = "保存流程分类")
    public ApiResult<String> saveFlowCategory(@RequestBody FlowCategoryBean bean) {
        return ApiResult.success(flowCategoryService.saveBean(bean));
    }

    /**
     * 生成流程分类编号
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/generateFlowCategoryCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "生成流程分类编号", notes = "生成流程分类编号")
    public ApiResult<String> generateFlowCategoryCode() {
        return ApiResult.success(flowCategoryService.generateFlowCategoryCode());
    }

    /**
     * 删除流程分类
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除流程分类", notes = "删除流程分类")
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        flowCategoryService.removeAllByPk(uuids);
        return ApiResult.success();
    }

    /**
     * 删除没用的流程分类
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/deleteWhenNotUsed", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除没用的流程分类", notes = "删除没用的流程分类")
    public ApiResult<Integer> deleteWhenNotUsed(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(flowCategoryService.deleteWhenNotUsed(uuid));
    }

    @ApiModel("流程分类按系统单位及名称查询请求数据")
    private static final class FlowCategoryGetRequest extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -3118726890041225608L;

        @ApiModelProperty("名称")
        private String name;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name 要设置的name
         */
        public void setName(String name) {
            this.name = name;
        }

    }

}
