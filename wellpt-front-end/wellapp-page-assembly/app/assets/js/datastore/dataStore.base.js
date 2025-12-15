define(['jquery', 'commons', 'constant', 'server'], function ($, commons, constant, server) {
  var DataStore = function (options) {
    var _self = this;
    _self.options = $.extend(
      {
        dataStoreId: null,
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
    if (typeof _self.options.onDataChange != 'function') {
      throw new Error('参数onDataChange必须是函数！');
    }
    if (commons.StringUtils.isBlank(_self.options.dataStoreId)) {
      throw new Error('dataStoreId不允许为空！');
    }
  };

  DataStore.prototype.cloneDataStore = function (options) {
    var self = this;
    var options = $.extend({}, self.options, options);
    return new DataStore(options);
  };
  DataStore.prototype.exportData = function (filter, error, success) {
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
      exportTemplateId: filter.exportTemplateId,
      searchSnapshot: filter.searchSnapshot,
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
      try {
        appContext.eval(
          filter.beforeExportCode,
          $(this),
          {
            $this: $(this),
            event: window.event,
            commons: commons,
            server: server,
            Api: filter.Api ? filter.Api : null,
            params: params
          },
          function (v) {
            if (v === false) {
              doExport = false; //不继续导出
            }
          }
        );
      } catch (e) {
        console.error('执行导出前的js代码事件处理异常', e);
      }
    }
    if (doExport) {
      server.FileDownloadUtils.download({
        service: 'cdDataStoreService.exportData',
        data: [filter.type, params],
        success: success,
        error: error
      });
    }
  };
  DataStore.prototype.setFilter = function (filter) {
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
  DataStore.prototype.hasPrevious = function () {
    return this.options.currentPage > 1;
  };
  DataStore.prototype.previousPage = function (data) {
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
  DataStore.prototype.hasNext = function () {
    var self = this;
    if (self.options.autoCount === false) {
      return true;
    }
    if (self.loaded === false) {
      return true;
    }
    return self.options.pageSize * self.options.currentPage < self.totalCount;
  };
  DataStore.prototype.nextPage = function (data) {
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
  DataStore.prototype.firstPage = function (data) {
    var self = this;
    var currentPage = self.options.currentPage;
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
  DataStore.prototype.lastPage = function (data) {
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
  DataStore.prototype.load = function (filter, params) {
    var self = this;
    var _filter = $.extend(
      {
        keyword: self.getKeyword(),
        criterions: self.options.criterions
      },
      filter
    );
    self.setFilter(_filter);
    self._loadData(params);
  };
  DataStore.prototype.getSortState = function () {
    return this.options.orders;
  };
  DataStore.prototype.getCount = function (always, filter, params) {
    var self = this;
    if (always === true) {
      self.setFilter(filter);
      self._loadCount(params);

      if (!window.dataStoreEvents) {
        window.dataStoreEvents = {};
      }
      window.dataStoreEvents[self.options.dataStoreId + '_getCount'] = function () {
        self._loadCount(params);
      };
    } else {
      return self.totalCount;
    }
  };
  DataStore.prototype.getData = function () {
    return this.data;
  };
  DataStore.prototype.getParams = function () {
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
    $.each(options.defaultOrders, function (idx, order) {
      var orders = params.orders;
      for (var i = 0; i < orders.length; i++) {
        if (order['sortName'] === orders[i]['sortName']) {
          return; //
        }
      }
      params.orders.push(order);
    });
    params.criterions = params.criterions.concat(options.defaultCriterions);
    return params;
  };
  DataStore.prototype._loadCount = function (params) {
    var _self = this;
    var options = _self.options;

    // server.JDS.call({
    //   service: 'cdDataStoreService.loadCount',
    //   data: [_self.getParams()],
    //   success: function (result) {
    server.JDS.restfulPost({
      url: '/proxy/api/datastore/loadCount',
      data: _self.getParams(),
      success: function (result) {
        if (result.code == 0) {
          _self.totalCount = result.data;
          _self.loaded = true;
        }

        _self.notifyChange(params);
      },
      error: options.error
    });
  };
  DataStore.prototype._loadData = function (params) {
    var _self = this;
    var options = _self.options;
    server.JDS.call({
      async: options.async,
      service: 'cdDataStoreService.loadData',
      data: [_self.getParams()],
      success: function (result) {
        if (result.msg == 'success') {
          var dataStoreData = result.data;
          _self.data = dataStoreData.data;
          _self.totalCount = dataStoreData.pagination.totalCount;
          _self.loaded = true;

          // 如果这个数据仓库有使用getCount事件，则重新执行该事件
          if (window.dataStoreEvents && window.dataStoreEvents[options.dataStoreId + '_getCount']) {
            window.dataStoreEvents[options.dataStoreId + '_getCount']();
          }
        }
        _self.notifyChange(params);
      },
      error: options.error
    });
  };
  DataStore.prototype.reflesh = function () {
    this._loadData();
  };
  DataStore.prototype.notifyChange = function (params) {
    var self = this;
    if (self.options.receiver && self.options.onDataChange) {
      self.options.onDataChange.call(self.receiver, self.data, self.totalCount, params, self.getDefinitionJson());
    } else if (self.options.onDataChange) {
      self.options.onDataChange(self.data, self.totalCount, params, self.getDefinitionJson());
    }
  };
  DataStore.prototype.clearParams = function () {
    this.options.params = {};
  };
  DataStore.prototype.addParam = function (key, value) {
    this.options.params[key] = value;
  };
  DataStore.prototype.removeParam = function (key) {
    var self = this;
    var value = self.options.params[key];
    delete self.options.params[key];
    return value;
  };
  DataStore.prototype.getParam = function (key) {
    return this.options.params[key];
  };
  DataStore.prototype.getKeyword = function () {
    return this.options.params['keyword'] || '';
  };
  DataStore.prototype.setKeyword = function (keyword) {
    return (this.options.params['keyword'] = keyword);
  };
  /**
   * 获取定义信息
   */
  DataStore.prototype.getDefinitionJson = function (params) {
    var _self = this;
    return _self.definition || {};
  };

  // 旧数据源
  var AutoComplete = function (options) {
    options.pageSize = options.pageSize || 10;
    DataStore.apply(this, arguments);
  };

  commons.inherit(AutoComplete, DataStore, {
    getParams: function () {
      var self = this;
      var options = self.options;
      var searchText = self.getKeyword();
      var contraint = options.criterions || {};
      var params = {
        searchText: searchText,
        pageSize: options.pageSize,
        currentPage: options.currentPage,
        dataSourceId: options.dataStoreId,
        contraint: JSON.stringify(contraint),
        fields: JSON.stringify(options.sqlFields)
      };
      return params;
    },
    _loadCount: function (params) {
      // 假分页
      var self = this;
      self.loaded = true;
      self.totalCount = self.options.pageSize;
    },
    _loadData: function (params) {
      var _self = this;
      var options = _self.options;
      $.ajax({
        async: options.async,
        url: ctx + '/dyform/autoComplete', // 服务器的地址
        data: _self.getParams(), // 参数
        dataType: 'json', // 返回数据类型
        type: 'POST', // 请求类型
        success: function (data) {
          _self.data = data;
          _self.loaded = true;
          _self.totalCount = data.length;
          _self.notifyChange(params);
        }
      });
    },
    getDefinitionJson: function (params) {
      return {};
    }
  });

  // 旧视图
  var DyViewDataStore = function (options) {
    options.pageSize = options.pageSize || 10;
    DataStore.apply(this, arguments);
  };

  commons.inherit(DyViewDataStore, DataStore, {
    getDyviewQueryInfo: function () {
      var self = this;
      var options = self.options;
      // 关键字
      var keyWord = self.getKeyword();
      if (keyWord && keyWord.trim) {
        keyWord = keyWord.trim();
      }
      keyWordObj = {};
      keyWordObj.all = keyWord;
      keyWordsArray = [];
      keyWordsArray.push(keyWordObj);
      var condSelectList = [];
      condSelectList.push({
        keyWords: keyWordsArray
      });
      // 分页
      var pageInfo = {};
      pageInfo.currentPage = options.currentPage;
      pageInfo.pageSize = options.pageSize;
      pageInfo.totalCount = 0;
      pageInfo.first = 0;
      pageInfo.autoCount = true;
      var dyViewQueryInfo = {};
      dyViewQueryInfo.viewName = '';
      dyViewQueryInfo.pageInfo = pageInfo;
      dyViewQueryInfo.viewUuid = options.dataStoreId;
      dyViewQueryInfo.condSelectList = condSelectList;
      return dyViewQueryInfo;
    },
    getParams: function () {
      var self = this;
      var options = self.options;
      var dyViewQueryInfo = self.getDyviewQueryInfo(self);
      dyViewQueryInfo.viewUuid = options.dataStoreId;
      return dyViewQueryInfo;
    },
    _loadCount: function (params) {
      // 假分页
      var self = this;
      self.loaded = true;
    },
    _loadData: function (params) {
      var _self = this;
      var options = _self.options;
      $.ajax({
        async: options.async,
        url: ctx + '/basicdata/view/loadViewData', // 服务器的地址
        data: JSON.stringify(_self.getParams()), // 参数
        dataType: 'json', // 返回数据类型
        contentType: 'application/json',
        type: 'POST', // 请求类型
        success: function (data) {
          _self.loaded = true;
          _self.data = data.data;
          _self.totalCount = data.total;
          _self.definition = data.viewDefinitionBean;
          _self.notifyChange(params);
        }
      });
    },
    getDefinitionJson: function (params) {
      var self = this;
      return self.definition || {};
    }
  });

  var DataStoreFactory = function (options) {
    if (options && options.type === 1) {
      return new AutoComplete(options);
    } else if (options && options.type === 2) {
      return new DyViewDataStore(options);
    }
    return new DataStore(options);
  };
  return DataStoreFactory;
});
