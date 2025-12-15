package com.wellsoft.pt.bpm.engine.expression;

import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.core.FlowDefConstants;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.exception.MultiJobNotSelectedException;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.IdentityService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowTodoType;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.constant.UnitParamConstant;
import com.wellsoft.pt.multi.org.dto.CurrentUnitJobGradeDto;
import com.wellsoft.pt.multi.org.dto.DutySeqAndjobRankDto;
import com.wellsoft.pt.multi.org.dto.OrgDutySeqTreeDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgJobRank;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.multi.org.vo.JobRankLevelAndDutySeqCodeVo;
import com.wellsoft.pt.multi.org.vo.JobRankLevelVo;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Description:职等职级判断条件
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/11/30.1	    zenghw		2021/11/30		    Create
 * </pre>
 * @date 2021/11/30
 */
public class DutyGradeCondition extends AbstractCondition {

    private String prefix = "";

    private String suffix = "";

    /**
     * @param expression
     */
    public DutyGradeCondition(String expression) {
        String exp = StringUtils.trim(expression);
        exp = exp.replace("@DUTYGRADE", "");
        exp = exp.replace("\"", "");
        exp = exp.replace("(", "");
        exp = exp.replace(")", "");
        if (exp.startsWith("&") || exp.startsWith("|")) {
            prefix = exp.substring(0, 1);
            exp = exp.substring(1);
        }

        setExpression(exp);
    }

    @Override
    public String evaluate(Token token, Node to) {
        String expression = this.getExpression().trim();

        String returnExpression = conditionHandler(expression, token);

        return prefix + returnExpression + suffix;
    }

    /**
     * 解析表达式
     *
     * @param expression
     * @return com.wellsoft.pt.bpm.engine.expression.DutyGradeCondition.ConditionObj
     **/
    private ConditionObj getConditionObj(String expression) {
        ConditionObj conditionObj = new ConditionObj();
        String[] expressions = null;
        if (expression.indexOf(EQUAL) != -1) {
            expressions = expression.split(EQUAL);
            conditionObj.setCondition(EQUAL);
        } else if (expression.indexOf(NOT_EQUAL) != -1) {
            expressions = expression.split(NOT_EQUAL);
            conditionObj.setCondition(NOT_EQUAL);
        } else if (expression.indexOf(OVER_TOP) != -1) {
            if (expression.indexOf(OVER_TOP_EQUAL) != -1) {
                expressions = expression.split(OVER_TOP_EQUAL);
                conditionObj.setCondition(OVER_TOP_EQUAL);
            } else {
                expressions = expression.split(OVER_TOP);
                conditionObj.setCondition(OVER_TOP);
            }

        } else if (expression.indexOf(LOWER_THAN) != -1) {
            if (expression.indexOf(LOWER_THAN_EQUAL) != -1) {
                expressions = expression.split(LOWER_THAN_EQUAL);
                conditionObj.setCondition(LOWER_THAN_EQUAL);
            } else {
                expressions = expression.split(LOWER_THAN);
                conditionObj.setCondition(LOWER_THAN);
            }
        } else if (expression.indexOf(NOLIKE) != -1) {
            expressions = expression.split(NOLIKE);
            conditionObj.setCondition(NOLIKE);
        } else if (expression.indexOf(LIKE) != -1) {
            expressions = expression.split(LIKE);
            conditionObj.setCondition(LIKE);
        }

        conditionObj.setConditionName(expressions[0]);

        conditionObj.setConditionValue(expressions[1].split(":")[0].trim());

        if (StringUtils.endsWith(expression, ":2")) {
            conditionObj.setFormValueFlag(Boolean.TRUE);
        }
        return conditionObj;
    }

