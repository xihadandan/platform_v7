<template>
  <view class="w-drawer">
    <uni-drawer ref="drawer" mode="right" :mask-click="false">
      <view class="scroll-view">
        <scroll-view
          scroll-y="true"
          style="height: calc(100vh - var(--window-top) - var(--status-bar-height) - var(--window-bottom))"
        >
        </scroll-view>
      </view>
    </uni-drawer>
  </view>
</template>
<style></style>
<script>
import mixin from "../page-widget-mixin";
import { isEmpty } from "lodash";
import { storage, appContext } from "wellapp-uni-framework";
export default {
  mixins: [mixin],
  name: "w-drawer",
  props: {},
  components: {},
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {},
};
</script>
