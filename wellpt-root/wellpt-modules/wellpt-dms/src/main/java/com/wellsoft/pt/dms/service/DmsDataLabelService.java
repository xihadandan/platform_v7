package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.bean.DmsDataLabelDto;
import com.wellsoft.pt.dms.dao.impl.DmsDataLabelDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDataLabelEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 数据标签服务
 *
 * @author chenq
 * @date 2018/6/11
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/11    chenq		2018/6/11		Create
 * </pre>
 */
public interface DmsDataLabelService extends
        JpaService<DmsDataLabelEntity, DmsDataLabelDaoImpl, String> {

    /**
     * 根据用户id、模块id查询标签
     *
     * @param currentUserId
     * @param moduleId
     * @return
     */
    List<DmsDataLabelEntity> queryByUserIdAndModuleId(String currentUserId, String moduleId);

    DmsDataLabelEntity saveDmsDataLabel(DmsDataLabelDto dto);

    void deleteLabelsAndRelaData(ArrayList<String> uuids);
}
