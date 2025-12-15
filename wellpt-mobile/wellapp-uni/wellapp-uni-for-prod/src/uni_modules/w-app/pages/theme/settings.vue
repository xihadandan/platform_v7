<template>
  <view class="theme-settings" :style="theme">
    <scroll-view scroll-y="true">
      <view class="uni-list">
        <radio-group @change="onRadioChange">
          <label class="uni-list-cell uni-list-cell-pd" v-for="(item, index) in themeList" :key="index">
            <view style="display: none">
              <radio :value="item.value" :checked="item.checked" />
            </view>
            <view class="check-item">
              <view style="flex: 1">{{ item.name }}</view>
              <view class="check-icon" v-if="item.value == themeName">
                <uni-icons type="checkmarkempty" :size="22" color="#2979ff" />
              </view>
            </view>
          </label>
        </radio-group>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { mapState, mapMutations } from "vuex";
export default {
  data() {
    return {};
  },
  computed: mapState(["themeList", "themeName"]),
  methods: {
    ...mapMutations(["setTheme"]),
    onRadioChange: function (event) {
      this.setTheme(event.detail.value);
    },
  },
};
</script>

<style lang="scss" scoped>
.theme-settings {
  width: 100%;
  background: $uni-bg-color;

  .uni-list {
    background-color: $uni-bg-secondary-color;
    color: $uni-text-color;
  }

  .check-item {
    display: flex;
    flex-direction: row;
    width: 100%;
    height: 26px;
    justify-content: center;
    align-items: center;

    .check-icon {
      width: 20px;
      padding-right: 6px;
    }
  }
}
</style>
