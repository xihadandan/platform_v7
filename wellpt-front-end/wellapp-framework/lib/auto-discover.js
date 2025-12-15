'use strict';
const fs = require('fs');
const path = require('path');
const assert = require('assert');
const is = require('is-type-of');
const utility = require('utility');
const utils = require('egg-core/lib/utils');
const FULLPATH = require('egg-core/lib/loader/file_loader').FULLPATH;
const lodash = require('lodash');
const DataStore = require('./nedb');
const crypto = require('crypto');
const _utils = require('./utils');
const extend = require('extend');
const assetjsComment = require('./assetjs-comment');

class AutoDiscover {
  constructor() {
    const _this = this;
    this.modules = [];
    fs.readdirSync('./node_modules').forEach(function (dirPath) {
      if (!lodash.startsWith(dirPath, 'wellapp-')) {
        return;
      }
      _this.modules.push(dirPath);
    });

  }
  /**
   *  自动加载业务模块下的controller
   * @param {*} options
   */
  autoDiscoverController(options) {
    const { app } = options;
    const controllerDirs = [], routers = [];
    app.WELLAPP_MODULES = this.modules;
    this.modules.forEach(function (dirPath) {
      const { app } = options;
      const _directory = path.join(app.baseDir, './node_modules', dirPath, '/app/controller');
      controllerDirs.push(_directory);
      routers.push(path.join(app.baseDir, './node_modules', dirPath, '/app/router.js'));
    });
    app.logger.info('开始自动加载依赖模块的Controller: %s', controllerDirs);
    app.loader.loadToApp(controllerDirs, 'controller', {
      caseStyle: 'lower',
      // directory: _directory,
      initializer: (obj, opt) => {
        if (is.function(obj) && !is.generatorFunction(obj) && !is.class(obj) && !is.asyncFunction(obj)) {
          obj = obj(app);
        }
        if (is.class(obj)) {
          obj.prototype.pathName = opt.pathName;
          obj.prototype.fullPath = opt.path;
          return wrapClass(obj);
        }
        if (is.object(obj)) {
          return wrapObject(obj, opt.path);
        }
        // support generatorFunction for forward compatbility
        if (is.generatorFunction(obj) || is.asyncFunction(obj)) {
          return wrapObject({ 'module.exports': obj }, opt.path)['module.exports'];
        }

        return obj;
      }
    });
    app.logger.info('开始自动加载依赖模块的Router: %s', routers);
    routers.forEach(r => {
      app.loader.loadFile(r);
    })



  }

  /**
   * 业务模块是以插件形式添加的，开启自动发现机制，不需要在plugin.js里面启用
   * @param {exports} exp
   */
  autoDiscoverPluginModule(exp) {
    // const content = require(path.resolve('./package.json'));

    this.modules.forEach(function (dirPath) {
      const packPath = path.join(path.resolve('./node_modules'), dirPath, 'package.json');
      if (!fs.existsSync(packPath)) {
        return;
      }

      const pack = require(packPath);

      if (!pack.eggPlugin || pack.name.indexOf('wellapp-') !== 0) {
        return;
      }

      assert(!exp[pack.eggPlugin.name]);

      exp[pack.eggPlugin.name] = {
        enable: true,
        package: pack.name
      };
    });
  }

  /**
   * 自动发现视图文件路径
   * @param  options
   */
  autoDiscoverViewFilePath(options) {
    options.app.viewPaths = new Set();
    const appViewDir = path.join('app', 'view');
    function ReadDirSync(p) {
      const files = fs.readdirSync(p);
      if (files.length) {
        files.forEach(f => {
          const _p = path.join(p, f);
          if (lodash.endsWith(f, '.nj') || lodash.endsWith(f, '.html')) {
            options.app.viewPaths.add(_p.substring(_p.indexOf(appViewDir) + appViewDir.length).replace(/\\/g, '/'));
          } else {
            if (fs.statSync(_p).isDirectory()) {
              ReadDirSync(_p);
            }
          }
        });
      }
    }

    options.app.config.view.root.forEach(element => {
      ReadDirSync(element);
    });
    options.app.viewPaths = Array.from(options.app.viewPaths);
  }

