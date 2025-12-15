<template>
  <uni-col
    :class="['w-col', isCollapseCol ? 'col-collapse' : '']"
    :span="rowDisplay ? 24 : widget.configuration.span"
    :style="colStyle"
  >
    <view class="col-widget-item">
      <template v-for="(wgt, index) in widget.configuration.widgets">
        <widget :key="wgt.id" :widget="wgt" :parent="widget" :widgetsOfParent="widget.configuration.widgets"></widget>
      </template>
    </view>
    <template v-if="isCollapse">
      <template v-for="direction in collapseDirection">
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
  </uni-col>
</template>
<script type="text/babel">
import mixin from "../page-widget-mixin";
import colMixin from "./colMixin";
export default {
  name: "GridCol",
  props: {
    rowDisplay: Boolean,
  },
  mixins: [mixin, colMixin],
  data() {
    return {
      hiddenWidgets: [],
      hiddenState: false,
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    colStyle() {
      let style = {};
      if (this.colHeight) {
        style.height = this.colHeight;
      }
      return style;
    },
  },
  created() {},
  methods: {
    handleChildVisibleChange(child, visible) {
      if (!visible) {
        for (let i = 0, len = this.widget.configuration.widgets.length; i < len; i++) {
          if (this.widget.configuration.widgets[i].id == child) {
            if (!this.hiddenWidgets.includes(child)) {
              this.hiddenWidgets.push(child);
              //FIXME: grid span 要补位给兄弟节点？？
            }
            break;
          }
        }
      } else {
        let i = this.hiddenWidgets.indexOf(child);
        if (i != -1) {
          this.hiddenWidgets.splice(i, 1);
        }
      }
      if (
        (this.hiddenState && this.hiddenWidgets.length !== this.widget.configuration.widgets.length) ||
        (!this.hiddenState && this.hiddenWidgets.length == this.widget.configuration.widgets.length)
      ) {
        this.hiddenState = this.hiddenWidgets.length == this.widget.configuration.widgets.length;
        // 状态发生变更，向上通知
        this.emitVisibleChange(!this.hiddenState);
      }
    },
  },
  mounted() {},
};
</script>
