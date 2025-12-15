/*
 * @(#)2016年5月20日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.form.assembler;

import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import org.springframework.core.Ordered;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年5月20日.1	zhulh		2016年5月20日		Create
 * </pre>
 * @date 2016年5月20日
 */
public interface TaskFormOpinionAssembler extends Ordered {

    /**
     * @return
     */
    String getName();

    /**
     * @param flowInstUuid
     * @param taskInstUuid
     * @param dyFormData
     * @param fieldName
     * @param appendedOpinion
     * @param contentOrigin   历史内容来源 1：流程信息记录，2：表单字段值
     * @return
     */
    String assemble(String flowInstUuid, String taskInstUuid, DyFormData dyFormData, String fieldName,
                    String appendedOpinion, String contentOrigin);

}
