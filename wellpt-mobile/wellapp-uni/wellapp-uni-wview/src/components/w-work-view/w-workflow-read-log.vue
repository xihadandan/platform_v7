<!-- 阅读记录 -->
<template>
  <view class="w-workflow-read-log">
    <u-tabs :list="tabs" keyName="title" @change="onChangeTab"> </u-tabs>
    <view v-for="(tab, i) in tabs" :key="'tab_' + i">
      <scroll-view v-show="current === i" scroll-y="true" style="height: 300px">
        <uni-list v-if="data[tab.param].length">
          <uni-list-item
            v-for="(log, j) in data[tab.param]"
            :key="'tab_' + i + '_' + j"
            :title="log.userName"
            :rightText="log.readTimeString || ''"
          />
        </uni-list>
        <uni-w-empty v-else></uni-w-empty>
      </scroll-view>
    </view>
  </view>
</template>

<script>
export default {
  props: {
    data: {
      type: Object,
      default: () => {
        return {
          readUser: [], // 阅读记录
          unReadUser: [],
        };
      },
    },
  },
  data() {
    return {
      tabs: [
        {
          title: "已阅人员(" + this.data.readUser.length + ")人",
          name: "已阅人员",
          param: "readUser",
          count: this.data.readUser.length,
        },
        {
          title: "未阅人员(" + this.data.unReadUser.length + ")人",
          name: "未阅人员",
          param: "unReadUser",
          count: this.data.unReadUser.length,
        },
      ],
      current: 0,
    };
  },
  created: function () {},
  mounted: function () {},
  computed: {},
  methods: {
    onChangeTab(e) {
      let index = typeof e == "number" ? e : e.index;
      if (this.current != index) {
        this.current = index;
      }
    },
  },
  watch: {
    data: {
      deep: true,
      handler(v) {
        this.tabs[0].count = this.data.readUser && this.data.readUser.length;
        this.tabs[0].title = `${this.tabs[0].name}(${this.tabs[0].count})人`;
        this.tabs[1].count = this.data.unReadUser && this.data.unReadUser.length;
        this.tabs[1].title = `${this.tabs[1].name}(${this.tabs[1].count})人`;
      },
    },
  },
};
</script>

<style lang="scss" scoped>
.w-workflow-read-log {
}
</style>
