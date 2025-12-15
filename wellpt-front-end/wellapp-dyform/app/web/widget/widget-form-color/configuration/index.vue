<template>
  <div>
    <a-form-model
      ref="form"
      :model="widget.configuration"
      :rules="rules"
      labelAlign="left"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 15, style: { textAlign: 'right' } }"
    >
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <FieldNameInput :widget="widget" />
          <FieldCodeInput :widget="widget" />
          <FieldLengthInput :widget="widget" />
          <a-form-model-item label="默认值" prop="">
            <ColorPicker v-model="widget.configuration.defaultValue" allowClear @ok="onColorPickOk"></ColorPicker>
          </a-form-model-item>
          <a-form-model-item label="组件尺寸">
            <a-radio-group size="small" v-model="widget.configuration.size">
              <a-radio-button value="default">默认</a-radio-button>
              <a-radio-button value="sm">小</a-radio-button>
              <a-radio-button value="lg">大</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="默认状态">
            <a-radio-group size="small" v-model="widget.configuration.defaultDisplayState">
              <a-radio-button value="edit">可编辑</a-radio-button>
              <a-radio-button value="unedit">不可编辑</a-radio-button>
              <a-radio-button value="hidden">隐藏</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-collapse :bordered="false" expandIconPosition="right">
            <a-collapse-panel key="uneditMode" header="不可编辑模式属性">
              <a-form-model-item label="不可编辑状态">
                <a-radio-group size="small" v-model="widget.configuration.uneditableDisplayState">
                  <a-radio-button value="label">纯文本</a-radio-button>
                  <a-radio-button value="readonly">只读(显示组件样式)</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
            </a-collapse-panel>
            <a-collapse-panel key="editMode" header="编辑模式属性">
              <a-form-model-item label="选择面板">
                <a-radio-group size="small" v-model="widget.configuration.picker">
                  <a-radio-button value="Twitter">预设颜色</a-radio-button>
                  <a-radio-button value="Chrome">色盘</a-radio-button>
                  <a-radio-button value="Sketch">组合面板</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <div v-show="['Twitter', 'Sketch'].indexOf(widget.configuration.picker) > -1">
                <a-form-model-item label="" :wrapper-col="{ span: 24 }" v-if="['Sketch'].indexOf(widget.configuration.picker) > -1">
                  <span style="color: var(--w-text-color-light)">颜色选择面板中同时显示预设颜色和色盘</span>
                </a-form-model-item>
                <a-form-model-item label="设置预设颜色" :label-col="{ span: 8 }" :wrapper-col="{ span: 16, style: { textAlign: 'right' } }">
                  <a-radio-group size="small" v-model="widget.configuration.defaultColorType">
                    <a-radio-button value="define">自定义</a-radio-button>
                    <!-- <a-radio-button value="theme">平台主题色</a-radio-button> -->
                  </a-radio-group>
                </a-form-model-item>
                <ColorPaletteConfiguration
                  :designer="designer"
                  :widget="widget"
                  :colors="widget.configuration.defaultColors"
                ></ColorPaletteConfiguration>
              </div>
              <a-form-model-item
                label="允许切换至组合面板"
                :label-col="{ span: 12 }"
                class="item-lh"
                :wrapper-col="{ style: { textAlign: 'right' } }"
                v-show="widget.configuration.picker == 'Twitter'"
              >
                <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.pickerChange" />
              </a-form-model-item>
              <a-form-model-item label="显示所选色值" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
                <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.showText" />
              </a-form-model-item>
            </a-collapse-panel>
            <template v-if="designer.terminalType == 'mobile'">
              <a-collapse-panel key="widget-style" header="组件样式">
                <a-form-model-item label="显示边框" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
                  <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.uniConfiguration.inputBorder" />
                </a-form-model-item>
              </a-collapse-panel>
            </template>
            <!-- 其它属性 -->
            <a-collapse-panel key="otherProp" header="其它属性">
              <a-form-model-item label="应用于">
                <FieldApplySelect v-model="widget.configuration.applyToDatas" />
              </a-form-model-item>
              <a-form-model-item label="描述" :wrapper-col="{ style: { marginTop: '2px' } }">
                <a-textarea :rows="4" placeholder="请输入内容" v-model="widget.configuration.note" :maxLength="200" />
                <span class="textLengthShow">{{ widget.configuration.note | textLengthFilter }}/200</span>
              </a-form-model-item>
            </a-collapse-panel>
          </a-collapse>
        </a-tab-pane>
        <a-tab-pane key="2" tab="校验规则">
          <ValidateRuleConfiguration :widget="widget"></ValidateRuleConfiguration>
        </a-tab-pane>
        <a-tab-pane key="3" tab="事件设置">
          <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
import formConfigureMixin from '../../mixin/formConfigure.mixin';
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';
import ColorPaletteConfiguration from './color-palette-configuration';

export default {
  name: 'WidgetFormColorConfiguration',
  mixins: [formConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      rules: {
        name: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true },
        code: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true }
      }
    };
  },

  beforeCreate() {},
  components: { ColorPicker, ColorPaletteConfiguration },
  computed: {},
  filters: {
    textLengthFilter(text) {
      return text ? text.length : 0;
    }
  },
  created() {
    if (!this.widget.configuration.hasOwnProperty('uniConfiguration')) {
      this.$set(this.widget.configuration, 'uniConfiguration', { inputBorder: false });
    }
  },
  methods: {
    getPopupContainerByPs,
    onColorPickOk(color) {
      if (color) {
        this.widget.configuration.hasDefaultValue = true;
      } else {
        this.widget.configuration.hasDefaultValue = false;
      }
      this.$set(this.widget.configuration, 'defaultValue', color);
    }
  },
  mounted() {},
  updated() {}
};
</script>
