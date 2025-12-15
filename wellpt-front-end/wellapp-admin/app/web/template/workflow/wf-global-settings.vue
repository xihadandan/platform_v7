<template>
  <a-form-model
    :model="formData"
    class="flow-settings pt-form"
    :label-col="{ span: 6 }"
    labelAlign="left"
    :wrapper-col="{ span: 18 }"
    :colon="false"
  >
    <a-row type="flex">
      <a-col flex="240px">
        <div class="content-nav" :style="{ height: scrollHeight + 'px' }">
          <a-menu @click="onContentNavClick" :style="{ marginRight: '-12px', paddingRight: '12px' }">
            <template v-for="item in navs">
              <a-menu-item v-if="true" :key="item.value" style="font-weight: bold">{{ item.label }}</a-menu-item>
              <template v-if="item.children">
                <a-menu-item v-for="item in item.children" :key="item.value" class="child-nav">{{ item.label }}</a-menu-item>
              </template>
            </template>
          </a-menu>
        </div>
      </a-col>
      <a-col flex="auto" class="content-body">
        <PerfectScrollbar
          :style="{
            height: scrollHeight - 60 + 'px',
            marginRight: '-20px',
            paddingRight: '20px'
          }"
        >
          <a-card :bordered="false">
            <span slot="title">
              <span class="header-title" href="#flowDefinition">流程定义</span>
              |
              <span class="title-description">主要设置流程设计器中相关规则配置项的默认值</span>
            </span>
            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#flow">流程</div>
              <a-form-model-item label="默认流程标题">
                <TitleDefineTemplate
                  ref="titleDefineTemplate"
                  v-model="formData.FLOW_DEFINITION.titleExpression"
                  :formData="formData.FLOW_DEFINITION"
                  prop="titleExpression"
                  :hasDyformVar="false"
                  alert="在下方编辑流程标题表达式，可插入流程内置变量和文本。"
                >
                  <a-button slot="trigger" type="link" size="small">
                    <Icon type="iconfont icon-ptkj-shezhi"></Icon>
                    设置
                  </a-button>
                </TitleDefineTemplate>
                {{ formData.FLOW_DEFINITION.titleExpression }}
              </a-form-model-item>
              <a-form-model-item label="审批去重功能默认">
                <a-switch v-model="formData.FLOW_DEFINITION.enabledAutoSubmit" checkedChildren="开启" unCheckedChildren="关闭"></a-switch>
              </a-form-model-item>
              <!-- <a-form-model-item label="流程标题自动翻译">
                <a-switch v-model="formData.FLOW_DEFINITION.autoTranslateTitle" checkedChildren="开启" unCheckedChildren="关闭"></a-switch>
              </a-form-model-item> -->
              <a-form-model-item v-if="formData.FLOW_DEFINITION.enabledAutoSubmit" label="审批去重模式默认">
                <a-radio-group v-model="formData.FLOW_DEFINITION.autoSubmitMode" button-style="solid">
                  <a-radio-button value="before">前置审批</a-radio-button>
                  <a-radio-button value="after">后置审批</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item v-if="formData.FLOW_DEFINITION.saveEncoder" label="保存时数据传递编码方式">
                <a-radio-group v-model="formData.FLOW_DEFINITION.saveEncoder" button-style="solid">
                  <a-radio-button value="none">不编码</a-radio-button>
                  <a-radio-button value="base64">Base64编码</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
            </div>

            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#task">环节</div>
              <a-form-model-item label="会签默认表单权限">
                <a-radio-group v-model="formData.FLOW_DEFINITION.task.counterSignViewFormMode" button-style="solid">
                  <a-radio-button value="default">同会签人表单权限</a-radio-button>
                  <a-radio-button value="readonly">只读权限</a-radio-button>
                  <a-radio-button value="custom">由会签人选择</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="加签默认表单权限">
                <a-radio-group v-model="formData.FLOW_DEFINITION.task.addSignViewFormMode" button-style="solid">
                  <a-radio-button value="default">同加签人表单权限</a-radio-button>
                  <a-radio-button value="readonly">只读权限</a-radio-button>
                  <a-radio-button value="custom">由加签人选择</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="转办默认表单权限">
                <a-radio-group v-model="formData.FLOW_DEFINITION.task.transferViewFormMode" button-style="solid">
                  <a-radio-button value="default">同转办人表单权限</a-radio-button>
                  <a-radio-button value="readonly">只读权限</a-radio-button>
                  <a-radio-button value="custom">由转办人选择</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
            </div>

            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#otherDef">其他</div>
              <a-form-model-item label="职务体系相关审批功能">
                <a-switch v-model="formData.FLOW_DEFINITION.enabledJobDuty"></a-switch>
                <div v-if="formData.FLOW_DEFINITION.enabledJobDuty" class="item-tip">
                  流程定义中的环节办理人配置支持职等、职级的相关配置，如获取指定职等/职级的办理人，条件判断中增加职等/职级的判断条件类型
                </div>
              </a-form-model-item>
            </div>
          </a-card>

          <a-card :bordered="false">
            <span slot="title">
              <span class="header-title" href="#workflow">流程签批</span>
              |
              <span class="title-description">主要设置流程签批过程中的各类处理规则</span>
            </span>
            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#general">通用设置</div>
              <a-form-model-item label="多角色时流程操作按角色隔离">
                <a-switch v-model="formData.GENERAL.aclRoleIsolation"></a-switch>
                <div v-if="formData.GENERAL.aclRoleIsolation" class="item-tip">
                  不同流程角色需要通过不同的流程实例链接生效。如：用户对一笔流程实例同时有待办、已办权限，通过待办中打开只显示提交等相关待办操作，已办中打开只显示撤回等相关已办操作
                </div>
                <div v-else class="item-tip">
                  不同流程角色不做隔离，在同一流程实例链接生效。如：用户对一笔流程实例同时有待办、已办权限，则通过任一链接打开该流程实例可操作提交、撤回
                </div>
              </a-form-model-item>
              <div class="item-label">通用交互</div>
              <a-form-model-item label="签署意见">
                <a-radio-group button-style="solid" v-model="formData.OPINION_EDITOR.showMode">
                  <a-radio-button value="sidebar">侧边栏意见输入</a-radio-button>
                  <a-radio-button value="modal">签署意见按钮弹窗输入</a-radio-button>
                  <a-radio-button value="all">全部</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="查看办理过程">
                <a-radio-group button-style="solid" v-model="formData.PROCESS_VIEWER.showMode">
                  <a-radio-button value="sidebar">侧边栏查看</a-radio-button>
                  <a-radio-button value="modal">查看按钮查看办理过程明细</a-radio-button>
                  <a-radio-button value="all">全部</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="办理过程办理人身份显示">
                <a-radio-group button-style="solid" v-model="formData.PROCESS_VIEWER.operatorIdentityMode">
                  <a-radio-button value="primary">显示主身份</a-radio-button>
                  <a-radio-button value="actual">显示实际办理身份</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="查看流程图">
                <a-radio-group button-style="solid" v-model="formData.PROCESS_VIEWER.designerShowMode">
                  <a-radio-button value="sidebar">侧边栏查看</a-radio-button>
                  <a-radio-button value="modal">查看按钮查看流程图</a-radio-button>
                  <a-radio-button value="all">全部</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="流程详情右侧边栏初始默认展开">
                <a-switch v-model="formData.GENERAL.defaultOpenWorkViewSidebar"></a-switch>
              </a-form-model-item>
              <a-form-model-item label="流程详情右侧边栏默认分屏固定">
                <a-switch v-model="formData.GENERAL.defaultSplitWorkView"></a-switch>
              </a-form-model-item>
            </div>

            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#start">发起设置</div>
              <div class="item-label">发起流程弹窗</div>
              <a-form-model-item label="最近使用流程">
                <a-switch v-model="formData.GENERAL.showRecentUsed"></a-switch>
                <span v-show="formData.GENERAL.showRecentUsed" class="item-tip">发起流程弹窗中显示当前用户“最近使用”的流程</span>
              </a-form-model-item>
            </div>

            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#todo">待办设置</div>
              <div class="item-label">签署意见</div>
              <a-form-model-item label="签署意见时添加附件">
                <a-switch v-model="formData.OPINION_FILE.enabled"></a-switch>
              </a-form-model-item>
              <template v-if="formData.OPINION_FILE.enabled">
                <a-form-model-item label="允许上传附件数量">
                  <a-input-number v-model="formData.OPINION_FILE.numLimit" :min="1" placeholder="为空不限制"></a-input-number>
                </a-form-model-item>
                <a-form-model-item label="允许上传附件大小(单个)">
                  <a-row type="flex">
                    <a-col flex="100px">
                      <a-input-number v-model="formData.OPINION_FILE.sizeLimit" :min="1" placeholder="为空不限制"></a-input-number>
                    </a-col>
                    <a-col flex="50px">
                      <a-select v-model="formData.OPINION_FILE.sizeLimitUnit" :options="fileSizeUnitOptions"></a-select>
                    </a-col>
                  </a-row>
                </a-form-model-item>
                <a-form-model-item label="允许上传文件类型">
                  <a-select
                    v-model="formData.OPINION_FILE.accept"
                    mode="multiple"
                    allow-clear
                    :options="fileExtensionOptions"
                    placeholder="为空不限制"
                  ></a-select>
                </a-form-model-item>
                <a-form-model-item label="允许上传重名文件">
                  <a-switch v-model="formData.OPINION_FILE.allowNameRepeat"></a-switch>
                </a-form-model-item>
                <a-form-model-item label="批量下载方式">
                  <a-radio-group button-style="solid" v-model="formData.OPINION_FILE.downloadAllType">
                    <a-radio-button value="1">下载压缩包</a-radio-button>
                    <a-radio-button value="2">下载源文件</a-radio-button>
                  </a-radio-group>
                </a-form-model-item>
              </template>
              <a-form-model-item label="常用意见栏显示快捷提交操作">
                <a-switch v-model="formData.OPINION_EDITOR.showSignAndSubmitButton"></a-switch>
                <span v-show="formData.OPINION_EDITOR.showSignAndSubmitButton" class="item-tip">常用意见上显示“签署并提交”按钮</span>
              </a-form-model-item>
              <a-form-model-item label="签署意见框显示提交按钮">
                <a-switch v-model="formData.OPINION_EDITOR.showConfirmSubmitButton"></a-switch>
                <span v-show="formData.OPINION_EDITOR.showConfirmSubmitButton" class="item-tip">签署意见框显示“确定提交”按钮</span>
              </a-form-model-item>
              <a-form-model-item label="流程操作显示流转提示">
                <a-switch v-model="formData.GENERAL.showActionTip"></a-switch>
                <span v-show="formData.GENERAL.showActionTip" class="item-tip">
                  提交、退回、退回前办理人等操作将显示流转提示，如显示目标环节、下一办理人等
                </span>
              </a-form-model-item>
              <a-form-model-item label="待办删除设置">
                <a-radio-group button-style="solid" v-model="formData.GENERAL.todoDeleteMode">
                  <a-radio-button value="logical">逻辑删除</a-radio-button>
                  <a-radio-button value="physical">物理删除</a-radio-button>
                </a-radio-group>
                <div class="item-tip">
                  用户删除待办流程时是逻辑删除还是物理删除，逻辑删除（假删除）可恢复，物理删除（真删除）不可恢复，仅可删除撤回或被退回至发起环节的待办流程
                </div>
              </a-form-model-item>
            </div>

            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#done">已办设置</div>
              <a-form-model-item label="已办流程表单权限">
                <a-radio-group button-style="solid" v-model="formData.DONE.viewFormMode">
                  <a-radio-button value="done">限于已办人所办理环节的表单内容</a-radio-button>
                  <a-radio-button value="current">查看流程当前环节的表单内容</a-radio-button>
                </a-radio-group>
                <div v-if="formData.DONE.viewFormMode == 'done'" class="item-tip">
                  已办人查看流程详情时，仅查看已办人所办理环节的表单内容，如已办理多个环节则查看最新办理环节的表单内容
                  <br />
                  已办人进行抄送、套打时，表单权限和打印模板等取值来自已办人的办理环节
                </div>
                <div v-else class="item-tip">
                  已办人查看流程详情时，查看流程当前环节的表单内容（流程办结则为办结前一环节）
                  <br />
                  已办人进行抄送、套打时，表单权限和打印模板等取值来自流程实例当前环节
                </div>
              </a-form-model-item>
              <div class="item-label">流程撤回规则</div>
              <a-form-model-item label="撤回时默认撤回已抄送数据">
                <a-switch v-model="formData.DONE.cancelCopyTo"></a-switch>
              </a-form-model-item>
              <a-form-model-item v-if="formData.DONE.cancelCopyTo" label="含系统自动抄送数据">
                <a-switch v-model="formData.DONE.cancelAutoCopyTo"></a-switch>
              </a-form-model-item>
              <a-form-model-item label="已办结流程可撤回">
                <a-switch v-model="formData.DONE.allowCancelOver"></a-switch>
              </a-form-model-item>
            </div>

            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#delegation">委托设置</div>
              <a-form-model-item label="委托对象范围设置">
                <a-radio-group button-style="solid" v-model="formData.DELEGATION.trusteeScope">
                  <a-radio-button value="all">可委托全组织</a-radio-button>
                  <a-radio-button value="consignorDept">仅可委托同部门人员</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
            </div>

            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#other">其他设置</div>
              <a-form-model-item label="被抄送人表单权限">
                <a-radio-group button-style="solid" v-model="formData.COPY_TO.viewFormMode">
                  <a-radio-button value="copy">固定为抄送时的表单内容</a-radio-button>
                  <a-radio-button value="current">流程当前环节的表单内容</a-radio-button>
                </a-radio-group>
                <div v-if="formData.COPY_TO.viewFormMode == 'copy'" class="item-tip">
                  被抄送人查看的表单内容为抄送时的表单快照，不随流程环节的变化而变化
                </div>
                <div v-else class="item-tip">被抄送人查看的表单内容为流程环节的当前环节表单内容，随流程环节的变化而变化</div>
              </a-form-model-item>
              <div class="item-label">连续签批</div>
              <a-form-model-item label="启用连续签批模式">
                <a-switch v-model="formData.GENERAL.enabledContinuousWorkView"></a-switch>
                <div v-show="formData.GENERAL.enabledContinuousWorkView" class="item-tip">
                  在各类数据视图组件中，如果配置工作流待办数据仓库，则打开待办流程详情时显示“进入连续签批”按钮，点击进入连续签批模式，可快捷连续签批待办
                </div>
              </a-form-model-item>
              <a-form-model-item v-if="formData.GENERAL.enabledContinuousWorkView" label="默认进入连续签批模式">
                <a-switch v-model="formData.GENERAL.defaultContinuousWorkView"></a-switch>
                <span v-show="formData.GENERAL.defaultContinuousWorkView" class="item-tip">
                  前端用户打开流程待办详情时将默认进入连续签批模式
                </span>
              </a-form-model-item>
              <a-form-model-item label="日志保留天数">
                <a-input-number v-model="formData.GENERAL.logRetentionDays" :min="30" :precision="0" style="width: 300px"></a-input-number>
              </a-form-model-item>
              <a-form-model-item label="启用流程审计">
                <a-switch v-model="formData.GENERAL.enabledAudit"></a-switch>
              </a-form-model-item>
              <a-form-model-item label="审计流程" v-if="formData.GENERAL.enabledAudit">
                <a-radio-group button-style="solid" v-model="formData.GENERAL.auditScope">
                  <a-radio-button value="all">所有流程</a-radio-button>
                  <a-radio-button value="specify">指定流程</a-radio-button>
                </a-radio-group>
                <FlowSelect
                  v-if="formData.GENERAL.auditScope == 'specify'"
                  key="flow"
                  mode="list"
                  v-model="formData.GENERAL.auditFlowDefIds"
                  categoryDataAsId
                  style="width: 85%"
                ></FlowSelect>
              </a-form-model-item>
            </div>
          </a-card>

          <a-card :bordered="false">
            <span slot="title">
              <span class="header-title" href="#flowSimulation">流程仿真</span>
              |
              <span class="title-description">模拟流程流转</span>
            </span>
            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#params">仿真参数</div>
              <a-form-model-item label="启动人">
                <OrgSelect
                  v-model="formData.FLOW_SIMULATION.params.startUserId"
                  :multiSelect="formData.FLOW_SIMULATION.params.multiStartUser"
                  :checkableTypes="['user']"
                  style="width: 300px"
                  @change="({ label }) => (formData.FLOW_SIMULATION.params.startUserName = label)"
                ></OrgSelect>
                <span class="item-tip">默认设置的启动人，启动人为空时取当前用户</span>
              </a-form-model-item>
              <a-form-model-item label="启动人可多选">
                <a-switch v-model="formData.FLOW_SIMULATION.params.multiStartUser" @change="multiStartUserChange"></a-switch>
                <span v-show="formData.FLOW_SIMULATION.params.multiStartUser" class="item-tip">多选时按每个启动人依次执行</span>
              </a-form-model-item>
              <a-form-model-item label="仿真报告保留天数">
                <a-input-number
                  v-model="formData.FLOW_SIMULATION.params.recordRetainDays"
                  :min="0"
                  :max="90"
                  :precision="0"
                  style="width: 300px"
                />
              </a-form-model-item>
            </div>
            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#runtime">仿真运行</div>
              <a-form-model-item label="需要弹窗指定办理人时">
                <a-radio-group button-style="solid" v-model="formData.FLOW_SIMULATION.runtime.taskUserNoFound">
                  <a-radio-button value="modal">弹窗由用户选择</a-radio-button>
                  <a-radio-button value="current">取当前用户作为办理人</a-radio-button>
                  <a-radio-button value="assign">指定办理人</a-radio-button>
                </a-radio-group>
                <OrgSelect
                  v-show="formData.FLOW_SIMULATION.runtime.taskUserNoFound == 'assign'"
                  v-model="formData.FLOW_SIMULATION.runtime.assignTaskUserId"
                  :checkableTypes="['user']"
                  style="width: 300px; margin-left: 8px"
                ></OrgSelect>
              </a-form-model-item>
              <a-form-model-item label="需要弹窗指定抄送人时">
                <a-radio-group button-style="solid" v-model="formData.FLOW_SIMULATION.runtime.taskCopyUserNoFound">
                  <a-radio-button value="modal">弹窗由用户选择</a-radio-button>
                  <a-radio-button value="current">取当前用户作为抄送人</a-radio-button>
                  <a-radio-button value="assign">指定抄送人</a-radio-button>
                </a-radio-group>
                <OrgSelect
                  v-show="formData.FLOW_SIMULATION.runtime.taskCopyUserNoFound == 'assign'"
                  v-model="formData.FLOW_SIMULATION.runtime.assignTaskCopyUserId"
                  :checkableTypes="['user']"
                  style="width: 300px; margin-left: 8px"
                ></OrgSelect>
              </a-form-model-item>
              <a-form-model-item label="需要弹窗指定督办人时">
                <a-radio-group button-style="solid" v-model="formData.FLOW_SIMULATION.runtime.taskSuperviseUserNoFound">
                  <a-radio-button value="modal">弹窗由用户选择</a-radio-button>
                  <a-radio-button value="current">取当前用户作为督办人</a-radio-button>
                  <a-radio-button value="assign">指定督办人</a-radio-button>
                </a-radio-group>
                <OrgSelect
                  v-show="formData.FLOW_SIMULATION.runtime.taskSuperviseUserNoFound == 'assign'"
                  v-model="formData.FLOW_SIMULATION.runtime.assignTaskSuperviseUserId"
                  :checkableTypes="['user']"
                  style="width: 300px; margin-left: 8px"
                ></OrgSelect>
              </a-form-model-item>
              <a-form-model-item label="需要弹窗指定决策人时">
                <a-radio-group button-style="solid" v-model="formData.FLOW_SIMULATION.runtime.taskDecisionMakerNoFound">
                  <a-radio-button value="modal">弹窗由用户选择</a-radio-button>
                  <a-radio-button value="current">取当前用户作为决策人</a-radio-button>
                  <a-radio-button value="assign">指定决策人</a-radio-button>
                </a-radio-group>
                <OrgSelect
                  v-show="formData.FLOW_SIMULATION.runtime.taskDecisionMakerNoFound == 'assign'"
                  v-model="formData.FLOW_SIMULATION.runtime.assignTaskDecisionMakerId"
                  :checkableTypes="['user']"
                  style="width: 300px; margin-left: 8px"
                ></OrgSelect>
              </a-form-model-item>
              <div class="item-label">多个办理人</div>
              <a-form-model-item label="多个办理人选择一个具体办理人时">
                <a-radio-group button-style="solid" v-model="formData.FLOW_SIMULATION.runtime.chooseOneUser">
                  <a-radio-button value="modal">弹窗由用户选择</a-radio-button>
                  <a-radio-button value="first">自动取第一个办理人</a-radio-button>
                  <a-radio-button value="random">自动随机取一个办理人</a-radio-button>
                  <a-radio-button value="last">自动取最后一个办理人</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="多个办理人选择多个具体办理人时">
                <a-radio-group button-style="solid" v-model="formData.FLOW_SIMULATION.runtime.chooseMultiUser">
                  <a-radio-button value="modal">弹窗由用户选择</a-radio-button>
                  <a-radio-button value="all">取全部办理人</a-radio-button>
                  <a-radio-button value="random">随机取{{ formData.FLOW_SIMULATION.runtime.randomUserCount }}个办理人</a-radio-button>
                </a-radio-group>
                <a-input-number
                  v-show="formData.FLOW_SIMULATION.runtime.chooseMultiUser == 'random'"
                  v-model="formData.FLOW_SIMULATION.runtime.randomUserCount"
                  :min="1"
                  :max="10"
                  :precision="0"
                  style="width: 150px; margin-left: 8px"
                />
              </a-form-model-item>
              <div class="item-label">多个流向</div>
              <a-form-model-item label="多个流向选择一个流向时">
                <a-radio-group button-style="solid" v-model="formData.FLOW_SIMULATION.runtime.chooseOneDirection">
                  <a-radio-button value="modal">弹窗由用户选择</a-radio-button>
                  <a-radio-button value="random">自动随机取一个流向</a-radio-button>
                  <a-radio-button value="traverse">依次遍历所有流向</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="多个流向选择多个流向时">
                <a-radio-group button-style="solid" v-model="formData.FLOW_SIMULATION.runtime.chooseMultiDirection">
                  <a-radio-button value="modal">弹窗由用户选择</a-radio-button>
                  <a-radio-button value="all">取全部流向</a-radio-button>
                  <a-radio-button value="random">随机取{{ formData.FLOW_SIMULATION.runtime.randomDirectionCount }}个流向</a-radio-button>
                </a-radio-group>
                <a-input-number
                  v-show="formData.FLOW_SIMULATION.runtime.chooseMultiDirection == 'random'"
                  v-model="formData.FLOW_SIMULATION.runtime.randomDirectionCount"
                  :min="1"
                  :max="10"
                  :precision="0"
                  style="width: 150px; margin-left: 8px"
                />
              </a-form-model-item>
              <div class="item-label">一人多职流转</div>
              <a-form-model-item label="一人多职流转选择职位时">
                <a-radio-group button-style="solid" v-model="formData.FLOW_SIMULATION.runtime.chooseOneJob">
                  <a-radio-button value="modal">弹窗由用户选择</a-radio-button>
                  <a-radio-button value="first">自动取第一个职位</a-radio-button>
                  <a-radio-button value="random">自动随机取一个职位</a-radio-button>
                  <a-radio-button value="last">自动取最后一个职位</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <div class="item-label">流程归档</div>
              <a-form-model-item label="流程归档选择归档夹时">
                <a-radio-group button-style="solid" v-model="formData.FLOW_SIMULATION.runtime.chooseArchiveFolder">
                  <a-radio-button value="modal">弹窗由用户选择</a-radio-button>
                  <a-radio-button value="assign">指定归档夹</a-radio-button>
                </a-radio-group>
                <a-tree-select
                  v-show="formData.FLOW_SIMULATION.runtime.chooseArchiveFolder == 'assign'"
                  v-model="assignArchiveFolderName"
                  tree-data-simple-mode
                  style="width: 300px; margin-left: 8px"
                  :dropdown-style="{ maxHeight: '300px', overflow: 'auto' }"
                  :tree-data="folderTreeData"
                  :replaceFields="{
                    title: 'name',
                    key: 'id',
                    value: 'id'
                  }"
                  placeholder="请选择"
                  :load-data="onLoadFolderTreeData"
                  @select="onSelectedFolderTreeNode"
                />
              </a-form-model-item>
            </div>
          </a-card>

          <a-card :bordered="false">
            <span slot="title">
              <span class="header-title" href="#flowReport">统计报表</span>
              |
              <span class="title-description">流程统计分析</span>
            </span>
            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#search">高级查询</div>
              <a-form-model-item label="最近发起时间默认天数">
                <a-input-number v-model="formData.REPORT.search.recentStartTimeRange" :min="1"></a-input-number>
              </a-form-model-item>
            </div>
            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#flowInstCount">流程量分析</div>
              <a-form-model-item label="流程分类流程量排名最多显示柱状个数">
                <a-input-number v-model="formData.REPORT.flowInstCount.maxBarCount" :min="1"></a-input-number>
              </a-form-model-item>
            </div>
            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#flowEfficiency">流程效率分析</div>
              <a-form-model-item label="流程审批平均时长最多显示柱状个数">
                <a-input-number v-model="formData.REPORT.flowEfficiency.maxFlowBarCount" :min="1"></a-input-number>
              </a-form-model-item>
              <a-form-model-item label="流程审批环节平均用时最多显示柱状个数">
                <a-input-number v-model="formData.REPORT.flowEfficiency.maxTaskBarCount" :min="1"></a-input-number>
              </a-form-model-item>
            </div>
            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#flowOverdue">流程逾期分析</div>
              <a-form-model-item label="流程逾期数量排名最多显示柱状个数">
                <a-input-number v-model="formData.REPORT.flowOverdue.maxOverdueBarCount" :min="1"></a-input-number>
              </a-form-model-item>
              <a-form-model-item label="流程平均逾期时长排名最多显示柱状个数">
                <a-input-number v-model="formData.REPORT.flowOverdue.maxAvgOverdueBarCount" :min="1"></a-input-number>
              </a-form-model-item>
            </div>
            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#flowOperation">流程行为分析</div>
              <a-form-model-item label="流程低效操作率最多显示柱状个数">
                <a-input-number v-model="formData.REPORT.flowOperation.maxInefficientBarCount" :min="1"></a-input-number>
              </a-form-model-item>
            </div>
            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#flowTodo">流程待办分析</div>
              <a-form-model-item label="人员待办最多显示柱状个数">
                <a-input-number v-model="formData.REPORT.flowTodo.maxUserBarCount" :min="1"></a-input-number>
              </a-form-model-item>
              <a-form-model-item label="部门待办最多显示柱状个数">
                <a-input-number v-model="formData.REPORT.flowTodo.maxDeptBarCount" :min="1"></a-input-number>
              </a-form-model-item>
            </div>
            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#flowHandleEfficiency">办理效率分析</div>
              <a-form-model-item label="按人员统计最多显示柱状个数">
                <a-input-number v-model="formData.REPORT.flowHandleEfficiency.maxUserBarCount" :min="1"></a-input-number>
              </a-form-model-item>
              <a-form-model-item label="按部门统计最多显示柱状个数">
                <a-input-number v-model="formData.REPORT.flowHandleEfficiency.maxDeptBarCount" :min="1"></a-input-number>
              </a-form-model-item>
            </div>
            <div class="content-item">
              <div class="sub-title pt-title-vertical-line" href="#flowHandleOverdue">办理逾期分析</div>
              <a-form-model-item label="按人员统计最多显示柱状个数">
                <a-input-number v-model="formData.REPORT.flowHandleOverdue.maxUserBarCount" :min="1"></a-input-number>
              </a-form-model-item>
              <a-form-model-item label="按部门统计最多显示柱状个数">
                <a-input-number v-model="formData.REPORT.flowHandleOverdue.maxDeptBarCount" :min="1"></a-input-number>
              </a-form-model-item>
            </div>
          </a-card>
        </PerfectScrollbar>
      </a-col>
    </a-row>
    <div class="btn-container">
      <a-button @click="restoreFlowSettings">恢复默认</a-button>
      <a-button type="primary" @click="saveFlowSettings">保存</a-button>
    </div>
  </a-form-model>
