package com.wellsoft.pt.dyform.implement.definition.constant;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.InputMode.EnumFileInputMode;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.InputMode.EnumOrgInputMode;
import com.wellsoft.pt.dyform.implement.definition.control.type.ControlType;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.security.core.userdetails.DefaultUserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.core.userdetails.UserSystemOrgDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 该类要实时与前台的dyform_constantw文件保存同步
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-6-23.1	hunt		2014-6-23		Create
 * </pre>
 * @date 2014-6-23
 */

public class DyFormConfig {
    public static final String CHARSET = "UTF-8";
    public static final String digestAlgorithm = "MD5";
    // 表单数据签名，JCR结点名后缀
    public static final String DYTABLE_SIGNATURE_NODE_NAME_SUFFIX = "_signature";
    public static final Integer DYTABLE_VERIFICATION_NOTNULL = 1;
    public static final Integer DYTABLE_VERIFICATION_URL = 2;
    public static final Integer DYTABLE_VERIFICATION_EMAIL = 3;
    public static final Integer DYTABLE_VERIFICATION_ISCARD = 4;
    public static final Integer DYTABLE_VERIFICATION_UNIQUE = 5;
    public static final String INPUTMODE_Text = "1";// 普通表单输入
    public static final String INPUTMODE_CKEDIT = "2";// 富文本编辑
    public static final String INPUTMODE_ACCESSORY = EnumFileInputMode.INPUTMODE_ACCESSORY.getValue();// 附件
    public static final String INPUTMODE_ACCESSORY1 = EnumFileInputMode.INPUTMODE_ACCESSORY1.getValue();// 图标显示
    public static final String INPUTMODE_ACCESSORY2 = EnumFileInputMode.INPUTMODE_ACCESSORY2.getValue();// 图标显示（含正文）
    public static final String INPUTMODE_ACCESSORY3 = EnumFileInputMode.INPUTMODE_ACCESSORY3.getValue();// 列表显示（不含正文）
    public static final String INPUTMODE_ACCESSORYIMG = EnumFileInputMode.INPUTMODE_ACCESSORYIMG.getValue();// 图片上传控件
    public static final String INPUTMODE_FILEUPLOAD = EnumFileInputMode.INPUTMODE_FILEUPLOAD.getValue();// 附件上传
    public static final String INPUTMODE_TEXTBODY = EnumFileInputMode.INPUTMODE_TEXTBODY.getValue();// 正文
    public static final String INPUTMODE_SerialNumber = "7";// 可编辑流水号
    public static final String INPUTMODE_ORGSELECT = EnumOrgInputMode.INPUTMODE_ORGSELECT.getValue();// 组织选择框
    public static final String INPUTMODE_ORGSELECTSTAFF = EnumOrgInputMode.INPUTMODE_ORGSELECTSTAFF.getValue();// 组织选择框（人员）
    public static final String INPUTMODE_ORGSELECTDEPARTMENT = EnumOrgInputMode.INPUTMODE_ORGSELECTDEPARTMENT
            .getValue();// 组织选择框（部门）
    public static final String INPUTMODE_ORGSELECTSTADEP = EnumOrgInputMode.INPUTMODE_ORGSELECTSTADEP.getValue();// 组织选择框（部门+人员）
    public static final String INPUTMODE_ORGSELECTADDRESS = EnumOrgInputMode.INPUTMODE_ORGSELECTADDRESS.getValue();// 组织选择框
    // (单位通讯录)
    public static final String INPUTMODE_ORGSELECTJOB = EnumOrgInputMode.INPUTMODE_ORGSELECTJOB.getValue();// 组织选择框
    // (部门+职位)
    public static final String INPUTMODE_ORGSELECTPUBLICGROUP = EnumOrgInputMode.INPUTMODE_ORGSELECTPUBLICGROUP
            .getValue();// 组织选择框 (群组)
    public static final String INPUTMODE_ORGSELECTMYDEPT = EnumOrgInputMode.INPUTMODE_ORGSELECTMYDEPT.getValue();// 组织选择框(我的部门)
    public static final String INPUTMODE_ORGSELECTMYPARENTDEPT = EnumOrgInputMode.INPUTMODE_ORGSELECTMYPARENTDEPT
            .getValue();// 组织选择框(上级部门)
    public static final String INPUTMODE_ORGSELECTMYUNIT = EnumOrgInputMode.INPUTMODE_ORGSELECTMYUNIT.getValue();// 组织选择框(部门+职位+人员)
    public static final String INPUTMODE_ORGSELECTPUBLICGROUPSTA = EnumOrgInputMode.INPUTMODE_ORGSELECTPUBLICGROUPSTA
            .getValue();// 组织选择框 (群组+人员)
    public static final String INPUTMODE_ORGSELECTGROUP = EnumOrgInputMode.INPUTMODE_ORGSELECTGROUP.getValue();// 组织选择框
    // (集团通讯录)
    public static final String INPUTMODE_ORGSELECT2 = EnumOrgInputMode.INPUTMODE_ORGSELECT2.getValue();// 组织选择框2
    public static final String INPUTMODE_TIMEEMPLOY = "12";// 资源选择
    public static final String INPUTMODE_TIMEEMPLOYFORMEET = "13";// 资源选择（会议）
    public static final String INPUTMODE_TIMEEMPLOYFORCAR = "14";// 资源选择（车辆）
    public static final String INPUTMODE_TIMEEMPLOYFORDRIVER = "15";// 资源选择（司机）
    public static final String INPUTMODE_TREESELECT = "16";// 树形下拉框
    public static final String INPUTMODE_RADIO = "17";// radio表单元素
    public static final String INPUTMODE_CHECKBOX = "18";// checkbox表单元素
    public static final String INPUTMODE_SELECTMUTILFASE = "19";// 下拉单选框
    public static final String INPUTMODE_COMBOSELECT = "191";// 下拉选项框
    public static final String INPUTMODE_SELECT = "199";// select2普通下拉框（新版）
    public static final String INPUTMODE_TEXTAREA = "20";// 文本域输入
    public static final String INPUTMODE_INT = "23";
    public static final String INPUTMODE_LONG = "24";
    public static final String INPUTMODE_FLOAT = "25";
    public static final String INPUTMODE_DIALOG = "26";// 弹出框
    public static final String INPUTMODE_XML = "27";// XML
    public static final String INPUTMODE_UNEDITSERIALUMBER = "29";// 不可编辑流水号
    public static final String INPUTMODE_DATE = "30";// 日期
    public static final String INPUTMODE_NUMBER = "31";// 数字输入框
    public static final String INPUTMODE_VIEWDISPLAY = "32";// 视图展示
    public static final String INPUTMODE_EMBEDDED = "40";// url嵌入页面
    public static final String INPUTMODE_JOB = "41";// 职位控件
    public static final String INPUTMODE_TEMPLATE = "44";// 模板
    public static final String INPUTMODE_TABLEVIEW = "45";// 视图列表控件
    public static final String INPUTTYPE_TEXT = "1";// 文本输入框，允许输入字符串
    public static final String INPUTTYPE_DATE = "2";// 日期选择，只允许日期控件丢或者手动输入合法的日期
    public static final String INPUTTYPE_DATETIMEHOUR = "6";// 日期到时
    public static final String INPUTTYPE_DATETIMEMIN = "7";// 日期到分
    public static final String INPUTTYPE_DATETIMESEC = "8";// 日期到秒
    public static final String INPUTTYPE_TIMEHOUR = "9";// 时间到时
    public static final String INPUTTYPE_TIMEMIN = "10";// 时间到分
    public static final String INPUTTYPE_TIMESEC = "11";// 时间到秒
    public static final String INPUTTYPE_INT = "13";// 文本框整数输入
    public static final String INPUTTYPE_INT_POSITIVE = "131";// 文本框正整数输入
    public static final String INPUTTYPE_INT_NEGTIVE = "132";// 文本框负整数输入
    public static final String INPUTTYPE_LONG = "14";// 长整型输入
    public static final String INPUTTYPE_FLOAT = "15";// 浮点数输入
    public static final String INPUTTYPE_CLOB = "16";// 大字段
    public static final String INPUTTYPE_TAGGROUP = "126";// 标签组
    public static final String INPUTTYPE_CHAINED = "61";// 级联控件
    public static final String INPUTTYPE_COLORS = "127";// 颜色
    public static final String INPUTTYPE_SWITCHS = "128";// 按钮开关
    public static final String INPUTTYPE_PROGRESS = "129";// 按钮开关
    public static final String INPUTTYPE_PLACEHOLDER = "130";// 按钮开关
    public static final String BODY_FILEID_PREFIX = "BODY_FILEID_";
    public static final String ORDER_SUBFORM_FIELDNAME_PREFIX = "_ORDER";
    public static final String assistedpofix4realValue = "_real";
    public static final String assistedpofix4DisplayValue = "_display";
    public static final String DYFORM_RELATIONTBL_POSTFIX = "_RL";
    public static final String VIEWNAME_OF_SUBFORM = "subformdataview";
    public static final String DYRELATIVEMETHOD_FIELD = "relativeMethod";
    public static final String DYRELATIVEMETHOD_DIALOG = "1";
    public static final String DYRELATIVEMETHOD_SEARCH = "2";
    public static final String INPUTMODE_FILELIBRARY = "133";// 文件库列表控件
    private static Logger logger = LoggerFactory.getLogger(DyFormConfig.class);

