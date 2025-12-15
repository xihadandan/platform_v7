'use strict';
const _framework = require('wellapp-framework');
const extend = require('extend');
module.exports = extend(true, _framework.webpackConfig({
  analyzer: false, // 是否开启打包chunk分析
  speedMeasure: false, // 是否开启打包速度汇总
  alias: {},// 别名添加
  excludeModules: [], // 排除webpack扫描的依赖 wellapp-* 模块
  themeClass: undefined,// 默认系统主题类
}));
