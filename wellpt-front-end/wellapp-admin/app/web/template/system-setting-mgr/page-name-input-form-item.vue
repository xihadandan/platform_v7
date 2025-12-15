<template>
  <a-form-model-item
    label="名称"
    prop="NAME"
    :rules="[{ required: true, type: 'string', message: `名称必填`, trigger: ['blur', 'change'] }]"
  >
    <a-input v-model="form.NAME">
      <template slot="addonAfter">
        <WI18nInput code="name" :target="form" v-model="form.NAME" />
      </template>
    </a-input>
  </a-form-model-item>
</template>
<style lang="less"></style>
<script type="text/babel">
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'PageNameInputFormItem',
  __ANT_NEW_FORM_ITEM: true,
  inject: ['form'],
  props: { widget: Object },
  components: { WI18nInput },
  computed: {},
  data() {
    console.log('PageNameInputFormItem', this.widget);
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
