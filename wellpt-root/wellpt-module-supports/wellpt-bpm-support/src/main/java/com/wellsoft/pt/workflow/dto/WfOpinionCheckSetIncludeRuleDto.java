package com.wellsoft.pt.workflow.dto;

import com.wellsoft.pt.workflow.entity.WfOpinionCheckSetEntity;

/**
 * Description: 签署意见规则配置包含规则详情
 *
 * @author
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/5/18.1	    zenghw		2021/5/18		    Create
 * </pre>
 * @date 2021/5/18
 */
public class WfOpinionCheckSetIncludeRuleDto extends WfOpinionCheckSetEntity {

    private OpinionRuleIncludeItemDto opinionRuleDto;

    public OpinionRuleIncludeItemDto getOpinionRuleDto() {
        return opinionRuleDto;
    }

    public void setOpinionRuleDto(OpinionRuleIncludeItemDto opinionRuleDto) {
        this.opinionRuleDto = opinionRuleDto;
    }
}
