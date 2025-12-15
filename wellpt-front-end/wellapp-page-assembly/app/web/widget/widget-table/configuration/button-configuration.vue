<template>
  <div>
    <a-form-model ref="form" :model="button" :rules="rules">
      <a-form-model-item label="按钮名称" prop="title">
        <a-input v-model="button.title">
          <template slot="addonAfter">
            <WI18nInput :widget="widget" :designer="designer" :code="button.id" :target="button" v-model="button.title" />
          </template>
        </a-input>
      </a-form-model-item>
      <!-- <a-form-model-item label="编码" prop="code">
        <a-input v-model="button.code" />
      </a-form-model-item> -->
      <a-form-model-item label="按钮类型">
        <a-select :options="buttonTypeOptions" v-model="button.style.type" :style="{ width: '100%' }"></a-select>
      </a-form-model-item>
      <a-form-model-item label="隐藏文本">
        <a-switch v-model="button.style.textHidden" />
      </a-form-model-item>
      <!-- <a-form-model-item label="角色可访问">
        <RoleSelect v-model="button.role" />
      </a-form-model-item> -->
      <a-form-model-item v-if="position !== 'row'">
        <template slot="label">
          未选中行提示
          <a-checkbox v-model="button.enableUnselectRowTip" @change="onChangeEnableUnselectRowTip" />
        </template>
        <a-input v-model="button.unselectRowTip" v-show="button.enableUnselectRowTip">
          <template slot="addonAfter">
            <WI18nInput
              :widget="widget"
              :designer="designer"
              :code="button.id + '_unselectRowTip'"
              v-model="button.unselectRowTip"
              :target="button"
            />
          </template>
        </a-input>
      </a-form-model-item>

      <PopconfirmConfiguration :configuration="button" :designer="designer">
        <template slot="popconfirmHelpSlot">
          <a-popover placement="left">
            <template slot="content">
              <div v-if="setDisplayPosition">
                支持通过
                <a-tag>${ 字段 }</a-tag>
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
      <DefaultVisibleConfiguration :designer="designer" :configuration="button" compact>
        <template slot="extraAutoCompleteSelectGroup">
          <a-select-opt-group>
            <span slot="label">
              <a-icon type="appstore" />
              字段
            </span>
            <a-select-option v-for="opt in columnIndexOptions" :key="opt.value" :title="opt.label">
              {{ opt.label }}
              <a-tag style="position: absolute; right: 0px; top: 4px" @click.stop="() => {}">
                {{ opt.value }}
              </a-tag>
            </a-select-option>
          </a-select-opt-group>
          <a-select-opt-group v-if="button.displayPosition == undefined">
            <span slot="label">
              <a-icon type="appstore" />
              其他
            </span>
            <a-select-option v-for="opt in otherBtnVisibleOptions" :key="opt.value" :title="opt.label">
              {{ opt.label }}
            </a-select-option>
          </a-select-opt-group>
        </template>
      </DefaultVisibleConfiguration>
      <div v-if="button.style.type === 'switch' && button.switch != undefined">
        <a-form-model-item label="选中时内容">
          <a-input v-model="button.switch.checkedText" />
        </a-form-model-item>
        <a-form-model-item label="非选中时内容">
          <a-input v-model="button.switch.UnCheckedText" />
        </a-form-model-item>

        <a-form-model-item label="默认选中">
          <a-switch v-model="button.switch.checked" />
        </a-form-model-item>
        <a-form-model-item label="选中条件">
          <a-input-group compact :style="{ display: 'flex' }">
            <a-auto-complete v-model="button.switch.checkedCondition.code" style="width: 100%" allowClear :filterOption="filterOption">
              <template slot="dataSource">
                <a-select-opt-group>
                  <span slot="label">
                    <a-icon type="appstore" />
                    列
                  </span>
                  <a-select-option v-for="opt in columnOptions" :key="opt.value">{{ opt.label }}</a-select-option>
                </a-select-opt-group>
                <a-select-opt-group>
                  <span slot="label">
                    <a-icon type="code" />
                    页面变量
                  </span>
                  <a-select-option v-for="opt in pageVarOptions" :key="opt.value">{{ opt.label }}</a-select-option>
                </a-select-opt-group>
              </template>
            </a-auto-complete>
            <a-select :options="operatorOptions" v-model="button.switch.checkedCondition.operator" :style="{ width: '100px' }" />
            <a-input
              v-model="button.switch.checkedCondition.value"
              v-show="!['true', 'false'].includes(button.switch.checkedCondition.operator)"
            />
          </a-input-group>
        </a-form-model-item>
      </div>
      <!-- <a-form-model-item label="展示条件" v-show="setDisplayPosition">
        <WidgetCodeEditor @save="value => (button.visibleCondition = value)" :value="button.visibleCondition">
          <a-button icon="code">编写代码</a-button>
          <template slot="help">
            <div>入参说明：</div>
            <ul>
              <li>
                <a-tag>row</a-tag>
                : 表示表格内的行数据
              </li>
            </ul>
          </template>
        </WidgetCodeEditor>
      </a-form-model-item> -->

      <a-form-model-item label="展示位置" v-show="setDisplayPosition">
        <a-input-group compact :style="{ display: 'flex' }">
          <a-select
            :options="buttonDisplayPositions"
            :style="{ width: '100%' }"
            v-model="button.displayPosition"
            @change="onChangeDisplayPosition"
          ></a-select>
          <a-select
            placeholder="选中展示的列位置"
            :options="visibleColumnOptions"
            :style="{ width: '100%' }"
            v-model="button.displayColumnId"
            v-if="button.displayPosition === 'hoverRow' || button.displayPosition === 'underRow'"
          ></a-select>
        </a-input-group>
      </a-form-model-item>
      <a-form-model-item label="按钮图标" v-show="button.style.type != 'switch'">
        <WidgetDesignModal
          title="选择图标"
          :zIndex="1000"
          :width="640"
          dialogClass="pt-modal widget-icon-lib-modal"
          :bodyStyle="{ height: '560px' }"
          :maxHeight="560"
          mask
          bodyContainer
        >
          <IconSetBadge v-model="button.style.icon"></IconSetBadge>
          <template slot="content">
            <WidgetIconLib v-model="button.style.icon" />
          </template>
        </WidgetDesignModal>
      </a-form-model-item>

      <!-- <a-form-model-item label="询问确认框">
        <a-switch v-model="button.enableConfirm" @change="onChangeEnableConfirm"></a-switch>
      </a-form-model-item>
      <div v-show="button.enableConfirm">
        <a-form-model-item label="确认框风格">
          <a-radio-group v-model="button.confirmStyleType" button-style="solid" size="small">
            <a-radio-button value="popconfirm">气泡式</a-radio-button>
            <a-radio-button value="confirm">弹窗式</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="询问标题">
          <a-input v-model="button.confirmTitle" allow-clear></a-input>
        </a-form-model-item>
        <a-form-model-item label="询问内容">
          <a-input v-model="button.confirmContent" allow-clear></a-input>
        </a-form-model-item>
      </div> -->
      <BehaviorLogConfiguration
        :widget="widget"
        :configuration="button"
        :designer="designer"
        :extFormulaVariableOptions="extFormulaVariableOptions"
        :extFormulaFunctionLibrary="extFormulaFunctionLibrary"
      />
      <slot name="extraInfo"></slot>
    </a-form-model>
    <WidgetEventHandler
      :eventModel="button.eventHandler"
      :designer="designer"
      :widget="widget"
      :rule="
        editRule && editRule.button && editRule.button[button.id] && editRule.button[button.id].eventHandlerRule
          ? editRule.button[button.id].eventHandlerRule
          : {}
      "
      v-if="
        button.eventHandler != undefined &&
        (editRule.button == undefined || editRule.button[button.id] == undefined || editRule.button[button.id].eventHandlerHidden != true)
      "
    >
      <template slot="urlHelpSlot" v-if="setDisplayPosition">
        <a-popover placement="rightTop" :title="null">
          <template slot="content">
            支持通过
            <a-tag>${ 字段 }</a-tag>
            设值到地址上
          </template>
          <a-button type="link" size="small"><Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon></a-button>
        </a-popover>
      </template>
      <template slot="eventParamValueHelpSlot" v-if="setDisplayPosition">
        支持通过
        <a-tag>${ 字段 }</a-tag>
        取值
      </template>
      <template #extraOptions="{ eventConfig }">
        <EventHandlerCopyFormDataConfiguration
          :eventConfig="eventConfig"
          :widget="widget"
          :sourceColumns="widget.configuration.columns"
        ></EventHandlerCopyFormDataConfiguration>
      </template>
    </WidgetEventHandler>
  </div>
