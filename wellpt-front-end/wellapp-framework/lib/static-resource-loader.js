const path = require('path');
const fs = require('fs');
const lodash = require('lodash');
const { read } = require('./assetjs-comment.js');
const { Resolver } = require('dns');
const utils = require('./utils');
const extend = require('extend');

function loader(app, modules) {
  let jsPack = {}; // 脚本包
  let cssPack = {}; // 样式包
  let themePack = {}; // 主题包
  let jsTemplatePack = {}; // js模板
  let mobileJsPack = {}; // 手机端的脚本包
  // let promises = [];
  let assetsVersion = undefined;

  // 资源有缓存时效的情况下，需要生成资源版本号，以便应用重启时候自动刷新版本号
  if (!!app.config.static.maxAge) {
    assetsVersion = utils.hashMd5(); // 统一生成资源版本号
  }

  // 清理缓存
  ['ROOT'].concat(modules).forEach(function (dirPath) {
    // 读取二开脚本注释
    // promises.push(
    //   read(
    //     dirPath === 'ROOT'
    //       ? path.join(app.baseDir, 'app', 'assets', 'develop-js')
    //       : path.join(app.baseDir, 'node_modules', dirPath, 'app', 'assets', 'develop-js')
    //   )
    // );

    const resourceDefDir =
      dirPath === 'ROOT'
        ? path.join(app.baseDir, '/config/resource')
        : path.join(app.baseDir, './node_modules', dirPath, '/config/resource');
    if (!fs.existsSync(resourceDefDir)) {
      return;
    }
    const files = fs.readdirSync(resourceDefDir);
    for (let f = 0, flen = files.length; f < flen; f++) {
      if (/.*resource\.json$/i.test(files[f])) {
        // 匹配**resource.json 资源描述文件
        const dir = path.join(resourceDefDir, files[f]);
        app.logger.debug('读取config/resource文件: %s', dir);
        let pack = {};
        try {
          pack = JSON.parse(fs.readFileSync(dir, 'utf-8'));
        } catch (error) {
          app.logger.error('读取config/resource文件: %s , 异常: ', dir, error);
        }
        extend(true, jsPack, pack.js);
        extend(true, mobileJsPack, pack.js);
        extend(true, cssPack, pack.css);
        extend(true, themePack, pack.theme);
        extend(true, jsTemplatePack, pack.jsTemplate);
      }
    }
  });

  fs.writeFile('run/application_js_meta.json', JSON.stringify(jsPack, null, '\t'), (err, data) => {});
  app.ASSETS_JS_META = { assetsVersion, jsPack, cssPack, themePack, jsTemplatePack, mobileJsPack, md5StaticPathEncoder: {} };
  resolveRequirejsConfig(app);
}

function resolveRequirejsConfig(app) {
  // 解析PC端脚本依赖
  const save = [];
  jsPackDependencyResolve(app.ASSETS_JS_META.jsPack, app, save);

  // 解析移动端脚本依赖
  mobileJsPackAliasResolve(app.ASSETS_JS_META.mobileJsPack);
  jsPackDependencyResolve(app.ASSETS_JS_META.mobileJsPack, app, null, true);
  // app.logger.info("%j",app.mobileJsPack);

  // requirejs全局配置
  const cf = {
    paths: {},
    shim: {},
    baseUrl: global.STATIC_PREFIX,
    waitSeconds: 0,
    map: {
      '*': {
        css: global.STATIC_PREFIX + '/js/requirejs/css.min.js'
      }
    }
  };
  if (app.ASSETS_JS_META.assetsVersion) {
    cf.urlArgs = 'v=' + app.ASSETS_JS_META.assetsVersion;
  }
  const _targetPack = app.ASSETS_JS_META.jsPack;
  for (let id in _targetPack) {
    if (!cf.paths[id] && _targetPack[id].abstract !== true) {
      if (_targetPack[id].path) {
        cf.paths[id] = '/static' + _targetPack[id].path;
      }
      cf.shim[id] = {
        deps: _targetPack[id].dependencies,
        exports: id
      };
    }
    // 依赖解析
    if (_targetPack[id].dependencies && _targetPack[id].dependencies.length) {
      _targetPack[id].dependencies.forEach(function (j) {
        if (_targetPack[j]) {
          if (!cf.paths[j] && _targetPack[j].abstract !== true) {
            if (_targetPack[j].path) {
              cf.paths[j] = '/static' + _targetPack[j].path;
            }
            cf.shim[j] = {
              deps: _targetPack[j].dependencies,
              exports: j
            };
          }
        }
      });
    }
  }

  fs.mkdir(path.join(app.baseDir, '/app/assets'), () => {
    fs.writeFile(
      path.join(app.baseDir, '/app/assets/requirejs-config.autobuild.js'),
      `requirejs.config(${JSON.stringify(cf, null, '')});window.WebApp={"configJsModules":${JSON.stringify(cf.paths, null, '')}};`,
      (err, data) => {
        if (err) app.logger.error('自动构建全局requirejs配置js文件异常：%s', err);
      }
    );
  });

  for (const k in app.ASSETS_JS_META.cssPack) {
    app.ASSETS_JS_META.cssPack[k].id = k;
    app.ASSETS_JS_META.cssPack[k].path =
      (lodash.startsWith(app.ASSETS_JS_META.cssPack[k].path, '/') ? '' : '/') + app.ASSETS_JS_META.cssPack[k].path;
    if (!lodash.endsWith(app.ASSETS_JS_META.cssPack[k].path, '.css')) {
      app.ASSETS_JS_META.cssPack[k].path += '.css';
    }

    app.ASSETS_JS_META.cssPack[k].path =
      (lodash.startsWith(global.STATIC_PREFIX) ? '' : global.STATIC_PREFIX) + app.ASSETS_JS_META.cssPack[k].path;
    save.push({
      id: k,
      name: app.ASSETS_JS_META.cssPack[k].name || k,
      path: app.ASSETS_JS_META.cssPack[k].path,
      type: 'css'
    });
  }

  for (const k in app.ASSETS_JS_META.jsTemplatePack) {
    app.ASSETS_JS_META.jsTemplatePack[k].id = k;
    app.ASSETS_JS_META.jsTemplatePack[k].path =
      (lodash.startsWith(app.ASSETS_JS_META.jsTemplatePack[k].path, '/') ? '' : '/') + app.ASSETS_JS_META.jsTemplatePack[k].path;
    save.push({
      id: k,
      name: app.ASSETS_JS_META.jsTemplatePack[k].name || k,
      path: app.ASSETS_JS_META.jsTemplatePack[k].path,
      type: 'jsTemplate'
    });
  }
  app.ASSETS_JS_META.nedbDatas = save;
  fs.writeFile('run/application_js_meta_deps.json', JSON.stringify(save, null, '\t'), (err, data) => {});
}

