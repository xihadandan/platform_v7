<template>
  <div>
    <a-form-model>
      <!--从表头部按钮和行按钮存在相同id的情况，头部按钮国际化code做额外处理，行按钮不做处理 -->
      <a-form-model-item label="按钮名称">
        <a-input v-model="button.title">
          <template slot="addonAfter">
            <WI18nInput
              :widget="widget"
              :designer="designer"
              :code="position == 'header' ? position + '_' + button.id : button.id"
              v-model="button.title"
              :target="button"
            />
          </template>
        </a-input>
      </a-form-model-item>
      <!-- <a-form-model-item label="按钮类型">
      {{ button.default ? '内置按钮' : '扩展按钮' }}
    </a-form-model-item> -->
      <!-- <a-form-model-item label="按钮编码">
      <a-input v-model="button.code" v-show="!button.default" />
      <label v-show="button.default">{{ button.code }}</label>
    </a-form-model-item> -->
      <template
        v-if="
          designer.terminalType == 'pc' ||
          (designer.terminalType == 'mobile' && (position !== 'rowEnd' || widget.configuration.uniConfiguration.layout !== 'form-inline'))
        "
      >
        <a-form-model-item label="按钮类型">
          <a-select :options="buttonTypeOptions" v-model="button.style.type" :style="{ width: '100%' }"></a-select>
        </a-form-model-item>
      </template>
      <a-form-model-item label="行末样式" v-else>
        <div style="border-radius: 4px; border: 1px dotted var(--w-border-color-light); padding: var(--w-padding-2xs)">
          <a-form-model-item label="背景颜色">
            <ColorPicker v-model="button.bgColor" allow-clear></ColorPicker>
          </a-form-model-item>
          <a-form-model-item label="文字颜色">
            <ColorPicker v-model="button.textColor" allow-clear></ColorPicker>
          </a-form-model-item>
        </div>
      </a-form-model-item>
      <a-form-model-item label="按钮图标">
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
          <IconSetBadge v-model="button.style.icon"></IconSetBadge>
          <template slot="content">
            <WidgetIconLib v-model="button.style.icon" />
          </template>
        </WidgetDesignModal>
      </a-form-model-item>
      <a-form-model-item label="隐藏文本">
        <a-switch v-model="button.style.textHidden" />
      </a-form-model-item>
      <PopconfirmConfiguration :configuration="button" :designer="designer">
        <template slot="popconfirmTypeHelpSlot" v-if="designer.terminalType == 'mobile'">
          <a-popover placement="left">
            <template slot="content">移动端均按居中对话框显示</template>
            <a-button type="link" size="small"><Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon></a-button>
          </a-popover>
        </template>
        <template slot="popconfirmHelpSlot">
          <a-popover placement="left">
            <template slot="content">
              <div v-if="position == 'rowEnd'">
                支持通过
                <a-tag>${ 列字段 }</a-tag>
                设值
              </div>
              <div v-else>
                支持通过访问数据变量
                <a-tag>selectedRows</a-tag>
                <a-tag>selectedRowKeys</a-tag>
                设值
                <br />
                例如: ${ selectedRows.length } 获取行数
              </div>
            </template>
            <a-button type="link" size="small"><Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon></a-button>
          </a-popover>
        </template>
      </PopconfirmConfiguration>

      <DefaultVisibleConfiguration compact :designer="designer" :configuration="button" :widget="widget" :allowFormVariable="false">
        <template slot="extraAutoCompleteSelectGroup">
          <a-select-opt-group>
            <span slot="label">
              <a-icon type="code" />
              主表单数据
            </span>
            <a-select-option
              v-for="opt in fieldVarOptions"
              :key="'MAIN_FORM_DATA.' + opt.value"
              readonly
              :value="'MAIN_FORM_DATA.' + opt.value"
              :title="opt.label"
            >
              {{ opt.label }}
            </a-select-option>
          </a-select-opt-group>
          <a-select-opt-group>
            <span slot="label">
              <a-icon type="code" />
              {{ position == 'rowEnd' ? '行字段数据' : '已选行字段数据' }}
            </span>
            <a-select-option key="uuid" title="UUID">UUID</a-select-option>
            <a-select-option v-for="opt in widget.configuration.columns" :key="opt.dataIndex" :title="opt.title">
              {{ opt.title }}
            </a-select-option>
          </a-select-opt-group>
          <a-select-opt-group>
            <span slot="label">
              <a-icon type="code" />
              其他
            </span>
            <a-select-option key="__TABLE__.selectedRowCount" title="选中行数">选中行数</a-select-option>
          </a-select-opt-group>
        </template>
      </DefaultVisibleConfiguration>

      <a-form-model-item label="展示位置" v-show="!button.default && position == 'rowEnd'">
        <a-input-group compact :style="{ display: 'flex' }">
          <a-select
            :options="buttonDisplayPositions"
            :style="{ width: '100%' }"
            v-model="button.position"
            @change="onChangeDisplayPosition"
          ></a-select>
          <a-select
            placeholder="选中展示的列位置"
            :options="fieldSelectOptions"
            :style="{ width: '100%' }"
            v-model="button.displayUnderColumn"
            v-if="button.position === 'underColumn'"
          ></a-select>
        </a-input-group>
      </a-form-model-item>
      <slot name="extraInfo" :button="button"></slot>
      <!-- <a-collapse :bordered="false" expandIconPosition="right" :style="{ background: '#fff' }" defaultActiveKey="eventSetting">
      <a-collapse-panel key="eventSetting">
        <template slot="header">自定义事件</template>
        <a-form-model-item label="鼠标点击">
          <WidgetCodeEditor @save="value => (button.customEvent.click = value)" :value="button.customEvent.click">
            <a-button icon="code">编写代码</a-button>
          </WidgetCodeEditor>
        </a-form-model-item>

      </a-collapse-panel>
    </a-collapse> -->
    </a-form-model>
    <WidgetEventHandler
      v-if="button.eventHandler != undefined"
      :eventModel="button.eventHandler"
      :designer="designer"
      :widget="widget"
      :rule="{ name: false }"
    >
      <template slot="eventParamValueHelpSlot" v-if="position == 'rowEnd'"><FormEventParamHelp /></template>
    </WidgetEventHandler>
  </div>
