/*
 * @(#)2012-11-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.bean;

import com.google.common.collect.Maps;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.config.SystemParamsUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicButton;
import com.wellsoft.pt.bpm.engine.form.Record;
import com.wellsoft.pt.bpm.engine.form.TaskForm;
import com.wellsoft.pt.bpm.engine.support.InteractionTaskData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.support.DyFormDataDeserializer;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.springframework.security.acls.model.Permission;

import java.util.*;

/**
 * Description: 流程工作数据
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-26.1	zhulh		2012-11-26		Create
 * </pre>
 * @date 2012-11-26
 */
@ApiModel("流程工作数据")
public class WorkBean extends InteractionTaskData {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -9056626873656529498L;
    // 全局自定义JS的KEY
    private static final String KEY_WORKFLOW_CUSTOM_GLOBAL_SCRIPT_URL = "workflow.custom.global.script.url";
    // 签署意见模式 1内嵌 2弹出框
    private static final String KEY_WORKFLOW_WORK_SIGN_OPINION_MODEL = "workflow.work.sign.opinion.model";

    // 业务类型ID
    @ApiModelProperty("业务类型ID")
    private String businessTypeId;
    // 当前查看的工作ACL权限
    @ApiModelProperty("当前查看的工作ACL角色")
    private String aclRole;
    @ApiModelProperty("当前查看的工作ACL权限列表")
    private Set<Permission> permissions;
    // 当前环节实例UUID
    @ApiModelProperty("环节实例UUID")
    private String taskInstUuid;
    @ApiModelProperty("环节实例版本号")
    private Integer taskInstRecVer;
    // 当前环节ID
    @ApiModelProperty("当前环节ID")
    private String taskId;
    // 当前环节名称
    @ApiModelProperty("当前环节名称")
    private String taskName;
    // 环节开始时间
    @ApiModelProperty("环节开始时间")
    private Date taskStartTime;
    // 当前环节办理人标识UUID
    @ApiModelProperty("当前环节办理人标识UUID")
    private String taskIdentityUuid;
    // 流程实例UUID
    @ApiModelProperty("流程实例UUID")
    private String flowInstUuid;
    // 流程定义UUID
    @ApiModelProperty("流程定义UUID")
    private String flowDefUuid;
    // 流程定义ID
    @ApiModelProperty("流程定义ID")
    private String flowDefId;
    // 流程标题
    @ApiModelProperty("流程标题")
    private String title;

    // 自动更新流程标题
    @ApiModelProperty("自动更新流程标题")
    private boolean autoUpdateTitle;

    @ApiModelProperty(hidden = true)
    private String name;

    // 挂起状态
    @ApiModelProperty("挂起状态")
    private int suspensionState;

    // 流水号定义ID
    @ApiModelProperty("流水号定义ID")
    private String serialNoDefId;

    // 当前用户ID
    @ApiModelProperty("当前用户ID")
    private String userId;
    // 当前用户名称
    @ApiModelProperty("当前用户名称")
    private String userName;

    // 表单定义UUID
    @ApiModelProperty("表单定义UUID")
    private String formUuid;
    // 默认显示单据定义UUID
    @ApiModelProperty("默认显示单据定义UUID")
    private String defaultVFormUuid;
    // 默认手机单据定义UUID
    @ApiModelProperty("默认手机单据定义UUID")
    private String defaultMFormUuid;
    // 表单数据UUID
    @ApiModelProperty("表单数据UUID")
    private String dataUuid;
    // 动态表单数据
    // private FormAndDataBean formAndDataBean;
    // 动态表单数据
    // private RootFormDataBean rootFormDataBean;
    // 加载动态表单数据
    @ApiModelProperty("加载动态表单数据")
    private boolean loadDyFormData = true;
    // 动态表单数据
    @JsonDeserialize(using = DyFormDataDeserializer.class)
    @ApiModelProperty("表单数据")
    private DyFormData dyFormData;
    @ApiModelProperty("临时表单数据")
    private Map<String, List<Map<String, Object>>> tempFormDatas;
    @ApiModelProperty("临时环节实例数据版本号")
    private Integer tempTaskInstRecVer;

    // 流程所有者ID
    @ApiModelProperty("流程所有者ID")
    private String ownerId;
    // 流程发起部门ID
    @ApiModelProperty("流程发起部门ID")
    private String startDepartmentId;
    // 流程所属部门ID
    @ApiModelProperty("流程所属部门ID")
    private String ownerDepartmentId;
    // 流程发起单位ID
    @ApiModelProperty("流程发起单位ID")
    private String startUnitId;
    // 流程所属单位ID
    @ApiModelProperty("流程所属单位ID")
    private String ownerUnitId;

    // 是否允许移动端提交
    @ApiModelProperty("是否允许移动端提交")
    private String isAllowApp;

    @ApiModelProperty("可编辑表单")
    private Boolean canEditForm = null;

    // 操作按钮
    @ApiModelProperty("操作按钮列表")
    private List<CustomDynamicButton> buttons = new ArrayList<CustomDynamicButton>(0);
    // 提交按钮ID
    @ApiModelProperty("提交按钮ID")
    private String submitButtonId;

    //	// 参与者
    //	private Map<String, List<String>> taskUsers = new HashMap<String, List<String>>(0);
    //	// 抄送人
    //	private Map<String, List<String>> taskCopyUsers = new HashMap<String, List<String>>(0);
    //	// 督办人
    //	private Map<String, List<String>> taskMonitors = new HashMap<String, List<String>>(0);