function mobileJsPackAliasResolve(mobilejspack) {
  mobilejspack.aliasMap = {};
  for (const k in mobilejspack) {
    let alias = mobilejspack[k].alias;
    if (alias && alias != k) {
      mobilejspack.aliasMap[k] = alias;
      mobilejspack[alias] = {};
      extend(true, mobilejspack[alias], mobilejspack[k]);
      mobilejspack[alias].id = alias;
      delete mobilejspack[alias].alias;
    }
  }
}

function jsPackDependencyResolve(jspack, app, savedb, isMobileJs) {
  const seed = Math.random();
  for (const k in jspack) {
    jspack[k].id = k;
    if (jspack[k].path) {
      jspack[k].path = (lodash.startsWith(jspack[k].path, '/') ? '' : '/') + jspack[k].path;
      if (app.config.confuseStaticPath) {
        const encodeStr = utils.hashMd5(Buffer.from(seed + jspack[k].path, 'binary'));
        app.ASSETS_JS_META.md5StaticPathEncoder[encodeStr + '.js'] = jspack[k].path + '.js'; // 加密地址与真实值进行映射
        jspack[k].path = '/' + encodeStr;
      }
    }

    resolveJsResourceDependencies(app, jspack, k, [], isMobileJs); // 解析脚本的完整依赖关系

    if (savedb != null && !jspack[k].abstract) {
      savedb.push({
        id: k,
        name: jspack[k].name || k,
        path: jspack[k].path,
        type: 'js',
        dependencies: jspack[k].dependencies,
        _dependencies: jspack[k]._dependencies,
        hooks: jspack[k].hooks
      });
    }
  }
}

function resolveJsResourceDependencies(app, jspack, id, chain, isMobileJs) {
  if (jspack[id].resolved) {
    return;
  }

  if (chain.indexOf(id) !== -1) {
    // 判断是否存在循环依赖
    chain.push(id);
    app.logger.info('存在循环依赖：%s', chain);
    throw new Error('resource circle dependency');
  }
  chain.push(id);

  if (jspack[id].dependencies && jspack[id].dependencies.length) {
    jspack[id]._dependencies = jspack[id].dependencies;
    let _newDependencies = [];
    for (let i = 0; i < jspack[id].dependencies.length; i++) {
      let dependencyid = jspack[id].dependencies[i];
      if (!jspack[dependencyid]) {
        // app.logger.warn('%s - 不存在的依赖：%s -> %s', isMobileJs === true ? '手机端脚本' : 'PC端脚本', id, dependencyid);
        continue;
      }
      if (isMobileJs && jspack[dependencyid].alias) {
        //移动端用别名替换依赖
        dependencyid = jspack[dependencyid].alias;
      }

      resolveJsResourceDependencies(app, jspack, dependencyid, chain, isMobileJs);
      if (!jspack[dependencyid].abstract) {
        _newDependencies.push(dependencyid);
      }
      // app.logger.info('解析依赖：%s -> %s -> %s',id , dependencyid,jspack[dependencyid].dependencies||[]);
      _newDependencies = _newDependencies.concat(jspack[dependencyid].dependencies || []);
    }

    jspack[id].dependencies = Array.from(new Set(_newDependencies));
  }
  jspack[id].resolved = true;
}

module.exports = { loader };
