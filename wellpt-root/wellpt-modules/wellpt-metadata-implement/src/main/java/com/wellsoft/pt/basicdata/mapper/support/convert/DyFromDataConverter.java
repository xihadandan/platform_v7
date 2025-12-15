/*
 * @(#)2017年10月13日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper.support.convert;

import com.wellsoft.context.util.encode.JsonBinder;
import com.wellsoft.pt.basicdata.mapper.MapperException;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import net.sf.json.JSONObject;
import org.dozer.CustomConverter;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年10月13日.1	zhongzh		2017年10月13日		Create
 * </pre>
 * @date 2017年10月13日
 */
public class DyFromDataConverter implements CustomConverter {

    public Object convert(Object destination, Object source, Class<?> destClass, Class<?> sourceClass) {
        if (source == null) {
            return null;
        }
        DyFormData dest = null;
        if (source instanceof String) {
            // check to see if the object already exists
            dest = JsonBinder.buildNormalBinder().fromJson((String) source, DyFormData.class);
            if (destination != null && destination instanceof DyFormData) {
                DyFormData dest2 = ((DyFormData) destination);
                dest2.setFormDatas(dest.getFormDatas(), true);
                dest = dest2;
            }
            return dest;
        } else if (source instanceof DyFormData) {
            return JSONObject.fromObject(source).toString();
        }
        throw new MapperException("Converter DyFromCustomConverter " + "used incorrectly. Arguments passed in were:"
                + destination + " and " + source);
    }
}