    // 创建时间
    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    // 办理类型工作待办(1)、会签待办(2)、转办待办(3)、移交待办(4)
    @ApiModelProperty("办理类型工作待办(1)、会签待办(2)、转办待办(3)、移交待办(4)")
    private Integer todoType;

    @ApiModelProperty("转办、会签、加签查看表单方式")
    private String viewFormMode;

    //	// 开始环节
    //	private String fromTaskId;
    //	// 下一流向ID
    //	private String toDirectionId;
    //	// 下一环节ID
    //	private String toTaskId;
    // 是否跳转到下一环节
    @ApiModelProperty("是否跳转到下一环节")
    private boolean gotoTask;
    //	// 要跳转的环节ID
    //	private String gotoTaskId;

    // 操作动作(提交)
    @ApiModelProperty("操作动作名称")
    private String action;

    // 操作动作类型(Submit)
    @ApiModelProperty("操作动作类型")
    private String actionType;

    // 操作动作类型(Submit)
    @ApiModelProperty("操作动作代码")
    private Integer actionCode;

    //	// 选择的下一子流程ID
    //	private String toSubFlowId;
    //	// 子流程等待合并<subFlowInstUuid, isWait>
    //	private Map<String, Boolean> waitForMerge = new HashMap<String, Boolean>(0);

    // 自定义操作按钮提交
    @ApiModelProperty("自定义操作按钮提交")
    private CustomDynamicButton customDynamicButton;

    //	// 要退回环节
    //	private String rollbackToTaskId;
    //	// 要退回环节实例UUID
    //	private String rollbackToTaskInstUuid;
    // 是否退回到前环节
    @ApiModelProperty("是否退回到前环节")
    private boolean rollbackToPreTask;

    // 打印模板ID
    @ApiModelProperty("打印模板ID")
    private String printTemplateId;
    // 打印模板UUID
    @ApiModelProperty("打印模板UUID")
    private String printTemplateUuid;
    @ApiModelProperty("打印模板语言")
    private String printTemplateLang;

    // 意见立场
    @ApiModelProperty("意见立场")
    private List<Map<String, String>> opinions;
    // 签署的意见立场值
    @ApiModelProperty("签署的意见立场值")
    private String opinionValue;
    // 签署的意见立场文本
    @ApiModelProperty("签署的意见立场文本")
    private String opinionLabel;
    // 签署的意见内容
    @ApiModelProperty("签署的意见内容")
    private String opinionText;
    @ApiModelProperty("签署的附件")
    private List<LogicFileInfo> opinionFiles;

    @ApiModelProperty(hidden = true)
    private String opinionsAsJson;

    // 是否后台运行的工作
    @ApiModelProperty("是否后台运行的工作")
    private boolean daemon;

    // 是否需要提交权限
    @ApiModelProperty("是否需要提交权限")
    private boolean requiredSubmitPermission = true;

    // 办理过程信息
    @ApiModelProperty("办理过程信息")
    private Map<String, Map<String, String>> workProcess = new HashMap<String, Map<String, String>>(0);

    // 分支流数据
    @ApiModelProperty("分支流数据")
    private BranchTaskData branchTaskData;
    // 子流程数据
    @ApiModelProperty("子流程数据")
    private SubTaskData subTaskData;

    @ApiModelProperty("表单控制信息")
    private TaskForm taskForm;

    // 信息记录
    @ApiModelProperty("信息记录列表")
    private List<Record> records = new ArrayList<Record>(0);

    /**
     * 预留字段
     **/
    // 255字符长度
    @ApiModelProperty("预留文本字段1")
    private String reservedText1;
    // 255字符长度
    @ApiModelProperty("预留文本字段2")
    private String reservedText2;
    // 255字符长度
    @ApiModelProperty("预留文本字段3")
    private String reservedText3;
    // 255字符长度
    @ApiModelProperty("预留文本字段4")
    private String reservedText4;
    // 255字符长度
    @ApiModelProperty("预留文本字段5")
    private String reservedText5;
    // 255字符长度
    @ApiModelProperty("预留文本字段6")
    private String reservedText6;
    // 255字符长度
    @ApiModelProperty("预留文本字段7")
    private String reservedText7;
    // 255字符长度
    @ApiModelProperty("预留文本字段8")
    private String reservedText8;
    // 255字符长度
    @ApiModelProperty("预留文本字段9")
    private String reservedText9;
    // 255字符长度
    @ApiModelProperty("预留文本字段10")
    private String reservedText10;
    // 255字符长度
    @ApiModelProperty("预留文本字段11")
    private String reservedText11;
    // 255字符长度
    @ApiModelProperty("预留文本字段12")
    private String reservedText12;

    @ApiModelProperty("预留数字字段1")
    private Integer reservedNumber1;
    @ApiModelProperty("预留数字字段2")
    private Double reservedNumber2;
    @ApiModelProperty("预留数字字段3")
    private Double reservedNumber3;

    @ApiModelProperty("预留日期字段1")
    private Date reservedDate1;
    @ApiModelProperty("预留日期字段2")
    private Date reservedDate2;

    /**
     * 服务扩展
     **/
    // 自定义打印服务
    @ApiModelProperty(hidden = true)
    private String printService;
    // 打印结果直接显示
    @ApiModelProperty(hidden = true)
    private boolean printToDisplay;
    // 是否提交后打印
    @ApiModelProperty(hidden = true)
    private boolean printAfterSubmit;

