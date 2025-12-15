package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.AppDefElementI18nDao;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年03月04日   chenq	 Create
 * </pre>
 */
public interface AppDefElementI18nService extends JpaService<AppDefElementI18nEntity, AppDefElementI18nDao, Long> {

    void deleteAllI18n(String elementId, String defId, BigDecimal version, String applyTo);

    void deleteAllCodeI18n(String code, String defId, BigDecimal version, String applyTo);


    List<AppDefElementI18nEntity> getI18ns(String defId, String elementId, BigDecimal version, String applyTo);

    List<AppDefElementI18nEntity> getI18ns(Set<String> defId, String applyTo, String code, String locale);

    List<AppDefElementI18nEntity> getI18ns(Set<String> defId, String applyTo, String code, BigDecimal version, String locale);

    List<AppDefElementI18nEntity> getI18ns(String defId, String elementId, BigDecimal version, String applyTo, String locale);

    List<AppDefElementI18nEntity> getI18ns(String defId, String elementId, String code, BigDecimal version, String applyTo, String locale);

    AppDefElementI18nEntity getI18n(String defId, String elementId, String code, BigDecimal version, String applyTo, String locale);


}
