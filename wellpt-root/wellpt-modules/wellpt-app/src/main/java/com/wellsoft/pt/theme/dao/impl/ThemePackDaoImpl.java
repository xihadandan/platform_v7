package com.wellsoft.pt.theme.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.theme.entity.ThemePackEntity;
import com.wellsoft.pt.theme.entity.ThemePackTagEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年06月19日   Qiong	 Create
 * </pre>
 */
@Repository
public class ThemePackDaoImpl extends AbstractJpaDaoImpl<ThemePackEntity, Long> {
    public List<Long> getThemePackTagUuids(Long uuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        List<Long> uuids = this.listNumberByHQL("select tagUuid from ThemePackTagEntity where packUuid=:uuid", params);
        return uuids;
    }

    public void deleteThemePackTag(Long uuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        this.deleteByHQL(" delete from ThemePackTagEntity where packUuid=:uuid", params);
    }

    public void savePackTags(List<ThemePackTagEntity> packTagEntities) {
        for (ThemePackTagEntity packTagEntity : packTagEntities) {
            getSession().saveOrUpdate(packTagEntity);
        }
    }
}
