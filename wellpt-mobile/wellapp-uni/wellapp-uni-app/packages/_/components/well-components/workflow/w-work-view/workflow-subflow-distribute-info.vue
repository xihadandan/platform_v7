<template>
  <uni-collapse>
    <uni-collapse-item :title="title">
      <scroll-view style="height: 100%; max-height: 300px" scroll-y="true">
        <uni-list>
          <uni-list-item
            v-for="(item, index) in dataList"
            :key="index"
            :title="item.distributorName"
            :rightText="item.createTime"
            :note="item.content"
            clickable
            @click="itemClick($event, item)"
          ></uni-list-item>
        </uni-list>
      </scroll-view>
    </uni-collapse-item>
  </uni-collapse>
</template>

<script>
import { isEmpty } from "lodash";
export default {
  props: {
    workView: Object,
    subTaskData: Object,
    distributeInfo: Object,
    index: Number,
  },
  data() {
    return {
      dataList: [],
    };
  },
  created() {
    const _self = this;
    _self.dataList = _self.distributeInfo.distributeInfos || [];
  },
  methods: {
    itemClick(event, item) {
      console.log(item);
    },
  },
  computed: {
    title() {
      const _self = this;
      if (!isEmpty(_self.distributeInfo.title)) {
        return _self.distributeInfo.title;
      }
      return "信息分发" + (_self.index + 1);
    },
  },
};
</script>

<style scoped></style>