</template>
<script type="text/babel">
import { buttonTypeOptions, buttonShapeOptions, operatorOptions } from '../../commons/constant';
import PopconfirmConfiguration from '../../commons/popconfirm-configuration.vue';
import EventHandlerCopyFormDataConfiguration from '../../commons/event-handler-copy-form-data-configuration.vue';
import { filterSelectOption } from '@framework/vue/utils/function';
export default {
  name: 'ButtonConfiguration',
  mixins: [],
  inject: ['columnIndexOptions'],
  props: {
    widget: Object,
    designer: Object,
    button: Object,
    position: String,
    setDisplayPosition: {
      type: Boolean,
      default: false
    },
    editRule: Object
  },
  components: { PopconfirmConfiguration, EventHandlerCopyFormDataConfiguration },
  data() {
    return {
      options: {},
      jsHooks: [],
      iconVisible: this.button.style.icon != undefined,
      buttonDisplayPositions: [
        // { label: '表格头部', value: 'tableHeader' },
        // { label: '表格底部', value: 'tableFooter' },
        { label: '行末', value: 'rowEnd' },
        { label: '悬浮行上', value: 'hoverRow' },
        { label: '列下', value: 'underRow' }
      ],
      buttonTypeOptions,
      buttonShapeOptions,
      selectedButtonRowKeys: [],
      selectedButtonRows: [],
      fillSourceFormDefinitionColumns: [],
      operatorOptions,
      rules: {
        title: [{ required: true, message: '按钮名称不能为空！', trigger: 'change' }]
        // code: [{ required: true, message: '按钮编码不能为空！', trigger: 'blur' }]
      },
      otherBtnVisibleOptions: [{ label: '选中行数', value: '__TABLE__.selectedRowCount' }, ...(this.button.customBtnVisibleOptions || [])],
      dyformFillColumns: {
        source: [],
        target: []
      },
      dyformFillTableColumns: [
        {
          dataIndex: 'sourceDataIndex',
          width: '100%',
          scopedSlots: { customRender: 'mappingSlot' }
        }
      ]
    };
  },
  computed: {
    extFormulaVariableOptions() {
      let groups = [];
      if (this.position == 'row') {
        let colDataOptions = {
          title: '列字段数据',
          options: []
        };
        for (let i of this.columnIndexOptions) {
          colDataOptions.options.push({
            label: i.label,
            value: `BUTTON_META_DATA.${i.value}`
          });
        }
        groups.push(colDataOptions);
      }
      return groups;
    },

    extFormulaFunctionLibrary() {
      let lib = {
        category: {
          name: '表格函数'
        },
        functions: [
          {
            key: 'getSelectedRowColValueString',
            meta: {
              name: '返回选中行对应的列值',
              description: '将表格选中行对应的列值用分隔符进行返回',
              example: '选中行的列值(列,分隔符) → "值1,值2,..."',
              args: [
                { name: '列', description: '对应表格的列名' },
                { name: '分隔符', description: '分隔符, 默认为逗号' }
              ],
              returnType: 'string'
            }
          }
        ]
      };
      return lib;
    },

    pageVarOptions() {
      // 页面变量路径
      let paths = this.designer.pageVarKeyPaths(),
        options = [];
      for (let i = 0, len = paths.length; i < len; i++) {
        options.push({ label: paths[i], value: paths[i] });
      }
      return options;
    },

    visibleColumnOptions() {
      let options = [];
      for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
        let col = this.widget.configuration.columns[i];
        if (!col.hidden && col.dataIndex != '__serialnumber' && col.dataIndex != '__operation') {
          options.push({ label: this.widget.configuration.columns[i].title, value: this.widget.configuration.columns[i].id });
        }
      }
      return options;
    }
  },
  watch: {
    'widget.configuration.jsModules': {
      deep: true,
      handler(v) {
        this.jsModuleChanged(v);
      }
    }
  },
  beforeCreate() {},
  created() {
    if (this.button.role == undefined) {
      this.$set(this.button, 'role', []);
    }
    if (this.button.style && this.button.style.type == undefined) {
      this.$set(this.button.style, 'type', 'default');
    }
  },
  beforeMount() {
    if (this.widget.configuration.jsModules != undefined) {
      this.jsModuleChanged(this.widget.configuration.jsModules);
    }
  },
  mounted() {},
  methods: {
    // onChangeEnableConfirm() {
    //   if (this.button.enableConfirm && this.button.confirmStyleType == undefined) {
    //     this.$set(this.button, 'confirmStyleType', 'popconfirm');
    //   }
    // },
    filterOption(input, option) {
      return (
        option.componentOptions.tag != 'a-select-opt-group' &&
        (option.componentOptions.children[0].text.toUpperCase().indexOf(input.toUpperCase()) >= 0 ||
          (option.key && option.key.indexOf(input.toUpperCase()) >= 0))
      );
    },
    onChangeDisplayPosition(v, option) {
      // this.button.displayPositionName = v ? option.componentOptions.children[0].text.trim() : undefined;
    },

    jsModuleChanged(v) {
      let options = [];
      for (let i = 0, len = v.length; i < len; i++) {
        let _d = this.__developScript[v[i].key];
        if (_d) {
          try {
            let meta = _d.default.prototype.META;
            if (meta && meta.hook) {
              for (let h in meta.hook) {
                options.push({ label: meta.hook[h], value: `${v[i].key}.${h}` });
              }
            }
          } catch (error) {
            console.error(`二开脚本 ${v[i].key} 解析脚本数据异常: `, error);
          }
        }
      }
      this.jsHooks = options;
    },

    validate() {
      return this.$refs.form.validate();
    },
    onChangeEnableUnselectRowTip() {
      if (!this.button.hasOwnProperty('unselectRowTip') && this.button.enableUnselectRowTip) {
        this.$set(this.button, 'unselectRowTip', '请选择行');
      }
    }
  }
};
</script>