    public enum EnumDySysVariable {
        currentYearMonthDate("{CURRENTYEARMONTHDATE}", "yyyy-MM-dd"), // 当前日期(2000-01-01)
        currentYearMonthDateCn("{CURRENTYEARMONTHDATECN}", "yyyy年MM月dd日"), // 当前日期(2000年1月1日)
        currentYearCn("{CURRENTYEARCN}", "yyyy年"), // 当前日期(2000年)
        currentYearMonthCn("{CURRENTYEARMONTHCN}", "yyyy年MM月"), // 当前日期(2000年1月)
        currentMonthDateCn("{CURRENTMONTHDATECN}", "MM月dd日"), // 当前日期(1月1日)
        currentWeekCn("{CURRENTWEEKCN}", "EEE"), // 当前日期(星期一)
        currentYear("{CURRENTYEAR}", "yyyy"), // 当前年份(2000)
        currentTimeMin("{CURRENTTIMEMIN}", "HH:mm"), // 当前时间(12 : 00)
        currentTimeSec("{CURRENTTIMESEC}", "HH:mm:ss"), // 当前时间(12 : 00 : 00)
        currentDateTimeMin("{CURRENTDATETIMEMIN}", "yyyy-MM-dd HH:mm"), // 日期到分
        // 当前日期时间(2000-01-01
        // 12 :
        // 00)
        currentDateTimeSec("{CURRENTDATETIMESEC}", "yyyy-MM-dd HH:mm:ss"), // 日期到秒
        // 当前日期时间(2000-01-01
        // 12
        // :
        // 00
        // :
        // {当前用户}
        currentUser("{CURRENTUSER}", "") {
            @Override
            public String getValue() {
                UserDetails user = SpringSecurityUtils.getCurrentUser();
                if (user != null) {
                    return user.getUserName();
                }
                return null;
            }
        },
        // {当前用户ID}
        currentUserId("{CURRENTUSERID}", "") {
            @Override
            public String getValue() {
                return SpringSecurityUtils.getCurrentUserId();
            }
        },
        // {当前用户姓名}
        currentUserName("{CURRENTUSERNAME}", "") {
            @Override
            public String getValue() {
                return SpringSecurityUtils.getCurrentLocalUserName();
            }
        },
        // {当前用户部门}
        currentUserDepartment("{CURRENTUSERDEPARTMENT}", "") {
            @Override
            public String getValue() {
                DefaultUserDetails user = SpringSecurityUtils.getCurrentUser();
                return user.getLocalMainDepartmentPath();
            }
        },
        // {当前用户部门ID}
        currentUserDepartmentId("{CURRENTUSERDEPARTMENTID}", "") {
            @Override
            public String getValue() {
                return SpringSecurityUtils.getCurrentUserDepartmentId();
            }
        },
        // {当前用户部门(短名称)}
        currentUserDepartmentName("{CURRENTUSERDEPARTMENTNAME}", "") {
            @Override
            public String getValue() {
                return SpringSecurityUtils.getCurrentUserLocalDepartmentName();
            }
        },
        // {当前用户部门(长名称)}
        currentUserDepartmentPath("{CURRENTUSERDEPARTMENTPATH}", "") {
            @Override
            public String getValue() {
                DefaultUserDetails user = SpringSecurityUtils.getCurrentUser();
                return user.getLocalMainDepartmentPath();
            }
        },
        // {当前用户主职位}
        currentUserMainJob("{CURRENTUSERMAINJOB}", "") {
            @Override
            public String getValue() {
                UserDetails user = SpringSecurityUtils.getCurrentUser();
                if (user != null) {
                    if (user.getMainJob() != null) {
                        return StringUtils.defaultIfBlank(user.getMainJob().getLocalName(), user.getMainJob().getName());
                    }
                    if (CollectionUtils.isNotEmpty(user.getOtherJobs())) {
                        return StringUtils.defaultIfBlank(user.getOtherJobs().get(0).getLocalName(), user.getOtherJobs().get(0).getName());
                    }
                }
                return null;
            }
        },
        // {当前用户主职位ID}
        currentUserMainJobId("{CURRENTUSERMAINJOBID}", "") {
            @Override
            public String getValue() {
                UserDetails user = SpringSecurityUtils.getCurrentUser();
                OrgTreeNodeDto job = user.getMainJob();
                if (job != null) {
                    return job.getEleId();
                }

                if (CollectionUtils.isNotEmpty(user.getOtherJobs())) {
                    return user.getOtherJobs().get(0).getEleId();
                }
                return null;
            }
        },
        // {当前用户主职位(短名称)}
        currentUserMainJobName("{CURRENTUSERMAINJOBNAME}", "") {
            @Override
            public String getValue() {
                UserDetails user = SpringSecurityUtils.getCurrentUser();
                OrgTreeNodeDto job = user.getMainJob();
                if (job != null) {
                    return StringUtils.defaultIfBlank(job.getLocalName(), job.getName());
                }
                if (CollectionUtils.isNotEmpty(user.getOtherJobs())) {
                    return StringUtils.defaultIfBlank(user.getOtherJobs().get(0).getLocalName(), user.getOtherJobs().get(0).getName());
                }
                return null;
            }
        },
        // {当前用户主职位(长名称)}
        currentUserMainJobPath("{CURRENTUSERMAINJOBPATH}", "") {
            @Override
            public String getValue() {
                DefaultUserDetails user = SpringSecurityUtils.getCurrentUser();
                if (user != null) {
                    if (user.getMainJob() != null) {
                        return StringUtils.defaultIfBlank(user.getMainJob().getLocalEleNamePath(), user.getMainJob().getEleNamePath());
                    }

                    if (CollectionUtils.isNotEmpty(user.getOtherJobs())) {
                        return StringUtils.defaultIfBlank(user.getOtherJobs().get(0).getLocalEleNamePath(), user.getOtherJobs().get(0).getEleNamePath());
                    }
                }
                return null;
            }
        },
        // {当前用户业务单位}
        currentUserBizUnit("{CURRENTUSERBIZUNIT}", "") {
            @Override
            public String getValue() {
                // FIXME: 7.0 组织无业务单位
//                UserDetails user = SpringSecurityUtils.getCurrentUser();
//                OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
//                if (user.getMainJob() != null) {
//                    String id = user.getMainJob().getBusinessUnitId();
//                    MultiOrgElement ele = orgApiFacade.getOrgElementById(id);
//                    if (ele != null) {
//                        return ele.getName();
//                    }
//                }
                return null;
            }
        },
        // {当前用户的单位ID}
        currentUserBizUnitId("{CURRENTUSERBIZUNITID}", "") {
            @Override
            public String getValue() {
                DefaultUserDetails user = SpringSecurityUtils.getCurrentUser();
                UserSystemOrgDetails.OrgDetail details = user.getUserSystemOrgDetails().currentSystemOrgDetail();
                if (details != null && details.getUnit() != null) {
                    return details.getUnit().getEleId();
                }
//                UserDetails user = SpringSecurityUtils.getCurrentUser();
//                if (user != null && user.getMainJob() != null) {
//                    return user.getMainJob().getBusinessUnitId();
//                }
                return null;
            }
        },
        // {当前用户的单位名称}
        currentUserBizUnitNAME("{CURRENTUSERBIZUNITNAME}", "") {
            @Override
            public String getValue() {
//                UserDetails user = SpringSecurityUtils.getCurrentUser();
//                if (user != null && user.getMainJob() != null) {
//                    OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
//                    String id = user.getMainJob().getBusinessUnitId();
//                    MultiOrgElement ele = orgApiFacade.getOrgElementById(id);
//                    if (ele != null) {
//                        return ele.getName();
//                    }
//                }
                return null;

            }
        },
        // {当前用户的单位}
        currentUserSysUnit("{CURRENTUSERSYSUNIT}", "") {
            @Override
            public String getValue() {

                DefaultUserDetails user = SpringSecurityUtils.getCurrentUser();
                UserSystemOrgDetails.OrgDetail details = user.getUserSystemOrgDetails().currentSystemOrgDetail();
                if (details != null && details.getUnit() != null) {
                    return StringUtils.defaultIfBlank(details.getUnit().getLocalName(), details.getUnit().getName());
                }

//                UserDetails user = SpringSecurityUtils.getCurrentUser();
//                if (user != null) {
//                    return user.getSystemUnit().getName();
//                }


                return null;
            }
        },
        // {当前用户的单位ID}
        currentUserSysUnitId("{CURRENTUSERSYSUNITID}", "") {
            @Override
            public String getValue() {
                DefaultUserDetails user = SpringSecurityUtils.getCurrentUser();
                UserSystemOrgDetails.OrgDetail details = user.getUserSystemOrgDetails().currentSystemOrgDetail();
                if (details != null && details.getUnit() != null) {
                    return details.getUnit().getEleId();
                }
                return SpringSecurityUtils.getCurrentUserUnitId();
            }
        },
        // {当前用户的单位名称}
        currentUserSysUnitNAME("{CURRENTUSERSYSUNITNAME}", "") {
            @Override
            public String getValue() {
                DefaultUserDetails user = SpringSecurityUtils.getCurrentUser();
                UserSystemOrgDetails.OrgDetail details = user.getUserSystemOrgDetails().currentSystemOrgDetail();
                if (details != null && details.getUnit() != null) {
                    return details.getUnit().getEleId();
                }
//
//                UserDetails user = SpringSecurityUtils.getCurrentUser();
//                if (user != null) {
//                    return user.getSystemUnit().getName();
//                }
                return null;
            }
        };

