const fs = require('fs');
const path = require('path');
const lodash = require('lodash');
async function read(dir) {
  let metas = {};
  if (fs.existsSync(dir)) {
    getJsFiles(dir, metas);
  }
  return metas;
}

function define(param) {
  return Array.isArray(param) ? param : [];
}
function jsMeata(jsStr) {
  let metas = jsStr.substr(0, jsStr.indexOf('define(')).split('\n');
  let meta = { hooks: [] };
  for (let i = 0, len = metas.length; i < len; i++) {
    if (metas[i].indexOf('@description') != -1) {
      // 文件头部描述
      let splits = metas[i].split('@description');
      meta.name = splits[1].trim();
    }
  }
  // 分析文件内部方法描述
  let codeSegment = jsStr.split('define(')[1];
  let lines = codeSegment.split('\n'),
    _hook = null;
  for (let i = 0, len = lines.length; i < len; i++) {
    if (lines[i].indexOf('@hook') != -1) {
      _hook = {};
      continue;
    }
    if (_hook != null) {
      if (lines[i].indexOf('@description') != -1) {
        _hook.description = lines[i].split('@description')[1].trim();
        continue;
      }
      if (lines[i].indexOf('.prototype.') != -1) {
        // 描述信息
        let parts = lines[i].split('.prototype.');
        _hook.key = parts[1].split('=')[0].trim();
        if (!_hook.description) {
          _hook.description = parts[0] + '.' + _hook.key;
        }
        meta.hooks.push(_hook);
        _hook = null;
      }
    }
  }
  meta.deps = eval(jsStr);
  return meta;
}

function getJsFiles(dir, metas, filename) {
  const stat = fs.statSync(dir);
  if (stat.isDirectory()) {
    //判断是不是目录
    const dirs = fs.readdirSync(dir);
    dirs.forEach(value => {
      getJsFiles(path.join(dir, value), metas, value);
    });
  } else if (stat.isFile() && lodash.endsWith(filename, '.js')) {
    let jspath = dir.split(path.join('app', 'assets'))[1];
    if (process.platform === 'win32') {
      jspath = jspath.replace(/\\/g, '/');
    }

    // 读取文件内容
    let x = fs.readFileSync(dir, 'utf-8');
    try {
      let meta = jsMeata(x);
      // console.log(`>> ${filename} metadata: `, JSON.stringify(meta));
      metas[filename.substr(0, filename.indexOf('.js'))] = {
        path: jspath.substr(0, jspath.indexOf('.js')),
        name: meta.name,
        dependencies: meta.deps,
        hooks: meta.hooks
      };
    } catch (error) {}
  }
}

module.exports = { read };
