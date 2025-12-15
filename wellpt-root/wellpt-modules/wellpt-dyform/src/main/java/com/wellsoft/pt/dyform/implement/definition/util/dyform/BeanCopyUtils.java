package com.wellsoft.pt.dyform.implement.definition.util.dyform;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;

public class BeanCopyUtils extends BeanUtilsBean {
	/*public static void copyProperties(Object src, Object dest) throws Exception {
		Class srcClazz = src.getClass();
		Class destClazz = dest.getClass();
		if (srcClazz != destClazz) {
			throw new Exception("class type is different between src and dest");
		}
		Method[] mehtods = srcClazz.getDeclaredMethods();
		for (Method method : mehtods) {
			String methodName = method.getName();

			char isUpperCase = '0';
			String fieldName = "";
			if (!methodName.startsWith("get") && !methodName.startsWith("is")) {
				continue;
			}

			if (methodName.startsWith("get")) {
				if (methodName.length() == 3) {
					continue;
				}
				isUpperCase = methodName.charAt(3);
				fieldName = methodName.substring(3);
			}

			if (methodName.startsWith("is")) {
				if (methodName.length() == 2) {
					continue;
				}
				isUpperCase = methodName.charAt(2);
				fieldName = methodName.substring(2);
			}

			if (!Character.isUpperCase(isUpperCase)) {
				continue;
			}

			Object value = method.invoke(src, new Object[0]);
			if (value != null) {
				Method setMehtod = destClazz.getDeclaredMethod("set" + fieldName, value.getClass());
				setMehtod.invoke(dest, new Object[] { value });
			}
		}
	}*/

    @Override
    public void copyProperty(Object dest, String name, Object value) throws IllegalAccessException,
            InvocationTargetException {
        if (value == null)
            return;
        super.copyProperty(dest, name, value);
    }

}
