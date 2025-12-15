/**
 * 发布前自动运行脚本。
 * - 生成主题文件
 * - 生成静态资源配置文件
 */

const { compileAllThemes, generateResourceJson } = require('./compiler');

// 1. 生成静态资源配置文件
generateResourceJson(true);

// 2. 生成主题压缩版文件
console.log('开始编译主题');
console.time('主题编译结束，用时');

compileAllThemes(false).then(() => {
  compileAllThemes(true).finally(() => {
    console.timeEnd('主题编译结束，用时');

  })
})

