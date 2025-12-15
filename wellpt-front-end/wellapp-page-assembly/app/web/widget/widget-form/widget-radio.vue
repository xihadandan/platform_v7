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
      <a-radio-group
        v-model="form[widget.configuration.code]"
        :buttonStyle="widget.configuration.buttonSelectedStyle"
        :ref="'radio_' + widget.configuration.code"
        :disabled="designMode ? false : disable"
        @blur="onBlur"
        @change="onChange"
      >
        <template v-if="widget.configuration.styleType == 'button'">
          <a-radio-button
            v-for="(opt, i) in radioOptions"
            :key="'radio-' + widget.id + i"
            :value="opt.value"
            :ref="'radio' + i"
            :disabled="designMode ? false : readonly && opt.value != form[widget.configuration.code]"
            @click.stop="$evt => onClick($evt, 'radio' + i, opt.value)"
          >
            {{ opt.label }}
          </a-radio-button>
        </template>
        <template v-else>
          <a-radio
            v-for="(opt, i) in radioOptions"
            :key="'radio-' + widget.id + i"
            :style="radioStyle"
            :value="opt.value"
            :ref="'radio' + i"
            :disabled="designMode ? false : readonly && opt.value != form[widget.configuration.code]"
            @click.stop="$evt => onClick($evt, 'radio' + i, opt.value)"
          >
            {{ opt.label }}
          </a-radio>
        </template>
      </a-radio-group>
    </template>
    <template v-else>
      <span class="textonly" v-if="checkedLabel">{{ checkedLabel }}</span>
    </template>
  </a-form-model-item>
</template>

<script type="text/babel">
import formElementMinxin from './form-element.mixin';
import formMixin from '@dyform/app/web/widget/mixin/form-common.mixin';

export default {
  name: 'WidgetRadio',
  mixins: [formElementMinxin, formMixin],
  props: {
    defaultCheckOptions: Array
  },
  data() {
    let radioOptions = this.defaultRadioOptions || [];
    return { radioOptions, checkedLabel: null };
  },
  beforeCreate() {},
  components: {},
  computed: {
    radioStyle() {
      return {
        display: this.widget.configuration.layout == 'vertical' ? 'block' : 'inline-block',
        width:
          this.widget.configuration.layout == 'horizontal' && this.widget.configuration.alignType == 'fixedWidth'
            ? this.widget.configuration.itemWidth + 'px'
            : 'auto'
      };
    }
  },
  created() {},
  methods: {
    //重新加载备选项
    refetchOption() {
      this.fetchRadioOptions();
    },
    onBlur() {},
    onChange(e) {
      this.setCheckdLabel();
    },
    onClick(evt, ref, val) {
      if (val == this.getValue() && this.widget.configuration.cancleChecked) {
        //取消选中
        this.form[this.widget.configuration.code] = undefined;
      }
    },
    fetchRadioOptions() {
      if (this.widget.configuration.options.type == 'selfDefine' && this.widget.configuration.options.defineOptions.length) {
        this.radioOptions = this.widget.configuration.options.defineOptions;
        for (let o of this.radioOptions) {
          o.label = this.$t(o.id, o.label);
        }
        this.setCheckdLabel();
      } else if (this.widget.configuration.options.type == 'dataSource') {
        this.fetchRadioOptionByDataSource(this.widget.configuration.options);
      } else if (this.widget.configuration.options.type == 'dataDictionary') {
        this.fetchRadioOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid);
      } else if (this.widget.configuration.options.type == 'dataModel') {
        this.fetchRadioOptionByDataSource({
          ...this.widget.configuration.options,
          loadDataUrl: `/proxy/api/dm/loadData/${this.widget.configuration.options.dataModelUuid}`,
          loadDataCountUrl: `/proxy/api/dm/loadDataCount/${this.widget.configuration.options.dataModelUuid}`
        });
      } else if (this.widget.configuration.options.type == 'apiLinkService') {
        this.fetchRadioOptionByApiLink(this.widget.configuration.options);
      }
    },
    fetchRadioOptionByApiLink(options, callback) {
      let _this = this;
      _this.radioOptions.splice(0, _this.radioOptions.length);
      this.fetchDataByApiLinkInvocation(options.apiInvocationConfig).then(result => {
        if (Array.isArray(result)) {
          _this.radioOptions = result;
          _this.setCheckdLabel();
        }
      });
    },
    fetchRadioOptionByDataSource(options) {
      let _this = this;
      this.getLabelValueOptionByDataSource(options, function (result) {
        _this.radioOptions = result;
        _this.setCheckdLabel();
      });
    },
    fetchRadioOptionByDataDic(dataDicUuid) {
      let _this = this;
      this.getLabelValueOptionByDataDic(dataDicUuid, function (result) {
        _this.radioOptions = result;
        _this.setCheckdLabel();
      });
    },
    setCheckdLabel(value) {
      if (value == undefined) {
        value = this.form[this.widget.configuration.code];
        this.checkedLabel = null;
      }
      for (let i = 0, len = this.radioOptions.length; i < len; i++) {
        if (value === this.radioOptions[i].value) {
          this.checkedLabel = this.radioOptions[i].label;
          break;
        }
      }
      return this.checkedLabel;
    }
  },
  beforeMount() {
    this.fetchRadioOptions();
  },
  mounted() {},
  beforeUpdate() {}
};
</script>
