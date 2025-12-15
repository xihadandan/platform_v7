<template>
  <a-form-model-item>
    <template slot="label">
      <div style="display: inline-flex; flex-wrap: wrap">
        <a-select
          size="small"
          :options="[
            { label: '外部' + label, value: '' },
            { label: '内部' + label, value: 'inset' }
          ]"
          v-model="boxShadowType"
          style="width: 100px"
          @change="onChangeShadowType"
        />
        <label class="code">{{ codeLabel }}</label>
      </div>
    </template>

    <a-tree-select
      :value="selected"
      show-search
      style="width: 250px"
      :dropdown-style="{ maxHeight: '400px', overflow: 'auto', maxWidth: '200px' }"
      allow-clear
      dropdownClassName="style-color-tree-select"
      @change="onChange"
    >
      <template v-for="(key, k) in ['levelOne', 'levelTwo', 'levelThree']">
        <a-tree-select-node :key="'shadowCfg_' + key" :title="levelLabels[key]" :selectable="false">
          <a-tree-select-node
            v-for="(derive, i) in packJson.shadowConfig[key].derive"
            :title="derive.code"
            :value="derive.code"
            :key="derive.code"
          ></a-tree-select-node>
        </a-tree-select-node>
      </template>
    </a-tree-select>
  </a-form-model-item>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'StyleBoxShadowTreeSelectFormItem',
  inject: ['packJson'],
  props: {
    label: String,
    codeLabel: String,
    value: String
  },
  components: {},
  computed: {},
  data() {
    return {
      boxShadowType: this.value && this.value.startsWith('inset') ? 'inset' : '',
      selected: this.value && this.value.startsWith('inset') ? this.value.split('inset ')[1] : this.value,
      levelLabels: { levelOne: '一级阴影', levelTwo: '二级阴影', levelThree: '三级阴影' }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onChange(v) {
      this.selected = v;
      let boxShadow = this.boxShadowType == 'inset' ? 'inset ' + this.selected : this.selected;
      this.$emit('input', boxShadow);
      this.$emit('change', boxShadow);
    },
    onChangeShadowType(v) {
      if (this.selected) {
        let boxShadow = v == 'inset' ? v + ' ' + this.selected : this.selected;
        this.$emit('input', boxShadow);
        this.$emit('change', boxShadow);
      }
    }
  }
};
</script>
