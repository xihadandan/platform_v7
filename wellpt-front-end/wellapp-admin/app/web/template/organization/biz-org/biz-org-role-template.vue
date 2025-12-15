<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form" :colon="false">
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
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'BizOrgRoleTemplate',
  inject: ['$event', 'currentWindow', 'pageContext'],
  props: {},
  components: { WI18nInput },
  computed: {},
  data() {
    let form = {};
    if (this.$event && this.$event.meta) {
      form = JSON.parse(JSON.stringify(this.$event.meta));
    }
    return {
      form,
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      rules: {
        name: [{ required: true, message: '必填' }],
        applyTo: [{ required: true, message: '必填' }]
      }
    };
  },
  beforeCreate() {},
  created() {
    if (this.form.uuid == undefined) {
      this.rules.id = [{ required: true, message: '必填' }];
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    save() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.pageContext.emitEvent('CRJllZWayEsxvjxpKHEqcGgHZMDJeYnq:qYbteKtBpfHYopYgIXAVSkRTETBlvCgc', this.form);
          // 关闭弹窗
          this.pageContext.emitEvent('esnKUBEDnpuuOZTJnLMUPtBhTnfzlhgP:closeModal');
        }
      });
    }
  },
  META: {
    method: {
      save: '保存'
    }
  }
};
</script>
