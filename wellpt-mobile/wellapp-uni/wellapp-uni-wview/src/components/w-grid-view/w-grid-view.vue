<template>
  <view class="w-grid-view" :class="customClass">
    <!-- 单页展示 -->
    <uni-grid v-if="!useSwiper" :column="numColumns" :show-border="bordered" :highlight="true" @change="gridChange">
      <uni-grid-item v-for="item in groups[0]" :index="item.index">
        <slot name="grid-item" :item="item">
          <view class="grid-item-box">
            <uni-badge v-if="item.badgeNum" class="badge" :text="item.badgeNum" type="primary" />
            <view class="icon-box" v-if="item.icon.src != undefined">
              <image v-if="item.icon.src.startsWith('/')" class="image" mode="aspectFit" :src="item.icon.url" />
              <uni-icons
                v-else
                :custom-prefix="item.icon.src.startsWith('iconfont') ? 'iconfont' : undefined"
                :type="item.icon.src"
                :size="item.icon.fontSize"
                :color="item.icon.color"
              ></uni-icons>
            </view>
            <text class="text" v-if="item.text">{{ item.text }}</text>
          </view>
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
              <view class="grid-item-box">
                <uni-badge v-if="item.badgeNum" class="badge" :text="item.badgeNum" type="primary" />
                <view class="icon-box">
                  <image v-if="item.icon.src.startsWith('/')" class="image" mode="aspectFit" :src="item.icon.url" />
                  <uni-icons
                    v-else
                    :custom-prefix="item.icon.src.startsWith('iconfont') ? 'iconfont' : undefined"
                    :type="item.icon.src"
                    :color="item.icon.color"
                    :size="item.icon.fontSize"
                  ></uni-icons>
                </view>
                <text class="text" v-if="item.text">{{ item.text }}</text>
              </view>
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
export default {
  mixins: [mixin],
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
    var _self = this;

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
        _.each(groupItems, function (item) {
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
        groups.groupItems.push(columns);
        groups.groupCount = 1;
      }
      return groups;
    },
    addImageUrlIfRequire: function (column) {
      if (column.icon.src && column.icon.src.startsWith("/")) {
        if (column.icon.src.startsWith("/static/")) {
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
  background: $uni-bg-secondary-color;
}

.swiper {
  height: 420px;
}

.grid-item-box {
  flex: 1;
  // position: relative;
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px 0;

  .icon-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 38px;
    height: 38px;
  }

  .icon-box-square {
  }

  .icon-box-corner {
    border-radius: 15px;
  }

  .icon-box-circular {
    border-radius: 50%;
  }

  .icon {
    color: $uni-icon-color !important;
  }

  .text {
    color: $uni-text-color;
  }
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
