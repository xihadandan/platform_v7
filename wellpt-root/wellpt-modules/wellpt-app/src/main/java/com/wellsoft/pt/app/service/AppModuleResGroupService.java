package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.impl.AppModuleResGroupDaoImpl;
import com.wellsoft.pt.app.entity.AppModuleResGroupEntity;
import com.wellsoft.pt.app.entity.AppModuleResGroupMemberEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

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
public interface AppModuleResGroupService extends JpaService<AppModuleResGroupEntity, AppModuleResGroupDaoImpl, Long> {
    List<AppModuleResGroupEntity> listByModuleId(String moduleId);


    void updateModuleResGroupMem(Long memUuid, Long groupUuid, String type, Long memberUuidUpdated);

    List<AppModuleResGroupMemberEntity> listModuleResGrpMember(String moduleId);

    void deleteGroup(Long uuid);

    void rename(Long uuid, String name);

    void addMember(Long memberUuid, Long afterMemberUuid,String type);

    List<AppModuleResGroupMemberEntity> listResGroupMemberByGroup(Long uuid);
}
