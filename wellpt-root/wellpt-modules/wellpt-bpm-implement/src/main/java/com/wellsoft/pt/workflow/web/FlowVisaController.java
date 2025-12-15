package com.wellsoft.pt.workflow.web;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.params.facade.SystemParams;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.message.service.MessageInboxService;
import com.wellsoft.pt.message.websocket.service.WebSocketService;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.workflow.dto.*;
import com.wellsoft.pt.workflow.entity.WfFlowInspectionFileRecordEntity;
import com.wellsoft.pt.workflow.service.FlowVisaService;
import com.wellsoft.pt.workflow.service.WfFlowInspectionFileRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 流程签批接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/8/19.1	    zenghw		2021/8/19		    Create
 * </pre>
 * @date 2021/8/19
 */
@Api(tags = "流程签批接口")
@RestController
@RequestMapping("/api/workflow/visa/inspection")
public class FlowVisaController extends BaseController {

    // 管理后台 系统参数：在线预览服务IP+端口
    private static final String KEY_DOCUMENT_PREVIEW_PATH = "document.preview.path";
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ReadMarkerService readMarkerService;
    @Autowired
    private FlowVisaService flowVisaService;
    @Autowired
    private WfFlowInspectionFileRecordService wfFlowInspectionFileRecordService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private FlowInstanceService flowInstanceService;
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private MessageInboxService messageInboxService;