        // currentCreatorId("{CURRENTCREATORID}", "") {
        // @Override
        // public String getValue() {
        //
        // return SpringSecurityUtils.getCurrentUserId();
        // }
        // }, // {创建人ID}
        // currentCreatorName("{CURRENTCREATORNAME}", "") {
        // @Override
        // public String getValue() {
        //
        // return SpringSecurityUtils.getCurrentUserName();
        // }
        // }, // {创建人姓名}
        // currentCreatorDepartmentPath("{CURRENTCREATORDEPARTMENTPATH}", "") {
        // @Override
        // public String getValue() {
        // return SpringSecurityUtils.getCurrentUserDepartmentPath();
        // }
        // }, // {创建人部门(长名称)}
        // currentCreatorDepartmentId("{CURRENTUSERUNIT}", "") {
        // @Override
        // public String getValue() {
        // return SpringSecurityUtils.getCurrentUserDepartmentId();
        // }
        // }, // {创建人部门ID}
        // currentCreatorDepartmentName("{CURRENTCREATORDEPARTMENTNAME}", "") {
        // @Override
        // public String getValue() {
        // return SpringSecurityUtils.getCurrentUserDepartmentName();
        // }
        // }, // {创建人用户单位}
        // currentCreatorMainJobName("{CURRENTCREATORMAINJOBNAME}", "") {
        // @Override
        // public String getValue() {
        // DyFormFacade dyFormApiFacade =
        // ApplicationContextHolder.getBean(DyFormFacade.class);
        // return dyFormApiFacade.getCurrentUserMainJobName();
        // }
        // };// {创建人主职位}

