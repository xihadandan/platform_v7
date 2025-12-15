<template>
  <a-form-model
    ref="form"
    :model="formData"
    :rules="rules"
    class="dingtalk-config pt-form"
    :label-col="{ span: 6 }"
    :wrapper-col="{ span: 18 }"
    :colon="false"
  >
    <a-tabs v-model="activeKey" default-active-key="1" type="card">
      <a-tab-pane key="1" tab="基本信息">
        <a-form-model-item prop="appId">
          <template slot="label">
            <a-space>
              App ID
              <a-popover>
                <template slot="content">
                  应用唯一标识（UnifiedAppId）,可用于获取应用的基本信息、配置信息、数据等，后续会逐步替代原内部应用AgentId和三方应用AppId
                </template>
                <a-icon type="info-circle" />
              </a-popover>
            </a-space>
          </template>
          <a-input v-model="formData.appId"></a-input>
        </a-form-model-item>
        <a-form-model-item prop="clientId">
          <template slot="label">
            <a-space>
              Client ID
              <a-popover>
                <template slot="content">Client ID (原 AppKey 和 SuiteKey)</template>
                <a-icon type="info-circle" />
              </a-popover>
            </a-space>
          </template>
          <a-input v-model="formData.clientId"></a-input>
        </a-form-model-item>
        <a-form-model-item prop="clientSecret">
          <template slot="label">
            <a-space>
              Client Secret
              <a-popover>
                <template slot="content">Client Secret (原 AppSecret 和 SuiteSecret)</template>
                <a-icon type="info-circle" />
              </a-popover>
            </a-space>
          </template>
          <a-input v-model="formData.clientSecret"></a-input>
        </a-form-model-item>
        <!-- <a-form-model-item label="服务URL" prop="serviceUri">
          <a-input v-model="formData.serviceUri" placeholder="https://api.dingtalk.com"></a-input>
        </a-form-model-item> -->
        <a-form-model-item prop="agentId">
          <template slot="label">
            <a-space>
              企业代理ID
              <a-popover>
                <template slot="content">原企业内部应用AgentId</template>
                <a-icon type="info-circle" />
              </a-popover>
            </a-space>
          </template>
          <a-input v-model="formData.agentId"></a-input>
        </a-form-model-item>
        <a-form-model-item label="企业ID" prop="corpId">
          <a-input v-model="formData.corpId"></a-input>
        </a-form-model-item>
        <a-form-model-item label="企业回调域名" prop="corpDomainUri">
          <a-input v-model="formData.corpDomainUri"></a-input>
        </a-form-model-item>
        <a-form-model-item label="移动端服务URL" prop="mobileAppUri">
          <a-input v-model="formData.mobileAppUri"></a-input>
        </a-form-model-item>
        <a-form-model-item label="是否启用" prop="enabled">
          <a-switch v-model="formData.enabled" checkedChildren="开启" unCheckedChildren="关闭"></a-switch>
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="2" tab="组织同步">
        <a-form-model-item label="钉钉通讯录全量同步到平台行政组织" prop="enabledSyncOrg">
          <a-switch v-model="formData.configuration.enabledSyncOrg" checkedChildren="开启" unCheckedChildren="关闭"></a-switch>
          <span v-if="formData.configuration.enabledSyncOrg">
            接口权限要求: 通讯录部门信息读权限、通讯录部门成员读权限、企业员工手机号信息、邮箱等个人信息
          </span>
        </a-form-model-item>
        <a-form-model-item label="被同步的行政组织" prop="orgUuid">
          <a-select
            v-model="formData.configuration.orgUuid"
            show-search
            style="width: 100%"
            :options="orgOptions"
            :filter-option="filterSelectOption"
          ></a-select>
        </a-form-model-item>
        <a-form-model-item label="同步内容">
          <a-descriptions layout="horizontal" bordered :column="1">
            <a-descriptions-item label="部门">
              <a-switch
                v-model="formData.configuration.orgSyncOption.dept"
                disabled
                checkedChildren="开启"
                unCheckedChildren="关闭"
              ></a-switch>
            </a-descriptions-item>
            <a-descriptions-item label="人员">
              <a-row>
                <a-col span="4">姓名</a-col>
                <a-col span="12">作为 OA姓名</a-col>
                <a-col span="8">
                  <a-switch
                    v-model="formData.configuration.orgSyncOption.userName"
                    disabled
                    checkedChildren="开启"
                    unCheckedChildren="关闭"
                  ></a-switch>
                </a-col>
              </a-row>
              <a-row>
                <a-col span="4">头像</a-col>
                <a-col span="12">作为 OA头像</a-col>
                <a-col span="8">
                  <a-switch
                    v-model="formData.configuration.orgSyncOption.userAvatar"
                    checkedChildren="开启"
                    unCheckedChildren="关闭"
                  ></a-switch>
                </a-col>
              </a-row>
              <a-row>
                <a-col span="4">手机号码</a-col>
                <a-col span="12">作为 OA手机号码</a-col>
                <a-col span="8">
                  <a-switch
                    v-model="formData.configuration.orgSyncOption.userMobile"
                    disabled
                    checkedChildren="开启"
                    unCheckedChildren="关闭"
                  ></a-switch>
                </a-col>
              </a-row>
              <a-row>
                <a-col span="4">分机号</a-col>
                <a-col span="12">作为 OA办公电话</a-col>
                <a-col span="8">
                  <a-switch
                    v-model="formData.configuration.orgSyncOption.userTelephone"
                    checkedChildren="开启"
                    unCheckedChildren="关闭"
                  ></a-switch>
                </a-col>
              </a-row>
              <a-row>
                <a-col span="4">邮箱</a-col>
                <a-col span="12">作为 OA邮箱</a-col>
                <a-col span="8">
                  <a-switch
                    v-model="formData.configuration.orgSyncOption.userEmail"
                    checkedChildren="开启"
                    unCheckedChildren="关闭"
                  ></a-switch>
                </a-col>
              </a-row>
              <a-row>
                <a-col span="4">员工编号</a-col>
                <a-col span="12">作为 OA员工编号</a-col>
                <a-col span="8">
                  <a-switch
                    v-model="formData.configuration.orgSyncOption.userNo"
                    checkedChildren="开启"
                    unCheckedChildren="关闭"
                  ></a-switch>
                </a-col>
              </a-row>
              <a-row>
                <a-col span="4">备注</a-col>
                <a-col span="12">作为 OA备注</a-col>
                <a-col span="8">
                  <a-switch
                    v-model="formData.configuration.orgSyncOption.userRemark"
                    checkedChildren="开启"
                    unCheckedChildren="关闭"
                  ></a-switch>
                </a-col>
              </a-row>
            </a-descriptions-item>
            <a-descriptions-item label="人员和职位的关系">
              <a-switch v-model="formData.configuration.orgSyncOption.job" checkedChildren="开启" unCheckedChildren="关闭"></a-switch>
            </a-descriptions-item>
          </a-descriptions>
        </a-form-model-item>
        <a-form-model-item v-if="formData.configuration.orgSyncOption.job" label="主职位同步方式">
          <a-radio-group v-model="formData.configuration.orgSyncOption.jobMode" :options="jobModeOptions" />
        </a-form-model-item>
        <a-form-model-item label="监听事件同步">
          <a-checkbox-group v-model="formData.configuration.orgSyncEvents" :options="orgSyncEventOptions" />
          <span>接口权限要求: 成员信息读权限、获取部门详情</span>
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="3" tab="流程审批">
        <a-form-model-item label="待办任务" prop="enabledTodoTask">
          <a-switch v-model="formData.configuration.enabledTodoTask" checkedChildren="开启" unCheckedChildren="关闭"></a-switch>
          <span v-if="formData.configuration.enabledTodoTask">工作到达时创建钉钉待办任务，权限要求: 待办应用中待办写权限</span>
        </a-form-model-item>
        <a-form-model-item label="消息通知" prop="enabledPushMsg">
          <a-switch v-model="formData.configuration.enabledPushMsg" checkedChildren="开启" unCheckedChildren="关闭"></a-switch>
        </a-form-model-item>
        <template v-if="formData.configuration.enabledPushMsg">
          <a-form-model-item prop="todoMsgTemplateType">
            <template slot="label">
              <a-space>
                待办消息内容模板
                <a-popover>
                  <template slot="content">
                    使用在线消息模板的标题及内容，增加内置变量_dingtalkMsg(标记为钉钉消息)、_workUrl(工作详情地址)
                  </template>
                  <a-icon type="info-circle" />
                </a-popover>
              </a-space>
            </template>
            <a-radio-group size="small" v-model="formData.configuration.todoMsgTemplateType">
              <a-radio-button value="todo">按流程待办消息配置的模板</a-radio-button>
              <a-radio-button value="custom">统一指定</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item v-if="formData.configuration.todoMsgTemplateType == 'custom'" label="消息模板" prop="todoMsgTemplateId">
            <a-select
              v-model="formData.configuration.todoMsgTemplateId"
              show-search
              style="width: 100%"
              :options="messageTemplateOptions"
              :filter-option="filterSelectOption"
            ></a-select>
          </a-form-model-item>
        </template>
        <a-form-model-item label="事项群聊">
          <a-switch v-model="formData.configuration.groupChat.enabled" checkedChildren="开启" unCheckedChildren="关闭"></a-switch>
          <span v-if="formData.configuration.groupChat.enabled">权限要求: 钉钉群基础信息管理权限</span>
        </a-form-model-item>
        <template v-if="formData.configuration.groupChat.enabled">
          <a-form-model-item label="使用群模板">
            <a-switch v-model="formData.configuration.groupChat.useTemplate" checkedChildren="开启" unCheckedChildren="关闭"></a-switch>
            <span v-if="formData.configuration.groupChat.useTemplate">通过群模板添加群机器人，可通过机器人发送消息到群</span>
          </a-form-model-item>
          <template v-if="formData.configuration.groupChat.useTemplate">
            <a-form-model-item label="群模板ID">
              <a-input v-model="formData.configuration.groupChat.templateId" />
              通过钉钉
              <a href="https://open-dev.dingtalk.com/fe/im?hash=%23%2Fgroup%2Flist#/group/list" target="_blank">群模板</a>
              创建的模板ID
              <br />
            </a-form-model-item>
            <a-form-model-item label="群机器人ID">
              <a-input v-model="formData.configuration.groupChat.robotId" />
              群模板添加的群机器人ID，可通过机器人发送消息到群。权限要求：企业内机器人发送消息权限
              <br />
              消息回调地址: http{s}://平台公网服务域名:端口/api/dingtalk/robot/callback/{{ _$SYSTEM_ID }}
              <br />
              <a @click="getRobotToken">获取消息回调token</a>
              <div v-if="robotToken">{{ robotToken }}</div>
            </a-form-model-item>
          </template>
          <a-form-model-item label="事项群聊发起方式">
            <a-checkbox-group v-model="formData.configuration.groupChat.startModes">
              <a-checkbox value="multiUser">按环节多人办理且每个人都要办理</a-checkbox>
              <a-checkbox value="collaboration">按协作环节</a-checkbox>
            </a-checkbox-group>
          </a-form-model-item>
          <a-form-model-item label="事项群聊流程">
            <a-radio-group
              size="small"
              v-model="formData.configuration.groupChat.scope"
              @change="formData.configuration.groupChat.values = []"
            >
              <a-radio-button value="all">全部流程</a-radio-button>
              <a-radio-button value="flow">指定流程</a-radio-button>
              <a-radio-button value="task">指定流程环节</a-radio-button>
            </a-radio-group>
            <div v-if="formData.configuration.groupChat.scope == 'flow'">
              <FlowSelect
                key="flow"
                mode="list"
                v-model="formData.configuration.groupChat.values"
                showRecentUsed
                categoryDataAsId
                style="width: 85%"
              ></FlowSelect>
              <br />
              *选择具体流程作可进行事项群聊
            </div>
            <div v-if="formData.configuration.groupChat.scope == 'task'">
              <FlowSelect
                key="task"
                title="选择环节"
                mode="list"
                v-model="formData.configuration.groupChat.values"
                checkType="task"
                style="width: 85%"
              ></FlowSelect>
              <br />
              *选择指定流程的某些环节可进行事项群聊
            </div>
          </a-form-model-item>
        </template>
      </a-tab-pane>
    </a-tabs>
    <div style="text-align: center; margin: 10px 0 20px">
      <a-button type="primary" @click="saveConfig">保存</a-button>
      <a-button v-if="activeKey == '1'" @click="testCreateToken">生成测试访问凭证</a-button>
      <a-button v-if="activeKey == '2' && formData.configuration.enabledSyncOrg && formData.configuration.orgUuid" @click="syncOrg">
        全量同步
      </a-button>
    </div>
  </a-form-model>
