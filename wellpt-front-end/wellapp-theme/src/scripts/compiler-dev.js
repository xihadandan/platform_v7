const path = require('path');
const fs = require('fs-extra');
const { getAllThemes, compileLess } = require('./compiler');

function generateDevResourceJson(min) {
  const rootDir = process.cwd();
  const resourceJson = { js: {}, css: {} };
  const files = getAllThemes();

  files.forEach(file => {
    const name = file.filename.replace('.min.css', '').replace('.css', '') + '-dev';
    const path = `/themes/${name}${min ? '.min' : ''}`;
    resourceJson.css[name] = { name, path, dependencies: [], order: -2000 };
  });

  const resourcePath = path.join(rootDir, 'config', 'resource');
  fs.writeJsonSync(path.join(resourcePath, 'themes-dev.resource.json'), resourceJson);
  console.log(`成功生成业务线主题资源静态文件: ${resourcePath} `);
}

async function compileAllDevThemes(min) {
  const rootDir = process.cwd();
  const ptEntryDir = path.join(__dirname, '..', 'styles', 'core', 'themes');
  const devEntryDir = path.join(rootDir, 'app', 'styles');
  const targetDir = path.join(rootDir, 'app', 'assets', 'themes');

  const files = getAllThemes();
  const promiseList = [];

  for (const file of files) {
    const ptEntryFilePath = path.join(ptEntryDir, `${file.theme}.less`);
    const devEntryFilePath = path.join(devEntryDir, `index.less`);
    const lessTemplate = `@import '${ptEntryFilePath}'; @import '${devEntryFilePath}'; ${file.customLessStr}`;

    if (min) {
      const filename = file.filename.replace('.min.css', '').replace('.css', '') + '-dev.min.css';
      const filePath = path.join(targetDir, filename);
      promiseList.push(compileLess(lessTemplate, filePath, true));
    } else {
      const filename = file.filename.replace('.min.css', '').replace('.css', '') + '-dev.css';
      const filePath = path.join(targetDir, filename);
      promiseList.push(compileLess(lessTemplate, filePath, false));
    }
  }

  try {
    return await Promise.all(promiseList);
  } catch (err) {
    console.log(err);
    return err;
  }
}

async function regenerateDevTheme(fileName) {
  const rootDir = process.cwd();
  const ptEntryDir = path.join(__dirname, '..', 'styles', 'core', 'themes');
  const devEntryDir = path.join(rootDir, 'app', 'styles');
  const targetDir = path.join(rootDir, 'app', 'assets', 'themes');

  const files = getAllThemes();
  const file = files.find(f => f.filename === `${fileName}.css`);
  file.filename = file.filename.replace('.min.css', '').replace('.css', '') + '-dev.css';
  const targetFilePath = path.join(targetDir, file.filename);
  const ptEntryFilePath = path.join(ptEntryDir, `${file.theme}.less`);
  const devEntryFilePath = path.join(devEntryDir, `index.less`);
  const lessTemplate = `@import '${ptEntryFilePath}'; @import '${devEntryFilePath}'; ${file.customLessStr}`;

  await compileLess(lessTemplate, targetFilePath, false);
}

module.exports = { generateDevResourceJson, regenerateDevTheme, compileAllDevThemes };
