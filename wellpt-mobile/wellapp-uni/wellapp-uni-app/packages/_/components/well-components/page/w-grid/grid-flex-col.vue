<template>
  <view :class="['w-col', isCollapseCol ? 'col-collapse' : '', flexClass]" :style="colStyle">
    <div class="col-widget-item">
      <template v-for="(wgt, index) in widget.configuration.widgets">
        <widget :key="wgt.id" :widget="wgt" :parent="widget" :widgetsOfParent="widget.configuration.widgets"></widget>
      </template>
    </div>
    <template v-if="isCollapse">
      <template v-for="direction in collapseDirection" v-if="collapseDirectionShow(direction)">
        <w-icon
          :icon="
            (direction == 'left' ? leftCollapsed : rightCollapsed) ? collapsedType(direction) : expendedType(direction)
          "
          :class="['grid-sider-trigger', direction]"
          :size="12"
          @onTap="changeCollapsed(direction)"
        />
      </template>
    </template>
  </view>
</template>
<script type="text/babel">
import mixin from "../page-widget-mixin";
import colMixin from "./colMixin";
export default {
  name: "GridFlexCol",
  mixins: [mixin, colMixin],
  data() {
    return {};
  },

  beforeCreate() {},
  components: {},
  computed: {
    flexClass() {
      if (this.widget.configuration.uniflex || this.widget.configuration.uniflex === 0) {
        return "f_s_0";
      }
      return "f_g_1";
    },
    colStyle() {
      let style = {};
      style.width = "0px";
      if (this.widget.configuration.uniflex || this.widget.configuration.uniflex === 0) {
        style.width = this.widget.configuration.uniflex + "px";
      }
      if (this.colHeight) {
        style.height = this.colHeight;
      }
      return style;
    },
  },
  created() {},
  methods: {
    collapseDirectionShow(direction) {
      if (direction == "left") {
        // 左侧折叠，第一个栅格要有宽度
        return (
          this.widgetsOfParent[0].configuration.uniflex !== undefined &&
          this.widgetsOfParent[0].configuration.uniflex !== null
        );
      } else if (direction == "right") {
        // 右侧折叠，最后一个栅格要有宽度
        return (
          this.widgetsOfParent[this.widgetsOfParent.length - 1].configuration.uniflex !== undefined &&
          this.widgetsOfParent[this.widgetsOfParent.length - 1].configuration.uniflex !== null
        );
      }
    },
  },
  mounted() {},
};
</script>
