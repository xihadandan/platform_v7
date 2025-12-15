package com.wellsoft.pt.workflow.dto;

import com.google.common.collect.Maps;
import com.wellsoft.pt.workflow.entity.OpinionRuleEntity;
import com.wellsoft.pt.workflow.entity.WfOpinionRuleItemEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/5/14.1	    zenghw		2021/5/14		    Create
 * </pre>
 * @date 2021/5/14
 */
public class OpinionRuleIncludeItemDto extends OpinionRuleEntity {

    private List<WfOpinionRuleItemEntity> opinionRuleItemEntitys = new ArrayList<>();

    public List<WfOpinionRuleItemEntity> getOpinionRuleItemEntitys() {
        return opinionRuleItemEntitys;
    }

    public void setOpinionRuleItemEntitys(List<WfOpinionRuleItemEntity> opinionRuleItemEntitys) {
        this.opinionRuleItemEntitys = opinionRuleItemEntitys;
    }

    private Map<String, Map<String, String>> i18n = Maps.newHashMap();

    public Map<String, Map<String, String>> getI18n() {
        return i18n;
    }

    public void setI18n(Map<String, Map<String, String>> i18n) {
        this.i18n = i18n;
    }
}
