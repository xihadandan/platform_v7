'use strict';
// 客户端渲染加载器
// FIXME: 该文件修改，需要重启服务
const path = require('path');
module.exports = function (source) {
  return `
    import render from '@framework/vue/entry/client.js';
    import Page from '${this.resourcePath.replace(/\\/g, '\\\\')}';
    export default render(Page);
  `;
};
