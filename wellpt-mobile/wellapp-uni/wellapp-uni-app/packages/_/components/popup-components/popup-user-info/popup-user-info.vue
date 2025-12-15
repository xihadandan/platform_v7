<!-- 用户信息-弹框内容 -->
<template>
  <view class="popop-content">
    <view class="flex f_y_c user-header">
      <view class="user-avatar">
        <image
          class="user-avatar-img"
          v-if="!iconShow"
          :src="'/server-api/org/user/view/photo/' + options.userId"
          @error="imageError"
        />
        <text v-else>{{ options.userName.slice(0, 1) }}</text>
      </view>
      <view class="username">{{ options.userName }}</view>
    </view>
    <view class="scroll-view">
      <scroll-view class="scroll-view-box" scroll-y="true">
        <view v-if="options.jobList" class="jobname" v-for="(job, jidx) in options.jobList" :key="jidx">
          {{ job }}
        </view>
      </scroll-view>
    </view>
    <view class="user-bottom">
      <uni-w-button block type="primary" @click="closePopup">{{ $t("global.close", "关闭") }}</uni-w-button>
    </view>
  </view>
</template>
<script>
import { isFunction } from "lodash";
export default {
  name: "PopupUserInfo",
  props: {
    options: Object,
  },
  components: {},
  computed: {},
  data() {
    return {
      iconShow: false,
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    imageError: function (e) {
      this.$set(this, "iconShow", true);
    },
    closePopup() {
      let _this = this;
      if (isFunction(_this.options.onClosePopup)) {
        _this.options.onClosePopup();
      }
      this.$emit("close");
    },
  },
};
</script>
<style lang="scss" scoped>
.popop-content {
  .scroll-view {
    /* #ifndef APP-NVUE */
    width: 100%;
    height: 400px;
    /* #endif */
    flex: 1;
    background-color: var(--w-bg-color-mobile-bg);
  }
  // 处理抽屉内容滚动
  .scroll-view-box {
    padding-top: 58px;
    // padding-bottom: 10px;
    flex: 1;
    position: absolute;
    top: 0;
    right: 0;
    bottom: 76px;
    left: 0;
  }
  .user-header {
    padding: var(--w-padding-2xs) var(--w-padding-xs);
  }

  .user-avatar {
    min-width: 32px;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background-color: var(--w-primary-color);
    color: #ffffff;
    text-align: center;
    line-height: 32px;
    font-size: 16px;

    .user-avatar-img {
      width: 100%;
      height: 100%;
      border-radius: 50%;
    }
  }
  .username {
    word-break: break-all;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    display: inline-block;
    color: var(--w-text-color-mobile);
    font-size: var(--w-font-size-xl);
    line-height: 31px;
    font-weight: bold;
    vertical-align: middle;
    padding-left: var(--w-padding-2xs);
  }
  .jobname {
    padding: var(--w-padding-xs);
    background-color: #ffffff;
    border-bottom: 1px solid var(--w-border-color-mobile);
    font-size: var(--w-font-size-md);
    color: var(--w-text-color-mobile);
  }
  .user-bottom {
    background-color: var(--w-bg-color-mobile-bg);
    padding: 0 12px 35px;
  }
}
</style>
