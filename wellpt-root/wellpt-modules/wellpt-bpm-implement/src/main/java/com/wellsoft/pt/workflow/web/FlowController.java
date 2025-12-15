/*
 * @(#)WorkFlowController.java 2012-10-16 1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.web;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.bpm.engine.form.Record;
import com.wellsoft.pt.bpm.engine.form.assembler.TaskFormOpinionAssembler;
import com.wellsoft.pt.org.entity.Organization;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Description: 工作流控制器
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-19.1	zhulh		2012-10-19		Create
 * </pre>
 * @date 2012-10-19
 */
@Controller
@RequestMapping("/workflow")
public class FlowController extends BaseController {

    /**
     * 跳到工作流编辑界面主页
     *
     * @return
     */
    @RequestMapping("/index")
    public String index(Model model, HttpServletRequest request,
                        @RequestParam(required = false) String moduleId, @RequestParam(required = false) Integer isRef) {
        model.addAttribute("moduleId", moduleId);
        model.addAttribute("isRef", isRef);
        return forward("/workflow/index");
    }

    /**
     * 跳到工作流查看界面主页
     *
     * @return
     */
    @RequestMapping("/show")
    public String show() {
        return forward("/workflow/show");
    }

    /**
     * iframe中包含的文件
     *
     * @return
     */
    @RequestMapping("/work")
    public String work() {
        return forward("/workflow/work");
    }

    /*add by huanglinchuan 2014.12.30 begin*/
    @RequestMapping("/view")
    public String view() {
        return forward("/workflow/view");
    }

    /*add by huanglinchuan 2014.12.30 end*/

    /**
     * 工作流帮助
     *
     * @return
     */
    @RequestMapping("/help/index")
    public String help() {
        return forward("/workflow/help/index");
    }

    /**
     * 打开环节属性
     *
     * @return
     */
    @RequestMapping("/task")
    public String task() {
        return forward("/workflow/task");
    }

    /**
     * 打开环节属性
     *
     * @return
     */
    @RequestMapping("/task2")
    public String task2() {
        return forward("/workflow/task2");
    }

    /**
     * 打开环节属性->人员设置->选择用户
     *
     * @return
     */
    @RequestMapping("/user/select")
    public String selectUser() {
        return forward("/workflow/user_select");
    }

    /**
     * 打开环节属性->人员设置->选择用户
     *
     * @return
     */
    @RequestMapping("/user/select2")
    public String selectUser2() {
        return forward("/workflow/user_select2");
    }

    /**
     * 打开环节属性->人员设置->自定义
     *
     * @return
     */
    @RequestMapping("/user/custom")
    public String customUser2(Model model) {
        model.addAttribute("organization", new Organization());
        return forward("/workflow/user_custom");
    }

    /**
     * 打开环节属性->权限设置->选择复选框
     *
     * @return
     */
    @RequestMapping("/checkbox/select")
    public String checkboxSelect() {
        return forward("/workflow/checkbox_select");
    }

    /**
     * 打开环节属性->权限设置->选择复选框
     *
     * @return
     */
    @RequestMapping("/checkbox/select2")
    public String checkboxSelect2() {
        return forward("/workflow/checkbox_select2");
    }

    /**
     * 打开环节属性->权限设置->操作定义
     *
     * @return
     */
    @RequestMapping("/button")
    public String button() {
        return forward("/workflow/button");
    }

    /**
     * 打开环节属性->权限设置->操作定义
     *
     * @return
     */
    @RequestMapping("/button2")
    public String button2() {
        return forward("/workflow/button2");
    }

    /**
     * 打开环节属性->权限设置->意见立场
     *
     * @return
     */
    @RequestMapping("/option")
    public String option() {
        return forward("/workflow/option");
    }

    /**
     * 打开环节属性->权限设置->意见立场
     *
     * @return
     */
    @RequestMapping("/option2")
    public String option2() {
        return forward("/workflow/option2");
    }

    /**
     * 打开环节属性->表单控制->字段属性
     *
     * @return
     */
    @RequestMapping("/field/property")
    public String fieldProperty() {
        return forward("/workflow/field_property");
    }

    /**
     * 打开环节属性->表单控制->信息记录
     *
     * @return
     */
    @RequestMapping("/remark")
    public String remark() {
        return forward("/workflow/remark");
    }

    /**
     * 打开环节属性->表单控制->信息记录
     *
     * @return
     */
    @RequestMapping("/remark2")
    public String remark2(
            @RequestParam(value = "isFlowForm", required = false, defaultValue = "0") String isFlowForm,
            Model model) {
        model.addAttribute("isFlowForm", isFlowForm);
        model.addAttribute("remarkForm", new Record());
        Map<String, TaskFormOpinionAssembler> assemblers = ApplicationContextHolder.getApplicationContext()
                .getBeansOfType(TaskFormOpinionAssembler.class);
        Map<String, String> assemblerMap = new LinkedHashMap<String, String>();
        for (Entry<String, TaskFormOpinionAssembler> entry : assemblers.entrySet()) {
            TaskFormOpinionAssembler value = entry.getValue();
            assemblerMap.put(entry.getKey(), value.getName());
        }
        model.addAttribute("assemblers", assemblerMap);
        return forward("/workflow/remark2");
    }

