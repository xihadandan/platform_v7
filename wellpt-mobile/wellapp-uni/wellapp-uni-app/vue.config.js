"use strict";

let config = require("wellapp-uni-framework/vue.config.js");

module.exports = {
	/* eslint-disable */
	chainWebpack: (config) => {},
	devServer: {
		proxy: {
			// 后端代理api服务
			"/server-api": {
				target: process.env.VUE_APP_WELLAPP_BACKEND_URL,
				changeOrigin: true,
				pathRewrite: {
					"^/server-api": ""
				},
			},
			// 代理前端web服务的静态资源
			"/app-resources": {
				target: process.env.VUE_APP_WELLAPP_WEB_URL,
				changeOrigin: true,
			},
		},
	},
	/* eslint-enable */
	pluginOptions: {},
	...config,
};