        SimpleDateFormat sdf = new SimpleDateFormat();
        private String key;
        private String pattern;

        private EnumDySysVariable(String key, String pattern) {
            this.key = key;
            this.pattern = pattern;
        }

        public static EnumDySysVariable key2EnumObj(String keyValue) {
            EnumDySysVariable enumObj = null;
            for (EnumDySysVariable status : EnumDySysVariable.values()) {
                if (status.key.equals(keyValue)) {
                    enumObj = status;
                }
            }
            return enumObj;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public String getValue() {
            sdf.applyPattern(this.getPattern());
            return sdf.format(new Date());
        }
    }

    /**
     * Description: 表单定义中,各属性名称
     *
     * @author hunt
     * @version 1.0
     *
     * <pre>
     * 修改记录:
     * 修改后版本	修改人		修改日期			修改内容
     * 2014-7-3.1	hunt		2014-7-3		Create
     * </pre>
     * @date 2014-7-3
     */
    public enum EnumFormPropertyName {
        tableName, // 表名
        uuid, // 定义uuid
        name, // 表单显示名称
        // applyTo, // 应用于
        fields, //
        subforms, // 从表的配置
        relationTbl, // 与数据表对应的数据关系表
        id, remark, // 描述
        // formDisplay, // 表单显示形式 ： 两种 一种是可编辑展示、 一种是直接展示文本,这个字段已没用
        // moduleId, // 模块ID
        // moduleName, // 模块名
        // printTemplateId, // 打印模板的ID
        // printTemplateName, // 打印模板的名称
        // displayFormModelName, // 显示单据的名称,这个字段没有用
        // displayFormModelId, // 显示单据对应的表单uuid
        code, // 表单编号
        version, // 版本 ,形式：1.0
        enableSignature, // 是否启用表单签名
        html, // 模板
        blocks, // 区域
        layouts, // 页签
        // showMobileModelId, // 手机显示单据
        // mobileConfig
        placeholderCtr, // 真实值占位符
        fileLibrary, // 文件库维护组件
        tableView// 视图列表
        , templates, master, defaultVFormUuid, defaultMFormUuid, dbCharacterSet, formType, pFormUuid, titleType, titleContent
    }

