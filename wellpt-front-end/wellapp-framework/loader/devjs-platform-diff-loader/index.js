
/**
 * 针对二开脚本的不同终端平台编译解析
 * @param {*} source
 * @returns
 */
module.exports = function (source) {
  if (source.indexOf('#ifdef') != -1) {
    let sources = source.split('\n');
    let remove = false;
    for (let index = 0; index < sources.length; index++) {
      let lineCode = sources[index];
      if (lineCode.indexOf('#ifdef ') != -1 && lineCode.indexOf('#ifdef PC') == -1) {
        // 非 PC 端可执行脚本
        sources.splice(index--, 1);
        remove = true;
        continue;
      }
      if (remove) {
        if (lineCode.indexOf('#endif') == -1) {
          sources.splice(index--, 1);
          continue;
        } else {
          sources.splice(index--, 1);
          remove = false;
        }
      }
    }
    return sources.join('\n');
  }
  return source;

};
