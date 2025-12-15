package com.wellsoft.oauth2.web.search;

import com.google.common.base.Throwables;
import com.wellsoft.oauth2.utils.ServletUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import javax.servlet.ServletRequest;
import java.text.ParseException;

public class DynamicSpecifications {
    /**
     * 动态查询，参数前缀
     */
    public final static String SEARCH_PREFIX = "search_";
    private static final Logger logger = LoggerFactory.getLogger(DynamicSpecifications.class);
    private static final String SHORT_DATE = "yyyy-MM-dd";
    private static final String SLASH_SHORT_DATE = "yyyy/MM/dd";
    private static final String LONG_DATE = "yyyy-MM-dd HH:mm:ss";
    private static final String SLASH_LONG_DATE = "yyyy/MM/dd HH:mm:ss";
    private static final String TIME = "HH:mm:ss";

    public static Collection<WebSearchFilter> genSearchFilter(ServletRequest request) {
        Map<String, Object> searchParams = ServletUtils.getParametersStartingWith(request,
                DynamicSpecifications.SEARCH_PREFIX);
        Map<String, WebSearchFilter> filters = WebSearchFilter.parse(searchParams);
        return filters.values();
    }

    public static <T> Specification<T> bySearchFilter(ServletRequest request,
                                                      final Class<T> entityClazz,
                                                      final Collection<WebSearchFilter> searchFilters) {
        return bySearchFilter(request, entityClazz, searchFilters.toArray(new WebSearchFilter[]{}));
    }

    public static <T> Specification<T> bySearchFilter(ServletRequest request,
                                                      final Class<T> entityClazz,
                                                      final WebSearchFilter... searchFilters) {
        Collection<WebSearchFilter> filters = genSearchFilter(request);
        Set<WebSearchFilter> set = new HashSet<WebSearchFilter>(filters);
        for (WebSearchFilter searchFilter : searchFilters) {
            set.add(searchFilter);
        }
        return bySearchFilter(entityClazz, set);
    }

    public static <T> Specification<T> bySearchFilter(final Class<T> entityClazz,
                                                      final Collection<WebSearchFilter> searchFilters) {
        final Set<WebSearchFilter> filterSet = new HashSet<WebSearchFilter>();
        // 自定义
        for (WebSearchFilter searchFilter : searchFilters) {
            filterSet.add(searchFilter);
        }

        return new Specification<T>() {
            @SuppressWarnings({"rawtypes"})
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
                                         CriteriaBuilder builder) {
                if (filterSet != null && !filterSet.isEmpty()) {
                    List<Predicate> predicates = new ArrayList<Predicate>();
                    for (WebSearchFilter filter : filterSet) {
                        // nested path translate, 如Task的名为"user.name"的filedName, 转换为Task.user.name属性
                        String[] names = StringUtils.split(filter.getFieldName(), ".");
                        Path expression = root.get(names[0]);
                        for (int i = 1; i < names.length; i++) {
                            expression = expression.get(names[i]);
                        }

                        // 自动进行enum和date的转换。
                        Class clazz = expression.getJavaType();
                        if (Date.class.isAssignableFrom(
                                clazz) && !filter.getValue().getClass().equals(clazz)) {
                            filter.setValue(convert2Date((String) filter.getValue()));
                        } else if (Enum.class.isAssignableFrom(
                                clazz) && !filter.getValue().getClass().equals(clazz)) {
                            filter.setValue(convert2Enum(clazz, (String) filter.getValue()));
                        } else if (Boolean.class.isAssignableFrom(
                                clazz) && !filter.getValue().getClass().equals(clazz)) {
                            filter.setValue(Boolean.valueOf(filter.getValue().toString()));
                        } else if (boolean.class.isAssignableFrom(clazz)
                                && !filter.getValue().getClass().equals(clazz)) {
                            filter.setValue(Boolean.valueOf(filter.getValue().toString()));
                        }

                        // logic operator
                        switch (filter.getOperator()) {
                            case EQ:
                                predicates.add(builder.equal(expression, filter.getValue()));
                                break;
                            case LIKE:
                                predicates.add(
                                        builder.like(expression, "%" + filter.getValue() + "%"));
                                break;
                            case GT:
                                predicates.add(builder.greaterThan(expression,
                                        (Comparable) filter.getValue()));
                                break;
                            case LT:
                                predicates.add(builder.lessThan(expression,
                                        (Comparable) filter.getValue()));
                                break;
                            case GTE:
                                predicates.add(builder.greaterThanOrEqualTo(expression,
                                        (Comparable) filter.getValue()));
                                break;
                            case LTE:
                                predicates.add(builder.lessThanOrEqualTo(expression,
                                        (Comparable) filter.getValue()));
                                break;
                            case IN:
                                predicates.add(
                                        builder.and(expression.in((Object[]) filter.getValue())));
                                break;
                            case NOTNULL:
                                if (Boolean.valueOf(filter.getValue().toString())) {
                                    predicates.add(builder.isNotNull(expression));
                                } else {
                                    predicates.add(builder.isNull(expression));
                                }
                                break;
                            case ISNULL:
                                if (Boolean.valueOf(filter.getValue().toString())) {
                                    predicates.add(builder.isNull(expression));
                                } else {
                                    predicates.add(builder.isNotNull(expression));
                                }
                        }
                    }

                    // 将所有条件用 and 联合起来
                    if (predicates.size() > 0) {
                        return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                    }
                }

                return builder.conjunction();
            }
        };
    }

    private static Date convert2Date(String dateString) {
        try {
            return DateUtils.parseDate(dateString, SHORT_DATE, LONG_DATE, SLASH_SHORT_DATE,
                    SLASH_LONG_DATE, TIME);
        } catch (ParseException e) {
            logger.error("Convert time is error! The dateString is " + dateString + "."
                    + Throwables.getStackTraceAsString(e));
        }

        return null;
    }

    private static <E extends Enum<E>> E convert2Enum(Class<E> enumClass, String enumString) {
        return EnumUtils.getEnum(enumClass, enumString);
    }
}