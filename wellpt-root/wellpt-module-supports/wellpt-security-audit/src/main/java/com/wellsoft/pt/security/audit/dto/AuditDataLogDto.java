package com.wellsoft.pt.security.audit.dto;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.jdbc.entity.SysEntity;
import com.wellsoft.context.util.web.ServletUtils;
import com.wellsoft.pt.security.audit.entity.AuditDataLogEntity;
import com.wellsoft.pt.security.audit.entity.Role;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.annotations.Cascade;
import org.reflections.ReflectionUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Nullable;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年12月26日   chenq	 Create
 * </pre>
 */
public class AuditDataLogDto extends AuditDataLogEntity {
    private static final long serialVersionUID = 6853342938690466919L;

    private List<AuditDataLogDto> children = Lists.newArrayList();

    private List<AuditDataItemLogDto> dataItems = Lists.newArrayList();

    public AuditDataLogDto dataUuid(String dataUuid) {
        this.setDataUuid(dataUuid);
        return this;
    }

    public AuditDataLogDto name(String name) {
        this.setName(name);
        return this;
    }

    public AuditDataLogDto tableName(String tableName) {
        this.setTableName(tableName);
        return this;
    }

    public AuditDataLogDto modifierName(String modifierName) {
        this.setModifierName(modifierName);
        return this;
    }

    public AuditDataLogDto dataVer(Integer dataVer) {
        this.setDataVer(dataVer);
        return this;
    }

    public AuditDataLogDto category(String category) {
        this.setCategory(category);
        return this;
    }

    public AuditDataLogDto operation(String operation) {
        this.setOperation(operation);
        return this;
    }

    public AuditDataLogDto parentUuid(Long parentUuid) {
        this.setParentUuid(parentUuid);
        return this;
    }

    public AuditDataLogDto remark(String remark) {
        this.setRemark(remark);
        return this;
    }

    public List<AuditDataLogDto> getChildren() {
        return children;
    }

    public void setChildren(List<AuditDataLogDto> children) {
        this.children = children;
    }

    public List<AuditDataItemLogDto> getDataItems() {
        return dataItems;
    }

    public void setDataItems(List<AuditDataItemLogDto> dataItems) {
        this.dataItems = dataItems;
    }

