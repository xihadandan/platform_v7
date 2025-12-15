<template>
  <a-form-model-item label="显示标题" prop="TITLE">
    <a-input v-model="form.TITLE">
      <template slot="addonAfter">
        <WI18nInput code="title" :target="form" v-model="form.TITLE" />
      </template>
    </a-input>
  </a-form-model-item>
</template>
<style lang="less"></style>
<script type="text/babel">
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'PageTitleInputFormItem',
  __ANT_NEW_FORM_ITEM: true,
  inject: ['form'],
  props: {},
  components: { WI18nInput },
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