  /**
   * 自动发现组件定义
   * @param {*} options
   */
  autoDiscoverComponent(options) {
    options.app.timing.start('resolve component def');
    let _existsCompPath = {};

    this.modules.forEach(function (dirPath) {
      let componentPath = path.join(path.resolve('./node_modules'), dirPath, '/config/component.json');
      if (!fs.existsSync(componentPath)) {
        return;
      }
      const componentJsonDef = require(componentPath);
      if (!options.app.component) {
        options.app.component = {};
      }
      Object.assign(options.app.component, componentJsonDef);
      const registerAppComponent = {};
      for (const c in options.app.component) {
        if (_existsCompPath[c]) {
          options.app.logger.warn('路径=%s , 重复的组件wtype=%s', c);
          continue;
        }
        _existsCompPath[c] = true;
        options.app.component[c] = Object.assign(
          {
            supportsWysiwyg: true, // 默认组件支持设计所见即所得
            defineJs: lodash.snakeCase(c).replace('w_', 'widget_'), // 默认命名格式的组件定义脚本: widget_**_**
            previewHtml: '<div></div>', // 预览html
            configurable: true, // 默认可编辑
            category: 'app', // 默认类别应用组件
            enable: true, // 是否启用
            scope: ['wPage', 'wLayoutit'], // 组件默认生效页面类型:wPage
            defaultOptions: {
              // 组件配置的默认选项
              wtype: c,
              configuration: {}
            }
          },
          options.app.component[c]
        );
        options.app.component[c].defaultOptions.wtype = c;
      }
    });

    // 通过js定义的组件类解析
    _existsCompPath = {};
    this.modules.forEach(dirPath => {
      let componentPath = path.join(path.resolve('./node_modules'), dirPath, '/app/component');
      if (!fs.existsSync(componentPath)) {
        return;
      }
      const files = fs.readdirSync(componentPath);
      for (let f = 0, flen = files.length; f < flen; f++) {
        if (/^w.*\.js$/i.test(files[f])) {
          // 匹配w**.js组件定义
          const dir = path.join(componentPath, files[f]);
          const ComponentClass = options.app.loader.loadFile(dir, options.app.context);
          const componentInst = new ComponentClass();
          if (componentInst) {
            if (_existsCompPath[componentInst.defaultOptions.wtype]) {
              options.app.logger.warn('路径=%s , 重复的组件wtype=%s', dir, componentInst.defaultOptions.wtype);
              continue;
            }
            _existsCompPath[componentInst.defaultOptions.wtype] = true;
            options.app.component[componentInst.defaultOptions.wtype] = componentInst;
          }
        }
      }
    });

    options.app.logger.info('解析组件定义结束，耗时: %d ms', options.app.timing.end('resolve component def').duration);
  }

  autodIscoverImages(options) {
    options.app.timing.start('resolve images');
    options.app.projectImages = {};
    const imgs = new Set();
    const imgFolders = {};
    const _paths = [path.join(path.resolve('./'), '/app/public/images')];
    this.modules.forEach(dirPath => {
      let componentPath = path.join(path.resolve('./node_modules'), dirPath, '/app/public/images');
      if (!fs.existsSync(componentPath)) {
        return;
      }
      _paths.push(componentPath);
    });
    const imagesDir = path.join('app', 'public', 'images');
    _paths.forEach(dirPath => {
      if (!fs.existsSync(dirPath)) {
        return;
      }
      findImageFiles(dirPath, imgs, imgFolders, imagesDir);
    });
    options.app._imgFolders = imgFolders;
    options.app.timing.end('resolve images');
  }


