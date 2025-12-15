<template>
  <uni-collapse>
    <uni-collapse-item :title="title">
      <scroll-view style="height: 100%; max-height: 300px" scroll-y="true">
        <uni-list>
          <uni-list-item
            v-for="(item, index) in dataList"
            :key="index"
            :title="item.todoName"
            :rightText="item.currentTodoUserName"
            :note="item.currentTaskName"
            clickable
            @click="itemClick($event, item)"
          ></uni-list-item>
        </uni-list>
      </scroll-view>
    </uni-collapse-item>
  </uni-collapse>
</template>

<script>
import { isEmpty, each as forEach } from "lodash";
export default {
  props: {
    workView: Object,
    subTaskData: Object,
    shareData: Object,
    index: Number,
  },
  data() {
    return {
      dataList: [],
    };
  },
  created() {
    const _self = this;
    let dataList = [];
    let shareItems = _self.shareData.shareItems || [];
    forEach(shareItems, function (item, index) {
      let record = Object.assign({}, item);
      if (item.columnValues) {
        forEach(item.columnValues, function (columnValue) {
          record[columnValue.index] = columnValue.value;
        });
      }
      record._order = index + 1;
      dataList.push(record);
    });
    _self.dataList = dataList;
  },
  methods: {
    itemClick(event, item) {
      var _self = this;
      // 监听子流程操作事件
      uni.$off("subflowActionSuccess");
      uni.$on("subflowActionSuccess", (result) => {
        uni.showToast({ title: result.message });
        setTimeout(() => {
          uni.navigateBack({
            delta: 1,
          });
          _self.onSubflowActionSuccess(result);
        }, 2000);
      });

      _self.setPageParameter("workView", _self.workView);
      _self.setPageParameter("subTaskData", _self.subTaskData);
      _self.setPageParameter("shareData", _self.shareData);
      _self.setPageParameter("item", item);
      uni.navigateTo({
        url: "/uni_modules/w-app/pages/workflow/subflow_details",
      });
    },
    // 子流程操作成功事件处理
    onSubflowActionSuccess(result) {
      const _self = this;
      let action = result.action;
      // uni.showToast({ title: result.message });

      switch (action) {
        case "subflowRemind": // 子流程催办
          _self.workView.reload();
          break;
        case "subflowLimitTime": // 子流程协办时限
          _self.workView.reload();
          break;
        case "subflowRedo": // 子流程重办
          _self.workView.reload();
          break;
        case "subflowStop": // 子流程终止
          if (result.stopSelf) {
            _self.emitRefreshAndGoBackLater("subflowStop");
          } else {
            _self.workView.reload();
          }
          break;
      }
    },
  },
  computed: {
    title() {
      const _self = this;
      if (!isEmpty(_self.shareData.title)) {
        return _self.shareData.title;
      }
      return "承办情况" + (_self.index + 1);
    },
  },
};
</script>

<style scoped></style>
