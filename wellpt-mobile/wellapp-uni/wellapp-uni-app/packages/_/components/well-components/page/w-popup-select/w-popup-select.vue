<template>
  <view class="popup-select" :style="{ background: this.background, borderRadius: this.borderRadius }">
    <view v-if="title" class="popup-title">
      <view class="left">
        <slot name="title-left"></slot>
      </view>
      <view class="center">
        <slot name="title-center">
          {{ title }}
        </slot>
      </view>
      <view class="right">
        <slot name="title-right">
          <uni-w-button type="text" @click="onClose" icon="iconfont icon-ptkj-dacha-xiao"></uni-w-button>
        </slot>
      </view>
    </view>
    <view class="popup-content">
      <scroll-view class="popup-content-scroll-view" :class="{ 'hide-confirm-btn': !multiple }" scroll-y="true">
        <view class="popup-content-list">
          <checkbox-group v-if="multiple" @change="onCheckboxChange">
            <label class="popup-content-item" v-for="(item, index) in items" :key="index">
              <view style="display: none">
                <checkbox :value="item.value" :checked="item.checked" />
              </view>
              <view :class="{ 'popup-check-item': true, checked: item.checked }">
                <view style="flex: 1">{{ item.text }}</view>
                <view class="popup-check-icon" v-if="item.checked">
                  <uni-icons type="checkmarkempty" :size="22" color="var(--w-primary-color)" />
                </view>
              </view>
            </label>
          </checkbox-group>
          <radio-group v-else @change="onRadioChange">
            <label class="popup-content-item" v-for="(item, index) in items" :key="index">
              <view style="display: none">
                <radio :value="item.value" :checked="item.checked" />
              </view>
              <view :class="{ 'popup-check-item': true, checked: item.checked }">
                <view style="flex: 1">{{ item.text }}</view>
                <view class="popup-check-icon" v-if="item.checked">
                  <uni-icons type="checkmarkempty" :size="22" color="var(--w-primary-color)" />
                </view>
              </view>
            </label>
          </radio-group>
        </view>
      </scroll-view>
    </view>
    <view v-if="multiple" class="popup-button-box">
      <uni-w-button block type="primary" @click="onOk">{{ $t("global.confirm", "确定") }}</uni-w-button>
    </view>
  </view>
</template>

<script>
export default {
  props: {
    title: String,
    items: Array,
    multiple: Boolean,
    background: String,
    borderRadius: String,
  },
  methods: {
    onCheckboxChange: function (event) {
      var _self = this;
      var items = _self.items;
      var values = event.detail.value;
      for (let i = 0; i < items.length; i++) {
        const item = items[i];
        if (values.indexOf(item.value) >= 0) {
          // item.checked = true;
          _self.$set(item, "checked", true);
        } else {
          // item.checked = false;
          _self.$set(item, "checked", false);
        }
      }
    },
    onRadioChange: function (event) {
      var _self = this;
      var items = _self.items;
      for (let i = 0; i < items.length; i++) {
        const item = items[i];
        if (item.value === event.detail.value) {
          // item.checked = true;
          _self.$set(item, "checked", true);
        } else {
          // item.checked = false;
          _self.$set(item, "checked", false);
        }
      }
      // 单选关闭弹出框
      _self.onOk();
    },
    onOk: function () {
      let _self = this;
      let items = _self.items;
      let checkedValues = [];
      let displayValues = [];
      for (var i = 0; i < items.length; i++) {
        const item = items[i];
        if (item.checked === true) {
          checkedValues.push(item.value);
          displayValues.push(item.text);
        }
      }
      _self.$emit("ok", checkedValues.join(";"), displayValues.join(";"));
    },
    onClose: function () {
      this.$emit("close");
    },
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
  },
};
</script>

<style lang="scss" scoped>
.popup-select {
  // background-color: $uni-bg-secondary-color;
  height: 300px;

  .popup-title {
    padding: 14px 5rem 8px;
    font-size: var(--w-font-size-base);
    display: flex;
    align-items: center;
    justify-content: space-between;
    position: relative;

    .left {
      position: absolute;
      left: 10px;
    }

    .center {
      flex: 1;
      text-align: center;
      font-size: var(--w-font-size-lg);
      color: var(--w-text-color-mobile);
      font-weight: bold;
      word-break: break-all;
    }

    .right {
      position: absolute;
      right: 10px;
    }
  }

  .popup-content {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    justify-content: center;
    // padding-top: 10px;

    .popup-content-scroll-view {
      height: 200px;
    }

    .hide-confirm-btn {
      height: 250px;
    }

    .popup-content-list {
      .popup-content-item {
        display: block;
        font-size: var(--w-font-size-md);
        color: var(--w-text-color-mobile);
        padding: var(--w-padding-xs);
        border-bottom: 1px solid var(--w-border-color-mobile);

        .popup-check-item {
          display: flex;
          flex-direction: row;
          width: 100%;
          // height: 26px;
          justify-content: center;
          align-items: center;
          .popup-check-icon {
            width: 20px;
            padding-right: 6px;
          }

          &.checked {
            color: var(--w-primary-color);
          }
        }
      }
    }
  }

  .popup-button-box {
    padding: 8px 12px;
  }
}
</style>
