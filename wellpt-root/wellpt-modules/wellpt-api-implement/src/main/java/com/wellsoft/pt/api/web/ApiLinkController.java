package com.wellsoft.pt.api.web;

import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.api.entity.ApiInvokeLogEntity;
import com.wellsoft.pt.api.entity.ApiLinkEntity;
import com.wellsoft.pt.api.entity.ApiOperationEntity;
import com.wellsoft.pt.api.service.ApiLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年05月13日   chenq	 Create
 * </pre>
 */
@RestController
@RequestMapping("/api/apiLink")
public class ApiLinkController extends BaseController {

    @Autowired
    private ApiLinkService apiLinkService;

    @GetMapping("/details/{uuid}")
    public ApiResult<ApiLinkEntity> getApiLinkDetails(@PathVariable Long uuid) {
        return ApiResult.success(apiLinkService.getApiLinkDetails(uuid));
    }

    @GetMapping("/operationDetails/{uuid}")
    public ApiResult<ApiOperationEntity> operationDetails(@PathVariable Long uuid) {
        return ApiResult.success(apiLinkService.getApiOperationDetails(uuid));
    }

    @GetMapping("/existApiLinkId")
    public ApiResult<Boolean> existApiLinkId(@RequestParam String id) {
        return ApiResult.success(apiLinkService.getApiLinkById(id) != null);
    }

    @GetMapping("/delete/{uuid}")
    public ApiResult<Void> deleteByUuid(@PathVariable Long uuid) {
        apiLinkService.delete(uuid);
        return ApiResult.success();
    }

    @PostMapping("/saveApiLink")
    public @ResponseBody
    ApiResult<Long> saveApiLink(@RequestBody ApiLinkEntity dto) {
        return ApiResult.success(apiLinkService.saveApiLink(dto));
    }

    @PostMapping("/saveApiOperation")
    public @ResponseBody
    ApiResult<Long> saveApiOperation(@RequestBody ApiOperationEntity operationEntity) {
        return ApiResult.success(apiLinkService.saveApiOperation(operationEntity));
    }

    @GetMapping("/deleteApiOperation/{uuid}")
    public ApiResult<Void> deleteApiOperation(@PathVariable Long uuid) {
        apiLinkService.deleteApiOperation(uuid);
        return ApiResult.success();
    }


    @PostMapping("/delete")
    public ApiResult<Void> deleteByUuids(@RequestBody List<Long> uuids) {
        apiLinkService.deleteByUuids(uuids);
        return ApiResult.success();
    }

    @GetMapping("/getApiLinksByAppId")
    public ApiResult<List<ApiLinkEntity>> getApiLinksByAppId(@RequestParam String appId) {
        return ApiResult.success(apiLinkService.getApiLinksByAppId(appId));
    }

    @GetMapping("/getApiOperationsByAppId")
    public ApiResult<List<ApiOperationEntity>> getApiOperationsByAppId(@RequestParam String appId) {
        return ApiResult.success(apiLinkService.getApiOperationsByAppId(appId));
    }


    @PostMapping("/commitInvokeLog")
    public ApiResult<Void> commitInvokeLog(@RequestBody ApiInvokeLogEntity logEntity) {
        apiLinkService.saveApiInvokeLog(logEntity);
        return ApiResult.success();
    }

    @PostMapping("/listPageSimpleInvokeLog")
    public ApiResult<Page> listPageSimpleInvokeLog(@RequestBody Map<String, Object> params) {
        Map<String, Object> pageParam = (Map) params.get("pagingInfo");
        PagingInfo pagingInfo = new PagingInfo((int) pageParam.get("pageIndex"), (int) pageParam.get("pageSize"), (boolean) pageParam.get("autoCount"));
        Page page = new Page();
        page.setResult(apiLinkService.listPageSimpleApiInvokeLogs(Long.parseLong(params.get("apiLinkUuid").toString()),
                params.containsKey("apiOperationUuid") ?
                        Long.parseLong(params.get("apiOperationUuid").toString()) : null, pagingInfo));
        page.setPageNo(pagingInfo.getCurrentPage());
        page.setTotalCount(pagingInfo.getTotalCount());
        return ApiResult.success(page);
    }

    @GetMapping("/getApiInvokeLogDetails")
    public ApiResult<ApiInvokeLogEntity> getApiInvokeLogDetails(@RequestParam Long uuid) {
        return ApiResult.success(apiLinkService.getApiInvokeLogDetails(uuid));
    }
}