    /**
     * 字段定义中各属性名
     *
     * @author Administrator
     * @version 1.0
     *
     * <pre>
     * 修改记录:
     * 修改后版本	修改人		修改日期			修改内容
     * 2014-6-26.1	hunt		2014-6-26		Create
     * </pre>
     * @date 2014-6-26
     */
    public enum EnumFieldPropertyName {
        name(), // 字段名
        oldName, // 旧的字段名
        realDisplay, // 真实值与显示值的字段名，一个JSON对象,real为真实值的字段名，display为显示值的字段名
        showType(), // 编辑模式
        applyTo(), //
        displayName(), //
        dbDataType(), //
        indexed(), //
        showed(), //
        sorted(), //
        sysType(), //
        length(), //
        defaultValue(), //
        valueCreateMethod(), //
        onlyreadUrl(), //
        inputMode(), //
        textAlign(), //
        ctlWidth(), //
        ctlHight(), //
        fontSize(), //
        fontColor(), //
        fieldCheckRules(), //
        contentFormat(), //
        optionDataSource(), //
        dictCode(), //
        optionSet(), //
        dataSourceId(), //
        dataSourceFieldName(), //
        dataSourceDisplayName(), //
        dataSourceGroup(), //
        defaultCondition(), precision(), //
        scale(), //
        isShow(), //
        allowDownload(), //
        allowDelete(), //
        allowUpload(), //
        relationDataValueTwo(), //
        saveFileName2Field(), // 是否保存文件名称 add by zhangyh 20170411
        optionMap, refId, refObj, valSeparator, dataSources
    }

    // add by wujx 20160721
    public enum EnumDataSourceValueField {
        VALUE_FIELD("value"), NAME_FIELD("name"), GROUP_FIELD("group"), DATA_FIELD("data");

        private String field = "";

        private EnumDataSourceValueField(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }
    }

    public enum EnumSubformPropertyName {
        formUuid, outerId, name, displayName,

        isGroupShowTitle, // 是否要分组展示
        groupShowTitle, // 分组展示标题
        isGroupColumnShow, // 分组字段是否展示 20140701 add
        subformApplyTableId, subrRelationDataDefiantion, tableOpen, // 从表是展示还是折叠（收缩）

        editMode, // 编辑模式1.行内编辑 2.弹出窗口编辑
        hideButtons, // 1：不隐藏;2:隐藏
        fields, // 参照表单从表字段定义
        tableButtonInfo, // 从表按钮事件定义
        applyTo, showImpButton2, showExpButton2, hideButtons2, editableOperateBtns;
    }

    public enum EnumFieldPropertyName_fieldCheckRules {
        value(), label();
    }

    public enum EnumDyCheckRule {
        // 约束条件
        notNull("1", "非空"), // 非空
        unique("5", "唯一"), // 唯一校验

        // 文本样式，校验规则
        common("10", ""), //
        url("11", "url"), //
        email("12", "email"), //
        idCard("13", "身份证"), // 身份证
        tel("14", "固定电话"), // 固定电话
        mobilePhone("15", "手机");// 手机

        private String ruleKey = "";
        private String ruleLabel = "";

        private EnumDyCheckRule(String ruleKey, String ruleLabel) {
            this.ruleKey = ruleKey;
            this.ruleLabel = ruleLabel;
        }

        public String getRuleKey() {
            return ruleKey;
        }

        public String getRuleLabel() {
            return ruleLabel;
        }

    }

    public enum EnumSignature {

        enable("1", "不启用"), disable("2", "启用");

        private String value = "";
        private String remark = "";

        private EnumSignature(String value, String remark) {
            this.value = value;
            this.remark = remark;
        }

