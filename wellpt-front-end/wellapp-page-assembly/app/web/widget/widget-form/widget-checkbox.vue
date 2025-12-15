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
      <template v-if="checkboxOptions.length > 1">
        <a-checkbox-group v-model="value" @change="onChange" :disabled="designMode ? false : disable || readonly">
          <a-checkbox v-for="(opt, i) in checkboxOptions" :key="'checkbox-' + widget.id + i" :style="checkboxStyle" :value="opt.value">
            {{ opt.label }}
          </a-checkbox>
        </a-checkbox-group>
      </template>
      <template v-else-if="checkboxOptions.length === 1">
        <a-checkbox v-model="form[widget.configuration.code]" :disabled="designMode ? false : disable || readonly">
          {{ checkboxOptions[0].label }}
        </a-checkbox>
      </template>
    </template>
    <template v-else>
      <span v-if="checkedLabel" class="textonly">{{ checkedLabel }}</span>
    </template>
  </a-form-model-item>
</template>

<script type="text/babel">
import formElementMinxin from './form-element.mixin';
import formMixin from '@dyform/app/web/widget/mixin/form-common.mixin';

export default {
  name: 'WidgetCheckbox',
  mixins: [formElementMinxin, formMixin],
  props: {
    defaultCheckOptions: Array
  },
  data() {
    let checkboxOptions = this.defaultCheckOptions || [];
    return { checkboxOptions, checkedLabels: [], value: null, indeterminate: true, checkAll: false };
  },
  beforeCreate() {},
  components: {},
  computed: {
    checkedLabel() {
      return this.checkedLabels.join(this.widget.configuration.tokenSeparators);
    },
    checkboxStyle() {
      let style = {
        display: this.widget.configuration.layout == 'vertical' ? 'block' : 'inline-block',
        width:
          this.widget.configuration.layout == 'horizontal' && this.widget.configuration.alignType == 'fixedWidth'
            ? this.widget.configuration.itemWidth + 'px'
            : 'auto'
      };
      if (style.display === 'block' || style.width != 'auto') {
        style.margin = '0px';
      }
      return style;
    }
  },
  created() {},
  methods: {
    init() {
      this.fetchCheckboxOptions();
    },
    setValue(v) {
      this.value = v;
      if (typeof v === 'string') {
        this.value = v.split(this.widget.configuration.tokenSeparators);
      }
      if (v == undefined && !this.designMode) {
        this.value = this.widget.configuration.defaultValue
          ? this.widget.configuration.defaultValue.split(this.widget.configuration.tokenSeparators)
          : [];
      }
      this.form[this.widget.configuration.code] = Array.isArray(v) ? v.join(this.widget.configuration.tokenSeparators) : v;
      this.setCheckedLabels();
      this.clearValidate();
    },
    //重新加载备选项
    refetchOption() {
      this.fetchCheckboxOptions();
    },
    fetchCheckboxOptions() {
      // 常量
      if (this.widget.configuration.options.type == 'selfDefine' && this.widget.configuration.options.defineOptions.length) {
        this.checkboxOptions = this.widget.configuration.options.defineOptions;
        for (let o of this.checkboxOptions) {
          o.label = this.$t(o.id, o.label);
        }
        this.setCheckedLabels();
      } else if (this.widget.configuration.options.type == 'dataSource') {
        this.fetchCheckboxOptionByDataSource(this.widget.configuration.options);
      } else if (this.widget.configuration.options.type == 'dataDictionary') {
        this.fetchCheckboxOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid);
      } else if (this.widget.configuration.options.type == 'dataModel') {
        this.fetchCheckboxOptionByDataSource({
          ...this.widget.configuration.options,
          loadDataUrl: `/proxy/api/dm/loadData/${this.widget.configuration.options.dataModelUuid}`,
          loadDataCountUrl: `/proxy/api/dm/loadDataCount/${this.widget.configuration.options.dataModelUuid}`
        });
      } else if (this.widget.configuration.options.type == 'apiLinkService') {
        this.fetchCheckboxOptionByApiLink(this.widget.configuration.options);
      }
    },
    fetchCheckboxOptionByApiLink(options) {
      let _this = this;
      _this.checkboxOptions.splice(0, _this.checkboxOptions.length);
      this.fetchDataByApiLinkInvocation(options.apiInvocationConfig).then(result => {
        if (Array.isArray(result)) {
          _this.checkboxOptions = result;
          _this.setCheckedLabels();
        }
      });
    },
    fetchCheckboxOptionByDataSource(options, loadDataUrl, loadDataCntUrl) {
      let _this = this;
      this.getLabelValueOptionByDataSource(options, function (result) {
        _this.checkboxOptions = result;
        _this.setCheckedLabels();
      });
    },

    fetchCheckboxOptionByDataDic(dataDicUuid) {
      let _this = this;
      this.getLabelValueOptionByDataDic(dataDicUuid, function (result) {
        _this.checkboxOptions = result;
        _this.setCheckedLabels();
      });
    },
    onChange(values) {
      this.form[this.fieldCode] = values ? values.join(this.widget.configuration.tokenSeparators) : null;
      this.setCheckedLabels(values);
    },
    setCheckedLabels(values) {
      this.checkedLabels.splice(0, this.checkedLabels.length);
      if (values == undefined) {
        values = this.value;
      }
      if (values == undefined) {
        return [];
      }
      for (let i = 0, len = this.checkboxOptions.length; i < len; i++) {
        if (values.indexOf(this.checkboxOptions[i].value) != -1) {
          this.checkedLabels.push(this.checkboxOptions[i].label);
        }
      }
      return this.checkedLabels;
    }
  },
  mounted() {
    let _this = this;
    this.init();
    if (this.form[this.widget.configuration.code]) {
      this.setValue(this.form[this.widget.configuration.code]);
    }
    this.$watch('form.' + this.widget.configuration.code, function (newVal, oldVal) {
      if (newVal && newVal.split(this.widget.configuration.tokenSeparators) !== _this.value) {
        _this.value = newVal.split(this.widget.configuration.tokenSeparators);
      }
    });
  },
  beforeUpdate() {},
  beforeMount() {}
};
</script>
