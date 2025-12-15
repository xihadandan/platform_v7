<template>
  <view
    v-if="hiddenWidgets.length !== widget.configuration.cols.length"
    class="w-grid"
    :class="[
      isDividingLine ? 'dividingLine' : '',
      widgetHeight ? 'hasScroll' : '',
      rowDisplay ? 'rowDisplay' : '',
      customClassCom,
    ]"
    :style="gridStyle"
  >
    <template v-if="rowDisplay">
      <uni-row class="w-row">
        <template v-for="(col, i) in widget.configuration.cols">
          <GridCol
            v-if="!hiddenWidgets.includes(col.id)"
            rowDisplay
            :key="col.id"
            :widget="col"
            :index="cindex"
            :widgetsOfParent="cols"
            :parent="widget"
            :colHeight="widgetHeight"
            :dividingLine="isDividingLine && cindex < widget.configuration.cols.length - 1"
          ></GridCol>
        </template>
      </uni-row>
    </template>
    <template v-else-if="colType == 'GridCol'" v-for="(row, index) in defaultRowsData">
      <uni-row class="w-row" :key="'grid-row-' + index" :gutter="gutter">
        <template v-for="(col, cindex) in row">
          <GridCol
            v-if="!hiddenWidgets.includes(col.id)"
            :key="col.id"
            :widget="col"
            :index="cindex"
            :widgetsOfParent="cols"
            :parent="widget"
            :colHeight="widgetHeight"
            :collaspeType="colsCollaspeType"
            :dividingLine="isDividingLine && cindex < widget.configuration.cols.length - 1"
            @colsChange="colsChange"
          ></GridCol>
        </template>
      </uni-row>
    </template>
    <template v-else>
      <view class="w-row flex">
        <template v-for="(col, cindex) in cols">
          <GridFlexCol
            v-if="!hiddenWidgets.includes(col.id)"
            :key="col.id"
            :widget="col"
            :index="cindex"
            :widgetsOfParent="cols"
            :parent="widget"
            :colHeight="widgetHeight"
            :collaspeType="colsCollaspeType"
            :dividingLine="isDividingLine && cindex < widget.configuration.cols.length - 1"
            @colsChange="colsChange"
          ></GridFlexCol>
        </template>
      </view>
    </template>
  </view>
</template>

