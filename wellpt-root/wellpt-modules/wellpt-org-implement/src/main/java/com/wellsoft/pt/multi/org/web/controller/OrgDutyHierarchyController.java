package com.wellsoft.pt.multi.org.web.controller;

import com.wellsoft.context.exception.WellException;
import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.multi.org.constant.UnitParamConstant;
import com.wellsoft.pt.multi.org.dto.OrgDutyHierarchyViewDto;
import com.wellsoft.pt.multi.org.dto.OrgDutySeqTreeDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgJobRank;
import com.wellsoft.pt.multi.org.entity.OrgDutySeqEntity;
import com.wellsoft.pt.multi.org.entity.OrgJobGradeEntity;
import com.wellsoft.pt.multi.org.service.MultiOrgJobRankService;
import com.wellsoft.pt.multi.org.service.OrgDutySeqService;
import com.wellsoft.pt.multi.org.service.OrgJobGradeService;
import com.wellsoft.pt.multi.org.service.UnitParamService;
import com.wellsoft.pt.multi.org.vo.OrgDutySeqVo;
import com.wellsoft.pt.multi.org.vo.OrgJobGradesVo;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * Description:
 * 组织职务管理体系
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/20   Create
 * </pre>
 */
@Api(tags = "组织职务管理体系")
@RestController
@RequestMapping("/api/org/duty/hierarchy")
public class OrgDutyHierarchyController extends BaseController {

    @Autowired
    UnitParamService unitParamService;

    @Autowired
    OrgDutySeqService orgDutySeqService;

    @Autowired
    OrgJobGradeService orgJobGradeService;

    @Autowired
    MultiOrgJobRankService multiOrgJobRankService;

    @ApiOperation(value = "职务体系开关", notes = "参数不传为获取系统配置，传入参数会修改系统配置")
    @ApiImplicitParam(name = "isEnable", value = "是否启用(1:启用,0:禁用)", dataType = "String", required = false)
    @ApiOperationSupport(order = 10)
    @GetMapping("/switch")
    public ApiResult switchManage(@RequestParam(required = false) String isEnable) {
        // 如果isEnable为空则获取系统配置
        if (StringUtils.isBlank(isEnable)) {
            isEnable = unitParamService.getValue(UnitParamConstant.DUTY_HIERARCHY_SWITCH);
        } else {// 修改系统配置
            unitParamService.setValue(UnitParamConstant.DUTY_HIERARCHY_SWITCH, isEnable);
        }
        return ApiResult.success(isEnable == null ? 1 : isEnable);
    }

    @ApiOperation(value = "职务序列保存", notes = "职务序列保存")
    @ApiOperationSupport(order = 20)
    @PostMapping("/dutySeqSave")
    public ApiResult dutySeqSave(@Validated @RequestBody OrgDutySeqVo dutySeqVo) {
        String message = orgDutySeqService.saveDutySeq(dutySeqVo);
        if (StringUtils.isNotBlank(message)) {
            return ApiResult.fail(message);
        }
        return ApiResult.success();
    }

    @ApiOperation(value = "根据uuid查询职务序列详情", notes = "根据uuid查询职务序列详情")
    @ApiImplicitParam(name = "uuid", value = "职务序列uuid", dataType = "String", required = true)
    @ApiOperationSupport(order = 30)
    @GetMapping("/dutySeqInfo/{uuid}")
    public ApiResult<OrgDutySeqEntity> dutySeqById(@PathVariable("uuid") String uuid) {
        OrgDutySeqEntity dutySeqEntity = orgDutySeqService.getOne(uuid);
        if (dutySeqEntity != null && !"0".equals(dutySeqEntity.getParentUuid())) {
            OrgDutySeqEntity parentSeqEntity = orgDutySeqService.getOne(dutySeqEntity.getParentUuid());
            dutySeqEntity.setParentSeqName(parentSeqEntity.getDutySeqName());
        }
        return ApiResult.success(dutySeqEntity);
    }

    @ApiOperation(value = "职务序列更新", notes = "职务序列更新")
    @ApiOperationSupport(order = 40)
    @PostMapping("/dutySeqUpdate")
    public ApiResult dutySeqUpdate(@Validated @RequestBody OrgDutySeqVo dutySeqVo) {
        String message = orgDutySeqService.updateDutySeq(dutySeqVo);
        if (StringUtils.isNotBlank(message)) {
            return ApiResult.fail(message);
        }
        return ApiResult.success();
    }

    @ApiOperation(value = "职务序列删除", notes = "职务序列删除")
    @ApiImplicitParam(name = "uuid", value = "职务序列uuid", dataType = "String", required = true)
    @ApiOperationSupport(order = 50)
    @DeleteMapping("/dutySeqDelete/{uuid}")
    public ApiResult dutySeqDelete(@PathVariable("uuid") String uuid) {
        String msg = orgDutySeqService.deleteDutySeq(uuid);
        if (StringUtils.isNotBlank(msg)) {
            return ApiResult.fail(msg);
        }
        return ApiResult.success();
    }

