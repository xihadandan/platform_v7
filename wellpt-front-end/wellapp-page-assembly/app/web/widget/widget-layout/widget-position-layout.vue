<template>
  <div class="widget-position-layout" :style="positionStyle">
    <template v-for="(wgt, index) in widget.configuration.widgets">
      <div
        v-if="widgetPosition[wgt.id]"
        :style="{
          position: 'absolute',
          left: widgetPosition[wgt.id].left || 'unset',
          top: widgetPosition[wgt.id].top || 'unset',
          bottom: widgetPosition[wgt.id].bottom || 'unset',
          right: widgetPosition[wgt.id].right || 'unset',
          transform: widgetPosition[wgt.id].transform || 'unset',
          zIndex: widgetPosition[wgt.id].zIndex,
          width: widgetPosition[wgt.id].wFixed ? widgetPosition[wgt.id].w + 'px' : widgetPosition[wgt.id].widthPercent,
          height: widgetPosition[wgt.id].h + 'px'
        }"
      >
        <component
          :key="wgt.id"
          :is="resolveWidgetType(wgt)"
          :widget="wgt"
          :parent="widget"
          :index="index"
          :widgetsOfParent="widget.configuration.widgets"
          :designer="designer"
          v-bind="wgt.props"
        ></component>
      </div>
    </template>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { deepClone } from '@framework/vue/utils/util';

export default {
  name: 'WidgetPositionLayout',
  mixins: [widgetMixin],
  props: {},
  components: {},
  computed: {
    positionStyle() {
      let style = {
        position: 'relative',
        width: '100%',
        overflow: 'hidden',
        height: this.widget.configuration.style.height + 'px'
      };
      return style;
    }
  },
  data() {
    let widgetPosition = deepClone(this.widget.configuration.widgetPosition);
    for (let k in widgetPosition) {
      let position = widgetPosition[k];
      // if (position.widthPercent) {
      //     position.width = position.widthPercent;
      // }
      // if (position.x != undefined) {
      //   position.left = `${position.x}px`;
      // }
      // if (position.y != undefined) {
      //   position.top = `${position.y}px`;
      // }
      // if (position.w != undefined) {
      //   position.width = `${position.x}px`;
      // } else {
      //   position.width = 'fit-content';
      // }
      // if (position.h != undefined) {
      //   position.height = `${position.h}px`;
      // } else {
      //   position.height = 'auto';
      // }

      if (position.z != undefined) {
        position.zIndex = position.z;
      }
    }

    return {
      width: undefined,
      widgetPosition
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    // this.width = this.$el.clientWidth;
    // // 定位按百分比修正
    // for (let k in this.widgetPosition) {
    //   let position = this.widgetPosition[k];
    //   let left = parseInt(position.left);
    //   left = left / this.width;
    //   this.widgetPosition[k].left = left;
    // }
  },
  methods: {
    // positionStyle(wgt) {
    //   let style = {};
    //   if (wgt.configuration && wgt.configuration.style) {
    //     style.position = wgt.configuration.style.position;
    //     style.top = wgt.configuration.style.top;
    //     style.left = wgt.configuration.style.left;
    //   }
    //   return style;
    // }
  }
};
</script>
