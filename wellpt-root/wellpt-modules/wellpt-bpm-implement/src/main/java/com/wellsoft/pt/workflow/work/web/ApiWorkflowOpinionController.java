package com.wellsoft.pt.workflow.work.web;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.bean.FlowOpinionCategoryBean;
import com.wellsoft.pt.workflow.dto.IsOpinionRuleCheckDto;
import com.wellsoft.pt.workflow.entity.FlowOpinion;
import com.wellsoft.pt.workflow.entity.FlowOpinionCategory;
import com.wellsoft.pt.workflow.facade.service.WfOpinionCheckSetFacadeService;
import com.wellsoft.pt.workflow.service.FlowOpinionCategoryService;
import com.wellsoft.pt.workflow.service.FlowOpinionService;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.Collection;
import java.util.List;

/**
 * Description: 流程意见接口控制层
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年12月03日   chenq	 Create
 * </pre>
 */
@Api(tags = "流程意见")
@RestController
@RequestMapping("/api/workflow/opinion")
public class ApiWorkflowOpinionController extends BaseController {

    @Autowired
    private FlowOpinionService flowOpinionService;

    @Autowired
    private FlowOpinionCategoryService flowOpinionCategoryService;

    @Autowired
    private WfOpinionCheckSetFacadeService wfOpinionCheckSetFacadeService;

    /**
     * 获取意见
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "获取意见", notes = "获取意见")
    @GetMapping("/get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "意见UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<FlowOpinion> get(@RequestParam(name = "uuid", required = true) String uuid) {
        FlowOpinion opinion = flowOpinionService.getOne(uuid);
        return ApiResult.success(opinion);
    }

    /**
     * 根据意见分类UUID获取意见
     *
     * @param categoryUuid
     * @return
     */
    @ApiOperation(value = "根据意见分类UUID获取意见", notes = "根据意见分类UUID获取意见")
    @GetMapping("/getByOpinionCategory")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryUuid", value = "意见分类UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<FlowOpinion>> getByOpinionCategory(
            @RequestParam(name = "categoryUuid", required = true) String categoryUuid) {
        return ApiResult.success(flowOpinionService.getByOpinionCategory(categoryUuid));
    }

    /**
     * 保存意见
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "保存意见", notes = "保存意见")
    @PostMapping("/save")
    public ApiResult<Void> save(@RequestBody FlowOpinion entity) {
        flowOpinionService.save(entity);
        return ApiResult.success();
    }

    /**
     * 校验签署意见是否符合规则
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "校验签署意见是否符合规则", notes = "校验签署意见是否符合规则")
    @PostMapping("/isOpinionRuleCheck")
    public ApiResult<IsOpinionRuleCheckDto> isOpinionRuleCheck(@RequestBody IsOpinionRuleCheckDtoRequest request) {
        return ApiResult.success(wfOpinionCheckSetFacadeService.isOpinionRuleCheck(request.getOpinionText(),
                request.getFlowId(), request.getTaskId(), request.getScene()));
    }

    /**
     * 删除意见
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "删除意见", notes = "删除意见")
    @PostMapping("/deleteAll")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuids", value = "意见UUID列表", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        flowOpinionService.deleteByUuids(uuids);
        return ApiResult.success();
    }

    /**
     * 保存意见立场
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "保存意见立场", notes = "保存意见立场")
    @PostMapping("/saveFlowOpinionCategories")
    public ApiResult<Void> saveFlowOpinionCategories(@RequestBody FlowOpinionCategoryRequest request) {
        flowOpinionService.saveFlowOpinionCategoryBeans(request.getOpinionCategories(),
                request.getDeletedCategoryUuids());
        return ApiResult.success();
    }

    @ApiOperation(value = "删除用户的最近意见", notes = "根据用户ID（不传的情况下是当前用户ID）删除指定的最近意见")
    @DeleteMapping("/deleteRecentOpinion")
    public ApiResult<Void> deleteRecentOpinion(
            @ApiParam(value = "意见内容", required = true) @RequestParam("content") String content,
            @ApiParam(value = "用户ID", required = false) @RequestParam(value = "userId", required = false) String userId) {
        flowOpinionService.deleteUserRecentOption(HtmlUtils.htmlUnescape(content),
                StringUtils.isBlank(userId) ? SpringSecurityUtils.getCurrentUserId() : userId);
        return ApiResult.success();
    }

    /**
     * 获取意见分类
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/category/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取意见分类", notes = "获取意见分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "意见分类UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<FlowOpinionCategory> getCategory(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(flowOpinionCategoryService.getOne(uuid));
    }

    /**
     * 保存意见分类
     *
     * @param entity
     * @return
     */
    @ApiOperation(value = "保存意见分类", notes = "保存意见分类")
    @PostMapping("/category/save")
    public ApiResult<Void> saveCategory(@RequestBody FlowOpinionCategory entity) {
        flowOpinionCategoryService.save(entity);
        return ApiResult.success();
    }

