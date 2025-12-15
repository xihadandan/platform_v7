/*
 * @(#)2019年8月16日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.validator;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.regex.RegexUtils;
import com.wellsoft.pt.common.i18n.AppCodeI18nMessageSource;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.utils.FormDataHandler;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.annotation.PostConstruct;
import java.net.IDN;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月16日.1	zhongzh		2019年8月16日		Create
 * </pre>
 * @date 2019年8月16日
 */
public class ValidatorMaps {

    public static final Validator METHOD_REQURIED = new RequiredValiator();
    public static final Validator METHOD_UNIQUE = new UniqueValiator();
    public static final Validator METHOD_CUSTOM = new CustomValiator();
    public static Map<String, Validator> maps = new HashMap<String, Validator>();
    private static Logger logger = Logger.getLogger(ValidatorMaps.class);

    @PostConstruct
    private void init() {
        maps.put("requried", METHOD_REQURIED);

        maps.put("unique", METHOD_UNIQUE);

        maps.put("custom", METHOD_CUSTOM);
    }

    public static class RequiredValiator implements Validator {

        @Override
        public ValidatorResult isValid(Object value, ValidatorFieldContent fieldDefinition, Map<String, Object> params) {
            if (value != null) {
                if (value instanceof Collection && ((Collection) value).isEmpty()) {
                    return new ValidatorResult(false, getValidMessage("formDataValidator.message.notNull", "非空"));
                } else if (value.toString().length() == 0) {
                    return new ValidatorResult(false, getValidMessage("formDataValidator.message.notNull", "非空"));
                }
                return null;
            }
            return new ValidatorResult(false, getValidMessage("formDataValidator.message.notNull", "非空"));
        }
    }

    private static String getValidMessage(String code, String defaultValue) {
        String msg = AppCodeI18nMessageSource.getMessage(code, "pt-dyform", LocaleContextHolder.getLocale().toString());
        return StringUtils.defaultIfBlank(msg, defaultValue);
    }

    private static String getValidMessage(String code, String defaultValue, Map<String, Object> params) {
        String msg = AppCodeI18nMessageSource.getMessage(code, "pt-dyform", LocaleContextHolder.getLocale().toString(), params);
        return StringUtils.defaultIfBlank(msg, defaultValue);
    }

    public static class UniqueValiator extends OptionalValidator implements Validator, ValidatorInit {

        @Override
        public Map<String, Object> init(Object obj) {
            Map<String, Object> params = CollectionHelper.newHashMap();
            params.put("dUtils", obj);
            return params;
        }