    /**
     * 打开流程属性界面
     *
     * @return
     */
    @RequestMapping("/flow")
    public String flow() {
        return forward("/workflow/flow");
    }

    /**
     * 打开流程属性界面
     *
     * @return
     */
    @RequestMapping("/flow2")
    public String flow2() {
        return forward("/workflow/flow2");
    }

    /**
     * 打开流程属性->计时系统
     *
     * @return
     */
    @RequestMapping("/timer")
    public String timer() {
        return forward("/workflow/timer");
    }

    /**
     * 打开流程属性->计时系统
     *
     * @return
     */
    @RequestMapping("/timer2")
    public String timer2() {
        return forward("/workflow/timer2");
    }

    /**
     * 打开流程属性->高级设置->岗位替代
     *
     * @return
     */
    @RequestMapping("/user/back")
    public String userBack() {
        return forward("/workflow/user_back");
    }

    /**
     * 打开流程属性->高级设置->岗位替代
     *
     * @return
     */
    @RequestMapping("/user/back2")
    public String userBack2() {
        return forward("/workflow/user_back2");
    }

    /**
     * 打开流程属性->高级设置->消息模板
     *
     * @return
     */
    @RequestMapping("/message/template")
    public String messageTemplate() {
        return forward("/workflow/message_template");
    }

    /**
     * 打开流向属性界面
     *
     * @return
     */
    @RequestMapping("/direction")
    public String direction() {
        return forward("/workflow/direction");
    }

    /**
     * 打开流向属性界面
     *
     * @return
     */
    @RequestMapping("/direction2")
    public String direction2() {
        return forward("/workflow/direction2");
    }

    /**
     * 打开流向属性界面
     *
     * @return
     */
    @RequestMapping("/condition")
    public String condition() {
        return forward("/workflow/condition");
    }

    /**
     * 打开流向属性界面
     *
     * @return
     */
    @RequestMapping("/condition2")
    public String condition2() {
        return forward("/workflow/condition2");
    }

    /**
     * 打开流向属性界面->添加条件
     *
     * @return
     */
    @RequestMapping("/logic")
    public String logic() {
        return forward("/workflow/logic");
    }

    /**
     * 打开流向属性界面->添加条件
     *
     * @return
     */
    @RequestMapping("/logic2")
    public String logic2() {
        return forward("/workflow/logic2");
    }

    /**
     * 打开流向归档设置->归档规则
     *
     * @return
     */
    @RequestMapping("/archive/rule")
    public String archiveRule() {
        return forward("/workflow/archive_rule");
    }

    /**
     * 打开子流程属性界面
     *
     * @return
     */
    @RequestMapping("/sub/flow")
    public String subFlow() {
        return forward("/workflow/sub_flow");
    }

    /**
     * 打开子流程属性界面
     *
     * @return
     */
    @RequestMapping("/sub/flow2")
    public String subFlow2() {
        return forward("/workflow/sub_flow2");
    }

    /**
     * 打开流程选择窗口
     *
     * @return
     */
    @RequestMapping("/flow/select")
    public String selectFlow() {
        return forward("/workflow/flow_select");
    }

    /**
     * 打开流程选择窗口
     *
     * @return
     */
    @RequestMapping("/flow/select2")
    public String selectFlow2() {
        return forward("/workflow/flow_select2");
    }

    /**
     * 打开子流程分发条件
     *
     * @return
     */
    @RequestMapping("/flow/select/logic")
    public String selectFlowLogic() {
        return forward("/workflow/flow_select_logic");
    }

    /**
     * 新流程字段传递设置
     *
     * @return
     */
    @RequestMapping("/field/select")
    public String selectField() {
        return forward("/workflow/field_select");
    }

    /**
     * 打开流程前后置配置关系
     *
     * @return
     */
    @RequestMapping("/flow/relation")
    public String relationFlow() {
        return forward("/workflow/flow_relation");
    }

    /**
     * 打开流程分阶段办理配置关系
     *
     * @return
     */
    @RequestMapping("/flow/stage")
    public String stageFlow() {
        return forward("/workflow/flow_stage");
    }

    /**
     * 打开流程办理信息标题表达式配置
     *
     * @return
     */
    @RequestMapping("/undertake/title/expression")
    public String undertakeTitleExpression() {
        return forward("/workflow/undertake_title_expression");
    }

    /**
     * 打开流程办理进度列配置
     *
     * @return
     */
    @RequestMapping("/undertake/situation/column")
    public String undertakeSituationColumn() {
        return forward("/workflow/undertake_situation_column");
    }

    /**
     * 打开等价流程选择窗口
     *
     * @return
     */
    @RequestMapping("/equal/flow/select")
    public String equalFlowSelect() {
        return forward("/workflow/equal_flow_select");
    }
}
