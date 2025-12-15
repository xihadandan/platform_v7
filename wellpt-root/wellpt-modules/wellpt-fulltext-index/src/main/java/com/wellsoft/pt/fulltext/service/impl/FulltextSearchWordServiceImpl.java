/*
 * @(#)7/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.fulltext.dao.FulltextSearchWordDao;
import com.wellsoft.pt.fulltext.dao.FulltextSearchWordTermDao;
import com.wellsoft.pt.fulltext.entity.FulltextSearchWordEntity;
import com.wellsoft.pt.fulltext.entity.FulltextSearchWordTermEntity;
import com.wellsoft.pt.fulltext.service.FulltextSearchWordService;
import com.wellsoft.pt.fulltext.utils.ElasticsearchClientUtil;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 7/25/25.1	    zhulh		7/25/25		    Create
 * </pre>
 * @date 7/25/25
 */
@Service
public class FulltextSearchWordServiceImpl extends AbstractJpaServiceImpl<FulltextSearchWordEntity, FulltextSearchWordDao, Long>
        implements FulltextSearchWordService {

    @Autowired
    private ElasticsearchClientUtil clientUtil;

    @Autowired
    private FulltextSearchWordTermDao fulltextSearchWordTermDao;

    @Override
    @Transactional
    public void addUserSearchWord(String userId, String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return;
        }

        FulltextSearchWordEntity entity = this.getByUserIdAndKeyword(userId, keyword);
        if (entity == null) {
            entity = new FulltextSearchWordEntity();
            entity.setUserId(userId);
            entity.setKeyword(keyword);
            entity.setSearchCount(1l);
            entity.setSystem(RequestSystemContextPathResolver.system());
            entity.setTenant(SpringSecurityUtils.getCurrentTenantId());
        } else {
            entity.setSearchCount(entity.getSearchCount() + 1l);
        }
        this.dao.save(entity);

        List<String> terms = clientUtil.analyze(keyword);
        if (CollectionUtils.size(terms) > 1) {
            terms.add(0, keyword);
        }
        Long searchWordUuid = entity.getUuid();
        fulltextSearchWordTermDao.deleteBySearchUuid(searchWordUuid);
        if (CollectionUtils.isNotEmpty(terms)) {
            List<FulltextSearchWordTermEntity> termEntities = Lists.newArrayList();
            for (int index = 0; index < terms.size(); index++) {
                FulltextSearchWordTermEntity termEntity = new FulltextSearchWordTermEntity();
                termEntity.setSearchWordUuid(searchWordUuid);
                termEntity.setTerm(terms.get(index));
                termEntity.setSortOrder(index);
                termEntities.add(termEntity);
            }
            fulltextSearchWordTermDao.saveAll(termEntities);
        }
    }

    @Override
    public List<String> listHotWordBySystem(String system) {
        if (StringUtils.isBlank(system)) {
            return Lists.newArrayList();
        }

        String hql = new StringBuilder("select t1.term as world,  count(t1.term) as count ")
                .append("from  fulltext_search_word_term t1 left join fulltext_search_word t2 on t1.search_word_uuid = t2.uuid ")
                .append("where t1.create_time > :deadline and t2.system = :system ")
                .append("group by t1.term order by count desc ").toString();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -2);
        Map<String, Object> params = Maps.newHashMap();
        params.put("system", system);
        params.put("deadline", calendar.getTime());
        List<QueryItem> queryItems = this.dao.listQueryItemBySQL(hql, params, new PagingInfo(1, 50));
        List<String> words = queryItems.stream().map(item -> Objects.toString(item.get("world")))
                .filter(world -> StringUtils.isNotBlank(world) && StringUtils.length(world) > 1)
                .collect(Collectors.toList());
        // 去除包含关系的词
        Collections.reverse(words);
        words.removeIf(word -> {
            boolean startCheck = false;
            for (String term : words) {
                if (StringUtils.equals(word, term)) {
                    startCheck = true;
                    continue;
                }
                if (startCheck && StringUtils.contains(term, word)) {
                    return true;
                }
            }
            return false;
        });
        Collections.reverse(words);
        return CollectionUtils.size(words) <= 10 ? words : words.subList(0, 10);
    }

    @Override
    public List<String> listUserSearchHistory(String userId, String system) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(system)) {
            return Lists.newArrayList();
        }
        
        String hql = "select t.keyword as keyword from FulltextSearchWordEntity t where t.userId = :userId and t.system = :system order by t.createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("system", system);
        return this.dao.listCharSequenceByHqlAndPage(hql, params, new PagingInfo(1, 20));
    }

    @Override
    public List<String> listUserCommonSearch(String userId, String system) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(system)) {
            return Lists.newArrayList();
        }

        String hql = "select t.keyword as keyword from FulltextSearchWordEntity t where t.userId = :userId and t.system = :system order by t.searchCount desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("system", system);
        return this.dao.listCharSequenceByHqlAndPage(hql, params, new PagingInfo(1, 20));
    }

    @Override
    public List<String> listKeywordLike(String keyword, String system) {
        if (StringUtils.isBlank(keyword) || StringUtils.isBlank(system)) {
            return Lists.newArrayList();
        }

        String hql = "select distinct t1.term as term from FulltextSearchWordTermEntity t1 where t1.term like :keyword || '%' and exists (select 1 from FulltextSearchWordEntity t2 where t1.searchWordUuid = t2.uuid and t2.system = :system)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("keyword", keyword);
        params.put("system", system);
        return this.dao.listCharSequenceByHqlAndPage(hql, params, new PagingInfo(1, 10));
    }

    private FulltextSearchWordEntity getByUserIdAndKeyword(String userId, String keyword) {
        FulltextSearchWordEntity entity = new FulltextSearchWordEntity();
        entity.setUserId(userId);
        entity.setKeyword(keyword);
        entity.setSystem(RequestSystemContextPathResolver.system());
        List<FulltextSearchWordEntity> entities = this.dao.listByEntity(entity);
        return CollectionUtils.isEmpty(entities) ? null : entities.get(0);
    }

}
