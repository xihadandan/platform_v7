package com.wellsoft.pt.common.i18n.service;

import com.wellsoft.pt.common.i18n.dao.I18nEntityDao;
import com.wellsoft.pt.common.i18n.entity.I18nEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Collection;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年03月25日   chenq	 Create
 * </pre>
 */
public interface DataI18nService extends JpaService<I18nEntity, I18nEntityDao, Long> {

    <T extends I18nEntity> T getByDataIdAndCodeAndLocale(Class<T> type, String dataId, String dataCode, String locale);

    <T extends I18nEntity> T getByDataUuidAndCodeAndLocale(Class<T> type, Long dataUuid, String dataCode, String locale);

    <T extends I18nEntity> List<T> listByDataUuids(Class<T> type, Collection<Long> dataUuids, String locale);

    <T extends I18nEntity> List<T> listByDataIds(Class<T> type, List<String> dataIds, String locale);

    <T extends I18nEntity> List<T> listByDataUuidsAndCode(Class<T> type, List<Long> dataUuids, String dataCode);

    <T extends I18nEntity> List<T> listByDataIdsCodeLocale(Class<T> type, List<String> dataIds, String dataCode, String locale);

    <T extends I18nEntity> List<T> listByDataUuidsCodeLocale(Class<T> type, List<Long> dataUuids, String dataCode, String locale);

    <T extends I18nEntity> List<T> listByDataIdsAndCode(Class<T> type, List<String> dataIds, String dataCode);

    void deleteByDataUuid(Long dataUuid, Class<? extends I18nEntity> type);

    void deleteByDataUuids(List<Long> dataUuid, Class<? extends I18nEntity> type);

    void deleteByDataId(String dataId, Class<? extends I18nEntity> type);

    void deleteByDataIdAndCodeAndLocale(String dataId, String dataCode, String locale, Class<? extends I18nEntity> type);

    void saveAll(String dataId, List<? extends I18nEntity> list, Class<? extends I18nEntity> type);

    void saveAll(Long dataUuid, List<? extends I18nEntity> list, Class<? extends I18nEntity> type);


}
