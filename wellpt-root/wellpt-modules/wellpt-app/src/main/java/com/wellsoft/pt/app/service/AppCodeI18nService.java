package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.AppCodeI18nDao;
import com.wellsoft.pt.app.entity.AppCodeI18nEntity;
import com.wellsoft.pt.app.entity.AppI18nLocaleEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;
import java.util.Set;

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
public interface AppCodeI18nService extends JpaService<AppCodeI18nEntity, AppCodeI18nDao, Long> {


    void deleteAppCodeI18nByCodeAndApplyTo(String code, String applyTo);

    List<AppCodeI18nEntity> getAppCodeI18nByCodeAndApplyTo(String code, String applyTo);

    AppCodeI18nEntity getLocaleAppCodeI18nByCodeAndApplyTo(String code, String applyTo, String locale);


    List<AppCodeI18nEntity> getAppCodeI18nByApplyTo(String applyTo);

    List<AppI18nLocaleEntity> getAllLocales();

    Set<String> getAllLocaleString();

    void updateCache();

    void saveI18nsCode(List<AppCodeI18nEntity> list);

    String getLocaleSortLetters(String defaultIfBlank);

    void addLocale(AppI18nLocaleEntity locale);

    AppI18nLocaleEntity getLocale(String locale);

    void translateAllToLocale(String fromLocale, String toLocale);

    long countWordsToTranslate(String fromLocale);
}
