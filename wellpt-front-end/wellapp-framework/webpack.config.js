const path = require('path');
const fs = require('fs');
const lodash = require('lodash');
const { spawn, exec } = require('child_process');

module.exports = options => {
  const commandOptions = require('minimist')(process.argv.slice(2));
  const SpeedMeasurePlugin = require('speed-measure-webpack-plugin');
  const lessToJs = require('less-vars-to-js');
  const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;
  // const LodashModuleReplacementPlugin = require('lodash-webpack-plugin');
  const prettier = require('prettier');
  const webpack = require('webpack');
  // 解析依赖模块
  let _include = ['app/web/page'],
    _vueLoaderPath = [path.join(path.resolve('./node_modules'), 'wellapp-framework', 'app', 'web', 'framework')],
    cwd = process.cwd(),
    babelRegExpTest = process.platform === 'win32' ? /wellapp-.+\\app\\web\\.+\.js$/ : /wellapp-.+\/app\/web\/.+\.js$/,
    babelRegExpExclude = process.platform === 'win32' ? /wellapp-.+\\app\\web/ : /wellapp-.+\/app\/web\/.+\.js$/;
  options = options || {};
  let { excludeModules } = options;
  let vueSilent = options.vueSilent == undefined ? false : options.vueSilent;
  let existAppIconfont = false;
  let requireDevjs = [],
    templates = [],
    alias = options.alias || {},
    themeClass = options.themeClass || '',
    metaInfos = [],
    wellappModules = [];

  // 提供less变量修改: 可用于修改antv默认的变量
  const modifyVars = lessToJs(fs.readFileSync(path.resolve('./node_modules/wellapp-framework/app/web/style/css/modifyVars.less'), 'utf8'));
  if (fs.existsSync(path.resolve('./app/web/style/css/modifyVars.less'))) {
    localModifyVars = lessToJs(fs.readFileSync(path.resolve('./app/web/style/css/modifyVars.less'), 'utf8'));
    Object.assign(modifyVars, localModifyVars);
  }
  fs.writeFile(`run/lessModifyVars.json`, JSON.stringify(modifyVars, null, '\t'), (err, data) => {});

  fs.readdirSync('./node_modules').forEach(function (dirPath) {
    let pagePath = 'node_modules/' + dirPath + '/app/web/page'; // 页面入口
    let templatePath = 'node_modules/' + dirPath + '/app/web/template';
    let devJsPath = 'node_modules/' + dirPath + '/app/web/widget/@develop';
    let libPath = 'node_modules/' + dirPath + '/app/web/lib';
    let widgetPath = 'node_modules/' + dirPath + '/app/web/widget';
    let frameworkIndexPath = 'node_modules/' + dirPath + '/app/web/framework/vue/index.js';
    if (dirPath.startsWith('wellapp-')) {
      wellappModules.push(dirPath);
      if (fs.existsSync(devJsPath)) {
        // 导入二开脚本
        requireDevjs.push(`require.context('./${dirPath}/app/web/widget/@develop', true, /\\\w+\\\.js$/)`);
      }
      let frameworkIndexExist = false;
      if (dirPath !== 'wellapp-framework' && dirPath !== 'wellapp-theme') {
        let moduleAlias = `@${lodash.camelCase(dirPath.replace('wellapp-', ''))}`;
        alias[moduleAlias] = path.join(cwd, 'node_modules', dirPath);
        if (excludeModules && excludeModules.length && excludeModules.indexOf(dirPath) != -1) {
          return;
        }

        if (fs.existsSync(templatePath)) {
          _vueLoaderPath.push(path.resolve(templatePath));
          // 导入模板vue文件（提供模板组件加载使用）
          templates.push(`require.context('./${dirPath}/app/web/template', true, /\\\w+\\\.vue$/, 'lazy')`);
        }

        if (fs.existsSync(pagePath)) {
          _include.push(pagePath);
          _vueLoaderPath.push(path.resolve(pagePath));
        }

        if (fs.existsSync(libPath)) {
          _vueLoaderPath.push(path.resolve(libPath));
        }

        // frameworkIndexExist =
        //   fs.existsSync(frameworkIndexPath) &&
        //   fs.existsSync(path.join(path.resolve('./node_modules'), dirPath, 'app', 'web', 'framework', 'vue', 'index.js'));
        // if (frameworkIndexExist) {
        //   imports.push(`import './${dirPath}/app/web/framework/vue';`);
        // }
      }

      if (fs.existsSync(widgetPath)) {
        // 模块有定义组件
        _vueLoaderPath.push(path.resolve(widgetPath));
        // 加载组件定义信息
        metaInfos.push(`require.context('./${dirPath}/app/web/widget', true, /META-INF\\.js$/)`);
      }

      if (frameworkIndexExist) {
        _vueLoaderPath.push(path.resolve(frameworkIndexPath));
      }
    }
  });

  if (metaInfos.length > 0) {
    let codeSegment = `
      const infos = [];
      if(!EASY_ENV_IS_NODE){
        let requireArr = [ ${metaInfos.join(',')} ];
        for(let r of requireArr){
          r.keys().forEach(fileName => {
            let comp = r(fileName);
            if (comp.default) {
              if (Array.isArray(comp.default)) {
                infos.push(...comp.default);
              } else {
                infos.push(comp.default);
              }
            }
          });
        }
      }
      export default infos;
    `;
    codeSegment = prettier.format(codeSegment, { semi: true, parser: 'typescript', printWidth: 140, tabWidth: 2 });
    fs.writeFileSync('./node_modules/.webpack.widget.meta-info.js', codeSegment); // 组件元数据信息加载
  }

  if (fs.existsSync(path.resolve('./app/web/widget/@develop'))) {
    requireDevjs.push(`require.context('../app/web/widget/@develop', true, /\\\w+\\\.js$/)`);
  }

  if (requireDevjs.length > 0) {
    let codeSegment = `
    if(!EASY_ENV_IS_NODE){
      let requireArr = [ ${requireDevjs.join(',')}];
      for(let r of requireArr){
        r.keys().forEach(fileName =>{
          let _module = r(fileName);
          if(_module.default){
            if(window.Vue.prototype.__developScript == undefined){
              window.Vue.prototype.__developScript = {};
            }
            let name = fileName.substr(fileName.lastIndexOf('/') + 1).replace('.js', '');
            if(window.Vue.prototype.__developScript[name]){
              console.warn('[ ' + name + ' ] 二开文件名重复, 路径: ' + fileName);
            }
            window.Vue.prototype.__developScript[name] = _module;
          }
        });
      }
    }
    `;

    codeSegment = prettier.format(codeSegment, { semi: true, parser: 'typescript', printWidth: 140, tabWidth: 2 });
    fs.writeFileSync('./node_modules/.webpack.runtime.devjs.js', codeSegment); // 二开脚本加载
  }

  if (fs.existsSync(path.resolve('./app/web/template'))) {
    templates.push(`require.context('../app/web/template', true, /\\\w+\\\.vue$/, 'lazy')`);
  }
  if (templates.length > 0) {
    let codeSegment = `
    if(!EASY_ENV_IS_NODE){
      let requireArr = [ ${templates.join(',')}];
      for(let r of requireArr){
        r.keys().forEach(fileName =>{
          let name = fileName.substr(fileName.lastIndexOf('/') + 1).replace('.vue', '');
          const resolve = () => r(fileName);
          resolve.META = { fileName, name  };
          window.Vue.component( name, resolve);
        });
      };
    }
    `;
    codeSegment = prettier.format(codeSegment, { semi: true, parser: 'typescript', printWidth: 140, tabWidth: 2 });
    fs.writeFileSync('./node_modules/.webpack.runtime.wtemplate.js', codeSegment); // 模板组件加载
  }

  if (fs.existsSync('app/web/page/app/app.js')) {
    _include.push({ 'app/app': 'app/web/page/app/app.js?loader=false' }); // 单页应用
  }

  // 读取主题包
  let importThemeLess = [],
    themeMetadata = [];
  if (fs.existsSync(path.resolve('./app/web/themepack'))) {
    fs.readdirSync('./app/web/themepack').forEach(function (dirPath) {
      // if (fs.existsSync(path.resolve('app/web/themepack/' + dirPath + '/meta.json'))) {
      //   let metajson = require(path.resolve('app/web/themepack/' + dirPath + '/meta.json'));
      //   themeMetadata.push(metajson);
      // }
      if (fs.existsSync(path.resolve('app/web/themepack/' + dirPath + '/index.less'))) {
        importThemeLess.push('import "~/app/web/themepack/' + dirPath + '/index.less";');
      }
    });
  }
  themeMetadata = JSON.stringify(themeMetadata);
  fs.writeFileSync('./node_modules/.webpack.themepack.js', `${importThemeLess.join('\n\r')}`);

  // console.log('!!!! entry include : ', _include);
  // console.log('!!!! vue-loader include : ', _vueLoaderPath);

  let localePaths = [],
    localeJsonString = undefined;

  class CopyLocalePlugin {
    constructor(options) {
      // 插件初始化时接收的参数
      this.options = options;
    }

    // Webpack 会在编译流程的不同阶段触发这个 apply 方法
    apply(compiler) {
      // 注册编译前的钩子
      if (process.env.NODE_ENV === 'development') {
        compiler.hooks.watchRun.tap('CopyLocalePlugin', compiler => {
          let result = mergeLocaleJsonFiles(wellappModules, localeJsonString, 'development', true);
          localeJsonString = result.localeJsonString;
        });

        compiler.hooks.beforeCompile.tapAsync('CopyLocalePlugin', (compilation, callback) => {
          if (localePaths.length) {
            compiler.hooks.afterCompile.tap('CopyLocalePlugin', compilation => {
              localePaths.forEach(dep => {
                compilation.contextDependencies.add(dep);
              });
            });
          }

          // localeJsonHashCode = mergeLocaleJsonFiles(wellappModules, localeJsonHashCode, compiler);
          // const startTime = process.hrtime(); // 记录开始时间
          // wellappModules.forEach(function (module) {
          //   if (fs.existsSync('node_modules/' + module + '/app/web')) {
          //     traverseDirectory('node_modules/' + module + '/app/web', mergeLocaleJson, false);
          //   }
          // });
          // let rootLocaleDir = 'app/web/locale';
          // if (fs.existsSync(rootLocaleDir)) {
          //   traverseDirectory(rootLocaleDir, mergeLocaleJson, true);
          // }
          // if (!fs.existsSync('node_modules/.locale')) {
          //   fs.mkdirSync('node_modules/.locale');
          // }
          // let str = JSON.stringify(localeJSON);
          // if (this.localeStr != str) {
          //   this.localeStr = str;
          //   for (let key in localeJSON) {
          //     fs.writeFileSync(`node_modules/.locale/${key}.json`, JSON.stringify(localeJSON[key], null, '\t'));
          //   }
          // }

          // const endTime = process.hrtime(startTime); // 记录结束时间
          // const duration = (endTime[0] * 1e9 + endTime[1]) / 1e6; // 转换为毫秒
          // if (process.env.NODE_ENV === 'development') {
          //   console.log(`国际化json数据处理耗时: ${duration.toFixed(2)} ms`);
          // }
          callback();
        });
      }
    }
  }

  // 设置你的组件库目录
  const COMPONENTS_DIR = path.join(cwd, 'node_modules/vue-color/src');
  const OUTPUT_CSS_PATH = path.join(cwd, 'app/assets/css/vue-color.css');

  // extractAllCSS(COMPONENTS_DIR, OUTPUT_CSS_PATH);
  let config = {};
  if (options.target === 'web') {
    config.target = 'web';
  }
  if (process.env.WELLAPP_WEBPACK_TARGET === 'web') {
    console.log('🔧 检测到 webpack 仅编译web端源码: WELLAPP_WEBPACK_TARGET = web');
    // 读取环境变量设置的只编译浏览器端
    config.target = 'web';
  }

  return {
    egg: true,
    framework: 'vue', // 使用 easywebpack-vue 构建解决方案
    ...config,
    compile: {
      thread: true, // 多进程编译
      cache: true // 启动编译缓存
    },
    dll: ['axios', 'lodash', 'ant-design-vue'], //公共类库的单独提取: 多个DLL中不要存在相同的依赖
    cache: {
      type: 'filesystem' // 编译缓存将保存在文件中，不占用内存
    },
    watchOptions: {
      ignored: /node_modules\/(?!wellapp-)[^.]*(\.(?:js|vue|less))$|^$/, // 排除除了以 'wellapp-' 开头的 node_modules 文件变化，并且排除非 .js、.vue、.less 文件
      // ignored: /node_modules\/[^wellapp-].*$/, // 排除除了以 'wellapp-' 开头的 node_modules 文件变化
      aggregateTimeout: 2000
    },
    create: function () {
      if (this.webpackConfig.target == 'web') {
        spawn('cross-env', ['wellapp-svg-sprite'].concat(this.webpackConfig.mode === 'development' ? ['--watch'] : []), {
          stdio: 'inherit',
          shell: process.platform === 'win32'
        }).on('error', err => {
          console.error('[子进程处理 svg 异常]', err.message);
          process.exit(1); // 可选：让父进程也退出
        });
      }
    },
    customize: webpackConfig => {
      for (let key in webpackConfig.entry) {
        if (key.split('/').length > 2) {
          // 只支持 page 目录下一、二级目录的内容页入口
          delete webpackConfig.entry[key];
        }
      }
      console.log('🔧 Webpack Entry: ', Object.keys(webpackConfig.entry));
      if (options.analyzer) {
        // 开启打包分析
        webpackConfig.plugins.push(
          new BundleAnalyzerPlugin({
            analyzerPort: webpackConfig.target === 'web' ? 8888 : 8889
          })
        );
      }
      webpackConfig.module.rules.push({
        test: /\.mjs$/,
        include: /node_modules/,
        type: 'javascript/auto'
      });

      // webpackConfig.module.rules.push({
      //   test: /\.(vue|js)$/,
      //   resourceQuery: /raw/,
      //   use: 'raw-loader',
      // })

      // vue 引用指向同一个，避免重复加载依赖
      if (webpackConfig.resolve.alias.vue) {
        webpackConfig.resolve.alias.vue = path.join(cwd, 'node_modules', webpackConfig.resolve.alias.vue);
      }

      // 对外提供自定义配置口
      if (typeof options.customize == 'function') {
        options.customize(webpackConfig);
      }

      if (webpackConfig.target == 'web') {
        if (commandOptions.devtool) {
          webpackConfig.devtool = commandOptions.devtool;
        }

        let result = mergeLocaleJsonFiles(wellappModules, localeJsonString, webpackConfig.mode, true);
        if (webpackConfig.mode === 'development') {
          localePaths = result.localePaths;
          localeJsonString = result.localeJsonString;
          webpackConfig.plugins.push(new CopyLocalePlugin());
        }
      }
      webpackConfig.plugins.push({
        apply(compiler) {
          let startTime;
          // 在构建开始前记录时间
          compiler.hooks.compile.tap('BuildTimePlugin', () => {
            startTime = Date.now();
            console.log(`⏳ [Webpack][${webpackConfig.target}] 开始构建...`);
          });

          // 在构建完成后计算总耗时
          compiler.hooks.done.tap('BuildTimePlugin', stats => {
            const diffMs = new Date(Date.now()) - new Date(startTime);
            const totalSeconds = Math.floor(diffMs / 1000);
            const minutes = Math.floor(totalSeconds / 60);
            const seconds = totalSeconds % 60;
            console.log(`✅ [Webpack][${webpackConfig.target}] 构建完成，总耗时：${minutes}分${seconds}秒`);
          });
        }
      });

      // fs.writeFile(`run/webpackConfig.${webpackConfig.target}.json`, JSON.stringify(webpackConfig, null, '\t'), (err, data) => { });
      // if (webpackConfig.target == 'web') {
      //   if (!fs.existsSync('node_modules/.well-widget-locale')) {
      //     fs.mkdirSync('node_modules/.well-widget-locale');
      //   }
      //   for (let key in widgetLocale) {
      //     fs.writeFileSync(`node_modules/.well-widget-locale/${key}.json`, JSON.stringify(widgetLocale[key], null, '\t'));
      //   }
      // }

      return options.speedMeasure ? new SpeedMeasurePlugin().wrap(webpackConfig) : webpackConfig;
    },
    entry: {
      include: _include,

      // easywebpack 提供了通过 配置 entry.loader 实现入口代码模板化，并且代码模板完全有项目自己实现. 项目只需要实现对应的 loader
      // 这样就不用写单独的 js 入口文件， vue  文件作为 entry 就可以直接构建出完整的 JSBundle 文件。
      // easywebpack 直接根据 include 目录下的 vue 文件 和  entry loader 构建出完整的 JSBundle 文件
      loader: {
        client: 'node_modules/wellapp-framework/app/web/framework/vue/entry/client-loader.js',
        server: 'node_modules/wellapp-framework/app/web/framework/vue/entry/server-loader.js'
      }
    },

    // 目录别名
    alias: {
      '~': cwd, // 工作目录
      '@modules': path.join(cwd, 'node_modules'), // 工作目录下的模块依赖目录
      '@develop': path.join(cwd, 'node_modules', 'wellapp-framework/app/web/widget/@develop'),
      '@framework': path.join(cwd, 'node_modules', 'wellapp-framework/app/web/framework'),
      '@locale': path.join(cwd, 'node_modules', '.locale'),
      ...alias,
      '@dyformWidget': path.join(cwd, 'node_modules/wellapp-dyform/app/web/widget'),
      '@pageWidget': path.join(cwd, 'node_modules/wellapp-page-assembly/app/web/widget'),
      '@installPageWidget': path.join(cwd, 'node_modules/wellapp-page-assembly/app/web/framework/vue/install'),
      '@installDyformWidget': path.join(cwd, 'node_modules/wellapp-dyform/app/web/framework/vue/install'),
      '@installWorkflowWidget': path.join(cwd, 'node_modules/wellapp-workflow/app/web/framework/vue/installWidget'),

      // 指定别命，避免依赖重复加载，导致打包文件大小较大的问题
      'ant-design-vue/dist/antd.min.css': path.join(cwd, 'node_modules', 'ant-design-vue/dist/antd.min.css'),
      'ant-design-vue': path.join(cwd, 'node_modules', 'ant-design-vue'),
      '@ant-design': path.join(cwd, 'node_modules', '@ant-design'),
      vuex: path.join(cwd, 'node_modules', 'vuex'),
      'vue-i18n': path.join(cwd, 'node_modules', 'vue-i18n'),
      moment: path.join(cwd, 'node_modules', 'moment'),
      lodash: path.join(cwd, 'node_modules', 'lodash')
    },
    resolve: {
      extensions: ['.mjs']
    },

    module: {
      rules: [
        {
          test: /\.js$/,
          use: [path.join(cwd, 'node_modules', 'wellapp-framework', 'loader', 'devjs-platform-diff-loader')],
          include:
            process.platform === 'win32'
              ? /.+app\\web\\widget\\@develop|node_modules\\wellapp.+\\app\\web\\widget\\@develop/
              : /.+app\/web\/widget\/@develop|node_modules\/wellapp.+\/app\/web\/widget\/@develop/
        },
        {
          test: /\.(png|jpe?g|gif|svg)(\?.*)?$/,
          use: [
            {
              loader: 'url-loader',
              options: {
                limit: 1024,
                name: 'images/[hash:8].[name].[ext]'
              }
            }
          ]
        },
        // {
        //   test: /\.(png|jpe?g|gif|svg)(\?.*)?$/,
        //   loader: 'url-loader?limit=1024&name=images/[hash:8].[name].[ext]'
        // },
        {
          test: /\.vue$/,
          include: _vueLoaderPath,
          exclude: [path.resolve('app/web/page')],
          use: 'vue-loader'
        },
        // {
        //   test: /\.(vue|js)$/,
        //   resourceQuery: /raw/,
        //   use: 'raw-loader',
        //   // include: _vueLoaderPath,
        //   // exclude: [path.resolve('app/web/page')]
        // },
        { babel: false }, // 禁用默认
        { urlimage: false },

        {
          test: babelRegExpTest,
          exclude: file => {
            // fs.appendFile(`run/webpack.babel-loader.runtime.${process.pid}.log`, file + '\r', err => {});
            return !babelRegExpExclude.test(file);
          },
          use: [
            {
              loader: 'babel-loader',
              options: {
                cacheDirectory: true
              }
            }
          ]
        },
        {
          test: /\.less/,
          postcss: true,
          framework: true,
          use: [
            'css-loader',
            {
              loader: 'less-loader',
              options: {
                paths: [path.resolve(__dirname, 'node_modules/ant-design-vue/dist')],
                modifyVars
              }
            }
          ]
        }
      ]
    },

    plugins: [
      // new LodashModuleReplacementPlugin(),
      new webpack.IgnorePlugin(/^\.\/locale$/, /moment$/), // 排除语言包
      {
        define: {
          args() {
            // 定义全局变量注入代码中使用:
            return {
              THEME_CLASS: '"' + themeClass + '"' /* 初始主题类*/,
              THEME_METADATA: themeMetadata,
              VUE_SILENT: vueSilent,
              EXIST_APP_ICONFONT: existAppIconfont
            };
          }
        }
      },
      { case: false },
      // { analyzer: true },

      { imagemini: false } // 禁用内置图片压缩插件配置
    ]
  };
};

