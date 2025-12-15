module.exports = function (source) {
  if (this.resourcePath.endsWith(".js")) {
    source = source.replace(/(?!\.+)\$axios/g, "uni.$axios"); // 替换代码里面的全局访问通过 uni 获取
  }
  return source;
};