        public static EnumSignature value2EnumObj(String keyValue) {
            EnumSignature enumObj = null;
            for (EnumSignature status : EnumSignature.values()) {
                if (status.value.equalsIgnoreCase(keyValue)) {
                    enumObj = status;
                }
            }
            return enumObj;
        }

        public String getValue() {
            return value;
        }

        public String getRemark() {
            return remark;
        }

    }

    public enum EnumSubformFieldPropertyName {
        name, displayName, order, // 在展示从表时各字段的排列顺序
        sortable, // 在展示从表时是否允许列排序
        srcFieldName, // 来源字段
        hidden, // 从表字段是否展示
        width, // 宽度
        editable, // 可否编辑
        controlable, // 直接显示为控件
        formula, // 运算公式
        isShow, // 手机端是否展示
        sort// 行排序
    }

    public enum EnumHideSubFormOperateBtn {
        show("1", "不隐藏"), hide("2", "隐藏"), showAdd("3", "只显示添加"), showDel("4", "只显示删除");

        private String value = "";
        private String remark = "";

        private EnumHideSubFormOperateBtn(String value, String remark) {
            this.value = value;
            this.remark = remark;
        }

        public static EnumHideSubFormOperateBtn value2EnumObj(String keyValue) {
            EnumHideSubFormOperateBtn enumObj = null;
            for (EnumHideSubFormOperateBtn status : EnumHideSubFormOperateBtn.values()) {
                if (status.value.equalsIgnoreCase(keyValue)) {
                    enumObj = status;
                }
            }
            return enumObj;
        }

        public String getValue() {
            return value;
        }

        public String getRemark() {
            return remark;
        }

    }

    public enum EnumSubFormFieldShow {
        show("1", "不隐藏"), hide("2", "隐藏");

        private String value = "";
        private String remark = "";

        private EnumSubFormFieldShow(String value, String remark) {
            this.value = value;
            this.remark = remark;
        }

        public static EnumSubFormFieldShow value2EnumObj(String keyValue) {
            EnumSubFormFieldShow enumObj = null;
            for (EnumSubFormFieldShow status : EnumSubFormFieldShow.values()) {
                if (status.value.equalsIgnoreCase(keyValue)) {
                    enumObj = status;
                }
            }
            return enumObj;
        }

        public String getValue() {
            return value;
        }

        public String getRemark() {
            return remark;
        }

    }

    public enum EnumInputModeType {
        ATTACH, DATE, NUMBER, TEXT
    }

    /**
     * Description =  字段值的产生方式
     *
     * @author Administrator
     * @version 1.0
     *
     * <pre>
     * 修改记录 =
     * 修改后版本	修改人		修改日期			修改内容
     * 2014-6-8.1	hunt		2014-6-8		Create
     * </pre>
     * @date 2014-6-8
     */
    public interface ValueCreateMethod {

        String userImport = "1";// 用户输入
        String jsEquation = "2";// JS公式
        String creatOperation = "3";// 创建时计算
        String showOperation = "4";// 显示时计算
        String twoDimensionCode = "5";// 二维码
        String shapeCod = "6";// 条形码
        String relationDoc = "7";// 关联文档
    }

    /**
     * Description =  对应的数据库字段类型
     *
     * @author hunt
     * @version 1.0
     *
     * <pre>
     * 修改记录 =
     * 修改后版本	修改人		修改日期			修改内容
     * 2014-6-8.1	hunt		2014-6-8		Create
     * </pre>
     * @date 2014-6-8
     */
    public interface DbDataType {
        String _string = "1";// 字符串
        String _date = "2";// 日期
        String _int = "13";// 整数
        String _int_positive = "131";// 正整数
        String _int_negtive = "132";// 负整数
        String _long = "14";// 长整型
        String _float = "15";// 浮点数
        String _clob = "16";// 大字段
        String _double = "12";// 双精度浮点数
        String _number = "17";// number类型
        String _decimal = "151";
    }

    /**
     * 用于标识表单中各字段的类型
     */
    public interface DyFieldSysType {
        int system = 0;// 系统字段
        int custom = 2;// 用户自定义字段
        int admin = 1;// 管理员定义字段
        // int parentForm = 3;//该字段用于保存对应的记录的主表的数据uuid,及排序
        // int assist = 4;//辅助性字段
    }

    public interface DyDateFomat {
        /**
         * 当前日期当前日期(2000-01-01)
         **/
        String yearMonthDate = "1";// 当前日期(2000-01-01)
        /**
         * 当前日期(2000年1月1日)
         **/
        String yearMonthDateCn = "2";// 当前日期(2000年1月1日)
        String yearCn = "3";// 当前日期(2000年)
        String yearMonthCn = "4";// 当前日期(2000年1月)
        String monthDateCn = "5";// 当前日期(1月1日)
        // String weekCn = "6";//当前日期(星期一)
        String year = "7";// 当前年份(2000)
        String timeHour = "8";// 当前时间(12)
        String timeMin = "9";// 当前时间(12 = 00)
        String timeSec = "10";// 当前时间(12 = 00 = 00)
        String dateTimeHour = "11";// 日期到时 当前日期时间(2000-01-01 12)
        String dateTimeMin = "12";// 日期到分 当前日期时间(2000-01-01 12 = 00)
        String dateTimeSec = "13";// 日期到秒 当前日期时间(2000-01-01 12 = 00 = 00)
        String yearMonth = "14";// 当前日期(2000-01)
        String dateWeak = "15";// 第几周
        /**
         * 当前日期(2000-1-1)
         **/
        String yearMonthDate2 = "16";
        /**
         * 当前日期(2000年01月01日)
         **/
        String yearMonthDateCn2 = "17";
    }

