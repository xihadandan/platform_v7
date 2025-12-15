package com.wellsoft.pt.message.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.message.entity.MessageClassify;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yt
 * @title: MessageClassifyIexportDataProvider
 * @date 2020/10/21 10:25
 */
@Service
@Transactional(readOnly = true)
public class MessageClassifyIexportDataProvider extends AbstractIexportDataProvider<MessageClassify, String> {


    @Override
    public String getType() {
        return IexportType.MessageClassify;
    }

    @Override
    public IexportData getData(String uuid) {
        return null;
    }

    @Override
    public String getTreeName(MessageClassify classify) {
        return "消息模板分类：" + classify.getName();
    }

    @Override
    public Map<String, List<MessageClassify>> getParentMapList(ProtoDataHql protoDataHql) {
        List<MessageClassify> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), MessageClassify.class);
        Map<String, List<MessageClassify>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.MessageTemplate)) {
            for (MessageClassify messageClassify : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + messageClassify.getUuid();
                this.putParentMap(map, messageClassify, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
