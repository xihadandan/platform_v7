/**
 * 徽章：dataStoreCount
 * 通过数据仓库获取数量
 */
function dataStoreCount(dataStoreId, callback, params) {
  uni.request({
    url: "/api/datastore/loadCount",
    data: { dataStoreId },
    method: "POST",
    async: false,
    success: (res) => {
      let data = res.data;
      if (data.code == "0") {
        callback(params, data.data);
      }
    },
    fail: () => {},
    complete: () => {},
  });
}

function dataModelCount(dataModelUuid, callback, params) {
  uni.request({
    url: "/api/dm/loadDataCount" + dataModelUuid,
    data: {},
    method: "POST",
    async: false,
    success: (res) => {
      let data = res.data;
      if (data.code == "0") {
        callback(params, data.data);
      }
    },
    fail: () => {},
    complete: () => {},
  });
}

export { dataStoreCount, dataModelCount };
