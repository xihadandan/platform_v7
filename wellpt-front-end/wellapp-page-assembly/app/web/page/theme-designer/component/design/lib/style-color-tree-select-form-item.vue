<template>
  <a-form-model-item>
    <template slot="label">
      <div class="theme-form-item-label">
        {{ label }}
        <label class="code" v-if="codeLabel != undefined">{{ codeLabel }}</label>
      </div>
    </template>
    <StyleColorTreeSelect :colorConfig="packJson.colorConfig" v-model="selected" @change="onChange" />
  </a-form-model-item>
</template>
<style lang="less"></style>
<script type="text/babel">
import StyleColorTreeSelect from './style-color-tree-select.vue';
export default {
  name: 'StyleColorTreeSelectFormItem',
  inject: ['packJson'],
  props: {
    label: String,
    codeLabel: String,
    value: String
  },
  components: { StyleColorTreeSelect },
  computed: {},
  data() {
    return { selected: this.value };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onChange(v) {
      this.$emit('input', v);
    }
  }
};
</script>
