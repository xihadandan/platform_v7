<template>
  <div>
    <a-form-model-item>
      <template slot="label">
        {{ label }}
        <template v-if="onlyValue">
          <a-tooltip title="使用内置颜色变量" :getPopupContainer="popupContainer">
            <a-checkbox v-if="hasThemeColor" v-model="fontColorUseBar" @change="value[colorField] = undefined" />
          </a-tooltip>
        </template>
      </template>
      <a-radio-group v-model="value[typeField]" :size="radioSize" :button-style="radioStyle" v-if="!onlyValue">
        <a-radio-button value="default">默认</a-radio-button>
        <a-radio-button value="value">
          指定颜色
          <a-tooltip title="使用内置颜色变量">
            <a-checkbox v-if="hasThemeColor" v-model="fontColorUseBar" @change="value[colorField] = undefined" />
          </a-tooltip>
        </a-radio-button>
        <a-popover>
          <template slot="content">
            <div>支持使用函数返回颜色值, 入参传入的是 params 参数对象，对象上包括数据项 item , count 参数</div>
            <div>例如: return count > 100 ? 'red' : undefined ;</div>
          </template>
          <a-radio-button value="function" v-if="hasFunction">运行时函数判断</a-radio-button>
        </a-popover>
      </a-radio-group>
      <template v-if="(!onlyValue && value[typeField] == 'value') || onlyValue">
        <StyleColorTreeSelect
          v-if="hasThemeColor && fontColorUseBar && colorConfigOptions"
          :colorConfig="colorConfigOptions"
          style="width: 100%"
          v-model="value[colorField]"
          :popupContainer="popupContainer"
        />
        <div v-else>
          <ColorPicker
            v-model="value[colorField]"
            :width="150"
            :allowClear="true"
            :popupContainer="popupContainer"
            @ok="changeColorPicker"
          />
        </div>
      </template>
      <WidgetCodeEditor
        v-if="value[typeField] == 'function'"
        v-model="value[colorField]"
        width="auto"
        height="100px"
        lang="js"
        :hideError="true"
      ></WidgetCodeEditor>
    </a-form-model-item>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import StyleColorTreeSelect from '@pageAssembly/app/web/page/theme-designer/component/design/lib/style-color-tree-select.vue';
import { getPopupContainerNearestPs } from '@framework/vue/utils/function.js';
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
import WidgetCodeEditor from '@pageWidget/commons/widget-code-editor.vue';

export default {
  name: 'ColorSelectConfiguration',
  props: {
    value: Object,
    colorConfig: Object,
    hasFunction: {
      type: Boolean,
      default: false
    },
    typeField: {
      type: String,
      default: 'type'
    },
    colorField: {
      type: String,
      default: 'color'
    },
    label: {
      type: String,
      default: '设置颜色'
    },
    // 只有颜色选择
    onlyValue: {
      type: Boolean,
      default: false
    },
    // 是否可以选择主题颜色
    hasThemeColor: {
      type: Boolean,
      default: true
    },
    radioSize: String,
    radioStyle: String,
    popupContainer: Function,
    responsive: {
      type: Boolean,
      default: false
    }
  },
  components: { StyleColorTreeSelect, ColorPicker, WidgetCodeEditor },
  data() {
    if (!this.value.hasOwnProperty(this.colorField)) {
      this.value[this.colorField] = undefined;
    }
    if (!this.onlyValue && !this.value.hasOwnProperty(this.typeField)) {
      this.value[this.typeField] = 'default';
    }
    let fontColorUseBar = false;
    if (this.onlyValue) {
      fontColorUseBar = this.value[this.colorField] != undefined && this.value[this.colorField].startsWith('--');
    } else {
      fontColorUseBar =
        this.value[this.typeField] != undefined &&
        this.value[this.typeField] == 'value' &&
        this.value[this.colorField] != undefined &&
        this.value[this.colorField].startsWith('--');
    }
    return {
      fontColorUseBar,
      colorConfigJson: undefined
    };
  },
  computed: {
    colorConfigOptions() {
      if (this.colorConfig) {
        return this.colorConfig;
      }
      return this.colorConfigJson;
    }
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchThemeSpecify();
  },
  mounted() {},
  methods: {
    changeColorPicker(color) {
      if (this.responsive) {
        this.$emit('input', JSON.parse(JSON.stringify(this.value)));
      }
    },
    getPopupContainerNearestPs,
    fetchThemeSpecify() {
      $axios
        .get(`/proxy/api/theme/specify/getEnabled`, { params: {} })
        .then(({ data }) => {
          console.log('获取主题规范', data);
          if (data.code == 0) {
            let specifyDefJson = JSON.parse(data.data.defJson);
            this.colorConfigJson = specifyDefJson.colorConfig;
          }
        })
        .catch(error => {});
    }
  }
};
</script>
