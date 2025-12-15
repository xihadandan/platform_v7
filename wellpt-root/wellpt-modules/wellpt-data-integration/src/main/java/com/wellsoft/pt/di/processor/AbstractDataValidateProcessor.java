package com.wellsoft.pt.di.processor;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/15    chenq		2019/8/15		Create
 * </pre>
 */
public abstract class AbstractDataValidateProcessor extends AbstractDIProcessor {


    public abstract Validation validate(Object o);

    @Override
    void action(Object o) {
        Validation validation = validate(o);
        if (!validation.isSuccess()) {
            faultStop(validation.msg());
        }

    }


    public static class Validation extends CaseInsensitiveMap {

        public Validation fail(String failMsg) {
            this.put("msg", failMsg);
            this.put("success", false);
            return this;
        }

        public Validation success() {
            this.put("success", true);
            return this;
        }

        public boolean isSuccess() {
            return (Boolean) this.get("success");
        }

        public String msg() {
            return (String) this.get("msg");
        }


    }
}
