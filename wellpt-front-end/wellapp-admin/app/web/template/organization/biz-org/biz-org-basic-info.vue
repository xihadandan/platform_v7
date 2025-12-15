<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form" class="pt-form" :colon="false">
    <a-form-model-item label="业务组织名称" prop="name">
      <a-input v-model="form.name" :maxLength="120">
        <template slot="addonAfter">
          <WI18nInput :target="form" code="name" v-model="form.name" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="关联组织" prop="orgUuid" v-if="showOrgSelect">
      <a-button size="small" type="link" @click="jumpToOrgDesign" v-if="form.orgName">{{ form.orgName }}</a-button>
      <a-select
        :filter-option="filterSelectOption"
        show-search
        :options="orgOptions"
        v-else-if="form.uuid == undefined"
        v-model="form.orgUuid"
        placeholder="请选择关联组织"
        style="width: 100%"
      />
    </a-form-model-item>
    <a-form-model-item label="有效期" prop="neverExpire">
      <a-radio-group v-model="form.neverExpire" button-style="solid" size="small">
        <a-radio-button :value="true">长期</a-radio-button>
        <a-radio-button :value="false">指定时间失效</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="失效时间" prop="expireTime" v-show="!form.neverExpire">
      <a-date-picker :disabled-date="disabledDate" v-model="form.expireTime" />
    </a-form-model-item>
    <a-form-model-item label="描述" prop="remark"><a-textarea v-model="form.remark" :maxLength="300" /></a-form-model-item>
    <a-form-model-item label="状态">
      <a-switch v-model="form.enable" checked-children="启用" un-checked-children="停用" />
    </a-form-model-item>
    <template v-if="uuid">
      <a-form-model-item label="创建人">
        <UserDisplay :userId="form.creator" v-if="!loading" />
      </a-form-model-item>
      <a-form-model-item label="创建时间">{{ form.createTime }}</a-form-model-item>
    </template>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import moment from 'moment';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';
import { filterSelectOption } from '@framework/vue/utils/function.js';

export default {
  name: 'BizOrgBasicInfo',
  inject: ['$event', 'currentWindow'],
  props: {
    uuid: String
  },
  components: { WI18nInput },
  computed: {},
  data() {
    let form = {
        neverExpire: true
      },
      showOrgSelect = true,
      rules = {
        name: [{ required: true, message: '请输入业务组织名称', trigger: 'blur' }]
      };
    if (this.$event) {
      if (this.$event.eventParams['formData.orgUuid']) {
        form.orgUuid = this.$event.eventParams['formData.orgUuid'];
        showOrgSelect = false;
      }

      if (this.$event.eventParams['formData.uuid']) {
        this.uuid = this.$event.eventParams['formData.uuid'];
      }
    }
    return {
      form,
      showOrgSelect,
      orgOptions: [],
      loading: true,
      labelCol: { span: 6 },
      wrapperCol: { span: 17 },
      rules
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchBizOrg(this.uuid);
    if (this.form.orgUuid == undefined && this.showOrgSelect) {
      this.fetchOrgOptions();
      this.rules.orgUuid = [{ required: true, message: '请选择关联组织', trigger: 'blur' }];
    }
  },
  mounted() {},
  methods: {
    filterSelectOption,
    fetchOrgOptions() {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/org/organization/queryEnableOrgs`, { params: { system: this._$SYSTEM_ID } })
          .then(({ data }) => {
            if (data.data) {
              for (let d of data.data) {
                this.orgOptions.push({
                  label: d.name,
                  value: d.uuid
                });
              }
            }
          })
          .catch(error => {});
      });
    },
    moment,
    jumpToOrgDesign() {
      window.open(`/org/manager?uuid=${this.form.orgUuid}`, '_blank');
    },
    disabledDate(current) {
      return current && current < moment().add(-1, 'd').endOf('day');
    },
    save() {
      return new Promise((resolve, reject) => {
        let _this = this;
        this.$refs.form.validate(valid => {
          if (valid) {
            let formData = JSON.parse(JSON.stringify(this.form));
            if (formData.neverExpire) {
              formData.expireTime = null;
            }
            if (this.form.expireTime) {
              formData.expireTime = this.form.expireTime.endOf('d').toDate().getTime();
            }

            if (this.form.i18n) {
              let i18ns = [];
              for (let locale in this.form.i18n) {
                for (let key in this.form.i18n[locale]) {
                  if (this.form.i18n[locale][key]) {
                    i18ns.push({
                      locale: locale,
                      content: this.form.i18n[locale][key],
                      dataCode: key
                    });
                  }
                }
              }
              formData.i18ns = i18ns;
            }

            _this.$loading('保存中');
            _this.$axios
              .post(`/proxy/api/org/biz/saveBizOrg`, formData)
              .then(({ data }) => {
                _this.$loading(false);
                if (data.code == 0) {
                  _this.$message.success('保存成功');
                  if (_this.$event && _this.$event.$evtWidget) {
                    _this.$event.$evtWidget.refetch();
                    _this.currentWindow.close();
                  }
                  resolve(formData);
                }
              })
              .catch(error => {
                _this.$loading(false);
              });
          }
        });
      });
    },
    fetchBizOrg(uuid) {
      this.$axios
        .get(`/proxy/api/org/biz/getBizOrgByUuid`, { params: { uuid } })
        .then(({ data }) => {
          this.loading = false;
          if (data.code == 0) {
            let keys = [
              'uuid',
              'id',
              'name',
              'createTime',
              'creator',
              'orgUuid',
              'neverExpire',
              'expireTime',
              'remark',
              'enable',
              'orgName'
            ];
            for (let key of keys) {
              this.$set(this.form, key, data.data[key]);
            }
            if (this.form.expireTime) {
              this.form.expireTime = moment(this.form.expireTime, 'yyyy-MM-DD HH:mm:ss');
            }

            if (data.data.i18ns) {
              let i18n = {};
              for (let item of data.data.i18ns) {
                if (i18n[item.locale] == undefined) {
                  i18n[item.locale] = {};
                }
                i18n[item.locale][item.dataCode] = item.content;
              }
              this.$set(this.form, 'i18n', i18n);
            }
          }
        })
        .catch(error => {});
    }
  },
  META: {
    method: {
      save: '保存'
    }
  }
};
</script>
