<template>
  <a-collapse :bordered="false" expandIconPosition="right">
    <a-collapse-panel key="component_mode_properties" header="编辑模式属性">
      <a-form-model-item label="允许再次点击后清除">
        <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.rate.allowClear" />
      </a-form-model-item>
      <a-form-model-item label="允许半选">
        <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.rate.allowHalf" />
      </a-form-model-item>
    </a-collapse-panel>
    <a-collapse-panel key="component_show_properties">
      <template slot="header">
        <a-tooltip placement="bottomRight">
          <template slot="title">
            <ul style="padding-inline-start: 20px; margin-block-end: 0px">
              <li>移动端评分超出屏幕区域不换行，请合理配置</li>
              <li>移动端不支持ANT图标库</li>
            </ul>
          </template>
          显示设置
          <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" style="font-size: inherit; font-weight: normal; color: inherit" />
        </a-tooltip>
      </template>
      <a-form-model-item label="字符类型">
        <a-radio-group size="small" v-model="widget.configuration.rate.characterType" button-style="solid">
          <a-radio-button value="icon">图标</a-radio-button>
          <a-radio-button value="string">字符串</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <template v-if="widget.configuration.rate.characterType == 'string'">
        <a-form-model-item label="字符串">
          <a-input v-model="widget.configuration.rate.characterString" allowClear />
        </a-form-model-item>
      </template>
      <template v-else-if="widget.configuration.rate.characterType == 'icon'">
        <a-form-model-item label="图标">
          <WidgetDesignDrawer
            :id="'widgetFormRateIcon' + widget.id"
            title="选择图标"
            :width="640"
            :bodyStyle="{ height: '100%' }"
            :designer="designer"
          >
            <IconSetBadge v-model="widget.configuration.rate.characterIcon" onlyIconClass />
            <template slot="content">
              <WidgetIconLib v-model="widget.configuration.rate.characterIcon" />
            </template>
          </WidgetDesignDrawer>
        </a-form-model-item>
      </template>
      <a-form-model-item label="字符数量">
        <a-input-number v-model="widget.configuration.rate.count" :min="1" />
      </a-form-model-item>
      <a-form-model-item label="字符样式">
        <a-radio-group size="small" v-model="widget.configuration.rate.defaultStyle" button-style="solid">
          <a-radio-button value="default">默认</a-radio-button>
          <a-radio-button value="define">自定义</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <template v-if="widget.configuration.rate.defaultStyle == 'define'">
        <ColorSelectConfiguration
          label="选中颜色"
          v-model="widget.configuration.rate"
          :onlyValue="true"
          colorField="selectedColor"
          radioSize="small"
          radioStyle="solid"
        ></ColorSelectConfiguration>
        <ColorSelectConfiguration
          label="背景颜色"
          v-model="widget.configuration.rate"
          :onlyValue="true"
          colorField="bgColor"
          radioSize="small"
          radioStyle="solid"
        ></ColorSelectConfiguration>
        <a-form-model-item label="字符大小">
          <a-input-number v-model="widget.configuration.rate.fontSize" :min="12" />
          px
        </a-form-model-item>
        <a-form-model-item label="字重大小" v-if="widget.configuration.rate.characterType == 'string'">
          <a-select
            :options="fontWeightOptions"
            v-model="widget.configuration.rate.fontWeight"
            allowClear
            :getPopupContainer="getPopupContainerByPs()"
            :dropdownClassName="getDropdownClassName()"
          ></a-select>
        </a-form-model-item>
      </template>
    </a-collapse-panel>
    <a-collapse-panel key="component_other_properties" header="其他设置" v-if="designer.terminalType == 'pc'">
      <a-form-model-item label="显示提示信息">
        <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.rate.isTooltips" />
      </a-form-model-item>
      <template v-if="widget.configuration.rate.isTooltips">
        <a-form-model-item :label="'提示信息' + (item + 1)" v-for="item in countArr" :key="'tooltips_' + item">
          <a-input v-model="widget.configuration.rate.tooltips[item]" allowClear />
        </a-form-model-item>
      </template>
    </a-collapse-panel>
  </a-collapse>
</template>

<script>
import { times } from 'lodash';
import ColorSelectConfiguration from '@pageAssembly/app/web/widget/commons/color-select-configuration.vue';
// import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
export default {
  name: 'WidgetFormNumericalBarRateConfiguration',
  props: {
    widget: Object,
    designer: Object
  },
  components: { ColorSelectConfiguration },
  data() {
    return {
      fontWeightOptions: [
        { label: '默认值', value: 'normal' },
        { label: '粗体', value: 'bold' },
        { label: '更粗', value: 'bolder' },
        { label: '更细', value: 'lighter' }
      ]
    };
  },
  computed: {
    countArr() {
      return times(this.widget.configuration.rate.count);
    }
  },
  methods: {
    getPopupContainerByPs,
    getDropdownClassName
  }
};
</script>