function traverseDirectory(dir, callback, underLocaleDirectory) {
  fs.readdirSync(dir).forEach(item => {
    const fullPath = path.join(dir, item);
    const stats = fs.statSync(fullPath);
    if (stats.isDirectory()) {
      // 如果是 locale 目录，则递归调用
      traverseDirectory(fullPath, callback, underLocaleDirectory || item == 'locale');
    } else if (stats.isFile() && /^([a-z]+_[A-Z]+)\.json$/.test(item) && underLocaleDirectory) {
      // 如果是文件且匹配国际化文件名，则调用回调函数
      callback(fullPath, item, dir);
    }
  });
}
function mergeLocaleJson(localeJSON, filePath, filename, dir, compiler) {
  try {
    let modulePath = require.resolve(path.resolve(filePath));
    delete require.cache[modulePath];
    let json = require(modulePath);
    let locale = filename.replace('.json', '');
    lodash.merge(localeJSON, {
      [locale]: json
    });
  } catch (error) {
    console.error(error);
  }

  // if (compiler && process.env.NODE_ENV === 'development') {
  //   // 监听指定目录
  //   compiler.hooks.afterCompile.tap('CopyLocalePlugin', (compilation) => {
  //     compilation.contextDependencies.add(dir);
  //   });
  // }
}
function mergeLocaleJsonFiles(wellappModules, localeJsonString, mode, logPrint) {
  const startTime = process.hrtime(); // 记录开始时间
  const localeJSON = {};
  let localePaths = [];
  wellappModules.forEach(function (module) {
    if (fs.existsSync('node_modules/' + module + '/app/web')) {
      traverseDirectory(
        'node_modules/' + module + '/app/web',
        function (filePath, filename, dir) {
          localePaths.push(dir);
          mergeLocaleJson(localeJSON, filePath, filename, dir);
        },
        false
      );
    }
  });
  let rootLocaleDir = 'app/web/locale';
  if (fs.existsSync(rootLocaleDir)) {
    traverseDirectory(
      rootLocaleDir,
      function (filePath, filename, dir) {
        localePaths.push(dir);
        mergeLocaleJson(localeJSON, filePath, filename, dir);
      },
      true
    );
  }
  if (!fs.existsSync('node_modules/.locale')) {
    fs.mkdirSync('node_modules/.locale');
  }
  let str = JSON.stringify(localeJSON);
  if (localeJsonString != str) {
    localeJsonString = str;
    for (let key in localeJSON) {
      fs.writeFileSync(
        `node_modules/.locale/${key}.json`,
        mode == 'production' ? JSON.stringify(localeJSON[key]) : JSON.stringify(localeJSON[key], null, '\t')
      );
    }

    const endTime = process.hrtime(startTime); // 记录结束时间
    const duration = (endTime[0] * 1e9 + endTime[1]) / 1e6; // 转换为毫秒
    if (logPrint) {
      console.log(`国际化json数据处理耗时: ${duration.toFixed(2)} ms`);
    }
  }

  return {
    localePaths,
    localeJsonString
  };
}

