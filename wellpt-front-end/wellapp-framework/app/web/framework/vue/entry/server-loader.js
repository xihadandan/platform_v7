'use strict';
// FIXME: 该文件修改，需要重启服务
const path = require('path');
module.exports = function (source) {
  this.cacheable();
  return `
    import Vue from 'vue';
    import Vuex from 'vuex';
    import '@framework/vue';
    import '~/app/web/framework/vue'; // 加载本地工作目录的组件
    import render from '@framework/vue/entry/server.js';
    import Page from '${this.resourcePath.replace(/\\/g, '\\\\')}';
    export default render(Page, Vue , Vuex );
  `;
};
