<template>
  <a-form-model-item :ref="widget.configuration.code" :prop="widget.configuration.code" :rules="rules" :style="itemStyle" v-if="vShow">
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
      <a-textarea
        v-if="widget.configuration.type === 'textarea'"
        v-model="form[widget.configuration.code]"
        :disabled="designMode ? false : disable"
        :readOnly="readonly"
        :maxLength="widget.configuration.maxLength"
      />
      <a-input
        v-else
        v-model="form[widget.configuration.code]"
        @change="onWidgetInputChange"
        :disabled="designMode ? false : disable"
        :readOnly="readonly"
        :maxLength="widget.configuration.maxLength"
      >
        <template v-if="widget.configuration.i18nVisible">
          <WI18nInput slot="addonAfter" :target="form" :code="widget.configuration.i18nCode" v-model="form[widget.configuration.code]" />
        </template>
      </a-input>
    </template>
    <template v-else>
      <span class="textonly">{{ form[widget.configuration.code] }}</span>
    </template>
  </a-form-model-item>
</template>
<script type="text/babel">
import formElementMinxin from './form-element.mixin';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'WidgetInput',
  mixins: [formElementMinxin],
  data() {
    return {};
  },
  components: {
    WI18nInput
  },
  watch: {},
  beforeCreate() {},
  computed: {},
  created() {},
  methods: {
    onWidgetInputChange(e) {
      let code = this.widget.configuration.code;
      if (this.form[code] != undefined) {
        if (this.widget.configuration.formatType === 'toUpperCase' || this.widget.configuration.formatType === 'toLowerCase') {
          // 自动转大写
          this.form[code] = ''[this.widget.configuration.formatType].call(this.form[code]);
          let start = e.target.selectionStart;
          this.$nextTick(() => {
            e.target.setSelectionRange && e.target.setSelectionRange(start, start);
          });
        }
      }
    }
  },
  beforeMount() {},
  mounted() {},
  destroyed() {}
};
</script>
