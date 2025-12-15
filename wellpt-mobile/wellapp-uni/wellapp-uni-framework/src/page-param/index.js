let pageParams = {};
module.exports = {
  setPageParameter: function (name, value) {
    pageParams[name] = value;
    // 10妙内清空参数
    setTimeout(function () {
      delete pageParams[name];
    }, 10000);
  },
  getPageParameter: function (name) {
    let value = pageParams[name];
    delete pageParams[name];
    return value;
  },
};
