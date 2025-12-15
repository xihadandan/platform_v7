<template>
  <span :data-id="row['$$id']" class="widget-subform-table-cell">
    <template v-if="option.enableColRenderSlot && option.colRenderTemplateName">
      <component v-if="option.colRenderTemplateName" :is="option.colRenderTemplateName">
        <template slot="default">
          <template v-if="!forceRenderWtypes.includes(widget.wtype)">
            <template v-if="widget.wtype == 'WidgetFormRichTextEditor'">
              <label
                v-html="vLabel"
                v-show="showAsLabel"
                :title="regexHtmlToText(vLabel)"
                :isSubformCell="true"
                :class="[option.ellipsis ? 'overflow-ellipsis' : '']"
              ></label>
            </template>
            <template v-else>
              <label :title="vLabel" v-show="showAsLabel" :isSubformCell="true" :class="[option.ellipsis ? 'overflow-ellipsis' : '']">
                {{ vLabel }}
              </label>
            </template>
            <div
              style="color: var(--w-input-error-word-color)"
              v-if="showAsLabel && validateStatus == 'error' && validateMessage != undefined"
            >
              {{ validateMessage }}
            </div>
          </template>
          <div v-show="forceRenderWtypes.includes(widget.wtype) || (widgetSubform.configuration.rowEditMode == 'cell' && !showAsLabel)">
            <component
              :is="widget.wtype"
              :widget="widget"
              :form="form"
              :isSubformCell="true"
              @change="$evt => onCellComponentChange($evt)"
              @mounted="$evt => onCellComponentMounted($evt)"
              @valueChange="$evt => onValueChange($evt)"
              @validateStatusChange="validateStatusChange"
              ref="widget"
            />
          </div>
        </template>
      </component>
    </template>
    <template v-else>
      <template v-if="!forceRenderWtypes.includes(widget.wtype)">
        <template v-if="widget.wtype == 'WidgetFormRichTextEditor'">
          <label
            :title="regexHtmlToText(vLabel)"
            v-html="vLabel"
            v-show="showAsLabel"
            :isSubformCell="true"
            :class="[option.ellipsis ? 'overflow-ellipsis' : '']"
          ></label>
        </template>
        <template v-else>
          <label :title="vLabel" v-show="showAsLabel" :isSubformCell="true" :class="[option.ellipsis ? 'overflow-ellipsis' : '']">
            {{ vLabel }}
          </label>
        </template>
        <div style="color: var(--w-input-error-word-color)" v-if="showAsLabel && validateStatus == 'error' && validateMessage != undefined">
          {{ validateMessage }}
        </div>
      </template>
      <div v-show="forceRenderWtypes.includes(widget.wtype) || (widgetSubform.configuration.rowEditMode == 'cell' && !showAsLabel)">
        <component
          :is="widget.wtype"
          :widget="widget"
          :form="form"
          :isSubformCell="true"
          @change="$evt => onCellComponentChange($evt)"
          @mounted="$evt => onCellComponentMounted($evt)"
          @valueChange="$evt => onValueChange($evt)"
          @validateStatusChange="validateStatusChange"
          @stateChange="onStateChange"
          ref="widget"
        />
      </div>
    </template>

    <WidgetTableButtons
      v-if="option.button != undefined"
      :button="option.button"
      :meta="row"
      :visibleJudgementData="vTableButtonVisibleJudgementData"
      size="small"
      buttonDefaultType="link"
    ></WidgetTableButtons>
  </span>
