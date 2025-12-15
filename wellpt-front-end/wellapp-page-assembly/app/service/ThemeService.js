'use strict';

const Service = require('wellapp-framework').Service;

const prettier = require('prettier');
const path = require('path');
const fs = require('fs');
const compressing = require('compressing');
const lodash = require('lodash');
const less = require('less');
const tinycolor = require('tinycolor2');;

class ThemeService extends Service {
  async exportTheme(uuid, contentType, baseClass) {
    const { app, ctx } = this;

    try {
      const result = await ctx.curl(app.config.backendURL + '/api/theme/pack/details/' + uuid, {
        method: 'GET', contentType: 'json', dataType: 'json'
      });
      if (result.data && result.data.code === 0) {
        let packResult = await this.convertThemPackJsonToLess(result.data.data, contentType, baseClass);
        if (packResult) {
          return packResult;
        }
      }

    } catch (error) {
      app.logger.error('导出主题内容样式异常：%s', error);
    }
  }

  async convertThemPackJsonToLess(pack, contentType, baseClass) {
    const { app, ctx } = this;

    let defJson = JSON.parse(pack.defJson);

    if (baseClass) {
      baseClass.push(pack.themeClass);
    }
    let less = [`.${baseClass ? baseClass.join('.') : pack.themeClass} { \r`];
    // 颜色变量
    let body = defJson.body, themeColors = [], themeFontSize = [], defaultThemeColor = undefined;

    const getCssVariable = (target, wrapper) => {
      if (typeof target === 'object') {
        if (target.code != undefined) {
          if (wrapper) {
            less.push(wrapper);
            less.push(' { ');
          }
          if (target.value != undefined) {
            let value = target.value.trim();
            if (value.startsWith('calc(')) {
              value = `e('${value}')`;
            }
            less.push(`${target.code}: ${value}; `);
            if (target.code == '--w-primary-color') {// 计算主题色的 rgb 数值
              try {
                let rgb = tinycolor(target.value).toRgb();
                less.push(`${target.code}-rgb: ${rgb.r},${rgb.g},${rgb.b};`);
              } catch (error) {

              }
            }
          }
          if (target.derive && target.derive.length > 0) { // 衍生
            for (let i = 0, len = target.derive.length; i < len; i++) {
              let value = target.derive[i].value;
              if (value != undefined) {
                value = value.trim();
                if (value.startsWith('calc(')) {
                  value = `e('${value}')`;
                }
                less.push(`${target.derive[i].code}: ${value}; `);
              }
            }
          }

          if (wrapper) {
            less.push(' } ');
          }


        } else if (target.classify != undefined) {
          if (target.classify[0].code == '--w-primary-color') {
            // 主题色样式：生成色号变量
            for (let i = 0, len = target.classify.length; i < len; i++) {
              themeColors.push(target.classify[i].value);
              getCssVariable(target.classify[i], `&.primary-color-${i + 1}`);
              if (target.classify[i].default) {
                defaultThemeColor = target.classify[i].value;
                getCssVariable(target.classify[i]); // 额外生成默认
              }
            }
          } else if (target.classify[0].code == '--w-font-size') {

            // 字号
            for (let i = 0, len = target.classify.length; i < len; i++) {
              themeFontSize.push({
                label: target.classify[i].title,
                value: target.classify[i].value
              });
              if (i == 0) {
                getCssVariable(target.classify[i]); // 额外生成默认
              } else {
                // 生成字体类
                getCssVariable(target.classify[i], `&.font-size-${i + 1}`);
              }

            }
          } else if (target.classify[0].code && target.classify[0].code.indexOf("--w-") > -1) {
            for (let i = 0, len = target.classify.length; i < len; i++) {
              getCssVariable(target.classify[i]); // 额外生成默认
            }
          }

        } else {
          for (let k in target) {
            getCssVariable(target[k]);
          }
        }
      }
    }

    // 变量递归出基础样式
    for (let key in body) {
      if (key !== 'componentConfig' && key !== 'imageConfig') {
        getCssVariable(body[key]);
      }
    }


    // this.ctx.attachment('index.less');
    // this.ctx.set('Content-Type', 'application/octet-stream');
    let dirname = null;
    if (contentType != 'text') {
      // 生成文件夹，并压缩打包下载
      dirname = path.resolve(app.config.multipart.tmpdir, `${pack.themeClass}_` + new Date().getTime());
      // 创建临时主题包目录
      fs.mkdirSync(dirname, { recursive: true });
    }

    // 生成各个组件.less文件目录
    let imports = [];
    if (body.componentConfig) {
      for (let cfgKey in body.componentConfig) {
        let compStyleConfig = body.componentConfig[cfgKey];
        if (compStyleConfig) {
          let compDir = null;
          if (dirname) {
            compDir = lodash.kebabCase(cfgKey);
            fs.mkdirSync(path.join(dirname, compDir));
          }

          let inputs = [];
          // 变量数据
          for (let key in compStyleConfig) {
            if (key === 'styleDevelopment') {
              // 样式二开
              continue;
            }

            let cfg = compStyleConfig[key];
            for (let prop in cfg) {
              if (cfg[prop] != undefined) {
                let value = cfg[prop].trim();
                if (value.startsWith('--w-')) {
                  value = `var(${value})`;
                } else if (value.startsWith('inset ')) {
                  value = `inset var(${value.split('inset ')[1]})`;
                }
                inputs.push(`--${lodash.kebabCase(prop)}: ${value}; `);
              }
            }
          }

          if (compStyleConfig.styleDevelopment) {
            // 样式二开
            inputs.push(`/* ${cfgKey} - 样式二开: */ ${compStyleConfig.styleDevelopment}`);
          }
          if (dirname) {
            try {
              fs.writeFileSync(path.join(dirname, compDir, 'index.less'),
                prettier.format('& {' + inputs.join(' ') + '}', { semi: true, parser: "css", printWidth: 140, tabWidth: 2 })
              )
              imports.push(`@import './${compDir}/index.less';`);
            } catch (error) {
              app.logger.error('编译样式异常: %s ', compDir, error);
              throw error;
            }

          } else {
            less.push(...inputs);
          }
        }
      }
    }
    if (imports.length > 0) {
      less.push(...imports);
    }
    less.push(' } ');
    let lessContent = less.join(' '), lessBody = undefined;
    try {
      lessBody = prettier.format(lessContent, { semi: true, parser: "css", printWidth: 140, tabWidth: 2 });
    } catch (error) {
      lessBody = lessContent;
    }

    if (dirname == null) {
      return {
        body: lessBody,
        originalBody: lessContent,
        defaultThemeColor,
        themeColors,
        themeFontSize
      }
    }
    // 主题样式入口文件
    fs.writeFileSync(path.join(dirname, 'index.less'), lessBody)
    if (ctx.query.downloadReqId) { // 返回下载请求ID，提示前端交互下载完成
      ctx.cookies.set('downloadReqId', ctx.query.downloadReqId, { httpOnly: false });
    }
    // 主题元数据定义
    let meta = {
      id: pack.uuid,
      title: pack.name,
      class: pack.themeClass,
      colors: themeColors,
      fontSizes: themeFontSize
    }
    fs.writeFileSync(path.join(dirname, 'meta.json'), JSON.stringify(meta, null, '\t'))

    const zipStream = new compressing.zip.Stream();
    let dirs = fs.readdirSync(dirname);
    for (let d of dirs) {
      zipStream.addEntry(path.join(dirname, d));
    }
    ctx.attachment(`${pack.themeClass}.zip`);
    ctx.set('Content-Type', 'application/octet-stream; charset=UTF-8');
    ctx.body = zipStream;

  }