    @ApiOperation(value = "数据状态变更", notes = "数据状态变更")
    @PostMapping("/readStatusUpdate")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前用户ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "flowInstUuids", value = "流程实例UUID", paramType = "query", dataType = "String", allowMultiple = true, required = true),
            @ApiImplicitParam(name = "dataStatuses", value = "数据状态(已阅变更未阅，未阅变更已阅)已阅：true;未阅：false", paramType = "query", dataType = "String", allowMultiple = true, required = true)})
    public ApiResult<Boolean> readStatusUpdate(@RequestParam(name = "userId", required = true) String userId,
                                               @RequestParam(name = "flowInstUuids", required = true) String[] flowInstUuids,
                                               @RequestParam(name = "dataStatuses", required = true) String[] dataStatuses) {
        if (flowInstUuids.length != dataStatuses.length) {
            return ApiResult.fail("flowInstUuids和dataStatuses的数据数组没对应上");
        }

        Boolean flg = readMarkerService.readStatusUpdate(userId, flowInstUuids, dataStatuses);
        if (flg) {
            return ApiResult.success(flg);
        }
        return ApiResult.fail(flg);
    }

    @ApiOperation(value = "返回指定条数的最近使用意见", notes = "返回指定条数的最近使用意见")
    @GetMapping("/getUserRecentOpinions")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前用户ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "flowInstUuid", value = "流程实例UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "num", value = "返回的条数", paramType = "query", dataType = "Integer", required = true)})
    public ApiResult<List<FlowOpinionDto>> getUserRecentOpinions(
            @RequestParam(name = "userId", required = true) String userId,
            @RequestParam(name = "flowInstUuid", required = true) String flowInstUuid,
            @RequestParam(name = "num", required = true) Integer num) {
        List<FlowOpinionDto> flowOpinionDtos = flowVisaService.getUserRecentOpinions(userId, flowInstUuid, num);
        return ApiResult.success(flowOpinionDtos);
    }

    @ApiOperation(value = "返回当前用户的常用意见", notes = "返回当前用户的常用意见")
    @GetMapping("/getUserCommonOpinions")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前用户ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "num", value = "返回的条数", paramType = "query", dataType = "Integer", required = true)})
    public ApiResult<List<FlowOpinionDto>> getUserCommonOpinions(
            @RequestParam(name = "userId", required = true) String userId,
            @RequestParam(name = "num", required = true) Integer num) {

        List<FlowOpinionDto> flowOpinionDtos = flowVisaService.getUserCommonOpinions(userId, num);

        return ApiResult.success(flowOpinionDtos);
    }

    @ApiOperation(value = "返回指定环节的环节意见立场", notes = "返回指定环节的环节意见立场")
    @GetMapping("/getTaskOpinion")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowInstUuid", value = "流程实例UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "taskId", value = "指定环节ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<UnitElementDto>> getTaskOpinion(
            @RequestParam(name = "flowInstUuid", required = true) String flowInstUuid,
            @RequestParam(name = "taskId", required = true) String taskId) {
        try {
            List<UnitElementDto> list = flowVisaService.getTaskOpinion(flowInstUuid, taskId);
            return ApiResult.success(list);
        } catch (RuntimeException e) {
            logger.error("getTaskOpinion 异常：", e);
            return ApiResult.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "手写签批附件列表", notes = "手写签批附件列表")
    @GetMapping("/getInspectionFileList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowInstUuid", value = "流程实例UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "fieldCodes", value = "附件字段编码集合", paramType = "query", dataType = "String", allowMultiple = true, required = true)})
    public ApiResult<List<GetInspectionFileListDto>> getInspectionFileList(
            @RequestParam(name = "flowInstUuid", required = true) String flowInstUuid,
            @RequestParam(name = "fieldCodes", required = true) String[] fieldCodes) {
        List<GetInspectionFileListDto> getInspectionFileListDtos = flowVisaService.getInspectionFileList(flowInstUuid,
                fieldCodes);
        return ApiResult.success(getInspectionFileListDtos);
    }

    /**
     * //1、先通过fileUuid 去流程签批记录表查对应数据，如果找不到，则先转化
     * //1.1、 转化失败，则返回字符串提示：此格式不支持转化pdf格式”转化的字节数据。（code编码不为0）
     * //2、如果已经转化了，则直接通过流程签批记录表的签批附件uuid，获取对应的文件
     *
     * @return com.wellsoft.context.web.controller.ApiResult<byte [ ]>
     **/
    @ApiOperation(value = "手写签批附件详情", notes = "手写签批附件详情")
    @GetMapping("/getInspectionFileDetail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowInstUuid", value = "流程实例UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "fileUuid", value = "原字段的附件uuid", paramType = "query", dataType = "String", required = true)})
    public ApiResult<GetInspectionFileDetailDto> getInspectionFileDetail(
            @RequestParam(name = "flowInstUuid", required = true) String flowInstUuid,
            @RequestParam(name = "fileUuid", required = true) String fileUuid, HttpServletRequest request,
            HttpServletResponse response) {

        WfFlowInspectionFileRecordEntity wfFlowInspectionFileRecordEntity = wfFlowInspectionFileRecordService
                .getFlowInspectionFileRecordByFileUuid(fileUuid);
        MongoFileEntity mongoFileEntity = mongoFileService.getFile(fileUuid);
        if (mongoFileEntity == null) {
            return ApiResult.fail("此附件不存在 fileUuid:" + fileUuid);
        }
        byte[] fileBytes = null;
        if (wfFlowInspectionFileRecordEntity == null) {
            try {
                fileBytes = fileToPdf(request, flowInstUuid, fileUuid);
            } catch (RuntimeException e) {
                return ApiResult.fail(e.getMessage());
            }
        } else {
            mongoFileEntity = mongoFileService.getFile(wfFlowInspectionFileRecordEntity.getInspectionFileUuid());
            try {
                fileBytes = readBytes(mongoFileEntity.getInputstream());
            } catch (Exception e) {
                logger.error("Inputstream 转byte 失败：", e);
                return ApiResult.fail("nputstream 转byte 失败");
            }
        }

        GetInspectionFileDetailDto getInspectionFileDetailDto = new GetInspectionFileDetailDto();
        String fileBytesBase64 = Base64.encode(fileBytes);
        getInspectionFileDetailDto.setFileBytes(fileBytesBase64);
        getInspectionFileDetailDto.setFileName(mongoFileEntity.getFileName());
        return ApiResult.success(getInspectionFileDetailDto);
    }

    @ApiOperation(value = "更新手写签批附件", notes = "更新手写签批附件")
    @PostMapping("/updateInspectionFile")
    public ApiResult<String> updateInspectionFile(@RequestBody UpdateInspectionFileDto updateInspectionFileDto) {
        if (StringUtils.isBlank(updateInspectionFileDto.getInspectionFile())) {
            return ApiResult.fail("手写签批附件不能为空");
        }
        try {
            // base64解码
            byte[] inspectionFileBytes = Base64.decode(updateInspectionFileDto.getInspectionFile());
            String inspectionFileUuid = flowVisaService.updateInspectionFile(updateInspectionFileDto.getFileUuid(),
                    inspectionFileBytes);
            return ApiResult.success(inspectionFileUuid);
        } catch (Exception e) {
            logger.error("解析Base64异常", e);
            return ApiResult.fail("解析Base64异常");
        }

    }

    @ApiOperation(value = "提交流程", notes = "提交流程")
    @PostMapping("/submitFlow")
    public ApiResult<String> submitFlow(@RequestBody SubmitFlowDto submitFlowDto) {
        if (submitFlowDto.getFileUuids().size() != submitFlowDto.getInspectionFileUuids().size()) {
            return ApiResult.fail("字段附件uuid集合和签批附件集合数组数量没对应上");
        }
        if (StringUtils.isBlank(submitFlowDto.getFlowInstUuid())
                || StringUtils.isBlank(submitFlowDto.getTaskInstUuid())) {
            return ApiResult.fail("提交失败:FlowInstUuid和TaskInstUuid不能为空");
        }
        try {
            Boolean flg = flowVisaService.submitFlow(submitFlowDto);
            if (flg) {
                return ApiResult.success("提交成功");
            }
            return ApiResult.fail("流程配置异常，请联系管理员！");
        } catch (Exception e) {
            logger.error("流程异常，请检查：", e);
            return ApiResult.fail("流程配置异常，请联系管理员！");
        }
    }

    @ApiOperation(value = "保存流程", notes = "保存流程")
    @PostMapping("/saveFlow")
    public ApiResult<String> saveFlow(@RequestBody SubmitFlowDto submitFlowDto) {
        if (submitFlowDto.getFileUuids().size() != submitFlowDto.getInspectionFileUuids().size()) {
            return ApiResult.fail("字段附件uuid集合和签批附件集合数组数量没对应上");
        }
        Boolean flg = flowVisaService.saveFlow(submitFlowDto);
        if (flg) {
            return ApiResult.success("保存成功");
        }
        return ApiResult.fail("保存失败");
    }

    @ApiOperation(value = "退回流程", notes = "退回流程")
    @PostMapping("/rollbackFlow")
    public ApiResult<String> rollbackFlow(@RequestBody RollbackFlowDto rollbackFlowDto) {
        if (rollbackFlowDto != null && Integer.valueOf(1).equals(rollbackFlowDto.getRollbackType())
                || Integer.valueOf(2).equals(rollbackFlowDto.getRollbackType())) {
            if (StringUtils.isBlank(rollbackFlowDto.getFlowInstUuid())
                    || StringUtils.isBlank(rollbackFlowDto.getTaskInstUuid())) {
                return ApiResult.fail("提交失败:FlowInstUuid和TaskInstUuid不能为空");
            }
            try {
                Boolean flg = flowVisaService.rollbackFlow(rollbackFlowDto);
                if (flg) {
                    return ApiResult.success("提交成功");
                }
                return ApiResult.fail("提交失败");
            } catch (Exception e) {
                return ApiResult.fail("提交失败");
            }
        } else {
            return ApiResult.fail("RollbackType的值非法，请输入正确的退回类型（1:退回，2:直接退回）");
        }

    }

    @ApiOperation(value = "表格组件查询接口", notes = "表格组件查询接口，附件数量 要异步调用getFileNumsByFormFields接口")
    @GetMapping("/getBootstrapTableList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bootstrapTableUuid", value = "表格组件UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "bootstrapTableKey", value = "关键字查询", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "pageNum", value = "分页：第几页", paramType = "query", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "pageSize", value = "分页：每页条数", paramType = "query", dataType = "Integer", required = true)})
    public ApiResult<GetBootstrapTableDto> getBootstrapTableList(
            @RequestParam(name = "bootstrapTableUuid", required = true) String bootstrapTableUuid,
            @RequestParam(name = "bootstrapTableKey", required = false) String bootstrapTableKey,
            @RequestParam(name = "pageNum", required = true) Integer pageNum,
            @RequestParam(name = "pageSize", required = true) Integer pageSize) {
        try {
            GetBootstrapTableDto bootstrapTableDto = flowVisaService.getBootstrapTableList(bootstrapTableUuid,
                    bootstrapTableKey, pageNum, pageSize);
            return ApiResult.success(bootstrapTableDto);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ApiResult.fail(e.getMessage());
        }

    }

    @ApiOperation(value = "获取签署意见和意见立场接口", notes = "获取签署意见和意见立场接口")
    @GetMapping("/getSignOpinionAndOpinionPosition")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowInstUuid", value = "流程实例UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "userId", value = "用户ID", paramType = "query", dataType = "String", allowMultiple = true, required = true)})
    public ApiResult<GetSignOpinionAndOpinionPositionDto> getSignOpinionAndOpinionPosition(
            @RequestParam(name = "flowInstUuid", required = true) String flowInstUuid,
            @RequestParam(name = "userId", required = true) String userId) {
        GetSignOpinionAndOpinionPositionDto dto = flowVisaService.getSignOpinionAndOpinionPosition(flowInstUuid,
                userId);
        return ApiResult.success(dto);
    }

    @ApiOperation(value = "表格组件查询接口-表单指定字段获取总附件数量", notes = "表格组件查询接口-表单指定字段获取总附件数量")
    @GetMapping("/getFileNumsByFormFields")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowInstUuid", value = "流程实例UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "fieldCodes", value = "附件字段编码集合", paramType = "query", dataType = "String", allowMultiple = true, required = true)})
    public ApiResult<Integer> getFileNumsByFormFields(
            @RequestParam(name = "flowInstUuid", required = true) String flowInstUuid,
            @RequestParam(name = "fieldCodes", required = true) String[] fieldCodes) {
        Integer fileNums = flowVisaService.getFileNumsByFormFields(flowInstUuid, fieldCodes);
        return ApiResult.success(fileNums);
    }

    @ApiOperation(value = "表格组件数据量查询接口", notes = "表格组件数据量查询接口")
    @GetMapping("/getBootstrapTableNum")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bootstrapTableUuid", value = "表格组件UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Integer> getBootstrapTableNum(
            @RequestParam(name = "bootstrapTableUuid", required = true) String bootstrapTableUuid) {
        Integer num = flowVisaService.getBootstrapTableListAllNum(bootstrapTableUuid);
        return ApiResult.success(num);
    }

    @ApiOperation(value = "获取流程实例对象接口", notes = "获取流程实例对象接口")
    @GetMapping("/getFlowInstanceDetail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowInstUuid", value = "流程实例uuid", paramType = "query", dataType = "String", required = true)})
    public ApiResult<GetFlowInstanceDetailDto> getFlowInstanceDetail(
            @RequestParam(name = "flowInstUuid", required = true) String flowInstUuid) {
        FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);
        GetFlowInstanceDetailDto detailDto = new GetFlowInstanceDetailDto();
        BeanUtils.copyProperties(flowInstance, detailDto);
        return ApiResult.success(detailDto);
    }

    @ApiOperation(value = "获取手写签批记录接口", notes = "获取手写签批记录接口")
    @GetMapping("/getInspectionLogs")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowInstUuid", value = "流程实例uuid", paramType = "query", dataType = "String", required = true)})
    public ApiResult<String> getInspectionLogs(
            @RequestParam(name = "flowInstUuid", required = true) String flowInstUuid) {
        String logJsons = flowVisaService.getInspectionLogs(flowInstUuid);
        return ApiResult.success(logJsons);
    }

    @ApiOperation(value = "测试接口", notes = "测试接口")
    @GetMapping("/test")
    public ApiResult test() {
        // SubmitFlowDto submitFlowDto = new SubmitFlowDto();
        // submitFlowDto.setFlowInstUuid("45d41712-736e-46ac-a36d-42cda8bf2815");
        // submitFlowDto.setTaskInstUuid("29aa613c-9f24-4c92-9c5b-9055e7f520db");
        // submitFlowDto.setFileUuid("e9d400e944d84232b6764cc9279dde53");
        //
        // File src = new File("C:\\Users\\Administrator\\Desktop\\xuzhu/test0826.doc")
        // ;
        // InputStream input = null;
        // try {
        // input = new FileInputStream(src);
        // byte[] inspectionFile =readBytes(input);
        // submitFlowDto.setInspectionFile(inspectionFile);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        //
        // submitFlowDto.setInspectionLog("setInspectionLog");
        // submitFlowDto.setOpinionPosition("setOpinionPosition");
        // submitFlowDto.setSignOpinion("setSignOpinion");
        // submitFlow(submitFlowDto);

        //// updateInspectionFile 返回“c3883b6eb9d54ec185f6009a59f7bd1a”
        // File src = new File("C:\\Users\\Administrator\\Desktop\\xuzhu/test0826.doc")
        //// ;
        // InputStream input = null;
        // try {
        // input = new FileInputStream(src);
        // byte[] inspectionFile =readBytes(input);
        // return updateInspectionFile("xiaoxuzhu",inspectionFile);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

        //// submitFlow
        // SubmitFlowDto submitFlowDto = new SubmitFlowDto();
        // submitFlowDto.setFlowInstUuid("9be6d3a5-8874-4c38-a595-cf3af4ed1bb4");
        // submitFlowDto.setTaskInstUuid("00a1fee4-f525-4961-b97b-67ab62ce517b");
        // List<String> fileUuids = new ArrayList<>();
        // fileUuids.add("xiaoxuzhu");
        // submitFlowDto.setFileUuids(fileUuids);
        // List<String> inspectionFileUuids = new ArrayList<>();
        // inspectionFileUuids.add("c3883b6eb9d54ec185f6009a59f7bd1a");
        // submitFlowDto.setInspectionFileUuids(inspectionFileUuids);
        // submitFlowDto.setInspectionLog("setInspectionLog");
        // submitFlowDto.setOpinionPositionLable("setOpinionPositionLable");
        // submitFlowDto.setOpinionPositionValue("setOpinionPositionValue");
        // submitFlowDto.setSignOpinion("setSignOpinion");
        // return submitFlow(submitFlowDto);

        // saveFlow
        SubmitFlowDto submitFlowDto = new SubmitFlowDto();
        submitFlowDto.setFlowInstUuid("9be6d3a5-8874-4c38-a595-cf3af4ed1bb4");
        submitFlowDto.setTaskInstUuid("00a1fee4-f525-4961-b97b-67ab62ce517b");
        List<String> fileUuids = new ArrayList<>();
        fileUuids.add("xiaoxuzhu");
        submitFlowDto.setFileUuids(fileUuids);
        List<String> inspectionFileUuids = new ArrayList<>();
        inspectionFileUuids.add("c3883b6eb9d54ec185f6009a59f7bd1a");
        submitFlowDto.setInspectionFileUuids(inspectionFileUuids);
        submitFlowDto.setInspectionLog("setInspectionLog");
        submitFlowDto.setOpinionPositionLable("setOpinionPositionLable");
        submitFlowDto.setOpinionPositionValue("setOpinionPositionValue");
        submitFlowDto.setSignOpinion("setSignOpinion");
        return saveFlow(submitFlowDto);
    }

    /**
     * 文件转化为pdf
     *
     * @param request
     * @param flowInstUuid 流程实例uuid
     * @param fileUuid     文件Uuid
     * @return java.lang.Boolean
     **/
    private byte[] fileToPdf(HttpServletRequest request, String flowInstUuid, String fileUuid) throws RuntimeException {
        Boolean isToPdfSuccess = true;
        byte[] fileBytes = null;
        OutputStream outputStream = null;
        BufferedInputStream bufferedInputStream = null;
        InputStream content = null;
        String dest = "";

        HttpClient httpClient = HttpClients.createDefault();
        String documentPreviewPath = SystemParams.getValue(KEY_DOCUMENT_PREVIEW_PATH);
        if (StringUtils.isBlank(documentPreviewPath)) {
            throw new RuntimeException("请联系管理员去后台配置系统参数：document.preview.path");
        }
        // http://<client server>:<port>/document/online/viewer?WOPISrc=
        // encodeURIComponent(http://<host
        // server>:<port>/wopi/files/<file_id>?access_token=<token>)&PdfMode=<PdfMode>
        // <client server>:<port> 在线预览服务器地址及端口
        // <host server>:<port> 应用服务器地址及端口
        // <file_id> 应用服务器的Mongo文件ID
        // <token> 应用服务器的生成的access_token
        // <PdfMode> 是否以pdf预览，1是，0否，默认1

        try {
            String url = documentPreviewPath + "/document/online/viewer";
            URIBuilder uriBuilder = new URIBuilder(url);
            String token = "336dc563-1d17-44a3-a916-e8abe2e88cbb";// 参考前端的预览，先写死token
            String wopiSrc = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                    + "/wopi/files/" + fileUuid + "?access_token=" + token + "&PdfMode=1";
            uriBuilder.setParameter("WOPISrc", wopiSrc);
            // 同步，非异步
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            HttpResponse httpResponse = httpClient.execute(httpGet);
            content = httpResponse.getEntity().getContent();
            Thread.sleep(2000);
            // 转换成功后，再获取文件流
            url = documentPreviewPath + "/document/online/pdf/viewer/" + fileUuid;
            uriBuilder = new URIBuilder(url);
            httpGet = new HttpGet(uriBuilder.build());
            httpResponse = httpClient.execute(httpGet);
            content = httpResponse.getEntity().getContent();
            byte[] buffer = new byte[1024 * 4];
            String tmpDirPath = System.getProperty("java.io.tmpdir");
            FileUtil.mkdir(tmpDirPath);
            logger.info("临时文件要保存的临时目录：" + tmpDirPath);
            dest = tmpDirPath + "/" + fileUuid + ".pdf";
            File file = new File(dest);
            if (!file.exists()) {
                file.createNewFile();
            }
            outputStream = new BufferedOutputStream(new FileOutputStream(dest));
            int n = 0;
            while (-1 != (n = content.read(buffer))) {
                outputStream.write(buffer, 0, n);
            }
            outputStream.flush();
            bufferedInputStream = FileUtil.getInputStream(dest);
            String wopiFileInfoJson = IoUtil.read(bufferedInputStream, "UTF-8");
            if (wopiFileInfoJson.indexOf("PDF") < 0 || wopiFileInfoJson.indexOf("该文件不支持在线预览") > 0) {
                isToPdfSuccess = false;
            } else {
                bufferedInputStream = FileUtil.getInputStream(dest);
                flowVisaService.updateInspectionFile(flowInstUuid, fileUuid, bufferedInputStream);
                fileBytes = FileUtil.readBytes(dest);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        } finally {
            IoUtil.close(outputStream);
            IoUtil.close(content);
            IoUtil.close(bufferedInputStream);
            FileUtil.del(dest);
        }
        if (!isToPdfSuccess) {
            throw new RuntimeException("此格式不支持转化pdf格式");
        }
        return fileBytes;
    }

    private byte[] readBytes(InputStream in) throws Exception {
        if (in instanceof FileInputStream) {
            // 文件流的长度是可预见的，此时直接读取效率更高
            final byte[] result;
            try {
                final int available = in.available();
                result = new byte[available];
                final int readLength = in.read(result);
                if (readLength != available) {
                    throw new IOException("File length is [{" + available + "}] but read [{" + readLength + "}]!");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                in.close();
            }
            return result;
        }
        // 未知bytes总量的流
        final FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        try {
            IoUtil.copy(in, out);
        } finally {
            IoUtil.close(in);
        }
        return out.toByteArray();
    }
}
