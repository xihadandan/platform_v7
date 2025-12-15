<template>
  <a-form-model
    ref="form"
    :model="formData"
    :rules="rules"
    class="feishu-settings pt-form"
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
                <template slot="content">应用唯一的ID标识</template>
                <a-icon type="info-circle" />
              </a-popover>
            </a-space>
          </template>
          <a-input v-model="formData.appId"></a-input>
        </a-form-model-item>
        <a-form-model-item prop="appSecret">
          <template slot="label">
            <a-space>
              App Secret
              <a-popover>
                <template slot="content">应用的密钥，在飞书创建应用时生成，可用于获取app_access_token</template>
                <a-icon type="info-circle" />
              </a-popover>
            </a-space>
          </template>
          <a-input v-model="formData.appSecret"></a-input>
        </a-form-model-item>
        <a-form-model-item label="服务URL" prop="serviceUri">
          <a-input v-model="formData.serviceUri" placeholder="https://open.feishu.cn"></a-input>
        </a-form-model-item>
        <a-form-model-item label="企业回调域名" prop="redirectUri">
          <a-input v-model="formData.redirectUri"></a-input>
        </a-form-model-item>
        <a-form-model-item label="移动端服务URL" prop="mobileAppUri">
          <a-input v-model="formData.mobileAppUri"></a-input>
        </a-form-model-item>
        <a-form-model-item label="是否启用" prop="enabled">
          <a-switch v-model="formData.enabled" checkedChildren="开启" unCheckedChildren="关闭"></a-switch>
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="2" tab="组织同步">
        <a-form-model-item label="飞书通讯录全量同步到平台行政组织" prop="enabledSyncOrg">
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
                <a-col span="4">性别</a-col>
                <a-col span="12">作为 OA性别</a-col>
                <a-col span="8">
                  <a-switch
                    v-model="formData.configuration.orgSyncOption.userGender"
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
          </a-descriptions>
        </a-form-model-item>
        <a-form-model-item label="监听事件同步">
          <a-checkbox-group v-model="formData.configuration.orgSyncEvents" :options="orgSyncEventOptions" />
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
                    使用在线消息模板的标题及内容，增加内置变量_feishuMsg(标记为飞书消息)、_workUrl(工作详情地址)
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
          <a-form-model-item label="事项群聊结束时">
            <a-radio-group size="small" v-model="formData.configuration.groupChat.saveContentMode">
              <a-radio-button value="none">群聊记录不带入流程</a-radio-button>
              <a-radio-button value="text">群聊记录带入流程(不包含附件)</a-radio-button>
              <a-radio-button value="textAndFile">群聊记录带入流程(包含附件)</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="事项群聊解散">
            <a-radio-group size="small" v-model="formData.configuration.groupChat.deleteMode">
              <a-radio-button value="taskEnd">群聊结束立即解散</a-radio-button>
              <a-radio-button value="flowEnd">流程结束时解散</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="事项群聊自动提交">
            <a-radio-group size="small" v-model="formData.configuration.groupChat.autoSubmitMode">
              <a-radio-button value="none">不自动提交</a-radio-button>
              <a-radio-button value="@_all">@所有人时自动提交</a-radio-button>
            </a-radio-group>
            <p />
            <p />
          </a-form-model-item>
        </template>
      </a-tab-pane>
    </a-tabs>
    <div style="text-align: center; margin: 10px 0 20px">
      <a-button type="primary" @click="saveConfig">保存</a-button>
      <a-button v-if="activeKey == '1'" @click="testCreateToken">生成测试访问凭证</a-button>
      <a-button v-if="activeKey == '2' && formData.configuration.enabledSyncOrg" @click="syncOrg">全量同步</a-button>
    </div>
  </a-form-model>
</template>

<script>
import { filterSelectOption } from '@framework/vue/utils/function';
import { getElSpacingForTarget } from '@framework/vue/utils/util';
import FlowSelect from '@workflow/app/web/lib/flow-select.vue';
export default {
  components: { FlowSelect },
  inject: ['designMode'],
  data() {
    return {
      activeKey: '1',
      formData: {
        appId: '',
        appSecret: '',
        serviceUri: '',
        redirectUri: '',
        enabled: false,
        configuration: {
          orgSyncOption: { dept: true, userName: true, userMobile: true },
          orgSyncEvents: [],
          todoMsgTemplateType: 'todo',
          groupChat: { scope: 'all' }
        }
      },
      rules: {
        appId: [{ required: true, message: '不能为空！', trigger: 'blur' }],
        appSecret: [{ required: true, message: '不能为空！', trigger: 'blur' }]
      },
      orgOptions: [],
      orgSyncEventOptions: [
        { label: '员工入职', value: 'contact.user.created_v3' },
        { label: '员工信息被修改', value: 'contact.user.updated_v3' },
        { label: '员工离职', value: 'contact.user.deleted_v3' },
        { label: '部门新建', value: 'contact.department.created_v3' },
        { label: '部门信息变化', value: 'contact.department.updated_v3' },
        { label: '部门被删除', value: 'contact.department.deleted_v3' }
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
        .get('/proxy/api/feishu/config/query')
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
          $axios
            .post('/proxy/api/feishu/config/save', formData)
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
          `/proxy/api/feishu/config/testCreateToken?appId=${this.formData.appId}&appSecret=${this.formData.appSecret}&baseUrl=${
            this.formData.serviceUri || ''
          }`
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
          _this.$confirm({
            title: '确认',
            content: `存在组织元素时，组织全量同步会生成新的组织版本，确认全量同步组织？`,
            onOk() {
              _this.$loading('同步中...');
              $axios
                .post('/proxy/api/feishu/config/syncOrg', formData)
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
