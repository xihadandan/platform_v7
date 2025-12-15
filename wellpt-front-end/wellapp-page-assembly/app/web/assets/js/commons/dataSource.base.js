import { download } from '@framework/vue/utils/util';
import { addDbHeaderByDbId } from '@framework/vue/utils/function';

function DataSource(options) {
  var _self = this;
  _self.options = Object.assign(
    {},
    {
      dataStoreId: null,
      loadDataUrl: '/proxy/api/datastore/loadData',
      loadDataCntUrl: '/proxy/api/datastore/loadCount',
      proxy: {}, // 代理数据源
      onDataChange: null,
      receiver: null,
      exportColumns: [],
      renderers: [],
      defaultOrders: [],
      orders: [],
      autoCount: true,
      defaultCriterions: [],
      criterions: [],
      params: {},
      currentPage: 1,
      pageSize: 20
    },
    options
  );

  _self.data = [];
  _self.totalCount = 0;
  _self.loaded = false;
  _self.definition = null;
  if (_self.options.onDataChange != undefined && typeof _self.options.onDataChange != 'function') {
    throw new Error('参数onDataChange必须是函数！');
  }
}

DataSource.prototype.cloneDataSource = function (options) {
  var self = this;
  var options = Object.assign({}, self.options, options);
  return new DataSource(options);
};
DataSource.prototype.exportData = function (filter, error, success) {
  var _self = this;
  var options = _self.options;
  var params = {
    params: {
      dataStoreId: options.dataStoreId,
      pagingInfo: {
        pageSize: options.pageSize,
        currentPage: options.currentPage
      },
      params: options.params,
      criterions: options.criterions,
      renderers: options.renderers,
      orders: options.orders
    },
    columns: options.exportColumns,
    fileName: filter.fileName,
    // exportTemplateId: filter.exportTemplateId,
    // searchSnapshot: filter.searchSnapshot,
    extras: filter.extras
  };

  if (filter.pagination) {
    if (filter.pagination.currentPage) {
      params.params.pagingInfo.currentPage = filter.pagination.currentPage < 1 ? 1 : filter.pagination.currentPage;
    }
    if (filter.pagination.pageSize) {
      params.params.pagingInfo.pageSize = filter.pagination.pageSize;
    }
  }
  if (filter.exportColumns) {
    params.columns = filter.exportColumns;
  }
  if (filter.criterions && filter.criterions.length > 0) {
    params.params.criterions = filter.criterions;
  }
  params.params.orders = params.params.orders.concat(options.defaultOrders);
  params.params.criterions = params.params.criterions.concat(options.defaultCriterions);
  var doExport = true;
  if (filter.beforeExportCode) {
    //导出前自定义事件处理
    //FIXME: 数据导出前的执行代码块
    // try {
    //   appContext.eval(
    //     filter.beforeExportCode,
    //     $(this),
    //     {
    //       $this: $(this),
    //       event: window.event,
    //       commons: commons,
    //       server: server,
    //       Api: filter.Api ? filter.Api : null,
    //       params: params
    //     },
    //     function (v) {
    //       if (v === false) {
    //         doExport = false; //不继续导出
    //       }
    //     }
    //   );
    // } catch (e) {
    //   console.error('执行导出前的js代码事件处理异常', e);
    // }
  }
  if (doExport) {
    let jsonData = { serviceName: 'cdDataStoreService', methodName: 'exportData' };
    let args = [filter.type, params];
    if (this.options.loadDataUrl.startsWith('/proxy/api/dm/loadData')) {
      // 数据模型数据导出
      jsonData.serviceName = 'dataModelService';
      args = [this.options.loadDataUrl.split('/proxy/api/dm/loadData/')[1], filter.type, params];
    }
    download({
      url: '/proxy/file/download/services',
      data: {
        jsonData: JSON.stringify(jsonData),
        args: JSON.stringify(args)
      }
    });
  }
};
DataSource.prototype.setDefaultCriterions = function (criterions) {
  this.options.defaultCriterions = criterions || [];
};
DataSource.prototype.setFilter = function (filter) {
  if (!filter) {
    return;
  }
  var self = this;
  var options = self.options;
  if (filter.pagination) {
    if (filter.pagination.currentPage) {
      options.currentPage = filter.pagination.currentPage < 1 ? 1 : filter.pagination.currentPage;
    }
    if (filter.pagination.pageSize) {
      options.pageSize = filter.pagination.pageSize;
    }
  }
  if (filter.criterions) {
    options.criterions = filter.criterions;
  }
  if (filter.renderers) {
    options.renderers = filter.renderers;
  }
  if (filter.orders) {
    options.orders = filter.orders;
  }
  self.setKeyword(filter.keyword);
};
DataSource.prototype.hasPrevious = function () {
  return this.options.currentPage > 1;
};
DataSource.prototype.previousPage = function (data) {
  var self = this;
  if (self.hasPrevious()) {
    var currentPage = self.options.currentPage;
    self.load(
      {
        pagination: {
          pageSize: self.options.pageSize,
          currentPage: parseInt(currentPage) - 1
        }
      },
      data
    );
  }
};
DataSource.prototype.hasNext = function () {
  var self = this;
  if (self.options.autoCount === false) {
    return true;
  }
  if (self.loaded === false) {
    return true;
  }
  return self.options.pageSize * self.options.currentPage < self.totalCount;
};
DataSource.prototype.nextPage = function (data) {
  var self = this;
  if (self.hasNext()) {
    var currentPage = self.options.currentPage;
    self.load(
      {
        pagination: {
          pageSize: self.options.pageSize,
          currentPage: parseInt(currentPage) + 1
        }
      },
      data
    );
  }
};
DataSource.prototype.firstPage = function (data) {
  var self = this;
  self.load(
    {
      pagination: {
        pageSize: self.options.pageSize,
        currentPage: 1
      }
    },
    data
  );
};
DataSource.prototype.lastPage = function (data) {
  var self = this;
  var lastPage = 1;
  if (self.loaded === true && self.options.autoCount === true) {
    lastPage = Math.ceil(self.totalCount / self.options.pageSize);
  }
  this.load(
    {
      pagination: {
        pageSize: self.options.pageSize,
        currentPage: lastPage
      }
    },
    data
  );
};
DataSource.prototype.load = function (filter, params) {
  var self = this;
  var _filter = Object.assign(
    {},
    {
      keyword: self.getKeyword(),
      criterions: self.options.criterions
    },
    filter
  );
  self.setFilter(_filter);
  return self._loadData(params);
};
DataSource.prototype.getSortState = function () {
  return this.options.orders;
};
DataSource.prototype.getCount = function (always, filter, params) {
  var self = this;
  if (always === true) {
    self.setFilter(filter);
    self._loadCount(params);

    if (!window.DataSourceEvents) {
      window.DataSourceEvents = {};
    }
    window.DataSourceEvents[self.options.dataStoreId + '_getCount'] = function () {
      self._loadCount(params);
    };
  } else {
    return self.totalCount;
  }
};
DataSource.prototype.getData = function () {
  return this.data;
};
DataSource.prototype.getParams = function () {
  var _self = this;
  var options = _self.options;
  options.proxy.storeId = options.dataStoreId;
  var params = {
    dataStoreId: options.dataStoreId,
    proxy: options.proxy,
    pagingInfo: {
      pageSize: options.pageSize,
      currentPage: options.currentPage,
      autoCount: options.autoCount
    },
    params: options.params,
    criterions: options.criterions,
    renderers: options.renderers,
    orders: options.orders
  };
  params.orders = params.orders || [];
  for (let i = 0, len = options.defaultOrders.length; i < len; i++) {
    let orders = params.orders,
      order = options.defaultOrders[i],
      complict = false;
    for (let j = 0; j < orders.length; j++) {
      if (options.defaultOrders[i]['sortName'] === orders[j]['sortName']) {
        complict = true;
        break;
      }
    }
    if (!complict) {
      params.orders.push(order);
    }
  }

  params.criterions = params.criterions.concat(options.defaultCriterions);
  return params;
};
DataSource.prototype._loadCount = function (params) {
  var _self = this;
  var _p = _self.getParams();
  if (params != undefined) {
    Object.assign(_p.params, params);
  }
  let head = {};
  if (_p.params.dbLinkConfUuid) {
    head = addDbHeaderByDbId(head, _p.params.dbLinkConfUuid);
  }
  $axios.post(this.options.loadDataCntUrl, _p, head).then(({ data }) => {
    if (data.code == 0) {
      _self.totalCount = parseInt(data.data);
      _self.loaded = true;
    }

    _self.notifyChange(params);
  });
};
DataSource.prototype._loadData = function (params) {
  var _self = this;
  var options = _self.options;
  var _p = _self.getParams();
  if (params != undefined) {
    Object.assign(_p.params, params);
  }
  return new Promise((resolve, reject) => {
    let head = {};
    if (_p.params.dbLinkConfUuid) {
      head = addDbHeaderByDbId(head, _p.params.dbLinkConfUuid);
    }
    $axios
      .post(options.loadDataUrl, _p, head)
      .then(({ data }) => {
        if (data.code == 0) {
          _self.data = data.data;
          _self.totalCount = data.data.pagination.totalCount;
          _self.loaded = true;

          // 如果这个数据仓库有使用getCount事件，则重新执行该事件
          if (!_self.totalCount && window.DataSourceEvents && window.DataSourceEvents[options.dataStoreId + '_getCount']) {
            window.DataSourceEvents[options.dataStoreId + '_getCount']();
          }
          resolve(data.data);
        }
        _self.notifyChange(params);
      })
      .catch(error => {
        reject(error);
      });
  });
};
DataSource.prototype.reflesh = function () {
  this._loadData();
};
DataSource.prototype.notifyChange = function (params) {
  var self = this;
  if (self.options.receiver && self.options.onDataChange) {
    self.options.onDataChange.call(self.receiver, self.data, self.totalCount, params, self.getDefinitionJson());
  } else if (self.options.onDataChange) {
    self.options.onDataChange(self.data, self.totalCount, params, self.getDefinitionJson());
  }
};
DataSource.prototype.clearParams = function () {
  this.options.params = {};
};
DataSource.prototype.addParam = function (key, value) {
  if (this.options.params == undefined) {
    this.options.params = {};
  }
  this.options.params[key] = value;
};
DataSource.prototype.removeParam = function (key) {
  var self = this;
  if (this.options.params != undefined) {
    var value = self.options.params[key];
    delete self.options.params[key];
    return value;
  }
  return undefined;
};
DataSource.prototype.getParam = function (key) {
  return this.options.params != undefined ? this.options.params[key] : undefined;
};
DataSource.prototype.getKeyword = function () {
  return this.options.params != undefined ? this.options.params['keyword'] || '' : '';
};
DataSource.prototype.setKeyword = function (keyword) {
  if (this.options.params == undefined) {
    this.options.params = {};
  }
  this.options.params['keyword'] = keyword;
};
/**
 * 获取定义信息
 */
DataSource.prototype.getDefinitionJson = function (params) {
  var _self = this;
  return _self.definition || {};
};

export default DataSource;