</template>

<script type="text/babel">
import { buttonTypeOptions } from '@pageAssembly/app/web/widget/commons/constant.js';
import PopconfirmConfiguration from '@pageAssembly/app/web/widget/commons/popconfirm-configuration.vue';
import IconSetBadge from '@pageAssembly/app/web/widget/commons/icon-set-badge.vue';
export default {
  name: 'ButtonConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    button: Object,
    position: String,
    columnIndexOptions: Array
  },
  data() {
    return {
      iconVisible: this.button.style.icon != undefined,
      buttonTypeOptions,
      buttonDisplayPositions: [
        { label: '行末', value: 'rowEnd' },
        { label: '列下', value: 'underColumn' }
      ]
    };
  },

  beforeCreate() {},
  components: { PopconfirmConfiguration, IconSetBadge },
  computed: {
    fieldSelectOptions() {
      let fieldSelectOptions = [];
      for (let i = 0, len = this.columnIndexOptions.length; i < len; i++) {
        fieldSelectOptions.push({
          label: this.columnIndexOptions[i].configuration.name,
          value: this.columnIndexOptions[i].configuration.code
        });
      }
      return fieldSelectOptions;
    },
    fieldVarOptions() {
      let opt = [];
      if (this.designer.SimpleFieldInfos != undefined) {
        for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
          let info = this.designer.SimpleFieldInfos[i];
          opt.push({
            label: info.name,
            value: info.code
          });
        }
      }

      return opt;
    }
  },
  created() {
    if (!this.button.default && this.button.position == undefined && this.position == 'rowEnd') {
      this.$set(this.button, 'position', 'rowEnd');
    }
  },
  methods: {
    deleteIcon() {
      this.button.style.icon = undefined;
    },
    iconSelected(icon, data) {
      data.icon = icon;
      this.iconVisible = true;
    }
  },
  mounted() {}
};
</script>
