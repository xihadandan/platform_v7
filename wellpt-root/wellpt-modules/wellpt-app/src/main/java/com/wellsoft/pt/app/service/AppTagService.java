package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.AppTagDao;
import com.wellsoft.pt.app.entity.AppTagEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月28日   chenq	 Create
 * </pre>
 */
public interface AppTagService extends JpaService<AppTagEntity, AppTagDao, Long> {

    Long createTag(String name, String applyTo);

    void saveDataTag(List<String> dataIds, List<Long> tagUuids);

    void deleteDataTag(String dataId, Long tagUuid);

    List<AppTagEntity> getTagsByDataId(String dataId);

    List<AppTagEntity> getTagsByApplyTo(String applyTo);

    void deleteTag(Long uuid);

    void deleteUnUsedTag();
}
