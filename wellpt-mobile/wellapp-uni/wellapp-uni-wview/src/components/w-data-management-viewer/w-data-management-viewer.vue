<template>
  <view class="w-data-management-viewer" :class="customClass" v-if="isEmptyUniJsModule">
    <view class="w-data-viewer" v-for="item in items" :key="item.id">
      <w-list-view
        ref="listView"
        v-if="item.wtype == 'wMobileListView' || item.wtype == 'WidgetUniListView'"
        :widget="item"
        @itemClick="listViewItemClick"
        @buttonClick="listViewButtonClick"
        :parent="widget"
      ></w-list-view>
      <view v-else> 数据管理查询器不支持的组件: {{ item.title }}, {{ item.wtype }} </view>
    </view>
  </view>
  <w-widget-development v-else :widget="widget" :parent="parent"></w-widget-development>
</template>

<script>
import mixin from "../page-widget-mixin";
import { appContext } from "wellapp-uni-framework";
import { isArray, each as forEach } from "lodash";
export default {
  mixins: [mixin],
  data() {
    const _self = this;
    return {
      items: _self.widget.items || [],
    };
  },
  methods: {
    listViewItemClick: function (item, editState) {
      const _self = this;
      if (editState) {
        return;
      }
      let dmsId = _self.widget.id;
      let configuration = _self.widget.configuration;
      _self.setPageParameter("dmsId", dmsId);
      _self.setPageParameter("configuration", configuration);
      _self.setPageParameter("item", item);
      _self.setPageParameter("extraParams", {});
      uni.navigateTo({
        url: "/uni_modules/w-app/pages/dms/dms_dyform_view",
      });
    },
    listViewButtonClick: function (button, selection) {
      const _self = this;
      let dmsId = _self.widget.id;
      let configuration = _self.widget.configuration;
      let eventHandler = button.eventHandler || {};
      let eventParams = button.eventParams || {};
      appContext.startApp({
        ui: _self,
        appId: eventHandler.id,
        appType: eventHandler.type,
        appPath: eventHandler.path,
        params: eventParams.params,
        dmsId,
        pageUrl: button.pageUrl,
        appFunction: button.appFunction,
        data: selection,
        rowData: selection, // 兼容pc端数据格式
        pageParameters: {
          dmsId,
          configuration,
        },
      });
    },
    // 刷新
    refresh: function (options) {
      let listView = this.$refs.listView;
      if (listView.refresh) {
        listView.refresh(options);
      } else if (isArray(listView)) {
        forEach(listView, function (view) {
          if (view.refresh) {
            view.refresh(options);
          }
        });
      }
    },
  },
};
</script>

<style></style>
