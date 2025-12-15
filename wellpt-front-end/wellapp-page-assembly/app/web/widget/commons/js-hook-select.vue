<template>
  <a-input-group compact v-if="designer.isUniAppDesign">
    <a-input placeholder="脚本名" style="width: 49%" v-model="jsName" @change="onChangeInput"></a-input>
    <a-input placeholder="方法名" style="width: 51%" v-model="methodName" @change="onChangeInput">
      <template slot="prefix"><label style="font-weight: bolder">.</label></template>
    </a-input>
  </a-input-group>
  <a-select
    v-else
    :filter-option="filterOption"
    showSearch
    allowClear
    :style="{ width: '100%' }"
    :options="jsOptions"
    @change="onChange"
    v-model="selected"
  ></a-select>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'JsHookSelect',
  props: {
    value: String,
    jsKeys: Array,
    designer: Object,
    widget: Object
  },
  data() {
    return { selected: this.value, jsName: undefined, methodName: undefined };
  },

  beforeCreate() {},
  components: {},
  computed: {
    jsOptions() {
      let keys = [],
        options = [];
      if (this.designer != undefined && this.designer.pageJsModule != undefined) {
        // 页面脚本
        keys.push(this.designer.pageJsModule.key);
      }

      if (this.widget != undefined && this.widget.configuration.jsModules) {
        // 组件脚本
        let widgetJsModules = Array.isArray(this.widget.configuration.jsModules)
          ? this.widget.configuration.jsModules
          : [this.widget.configuration.jsModules];
        for (let i = 0, len = widgetJsModules.length; i < len; i++) {
          keys.push(widgetJsModules[i].key);
        }
      }
      if (this.jsKeys != undefined) {
        keys = keys.concat(this.jsKeys);
      }

      for (let i = 0, len = keys.length; i < len; i++) {
        let _d = this.__developScript[keys[i]];
        if (_d) {
          try {
            let meta = _d.default.prototype.META;
            if (meta && meta.hook) {
              for (let h in meta.hook) {
                let label = typeof meta.hook[h] == 'string' ? meta.hook[h] : meta.hook[h].title;
                let opt = { label, value: `${keys[i]}.${h}` };
                if (typeof meta.hook[h] !== 'string' && meta.hook[h].eventParams) {
                  opt.eventParams = JSON.parse(JSON.stringify(meta.hook[h].eventParams));
                }
                options.push(opt);
              }
            }
          } catch (error) {
            console.error(`二开脚本 ${keys[i]} 解析脚本数据异常: `, error);
          }
        }
      }
      return options;
    },
    vInputChange() {
      return this.jsName + '.' + this.methodName;
    }
  },
  created() {
    if (this.designer.isUniAppDesign && this.selected) {
      let parts = this.selected.split('.');
      this.jsName = parts[0];
      this.methodName = parts[1];
    }
  },
  methods: {
    onChangeInput() {
      this.$emit('input', this.vInputChange);
    },
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },
    onChange(v) {
      this.$emit('input', v);
      let opt = this.jsOptions.find(o => o.value === v);
      this.$emit('change', v, opt);
    }
  },
  beforeMount() {},
  mounted() {}
};
</script>
