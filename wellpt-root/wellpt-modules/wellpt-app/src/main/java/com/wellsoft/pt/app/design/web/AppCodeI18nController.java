package com.wellsoft.pt.app.design.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.entity.AppCodeI18nEntity;
import com.wellsoft.pt.app.entity.AppI18nLocaleEntity;
import com.wellsoft.pt.app.service.AppCodeI18nService;
import com.wellsoft.pt.common.i18n.AppCodeI18nMessageSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年02月11日   chenq	 Create
 * </pre>
 */
@RestController
@RequestMapping("/api/app/codeI18n")
public class AppCodeI18nController extends BaseController {

    @Autowired
    AppCodeI18nService appCodeI18nService;


    @PostMapping("/save")
    public ApiResult<Void> save(@RequestBody List<AppCodeI18nEntity> body) {
        try {
            appCodeI18nService.saveI18nsCode(body);
        } catch (Exception e) {
            return ApiResult.fail(e.getMessage());
        }
        return ApiResult.success();
    }

    @GetMapping("/deleteAppCodeI18nByCodeAndApplyTo")
    public ApiResult<Void> deleteAppCodeI18nByCodeAndApplyTo(@RequestParam String code, @RequestParam String applyTo) {
        appCodeI18nService.deleteAppCodeI18nByCodeAndApplyTo(code, applyTo);
        return ApiResult.success();
    }

    @PostMapping("/deleteAppCodeI18nByUuids")
    public ApiResult<Void> deleteAppCodeI18nByUuids(@RequestBody List<Long> uuids) {
        appCodeI18nService.deleteByUuids(uuids);
        return ApiResult.success();
    }

    @GetMapping("/getAppCodeI18nByApplyTo")
    public ApiResult<List<AppCodeI18nEntity>> getAppCodeI18nByApplyTo(@RequestParam String applyTo) {
        return ApiResult.success(appCodeI18nService.getAppCodeI18nByApplyTo(applyTo));
    }

    @GetMapping("/getAppCodeI18nByCodeAndApplyTo")
    public ApiResult<List<AppCodeI18nEntity>> getAppCodeI18nByCodeAndApplyTo(@RequestParam String code, @RequestParam String applyTo) {
        return ApiResult.success(appCodeI18nService.getAppCodeI18nByCodeAndApplyTo(code, applyTo));
    }

    @GetMapping("/getAllLocales")
    public ApiResult<List<AppI18nLocaleEntity>> getAllLocales() {
        return ApiResult.success(appCodeI18nService.getAllLocales());
    }

    @GetMapping("/getLocaleSortLetters")
    public ApiResult<String> getLocaleSortLetters(@RequestParam(required = false) String locale) {
        return ApiResult.success(appCodeI18nService.getLocaleSortLetters(StringUtils.defaultIfBlank(locale, LocaleContextHolder.getLocale().toString())));
    }

    @GetMapping("/updateCache")
    public ApiResult<Void> updateCache() {
        appCodeI18nService.updateCache();
        return ApiResult.success();
    }

    @GetMapping("/getCodeI18nMessage")
    public ApiResult<String> getCodeI18nMessage(@RequestParam String code, @RequestParam String applyTo, @RequestParam(required = false) String locale) {
        return ApiResult.success(AppCodeI18nMessageSource.getMessage(code, applyTo, locale));
    }
}
