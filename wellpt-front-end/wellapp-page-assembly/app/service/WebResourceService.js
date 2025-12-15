'use strict';

const Service = require('wellapp-framework').Service;
const lodash = require('lodash');

/**
 * @description web资源服务
 */
class WebResourceService extends Service {
  async getJavascriptById(id) {
    return this.app.nedb.findOneSync({
      id,
      type: 'js'
    });
  }

  async getJavascriptsByIds(ids) {
    return this.app.nedb.findSync({
      id: { $in: ids },
      type: 'js'
    });
  }

  async getJavascriptTemplates(ids) {
    this.app.jsTemplatePack;
    const templates = [];
    for (let i = 0, len = ids.length; i < len; i++) {
      if (this.app.jsTemplatePack[ids[i]]) {
      }
    }
  }

  async listJavascriptContainsDependency(id) {
    return this.app.nedb.findSync({
      $where: function () {
        return this.type == 'js' && this.dependencies != undefined && this.dependencies.indexOf(id) != -1;
      }
    });
  }

  async matchJavascriptContainsDependency(dependencyId, likeValue, pageSize, ids) {
    let params = {
      type: 'js'
    };

    if (dependencyId) {
      params.dependencies = dependencyId;
    }

    let results = [];
    if (ids) {
      // 有指定查找id集合的情况
      params.id = { $in: ids };
      results = await this.app.nedb.findSync(params);
    }

    if (likeValue) {
      params.$or = [{ id: { $regex: new RegExp(likeValue) } }, { name: { $regex: new RegExp(likeValue) } }];
    }

    if (ids) {
      params.id = { $nin: ids };
    }
    results = results.concat(await this.app.nedb.findSync(params));
    if (pageSize != undefined && pageSize < results.length) {
      return results.slice(0, pageSize);
    }
    return results;
  }

  async listAllByIdLikeAndNameLikeAndType(likeValue, type) {
    const params = {
      type
    };
    if (likeValue) {
      params.$or = [{ id: { $regex: new RegExp(likeValue) } }, { name: { $regex: new RegExp(likeValue) } }];
    }

    return this.app.nedb.findSync(params);
  }

  async getJavascriptRequirejsConfig(ids, isMobile) {
    const cf = {
      paths: {},
      shim: {},
      waitSeconds: 0,
      map: { '*': { css: global.STATIC_PREFIX + '/js/requirejs/css.min.js' } }
    };

    cf.baseUrl = global.STATIC_PREFIX;
    const { app, ctx } = this;
    const _targetPack = isMobile ? app.mobileJsPack : app.jsPack;
    ids.forEach(id => {
      if (_targetPack[id]) {
        if (!cf.paths[id]) {
          cf.paths[id] = '/static' + _targetPack[id].path;
          cf.shim[id] = {
            deps: _targetPack[id].dependencies,
            exports: id
          };
        }
        // 依赖解析
        if (_targetPack[id].dependencies && _targetPack[id].dependencies.length) {
          _targetPack[id].dependencies.forEach(function (j) {
            if (_targetPack[j]) {
              if (!cf.paths[j]) {
                cf.paths[j] = '/static' + _targetPack[j].path;
                cf.shim[j] = {
                  deps: _targetPack[j].dependencies,
                  exports: j
                };
              }
            }
          });
        }
      }
    });

    return cf;
  }

  async getThemeCss(extras = [], isThemeCssNeeded) {
    const { app, ctx } = this;

    const css = [];
    const themeCss = isThemeCssNeeded ? await ctx.service.userThemeService.getThemeCss() : [];
    const cssNeeded = [...new Set([...extras, ...themeCss])];

    for (const file of cssNeeded) {
      if (app.cssPack[file]) css.push(app.cssPack[file]);
    }

    return lodash.orderBy(css, ['order'], ['asc']);
  }

  async getProjectImageFolders(folderName) {
    let id = '__ProjectImageFolders__' + folderName;
    let data = await this.app.nedb.findOneSync({
      id,
    });
    if (!data) {
      let projectImgRoot = { id: -1, name: '项目图片', children: [], data: { folderType: 2 } };
      if (this.app[folderName]) {
        let _cascadeChild = function (_parent, folders) {
          for (let k in folders) {
            if (k === '__imgs') {
              _parent.data.__imgs = [];
              for (let f = 0, flen = folders[k].length; f < flen; f++) {
                _parent.data.__imgs.push({
                  folderType: 2,
                  id: folders[k][f]
                });
              }

              continue;
            }
            let f = { id: k, name: k, children: [], data: { folderType: 2 } };
            _parent.children.push(f);
            _cascadeChild(f, folders[k]);
          }
        };
        _cascadeChild(projectImgRoot, this.app[folderName]);
      }
      this.app.nedb.insert({
        id,
        folders: projectImgRoot
      });
      return projectImgRoot;
    }
    return data.folders;
  }
}

module.exports = WebResourceService;
