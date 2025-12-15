<template>
  <div>
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model ref="form" :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }">
          <DeviceVisible :widget="widget" />
          <a-form-model-item label="标题">
            <a-input
              v-model="widget.configuration.title"
              @change="
                evt => {
                  widget.title = evt.target.value;
                }
              "
            >
              <template slot="addonBefore">
                <WidgetDesignModal
                  title="选择图标"
                  :zIndex="1000"
                  dialogClass="pt-modal widget-icon-lib-modal"
                  :bodyStyle="{ height: '560px' }"
                  :maxHeight="560"
                  :width="640"
                  mask
                  bodyContainer
                >
                  <IconSetBadge title="设置标题图标" v-model="widget.configuration.titleIcon" :size="24" :fontSize="16" />
                  <template slot="content">
                    <WidgetIconLib v-model="widget.configuration.titleIcon" />
                  </template>
                </WidgetDesignModal>
              </template>
              <template slot="addonAfter">
                <a-switch size="small" :checked="!widget.configuration.titleHidden" @change="onChangeTitleHidden" title="显示标题" />

                <WI18nInput
                  v-show="!widget.configuration.titleHidden"
                  :widget="widget"
                  :designer="designer"
                  code="title"
                  v-model="widget.configuration.title"
                />
              </template>
            </a-input>
          </a-form-model-item>
          <a-form-model-item label="编码">
            <a-input v-model="widget.configuration.code" />
          </a-form-model-item>
          <a-form-model-item label="单元格布局">
            <a-radio-group v-show="designer.terminalType == 'pc'" v-model="widget.configuration.layout" button-style="solid" size="small">
              <a-radio-button value="horizontal">水平</a-radio-button>
              <a-radio-button value="vertical">垂直</a-radio-button>
            </a-radio-group>
            <a-radio-group
              v-show="designer.terminalType == 'mobile'"
              v-model="widget.configuration.uniConfiguration.layout"
              button-style="solid"
              size="small"
            >
              <a-radio-button value="horizontal">水平</a-radio-button>
              <a-radio-button value="vertical">垂直</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <div v-show="designer.terminalType == 'pc'">
            <a-form-model-item label="边框">
              <a-switch v-model="widget.configuration.bordered" />
            </a-form-model-item>
            <a-form-model-item label="显示冒号">
              <a-switch v-model="widget.configuration.colon" />
            </a-form-model-item>

            <a-form-model-item label="单元格标题内容合并展示" v-if="widget.configuration.layout != 'vertical'">
              <a-switch v-model="widget.configuration.colLabelContentMergeShow" />
            </a-form-model-item>
            <a-form-model-item
              label="单元格标题对齐"
              v-if="widget.configuration.layout != 'vertical' && !widget.configuration.colLabelContentMergeShow"
            >
              <a-radio-group v-model="widget.configuration.labelAlign" default-value="left" button-style="solid" size="small">
                <a-radio-button value="left" title="左对齐"><a-icon type="align-left" /></a-radio-button>
                <a-radio-button value="right" title="右对齐"><a-icon type="align-right" /></a-radio-button>
              </a-radio-group>
            </a-form-model-item>

            <a-form-model-item label="每行单元格数">
              <a-input-number v-model="widget.configuration.column" :min="1" :max="24" @change="onChangeColumn" />
            </a-form-model-item>
            <a-form-model-item label="单元格标题宽" v-if="widget.configuration.layout != 'vertical'">
              <a-input-group compact>
                <a-input-number v-model="widget.configuration.labelColumnWidth" :min="80" />
                <a-button>px</a-button>
              </a-input-group>
            </a-form-model-item>
            <a-form-model-item>
              <template slot="label">
                <a-popover>
                  <template slot="content">
                    <ul style="list-style: none; margin: 0px; padding: 0; line-height: 1.3">
                      <li>均不设置宽度的情况下, 列的宽度将等分展示。</li>
                      <li>如果仅设置其中个别列的宽度像素, 其余列的宽度将等分展示。</li>
                    </ul>
                  </template>
                  列宽度
                  <a-icon type="info-circle" />
                </a-popover>
              </template>
              <a-checkbox v-model="widget.configuration.columnWidthAvg" @change="onChangeColWidAvg">等分</a-checkbox>
              <div v-show="!widget.configuration.columnWidthAvg">
                <template v-for="(col, i) in widget.configuration.colgroup">
                  <a-input-group compact :key="'col_input_width_' + i">
                    <a-input-number v-model="col.width" allow-clear :min="0"></a-input-number>
                    <a-select
                      v-model="col.widthUnit"
                      :options="[
                        { label: 'px', value: 'px' },
                        { label: '%', value: '%' }
                      ]"
                    ></a-select>
                  </a-input-group>
                  <!-- <a-input :key="'col_input_width_' + i" v-model="col.width" allow-clear>
                  <template slot="addonAfter">
                    <a-select
                      v-model="col.widthUnit"
                      :options="[
                        { label: 'px', value: 'px' },
                        { label: '%', value: '%' }
                      ]"
                    ></a-select>
                  </template>
                </a-input> -->
                </template>
              </div>
            </a-form-model-item>
          </div>
          <DefaultVisibleConfiguration
            compact
            :designer="designer"
            :configuration="widget.configuration"
            :widget="widget"
          ></DefaultVisibleConfiguration>
        </a-form-model>
      </a-tab-pane>
      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>
