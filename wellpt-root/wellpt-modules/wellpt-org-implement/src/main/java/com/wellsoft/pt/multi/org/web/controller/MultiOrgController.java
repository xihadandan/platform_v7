package com.wellsoft.pt.multi.org.web.controller;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.dto.DeleteUsersDto;
import com.wellsoft.pt.multi.org.dto.MultiOrgJobRankDto;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.util.PwdUtils;
import com.wellsoft.pt.multi.org.vo.MultiOrgJobRankVo;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Description: 多组织管理接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/3.1	    zenghw		2021/3/3		    Create
 * </pre>
 * @date 2021/3/3
 */
@Api(tags = "多组织管理接口")
@RestController
@RequestMapping("/api/org/multi")
public class MultiOrgController extends BaseController {

    @Autowired
    private MultiOrgService multiOrgService;

    @ApiOperation(value = "添加一个组织子节点", notes = "添加一个组织子节点，根据里面的type做不同的业务处理", tags = {"组织子节点分组", "组织管理_组织版本配置编辑页"})
    @PostMapping("/addOrgChildNode")
    @ApiOperationSupport(order = 10)
    public ApiResult<MultiOrgTreeNode> addOrgChildNode(
            @ApiParam(value = "组织节点vo", required = true) @RequestBody @Validated OrgTreeNodeVo nodeVo) {
        MultiOrgTreeNode multiOrgTreeNode = multiOrgService.addOrgChildNode(nodeVo);
        return ApiResult.success(multiOrgTreeNode);
    }

    @ApiOperation(value = "修改一个组织子节点", notes = "修改一个组织子节点", tags = {"组织子节点分组", "组织管理_组织版本配置编辑页"})
    @PutMapping("/modifyOrgChildNode")
    @ApiOperationSupport(order = 11)
    public ApiResult modifyOrgChildNode(
            @ApiParam(value = "组织节点vo", required = true) @RequestBody @Validated OrgTreeNodeVo nodeVo,
            @ApiParam(value = "是否解绑，是：插入一个新节点，继承的版本的节点信息不变，否：所有版本的节点信息都跟着变化", required = true, type = "boolean") @RequestParam(name = "isUnbind") @NotNull(message = "是否解绑不能为空") boolean isUnbind) {
        boolean flg = multiOrgService.modifyOrgChildNode(nodeVo, isUnbind);
        if (flg) {
            return ApiResult.success();
        }
        return ApiResult.fail();
    }

    @ApiOperation(value = "通过树节点UUID获取一个组织节点的信息", notes = "通过树节点UUID获取一个组织节点的信息", tags = {"组织子节点分组",
            "组织管理_组织版本配置编辑页"})
    @GetMapping("/getOrgNodeByTreeUuid")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgTreeUuid", value = "树节点UUID", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 12)
    public ApiResult<OrgTreeNodeVo> getOrgNodeByTreeUuid(
            @RequestParam(name = "orgTreeUuid", required = false) String orgTreeUuid) {
        return ApiResult.success(multiOrgService.getOrgNodeByTreeUuid(orgTreeUuid));
    }

    @ApiOperation(value = "删除组织节点", notes = "删除组织节点", tags = {"组织子节点分组", "组织管理_组织版本配置编辑页"})
    @PostMapping("/deleteOrgChildNode")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "treeNodeUuid", value = "组织节点uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 13)
    public ApiResult<Boolean> deleteOrgChildNode(
            @RequestParam(name = "treeNodeUuid", required = false) String treeNodeUuid) {
        boolean flg = multiOrgService.deleteOrgChildNode(treeNodeUuid);
        if (flg) {
            return ApiResult.success();
        }
        return ApiResult.fail();
    }

    @ApiOperation(value = "获取指定版本的组织的完整组织树", notes = "获取指定版本的组织的完整组织树", tags = {"组织树分组"})
    @GetMapping("/getOrgAsTreeByVersionId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgVersionId", value = "版本ID", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 20)
    public ApiResult<OrgTreeNode> getOrgAsTreeByVersionId(
            @RequestParam(name = "orgVersionId", required = false) String orgVersionId) {
        return ApiResult.success(multiOrgService.getOrgAsTreeByVersionId(orgVersionId));
    }

