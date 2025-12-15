package com.wellsoft.pt.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Description: 获取签署意见和意见立场对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/9/10.1	    zenghw		2021/9/10		    Create
 * </pre>
 * @date 2021/9/10
 */
@ApiModel("获取签署意见和意见立场对象")
public class GetSignOpinionAndOpinionPositionDto implements Serializable {
    private static final long serialVersionUID = 7719630605874776211L;

    @ApiModelProperty("签署意见")
    private String signOpinion;
    @ApiModelProperty("意见立场-显示值")
    private String opinionPositionLable;
    @ApiModelProperty("意见立场-值")
    private String opinionPositionValue;

    public String getSignOpinion() {
        return this.signOpinion;
    }

    public void setSignOpinion(final String signOpinion) {
        this.signOpinion = signOpinion;
    }

    public String getOpinionPositionLable() {
        return this.opinionPositionLable;
    }

    public void setOpinionPositionLable(final String opinionPositionLable) {
        this.opinionPositionLable = opinionPositionLable;
    }

    public String getOpinionPositionValue() {
        return this.opinionPositionValue;
    }

    public void setOpinionPositionValue(final String opinionPositionValue) {
        this.opinionPositionValue = opinionPositionValue;
    }
}