  autoDiscoverStaticResourceImages(options) {
    // 仅解析静态资源模块：作为移动端、pc端的公用静态资源模块
    options.app.timing.start('resolve static resource images');
    const imgs = new Set();
    const imgFolders = {};
    const _paths = [];
    this.modules.forEach(dirPath => {
      const pkJson = require(path.join(path.resolve('./node_modules'), dirPath, '/package.json'));
      if (pkJson.staticPackage && dirPath != 'wellapp-static-resource') {
        let componentPath = path.join(path.resolve('./node_modules'), dirPath, '/app/public/resource/images');
        if (!fs.existsSync(componentPath)) {
          return;
        }
        _paths.push(componentPath);
      }
    });
    let ptResPack = path.join(path.resolve('./node_modules'), 'wellapp-static-resource', '/app/public/resource/images')
    if (fs.existsSync(ptResPack)) {
      _paths.push(ptResPack);
    }
    const imagesDir = path.join('app', 'public', 'resource', 'images');
    _paths.forEach(dirPath => {
      if (!fs.existsSync(dirPath)) {
        return;
      }
      findImageFiles(dirPath, imgs, imgFolders, imagesDir);
    });
    options.app._staticResourceImgFolders = imgFolders;
    options.app.timing.end('resolve static resource images');
  }
}




function findImageFiles(_path, imgs, imgFolders, imagesDir) {
  let files = fs.readdirSync(_path);
  files.forEach(function (item, index) {
    let fPath = path.join(_path, item);
    let stat = fs.statSync(fPath);
    if (stat.isDirectory() === true) {
      if (!imgFolders[item]) {
        imgFolders[item] = {};
      }
      findImageFiles(fPath, imgs, imgFolders[item], imagesDir);
    }
    if (stat.isFile() === true && /\.png$|\.jpg$|\.bpm$|\.jepg$|\.gif$|\.svg$|\.jpeg$/.test(item)) {
      if (!imgFolders.__imgs) {
        imgFolders.__imgs = [];
      }
      let p = fPath.substr(fPath.indexOf(imagesDir) + imagesDir.length);
      imgFolders.__imgs.push(p);
      imgs.add(p)
    }
  });
}

function wrapClass(Controller) {
  let proto = Controller.prototype;
  const ret = {};
  while (proto !== Object.prototype) {
    const keys = Object.getOwnPropertyNames(proto);
    for (const key of keys) {
      if (key === 'constructor') {
        continue;
      }
      const d = Object.getOwnPropertyDescriptor(proto, key);
      if (is.function(d.value) && !ret.hasOwnProperty(key)) {
        ret[key] = methodToMiddleware(Controller, key);
        ret[key][FULLPATH] = Controller.prototype.fullPath + '#' + Controller.name + '.' + key + '()';
      }
    }
    proto = Object.getPrototypeOf(proto);
  }
  return ret;

  function methodToMiddleware(Controller, key) {
    return function classControllerMiddleware(...args) {
      const controller = new Controller(this);
      if (!this.app.config.controller || !this.app.config.controller.supportParams) {
        args = [this];
      }
      return utils.callFn(controller[key], args, controller);
    };
  }
}

function wrapObject(obj, path, prefix) {
  const keys = Object.keys(obj);
  const ret = {};
  for (const key of keys) {
    if (is.function(obj[key])) {
      const names = utility.getParamNames(obj[key]);
      if (names[0] === 'next') {
        throw new Error(`controller \`${prefix || ''}${key}\` should not use next as argument from file ${path}`);
      }
      ret[key] = functionToMiddleware(obj[key]);
      ret[key][FULLPATH] = `${path}#${prefix || ''}${key}()`;
    } else if (is.object(obj[key])) {
      ret[key] = wrapObject(obj[key], path, `${prefix || ''}${key}.`);
    }
  }
  return ret;

  function functionToMiddleware(func) {
    const objectControllerMiddleware = async function (...args) {
      if (!this.app.config.controller || !this.app.config.controller.supportParams) {
        args = [this];
      }
      return await utils.callFn(func, args, this);
    };
    for (const key in func) {
      objectControllerMiddleware[key] = func[key];
    }
    return objectControllerMiddleware;
  }
}

module.exports = AutoDiscover;
