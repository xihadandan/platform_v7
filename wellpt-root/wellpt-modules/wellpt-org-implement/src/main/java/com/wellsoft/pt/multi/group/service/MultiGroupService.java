package com.wellsoft.pt.multi.group.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.group.dao.MultiGroupDao;
import com.wellsoft.pt.multi.group.entity.MultiGroup;
import com.wellsoft.pt.multi.group.vo.MultiGroupVo;

import java.util.List;

/**
 * @Auther: yt
 * @Date: 2022/2/10 10:09
 * @Description:
 */
public interface MultiGroupService extends JpaService<MultiGroup, MultiGroupDao, String> {
    void addOrUpdate(MultiGroupVo multiGroupVo);

    void del(List<String> uuids);

    void isEnable(String uuid, Integer isEnable);

    MultiGroupVo get(String uuid);

    MultiGroup getById(String id);

    List<MultiGroup> getEnableList();

    List<MultiGroup> getEnableList(String systemUnitId);

}