    public interface DyShowType {
        String edit = "1";// 可编辑
        String showAsLabel = "2";// 直接以文本的形式显示
        String readonly = "3";// 有输入框但只读
        String disabled = "4";// 有输入框但被disabled
        String hide = "5";// 隐藏整行
    }

    public interface DyDataSourceType {
        String dataConstant = "1";
        String dataDictionary = "2";
        String dataView = "3";// 视图
        String dataSource = "4";// 数据源
        String optionProvider = "5";// 通过选项接口
    }

    public interface InputMode {
        public enum EnumFileInputMode {
            INPUTMODE_ACCESSORY("3", "附件"), // 附件
            INPUTMODE_ACCESSORY1("4", "图标显示"), // 图标显示
            INPUTMODE_ACCESSORY2("5", "图标显示（含正文）"), // 图标显示（含正文）
            INPUTMODE_ACCESSORY3("6", "列表显示（不含正文）"), // 列表显示（不含正文）
            INPUTMODE_ACCESSORYIMG("33", "图片上传控件"), // 图片上传控件
            INPUTMODE_FILEUPLOAD("21", "附件上传"), // 附件上传
            INPUTMODE_TEXTBODY("22", "正文");// 正文

            private String value;
            private String remark;

            private EnumFileInputMode(String value, String remark) {
                this.value = value;
                this.remark = remark;
            }

            public static boolean isValueInEnum(String value) {
                if (value == null || value.trim().length() == 0) {
                    return false;
                }
                for (EnumFileInputMode mode : EnumFileInputMode.values()) {
                    if (mode.getValue().equals(value.trim())) {
                        return true;
                    }
                }

                return false;
            }

            public String getValue() {
                return value;
            }

            public String getRemark() {
                return remark;
            }
        }

        public enum EnumOrgInputMode {
            INPUTMODE_ORGSELECT("8", "组织选择框"), // 附件
            INPUTMODE_ORGSELECTSTAFF("9", "组织选择框（人员）"), // 附件
            INPUTMODE_ORGSELECTDEPARTMENT("10", "组织选择框（部门）"), // 附件
            INPUTMODE_ORGSELECTSTADEP("11", "组织选择框（部门+人员）"), // 附件
            INPUTMODE_ORGSELECTADDRESS("28", "组织选择框 (单位通讯录)"), // 附件
            INPUTMODE_ORGSELECTJOB("51", "组织选择框 (部门+职位)"), // 附件
            INPUTMODE_ORGSELECTPUBLICGROUP("52", "组织选择框 (群组)"), // 附件
            INPUTMODE_ORGSELECTMYDEPT("53", "组织选择框(我的部门)"), // 附件
            INPUTMODE_ORGSELECTMYPARENTDEPT("54", "组织选择框(上级部门)"), // 附件
            INPUTMODE_ORGSELECTMYUNIT("55", "组织选择框(部门+职位+人员)"), // 附件
            INPUTMODE_ORGSELECTPUBLICGROUPSTA("56", "组织选择框 (群组+人员)"), // 附件
            INPUTMODE_ORGSELECTGROUP("57", "组织选择框 (集团通讯录)"), // 附件
            INPUTMODE_ORGSELECT2("43", "组织选择框2");// 附件

            private String value;
            private String remark;

            private EnumOrgInputMode(String value, String remark) {
                this.value = value;
                this.remark = remark;
            }

            public String getValue() {
                return value;
            }

            public String getRemark() {
                return remark;
            }
        }

    }

    public static class ControlTypeUtils {
        @Autowired(required = false)
        static Map<String, ControlType> controlTypes = null;
        static Map<String, ControlType> inputModeControlTypes = new HashMap<String, ControlType>();
        private static Logger logger = LoggerFactory.getLogger(ControlTypeUtils.class);

        static {
            // 设置控件类型
            setControlTypes();
        }

        /**
         * 设置控件类型
         */
        private static void setControlTypes() {
            if (controlTypes == null) {
                controlTypes = // SpringContextHolder.getApplicationContext().getBeansOfType(ControlType.class);
                        ApplicationContextHolder.getApplicationContext().getBeansOfType(ControlType.class);
                for (ControlType controlType : controlTypes.values()) {
                    String inputMode = controlType.getInputMode();
                    if (inputModeControlTypes.containsKey(inputMode)) {// inputMode必须是唯一的
                        String msg = "存在两个值为" + inputMode + "的控件类型";
                        RuntimeException e = new RuntimeException(msg);
                        logger.error(msg, e);
                        throw e;
                    }
                    inputModeControlTypes.put(inputMode, controlType);
                }
            }
        }

        /**
         * 根据inputMode获取控件类型
         *
         * @param inputMode
         * @return
         */
        public static ControlType getControlType(String inputMode) {
            // setControlTypes();
            ControlType type = inputModeControlTypes.get(inputMode);
            if (type == null) {
                logger.error(" cann't get control type which inputMode = " + inputMode);
            }
            return type;
        }