  async queryPublishedThemePack() {
    const { app, ctx } = this;

    const result = await ctx.curl(app.config.backendURL + '/api/theme/pack/getAllPublished', {
      method: 'GET',
      contentType: 'json',
      dataType: 'json',
      data: {
        type: ctx.query.type
      }
    });
    return result.data.data;

  }

  async queryPublishedThemeOptions() {
    let options = await this.app.redis.get('themePackPublished_' + this.ctx.query.type);
    if (options) {
      return JSON.parse(options);
    }
    let packs = await this.queryPublishedThemePack();
    options = [];
    for (let p of packs) {
      let defJson = JSON.parse(p.defJson), body = defJson.body;
      let themeColorClassify = body.colorConfig.themeColor.classify,
        fontSizeClassify = body.fontConfig.fontSize.classify,
        color = [], fontSize = [];
      for (let c of themeColorClassify) {
        color.push({
          color: c.value, default: c.default, title: c.title
        })
      } for (let c of fontSizeClassify) {
        fontSize.push({
          fontSize: c.value, title: c.title
        })
      }
      options.push({
        title: p.name,
        class: p.themeClass,
        color, fontSize
      });
    }
    this.app.redis.set('themePackPublished_' + this.ctx.query.type, JSON.stringify(options))
    return options;
  }


  async publishThemePackAsChunkContent(uuid) {
    const { app, ctx } = this;
    app.redis.del('themePackPublished_' + this.ctx.query.type);
    const packs = await this.queryPublishedThemePack();
    if (packs && packs.length) {
      let themeCss = [];
      for (let p of packs) {
        // if (uuid && p.uuid == uuid) {
        try {
          let result = await this.convertThemPackJsonToLess(p, 'text');
          let output = await less.render(result.body, {
            compress: true
          });
          app.redis.sadd('THEME_DESIGN_CLASS', p.themeClass)
          themeCss.push(output.css);
          app.redis.set(`THEME_DESIGN_CLASS_CSS:${p.themeClass}`, output.css);

        } catch (error) {
          app.logger.error('发布主题 %s 失败 : ', p.name, error)
        }
        // }

        // fs.writeFile(
        //   path.join(app.baseDir, './app/public', `${p.themeClass}.css`),
        //   Buffer.from(output.css),
        //   () => { }
        // );
      }
      app.redis.set(`THEME_DESIGN_CSS_CONTENT`, themeCss.join('/n/r'))
    }
  }



}

module.exports = ThemeService;
