export const fetchSearch = () => {
  return new Promise((resolve, reject) => {
    $axios.get('/proxy/api/fulltext/setting/getSearch').then(res => {
      if (res.status === 200) {
        if (res.data && res.data.code === 0) {
          const data = res.data.data;
          resolve(data);
        } else {
          reject(res.data);
        }
      } else {
        reject(res);
      }
    });
  });
};

export const fetchIndex = () => {
  return new Promise((resolve, reject) => {
    $axios.get('/proxy/api/fulltext/setting/getIndex').then(res => {
      if (res.status === 200) {
        if (res.data && res.data.code === 0) {
          const data = res.data.data;
          resolve(data);
        } else {
          reject(res.data);
        }
      } else {
        reject(res);
      }
    });
  });
};

export const fetchSaveSetting = data => {
  return new Promise((resolve, reject) => {
    $axios.post('/proxy/api/fulltext/setting/save', data).then(res => {
      if (res.status === 200) {
        if (res.data && res.data.code === 0) {
          const data = res.data.data;
          resolve(data);
        } else {
          reject(res.data);
        }
      } else {
        reject(res);
      }
    });
  });
};
// 根据系统ID获取全文检索分类
export const fetchListCategoryBySystem = () => {
  return new Promise((resolve, reject) => {
    $axios.get('/proxy/api/fulltext/category/listBySystem').then(res => {
      if (res.status === 200) {
        if (res.data && res.data.code === 0) {
          const data = res.data.data;
          resolve(data);
        } else {
          reject(res.data);
        }
      } else {
        reject(res);
      }
    });
  });
};
// 获取索引重建日志列表
export const fetchRebuildLog = params => {
  return new Promise((resolve, reject) => {
    $axios
      .get('/proxy/api/fulltext/rebuild/log/listBySettingUuid', {
        params
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            const data = res.data.data;
            resolve(data);
          } else {
            reject(res.data);
          }
        } else {
          reject(res);
        }
      });
  });
};

// 请求查询索引
export const fetchQueryFulltextIndex = data => {
  return new Promise((resolve, reject) => {
    $axios.post('/proxy/api/fulltext/index/query', data).then(res => {
      if (res.status === 200) {
        if (res.data && res.data.code === 0) {
          const data = res.data.data;
          resolve(data);
        } else {
          reject(res.data);
        }
      } else {
        reject(res);
      }
    });
  });
};

// 删除redis缓存（通过key）
export const fetchDeleteCacheByKey = params => {
  return new Promise((resolve, reject) => {
    $axios
      .get('/api/cache/deleteByKey', {
        params
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data === 'ok') {
            resolve();
          } else {
            reject(res);
          }
        } else {
          reject(res);
        }
      });
  });
};


// 请求默认组织版本
export const fetchDefaultOrgVersion = params => {
  return new Promise((resolve, reject) => {
    $axios
      .get('/proxy/api/org/organization/version/published', {
        params
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            resolve(res.data.data);
          } else {
            reject(res);
          }
        } else {
          reject(res);
        }
      });
  });
};

// 根据数据模型UUID获取全文检索数据模型
export const fetchByDataModeUuid = params => {
  return new Promise((resolve, reject) => {
    $axios
      .get('/proxy/api/fulltext/model/getByDataModeUuid/' + params.dataModeUuid)
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            resolve(res.data.data);
          } else {
            reject(res);
          }
        } else {
          reject(res);
        }
      });
  });
};
