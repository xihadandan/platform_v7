const path = require("path");

module.exports = {
    // webpack配置 - 简单配置方式
    configureWebpack: {
        resolve: {
            alias: {
                "@wellapp-uni-framework": path.join(__dirname, "node_modules", "wellapp-uni-framework", "src"),
                // vue$: path.resolve(__dirname, 'src/common/vue/vue.esm.js')
                // 更改h5-vue使用的库
                // vue$: '@dcloudio/vue-cli-plugin-uni/packages/h5-vue/dist/vue.esm.js'
                // mp-weixin使用库，不包含vue编译器，要重新提供
                // vue$: '@dcloudio/vue-cli-plugin-uni/packages/mp-vue/dist/mp.runtime.esm.js',
            },
        },
    },
};
