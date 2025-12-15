<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="formRules" ref="form" class="pt-form">
    <a-tabs v-model="activeKey" type="card">
      <a-tab-pane key="1" tab="基本信息">
        <a-form-model-item label="邮件服务" prop="mailServerType">
          <a-select v-model="form.mailServerType" allow-clear>
            <a-select-option v-for="d in mailServerTypeOptions" :key="d.id">
              {{ d.text }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="邮件域名" prop="domain">
          <a-input v-model="form.domain" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="接收邮件服务器(POP3)" prop="pop3Server">
          <a-input v-model="form.pop3Server" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="POP3端口" prop="pop3Port">
          <a-input v-model="form.pop3Port" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="发送邮件服务器(SMTP)" prop="smtpServer">
          <a-input v-model="form.smtpServer" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="SMTP端口" prop="smtpPort">
          <a-input v-model="form.smtpPort" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="发送邮件服务器(IMAP)" prop="imapServer">
          <a-input v-model="form.imapServer" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="IMAP端口" prop="imapPort">
          <a-input v-model="form.imapPort" allow-clear />
        </a-form-model-item>
        <a-form-model-item>
          <template slot="label">
            <a-tooltip overlayClassName="widget-tooltip-overlay">
              <template #title>如果您希望邮件服务定位为公网邮箱，支持对外收发邮件，请选中！</template>
              是否公网邮箱
              <span class="widget-tooltip"><a-icon type="info-circle" /></span>
            </a-tooltip>
          </template>
          <a-checkbox v-model="form.isPublicEmail" @change="isPublicEmailChange"></a-checkbox>
        </a-form-model-item>
        <a-form-model-item label="API端口" v-if="form.mailServerType == 'CoreMail'" prop="apiPort">
          <a-input v-model="form.apiPort" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="在服务器保留备份" v-if="form.mailServerType == 'JamesMail'">
          <a-checkbox v-model="form.keepOnServer"></a-checkbox>
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="2" tab="限额设置">
        <a-form-model-item label="邮箱限额">
          <a-checkbox v-model="mailCapacityLimit">启用</a-checkbox>
        </a-form-model-item>
        <template v-if="mailCapacityLimit">
          <a-form-model-item label="默认限额(MB)">
            <a-input-number v-model="form.defaultCapacity" :precision="0" allow-clear :min="1" style="width: 320px" />
          </a-form-model-item>
          <a-form-model-item label="容量提醒">
            邮箱已使用空间达到
            <a-input-number
              v-model="form.deadlineCapacity"
              :precision="0"
              allow-clear
              :min="1"
              :max="100"
              style="width: 90px; margin: 0 3px"
            />
            %时，提醒用户。(未设置时不提醒)
          </a-form-model-item>
        </template>
      </a-tab-pane>
      <a-tab-pane key="3" tab="其他">
        <a-form-model-item label="收件组织选择">
          <a-select v-model="allowOrgOptionsVal" allow-clear mode="multiple">
            <a-select-option v-for="d in allowOrgOptions" :key="d.id">
              {{ d.text }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="允许的附件大小（M）">
          <a-input v-model="form.attachmentSizeLimit" allow-clear placeholder="为空时，表示不限制大小" />
        </a-form-model-item>
        <a-form-model-item>
          <template slot="label">
            <a-tooltip overlayClassName="widget-tooltip-overlay">
              <template #title>发送人请求阅读回执时，收件人查看后自动发送阅读回执</template>
              阅读回执
              <span class="widget-tooltip"><a-icon type="info-circle" /></span>
            </a-tooltip>
          </template>
          <a-checkbox v-model="form.sendReceipt">自动发送阅读回执</a-checkbox>
        </a-form-model-item>
        <a-form-model-item label="操作“收邮件”后的页面">
          <a-radio-group v-model="form.receiveMailAction">
            <a-radio :value="0" name="receiveMailAction">显示“收件箱”</a-radio>
            <a-radio :value="1" name="receiveMailAction">维持不变</a-radio>
          </a-radio-group>
        </a-form-model-item>
      </a-tab-pane>
    </a-tabs>
    <div style="text-align: center; margin: 20px 0 10px 0">
      <a-button @click="resetForm" type="primary" v-if="resetShow">重置用户邮箱账号</a-button>
      <a-button @click="saveForm" type="primary">保存</a-button>
      <a-button @click="delForm" type="danger" v-if="delShow">删除</a-button>
    </div>
  </a-form-model>
</template>

<script type="text/babel">
import { generateId, getElSpacingForTarget, deepClone } from '@framework/vue/utils/util';
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';
import { assignIn } from 'lodash';

export default {
  name: 'WebMailSet',
  props: {
    uuid: String,
    displayState: {
      type: String,
      default: 'edit' // edit、label
    }
  },
  components: {},
  inject: ['pageContext', 'vPageState', '$event'],
  data() {
    return {
      $evtWidget: undefined,
      $dialogWidget: undefined,
      activeKey: '1',
      initformData: {
        uuid: null, // UUID
        recVer: null, // 版本号
        domain: null, // 域名
        pop3Server: null, // POP3服务器
        pop3Port: null, // POP3服务器端口110
        smtpServer: null, // 发送服务器
        smtpPort: null, // 发送服务器端口25
        imapServer: null, // IMAP服务器
        imapPort: null, // IMAP服务器端口143
        keepOnServer: null, // 在服务器保留备份
        defaultCapacity: null,
        deadlineCapacity: null,
        mailServerType: null,
        apiPort: null,
        attachmentSizeLimit: null,
        isPublicEmail: null, // 是否公网邮箱
        sendReceipt: null // 是否自动发送回执 0：否，1：是
      },
      form: {},
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      rules: {
        mailServerType: { required: true, message: '邮件服务必填', trigger: ['blur', 'change'] },
        domain: { required: true, message: '邮件域名必填', trigger: ['blur', 'change'] },
        pop3Server: { required: true, message: '接收邮件服务器(POP3)必填', trigger: ['blur', 'change'] },
        pop3Port: { required: true, message: 'POP3端口必填', trigger: ['blur', 'change'] },
        smtpServer: { required: true, message: '发送邮件服务器(SMTP)必填', trigger: ['blur', 'change'] },
        smtpPort: { required: true, message: 'SMTP端口必填', trigger: ['blur', 'change'] },
        imapServer: { required: true, message: '发送邮件服务器(IMAP)必填', trigger: ['blur', 'change'] },
        imapPort: { required: true, message: 'IMAP端口必填', trigger: ['blur', 'change'] }
      },
      mailServerTypeOptions: [
        {
          id: 'JamesMail',
          text: 'JamesMail'
        },
        {
          id: 'CoreMail',
          text: 'CoreMail'
        }
      ],
      allowOrgOptionsVal: [],
      allowOrgOptions: [],
      mailCapacityLimit: false,
      wTemplate: {
        $options: {
          methods: {
            saveForm: this.saveForm
          },
          META: {
            method: {
              saveForm: '保存表单'
            }
          }
        }
      }
    };
  },
  META: {
    method: {
      saveForm: '保存表单'
    }
  },
  computed: {
    formRules() {
      let rules = assignIn({}, this.rules);
      if (this.form.mailServerType == 'CoreMail') {
        rules = assignIn(
          {
            apiPort: { required: true, message: 'API端口必填', trigger: ['blur', 'change'] }
          },
          rules
        );
      }
      return rules;
    },
    resetShow() {
      return !!this.form.uuid;
    },
    delShow() {
      return !!this.form.uuid;
    }
  },
  watch: {},
  beforeCreate() {},
  created() {},
  beforeMount() {
    let _this = this;
  },
  mounted() {
    let $event = this._provided.$event;
    this.$evtWidget = $event && $event.$evtWidget;
    this.$dialogWidget = this._provided && this._provided.dialogContext;
    this.getFormData();
    this.getAllowOrgOptions();

    let $parent = this.$root.$el.classList.contains('preview') ? this.$root.$el : this.$el.closest('.widget-vpage');
    const { maxHeight, totalBottom, totalNextSibling } = getElSpacingForTarget(this.$el.querySelector('.ant-tabs-content'), $parent);
    this.$el.querySelector('.ant-tabs-content').style.cssText = `overflow-y:auto; height:${maxHeight}px`;
  },
  methods: {
    getPopupContainerByPs,
    initFormDataFun() {
      this.form = deepClone(this.initformData);
      this.mailCapacityLimit = false;
      this.allowOrgOptionsVal = [];
    },
    getFormData() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'wmMailConfigMgr',
          methodName: this.form.uuid ? 'getBean' : 'getConfig',
          args: this.form.uuid ? JSON.stringify([this.form.uuid]) : '[]',
          argTypes: [],
          validate: false
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            _this.form = data.data;
            if (_this.form.defaultCapacity != null) {
              _this.mailCapacityLimit = true;
            }
            this.allowOrgOptionsVal = this.form.allowOrgOptions ? this.form.allowOrgOptions.split(';') : [];
          }
        });
    },
    getAllowOrgOptions() {
      let _this = this;
      _this.$axios
        .post('/common/select2/query', {
          serviceName: 'multiOrgOptionService',
          queryMethod: 'loadSelectDataNoId',
          params: {
            excludeId: 'Role'
          },
          searchValue: '',
          pageSize: 1000,
          pageNo: 1
        })
        .then(({ data }) => {
          if (data.results) {
            _this.allowOrgOptions = data.results;
          }
        });
    },
    isPublicEmailChange(checked) {
      if (checked && !this.form.attachmentSizeLimit) {
        this.form.attachmentSizeLimit = '50';
      }
    },
    saveForm(callback) {
      console.log('保存表单');
      this.$refs.form.validate(valid => {
        if (valid) {
          this.beforeSaveReq(callback);
        } else {
          this.activeKey = '1';
          console.log('error submit!!');
          return false;
        }
      });
    },
    beforeSaveReq(callback) {
      let bean = JSON.parse(JSON.stringify(this.form));
      if (!this.mailCapacityLimit) {
        bean.deadlineCapacity = '';
        bean.defaultCapacity = '';
      }
      bean.allowOrgOptions = this.allowOrgOptionsVal.join(';');
      if (typeof callback == 'function') {
        callback(bean);
      } else {
        this.saveFormData(bean);
      }
    },
    saveFormData(bean, callback) {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'wmMailConfigMgr',
          methodName: 'saveBean',
          args: JSON.stringify([bean]),
          validate: false
        })
        .then(({ data }) => {
          if (data.code == 0) {
            if (typeof callback == 'function') {
              callback();
            } else {
              this.$message.success('保存成功');
            }
          } else {
            this.$message.error(data.msg || '保存失败');
          }
        })
        .catch(err => {
          let data = err && err.response && err.response.data;
          this.$message.error(data.msg || '保存失败');
        });
    },
    resetForm() {
      let _this = this;
      this.saveForm(bean => {
        _this.$confirm({
          title: '确定要重置所有用户的邮箱客户端密码为初始化密码吗？',
          onOk() {
            _this.saveFormData(bean, () => {
              _this.resetReq();
            });
          },
          onCancel() {}
        });
      });
    },
    resetReq() {
      $axios
        .post('/json/data/services', {
          serviceName: 'wmMailConfigMgr',
          methodName: 'resetMailUser',
          args: JSON.stringify([this.form.uuid]),
          argTypes: [],
          validate: false
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            this.$message.success('重置成功');
          }
        });
    },
    delForm() {
      let _this = this;
      this.$confirm({
        title: '确定要删除该配置吗？',
        onOk() {
          $axios
            .post('/json/data/services', {
              serviceName: 'wmMailConfigMgr',
              methodName: 'remove',
              args: JSON.stringify([_this.form.uuid]),
              argTypes: [],
              validate: false
            })
            .then(({ data }) => {
              if (data.code == 0) {
                _this.$message.success('删除成功');
                _this.initFormDataFun();
              }
            });
        },
        onCancel() {}
      });
    }
  }
};
</script>