    @ApiOperation(value = "获取指定版本，指定位置的组织树, 包含子单位", notes = "获取指定版本，指定位置的组织树, 包含子单位", tags = {"组织树分组"})
    @GetMapping("/getOrgAsTreeByEleIdPath")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgVersionId", value = "组织版本ID", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "eleIdPath", value = "组织ID路径", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "isInMyUnit", value = "是否只看本单位内的数据", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 21)
    public ApiResult<OrgTreeNode> getOrgAsTreeByEleIdPath(
            @RequestParam(name = "orgVersionId", required = false) String orgVersionId,
            @RequestParam(name = "eleIdPath", required = false) String eleIdPath,
            @RequestParam(name = "isInMyUnit", required = false) boolean isInMyUnit) {
        return ApiResult.success(multiOrgService.getOrgAsTreeByEleIdPath(orgVersionId, eleIdPath, isInMyUnit));
    }

    @ApiOperation(value = "获取指定版本，指定位置、类型的组织树, 包含子单位", notes = "获取指定版本，指定位置、类型的组织树, 包含子单位", tags = {"组织树分组"})
    @GetMapping("/getOrgAsTreeByEleIdPathAndEleTypes")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgVersionId", value = "组织版本", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "eleIdPath", value = "结点ID路径", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "isInMyUnit", value = "是否只看本单位内的数据", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "eleTypes", value = "组织元素类型", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 22)
    public ApiResult<OrgTreeNode> getOrgAsTreeByEleIdPathAndEleTypes(
            @RequestParam(name = "orgVersionId", required = false) String orgVersionId,
            @RequestParam(name = "eleIdPath", required = false) String eleIdPath,
            @RequestParam(name = "isInMyUnit", required = false) boolean isInMyUnit,
            @RequestParam(name = "eleTypes", required = false) String... eleTypes) {
        return ApiResult.success(
                multiOrgService.getOrgAsTreeByEleIdPathAndEleTypes(orgVersionId, eleIdPath, isInMyUnit, eleTypes));
    }

    @ApiOperation(value = "获取一个组织树节点信息", notes = "获取一个组织树节点信息", tags = {"组织树分组"})
    @GetMapping("/getOrgTreeNodeDto")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "treeNodeUuid", value = "组织节点uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 23)
    public ApiResult<OrgTreeNodeDto> getOrgTreeNodeDto(
            @RequestParam(name = "treeNodeUuid", required = false) String treeNodeUuid) {
        return ApiResult.success(multiOrgService.getOrgTreeNodeDto(treeNodeUuid));
    }

