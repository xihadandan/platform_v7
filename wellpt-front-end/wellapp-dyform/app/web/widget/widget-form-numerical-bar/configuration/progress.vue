<template>
  <a-collapse :bordered="false" expandIconPosition="right">
    <a-collapse-panel key="component_show_properties" header="进度条默认设置">
      <!-- <a-form-model-item label="手动输入">
        <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.progress.showInput" />
      </a-form-model-item> -->
      <template v-if="designer.terminalType == 'mobile'">
        <a-alert message="移动端暂不支持内置变量颜色" type="info" show-icon />
      </template>
      <a-form-model-item label="类型">
        <a-select
          :options="typeOptions"
          v-model="widget.configuration.progress.type"
          :getPopupContainer="getPopupContainerByPs()"
          :dropdownClassName="getDropdownClassName()"
        ></a-select>
      </a-form-model-item>
      <WidgetNumericalBarProgressBarColorConfiguration
        v-model="widget.configuration.progress"
      ></WidgetNumericalBarProgressBarColorConfiguration>
      <a-form-model-item label="线段宽度">
        <a-input-number v-model="widget.configuration.progress.strokeWidth" :min="1" />
        px
      </a-form-model-item>
      <a-form-model-item label="圆角">
        <a-switch v-model="widget.configuration.progress.round"></a-switch>
      </a-form-model-item>
      <template v-if="widget.configuration.progress.type == 'circle' || widget.configuration.progress.type == 'dashboard'">
        <a-form-model-item label="画布宽度">
          <a-input-number v-model="widget.configuration.progress.width" :min="1" />
          px
        </a-form-model-item>
      </template>
      <template v-if="widget.configuration.progress.type == 'dashboard'">
        <a-form-model-item label="缺口角度">
          <a-input-number v-model="widget.configuration.progress.gapDegree" :min="0" />
        </a-form-model-item>
        <a-form-model-item label="缺口位置">
          <a-select
            :options="gapPositionOptions"
            v-model="widget.configuration.progress.gapPosition"
            :getPopupContainer="getPopupContainerByPs()"
            :dropdownClassName="getDropdownClassName()"
          ></a-select>
        </a-form-model-item>
      </template>
    </a-collapse-panel>
    <a-collapse-panel key="component_text_properties" header="进度数值及内容设置">
      <a-form-model-item label="显示进度数值或状态图标">
        <a-switch v-model="widget.configuration.progress.showInfo" checked-children="是" un-checked-children="否"></a-switch>
      </a-form-model-item>
      <template v-if="widget.configuration.progress.showInfo">
        <a-form-model-item label="进度数值样式">
          <a-radio-group size="small" v-model="widget.configuration.progress.defaultTextStyle" button-style="solid">
            <a-radio-button value="default">默认</a-radio-button>
            <a-radio-button value="define">自定义</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <template v-if="widget.configuration.progress.defaultTextStyle == 'define'">
          <ColorSelectConfiguration
            label="进度数值颜色"
            v-model="widget.configuration.progress"
            :onlyValue="true"
            colorField="textColor"
            radioSize="small"
            radioStyle="solid"
          ></ColorSelectConfiguration>
          <a-form-model-item label="进度数值字体大小">
            <a-input-number v-model="widget.configuration.progress.fontSize" :min="12" />
            px
          </a-form-model-item>
          <a-form-model-item label="进度数值字重大小">
            <a-select
              :options="fontWeightOptions"
              v-model="widget.configuration.progress.fontWeight"
              allowClear
              :getPopupContainer="getPopupContainerByPs()"
              :dropdownClassName="getDropdownClassName()"
            ></a-select>
          </a-form-model-item>
        </template>
        <a-form-model-item>
          <template slot="label">
            <a-tooltip placement="bottomRight">
              <template slot="title">
                <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                  <li>内容建议不超过三个字符，三个以上会超出边框范围</li>
                </ul>
              </template>
              使用内容模板
              <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
            </a-tooltip>
          </template>
          <a-switch v-model="widget.configuration.progress.isUseFormat" checked-children="是" un-checked-children="否"></a-switch>
        </a-form-model-item>
        <a-form-model-item label="内容的模板函数" v-if="widget.configuration.progress.isUseFormat">
          <WidgetCodeEditor v-model="widget.configuration.progress.format" lang="html" width="500px" height="500px" :hideError="true">
            <div slot="help">
              支持参数: 百分比
              <a-tag @click="e => onClickCopy(e, 'percent')">percent</a-tag>
              <!-- , 已完成的分段百分比
            <a-tag>successPercent</a-tag> -->
              , 状态
              <a-tag @click="e => onClickCopy(e, 'status')">status</a-tag>
              , form对象
              <a-tag @click="e => onClickCopy(e, 'form')">form</a-tag>
              , form对象内的表单数据
              <a-tag @click="e => onClickCopy(e, 'formData')">formData</a-tag>
            </div>
            <a-button icon="code">编写代码</a-button>
          </WidgetCodeEditor>
        </a-form-model-item>
      </template>
    </a-collapse-panel>
    <a-collapse-panel key="component_status_properties" header="状态设置">
      <a-form-model-item v-if="false">
        <template slot="label">
          <a-tooltip placement="bottomRight">
            <template slot="title">
              <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                <li>选择字段的组件都是数字输入组件</li>
                <li>值等于（已完成数/全部数）*100%</li>
              </ul>
            </template>
            显示已完成的分段
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
          </a-tooltip>
        </template>
        <a-switch v-model="widget.configuration.progress.showSuccessPercent" checked-children="是" un-checked-children="否"></a-switch>
      </a-form-model-item>
      <template v-if="widget.configuration.progress.showSuccessPercent && false">
        <a-form-model-item label="已完成数字段">
          <a-select
            v-model="widget.configuration.progress.successField"
            placeholder="已完成数字段"
            :allowClear="true"
            :options="formFieldOptions"
            :style="{ width: '100%' }"
            :getPopupContainer="getPopupContainerByPs()"
            :dropdownClassName="getDropdownClassName()"
          ></a-select>
        </a-form-model-item>
        <a-form-model-item label="总数字段">
          <a-select
            v-model="widget.configuration.progress.totalField"
            placeholder="总数字段"
            :allowClear="true"
            :options="formFieldOptions"
            :style="{ width: '100%' }"
            :getPopupContainer="getPopupContainerByPs()"
            :dropdownClassName="getDropdownClassName()"
          ></a-select>
        </a-form-model-item>
      </template>
      <a-form-model-item>
        <template slot="label">
          <a-tooltip placement="bottomRight">
            <template slot="title">
              <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                <li>自定义，即根据状态选择显示进度条状态</li>
                <li>
                  根据状态数值：即根据配置的【成功/预警】值显示进度条状态。运算符为区间时，前面一个数应小于后面一个数；配置时请合理设置数值；存在冲突时，会先判断成功状态
                </li>
              </ul>
            </template>
            状态显示
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
          </a-tooltip>
        </template>
        <a-radio-group size="small" v-model="widget.configuration.progress.statusShowType" button-style="solid">
          <a-radio-button value="define">自定义</a-radio-button>
          <a-radio-button value="change">根据状态数值</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <template v-if="widget.configuration.progress.statusShowType == 'define'">
        <a-form-model-item label="状态">
          <a-select
            :options="statusOptions"
            v-model="widget.configuration.progress.status"
            allowClear
            :getPopupContainer="getPopupContainerByPs()"
            :dropdownClassName="getDropdownClassName()"
          ></a-select>
        </a-form-model-item>
      </template>
      <template v-else>
        <a-form-model-item label="成功值">
          <a-select
            :options="operateOptions"
            v-model="widget.configuration.progress.success.operator"
            :getPopupContainer="getPopupContainerByPs()"
            :dropdownClassName="getDropdownClassName()"
          ></a-select>
          <a-input-number v-model="widget.configuration.progress.success.number" :min="0" />
          %
          <template
            v-if="widget.configuration.progress.success.operator && widget.configuration.progress.success.operator.indexOf('range') > -1"
          >
            <a-input-number style="margin-left: 8px" v-model="widget.configuration.progress.success.number1" :min="0" />
            %
          </template>
        </a-form-model-item>
        <a-form-model-item label="预警值">
          <a-select
            :options="operateOptions"
            v-model="widget.configuration.progress.exception.operator"
            :getPopupContainer="getPopupContainerByPs()"
            :dropdownClassName="getDropdownClassName()"
          ></a-select>
          <a-input-number v-model="widget.configuration.progress.exception.number" :min="0" />
          %
          <template
            v-if="
              widget.configuration.progress.exception.operator && widget.configuration.progress.exception.operator.indexOf('range') > -1
            "
          >
            <a-input-number style="margin-left: 8px" v-model="widget.configuration.progress.exception.number1" :min="0" />
            %
          </template>
        </a-form-model-item>
      </template>
    </a-collapse-panel>
    <a-collapse-panel key="component_success_properties">
      <template slot="header">
        <a-tooltip placement="bottomRight">
          <template slot="title">
            <ul style="padding-inline-start: 20px; margin-block-end: 0px">
              <li>进度数值样式属于自定义时，状态的进度数值颜色才会生效</li>
            </ul>
          </template>
          成功状态设置
          <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" style="font-size: inherit; font-weight: normal; color: inherit" />
        </a-tooltip>
      </template>
      <WidgetNumericalBarProgressBarColorConfiguration
        :value="widget.configuration.progress.success"
      ></WidgetNumericalBarProgressBarColorConfiguration>
      <ColorSelectConfiguration
        label="进度数值颜色"
        v-model="widget.configuration.progress.success"
        :onlyValue="true"
        colorField="textColor"
        radioSize="small"
        radioStyle="solid"
      ></ColorSelectConfiguration>
    </a-collapse-panel>
    <a-collapse-panel key="component_exception_properties">
      <template slot="header">
        <a-tooltip placement="bottomRight">
          <template slot="title">
            <ul style="padding-inline-start: 20px; margin-block-end: 0px">
              <li>进度数值样式属于自定义时，状态的进度数值颜色才会生效</li>
            </ul>
          </template>
          预警状态设置
          <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" style="font-size: inherit; font-weight: normal; color: inherit" />
        </a-tooltip>
      </template>
      <WidgetNumericalBarProgressBarColorConfiguration
        :value="widget.configuration.progress.exception"
      ></WidgetNumericalBarProgressBarColorConfiguration>
      <ColorSelectConfiguration
        label="进度数值颜色"
        v-model="widget.configuration.progress.exception"
        :onlyValue="true"
        colorField="textColor"
        radioSize="small"
        radioStyle="solid"
      ></ColorSelectConfiguration>
    </a-collapse-panel>
    <a-collapse-panel key="component_mode_properties" header="其他设置">
      <a-form-model-item label="显示提示信息">
        <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.progress.isTooltips" />
      </a-form-model-item>
      <a-form-model-item label="提示内容" v-if="widget.configuration.progress.isTooltips">
        <WidgetCodeEditor v-model="widget.configuration.progress.tooltip" lang="html" width="500px" height="500px" :hideError="true">
          <div slot="help">
            支持参数: 百分比
            <a-tag @click="e => onClickCopy(e, 'percent')">percent</a-tag>
            <!-- , 已完成的分段百分比
            <a-tag>successPercent</a-tag> -->
            , 状态
            <a-tag @click="e => onClickCopy(e, 'status')">status</a-tag>
            , form对象
            <a-tag @click="e => onClickCopy(e, 'form')">form</a-tag>
            , form对象内的表单数据
            <a-tag @click="e => onClickCopy(e, 'formData')">formData</a-tag>
          </div>
          <a-button icon="code">编写代码</a-button>
        </WidgetCodeEditor>
      </a-form-model-item>
    </a-collapse-panel>
  </a-collapse>
