package com.wellsoft.pt.bpm.engine.management.web;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.exception.WellException;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.bpm.engine.element.*;
import com.wellsoft.pt.bpm.engine.entity.FlowSchema;
import com.wellsoft.pt.bpm.engine.management.service.IdentityReplaceService;
import com.wellsoft.pt.bpm.engine.management.support.IdentityReplaceRequest;
import com.wellsoft.pt.log.entity.UserOperationLog;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({"/identityReplace", "/api/workflow/identity/replace"})
public class IdentityReplaceController extends BaseController {
    @Autowired
    private IdentityReplaceService identityReplaceService;

    /**
     * 拼接人员信息
     *
     * @param unitElements
     * @return
     */
    public static String appenUserString(List<UserUnitElement> unitElements) {
        StringBuilder dateString = new StringBuilder();
        if (unitElements == null) {
            return "";
        }
        for (UnitElement unitElement : unitElements) {
            if (!StringUtils.isEmpty(unitElement.getArgValue())) {
                dateString.append(unitElement.getArgValue() + ";");
            } else {
                dateString.append(unitElement.getValue() + ";");
            }
        }
        String returnString = StringUtils.isEmpty(
                dateString.toString()) ? "" : (dateString.toString()).substring(0,
                (dateString.toString()).lastIndexOf(";"));
        return returnString;
    }

    /**
     * 静态方法用于转换Grid数据
     * COPY BY COMMON
     **/
    protected static JqGridQueryData convertToJqGridQueryData(QueryData querydata) {
        JqGridQueryData jqgridquerydata = new JqGridQueryData();
        PagingInfo paginginfo = querydata.getPagingInfo();
        jqgridquerydata.setDataList(querydata.getDataList());
        jqgridquerydata.setTotalPages(paginginfo.getTotalPages());
        jqgridquerydata.setCurrentPage(paginginfo.getCurrentPage());
        jqgridquerydata.setTotalRows(querydata.getDataList().size());
        return jqgridquerydata;
    }

    protected static QueryInfo buildQueryInfo(JqGridQueryInfo jqGridQueryInfo,
                                              HttpServletRequest request) {
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setPageSize(jqGridQueryInfo.getRows());
        pagingInfo.setCurrentPage(jqGridQueryInfo.getPage());
        pagingInfo.setAutoCount(true);

        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setServiceName(jqGridQueryInfo.getServiceName());
        queryInfo.setQueryType(jqGridQueryInfo.getQueryType());
        queryInfo.setPagingInfo(pagingInfo);
        // sort
        if (StringUtils.isNotBlank(jqGridQueryInfo.getSidx())) {
            queryInfo.setOrderBy(jqGridQueryInfo.getSidx() + " " + jqGridQueryInfo.getSord());
        }
        String queryPrefix = getQueryPrefix(request);
        boolean queryOr = getQueryOr(request);
        List<PropertyFilter> propertyFilters = PropertyFilter.buildFromHttpRequest(request,
                queryPrefix, queryOr);
        queryInfo.setPropertyFilters(propertyFilters);
        return queryInfo;
    }

    /**
     * @param request
     * @return
     */
    protected static String getQueryPrefix(HttpServletRequest request) {
        String queryPrefix = request.getParameter("queryPrefix");
        if (StringUtils.isBlank(queryPrefix)) {
            queryPrefix = "filter";
        }
        return queryPrefix;
    }

    /**
     * 如何描述该方法
     *
     * @param request
     * @return
     */
    protected static boolean getQueryOr(HttpServletRequest request) {
        String queryOr = request.getParameter("queryOr");
        if (StringUtils.isBlank(queryOr)) {
            return false;
        }
        return "true".equalsIgnoreCase(queryOr);
    }

