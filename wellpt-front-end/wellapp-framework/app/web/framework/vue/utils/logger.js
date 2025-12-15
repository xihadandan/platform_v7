class Logger {
  constructor(options) {}

  /**
   * 提交用户行为日志
   * @param {Object} logInfo - 行为日志信息
   * @param {string} logInfo.type - 行为类型，如 'click'
   * @param {string} logInfo.desc - 行为描述，如 '用户点击了提交按钮'
   * @param {Object} [logInfo.element] - 操作的UI元素信息
   * @param {string} [logInfo.element.tag] - 元素标签，如 'BUTTON'
   * @param {string} [logInfo.element.text] - 元素文本，如 '提交'
   * @param {string} [logInfo.element.id] - 元素ID，如 'submit-btn'
   * @param {string} [logInfo.element.xpath] - 元素xpath
   * @param {Object} [logInfo.page] - 当前页面信息
   * @param {string} [logInfo.page.id] - 页面ID
   * @param {string} [logInfo.page.url] - 页面URL
   * @param {string} [logInfo.page.title] - 页面标题
   * @param {string} [logInfo.page.referrer] - 页面来源
   * @param {Object} [logInfo.extraInfo] - 业务扩展信息
   * @param {string} logInfo.businessCode - 业务编码
   * @param {string} logInfo.moduleId - 模块ID
   * @param {string} [logInfo.userId] - 用户ID（可选）
   * @param {string} [logInfo.userName] - 用户ID（可选）
   * @returns {Promise<any>} 返回一个Promise
   */
  commitBehaviorLog(logInfo) {
    return new Promise((resolve, reject) => {
      try {
        if (logInfo) {
          let commit = info => {
            let p = [],
              codes = [];
            if (info.businessCode && typeof info.businessCode.then == 'function') {
              p.push(info.businessCode);
              codes.push('businessCode');
            }
            if (info.description && typeof info.description.then == 'function') {
              p.push(info.description);
              codes.push('description');
            }
            if (info.extraInfo && typeof info.extraInfo.then == 'function') {
              p.push(info.extraInfo);
              codes.push('extraInfo');
            }
            let req = () => {
              $axios
                .post(`/api/user/behavior/log`, info)
                .then(({ data }) => {
                  resolve();
                })
                .catch(error => {
                  console.error(error);
                  reject();
                });
            };
            if (p.length > 0) {
              Promise.all(p).then(values => {
                for (let i = 0; i < values.length; i++) {
                  info[codes[i]] = values[i];
                }
                req();
              });
            } else {
              req();
            }
          };
          if (typeof logInfo.then == 'function') {
            logInfo.then(info => {
              commit(info);
            });
          } else {
            commit(logInfo);
          }
        }
      } catch (error) {
        console.error('提交用户行为日志失败', error);
        reject();
      }
    });
  }
}
export { Logger };
