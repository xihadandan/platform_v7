/**
 * 常用的函数方法
 */

// 下拉框输入项筛选函数
export function filterSelectOption(input, option) {
  return (
    (option.componentOptions.tag != 'a-select-opt-group' &&
      (option.componentOptions.children[0].text.toUpperCase().indexOf(input.toUpperCase()) >= 0 ||
        (option.key && option.key.indexOf(input.toUpperCase()) >= 0))) ||
    (option.componentOptions.propsData.value && option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0) ||
    (option.componentOptions.children[0].text && option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0)
  );
}

/**
 *
 * @param {*} height 在ps滚动区域，ps高度小于此高度，该弹出框挂载在body上，超出此高度，挂载在ps上
 * @param {*} parentNode 在ps区域外，如果是true，挂载在body上
 * @returns
 */
export const getPopupContainerNearestPs = function (height, parentNode) {
  return triggerNode => {
    // height为弹出框高度
    if (!height) {
      height = 600;
    }
    if (triggerNode && !parentNode) {
      parentNode = triggerNode.parentNode;
    }
    if (triggerNode && triggerNode.closest('.ps')) {
      if (triggerNode.closest('.ps').clientHeight < height) {
        // 页面高度小于600时，挂载到body下
        return document.body;
      } else {
        return triggerNode.closest('.ps');
      }
    } else if (triggerNode && triggerNode.closest('.ant-table')) {
      return triggerNode.closest('.ant-table');
    } else if (parentNode === true) {
      return document.body;
    }
    return parentNode || document.body;
  };
};
export const getDropdownClassName = function () {
  return 'ps__child--consume'; //有ps滚动时，阻止外层ps下拉滚动
};

export const getTopContainer = function () {
  return findTopBody();
};
function findTopBody(win = window) {
  try {
    // 如果当前窗口有父窗口且父窗口不是自己
    if (win.parent && win.parent !== win) {
      return findTopBody(win.parent);
    }
    return win.document.body;
  } catch (e) {
    // 跨域访问会抛出异常，返回当前窗口的body
    return win.document.body;
  }
}

/**
 * 文件上传到后端服务
 * @param {*} options
 * @returns
 */
export const customFileUploadRequest = function (options) {
  return new Promise((resolve, reject) => {
    let file = options.file,
      fileSize = file.size,
      fileName = file.name,
      formData = new FormData();
    formData.set('frontUUID', file.uid);
    formData.set('localFileSourceIcon', '');
    formData.set('size', fileSize);
    let pushFolder = options.folder && options.folder.folderID != undefined;
    if (pushFolder) {
      // 文件夹参数
      formData.set('folderID', options.folder.folderID);
      if (options.folder.purpose) {
        formData.set('purpose', options.folder.purpose);
      }
      if (options.folder.popFolderFile) {
        formData.set('popFolderFile', options.folder.popFolderFile);
      }
    }
    let headers = {
      'Content-Disposition': `attachment; filename="${encodeURIComponent(fileName)}"`,
      'Content-Type': 'multipart/form-data'
    };
    formData.set('file', file);

    $axios
      .post(`/proxy-repository/repository/file/mongo/${!pushFolder ? 'savefilesChunk' : 'saveFilesAndPushToFolder'}`, formData, {
        headers: headers
      })
      .then(({ data }) => {
        if (data.code == 0 && data.data) {
          if (typeof options.onSuccess == 'function') {
            options.onSuccess();
          }
          resolve(data.data[0]);
        }
      });
  });
};

export const downloadLink = function (url, removeTimeout = 5000) {
  let iframe = document.createElement('iframe');
  iframe.src = url;
  iframe.setAttribute('style', 'display:none;');
  document.body.appendChild(iframe);
  setTimeout(function () {
    iframe.remove();
  }, removeTimeout);
};

// 获取链接地址参数
export const getQueryString = function (name, defaultValue) {
  var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)');
  var values = window.location.search.substr(1).match(reg);
  if (values != null) {
    return decodeURIComponent(values[2]);
  }
  if (defaultValue != null) {
    return defaultValue;
  }
  return null;
};

/**
 * 外层使用Scroll组件滚动条组件，重置滚动条
 * @param {*} vm 当前组件
 * @param {*} top 滚动的高度,置顶时top=0
 */
export const psScrollResize = function (vm, top) {
  vm.$nextTick(() => {
    vm.pageContext && vm.pageContext.emitEvent('perfectScrollbarToResize', { top });
  });
};

/**
 *  给请求参数添加请求头，数据Id通过查询参数自动获取
 * @param config 请求参数配置
 * @returns {*}
 */
export const addDbHeader = function (config) {
  let dbLinkConfUuid = getQueryString('dbLinkConfUuid');
  return addDbHeaderByDbId(config, dbLinkConfUuid);
};

/**
 *  给请求参数添加请求头
 * @param config  请求参数配置
 * @param dbLinkConfUuid 数据库Id
 * @returns {{headers}|*}
 */
export const addDbHeaderByDbId = function (config, dbLinkConfUuid) {
  if (dbLinkConfUuid) {
    if (!config.hasOwnProperty('headers')) {
      config['headers'] = {};
    }
    config.headers.dbLinkConfUuid = dbLinkConfUuid;
  }
  return config;
};

export const addDbUrl = function (evt, url) {
  let dbLinkConfUuid = evt.$evtWidget.dataSourceParams.dbLinkConfUuid;
  if (dbLinkConfUuid) {
    url = url + '&dbLinkConfUuid=' + dbLinkConfUuid;
  }
  return url;
};
