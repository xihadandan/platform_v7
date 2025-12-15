package com.wellsoft.pt.app.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.app.entity.AppModuleResSeqEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月06日   chenq	 Create
 * </pre>
 */
@Repository
public class AppModuleResSeqDaoImpl extends AbstractJpaDaoImpl<AppModuleResSeqEntity, Long> {

    @Transactional
    public void updateModuleResSeq(List<AppModuleResSeqEntity> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("moduleId", list.get(0).getModuleId());
            for (AppModuleResSeqEntity seq : list) {
                params.put("seq", seq.getSeq());
                params.put("resUuid", seq.getResUuid());
                params.put("type", seq.getType());
                int row = this.updateByHQL("UPDATE AppModuleResSeqEntity SET seq=:seq, type=:type  where moduleId=:moduleId and resUuid=:resUuid", params);
                if (row == 0) {
                    AppModuleResSeqEntity newSeq = new AppModuleResSeqEntity();
                    newSeq.setModuleId(seq.getModuleId());
                    newSeq.setResUuid(seq.getResUuid());
                    newSeq.setSeq(seq.getSeq());
                    newSeq.setType(seq.getType());
                    save(newSeq);
                }
            }
        }
    }

    public List<AppModuleResSeqEntity> listByModuleId(String moduleId) {
        return listByFieldEqValue("moduleId", moduleId);
    }
}