function extractAllCSS(dir, output) {
  const vueFiles = getVueFiles(dir);
  let allStyles = [];

  for (const file of vueFiles) {
    const styles = extractStyleBlocks(file);
    if (styles.length > 0) {
      allStyles = allStyles.concat(styles);
    }
  }

  const finalCSS = allStyles.join('\n\n');

  fs.mkdirSync(path.dirname(output), { recursive: true });
  fs.writeFileSync(output, finalCSS, 'utf-8');

  // console.log(`✅ 提取完成，共提取 ${vueFiles.length} 个组件的 CSS`);
  // console.log(`📄 输出文件: ${OUTPUT_CSS_PATH}`);
}

function getVueFiles(dir) {
  let files = fs.readdirSync(dir);
  let vueFiles = [];

  for (const file of files) {
    const fullPath = path.join(dir, file);
    const stat = fs.statSync(fullPath);

    if (stat.isDirectory()) {
      vueFiles = vueFiles.concat(getVueFiles(fullPath));
    } else if (file.endsWith('.vue')) {
      vueFiles.push(fullPath);
    }
  }

  return vueFiles;
}

function extractStyleBlocks(filePath) {
  const content = fs.readFileSync(filePath, 'utf-8');

  // 提取所有 <style>...</style> 块，包括 scoped、lang 属性
  const regex = /<style(?:\s[^>]*)?>([\s\S]*?)<\/style>/gi;

  const styles = [];
  let match;
  while ((match = regex.exec(content)) !== null) {
    styles.push(`/* ${path.basename(filePath)} */\n` + match[1].trim());
  }

  return styles;
}
