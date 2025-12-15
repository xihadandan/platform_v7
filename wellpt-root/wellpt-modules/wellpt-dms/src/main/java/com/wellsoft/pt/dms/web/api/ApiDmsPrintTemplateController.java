package com.wellsoft.pt.dms.web.api;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.printtemplate.dto.GetPrintTemplateTreeByFolderUuidsDto;
import com.wellsoft.pt.basicdata.printtemplate.dto.PrintDto;
import com.wellsoft.pt.basicdata.printtemplate.dto.PrintsDto;
import com.wellsoft.pt.basicdata.printtemplate.web.PrintTemplateController;
import com.wellsoft.pt.dms.service.DmsFileService;
import com.wellsoft.pt.dms.service.DmsFolderConfigurationService;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Description:
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2022/3/7.1	    zenghw		2022/3/7		    Create
 * </pre>
 * @date 2022/3/7
 */
@Api(tags = "dms打印模版")
@Controller
@RequestMapping("/api/dms/printtemplate")
public class ApiDmsPrintTemplateController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(PrintTemplateController.class);

    @Autowired
    private DmsFolderConfigurationService dmsFolderConfigurationService;

    @Autowired
    private DmsFileService dmsFileService;

    /**
     * 根据用户获取对应的套打模板树
     * 超管用户：可见超管和全部系统单位定义的打印模板
     * 单位管理员：可见超管和当前系统单位中创建的打印模板
     *
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getPrintTemplateTreeByUser", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据用户获取对应的套打模板（树）", notes = "根据用户获取对应的套打模板（树）")
    @ApiOperationSupport(order = 180)
    public ApiResult<List<TreeNode>> getPrintTemplateTreeByUser() {

        List<TreeNode> treeNodeList = dmsFolderConfigurationService.getPrintTemplateTreeByUser();
        return ApiResult.success(treeNodeList);
    }

    @ResponseBody
    @PostMapping(value = "/getPrintTemplateTreeByFolderUuids", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据文件夹Uuid获取对应的套打模板（树）", notes = "根据文件夹Uuid获取对应的套打模板（树）")
    public ApiResult<List<TreeNode>> getPrintTemplateTreeByFolderUuids(
            @RequestBody GetPrintTemplateTreeByFolderUuidsDto getPrintTemplateTreeByFolderUuidsDto) {

        if (getPrintTemplateTreeByFolderUuidsDto.getFolderUuids().size() == 0) {
            return ApiResult.success(Lists.newArrayList());
        }
        List<TreeNode> treeNodeList = dmsFolderConfigurationService
                .getPrintTemplateTreeByFolderUuids(getPrintTemplateTreeByFolderUuidsDto.getFolderUuids());
        return ApiResult.success(treeNodeList);
    }

    @ResponseBody
    @PostMapping(value = "/print", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "文档内套打", notes = "文件管理组件-文档内套打")
    public ApiResult<LogicFileInfo> print(@RequestBody PrintDto printDto) {

        return ApiResult.success(dmsFileService.print(printDto));
    }

    @ResponseBody
    @PostMapping(value = "/prints", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "批量套打", notes = "文件管理组件-批量套打")
    public ApiResult<LogicFileInfo> prints(@RequestBody PrintsDto printsDto) {

        return ApiResult.success(dmsFileService.prints(printsDto));
    }
}
