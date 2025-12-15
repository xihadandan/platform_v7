<template>
  <view class="w-tiles" :class="customClassCom" v-if="isEmptyUniJsModule">
    <!-- 头部标题 -->
    <slot name="tile-title" :title="title">
      <w-section v-if="title" :title="title" type="line">
        <template v-if="widget.configuration && widget.configuration.iconClassName" slot="header">
          <uni-icons class="title-icon uni-mr-3 uni-pb-1" :type="widget.configuration.iconClassName"></uni-icons>
        </template>
      </w-section>
    </slot>
    <!-- 磁贴内容 -->
    <view class="tile-content">
      <uni-grid
        :class="'tiles-' + mode"
        :column="numColumns"
        :show-border="false"
        :highlight="true"
        @change="gridChange"
      >
        <uni-grid-item v-for="(item, index) in items" :index="index" :key="index">
          <slot name="tile-item" :item="item">
            <view
              class="tile-item-box"
              :style="{ backgroundColor: (item.backgroundColor && item.backgroundColor.iconColor) || '#fff' }"
            >
              <view class="tile-container">
                <view class="icon-box">
                  <image v-if="item.imageUrl" class="image" mode="aspectFit" :src="item.imageUrl" />
                  <uni-icons
                    v-if="item.icon && item.icon.className"
                    :type="item.icon.className"
                    :color="item.icon.iconColor || '5e6d82'"
                    :size="mode == 'standard' ? item.icon.iconSize || '30' : parseInt(item.icon.iconSize || '30') + 14"
                  ></uni-icons>
                </view>
                <view class="tile-text-box">
                  <view class="tile-title"> {{ item.title }}</view>
                  <view class="tile-subtitle">{{ item.subtitle }}</view>
                </view>
              </view>
            </view>
          </slot>
        </uni-grid-item>
      </uni-grid>
    </view>
  </view>
  <w-widget-development v-else :widget="widget" :parent="parent"></w-widget-development>
</template>

<script>
import mixin from "../page-widget-mixin";
import { isEmpty, each as forEach } from "lodash";
import { storage, appContext } from "wellapp-uni-framework";
export default {
  mixins: [mixin],
  data() {
    let configuration = this.widget.configuration || {};
    let mode = configuration.mode || "standard";
    return {
      title: configuration.name,
      mode,
      numColumns: mode == "large" ? 1 : 2,
      items: [],
    };
  },
  created() {
    var _self = this;
    let configuration = this.widget.configuration;
    let tiles = configuration.tiles || [];
    forEach(tiles, function (tile) {
      _self.addImageUrlIfRequire(tile);
    });
    _self.items = tiles;
  },
  methods: {
    gridChange(e) {
      const _self = this;
      let item = _self.items[e.detail.index];
      let eventHandler = item.eventHandler || {};
      let eventParams = item.eventParams || {};
      appContext.startApp({
        ui: _self,
        appId: eventHandler.id,
        appType: eventHandler.type,
        appPath: eventHandler.path,
        params: eventParams.params,
        pageUrl: item.pageUrl,
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
    addImageUrlIfRequire: function (column) {
      const _self = this;
      var imageUrl = (column.image || {}).iconPath;
      // 本地图片
      if (imageUrl && imageUrl.startsWith("/static/")) {
        // console.log(imageUrl);
      } else if (!isEmpty(imageUrl)) {
        // 服务器图片
        imageUrl = storage.fillAccessResourceUrl(imageUrl);
      }
      column.imageUrl = imageUrl;
    },
  },
};
</script>

<style lang="scss" scoped>
.w-tiles {
  background: $uni-bg-secondary-color;

  .tile-content {
    margin: 0 6px;
  }

  .tiles-standard {
    .uni-grid-item {
      height: 75px !important;
    }
  }

  .tile-item-box {
    flex: 1;
    // position: relative;
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin: 0 5px 10px;
    border-radius: 4px;

    .tile-container {
      display: flex;
      flex-direction: row;
      justify-content: flex-start;
      align-items: center;
      width: 100%;
      height: 100%;

      .icon-box {
        width: 50px;
        height: 50px;

        display: flex;
        justify-content: center;
        align-items: center;

        .image {
          width: 100%;
          height: 100%;
        }
      }

      .tile-text-box {
        -webkit-flex: 1;
        flex: 1;
        display: flex;
        flex-direction: column;
        -webkit-justify-content: flex-start;
        justify-content: flex-start;
      }

      .tile-title {
        color: $uni-text-color;
        font-size: 16px;
      }

      .tile-subtitle {
        color: $uni-text-color-grey;
        font-size: 10px;
      }
    }
  }

  // 大磁贴
  .tiles-large {
    .uni-grid-item {
      height: 95px !important;
    }

    .tile-item-box {
      .tile-container {
        .icon-box {
          width: 70px;
          height: 70px;
        }

        .tile-title {
          font-size: 20px;
        }
        .tile-subtitle {
          margin-top: 5px;
          font-size: 13px;
        }
      }
    }
  }

  // 方形磁贴
  .tiles-square {
    .uni-grid-item {
    }

    .tile-item-box {
      .tile-container {
        .icon-box {
          flex: 0.7;
          // width: 70px;
          // height: 70px;
        }

        .tile-title {
          font-size: 22px;
        }
        .tile-subtitle {
          margin-top: 10px;
          font-size: 14px;
        }
      }
    }
  }
}
</style>