        /**
         * 获取所有的控件类型
         *
         * @return
         */
        public static Collection<ControlType> getControlTypes() {
            // setControlTypes();
            return controlTypes.values();
        }

        public static final Boolean isInputModeEqAttach(String inputMode) {
            ControlType controlType = getControlType(inputMode);
            return controlType == null ? false : controlType.isInputModeAsAttach();
        }

        public static final Boolean isInputModeEqDate(String inputMode) {
            ControlType controlType = getControlType(inputMode);
            return controlType == null ? false : controlType.isInputModeAsDate();
        }

        public static boolean isInputModeEqNumber(String inputMode) {
            ControlType controlType = getControlType(inputMode);
            return controlType == null ? false : controlType.isInputModeAsNumber();
        }

        public static boolean isInputModeEqText(String inputMode) {
            ControlType controlType = getControlType(inputMode);
            return controlType == null ? false : controlType.isInputModeAsText();
        }
    }

    @Deprecated
    public static class InputModeUtils {
        public static final Boolean isInputModeEqAttach(String inputMode) {
            return ControlTypeUtils.isInputModeEqAttach(inputMode);
        }

        public static final Boolean isInputModeEqDate(String inputMode) {
            return ControlTypeUtils.isInputModeEqDate(inputMode);
        }

        public static boolean isInputModeEqNumber(String inputMode) {
            return ControlTypeUtils.isInputModeEqNumber(inputMode);
        }

        public static boolean isInputModeEqText(String inputMode) {
            return ControlTypeUtils.isInputModeEqText(inputMode);
        }

    }

    public static class DbDataTypeUtils {

        /**
         * 字段的对应的数据库类型是不是长整型
         *
         * @param fieldName
         * @return
         */
        public static boolean isDbDataTypeEqLong(String dbDataType) {

            if (DyFormConfig.DbDataType._long.equals(dbDataType)) {
                return true;
            }

            return false;
        }

        /**
         * 判断字段数据库类型是否为int类型
         *
         * @param fieldName
         * @return
         */
        public static boolean isDbDataTypeEqInt(String dbDataType) {

            if (DyFormConfig.DbDataType._int.equals(dbDataType)
                    || DyFormConfig.DbDataType._int_positive.equals(dbDataType)
                    || DyFormConfig.DbDataType._int_negtive.equals(dbDataType)) {
                return true;
            }
            return false;
        }

        /**
         * 判断字段数据库类型是否为number类型
         *
         * @param fieldName
         * @return
         */
        public static boolean isDbDataTypeEqNumber(String dbDataType) {
            // TODO
            if (DyFormConfig.DbDataType._number.equals(dbDataType)) {
                return true;
            }
            return false;
        }

        /**
         * 判断字段数据库类型是否为float类型
         *
         * @param fieldName
         * @return
         */
        public static boolean isDbDataTypeEqFloat(String dbDataType) {

            if (DyFormConfig.DbDataType._float.equals(dbDataType)) {
                return true;
            }
            return false;
        }

        public static boolean isDbDataTypeEqDouble(String dbDataType) {
            if (DyFormConfig.DbDataType._double.equals(dbDataType)) {
                return true;
            }
            return false;
        }

        /**
         * 判断字段数据库类型是否为date类型
         *
         * @param fieldName
         * @return
         */
        public static boolean isDbDataTypeEqDate(String dbDataType) {
            if (DyFormConfig.DbDataType._date.equals(dbDataType)) {
                return true;
            }
            return false;
        }

        /**
         * 判断字段数据库类型是否为string类型
         *
         * @param fieldName
         * @return
         */
        public static boolean isDbDataTypeEqString(String dbDataType) {
            if (DyFormConfig.DbDataType._string.equals(dbDataType)) {
                return true;
            }
            return false;
        }

        public static boolean isDbDataTypeAsNumber(String dbDataType) {
            if (isDbDataTypeEqFloat(dbDataType) || isDbDataTypeEqInt(dbDataType) || isDbDataTypeEqLong(dbDataType)
                    || isDbDataTypeEqDouble(dbDataType)) {
                return true;
            } else {
                return false;
            }
        }

        public static boolean isDbDataTypeEqClob(String dbDataType) {
            if (DyFormConfig.DbDataType._clob.equals(dbDataType)) {
                return true;
            }
            return false;
        }

        public static boolean isDbDataTypeAsString(String dbDataType) {
            if (isDbDataTypeEqString(dbDataType) || isDbDataTypeEqClob(dbDataType)) {
                return true;
            }
            return false;
        }

        public static String getDataTypeNameByNum(String dataTypeNum) {
            if (DbDataType._int.equals(dataTypeNum)) {
                return "INTEGER";
            } else if (DbDataType._date.equals(dataTypeNum)) {
                return "DATE";
            } else if (DbDataType._clob.equals(dataTypeNum)) {
                return "CLOB";
            } else if (DbDataType._float.equals(dataTypeNum)) {
                return "FLOAT";
            } else if (DbDataType._double.equals(dataTypeNum)) {
                return "DOUBLE";
            } else if (DbDataType._long.equals(dataTypeNum)) {
                return "LONG";
            } else if (DbDataType._string.equals(dataTypeNum)) {
                return "STRING";
            } else if (DbDataType._number.equals(dataTypeNum)) {
                return "NUMBER";
            } else {
                return "STRING";
            }
        }

    }
}
