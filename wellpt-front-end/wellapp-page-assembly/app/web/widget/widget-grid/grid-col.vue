<template>
  <a-col :class="['widget-col', isCollapseCol ? 'col-collapse' : '']" :span="widget.configuration.span" :style="colStyle">
    <div class="col-widget-item">
      <template v-for="(wgt, index) in widget.configuration.widgets">
        <component
          v-if="!hiddenWidgets.includes(wgt.id)"
          :key="wgt.id"
          :is="resolveWidgetType(wgt)"
          :widget="wgt"
          :index="index"
          :parent="widget"
          :widgetsOfParent="widget.configuration.widgets"
          :colHeight="widgetHeight"
          :dividingLine="isDividingLine && cindex < widget.configuration.cols.length - 1"
          :collaspeType="colsCollaspeType"
        ></component>
      </template>
    </div>
    <template v-if="isCollapse">
      <template v-for="direction in collapseDirection">
        <a-tooltip :placement="direction == 'left' ? 'right' : 'left'" :overlayStyle="{ zIndex: 10000 }" :key="widget.id + '_' + direction">
          <template slot="title">
            <span>
              {{
                (direction == 'left' ? leftCollapsed : rightCollapsed)
                  ? $t('WidgetLayout.expand', '展开')
                  : $t('WidgetLayout.collapse', '收起')
              }}
            </span>
          </template>
          <Icon
            :type="(direction == 'left' ? leftCollapsed : rightCollapsed) ? collapsedType(direction) : expendedType(direction)"
            :class="['grid-sider-trigger', direction]"
            @click.stop="changeCollapsed(direction)"
          />
        </a-tooltip>
      </template>
    </template>
  </a-col>
</template>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import colMixin from './colMixin';
export default {
  name: 'GridCol',
  mixins: [widgetMixin, colMixin],
  data() {
    return {
      hiddenWidgets: [],
      hiddenState: false
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
    }
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
    }
  },
  mounted() {}
};
</script>
