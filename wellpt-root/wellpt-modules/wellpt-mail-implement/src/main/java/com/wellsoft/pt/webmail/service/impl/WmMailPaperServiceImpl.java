/*
 * @(#)2018年3月7日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmMailPaperDto;
import com.wellsoft.pt.webmail.dao.WmMailPaperDao;
import com.wellsoft.pt.webmail.entity.WmMailPaperEntity;
import com.wellsoft.pt.webmail.service.WmMailPaperService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 邮件信纸服务实现类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月7日.1	chenqiong		2018年3月7日		Create
 * </pre>
 * @date 2018年3月7日
 */
@Service
public class WmMailPaperServiceImpl extends AbstractJpaServiceImpl<WmMailPaperEntity, WmMailPaperDao, String> implements
        WmMailPaperService {

    @Override
    @Transactional
    public void updateUserDefaultPaper(WmMailPaperDto dto) {
        // 是否有设置过默认信纸
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", dto.getUserId());
        this.dao.updateByHQL("update WmMailPaperEntity set isDefault=false where isDefault=true and userId=:userId",
                params);
        if (StringUtils.isNotBlank(dto.getUuid())) {// 使用的自己自定义的背景图片
            params.put("uuid", dto.getUuid());
            this.dao.updateByHQL("update WmMailPaperEntity set isDefault=true where userId=:userId and uuid=:uuid",
                    params);
        } else if (StringUtils.isNotBlank(dto.getBackgroundImgUrl())) {
            // 使用的是系统默认的几款背景图片
            params.put("backgroundImgUrl", dto.getBackgroundImgUrl());
            // params.put("backgroundColor", dto.getBackgroundColor());
            int row = this.dao
                    .updateByHQL(
                            "update WmMailPaperEntity set isDefault=true where userId=:userId and backgroundImgUrl=:backgroundImgUrl",
                            params);
            if (row == 0) {// 不存在，则新增一条默认的记录
                WmMailPaperEntity paper = new WmMailPaperEntity();
                paper.setUserId(params.get("userId").toString());
                paper.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
                paper.setBackgroundImgUrl(dto.getBackgroundImgUrl());
                paper.setBackgroundColor(dto.getBackgroundColor());
                paper.setBackgroundPosition(dto.getBackgroundPosition());
                paper.setBackgroundRepeat(dto.getBackgroundRepeat());
                paper.setIsDefault(true);
                this.dao.save(paper);
            }
        }
    }

    @Override
    public WmMailPaperEntity queryUserDefaultPaper(String userId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", SpringSecurityUtils.getCurrentUserId());
        List<WmMailPaperEntity> entities = listByHQL("from WmMailPaperEntity where userId=:userId and isDefault=true",
                params);
        return CollectionUtils.isNotEmpty(entities) ? entities.get(0) : null;
    }

    @Override
    public List<WmMailPaperEntity> querySystemMailPapers() {
        return this.dao.listByHQL("from WmMailPaperEntity where systemUnitId is null order by createTime asc", null);
    }

}
