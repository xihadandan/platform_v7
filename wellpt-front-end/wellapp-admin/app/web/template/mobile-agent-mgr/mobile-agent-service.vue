<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="formRules" ref="form" class="pt-form">
    <div class="mobile-agent-service-form">
      <a-tabs v-model="activeKey" type="card">
        <a-tab-pane key="1" tab="MAS设置(默认)">
          <a-form-model-item label="移动代理服务器IP" prop="imIp">
            <a-input v-model="form.imIp" allow-clear />
          </a-form-model-item>
          <a-form-model-item label="接口登录名" prop="loginName">
            <a-input v-model="form.loginName" allow-clear />
          </a-form-model-item>
          <a-form-model-item label="接口登录密码" prop="loginPassword">
            <a-input v-model="form.loginPassword" allow-clear />
          </a-form-model-item>
          <a-form-model-item label="接口编码" prop="apiCode">
            <a-input v-model="form.apiCode" allow-clear />
          </a-form-model-item>
          <a-form-model-item label="数据库名称" prop="dbName">
            <a-input v-model="form.dbName" allow-clear />
          </a-form-model-item>
          <a-form-model-item label="重发时限（/天）" prop="sendLimit">
            <a-input-number v-model="form.sendLimit" allow-clear :precision="0" :min="1" />
          </a-form-model-item>
          <a-form-model-item label="是否启用MAS">
            <a-checkbox v-model="form.isOpen" @change="e => isOpenChange(e, 'isOpen')">是</a-checkbox>
          </a-form-model-item>
        </a-tab-pane>
        <a-tab-pane key="2" tab="S-MAS设置">
          <a-form-model-item label="WebService地址" prop="sWebService">
            <a-textarea v-model="form.sWebService" allow-clear :auto-size="{ minRows: 2, maxRows: 6 }" />
          </a-form-model-item>
          <a-form-model-item label="接口登录名" prop="sLoginName">
            <a-input v-model="form.sLoginName" allow-clear />
          </a-form-model-item>
          <a-form-model-item label="接口登录密码" prop="sLoginPassword">
            <a-input v-model="form.sLoginPassword" allow-clear />
          </a-form-model-item>
          <a-form-model-item label="重发时限（/天）" prop="sSendLimit">
            <a-input-number v-model="form.sSendLimit" allow-clear :precision="0" :min="1" />
          </a-form-model-item>
          <a-form-model-item label="WebService实现类" prop="clientBeanName">
            <a-input v-model="form.clientBeanName" allow-clear />
          </a-form-model-item>
          <a-form-model-item label="是否启用MAS">
            <a-checkbox v-model="form.sIsOpen" @change="e => isOpenChange(e, 'sIsOpen')">是</a-checkbox>
          </a-form-model-item>
        </a-tab-pane>
        <a-tab-pane key="3" tab="云MAS设置">
          <a-form-model-item label="云MAS平台HTTP地址" prop="cloudMasHttp">
            <a-textarea v-model="form.cloudMasHttp" allow-clear :auto-size="{ minRows: 2, maxRows: 6 }" />
          </a-form-model-item>
          <a-form-model-item label="集团客户名称" prop="ecName">
            <a-input v-model="form.ecName" allow-clear />
          </a-form-model-item>
          <a-form-model-item label="用户名" prop="apId">
            <a-input v-model="form.apId" allow-clear />
          </a-form-model-item>
          <a-form-model-item label="密码" prop="secretKey">
            <a-input v-model="form.secretKey" allow-clear />
          </a-form-model-item>
          <a-form-model-item label="网关签名编码" prop="cloudSign">
            <a-input v-model="form.cloudSign" allow-clear />
          </a-form-model-item>
          <a-form-model-item label="扩展码" prop="addSerial">
            <a-input v-model="form.addSerial" allow-clear />
          </a-form-model-item>
          <a-form-model-item label="重发时限（/天）" prop="cloudSendLimit">
            <a-input-number v-model="form.cloudSendLimit" allow-clear :precision="0" :min="1" />
          </a-form-model-item>
          <a-form-model-item label="是否启用MAS">
            <a-checkbox v-model="form.cloudIsOpen" @change="e => isOpenChange(e, 'cloudIsOpen')">是</a-checkbox>
          </a-form-model-item>
        </a-tab-pane>
      </a-tabs>
      <a-form-model-item label="异步发送">
        <a-checkbox v-model="form.apiAsync"></a-checkbox>
      </a-form-model-item>
    </div>
    <div style="text-align: center; margin: 20px 0 10px 0">
      <a-button @click="saveForm" type="primary">保存</a-button>
    </div>
  </a-form-model>
</template>

<script type="text/babel">
import { generateId, getElSpacingForTarget } from '@framework/vue/utils/util';
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';
import { each, assignIn } from 'lodash';

export default {
  name: 'MobileAgentService',
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
      initformData: {},
      form: {},
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      rules: {},
      openParams: ['isOpen', 'sIsOpen', 'cloudIsOpen'],
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
      return rules;
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

    let $parent = this.$root.$el.classList.contains('preview') ? this.$root.$el : this.$el.closest('.widget-vpage');
    const { maxHeight, totalBottom, totalNextSibling } = getElSpacingForTarget(
      this.$el.querySelector('.mobile-agent-service-form'),
      $parent
    );
    this.$el.querySelector('.mobile-agent-service-form').style.cssText = `overflow-y:auto; height:${maxHeight}px`;
  },
  methods: {
    getPopupContainerByPs,
    getFormData() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'shortMessageService',
          methodName: 'getBean',
          args: '[]',
          argTypes: [],
          validate: false
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            _this.form = data.data;
            this.activeKey = _this.form.isOpen ? '1' : !_this.form.sIsOpen ? '1' : '2';
          }
        });
    },
    isOpenChange(e, param) {
      if (e.target.checked) {
        each(this.openParams, item => {
          if (item != param) {
            this.$set(this.form, item, false);
          }
        });
      }
    },
    saveForm() {
      console.log('保存表单');
      this.$refs.form.validate(valid => {
        if (valid) {
          this.saveFormData(this.form);
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    saveFormData(bean) {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'shortMessageService',
          methodName: 'saveMas',
          args: JSON.stringify([bean]),
          validate: false
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data == 'success') {
            _this.$confirm({
              title: '保存设置成功，是否立即应用生效？',
              onOk() {
                _this.applySetting(bean);
              },
              onCancel() {}
            });
          } else {
            this.$message.error(data.msg || '保存失败');
          }
        })
        .catch(err => {
          let data = err && err.response && err.response.data;
          this.$message.error(data.msg || '保存失败');
        });
    },
    applySetting(bean) {
      $axios
        .post('/json/data/services', {
          serviceName: 'shortMessageService',
          methodName: 'applyConfig',
          args: JSON.stringify([bean]),
          validate: false
        })
        .then(({ data }) => {
          if (data.data == true || data.data == 'true') {
            this.$message.success('应用成功！');
          } else {
            this.$message.error(data.msg || '应用失败，保存不成功');
          }
        });
    }
  }
};
</script>
