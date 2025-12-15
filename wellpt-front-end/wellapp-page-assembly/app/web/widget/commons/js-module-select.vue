<template>
  <a-select
    placeholder="请输入脚本名"
    v-if="designer && designer.isUniAppDesign"
    mode="tags"
    v-model="selectValue"
    :labelInValue="labelInValue"
    :showArrow="true"
    :options="[]"
    @change="onChange"
  ></a-select>

  <a-select
    v-else
    show-search
    :filter-option="filterOption"
    :style="{ width: width }"
    :size="size"
    allowClear
    :showSearch="true"
    :labelInValue="labelInValue"
    :mode="multiSelect ? 'multiple' : 'default'"
    v-model="selectValue"
    @change="onChange"
    @blur="onBlur"
    :getPopupContainer="getPopupContainer"
    dropdownClassName="ps__child--consume"
    :options="options"
  ></a-select>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'JsModuleSelect',
  inject: ['designer'],
  mixins: [],
  props: {
    value: {
      type: [String, Object, Array]
    },

    applyTo: String,
    width: {
      type: String,
      default: '100%'
    },
    size: {
      type: String,
      default: 'default'
    },
    dependencyFilter: { type: String, default: 'VueWidgetDevelopment' },
    multiSelect: {
      type: Boolean,
      default: true
    },
    labelInValue: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      options: [],
      selectValue:
        this.value === undefined || (!Array.isArray(this.value) && this.value && Object.keys(this.value).length == 0)
          ? undefined
          : this.value
    };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {
    getPopupContainer() {
      return this.$el.parentElement;
    },
    fetchJsOptions() {
      let scripts = this.__developScript;
      for (let key in scripts) {
        if (scripts[key] && scripts[key].default) {
          try {
            let meta = scripts[key].default.prototype.META;
            if (meta && this.matchDependencyFilter(scripts[key].default)) {
              this.options.push({ label: meta ? meta.name : key, value: key, title: meta ? meta.name : key });
            }
          } catch (error) {
            console.error(`二开脚本 ${key} 解析脚本数据异常: `, error);
          }
        }
      }
    },
    matchDependencyFilter(module) {
      return module.prototype.ROOT_CLASS == this.dependencyFilter;
    },
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    onChange(value, option) {
      this.$emit('input', value, this.options);
      this.$emit('change', value, option, this.options);
    },
    onBlur() {
      this.$emit('blur');
    }
  },
  beforeMount() {
    this.fetchJsOptions();
  },
  mounted() {}
};
</script>