    @ApiOperation(value = "职务序列树形查询", notes = "职务序列树形查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "职务序列名称、编号模糊搜索", dataType = "String", required = false),
            @ApiImplicitParam(name = "notDutySeqUuid", value = "排除职务序列UUID", dataType = "String", required = false)})

    @ApiOperationSupport(order = 60)
    @GetMapping("/dutySeqTree")
    public ApiResult<List<OrgDutySeqTreeDto>> dutySeqTree(@RequestParam(required = false) String keyword,
                                                          @RequestParam(required = false) String notDutySeqUuid) {
        List<OrgDutySeqTreeDto> result = orgDutySeqService.queryDutySeqTree(keyword, notDutySeqUuid);
        return ApiResult.success(result);
    }

    @ApiOperation(value = "职等保存", notes = "职等保存")
    @ApiOperationSupport(order = 70)
    @PostMapping("/jobGradeSave")
    public ApiResult jobGradeSave(@RequestBody OrgJobGradesVo orgJobGradeVo) {
        orgJobGradeService.saveJobGrade(orgJobGradeVo);
        String message = orgDutySeqService.isExceptionJobGrade();
        return ApiResult.success(message);
    }

    @ApiOperation(value = "查询职等列表", notes = "查询职等列表")
    @ApiOperationSupport(order = 80)
    @GetMapping("/jobGradeList")
    public ApiResult<List<OrgJobGradeEntity>> jobGradeList() {
        List<OrgJobGradeEntity> result = orgJobGradeService.jobGradeList();
        return ApiResult.success(result);
    }

    @ApiOperation(value = "查询职等排序", notes = "查询职等排序")
    @ApiOperationSupport(order = 90)
    @GetMapping("/jobGradeOrder")
    public ApiResult jobGradeOrder() {
        String result = unitParamService.getValue(UnitParamConstant.JOB_GRADE_ORDER);
        return ApiResult.success(result);
    }

    @ApiOperation(value = "职级树形查询", notes = "职级树形查询")
    @ApiImplicitParam(name = "keyword", value = "职务序列名称、职级模糊搜索", dataType = "String", required = false)
    @ApiOperationSupport(order = 100)
    @GetMapping("/jobRankTree")
    public ApiResult<List<OrgDutySeqTreeDto>> jobRankTree(@RequestParam(required = false) String keyword) {
        List<OrgDutySeqTreeDto> result = orgDutySeqService.queryJobRankTree(keyword);
        return ApiResult.success(result);
    }

    // @ApiOperation(value="职务树形查询",notes = "职务树形查询")
    // @ApiOperationSupport(order = 130)
    // @GetMapping("/dutyTree")
    // public ApiResult<List<OrgDutySeqTreeDto>> dutyTree(){
    // List<OrgDutySeqTreeDto> result = orgDutySeqService.queryDutyTree("");
    // return ApiResult.success(result);
    // }

    @ApiOperation(value = "职务体系视图", notes = "职务体系视图")
    @ApiOperationSupport(order = 110)
    @GetMapping("/dutyHierarchyView")
    public ApiResult<OrgDutyHierarchyViewDto> dutyHierarchyView() {
        OrgDutyHierarchyViewDto dto = orgDutySeqService.queryDutyHierarchyView();
        return ApiResult.success(dto);
    }

    @ApiOperation(value = "职务体系Excel导出", notes = "职务体系Excel导出")
    @ApiOperationSupport(order = 120)
    @GetMapping("/dutyHierarchyExport")
    public ApiResult dutyHierarchyExport(@RequestParam(required = false) String fileName, HttpServletRequest request,
                                         HttpServletResponse response) throws FileNotFoundException {
        fileName = StringUtils.isBlank(fileName) ? "职务体系.xls" : fileName + ".xls";
        File excelFile = orgDutySeqService.exportDutyHierarchyExcelFile(fileName);
        if (excelFile == null) {
            throw new WellException("导出文件失败");
        }
        InputStream is = new FileInputStream(excelFile);
        FileDownloadUtils.download(request, response, is, fileName); // WorkForm Def
        return ApiResult.success();
    }

    @ApiOperation(value = "职级详情查询", notes = "职级详情查询")
    @ApiImplicitParam(name = "uuid", value = "职级UUID", dataType = "String", required = true)
    @ApiOperationSupport(order = 130)
    @GetMapping("/getMultiOrgJobRankDetail")
    public ApiResult<MultiOrgJobRank> getMultiOrgJobRankDetail(@RequestParam(required = true) String uuid) {
        MultiOrgJobRank result = multiOrgJobRankService.getOne(uuid);
        return ApiResult.success(result);
    }

}
