<!-- 仅用于显示状态 -->
<template>
  <view :data-id="row['$$id']" class="widget-subform-table-cell">
    <!-- 自定义,暂未实现 -->
    <!-- <template v-if="false && option.enableColRenderSlot && option.colRenderTemplateName">
      <component v-if="option.colRenderTemplateName" :is="option.colRenderTemplateName">
        <template slot="default">
          <template v-if="!forceRenderWtypes.includes(widget.wtype)">
            <template v-if="widget.wtype == 'WidgetFormRichTextEditor'">
              <text
                v-html="vLabel"
                v-show="showAsLabel"
                :isSubformCell="true"
                :class="[option.ellipsis ? 'overflow-ellipsis' : '']"
              ></text>
            </template>
            <template v-else>
              <text v-show="showAsLabel" :isSubformCell="true" :class="[option.ellipsis ? 'overflow-ellipsis' : '']">
                {{ vLabel }}
              </text>
            </template>
            <view
              style="color: var(--w-input-error-word-color)"
              v-if="showAsLabel && validateStatus == 'error' && validateMessage != undefined"
            >
              {{ validateMessage }}
            </view>
          </template>
          <view
            v-show="
              forceRenderWtypes.includes(widget.wtype) ||
              (widgetSubform.configuration.rowEditMode == 'cell' && !showAsLabel)
            "
          >
            <widget
              :widget="widget"
              :form="form"
              :isSubformCell="true"
              :formData="form.formData"
              :formModelItemProp="rowKey + '_' + dataIndex"
              @change="($evt) => onCellComponentChange($evt)"
              @mounted="($evt) => onCellComponentMounted($evt)"
              @valueChange="($evt) => onValueChange($evt)"
              @validateStatusChange="validateStatusChange"
              ref="widget"
            />
          </view>
        </template>
      </component>
    </template> -->
    <template>
      <template v-if="!forceRenderWtypes.includes(widget.wtype)">
        <template v-if="widget.wtype == 'WidgetFormRichTextEditor'">
          <label
            v-html="vLabel"
            v-show="showAsLabel"
            :isSubformCell="true"
            :class="[option.ellipsis ? 'overflow-ellipsis' : '']"
          ></label>
        </template>
        <template v-else>
          <label v-show="showAsLabel" :isSubformCell="true" :class="[option.ellipsis ? 'overflow-ellipsis' : '']">
            {{ vLabel }}
          </label>
        </template>
        <view
          style="color: var(--w-input-error-word-color)"
          v-if="showAsLabel && validateStatus == 'error' && validateMessage != undefined"
        >
          {{ validateMessage }}
        </view>
      </template>
      <view v-show="forceRenderWtypes.includes(widget.wtype)">
        <widget
          :widget="widget"
          :form="form"
          :formData="form.formData"
          :isSubformCell="true"
          :formModelItemProp="[form.formUuid, rowKey, dataIndex]"
          @change="($evt) => onCellComponentChange($evt)"
          @mounted="($evt) => onCellComponentMounted($evt)"
          @validateStatusChange="validateStatusChange"
          ref="widget"
        />
        <!-- 用于当前值变化时触发 -->
        <text v-show="false">{{ vLabel }}</text>
      </view>
    </template>
    <view v-if="option.button != undefined">
      <SubformButton
        :button="option.button"
        @button-click="onSubformButtonClick"
        :meta="row"
        :visibleJudgementData="visibleJudgementData"
        size="small"
        :parentWidget="parentWidget"
      />
    </view>
  </view>
</template>
<style lang="less"></style>
<script type="text/babel">
import SubformButton from "./subform-button.vue";
import cellMinxin from "./cell.minxin";
export default {
  name: "SubformTableCell",
  mixins: [cellMinxin],
  provide() {
    return {};
  },
  components: { SubformButton },
  computed: {
    showAsLabel() {
      return true; //this.row.__MODEL__[`${this.dataIndex}_ShowAsLabel`];
    },
    vLabel() {
      let value = this.row[this.dataIndex];
      let $wgt = this.$refs.widget && this.$refs.widget.$refs.widget;
      if ($wgt && $wgt.$refs[this.dataIndex]) {
        // 更改uniFormItem组件值
        $wgt.$refs[this.dataIndex].itemSetValue(value);
      }
      if (this.widget.wtype == "WidgetFormFileUpload") {
        if ($wgt) {
          $wgt.setValue(value);
        }
        return value;
      } else if (this.widget.wtype == "WidgetFormDatePicker" && this.widget.subtype == "Range") {
        // 日期范围组件显示值为两个字段的合并
        if (this.row[this.dataIndex]) {
          return `${this.row[this.dataIndex]} ~ ${this.row[this.widget.configuration.endDateField]}`;
        }
      }
      return this.row.__MODEL__.label[this.dataIndex] || this.row[this.dataIndex] || "-";
    },
    dataIndex() {
      return this.widget.configuration.code;
    },
  },
  data() {
    return {
      forceRenderWtypes: ["WidgetFormOrgSelect", "WidgetFormFileUpload"],
      validateStatus: undefined,
      validateMessage: undefined,
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    // this.form.vueInstance = this.$parent.$parent; // 绑定事件派发的实例对象为当前表格行，使得派发事件在从表行级表单域内派发
  },
  mounted() {},
  methods: {
    onCellComponentChange(evt) {
      let $wgt = this.$refs.widget && this.$refs.widget.$refs.widget;
      if ($wgt) {
        let displayValue = $wgt.displayValue(undefined, false);
        // 设置单元格显示值
        this.$set(this.row.__MODEL__.label, this.dataIndex, displayValue);
        this.$set(this.row, this.dataIndex, $wgt.getValue()); //设置真实值
      }

      this.$emit("change", evt);
    },
    onCellComponentMounted(evt) {
      this.form.$fieldset[this.dataIndex] = evt.$vue;
      this.$emit("mounted", evt);
    },
    validateStatusChange(status, msg) {
      this.validateStatus = status;
      this.validateMessage = msg;
    },
    onSubformButtonClick(e, button) {
      this.$emit("button-click", e, button);
    },
    // 设置主表单uniForm的FormData值
    setMainFormDataValue(value) {
      let $wgt = this.$refs.widget && this.$refs.widget.$refs.widget;
      if ($wgt && $wgt.$refs[this.dataIndex]) {
        // 更改uniFormItem组件值
        $wgt.$refs[this.dataIndex].itemSetValue(value);
      }
    },
  },
};
</script>
