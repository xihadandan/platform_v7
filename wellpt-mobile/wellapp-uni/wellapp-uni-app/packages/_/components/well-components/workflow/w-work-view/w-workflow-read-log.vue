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
            :title="log.userName || log"
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
          title: this.$t(
            "WorkflowWork.workView.readUserCount",
            { count: this.data.readUser.length },
            "已阅人员(" + this.data.readUser.length + ")人"
          ),
          name: "readUserCount",
          param: "readUser",
          count: this.data.readUser.length,
        },
        {
          title: this.$t(
            "WorkflowWork.workView.unreadUserCount",
            { count: this.data.unReadUser.length },
            "已阅人员(" + this.data.unReadUser.length + ")人"
          ),
          name: "unreadUserCount",
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
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
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
        this.tabs[0].title = this.$t(
          "WorkflowWork.workView." + this.tabs[0].name,
          { count: this.tabs[0].count },
          undefined
        );
        this.tabs[1].count = this.data.unReadUser && this.data.unReadUser.length;
        this.tabs[1].title = this.$t(
          "WorkflowWork.workView." + this.tabs[1].name,
          { count: this.tabs[1].count },
          undefined
        );
      },
    },
  },
};
</script>

<style lang="scss" scoped>
.w-workflow-read-log {
}
</style>
