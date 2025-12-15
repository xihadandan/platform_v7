/*
 * @(#)2021年10月15日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.printtemplate.web;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.printtemplate.bean.PrintTemplateCategoryBean;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplateCategory;
import com.wellsoft.pt.basicdata.printtemplate.query.api.PrintTemplateCategoryQuery;
import com.wellsoft.pt.basicdata.printtemplate.query.api.PrintTemplateCategoryQueryItem;
import com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateCategoryService;
import com.wellsoft.pt.bpm.engine.FlowEngine;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "打印模版分类")
@Controller
@RequestMapping("/api/printTemplate/category")
public class ApiPrintTemplateCategoryController extends BaseController {

    @Autowired
    private PrintTemplateCategoryService printTemplateCategoryService;

    /**
     * 打印模版分类查询
     *
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getAllBySystemUnitIdsLikeName", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "打印模版分类按系统单位及名称查询", notes = "打印模版分类按系统单位及名称查询")
    public ApiResult<List<PrintTemplateCategory>> getAllBySystemUnitIdsLikeName(@RequestBody PrintTemplateCategoryGetRequest request) {
        return ApiResult.success(printTemplateCategoryService.getAllBySystemUnitIdsLikeName(request.getName()));
    }


    /**
     * 打印模版分类查询（树结构）
     *
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getTreeAllBySystemUnitIdsLikeName", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "打印模版分类按系统单位及名称查询（树结构）", notes = "打印模版分类按系统单位及名称查询（树结构）")
    public ApiResult<List<TreeNode>> getTreeAllBySystemUnitIdsLikeName(@RequestBody PrintTemplateCategoryGetRequest request) {
        return ApiResult.success(printTemplateCategoryService.getPrintTemplateCategoryTree(request.getName()));
    }

    /**
     * 打印模版分类查询
     *
     * @param keyword
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/query", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "打印模版分类查询", notes = "打印模版分类查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "查询关键字", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "uuids", value = "打印模版分类UUID，多个以逗号隔开", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "pageNo", value = "页码", paramType = "query", dataType = "int", required = false),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", paramType = "query", dataType = "int", required = false)})
    public ApiResult<List<PrintTemplateCategoryQueryItem>> query(@RequestParam(name = "keyword") String keyword,
                                                                 @RequestParam(name = "uuids", required = false) List<String> uuids,
                                                                 @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
                                                                 @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        PrintTemplateCategoryQuery printTemplateCategoryQuery = FlowEngine.getInstance().createQuery(PrintTemplateCategoryQuery.class);
        if (StringUtils.isNotBlank(keyword)) {
            printTemplateCategoryQuery.nameLike(keyword);
        }
        if (CollectionUtils.isNotEmpty(uuids)) {
            printTemplateCategoryQuery.uuids(uuids);
        }
        printTemplateCategoryQuery.systemUnitIds(
                Lists.newArrayList(MultiOrgSystemUnit.PT_ID, SpringSecurityUtils.getCurrentUserUnitId()));
        PagingInfo pagingInfo = new PagingInfo(pageNo, pageSize);
        printTemplateCategoryQuery.setFirstResult(pagingInfo.getFirst());
        printTemplateCategoryQuery.setMaxResults(pageSize);
        printTemplateCategoryQuery.orderByCodeAsc();
        List<PrintTemplateCategoryQueryItem> printTemplateCategoryQueryItems = printTemplateCategoryQuery.list();
        return ApiResult.success(printTemplateCategoryQueryItems);
    }

    /**
     * 获取打印模版分类
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取打印模版分类", notes = "获取打印模版分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "打印模版分类UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<PrintTemplateCategoryBean> get(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(printTemplateCategoryService.getBean(uuid));
    }

    /**
     * 保存打印模版分类
     *
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存打印模版分类", notes = "保存打印模版分类")
    public ApiResult<Void> generatePrintTemplateCategoryCode(@RequestBody PrintTemplateCategoryBean bean) {
        printTemplateCategoryService.saveBean(bean);
        return ApiResult.success();
    }

    /**
     * 生成打印模版分类编号
     *
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/generatePrintTemplateCategoryCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "生成打印模版分类编号", notes = "生成打印模版分类编号")
    public ApiResult<String> generatePrintTemplateCategoryCode() {
        return ApiResult.success(printTemplateCategoryService.generatePrintTemplateCategoryCode());
    }

    /**
     * 删除打印模版分类
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除打印模版分类", notes = "删除打印模版分类")
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        printTemplateCategoryService.removeAllByPk(uuids);
        return ApiResult.success();
    }

    /**
     * 删除没用的打印模版分类
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/deleteWhenNotUsed", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除没用的打印模版分类", notes = "删除没用的打印模版分类")
    public ApiResult<Integer> deleteWhenNotUsed(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(printTemplateCategoryService.deleteWhenNotUsed(uuid));
    }

    @ApiModel("打印模版分类按系统单位及名称查询请求数据")
    private static final class PrintTemplateCategoryGetRequest extends BaseObject {

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
