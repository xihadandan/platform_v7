<template>
  <view class="w-nav" :class="customClass" v-if="isEmptyUniJsModule">
    <!-- 有包含子结点的导航 -->
    <view v-if="hasChildren">
      <view v-for="(item, index) in navItems" :index="index" :key="index">
        <!-- 有子结点的导航 -->
        <uni-collapse v-if="item.children && item.children.length > 0" :value="isExpand">
          <uni-collapse-item :title="item.name" :show-animation="true">
            <!-- 子导航 -->
            <uni-list>
              <template v-if="isCardStyle">
                <uni-card
                  class="nav-card-item"
                  v-for="(subItem, subIndex) in item.children"
                  :index="subIndex"
                  :key="subIndex"
                  @click="onClick($event, subItem)"
                >
                  <slot name="nav-item" :item="subItem">
                    <nav-card-content :item="subItem" />
                  </slot>
                </uni-card>
              </template>
              <template v-else>
                <template v-for="(subItem, subIndex) in item.children" :index="subIndex">
                  <uni-list-item
                    class="nav-list-item"
                    :key="subIndex"
                    showArrow
                    clickable
                    @click="onClick($event, subItem)"
                  >
                    <template slot="body">
                      <slot name="nav-item" :item="subItem">
                        <nav-list-item-content :item="subItem" :thumbSize="item.thumbSize || 'sm'" />
                      </slot>
                    </template>
                  </uni-list-item>
                </template>
              </template>
            </uni-list>
          </uni-collapse-item>
        </uni-collapse>
        <!-- 没有子结点的导航 -->
        <template v-else>
          <template v-if="isCardStyle">
            <uni-card class="nav-card-item" @click="onClick($event, item)">
              <slot name="nav-item" :item="item">
                <nav-card-content :item="item" />
              </slot>
            </uni-card>
          </template>
          <template v-else>
            <uni-list>
              <uni-list-item class="nav-list-item" showArrow clickable @click="onClick($event, item)">
                <template slot="body">
                  <slot name="nav-item" :item="item">
                    <nav-list-item-content :item="item" />
                  </slot>
                </template>
              </uni-list-item>
            </uni-list>
          </template>
        </template>
      </view>
    </view>
    <!-- 全没有子结点的导航 -->
    <template v-else>
      <template v-if="isCardStyle">
        <uni-card
          class="nav-card-item"
          v-for="(item, index) in navItems"
          :index="index"
          :key="index"
          @click="onClick($event, item)"
        >
          <slot name="nav-item" :item="item">
            <nav-card-content :item="item" />
          </slot>
        </uni-card>
      </template>
      <template v-else>
        <uni-list>
          <template v-for="(item, index) in navItems" :index="index">
            <uni-list-item class="nav-list-item" :key="index" showArrow clickable @click="onClick($event, item)">
              <template slot="body">
                <slot name="nav-item" :item="item">
                  <nav-list-item-content :item="item" />
                </slot>
              </template>
            </uni-list-item>
          </template>
        </uni-list>
      </template>
    </template>
  </view>
  <w-widget-development v-else :widget="widget" :parent="parent"></w-widget-development>
</template>

