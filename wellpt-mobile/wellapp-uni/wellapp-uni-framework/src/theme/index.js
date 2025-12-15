"use strict";

//FIXME: 主题由7.0主题服务获取

// import vuex from "vuex";
// import store from "../store";
// import { findIndex } from "lodash";

// // 是否启用服务端主题
// const isEnableThemeFromServer = function (callback) {
//   uni.request({
//     url: "/basicdata/system/param/get?key=uni-app.theme.enabled",
//     method: "GET",
//     contentType: "application/json",
//     dataType: "json",
//     success: function (result) {
//       callback.call(this, result.data && result.data.data == "true");
//     },
//     fail: function (error) {
//       callback.call(this, false);
//     },
//   });
// };

// // 检测并更新服务器上的主题
// const checkAndUpdateFromServer = function (themeList, themeStyle) {
//   // console.log(themeList, themeStyle);
//   // 从本地存储合并主题信息
//   mergeThemeListFromStorage(themeList, themeStyle);

//   let checkUpdateDtos = [];
//   themeList.forEach(function (theme) {
//     checkUpdateDtos.push({
//       id: theme.value,
//       recVer: theme.recVer,
//     });
//   });
//   uni.request({
//     url: "/api/app/theme/listByApplyToWithUpdatedTheme?applyTo=uni-app",
//     method: "POST",
//     data: checkUpdateDtos,
//     contentType: "application/json",
//     dataType: "json",
//     success: function (result) {
//       let themes = result.data.data || [];
//       mergeAndSetThemes(themeList, themeStyle, themes);
//     },
//     fail: function (error) {
//       mergeAndSetThemes(themeList, themeStyle, []);
//     },
//   });
// };

// const mergeThemeListFromStorage = function (themeList, themeStyle) {
//   let storageThemes = uni.getStorageSync("themeList") || [];
//   storageThemes.forEach(function (theme) {
//     let themeIndex = findIndex(themeList, function (themeItem) {
//       return themeItem.value == theme.value;
//     });
//     if (themeIndex != -1) {
//       let themeItem = themeList[themeIndex];
//       themeItem.name = theme.name;
//       themeItem.enabled = theme.enabled;
//       themeItem.recVer = theme.recVer;
//     } else {
//       themeList.push({
//         name: theme.name,
//         value: theme.value,
//         enabled: theme.enabled,
//         recVer: theme.recVer,
//         order: theme.order,
//       });
//     }

//     if (theme.serverThemeStyle) {
//       themeStyle[theme.value] = theme.serverThemeStyle;
//     }
//   });
// };

// const mergeAndSetThemes = function (themeList, themeStyle, themes) {
//   themes.forEach(function (theme) {
//     let themeIndex = findIndex(themeList, function (themeItem) {
//       return themeItem.value == theme.id;
//     });
//     let themeItem = null;
//     if (themeIndex != -1) {
//       themeItem = themeList[themeIndex];
//       themeItem.name = theme.name;
//       themeItem.enabled = theme.enabled;
//       themeItem.order = theme.sortOrder || themeItem.order || 100;
//       themeItem.recVer = theme.recVer || 0;
//     } else {
//       // 代码不存在的主题从服务器上添加
//       themeItem = {
//         name: theme.name,
//         value: theme.id,
//         enabled: theme.enabled,
//         order: theme.sortOrder,
//         recVer: theme.recVer,
//       };
//       themeList.push(themeItem);
//     }

//     let themeDefinitionJson = JSON.parse(theme.definitionJson);
//     themeStyle[theme.id] = themeDefinitionJson.style;
//     themeItem.serverThemeStyle = themeDefinitionJson.style;
//   });

//   // 更新本地存储
//   if (themes.length > 0) {
//     themeList.sort(function (a, b) {
//       return a.order - b.order;
//     });
//     uni.setStorage({ key: "themeList", data: themeList });
//   }
//   store.commit("setThemeList", themeList);
//   store.commit("setThemeStyle", themeStyle);
// };

// const install = function (Vue) {



//   // 主题变量
//   Vue.mixin({
//     computed: {
//       ...vuex["mapGetters"]({
//         theme: "theme",
//       }),
//     },
//   });

//   // 加载主题文件
//   const themeFiles = require.context("@/static/theme", true, /\w+\.js$/);
//   let themeList = [];
//   let themeStyle = {};
//   themeFiles.keys().forEach(function (fileName) {
//     let themeName = fileName
//       .split("/")
//       .pop()
//       .replace(/\.\w+$/, "");
//     let themeModule = themeFiles(fileName);
//     let themeData = themeModule.default || themeModule;
//     if (themeData.disabled === true) {
//       return;
//     }
//     themeList.push({
//       name: themeData.name || themeName,
//       value: themeData.id || themeName,
//       order: themeData.order || 100,
//     });
//     themeStyle[themeData.id || themeName] = themeData.style;
//   });
//   themeList.sort(function (a, b) {
//     return a.order - b.order;
//   });

//   isEnableThemeFromServer(function (isEnable) {
//     if (isEnable) {
//       checkAndUpdateFromServer(themeList, themeStyle);
//     } else {
//       store.commit("setThemeList", themeList);
//       store.commit("setThemeStyle", themeStyle);
//     }
//   });
// };

// export default { install };