    /**
     * 根据数据字典获取意见分类树
     *
     * @param fetchOpinionCategory
     * @return
     */
    @ApiOperation(value = "根据数据字典获取意见分类树", notes = "根据数据字典获取意见分类树")
    @GetMapping("/getFlowOpinionCategoryTreeByBusinessAppDataDic")
    public ApiResult<TreeNode> getFlowOpinionCategoryTreeByBusinessAppDataDic(
            @RequestParam(name = "fetchOpinionCategory", defaultValue = "false", required = false) Boolean fetchOpinionCategory) {
        TreeNode treeNode = flowOpinionCategoryService
                .getFlowOpinionCategoryTreeByBusinessAppDataDic(fetchOpinionCategory);
        return ApiResult.success(treeNode);
    }

    /**
     * 根据意见分类UUID列表删除意见分类
     *
     * @param categoryUuid
     * @return
     */
    @ApiOperation(value = "根据意见分类UUID列表删除意见分类", notes = "根据意见分类UUID列表删除意见分类")
    @PostMapping("/category/delete")
    public ApiResult<Void> deleteCategory(
            @RequestParam(name = "categoryUuids", required = true) List<String> categoryUuids) {
        flowOpinionCategoryService.deleteByUuids(categoryUuids);
        return ApiResult.success();
    }

    /**
     * 根据意见分类UUID删除意见分类及意见
     *
     * @param categoryUuid
     * @return
     */
    @ApiOperation(value = "根据意见分类UUID删除意见分类及意见", notes = "根据意见分类UUID删除意见分类及意见")
    @PostMapping("/categoryAndOpinion/delete")
    public ApiResult<Void> deleteCategoryAndOpinion(
            @RequestParam(name = "categoryUuid", required = true) String categoryUuid) {
        flowOpinionCategoryService.deleteCategryAndOpinion(categoryUuid);
        return ApiResult.success();
    }

    @ApiModel("校验签署意见是否符合规则请求数据")
    private static final class IsOpinionRuleCheckDtoRequest extends BaseObject {
        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 2077880296770327358L;

        @ApiModelProperty("办理意见")
        private String opinionText;
        @ApiModelProperty("流程定义ID")
        private String flowId;
        @ApiModelProperty("环节ID")
        private String taskId;
        @ApiModelProperty("场景")
        private String scene;

        /**
         * @return the opinionText
         */
        public String getOpinionText() {
            return opinionText;
        }

        /**
         * @param opinionText 要设置的opinionText
         */
        public void setOpinionText(String opinionText) {
            this.opinionText = opinionText;
        }

        /**
         * @return the flowId
         */
        public String getFlowId() {
            return flowId;
        }

        /**
         * @param flowId 要设置的flowId
         */
        public void setFlowId(String flowId) {
            this.flowId = flowId;
        }

        /**
         * @return the taskId
         */
        public String getTaskId() {
            return taskId;
        }

        /**
         * @param taskId 要设置的taskId
         */
        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        /**
         * @return the scene
         */
        public String getScene() {
            return scene;
        }

        /**
         * @param scene 要设置的scene
         */
        public void setScene(String scene) {
            this.scene = scene;
        }

    }

    @ApiModel("流程意见分类请求数据")
    private static final class FlowOpinionCategoryRequest extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 3611854268932702818L;

        @ApiModelProperty("意见分类数据列表")
        private FlowOpinionCategoryBean[] opinionCategories;

        @ApiModelProperty("删除的意见分类UUID列表")
        private Collection<String> deletedCategoryUuids;

        /**
         * @return the opinionCategories
         */
        public FlowOpinionCategoryBean[] getOpinionCategories() {
            return opinionCategories;
        }

        /**
         * @param opinionCategories 要设置的opinionCategories
         */
        public void setOpinionCategories(FlowOpinionCategoryBean[] opinionCategories) {
            this.opinionCategories = opinionCategories;
        }

        /**
         * @return the deletedCategoryUuids
         */
        public Collection<String> getDeletedCategoryUuids() {
            return deletedCategoryUuids;
        }

        /**
         * @param deletedCategoryUuids 要设置的deletedCategoryUuids
         */
        public void setDeletedCategoryUuids(Collection<String> deletedCategoryUuids) {
            this.deletedCategoryUuids = deletedCategoryUuids;
        }

    }
}