<script>
import mixin from "../page-widget-mixin";
import navCardContent from "./nav-card-content.vue";
import navListItemContent from "./nav-list-item-content.vue";
import { each } from "lodash";
export default {
  mixins: [mixin],
  components: { navCardContent, navListItemContent },
  data() {
    return {
      navItems: [],
      hasChildren: false,
    };
  },
  created() {
    var _self = this;
    let configuration = _self.widget.configuration;
    let navItems = [];
    // 自定义导航
    if ((!configuration.navType && configuration.nav) || configuration.navType == "2") {
      for (var i = 0; i < configuration.nav.length; i++) {
        var item = configuration.nav[i];
        if (item.hidden) {
          continue;
        }
        if (item.extraIcon == null && item.data && item.data.icon) {
          item.extraIcon = { type: item.data.icon, size: 22 };
        }
        // 是否包含子菜单
        var children = item.children;
        if (children && children.length) {
          for (var j = 0; j < children.length; j++) {
            var child = children[j];
            console.log(child.extraIcon);
            if (child.extraIcon == null && child.data && child.data.icon) {
              child.extraIcon = { type: child.data.icon, size: 22 };
            }
          }
          _self.hasChildren = true;
        }
        navItems.push(item);
      }
      _self.navItemsDeal(navItems, configuration);
    } else if (configuration.navType == "3") {
      _self._loadTree(null, null, configuration.isShowRoot, configuration, function (navItems) {
        _self.navItemsDeal(navItems, configuration);
      });
    }
  },
  computed: {
    isExpand: function () {
      let configuration = this.widget.configuration || {};
      return configuration.autoToggle ? ["1"] : ["0"];
    },
    // 是否卡片风格
    isCardStyle: function () {
      let configuration = this.widget.configuration || {};
      return configuration.navStyle == "2";
    },
  },
  methods: {
    onClick: function (e, item) {
      console.log("navigateTo: " + item.pageUrl);
      uni.navigateTo({
        url: item.pageUrl,
      });
    },
    navItemsDeal: function (navItems, configuration) {
      let _self = this;
      // 不显示根结点
      if (_self.hasChildren && !configuration.isShowRoot) {
        var newNavs = [];
        for (var index = 0; index < navItems.length; index++) {
          var parentItem = navItems[index];
          let children = parentItem.children;
          if (children && children.length > 0) {
            for (let j = 0; j < children.length; j++) {
              newNavs.push(children[j]);
            }
          }
        }
        navItems = newNavs;
        _self.hasChildren = false;
      }

      // 导航权限处理
      _self.grantedFilter(navItems, function (items) {
        _self.navItems = items;

        // 子导航
        items.forEach(function (item) {
          if (item.children && item.children.length > 0) {
            _self.grantedFilter(item.children);
          }
        });
      });
    },
    // 导航权限处理
    grantedFilter: function (navItems, callback) {
      const _self = this;
      let checkGrantedItems = [];
      navItems.forEach(function (item) {
        if (item.data && item.data.eventHanlderId) {
          checkGrantedItems.push({
            object: item.data.eventHanlderId,
            functionType: "appProductIntegration",
          });
        }
      });
      if (checkGrantedItems.length > 0) {
        uni.request({
          service: "securityAuditFacadeService.isGranted",
          data: [checkGrantedItems],
          success: function (res) {
            let grantedIds = res.data.data;
            for (let index = 0; index < navItems.length; index++) {
              let item = navItems[index];
              if (item.data && item.data.eventHanlderId) {
                if (!grantedIds[item.data.eventHanlderId]) {
                  navItems.splice(index, 1);
                }
              }
            }
            if (callback) {
              callback.call(_self, navItems);
            }
          },
        });
      } else {
        if (callback) {
          callback.call(_self, navItems);
        }
      }
    },
    _loadTree: function (parentId, searchText, isShowRoot, configuration, callback) {
      var nav = [];
      var params = {};
      let _self = this;
      if (parentId) {
        params.parentId = parentId;
      }
      params.dataProvider = configuration.navInterface;
      params.nodeTypeInfo = JSON.stringify(configuration.nodeTypeInfo);
      uni.request({
        url: "/basicdata/treecomponent/loadTree",
        method: "POST",
        header: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        },
        data: params,
        contentType: "application/json",
        dataType: "json",
        async: false,
        success: function (res) {
          let data = res.data;
          if (isShowRoot === false) {
            each(data, function (item) {
              nav = nav.concat(item.children);
              _self.hasChildren = true;
            });
          } else {
            nav = data;
          }
          each(nav, function (item) {
            if (!_self.hasChildren && item.children.length > 0) {
              _self.hasChildren = true;
            }
          });
          callback(nav);
        },
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.w-nav {
  .nav-card-item {
    margin: 6px 8px !important;
    background-color: $uni-bg-secondary-color;
  }

  .nav-list-item {
    background-color: $uni-bg-secondary-color;
  }

  .nav-card-content {
    display: flex;
  }
}
</style>
