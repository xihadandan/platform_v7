<template>
  <div
    v-if="hiddenWidgets.length !== widget.configuration.cols.length"
    class="widget-grid"
    :class="[isDividingLine ? 'dividingLine' : '', pageParent ? 'pageParent' : '']"
    :style="gridStyle"
  >
    <a-row
      :key="widget.id"
      class="widget-row"
      :widget="widget.configuration.rowType"
      :type="widget.configuration.rowType === 'default' ? null : 'flex'"
      v-if="widget.configuration.rowType === 'flex'"
    >
      <template v-for="(col, cindex) in cols">
        <component
          v-if="!hiddenWidgets.includes(col.id)"
          :key="col.id"
          :is="colType"
          :widget="col"
          :index="cindex"
          :widgetsOfParent="cols"
          :parent="widget"
          :colHeight="widgetHeight"
          :dividingLine="isDividingLine && cindex < widget.configuration.cols.length - 1"
          :collaspeType="colsCollaspeType"
          @colsChange="colsChange"
        ></component>
      </template>
    </a-row>
    <template v-else v-for="(row, index) in defaultRowsData">
      <a-row :key="index" class="widget-row" :widget="widget.configuration.rowType">
        <template v-for="(col, cindex) in row">
          <component
            v-if="!hiddenWidgets.includes(col.id)"
            :key="col.id"
            :is="colType"
            :widget="col"
            :index="cindex"
            :widgetsOfParent="cols"
            :parent="widget"
            :colHeight="widgetHeight"
            :collaspeType="colsCollaspeType"
            :dividingLine="isDividingLine && cindex < widget.configuration.cols.length - 1"
            @colsChange="colsChange"
          ></component>
        </template>
      </a-row>
    </template>
  </div>
