<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form" :colon="false" class="pt-form">
    <a-form-model-item label="名称" prop="name" :maxLength="120">
      <a-input v-model="form.name">
        <template slot="addonAfter">
          <WI18nInput :target="form" code="name" v-model="form.name" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="ID" prop="id">
      <a-input v-model="form.id" :maxLength="64" v-if="!form.uuid" />
      <template v-else>
        {{ form.id }}
      </template>
    </a-form-model-item>
    <a-form-model-item label="应用于" prop="applyTo">
      <a-select v-model="form.applyTo" mode="multiple">
        <a-select-option value="BIZ_DIMENSION_ELEMENT">业务维度节点</a-select-option>
        <a-select-option value="ORG_ELEMENT">业务组织节点</a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item label="描述" prop="remark" :maxLength="300">
      <a-textarea v-model="form.remark" :rows="4" />
    </a-form-model-item>
    <!-- <a-form-model-item label="成员可选类型" prop="allowMemberType">
      <a-select v-model="form.allowMemberType" mode="multiple">
        <a-select-option value="user">人员</a-select-option>
        <a-select-option :value="opt.id" v-for="(opt, i) in orgElementModels" :key="'so_' + i">{{ opt.name }}</a-select-option>
      </a-select>
    </a-form-model-item> -->
    <!-- <a-form-model-item label="成员单 / 多选" prop="multipleSelectMember">
      <a-radio-group v-model="form.multipleSelectMember" button-style="solid" size="small">
        <a-radio-button :value="false">单选</a-radio-button>
        <a-radio-button :value="true">多选</a-radio-button>
      </a-radio-group>
    </a-form-model-item> -->
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import { debounce } from 'lodash';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'BizOrgRoleInfo',
  props: {
    roleInfo: Object,
    orgElementModels: Array,
    roleList: Array,
    bizOrgUuid: String
  },
  components: { WI18nInput },
  computed: {},
  data() {
    let form = JSON.parse(JSON.stringify(this.roleInfo));
    if (form.multipleSelectMember == undefined) {
      form.multipleSelectMember = false;
    }
    if (this.roleInfo.i18ns && this.roleInfo.i18ns.length > 0) {
      let i18n = {};
      for (let i of this.roleInfo.i18ns) {
        if (i18n[i.locale] == undefined) {
          i18n[i.locale] = {};
        }
        i18n[i.locale][i.dataCode] = i.content;
      }
      form.i18n = i18n;
    }

    return {
      form,
      labelCol: { span: 6 },
      wrapperCol: { span: 18 },
      rules: {
        name: [{ required: true, message: '必填' }],
        applyTo: [{ required: true, message: '必填' }]
      }
    };
  },
  beforeCreate() {},
  created() {
    if (this.form.uuid == undefined) {
      this.rules.id = [
        { required: true, message: '必填' },
        {
          trigger: ['blur', 'change'],
          type: 'string',
          validator: debounce(this.roleIdValidator, 300)
        }
      ];
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    roleIdValidator(rule, value, callback) {
      if (this.roleList.length > 0) {
        for (let r of this.roleList) {
          if (r != this.roleInfo && r.id == this.form.id) {
            callback(new Error('角色ID在列表内重复'));
            return;
          }
        }
      }

      this.$axios
        .get(`/proxy/api/org/biz/existBizOrgRoleIdUnderBizOrg`, { params: { id: this.form.id, bizOrgUuid: this.bizOrgUuid } })
        .then(({ data }) => {
          callback(data.data ? new Error('ID已存在') : undefined);
        })
        .catch(error => {
          callback(new Error('服务异常'));
        });
    },
    collectFormData() {
      let _this = this;
      return new Promise((resolve, reject) => {
        this.$refs.form.validate(function (valid, messages) {
          if (valid) {
            resolve(JSON.parse(JSON.stringify(_this.form)));
          }
        });
      });
    }
  }
};
</script>