        @Override
        protected ValidatorResult isValidValue(Object value, ValidatorFieldContent fieldDefinition,
                                               Map<String, Object> params) {
            DyFormFacade dyFormApiFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
            FormDataHandler dUtils = (FormDataHandler) params.get("dUtils");
            String uuid = (String) dUtils.getValue("uuid");
            String tableName = dUtils.getdUtils().getTblNameOfMainform();
            String fieldName = fieldDefinition.getFieldDefinition().getName();
            boolean isExist = false;
            if (StringUtils.isBlank(uuid)) {
                try {
                    isExist = dyFormApiFacade.queryFormDataExists(tableName, fieldName, value.toString());
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            } else {
                try {
                    isExist = dyFormApiFacade.queryFormDataExists(tableName, fieldName, value.toString(), uuid);
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
            if (isExist) {
                return new ValidatorResult(false, getValidMessage("formDataValidator.message.unique", "唯一"));
            }
            return super.isValidValue(value, fieldDefinition, params);
        }
    }

    public static class CustomValiator extends OptionalValidator implements Validator, ValidatorInit {

        @Override
        public Map<String, Object> init(Object obj) {
            return CollectionHelper.newHashMap();
        }

        @Override
        protected ValidatorResult isValidValue(Object value, ValidatorFieldContent fieldDefinition,
                                               Map<String, Object> params) {
            List<Validator> validators = (List<Validator>) params.get("custom");
            for (Validator validator : validators) {
                ValidatorResult validatorResult = validator.isValid(value, fieldDefinition, params);
                if (validatorResult != null && false == validatorResult.isValid()) {
                    return validatorResult;
                }
            }
            return super.isValidValue(value, fieldDefinition, params);
        }
    }

    public static class MaxLengthValidator extends OptionalValidator implements Validator {

        private int maxLength;

        public MaxLengthValidator(int params) {
            this.maxLength = params;
        }

        @Override
        protected ValidatorResult isValidValue(Object value, ValidatorFieldContent fieldDefinition,
                                               Map<String, Object> params) {
            if (value.toString().length() > maxLength) {
                return new ValidatorResult(false, getValidMessage("formDataValidator.message.maxLengthIs", "最大长度为") + " " + maxLength);
            }
            return super.isValidValue(value, fieldDefinition, params);
        }

    }

    public static class URLValidator extends OptionalValidator implements Validator {
        @Override
        protected ValidatorResult isValidValue(Object value, ValidatorFieldContent fieldDefinition,
                                               Map<String, Object> params) {
            try {
                new java.net.URL(value.toString());
                return super.isValidValue(value, fieldDefinition, params);
            } catch (MalformedURLException e) {
                return new ValidatorResult(false, getValidMessage("formDataValidator.message.InvalidUrl", "非有效的URL"));
            }
        }
    }

    public static class EmailValidator extends OptionalValidator implements Validator {
        private static String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]";
        private static String DOMAIN = ATOM + "+(\\." + ATOM + "+)*";
        private static String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";

        /**
         * Regular expression for the local part of an email address (everything before '@')
         */
        private Pattern localPattern = java.util.regex.Pattern.compile(ATOM + "+(\\." + ATOM + "+)*",
                Pattern.CASE_INSENSITIVE);

        /**
         * Regular expression for the domain part of an email address (everything after '@')
         */
        private Pattern domainPattern = java.util.regex.Pattern.compile(DOMAIN + "|" + IP_DOMAIN,
                Pattern.CASE_INSENSITIVE);

        @Override
        protected ValidatorResult isValidValue(Object value, ValidatorFieldContent fieldDefinition,
                                               Map<String, Object> params) {
            ValidatorResult result = new ValidatorResult(false, getValidMessage("formDataValidator.message.InvalidEmail", "非有效的Email"));
            // split email at '@' and consider local and domain part separately
            String[] emailParts = value.toString().split("@");
            if (emailParts.length != 2) {
                return result;
            }

            // if we have a trailing dot in local or domain part we have an invalid email address.
            // the regular expression match would take care of this, but IDN.toASCII drops trailing the trailing '.'
            // (imo a bug in the implementation)
            if (emailParts[0].endsWith(".") || emailParts[1].endsWith(".")) {
                return result;
            }

            if (!matchPart(emailParts[0], localPattern)) {
                return result;
            }
            if (!matchPart(emailParts[1], domainPattern)) {
                return result;
            }
            return super.isValidValue(value, fieldDefinition, params);
        }

        private boolean matchPart(String part, Pattern pattern) {
            try {
                part = IDN.toASCII(part);
            } catch (IllegalArgumentException e) {
                // occurs when the label is too long (>63, even though it should probably be 64 - see http://www.rfc-editor.org/errata_search.php?rfc=3696,
                // practically that should not be a problem)
                return false;
            }
            Matcher matcher = pattern.matcher(part);
            return matcher.matches();
        }
    }

    public static class IdCardValidator extends OptionalValidator implements Validator {
        @Override
        protected ValidatorResult isValidValue(Object value, ValidatorFieldContent fieldDefinition,
                                               Map<String, Object> params) {
            if (!RegexUtils.isIdCardNumber(value.toString())) {
                return new ValidatorResult(false, getValidMessage("formDataValidator.message.InvalidIdNumber", "非有效身份证"));
            }
            return super.isValidValue(value, fieldDefinition, params);
        }
    }

    public static class TelPhoneValidator extends OptionalValidator implements Validator {
        @Override
        protected ValidatorResult isValidValue(Object value, ValidatorFieldContent fieldDefinition,
                                               Map<String, Object> params) {
            if (!RegexUtils.isTelephone(value.toString())) {
                return new ValidatorResult(false, getValidMessage("formDataValidator.message.InvalidPhoneNumber", "非有效电话号"));
            }
            return super.isValidValue(value, fieldDefinition, params);
        }
    }

    public static class MobilePhoneValidator extends OptionalValidator implements Validator {
        @Override
        protected ValidatorResult isValidValue(Object value, ValidatorFieldContent fieldDefinition,
                                               Map<String, Object> params) {
            if (!RegexUtils.isMobilePhone(value.toString())) {
                return new ValidatorResult(false, "非有效手机号");
            }
            return super.isValidValue(value, fieldDefinition, params);
        }
    }

    public static class DoubleValidator extends OptionalValidator implements Validator {

        private static String FLOAT = "^[-\\+]?\\d+(\\.\\d+)?$";
        private Pattern FLOAT_PATTERN = java.util.regex.Pattern.compile(FLOAT, Pattern.CASE_INSENSITIVE);

        @Override
        protected ValidatorResult isValidValue(Object value, ValidatorFieldContent fieldDefinition,
                                               Map<String, Object> params) {
            if (!FLOAT_PATTERN.matcher(value.toString()).matches()) {
                return new ValidatorResult(false, getValidMessage("formDataValidator.message.mustFloatNumber", "浮点数"));
            }
            return super.isValidValue(value, fieldDefinition, params);
        }
    }

    public static class FloatValidator extends OptionalValidator implements Validator {

        private static String FLOAT = "^[-\\+]?\\d+(\\.\\d+)?$";
        private Pattern FLOAT_PATTERN = java.util.regex.Pattern.compile(FLOAT, Pattern.CASE_INSENSITIVE);

        @Override
        protected ValidatorResult isValidValue(Object value, ValidatorFieldContent fieldDefinition,
                                               Map<String, Object> params) {
            if (!FLOAT_PATTERN.matcher(value.toString()).matches()) {
                return new ValidatorResult(false, getValidMessage("formDataValidator.message.mustFloatNumber", "浮点数"));
            }
            return super.isValidValue(value, fieldDefinition, params);
        }
    }

    public static class IntegerValidator extends OptionalValidator implements Validator {

        private static String INTEGER = "^[-\\+]?\\d+$";
        private Pattern INTEGER_PATTERN = java.util.regex.Pattern.compile(INTEGER, Pattern.CASE_INSENSITIVE);

        @Override
        protected ValidatorResult isValidValue(Object value, ValidatorFieldContent fieldDefinition,
                                               Map<String, Object> params) {
            if (!INTEGER_PATTERN.matcher(value.toString()).matches()) {
                return new ValidatorResult(false, getValidMessage("formDataValidator.message.mustInteger", "整数"));
            }
            return super.isValidValue(value, fieldDefinition, params);
        }
    }

    public static class MaxValidator extends OptionalValidator implements Validator {

        private int max;

        public MaxValidator(int params) {
            this.max = params;
        }

        @Override
        protected ValidatorResult isValidValue(Object value, ValidatorFieldContent fieldDefinition,
                                               Map<String, Object> params) {
            Double num = null;
            try {
                num = Double.parseDouble(value.toString());
            } catch (Exception ex) {

            }
            if (num == null || num > max) {
                return new ValidatorResult(false, getValidMessage("formDataValidator.message.maxNumber", "最大值为") + " " + max);
            }
            return super.isValidValue(value, fieldDefinition, params);
        }

    }

    public static class MinValidator extends OptionalValidator implements Validator {

        private int min;

        public MinValidator(int params) {
            this.min = params;
        }

        @Override
        protected ValidatorResult isValidValue(Object value, ValidatorFieldContent fieldDefinition,
                                               Map<String, Object> params) {
            Double num = null;
            try {
                num = Double.parseDouble(value.toString());
            } catch (Exception ex) {

            }
            if (num == null || num < min) {
                return new ValidatorResult(false, getValidMessage("formDataValidator.message.minNumber", "最小值为") + " " + min);
            }
            return super.isValidValue(value, fieldDefinition, params);
        }

    }

    public static class DictValidator extends OptionalValidator implements Validator {
        private Set<String> sets;

        /**
         * 如何描述该构造方法
         *
         * @param sets
         */
        public DictValidator(Set<String> sets) {
            super();
            this.sets = sets;
        }

        @Override
        protected ValidatorResult isValidValue(Object value, ValidatorFieldContent fieldDefinition,
                                               Map<String, Object> params) {
            String[] values = value.toString().split(Separator.SEMICOLON.getValue());
            for (String o : values) {
                if (sets.contains(o)) {
                    continue;
                }
                Map<String, Object> stringParams = Maps.newHashMap();
                stringParams.put("option", o);
                return new ValidatorResult(false, getValidMessage("formDataValidator.message.optionIsNotValidData", "选项[" + o + "]非合规数据", stringParams));
            }
            return super.isValidValue(value, fieldDefinition, params);
        }

    }

    public static class DateTimeValidator extends OptionalValidator implements Validator {

        private SimpleDateFormat sdf = new SimpleDateFormat();

        private String pattern;


        /**
         *
         */
        public DateTimeValidator(String pattern) {
            super();
            this.pattern = pattern;
        }

        @Override
        protected ValidatorResult isValidValue(Object value, ValidatorFieldContent fieldDefinition,
                                               Map<String, Object> params) {
            if (value instanceof Date) {
                return new ValidatorResult(true);
            }
            String dateTimeString = ObjectUtils.toString(value, StringUtils.EMPTY);
            if (StringUtils.isBlank(dateTimeString)) {
                return new ValidatorResult(true);
            }
            try {
                sdf.applyPattern(pattern);
                sdf.parse(dateTimeString);
                return new ValidatorResult(true);
            } catch (Exception e) {
            }
            Map<String, Object> stringParams = Maps.newHashMap();
            stringParams.put("dateTimeString", dateTimeString);
            stringParams.put("pattern", pattern);
            return new ValidatorResult(false, getValidMessage("formDataValidator.message.datePatternUnparsed", "日期字符串(" + dateTimeString + ")无法通过格式(" + pattern + ")转为日期", stringParams));
        }

    }

}
