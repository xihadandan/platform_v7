<template>
  <div class="field-content-loading" v-if="loading"></div>
  <component
    v-else-if="fieldWidget != undefined"
    :is="fieldWidget.wtype"
    :widget="fieldWidget"
    :key="key"
    :index="index"
    :form="form"
    :widgetsOfParent="widgetsOfParent"
    :parent="parent"
    :formModelItemProp="formModelItemProp"
  />
</template>
<style lang="less">
.field-content-loading {
  height: 16px;
  background: linear-gradient(90deg, #f2f2f2 25%, #e6e6e6 37%, #f2f2f2 63%);
  background-size: 400% 100%;
  -webkit-animation: ant-skeleton-loading 1.4s ease infinite;
  animation: ant-skeleton-loading 1.4s ease infinite;
}
</style>
<script type="text/babel">
import { cloneDeep } from 'lodash';
export default {
  name: 'WidgetRefFormFieldPlaceholder',
  inject: ['designMode', 'refDyforms', 'widgetDyformContext'],
  mixins: [],
  props: {
    widget: Object, // 组件定义
    widgetsOfParent: Array, // 父组件的所有子组件
    designer: Object, // 设计器实例
    parent: Object, // 父组件
    index: Number //当前组件在父组件的子组件列表的序号
  },

  provide() {
    return {
      dyform: undefined
    };
  },

  components: {},
  computed: {
    formModelItemProp() {
      // 指定表单数据域的属性路径：引用表单数据会在主表单数据域下以嵌套数据存在 { ... 主表单属性值 , __REF_FORM_DATA__ : { 引用表单ID : { 引用表单属性值 } } }
      return `__REF_FORM_DATA__.${this.widget.refDyformId}.${this.widget.configuration.code}`;
    }
  },
  data() {
    return { loading: true, form: undefined, fieldWidget: undefined };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    if (!this.designMode) {
      // 主动获取机制，兼容偶发一直加载中的情况
      this.delayFetchTimeout();
    } else if (this.refDyforms.length > 0) {
      for (let f of this.refDyforms) {
        if (f.formId == this.widget.refDyformId) {
          this.form = f;
          this.setFieldWidget(f);
          this.loading = false;
          break;
        }
      }
    }
  },
  methods: {
    delayFetchTimeout() {
      this.delayFetchTimeoutTag = setTimeout(() => {
        if (this.loading && this.refDyforms && this.refDyforms.length > 0) {
          for (let ref of this.refDyforms) {
            if (ref.formId == this.widget.refDyformId) {
              this.form = ref;
              this._provided.dyform = ref;
              this.setFieldWidget(ref);
              this.loading = false;
              return true;
            }
          }
        }
        if (this.loading) {
          // 加载中，继续下次重试
          this.delayFetchTimeout();
        }
      }, 2000);
    },
    setFieldWidget(f) {
      let json = f.formDefinitionJson,
        fields = json.fields;
      for (let i = 0, len = fields.length; i < len; i++) {
        if (fields[i].id == this.widget.id || fields[i].configuration.code == this.widget.configuration.code) {
          let wgt = JSON.parse(JSON.stringify(this.widget));
          wgt.wtype = fields[i].wtype;
          wgt.configuration = JSON.parse(JSON.stringify(fields[i].configuration));
          wgt.configuration.name = this.widget.configuration.name;
          wgt.configuration.fieldNameVisible = this.widget.configuration.fieldNameVisible;
          wgt.configuration.defaultDisplayState = this.widget.configuration.defaultDisplayState;
          if (wgt.configuration.defaultDisplayState == 'unedit') {
            // 不可编辑情况下，不需要规则校验
            wgt.configuration.required = false;
            wgt.configuration.validateRule = undefined;
          }
          wgt.configuration.defineEvents = []; // 清空事件，避免引用表单的事件导致逻辑错乱
          this.fieldWidget = wgt;
        }
      }
    }
  },
  watch: {
    refDyforms: {
      handler(v) {
        if (v && v.length > 0 && this.loading) {
          for (let f of v) {
            if (f.formId == this.widget.refDyformId) {
              this.form = f;
              this._provided.dyform = f;
              this.setFieldWidget(f);
              this.loading = false;
              break;
            }
          }
        }
      }
    }
  }
};
</script>