    public AuditDataLogDto diff(Object newObject, Object oldObject, String[] compareField) {
        try {
            List<AuditDataItemLogDto> dataItems = Lists.newArrayList();
            Map<String, Field> newObjFieldMap = Maps.newHashMap();
            Map<String, Field> oldObjectFieldMap = Maps.newHashMap();
            List<String> fieldCodes = Lists.newArrayList();
            Map<String, String> fieldNames = Maps.newHashMap();
            if (ArrayUtils.isNotEmpty(compareField)) {
                fieldCodes.addAll(Lists.newArrayList(compareField));
            }
            ReflectionUtils.getAllFields(newObject.getClass(), new Predicate<Field>() {
                @Override
                public boolean apply(@Nullable Field field) {
                    if (fieldCodes.contains(field.getName())) {
                        field.setAccessible(true);
                        newObjFieldMap.put(field.getName(), field);
                        ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
                        if (apiModelProperty != null) {
                            fieldNames.put(field.getName(), apiModelProperty.value());
                        }
                    }
                    return false;
                }
            });
            if (oldObject != null) {
                ReflectionUtils.getAllFields(oldObject.getClass(), new Predicate<Field>() {
                    @Override
                    public boolean apply(@Nullable Field field) {
                        if (fieldCodes.contains(field.getName())) {
                            field.setAccessible(true);
                            oldObjectFieldMap.put(field.getName(), field);
                        }
                        return false;
                    }
                });
            }
            for (int i = 0, len = fieldCodes.size(); i < len; i++) {
                String code = fieldCodes.get(i);
                String name = fieldNames.get(code);
                if (StringUtils.isBlank(name)) {
                    name = code;
                }
                try {
                    Object newValueObj = newObjFieldMap.get(code).get(newObject);
                    Object oldValueObj = oldObject != null ? oldObjectFieldMap.get(code).get(oldObject) : null;
                    String newValue = this.objectToString(newValueObj);
                    String oldValue = this.objectToString(oldValueObj);
                    if ((newValue != null && oldValue != null && !newValue.equals(oldValue))
                            || (newValue == null && oldValue != null) || (newValue != null && oldValue == null)) {
                        dataItems.add(new AuditDataItemLogDto(name, code, null, newValue, oldValue));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.setDataItems(dataItems);
        } catch (Exception e) {

        }
        return this;
    }

    public AuditDataLogDto diffEntity(JpaEntity newObject, JpaEntity oldObject) {
        if (newObject == null && oldObject == null) {
            return this;
        }
        try {


            List<AuditDataItemLogDto> dataItems = Lists.newArrayList();
            Map<String, Field> newObjFieldMap = Maps.newHashMap();
            Map<String, Field> oldObjectFieldMap = Maps.newHashMap();
            Set<String> fieldCodes = Sets.newLinkedHashSet();
            Map<String, String> fieldNames = Maps.newHashMap();

            fieldCodes.addAll(Lists.newArrayList(JpaEntity.BASE_FIELDS));

            if ((newObject != null && newObject instanceof SysEntity) || (oldObject != null && oldObject instanceof SysEntity)) {
                fieldCodes.addAll(Lists.newArrayList("system", "tenant"));
                fieldNames.put("system", "归属系统");
                fieldNames.put("tenant", "归属租户");
            }
            if (newObject != null) {
                this.dataUuid(newObject.getUuid().toString()).dataVer(newObject.getRecVer());
            } else if (oldObject != null) {
                this.dataUuid(oldObject.getUuid().toString()).dataVer(oldObject.getRecVer());
            }
            Table tableAnnotation = AnnotationUtils.findAnnotation(newObject != null ? newObject.getClass() : oldObject.getClass(), Table.class);
            if (tableAnnotation != null) {
                this.tableName(tableAnnotation.name().toLowerCase());
                this.category(this.getTableName());
            }
            if (StringUtils.isBlank(this.getOperation())) {
                this.operation(oldObject == null ? "create" : "edit");
            }
            if (StringUtils.isBlank(this.getRemark())) {
                this.remark(oldObject == null ? "创建" : "修改");
            }
            if (newObject != null) {
                ReflectionUtils.getAllFields(newObject.getClass(), new Predicate<Field>() {
                    @Override
                    public boolean apply(@Nullable Field field) {
                        String getterMethodName = "get" + StringUtils.capitalize(field.getName());
                        try {
                            Method getterMethod = newObject.getClass().getMethod(getterMethodName);
                            if (getterMethod.isAnnotationPresent(Cascade.class) || getterMethod.isAnnotationPresent(Transient.class)) {
                                return false;
                            }
                        } catch (NoSuchMethodException e) {
                            return false;
                        }

                        field.setAccessible(true);
                        newObjFieldMap.put(field.getName(), field);
                        ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
                        if (apiModelProperty != null) {
                            fieldNames.put(field.getName(), apiModelProperty.value());
                        }
                        fieldCodes.add(field.getName());
                        return false;
                    }
                });
            }
            if (oldObject != null) {
                ReflectionUtils.getAllFields(oldObject.getClass(), new Predicate<Field>() {
                    @Override
                    public boolean apply(@Nullable Field field) {
                        String getterMethodName = "get" + StringUtils.capitalize(field.getName());
                        try {
                            Method getterMethod = oldObject.getClass().getMethod(getterMethodName);
                            if (getterMethod.isAnnotationPresent(Cascade.class) || getterMethod.isAnnotationPresent(Transient.class)) {
                                return false;
                            }
                        } catch (NoSuchMethodException e) {
                            return false;
                        }
                        field.setAccessible(true);
                        oldObjectFieldMap.put(field.getName(), field);
                        fieldCodes.add(field.getName());
                        if (newObject == null && !fieldNames.containsKey(field.getName())) {
                            ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
                            if (apiModelProperty != null) {
                                fieldNames.put(field.getName(), apiModelProperty.value());
                            }
                        }
                        return false;
                    }
                });
            }
            for (String code : fieldCodes) {
                String name = fieldNames.get(code);
                if (StringUtils.isBlank(name)) {
                    name = code;
                }
                try {
                    Object newValueObj = newObject != null ? newObjFieldMap.get(code).get(newObject) : null;
                    Object oldValueObj = oldObject != null ? oldObjectFieldMap.get(code).get(oldObject) : null;
                    String newValue = this.objectToString(newValueObj);
                    String oldValue = this.objectToString(oldValueObj);
                    if ((newValue != null && oldValue != null && !newValue.equals(oldValue))
                            || (newValue == null && oldValue != null) || (newValue != null && oldValue == null)) {
                        dataItems.add(new AuditDataItemLogDto(name, code, null, newValue, oldValue));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.setDataItems(dataItems);

            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            this.setIp(ServletUtils.getRemoteAddr(request));
            if (newObject instanceof SysEntity) {
                this.setSystem(((SysEntity) newObject).getSystem());
                this.setTenant(((SysEntity) newObject).getTenant());
            } else if (request.getAttribute("system_id") != null) {
                this.setSystem(request.getAttribute("system_id").toString());
            }
            this.setCreateTime(new Date());
            this.setModifyTime(this.getCreateTime());

        } catch (Exception e) {

        }
        return this;
    }


    private String objectToString(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Date) {
            return DateFormatUtils.format((Date) obj, "yyyy-MM-dd HH:mm:ss.SSS");
        } else if (obj instanceof Boolean) {
            return BooleanUtils.toString((Boolean) obj, "true", "false");
        } else if (obj instanceof Number) {
            return ((Number) obj).toString();
        }
        return obj.toString();
    }

    public static void main(String[] args) {
        AuditDataLogDto dto = new AuditDataLogDto();
        Role n = new Role("1");
        n.setName("code");
        n.setCreateTime(new Date());
        n.setIssys(true);
        n.setRecVer(200);
        Role o = new Role("2");
        o.setName("code2");
        o.setCreateTime(DateUtils.addDays(new Date(), 10));
        o.setIssys(false);
        o.setRecVer(100);
        dto.diffEntity(n, o);
        System.out.println();
    }


}
