//  根据流程uuid获取数据
export const fetchFlowData = (uuid) => {
  const params = {
    uuid
  };
  return new Promise((resolve, reject) => {
    window.$axios
      .get('/proxy/api/workflow/scheme/flow/json.action', {
        params
      })
      .then(res => {
        if (res.status === 200 && res.data) {
          resolve(res.data);
        } else {
          reject(res)
        }
      });
  });
}

// 根据流程id获取环节
export const fetchFlowTasksById = (flowDefId) => {
  const params = {
    flowDefId
  };
  return new Promise((resolve, reject) => {
    window.$axios
      .get('/api/workflow/definition/getFlowTasks', {
        params
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            const data = res.data.data;
            resolve(data);
          } else {
            reject(res.data)
          }
        } else {
          reject(res)
        }
      });
  });
}

// 获取系统默认组织
export const fetchedDefaultOrgBySystem = (systemId) => {
  return new Promise((resolve, reject) => {
    const params = {
      system: systemId
    };
    $axios
      .get('/proxy/api/org/organization/getDefaultOrgBySystem', {
        params
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            const data = res.data.data;
            resolve(data);
            /*
            data.id 组织ID"O_"开头
            data.uuid === orgUuid 组织UUID
            */
            console.log('获取默认组织', data);
          } else {
            reject(res.data)
          }
        } else {
          reject(res)
        }
      });
  });
}

// 获取子流程自定接口
export const fetchSubtaskDispatcherCustomInterfaces = () => {
  return new Promise((resolve, reject) => {
    window.$axios.get('/api/workflow/definition/getSubtaskDispatcherCustomInterfaces').then(res => {
      if (res.status === 200) {
        if (res.data && res.data.code === 0) {
          const data = res.data.data;
          resolve(data);
        } else {
          reject(res.data)
        }
      } else {
        reject(res)
      }
    });
  });
}

// 获取计时服务列表
export const fetchTimerService = ({ pageSize = 10, currentPage = 1, keyword = '' } = {}) => {
  const params = {
    dataStoreId: "CD_DS_TS_TIMER_CONFIG",
    proxy: {
      storeId: "CD_DS_TS_TIMER_CONFIG"
    },
    pagingInfo: {
      pageSize,
      currentPage,
      autoCount: true
    },
    params: {
      keyword
    },
    criterions: [
      {
        sql: "(system_unit_id = :currentUserUnitId or system_unit_id = 'S0000000000') and category_uuid in (select c.uuid from ts_timer_category c where c.id = 'flowTiming')"
      }
    ],
    renderers: [],
    orders: [
      {
        sortName: "code",
        sortOrder: "asc"
      }
    ]
  }
  if (keyword) {
    params.criterions.push({
      type: 'or',
      conditions: [
        { columnIndex: "name", type: "like", value: keyword },
        { columnIndex: "id", type: "like", value: keyword },
        { columnIndex: "code", type: "like", value: keyword },
        { columnIndex: "categoryName", type: "like", value: keyword },
        { columnIndex: "timingModeTypeName", type: "like", value: keyword },
        { columnIndex: "timeLimitTypeName", type: "like", value: keyword },
      ]
    })
  }
  return new Promise((resolve, reject) => {
    $axios
      .post('/proxy/api/datastore/loadData', {
        ...params
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            const data = res.data.data;
            resolve(data);
          } else {
            reject(res.data)
          }
        } else {
          reject(res)
        }
      });
  });
}

// 获取计时服务详情
export const fetchTimerItem = (uuid) => {
  const params = {
    uuid
  };
  return new Promise((resolve, reject) => {
    $axios
      .get('/api/ts/timer/config/get', {
        params
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            const data = res.data.data;
            resolve(data);
          } else {
            reject(res.data)
          }
        } else {
          reject(res)
        }
      });
  })
}

// 请求单据转换列表
export const fetchBotRuleConfFacadeService = () => {
  const params = {
    serviceName: 'botRuleConfFacadeService',
    queryMethod: 'loadSelectData',
    pageSize: 1000,
    pageNo: 1
  };

  return new Promise((resolve, reject) => {
    $axios
      .post('/common/select2/query', {
        ...params
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.results) {
            const data = res.data.results;
            resolve(data);
          }
        } else {
          reject(res)
        }
      });
  })
}

// 请求流程id是否存在
export const isExitWorkFlowId = (flowDefId) => {
  const params = {
    flowDefId
  };
  return new Promise((resolve, reject) => {
    $axios
      .get('/api/workflow/definition/countById', {
        params
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            let data = res.data.data; // data:'0'不存在
            data = parseInt(data);
            resolve(data);
          } else {
            reject(res.data);
          }
        } else {
          reject(res);
        }
      });
  });
}

//  获取流程设置
export const fetchFlowSettingByKey = (key) => {
  const params = {
    key
  };
  return new Promise((resolve, reject) => {
    window.$axios
      .get('/proxy/api/workflow/setting/getByKey', {
        params
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            const data = res.data.data;
            resolve(data);
          } else {
            reject(res.data)
          }
        } else {
          reject(res)
        }
      });
  });
}

// 请求系统参数
export const fetchSystemParamValue = (key) => {
  const params = {
    key
  };
  return new Promise((resolve, reject) => {
    window.$axios
      .get(`/basicdata/system/param/get`, {
        params
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            const data = res.data.data;
            resolve(data);
          } else {
            reject(res.data)
          }
        } else {
          reject(res)
        }
      })
  })
}