</template>

<script>
import { deepClone } from '@framework/vue/utils/util';
import TitleDefineTemplate from '@workflow/app/web/page/workflow-designer/component/commons/title-define-template.vue';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import FlowSelect from '@workflow/app/web/lib/flow-select.vue';
const DEFAULT_SETTINGS = {
  FLOW_DEFINITION: {
    titleExpression: '${流程名称}_${发起人姓名}-${发起人所在部门名称}_${发起年}-${发起月}-${发起日}',
    enabledAutoSubmit: false,
    autoSubmitMode: 'before', // before前置审批、after后置审批
    task: {
      counterSignViewFormMode: 'default', // default, readonly，custom
      addSignViewFormMode: 'default', // default, readonly，custom
      transferViewFormMode: 'default' // default, readonly，custom
    },
    enabledJobDuty: false,
    saveEncoder: 'none' // none, base64
  },
  GENERAL: {
    defaultOpenWorkViewSidebar: false,
    defaultSplitWorkView: false,
    aclRoleIsolation: true,
    showRecentUsed: true,
    showActionTip: false,
    enabledContinuousWorkView: false,
    defaultContinuousWorkView: false,
    todoDeleteMode: '', // logical、physical
    logRetentionDays: 90,
    enabledAudit: false,
    auditScope: 'all', // all、specify
    auditFlowDefIds: []
  },
  OPINION_FILE: {
    enabled: true,
    numLimit: 10,
    sizeLimit: 100,
    sizeLimitUnit: 'MB',
    accept: [],
    allowNameRepeat: true,
    downloadAllType: '1',
    category: 'SIGN_OPINION'
  },
  OPINION_EDITOR: {
    showMode: 'sidebar', // all、modal、sidebar
    showSignAndSubmitButton: true,
    showConfirmSubmitButton: true,
    category: 'SIGN_OPINION'
  },
  PROCESS_VIEWER: {
    showMode: 'sidebar', // all、modal、sidebar
    designerShowMode: 'sidebar', // all、modal、sidebar
    operatorIdentityMode: 'primary' // primary、actual
  },
  DONE: {
    viewFormMode: 'done', // done、current
    cancelCopyTo: false,
    cancelAutoCopyTo: false,
    allowCancelOver: true
  },
  COPY_TO: {
    viewFormMode: 'copy' // copy、current
  },
  DELEGATION: {
    trusteeScope: 'all' // all, consignorDept
  },
  FLOW_SIMULATION: {
    params: { startUserId: '', startUserName: '', multiStartUser: false, recordRetainDays: 3 },
    runtime: {
      taskUserNoFound: 'modal', // modal、current、assign
      assignTaskUserId: '',
      taskCopyUserNoFound: 'modal', // modal、current、assign
      assignTaskCopyUserId: '',
      taskSuperviseUserNoFound: 'modal', // modal、current、assign
      assignTaskSuperviseUserId: '',
      taskDecisionMakerNoFound: 'modal', // modal、current、assign
      assignTaskDecisionMakerId: '',
      chooseOneUser: 'modal', // modal、first、random、last
      chooseMultiUser: 'modal', // modal、all、random
      randomUserCount: 3,
      chooseOneDirection: 'modal', // modal、random、traverse
      chooseMultiDirection: 'modal', // modal、all、random
      randomDirectionCount: 3,
      chooseOneJob: 'modal', // modal、first、random、last
      chooseArchiveFolder: 'modal', // modal、assign
      assignArchiveFolderUuid: '',
      assignArchiveFolderName: ''
    }
  },
  REPORT: {
    search: {
      recentStartTimeRange: 30
    },
    flowInstCount: {
      maxBarCount: 12
    },
    flowEfficiency: {
      maxFlowBarCount: 12,
      maxTaskBarCount: 12
    },
    flowOverdue: {
      maxOverdueBarCount: 12,
      maxAvgOverdueBarCount: 12
    },
    flowOperation: {
      maxInefficientBarCount: 12
    },
    flowTodo: {
      maxUserBarCount: 12,
      maxDeptBarCount: 12
    },
    flowHandleEfficiency: {
      maxUserBarCount: 12,
      maxDeptBarCount: 12
    },
    flowHandleOverdue: {
      maxUserBarCount: 12,
      maxDeptBarCount: 12
    }
  }
};
export default {
  components: { TitleDefineTemplate, OrgSelect, FlowSelect },
  data() {
    return {
      formData: deepClone(DEFAULT_SETTINGS),
      navs: [
        {
          label: '流程定义',
          value: 'flowDefinition',
          children: [
            { label: '流程', value: 'flow' },
            { label: '环节', value: 'task' },
            { label: '子流程', value: 'subtask' },
            { label: '其他', value: 'otherDef' }
          ]
        },
        {
          label: '流程签批',
          value: 'workflow',
          children: [
            { label: '通用设置', value: 'general' },
            { label: '发起设置', value: 'start' },
            { label: '待办设置', value: 'todo' },
            { label: '已办设置', value: 'done' },
            { label: '委托设置', value: 'delegation' },
            { label: '其他设置', value: 'other' }
          ]
        },
        // {
        //   label: '流程管理',
        //   value: 'flowManager'
        // },
        {
          label: '流程仿真',
          value: 'flowSimulation',
          children: [
            { label: '仿真参数', value: 'params' },
            { label: '仿真运行', value: 'runtime' }
          ]
        },
        {
          label: '统计报表',
          value: 'flowReport',
          children: [
            { label: '高级查询', value: 'search' },
            { label: '流程量分析', value: 'flowInstCount' },
            { label: '流程效率分析', value: 'flowEfficiency' },
            { label: '流程逾期分析', value: 'flowOverdue' },
            { label: '流程行为分析', value: 'flowOperation' },
            { label: '流程待办分析', value: 'flowTodo' },
            { label: '办理效率分析', value: 'flowHandleEfficiency' },
            { label: '办理逾期分析', value: 'flowHandleOverdue' }
          ]
        }
      ],
      fileSizeUnitOptions: [
        { label: 'KB', value: 'KB' },
        { label: 'MB', value: 'MB' },
        { label: 'GB', value: 'GB' }
      ],
      fileExtensionOptions: [
        { label: '.doc', value: '.doc' },
        { label: '.docx', value: '.docx' },
        { label: '.ppt', value: '.ppt' },
        { label: '.pptx', value: '.pptx' },
        { label: '.xls', value: '.xls' },
        { label: '.xlsx', value: '.xlsx' },
        { label: '.pdf', value: '.pdf' },
        { label: '.txt', value: '.txt' },
        { label: '.zip', value: '.zip' },
        { label: '.png', value: '.png' },
        { label: '.jpg', value: '.jpg' },
        { label: '.jpeg', value: '.jpeg' },
        { label: '.gif', value: '.gif' }
      ],
      folderTreeData: [],
      assignArchiveFolderName: '',
      scrollHeight: 700
    };
  },
  created() {
    this.loadFlowSettings();
    this.loadFolderTreeData().then(treeData => {
      this.folderTreeData = treeData;
    });
  },
  mounted() {
    this.getVpageHeight();
  },
  methods: {
    onContentNavClick({ key }) {
      let targetEl = this.$el.querySelector(`*[href="#${key}"]`);
      targetEl && targetEl.scrollIntoView();
    },
    loadFlowSettings() {
      let formData = deepClone(DEFAULT_SETTINGS);
      $axios.get('/proxy/api/workflow/setting/list').then(({ data: result }) => {
        if (result.data) {
          result.data.forEach(item => {
            if (formData[item.attrKey]) {
              formData[item.attrKey] = Object.assign(formData[item.attrKey], JSON.parse(item.attrVal));
            } else {
              formData[item.attrKey] = JSON.parse(item.attrVal);
            }
            formData[item.attrKey].enabled = item.enabled;
            formData[item.attrKey].category = item.category;
            formData[item.attrKey].remark = item.remark;
          });
          this.formData = formData;
          if (this.formData && this.formData.FLOW_DEFINITION && this.formData.FLOW_DEFINITION.titleExpression) {
            this.$refs.titleDefineTemplate.$refs.template.update({
              value: this.formData.FLOW_DEFINITION.titleExpression
            });
          }
          this.assignArchiveFolderName = this.formData.FLOW_SIMULATION.runtime.assignArchiveFolderName;
        }
      });
    },
    multiStartUserChange() {
      if (!this.formData.FLOW_SIMULATION.params.multiStartUser) {
        let startUserId = this.formData.FLOW_SIMULATION.params.startUserId || '';
        let userIds = startUserId.split(';');
        if (userIds.length > 1) {
          this.formData.FLOW_SIMULATION.params.startUserId = userIds[0];
        }
      }
    },
    loadFolderTreeData(parentUuid = '-1') {
      return $axios
        .post('/json/data/services', {
          serviceName: 'dmsFileManagerService',
          methodName: 'getFolderTreeAsync',
          args: JSON.stringify([parentUuid])
        })
        .then(({ data: result }) => {
          let treeData = result.data || [];
          treeData.forEach(node => {
            if (node.id == this.formData.FLOW_SIMULATION.runtime.assignArchiveFolderUuid) {
              this.assignArchiveFolderName = node.id;
            }
          });
          return treeData;
        });
    },
    onLoadFolderTreeData(treeNode) {
      return this.loadFolderTreeData(treeNode.dataRef.id).then(treeData => {
        let nodeData = treeNode.dataRef;
        nodeData.children = treeData;
        this.folderTreeData = [...this.folderTreeData];
      });
    },
    onSelectedFolderTreeNode(value, node, extra) {
      this.formData.FLOW_SIMULATION.runtime.assignArchiveFolderUuid = value;
      this.formData.FLOW_SIMULATION.runtime.assignArchiveFolderName = node.dataRef.name;
    },
    saveFlowSettings() {
      let settings = [];
      for (let key in this.formData) {
        let valObject = this.formData[key];
        let val = JSON.stringify(valObject);
        let enabled = valObject.hasOwnProperty('enabled') ? valObject.enabled : true;
        let category = valObject.hasOwnProperty('category') ? valObject.category : 'DEFAULT';
        let remark = valObject.hasOwnProperty('remark') ? valObject.remark : null;
        settings.push({
          attrKey: key,
          attrVal: val,
          enabled,
          category,
          remark
        });
      }
      $axios
        .post('/api/workflow/setting/saveAll', settings)
        .then(({ data: result }) => {
          if (result.code == 0) {
            this.$message.success('保存成功！');
          } else {
            this.$message.error(result.msg || '保存失败！');
          }
        })
        .catch(({ response }) => {
          this.$message.error((response && response.data && response.data.msg) || '服务异常！');
        });
    },
    restoreFlowSettings() {
      this.formData = deepClone(DEFAULT_SETTINGS);
      this.$message.info('已恢复，未保存！');
    },
    getVpageHeight() {
      let vpageHeight = 700;
      if (this.$root.$el.classList.contains('preview')) {
        vpageHeight = this.$root.$el.offsetHeight;
      } else if (this.$el.closest('.widget-vpage')) {
        vpageHeight = this.$el.closest('.widget-vpage').offsetHeight;
      } else {
        setTimeout(() => {
          this.getVpageHeight();
        }, 0);
      }
      this.scrollHeight = vpageHeight - 46;
    }
  }
};
</script>

