<template>
  <a-form-model-item :style="itemStyle" v-if="vShow">
    <template slot="label" v-if="widget.configuration.titleHidden !== true">
      {{ fieldName }}
      <template v-if="widget.configuration.enableTooltip && widget.configuration.tooltip != undefined">
        <a-tooltip
          v-if="widget.configuration.tooltipDisplayType == 'tooltip'"
          :title="$t(widget.id + '_tooltip', widget.configuration.tooltip)"
        >
          <a-button size="small" icon="info-circle" type="link" />
        </a-tooltip>
        <a-popover v-else trigger="hover" :content="$t(widget.id + '_tooltip', widget.configuration.tooltip)" :title="null">
          <a-button size="small" icon="info-circle" type="link" />
        </a-popover>
      </template>
    </template>
    <template v-if="editable || readonly">
      <a-switch v-model="checked" @change="onChange" :disabled="designMode ? false : disable || readonly">
        <template v-if="widget.configuration.switchStyle == 'label'">
          <label slot="checkedChildren">{{ $t('checkedLabel', widget.configuration.checkedLabel) }}</label>
          <label slot="unCheckedChildren">{{ $t('uncheckedLabel', widget.configuration.uncheckedLabel) }}</label>
        </template>
      </a-switch>
    </template>
    <span v-else class="textonly">
      {{ displayValue() }}
    </span>
  </a-form-model-item>
</template>

<script type="text/babel">
import formElementMinxin from './form-element.mixin';
import formMixin from '@dyform/app/web/widget/mixin/form-common.mixin';

export default {
  name: 'WidgetSwitch',
  mixins: [formElementMinxin, formMixin],
  props: {},
  data() {
    return { checked: this.form != undefined && this.form[this.widget.configuration.code] === this.widget.configuration.checkedValue };
  },
  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {
    onChange(checked) {
      this.form[this.widget.configuration.code] = this.widget.configuration[checked ? 'checkedValue' : 'uncheckedValue'];
    },
    displayValue(v, template) {
      let checked = this.checked;
      if (v != undefined) {
        checked = this.widget.configuration.checkedValue == v;
      }
      if (this.configuration.uneditableDisplayState == 'label') {
        // 不可编辑模式 纯文本
        if (this.configuration.switchStyle != 'label') {
          return checked ? this.configuration.checkedValue : this.configuration.uncheckedValue;
        }
      }
      let label = this.widget.configuration.switchStyle == 'label';
      if (label) {
        return checked ? this.checkedLabel : this.uncheckedLabel;
      } else {
        return {
          label: checked ? this.checkedLabel : this.uncheckedLabel,
          template: checked
            ? `<a-icon type="${this.widget.configuration.checkedIcon}" />`
            : `<a-icon type="${this.widget.configuration.uncheckedIcon}" />`
        };
      }
    }
  },
  mounted() {
    let checkedValue = this.widget.configuration.checkedValue,
      _this = this;
    // 开关值默认，未选中
    if (this.form != undefined && this.form[this.widget.configuration.code] === undefined) {
      _this.form[this.widget.configuration.code] = this.widget.configuration.uncheckedValue;
      _this.checked = false;
    }
    this.$watch('form.' + this.widget.configuration.code, function (newVal, oldVal) {
      _this.checked = newVal === checkedValue;
    });
  },
  beforeUpdate() {},
  beforeMount() {},
  watch: {}
};
</script>
