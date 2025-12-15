'use strict';

/**
 * @description 平台框架默认配置处理
 * @author chenq
 */
const path = require('path');
const lodash = require('lodash');
const fs = require('fs');
const os = require('os');

module.exports = appInfo => {
  const config = {};
  if (global.wellappFrameworkConfgloaded) {
    return config;
  }
  global.wellappFrameworkConfgloaded = true;
  // const content = require(path.resolve('./package.json'));
  const modules = [];
  // 读取依赖的内部应用模块，识别以wellapp-前缀
  fs.readdirSync('./node_modules').forEach(function (dirPath) {
    if (!lodash.startsWith(dirPath, 'wellapp-')) {
      return;
    }
    modules.push(dirPath);
  });

  const viewDirs = [path.join(appInfo.baseDir, '/app/view')]; // 视图目录
  const staticDirs = [path.join(appInfo.baseDir, '/app/public'), path.join(appInfo.baseDir, '/app/assets')]; // js/css等其他静态静态资源目录
  const watchModuleDirs = []; // 监视的模块目录
  const modulesDone = [],
    sortedStaticModules = [];
  for (let i = 0, len = modules.length; i < len; i++) {
    if (modulesDone.indexOf(modules[i]) !== -1) {
      continue;
    }
    sortedStaticModules.push(modules[i]);
    modulesDone.push(modules[i]);
    watchModuleDirs.push('node_modules/' + modules[i]);
    // 添加子模块的视图资源目录
    viewDirs.push(path.join(appInfo.baseDir, '/node_modules/', modules[i], '/app/view'));
  }
  // 静态资源顺序：业务web主工程、 wellapp-web、其他静态资源包
  if (!appInfo.baseDir.endsWith('wellapp-web')) {
    sortedStaticModules.splice(sortedStaticModules.indexOf('wellapp-web'), 1);
    sortedStaticModules.splice(0, 0, 'wellapp-web');
  }

  for (let m of sortedStaticModules) {
    // 添加子模块的静态资源目录
    staticDirs.push(path.join(appInfo.baseDir, '/node_modules/', m, '/app/public'));
    staticDirs.push(path.join(appInfo.baseDir, '/node_modules/', m, '/app/assets'));
  }

  console.info('视图解析路径', viewDirs);
  console.info('静态资源解析路径', staticDirs);

  // 定义视图解析引擎
  config.view = {
    root: viewDirs.join(','),
    defaultExtension: '.html', // 默认后缀
    defaultViewEngine: 'nunjucks', // 默认视图引擎: nunjucks
    mapping: {
      '.nj': 'nunjucks'
    }
  };

  global.STATIC_PREFIX = '/static';

  // 定义静态资源映射路径
  config.static = {
    // prefix: "/static",
    dir: (() => {
      let _d = [{ prefix: '/public', dir: path.join(appInfo.baseDir, '/public/') }];
      for (let i = 0, len = staticDirs.length; i < len; i++) {
        _d.push({ prefix: '/static', dir: staticDirs[i] }, { prefix: '/app-resources', dir: staticDirs[i] });
      }
      return _d;
    })(),
    gzip: true,
    usePrecompiledGzip: true,
    alias: {}
  };

  [('/static/wps/oaassist/EtOAAssist/', '/static/wps/oaassist/WppOAAssist/', '/static/wps/oaassist/WpsOAAssist/')].forEach(s => {
    let key = path.normalize(s);
    config.static.alias[key] = path.join(key, 'index.html');
  });

  console.debug('监视目录', watchModuleDirs);

  // 文件变更触发重启
  const watchDirs = [],
    ignoreDirs = [];
  watchModuleDirs.forEach(function (d) {
    watchDirs.push(path.join(d, 'lib'));
    watchDirs.push(path.join(d, 'app'));
    watchDirs.push(path.join(d, 'app.js'));
    watchDirs.push(path.join(d, 'agent.js'));
    watchDirs.push(path.join(d, 'index.js'));
    watchDirs.push(path.join(d, 'config'));
    // 监视目录忽略的相关路径：变更时候不会重启
    ignoreDirs.push(path.join(d, 'app/public'));
    ignoreDirs.push(path.join(d, 'app/assets'));
    ignoreDirs.push(path.join(d, 'app/view'));
    ignoreDirs.push(path.join(d, 'app/web'));

    if (d.includes('wellapp-theme')) {
      watchDirs.push(path.join(d, 'src/scripts'));
      ignoreDirs.push(path.join(d, 'config/resource'));
    }
  });

  // 监听目录内的文件修改
  config.development = {
    watchDirs, // 监听目录
    reloadPattern: ['**/**.js', '!**/manifest.json', '!**/**.vue'],
    ignoreDirs
  };

  config.test = {
    key: appInfo.name + '_123456'
  };

  // jwt密钥
  config.jwt = {
    secret:
      "g(SPdXCIGQDhqIShNCprAFq0EfQJNXNBbVnHTdDXhJGgPA9RoDkib57irDu2XNJsDY(OpehC^2iC30246e7U4G5n5gCLCW5ma2BwaVI3dTmEe0c~(SMDexqP6qyYVJ4qnWb~yb(14HuZl#08qHvUkj#yPn58WJFx1UwTe#T3R4GLb4vozIlxrKzRKQ7()VAX#DyQz)8wIiWnViPD40VYv3J0)l^N8oSRCJmrQ0#HnLY2U6F^6OLwu5HFqY3YK~Sz'}"
  };

  // 默认中文
  config.i18n = {
    defaultLocale: 'zh-CN',
    writeCookie: false
  };

  const faviconPath = path.join(appInfo.baseDir, 'favicon.png');
  if (fs.existsSync(faviconPath)) {
    // 网址icon
    config.siteFile = {
      '/favicon.ico': fs.readFileSync(faviconPath)
    };
  }

  // 定义统一异常处理
  config.onerror = {
    errorPageUrl: '/error'
  };

  // 404 页面定义
  // config.notfound = {
  //   pageUrl: '/error?real_status=404',
  // };

  const _mobileAuthOptions = {
    successRedirect: '/mobile/mui/login',
    failureRedirect: '/mobile/mui/login',
    logoutRedirect: '/mobile/mui/login',
    failureMessage: true
  };
  // 登录认证相关跳转地址配置
  config.authenticateOptions = {
    //默认用户登录
    local: {
      successRedirect: '/login?success',
      failureRedirect: '/login?error',
      logoutRedirect: '/login?logout',
      failureMessage: true,
      mobile: _mobileAuthOptions
    },

    //身份证token登录
    'idnumber-token': {
      successRedirect: '/login?success',
      failureRedirect: '/mobile/mui/login.jsp?idnumberTokenFail',
      failureMessage: true
    },
    //超级管理员登录
    superadmin: {
      successRedirect: '/superadmin/login?success',
      failureRedirect: '/superadmin/login?error',
      logoutRedirect: '/superadmin/login?logout',
      failureMessage: true
    },
    //oauth2授权码模式登录
    oauth2: { successRedirect: '/', failureRedirect: '/login' },
    'oauth2-password': {
      successRedirect: '/login?success',
      failureRedirect: '/login?redirectOauth2GrantPasswordError',
      failureMessage: true
    },
    'dingtalk-qr': {
      successRedirect: '/login?success',
      failureRedirect: '/login?error',
      logoutRedirect: '/login?logout',
      failureMessage: true
    },
    'feishu-token': {
      successRedirect: '/login?success',
      failureRedirect: '/login?error',
      logoutRedirect: '/login?logout',
      failureMessage: true
    },
    'jwt-token': {
      successRedirect: '/login?success',
      failureRedirect: '/login?error',
      failureMessage: true
    }
  };

  // 文件上传开启
  config.multipart = {
    mode: 'stream', //默认为流
    fileModeMatch: /^(?!(\/proxy|\/repository\/file\/mongo\/save)).*/, // 非/proxy开头地址使用 mode=file 代理转发文件必须为stream
    fileSize: '2048mb', //文件上传限制  富文本图片+视频上传有用到，其他未知
    tmpdir: path.join(os.tmpdir(), 'egg-multipart-tmp', appInfo.name),
    cleanSchedule: {
      cron: '0 30 4 * * *',
      disable: false
    },
    whitelist: function (filename) {
      return true; //支持所有文件类型上传
    }
  };

  // 请求报文的限制大小
  config.bodyParser = {
    formLimit: '30mb',
    jsonLimit: '30mb',
    textLimit: '30mb'
  };

  // 默认的匿名访问地址，项目扩展可定义annoymousUrls
  config.defaultAnnoymousUrls = [
    '/oauth/*',
    '/login',
    '/sys/[a-zA-Z_0-9-]+/(login|user_reg|user_forget_pwd)',
    '/sys-public/[a-zA-Z_0-9-]+/index',
    '/logout',
    '/j_spring_security_logout',
    '/superadmin/login',
    '/error',
    '/basicdata/system/param/get',
    '/helloworld',
    '/mobile/',
    '/login/oauth2/callback',
    '/captcha',
    '/webservices/wellpt/rest/service',
    '/login/idnumberLogin',
    '/dx-post-proxy',
    '/uni-app-design-json/get',
    '/login/feishuToken',
    '/login/feishuUserTokenInfo',
    '/login/dingtalkAuth',
    '/login/weixinAuth'
  ];

  // 首页定义
  config.index = {
    page: '/web/app/security_homepage',
    security: true
  };

  // /mobile/pt/dingtalk/getconnect/oauth2','/mobile/pt/dingtalk/auth',
  config.session = {
    key: '_SESSION',
    renew: true, // Session 的有效期仅剩下最大有效期一半的时候，重置 Session 的有效期
    maxAge: 'session'
    // maxAge: 3 * 3600 * 1000, // session 有效时长：3小时
  };
  config.security = {
    csrf: {
      queryName: '_csrf', // 通过 query 传递 CSRF token 的默认字段为 _csrf
      bodyName: '_csrf', // 通过 body 传递 CSRF token 的默认字段为 _csrf
      headerName: 'x-csrf-token',
      cookieName: '_csrfToken', // Cookie 中的字段名，默认为 csrfToken
      ignore: [
        '/mobile/pt/dingtalk/**',
        '/webservices/wellpt/rest/service',
        '/uni/app/page/get',
        '/dx-post-proxy/**',
        '/proxy-repository/repository/file/mongo/**',
        '/repository/file/mongo/**',
        '/oauth/token',
        '/heartbeat'
      ]
    },
    domainWhiteList: ['localhost']
  };

  config.gzip = {
    threshold: 1024 // 小于 1k 的响应体不压缩
  };

  config.dxPostProxy = {
    request: async (ctx, data) => {
      ctx.body = '404';
    }
  };

  config.vuessr = {
    layout: path.join(appInfo.baseDir, 'node_modules/wellapp-framework/app/web/view/layout.html'),
    renderOptions: { basedir: path.join(appInfo.baseDir, 'app/view') }
  };

  //https
  const certDir = path.join(appInfo.baseDir, '../cert');
  if (fs.existsSync(certDir)) {
    const files = fs.readdirSync(certDir);
    const https = {};
    if (files.length) {
      files.forEach(f => {
        let _p = path.join(certDir, f);
        if (lodash.endsWith(f, '.key')) {
          https.key = _p;
        }
        if (lodash.endsWith(f, '.crt') || lodash.endsWith(f, '.cer')) {
          https.cert = _p;
        }
        if (lodash.endsWith(f, '*.pem')) {
          https.ca = _p;
        }
      });
    }

    config.cluster = {
      https
    };
    console.log('启用https : ', https);
  }

  config.webpack = {
    browser: false // 关闭自动打开浏览器，也可以指定为 url 地址
  };

  let address = 'localhost';
  if (!global.process.env.H5_SERVER) {
    try {
      const interfaces = os.networkInterfaces();
      for (const iface of Object.values(interfaces)) {
        for (const config of iface) {
          if (config.family === 'IPv4' && !config.internal) {
            address = config.address;
            // console.log('解析到本地IP地址: ', address)
            break;
          }
        }
      }
    } catch (error) {}
  }

  config.h5Server = global.process.env.H5_SERVER || `http://${address}:8081`;
  config.domain = global.process.env.SERVER_DOMAIN || 'http://127.0.0.1:7001';

  return config;
};
