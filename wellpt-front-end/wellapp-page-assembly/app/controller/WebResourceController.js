'use strict';

const Controller = require('wellapp-framework').Controller;
const lodash = require('lodash');
const path = require('path');
const fs = require('fs');
const prettier = require('prettier');
const esprima = require('esprima');

/**
 * 解析web资源文件相关请求控制层
 */
class WebResourceController extends Controller {
  async getProjectImageFolder() {
    let { app, ctx } = this;
    let projectImgRoot = await ctx.service.webResourceService.getProjectImageFolders();
    let mongodbImgRoot = { id: -2, name: '数据库图片', children: [], data: { folderType: 1 } };
    try {
      let result = await this.ctx.curl(app.config.backendURL + '/repository/file/mongo/getFilesFromFolder', {
        dataAsQueryString: true,
        dataType: 'json',
        data: {
          folder: 'pictureL-pict-pict-pict-pictureLibpi',
          purpose: ''
        }
      });
      if (result && result.data && result.data.length) {
        mongodbImgRoot.data.__imgs = [];
        let _data = result.data;
        for (let i = 0, len = _data.length; i < len; i++) {
          mongodbImgRoot.data.__imgs.push({
            folderType: 1,
            id: _data[i].fileid,
            name: _data[i].filename
          });
        }
      }
    } catch (error) {
      this.app.logger.error('查询数据库图片异常: %s', error);
    }

    this.ctx.body = [projectImgRoot, mongodbImgRoot];
  }

  async viewCodeSource() {
    let { app, ctx } = this, fileName = ctx.query.fileName, fileType = ctx.query.fileType, methodName = ctx.query.methodName;
    let subDir = fileType == 'js' ? '/app/web/widget/@develop' : '/app/web/template';
    let findFile = (dir) => {
      let p = path.join(dir, fileName + (fileType == 'js' ? '.js' : '.vue'));
      if (fs.existsSync(p)) {
        return p;
      }
      let subDirectories = fs.readdirSync(dir);
      for (let d of subDirectories) {
        let p = path.join(dir, d);
        if (fs.statSync(p).isDirectory()) {
          let result = findFile(p);
          if (result) {
            return result;
          }
        }
      }
    }

    for (let m of app.WELLAPP_MODULES) {
      let p = path.join(app.baseDir, './node_modules', m, subDir);
      if (fs.existsSync(p)) {
        let result = findFile(p);
        if (result) {
          let content = fs.readFileSync(result, 'utf-8');
          ctx.body = { content };//  prettier.format(content, { semi: true, parser: "babel", printWidth: 140, tabWidth: 2 })
          if (fileType == 'js' && methodName) {
            try {
              let module = esprima.parseModule(content, { range: true, loc: true });
              for (let item of module.body) {
                if (item.id && item.id.name == fileName) {
                  // 找到解析的类体
                  let body = item.body.body;
                  for (let b of body) {
                    if (b.kind == 'method' && methodName == b.key.name) {
                      ctx.body = {
                        line: [b.loc.start.line, b.loc.end.line],
                        content,
                      };
                      return;
                    }
                  }
                  break;
                }
              }
            } catch (error) {
              console.error(error)
            }
          }
          return;
        }
      }
    }
    ctx.body = {
      content: undefined
    };

  }

  async getProjectImages() {
    let { app, ctx } = this;
    let projectImgRoot = await ctx.service.webResourceService.getProjectImageFolders('_imgFolders'),
      commonImgRoot = await ctx.service.webResourceService.getProjectImageFolders('_staticResourceImgFolders');
    let images = { project: [], common: [] };
    let cascadeGetImages = (root, key) => {
      if (root.data && root.data.__imgs && root.data.__imgs.length > 0) {
        root.data.__imgs.forEach(img => {
          images[key].push({
            url: img.id.replace(/\\/g, '/')
          })
        })
      }
      if (root.children && root.children.length > 0) {
        root.children.forEach(child => {
          cascadeGetImages(child, key);
        })
      }
    };
    cascadeGetImages(projectImgRoot, 'project');
    cascadeGetImages(commonImgRoot, 'common');

    this.ctx.body = images;
  }

  async getImagesUnderFolderPath() { }

  async queryJavascriptContainsDependency() {
    let ctx = this.ctx;
    ctx.body = await ctx.service.webResourceService.listJavascriptContainsDependency(
      ctx.request.body.dependencyFilter
    );
  }

  async queryJavascriptByIds() {
    let ctx = this.ctx;
    ctx.body = await ctx.service.webResourceService.getJavascriptsByIds(
      ctx.request.body.ids
    );
  }
}

module.exports = WebResourceController;
