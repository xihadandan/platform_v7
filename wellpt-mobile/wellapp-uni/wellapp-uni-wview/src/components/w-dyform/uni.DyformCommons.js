// 表单公共方法
var exports = {};
const uuidv4 = require("uuid");
var _ = require("lodash");
// 1、创建UUID
exports.createUUID = function () {
  return new uuidv4();
};

// 7、获取真实值
exports.getRealValue = function (value) {
  if (value == null || (typeof value === "string" && !value.trim())) {
    return "";
  }
  var valueObj = value;
  if (_.isPlainObject(valueObj) == false) {
    valueObj = JSON.parse(valueObj); // $.parseJSON(valueObj);
  }
  var displayValue = [];
  for (var i in valueObj) {
    displayValue.push(i);
  }
  return displayValue.join(";");
};

// 8、获取显示值
exports.getDisplayValue = function (value) {
  if (value == null || (typeof value === "string" && !value.trim())) {
    return "";
  }
  var valueObj = value;
  if (_.isPlainObject(valueObj) == false) {
    valueObj = JSON.parse(valueObj); // $.parseJSON(valueObj);
  }
  var displayValue = [];
  for (var i in valueObj) {
    displayValue.push(valueObj[i]);
  }
  return displayValue.join(";");
};
exports.getDisplayValue2 = function (value, optionSet) {
  var displayValue = [];
  value = _.isArray(value) ? value : typeof value === "string" ? value.split(";") : [value];
  for (var i = 0; i < value.length; i++) {
    for (var j = 0; j < optionSet.length; j++) {
      if (optionSet[j].value === value[i]) {
        displayValue.push(optionSet[j].text);
      }
    }
  }
  return displayValue.join(";");
};

// 9、根据真实值、可选项数据，获取radio、checkbox、select的可选数据的ValueMap对象
exports.getValueMap = function (realValue, dataArray) {
  var valueMap = {};
  if (_.isEmpty(realValue)) {
    return valueMap;
  }
  var dataMap = {};
  if (dataArray != null) {
    _.each(dataArray, function (data) {
      dataMap[data.value] = data;
    });
  }

  var realValues = realValue.split(";");
  for (var i = 0; i < realValues.length; i++) {
    var value = realValues[i];
    if (dataMap[value] != null) {
      valueMap[value] = dataMap[value].text;
    } else if (value === "空") {
      // 正常
    } else {
      console.error("real value [" + realValue + "] is not found");
    }
  }
  return valueMap;
};

