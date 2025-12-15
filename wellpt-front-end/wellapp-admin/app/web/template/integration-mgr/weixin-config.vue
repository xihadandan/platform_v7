<template>
  <a-form-model
    ref="form"
    :model="formData"
    :rules="rules"
    class="weixin-config pt-form"
    :label-col="{ span: 6 }"
    :wrapper-col="{ span: 18 }"
    :colon="false"
  >
    <a-tabs v-model="activeKey" default-active-key="1" type="card">
      <a-tab-pane key="1" tab="基本信息">
        <a-form-model-item prop="appId">
          <template slot="label">
            <a-space>
              应用的ID
              <a-popover>
                <template slot="content">应用AgentId</template>
                <a-icon type="info-circle" />
              </a-popover>
            </a-space>
          </template>
          <a-input v-model="formData.appId"></a-input>
        </a-form-model-item>
        <a-form-model-item prop="appSecret">
          <template slot="label">
            <a-space>
              应用的凭证密钥
              <a-popover>
                <template slot="content">
                  secret是企业应用里面用于保障数据安全的“钥匙”，每一个应用都有一个独立的访问密钥，为了保证数据的安全，secret务必不能泄漏
                </template>
                <a-icon type="info-circle" />
              </a-popover>
            </a-space>
          </template>
          <a-input v-model="formData.appSecret"></a-input>
        </a-form-model-item>
        <a-form-model-item prop="corpId">
          <template slot="label">
            <a-space>
              企业ID
              <a-popover>
                <template slot="content">
                  每个企业都拥有唯一的corpid，获取此信息可在管理后台“我的企业”－“企业信息”下查看“企业ID”（需要有管理员权限）
                </template>
                <a-icon type="info-circle" />
              </a-popover>
            </a-space>
          </template>
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
        <a-form-model-item label="企业微信通讯录全量同步到平台行政组织" prop="enabledSyncOrg">
          <a-switch v-model="formData.configuration.enabledSyncOrg" checkedChildren="开启" unCheckedChildren="关闭"></a-switch>
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
              <a-switch
                v-model="formData.configuration.orgSyncOption.user"
                disabled
                checkedChildren="开启"
                unCheckedChildren="关闭"
              ></a-switch>
            </a-descriptions-item>
          </a-descriptions>
          <span v-if="formData.configuration.enabledSyncOrg">
            从2022年6月20号20点开始，除通讯录同步以外的基础应用（如客户联系、微信客服、会话存档、日程等），以及新创建的自建应用与代开发应用，调用接口时，不再返回以下字段：头像、性别、手机、邮箱、企业邮箱、员工个人二维码、地址
          </span>
        </a-form-model-item>
        <a-form-model-item>
          <template slot="label">
            <a-space>
              通讯录回调通知
              <a-popover>
                <template slot="content">
                  在企业微信管理后台的“管理工具-通讯录同步-设置接收事件服务器”处， 进入配置页面，
                  <br />
                  要求填写通讯录同步助手的URL、Token、EncodingAESKey三个参数。
                  <br />
                  为保证企业数据安全，URL需要配置本企业主体的域名链接。
                </template>
                <a-icon type="info-circle" />
              </a-popover>
            </a-space>
          </template>
          <a-checkbox-group v-model="formData.configuration.orgSyncEvents" :options="orgSyncEventOptions" />
        </a-form-model-item>
        <a-form-model-item label="通讯录回调通知服务器URL">
          http{s}://平台公网服务域名:端口/api/weixin/callback/org/{{ _$SYSTEM_ID }}
        </a-form-model-item>
        <a-form-model-item label="通讯录回调通知Token">
          <a-input v-model="formData.configuration.orgSyncOption.eventToken"></a-input>
        </a-form-model-item>
        <a-form-model-item label="通讯录回调通知EncodingAESKey">
          <a-input v-model="formData.configuration.orgSyncOption.eventAesKey"></a-input>
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="3" tab="流程审批">
        <a-form-model-item label="消息推送" prop="enabledPushMsg">
          <a-switch v-model="formData.configuration.enabledPushMsg" checkedChildren="开启" unCheckedChildren="关闭"></a-switch>
        </a-form-model-item>
        <template v-if="formData.configuration.enabledPushMsg">
          <a-form-model-item prop="todoMsgTemplateType">
            <template slot="label">
              <a-space>
                待办消息内容模板
                <a-popover>
                  <template slot="content">
                    使用在线消息模板的标题及内容，增加内置变量_weixinMsg(标记为微信消息)、_workUrl(工作详情地址)
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
        </a-form-model-item>
        <template v-if="formData.configuration.groupChat.enabled">
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
        appSecret: '',
        corpId: '',
        enabled: false,
        configuration: {
          orgSyncOption: {},
          jobMode: 'first',
          groupChat: { scope: 'all' }
        }
      },
      rules: {
        corpId: [{ required: true, message: '不能为空！', trigger: 'blur' }],
        appId: [{ required: true, message: '不能为空！', trigger: 'blur' }],
        appSecret: [{ required: true, message: '不能为空！', trigger: 'blur' }]
      },
      orgOptions: [],
      orgSyncEventOptions: [
        { label: '新增成员事件', value: 'create_user' },
        { label: '更新成员事件', value: 'update_user' },
        { label: '删除成员事件', value: 'delete_user' },
        { label: '新增部门事件', value: 'create_party' },
        { label: '更新部门事件', value: 'update_party' },
        { label: '删除部门事件', value: 'delete_party' }
      ],
      messageTemplateOptions: []
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
        .get('/proxy/api/weixin/config/getByCurrentSystem')
        .then(({ data: result }) => {
          if (result.data) {
            let formData = result.data;
            if (formData.definitionJson) {
              formData.configuration = JSON.parse(formData.definitionJson);
            } else {
              formData.configuration = formData.configuration || {};
            }
            if (!formData.configuration.orgSyncOption) {
              formData.configuration.orgSyncOption = { dept: true, user: true };
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
            .post('/proxy/api/weixin/config/save', formData)
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
        .get(`/proxy/api/weixin/config/testCreateToken?corpId=${this.formData.corpId}&appSecret=${this.formData.appSecret}`)
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
                .post('/proxy/api/weixin/config/syncOrg', formData)
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
    }
  }
};
</script>
