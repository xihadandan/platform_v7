<template>
  <div
    class="widget-scan-code"
    :style="{
      textAlign: widget.configuration.align,
      display: visible ? 'block' : 'none'
    }"
  >
    <a-button :type="widget.configuration.type" :block="widget.configuration.block" :size="widget.configuration.size">
      <Icon :type="widget.configuration.icon" v-if="widget.configuration.icon" :size="20" />
      {{ widget.configuration.textHidden ? '' : $t(widget.id, widget.title) }}
      <Icon :type="widget.configuration.suffixIcon" v-if="widget.configuration.suffixIcon" />
    </a-button>
  </div>
</template>

<script>
import widgetMixin from '@framework/vue/mixin/widgetMixin';

export default {
  name: 'WidgetScanCode',
  mixins: [widgetMixin],
  created() {
    this.addProperty();
  },
  computed: {
    visible() {
      let visible = this.widget.configuration.pcVisible;
      if (this.designer) {
        if (this.designer.terminalType === 'mobile') {
          visible = true;
        }
      }
      return visible;
    }
  },
  methods: {
    addProperty() {
      if (this.widget.configuration.pcVisible === undefined) {
        this.$set(this.widget.configuration, 'pcVisible', false);
      }
    }
  }
};
</script>