</template>

<script>
import ColorSelectConfiguration from '@pageAssembly/app/web/widget/commons/color-select-configuration.vue';
import WidgetNumericalBarProgressBarColorConfiguration from './progressBarColor.vue';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
import { copyToClipboard } from '@framework/vue/utils/util';
export default {
  name: 'WidgetNumericalBarProgressConfiguration',
  props: {
    widget: Object,
    designer: Object
  },
  components: { ColorSelectConfiguration, WidgetNumericalBarProgressBarColorConfiguration },
  provide() {},
  data() {
    return {
      typeOptions: [
        { label: '线性', value: 'line' },
        { label: '圆形', value: 'circle' },
        { label: '仪表盘', value: 'dashboard' }
      ],
      gapPositionOptions: [
        { label: '上方', value: 'top' },
        { label: '下方', value: 'bottom' },
        { label: '左侧', value: 'left' },
        { label: '右侧', value: 'right' }
      ],
      fontWeightOptions: [
        { label: '默认值', value: 'normal' },
        { label: '粗体', value: 'bold' },
        // { label: '更粗', value: 'bolder' },
        { label: '更细', value: 'lighter' }
      ],
      statusOptions: [
        { label: '默认', value: 'normal' },
        { label: '成功', value: 'success' },
        { label: '预警', value: 'exception' },
        { label: '活动（仅线性有效）', value: 'active' }
      ],
      operateOptions: [
        { label: '大于', value: '>' },
        { label: '大于等于', value: '>=' },
        { label: '小于', value: '<' },
        { label: '小于等于', value: '<=' },
        { label: '区间', value: 'range' },
        { label: '区间（左闭右开）', value: 'range>=' },
        { label: '区间（左开右闭）', value: 'range<=' },
        { label: '区间（闭区间，含等于）', value: 'range==' },
        { label: '区间（小于或大于）', value: 'range<|>' },
        { label: '区间（小于闭或大于）', value: 'range<=|>' },
        { label: '区间（小于或大于闭）', value: 'range<|>=' },
        { label: '区间（小于或大于，含等于）', value: 'range<=|>=' }
      ]
    };
  },
  computed: {
    // 表单里的数字输入框
    formFieldOptions() {
      let opt = [];
      if (this.designer.FieldWidgets && this.designer.FieldWidgets.length) {
        for (let k = 0, len = this.designer.FieldWidgets.length; k < len; k++) {
          let field = this.designer.FieldWidgets[k];
          if (field.configuration.code && field.id != this.widget.id && field.wtype == 'WidgetFormInputNumber') {
            opt.push({
              id: field.id,
              label: field.configuration.name || field.configuration.code,
              value: field.configuration.code
            });
          }
        }
      }
      return opt;
    }
  },
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
    onClickCopy(e, text) {
      let _this = this;
      copyToClipboard(text, e, function (success) {
        if (success) {
          // message不支持修改样式，代码编辑组件弹框widget-code-editor层级为2000，导致message提示框显示在遮罩下面
          _this.$message.success({
            content: '已复制'
          });
        }
      });
    }
  }
};
</script>