// 请求指定字段
export const fetchFormFieldSelections = ({ searchValue, formUuid, inputMode = 41 }) => {
  const params = {
    searchValue,
    serviceName: 'flowSchemeService',
    queryMethod: 'getFormFieldSelections',
    pageSize: 20,
    formUuid,
    inputMode
  };

  return new Promise((resolve, reject) => {
    $axios
      .post('/common/select2/query', {
        ...params
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.results) {
            const data = res.data.results;
            resolve(data);
          }
        } else {
          reject(res)
        }
      });
  })
}

// 查询所有启用的组织列表 queryEnableOrgUnderSystems可查询多个系统
export const fetchEnableOrgs = (params) => {
  return new Promise((resolve, reject) => {
    window.$axios
      .get(`/proxy/api/org/organization/queryEnableOrgs`, {
        params
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            const data = res.data.data;
            resolve(data);
          } else {
            reject(res.data)
          }
        } else {
          reject(res)
        }
      })
  })
}

// 查询所有的组织列表 
export const fetchQueryOrgs = (params) => {
  return new Promise((resolve, reject) => {
    window.$axios
      .get(`/proxy/api/org/organization/queryOrgs`, {
        params
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            const data = res.data.data;
            resolve(data);
          } else {
            reject(res.data)
          }
        } else {
          reject(res)
        }
      })
  })
}

// 通过组织ID获取组织版本ID
export const fetchOrgVersionIdByOrgId = (orgId) => {
  return new Promise((resolve, reject) => {
    window.$axios
      .get('/proxy/api/org/organization/version/getOrgVersionIdByOrgId', {
        params: { orgId }
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            const data = res.data.data;
            resolve(data);
          } else {
            reject(res.data)
          }
        } else {
          reject(res)
        }
      })
  });
}

// 查询职等列表
export const fetchJobGradeList = () => {
  return new Promise((resolve, reject) => {
    window.$axios
      .get('/proxy/api/org/duty/hierarchy/jobGradeList')
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            const data = res.data.data;
            resolve(data);
          } else {
            reject(res.data)
          }
        } else {
          reject(res)
        }
      })
  });
}

// 请求职级树
export const fetchJobRankTree = () => {
  return new Promise((resolve, reject) => {
    const params = {
      args: JSON.stringify(['']),
      methodName: 'queryJobRankSelect',
      serviceName: 'orgDutySeqService'
    };
    window.$axios
      .post('/json/data/services', {
        ...params
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            const data = res.data.data;
            resolve(data);
          } else {
            reject(res.data)
          }
        } else {
          reject(res)
        }
      });
  });
}

// 请求业务组织角色，跟业务组织id
export const fetchBizOrgRolesByBizOrgId = (params) => {
  return new Promise((resolve, reject) => {
    window.$axios
      .get('/proxy/api/org/biz/getBizOrgRolesByBizOrgId', {
        params
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            const data = res.data.data;
            resolve(data);
          } else {
            reject(res.data)
          }
        } else {
          reject(res)
        }
      })
  });
}

// 根据流程定义UUID判断是否存在未办结的流程实例
export const isExistsUnfinishedFlowInstanceByFlowDefUuid = (params) => {
  return new Promise((resolve, reject) => {
    window.$axios
      .get('/proxy/api/workflow/definition/isExistsUnfinishedFlowInstanceByFlowDefUuid', {
        params
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            const data = res.data.data;
            resolve(data);
          } else {
            reject(res.data)
          }
        } else {
          reject(res)
        }
      })
  });
}

// 同步方式获取流程树
export const fetchAllFlowAsCategoryTree = () => {
  return new Promise((resolve, reject) => {
    const params = {
      methodName: 'getAllFlowAsCategoryTree',
      serviceName: 'flowSchemeService'
    };
    window.$axios({
      method: 'post',
      url: '/json/data/services',
      data: params,
      // headers: 
    }).then(res => {
      if (res.status === 200) {
        if (res.data && res.data.code === 0) {
          const data = res.data.data;
          resolve(data);
        } else {
          reject(res.data)
        }
      } else {
        reject(res)
      }
    });
  });
}

// 请求表单配置
export const fetchFormDefinitionByUuidJustDataAndDef = (data) => {
  return new Promise((resolve, reject) => {
    $axios
      .post(`/proxy/api/dyform/definition/getFormDefinition?formUuid=${data.uuid}&justDataAndDef=false`)
      .then(res => {
        if (res.status === 200) {
          resolve(res.data);
        } else {
          reject(res)
        }
      });
  })
}

// 请求国际化选项
export const fetchLocaleOptions = () => {
  return new Promise((resolve, reject) => {
    window.$tempStorage.getCache(
      'allLocaleOptions',
      () => {
        return new Promise((resolve, reject) => {
          window.$axios
            .get(`/proxy/api/app/codeI18n/getAllLocales`, { params: {} })
            .then(({ data }) => {
              let options = [];
              if (data.code == 0) {
                for (let d of data.data) {
                  options.push({
                    label: d.name,
                    value: d.locale,
                    description: d.remark || d.name,
                    transCode: d.translateCode
                  });
                }
              }
              resolve(options);
            })
            .catch(error => { });
        });
      },
      results => {
        resolve(results);
      }
    );
  });
}
// 请求用户详情
export const fetchUserById = (params) => {
  return new Promise((resolve, reject) => {
    window.$axios
      .get('/proxy/api/org/user/getUserById', {
        params
      })
      .then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            const data = res.data.data;
            resolve(data);
          } else {
            reject(res.data)
          }
        } else {
          reject(res)
        }
      })
  });
}