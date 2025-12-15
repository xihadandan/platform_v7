<template>
  <uni-collapse>
    <uni-collapse-item :title="title">
      <scroll-view style="height: 100%; max-height: 300px" scroll-y="true">
        <uni-list>
          <uni-list-item
            v-for="(item, index) in dataList"
            :key="index"
            :title="item.assigneeName"
            :rightText="item.action"
            :note="item.opinionText"
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
    relateOperation: Object,
    index: Number,
  },
  data() {
    return {
      dataList: [],
    };
  },
  created() {
    const _self = this;
    _self.dataList = _self.relateOperation.operations || [];
  },
  methods: {
    itemClick(event, item) {
      console.log(item);
    },
  },
  computed: {
    title() {
      const _self = this;
      if (!isEmpty(_self.relateOperation.title)) {
        return _self.relateOperation.title;
      }
      return "操作记录" + (_self.index + 1);
    },
  },
};
</script>

<style scoped></style>
