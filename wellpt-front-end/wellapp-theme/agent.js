const { join, resolve } = require('path');
const { debounce } = require('lodash');
const fs = require('fs');

const { regenerateTheme } = require('./src/scripts/compiler');
const { regenerateDevTheme } = require('./src/scripts/compiler-dev');

let generatedThemeFiles = [];
let latestRequestTheme = '';

async function reCompileThemeFile(themeName) {
  if (generatedThemeFiles.includes(themeName)) return;

  await regenerateTheme(themeName);
  generatedThemeFiles.push(themeName);
}

// 业务线重新编译函数
async function reCompileDevThemeFile(themeName) {
  if (generatedThemeFiles.includes(themeName)) return;

  await regenerateDevTheme(themeName);
  generatedThemeFiles.push(themeName);
}

module.exports = async () => { }


// 7.0 不在用以下方式监听生成样式主题
/*


module.exports = async function (agent) {

  const _debounceCompileFn =
    agent.config.name === 'wellapp-web'
      ? debounce(reCompileThemeFile, 2000, { leading: true, trailing: false })
      : debounce(reCompileDevThemeFile, 2000, { leading: true, trailing: false });
  // 判断环境
  if (agent.config.name === 'wellapp-web') {
    // 平台 默认
    if (agent.config.env === 'local') {
      const themeDir = join(__dirname, 'src', 'styles');
      agent.watcher.watch(themeDir, (event, filename) => {
        console.log(`样式文件 “${event.path}” 变更，下次请求时会重新生成主题文件`);
        generatedThemeFiles = [];
        if (latestRequestTheme) _debounceCompileFn(latestRequestTheme);
      });
    }
  } else {


    if (agent.config.env === 'local') {
      const rootDir = join(__dirname, '../');// / 查找 node_modules 下 wellapp 模块

      let themeDir = [];
      const files = fs.readdirSync(rootDir);
      files.forEach(function (item, index) {
        if (item.startsWith('wellapp-')) {
          let stat = fs.lstatSync(rootDir + '/' + item);
          if (stat.isDirectory() === true) {
            themeDir.push(rootDir + item + '/app/styles');
          }
        }

      });
      console.log('监听主题样式文件夹路径: ', themeDir);
      agent.watcher.watch(themeDir, (event, filename) => {
        console.log(`主题样式文件 “${event.path}” 变更，下次请求时会重新生成主题文件`);
        generatedThemeFiles = [];
        if (latestRequestTheme) _debounceCompileFn(latestRequestTheme);
      });
    }
  }

  // agent 发起请求
  agent.messenger.on('onThemeRequest', async function ({ theme, themeName }) {
    latestRequestTheme = themeName;
    await _debounceCompileFn(themeName);
  });
};



 */
