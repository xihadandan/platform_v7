<template>
  <view v-show="enabled" class="w-tab-bar" :class="customClassCom" v-if="isEmptyUniJsModule">
    <view v-if="enabled" class="tab-bar-wrap">
      <view class="tab-bar-container">
        <view
          class="tab-bar-item"
          :class="{ 'tab-bar-item-active': currentTabBarId == item.id }"
          v-for="(item, index) in items"
          :key="index"
          @click.stop="onTabClick(item)"
        >
          <slot name="tab-item" :item="item">
            <view class="tab-bar-icon">
              <uni-icons
                v-if="item.icon && item.icon.className"
                :type="item.icon.className"
                :color="item.icon.iconColor || '646566'"
                :size="item.icon.iconSize || '16'"
              ></uni-icons>
            </view>
            <view class="tab-bar-text">
              <text class="tab-bar-item-text">{{ item.text }}</text>
              <text class="tab-bar-item-bage-num" v-if="item.badgeNum">({{ item.badgeNum }})</text>
            </view>
          </slot>
        </view>
      </view>
    </view>
    <view class="tab-bar-fill"></view>
  </view>
  <w-widget-development v-else :widget="widget" :parent="parent"></w-widget-development>
</template>

<script>
import mixin from "../page-widget-mixin";
import { isEmpty, forEach } from "lodash";
import { mapMutations } from "vuex";
export default {
  mixins: [mixin],
  data() {
    const _self = this;
    let configuration = _self.widget.configuration || {};
    let enabled = configuration.enabled || false;
    let tabItems = configuration.tabItems || [];
    let displayItems = [];
    forEach(tabItems, function (item) {
      if (item.hidden != "1") {
        displayItems.push(item);
      }
    });
    // 存在自定义底部选项卡时，隐藏原生底部选项卡
    if (enabled && !isEmpty(displayItems)) {
      uni.hideTabBar();
      _self.setCustomTabBar(true);
    } else {
      _self.setCustomTabBar(false);
    }
    return { items: displayItems, currentTabBarId: "", enabled };
  },
  created() {
    const _self = this;
    if (!_self.enabled) {
      return;
    }

    // 默认选中上次选择的或第一个
    let tabItems = _self.items;
    if (!isEmpty(tabItems)) {
      _self.currentTabBarId = _self.getPageParameter("current_tab_bar_id") || tabItems[0].id;
      // 获取徽章数量
      forEach(tabItems, function (item) {
        _self.getBadgeCount(item);
      });
    }
  },
  methods: {
    ...mapMutations(["setCustomTabBar"]),
    onTabClick: function (item) {
      const _self = this;
      // 原生底部选项卡配置的页面无法通过uni.navigateTo切换，通过配置目标位置为当前页面处理
      if (item.target && item.target.position == "_self") {
        _self.currentTabBarId = item.id;
        _self.setPageParameter("current_tab_bar_id", item.id);
        uni.reLaunch({
          url: item.pagePath || item.pageUrl,
        });
      } else {
        uni.navigateTo({
          url: item.pagePath || item.pageUrl,
        });
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.w-tab-bar {
  .tab-bar-wrap {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: column;
    position: fixed;
    left: 0;
    right: 0;
    /* #ifdef H5 */
    left: var(--window-left);
    right: var(--window-right);
    /* #endif */
    bottom: 0;
    z-index: 10;
  }

  .tab-bar-fill {
    height: 50px;
  }

  .tab-bar-container {
    height: 50px;
    background-color: $uni-bg-secondary-color;
    border-top: 1px solid #d5d5d5;

    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;

    .tab-bar-item {
      flex: 1;
      display: flex;
      position: relative;
      justify-content: center;
      align-items: center;
      flex-direction: column;
      margin: 0 10px;
      cursor: pointer;

      .tab-bar-icon {
        width: 18px;
        height: 18px;
      }
      .tab-bar-text {
        flex: 1;
        font-size: 12px;
        margin-top: 2px;
      }

      .tab-bar-item-bage-num {
        color: #f00;
      }
    }

    .tab-bar-item-active {
      color: $uni-text-color-active;
    }
  }
}
</style>