</template>
<style lang="less"></style>
<script type="text/babel">
import cellMinxin from './cell.minxin';
export default {
  name: 'SubformTableCell',
  mixins: [cellMinxin],
  provide() {
    return {};
  },
  components: {},
  computed: {
    showAsLabel() {
      return this.row.__MODEL__[`${this.dataIndex}_ShowAsLabel`];
    },
    vLabel() {
      if (this.widget.wtype == 'WidgetFormDatePicker' && this.widget.subtype == 'Range') {
        // 日期范围组件显示值为两个字段的合并
        if (this.form.formData[this.dataIndex]) {
          return `${this.form.formData[this.dataIndex]} ~ ${this.form.formData[this.widget.configuration.endDateField]}`;
        }
      }
      return this.row.__MODEL__.label[this.dataIndex] || this.row[this.dataIndex];
    },
    dataIndex() {
      return this.widget.configuration.code;
    }
  },
  data() {
    return {
      forceRenderWtypes: ['WidgetFormOrgSelect', 'WidgetFormFileUpload', 'WidgetFormSetIcon'],
      validateStatus: undefined,
      validateMessage: undefined
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    if (this.$parent.$parent && this.form.vueInstance == undefined) {
      this.form.vueInstance = this.$parent.$parent; // 绑定事件派发的实例对象为当前表格行，使得派发事件在从表行级表单域内派发
    }
  },
  mounted() {
    if (this.showAsLabel) {
      // 文本状态情况下，附件上传不可编辑（不展示上传按钮）
      // 文本状态情况下，不可编辑
      if (this.forceRenderWtypes.includes(this.widget.wtype)) {
        this.$refs.widget.setEditable(false);
      }
    }
    if (this.row.$$id.startsWith('new_') && this.form.formData[this.dataIndex] != undefined && this.form.formData[this.dataIndex] !== '') {
      this.onValueChange({
        newValue: this.form.formData[this.dataIndex],
        oldValue: undefined
      });
    }
  },
  methods: {
    onCellComponentChange(evt) {
      let $wgt = this.$refs.widget;
      let displayValue = $wgt.displayValue(undefined, false);
      // 设置单元格显示值
      this.$set(this.row.__MODEL__.label, this.dataIndex, displayValue);
      this.$set(this.row, this.dataIndex, $wgt.getValue()); //设置真实值

      this.$emit('change', evt);
    },
    onCellComponentMounted(evt) {
      if (this.formDefaultData[this.dataIndex]) {
        if (this.row[this.dataIndex] == this.formDefaultData[this.dataIndex]) {
          this.$refs.widget.setValue(this.formDefaultData[this.dataIndex]);
        }
      }
      this.$emit('mounted', evt);
    },
    validateStatusChange(status, msg) {
      this.validateStatus = status;
      this.validateMessage = msg;
    },
    onStateChange({ editable, hidden, displayAsLabel, readonly }) {
      this.row.__MODEL__[`${this.dataIndex}_ShowAsLabel`] = displayAsLabel || readonly;
      let rule = this.form.formElementRules[this.widget.id];
      if (rule == undefined) {
        this.form.formElementRules[this.widget.id] = { editable, displayAsLabel, hidden };
      } else {
        rule.editable = editable;
        rule.displayAsLabel = displayAsLabel;
        rule.hidden = hidden;
      }
    },
    regexHtmlToText(html) {
      if (html) {
        const text = html.replace(/(<img[^>]*>)|<[^>]+>/g, (match, imgTag) => (imgTag ? '[图片]' : ''));
        return text;
      }
      return '';
    }
  },
  watch: {
    showAsLabel: {
      handler(val) {
        let rule = this.form.formElementRules[this.widget.id];
        // 文本状态情况下，附件上传不可编辑（不展示上传按钮）,可编辑状态显示上传按钮
        // 文本状态情况下，不可编辑
        if (this.forceRenderWtypes.includes(this.widget.wtype)) {
          this.$refs.widget.setEditable(!val);
        } else if (this.$refs.widget) {
          if ((!val && (rule == undefined || (rule && rule.editable))) || val) {
            // 仅当规则未设置或规则允许编辑时，才允许切换编辑状态
            this.$refs.widget.editable = !val;
            this.$refs.widget.displayAsLabel = val;
          }
        }
      }
    }
  }
};
</script>