    // 保存前服务处理
    @ApiModelProperty(hidden = true)
    private String beforeSaveService;

    // 保存后服务处理
    @ApiModelProperty(hidden = true)
    private String afterSaveService;

    // 提交前服务处理
    @ApiModelProperty(hidden = true)
    private String beforeSubmitService;

    // 提交后服务处理
    @ApiModelProperty(hidden = true)
    private String afterSubmitService;

    // 退回前服务处理
    @ApiModelProperty(hidden = true)
    private String beforeRollbackService;

    // 二次开发JSON信息
    @ApiModelProperty("二次开发JSON信息")
    private String developJson;
    // 二次开发JS模块
    @ApiModelProperty("二次开发JS模块")
    private String customJsModule;
    // 二次开发JS片段模块
    @ApiModelProperty("二次开发JS片段模块")
    private String customJsFragmentModule;

    // 全局的JS
    // private String globalScriptUrl;

    // 附加参数
    @ApiModelProperty("附加参数")
    private Map<String, Object> extraParams = new LinkedHashMap<String, Object>(0);

    // 是否第一个环节节点
    @ApiModelProperty("是否第一个环节节点")
    private Boolean isFirstTaskNode;

    @ApiModelProperty("流程业务定义ID")
    private String flowBizDefId;

    @ApiModelProperty("多个办理方式")
    private String multiSubmitType;

    @ApiModelProperty("国际化信息")
    private Map<String, String> i18n = Maps.newHashMap();

    @ApiModelProperty("版本号")
    private String version;

    /**
     * @return the isFirstTaskNode
     */
    public Boolean getIsFirstTaskNode() {
        return isFirstTaskNode;
    }

    /**
     * @param isFirstTaskNode
     */
    public void setIsFirstTaskNode(Boolean isFirstTaskNode) {
        this.isFirstTaskNode = isFirstTaskNode;
    }

    /**
     * @return the businessTypeId
     */
    public String getBusinessTypeId() {
        return businessTypeId;
    }

    /**
     * @param businessTypeId 要设置的businessTypeId
     */
    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    /**
     * @return the aclRole
     */
    public String getAclRole() {
        return aclRole;
    }

    /**
     * @param aclRole 要设置的aclRole
     */
    public void setAclRole(String aclRole) {
        this.aclRole = aclRole;
    }

    /**
     * @return the permissions
     */
    @JsonIgnore
    public Set<Permission> getPermissions() {
        return permissions;
    }

    /**
     * @param permissions 要设置的permissions
     */
    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    /**
     * @return the taskInstUuid
     */
    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    /**
     * @param taskInstUuid 要设置的taskInstUuid
     */
    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    /**
     * @return the taskInstRecVer
     */
    public Integer getTaskInstRecVer() {
        return taskInstRecVer;
    }