</template>

<script>
import { filterSelectOption } from '@framework/vue/utils/function';
import { getElSpacingForTarget } from '@framework/vue/utils/util';
import FlowSelect from '@workflow/app/web/lib/flow-select.vue';
export default {
  components: { FlowSelect },
  data() {
    return {
      activeKey: '1',
      formData: {
        appId: '',
        clientId: '',
        clientSecret: '',
        serviceUri: '',
        agentId: '',
        corpId: '',
        corpDomainUri: '',
        mobileAppUri: '',
        enabled: false,
        configuration: {
          orgSyncOption: {},
          jobMode: 'first',
          groupChat: { scope: 'all' }
        }
      },
      rules: {
        clientId: [{ required: true, message: '不能为空！', trigger: 'blur' }],
        clientSecret: [{ required: true, message: '不能为空！', trigger: 'blur' }]
      },
      orgOptions: [],
      orgSyncEventOptions: [
        { label: '通讯录用户增加', value: 'user_add_org' },
        { label: '通讯录用户更改', value: 'user_modify_org' },
        { label: '通讯录用户离职', value: 'user_leave_org' },
        { label: '加入企业后用户激活', value: 'user_active_org' },
        { label: '通讯录企业部门创建', value: 'org_dept_create' },
        { label: '通讯录企业部门修改', value: 'org_dept_modify' },
        { label: '通讯录企业部门删除', value: 'org_dept_remove' }
      ],
      jobModeOptions: [
        { label: '人员最先加入的部门', value: 'first' },
        { label: '人员最后加入的部门', value: 'last' }
      ],
      messageTemplateOptions: [],
      robotToken: ''
    };
  },
  beforeMount() {
    if (!this.designMode) {
      this.loadConfig();
      this.loadOrgOptions();
      this.loadMessageTemplateOptions();
    }
  },
  mounted() {
    this.setTabContentHeight();
  },
  methods: {
    setTabContentHeight() {
      if (this.$el) {
        let $parent = this.$root.$el.classList.contains('preview') ? this.$root.$el : this.$el.closest('.widget-vpage');
        const { maxHeight, totalBottom, totalNextSibling } = getElSpacingForTarget(this.$el.querySelector('.ant-tabs-content'), $parent);
        this.$el.querySelector('.ant-tabs-content').style.cssText = `overflow-y:auto; height:${maxHeight}px`;
      } else {
        setTimeout(() => {
          this.setTabContentHeight();
        }, 100);
      }
    },
    filterSelectOption,
    loadConfig() {
      const _this = this;
      $axios
        .get('/proxy/api/dingtalk/config/getByCurrentSystem')
        .then(({ data: result }) => {
          if (result.data) {
            let formData = result.data;
            if (formData.definitionJson) {
              formData.configuration = JSON.parse(formData.definitionJson);
            } else {
              formData.configuration = formData.configuration || {};
            }
            if (!formData.configuration.orgSyncOption) {
              formData.configuration.orgSyncOption = { dept: true, userName: true, userMobile: true };
            }
            if (!formData.configuration.groupChat) {
              formData.configuration.groupChat = { scope: 'all' };
            }
            _this.formData = formData;
          } else {
            _this.$message.error(result.msg || '服务异常！');
          }
        })
        .catch(({ response }) => {
          _this.$message.error((response && response.data && response.data.msg) || '服务异常！');
        });
    },
    loadOrgOptions() {
      $axios.get(`/proxy/api/org/organization/queryEnableOrgs?system=${this._$SYSTEM_ID}`).then(({ data: result }) => {
        if (result.data) {
          this.orgOptions = result.data.map(item => ({ value: item.uuid, label: item.name }));
        }
      });
    },
    loadMessageTemplateOptions() {
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'messageTemplateApiFacade',
          pageSize: 1000,
          pageNo: 1
        })
        .then(({ data: result }) => {
          if (result.results) {
            this.messageTemplateOptions = result.results.map(item => ({ value: item.id, label: item.text }));
          }
        });
    },
    saveConfig() {
      const _this = this;
      _this.$refs.form.validate().then(valid => {
        if (valid) {
          let formData = _this.formData;
          if (formData.configuration) {
            formData.definitionJson = JSON.stringify(formData.configuration);
          }
          $axios
            .post('/proxy/api/dingtalk/config/save', formData)
            .then(({ data: result }) => {
              if (result.code == 0) {
                if (!formData.uuid) {
                  formData.uuid = result.data;
                }
                _this.$message.success('保存成功！');
              } else {
                _this.$message.error(result.msg || '服务异常！');
              }
            })
            .catch(({ response }) => {
              _this.$message.error((response && response.data && response.data.msg) || '服务异常！');
            });
        }
      });
    },
    testCreateToken() {
      const _this = this;
      $axios
        .get(
          `/proxy/api/dingtalk/config/testCreateToken?clientId=${this.formData.clientId}&clientSecret=${
            this.formData.clientSecret
          }&baseUrl=${this.formData.serviceUri || ''}`
        )
        .then(({ data: result }) => {
          if (result.code == 0) {
            _this.$message.success('测试成功！');
          } else {
            _this.$message.error(result.msg || '测试失败！');
          }
        })
        .catch(({ response }) => {
          _this.$message.error((response && response.data && response.data.msg) || '服务异常！');
        });
    },
    syncOrg() {
      const _this = this;
      _this.$refs.form.validate().then(valid => {
        if (valid) {
          let formData = _this.formData;
          if (formData.configuration) {
            formData.definitionJson = JSON.stringify(formData.configuration);
          }
          _this.$confirm({
            title: '确认',
            content: `存在组织元素时，组织全量同步会生成新的组织版本，确认全量同步组织？`,
            onOk() {
              _this.$loading('同步中...');
              $axios
                .post('/proxy/api/dingtalk/config/syncOrg', formData)
                .then(({ data: result }) => {
                  _this.$loading(false);
                  if (result.data) {
                    _this.$message.success('同步成功！');
                  } else {
                    _this.$message.error('同步失败！');
                  }
                })
                .catch(({ response }) => {
                  _this.$loading(false);
                  _this.$message.error((response && response.data && response.data.msg) || '服务异常！');
                });
            }
          });
        }
      });
    },
    getRobotToken() {
      $axios.get('/proxy/api/dingtalk/robot/getToken').then(({ data: result }) => {
        this.robotToken = result.data;
      });
    }
  }
};
</script>

<style lang="less" scoped>
.btn-container {
  position: fixed;
  left: 100px;
  bottom: 20px;
  width: 100%;
  text-align: center;
  z-index: 1;
  background: var(--w-bg-color-body);
}
</style>