    /**
     * 获取指定用户的主职位
     *
     * @param userId
     * @return java.lang.String
     **/
    private String getMainJobIdByUserId(String userId, Token token) {
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
//        // 申请人职位
//        List<String> userids = Lists.newArrayList();
//        userids.add(userId);
//        Map<String, List<OrgUserJobDto>> userJobsMap = orgApiFacade.getAllUserJobIdsIgnoreVersion(userids);
//        List<OrgUserJobDto> userJobs = userJobsMap.get(userId);
//        if (userJobs == null) {
//            userJobs = Lists.newArrayList();
//        }
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        List<OrgUserJobDto> userJobDtos = workflowOrgService.listUserJobs(userId, OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token));
        // 主职位
        String mainJobId = getUserMainJobId(userJobDtos);
        return mainJobId;
    }

    /**
     * 获取主职
     *
     * @param priorUserJobs
     * @return java.lang.String
     **/
    private String getUserMainJobId(List<OrgUserJobDto> priorUserJobs) {
        String priorUserMainJobId = "";
        for (OrgUserJobDto priorUserJob : priorUserJobs) {
            if (priorUserJob.isPrimary()) {
                priorUserMainJobId = priorUserJob.getJobId();
                break;
            }
        }
        return priorUserMainJobId;
    }

    /**
     * 逻辑判断
     *
     * @param expression 表达式
     * @param token
     * @return java.lang.String
     **/
    private String conditionHandler(String expression, Token token) {
        StringBuilder sb = new StringBuilder();
        ConditionObj conditionObj = getConditionObj(expression);
        // 申请人
        String creator = token.getFlowInstance().getStartUserId();
        // 当前办理人
        String currentUserId = token.getTaskData().getUserId();
        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);

        // 申请人-主职
        String creatorMainJobId = getMainJobIdByUserId(creator, token);
        // 申请人-当前办理人
        String currentMainJobId = getMainJobIdByUserId(currentUserId, token);

        String jobGradeOrder = "";
        Boolean result = false;

        TaskData taskData = token.getTaskData();
        DyFormData mainData = taskData.getDyFormData(taskData.getDataUuid());
        // 更新用户范围组织或表单字段值
        updateConditionValueIfRequired(conditionObj, mainData.getFormDataOfMainform());

        // 选择项是按字段值时，
        if (conditionObj.getFormValueFlag()) {
            if (StringUtils.isBlank(conditionObj.getConditionValue())) {
                // 字段值为空，直接返回false
                return sb.append(Boolean.FALSE).toString();
            }
            String[] conditionValues = conditionObj.getConditionValue().split(";");
            if (conditionValues.length > 1) {
                // 要判断值，职等，职级多值时 则直接返回false
                return sb.append(Boolean.FALSE).toString();
            }

            if (APPLY_USER_GRADE.equals(conditionObj.getConditionName().trim())
                    || CURRENT_USER_GRADE.equals(conditionObj.getConditionName().trim())) {
                // 职等要判断是否为数字，非数字，直接返回false
                if (!NumberUtil.isNumber(conditionObj.getConditionValue())) {
                    return sb.append(Boolean.FALSE).toString();
                }
            }
        }
        List<JobRankLevelVo> jobRankLevelVos = null;
        switch (conditionObj.getConditionName().trim()) {
            case APPLY_USER_GRADE:
                // 申请人职等
                // 查询当前用户单位职等排序
                jobGradeOrder = orgApiFacade.getCurrentUnitJobGradeOrder();
                // 获取指定用户的职等职级接口
                // List<JobRankLevelVo> jobRankLevelVos = orgApiFacade.queryJobRankLevelListByUserId(creator);
                jobRankLevelVos = workflowOrgService.listUserJobRankLevels(creator, OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token));

                // 不属于职等设置中设置的职等范围的，属于异常，不参与判断 返回false

                // 查询当前用户单位查询职等列表
                if ("asc".equals(jobGradeOrder)) {
                    // 升序 1>2
                    result = ascGradeConditionHandler(conditionObj, jobRankLevelVos, token, creatorMainJobId);
                } else {
                    // 降序 1<2
                    result = descGradeConditionHandler(conditionObj, jobRankLevelVos, token, creatorMainJobId);
                }

                break; // 可选
            case APPLY_DUTY:
                // 申请人职级
                // 获取指定用户的职等职级接口
                // jobRankLevelVos = orgApiFacade.queryJobRankLevelListByUserId(creator);
                jobRankLevelVos = workflowOrgService.listUserJobRankLevels(creator, OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token));
                result = dutyConditionHandler(conditionObj, jobRankLevelVos, token, creatorMainJobId);
                break; // 可选
            case CURRENT_USER_GRADE:
                // 当前办理人职等
                // 申请人职等
                // 查询当前办理人单位职等排序
                jobGradeOrder = orgApiFacade.getCurrentUnitJobGradeOrder();
                // 获取指定用户的职等职级接口
                // jobRankLevelVos = orgApiFacade.queryJobRankLevelListByUserId(currentUserId);
                jobRankLevelVos = workflowOrgService.listUserJobRankLevels(currentUserId, OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token));
                if ("asc".equals(jobGradeOrder)) {
                    // 升序 1>2
                    result = ascGradeConditionHandler(conditionObj, jobRankLevelVos, token, currentMainJobId);
                } else {
                    // 降序 1<2
                    result = descGradeConditionHandler(conditionObj, jobRankLevelVos, token, currentMainJobId);
                }
                break; // 可选
            case CURRENT_USER_DUTY:
                // 当前办理人职级
                // 获取指定用户的职等职级接口
                // jobRankLevelVos = orgApiFacade.queryJobRankLevelListByUserId(currentUserId);
                jobRankLevelVos = workflowOrgService.listUserJobRankLevels(currentUserId, OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token));
                result = dutyConditionHandler(conditionObj, jobRankLevelVos, token, currentUserId);
                break; // 可选
            default: // 可选
                // 语句
        }
        sb.append(result);
        return sb.toString();
    }

    /**
     * 职等判断-按职等列表降序逻辑
     * 1<2
     *
     * @param conditionObj    条件对象
     * @param jobRankLevelVos 指定用户的职等职级列表
     * @return java.lang.Boolean
     **/
    private Boolean descGradeConditionHandler(ConditionObj conditionObj, List<JobRankLevelVo> jobRankLevelVos,
                                              Token token, String mainJobId) {
        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        Boolean result = Boolean.TRUE;
        // 流程一人多职的按流转配置
        String multiJobFlowType = token.getFlowInstance().getFlowDefinition().getMultiJobFlowType();
        if (StringUtils.isBlank(multiJobFlowType)) {
            multiJobFlowType = FlowDefConstants.FLOW_BY_USER_ALL_JOBS;
        }
        String selectJobId = getFlowByUserSelectJob(token);// 获取多职流转的职位选择;
        List<CurrentUnitJobGradeDto> currentUnitJobGradeList = orgApiFacade.getCurrentUnitJobGradeList();
        List<Integer> gradeValueAllList = getGradeValueAllList(jobRankLevelVos, currentUnitJobGradeList);
        Integer maxGradeValue = getMaxGradeValueByOrderType(jobRankLevelVos, currentUnitJobGradeList, "desc");
        Integer mainJobIdGradeValue = getJobIdGradeValueByJobId(jobRankLevelVos, mainJobId, currentUnitJobGradeList);
        Integer selectJobIdGradeValue = getJobIdGradeValueByJobId(jobRankLevelVos, selectJobId,
                currentUnitJobGradeList);
        if (StringUtils.isBlank(conditionObj.getConditionValue())) {
            return Boolean.FALSE;
        }
        if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
            if (maxGradeValue == 0) {
                return Boolean.FALSE;
            }
        } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
            if (mainJobIdGradeValue == 0) {
                return Boolean.FALSE;
            }
        } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
            if (selectJobIdGradeValue == 0) {
                return Boolean.FALSE;
            }
        }
        switch (conditionObj.getCondition()) {
            case EQUAL:
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()).equals(maxGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()).equals(mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()).equals(selectJobIdGradeValue);
                }
                break; // 可选
            case NOT_EQUAL:
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    result = !Integer.valueOf(conditionObj.getConditionValue()).equals(maxGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = !Integer.valueOf(conditionObj.getConditionValue()).equals(mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = !Integer.valueOf(conditionObj.getConditionValue()).equals(selectJobIdGradeValue);
                }
                break; // 可选
            case OVER_TOP:
                // 1<2
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {

                    result = Integer.valueOf(conditionObj.getConditionValue()) < (maxGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()) < (mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()) < (selectJobIdGradeValue);
                }
                break; // 可选
            case OVER_TOP_EQUAL:
                // 1<2
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {

                    result = Integer.valueOf(conditionObj.getConditionValue()) <= (maxGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()) <= (mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()) <= (selectJobIdGradeValue);
                }
                break; // 可选
            case LOWER_THAN:
                // 1<2
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {

                    result = Integer.valueOf(conditionObj.getConditionValue()) > (maxGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()) > (mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()) > (selectJobIdGradeValue);
                }
                break; // 可选
            case LOWER_THAN_EQUAL:
                // 1<2
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {

                    result = Integer.valueOf(conditionObj.getConditionValue()) >= (maxGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()) >= (mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()) >= (selectJobIdGradeValue);
                }
                break; // 可选
            // case LIKE:
            // // 语句 选择按职等判断时，操作符 包含、不包含不做判断
            // break; // 可选
            // case NOLIKE:
            // // 语句
            // break; // 可选
            default: // 可选
                // 语句
        }
        return result;
    }

    /**
     * 职级判断-中的职等判断-按职等列表降序逻辑
     * 1<2
     *
     * @param conditionObj    条件对象
     * @param jobRankLevelVos 指定用户的职等职级列表
     * @return java.lang.Boolean
     **/
    private Boolean descGradeConditionHandler(ConditionObj conditionObj, List<JobRankLevelVo> jobRankLevelVos,
                                              List<MultiOrgJobRank> multiOrgJobRanks, Token token, String mainJobId) {
        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        Boolean result = Boolean.FALSE;
        // 流程一人多职的按流转配置
        String multiJobFlowType = token.getFlowInstance().getFlowDefinition().getMultiJobFlowType();
        if (StringUtils.isBlank(multiJobFlowType)) {
            multiJobFlowType = FlowDefConstants.FLOW_BY_USER_ALL_JOBS;
        }
        String selectJobId = getFlowByUserSelectJob(token);// 获取多职流转的职位选择;
        // 查询当前用户单位查询职等列表
        List<CurrentUnitJobGradeDto> currentUnitJobGradeList = orgApiFacade.getCurrentUnitJobGradeList();
        List<Integer> getGradeValues = getMoreGradeValue(jobRankLevelVos, currentUnitJobGradeList);
        Integer mainJobIdGradeValue = getJobIdGradeValueByJobId(jobRankLevelVos, mainJobId, currentUnitJobGradeList);
        Integer selectJobIdGradeValue = getJobIdGradeValueByJobId(jobRankLevelVos, selectJobId,
                currentUnitJobGradeList);

        if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType) && getGradeValues.equals(0)) {
            return Boolean.FALSE;
        } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)
                && mainJobIdGradeValue.equals(0)) {
            return Boolean.FALSE;
        } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)
                && selectJobIdGradeValue.equals(0)) {
            return Boolean.FALSE;
        }
        // 是职级的uuid 要转为职等的值
        MultiOrgJobRank multiOrgJobRank = null;
        String jobRankIdValue = conditionObj.getConditionValue();
        if (StringUtils.isNotBlank(jobRankIdValue)) {
            if (jobRankIdValue.startsWith(IdPrefix.RANK.getValue())) {
                multiOrgJobRank = orgApiFacade.getMultiOrgJobRankDetailById(jobRankIdValue);
            } else {
                multiOrgJobRank = orgApiFacade.getMultiOrgJobRankDetailByUuid(jobRankIdValue);
            }
        }
        if (multiOrgJobRank == null) {
            return Boolean.FALSE;
        }
        Integer jobGrade = multiOrgJobRank.getJobGrade();
        switch (conditionObj.getCondition()) {
            case EQUAL:
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    // 取人员的全部职级及对应的职等进行判断
                    // 例：有3个职位，一个命中，两个没命中，返回true
                    result = Boolean.FALSE;
                    for (Integer getGradeValue : getGradeValues) {
                        if (jobGrade.equals(getGradeValue)) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade.equals(mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade.equals(selectJobIdGradeValue);
                }
                break; // 可选
            case NOT_EQUAL:
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    // 取人员的全部职级及对应的职等进行判断
                    // 例：有3个职位，一个命中，两个没命中，返回true
                    result = Boolean.FALSE;
                    for (Integer getGradeValue : getGradeValues) {
                        if (!jobGrade.equals(getGradeValue)) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = !jobGrade.equals(mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = !jobGrade.equals(selectJobIdGradeValue);
                }
                break; // 可选
            case OVER_TOP:
                // 1<2
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    // 取人员的全部职级及对应的职等进行判断
                    // 例：有3个职位，一个命中，两个没命中，返回true
                    result = Boolean.FALSE;
                    for (Integer getGradeValue : getGradeValues) {
                        if (jobGrade < (getGradeValue)) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade < (mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade < (selectJobIdGradeValue);
                }
                break; // 可选
            case OVER_TOP_EQUAL:
                // 1<2
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    // 取人员的全部职级及对应的职等进行判断
                    // 例：有3个职位，一个命中，两个没命中，返回true
                    result = Boolean.FALSE;
                    for (Integer getGradeValue : getGradeValues) {
                        if (jobGrade <= (getGradeValue)) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade <= (mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade <= (selectJobIdGradeValue);
                }
                break; // 可选
            case LOWER_THAN:
                // 1<2
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    // 取人员的全部职级及对应的职等进行判断
                    // 例：有3个职位，一个命中，两个没命中，返回true
                    result = Boolean.FALSE;
                    for (Integer getGradeValue : getGradeValues) {
                        if (jobGrade > (getGradeValue)) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade > (mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade > (selectJobIdGradeValue);
                }
                break; // 可选
            case LOWER_THAN_EQUAL:
                // 1<2
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    // 取人员的全部职级及对应的职等进行判断
                    // 例：有3个职位，一个命中，两个没命中，返回true
                    result = Boolean.FALSE;
                    for (Integer getGradeValue : getGradeValues) {
                        if (jobGrade >= (getGradeValue)) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade >= (mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade >= (selectJobIdGradeValue);
                }
                break; // 可选
            case LIKE:
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    // 取人员的全部职级及对应的职等进行判断
                    // 例：有3个职位，一个命中，两个没命中，返回true
                    result = Boolean.FALSE;
                    for (Integer getGradeValue : getGradeValues) {
                        if (getGradeValue != 0) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    // result = jobRankIdExistScopeFlag(mainJobIdGradeValue,
                    // currentUnitJobGradeList);
                    result = jobGrade.equals(mainJobIdGradeValue);

                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    // result = jobRankIdExistScopeFlag(selectJobIdGradeValue,
                    // currentUnitJobGradeList);
                    result = jobGrade.equals(selectJobIdGradeValue);
                }
                break; // 可选
            case NOLIKE:
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    // 取人员的全部职级及对应的职等进行判断
                    // 例：有3个职位，一个命中，两个没命中，返回true
                    result = Boolean.FALSE;
                    for (Integer getGradeValue : getGradeValues) {
                        if (getGradeValue != 0) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                    result = !result;
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = !jobGrade.equals(mainJobIdGradeValue);
                    // result = !jobRankIdExistScopeFlag(mainJobIdGradeValue,
                    // currentUnitJobGradeList);

                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = !jobGrade.equals(selectJobIdGradeValue);
                    // result = !jobRankIdExistScopeFlag(selectJobIdGradeValue,
                    // currentUnitJobGradeList);
                }
                break; // 可选
            default: // 可选
                // 语句
        }
        return result;
    }

    /**
     * 职级判断-中的职等判断-按职等列表升序逻辑
     * 1>2
     *
     * @param conditionObj    条件对象
     * @param jobRankLevelVos 指定用户的职等职级列表
     * @return java.lang.Boolean
     **/
    private Boolean ascGradeConditionHandler(ConditionObj conditionObj, List<JobRankLevelVo> jobRankLevelVos,
                                             List<MultiOrgJobRank> multiOrgJobRanks, Token token, String mainJobId) {
        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        Boolean result = Boolean.FALSE;
        // 流程一人多职的按流转配置
        String multiJobFlowType = token.getFlowInstance().getFlowDefinition().getMultiJobFlowType();
        if (StringUtils.isBlank(multiJobFlowType)) {
            multiJobFlowType = FlowDefConstants.FLOW_BY_USER_ALL_JOBS;
        }
        String selectJobId = getFlowByUserSelectJob(token);// 获取多职流转的职位选择;
        // 查询当前用户单位查询职等列表
        List<CurrentUnitJobGradeDto> currentUnitJobGradeList = orgApiFacade.getCurrentUnitJobGradeList();
        List<Integer> getGradeValues = getMoreGradeValue(jobRankLevelVos, currentUnitJobGradeList);
        Integer mainJobIdGradeValue = getJobIdGradeValueByJobId(jobRankLevelVos, mainJobId, currentUnitJobGradeList);
        Integer selectJobIdGradeValue = getJobIdGradeValueByJobId(jobRankLevelVos, selectJobId,
                currentUnitJobGradeList);
        if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType) && getGradeValues.equals(0)) {
            return Boolean.FALSE;
        } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)
                && mainJobIdGradeValue.equals(0)) {
            return Boolean.FALSE;
        } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)
                && selectJobIdGradeValue.equals(0)) {
            return Boolean.FALSE;
        }
        // 是职级的uuid 要转为职等的值
        MultiOrgJobRank multiOrgJobRank = null;
        String jobRankIdValue = conditionObj.getConditionValue();
        if (StringUtils.isNotBlank(jobRankIdValue)) {
            if (jobRankIdValue.startsWith(IdPrefix.RANK.getValue())) {
                multiOrgJobRank = orgApiFacade.getMultiOrgJobRankDetailById(jobRankIdValue);
            } else {
                multiOrgJobRank = orgApiFacade.getMultiOrgJobRankDetailByUuid(jobRankIdValue);
            }
        }
        if (multiOrgJobRank == null) {
            return Boolean.FALSE;
        }
        Integer jobGrade = multiOrgJobRank.getJobGrade();
        switch (conditionObj.getCondition()) {
            case EQUAL:
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    // 取人员的全部职级及对应的职等进行判断
                    // 例：有3个职位，一个命中，两个没命中，返回true
                    result = Boolean.FALSE;
                    for (Integer getGradeValue : getGradeValues) {
                        if (jobGrade.equals(getGradeValue)) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade.equals(mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade.equals(selectJobIdGradeValue);
                }
                break; // 可选
            case NOT_EQUAL:
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    // 取人员的全部职级及对应的职等进行判断
                    // 例：有3个职位，一个命中，两个没命中，返回true
                    result = Boolean.FALSE;
                    for (Integer getGradeValue : getGradeValues) {
                        if (!jobGrade.equals(getGradeValue)) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = !jobGrade.equals(mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = !jobGrade.equals(selectJobIdGradeValue);
                }
                break; // 可选
            case OVER_TOP:
                // 1>2
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    // 取人员的全部职级及对应的职等进行判断
                    // 例：有3个职位，一个命中，两个没命中，返回true
                    result = Boolean.FALSE;
                    for (Integer getGradeValue : getGradeValues) {
                        if (jobGrade > (getGradeValue)) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade > (mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade > (selectJobIdGradeValue);
                }
                break; // 可选
            case OVER_TOP_EQUAL:
                // 1>2
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    // 取人员的全部职级及对应的职等进行判断
                    // 例：有3个职位，一个命中，两个没命中，返回true
                    result = Boolean.FALSE;
                    for (Integer getGradeValue : getGradeValues) {
                        if (jobGrade >= (getGradeValue)) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade >= (mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade >= (selectJobIdGradeValue);
                }
                break; // 可选
            case LOWER_THAN:
                // 1>2
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    // 取人员的全部职级及对应的职等进行判断
                    // 例：有3个职位，一个命中，两个没命中，返回true
                    result = Boolean.FALSE;
                    for (Integer getGradeValue : getGradeValues) {
                        if (jobGrade < (getGradeValue)) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade < (mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade < (selectJobIdGradeValue);
                }
                break; // 可选
            case LOWER_THAN_EQUAL:
                // 1>2
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    // 取人员的全部职级及对应的职等进行判断
                    // 例：有3个职位，一个命中，两个没命中，返回true
                    result = Boolean.FALSE;
                    for (Integer getGradeValue : getGradeValues) {
                        if (jobGrade <= (getGradeValue)) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade <= (mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade <= (selectJobIdGradeValue);
                }
                break; // 可选
            case LIKE:
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    // 取人员的全部职级及对应的职等进行判断
                    // 例：有3个职位，一个命中，两个没命中，返回true
                    result = Boolean.FALSE;
                    for (Integer getGradeValue : getGradeValues) {
                        if (getGradeValue != 0) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade.equals(mainJobIdGradeValue);
                    // result = jobRankIdExistScopeFlag(mainJobIdGradeValue,
                    // currentUnitJobGradeList);

                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = jobGrade.equals(selectJobIdGradeValue);
                    // result = jobRankIdExistScopeFlag(selectJobIdGradeValue,
                    // currentUnitJobGradeList);
                }
                break; // 可选
            case NOLIKE:
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    // 取人员的全部职级及对应的职等进行判断
                    // 例：有3个职位，一个命中，两个没命中，返回true
                    result = Boolean.FALSE;
                    for (Integer getGradeValue : getGradeValues) {
                        if (getGradeValue != 0) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                    result = !result;
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    // result = !jobRankIdExistScopeFlag(mainJobIdGradeValue,
                    // currentUnitJobGradeList);
                    result = !jobGrade.equals(mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    // result = !jobRankIdExistScopeFlag(selectJobIdGradeValue,
                    // currentUnitJobGradeList);
                    result = !jobGrade.equals(selectJobIdGradeValue);
                }
                break; // 可选
            default: // 可选
                // 语句
        }
        return result;
    }

    /**
     * 职等判断-按职等列表升序逻辑
     * 1>2
     *
     * @param conditionObj    条件对象
     * @param jobRankLevelVos 指定用户的职等职级列表
     * @return java.lang.Boolean
     **/
    private Boolean ascGradeConditionHandler(ConditionObj conditionObj, List<JobRankLevelVo> jobRankLevelVos,
                                             Token token, String mainJobId) {
        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        Boolean result = Boolean.FALSE;
        // 流程一人多职的按流转配置
        String multiJobFlowType = token.getFlowInstance().getFlowDefinition().getMultiJobFlowType();
        if (StringUtils.isBlank(multiJobFlowType)) {
            multiJobFlowType = FlowDefConstants.FLOW_BY_USER_ALL_JOBS;
        }
        String selectJobId = getFlowByUserSelectJob(token);// 获取多职流转的职位选择;
        // 查询当前用户单位查询职等列表

        List<CurrentUnitJobGradeDto> currentUnitJobGradeList = orgApiFacade.getCurrentUnitJobGradeList();
        List<Integer> gradeValueAllList = getGradeValueAllList(jobRankLevelVos, currentUnitJobGradeList);
        Integer maxGradeValue = getMaxGradeValueByOrderType(jobRankLevelVos, currentUnitJobGradeList, "asc");
        Integer mainJobIdGradeValue = getJobIdGradeValueByJobId(jobRankLevelVos, mainJobId, currentUnitJobGradeList);
        Integer selectJobIdGradeValue = getJobIdGradeValueByJobId(jobRankLevelVos, selectJobId,
                currentUnitJobGradeList);
        if (StringUtils.isBlank(conditionObj.getConditionValue())) {
            return Boolean.FALSE;
        }
        if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
            if (maxGradeValue == 0) {
                return Boolean.FALSE;
            }
        } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
            if (mainJobIdGradeValue == 0) {
                return Boolean.FALSE;
            }
        } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
            if (selectJobIdGradeValue == 0) {
                return Boolean.FALSE;
            }
        }
        switch (conditionObj.getCondition()) {
            case EQUAL:
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()).equals(maxGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()).equals(mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()).equals(selectJobIdGradeValue);
                }
                break; // 可选
            case NOT_EQUAL:
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    result = !Integer.valueOf(conditionObj.getConditionValue()).equals(maxGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = !Integer.valueOf(conditionObj.getConditionValue()).equals(mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = !Integer.valueOf(conditionObj.getConditionValue()).equals(selectJobIdGradeValue);
                }
                break; // 可选
            case OVER_TOP:
                // 1>2
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {

                    result = Integer.valueOf(conditionObj.getConditionValue()) > (maxGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()) > (mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()) > (selectJobIdGradeValue);
                }

                break; // 可选
            case OVER_TOP_EQUAL:
                // 1>2
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {

                    result = Integer.valueOf(conditionObj.getConditionValue()) >= (maxGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()) >= (mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()) >= (selectJobIdGradeValue);
                }
                break; // 可选
            case LOWER_THAN:
                // 1>2
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {

                    result = Integer.valueOf(conditionObj.getConditionValue()) < (maxGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()) < (mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()) < (selectJobIdGradeValue);
                }
                break; // 可选
            case LOWER_THAN_EQUAL:
                // 1>2
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {

                    result = Integer.valueOf(conditionObj.getConditionValue()) <= (maxGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()) <= (mainJobIdGradeValue);
                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    result = Integer.valueOf(conditionObj.getConditionValue()) <= (selectJobIdGradeValue);
                }
                break; // 可选
            // case LIKE:
            // // 语句 选择按职等判断时，操作符 包含、不包含不做判断
            // break; // 可选
            // case NOLIKE:
            // // 语句
            // break; // 可选
            default: // 可选
                // 语句
        }
        return result;
    }

    /**
     * // 获取条件的指定用户的职级值不在用户的职位对应的职位范围内 直接返回false
     * // 同一序列分类的才能对比职级，如果获取条件的指定用户的职级值对应的序列和用户的职位对应的序列不在同一序列分类下，直接返回false
     *
     * @param jobRankLevelVos        获取指定用户的职等职级接口
     * @param newMultiOrgJobRankList 同一序列 的职级列表
     * @param conditionObj           条件
     * @return java.lang.Boolean
     **/
    private Boolean notExistScopeFlag(List<JobRankLevelVo> jobRankLevelVos,
                                      List<MultiOrgJobRank> newMultiOrgJobRankList, ConditionObj conditionObj,
                                      List<JobRankLevelVo> existScopeJobRankLevelVos) {
        Boolean notExistScopeFlag = Boolean.TRUE;
        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        // 查询当前用户单位查询职级列表
        List<OrgDutySeqTreeDto> orgDutySeqTreeDtos = orgApiFacade.queryJobRankTree();

        // 指定用户的职等职级 与条件里的职级是否是同一个序列
        List<MultiOrgJobRank> multiOrgJobRanks = orgApiFacade
                .getBelongSeqMultiOrgJobRankListByUuid(conditionObj.getConditionValue());

        // 用户的职级对应的序列
        if (jobRankLevelVos.size() > 0) {
            List<String> jobRankIds = Lists.newArrayList();
            for (JobRankLevelVo jobRankLevelVo : jobRankLevelVos) {
                if (StringUtils.isNotBlank(jobRankLevelVo.getJobRankId())) {
                    jobRankIds.add(jobRankLevelVo.getJobRankId());
                }
            }
            List<DutySeqAndjobRankDto> dutySeqAndjobRankDtos = orgApiFacade
                    .getDutySeqAndjobRankListByRankIds(jobRankIds);

            List<JobRankLevelAndDutySeqCodeVo> jobRankLevelAndDutySeqCodeVos = BeanUtils.copyCollection(jobRankLevelVos,
                    JobRankLevelAndDutySeqCodeVo.class);
            // 设置职务序列编号
            setSeqCode(jobRankLevelAndDutySeqCodeVos, dutySeqAndjobRankDtos);
            for (JobRankLevelAndDutySeqCodeVo jobRankLevelAndDutySeqCodeVo : jobRankLevelAndDutySeqCodeVos) {

                for (MultiOrgJobRank multiOrgJobRank : multiOrgJobRanks) {

                    if (multiOrgJobRank.getId().equals(jobRankLevelAndDutySeqCodeVo.getJobRankId())) {
                        newMultiOrgJobRankList.addAll(multiOrgJobRanks);
                        JobRankLevelVo jobRankLevelVo = new JobRankLevelVo();
                        BeanUtils.copyProperties(jobRankLevelAndDutySeqCodeVo, jobRankLevelVo);
                        existScopeJobRankLevelVos.add(jobRankLevelVo);
                        notExistScopeFlag = Boolean.FALSE;
                    }
                    if (!notExistScopeFlag) {
                        break;
                    }
                }
                if (!notExistScopeFlag) {
                    break;
                }
            }
        }

        //// 用户的职级对应的序列
        // if (jobRankLevelVos.size() > 0) {
        // List<String> jobRankIds = Lists.newArrayList();
        // for (JobRankLevelVo jobRankLevelVo : jobRankLevelVos) {
        // if (StringUtils.isNotBlank(jobRankLevelVo.getJobRankId())) {
        // jobRankIds.add(jobRankLevelVo.getJobRankId());
        // }
        //
        // }
        // List<DutySeqAndjobRankDto> dutySeqAndjobRankDtos = orgApiFacade
        // .getDutySeqAndjobRankListByRankIds(jobRankIds);
        //
        // List<JobRankLevelAndDutySeqCodeVo> jobRankLevelAndDutySeqCodeVos =
        //// BeanUtils.copyCollection(jobRankLevelVos,
        // JobRankLevelAndDutySeqCodeVo.class);
        // // 设置职务序列编号
        // setSeqCode(jobRankLevelAndDutySeqCodeVos, dutySeqAndjobRankDtos);
        //
        // for (JobRankLevelAndDutySeqCodeVo jobRankLevelAndDutySeqCodeVo :
        //// jobRankLevelAndDutySeqCodeVos) {
        //
        // for (OrgDutySeqTreeDto orgDutySeqTreeDto : orgDutySeqTreeDtos) {
        // // 递归
        // notExistScopeFlag = notExistScopeFlagHandler(orgDutySeqTreeDto,
        //// jobRankLevelAndDutySeqCodeVo,
        // jobRankIds, newOrgDutySeqTreeDtos);
        //
        // if (!notExistScopeFlag) {
        // break;
        // }
        // }
        // if (!notExistScopeFlag) {
        // break;
        // }
        // }
        // }
        return notExistScopeFlag;
    }

    /**
     * 设置职务序列编号
     *
     * @param jobRankLevelAndDutySeqCodeVos
     * @param dutySeqAndjobRankDtos
     * @return void
     **/
    private void setSeqCode(List<JobRankLevelAndDutySeqCodeVo> jobRankLevelAndDutySeqCodeVos,
                            List<DutySeqAndjobRankDto> dutySeqAndjobRankDtos) {
        Map<String, DutySeqAndjobRankDto> dutySeqAndjobRankDtoMap = Maps.newHashMap();

        for (DutySeqAndjobRankDto dutySeqAndjobRankDto : dutySeqAndjobRankDtos) {
            dutySeqAndjobRankDtoMap.put(dutySeqAndjobRankDto.getJobRankId(), dutySeqAndjobRankDto);
        }
        for (JobRankLevelAndDutySeqCodeVo jobRankLevelAndDutySeqCodeVo : jobRankLevelAndDutySeqCodeVos) {
            DutySeqAndjobRankDto dto = dutySeqAndjobRankDtoMap.get(jobRankLevelAndDutySeqCodeVo.getJobRankId());
            if (dto != null) {
                jobRankLevelAndDutySeqCodeVo.setDutySeqCode(dto.getDutySeqCode());
            }
        }
    }

    /**
     * 不存在判断逻辑处理
     * 递归
     *
     * @param
     * @return java.lang.Boolean
     **/
    private Boolean notExistScopeFlagHandler(OrgDutySeqTreeDto orgDutySeqTreeDto,
                                             JobRankLevelAndDutySeqCodeVo jobRankLevelAndDutySeqCodeVo, List<String> jobRankIds,
                                             List<OrgDutySeqTreeDto> newOrgDutySeqTreeDtos) {
        Boolean notExistScopeFlag = Boolean.TRUE;

        if (orgDutySeqTreeDto.getDutySeqCode() != null
                && orgDutySeqTreeDto.getDutySeqCode().equals(jobRankLevelAndDutySeqCodeVo.getDutySeqCode())) {
            // 同级序列
            List<OrgDutySeqTreeDto> orgDutySeqTreeDtos = orgDutySeqTreeDto.getChildrens();
            // 用户的职级值在不在这个系列范围里的职位范围中
            if (orgDutySeqTreeDtos != null && orgDutySeqTreeDtos.size() > 0) {
                for (String jobRankId : jobRankIds) {
                    for (OrgDutySeqTreeDto dutySeqTreeDto : orgDutySeqTreeDtos) {
                        if (dutySeqTreeDto.getChildrenType()
                                .equals(UnitParamConstant.DUTY_SEQ_TREE_CHILDREN_TYPE_SEQ)) {
                            if (dutySeqTreeDto.getChildrens().size() > 0) {
                                for (OrgDutySeqTreeDto children : dutySeqTreeDto.getChildrens()) {
                                    notExistScopeFlag = notExistScopeFlagHandler(children, jobRankLevelAndDutySeqCodeVo,
                                            jobRankIds, newOrgDutySeqTreeDtos);
                                    if (!notExistScopeFlag) {
                                        break;
                                    }
                                }
                            }
                            if (!notExistScopeFlag) {
                                break;
                            }
                        } else {
                            if (jobRankId.equals(dutySeqTreeDto.getId())) {
                                // 在范围里 有命中
                                notExistScopeFlag = Boolean.FALSE;
                                newOrgDutySeqTreeDtos.addAll(orgDutySeqTreeDtos);
                                break;
                            }
                        }

                    }
                    if (!notExistScopeFlag) {
                        break;
                    }
                }
            }
        } else {
            // 顶级序列没匹配中 看有没有存在子序列
            if (orgDutySeqTreeDto.getChildrenType().equals(UnitParamConstant.DUTY_SEQ_TREE_CHILDREN_TYPE_SEQ)) {
                if (orgDutySeqTreeDto.getChildrens().size() > 0) {
                    for (OrgDutySeqTreeDto children : orgDutySeqTreeDto.getChildrens()) {
                        notExistScopeFlag = notExistScopeFlagHandler(children, jobRankLevelAndDutySeqCodeVo, jobRankIds,
                                newOrgDutySeqTreeDtos);
                    }
                }
            } else if (orgDutySeqTreeDto.getChildrenType().equals(UnitParamConstant.DUTY_SEQ_TREE_CHILDREN_TYPE_RANK)) {
                // 职级
                for (String jobRankId : jobRankIds) {
                    if (jobRankId != null && jobRankId.equals(orgDutySeqTreeDto.getId())) {
                        // 在范围里 有命中
                        notExistScopeFlag = Boolean.FALSE;
                        newOrgDutySeqTreeDtos.add(orgDutySeqTreeDto);
                        break;
                    }
                }

            }
        }
        return notExistScopeFlag;
    }

    /**
     * 职级判断
     *
     * @param conditionObj
     * @param jobRankLevelVos
     * @param token
     * @param mainJobId
     * @return java.lang.Boolean
     **/
    private Boolean dutyConditionHandler(ConditionObj conditionObj, List<JobRankLevelVo> jobRankLevelVos, Token token,
                                         String mainJobId) {
        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        // 查询当前用户单位职等排序
        String jobGradeOrder = orgApiFacade.getCurrentUnitJobGradeOrder();
        Boolean result = Boolean.FALSE;

        // 不等于，不包含 先判断,不在同一职级可直接得判断结果
        if (notEqualsOrNotLikeFlag(conditionObj, jobRankLevelVos, token, mainJobId)) {
            return Boolean.TRUE;
        }
        // 获取条件的指定用户的职级值不在用户的职位对应的职位范围内 直接返回false
        // 同一序列分类的才能对比职级，如果获取条件的指定用户的职级值对应的序列和用户的职位对应的序列不在同一序列分类下，直接返回false
        List<MultiOrgJobRank> newOrgDutySeqTreeDtos = Lists.newArrayList();// 同一序列 的职级列表
        List<JobRankLevelVo> existScopeJobRankLevelVos = Lists.newArrayList();// 同一序列 用户符合条件的职级列表
        if (notExistScopeFlag(jobRankLevelVos, newOrgDutySeqTreeDtos, conditionObj, existScopeJobRankLevelVos)) {
            return Boolean.FALSE;
        }

        List<MultiOrgJobRank> multiOrgJobRanks = newOrgDutySeqTreeDtos;
        // 接下来就是职等的对比 可以复用 职等的对应
        // 查询当前用户单位职等排序
        if ("asc".equals(jobGradeOrder)) {
            // 升序 1>2
            result = ascGradeConditionHandler(conditionObj, existScopeJobRankLevelVos, multiOrgJobRanks, token,
                    mainJobId);
        } else {
            // 降序 1<2
            result = descGradeConditionHandler(conditionObj, existScopeJobRankLevelVos, multiOrgJobRanks, token,
                    mainJobId);
        }

        return result;
    }

    private List<String> getMoreJobRankId(List<JobRankLevelVo> jobRankLevelVos,
                                          List<CurrentUnitJobGradeDto> currentUnitJobGradeList) {

        List<String> jobRankIds = Lists.newArrayList();
        for (JobRankLevelVo jobRankLevelVo : jobRankLevelVos) {
            for (CurrentUnitJobGradeDto currentUnitJobGradeDto : currentUnitJobGradeList) {
                // 职等范围内
                if (currentUnitJobGradeDto.getIsValid().equals(1)
                        && currentUnitJobGradeDto.getJobGrade().equals(jobRankLevelVo.getJobGrade())) {
                    if (StringUtils.isNotBlank(jobRankLevelVo.getJobRankId())) {
                        jobRankIds.add(jobRankLevelVo.getJobRankId());
                    }
                }
            }
        }
        return jobRankIds;
    }

    private String getJobIdJobRankIdByJobId(List<JobRankLevelVo> jobRankLevelVos, String jobId,
                                            List<CurrentUnitJobGradeDto> currentUnitJobGradeList) {
        String jobRankId = "";
        for (JobRankLevelVo jobRankLevelVo : jobRankLevelVos) {
            for (CurrentUnitJobGradeDto currentUnitJobGradeDto : currentUnitJobGradeList) {
                // 职等范围
                if (currentUnitJobGradeDto.getIsValid().equals(1)
                        && currentUnitJobGradeDto.getJobGrade().equals(jobRankLevelVo.getJobGrade())) {
                    if (StringUtils.isNotBlank(jobRankLevelVo.getJobId()) && StringUtils.isNotBlank(jobId)
                            && jobRankLevelVo.getJobId().indexOf(jobId) > -1) {
                        jobRankId = jobRankLevelVo.getJobRankId();
                    }
                }
            }

        }
        return jobRankId;
    }

    /**
     * // 不等于，不包含 先判断,不在同一职级可直接得判断结果
     * 符合不等于，不包含返回true
     * 职级有一个匹配，就是true了
     *
     * @param conditionObj
     * @param jobRankLevelVos
     * @param token
     * @param mainJobId
     * @return java.lang.Boolean
     **/
    private Boolean notEqualsOrNotLikeFlag(ConditionObj conditionObj, List<JobRankLevelVo> jobRankLevelVos, Token token,
                                           String mainJobId) {
        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        Boolean notEqualsOrNotLikeFlag = Boolean.FALSE;
        // 是职级的uuid 要转为职等的值
        MultiOrgJobRank multiOrgJobRank = null;
        String jobRankIdValue = conditionObj.getConditionValue();
        if (StringUtils.isNotBlank(jobRankIdValue)) {
            if (jobRankIdValue.startsWith(IdPrefix.RANK.getValue())) {
                multiOrgJobRank = orgApiFacade.getMultiOrgJobRankDetailById(jobRankIdValue);
            } else {
                multiOrgJobRank = orgApiFacade.getMultiOrgJobRankDetailByUuid(jobRankIdValue);
            }
        }
        if (multiOrgJobRank == null) {
            return Boolean.FALSE;
        }
        // 流程一人多职的按流转配置
        String multiJobFlowType = token.getFlowInstance().getFlowDefinition().getMultiJobFlowType();
        if (StringUtils.isBlank(multiJobFlowType)) {
            multiJobFlowType = FlowDefConstants.FLOW_BY_USER_ALL_JOBS;
        }
        String selectJobId = getFlowByUserSelectJob(token);// 获取多职流转的职位选择;

        List<CurrentUnitJobGradeDto> currentUnitJobGradeList = orgApiFacade.getCurrentUnitJobGradeList();
        List<String> jobRankIds = getMoreJobRankId(jobRankLevelVos, currentUnitJobGradeList);
        String mainJobIdJobRankId = getJobIdJobRankIdByJobId(jobRankLevelVos, mainJobId, currentUnitJobGradeList);
        String selectJobIdJobRankId = getJobIdJobRankIdByJobId(jobRankLevelVos, selectJobId, currentUnitJobGradeList);

        if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
            if (jobRankIds.size() == 0) {
                return Boolean.FALSE;
            }
        } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
            if (StringUtils.isBlank(mainJobIdJobRankId)) {
                return Boolean.FALSE;
            }
        } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
            if (StringUtils.isBlank(selectJobIdJobRankId)) {
                return Boolean.FALSE;
            }
        }
        switch (conditionObj.getCondition()) {
            case NOT_EQUAL:
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    Boolean equalFlag = Boolean.FALSE;
                    for (String jobRankId : jobRankIds) {
                        // 职级的不等于条件，要做调整，全部不等，才是true，不然是false
                        if (multiOrgJobRank.getId().equals(jobRankId)) {
                            equalFlag = Boolean.TRUE;
                            break;
                        }
                    }
                    notEqualsOrNotLikeFlag = !equalFlag;
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    notEqualsOrNotLikeFlag = !multiOrgJobRank.getId().equals(mainJobIdJobRankId);

                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    notEqualsOrNotLikeFlag = !multiOrgJobRank.getId().equals(selectJobIdJobRankId);
                }
                break; // 可选

            case NOLIKE:
                if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                    for (String jobRankId : jobRankIds) {
                        if (!multiOrgJobRank.getId().equals(jobRankId)) {
                            notEqualsOrNotLikeFlag = Boolean.TRUE;
                        } else {
                            notEqualsOrNotLikeFlag = Boolean.FALSE;
                        }
                    }
                    notEqualsOrNotLikeFlag = !notEqualsOrNotLikeFlag;
                } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    notEqualsOrNotLikeFlag = !multiOrgJobRank.getId().equals(mainJobIdJobRankId);

                } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                    notEqualsOrNotLikeFlag = !multiOrgJobRank.getId().equals(selectJobIdJobRankId);
                }
                break; // 可选
            default: // 可选
                // 语句
        }
        return notEqualsOrNotLikeFlag;
    }

    /**
     * 取指定人员的职等
     *
     * @param jobRankLevelVos 指定用户的职级职等列表
     * @return java.lang.Integer 返回0 直接可返回false
     **/
    private Integer getJobIdGradeValueByJobId(List<JobRankLevelVo> jobRankLevelVos, String jobId,
                                              List<CurrentUnitJobGradeDto> currentUnitJobGradeList) {
        Integer gradeValue = 0;
        if (jobRankLevelVos != null && jobRankLevelVos.size() > 0) {
            for (JobRankLevelVo jobRankLevelVo : jobRankLevelVos) {
                Boolean scopeFlag = Boolean.FALSE;
                for (CurrentUnitJobGradeDto currentUnitJobGradeDto : currentUnitJobGradeList) {
                    if (currentUnitJobGradeDto.getIsValid().equals(1)
                            && currentUnitJobGradeDto.getJobGrade().equals(jobRankLevelVo.getJobGrade())) {
                        scopeFlag = Boolean.TRUE;
                        break;
                    }
                }
                if (scopeFlag) {
                    if (StringUtils.isNotBlank(jobRankLevelVo.getJobId()) && StringUtils.isNotBlank(jobId)
                            && jobRankLevelVo.getJobId().indexOf(jobId) > -1) {
                        gradeValue = jobRankLevelVo.getJobGrade();
                    }
                }

            }
        }
        return gradeValue;
    }

    /**
     * 判断职位ID是否在职等列表范围内
     * 不在范围内返回false
     **/
    private Boolean jobRankIdExistScopeFlag(Integer jobIdGradeValue,
                                            List<CurrentUnitJobGradeDto> currentUnitJobGradeList) {
        Boolean scopeFlag = Boolean.FALSE;
        for (CurrentUnitJobGradeDto currentUnitJobGradeDto : currentUnitJobGradeList) {
            if (currentUnitJobGradeDto.getIsValid().equals(1)
                    && currentUnitJobGradeDto.getJobGrade().equals(jobIdGradeValue)) {
                scopeFlag = Boolean.TRUE;
                break;
            }
        }
        return scopeFlag;
    }

    /**
     * 取人员的合理职等列表
     * 不在范围内的职等，不纳入判断中
     *
     * @param jobRankLevelVos 指定用户的职级职等列表
     **/
    private List<Integer> getMoreGradeValue(List<JobRankLevelVo> jobRankLevelVos,
                                            List<CurrentUnitJobGradeDto> currentUnitJobGradeList) {
        List<Integer> moreGradeValues = Lists.newArrayList();
        Integer maxGradeValue = 0;
        if (jobRankLevelVos != null && jobRankLevelVos.size() > 0) {
            for (JobRankLevelVo jobRankLevelVo : jobRankLevelVos) {
                Boolean scopeFlag = Boolean.FALSE;
                for (CurrentUnitJobGradeDto currentUnitJobGradeDto : currentUnitJobGradeList) {
                    if (currentUnitJobGradeDto.getIsValid().equals(1)
                            && currentUnitJobGradeDto.getJobGrade().equals(jobRankLevelVo.getJobGrade())) {
                        scopeFlag = Boolean.TRUE;
                        break;
                    }
                }
                if (scopeFlag) {
                    moreGradeValues.add(jobRankLevelVo.getJobGrade());
                }
            }
        }
        return moreGradeValues;
    }

    /**
     * 取符合条件的职等数据
     *
     * @param jobRankLevelVos
     * @param currentUnitJobGradeList
     * @return java.util.List<java.lang.Integer>
     **/
    private List<Integer> getGradeValueAllList(List<JobRankLevelVo> jobRankLevelVos,
                                               List<CurrentUnitJobGradeDto> currentUnitJobGradeList) {
        List<Integer> gradeValueAllList = Lists.newArrayList();
        if (jobRankLevelVos != null && jobRankLevelVos.size() > 0) {
            for (JobRankLevelVo jobRankLevelVo : jobRankLevelVos) {
                Boolean scopeFlag = Boolean.FALSE;
                for (CurrentUnitJobGradeDto currentUnitJobGradeDto : currentUnitJobGradeList) {
                    if (currentUnitJobGradeDto.getIsValid().equals(1)
                            && currentUnitJobGradeDto.getJobGrade().equals(jobRankLevelVo.getJobGrade())) {
                        scopeFlag = Boolean.TRUE;
                        break;
                    }
                }
                if (scopeFlag) {
                    gradeValueAllList.add(jobRankLevelVo.getJobGrade());
                }
            }
        }
        return gradeValueAllList;
    }

    /**
     * 取人员的最高职等
     *
     * @param jobRankLevelVos 指定用户的职级职等列表
     * @param orderType       升序 asc 1>2       降序 desc 1<2
     * @return java.lang.Integer 返回0 直接可返回false
     **/
    private Integer getMaxGradeValueByOrderType(List<JobRankLevelVo> jobRankLevelVos,
                                                List<CurrentUnitJobGradeDto> currentUnitJobGradeList, String orderType) {
        Integer maxGradeValue = 0;
        if (jobRankLevelVos != null && jobRankLevelVos.size() > 0) {
            for (JobRankLevelVo jobRankLevelVo : jobRankLevelVos) {
                Boolean scopeFlag = Boolean.FALSE;
                for (CurrentUnitJobGradeDto currentUnitJobGradeDto : currentUnitJobGradeList) {
                    if (currentUnitJobGradeDto.getIsValid().equals(1)
                            && currentUnitJobGradeDto.getJobGrade().equals(jobRankLevelVo.getJobGrade())) {
                        scopeFlag = Boolean.TRUE;
                        break;
                    }
                }
                if (scopeFlag) {
                    if (maxGradeValue.equals(0)) {
                        maxGradeValue = jobRankLevelVo.getJobGrade();
                    }
                    if ("asc".equals(orderType)) {
                        if (maxGradeValue > jobRankLevelVo.getJobGrade()) {
                            maxGradeValue = jobRankLevelVo.getJobGrade();
                        }
                    } else {
                        if (maxGradeValue < jobRankLevelVo.getJobGrade()) {
                            maxGradeValue = jobRankLevelVo.getJobGrade();
                        }
                    }

                }
            }
        }
        return maxGradeValue;
    }

    /**
     * 多职流转按职位字段选择，获取职位值
     * 1.表单不存在职位字段的情况：
     * 1). 用户多职的情况下，抛异常到前端，让用户选择哪个职位，再提交上来
     * 2). 用户只有一个职位的情况下，直接使用该职位
     * 2.表单存在职位字段，使用职位字段的值
     *
     * @param token
     * @return
     */
    private String getFlowByUserSelectJob(Token token) {
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        String jobId = null;
        String multiJobFlowType = token.getFlowInstance().getFlowDefinition().getMultiJobFlowType();
        String jobField = token.getFlowInstance().getFlowDefinition().getJobField();
        String userId = token.getTaskData().getUserId();
        // 多职流转按职位字段选择流转的情况
        if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
            if (StringUtils.isBlank(jobField)) { // 无选择职位字段的情况下，判断多职选择
                if (StringUtils.isNotBlank(token.getTaskData().getJobSelected(userId))) {// 前端选择了职位
                    jobId = token.getTaskData().getJobSelected(userId);
                } else {
                    // 如果没有配置职位字段，且用户是多职的情况下，则抛出异常，前端进行多职选择提交
                    List<OrgUserJobDto> userJobDtos = workflowOrgService.listUserJobs(userId, OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token));
//                    MultiOrgUserWorkInfo workInfo = orgApiFacade.getUserWorkInfoByUserId(userId);
//                    if (workInfo != null && workInfo.getEleIdPaths() != null) {
                    if (CollectionUtils.size(userJobDtos) > 1) { // 多职情况下
                        String priorUser = getPriorUserId(token, userId);// 前办理人
                        Map<String, Object> data = Maps.newHashMap();
                        data.put("userId", priorUser);
                        // 获取前办理人的所有职位信息
//                        List<OrgElementVo> jobs = orgApiFacade.getUserJobElementByUserId(priorUser);
                        data.put("jobs", userJobDtos);
                        throw new MultiJobNotSelectedException(data);
                    } else if (CollectionUtils.size(userJobDtos) == 1) {// 仅一个职位
                        jobId = userJobDtos.get(0).getJobId();
                    }
                }
//                }
            } else {
                // 职位根据职位字段获取
                String jobFieldValue = (String) (token.getTaskData()
                        .getDyFormData(token.getFlowInstance().getDataUuid()).getFieldValue(jobField));
                jobId = StringUtils.isNotBlank(jobFieldValue) ? jobFieldValue : null;
            }
            // Assert.notNull(jobId, "当前用户无职位信息");
        }

        return jobId;
    }

    /**
     * 获取前办理人用户ID，如果是委托待办则返回委托人，否则返回当前用户
     *
     * @param token
     * @param currentUserId
     * @return
     */
    private String getPriorUserId(Token token, String currentUserId) {

        IdentityService identityService = ApplicationContextHolder.getBean(IdentityService.class);
        TaskInstance taskInstance = token.getTask();
        if (taskInstance == null) {
            return currentUserId;
        }
        String key = taskInstance.getUuid() + currentUserId;
        String taskIdentityUuid = token.getTaskData().getTaskIdentityUuid(key);
        if (StringUtils.isBlank(taskIdentityUuid)) {
            return currentUserId;
        }
        TaskIdentity taskIdentity = identityService.get(taskIdentityUuid);
        if (taskIdentity == null) {
            return currentUserId;
        }
        // 判断当前用户是否进行委托提交
        Integer todoType = taskIdentity.getTodoType();
        if (!WorkFlowTodoType.Delegation.equals(todoType)) {
            return currentUserId;
        }
        String ownerId = taskIdentity.getOwnerId();
        if (StringUtils.isNotBlank(ownerId)) {
            return ownerId;
        }
        return currentUserId;
    }

    /**
     * 条件值 根据是常量或者字段值
     *
     * @param conditionObj
     * @param root
     * @return void
     **/
    private void updateConditionValueIfRequired(ConditionObj conditionObj, Map<String, Object> root) {
        if (conditionObj.getFormValueFlag()) {
            String formValue = ObjectUtils.toString(root.get(conditionObj.getConditionValue()));
            conditionObj.setConditionValue(formValue);
        }
    }

    private class ConditionObj {
        /**
         * 条件名称
         **/
        private String conditionName;
        /**
         * 条件
         **/
        private String condition;
        /**
         * 条件值
         **/
        private String conditionValue;
        /**
         * 字段值为true 常量为false
         **/
        private Boolean formValueFlag = Boolean.FALSE;

        public String getConditionName() {
            return this.conditionName;
        }

        public void setConditionName(final String conditionName) {
            this.conditionName = conditionName;
        }

        public String getCondition() {
            return this.condition;
        }

        public void setCondition(final String condition) {
            this.condition = condition;
        }

        public String getConditionValue() {
            return this.conditionValue;
        }

        public void setConditionValue(final String conditionValue) {
            this.conditionValue = conditionValue;
        }

        public Boolean getFormValueFlag() {
            return this.formValueFlag;
        }

        public void setFormValueFlag(final Boolean formValueFlag) {
            this.formValueFlag = formValueFlag;
        }
    }

}