    /**
     * @param taskInstRecVer 要设置的taskInstRecVer
     */
    public void setTaskInstRecVer(Integer taskInstRecVer) {
        this.taskInstRecVer = taskInstRecVer;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId 要设置的taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * @param taskName 要设置的taskName
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * @return the taskStartTime
     */
    public Date getTaskStartTime() {
        return taskStartTime;
    }

    /**
     * @param taskStartTime 要设置的taskStartTime
     */
    public void setTaskStartTime(Date taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    /**
     * @return the taskIdentityUuid
     */
    public String getTaskIdentityUuid() {
        return taskIdentityUuid;
    }

    /**
     * @param taskIdentityUuid 要设置的taskIdentityUuid
     */
    public void setTaskIdentityUuid(String taskIdentityUuid) {
        this.taskIdentityUuid = taskIdentityUuid;
    }

    /**
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    /**
     * @param flowInstUuid 要设置的flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    /**
     * @return the flowDefUuid
     */
    public String getFlowDefUuid() {
        return flowDefUuid;
    }

    /**
     * @param flowDefUuid 要设置的flowDefUuid
     */
    public void setFlowDefUuid(String flowDefUuid) {
        this.flowDefUuid = flowDefUuid;
    }

    /**
     * @return the flowDefId
     */
    public String getFlowDefId() {
        return flowDefId;
    }

    /**
     * @param flowDefId 要设置的flowDefId
     */
    public void setFlowDefId(String flowDefId) {
        this.flowDefId = flowDefId;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the autoUpdateTitle
     */
    public boolean isAutoUpdateTitle() {
        return autoUpdateTitle;
    }

    /**
     * @param autoUpdateTitle 要设置的autoUpdateTitle
     */
    public void setAutoUpdateTitle(boolean autoUpdateTitle) {
        this.autoUpdateTitle = autoUpdateTitle;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the serialNoDefId
     */
    public String getSerialNoDefId() {
        return serialNoDefId;
    }

    /**
     * @param serialNoDefId 要设置的serialNoDefId
     */
    public void setSerialNoDefId(String serialNoDefId) {
        this.serialNoDefId = serialNoDefId;
    }

    /**
     * @return the suspensionState
     */
    public int getSuspensionState() {
        return suspensionState;
    }

    /**
     * @param suspensionState 要设置的suspensionState
     */
    public void setSuspensionState(int suspensionState) {
        this.suspensionState = suspensionState;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName 要设置的userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * @return the defaultVFormUuid
     */
    public String getDefaultVFormUuid() {
        return defaultVFormUuid;
    }

    /**
     * @param defaultVFormUuid 要设置的defaultVFormUuid
     */
    public void setDefaultVFormUuid(String defaultVFormUuid) {
        this.defaultVFormUuid = defaultVFormUuid;
    }

    /**
     * @return the defaultMFormUuid
     */
    public String getDefaultMFormUuid() {
        return defaultMFormUuid;
    }

    /**
     * @param defaultMFormUuid 要设置的defaultMFormUuid
     */
    public void setDefaultMFormUuid(String defaultMFormUuid) {
        this.defaultMFormUuid = defaultMFormUuid;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    // /**
    // * @return the formAndDataBean
    // */
    // public FormAndDataBean getFormAndDataBean() {
    // return formAndDataBean;
    // }
    //
    // /**
    // * @param formAndDataBean 要设置的formAndDataBean
    // */
    // public void setFormAndDataBean(FormAndDataBean formAndDataBean) {
    // this.formAndDataBean = formAndDataBean;
    // }

    // /**
    // * @return the rootFormDataBean
    // */
    // public RootFormDataBean getRootFormDataBean() {
    // return rootFormDataBean;
    // }
    //
    // /**
    // * @param rootFormDataBean 要设置的rootFormDataBean
    // */
    // public void setRootFormDataBean(RootFormDataBean rootFormDataBean) {
    // this.rootFormDataBean = rootFormDataBean;
    // }

    /**
     * @return the dyFormData
     */
    public DyFormData getDyFormData() {
        return dyFormData;
    }

    /**
     * @param dyFormData 要设置的dyFormData
     */
    public void setDyFormData(DyFormData dyFormData) {
        this.dyFormData = dyFormData;
    }

    /**
     * @return the tempFormDatas
     */
    public Map<String, List<Map<String, Object>>> getTempFormDatas() {
        return tempFormDatas;
    }

    /**
     * @param tempFormDatas 要设置的tempFormDatas
     */
    public void setTempFormDatas(Map<String, List<Map<String, Object>>> tempFormDatas) {
        this.tempFormDatas = tempFormDatas;
    }

    /**
     * @return the tempTaskInstRecVer
     */
    public Integer getTempTaskInstRecVer() {
        return tempTaskInstRecVer;
    }

    /**
     * @param tempTaskInstRecVer 要设置的tempTaskInstRecVer
     */
    public void setTempTaskInstRecVer(Integer tempTaskInstRecVer) {
        this.tempTaskInstRecVer = tempTaskInstRecVer;
    }

    /**
     * @return the loadDyFormData
     */
    public boolean isLoadDyFormData() {
        return loadDyFormData;
    }

    /**
     * @param loadDyFormData 要设置的loadDyFormData
     */
    public void setLoadDyFormData(boolean loadDyFormData) {
        this.loadDyFormData = loadDyFormData;
    }

    /**
     * @return the ownerId
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * @param ownerId 要设置的ownerId
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * @return the startDepartmentId
     */
    public String getStartDepartmentId() {
        return startDepartmentId;
    }

    /**
     * @param startDepartmentId 要设置的startDepartmentId
     */
    public void setStartDepartmentId(String startDepartmentId) {
        this.startDepartmentId = startDepartmentId;
    }

    /**
     * @return the ownerDepartmentId
     */
    public String getOwnerDepartmentId() {
        return ownerDepartmentId;
    }

    /**
     * @param ownerDepartmentId 要设置的ownerDepartmentId
     */
    public void setOwnerDepartmentId(String ownerDepartmentId) {
        this.ownerDepartmentId = ownerDepartmentId;
    }

    /**
     * @return the startUnitId
     */
    public String getStartUnitId() {
        return startUnitId;
    }

    /**
     * @param startUnitId 要设置的startUnitId
     */
    public void setStartUnitId(String startUnitId) {
        this.startUnitId = startUnitId;
    }

    /**
     * @return the ownerUnitId
     */
    public String getOwnerUnitId() {
        return ownerUnitId;
    }

    /**
     * @param ownerUnitId 要设置的ownerUnitId
     */
    public void setOwnerUnitId(String ownerUnitId) {
        this.ownerUnitId = ownerUnitId;
    }

    /**
     * @return the buttons
     */
    public List<CustomDynamicButton> getButtons() {
        return buttons;
    }

    /**
     * @param buttons 要设置的buttons
     */
    public void setButtons(List<CustomDynamicButton> buttons) {
        this.buttons = buttons;
    }

    /**
     * @return the submitButtonId
     */
    public String getSubmitButtonId() {
        return submitButtonId;
    }

    /**
     * @param submitButtonId 要设置的submitButtonId
     */
    public void setSubmitButtonId(String submitButtonId) {
        this.submitButtonId = submitButtonId;
    }

    //	/**
    //	 * @return the taskUsers
    //	 */
    //	public Map<String, List<String>> getTaskUsers() {
    //		return taskUsers;
    //	}
    //
    //	/**
    //	 * @param taskUsers 要设置的taskUsers
    //	 */
    //	public void setTaskUsers(Map<String, List<String>> taskUsers) {
    //		this.taskUsers = taskUsers;
    //	}
    //
    //	/**
    //	 * @return the taskCopyUsers
    //	 */
    //	public Map<String, List<String>> getTaskCopyUsers() {
    //		return taskCopyUsers;
    //	}
    //
    //	/**
    //	 * @param taskCopyUsers 要设置的taskCopyUsers
    //	 */
    //	public void setTaskCopyUsers(Map<String, List<String>> taskCopyUsers) {
    //		this.taskCopyUsers = taskCopyUsers;
    //	}
    //
    //	/**
    //	 * @return the taskMonitors
    //	 */
    //	public Map<String, List<String>> getTaskMonitors() {
    //		return taskMonitors;
    //	}
    //
    //	/**
    //	 * @param taskMonitors 要设置的taskMonitors
    //	 */
    //	public void setTaskMonitors(Map<String, List<String>> taskMonitors) {
    //		this.taskMonitors = taskMonitors;
    //	}

    /**
     * @return the createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime 要设置的createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime 要设置的endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the todoType
     */
    public Integer getTodoType() {
        return todoType;
    }

    /**
     * @param todoType 要设置的todoType
     */
    public void setTodoType(Integer todoType) {
        this.todoType = todoType;
    }

    /**
     * @return the viewFormMode
     */
    public String getViewFormMode() {
        return viewFormMode;
    }

    /**
     * @param viewFormMode 要设置的viewFormMode
     */
    public void setViewFormMode(String viewFormMode) {
        this.viewFormMode = viewFormMode;
    }

    //	/**
    //	 * @return the fromTaskId
    //	 */
    //	public String getFromTaskId() {
    //		return StringUtils.isBlank(fromTaskId) ? FlowConstant.START_FLOW_ID : fromTaskId;
    //	}
    //
    //	/**
    //	 * @param fromTaskId 要设置的fromTaskId
    //	 */
    //	public void setFromTaskId(String fromTaskId) {
    //		this.fromTaskId = fromTaskId;
    //	}
    //
    //	/**
    //	 * @return the toDirectionId
    //	 */
    //	public String getToDirectionId() {
    //		return toDirectionId;
    //	}
    //
    //	/**
    //	 * @param toDirectionId 要设置的toDirectionId
    //	 */
    //	public void setToDirectionId(String toDirectionId) {
    //		this.toDirectionId = toDirectionId;
    //	}
    //
    //	/**
    //	 * @return the toTaskId
    //	 */
    //	public String getToTaskId() {
    //		return toTaskId;
    //	}
    //
    //	/**
    //	 * @param toTaskId 要设置的toTaskId
    //	 */
    //	public void setToTaskId(String toTaskId) {
    //		this.toTaskId = toTaskId;
    //	}

    /**
     * @return the gotoTask
     */
    public boolean isGotoTask() {
        return gotoTask;
    }

    /**
     * @param gotoTask 要设置的gotoTask
     */
    public void setGotoTask(boolean gotoTask) {
        this.gotoTask = gotoTask;
    }

    //	/**
    //	 * @return the gotoTaskId
    //	 */
    //	public String getGotoTaskId() {
    //		return gotoTaskId;
    //	}
    //
    //	/**
    //	 * @param gotoTaskId 要设置的gotoTaskId
    //	 */
    //	public void setGotoTaskId(String gotoTaskId) {
    //		this.gotoTaskId = gotoTaskId;
    //	}

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action 要设置的action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the actionType
     */
    public String getActionType() {
        return actionType;
    }

    /**
     * @param actionType 要设置的actionType
     */
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    /**
     * @return the actionCode
     */
    public Integer getActionCode() {
        return actionCode;
    }

    /**
     * @param actionCode 要设置的actionCode
     */
    public void setActionCode(Integer actionCode) {
        this.actionCode = actionCode;
    }

    //	/**
    //	 * @return the toSubFlowId
    //	 */
    //	public String getToSubFlowId() {
    //		return toSubFlowId;
    //	}
    //
    //	/**
    //	 * @param toSubFlowId 要设置的toSubFlowId
    //	 */
    //	public void setToSubFlowId(String toSubFlowId) {
    //		this.toSubFlowId = toSubFlowId;
    //	}
    //
    //	/**
    //	 * @return the waitForMerge
    //	 */
    //	public Map<String, Boolean> getWaitForMerge() {
    //		return waitForMerge;
    //	}
    //
    //	/**
    //	 * @param waitForMerge 要设置的waitForMerge
    //	 */
    //	public void setWaitForMerge(Map<String, Boolean> waitForMerge) {
    //		this.waitForMerge = waitForMerge;
    //	}

    /**
     * @return the customDynamicButton
     */
    public CustomDynamicButton getCustomDynamicButton() {
        return customDynamicButton;
    }

    /**
     * @param customDynamicButton 要设置的customDynamicButton
     */
    public void setCustomDynamicButton(CustomDynamicButton customDynamicButton) {
        this.customDynamicButton = customDynamicButton;
    }

    //	/**
    //	 * @return the rollbackToTaskId
    //	 */
    //	public String getRollbackToTaskId() {
    //		return rollbackToTaskId;
    //	}
    //
    //	/**
    //	 * @param rollbackToTaskId 要设置的rollbackToTaskId
    //	 */
    //	public void setRollbackToTaskId(String rollbackToTaskId) {
    //		this.rollbackToTaskId = rollbackToTaskId;
    //	}
    //
    //	/**
    //	 * @return the rollbackToTaskInstUuid
    //	 */
    //	public String getRollbackToTaskInstUuid() {
    //		return rollbackToTaskInstUuid;
    //	}
    //
    //	/**
    //	 * @param rollbackToTaskInstUuid 要设置的rollbackToTaskInstUuid
    //	 */
    //	public void setRollbackToTaskInstUuid(String rollbackToTaskInstUuid) {
    //		this.rollbackToTaskInstUuid = rollbackToTaskInstUuid;
    //	}

    /**
     * @return the rollbackToPreTask
     */
    public boolean isRollbackToPreTask() {
        return rollbackToPreTask;
    }

    /**
     * @param rollbackToPreTask 要设置的rollbackToPreTask
     */
    public void setRollbackToPreTask(boolean rollbackToPreTask) {
        this.rollbackToPreTask = rollbackToPreTask;
    }

    /**
     * @return the opinions
     */
    public List<Map<String, String>> getOpinions() {
        return opinions;
    }

    /**
     * @param opinions 要设置的opinions
     */
    public void setOpinions(List<Map<String, String>> opinions) {
        this.opinions = opinions;
    }

    /**
     * @return the opinions
     */
    public String getOpinionsAsJson() {
        if (opinions == null) {
            opinions = new ArrayList<Map<String, String>>(0);
        }
        this.opinionsAsJson = JsonUtils.object2Json(opinions);
        return opinionsAsJson;
    }

    /**
     * @return the daemon
     */
    public boolean isDaemon() {
        return daemon;
    }

    /**
     * @param daemon 要设置的daemon
     */
    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    /**
     * @return the requiredSubmitPermission
     */
    public boolean isRequiredSubmitPermission() {
        return requiredSubmitPermission;
    }

    /**
     * @param requiredSubmitPermission 要设置的requiredSubmitPermission
     */
    public void setRequiredSubmitPermission(boolean requiredSubmitPermission) {
        this.requiredSubmitPermission = requiredSubmitPermission;
    }

    /**
     * @return the printTemplateId
     */
    public String getPrintTemplateId() {
        return printTemplateId;
    }

    /**
     * @param printTemplateId 要设置的printTemplateId
     */
    public void setPrintTemplateId(String printTemplateId) {
        this.printTemplateId = printTemplateId;
    }

    /**
     * @return the printTemplateUuid
     */
    public String getPrintTemplateUuid() {
        return printTemplateUuid;
    }

    /**
     * @param printTemplateUuid 要设置的printTemplateUuid
     */
    public void setPrintTemplateUuid(String printTemplateUuid) {
        this.printTemplateUuid = printTemplateUuid;
    }

    /**
     * @return the printTemplateLang
     */
    public String getPrintTemplateLang() {
        return printTemplateLang;
    }

    /**
     * @param printTemplateLang 要设置的printTemplateLang
     */
    public void setPrintTemplateLang(String printTemplateLang) {
        this.printTemplateLang = printTemplateLang;
    }

    /**
     * @return the opinionValue
     */
    public String getOpinionValue() {
        return opinionValue;
    }

    /**
     * @param opinionValue 要设置的opinionValue
     */
    public void setOpinionValue(String opinionValue) {
        this.opinionValue = opinionValue;
    }

    /**
     * @return the opinionLabel
     */
    public String getOpinionLabel() {
        return opinionLabel;
    }

    /**
     * @param opinionLabel 要设置的opinionLabel
     */
    public void setOpinionLabel(String opinionLabel) {
        this.opinionLabel = opinionLabel;
    }

    /**
     * @return the opinionText
     */
    public String getOpinionText() {
        return opinionText;
    }

    /**
     * @param opinionText 要设置的opinionText
     */
    public void setOpinionText(String opinionText) {
        this.opinionText = opinionText;
    }

    /**
     * @return the opinionFiles
     */
    public List<LogicFileInfo> getOpinionFiles() {
        return opinionFiles;
    }

    /**
     * @param opinionFiles 要设置的opinionFiles
     */
    public void setOpinionFiles(List<LogicFileInfo> opinionFiles) {
        this.opinionFiles = opinionFiles;
    }

    /**
     * @return the workProcess
     */
    public Map<String, Map<String, String>> getWorkProcess() {
        return workProcess;
    }

    /**
     * @param workProcess 要设置的workProcess
     */
    public void setWorkProcess(Map<String, Map<String, String>> workProcess) {
        this.workProcess = workProcess;
    }

    //	/**
    //	 * @return the shareDatas
    //	 */
    //	public List<FlowShareData> getShareDatas() {
    //		return shareDatas;
    //	}
    //
    //	/**
    //	 * @param shareDatas 要设置的shareDatas
    //	 */
    //	public void setShareDatas(List<FlowShareData> shareDatas) {
    //		this.shareDatas = shareDatas;
    //	}

    /**
     * @return the branchTaskData
     */
    public BranchTaskData getBranchTaskData() {
        return branchTaskData;
    }

    /**
     * @param branchTaskData 要设置的branchTaskData
     */
    public void setBranchTaskData(BranchTaskData branchTaskData) {
        this.branchTaskData = branchTaskData;
    }

    /**
     * @return the subTaskData
     */
    public SubTaskData getSubTaskData() {
        return subTaskData;
    }

    /**
     * @param subTaskData 要设置的subTaskData
     */
    public void setSubTaskData(SubTaskData subTaskData) {
        this.subTaskData = subTaskData;
    }

    /**
     * @return the taskForm
     */
    public TaskForm getTaskForm() {
        return taskForm;
    }

    /**
     * @param taskForm 要设置的taskForm
     */
    public void setTaskForm(TaskForm taskForm) {
        this.taskForm = taskForm;
    }

    /**
     * @return
     */
    public List<Record> getRecords() {
        return this.records;
    }

    /**
     * @param records
     */
    public void setRecords(List<Record> records) {
        this.records = records;
    }

    /**
     * @return the reservedText1
     */
    public String getReservedText1() {
        return reservedText1;
    }

    /**
     * @param reservedText1 要设置的reservedText1
     */
    public void setReservedText1(String reservedText1) {
        this.reservedText1 = reservedText1;
    }

    /**
     * @return the reservedText2
     */
    public String getReservedText2() {
        return reservedText2;
    }

    /**
     * @param reservedText2 要设置的reservedText2
     */
    public void setReservedText2(String reservedText2) {
        this.reservedText2 = reservedText2;
    }

    /**
     * @return the reservedText3
     */
    public String getReservedText3() {
        return reservedText3;
    }

    /**
     * @param reservedText3 要设置的reservedText3
     */
    public void setReservedText3(String reservedText3) {
        this.reservedText3 = reservedText3;
    }

    /**
     * @return the reservedText4
     */
    public String getReservedText4() {
        return reservedText4;
    }

    /**
     * @param reservedText4 要设置的reservedText4
     */
    public void setReservedText4(String reservedText4) {
        this.reservedText4 = reservedText4;
    }

    /**
     * @return the reservedText5
     */
    public String getReservedText5() {
        return reservedText5;
    }

    /**
     * @param reservedText5 要设置的reservedText5
     */
    public void setReservedText5(String reservedText5) {
        this.reservedText5 = reservedText5;
    }

    /**
     * @return the reservedText6
     */
    public String getReservedText6() {
        return reservedText6;
    }

    /**
     * @param reservedText6 要设置的reservedText6
     */
    public void setReservedText6(String reservedText6) {
        this.reservedText6 = reservedText6;
    }

    /**
     * @return the reservedText7
     */
    public String getReservedText7() {
        return reservedText7;
    }

    /**
     * @param reservedText7 要设置的reservedText7
     */
    public void setReservedText7(String reservedText7) {
        this.reservedText7 = reservedText7;
    }

    /**
     * @return the reservedText8
     */
    public String getReservedText8() {
        return reservedText8;
    }

    /**
     * @param reservedText8 要设置的reservedText8
     */
    public void setReservedText8(String reservedText8) {
        this.reservedText8 = reservedText8;
    }

    /**
     * @return the reservedText9
     */
    public String getReservedText9() {
        return reservedText9;
    }

    /**
     * @param reservedText9 要设置的reservedText9
     */
    public void setReservedText9(String reservedText9) {
        this.reservedText9 = reservedText9;
    }

    /**
     * @return the reservedText10
     */
    public String getReservedText10() {
        return reservedText10;
    }

    /**
     * @param reservedText10 要设置的reservedText10
     */
    public void setReservedText10(String reservedText10) {
        this.reservedText10 = reservedText10;
    }

    /**
     * @return the reservedText11
     */
    public String getReservedText11() {
        return reservedText11;
    }

    /**
     * @param reservedText11 要设置的reservedText11
     */
    public void setReservedText11(String reservedText11) {
        this.reservedText11 = reservedText11;
    }

    /**
     * @return the reservedText12
     */
    public String getReservedText12() {
        return reservedText12;
    }

    /**
     * @param reservedText12 要设置的reservedText12
     */
    public void setReservedText12(String reservedText12) {
        this.reservedText12 = reservedText12;
    }

    /**
     * @return the reservedNumber1
     */
    public Integer getReservedNumber1() {
        return reservedNumber1;
    }

    /**
     * @param reservedNumber1 要设置的reservedNumber1
     */
    public void setReservedNumber1(Integer reservedNumber1) {
        this.reservedNumber1 = reservedNumber1;
    }

    /**
     * @return the reservedNumber2
     */
    public Double getReservedNumber2() {
        return reservedNumber2;
    }

    /**
     * @param reservedNumber2 要设置的reservedNumber2
     */
    public void setReservedNumber2(Double reservedNumber2) {
        this.reservedNumber2 = reservedNumber2;
    }

    /**
     * @return the reservedNumber3
     */
    public Double getReservedNumber3() {
        return reservedNumber3;
    }

    /**
     * @param reservedNumber3 要设置的reservedNumber3
     */
    public void setReservedNumber3(Double reservedNumber3) {
        this.reservedNumber3 = reservedNumber3;
    }

    /**
     * @return the reservedDate1
     */
    public Date getReservedDate1() {
        return reservedDate1;
    }

    /**
     * @param reservedDate1 要设置的reservedDate1
     */
    public void setReservedDate1(Date reservedDate1) {
        this.reservedDate1 = reservedDate1;
    }

    /**
     * @return the reservedDate2
     */
    public Date getReservedDate2() {
        return reservedDate2;
    }

    /**
     * @param reservedDate2 要设置的reservedDate2
     */
    public void setReservedDate2(Date reservedDate2) {
        this.reservedDate2 = reservedDate2;
    }

    /**
     * @return the printService
     */
    public String getPrintService() {
        return printService;
    }

    /**
     * @param printService 要设置的printService
     */
    public void setPrintService(String printService) {
        this.printService = printService;
    }

    /**
     * @return the printToDisplay
     */
    public boolean isPrintToDisplay() {
        return printToDisplay;
    }

    /**
     * @param printToDisplay 要设置的printToDisplay
     */
    public void setPrintToDisplay(boolean printToDisplay) {
        this.printToDisplay = printToDisplay;
    }

    /**
     * @return the printAfterSubmit
     */
    public boolean isPrintAfterSubmit() {
        return printAfterSubmit;
    }

    /**
     * @param printAfterSubmit 要设置的printAfterSubmit
     */
    public void setPrintAfterSubmit(boolean printAfterSubmit) {
        this.printAfterSubmit = printAfterSubmit;
    }

    /**
     * @return the beforeSaveService
     */
    public String getBeforeSaveService() {
        return beforeSaveService;
    }

    /**
     * @param beforeSaveService 要设置的beforeSaveService
     */
    public void setBeforeSaveService(String beforeSaveService) {
        this.beforeSaveService = beforeSaveService;
    }

    /**
     * @return the afterSaveService
     */
    public String getAfterSaveService() {
        return afterSaveService;
    }

    /**
     * @param afterSaveService 要设置的afterSaveService
     */
    public void setAfterSaveService(String afterSaveService) {
        this.afterSaveService = afterSaveService;
    }

    /**
     * @return the beforeSubmitService
     */
    public String getBeforeSubmitService() {
        return beforeSubmitService;
    }

    /**
     * @param beforeSubmitService 要设置的beforeSubmitService
     */
    public void setBeforeSubmitService(String beforeSubmitService) {
        this.beforeSubmitService = beforeSubmitService;
    }

    /**
     * @return the afterSubmitService
     */
    public String getAfterSubmitService() {
        return afterSubmitService;
    }

    /**
     * @param afterSubmitService 要设置的afterSubmitService
     */
    public void setAfterSubmitService(String afterSubmitService) {
        this.afterSubmitService = afterSubmitService;
    }

    /**
     * @return the beforeRollbackService
     */
    public String getBeforeRollbackService() {
        return beforeRollbackService;
    }

    /**
     * @param beforeRollbackService 要设置的beforeRollbackService
     */
    public void setBeforeRollbackService(String beforeRollbackService) {
        this.beforeRollbackService = beforeRollbackService;
    }

    /**
     * @return the developJson
     */
    public String getDevelopJson() {
        return developJson;
    }

    /**
     * @param developJson 要设置的developJson
     */
    public void setDevelopJson(String developJson) {
        this.developJson = developJson;
    }

    /**
     * @return the developJson
     */
    public String getCustomJsModule() {
        return this.customJsModule;
    }

    /**
     * @param customJsModule 要设置的customJsModule
     */
    public void setCustomJsModule(String customJsModule) {
        this.customJsModule = customJsModule;
    }

    /**
     * @return the customJsFragmentModule
     */
    public String getCustomJsFragmentModule() {
        return customJsFragmentModule;
    }

    /**
     * @param customJsFragmentModule 要设置的customJsFragmentModule
     */
    public void setCustomJsFragmentModule(String customJsFragmentModule) {
        this.customJsFragmentModule = customJsFragmentModule;
    }

    /**
     * @return the globalScriptUrl
     */
    @ApiModelProperty(hidden = true)
    public String getGlobalScriptUrl() {
        return Config.getValue(KEY_WORKFLOW_CUSTOM_GLOBAL_SCRIPT_URL, "");
    }

    /**
     * @param globalScriptUrl 要设置的globalScriptUrl
     */
    public void setGlobalScriptUrl(String globalScriptUrl) {
    }

    /**
     * @return
     */
    @ApiModelProperty(hidden = true)
    public String getSignOpinionModel() {
        return SystemParamsUtils.getValue(KEY_WORKFLOW_WORK_SIGN_OPINION_MODEL, "1");
    }

    /**
     * @param signOpinionModel
     */
    public void setSignOpinionModel(String signOpinionModel) {
    }

    /**
     * 添加附加参数
     *
     * @param paramName
     * @param paramValue
     */
    public void addExtraParam(String paramName, String paramValue) {
        extraParams.put(paramName, paramValue);
    }

    /**
     * @return the extraParam
     */
    public Map<String, Object> getExtraParams() {
        return extraParams;
    }

    public String getIsAllowApp() {
        return isAllowApp;
    }

    public void setIsAllowApp(String isAllowApp) {
        this.isAllowApp = isAllowApp;
    }

    public Boolean getCanEditForm() {
        return canEditForm;
    }

    public void setCanEditForm(Boolean canEditForm) {
        this.canEditForm = canEditForm;
    }

    /**
     * @return the flowBizDefId
     */
    public String getFlowBizDefId() {
        return flowBizDefId;
    }

    /**
     * @param flowBizDefId 要设置的flowBizDefId
     */
    public void setFlowBizDefId(String flowBizDefId) {
        this.flowBizDefId = flowBizDefId;
    }

    /**
     * @return the multiSubmitType
     */
    public String getMultiSubmitType() {
        return multiSubmitType;
    }

    /**
     * @param multiSubmitType 要设置的multiSubmitType
     */
    public void setMultiSubmitType(String multiSubmitType) {
        this.multiSubmitType = multiSubmitType;
    }

    public Map<String, String> getI18n() {
        return i18n;
    }

    public void setI18n(Map<String, String> i18n) {
        this.i18n = i18n;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
