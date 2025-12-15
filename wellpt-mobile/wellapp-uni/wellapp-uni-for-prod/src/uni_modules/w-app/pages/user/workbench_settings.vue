<template>
  <view class="workbench-settings" :style="theme">
    <scroll-view scroll-y="true">
      <view class="uni-list">
        <radio-group @change="onRadioChange">
          <label class="uni-list-cell uni-list-cell-pd" v-for="(item, index1) in workbenches" :key="index1">
            <view style="display: none">
              <radio :value="item.id" :checked="item.checked" />
            </view>
            <view class="check-item">
              <view style="flex: 1">{{ item.name }}</view>
              <view class="check-icon" v-if="item.id == selectedPageUuid">
                <uni-icons type="checkmarkempty" :size="22" color="#2979ff" />
              </view>
            </view>
          </label>
        </radio-group>
      </view>
    </scroll-view>
    <view v-show="selectedPageUuid" class="ws-button-group">
      <view class="ws-button uni-border-left" @tap="onOk">
        <text class="ws-button-text ws-button-color">进入工作台</text>
      </view>
    </view>
  </view>
</template>

<script>
import { mapState, mapMutations } from "vuex";
import { storage } from "wellapp-uni-framework";
import { map, isEmpty } from "lodash";
export default {
  data() {
    return {
      workbenches: [],
      selectedPageUuid: "",
    };
  },
  created() {
    const _self = this;
    let workbenchAppPiPath = _self.workbenchAppPiPath;
    if (workbenchAppPiPath) {
      uni.request({
        service: "appPageDefinitionMgr.listFacadeByAppPath",
        method: "POST",
        data: [workbenchAppPiPath],
        success: function (result) {
          let dataList = result.data.data || [];
          dataList.sort(function (a, b) {
            let code1 = a.code || "";
            let code2 = b.code || "";
            return code1 > code2 ? 1 : -1;
          });
          _self.workbenches = map(dataList, function (pageInfo) {
            return {
              name: pageInfo.name,
              id: pageInfo.uuid,
              checked: _self.workbenchPageUuid == pageInfo.uuid,
            };
          });
        },
      });
    }

    _self.selectedPageUuid = _self.workbenchPageUuid;
  },
  computed: mapState(["workbenchAppPiPath", "workbenchPageUuid"]),
  methods: {
    ...mapMutations(["setWorkbenchPageUuid", "setCustomTabBar"]),
    onRadioChange: function (event) {
      const _self = this;
      _self.selectedPageUuid = event.detail.value;
      _self.setWorkbenchPageUuid(_self.selectedPageUuid);
      storage.setStorageSync("workbenchPageUuid", _self.selectedPageUuid);
    },
    onOk: function () {
      const _self = this;
      let workbenchPath = storage.getStorageSync("workbenchPath");
      if (isEmpty(workbenchPath)) {
        workbenchPath = "/uni_modules/w-app/pages/app/app";
      }
      let pageUrl = `${workbenchPath}?appPiPath=${_self.workbenchAppPiPath}&pageUuid=${_self.selectedPageUuid}`;
      uni.showTabBar();
      _self.setCustomTabBar(false);
      uni.reLaunch({
        url: pageUrl,
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.workbench-settings {
  width: 100%;
  background: $uni-bg-color;

  .tips {
    padding: 5px 16px;
    background-color: $uni-info-light;
  }

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

  .ws-button-group {
    position: fixed;
    width: 100%;
    bottom: var(--window-bottom, 0);
    background-color: $uni-bg-color;

    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    border-top-color: #f5f5f5;
    border-top-style: solid;
    border-top-width: 1px;

    .ws-button {
      /* #ifndef APP-NVUE */
      display: flex;
      /* #endif */

      box-shadow: $uni-shadow-base;

      flex: 1;
      flex-direction: row;
      justify-content: center;
      align-items: center;
      height: 40px;
    }

    .ws-button-text {
      font-size: 16px;
      color: #333;
    }

    .ws-button-color {
      color: #007aff;
    }
  }
}
</style>