<script>
// #ifndef APP-PLUS
import "./index.scss";
// #endif
import mixin from "../page-widget-mixin";
import GridCol from "./grid-col.vue";
import GridFlexCol from "./grid-flex-col.vue";
import { utils } from "wellapp-uni-framework";
import { each, isNumber } from "lodash";
export default {
  mixins: [mixin],
  name: "w-grid",
  data() {
    this.initUniConfiguration();
    return {
      rowDisplay: this.widget.configuration.uniConfiguration.rowDisplay,
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
      cols: utils.deepClone(this.widget.configuration.cols),
      widgetHeight: undefined,
      gridStyle: {},
      pageParent: false, //外层为页面
      parentElement: undefined,
    };
  },
  components: {
    GridFlexCol,
    GridCol,
  },
  computed: {
    colType() {
      if (this.widget.configuration.heightType == "colsEqualHeight") {
        return "GridFlexCol";
      }
      return this.widget.configuration.rowType === "flex" ? "GridFlexCol" : "GridCol";
    },
    // 默认布局，列宽超过24则另取一行
    defaultRowsData() {
      let rows = {},
        index = 0,
        span = 0;
      if (this.widget.configuration.rowType === "default") {
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
        if (this.widget.configuration.rowType === "default") {
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
          if (collaspeType.indexOf("left") > -1) {
            colsCollaspeType.push({ index: 1, type: "left" });
          }
          if (collaspeType.indexOf("right") > -1) {
            colsCollaspeType.push({ index: this.widget.configuration.cols.length - 2, type: "right" });
          }
        }
      }
      return colsCollaspeType;
    },
    gutter() {
      return this.isDividingLine ? 0 : 16;
    },
  },
  created() {
    this.initUniConfiguration();
  },
  mounted: function () {
    this.colsHeightSetting(this.isDividingLine);
  },
  methods: {
    initUniConfiguration() {
      if (!this.widget.configuration.hasOwnProperty("uniConfiguration")) {
        this.$set(this.widget.configuration, "uniConfiguration", { rowDisplay: true });
      }
    },
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
    // 各列高度设置
    colsHeightSetting(isDividingLine, isAgain) {
      if (
        (this.widget.configuration.heightType == "default" ||
          (this.widget.configuration.heightType == "define" &&
            (!this.widget.configuration.height || this.widget.configuration.height == "auto"))) &&
        isDividingLine &&
        !this.rowDisplay
      ) {
        // 更新窗口高度
        uni.getSystemInfo({
          success: (result) => {
            var windowHeight = result.windowHeight;
            // 默认高度，有分隔线，分隔线底部默认到页面底部
            const query = uni.createSelectorQuery().in(this);
            query
              .select(".w-grid")
              .boundingClientRect((data) => {
                let left = 0;
                if (data.left == 1) {
                  left = 2; // 卡片会有边框的情况
                }
                this.widgetHeight = windowHeight - data.top - left + "px";
              })
              .exec();
          },
        });
      } else if (this.widget.configuration.heightType == "define") {
        // 自定义高度
        this.widgetHeight = this.widget.configuration.height
          ? isNumber(this.widget.configuration.height)
            ? this.widget.configuration.height + "px"
            : this.widget.configuration.height
          : undefined;
        if (this.widgetHeight == "100vh") {
          // 默认高度，有分隔线，分隔线底部默认到页面底部
          const query = uni.createSelectorQuery().in(this);
          query
            .select(".w-grid")
            .boundingClientRect((data) => {
              this.gridStyle.height = `calc(100vh - ${data.top}px)`;
            })
            .exec();
          this.widgetHeight = "100%";
        } else if (this.widgetHeight && this.widgetHeight.indexOf("%") > -1) {
          // 百分比高度，依托父元素
          this.gridStyle.height = this.widgetHeight;
          this.widgetHeight = "100%";
        }
      } else {
        this.widgetHeight = undefined;
      }
    },

    // 左右折叠按钮点击后，重新计算各列宽度
    colsChange(direction, leftCollapsed, rightCollapsed) {
      if (direction == "left") {
        // 左收起展开
        this.colsChangeHandle(direction, leftCollapsed, rightCollapsed, 0, 1, 2);
      } else if (direction == "right") {
        // 右展开收起
        this.colsChangeHandle(
          direction,
          rightCollapsed,
          leftCollapsed,
          this.cols.length - 1,
          this.cols.length - 2,
          this.cols.length - 3
        );
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
        (item) => item.index == targetIndex && item.type == (direction == "left" ? "right" : "left")
      );
      if (this.colType === "GridCol") {
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
      } else if (this.colType === "GridFlexCol") {
        let span0 = this.widget.configuration.cols[index].configuration.uniflex;
        let span1 = this.widget.configuration.cols[targetIndex].configuration.uniflex;
        if (collapsed) {
          // 收起
          col.configuration.uniflex = 0;
          if (otherType && otherCollapsed) {
            // 有右收起，且是收起状态, 列宽度为实际宽度
            span1 = targetCol.configuration.uniflex;
          }
          if ((span0 || span0 === 0) && span1) {
            // 两列都有值，则两列宽度之和为总宽度
            targetCol.configuration.uniflex = span0 + span1;
          } else if (span1 && !span0) {
            // 只有第二列有值,收起的列自适应，则第二列自适应
            targetCol.configuration.uniflex = undefined;
          } else {
            targetCol.configuration.uniflex = span1;
          }
        } else {
          // 展开
          col.configuration.uniflex = span0;
          if (otherType && otherCollapsed) {
            // 有右收起，且是收起状态, 列宽度为第二列和第三列之和
            let span2 = this.widget.configuration.cols[otherIndex].configuration.flex;
            if ((span2 || span2 === 0) && span1) {
              // 两列都有值，则两列宽度之和为总宽度
              targetCol.configuration.uniflex = span2 + span1;
            } else if (span1 && !span2) {
              // 只有第二列有值,收起的列自适应，则第二列自适应
              targetCol.configuration.uniflex = undefined;
            } else {
              targetCol.configuration.uniflex = span1;
            }
          } else {
            targetCol.configuration.uniflex = span1;
          }
        }
      }
      this.collapsedDirectionHandle(direction, collapsed);
    },
    collapsedDirectionHandle(direction, collapsed) {},
  },
};
</script>
<style>
/* #ifdef APP-PLUS */
@import "./index.scss";
/* #endif */
</style>
