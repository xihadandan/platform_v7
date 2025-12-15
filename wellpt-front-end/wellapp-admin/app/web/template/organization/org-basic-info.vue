<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
    <Scroll style="height: 400px; padding-right: 10px">
      <div v-if="!editable" style="position: absolute; width: 100%; height: 600px; z-index: 1"></div>
      <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form" :colon="false" class="pt-form">
        <a-form-model-item label="组织名称" prop="name">
          <a-input v-model="form.name">
            <template slot="addonAfter">
              <WI18nInput :target="form" code="name" v-model="form.name" />
            </template>
          </a-input>
        </a-form-model-item>
        <a-form-model-item label="ID" v-if="form.uuid">
          <span>{{ form.id }}</span>
        </a-form-model-item>
        <a-form-model-item label="编号">
          <a-input v-model="form.code" />
        </a-form-model-item>
        <a-form-model-item label="描述">
          <a-textarea v-model="form.remark" allowClear :maxLength="100" />
        </a-form-model-item>
        <a-form-model-item label="有效期">
          <a-radio-group @change="onChangeLimitType" button-style="solid" v-model="limitType">
            <a-radio-button value="0">长期</a-radio-button>
            <a-radio-button value="1">指定期限</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="有效期限" v-show="!form.neverExpire">
          <a-date-picker :disabled-date="disabledDate" v-model="form.expireTime" />
        </a-form-model-item>
        <a-form-model-item label="创建人" v-if="form.uuid">
          <span><UserDisplay :userId="form.creator" v-if="form.creator != undefined" /></span>
        </a-form-model-item>
        <a-form-model-item label="创建时间" v-if="form.uuid">
          <span>{{ form.createTime }}</span>
        </a-form-model-item>

        <a-form-model-item label="组织管理员">
          <OrgSelect
            title="选择"
            ref="orgSelect"
            v-model="form.manager"
            :checkableTypes="['user']"
            orgType="MyOrg"
            :showBizOrgUnderOrg="false"
          />
        </a-form-model-item>

        <a-form-model-item label="版本发布流程" v-if="orgSettingFetched && orgSetting['ORG_VERSION_AUDIT_FLOW_ENABLE'].enable">
          <a-select allowClear style="width: 100%" v-model="form.publishFlowUuid" :options="flowDefOptions" :filter-option="filterOption" />
        </a-form-model-item>
      </a-form-model>
    </Scroll>
  </a-skeleton>
</template>

<script type="text/babel">
import moment from 'moment';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'OrgBasicInfo',
  props: {
    uuid: String,
    displayState: {
      type: String,
      default: 'edit' // edit、label
    }
  },
  components: { OrgSelect, WI18nInput },
  inject: ['pageContext', 'vPageState', '$event', 'namespace'],
  data() {
    let uuid = this.uuid;
    if (!uuid) {
      uuid = this.vPageState && this.vPageState.orgUuid ? this.vPageState.orgUuid : undefined;
      if (!uuid) {
        uuid = this.$event && this.$event.meta && this.$event.meta.uuid ? this.$event.meta.uuid : undefined;
      }
    }

    return {
      loading: uuid != undefined,
      uuid,
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      form: { neverExpire: true },
      limitType: '0',
      rules: {
        name: { required: true, message: '名称必填', trigger: ['blur', 'change'] }
      },
      orgSettingFetched: false,
      orgSetting: {},
      flowDefOptions: []
    };
  },
  computed: {
    editable() {
      return this.displayState == 'edit';
    }
  },
  watch: {},
  beforeCreate() {},
  created() {},
  beforeMount() {
    let _this = this;
    if (this.uuid) {
      this.getOrgDetails(() => {});
    }
    this.fetchWorkflowOptions();
    this.getSetting();
  },
  mounted() {},
  methods: {
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    moment,

    disabledDate(current) {
      return current && current < moment().add(-1, 'd').endOf('day');
    },

    onChangeLimitType(e) {
      this.form.neverExpire = e.target.value == 0;
    },
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    fetchWorkflowOptions() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'flowDefineService',
          methodName: 'query',
          args: JSON.stringify([{}])
        })
        .then(({ data }) => {
          let options = [];
          let ids = [];
          if (data.code == 0 && data.data) {
            for (let i = 0, len = data.data.length; i < len; i++) {
              if (!ids.includes(data.data[i].id)) {
                options.push({
                  label: data.data[i].name,
                  value: data.data[i].id
                });
                ids.push(data.data[i].id);
              }
            }
          }
          _this.flowDefOptions = options;
        });
    },
    getOrgDetails() {
      let _this = this;
      $axios.get(`/proxy/api/org/organization/details/${this.uuid}`, {}).then(({ data }) => {
        _this.loading = false;
        if (data.code == 0 && data.data) {
          _this.form = data.data;
          _this.limitType = _this.form.neverExpire ? '0' : '1';
          if (_this.form.expireTime) {
            _this.form.expireTime = moment(_this.form.expireTime, 'yyyy-MM-DD HH:mm:ss');
          }

          if (data.data.i18ns) {
            let i18n = {};
            for (let item of data.data.i18ns) {
              if (i18n[item.locale] == undefined) {
                i18n[item.locale] = {};
              }
              i18n[item.locale][item.dataCode] = item.content;
            }
            _this.$set(_this.form, 'i18n', i18n);
          }
        }
      });
    },
    getSetting() {
      $axios.get('/proxy/api/org/elementModel/queryOrgSetting', { params: { system: null } }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          for (let i = 0, len = data.data.length; i < len; i++) {
            this.orgSetting[data.data[i].attrKey] = data.data[i];
            this.orgSetting[data.data[i].attrKey].attrValueJson = data.data[i].attrVal ? JSON.parse(data.data[i].attrVal) : {};
          }
          console.log('组织参数设置: ', this.orgSetting);
          this.orgSettingFetched = true;
        }
      });
    },
    save(event) {
      let _this = this;
      if (!_this.editable) {
        return Promise.resolve(true);
      }

      return new Promise((resolve, reject) => {
        this.$refs.form.validate(valid => {
          if (valid) {
            let formData = { ..._this.form };
            if (formData.uuid == undefined) {
              formData.system = _this._$SYSTEM_ID; // 绑定组织到指定系统
            }
            if (formData.neverExpire) {
              formData.expireTime = null;
            }
            if (formData.expireTime) {
              formData.expireTime = formData.expireTime.endOf('d').toDate().getTime();
            }
            if (_this.form.i18n) {
              let i18ns = [];
              for (let locale in _this.form.i18n) {
                for (let key in _this.form.i18n[locale]) {
                  if (_this.form.i18n[locale][key]) {
                    i18ns.push({
                      locale: locale,
                      content: _this.form.i18n[locale][key],
                      dataCode: key
                    });
                  }
                }
              }
              formData.i18ns = i18ns;
            }
            $axios
              .post('/proxy/api/org/organization/save', formData)
              .then(({ data }) => {
                if (data.code == 0) {
                  _this.$message.success('保存成功');
                  resolve();
                  // 组织列表刷新表格
                  if (event) {
                    event.$evtWidget.closeModal(); // 关闭弹窗
                    _this.pageContext.emitEvent('ssYabJTWqkpnKAERsjLOhFcARZHDxNzd:refetch');
                  }
                }
              })
              .catch(error => {
                reject();
                // $widget.$message.error('服务异常');
              });
          }
        });
      });
    }
  }
};
</script>
