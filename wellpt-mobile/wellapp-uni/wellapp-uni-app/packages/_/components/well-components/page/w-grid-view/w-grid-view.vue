<template>
  <view class="w-grid-view" :class="customClassCom" :style="widget.configuration.mainStyle">
    <!-- 单页展示 -->
    <uni-grid v-if="!useSwiper" :column="numColumns" :show-border="bordered" :highlight="true" @change="gridChange">
      <uni-grid-item v-for="item in groups[0]" :index="item.index">
        <slot name="grid-item" :item="item">
          <WGridItem :item="item" :configuration="widget.configuration"></WGridItem>
        </slot>
      </uni-grid-item>
    </uni-grid>

    <!-- 九宫格展示 -->
    <swiper
      v-else
      class="swiper"
      :style="swiperStyle"
      :indicator-dots="true"
      :current="currentSwiper"
      @change="swiperChange"
    >
      <swiper-item v-for="group in groups">
        <uni-grid :column="numColumns" :highlight="true" :showBorder="bordered" @change="gridChange">
          <uni-grid-item v-for="item in group" :index="item.index">
            <slot name="grid-item" :item="item">
              <WGridItem :item="item" :configuration="widget.configuration"></WGridItem>
            </slot>
          </uni-grid-item>
        </uni-grid>
      </swiper-item>
    </swiper>
  </view>
</template>

<script>
import mixin from "../page-widget-mixin";
import { storage, appContext } from "wellapp-uni-framework";
import { each } from "lodash";
import WGridItem from "./w-grid-item.vue";
export default {
  mixins: [mixin],
  provide() {
    return {
      widgetGirdViewContext: this,
    };
  },
  components: { WGridItem },
  props: {
    showBorder: {
      type: Boolean,
      required: false,
      default: true,
    },
  },
  data() {
    return {
      bordered: this.showBorder === true,
      title: this.widget.configuration.name,
      swiperStyle: {
        height: 100 * this.widget.configuration.swiperItemRowCount + 35 + "px",
      },
      currentSwiper: 0,
      useSwiper: this.widget.configuration.enableSwiper,
      numColumns: this.widget.configuration.numColumns,
      groups: [],
    };
  },
  created() {
    let _self = this;

    let configuration = this.widget.configuration;
    let numColumns = Number(configuration.numColumns);
    let sliderGroups = this.getSliderGroups(configuration.columns, numColumns, configuration);
    _self.groups = sliderGroups.groupItems;
    if (this.widget.configuration.bordered !== undefined) {
      _self.bordered = this.widget.configuration.bordered;
    }
    _self.$nextTick(() => {
      for (let groupIndex = 0; groupIndex < _self.groups.length; groupIndex++) {
        let groupItems = _self.groups[groupIndex];
        each(groupItems, function (item) {
          _self.getBadgeCount(item);
        });
      }
    });
  },
  mounted() {},
  methods: {
    swiperChange(e) {
      this.currentSwiper = e.detail.current;
    },
    gridChange(e) {
      const _self = this;
      let item = _self.groups[_self.currentSwiper][e.detail.index];
      let eventHandler = item.eventHandler || {};
      let eventParams = item.eventParams || {};
      appContext.dispatchEvent({
        ui: _self,
        ...eventHandler,
      });

      // if (item.pageUrl) {
      //   uni.navigateTo({
      //     url: item.pageUrl,
      //   });
      //   //uni.navigateTo({url: item.eventHandler.pageUrl})
      // } else if (item.eventParams && item.eventParams.params && item.eventParams.params.pageUrl) {
      //   uni.navigateTo({
      //     url: item.eventParams.params.pageUrl,
      //   });
      // } else {
      //   console.log(JSON.stringify(item));
      // }
    },
    getSliderGroups: function (rawColumns, numColumns, config) {
      const _self = this;
      var columns = [];
      for (var i = 0; i < rawColumns.length; i++) {
        var column = rawColumns[i];
        if (column.hidden == "1") {
          continue;
        }
        // 图片路径处理
        _self.addImageUrlIfRequire(column);
        columns.push(column);
      }
      var groups = {
        groupItems: [],
        totalCount: 0,
        groupCount: 0,
      };
      // var groupColumns = [];
      var totalCount = columns.length;
      groups.totalCount = totalCount;
      let swiperItemRowCount = this.widget.configuration.swiperItemRowCount;
      if (this.useSwiper) {
        // 要判断单元格数目是否满足滑动需要
        if (totalCount <= numColumns * swiperItemRowCount) {
          this.useSwiper = false;
        }
      }

      if (this.useSwiper) {
        let idx = 0;
        for (let i = 0; i < columns.length; i++) {
          let column = columns[i];
          var groupIndex = parseInt(i / (swiperItemRowCount * numColumns));
          if (groups.groupItems[groupIndex] == null) {
            groups.groupItems[groupIndex] = [];
            idx = 0;
          }
          groups.groupCount = groupIndex + 1;
          column.index = idx++;
          groups.groupItems[groupIndex].push(column);
        }
      } else {
        groups.groupItems.push([]);
        for (let i = 0; i < columns.length; i++) {
          let column = columns[i];
          column.index = i;
          groups.groupItems[0].push(column);
        }
        // groups.groupItems.push(columns);
        groups.groupCount = 1;
      }
      return groups;
    },
    addImageUrlIfRequire: function (column) {
      if (column.icon.src && column.icon.src.startsWith("/")) {
        if (column.icon.src.startsWith("/static/")) {
          // 临时处理
          if (column.icon.src.indexOf("/static/resource/images/workbench/workbench_ ") > -1) {
            column.icon.src = column.icon.src.replace(
              "/static/resource/images/workbench/workbench_ ",
              "/static/images/icons/icon_ "
            );
          }
          // end临时处理
          column.icon.url = column.icon.src;
        } else {
          column.icon.url = storage.fillAccessResourceUrl(column.icon.src);
        }
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.w-grid-view {
  --w-grid-view-padding: 0 var(--w-margin-2xs) var(--w-margin-xs);
  background: $uni-bg-secondary-color;
  padding: var(--w-grid-view-padding);

  ::v-deep .uni-grid-item__box.uni-highlight:active {
    background-color: unset;
  }
}

.swiper {
  height: 420px;
}

.grid-item-box-row {
  flex: 1;
  // position: relative;
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex-direction: row;
  align-items: center;
  justify-content: center;
  padding: 15px 0;
}

.grid-dot {
  position: absolute;
  top: 5px;
  right: 15px;
}

.uni-grid-item {
  height: 100% !important;
}

.badge {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 1;
}

.image {
  margin: 0;
  width: 100%;
  height: 100%;
}

.text {
  font-size: 26rpx;
  margin-top: 10rpx;
}
</style>