<style lang="less" scoped>
.flow-settings {
  --w-radio-button-solid-border-radius: 4px;
  .content-nav {
    padding: 12px 12px 52px 12px;
    height: e('calc(100vh - 220px)');
    border-right: 1px solid var(--w-border-color-light);
    ::v-deep .ant-menu {
      height: 100%;
      overflow-y: auto;
    }

    .child-nav {
      padding-left: 28px;
    }
  }

  .header-title {
    font-weight: bold;
  }
  .title-description {
    font-size: var(--w-font-size-sm);
    color: var(--w-text-color-light);
  }

  .content-body {
    padding: 12px 20px;
    width: 0;

    .ant-card {
      --w-card-head-padding: 0 20px 0 0;
      --w-card-body-padding-lr: 0px;
    }
  }

  .content-item {
    .sub-title {
      padding-left: 8px;
      font-size: var(--w-font-size-base);
      color: var(--w-text-color-darker);
      line-height: 32px;
      margin-bottom: 12px;
    }

    ::v-deep .ant-form-item-no-colon {
      margin-left: 8px;
    }
    .item-label {
      margin: 12px 8px;
      font-size: var(--w-font-size-sm);
      color: var(--w-text-color-light);
    }
    .item-tip {
      font-size: var(--w-font-size-sm);
      color: var(--w-text-color-light);
      padding: 4px 0;
      line-height: 20px;
    }
    span.item-tip {
      padding-left: 12px;
    }
  }

  .btn-container {
    position: fixed;
    border-top: 1px solid var(--w-border-color-light);
    width: 100%;
    height: 52px;
    line-height: 52px;
    left: 5%;
    bottom: 0;
    text-align: center;
    z-index: 1;
    background-color: var(--w-bg-color-body);
    // position: fixed;
    // bottom: 30px;
    // padding-left: calc(33%);
    // width: e('calc(33% + 200px)');
    // text-align: center;
    // z-index: 1;
    // background: var(--w-bg-color-body);
  }
}
</style>