    /**
     * 字串比较
     *
     * @param tmp1
     * @param tmp2
     * @return
     */
    public static String[] compareStr(String tmp1, String tmp2) {
        String bcolor = "<font color='#ff0000;'>";
        String ecolor = "</font>";
        char[] a = new char[tmp1.length()];
        for (int i = 0; i < tmp1.length(); i++) {
            a[i] = tmp1.charAt(i);
        }
        char[] b = new char[tmp2.length()];
        for (int i = 0; i < tmp2.length(); i++) {
            b[i] = tmp2.charAt(i);
        }

        StringBuilder ret1 = new StringBuilder();
        StringBuilder ret2 = new StringBuilder();
        if (tmp1.length() > tmp2.length()) {
            for (int i = 0; i < a.length; i++) {

                if (i < tmp2.length()) {
                    if (a[i] == b[i]) {
                        ret1.append(a[i]);

                        ret2.append(b[i]);

                    } else {
                        ret1.append(bcolor).append(a[i]).append(ecolor);

                        ret2.append(bcolor).append(b[i]).append(ecolor);

                    }

                } else {
                    ret2.append(bcolor).append(a[i]).append(ecolor);
                }

            }
        } else if (tmp1.length() <= tmp2.length()) {
            for (int i = 0; i < b.length; i++) {
                if (i < tmp1.length()) {
                    if (a[i] == b[i]) {
                        ret1.append(a[i]);
                        ret2.append(b[i]);
                    } else {

                        ret1.append(bcolor).append(a[i]).append(ecolor);
                        ret2.append(bcolor).append(b[i]).append(ecolor);
                    }

                } else {
                    ret2.append(bcolor).append(b[i]).append(ecolor);
                }

            }
        }

        String[] ret = new String[2];
        ret[0] = ret1.toString();
        // System.out.println(ret[0]);
        ret[1] = ret2.toString();
        // System.out.println(ret[1]);
        return ret;
    }

    // 跳转查询页面
    @RequestMapping("/searchIndex")
    public String openXml() {
        return "/workflow/flowSearchIndex";
    }

    /**
     * 初始化jqGrid数据流程
     *
     * @param request
     * @param jqGridQueryInfo
     * @param name            名字
     * @param station         岗位
     * @param flowGroup       群组
     * @param department      部门
     * @return queryJson
     * @author linz
     */
    @RequestMapping("/parseXmlToGrid")
    public @ResponseBody
    JqGridQueryData queryList4page(HttpServletRequest request, JqGridQueryInfo jqGridQueryInfo,
                                   @RequestParam(value = "name") String name,
                                   @RequestParam(value = "station") String station,
                                   @RequestParam(value = "flowGroup") String flowGroup,
                                   @RequestParam(value = "department") String department,
                                   @RequestParam(value = "flowName") String flowName) {
        QueryInfo queryInfo = buildQueryInfo(jqGridQueryInfo, request);
        // 根据条件查询对应的实体对象
        name = URLDecoder.decode(name);
        station = URLDecoder.decode(station);
        flowGroup = URLDecoder.decode(flowGroup);
        department = URLDecoder.decode(department);
        flowName = URLDecoder.decode(flowName);
        QueryData queryData = identityReplaceService.getForPageAsTree(jqGridQueryInfo, queryInfo,
                name, station,
                flowGroup, department, flowName, request.getContextPath());
        // 转换成Grid JSON数据
        JqGridQueryData jqGridQueryData = convertToJqGridQueryData(queryData);
        // System.out.println(JsonBinder.buildNormalBinder().toJson(jqGridQueryData));
        return jqGridQueryData;

    }

    /**
     * 初始化jqGrid数据流程明细
     *
     * @param request
     * @param jqGridQueryInfo
     * @param uuid            流程主表ID
     * @author linz
     */
    @RequestMapping("/parseXmlToGridDetail")
    public @ResponseBody
    JqGridQueryData queryList4pageDetail(HttpServletRequest request,
                                         JqGridQueryInfo jqGridQueryInfo,
                                         @RequestParam(value = "uuid") String uuid) {
        QueryInfo queryInfo = buildQueryInfo(jqGridQueryInfo, request);
        // 根据条件查询对应的实体对象
        QueryData queryData = identityReplaceService.getForPageAsTreeDetail(jqGridQueryInfo,
                queryInfo, uuid);
        // 转换成Grid JSON数据
        JqGridQueryData jqGridQueryData = convertToJqGridQueryData(queryData);
        // System.out.println(JsonBinder.buildNormalBinder().toJson(jqGridQueryData));
        return jqGridQueryData;

    }

