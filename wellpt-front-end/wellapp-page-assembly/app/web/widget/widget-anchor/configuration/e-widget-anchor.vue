<template>
  <EditWrapper :widget="widget" :index="index" :enableDrag="true" :widgetsOfParent="widgetsOfParent" :designer="designer" :parent="parent">
    <div class="widget-anchor-container no-form-item-widget">
      <template>
        <a-row
          type="flex"
          :style="{ flexDirection: widget.configuration.flexDirection }"
          :key="flex"
          :class="'flex-direction-' + widget.configuration.flexDirection"
        >
          <a-col :flex="flex">
            <div style="width: 100%; height: 100%; display: block; position: absolute; z-index: 2"></div>
            <a-empty
              v-if="widget.configuration.anchors.length === 0"
              description="暂无锚点"
              :imageStyle="{
                height: widget.configuration.flexDirection != 'column' ? '40px' : '20px',
                'margin-bottom': '0px',
                'margin-top': widget.configuration.flexDirection != 'column' ? '20px' : '4px'
              }"
            />
            <template v-else>
              <template v-if="widget.configuration.flexDirection === 'column'">
                <a-tabs v-show="widget.configuration.anchors.length > 0" :class="['widget-anchor-tab', widget.configuration.inkShape]">
                  <a-tab-pane v-for="(anchor, i) in widget.configuration.anchors" :key="anchor.id">
                    <template slot="tab">
                      <span class="unactive-point"></span>
                      <label :title="anchor.label">{{ anchor.label }}</label>
                    </template>
                  </a-tab-pane>
                </a-tabs>
              </template>
              <template v-else>
                <a-anchor :wrapperClass="wrapperClass">
                  <template v-for="(anchor, i) in widget.configuration.anchors">
                    <AnchorLink :anchor="anchor" :key="'anchorlink_' + i" :titleStyle="{ width: rowStyleWidth }" />
                  </template>
                </a-anchor>
              </template>
            </template>
          </a-col>
          <a-col flex="auto">
            <!-- <a-empty v-if="widget.configuration.widgets.length === 0" description="拖拽组件到此处" :imageStyle="{ height: '40px' }" /> -->
            <draggable
              :list="widget.configuration.widgets"
              v-bind="{ group: dragGroup, ghostClass: 'ghost', animation: 200 }"
              handle=".widget-drag-handler"
              @paste.native="onDraggablePaste"
              :move="onDragMove"
              style="border: 0"
              :class="[!widget.configuration.widgets || widget.configuration.widgets.length === 0 ? 'widget-edit-empty' : '']"
            >
              <transition-group name="fade" tag="div" class="grid-col-drop-panel" :style="{ minHeight: '100px' }">
                <template v-for="(wgt, i) in widget.configuration.widgets">
                  <WDesignItem
                    :widget="wgt"
                    :key="wgt.id"
                    :index="i"
                    :widgetsOfParent="widget.configuration.widgets"
                    :designer="designer"
                    :dragGroup="dragGroup"
                    :parent="widget"
                  />
                </template>
              </transition-group>
            </draggable>
          </a-col>
        </a-row>
      </template>
    </div>
  </EditWrapper>
</template>

<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import draggable from '@framework/vue/designer/draggable';
import { debounce, sortBy } from 'lodash';
import AnchorLink from '../anchor-link.vue';
export default {
  name: 'EWidgetAnchor',
  mixins: [editWgtMixin, draggable],
  data() {
    return {};
  },

  components: { AnchorLink },
  computed: {
    wrapperClass() {
      let classes = ['widget-anchor'];
      if (this.widget.configuration.inkShape === 'line') {
        classes.push('ink-line');
      }
      // if (this.widget.configuration.flexDirection === 'row' && !this.levitate) {
      //   classes.push('align-right');
      // }
      // if (this.widget.configuration.flexDirection === 'row-reverse' && !this.levitate) {
      //   classes.push('align-left');
      // }
      return classes.join(' ');
    },
    flex() {
      return this.widget.configuration.flexDirection === 'column' ? '50px' : this.widget.configuration.levitate ? '0px' : '150px';
    },
    rowStyleWidth() {
      if (this.widget.configuration.flexDirection !== 'column') {
        return parseInt(this.flex) > 32 ? parseInt(this.flex) - 32 + 'px' : '';
      }
      return '';
    }
  },
  beforeCreate() {},
  created() {},
  methods: {
    resortAnchorByWidgetOrder: debounce(function (v) {
      // 锚点安装组件顺序排序，仅适用同级顺序调整，不适用父子关系变更
      const widgetTreeMap = this.designer.widgetTreeMap;
      let anchorTreeChildren = widgetTreeMap[this.widget.id].children || [];
      let treeNodeSeq = {},
        seq = 0;
      // 初始化所有组件排序，关联锚点的同级组件排序唯一确定
      for (let wid in widgetTreeMap) {
        treeNodeSeq[wid] = widgetTreeMap[wid].index || 0;
      }
      if (anchorTreeChildren) {
        let childrenSeq = function (nodes) {
          if (nodes)
            for (let i = 0, len = nodes.length; i < len; i++) {
              treeNodeSeq[nodes[i].key] = ++seq;
              childrenSeq(nodes[i].children);
            }
        };
        for (let i = 0, len = anchorTreeChildren.length; i < len; i++) {
          treeNodeSeq[anchorTreeChildren[i].key] = ++seq;
          childrenSeq(anchorTreeChildren[i].children);
        }
        let cacadeSubAnchors = function (anchor) {
          if (anchor.anchors) {
            anchor.anchors = sortBy(anchor.anchors, function (a) {
              cacadeSubAnchors(a);
              console.log(a.label + ' -> seq = ' + treeNodeSeq[a.href]);
              return treeNodeSeq[a.href];
            });
          }
        };
        if (this.widget.configuration.dependonWgtOrder) {
          this.widget.configuration.anchors = sortBy(this.widget.configuration.anchors, function (a) {
            cacadeSubAnchors(a);
            console.log(a.label + ' -> seq = ' + treeNodeSeq[a.href]);
            return treeNodeSeq[a.href];
          });
        }

        console.log(treeNodeSeq);
      }
    }, 500)
  },
  mounted() {},

  destroyed() {},
  watch: {
    'designer.widgetTreeMap': {
      deep: true,
      handler() {
        if (this.widget.configuration.dependonWgtOrder) {
          this.resortAnchorByWidgetOrder();
        }
      }
    }
  }
};
</script>

<style lang="less">
.widget-anchor-container {
  .flex-direction-row,
  .flex-direction-row-reverse {
    .ant-affix {
      position: static !important;
    }
  }
}

.widget-edit-wrapper {
  .widget-anchor-container .widget-anchor-tab.circle .ant-tabs-ink-bar-animated {
    transition: none;
  }
}
</style>
