package com.wellsoft.pt.basicdata.serialnumber.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.CommonValidateService;
import com.wellsoft.context.util.date.DateUtil;
import com.wellsoft.pt.basicdata.serialnumber.bean.SerialNumberBean;
import com.wellsoft.pt.basicdata.serialnumber.bean.SerialNumberMaintainBean;
import com.wellsoft.pt.basicdata.serialnumber.bean.SerialNumberSupplementBean;
import com.wellsoft.pt.basicdata.serialnumber.dao.SerialNumberDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumber;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberMaintain;
import com.wellsoft.pt.basicdata.serialnumber.facade.service.SerialNumberFacadeService;
import com.wellsoft.pt.basicdata.serialnumber.service.SerialNumberMaintainService;
import com.wellsoft.pt.basicdata.serialnumber.service.SerialNumberService;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberBuildParams;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberRecordParams;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.enums.SaveDataErrorCode;
import com.wellsoft.pt.dyform.implement.data.exceptions.SaveDataException;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.template.TemplateEngine;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroup;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.acl.entity.AclSid;
import com.wellsoft.pt.security.acl.service.AclService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 流水号定义服务层实现类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-6.1	zhouyq		2013-3-6		Create
 * </pre>
 * @date 2013-3-6
 */
@Service
public class SerialNumberServiceImpl extends AbstractJpaServiceImpl<SerialNumber, SerialNumberDao, String> implements
        SerialNumberService {

    /**
     * 日期转为中文大写
     */

    private static final String[] NUMBERS = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    @Autowired
    private CommonValidateService commonValidateService;
    @Autowired
    private SerialNumberMaintainService serialNumberMaintainService;
    @Autowired
    private AclService aclService;
    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private DyFormFacade dyFormApiFacade;
    @Autowired
    private SerialNumberFacadeService serialNumberFacadeService;

    /**
     * 将关键字定义格式全部替换成${}的形式
     *
     * @param keyWrod
     * @return
     */

    private static String replaceKeyWord(String keyWrod) {
        if (!(StringUtils.isBlank(keyWrod))) {
            return keyWrod.replace("{{", "${").replace("}}", "}");
        }
        return "";
    }

    /**
     * 关键字定义（实体集合）
     *
     * @return
     * @throws Exception
     */

    private static <ENTITY extends IdEntity> Map<Object, Object> getMapValue(String startDate,
                                                                             Collection<ENTITY> entities, Map<String, Object> dytableMap) throws Exception {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);// 获取年份
        int month = cal.get(Calendar.MONTH) + 1;// 获取月份
        int day = cal.get(Calendar.DATE);// 获取日
        int hour = cal.get(Calendar.HOUR_OF_DAY);// 小时
        int minute = cal.get(Calendar.MINUTE);// 分
        int second = cal.get(Calendar.SECOND);// 秒

        // 判断新年度日期设置是否为null或空
        if (startDate != null && !("".equals(startDate))) {
            String a[] = startDate.split("-");
            String m = a[0];
            String d = a[1];
            if (month < Integer.valueOf(m)) {
                year = year - 1;
            } else if (month == Integer.valueOf(m) && day < Integer.valueOf(d)) {
                year = year - 1;
            }
            // System.out.println("新年度开始日期：" + startDate);
            // System.out.println("截取月份：" + m);
            // System.out.println("截取天：" + d);
        }

        // System.out.println("当前年：" + year);

        Map<Object, Object> keyWordMap = new HashMap<Object, Object>();
        keyWordMap.put("年", String.valueOf(year));
        keyWordMap.put("月", DateUtil.getFormatDate(month));
        keyWordMap.put("日", DateUtil.getFormatDate(day));
        keyWordMap.put("时", DateUtil.getFormatDate(hour));
        keyWordMap.put("分", DateUtil.getFormatDate(minute));
        keyWordMap.put("秒", DateUtil.getFormatDate(second));
        keyWordMap.put("简年", String.valueOf(year).substring(2));
        keyWordMap.put("大写年", toChinese(String.valueOf(year)));
        keyWordMap.put("大写月", toChinese(String.valueOf(month)));
        keyWordMap.put("大写日", toChinese(String.valueOf(day)));

        keyWordMap.put("YEAR", String.valueOf(year));
        keyWordMap.put("MONTH", String.valueOf(month));
        keyWordMap.put("DAY", String.valueOf(day));
        keyWordMap.put("HOUR", String.valueOf(hour));
        keyWordMap.put("MINUTE", String.valueOf(minute));
        keyWordMap.put("SECOND", String.valueOf(second));
        keyWordMap.put("SHORTYEAR", String.valueOf(year).substring(2));

        // 动态表单解析
        keyWordMap.put("dytable", dytableMap);

        // 只传入${属性}时解析
        for (Object obj : entities) {
            // 反射取得实体类的属性及值存入keyWordMap集合中
            BeanWrapperImpl wrapper = new BeanWrapperImpl(obj);
            PropertyDescriptor[] descriptors = wrapper.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : descriptors) {
                String propertyName = descriptor.getName();
                // System.out.println(propertyName);
                keyWordMap.put(propertyName, wrapper.getPropertyValue(propertyName));
            }
        }

        // 传入${类.属性}时解析
        for (Object obj : entities) {
            String className = obj.getClass().getSimpleName();
            String lowerCaseName = className.substring(0, 1).toLowerCase() + className.substring(1);
            // System.out.println("首字母小写类名：" + lowerCaseName);
            keyWordMap.put(lowerCaseName, obj);
        }

        return keyWordMap;
    }

    /**
     * 关键字定义(单个实体)
     *
     * @return
     * @throws Exception
     */

    private static <ENTITY extends IdEntity> Map<Object, Object> getMapValue(String startDate, ENTITY entity,
                                                                             Map<String, Object> dytableMap) throws Exception {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);// 获取年份
        int month = cal.get(Calendar.MONTH) + 1;// 获取月份
        int day = cal.get(Calendar.DATE);// 获取日
        int hour = cal.get(Calendar.HOUR_OF_DAY);// 小时
        int minute = cal.get(Calendar.MINUTE);// 分
        int second = cal.get(Calendar.SECOND);// 秒

        // 判断新年度日期设置是否为null或空
        if (startDate != null && !("".equals(startDate))) {
            String a[] = startDate.split("-");
            String m = a[0];
            String d = a[1];
            if (month < Integer.valueOf(m)) {
                year = year - 1;
            } else if (month == Integer.valueOf(m) && day < Integer.valueOf(d)) {
                year = year - 1;
            }
            // System.out.println("新年度开始日期：" + startDate);
            // System.out.println("截取月份：" + m);
            // System.out.println("截取天：" + d);
        }

        // System.out.println("当前年：" + year);

        Map<Object, Object> keyWordMap = new HashMap<Object, Object>();
        keyWordMap.put("年", String.valueOf(year));
        keyWordMap.put("月", DateUtil.getFormatDate(month));
        keyWordMap.put("日", DateUtil.getFormatDate(day));
        keyWordMap.put("时", DateUtil.getFormatDate(hour));
        keyWordMap.put("分", DateUtil.getFormatDate(minute));
        keyWordMap.put("秒", DateUtil.getFormatDate(second));
        keyWordMap.put("简年", String.valueOf(year).substring(2));
        keyWordMap.put("大写年", toChinese(String.valueOf(year)));
        keyWordMap.put("大写月", toChinese(String.valueOf(month)));
        keyWordMap.put("大写日", toChinese(String.valueOf(day)));

        keyWordMap.put("YEAR", String.valueOf(year));
        keyWordMap.put("MONTH", String.valueOf(month));
        keyWordMap.put("DAY", String.valueOf(day));
        keyWordMap.put("HOUR", String.valueOf(hour));
        keyWordMap.put("MINUTE", String.valueOf(minute));
        keyWordMap.put("SECOND", String.valueOf(second));
        keyWordMap.put("SHORTYEAR", String.valueOf(year).substring(2));

        // 反射取得实体类的属性及值存入keyWordMap集合中
        BeanWrapperImpl wrapper = new BeanWrapperImpl(entity);
        PropertyDescriptor[] descriptors = wrapper.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : descriptors) {
            String propertyName = descriptor.getName();
            // System.out.println(propertyName);
            keyWordMap.put(propertyName, wrapper.getPropertyValue(propertyName));
        }
        // 传入${类.属性}时解析
        String className = entity.getClass().getSimpleName();
        String lowerCaseName = className.substring(0, 1).toLowerCase() + className.substring(1);
        // System.out.println("首字母小写类名：" + lowerCaseName);
        keyWordMap.put(lowerCaseName, entity);

        // 动态表单解析
        keyWordMap.put("dytable", dytableMap);
        return keyWordMap;
    }

    /**
     * 关键字定义（单个参数）
     *
     * @return
     * @throws Exception
     */

    private static <ENTITY extends IdEntity> Map<String, Object> getMapValue(String startDate) throws Exception {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);// 获取年份
        int month = cal.get(Calendar.MONTH) + 1;// 获取月份
        int day = cal.get(Calendar.DATE);// 获取日
        int hour = cal.get(Calendar.HOUR_OF_DAY);// 小时
        int minute = cal.get(Calendar.MINUTE);// 分
        int second = cal.get(Calendar.SECOND);// 秒

        // 判断新年度日期设置是否为null或空
        if (startDate != null && !("".equals(startDate))) {
            String a[] = startDate.split("-");
            String m = a[0];
            String d = a[1];
            if (month < Integer.valueOf(m)) {
                year = year - 1;
            } else if (month == Integer.valueOf(m) && day < Integer.valueOf(d)) {
                year = year - 1;
            }
            // System.out.println("新年度开始日期：" + startDate);
            // System.out.println("截取月份：" + m);
            // System.out.println("截取天：" + d);
        }

        // System.out.println("当前年：" + year);

        Map<String, Object> keyWordMap = Maps.newHashMap();
        keyWordMap.put("年", String.valueOf(year));
        keyWordMap.put("月", DateUtil.getFormatDate(month));
        keyWordMap.put("日", DateUtil.getFormatDate(day));
        keyWordMap.put("时", DateUtil.getFormatDate(hour));
        keyWordMap.put("分", DateUtil.getFormatDate(minute));
        keyWordMap.put("秒", DateUtil.getFormatDate(second));
        keyWordMap.put("简年", String.valueOf(year).substring(2));
        keyWordMap.put("大写年", toChinese(String.valueOf(year)));
        keyWordMap.put("大写月", toChinese(String.valueOf(month)));
        keyWordMap.put("大写日", toChinese(String.valueOf(day)));

        keyWordMap.put("YEAR", String.valueOf(year));
        keyWordMap.put("MONTH", String.valueOf(month));
        keyWordMap.put("DAY", String.valueOf(day));
        keyWordMap.put("HOUR", String.valueOf(hour));
        keyWordMap.put("MINUTE", String.valueOf(minute));
        keyWordMap.put("SECOND", String.valueOf(second));
        keyWordMap.put("SHORTYEAR", String.valueOf(year).substring(2));

        return keyWordMap;
    }

    public static String toChinese(String str) {
        StringBuffer sb = new StringBuffer();
        sb.append(getSplitDateStr(str, 0));
        return sb.toString();
    }

    public static String getSplitDateStr(String str, int unit) {
        // unit是单位 0=年 1=月 2日
        String[] DateStr = str.split("-");
        if (unit > DateStr.length)
            unit = 0;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < DateStr[unit].length(); i++) {

            if ((unit == 1 || unit == 2) && Integer.valueOf(DateStr[unit]) > 9) {
                sb.append(convertNum(DateStr[unit].substring(0, 1))).append("拾")
                        .append(convertNum(DateStr[unit].substring(1, 2)));
                break;
            } else {
                sb.append(convertNum(DateStr[unit].substring(i, i + 1)));
            }
        }
        if (unit == 1 || unit == 2) {
            return sb.toString().replaceAll("^壹", "").replace("零", "");
        }
        return sb.toString();
    }

    private static String convertNum(String str) {
        return NUMBERS[Integer.valueOf(str)];
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 获得所有流水号定义
     *
     * @return
     */
    @Override
    public List<SerialNumber> findAll() {
        return listAll();
    }

    /**
     * 根据名称来获取对应的可编辑流水号定义
     */
    @Override
    public SerialNumber getFormatedSerialNumberByName(String name) {
        String initialValue = null;
        SerialNumber serial = dao.getByName(name);
        String keyPart = serial.getKeyPart();
        String headPart = serial.getHeadPart();
        String lastPart = serial.getLastPart();
        String startDate = serial.getStartDate();

        // 将关键字定义格式全部替换成${}的形式
        keyPart = replaceKeyWord(keyPart);
        headPart = replaceKeyWord(headPart);
        lastPart = replaceKeyWord(lastPart);

        try {
            Map<String, Object> keyWordMap = getMapValue(startDate);

            TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();

            // 替换模板关键字为Map集合里面的值
            keyPart = templateEngine.process(keyPart, keyWordMap);
            // System.out.println(keyPart);
            headPart = templateEngine.process(headPart, keyWordMap);
            // System.out.println(headPart);
            lastPart = templateEngine.process(lastPart, keyWordMap);
            dao.getSession().clear();// 清除缓存
            serial.setKeyPart(keyPart);
            serial.setHeadPart(headPart);
            serial.setLastPart(lastPart);
            // System.out.println(lastPart);
        } catch (Exception e1) {
            logger.error(ExceptionUtils.getStackTrace(e1));

            return null;
        }
        return serial;
    }

    @Override
    public SerialNumber getOneByName(String name) {
        return dao.getByName(name);
    }

    /**
     * 获得所有可编辑流水号
     *
     * @return
     */
    @Override
    public List<SerialNumber> getByIsEditor(Boolean isEditor) {
        List<SerialNumber> serialList = null;
        if (isEditor) {
            serialList = dao.listByFieldEqValue("isEditor", isEditor);// 得到所有的可编辑流水号定义
        }
        return serialList;
    }

    /**
     * 根据指定ID获取可编辑流水号
     *
     * @param designatedId
     * @return
     */
    @Override
    public List<SerialNumber> getByDesignatedId(Boolean isEditor, String designatedId) {
        List<SerialNumber> serialList = null;
        List<SerialNumber> serialListById = new ArrayList<SerialNumber>();
        if (isEditor) {
            serialList = dao.listByFieldEqValue("isEditor", isEditor);// 得到所有的可编辑流水号定义
            if (serialList.size() > 0) {
                for (SerialNumber serialNumber : serialList) {
                    if (designatedId.equals(serialNumber.getId()) && hasPermission(serialNumber)) {
                        serialListById.add(serialNumber);
                    }
                }
            }

        }
        return serialListById;
    }

    /**
     * 根据指定类型获取可编辑流水号
     *
     * @param designatedType
     * @return
     */
    @Override
    public List<SerialNumber> getByDesignatedType(Boolean isEditor, String designatedType) {
        List<SerialNumber> serialList = null;
        List<SerialNumber> serialListByType = new ArrayList<SerialNumber>();
        if (isEditor) {
            serialList = dao.listByFieldEqValue("isEditor", isEditor);// 得到所有的可编辑流水号定义
            if (serialList.size() > 0) {
                for (SerialNumber serialNumber : serialList) {
                    if (designatedType.equals(serialNumber.getType())) {
                        serialListByType.add(serialNumber);
                    }
                }
            }
        }
        return serialListByType;
    }

    @Override
    public List<SerialNumber> listAllByDesignatedTypes(Boolean isEditor, List<String> designatedTypes) {
        List<SerialNumber> serialList = new ArrayList<>();
        if (CollectionUtils.isEmpty(designatedTypes)) {
            return serialList;
        }
        Map<String, Object> paramsMap = new HashMap<>();
        StringBuilder sbHql = new StringBuilder("from SerialNumber where ");
        paramsMap.put("types", designatedTypes);
        sbHql.append("type in (:types)");
        if (isEditor != null) {
            paramsMap.put("isEditor", isEditor);
            sbHql.append(" and isEditor = :isEditor");
        }
        sbHql.append(" order by code");
        serialList = this.listByHQL(sbHql.toString(), paramsMap);
        List<SerialNumber> serialListByType = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(serialList)) {
            for (SerialNumber serialNumber : serialList) {
                //增加权限判断
                if (hasPermission(serialNumber)) {
                    serialListByType.add(serialNumber);
                }
            }
        }
        return serialListByType;
    }

    /**
     * 通过uuid获取流水号定义
     *
     * @param uuid
     * @return
     */
    @Override
    public SerialNumber get(String uuid) {
        return dao.getOne(uuid);
    }

    /**
     * 通过uuid获取流水号定义VO对象
     *
     * @param uuid
     * @return
     */
    @Override
    public SerialNumberBean getBeanByUuid(String uuid) {
        SerialNumber serialNumber = this.dao.getOne(uuid);
        SerialNumberBean bean = new SerialNumberBean();
        BeanUtils.copyProperties(serialNumber, bean);

        // 设置流水号定义使用人
        List<AclSid> aclSids = aclService.getSid(serialNumber);
        List<String> sids = new ArrayList<String>();
        for (AclSid sid : aclSids) {
            if (ACL_SID.equals(sid.getSid())) {
                continue;
            }
            sids.add(sid.getSid());
        }
        StringBuilder ownerIds = new StringBuilder();
        StringBuilder ownerNames = new StringBuilder();
        Iterator<String> it = sids.iterator();
        while (it.hasNext()) {
            String sid = it.next();
            if (sid.startsWith(IdPrefix.USER.getValue())) {
                MultiOrgUserAccount user = orgApiFacade.getAccountByUserId(sid);
                ownerIds.append(user.getId());
                ownerNames.append(user.getUserName());
            } else if (sid.startsWith(IdPrefix.DUTY.getValue())) {
                continue;
                //List<OrgTreeNodeDto> currVerNodes = multiOrgService.queryJobNodeListOfCurrentVerisonByDutyId(sid);
            } else if (sid.startsWith(IdPrefix.DEPARTMENT.getValue())) {
                MultiOrgElement department = orgApiFacade.getOrgElementById(sid);
                ownerIds.append(department.getId());
                ownerNames.append(department.getName());
            } else if (sid.startsWith(IdPrefix.GROUP.getValue())) {
                MultiOrgGroup group = orgApiFacade.getGroupById(sid);
                ownerIds.append(group.getId());
                ownerNames.append(group.getName());
            } else if (sid.startsWith(IdPrefix.BUSINESS_UNIT.getValue())) {
                String name = orgApiFacade.getBusinessUnitNameByJobIdPath(sid);
                ownerIds.append(sid);
                ownerNames.append(name);
            } else {
                OrgTreeNodeDto node = orgApiFacade.getNodeOfCurrentVerisonByEleId(sid);
                ownerIds.append(sid);
                ownerNames.append(node.getEleNamePath());
            }
            if (it.hasNext()) {
                ownerIds.append(Separator.SEMICOLON.getValue());
                ownerNames.append(Separator.SEMICOLON.getValue());
            }
        }
        bean.setOwnerIds(ownerIds.toString());
        bean.setOwnerNames(ownerNames.toString());

        return bean;
    }

    /**
     * 保存流水号定义bean
     *
     * @see com.wellsoft.pt.message.service.SystemTableService#saveBean(com.wellsoft.pt.message.bean.PrintTemplateBean)
     */
    @Override
    @Transactional
    public void saveBean(SerialNumberBean bean) {
        SerialNumber serialNumber = new SerialNumber();
        // 保存新serialNumber 设置id值
        if (StringUtils.isBlank(bean.getUuid())) {
            bean.setUuid(null);
            if (StringUtils.isNotBlank(bean.getId())
                    && commonValidateService.checkExists("serialNumber", "id", bean.getId())) {
                // ID非空唯一性判断
                throw new RuntimeException("已经存在ID为[" + bean.getId() + "]的流水号!");
            }
        } else {
            serialNumber = this.dao.getOne(bean.getUuid());
            // ID非空唯一性判断
            if (StringUtils.isNotBlank(bean.getId())
                    && !commonValidateService.checkUnique(bean.getUuid(), "serialNumber", "id", bean.getId())) {
                throw new RuntimeException("已经存在ID为[" + bean.getId() + "]的流水号!");
            }
        }
        BeanUtils.copyProperties(bean, serialNumber);

        // 初始值不能为空
        if (StringUtils.isBlank(serialNumber.getInitialValue())) {
            throw new RuntimeException("初始值不能为空");
        }
        // 增量大于0验证
        if (serialNumber.getIncremental() == null || serialNumber.getIncremental() <= 0) {
            throw new RuntimeException("流水号增量必须大于0");
        }

        // 设置所有者
        if (StringUtils.isNotBlank(bean.getOwnerIds())) {
            String[] ownerIds = StringUtils.split(bean.getOwnerIds(), Separator.SEMICOLON.getValue());
            serialNumber.setOwners(Arrays.asList(ownerIds));
        }
        this.dao.save(serialNumber);

        // ACL行数据权限保存
        this.saveAcl(serialNumber);
    }

    /**
     * 删除流水号定义
     *
     * @see com.wellsoft.pt.message.service.SystemTableService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        this.dao.delete(uuid);
    }

    /**
     * 批量删除
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.serialnumber.service.SerialNumberService#deleteAllById(java.lang.String[])
     */
    @Override
    @Transactional
    public void deleteAllById(String[] ids) {
        for (int i = 0; i < ids.length; i++) {
            SerialNumber serialNumber = dao.getById(ids[i]);
            if (serialNumber == null) {
                serialNumber = dao.getOne(ids[i]);
            }
            dao.delete(serialNumber);
        }

    }

    /**
     * JQgrid流水号定义列表查询
     *
     * @see com.wellsoft.pt.message.service.SystemTableService#query(com.wellsoft.pt.common.component.jqgrid.JqGridQueryInfo)
     */
    @Override
    public JqGridQueryData query(JqGridQueryInfo queryInfo) {
        PagingInfo page = new PagingInfo(queryInfo.getPage(), queryInfo.getRows(), true);
        List<SerialNumber> serialNumbers = this.dao.listAllByOrderPage(page, null);
        List<SerialNumber> jqUsers = new ArrayList<SerialNumber>();
        for (SerialNumber serialNumber : serialNumbers) {
            SerialNumber jqSerialNumber = new SerialNumber();
            BeanUtils.copyProperties(serialNumber, jqSerialNumber);
            jqUsers.add(jqSerialNumber);
        }
        JqGridQueryData queryData = new JqGridQueryData();
        queryData.setCurrentPage(queryInfo.getPage());
        queryData.setDataList(jqUsers);
        queryData.setRepeatitems(false);
        queryData.setTotalPages(page.getTotalPages());
        queryData.setTotalRows(page.getTotalCount());
        return queryData;
    }

    @Override
    @Transactional
    public SerialNumber saveAcl(SerialNumber serialNumber) {
        //如果是可编辑的，预生成一流水号
        if (serialNumber.getIsEditor()) {
            Map<String, String> parts = explainSerialNumberParts(serialNumber);
            SerialNumberMaintain existMaintain = serialNumberMaintainService.getByIdAndKeyPart(serialNumber.getId(),
                    parts.get("keyPart"));
            if (StringUtils.isBlank(serialNumber.getUuid()) || existMaintain == null) {
                SerialNumberMaintain maintain = new SerialNumberMaintain();
                maintain.setId(serialNumber.getId());
                maintain.setName(serialNumber.getName());
                maintain.setKeyPart(parts.get("keyPart"));
                maintain.setHeadPart(parts.get("headPart"));
                maintain.setLastPart(parts.get("lastPart"));
                maintain.setPointer(String.format("%0" + serialNumber.getInitialValue().length() + "d",
                        Integer.parseInt(serialNumber.getInitialValue()) - 1));
                maintain.setIsEditor(serialNumber.getIsEditor());
                maintain.setModuleId(serialNumber.getModuleId());
                serialNumberMaintainService.save(maintain);
            }
            if (StringUtils.isNotBlank(serialNumber.getUuid()) && existMaintain != null) {
                //流水号定义变更，对应更新预生成的流水号数据
                existMaintain.setHeadPart(parts.get("headPart"));
                existMaintain.setLastPart(parts.get("lastPart"));
                existMaintain.setIsEditor(serialNumber.getIsEditor());
                serialNumberMaintainService.save(existMaintain);
            }
        }

        List<AclSid> aclSids = aclService.getSid(serialNumber);
        List<String> existSids = new ArrayList<String>();
        for (AclSid aclSid : aclSids) {
            existSids.add(aclSid.getSid());
        }
        List<String> sids = getAclSid(serialNumber);
        // 新的SID
        List<String> newSids = new ArrayList<String>();
        for (String newSid : sids) {
            if (!existSids.contains(newSid)) {
                newSids.add(newSid);
            }
        }
        // 要删除的SID
        List<String> delSids = new ArrayList<String>();
        for (String newSid : existSids) {
            if (!sids.contains(newSid)) {
                delSids.add(newSid);
            }
        }

        // 删除
        for (String sid : delSids) {
            aclService.removePermission(serialNumber, BasePermission.ADMINISTRATION, sid);
        }
        // 新增
        /*
         * if (serialNumber.getParent() != null) { aclService.save(serialNumber,
         * serialNumber.getParent(), sids.get(0),
         * BasePermission.ADMINISTRATION); }
         */
        for (String sid : sids) {
            aclService.addPermission(serialNumber, BasePermission.ADMINISTRATION, sid);
        }
        return aclService.get(SerialNumber.class, serialNumber.getUuid(), BasePermission.ADMINISTRATION);
    }

    /**
     * generateSerialNumberList
     * 返回流水号定义使用者在ACL中的SID
     *
     * @param serialNumber
     * @return
     */
    private List<String> getAclSid(SerialNumber serialNumber) {
        if (serialNumber.getOwners().isEmpty()) {
            // "ROLE_SERIAL_NUMBER"
            serialNumber.getOwners().add(ACL_SID);
            return serialNumber.getOwners();
        }
        // 返回组织部门中选择的角色作为SID
        return serialNumber.getOwners();
    }

    /**
     * 判断当前登录用户是否在指定的组织部门中
     *
     * @param serialNumber
     */
    private Boolean hasPermission(SerialNumber serialNumber) {
        if (!serialNumber.getIsEditor()) {
            return true;
        }
        // 获取该流水号的所有SID，判断是否有访问权限
        List<AclSid> aclSids = aclService.getSid(serialNumber);
        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        Set<String> eleIdSet = new HashSet<>();
        for (AclSid aclSid : aclSids) {
            String sid = aclSid.getSid();
            // 如果所有者是默认的则有权限
            if (sid.equals(ACL_SID)) {
                return true;
            } else if (sid.startsWith(IdPrefix.USER.getValue())) {
                if (StringUtils.equals(currentUserId, sid)) {
                    return true;
                }
            } else {
                eleIdSet.add(sid);
            }
        }
        return orgApiFacade.isExistUserByEleIds(currentUserId, eleIdSet);
    }

    // 以下部分为接口的解析
    @Override
    public Map<String, String> explainSerialNumberParts(SerialNumber serialNumber) {

        String keyPart = serialNumber.getKeyPart();
        String headPart = serialNumber.getHeadPart();
        String lastPart = serialNumber.getLastPart();
        String startDate = serialNumber.getStartDate();

        // 将关键字定义格式全部替换成${}的形式
        keyPart = replaceKeyWord(keyPart);
        headPart = replaceKeyWord(headPart);
        lastPart = replaceKeyWord(lastPart);
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        try {
            Map<String, Object> keyWordMap = getMapValue(startDate);
            // 替换模板关键字为Map集合里面的值
            keyPart = templateEngine.process(keyPart, keyWordMap);
            headPart = templateEngine.process(headPart, keyWordMap);
            lastPart = templateEngine.process(lastPart, keyWordMap);
            Map<String, String> parts = Maps.newHashMap();
            parts.put("keyPart", keyPart);
            parts.put("headPart", headPart);
            parts.put("lastPart", lastPart);
            return parts;
        } catch (Exception e) {
            logger.error("解析流水号异常：", e);
            return null;
        }
    }

    /**
     * 获取流水号接口,单个实体
     */
    @Override
    public <ENTITY extends IdEntity> String getSerialNumber(String id, ENTITY entity, Boolean isOccupied,
                                                            Map<String, Object> dytableMap, String fieldName) throws Exception {
        SerialNumber serialNumber = dao.getById(id);

        String type = serialNumber.getType();
        String name = serialNumber.getName();
        String numberId = serialNumber.getId();
        String keyPart = serialNumber.getKeyPart();
        String headPart = serialNumber.getHeadPart();
        String initialValue = serialNumber.getInitialValue();
        Boolean isFillPosition = serialNumber.getIsFillPosition();
        Integer incremental = serialNumber.getIncremental();
        String lastPart = serialNumber.getLastPart();
        Boolean isFillNumber = serialNumber.getIsFillNumber();
        String startDate = serialNumber.getStartDate();
        Boolean isEditor = serialNumber.getIsEditor();
        String remark = serialNumber.getRemark();

        // 将关键字定义格式全部替换成${}的形式
        keyPart = replaceKeyWord(keyPart);
        headPart = replaceKeyWord(headPart);
        lastPart = replaceKeyWord(lastPart);

        Map<Object, Object> keyWordMap = getMapValue(startDate, entity, dytableMap);
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        try {
            // 替换模板关键字为Map集合里面的值
            keyPart = templateEngine.process(keyPart, keyWordMap);
            // System.out.println(keyPart);
            headPart = templateEngine.process(headPart, keyWordMap);
            // System.out.println(headPart);
            lastPart = templateEngine.process(lastPart, keyWordMap);
            // System.out.println(lastPart);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));

            return null;
        }

        // System.out.println("keyPart:" + keyPart);
        // System.out.println("headPart:" + headPart);
        // System.out.println("lastPart:" + lastPart);

        // 检查流水号维护中是否已经存在该关键字的流水号
        // add by huanglinchuan 2014.10.17 begin
        // SerialNumberMaintain serialNumberMaintain =
        // serialNumberMaintainService.findUniqueBy("keyPart", keyPart);
        SerialNumberMaintain serialNumberMaintain = serialNumberMaintainService.getByIdAndKeyPart(id, keyPart);
        // add by huanglinchuan 2014.10.17 end
        String serialNum = null;
        if (serialNumberMaintain != null) {
            String pointer = serialNumberMaintain.getPointer();// 如果流水号维护中存在的话就获取当前的指针值
            // 补位处理
            if (isFillPosition) {
                int length = initialValue.length();
                // System.out.println("长度为：" + length);
                // 0 代表前面补充0
                // 4 代表长度为4
                // d 代表参数为正数型
                String formatContent = "%0" + String.valueOf(length) + "d";
                pointer = String.format(formatContent, Integer.valueOf(pointer));
                // System.out.println("补位后：" + pointer);
            }
            // String maintainHeadPart =
            // serialNumberMaintain.getHeadPart();//获取维护头部
            // String maintainLastPart =
            // serialNumberMaintain.getLastPart();//获取维护尾部
            // 补号处理
            if (isFillNumber) {
                String classFullName = entity.getClass().getName();// 获取类完全限定名
                String className = classFullName.substring(classFullName.lastIndexOf(".") + 1);// 获取类名
                List<String> serialList = dao.findByFieldName(fieldName, className);

                List<String> numList = new ArrayList<String>();// 真正流水号列集合
                String tempHeadPart = headPart;
                for (String value : serialList) {
                    // 判断当前流水号是否为null,为null不用截取
                    if (value != null && !("".equals(value))) {
                        value = value.substring(tempHeadPart.length(), tempHeadPart.length() + pointer.length());
                        numList.add(value);
                        // System.out.println("真正流水号:" + value);
                    }
                }
                Map<String, String> map = new HashMap<String, String>();// 真正流水号列集合
                List<String> fillNumList = new ArrayList<String>();// 补号的集合
                for (String value : numList) {
                    map.put(value, value);
                }
                for (int i = Integer.valueOf(initialValue); i < Integer.valueOf(pointer); i = i + incremental) {
                    // 若有补位，则返回的补号也要有补位
                    if (isFillPosition) {
                        int length = initialValue.length();
                        // System.out.println("长度为：" + length);
                        // 0 代表前面补充0
                        // 4 代表长度为4
                        // d 代表参数为正数型
                        String formatContent = "%0" + String.valueOf(length) + "d";
                        String temp = String.format(formatContent, i);
                        if (!map.containsKey(temp)) {
                            fillNumList.add(temp);
                            // System.out.println("需要的补号：" + temp);
                        }
                    }
                }
                // 如果补号集合里有补号
                if (fillNumList.size() > 0) {
                    Collections.sort(fillNumList);// 对补号集合进行排序
                    String minFillNum = fillNumList.get(0);
                    // System.out.println("最小的补号：" + minFillNum);
                    /*
                     * if (isOccupied) {
                     * serialNumberMaintain.setPointer(minFillNum);
                     * serialNumberMaintainService.save(serialNumberMaintain); }
                     */
                    serialNum = headPart + minFillNum + lastPart;
                    // System.out.println("更新维护指针:" + minFillNum);
                    // System.out.println("更新生成的流水号：" + headPart + "【" +
                    // minFillNum + "】" + lastPart);
                } else {
                    pointer = String.valueOf(Integer.valueOf(pointer) + incremental);// 若没有补号则指针值加上增量
                    // 补位处理
                    if (isFillPosition) {
                        int length = initialValue.length();
                        // System.out.println("长度为：" + length);
                        // 0 代表前面补充0
                        // 4 代表长度为4
                        // d 代表参数为正数型
                        String formatContent = "%0" + String.valueOf(length) + "d";
                        pointer = String.format(formatContent, Integer.valueOf(pointer));
                        // System.out.println("补位后：" + pointer);
                    }
                    // 若占用
                    if (isOccupied) {
                        serialNumberMaintain.setPointer(pointer);
                        serialNumberMaintain.setHeadPart(headPart);
                        serialNumberMaintain.setLastPart(lastPart);
                        serialNumberMaintain.setIsEditor(isEditor);
                        serialNumberMaintainService.save(serialNumberMaintain);
                    }
                    serialNum = headPart + pointer + lastPart;

                    // System.out.println("aaa更新维护指针:" + pointer);
                    // System.out.println("aaa更新生成的流水号：" + headPart + "【" +
                    // pointer + "】" + lastPart);
                }
            } else {
                pointer = String.valueOf(Integer.valueOf(pointer) + incremental);// 若没有补号则指针值加上增量
                // 补位处理
                if (isFillPosition) {
                    int length = initialValue.length();
                    // System.out.println("长度为：" + length);
                    // 0 代表前面补充0
                    // 4 代表长度为4
                    // d 代表参数为正数型
                    String formatContent = "%0" + String.valueOf(length) + "d";
                    pointer = String.format(formatContent, Integer.valueOf(pointer));
                    // System.out.println("补位后：" + pointer);
                }
                // 若占用
                if (isOccupied) {
                    serialNumberMaintain.setPointer(pointer);
                    serialNumberMaintain.setHeadPart(headPart);
                    serialNumberMaintain.setLastPart(lastPart);
                    serialNumberMaintain.setIsEditor(isEditor);
                    serialNumberMaintainService.save(serialNumberMaintain);
                }
                serialNum = headPart + pointer + lastPart;

                // System.out.println("更新维护指针:" + pointer);
                // System.out.println("更新生成的流水号：" + headPart + "【" + pointer +
                // "】" + lastPart);
            }

        } else {
            // 若流水号维护中不存在则新建流水号
            SerialNumberMaintain maintain = new SerialNumberMaintain();
            maintain.setId(numberId);
            maintain.setName(name);
            maintain.setKeyPart(keyPart);
            maintain.setHeadPart(headPart);
            maintain.setLastPart(lastPart);
            maintain.setPointer(initialValue);
            maintain.setIsEditor(isEditor);
            // 若被占用，存入数据库
            if (isOccupied) {
                serialNumberMaintainService.save(maintain);
            }
            serialNum = headPart + initialValue + lastPart;

            // System.out.println("新建维护指针:" + initialValue);
            // System.out.println("首次生成的流水号：" + headPart + "【" + initialValue +
            // "】" + lastPart);
        }
        // System.out.println("最终流水号：" + serialNum);
        return serialNum;
    }

    /**
     * 动态表单专用获取不可编辑流水号接口
     * <p>
     * (non-Javadoc)
     */
    @Override
    public String getNotEditorSerialNumberForDytable(String serialNumberId, Boolean isOccupied, String formUuid,
                                                     String field) {
        SerialNumberBuildParams params = new SerialNumberBuildParams();
        params.setSerialNumberId(serialNumberId);
        params.setFormField(field);
        params.setFormUuid(formUuid);
        params.setOccupied(isOccupied);
        return this.generateSerialNumber(params);
    }

    @Override
    public String generateSerialNumber(SerialNumberBuildParams params) {
        SerialNumberMaintain maintain = this.generateSerialNumberMaintain(params);
        String serialNum = null;
        if (null != maintain) {
            serialNum = maintain.getHeadPart() + maintain.getPointer() + maintain.getLastPart();//serialNum;
        }
        return serialNum;
    }

    @Override
    public SerialNumberMaintainBean generateSerialNumberMaintain(SerialNumberBuildParams params) {
        SerialNumber serialNumber = dao.getById(params.getSerialNumberId());
        if (null == serialNumber || !hasPermission(serialNumber)) {
            return null;
        }
        SerialNumberMaintainBean maintain = generateSerialNumber2(params);
        return maintain;
    }

    /**
     * @param paramss
     * @return
     */
    public SerialNumberMaintainBean generateSerialNumber2(SerialNumberBuildParams params) {
        SerialNumber serialNumber = dao.getById(params.getSerialNumberId());
        if (null == serialNumber) {
            throw new BusinessException("表单字段[" + params.getFormField() + "]选择的流水号[" + params.getSerialNumberId()
                    + "]不存在");
        }
        String name = serialNumber.getName();
        String numberId = serialNumber.getId();
        String keyPart = serialNumber.getKeyPart();
        String headPart = serialNumber.getHeadPart();
        String initialValue = serialNumber.getInitialValue();
        Boolean isFillPosition = serialNumber.getIsFillPosition();
        Integer incremental = serialNumber.getIncremental();
        String lastPart = serialNumber.getLastPart();
        Boolean isFillNumber = serialNumber.getIsFillNumber();
        String startDate = serialNumber.getStartDate();
        Boolean isEditor = serialNumber.getIsEditor();

        SerialNumberMaintain serialNumberMaintain = null;
        if (isFillNumber && StringUtils.isNotBlank(params.getUuid())) {
            serialNumberMaintain = serialNumberMaintainService.getOne(params.getUuid());
        }
        if (serialNumberMaintain == null) {
            // 将关键字定义格式全部替换成${}的形式
            keyPart = replaceKeyWord(keyPart);
            headPart = replaceKeyWord(headPart);
            lastPart = replaceKeyWord(lastPart);

            Map<String, Object> keyWordMap = Maps.newHashMap();
            try {
                keyWordMap = getMapValue(startDate);
            } catch (Exception e) {
                logger.error("生成流水号参数Map设置异常：", e);
            }
            keyWordMap.putAll(params.getRenderParams());//其他接口传入的渲染参数
            TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
            try {
                // 替换模板关键字为Map集合里面的值
                keyPart = templateEngine.process(keyPart, keyWordMap);
                headPart = templateEngine.process(headPart, keyWordMap);
                lastPart = templateEngine.process(lastPart, keyWordMap);
            } catch (Exception e) {
                logger.error("生成流水号freemarker模板解析异常：", e);
                return null;
            }
            serialNumberMaintain = serialNumberMaintainService.getByIdAndKeyPart(params.getSerialNumberId(), keyPart);
        } else {
            keyPart = serialNumberMaintain.getKeyPart();
            headPart = serialNumberMaintain.getHeadPart();
            lastPart = serialNumberMaintain.getLastPart();
        }
        keyPart = keyPart == null ? "" : keyPart;
        headPart = headPart == null ? "" : headPart;
        lastPart = lastPart == null ? "" : lastPart;

        SerialNumberMaintainBean maintainBean = new SerialNumberMaintainBean();
        if (serialNumberMaintain != null) {
            BeanUtils.copyProperties(serialNumberMaintain, maintainBean);
            String pointer = serialNumberMaintain.getPointer();// 如果流水号维护中存在的话就获取当前的指针值
            // 补号处理
            // 前端一直刷指针，后端提交,流水号值不为空 强制补号
            if (((params.getIsBackEnd() && StringUtils.isNotBlank(params.getSnValue()))
                    || isFillNumber)
                    && StringUtils.isNotBlank(params.getFormUuid())
                    && StringUtils.isNotBlank(params.getFormField())) {
                List<SerialNumberSupplementBean> supplementBeans = new ArrayList<>();
                if (params.getIsBackEnd()) {
                    supplementBeans = serialNumberFacadeService.supplementByTableFieldName(params.getSerialNumberId(), params.getTableName(), params.getFormField(), Lists.newArrayList(maintainBean));
                } else {
                    supplementBeans = serialNumberFacadeService.supplement(params.getSerialNumberId(), Lists.newArrayList(maintainBean));
                }
                if (CollectionUtils.isNotEmpty(supplementBeans)) {
                    for (SerialNumberSupplementBean supplementBean : supplementBeans) {
                        for (Integer i : supplementBean.getPointerList()) {
                            maintainBean.setHeadPart(headPart);
                            maintainBean.setLastPart(lastPart);
                            // 若有补位，则返回的补号也要有补位
                            if (isFillPosition) {
                                int length = initialValue.length();
                                String formatContent = "%0" + String.valueOf(length) + "d";
                                String temp = String.format(formatContent, i);
                                maintainBean.setPointer(temp);
                            } else {
                                maintainBean.setPointer(i + "");
                            }
                            if (StringUtils.isNotBlank(params.getSnValue())) {
                                String serialNum = maintainBean.getHeadPart() + maintainBean.getPointer() + maintainBean.getLastPart();
                                if (serialNum.equals(params.getSnValue())) {
                                    this.saveSerialNumberRecord(params, maintainBean);
                                    return maintainBean;
                                }
                            } else {
                                return maintainBean;
                            }
                        }
                    }
                }

                pointer = String.valueOf(Integer.valueOf(pointer) + incremental);// 若没有补号则指针值加上增量
                // 补位处理
                if (isFillPosition) {
                    int length = initialValue.length();
                    String formatContent = "%0" + String.valueOf(length) + "d";
                    pointer = String.format(formatContent, Integer.valueOf(pointer));
                }
                maintainBean.setOccupied(params.getOccupied());
                maintainBean.setHeadPart(headPart);
                maintainBean.setPointer(pointer);
                maintainBean.setLastPart(lastPart);
                // 若占用
                if (params.getOccupied()) {
                    serialNumberMaintain.setPointer(pointer);
                    serialNumberMaintain.setHeadPart(headPart);
                    serialNumberMaintain.setLastPart(lastPart);
                    serialNumberMaintain.setIsEditor(isEditor);
                    if (params.getDefaultTransaction()) {
                        serialNumberMaintainService.saveByDefaultTransaction(serialNumberMaintain);
                    } else {
                        serialNumberMaintainService.saveByNewTransaction(serialNumberMaintain);
                    }
                }
                this.saveSerialNumberRecord(params, maintainBean);
            } else {
                if (!params.getIsBackEnd()) {
                    pointer = String.valueOf(Integer.valueOf(pointer) + incremental);// 若没有补号则指针值加上增量
                }
                // 补位处理
                if (isFillPosition) {
                    int length = initialValue.length();
                    String formatContent = "%0" + String.valueOf(length) + "d";
                    pointer = String.format(formatContent, Integer.valueOf(pointer));
                }
                maintainBean.setOccupied(params.getOccupied());
                maintainBean.setHeadPart(headPart);
                maintainBean.setPointer(pointer);
                maintainBean.setLastPart(lastPart);
                // 若占用
                if (params.getOccupied()) {
                    serialNumberMaintain.setPointer(pointer);
                    serialNumberMaintain.setHeadPart(headPart);
                    serialNumberMaintain.setLastPart(lastPart);
                    serialNumberMaintain.setIsEditor(isEditor);
                    if (params.getDefaultTransaction()) {
                        serialNumberMaintainService.saveByDefaultTransaction(serialNumberMaintain);
                    } else {
                        serialNumberMaintainService.saveByNewTransaction(serialNumberMaintain);
                    }
                }
                this.saveSerialNumberRecord(params, maintainBean);
            }

        } else {
            serialNumberMaintain = new SerialNumberMaintain();
            // 若流水号维护中不存在则新建流水号
            serialNumberMaintain.setId(numberId);
            serialNumberMaintain.setName(name);
            serialNumberMaintain.setKeyPart(keyPart);
            serialNumberMaintain.setHeadPart(headPart);
            serialNumberMaintain.setLastPart(lastPart);
            serialNumberMaintain.setPointer(initialValue);
            serialNumberMaintain.setIsEditor(isEditor);

            maintainBean.setOccupied(params.getOccupied());
            maintainBean.setHeadPart(headPart);
            maintainBean.setPointer(initialValue);
            maintainBean.setLastPart(lastPart);
            // 若被占用，存入数据库
            if (params.getOccupied()) {
                if (params.getDefaultTransaction()) {
                    serialNumberMaintainService.saveByDefaultTransaction(serialNumberMaintain);
                } else {
                    serialNumberMaintainService.saveByNewTransaction(serialNumberMaintain);
                }
            }
            this.saveSerialNumberRecord(params, maintainBean);
        }
        return maintainBean;
    }

    private void saveSerialNumberRecord(SerialNumberBuildParams params, SerialNumberMaintainBean serialNumberMaintain) {
        if (StringUtils.isNotBlank(params.getDataUuid())) {
            SerialNumberRecordParams recordParams = SerialNumberRecordParams.recordTableParams(params, serialNumberMaintain);
            if (!serialNumberFacadeService.serialNumberRecord(recordParams)) {
                Map<String, Object> variables = Maps.newHashMap();
                variables.put("msg", "流水号重复");
                variables.put("recordParams", recordParams);
                throw new SaveDataException(SaveDataErrorCode.SerialNumberRepeat, variables);
            }
        }
    }

    @Override
    public List<SerialNumberMaintainBean> generateSerialNumberList(SerialNumberBuildParams params) {
        SerialNumber serialNumber = dao.getById(params.getSerialNumberId());
        if (null == serialNumber) {
            throw new BusinessException("表单字段[" + params.getFormField() + "]选择的流水号[" + params.getSerialNumberId()
                    + "]不存在");
        }
        if (!hasPermission(serialNumber)) {
            return new ArrayList<>();
        }
        String name = serialNumber.getName();
        String numberId = serialNumber.getId();
        String keyPart = serialNumber.getKeyPart();
        String headPart = serialNumber.getHeadPart();
        String initialValue = serialNumber.getInitialValue();
        Boolean isFillPosition = serialNumber.getIsFillPosition();
        Integer incremental = serialNumber.getIncremental();
        String lastPart = serialNumber.getLastPart();
        Boolean isFillNumber = serialNumber.getIsFillNumber();
        String startDate = serialNumber.getStartDate();
        Boolean isEditor = serialNumber.getIsEditor();

        // 将关键字定义格式全部替换成${}的形式
        keyPart = replaceKeyWord(keyPart);
        headPart = replaceKeyWord(headPart);
        lastPart = replaceKeyWord(lastPart);

        Map<String, Object> keyWordMap = Maps.newHashMap();
        try {
            keyWordMap = getMapValue(startDate);
        } catch (Exception e) {
            logger.error("生成流水号参数Map设置异常：", e);
        }
        keyWordMap.putAll(params.getRenderParams());//其他接口传入的渲染参数
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        try {
            // 替换模板关键字为Map集合里面的值
            keyPart = templateEngine.process(keyPart, keyWordMap);
            headPart = templateEngine.process(headPart, keyWordMap);
            lastPart = templateEngine.process(lastPart, keyWordMap);
        } catch (Exception e) {
            throw new BusinessException("生成流水号freemarker模板解析异常");
        }

        List<SerialNumberMaintainBean> serialNumberMaintainList = new ArrayList<>();
        //按头尾查询
        if (params.getQueryByHeadAndLast()) {
            headPart = params.getHeadPart();
            lastPart = params.getLastPart();
            SerialNumberMaintain queryMaintain = new SerialNumberMaintain();
            queryMaintain.setHeadPart(headPart);
            queryMaintain.setLastPart(lastPart);
            queryMaintain.setId(params.getSerialNumberId());
            List<SerialNumberMaintain> maintainList = serialNumberMaintainService.listByEntity(queryMaintain);
            if (maintainList.size() == 0) {
                throw new BusinessException("按流水号头尾查询数据不存在");
            }
            for (SerialNumberMaintain serialNumberMaintain : maintainList) {
                SerialNumberMaintainBean serialNumberMaintainBean = new SerialNumberMaintainBean();
                BeanUtils.copyProperties(serialNumberMaintain, serialNumberMaintainBean);
                serialNumberMaintainList.add(serialNumberMaintainBean);
            }

        } else {
            SerialNumberMaintain serialNumberMaintain = serialNumberMaintainService.getByIdAndKeyPart(params.getSerialNumberId(), keyPart);
            if (serialNumberMaintain != null) {
                SerialNumberMaintainBean serialNumberMaintainBean = new SerialNumberMaintainBean();
                BeanUtils.copyProperties(serialNumberMaintain, serialNumberMaintainBean);
                serialNumberMaintainList.add(serialNumberMaintainBean);
            }
        }

        if (serialNumberMaintainList.size() == 0) {
            SerialNumberMaintainBean maintain = new SerialNumberMaintainBean();
            maintain.setId(numberId);
            maintain.setName(name);
            maintain.setKeyPart(keyPart);
            maintain.setHeadPart(headPart);
            maintain.setPointer(initialValue);
            maintain.setLastPart(lastPart);
            serialNumberMaintainList.add(maintain);
            return serialNumberMaintainList;
        }
        int length = initialValue.length();
        String formatContent = "%0" + String.valueOf(length) + "d";
        List<SerialNumberMaintainBean> maintainList = new ArrayList<>();
        List<SerialNumberSupplementBean> supplementBeanList = serialNumberFacadeService.supplement(params.getSerialNumberId(), serialNumberMaintainList);
        for (SerialNumberSupplementBean supplementBean : supplementBeanList) {
            for (Integer i : supplementBean.getPointerList()) {
                SerialNumberMaintainBean maintain = getSerialNumberMaintain(name, numberId, isFillPosition, formatContent, supplementBean, i);
                maintainList.add(maintain);
            }
            for (int i = supplementBean.getPointer() + 1; i <= supplementBean.getPointer() + incremental; i++) {
                SerialNumberMaintainBean maintain = getSerialNumberMaintain(name, numberId, isFillPosition, formatContent, supplementBean, i);
                maintainList.add(maintain);
            }
        }
        return maintainList;
    }

    private SerialNumberMaintainBean getSerialNumberMaintain(String name, String numberId, Boolean isFillPosition, String formatContent,
                                                             SerialNumberSupplementBean supplementBean, Integer i) {
        SerialNumberMaintainBean maintain = new SerialNumberMaintainBean();
        maintain.setId(numberId);
        maintain.setName(name);
        maintain.setUuid(supplementBean.getUuid());
        maintain.setKeyPart(supplementBean.getKeyPart());
        maintain.setHeadPart(supplementBean.getHeadPart());
        maintain.setLastPart(supplementBean.getLastPart());
        String temp = String.valueOf(i);
        if (isFillPosition) {
            temp = String.format(formatContent, i);
        }
        maintain.setPointer(temp);
        return maintain;
    }

    @Override
    @Transactional
    public List<QueryItem> groupByHeadLast(String serialNumberId, Boolean filleNumberBtnFlg) {
        SerialNumber serialNumber = dao.getById(serialNumberId);
        if (null == serialNumber) {
            throw new BusinessException("选择的流水号[" + serialNumberId + "]不存在");
        }
        String name = serialNumber.getName();
        String numberId = serialNumber.getId();
        String keyPart = serialNumber.getKeyPart();
        String headPart = serialNumber.getHeadPart();
        String initialValue = serialNumber.getInitialValue();
        Boolean isFillPosition = serialNumber.getIsFillPosition();
        Integer incremental = serialNumber.getIncremental();
        String lastPart = serialNumber.getLastPart();
        Boolean isFillNumber = serialNumber.getIsFillNumber();
        String startDate = serialNumber.getStartDate();
        Boolean isEditor = serialNumber.getIsEditor();

        // 将关键字定义格式全部替换成${}的形式
        keyPart = replaceKeyWord(keyPart);
        headPart = replaceKeyWord(headPart);
        lastPart = replaceKeyWord(lastPart);

        Map<String, Object> keyWordMap = Maps.newHashMap();
        try {
            keyWordMap = getMapValue(startDate);
        } catch (Exception e) {
            logger.error("生成流水号参数Map设置异常：", e);
        }
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        try {
            // 替换模板关键字为Map集合里面的值
            keyPart = templateEngine.process(keyPart, keyWordMap);
            headPart = templateEngine.process(headPart, keyWordMap);
            lastPart = templateEngine.process(lastPart, keyWordMap);
        } catch (Exception e) {
            throw new BusinessException("生成流水号freemarker模板解析异常");
        }

        keyPart = keyPart == null ? "" : keyPart;
        headPart = headPart == null ? "" : headPart;
        lastPart = lastPart == null ? "" : lastPart;

        SerialNumberMaintain serialNumberMaintain = serialNumberMaintainService.getByIdAndKeyPart(serialNumberId, keyPart);
        if (serialNumberMaintain == null) {
            serialNumberMaintain = new SerialNumberMaintain();
            serialNumberMaintain.setId(numberId);
            serialNumberMaintain.setName(name);
            serialNumberMaintain.setKeyPart(keyPart);
            serialNumberMaintain.setHeadPart(headPart);
            serialNumberMaintain.setLastPart(lastPart);
            int initialVal = Integer.valueOf(initialValue);
            initialVal = initialVal - 1;
            if (initialVal < 0) {
                initialVal = 0;
            }
            String pointer = initialVal + "";
            // 补位处理
            if (isFillPosition) {
                int length = initialValue.length();
                String formatContent = "%0" + String.valueOf(length) + "d";
                pointer = String.format(formatContent, initialVal);
            }
            serialNumberMaintain.setPointer(pointer);
            serialNumberMaintain.setIsEditor(isEditor);
            serialNumberMaintainService.save(serialNumberMaintain);
            serialNumberMaintainService.flushSession();
        } else {
            if (!headPart.equals(serialNumberMaintain.getHeadPart()) || lastPart.equals(serialNumberMaintain.getLastPart())) {
                serialNumberMaintain.setHeadPart(headPart);
                serialNumberMaintain.setLastPart(lastPart);
                serialNumberMaintainService.update(serialNumberMaintain);
                serialNumberMaintainService.flushSession();
            }
        }

        String sql = "select headPart,lastPart,id from SerialNumberMaintain where id = :id order by createTime asc";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", serialNumberId);
        List<QueryItem> queryItemList = this.listQueryItemByHQL(sql, params, null);
        if (BooleanUtils.isFalse(filleNumberBtnFlg) && queryItemList.size() > 0) {
            queryItemList = queryItemList.subList(queryItemList.size() - 1, queryItemList.size());
        } else {
            List<QueryItem> itemList = new ArrayList<>();
            Set<String> set = new HashSet<>();
            for (QueryItem queryItem : queryItemList) {
                String qheadPart = queryItem.getString("0");
                String qlastPart = queryItem.getString("1");
                String key = StringUtils.join(qheadPart, qlastPart);
                if (!set.contains(key)) {
                    itemList.add(queryItem);
                    set.add(key);
                }
            }
            return itemList;
        }
        return queryItemList;
    }

    /**
     * 接口解析，实体集合
     * <p>
     * (non-Javadoc)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> String getSerialNumber(String id, Collection<ENTITY> entities, Boolean isOccupied,
                                                            Map<String, Object> dytableMap, String fieldName) throws Exception {
        SerialNumber serialNumber = dao.getById(id);

        String type = serialNumber.getType();
        String name = serialNumber.getName();
        String numberId = serialNumber.getId();
        String keyPart = serialNumber.getKeyPart();
        String headPart = serialNumber.getHeadPart();
        String initialValue = serialNumber.getInitialValue();
        Boolean isFillPosition = serialNumber.getIsFillPosition();
        Integer incremental = serialNumber.getIncremental();
        String lastPart = serialNumber.getLastPart();
        Boolean isFillNumber = serialNumber.getIsFillNumber();
        String startDate = serialNumber.getStartDate();
        Boolean isEditor = serialNumber.getIsEditor();
        String remark = serialNumber.getRemark();

        // 将关键字定义格式全部替换成${}的形式
        keyPart = replaceKeyWord(keyPart);
        headPart = replaceKeyWord(headPart);
        lastPart = replaceKeyWord(lastPart);

        Map<Object, Object> keyWordMap = getMapValue(startDate, entities, dytableMap);
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        try {
            // 替换模板关键字为Map集合里面的值
            keyPart = templateEngine.process(keyPart, keyWordMap);
            // System.out.println(keyPart);
            headPart = templateEngine.process(headPart, keyWordMap);
            // System.out.println(headPart);
            lastPart = templateEngine.process(lastPart, keyWordMap);
            // System.out.println(lastPart);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return null;
        }

        keyPart = keyPart == null ? "" : keyPart;
        headPart = headPart == null ? "" : headPart;
        lastPart = lastPart == null ? "" : lastPart;

        SerialNumberBuildParams params = new SerialNumberBuildParams();
        params.setSerialNumberId(numberId);
        ENTITY entity = null;
        if (CollectionUtils.isNotEmpty(entities)) {
            entity = entities.stream().findFirst().get();
            if (StringUtils.isNotBlank(entity.getUuid())) {
                SessionFactoryImpl sessionFactory = (SessionFactoryImpl) dao.getSession().getSessionFactory();
                SingleTableEntityPersister entityPersister = (SingleTableEntityPersister) sessionFactory.getEntityPersister(entity.getClass().getName());
                String tableName = entityPersister.getTableName();
                String columnName = entityPersister.getSubclassPropertyColumnNames(fieldName)[0];
                params.setDataUuid(entity.getUuid());
                params.setTableName(tableName);
                params.setFormField(columnName);
            }
        }
        SerialNumberMaintain serialNumberMaintain = serialNumberMaintainService.getByIdAndKeyPart(id, keyPart);
        String serialNum = null;
        if (serialNumberMaintain != null) {
            SerialNumberMaintainBean serialNumberMaintainBean = new SerialNumberMaintainBean();
            BeanUtils.copyProperties(serialNumberMaintain, serialNumberMaintainBean);
            String pointer = serialNumberMaintain.getPointer();// 如果流水号维护中存在的话就获取当前的指针值
            // 补号处理
            if (isFillNumber && entity != null) {
                List<SerialNumberSupplementBean> supplementBeans = serialNumberFacadeService.supplement(params.getSerialNumberId(), Lists.newArrayList(serialNumberMaintainBean));
                if (CollectionUtils.isNotEmpty(supplementBeans)) {
                    SerialNumberMaintainBean maintain = new SerialNumberMaintainBean();
                    BeanUtils.copyProperties(serialNumberMaintain, maintain);
                    for (SerialNumberSupplementBean supplementBean : supplementBeans) {
                        for (Integer i : supplementBean.getPointerList()) {
                            maintain.setHeadPart(headPart);
                            maintain.setLastPart(lastPart);
                            // 若有补位，则返回的补号也要有补位
                            if (isFillPosition) {
                                int length = initialValue.length();
                                String formatContent = "%0" + String.valueOf(length) + "d";
                                String temp = String.format(formatContent, i);
                                maintain.setPointer(temp);
                            } else {
                                maintain.setPointer(i + "");
                            }
                            serialNum = maintain.getHeadPart() + maintain.getPointer() + maintain.getLastPart();
                            this.saveSerialNumberRecord(params, maintain);
                            return serialNum;
                        }
                    }
                }

                pointer = String.valueOf(Integer.valueOf(pointer) + incremental);// 若没有补号则指针值加上增量
                // 补位处理
                if (isFillPosition) {
                    int length = initialValue.length();
                    String formatContent = "%0" + String.valueOf(length) + "d";
                    pointer = String.format(formatContent, Integer.valueOf(pointer));
                }
                if (isOccupied) {
                    serialNumberMaintain.setPointer(pointer);
                    serialNumberMaintain.setHeadPart(headPart);
                    serialNumberMaintain.setLastPart(lastPart);
                    serialNumberMaintain.setIsEditor(isEditor);
                    serialNumberMaintainService.save(serialNumberMaintain);

                    serialNumberMaintainBean.setPointer(pointer);
                    serialNumberMaintainBean.setHeadPart(headPart);
                    serialNumberMaintainBean.setLastPart(lastPart);
                    this.saveSerialNumberRecord(params, serialNumberMaintainBean);
                }
                serialNum = headPart + pointer + lastPart;
            } else {
                pointer = String.valueOf(Integer.valueOf(pointer) + incremental);// 若没有补号则指针值加上增量
                // 补位处理
                if (isFillPosition) {
                    int length = initialValue.length();
                    String formatContent = "%0" + String.valueOf(length) + "d";
                    pointer = String.format(formatContent, Integer.valueOf(pointer));
                }
                // 若占用
                if (isOccupied) {
                    serialNumberMaintain.setPointer(pointer);
                    serialNumberMaintain.setHeadPart(headPart);
                    serialNumberMaintain.setLastPart(lastPart);
                    serialNumberMaintain.setIsEditor(isEditor);
                    serialNumberMaintainService.save(serialNumberMaintain);

                    serialNumberMaintainBean.setPointer(pointer);
                    serialNumberMaintainBean.setHeadPart(headPart);
                    serialNumberMaintainBean.setLastPart(lastPart);
                    this.saveSerialNumberRecord(params, serialNumberMaintainBean);
                }
                serialNum = headPart + pointer + lastPart;
            }

        } else {
            SerialNumberMaintain maintain = new SerialNumberMaintain();
            maintain.setId(numberId);
            maintain.setName(name);
            maintain.setKeyPart(keyPart);
            maintain.setHeadPart(headPart);
            maintain.setLastPart(lastPart);
            maintain.setPointer(initialValue);
            maintain.setIsEditor(isEditor);
            // 若被占用，存入数据库
            if (isOccupied) {
                serialNumberMaintainService.save(maintain);
                SerialNumberMaintainBean serialNumberMaintainBean = new SerialNumberMaintainBean();
                BeanUtils.copyProperties(maintain, serialNumberMaintainBean);
                this.saveSerialNumberRecord(params, serialNumberMaintainBean);
            }
            serialNum = headPart + initialValue + lastPart;
        }
        return serialNum;

    }

    /**
     * 获取所有流水号id集合
     *
     * @return
     */
    @Override
    public List<String> getSerialNumberIdList() {
        List<SerialNumber> serialNumberList = listAll();
        List<String> serialNumberIdList = new ArrayList<String>();
        for (SerialNumber serialNumber : serialNumberList) {
            serialNumberIdList.add(serialNumber.getName());
        }
        return serialNumberIdList;
    }

    /**
     * 获取所有流水号id集合
     *
     * @return
     */
    @Override
    public List<TreeNode> getSerialNumberNameList(String name) {
        List<SerialNumber> serialNumberList = listAll();
        List<TreeNode> serialNumberIdList = new ArrayList<TreeNode>();
        for (SerialNumber serialNumber : serialNumberList) {
            TreeNode treeNode = new TreeNode();
            treeNode.setId("-1");

            treeNode.setName(serialNumber.getName());
            treeNode.setId(serialNumber.getId());

            serialNumberIdList.add(treeNode);
        }
        return serialNumberIdList;
    }

    /**
     * 获取所有流水号类型集合
     *
     * @return
     */
    @Override
    public List<String> getSerialNumberTypeList() {
        List<SerialNumber> serialNumberList = listAll();
        List<String> serialNumberTypeList = new ArrayList<String>();
        for (SerialNumber serialNumber : serialNumberList) {
            // 获取所有不重复的流水号类型
            if (Collections.frequency(serialNumberTypeList, serialNumber.getType()) < 1)
                serialNumberTypeList.add(serialNumber.getType());
        }
        return serialNumberTypeList;
    }

    /**
     * 获取所有流水号类型集合
     *
     * @return
     */
    @Override
    public List<TreeNode> getSerialTypeList(String name) {
        List<SerialNumber> serialNumberList = listAll();
        List<String> serialNumberTypeList1 = new ArrayList<String>();
        List<TreeNode> serialNumberTypeList = new ArrayList<TreeNode>();
        for (SerialNumber serialNumber : serialNumberList) {
            // 获取所有不重复的流水号类型
            if (Collections.frequency(serialNumberTypeList1, serialNumber.getType()) < 1) {
                TreeNode treeNode = new TreeNode();
                treeNode.setId("-1");

                treeNode.setName(serialNumber.getType());
                treeNode.setId(serialNumber.getId());

                serialNumberTypeList.add(treeNode);
                serialNumberTypeList1.add(serialNumber.getType());
            }
        }
        return serialNumberTypeList;
    }

    @Override
    public List<TreeNode> listSerialNumberNodes() {
        List<SerialNumber> serialNumberList = listAll();
        List<TreeNode> serialNumberIdList = new ArrayList<TreeNode>();
        for (SerialNumber serialNumber : serialNumberList) {
            TreeNode treeNode = new TreeNode();
            treeNode.setName(serialNumber.getName());
            treeNode.setId(serialNumber.getId());
            serialNumberIdList.add(treeNode);
        }
        return serialNumberIdList;
    }

    @Override
    public List<TreeNode> listAllSerialTypeNodes() {
        List<SerialNumber> serialNumberList = listAll();
        List<TreeNode> serialNumberTypeList = new ArrayList<TreeNode>();
        Set<String> types = Sets.newHashSet();
        for (SerialNumber serialNumber : serialNumberList) {
            boolean isAdd = StringUtils.isBlank(serialNumber.getType()) ? types.add("无分类") : types.add(serialNumber
                    .getType());
            if (isAdd) {
                TreeNode treeNode = new TreeNode();
                treeNode.setName(StringUtils.isBlank(serialNumber.getType()) ? "无分类" : serialNumber.getType());
                treeNode.setId(treeNode.getName());
                serialNumberTypeList.add(treeNode);
            }

        }
        return serialNumberTypeList;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.serialnumber.service.SerialNumberService#getById(java.lang.String)
     */
    @Override
    public SerialNumber getById(String serialNumberId) {
        return dao.getById(serialNumberId);
    }

    @Override
    public List<SerialNumber> getAllByName(String name) {
        return dao.listByFieldEqValue("name", name);
    }

}
