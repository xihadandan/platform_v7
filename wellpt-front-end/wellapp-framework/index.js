'use strict';

module.exports = require('./lib/framework.js');

// cms组件类
module.exports.Component = require('./app/component.js');

module.exports.Ftp = require('./lib/ftp.js');

module.exports.webpackConfig = require('./webpack.config.js');
