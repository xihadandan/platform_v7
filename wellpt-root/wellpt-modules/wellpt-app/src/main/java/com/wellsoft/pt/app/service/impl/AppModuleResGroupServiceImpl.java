package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.app.dao.impl.AppModuleResGroupDaoImpl;
import com.wellsoft.pt.app.dao.impl.AppModuleResGroupMemberDaoImpl;
import com.wellsoft.pt.app.dao.impl.AppModuleResSeqDaoImpl;
import com.wellsoft.pt.app.entity.AppModuleResGroupEntity;
import com.wellsoft.pt.app.entity.AppModuleResGroupMemberEntity;
import com.wellsoft.pt.app.entity.AppModuleResSeqEntity;
import com.wellsoft.pt.app.service.AppModuleResGroupService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
@Service
public class AppModuleResGroupServiceImpl extends AbstractJpaServiceImpl<AppModuleResGroupEntity, AppModuleResGroupDaoImpl, Long> implements AppModuleResGroupService {

    @Autowired
    AppModuleResGroupMemberDaoImpl memberDao;

    @Autowired
    AppModuleResSeqDaoImpl seqDao;

    @Override
    public List<AppModuleResGroupEntity> listByModuleId(String moduleId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("moduleId", moduleId);
        return this.dao.listByHQL("from AppModuleResGroupEntity where moduleId=:moduleId order by seq asc", params);
    }

    @Override
    public List<AppModuleResGroupMemberEntity> listModuleResGrpMember(String moduleId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("moduleId", moduleId);
        return memberDao.listByHQL("from AppModuleResGroupMemberEntity m where exists (" +
                " select 1 from AppModuleResGroupEntity g where g.moduleId=:moduleId and g.uuid = m.groupUuid" +
                ")", params);
    }

    @Override
    @Transactional
    public void deleteGroup(Long uuid) {
        delete(uuid);
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        List<AppModuleResGroupMemberEntity> memberEntities = memberDao.listByFieldEqValue("groupUuid", uuid);
        List<String> resUuids = Lists.newArrayList(uuid.toString());
        for (AppModuleResGroupMemberEntity mem : memberEntities) {
            resUuids.add(mem.getMemberUuid().toString());
        }
        params.put("resUuids", resUuids);
        memberDao.deleteByHQL("delete from AppModuleResGroupMemberEntity where groupUuid=:uuid", params);
        seqDao.deleteByHQL("delete from AppModuleResSeqEntity where resUuid in :resUuids", params);

    }

    @Override
    @Transactional
    public void rename(Long uuid, String name) {
        AppModuleResGroupEntity group = getOne(uuid);
        if (group != null) {
            group.setName(name);
            save(group);
        }
    }

    @Override
    @Transactional
    public void addMember(Long memberUuid, Long afterMemberUuid, String type) {
        AppModuleResGroupMemberEntity memberEntity = memberDao.getOneByFieldEq("memberUuid", afterMemberUuid);
        if (memberEntity != null) {
            AppModuleResGroupMemberEntity newMember = new AppModuleResGroupMemberEntity();
            newMember.setGroupUuid(memberEntity.getGroupUuid());
            newMember.setType(StringUtils.isNotBlank(type) ? type : memberEntity.getType());
            newMember.setMemberUuid(memberUuid);
            memberDao.save(newMember);
            AppModuleResSeqEntity seqEntity = seqDao.getOneByFieldEq("resUuid", afterMemberUuid.toString());
            if (seqEntity != null) {
                Map<String, Object> params = Maps.newHashMap();
                params.put("seq", seqEntity.getSeq());
                params.put("type", seqEntity.getType());
                params.put("moduleId", seqEntity.getModuleId());
                seqDao.updateByHQL("update AppModuleResSeqEntity set seq=seq+1 where seq>:seq and moduleId=:moduleId and type=:type", params);
                AppModuleResSeqEntity seq = new AppModuleResSeqEntity();
                seq.setSeq(seqEntity.getSeq() + 1);
                seq.setType(StringUtils.isNotBlank(type) ? type : seqEntity.getType());
                seq.setModuleId(seqEntity.getModuleId());
                seq.setResUuid(memberUuid.toString());
                seqDao.save(seq);
            }

        }
    }

    @Override
    public List<AppModuleResGroupMemberEntity> listResGroupMemberByGroup(Long uuid) {
        return memberDao.listByFieldEqValue("groupUuid", uuid);
    }

    @Override
    @Transactional
    public void updateModuleResGroupMem(Long memUuid, Long groupUuid, String type, Long newMemUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("memberUuid", memUuid);
        params.put("groupUuid", groupUuid);
        if (groupUuid == null && newMemUuid == null) {
            // 如果不指定分组，则代表要删除该成员关系
            memberDao.deleteByHQL("delete from AppModuleResGroupMemberEntity where  memberUuid=:memberUuid", params);
        } else {
            // 更新成员关系
            AppModuleResGroupMemberEntity memberEntity = memberDao.getOneByHQL("from AppModuleResGroupMemberEntity where memberUuid=:memberUuid  ", params);
            if (memberEntity == null) {
                if (groupUuid != null && type != null) {
                    // 新增
                    memberEntity = new AppModuleResGroupMemberEntity();
                    memberEntity.setMemberUuid(memUuid);
                    memberEntity.setType(type);
                    memberEntity.setGroupUuid(groupUuid);
                    memberDao.save(memberEntity);
                }

            } else {
                // 更新
                if (groupUuid != null) {
                    memberEntity.setGroupUuid(groupUuid);
                }
                if (StringUtils.isNotBlank(type)) {
                    memberEntity.setType(type);
                }
                if (newMemUuid != null) {
                    memberEntity.setMemberUuid(newMemUuid);
                }
                memberDao.save(memberEntity);
            }

        }


    }
}
