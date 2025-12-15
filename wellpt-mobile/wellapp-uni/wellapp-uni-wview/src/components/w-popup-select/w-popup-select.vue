<template>
  <view class="popup-select">
    <view v-if="title" class="popup-title">
      <text class="popup-title-text">{{ title }}</text>
    </view>
    <view class="popup-content">
      <scroll-view class="popup-content-scroll-view" :class="{ 'hide-confirm-btn': !multiple }" scroll-y="true">
        <view class="uni-list">
          <checkbox-group v-if="multiple" @change="onCheckboxChange">
            <label class="uni-list-cell uni-list-cell-pd" v-for="(item, index) in items" :key="index">
              <view style="display: none">
                <checkbox :value="item.value" :checked="item.checked" />
              </view>
              <view class="popup-check-item">
                <view style="flex: 1">{{ item.text }}</view>
                <view class="popup-check-icon" v-if="item.checked">
                  <uni-icons type="checkmarkempty" :size="22" color="#2979ff" />
                </view>
              </view>
            </label>
          </checkbox-group>
          <radio-group v-else @change="onRadioChange">
            <label class="uni-list-cell uni-list-cell-pd" v-for="(item, index) in items" :key="index">
              <view style="display: none">
                <radio :value="item.value" :checked="item.checked" />
              </view>
              <view class="popup-check-item">
                <view style="flex: 1">{{ item.text }}</view>
                <view class="popup-check-icon" v-if="item.checked">
                  <uni-icons type="checkmarkempty" :size="22" color="#2979ff" />
                </view>
              </view>
            </label>
          </radio-group>
        </view>
      </scroll-view>
    </view>
    <view v-if="multiple" class="popup-button-box">
      <button class="popup-button" @tap="onOk">确定</button>
    </view>
  </view>
</template>

<script>
export default {
  props: {
    title: String,
    items: Array,
    multiple: Boolean,
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
  },
};
</script>

<style lang="scss" scoped>
.popup-select {
  background-color: $uni-bg-secondary-color;
  height: 300px;

  .uni-list {
    background-color: $uni-bg-secondary-color;
  }

  .popup-title {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    align-items: center;
    justify-content: center;
    height: 40px;
    border-bottom: 1px solid $uni-border-3;
  }

  .popup-title-text {
    font-size: 16px;
    color: $uni-text-color;
    font-weight: bold;
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

    .popup-check-item {
      display: flex;
      flex-direction: row;
      width: 100%;
      height: 26px;
      justify-content: center;
      align-items: center;

      .popup-check-icon {
        width: 20px;
        padding-right: 6px;
      }
    }
  }

  .popup-button-box {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    padding: 10px 15px;

    .popup-button {
      flex: 1;
      border-radius: 50px;
      background-color: $uni-primary;
      color: #fff;
      font-size: 16px;
    }

    .popup-button::after {
      border-radius: 50px;
    }
  }
}
</style>
