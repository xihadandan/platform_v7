package com.wellsoft.pt.bot.facade.service.impl;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.bot.dto.BotRuleConfDto;
import com.wellsoft.pt.bot.entity.BotRuleConfEntity;
import com.wellsoft.pt.bot.facade.service.BotRuleConfFacadeService;
import com.wellsoft.pt.bot.service.BotRuleConfService;
import com.wellsoft.pt.bot.service.BotRuleObjMappingService;
import com.wellsoft.pt.bot.service.BotRuleObjRelaMappingService;
import com.wellsoft.pt.bot.service.BotRuleObjRelaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/9/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/14    chenq		2018/9/14		Create
 * </pre>
 */
@Service
public class BotRuleConfFacadeServiceImpl extends AbstractApiFacade implements
        BotRuleConfFacadeService {

    @Autowired
    BotRuleConfService botRuleConfService;

    @Autowired
    BotRuleObjMappingService botRuleObjMappingService;

    @Autowired
    BotRuleObjRelaService botRuleObjRelaService;

    @Autowired
    BotRuleObjRelaMappingService botRuleObjRelaMappingService;

    @Override
    public void saveBotRuleConfig(BotRuleConfDto dto) {
        botRuleConfService.save(dto);
    }

    @Override
    public void deleteRuleConfig(List<String> uuids) {
        botRuleConfService.deleteBotRuleConf(uuids);
    }


    @Override
    public BotRuleConfDto getBotRuleConfigDetail(String uuid) {
        return botRuleConfService.getDetailByUuid(uuid);

    }

    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        String type = queryInfo.getOtherParams("transferType", "");
        List<BotRuleConfEntity> botRuleConfEntities = botRuleConfService.listByTransferType(
                StringUtils.isNotBlank(type) ? Integer.parseInt(type) : null);
        return new Select2QueryData(botRuleConfEntities, "id", "ruleName",
                queryInfo.getPagingInfo());
    }

    @Override
    public List<BotRuleConfEntity> listAll() {
        return botRuleConfService.listAll();
    }


    public Select2QueryData querySelectDataFromBotRuleConfig(Select2QueryInfo select2QueryInfo) {

        String queryValue = select2QueryInfo.getSearchValue();
        List<BotRuleConfEntity> rules = null;
        if (StringUtils.isBlank(queryValue)) {
            rules = botRuleConfService.listAll();
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", "%" + queryValue + "%");
            rules = botRuleConfService.listByHQL("from BotRuleConfEntity where ruleName like :name",
                    map);
        }

        List<Select2DataBean> beans = new ArrayList<Select2DataBean>();
        for (BotRuleConfEntity rule : rules) {
            beans.add(new Select2DataBean(rule.getUuid(), rule.getRuleName()));
        }
        return new Select2QueryData(beans);

    }

    public Select2QueryData loadSelectDataFromBotRuleConfig(Select2QueryInfo select2QueryInfo) {
        String[] uuids = select2QueryInfo.getIds();
        if (uuids.length == 0) {
            return new Select2QueryData();
        }

        List<Select2DataBean> beans = new ArrayList<Select2DataBean>();
        BotRuleConfEntity rule = botRuleConfService.getOne(uuids[0]);
        beans.add(new Select2DataBean(rule.getId(), rule.getRuleName()));
        return new Select2QueryData(beans);
    }


}
