<template>
  <a-col :class="['widget-col', isCollapseCol ? 'col-collapse' : '']" :flex="flexValue" :style="colStyle">
    <div class="col-widget-item">
      <template v-for="(wgt, index) in widget.configuration.widgets">
        <component
          :key="wgt.id"
          :is="resolveWidgetType(wgt)"
          :widget="wgt"
          :index="index"
          :parent="widget"
          :widgetsOfParent="widget.configuration.widgets"
        ></component>
      </template>
    </div>
    <template v-if="isCollapse">
      <template v-for="direction in collapseDirection">
        <a-tooltip
          :placement="direction == 'left' ? 'right' : 'left'"
          :overlayStyle="{ zIndex: 10000 }"
          :key="widget.id + '_' + direction"
          v-if="collapseDirectionShow(direction)"
        >
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
  name: 'GridFlexCol',
  mixins: [widgetMixin, colMixin],
  data() {
    return {};
  },

  beforeCreate() {},
  components: {},
  computed: {
    flexValue() {
      if (this.parent.configuration.rowType === 'flex') {
        if (this.widget.configuration.flex || this.widget.configuration.flex === 0) {
          return this.widget.configuration.flex + 'px';
        }
        return 'auto';
      }
    },
    colStyle() {
      let style = {};
      if (this.parent.configuration.rowType === 'flex') {
        // if (!this.widget.configuration.flex) {
        // flex：auto设置width：0，使该列宽度固定
        style.width = '0';
        // }
      }
      if (this.colHeight) {
        style.height = this.colHeight;
      }
      return style;
    },
    leftColFlex() {}
  },
  created() {},
  methods: {
    collapseDirectionShow(direction) {
      if (direction == 'left') {
        // 左侧折叠，第一个栅格要有宽度
        return this.widgetsOfParent[0].configuration.flex !== undefined && this.widgetsOfParent[0].configuration.flex !== null;
      } else if (direction == 'right') {
        // 右侧折叠，最后一个栅格要有宽度
        return (
          this.widgetsOfParent[this.widgetsOfParent.length - 1].configuration.flex !== undefined &&
          this.widgetsOfParent[this.widgetsOfParent.length - 1].configuration.flex !== null
        );
      }
    }
  },
  mounted() {}
};
</script>
