package com.wellsoft.pt.dyform.implement.definition.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dyform.implement.definition.entity.FormCommonFieldRef;

import java.util.Collection;
import java.util.List;

/**
 * Description: 表单公共字段引用信息service接口
 *
 * @author qiufy
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年4月23日.1	qiufy		2019年4月23日		Create
 * </pre>
 * @date 2019年4月23日
 */
public interface FormCommonFieldRefService extends BaseService {
    /**
     * 获取关联表单数据
     *
     * @param fieldUuid 表单公共字段字义DYFORM_COMMON_FIELD_DEFINITION的uuid
     * @return
     */
    List<FormCommonFieldRef> getFormCommonFieldByFieldUuid(String fieldUuid);

    public abstract Long countRefsByFieldUuid(String fieldUuid);

    public abstract void saveRefs(String formUuid, Collection<String> refIds);

}
