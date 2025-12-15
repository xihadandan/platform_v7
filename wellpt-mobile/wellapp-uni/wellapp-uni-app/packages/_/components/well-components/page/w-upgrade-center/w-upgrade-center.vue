<template>
  <view class="w-update-center">
    <uni-popup ref="updatePopup" class="update-popup" :isMaskClick="popupMaskClick">
      <wgt-update-dialog title="更新资源包" :updatePercent="updatePercent" :updateTips="updateTips"></wgt-update-dialog>
    </uni-popup>
  </view>
</template>

<script>
import { isEmpty } from "lodash";
import wgtUpdateDialog from "./wgt-update-dialog.vue";
export default {
  props: {
    type: {
      // 更新类型，all全部、wgt资源包更新、pkg整包更新
      type: String,
      default: "wgt",
    },
  },
  components: { wgtUpdateDialog },
  data() {
    return {
      runtime: null,
      widgetInfo: null,
      downloadResult: null,
      checkresult: null,
      pkgInfoUrl: "",
      wgtInfoUrl: "",
      wgtResUrl: "",
      updating: false,
      updatedConfig: {},
      updatePercent: 0, // 更新进度
      updateTips: "更新包下载中...",
      popupMaskClick: false,
    };
  },
  mounted() {
    const _self = this;

    // #ifdef APP-PLUS
    // wgt资源包更新
    if (_self.type == "all" || _self.type == "wgt") {
      _self.tryWgtUpdate();
    } else if (_self.type == "pkg") {
      // pkg整包更新
      _self.tryPkgUpdate();
    }
    // #endif
  },
  methods: {
    // wgt资源包更新
    tryWgtUpdate: function () {
      const _self = this;
      _self.getSystemParams("uni-app.wgt.info.url", "", function (value) {
        _self.wgtInfoUrl = value;
        if (!isEmpty(_self.wgtInfoUrl)) {
          _self.getSystemParams("uni-app.wgt.res.url", "", function (value) {
            _self.wgtResUrl = value;
            if (!isEmpty(_self.wgtResUrl)) {
              _self.checkWgtUpdated(function () {
                _self.showConfirmWgtUpdate();
              });
            } else {
              _self.ignoreWgtUpdate();
            }
          });
        } else {
          _self.ignoreWgtUpdate();
        }
      });
    },
    ignoreWgtUpdate: function () {
      const _self = this;
      if (_self.type == "all") {
        // 资源包没有更新，检测整包更新
        _self.tryPkgUpdate();
      }
    },
    getSystemParams: function (key, defaultValue, callback) {
      uni.request({
        url: `/basicdata/system/param/get?key=${key}`,
        method: "GET",
        contentType: "application/json",
        dataType: "json",
        success: function (success) {
          let value = success.data.data;
          if (!isEmpty(value)) {
            callback.call(this, value);
          } else {
            callback.call(this, defaultValue);
          }
        },
      });
    },
    // 检测wgt资源是否有更新
    checkWgtUpdated(callback) {
      const _self = this;
      plus.runtime.getProperty(plus.runtime.appid, function (widgetInfo) {
        _self.widgetInfo = widgetInfo;
        let localVersionName = widgetInfo.version;
        let localVersionCode = widgetInfo.versionCode;
        uni.request({
          url: _self.wgtInfoUrl,
          data: {
            version: widgetInfo.version,
            name: widgetInfo.name,
          },
          success: (result) => {
            var data = result.data;
            _self.updatedConfig = data;
            // uni-app只检测versionName，平台同时检测versionName、versionCode，或后端返回update
            if (data.update || (data.versionName > localVersionName && data.versionCode > localVersionCode)) {
              callback.call(_self, data);
            } else {
              console.info("local version is latest, ", widgetInfo, data);
              _self.ignoreWgtUpdate();
            }
          },
        });
      });
    },
    // 确认是否更新版本
    showConfirmWgtUpdate: function () {
      const _self = this;
      uni.showModal({
        title: "资源包更新",
        content: "应用资源有更新，是否更新？",
        cancelText: "暂不更新",
        confirmText: "立即更新",
        success: function (result) {
          if (result.cancel) {
            return;
          }
          _self.updateWgtVersion();
        },
      });
    },
    // 更新版本
    updateWgtVersion: function () {
      const _self = this;

      _self.updating = true;
      _self.$refs.updatePopup.open("center");

      const downloadTask = uni.downloadFile({
        url: _self.wgtResUrl,
        success: (downloadResult) => {
          _self.downloadResult = downloadResult;
          if (downloadResult.statusCode === 200) {
            _self.showWgtUpdateTips(95, "更新包下载成功，开始安装...");

            let tempFilePath = downloadResult.tempFilePath;
            let forceInstall = _self.updatedConfig.forceInstall || false;
            // 安装更新
            setTimeout(function () {
              _self.installWgtUpdated(tempFilePath, forceInstall);
            }, 1000);
          } else {
            _self.wgtFileDownloadFailed();
          }
        },
      });
      downloadTask.onProgressUpdate((res) => {
        if (res.progress < 100) {
          let progress = (res.progress / 100) * 95;
          _self.showWgtUpdateTips(parseInt(progress), "更新包下载中...");
        } else {
          _self.showWgtUpdateTips(95, "更新包下载成功，开始安装...");
        }
      });
    },
    // 显示更新提示信息
    showWgtUpdateTips: function (updatePercent, tips) {
      const _self = this;
      _self.updatePercent = updatePercent;
      _self.updateTips = tips;
      // uni.showToast({
      //   icon: "none",
      //   title: tips,
      // });
    },
    // 安装更新
    installWgtUpdated: function (tempFilePath, forceInstall) {
      const _self = this;
      plus.runtime.install(
        tempFilePath,
        {
          force: forceInstall,
        },
        function () {
          // 更新成功
          _self.installWgtSuccess();
        },
        function (e) {
          // 更新成功
          _self.installWgtFailed(e);
        }
      );
    },
    // 安装成功
    installWgtSuccess: function () {
      const _self = this;
      _self.showWgtUpdateTips(100, "更新安装成功，即將重启应用！");
      setTimeout(function () {
        // uni.hideToast();
        _self.popupMaskClick = true;
        _self.updating = false;
        _self.$refs.updatePopup.close();
        plus.runtime.restart();
      }, 1000);
    },
    // 安装失败
    installWgtFailed: function (e) {
      const _self = this;
      _self.showWgtUpdateTips(0, `更新安装失败，${e.message}`);
      setTimeout(function () {
        _self.popupMaskClick = true;
        _self.updating = false;
        // _self.$refs.updatePopup.close();
      }, 2000);
    },
    // 文件下载失败
    wgtFileDownloadFailed: function () {
      const _self = this;
      _self.showWgtUpdateTips(0, "资源包下载失败，无法更新！");
      setTimeout(function () {
        _self.popupMaskClick = true;
        _self.updating = false;
        // _self.$refs.updatePopup.close();
      }, 2000);
    },
    // pkg整包更新
    tryPkgUpdate: function () {
      const _self = this;
      _self.getSystemParams("uni-app.pkg.info.url", "", function (value) {
        _self.pkgInfoUrl = value;
        if (!isEmpty(_self.pkgInfoUrl)) {
          _self.checkPkgUpdated(function (data) {
            _self.showConfirmPkgUpdate(data);
          });
        }
      });
    },
    checkPkgUpdated: function (callback) {
      const _self = this;
      let appid = plus.runtime.appid;
      let localVersionName = plus.runtime.version;
      let localVersionCode = plus.runtime.versionCode;
      uni.request({
        url: _self.pkgInfoUrl,
        data: {
          appid,
          version: localVersionName,
        },
        success: (result) => {
          let data = result.data;
          _self.updatedConfig = data;
          // 检测versionName、versionCode
          if (data.versionName > localVersionName && data.versionCode > localVersionCode) {
            callback.call(_self, data);
          } else {
            console.info("local version is latest, ", plus.runtime, data);
          }
        },
      });
    },
    showConfirmPkgUpdate: function (data) {
      const _self = this;
      uni.showModal({
        title: "应用更新",
        content: "应用有更新，是否到应用市场更新？",
        cancelText: "暂不更新",
        confirmText: "立即更新",
        success: function (result) {
          if (result.cancel) {
            return;
          }

          let appUrl = "";
          if (plus.os.name == "Android") {
            if (data.android) {
              appUrl = data.android.url;
            }
          } else {
            if (data.ios) {
              appUrl = data.ios.url;
            }
          }
          if (!isEmpty(appUrl)) {
            plus.runtime.openURL(appUrl);
          } else {
            uni.showToast({
              icon: "none",
              title: "应用市场地址为空，无法更新！",
            });
          }
        },
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.w-update-center {
  .progress-box {
    display: flex;
    flex-direction: column;
    height: 50rpx;
    margin-bottom: 60rpx;
  }

  .update-popup {
    ::v-deep .uni-popup__wrapper {
      width: 95%;
    }
  }
}
</style>