    @ApiOperation(value = "获取一个组织节点对应的角色的权限列表，以树形态返回", notes = "获取一个组织节点对应的角色的权限列表，以树形态返回", tags = {"组织节点权限列表分组",
            "组织管理_组织版本配置编辑页"})
    @GetMapping("/getOrgNodePrivilegeResultTree")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "eleId", value = "组织节点ID", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 30)
    public ApiResult<TreeNode> getOrgNodePrivilegeResultTree(
            @RequestParam(name = "eleId", required = false) String eleId) {
        return ApiResult.success(multiOrgService.getOrgNodePrivilegeResultTree(eleId));
    }

    @ApiOperation(value = "获取用户所有的权限，包括组织节点，表单控件等其他地方继承过来的角色权限", notes = "获取用户所有的权限，包括组织节点，表单控件等其他地方继承过来的角色权限", tags = {
            "组织节点权限列表分组", "组织管理_组织版本配置编辑页"})
    @GetMapping("/getUserAllPrivilegeResultTree")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "用户uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 31)
    public ApiResult<TreeNode> getUserAllPrivilegeResultTree(
            @RequestParam(name = "uuid", required = false) String uuid) {
        return ApiResult.success(multiOrgService.getUserAllPrivilegeResultTree(uuid));
    }

    @ApiOperation(value = "添加系统单位", notes = "添加系统单位", tags = {"系统单位分组", "超管-->系统单位管理"})
    @PostMapping("/addSystemUnit")
    @ApiOperationSupport(order = 40)
    public ApiResult<OrgSystemUnitVo> addSystemUnit(@RequestBody OrgSystemUnitVo unit) {
        return ApiResult.success(multiOrgService.addSystemUnit(unit));
    }

    @ApiOperation(value = "修改系统单位信息", notes = "修改系统单位信息", tags = {"系统单位分组", "超管-->系统单位管理"})
    @PostMapping("/modifySystemUnit")
    @ApiOperationSupport(order = 41)
    public ApiResult<OrgSystemUnitVo> modifySystemUnit(@RequestBody OrgSystemUnitVo unit) {
        return ApiResult.success(multiOrgService.modifySystemUnit(unit));
    }

    @ApiOperation(value = "获取系统单位信息", notes = "获取系统单位信息", tags = {"系统单位分组", "超管-->系统单位管理"})
    @GetMapping("/getSystemUnitVo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "系统单位uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 42)
    public ApiResult<OrgSystemUnitVo> getSystemUnitVo(@RequestParam(name = "uuid", required = false) String uuid) {
        return ApiResult.success(multiOrgService.getSystemUnitVo(uuid));
    }

    @ApiOperation(value = "获取所有的系统单位", notes = "获取所有的系统单位", tags = {"系统单位分组", "超管-->系统单位管理"})
    @GetMapping("/queryAllSystemUnitList")
    @ApiOperationSupport(order = 43)
    public ApiResult<List<MultiOrgSystemUnit>> queryAllSystemUnitList() {
        List<MultiOrgSystemUnit> list = multiOrgService.queryAllSystemUnitList();
        return ApiResult.success(list);
    }

    @ApiOperation(value = "添加职务", notes = "添加职务", tags = {"职务分组", "组织管理-->职务"})
    @PostMapping("/addDuty")
    @ApiOperationSupport(order = 50)
    public ApiResult<MultiOrgDuty> addDuty(@RequestBody MultiOrgDuty vo) {
        return ApiResult.success(multiOrgService.addDuty(vo));
    }

    @ApiOperation(value = "删除职务", notes = "删除职务", tags = {"职务分组", "组织管理-->职务"})
    @DeleteMapping("/deleteDuty/{uuid}")
    @ApiOperationSupport(order = 50)
    public ApiResult deleteDuty(@PathVariable String uuid) {
        String msg = multiOrgService.deleteDuty(uuid);
        if (StringUtils.isNotBlank(msg)) {
            return ApiResult.fail(msg);
        }
        return ApiResult.success();
    }

    @ApiOperation(value = "批量删除职务", notes = "批量删除职务", tags = {"职务分组", "组织管理-->职务"})
    @PostMapping("/deleteDuty")
    @ApiOperationSupport(order = 50)
    public ApiResult deleteDutys(String[] uuids) {
        String msg = multiOrgService.deleteDuty(uuids);
        if (StringUtils.isNotBlank(msg)) {
            return ApiResult.fail(msg);
        }
        return ApiResult.success();
    }

    @ApiOperation(value = "修改职务", notes = "修改职务", tags = {"职务分组", "组织管理-->职务"})
    @PostMapping("/modifyDuty")
    @ApiOperationSupport(order = 51)
    public ApiResult<MultiOrgDuty> modifyDuty(@RequestBody MultiOrgDuty vo) {
        return ApiResult.success(multiOrgService.modifyDuty(vo));
    }

    @ApiOperation(value = "获取职务信息", notes = "获取职务信息", tags = {"职务分组", "组织管理-->职务"})
    @GetMapping("/getDuty")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "职务uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 52)
    public ApiResult<MultiOrgDuty> getDuty(@RequestParam(name = "uuid", required = false) String uuid) {
        return ApiResult.success(multiOrgService.getDuty(uuid));
    }

    @ApiOperation(value = "添加职级", notes = "添加职级", tags = {"职级分组", "组织管理-->职级"})
    @PostMapping("/addJobRank")
    @ApiOperationSupport(order = 60)
    public ApiResult<MultiOrgJobRank> addJobRank(@RequestBody MultiOrgJobRankVo vo) {
        return ApiResult.success(multiOrgService.addJobRank(vo));
    }

    @ApiOperation(value = "修改职级", notes = "修改职级", tags = {"职级分组", "组织管理-->职级"})
    @PostMapping("/modifyJobRank")
    @ApiOperationSupport(order = 61)
    public ApiResult<MultiOrgJobRank> modifyJobRank(@RequestBody MultiOrgJobRankVo vo) {
        return ApiResult.success(multiOrgService.modifyJobRank(vo));
    }

    @ApiOperation(value = "根据职务序列UUID获取职级列表", notes = "根据职务序列UUID获取职级列表", tags = {"职级分组", "组织管理-->根据职务序列UUID获取职级列表"})
    @ApiOperationSupport(order = 62)
    @GetMapping("/listJobRankByDutySeqUuid/{uuid}")
    public ApiResult<List<MultiOrgJobRankDto>> listJobRankByDutySeqUuid(@PathVariable String uuid) {
        return ApiResult.success(multiOrgService.listJobRankByDutySeqUuid(uuid));
    }

    @ApiOperation(value = "删除职级", notes = "删除职级", tags = {"职务分组", "组织管理-->职级"})
    @DeleteMapping("/deleteJobRank/{uuid}")
    @ApiOperationSupport(order = 50)
    public ApiResult deleteJobRank(@PathVariable String uuid) {
        String msg = multiOrgService.deleteJobRank(uuid);
        if (StringUtils.isNotBlank(msg)) {
            return ApiResult.fail(msg);
        }
        return ApiResult.success();
    }

    @ApiOperation(value = "获取职级", notes = "获取职级", tags = {"职级分组", "组织管理-->职级"})
    @GetMapping("/getJobRank")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "职级UUID", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 62)
    public ApiResult<MultiOrgJobRankDto> getJobRank(@RequestParam(name = "uuid", required = false) String uuid) {
        return ApiResult.success(multiOrgService.getJobRank(uuid));
    }

    @ApiOperation(value = "根据职位ID查询职级", notes = "根据职位ID查询职级")
    @ApiOperationSupport(order = 140)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgVersion", value = "组织版本号", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "jobId", value = "职位ID", paramType = "query", dataType = "String", required = true)
    })
    @GetMapping("/getJobRankByJobId/{orgVersion}/{jobId}")
    public ApiResult<List<MultiOrgJobRankDto>> getJobRankByJobId(@PathVariable String orgVersion, @PathVariable String jobId) {
        return ApiResult.success(multiOrgService.getJobRankByJobId(orgVersion, jobId));
    }

    @ApiOperation(value = "添加组织选择项", notes = "添加组织选择项", tags = {"组织选择项分组", "系统单位管理-->组织选择项"})
    @PostMapping("/addOrgOption")
    @ApiOperationSupport(order = 70)
    public ApiResult<MultiOrgOption> addOrgOption(@RequestBody MultiOrgOption vo) {
        return ApiResult.success(multiOrgService.addOrgOption(vo));
    }

    @ApiOperation(value = "修改组织选择项", notes = "修改组织选择项", tags = {"组织选择项分组", "系统单位管理-->组织选择项"})
    @PostMapping("/modifyOrgOption")
    @ApiOperationSupport(order = 71)
    public ApiResult<MultiOrgOption> modifyOrgOption(@RequestBody MultiOrgOption vo) {
        return ApiResult.success(multiOrgService.modifyOrgOption(vo));
    }

    @ApiOperation(value = "获取一个组织选择项", notes = "获取一个组织选择项", tags = {"组织选择项分组", "系统单位管理-->组织选择项"})
    @GetMapping("/getOrgOption")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "组织选择项uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 72)
    public ApiResult<MultiOrgOption> getOrgOption(@RequestParam(name = "uuid", required = false) String uuid) {
        return ApiResult.success(multiOrgService.getOrgOption(uuid));
    }

    @ApiOperation(value = "获取某一系统单位的所有的组织选项，并且带平台的的组织选项;systemUnitId为空时,取当前单位", notes = "获取某一系统单位的所有的组织选项，并且带平台的的组织选项;systemUnitId为空时,取当前单位", tags = {
            "组织选择项分组", "系统单位管理-->组织选择项"})
    @GetMapping("/getOrgOptionListByUnitId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "systemUnitId", value = "系统单位ID", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 80)
    public ApiResult<List<MultiOrgOption>> getOrgOptionListByUnitId(
            @RequestParam(name = "systemUnitId", required = false) String systemUnitId) {
        List<MultiOrgOption> list = multiOrgService.getOrgOptionListByUnitId(systemUnitId);
        return ApiResult.success(list);
    }

    @ApiOperation(value = "获取某一系统单位的所有的组织选项，（组织选择项显示 可选）", notes = "获取某一系统单位的所有的组织选项，并且带平台的的组织选项;systemUnitId为空时,取当前单位", tags = {
            "组织选择项分组", "系统单位管理-->组织选择项"})
    @GetMapping("/getOrgOptionListByUnitIdAndOnlyShow")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "systemUnitId", value = "系统单位ID", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "onlyShow", value = "是否显示", paramType = "query", dataType = "Boolean", required = false)})
    @ApiOperationSupport(order = 81)
    public ApiResult<List<MultiOrgOption>> getOrgOptionListByUnitId(
            @RequestParam(name = "systemUnitId", required = false) String systemUnitId,
            @RequestParam(name = "onlyShow", required = false) boolean onlyShow) {
        List<MultiOrgOption> list = multiOrgService.getOrgOptionListByUnitId(systemUnitId, onlyShow);
        return ApiResult.success(list);
    }

    @ApiOperation(value = "获取某一系统单位的所有的组织选项，并且带平台的的组织选项", notes = "获取某一系统单位的所有的组织选项，并且带平台的的组织选项；（组织选择项默认显示）", tags = {
            "组织选择项分组", "系统单位管理-->组织选择项"})
    @GetMapping("/queryOrgOptionListBySystemUnitIdAndOptionOfPT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "systemUnitId", value = "系统单位ID", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 82)
    public ApiResult<List<MultiOrgOption>> queryOrgOptionListBySystemUnitIdAndOptionOfPT(
            @RequestParam(name = "systemUnitId", required = false) String systemUnitId) {
        List<MultiOrgOption> list = multiOrgService.queryOrgOptionListBySystemUnitIdAndOptionOfPT(systemUnitId);
        return ApiResult.success(list);
    }

    @ApiOperation(value = "前端wSelect2获取组织选择项列表", notes = "前端wSelect2获取组织选择项列表", tags = {"组织选择项分组", "系统单位管理-->组织选择项"})
    @GetMapping("/queryDutyListForSelect2")
    @ApiOperationSupport(order = 83)
    public ApiResult<Select2QueryData> queryDutyListForSelect2(@RequestBody Select2QueryInfo select2QueryInfo) {
        return ApiResult.success(multiOrgService.queryDutyListForSelect2(select2QueryInfo));
    }

    @ApiOperation(value = "添加组织类型", notes = "添加组织类型", tags = {"组织类型分组", "组织管理-->组织类型"})
    @PostMapping("/addOrgType")
    @ApiOperationSupport(order = 90)
    public ApiResult<MultiOrgType> addOrgType(@RequestBody MultiOrgType vo) {
        return ApiResult.success(multiOrgService.addOrgType(vo));
    }

    @ApiOperation(value = "修改组织类型", notes = "修改组织类型", tags = {"组织类型分组", "组织管理-->组织类型"})
    @PostMapping("/modifyOrgType")
    @ApiOperationSupport(order = 91)
    public ApiResult<MultiOrgType> modifyOrgType(@RequestBody MultiOrgType vo) {
        return ApiResult.success(multiOrgService.modifyOrgType(vo));
    }

    @ApiOperation(value = "获取一个组织类型", notes = "获取一个组织类型", tags = {"组织类型分组", "组织管理-->组织类型"})
    @GetMapping("/getOrgType")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "组织选择项uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 92)
    public ApiResult<MultiOrgType> getOrgType(@RequestParam(name = "uuid", required = false) String uuid) {
        return ApiResult.success(multiOrgService.getOrgType(uuid));
    }

    @ApiOperation(value = "添加单位管理员", notes = "添加单位管理员", tags = {"单位管理员分组", "系统单位管理-->单位管理员"})
    @PostMapping("/addUnitAdmin")
    @ApiOperationSupport(order = 110)
    public ApiResult<OrgUserVo> addUnitAdmin(@RequestBody OrgUserVo vo) {
        try {
            if (StringUtils.isNotBlank(vo.getPassword())) {
                vo.setPassword(PwdUtils.decodePwdBybase64AndUnicode(vo.getPassword()));
            } else {
                // 默认密码为0
                vo.setPassword("0");
            }
            return ApiResult.success(multiOrgService.addUnitAdmin(vo));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error("添加单位管理员异常：", e);
            return ApiResult.fail(e.getMessage());
        }

    }

    @ApiOperation(value = "修改单位管理员", notes = "修改单位管理员", tags = {"单位管理员分组", "系统单位管理-->单位管理员"})
    @PostMapping("/modifyUnitAdmin")
    @ApiOperationSupport(order = 111)
    public ApiResult<OrgUserVo> modifyUnitAdmin(@RequestBody OrgUserVo vo) {
        try {
            if (StringUtils.isNotBlank(vo.getPassword())) {
                vo.setPassword(PwdUtils.decodePwdBybase64AndUnicode(vo.getPassword()));
            }
            return ApiResult.success(multiOrgService.modifyUnitAdmin(vo));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("添加单位管理员异常：", e);
            return ApiResult.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "获取用户账号和INFO信息", notes = "获取用户账号和INFO信息", tags = {"用户账号分组", "组织管理--->用户"})
    @GetMapping("/getUser")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "用户uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 112)
    public ApiResult<OrgUserVo> getUser(@RequestParam(name = "uuid", required = false) String uuid) {
        return ApiResult.success(multiOrgService.getUser(uuid));
    }

    @ApiOperation(value = "获取用户对应的角色的权限列表，以角色树形态展示", notes = "获取用户对应的角色的权限列表，以角色树形态展示", tags = {"用户权限分组",
            "组织管理--->用户"})
    @GetMapping("/getUserPrivilegeResultTree")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "用户uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 120)
    public ApiResult<TreeNode> getUserPrivilegeResultTree(@RequestParam(name = "uuid", required = false) String uuid) {
        return ApiResult.success(multiOrgService.getUserPrivilegeResultTree(uuid));
    }

    @ApiOperation(value = "添加账号", notes = "添加账号", tags = {"用户账号分组", "组织管理--->用户"})
    @PostMapping("/addUser")
    @ApiOperationSupport(order = 130)
    public ApiResult addUser(@RequestBody OrgUserVo vo) {
        try {
            if (StringUtils.isNotBlank(vo.getPassword())) {
                vo.setPassword(PwdUtils.decodePwdBybase64AndUnicode(vo.getPassword()));
            }
            List<String> resultList = multiOrgService.checkOnly(vo);
            if (resultList != null && !resultList.isEmpty()) {
                return ApiResult.build(1, "登录用户名校验重复", resultList);
            }
            return ApiResult.success(multiOrgService.addUser(vo));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error("添加账号异常：", e);
            return ApiResult.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "修改账号", notes = "修改账号", tags = {"用户账号分组", "组织管理--->用户"})
    @PostMapping("/modifyUser")
    @ApiOperationSupport(order = 131)
    public ApiResult modifyUser(@RequestBody OrgUserVo vo) {
        List<String> resultList = multiOrgService.checkOnly(vo);
        if (resultList != null && !resultList.isEmpty()) {
            return ApiResult.build(1, "登录用户名校验重复", resultList);
        }
        vo.setPassword(null);
        return ApiResult.success(multiOrgService.modifyUser(vo));
    }

    @ApiOperation(value = "清空一家单位的所有用户，不包括单位管理员", notes = "清空一家单位的所有用户，不包括单位管理员", tags = {"用户账号分组", "组织管理--->用户"})
    @PostMapping("/clearAllUserOfUnit")
    @ApiOperationSupport(order = 140)
    public ApiResult addGroupByUuid() {
        multiOrgService.clearAllUserOfUnit();
        return ApiResult.success();
    }

    @ApiOperation(value = "批量删除用户", notes = "批量删除用户", tags = {"用户账号分组", "组织管理--->用户"})
    @PostMapping("/deleteUsers")
    @ApiOperationSupport(order = 150)
    public ApiResult deleteUsers(@RequestBody DeleteUsersDto vo) {
        multiOrgService.deleteUsers(vo.getUserIds());
        return ApiResult.success();
    }
}
