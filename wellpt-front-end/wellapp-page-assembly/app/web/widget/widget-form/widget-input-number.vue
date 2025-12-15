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
      <a-input-group compact v-if="widget.configuration.enableSuffix">
        <a-input-number v-model="form[widget.configuration.code]" :disabled="designMode ? false : editable || readonly" />
        <a-button>{{ widget.configuration.suffix }}</a-button>
      </a-input-group>
      <a-input-number v-else v-model="form[widget.configuration.code]" :disabled="designMode ? false : disable || readonly" />
    </template>
    <template v-else>
      <span class="textonly">{{ form[widget.configuration.code] }}</span>
    </template>
  </a-form-model-item>
</template>

<script type="text/babel">
import formElementMinxin from './form-element.mixin';
import formMixin from '@dyform/app/web/widget/mixin/form-common.mixin';

export default {
  name: 'WidgetInputNumber',
  mixins: [formElementMinxin, formMixin],
  props: {},
  data() {
    return {};
  },
  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {},
  mounted() {},
  beforeUpdate() {},
  beforeMount() {}
};
</script>