    @RequestMapping("/downLoadTransactXML")
    public void downLoadTransactXML(HttpServletRequest request, @RequestParam("id") String inId,
                                    HttpServletResponse response) {
        FlowSchema flowSchema = identityReplaceService.getFlowSchemaByUuid(inId);
        String fileName = flowSchema.getName();
        response.setContentType("application/x-msdownload");
        try {
            response.setHeader("Content-Disposition", "attachment; filename="
                    + new String(fileName.getBytes("gb2312"), "ISO-8859-1") + ".xml");
        } catch (UnsupportedEncodingException e1) {
            logger.error(ExceptionUtils.getStackTrace(e1));
        }
        try {
            OutputStream out = response.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(out);
            writer.write(flowSchema.getContentAsString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * XML比较
     *
     * @param request
     * @param flowSchemaUuid
     * @param flowSchemaLogUuid
     * @param response
     * @return
     */
    @RequestMapping("/compareXml")
    public void compareXml(HttpServletRequest request,
                           @RequestParam("flowSchemaLogUuid") String flowSchemaLogUuid,
                           HttpServletResponse response) {

        try {
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            Map<String, String> result = identityReplaceService.compareXml(flowSchemaLogUuid);
            response.getWriter().write("流程XML:" + result.get("flowXml") + "小版本XML:" + result.get("logXml") + "差异XML：" + result.get("diff"));
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }


    }

    /**
     * 初始化jqGrid数据,获取
     *
     * @param request
     * @param jqGridQueryInfo
     * @param uuid            流程主表ID
     * @return queryJson
     * @author linz
     */
    @RequestMapping("/getReplaceLog")
    public @ResponseBody
    JqGridQueryData queryList4Log(HttpServletRequest request, JqGridQueryInfo jqGridQueryInfo,
                                  @RequestParam(value = "uuid") String uuid) {
        QueryInfo queryInfo = buildQueryInfo(jqGridQueryInfo, request);
        // 根据条件查询对应的实体对象
        QueryData queryData = identityReplaceService.getUpdateContentLog(jqGridQueryInfo, queryInfo,
                uuid,
                request.getContextPath());
        // 转换成Grid JSON数据
        JqGridQueryData jqGridQueryData = convertToJqGridQueryData(queryData);
        // System.out.println(JsonBinder.buildNormalBinder().toJson(jqGridQueryData));
        return jqGridQueryData;

    }

    /**
     * @param request
     * @param uuid                 流程主表ID,
     * @param taskIds              taskid (流程主表ID,taskId)
     * @param isUpdatecreator      true:修改发起人 false:不修改发起人
     * @param isUpdatepropertyUser true:修改参与人 false:不修改参与人
     * @param isUpdatemonitor      true:修改督办人 false:不修改督办人
     * @param isUpdateadmin        true:修改监控者 false:不修改监控者
     * @param isUpdateviewer       true:修改阅读者 false:不修改阅读者
     * @param isUpdatetaskUser     true:修改办理人 false:不修改办理人
     * @param isUpdatecopyUser     true:修改抄送人 false:不修改抄送人
     * @param isInsert             true:找不到直接替换  false:不替换抛出异常
     * @param oldUser              旧人员/组织/群组
     * @param newUser              新人员/组织/群组
     * @throws IOException
     */
    @RequestMapping("/saveGridDate")
    public void checkGridDate(HttpServletRequest request,
                              @RequestParam(value = "uuids") String uuid,
                              @RequestParam(value = "taskIds") String taskIds,
                              @RequestParam(value = "isUpdatecreator") Boolean isUpdatecreator,
                              @RequestParam(value = "isUpdatepropertyUser") Boolean isUpdatepropertyUser,
                              @RequestParam(value = "isUpdatemonitor") Boolean isUpdatemonitor,
                              @RequestParam(value = "isUpdateadmin") Boolean isUpdateadmin,
                              @RequestParam(value = "isUpdateviewer") Boolean isUpdateviewer,
                              @RequestParam(value = "isUpdatetaskUser") Boolean isUpdatetaskUser,
                              @RequestParam(value = "isUpdatecopyUser") Boolean isUpdatecopyUser,
                              @RequestParam(value = "isInsert") Boolean isInsert,
                              @RequestParam(value = "oldUser") String oldUser,
                              @RequestParam(value = "newUser") String newUser,
                              @RequestParam(value = "oldUserId") String oldUserId,
                              @RequestParam(value = "newUserId") String newUserId,
                              HttpServletResponse response) throws IOException {
        String errMsg = "";
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        if (!isUpdatecreator && !isUpdatepropertyUser && !isUpdatemonitor && !isUpdateadmin && !isUpdateviewer
                && !isUpdatetaskUser && !isUpdatecopyUser) {
            errMsg = "未选择需要更新的人员信息,请先选择";
            response.getWriter().write(errMsg);
            return;
        }
        if (!isInsert && StringUtils.isEmpty(oldUser) && StringUtils.isEmpty(newUser)) {
            errMsg = "请先选择您要替换的人员信息！";
            response.getWriter().write(errMsg);
            return;
        }
        // 没勾选新增的时候旧人员不能为空！
        if (!isInsert && StringUtils.isEmpty(oldUser) && StringUtils.isNotEmpty(newUser)) {
            errMsg = "未勾选原人员不存在的流程中是否新增替换人员,旧人员不能为空！";
            response.getWriter().write(errMsg);
            return;
        }
        // 勾选更新后，新人员不能为空！
        if (isInsert && StringUtils.isEmpty(newUser)) {
            errMsg = "新增/替换人员信息不能为空！";
            response.getWriter().write(errMsg);
            return;
        }
        String[] oldUserList = oldUser.split(";");

        if (oldUserList.length > 1) {
            errMsg = "不允许多人员操作！";
            response.getWriter().write(errMsg);
            return;
        }
        String[] newUserList = newUser.split(";");

        // if (newUserList.length > 1) {
        // errMsg = "不允许多人员操作！";
        // response.getWriter().write(errMsg);
        // return;
        // }
        if (oldUser == newUser || oldUser.equals(newUser)) {
            errMsg = "原人员与修改人一致无需修改!";
            response.getWriter().write(errMsg);
            return;
        }
        // 通过校验调用保存方法
        try {
            String returnMsg = identityReplaceService.saveBatchDateByGrid(uuid, taskIds,
                    isUpdatecreator,
                    isUpdatepropertyUser, isUpdatemonitor, isUpdateadmin, isUpdateviewer,
                    isUpdatetaskUser,
                    isUpdatecopyUser, isInsert, oldUser, newUser, oldUserId, newUserId);
            response.getWriter().write(returnMsg);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));

            throw new WellException(e.getMessage());
        }
    }

    @RequestMapping("/exportGridDate")
    public void exportGridDate(HttpServletRequest request,
                               @RequestParam(value = "selectRowIds") String selectRowIds,
                               @RequestParam(value = "name") String name,
                               @RequestParam(value = "station") String station,
                               @RequestParam(value = "flowGroup") String flowGroup,
                               @RequestParam(value = "department") String department,
                               @RequestParam(value = "flowName") String flowName,
                               HttpServletResponse response) {
        HSSFWorkbook wb = new HSSFWorkbook();
        // 设置sheet页
        HSSFSheet sheet = wb.createSheet("流程导出");
        HSSFRow row = sheet.createRow((int) 0);
        row.setHeight((short) 250);
        HSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setBorderBottom(BorderStyle.THIN); // 下边框
        style.setBorderLeft(BorderStyle.THIN);// 左边框
        style.setBorderTop(BorderStyle.THIN);// 上边框
        style.setBorderRight(BorderStyle.THIN);// 右边框
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        String expFlowTitle[] = {"流程名称", "版本", "发起人", "参与人", "督办人", "监控者", "阅读者"};
        String expTaskTitle[] = {"环节名称", "办理人", "抄送人"};
        HSSFCell cell = null;
        // 构建title名称

        for (int titleIndex = 0; titleIndex < expFlowTitle.length; titleIndex++) {
            cell = row.createCell((short) titleIndex);
            cell.setCellValue(expFlowTitle[titleIndex]);
            cell.setCellStyle(style);
        }
        // 构建环节的title
        for (int titleIndex = expFlowTitle.length; titleIndex < (expFlowTitle.length + expTaskTitle.length); titleIndex++) {
            cell = row.createCell((short) titleIndex);
            cell.setCellValue(expTaskTitle[titleIndex - expFlowTitle.length]);
            cell.setCellStyle(style);
        }
        sheet.createFreezePane(0, 1, 0, 1);
        List<FlowElement> flowElements = identityReplaceService.queryFlow(selectRowIds,
                URLDecoder.decode(name),
                URLDecoder.decode(station), URLDecoder.decode(flowGroup),
                URLDecoder.decode(department),
                URLDecoder.decode(flowName));
        int rowNum = 1;
        int mergeNum = 0;
        int mergeNumEnd = 0;
        for (int i = 1; i <= flowElements.size(); i++) {
            FlowElement flowElement = flowElements.get(i - 1);
            List<TaskElement> taskElements = flowElement.getTasks();
            if (taskElements.size() != 0) {
                mergeNum = mergeNumEnd + 1;
                mergeNumEnd += taskElements.size();
            } else {
                mergeNum = mergeNumEnd;
                mergeNumEnd += taskElements.size();
            }
            sheet.addMergedRegion(new CellRangeAddress(mergeNum, (short) (0), mergeNumEnd, (short) (0)));
            sheet.addMergedRegion(new CellRangeAddress(mergeNum, (short) (1), mergeNumEnd, (short) (1)));
            sheet.addMergedRegion(new CellRangeAddress(mergeNum, (short) (2), mergeNumEnd, (short) (2)));
            sheet.addMergedRegion(new CellRangeAddress(mergeNum, (short) (3), mergeNumEnd, (short) (3)));
            sheet.addMergedRegion(new CellRangeAddress(mergeNum, (short) (4), mergeNumEnd, (short) (4)));
            sheet.addMergedRegion(new CellRangeAddress(mergeNum, (short) (5), mergeNumEnd, (short) (5)));
            sheet.addMergedRegion(new CellRangeAddress(mergeNum, (short) (6), mergeNumEnd, (short) (6)));
            for (int j = 0; j < taskElements.size(); j++) {
                // 四个参数分别是：起始行，起始列，结束行，结束列
                //
                row = sheet.createRow((int) mergeNum);
                cell = row.createCell((short) 0);
                cell.setCellValue(flowElement.getName());
                // sheet.addMergedRegion(new Region(dRow, (short)
                // (1),dRow+flowElement.getTasks().size(),(short) (1)));
                row = sheet.createRow((int) mergeNum);
                cell = row.createCell((short) 1);
                cell.setCellValue(flowElement.getVersion());

                // sheet.addMergedRegion(new Region(dRow, (short)
                // (2),dRow+flowElement.getTasks().size(),(short) (2)));
                row = sheet.createRow((int) mergeNum);
                cell = row.createCell((short) 2);
                // 取出人员信息
                PropertyElement propertyElement = flowElement.getProperty();
                List<UserUnitElement> unitElements = propertyElement.getCreators();
                String createdUser = appenUserString(unitElements);
                cell.setCellValue(createdUser);
                // sheet.addMergedRegion(new Region(dRow, (short)
                // (3),dRow+flowElement.getTasks().size(),(short) (3)));
                row = sheet.createRow((int) mergeNum);
                cell = row.createCell((short) 3);
                // 取出人员信息
                unitElements = propertyElement.getUsers();
                createdUser = appenUserString(unitElements);
                cell.setCellValue(createdUser);

                // sheet.addMergedRegion(new Region(dRow, (short)
                // (4),dRow+flowElement.getTasks().size(),(short) (4)));
                row = sheet.createRow((int) mergeNum);
                cell = row.createCell((short) 4);
                // 取出人员信息
                unitElements = propertyElement.getMonitors();
                createdUser = appenUserString(unitElements);
                cell.setCellValue(createdUser);

                // sheet.addMergedRegion(new Region(dRow, (short)
                // (5),dRow+flowElement.getTasks().size(),(short) (5)));
                row = sheet.createRow((int) mergeNum);
                cell = row.createCell((short) 5);
                // 取出人员信息
                unitElements = propertyElement.getAdmins();
                createdUser = appenUserString(unitElements);
                cell.setCellValue(createdUser);

                // sheet.addMergedRegion(new Region(dRow, (short)
                // (6),dRow+flowElement.getTasks().size(),(short) (6)));
                row = sheet.createRow((int) mergeNum);
                cell = row.createCell((short) 6);
                // 取出人员信息
                unitElements = propertyElement.getViewers();
                createdUser = appenUserString(unitElements);
                cell.setCellValue(createdUser);

                TaskElement taskElement = taskElements.get(j);
                row = sheet.createRow((int) rowNum);
                cell = row.createCell((short) 7);
                cell.setCellValue(taskElement.getName());

                row = sheet.createRow((int) rowNum);
                cell = row.createCell((short) 8);
                unitElements = taskElement.getUsers();
                createdUser = appenUserString(unitElements);
                cell.setCellValue(createdUser);

                row = sheet.createRow((int) rowNum);
                cell = row.createCell((short) 9);
                unitElements = taskElement.getCopyUsers();
                createdUser = appenUserString(unitElements);
                cell.setCellValue(createdUser);
                rowNum++;
            }
        }
        response.reset();
        response.setContentType("application/x-msdownload");
        try {
            response.setHeader("Content-Disposition", "attachment; filename="
                    + new String("流程办理人信息".getBytes("gb2312"), "ISO-8859-1") + ".xls");
        } catch (UnsupportedEncodingException e1) {
            logger.error(ExceptionUtils.getStackTrace(e1));
        }
        ServletOutputStream outStream = null;

        try {
            outStream = response.getOutputStream();
            wb.write(outStream);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }

    }

    /**
     * @param replaceRequest
     */
    @PostMapping("/modify")
    @ResponseBody
    public ApiResult<List<IdentityReplaceRequest.IdentityReplaceFlowDefinitionUserItem>> modify(@RequestBody IdentityReplaceRequest replaceRequest) {
        identityReplaceService.modify(replaceRequest);
        return ApiResult.success(replaceRequest.getRecords());
    }

    /**
     * @param queryInfo
     * @return
     */
    @PostMapping("/logs/list")
    @ResponseBody
    public ApiResult<QueryData> listLogs(@RequestBody IdentityReplaceLogQueryInfo queryInfo) {
        QueryData queryData = identityReplaceService.listLogs(queryInfo.getKeyword(), queryInfo.getPagingInfo(), queryInfo.getOrderBy());
        return ApiResult.success(queryData);
    }

    @GetMapping("/logs/get")
    @ResponseBody
    public ApiResult<UserOperationLog> getLog(@RequestParam(name = "logUuid") String logUuid) {
        UserOperationLog userOperationLog = identityReplaceService.getLogByUuid(logUuid);
        return ApiResult.success(userOperationLog);
    }

    /**
     *
     */
    private static final class IdentityReplaceLogQueryInfo extends BaseObject {

        private static final long serialVersionUID = -4613839918890460164L;

        private String keyword;

        private PagingInfo pagingInfo;

        private String orderBy;

        /**
         * @return the keyword
         */
        public String getKeyword() {
            return keyword;
        }

        /**
         * @param keyword 要设置的keyword
         */
        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        /**
         * @return the pagingInfo
         */
        public PagingInfo getPagingInfo() {
            return pagingInfo;
        }

        /**
         * @param pagingInfo 要设置的pagingInfo
         */
        public void setPagingInfo(PagingInfo pagingInfo) {
            this.pagingInfo = pagingInfo;
        }

        /**
         * @return the orderBy
         */
        public String getOrderBy() {
            return orderBy;
        }

        /**
         * @param orderBy 要设置的orderBy
         */
        public void setOrderBy(String orderBy) {
            this.orderBy = orderBy;
        }
    }

}
