<template>
  <a-form-model-item>
    <template slot="label">
      <div class="theme-form-item-label">
        {{ label }}
        <label class="code">{{ codeLabel }}</label>
      </div>
    </template>
    <a-select
      class="theme-design-select-style"
      dropdownClassName="theme-design-select-dropdown-style"
      style="width: 100%"
      v-model="selected"
      @change="onChange"
    >
      <a-select-option v-for="opt in options" :key="opt.value" :value="opt.value">
        <div class="select-option">
          {{ opt.label }}
          <label class="subLabel" v-if="opt.remark != undefined" :title="opt.remark">{{ opt.remark }}</label>
        </div>
      </a-select-option>
    </a-select>
  </a-form-model-item>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'StyleVarSelectFormItem',
  props: {
    label: String,
    codeLabel: String,
    value: String,
    options: []
  },
  components: {},
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
