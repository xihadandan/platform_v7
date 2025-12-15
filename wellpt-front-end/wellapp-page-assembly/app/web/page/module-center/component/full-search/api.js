// 请求分类树
export const fetchCategoryListAsTree = params => {
  return new Promise((resolve, reject) => {
    $axios
      .get('/proxy/api/fulltext/category/listAsTree', {
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
// 请求保存分类
export const fetchSaveCategory = data => {
  return new Promise((resolve, reject) => {
    $axios.post('/proxy/api/fulltext/category/save', data).then(res => {
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

// 请求删除分类
export const fetchDeleteCategory = uuid => {
  return new Promise((resolve, reject) => {
    $axios.delete('/proxy/api/fulltext/category/delete/' + uuid).then(res => {
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

export const fetchCategoryByUuid = uuid => {
  return new Promise((resolve, reject) => {
    $axios.get('/proxy/api/fulltext/category/get/' + uuid).then(res => {
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

// 请求存储对象
export const fetchDataModelsByType = data => {
  return new Promise((resolve, reject) => {
    $axios.post('/proxy/api/dm/getDataModelsByType', data).then(res => {
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

// 请求保存数据模型
export const fetchSaveModel = data => {
  return new Promise((resolve, reject) => {
    $axios.post('/proxy/api/fulltext/model/save', data).then(res => {
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

// 请求保存所有全文检索数据模型
export const fetchSaveAllModel = data => {
  return new Promise((resolve, reject) => {
    $axios.post('/proxy/api/fulltext/model/saveAll', data).then(res => {
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

// 请求数据模型列表（根据分类uuid）
export const fetchModelListByCategoryUuid = params => {
  return new Promise((resolve, reject) => {
    $axios
      .get('/proxy/api/fulltext/model/listByCategoryUuid', {
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

// 请求数据模型列表（根据分类模块id）
export const fetchModelListByModuleId = params => {
  return new Promise((resolve, reject) => {
    $axios
      .get('/proxy/api/fulltext/model/listByModuleId', {
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

// 请求删除搜索模型
export const fetchDeleteModel = uuid => {
  return new Promise((resolve, reject) => {
    $axios.delete('/proxy/api/fulltext/model/delete/{uuid}?delete=' + uuid).then(res => {
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

// 请求数据模型详情（通过uuid）
export const fetchModelDetailByUuid = params => {
  return new Promise((resolve, reject) => {
    $axios
      .get('/proxy/api/dm/getDetails', {
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

export const queryFormDefinitionIgnoreJsonByDataModelId = params => {
  return new Promise((resolve, reject) => {
    $axios
      .get('/proxy/api/dyform/definition/queryFormDefinitionIgnoreJsonByDataModelId', {
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

// 请求查询表单定义（通过模块id）
export const fetchFormDefinitionByModuleIds = data => {
  return new Promise((resolve, reject) => {
    $axios.post('/proxy/api/dyform/definition/queryFormDefinitionByModuleIds', data).then(res => {
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