</template>
<style>
.widget-row {
  flex-flow: row nowrap;
}
</style>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import GridFlexCol from './grid-flex-col.vue';
import GridCol from './grid-col.vue';
import './css/index.less';
import { each, isNumber } from 'lodash';
import { deepClone, getElSpacingForTarget, findParentVNodeByName } from '@framework/vue/utils/util';
export default {
  name: 'WidgetGrid',
  mixins: [widgetMixin],
  data() {
    return {
      hiddenWidgets: [],
      hiddenState: false,
      childrenRenderedTotal: 0, // 已经渲染的计数
      childrenWidgets: [],
      childrenElHeights: [],
      childrenRenderedCallBack: {},
      colElHeightMap: {}, // 列id:列里面组件高度
      colChildrenIdMap: {}, // 列id:列里面组件id
      childrenIdHeightMap: {}, // 列里面组件id:行最大高度
      childrenIdTabpaneActiveMap: {},
      colsLength: this.widget.configuration.cols.length,
      cols: deepClone(this.widget.configuration.cols),
      widgetHeight: undefined,
      gridStyle: {},
      pageParent: false, //外层为页面
      parentElement: undefined
    };
  },
  components: {
    GridFlexCol,
    GridCol
  },
  computed: {
    colType() {
      return this.widget.configuration.rowType === 'flex' ? 'GridFlexCol' : 'GridCol';
    },
    // 默认布局，列宽超过24则另取一行
    defaultRowsData() {
      let rows = {},
        index = 0,
        span = 0;
      if (this.widget.configuration.rowType === 'default') {
        each(this.cols, (col, idx) => {
          if (col.configuration.span) {
            span += col.configuration.span;
          }
          if (span <= 24) {
            if (!rows[index]) {
              rows[index] = [];
            }
            rows[index].push(col);
          } else {
            span = col.configuration.span;
            index++;
            rows[index] = [];
            rows[index].push(col);
          }
        });
      }
      return rows;
    },
    // 判断是否有分隔线
    isDividingLine() {
      if (this.widget.configuration.dividingLine) {
        if (this.widget.configuration.rowType === 'default') {
          let allSpan = 0;
          each(this.widget.configuration.cols, (col, idx) => {
            if (col.configuration.span || col.configuration.span === 0) {
              allSpan += col.configuration.span;
            } else {
              allSpan += 24;
            }
          });
          if (allSpan > 24) {
            return false;
          }
        }
        return true;
      }
      return false;
    },
    isCollapse() {
      return this.isDividingLine && this.widget.configuration.collaspe;
    },
    colsCollaspeType() {
      let colsCollaspeType = [];
      if (this.isCollapse) {
        let collaspeType = this.widget.configuration.collaspeType;
        if (this.widget.configuration.cols.length >= 2 && collaspeType.length > 0) {
          if (collaspeType.indexOf('left') > -1) {
            colsCollaspeType.push({ index: 1, type: 'left' });
          }
          if (collaspeType.indexOf('right') > -1) {
            colsCollaspeType.push({ index: this.widget.configuration.cols.length - 2, type: 'right' });
          }
        }
      }
      return colsCollaspeType;
    }
  },
  created() {
    this.onChildrenWidgetRendered();
  },
  mounted() {
    this.colsHeightSetting(this.isDividingLine);
  },
  methods: {
    handleChildVisibleChange(child, visible) {
      if (!visible) {
        for (let i = 0, len = this.widget.configuration.cols.length; i < len; i++) {
          if (this.widget.configuration.cols[i].id == child) {
            if (this.hiddenWidgets.indexOf(child) == -1) {
              this.hiddenWidgets.push(child);
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
        (this.hiddenState && this.hiddenWidgets.length !== this.widget.configuration.cols.length) ||
        (!this.hiddenState && this.hiddenWidgets.length == this.widget.configuration.cols.length)
      ) {
        this.hiddenState = this.hiddenWidgets.length == this.widget.configuration.cols.length;
        // 状态发生变更，向上通知
        this.emitVisibleChange(!this.hiddenState);
      }
    },
    onChildrenWidgetRendered() {
      if (this.designMode || !EASY_ENV_IS_BROWSER) {
        return;
      }
      if (this.configuration.colsEqualHeight) {
        let temp = document.createElement('style');
        (document.head || document.body).appendChild(temp);
        let css = `
          .widget-tabs-container>.widget-tab>.ant-tabs-bar {
            margin-top: 0;
            margin-bottom: 0;
          }
        `;
        temp.innerHTML = css;
        this.pageContext.handleEvent(`widgetGrid:${this.widget.id}:children:rendered`, this.getChildrenWidgetHeight);
      }
    },
    getChildrenWidgetHeight({ wgt, widgetHeight, callBack }) {
      if (this.childrenRenderedTotal >= this.colsLength) {
        return;
      }
      let validate = true;
      const tabsEl = wgt.$el.closest('.widget-tabs-container');
      if (tabsEl) {
        const activeKey = tabsEl.__vue__.widget.configuration.defaultActiveKey;
        const tabpaneActiveEl = wgt.$el.closest(`#${activeKey}`);
        this.pageContext.handleEvent(`${tabsEl.__vue__.widget.id}:change`, ({ activeKey }) => {
          this.$nextTick(() => {});
        });
        if (tabpaneActiveEl) {
          this.childrenIdTabpaneActiveMap[wgt.widget.id] = activeKey;
        } else {
          validate = false;
        }
      }

      if (validate) {
        this.childrenRenderedTotal++;
        this.childrenWidgets.push(wgt);
        if (callBack && typeof callBack === 'function') {
          this.childrenRenderedCallBack[wgt.widget.id] = callBack;
        }

        let elHeight;
        if (widgetHeight) {
          elHeight = widgetHeight;
        } else if (wgt.widget) {
          elHeight = wgt.$el.offsetHeight;
        }
        this.childrenElHeights.push(elHeight);

        if (this.widget.configuration.rowType === 'default') {
          const colEl = wgt.$el.closest('div[id^="grid-col-"]');
          if (colEl) {
            const colId = colEl.getAttribute('id');
            this.colElHeightMap[colId] = elHeight; // 列id:列里面组件高度
            this.colChildrenIdMap[colId] = wgt.widget.id; // 列id:列里面组件id
          }
        }
      }
      if (this.childrenRenderedTotal === this.colsLength) {
        // 子组件都渲染完成
        let maxHeight;
        if (this.widget.configuration.rowType === 'default') {
          for (const key in this.defaultRowsData) {
            const row = this.defaultRowsData[key];
            if (row && Array.isArray(row)) {
              const colsHeight = row.map(r => {
                return this.colElHeightMap[r.id];
              });
              let maxRowHeight = Math.max(...colsHeight); // 行最大高度
              row.forEach(r => {
                this.childrenIdHeightMap[this.colChildrenIdMap[r.id]] = maxRowHeight;
                row.maxRowHeight = maxRowHeight;
              });
            }
          }
        } else {
          maxHeight = Math.max(...this.childrenElHeights);
        }
        for (let index = 0; index < this.childrenWidgets.length; index++) {
          const item = this.childrenWidgets[index];
          const { wtype, configuration } = item.widget;
          const wgtId = item.widget.id;
          if (wtype === 'WidgetTable') {
            let maxElHeight;
            if (this.widget.configuration.rowType === 'default') {
              maxElHeight = this.childrenIdHeightMap[wgtId];
            } else {
              maxElHeight = maxHeight;
            }
            item.$el.style.cssText += `;margin-bottom:0px; height:${maxElHeight}px`;

            if (this.childrenIdTabpaneActiveMap[wgtId]) {
              // 设置其他标签页下的高度
              const tabesContentEl = item.$el.closest('.ant-tabs-content');
              if (tabesContentEl) {
                const tableEls = tabesContentEl.querySelectorAll('.widget-table');
                for (let i = 0; i < tableEls.length; i++) {
                  const tableEl = tableEls[i];
                  if (tableEl.getAttribute('id') === wgtId) {
                    continue;
                  }
                  tableEl.style.cssText += `;margin-bottom:0px; height:${maxElHeight}px`;
                  tableEl.parentElement.parentElement;
                  if (tableEl.parentElement && tableEl.parentElement.parentElement) {
                    if (tableEl.parentElement.parentElement.getAttribute('class') === 'ps') {
                      // 卡片套表格
                      tableEl.parentElement.parentElement.style.cssText += `;height:${maxElHeight}px`;
                    }
                  }

                  if (tableEl.__vue__.setContentByDisplayState) {
                    tableEl.__vue__.setContentByDisplayState();
                  }
                }
              }
            }
          }
          if (this.childrenRenderedCallBack[wgtId]) {
            this.childrenRenderedCallBack[wgtId]();
          }
        }
        this.resetChildrenRendered();
      }
    },
    resetChildrenRendered() {
      this.childrenRenderedTotal = 0;
      this.childrenWidgets = [];
      this.childrenElHeights = [];
      this.childrenRenderedCallBack = {};
      this.childrenIdTabpaneActiveMap = {};
    },
    // 左右折叠按钮点击后，重新计算各列宽度
    colsChange(direction, leftCollapsed, rightCollapsed) {
      if (direction == 'left') {
        // 左收起展开
        this.colsChangeHandle(direction, leftCollapsed, rightCollapsed, 0, 1, 2);
      } else if (direction == 'right') {
        // 右展开收起
        this.colsChangeHandle(direction, rightCollapsed, leftCollapsed, this.cols.length - 1, this.cols.length - 2, this.cols.length - 3);
      }
    },
    /**
     * 获取列的宽度
     * @param {String} direction 折叠方向
     * @param {*} collapsed 折叠状态
     * @param {*} otherCollapsed 其他方向的折叠状态
     * @param {*} index 收起展开列
     * @param {*} targetIndex 目标列
     * @param {*} otherIndex 其他状态的收起展开列
     */
    colsChangeHandle(direction, collapsed, otherCollapsed, index, targetIndex, otherIndex) {
      let col = this.cols[index];
      let targetCol = this.cols[targetIndex];
      // 目标栅格有没有其他折叠类型
      let otherType = this.colsCollaspeType.filter(
        item => item.index == targetIndex && item.type == (direction == 'left' ? 'right' : 'left')
      );
      if (this.widget.configuration.rowType === 'default') {
        let span0 = this.widget.configuration.cols[index].configuration.span;
        let span1 = this.widget.configuration.cols[targetIndex].configuration.span;
        if (collapsed) {
          // 收起
          col.configuration.span = 0;
          if (otherType && otherCollapsed) {
            // 有右收起，且是收起状态, 列宽度为实际宽度
            span1 = targetCol.configuration.span;
          }
          // 两列都有值，则两列宽度之和为总宽度
          targetCol.configuration.span = span0 + span1;
        } else {
          // 展开
          col.configuration.span = span0;
          if (otherType && otherCollapsed) {
            // 有右收起，且是收起状态, 列宽度为第二列和第三列之和
            let span2 = this.widget.configuration.cols[otherIndex].configuration.span;
            // 两列都有值，则两列宽度之和为总宽度
            targetCol.configuration.span = span2 + span1;
          } else {
            targetCol.configuration.span = span1;
          }
        }
      } else if (this.widget.configuration.rowType === 'flex') {
        let span0 = this.widget.configuration.cols[index].configuration.flex;
        let span1 = this.widget.configuration.cols[targetIndex].configuration.flex;
        if (collapsed) {
          // 收起
          col.configuration.flex = 0;
          if (otherType && otherCollapsed) {
            // 有右收起，且是收起状态, 列宽度为实际宽度
            span1 = targetCol.configuration.flex;
          }
          if ((span0 || span0 === 0) && span1) {
            // 两列都有值，则两列宽度之和为总宽度
            targetCol.configuration.flex = span0 + span1;
          } else if (span1 && !span0) {
            // 只有第二列有值,收起的列自适应，则第二列自适应
            targetCol.configuration.flex = undefined;
          } else {
            targetCol.configuration.flex = span1;
          }
        } else {
          // 展开
          col.configuration.flex = span0;
          if (otherType && otherCollapsed) {
            // 有右收起，且是收起状态, 列宽度为第二列和第三列之和
            let span2 = this.widget.configuration.cols[otherIndex].configuration.flex;
            if ((span2 || span2 === 0) && span1) {
              // 两列都有值，则两列宽度之和为总宽度
              targetCol.configuration.flex = span2 + span1;
            } else if (span1 && !span2) {
              // 只有第二列有值,收起的列自适应，则第二列自适应
              targetCol.configuration.flex = undefined;
            } else {
              targetCol.configuration.flex = span1;
            }
          } else {
            targetCol.configuration.flex = span1;
          }
        }
      }
      this.collapsedDirectionHandle(direction, collapsed);
    },
    // 收起展开后，栅格外层组件样式修改
    collapsedDirectionHandle(direction, collapsed) {
      if (this.parentElement) {
        if (collapsed) {
          // 收起
          if (!this.parentElement.classList.contains('hide_' + direction + '_col')) {
            this.parentElement.classList.add('hide_' + direction + '_col');
          }
        } else {
          // 展开
          if (this.parentElement.classList.contains('hide_' + direction + '_col')) {
            this.parentElement.classList.remove('hide_' + direction + '_col');
          }
        }
      }
    },
    // 各列高度设置
    colsHeightSetting(isDividingLine, isAgain) {
      if (
        (this.widget.configuration.heightType == 'default' ||
          (this.widget.configuration.heightType == 'define' &&
            (!this.widget.configuration.height || this.widget.configuration.height == 'auto'))) &&
        isDividingLine
      ) {
        // 默认高度，有分隔线，分隔线底部默认到页面底部
        if (this.$el) {
          let $el = this.$el;
          let parent = deepClone(this.parent);
          let $parent = this.$el.closest(`#${parent.id}`);
          if (parent.id.startsWith('page_')) {
            $parent = $el.closest(`[namespace="${parent.id}"]`);
            // 如果有引用页面
            if ($parent.parentNode.classList.contains('widget-ref-vpage')) {
              let $refPage = $parent.parentNode;
              let refId = $refPage.getAttribute('id');
              let refPageVue = this.pageContext.getVueWidgetById(refId);
              if (refPageVue && refPageVue.parent && refPageVue.parent.id && !refPageVue.parent.id.startsWith('page_')) {
                parent = refPageVue.parent;
                $el = refPageVue.$el;
                $parent = refPageVue.$el.closest(`#${parent.id}`);
              }
            }
          }
          if ((!isAgain || !$parent) && parent && !parent.id.startsWith('page_')) {
            // 父组件的id可能尚未生成
            setTimeout(() => {
              this.colsHeightSetting(isDividingLine, true);
            }, 100);
            return false;
          }

          if (!$parent.classList.contains('grid-dividing-line')) {
            $parent.classList.add('grid-dividing-line');
            this.parentElement = $parent;
          }
          if (parent.id.startsWith('page_')) {
            this.pageParent = true;
          }
          let vpageHeight = $el.closest('.widget-vpage').offsetHeight;
          if ($el.closest('.widget-layout-content')) {
          } else if (this.$root.$el.classList.contains('preview')) {
            // 预览页
            vpageHeight = this.$root.$el.offsetHeight;
            if (parent.id.startsWith('page_')) {
              $parent = this.$root.$el;
            }
          }
          if (parent.wtype == 'WidgetTab') {
            this.tabContentScrollHeight($parent, vpageHeight);
          } else {
            if (parent.configuration && (!parent.configuration.height || parent.configuration.height == 'auto')) {
              $parent.style.cssText += `; height:${vpageHeight}px`;
            }
            const { maxHeight, parentBottom, parentTop } = getElSpacingForTarget($el, $parent);
            console.log(maxHeight);
            console.log(vpageHeight);
            this.widgetHeight = maxHeight + parentBottom - 2 + 'px';
          }
        }
      } else if (this.widget.configuration.heightType == 'define') {
        // 自定义高度
        this.widgetHeight = this.widget.configuration.height
          ? isNumber(this.widget.configuration.height)
            ? this.widget.configuration.height + 'px'
            : this.widget.configuration.height
          : undefined;
        if (this.widgetHeight && this.widgetHeight.indexOf('%') > -1 && isDividingLine) {
          this.gridStyle.height = this.widgetHeight;
          this.widgetHeight = '100%';
        }
      } else {
        this.widgetHeight = undefined;
      }
    },
    // 标签页组件内容高度设置
    tabContentScrollHeight($parent, vpageHeight) {
      const findVNode = findParentVNodeByName(this, 'WidgetTab');
      let headerHeight = 0;
      if (findVNode.widget.configuration.tabPosition === 'top' || findVNode.widget.configuration.tabPosition === 'bottom') {
        let $header = $parent.querySelector('.ant-tabs-bar');
        const $headerStyle = window.getComputedStyle($header);
        headerHeight = $header.offsetHeight + (parseFloat($headerStyle.marginBottom) || 0) + (parseFloat($headerStyle.marginTop) || 0);
      }
      findVNode.tabContentScrollHeight = vpageHeight - headerHeight + 'px';
      this.gridStyle.height = '100%';
      this.widgetHeight = '100%';
    }
  }
};
</script>
