"use strict";
const getDeviceUUID = function () {
  let deviceId =
    uni.getStorageSync("uni_deviceId") ||
    uni.getSystemInfoSync().deviceId ||
    uni.getSystemInfoSync().system + "_" + Math.random().toString(36).substr(2);

  uni.setStorageSync("uni_deviceId", deviceId);
  return deviceId;
};
export default { getDeviceUUID };