<style></style>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
export default {
  name: 'WidgetFormLayoutConfiguration',
  mixins: [],
  inject: [],
  props: {
    widget: Object,
    designer: Object,
    configuration: Object
  },
  data() {
    return {};
  },

  beforeCreate() {},
  components: {},
  computed: {},

  created() {
    if (this.widget.configuration && this.widget.configuration.labelColumnWidth == undefined) {
      this.$set(this.widget.configuration, 'labelColumnWidth', 120);
      // this.widget.configuration.labelColumnWidth = 120;
    }
  },
  methods: {
    onChangeTitleHidden() {
      this.$set(this.widget.configuration, 'titleHidden', !this.widget.configuration.titleHidden);
    },
    removeItem(ii) {
      this.widget.configuration.items.splice(ii, 1);
    },

    addItem() {
      let newItem = {
        wtype: 'WidgetFormItem',
        id: `form-item-${generateId()}`,
        title: '单元格',
        configuration: {
          label: '',
          span: 1,
          hidden: false,
          widgets: [],
          required: false
        }
      };
      this.widget.configuration.items.push(newItem);
    },
    onChangeColWidAvg() {},
    onChangeColumn() {
      let { colgroup, column } = this.widget.configuration;
      if (colgroup.length < column) {
        for (let i = 0, len = column - colgroup.length; i < len; i++) {
          colgroup.push({
            width: undefined,
            widthUnit: 'px'
          });
        }
      } else {
        colgroup.splice(column);
      }
    }
  },
  beforeMount() {},
  mounted() {},
  configuration() {
    let items = [];
    // 初始化两行两列
    let item = {
      wtype: 'WidgetFormItem',
      title: '单元格',
      configuration: {
        label: '',
        span: 1,
        required: false,
        hidden: false,
        widgets: [],
        labelWidgets: []
      }
    };
    for (let i = 0; i < 4; i++) {
      let _it = deepClone(item);
      _it.id = `form-item-${generateId()}`;
      items.push(_it);
    }
    return {
      title: '表单布局',
      titleHidden: false,
      titleIcon: undefined,
      hidden: false,
      bordered: true,
      columnWidthAvg: true,
      column: 2,
      colon: false,
      layout: 'horizontal',
      labelColumnWidth: 120,
      code: new Date().getTime(), // 初始编码随机
      isDatabaseField: false,
      items,
      style: {}
    };
  }
};
</script>