// 10、获取radio、checkbox、select的可选数据
// 批次ID
var batchId = 0;
exports.getOptionData = function (field, callback) {
  var fieldDefinition = field.definition;
  if (fieldDefinition.lazyLoading) {
    (function (field, callback) {
      lazySetDictOptions(field, callback);
    })(field, callback);
  } else {
    var options = fieldDefinition;
    var optionSet = getOptionSet(options);
    // 数组集合
    var hiddenValues = fieldDefinition.hiddenValues || {};
    var dataArray = convertOptionSet(optionSet, hiddenValues, fieldDefinition);
    callback.call(this, dataArray);
  }
};
function convertOptionSet(optionSet, hiddenValues, fieldDefinition) {
  var dataArray = [];
  // 批次ID
  var groupId = batchId;
  batchId++;
  if (_.isArray(optionSet)) {
    _.each(optionSet, function (option) {
      var item = {
        id: uuidv4(),
        groupId: groupId,
        value: option.value || option.code,
        text: option.name,
        name: fieldDefinition.name, // field.getName()
      };
      if (hiddenValues[item.value] === true) {
        return; //continue;
      }
      dataArray.push(item);
    });
  } else {
    // 对象集合
    for (var p in optionSet) {
      var item = {
        id: uuidv4(),
        groupId: groupId,
        value: p,
        text: optionSet[p],
        name: fieldDefinition.name, //field.getName()
      };
      if (hiddenValues[item.value] === true) {
        continue; //return;
      }
      dataArray.push(item);
    }
  }
  return dataArray;
}
var optionSetCache = {};
var optionSetCacheRequest = {};
function lazySetDictOptions(field, cb) {
  var fieldDefinition = field.definition;
  var fieldName = fieldDefinition.name;
  var formUuid = field.getFormScope().getFormUuid();
  // 是否需要等待备选项加载
  if (waitOptionSetIfRequired(field, cb)) {
    return;
  }
  uni.request({
    url: "/pt/dyform/definition/getFieldDictionaryOptionSet",
    dataType: "json",
    method: "POST",
    data: {
      fieldName: fieldName,
      formUuid: formUuid,
      paramsObj: JSON.stringify({ dictCode: "" }),
    },
    success: function (result) {
      var optionSet = convertOptionSet(result.data || {}, {}, fieldDefinition);
      var cacheKey = getOptionSetCacheKey(field);
      if (cacheKey != null) {
        optionSetCache[cacheKey] = copyOptionSet(optionSet);
      }
      cb(optionSet);
      // 回调等待加载的备选项回调函数
      callbackWaitOptionSet(cacheKey);
    },
  });
}
function waitOptionSetIfRequired(field, cb) {
  var cacheKey = getOptionSetCacheKey(field);
  if (cacheKey == null) {
    return false;
  }
  if (optionSetCache[cacheKey]) {
    cb(copyOptionSet(optionSetCache[cacheKey]));
    return true;
  } else if (optionSetCacheRequest[cacheKey]) {
    var reqCb = function (optionSet) {
      cb(optionSet);
    };
    optionSetCacheRequest[cacheKey].push(reqCb);
    return true;
  } else {
    optionSetCacheRequest[cacheKey] = [];
    return false;
  }
}
function callbackWaitOptionSet(cacheKey) {
  if (cacheKey == null) {
    return;
  }
  var reqCbArray = optionSetCacheRequest[cacheKey] || [];
  while (reqCbArray.length > 0) {
    reqCbArray.shift()(copyOptionSet(optionSetCache[cacheKey]));
  }
  delete optionSetCacheRequest[cacheKey];
  // 清空缓存;
  var tryClearCache = function () {
    setTimeout(function () {
      var reqCbArray = optionSetCacheRequest[cacheKey] || [];
      if (reqCbArray.length == 0) {
        delete optionSetCache[cacheKey];
      } else {
        tryClearCache();
      }
    }, 10 * 1000);
  };
  tryClearCache();
}
function getOptionSetCacheKey(field) {
  var fieldDefinition = field.definition;
  if (fieldDefinition.dataSourceFieldName == null || fieldDefinition.dataSourceFieldName == "") {
    return null;
  }
  var dataSourceRequest = {
    dataSourceId: fieldDefinition.dataSourceId,
    dataSourceFieldNam: fieldDefinition.dataSourceFieldName,
    dataSourceDisplayName: fieldDefinition.dataSourceDisplayName,
    dataSourceGroup: fieldDefinition.dataSourceGroup,
    defaultCondition: fieldDefinition.defaultCondition,
    dictCode: fieldDefinition.dictCode,
  };
  var cacheKey = JSON.stringify(dataSourceRequest);
  return cacheKey;
}
function copyOptionSet(optionSet) {
  return JSON.parse(JSON.stringify(optionSet));
}
function getOptionSet(options) {
  var optionSet = {};
  var selectobj = options.optionSet;
  if (selectobj == null || (typeof selectobj == "string" && !selectobj.trim())) {
    console.error("a json parameter is null , used to initialize checkbox options ");
    return;
  }
  if (options.optionDataSource == "2") {
    // 来自字典,这时optionSet为数组
    if (selectobj.length == 0) {
      return;
    } else {
      for (var j = 0; j < selectobj.length; j++) {
        var obj = selectobj[j];
        if (Object.prototype.hasOwnProperty.call(obj, "value")) {
          optionSet[selectobj[j].value] = selectobj[j].name;
        } else {
          optionSet[selectobj[j].code] = selectobj[j].name;
        }
      }
    }
  } else {
    optionSet = selectobj;
  }
  if (typeof optionSet === "string") {
    try {
      optionSet = JSON.parse(optionSet);
    } catch (e) {
      console.error(optionSet + " -->not json format ");
      return;
    }
  }
  return optionSet;
}

/**
 * 根据指定的dataUuid从指定的formUuid表单中获取表单数据
 * */
exports.loadFormDefinitionData = function (formUuid, dataUuid) {
  if (!formUuid) {
    //未初始化
    throw new Error("formUuid or dataUuid is not initialized");
  }
  var formDatas = null;
  let url = `/pt/dyform/data/getFormDefinitionData?formUuid=${formUuid}&dataUuid=${dataUuid}`;
  uni.request({
    url: url,
    cache: false,
    method: "POST",
    dataType: "json",
    success: function (result) {
      if (result.data.success == "true" || result.data.success == true) {
        formDatas = result.data.data;
      } else {
        uni.showToast({ title: "数据获取失败" });
      }
    },
    error: function (re) {
      //加载定义失败
      uni.showToast({ title: "数据获取失败" });
      console.error(re);
    },
  });
  return formDatas;
};
/**
 * 根据指定的的formUuid表单中获取表单定义
 * */
exports.loadFormDefinition = function (uuid) {
  if (!uuid) {
    //未初始化
    throw new Error("formUuid is not initialized");
  }
  var definitionObj = null;
  var time1 = new Date().getTime();
  uni.request({
    url: "/pt/dyform/definition/getFormDefinition",
    cache: false,
    async: false, // 同步完成
    method: "POST",
    data: {
      formUuid: uuid,
    },
    dataType: "json",
    success: function (data) {
      definitionObj = data;
    },
    error: function () {
      // 加载定义失败
    },
  });
  var time2 = new Date().getTime();
  console.log("加载定义所用时间:" + (time2 - time1) / 1000.0 + "s");
  return definitionObj;
};
module.exports = exports;
