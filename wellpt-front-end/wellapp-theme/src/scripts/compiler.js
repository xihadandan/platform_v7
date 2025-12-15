const fs = require('fs-extra');
const less = require('less');
const path = require('path');
const LessPluginCleanCSS = require('less-plugin-clean-css');
const LessPluginAutoPrefix = require('less-plugin-autoprefix');

/**
 * 获得所有主题的数组
 * @returns {Array<{
 *  filename: string;
 *  theme: string;
 *  customLessStr: string;
 *  customSetting: Array<{ variable:string; name:string; value:string; }>
 * }>}
 */
function getAllThemes() {
  const files = [];

  const themesSettingJsonPath = path.join(__dirname, '..', 'styles', 'themes.json');
  const themes = fs.readJsonSync(themesSettingJsonPath);

  for (const theme of themes) {
    if (!theme.custom) {
      files.push({ filename: `${theme.theme}.css`, theme: theme.theme, customLessStr: '', customSetting: [] });
      continue;
    }

    const themesConfigMatrix = [[{ config: [] }]];
    for (const customItem in theme.custom) {
      themesConfigMatrix[0][0].config.push(customItem);
      themesConfigMatrix.push([]);

      for (const key in theme.custom[customItem]) {
        themesConfigMatrix[themesConfigMatrix.length - 1].push({ [key]: theme.custom[customItem][key] });
      }
    }
    const plainArray = toPlainArray(themesConfigMatrix);

    for (const item of plainArray) {
      let customLessStr = '';
      let filenameAffix = '';
      const customSetting = [];

      for (let i = 0; i < item[0].config.length; i++) {
        const variable = item[0].config[i];
        const variableName = Object.keys(item[i + 1])[0];
        const variableValue = item[i + 1][variableName];

        if (variable === '@primary-color') {
          customLessStr += `@theme-color: '${variableName}'; `;
        }

        filenameAffix += `-${variableName}`;
        customLessStr += `${variable}: ${variableValue}; `;

        customSetting.push({ variable, name: variableName, value: variableValue });
      }

      files.push({ filename: `${theme.theme}${filenameAffix}.css`, theme: theme.theme, customLessStr, customSetting });
    }
  }

  return files;
}

/**
 * 编译 Less 到 CSS
 * @param {string} lessInput 要编译的 Less 代码
 * @param {string} targetPath 生成文件的保存路径
 * @param {boolean} [min] 生成压缩版代码
 */
async function compileLess(lessInput, targetPath, min) {
  console.log(`正在编译主题${min ? '(压缩版)' : ''}: ${targetPath}`);
  const lessPluginAutoPrefix = new LessPluginAutoPrefix();
  const plugins = [lessPluginAutoPrefix];
  const lessOptions = {
    plugins,
    javascriptEnabled: true,
  };

  if (min) {
    plugins.push(new LessPluginCleanCSS({ advanced: true }));
  }

  try {
    const { css } = await less.render(lessInput, lessOptions);
    fs.writeFileSync(targetPath, css);
    console.log(`成功编译主题${min ? '(压缩版)' : ''}: ${targetPath}`);
    return true;
  } catch (err) {
    console.log(err);
    return err;
  }
}

/**
 * 编译所有主题
 * @param {boolean} [min] 生成压缩版代码
 */
async function compileAllThemes(min) {
  const entryPath = path.join(__dirname, '..', 'styles', 'entry');
  const targetDir = path.join(__dirname, '../../app/assets/themes');

  const files = getAllThemes();
  const promiseList = [];

  for (const file of files) {
    const targetFilePath = path.join(targetDir, file.filename);
    const lessTemplate = `@import '${entryPath}/${file.theme}.less'; ${file.customLessStr}`;

    if (min) {
      const minFilePath = targetFilePath.replace('.css', '.min.css');
      promiseList.push(compileLess(lessTemplate, minFilePath, true));
    } else {
      promiseList.push(compileLess(lessTemplate, targetFilePath, false));
    }
  }

  try {
    return await Promise.all(promiseList);
  } catch (err) {
    console.log(err);
    return err;
  }
}

/* 再次生成主题文件 */
async function regenerateTheme(fileName) {
  const entryPath = path.join(__dirname, '..', 'styles', 'entry');
  const targetDir = path.join(__dirname, '../../app/assets/themes');

  const files = getAllThemes();
  const file = files.find(f => f.filename === `${fileName}.css`);

  const targetFilePath = path.join(targetDir, file.filename);
  const lessTemplate = `@import '${entryPath}/${file.theme}.less'; ${file.customLessStr}`;
  await compileLess(lessTemplate, targetFilePath, false);
}

/** 生成静态资源配置文件 */
function generateResourceJson(min) {
  const resourceJson = { js: {}, css: {} };
  const files = getAllThemes();

  files.forEach(file => {
    const name = file.filename.replace('.min.css', '').replace('.css', '');

    const path = `/themes/${name}${min ? '.min' : ''}`;

    resourceJson.css[name] = { name, path, dependencies: [], order: -1000 };
  });

  const resourcePath = path.join(__dirname, '..', '..', 'config', 'resource');
  fs.writeJsonSync(path.join(resourcePath, 'themes.resource.json'), resourceJson);
  console.log(`成功生成平台主题静态文件: ${resourcePath} `);
}

function toPlainArray(array) {
  let resultArr = [];
  array.forEach(arrItem => {
    if (resultArr.length === 0) {
      resultArr = arrItem;
    } else {
      const emptyArray = [];
      resultArr.forEach(item => {
        arrItem.forEach(value => {
          Array.isArray(item) ? emptyArray.push([...item, value]) : emptyArray.push([item, value]);
        });
      });
      resultArr = emptyArray;
    }
  });
  return resultArr;
}

module.exports = { compileAllThemes, compileLess, getAllThemes, generateResourceJson, regenerateTheme };
