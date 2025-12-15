import { customAlphabet } from 'nanoid';
const nanoid = customAlphabet('ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz', 32);
import Clipboard from 'clipboard';
import qs from 'qs';
import jmespath from 'jmespath';
import Genid from './genid';
import localforage from 'localforage';
import md5 from './md5';
import jsonata from 'jsonata';
import lodash from 'lodash';
import { JsFormulaEngine } from '../formula/JsFormulaEngine';
import CryptoJS from 'crypto-js';
import moment from 'moment';

let genid = null;

export const addWindowResizeHandler = function (handler) {
  let oldHandler = window.onresize;
  if (typeof window.onresize != 'function') {
    window.onresize = handler;
  } else {
    window.onresize = function () {
      oldHandler();
      handler();
    };
  }
};

export const addElementResizeDetector = function (element, callback) {
  import('element-resize-detector').then(m => {
    let elementResizeDetectorMaker = m.default;
    //监听元素变化
    let erd = elementResizeDetectorMaker();
    erd.listenTo(element, function (_element) {
      callback();
    });
  });
};

export const generateId = function (arg) {
  if (arg == 'SF') {
    return snowfalkeId();
  }
  return nanoid(arg);
};

export const snowfalkeId = function () {
  if (genid == null) {
    let snfid = getCookie('snfid');
    if (snfid) {
      snfid = JSON.parse(snfid);
    }
    genid = new Genid({ ...{ Method: 2, WorkerId: 19, WorkerIdBitLength: 15 }, ...snfid });
  }
  let id = genid.NextId();
  return typeof id === 'bigint' ? id.toString() : id + '';
};

export const deepClone = function (origin) {
  if (origin === undefined) {
    return undefined;
  }

  return JSON.parse(JSON.stringify(origin));
};

export function executeJSFormula(formula, data, extFunctions) {
  return new JsFormulaEngine(extFunctions).execute(formula, data);
}

export const executeCodeSegment = function (script, params, $this, callback) {
  var paramNames = []; //参数键
  var paramValues = []; //参数值
  var _this = $this ? $this : this;
  if (params) {
    for (var k in params) {
      paramNames.push(k);
      paramValues.push(params[k]);
    }
  }
  var anonymousFunction = new Function(paramNames.join(','), script);
  var rt = anonymousFunction.apply(_this, paramValues);
  if (typeof callback === 'function') {
    //处理执行结果
    callback(rt);
  }
  return anonymousFunction;
};

export function getCookie(name) {
  if (typeof document != 'undefined') {
    var arr = document.cookie.match(new RegExp('(^| )' + name + '=([^;]*)(;|$)'));
    if (arr) {
      return decodeURIComponent(arr[2]);
    }
  }
  return null;
}
export function copyToClipboard(content, clickEvent, callback) {
  const clipboard = new Clipboard(clickEvent.target, {
    text: () => content
  });

  clipboard.on('success', () => {
    if (typeof callback === 'function') {
      callback(true);
    }
    clipboard.destroy();
  });

  clipboard.on('error', () => {
    if (typeof callback === 'function') {
      callback(false);
    }
    clipboard.destroy();
  });

  clipboard.onClick(clickEvent);
}

// 交换数组内元素的顺序
export function swapArrayElements(swaps, sources, equal, direction, swapCallback) {
  let _func = function (value) {
    for (let i = 0, len = sources.length; i < len; i++) {
      if (typeof equal === 'function') {
        if (equal(value, sources[i])) {
          // 找到目标元素
          let j = direction == 'forward' ? (i == 0 ? sources.length : i - 1) : i == sources.length - 1 ? 0 : i + 1;
          let temp = sources.splice(i, 1)[0];
          sources.splice(j, 0, temp);
          if (typeof swapCallback === 'function') {
            swapCallback(i, j);
          }
          break;
        }
      }
    }
  };
  for (let i = 0, len = swaps.length; i < len; i++) {
    _func(swaps[i]);
  }
}

export function queryString(string, options = {}) {
  return qs.parse(string, {
    allowDots: true,
    ...options,
    decoder(value) {
      if (/^(\d+|\d*\.\d+)$/.test(value) && value.length <= 16) {
        // 转数字
        return Number(value);
      }
      let keywords = {
        true: true,
        false: false
      };
      if (value in keywords) {
        // 转boolean
        return keywords[value];
      }
      return value;
    }
  });
}

export function queryStringify(obj, options = {}) {
  return qs.stringify(obj, options);
}

// https://www.npmjs.com/package/jmespath
export function jsonValue(obj, expression) {
  return jmespath.search(obj, expression);
}

export function getJsonValue(obj, expression) {
  let parts = expression.split('.'),
    nestedObj = obj;
  for (let i = 0, len = parts.length; i < len; i++) {
    let p = parts[i];
    nestedObj = nestedObj[p];
    if (i == len - 1 || nestedObj == undefined) {
      return nestedObj;
    }
  }
}

// 根据字段路径计算出数据中的值进行比较
export function expressionCompare(obj, keypath, operator, value, async) {
  // 空数据和参数不做判断
  if (!(obj && keypath)) {
    return false;
  }
  obj.TERMINALTYPE = { MOBILE: false }; //非移动端
  let _switchCompare = (val, operator) => {
    let ans = undefined;
    switch (operator) {
      case '==':
        ans = val == value;
        break;
      case '!=':
        ans = val != value;
        break;
      // case 'is null':
      //   ans = val == undefined || val == null || val == '';
      //   break;
      // case 'is not null':
      //   ans = val !== undefined || val !== null || val !== '';
      //   break;
      case 'true':
        ans = val === '1' || val === 1 || val === true || (val !== 0 && val !== false && val != undefined && val != null && val !== ''); // 默认按变量的真假判断是否显示
        break;
      case 'false':
        ans = val === '0' || val === '' || val === null || val === undefined || val === 0 || val === false; // 默认按变量的真假判断是否显示
        break;
      case '>=':
        ans = val != undefined && value != undefined ? Number(val) >= Number(value) : false;
        break;
      case '<=':
        ans = val != undefined && value != undefined ? Number(val) <= Number(value) : false;
        break;
      case '>':
        ans = val != undefined && value != undefined ? Number(val) > Number(value) : false;
        break;
      case '<':
        ans = val != undefined && value != undefined ? Number(val) < Number(value) : false;
        break;
      case 'in':
        ans = value != undefined ? (Array.isArray(value) ? (value + '').includes(val) : (value + '').indexOf(val) != -1) : false;
        break;
      case 'not in':
        ans = value != undefined ? (Array.isArray(value) ? !(value + '').includes(val) : (value + '').indexOf(val) == -1) : false;
        break;
      case 'contain':
        ans = Array.isArray(val) ? val.includes(value) : value != undefined && val != null ? (val + '').indexOf(value) != -1 : false;
        break;
      case 'not contain':
        ans = Array.isArray(val) ? !val.includes(value) : value != undefined && val != null ? (val + '').indexOf(value) == -1 : true;
        break;
      default:
        ans = false;
    }
    return ans;
  };
  if (async) {
    return new Promise((resolve, reject) => {
      jsonataEvaluate(keypath, obj, {})
        .then(val => {
          resolve(_switchCompare(val, operator));
        })
        .catch(() => {
          reject();
        });
    });
  }
  return _switchCompare(jsonValue(obj, keypath), operator);
}

export function compareArrayDifference(from, to, comparator, outputFormat) {
  let result = { from: [], to: [] };
  to.forEach(item => {
    if (typeof comparator == 'function') {
      if (!comparator(from, item)) {
        result.to.push(typeof outputFormat == 'function' ? outputFormat(item) : item);
      }
    } else {
      if (from.indexOf(item) == -1) {
        result.to.push(typeof outputFormat == 'function' ? outputFormat(item) : item);
      }
    }
  });
  from.forEach(item => {
    if (typeof comparator == 'function') {
      if (!comparator(to, item)) {
        result.from.push(typeof outputFormat == 'function' ? outputFormat(item) : item);
      }
    } else {
      if (to.indexOf(item) == -1) {
        result.from.push(typeof outputFormat == 'function' ? outputFormat(item) : item);
      }
    }
  });
  return result;
}

// form提交iframe下载请求
export function download({ method = 'post', url = '/proxy/file/download/services', data = {}, success, error }) {
  let DownloadIFrame = Vue.extend({
    template: `<iframe ref="iframe" style="display: none" name="download_iframe" @load="iframeLoad">
        <form ref="downloadForm" style="display: none" :method="method" target="download_iframe" :action="url">
          <input v-for="(value, key, index) in data" :key="key" type="text" :name="key" v-model="data[key]" />
          <input type="text" name="jwt" v-model="jwt" />
          <input type="text" name="_csrf" v-model="csrf" />
          <input type="text" name="system_id" v-model="system" />
        </form>
      </iframe>`,
    data() {
      let jwt = getCookie(getCookie('_auth'));
      let csrf = window.__INITIAL_STATE__.csrf;
      let system = this._$SYSTEM_ID;
      return {
        method,
        url,
        data,
        jwt,
        csrf,
        system
      };
    },
    mounted() {
      this.$refs.downloadForm.submit();
      if (success) {
        success();
      }
    },
    methods: {
      iframeLoad(e) {
        if (error && (!this.$refs.iframe.contentDocument || !this.$refs.iframe.contentDocument.title)) {
          error();
        }
      }
    }
  });
  let downloadDiv = document.createElement('div');
  downloadDiv.classList.add('download-container');
  document.body.appendChild(downloadDiv);
  let downloadIFrame = new DownloadIFrame();
  downloadIFrame.$mount(downloadDiv);
}

export function fileUpload(options, callback) {
  let file = options.file,
    fileSize = file.size,
    fileName = file.name,
    formData = new FormData();
  formData.set('frontUUID', file.uid);
  formData.set('localFileSourceIcon', '');
  formData.set('size', fileSize);
  let headers = {
    'Content-Disposition': `attachment; filename="${encodeURIComponent(fileName)}"`,
    'Content-Type': 'multipart/form-data'
  };
  formData.set('file', file);
  $axios
    .post('/proxy-repository/repository/file/mongo/savefilesChunk', formData, {
      headers: headers
    })
    .then(({ data }) => {
      if (data.code == 0 && data.data) {
        options.onSuccess();
        if (typeof callback == 'function') {
          let obj = { ...data.data[0] };
          obj.url = `/proxy-repository/repository/file/mongo/download?fileID=${data.data[0].fileID}`;
          console.log('文件上传返回信息: ', obj);
          callback(data.data[0]);
        }
      }
    });
}

export function dropLocalforage(storeName, name = 'WELL_TEMP_STORAGE') {
  return localforage.dropInstance({
    name,
    storeName
  });
}

export function createLocalforage(storeName, description, name = 'WELL_TEMP_STORAGE', size = 10485760) {
  console.log('创建缓存数据库', name, storeName, description);
  const tempStorage = localforage.createInstance({
    name,
    storeName: storeName,
    description,
    driver: [localforage.INDEXEDDB, localforage.LOCALSTORAGE],
    size
  });
  //！ 强制初始化避免浏览器多页签打开卡住现象
  tempStorage.getItem('init', function () {});

  tempStorage._queue = {};
  tempStorage._queueCallback = {};

  // 移除对应 key 的缓存
  tempStorage.removeCache = function (_key) {
    let _this = this;
    // console.log(`缓存数据库 ${this._dbInfo.storeName} -> 删除缓存 key = [ ${_key} ]`);
    if (_key == undefined) {
      // 移除所有缓存
      return new Promise((resolve, reject) => {
        _this.clear().then(() => {
          resolve();
        });
      });
    }
    let key = this.keyWrapper(_key);
    return new Promise((resolve, reject) => {
      delete _this._queue[key];
      _this.removeItem(key).then(() => {
        resolve();
      });
    });
  };

  // 缓存 key 包裹
  tempStorage.keyWrapper = function (key) {
    let userId = window.$app && window.$app._$USER ? window.$app._$USER.userId : '';
    let systemId = window.$app && window.$app._$SYSTEM_ID ? window.$app._$SYSTEM_ID : '';
    if (!(typeof key == 'string')) {
      key = md5(JSON.stringify(key));
    }
    return (systemId ? `${systemId}:` : '') + (userId ? `${userId}:` : '') + key;
  };

  // 获取缓存，如果缓存存在则返回，否则排队等待缓存请求设置
  tempStorage.getCache = function (_key, fetchPromise, callback) {
    let _this = this;
    let key = _this.keyWrapper(_key);
    this.getItem(key, (error, result) => {
      console.log(`缓存数据库 ${_this._dbInfo.storeName} -> 缓存数据获取 key = [ ${key} ]`, result);
      if (result == null) {
        if (_this._queue[key] == undefined) {
          _this._queue[key] = true;
          _this._queueCallback[key] = [];
          let result = fetchPromise;
          if (typeof fetchPromise === 'function') {
            result = fetchPromise();
          }
          if (result != undefined) {
            let _setItem = (key, o) => {
              _this.setItem(key, o, () => {
                console.log(`设置缓存数据 key = [ ${key} ]`, o);
                callback(o);
                let invokeQueueCallback = () => {
                  let cb = _this._queueCallback[key].shift();
                  if (cb != undefined) {
                    console.log(`排队获取缓存数据 key = [ ${key} ]`, o);
                    cb(o);
                    if (_this._queueCallback[key].length > 0) {
                      setTimeout(() => {
                        invokeQueueCallback();
                      }, 0);
                    } else {
                      delete _this._queue[key];
                    }
                  } else {
                    delete _this._queue[key];
                  }
                };
                invokeQueueCallback();
              });
            };
            if (result instanceof Promise) {
              result.then(o => {
                _setItem(key, o);
              });
            } else {
              _setItem(key, result);
            }
          }
        } else {
          // 放到队列里面等待回调
          _this._queueCallback[key].push(r => {
            if (typeof callback == 'function') {
              callback(r);
            }
          });
        }
      } else {
        if (typeof callback == 'function') {
          callback(result);
        }
      }
    });
  };

  return tempStorage;
}

export const getBottomStylesSum = (style = {}, element) => {
  return (parseFloat(style.marginBottom) || 0) + (parseFloat(style.paddingBottom) || 0) + (parseFloat(style.borderBottomWidth) || 0);
};

export const getTopStylesSum = (style = {}) => {
  return (parseFloat(style.marginTop) || 0) + (parseFloat(style.paddingTop) || 0) + (parseFloat(style.borderTopWidth) || 0);
};

const getPreviousSiblingTotalHeight = el => {
  let total = 0;
  if (!el) {
    return total;
  }
  const elRect = el.getBoundingClientRect();
  let sibling = el.previousElementSibling;

  while (sibling && sibling.className.indexOf('ps__rail-y') < 0) {
    const siblingRect = sibling.getBoundingClientRect();
    if (siblingRect.top < elRect.top) {
      // 上一个兄弟节点在上方
      const siblingStyle = window.getComputedStyle(sibling);
      const marginTop = parseFloat(siblingStyle.marginTop) || 0;
      const marginBottom = parseFloat(siblingStyle.marginBottom) || 0;
      total += sibling.offsetHeight + marginTop + marginBottom;
    }
    sibling = sibling.previousElementSibling;
  }

  return total;
};
const getNextSiblingsTotalHeight = el => {
  let total = 0;
  if (!el) {
    return total;
  }
  const elRect = el.getBoundingClientRect();
  let sibling = el.nextElementSibling;

  while (sibling && sibling.className.indexOf('ps__rail-y') < 0) {
    const siblingRect = sibling.getBoundingClientRect();
    if (siblingRect.top >= elRect.bottom) {
      // 下一个兄弟节点在下方
      const siblingStyle = window.getComputedStyle(sibling);
      const marginTop = parseFloat(siblingStyle.marginTop) || 0;
      const marginBottom = parseFloat(siblingStyle.marginBottom) || 0;
      total += sibling.offsetHeight + marginTop + marginBottom;
    }
    sibling = sibling.nextElementSibling;
  }

  return total;
};

export const getElSpacingForTarget = (el, target) => {
  const getStyleTotal = (el, target) => {
    let parentTop = 0,
      parentBottom = 0,
      previousSibling = getPreviousSiblingTotalHeight(el),
      nextSibling = getNextSiblingsTotalHeight(el);
    let current = el.parentElement;
    while ((current && current !== target) || (current === target && current.children.length !== 1)) {
      const currentStyle = window.getComputedStyle(current);
      parentTop += getTopStylesSum(currentStyle);
      parentBottom += getBottomStylesSum(currentStyle);
      previousSibling += getPreviousSiblingTotalHeight(current);
      nextSibling += getNextSiblingsTotalHeight(current);
      if (current === target) {
        current = null;
      } else {
        current = current.parentElement;
      }
    }

    return {
      parentTop,
      parentBottom,
      previousSibling,
      nextSibling
    };
  };

  if (!el || !target) return;

  const { parentTop, parentBottom, previousSibling, nextSibling } = getStyleTotal(el, target);

  // 最终计算
  const maxHeight = target.offsetHeight - parentTop - parentBottom - previousSibling - nextSibling;

  return {
    maxHeight,
    parentTop,
    parentBottom,
    previousSibling,
    nextSibling
  };
};

export const getElMaxHeightFromViewport = el => {
  const getBottomSpacingAndSiblings = (el, stopId = 'app') => {
    let totalBottom = 0,
      totalNextSibling = 0;
    let current = el.parentElement;
    while (current && current.id !== stopId) {
      const currentStyle = window.getComputedStyle(current);
      totalBottom += getBottomStylesSum(currentStyle);
      totalNextSibling += getNextSiblingsTotalHeight(current, currentStyle);
      current = current.parentElement;
    }

    return {
      totalBottom,
      totalNextSibling
    };
  };

  if (!el) return;

  const rect = el.getBoundingClientRect();
  const viewportHeight = window.innerHeight;

  const { totalBottom, totalNextSibling } = getBottomSpacingAndSiblings(el);

  // 最终计算
  const maxHeight = viewportHeight - rect.top - totalBottom - totalNextSibling - 1;
  // el.style.cssText += `;overflow-y:auto; height:${maxHeight}px`;
  return {
    viewportHeight,
    maxHeight,
    totalBottom,
    totalNextSibling
  };
};
// 查找上级组件-通过组件名
export const findParentVNodeByName = (vNode, name) => {
  let parent = vNode.$parent;
  let parentName = parent.$options.name || parent.$options._componentTag;
  while (parentName !== name) {
    parent = parent.$parent;
    if (!parent) return false;
    parentName = parent.$options.name || parent.$options._componentTag;
  }
  return parent;
};
export const getDocumentByNode = node => {
  if (!node) {
    return {};
  }
  return node.ownerDocument;
};
function ellipsizeText(ctx, text, maxWidth) {
  const ellipsis = '…';
  const ellipsisWidth = ctx.measureText(ellipsis).width;

  if (ctx.measureText(text).width <= maxWidth) return text;

  let truncated = '';
  for (let i = 0; i < text.length; i++) {
    const next = truncated + text[i];
    const nextWidth = ctx.measureText(next).width;
    if (nextWidth + ellipsisWidth > maxWidth) break;
    truncated = next;
  }

  return truncated + ellipsis;
}
// 图片水印
export const addWatermarksToImageFile = ({ imageFile, watermarks = [] }) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = function (e) {
      const img = new Image();
      img.onload = function () {
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        canvas.width = img.width;
        canvas.height = img.height;

        ctx.drawImage(img, 0, 0);
        const gap = 2; // 控制多个水印在垂直方向上的间距，避免重叠
        watermarks.forEach((wm, index) => {
          let { text = '水印', fontSize = '32px', fontColor = '#000000', opacity = 0.5, textAlign = 'center', baseline = 'middle' } = wm;

          const fontSizeValue = parseInt(fontSize);
          ctx.save();
          ctx.font = `${fontSize} sans-serif`;
          ctx.fillStyle = fontColor;
          ctx.globalAlpha = opacity; // 设置透明度
          ctx.textAlign = textAlign;
          ctx.textBaseline = 'alphabetic';

          // 水平位置
          let x;
          if (textAlign === 'left') x = 0;
          else if (textAlign === 'right') x = canvas.width;
          else x = canvas.width / 2;

          // 垂直分布
          let baseY;
          if (baseline === 'top') {
            baseY = fontSizeValue + index * (fontSizeValue + gap);
          } else if (baseline === 'bottom') {
            baseY = canvas.height - index * (fontSizeValue + gap);
          } else {
            const totalHeight = watermarks.length * (fontSizeValue + gap);
            baseY = (canvas.height - totalHeight) / 2 + index * (fontSizeValue + gap) + fontSizeValue;
          }

          const displayText = ellipsizeText(ctx, text, canvas.width);
          ctx.fillText(displayText, x, baseY);
          ctx.restore();
        });

        canvas.toBlob(blob => {
          if (!blob) return reject(new Error('Canvas toBlob failed'));
          resolve(new File([blob], imageFile.name, { type: blob.type }));
        }, imageFile.type);
      };
      img.onerror = () => reject(new Error('图片加载失败'));
      img.src = e.target.result;
    };
    reader.onerror = () => reject(new Error('文件读取失败'));
    reader.readAsDataURL(imageFile);
  });
};
// 图片压缩
export const compressImage = (file, quality = 0.7) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();

    reader.onload = event => {
      const img = new Image();
      img.onload = () => {
        const canvas = document.createElement('canvas');
        canvas.width = img.width;
        canvas.height = img.height;

        const ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0, img.width, img.height);

        canvas.toBlob(
          blob => {
            // resolve(blob);
            resolve(new File([blob], file.name, { type: blob.type }));
          },
          file.type,
          quality // 0 ~ 1，越小图片越小
        );
      };
      img.onerror = reject;
      img.src = event.target.result;
    };

    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
};

const jsonataCustomFunctions = {};

export const jsonataEvaluate = (expr, data, bindings, callback, valueFormatterOption) => {
  try {
    const expression = jsonata(expr);
    Object.entries(jsonataCustomFunctions).forEach(([name, fn]) => {
      expression.registerFunction(name, fn);
    });
    let valueFormat = result => {
      if (valueFormatterOption != undefined && result != undefined) {
        try {
          if (valueFormatterOption.formatter != undefined) {
            if (valueFormatterOption.formatter.use != undefined && valueFormatter()[valueFormatterOption.formatter.use] != undefined) {
              result = valueFormatter()[valueFormatterOption.formatter.use](result, valueFormatterOption.formatter.options);
            } else if (valueFormatterOption.formatter.function != undefined) {
              result = new Function('value,lodash', valueFormatterOption.formatter.function)(result, lodash);
            }
          }
        } catch (error) {
          console.error('值格式化异常', error);
        }
      }
      return result;
    };
    if (typeof callback == 'function') {
      expression.evaluate(data, bindings || {}, function (error, result) {
        if (error) {
          console.error('jsonata 表达式解析异常', error);
        }
        callback(valueFormat(result));
      });
    } else {
      return new Promise((resolve, reject) => {
        expression.evaluate(data, bindings || {}).then(result => {
          resolve(valueFormat(result));
        });
      });
    }
  } catch (error) {
    console.error('jsonata 表达式解析异常', error);
  }
};

function valueFormatter() {
  return {
    split: function (value, options) {
      return lodash.split(value, options.separator);
    },
    join: function (value, options) {
      return lodash.join(value, options.separator);
    },
    toUpper: function (value) {
      return lodash.toUpper(value);
    },
    toLower: function (value) {
      return lodash.toLower(value);
    },
    capitalize: function (value) {
      return lodash.capitalize(value);
    },
    camelCase: function (value) {
      return lodash.camelCase(value);
    },
    reverseArray: function (value) {
      if (value != undefined) {
        return value.reverse();
      }
      return value;
    },
    reverse: function (value) {
      if (value != undefined) {
        if (typeof value == 'string') {
          return value.split('').reverse().join('');
        }
      }
      return value;
    }
  };
}

async function resolveValue(valueOption, context) {
  if (!valueOption) return null;
  if (valueOption.valueType === 'variable' || valueOption.valueType === 'jsonpath') {
    try {
      const result = await jsonataEvaluate(valueOption.value, context);
      try {
        if (valueOption.formatter != undefined) {
          if (valueOption.formatter.use != undefined && valueFormatter()[valueOption.formatter.use] != undefined) {
            return valueFormatter()[valueOption.formatter.use](result, valueOption.formatter.options);
          } else if (valueOption.formatter.function != undefined) {
            return new Function('value,lodash', valueOption.formatter.function)(result, lodash);
          }
        }
      } catch (error) {
        console.error('值格式化异常', error);
      }
      return result === undefined ? null : result;
    } catch (e) {
      console.error(`JSONata 解析错误: ${e.message}`);
      return null;
    }
  } else if (valueOption.valueType === 'constant') {
    return valueOption.value;
  }
  return null;
}

async function processProperty(schema, context) {
  if (!schema) return null;

  if (schema.type === 'object') {
    return await processObject(schema, context);
  } else if (schema.type === 'array') {
    return await processArray(schema, context);
  } else {
    if (schema.valueOption) {
      return await resolveValue(schema.valueOption, context);
    }
    return null;
  }
}

async function processObject(schema, context) {
  const result = {};
  if (!schema.properties) {
    if (schema.valueOption) {
      return schema.valueOption ? await resolveValue(schema.valueOption, context) : {};
    }
    return result;
  }

  for (const [key, propertySchema] of Object.entries(schema.properties)) {
    result[key] = await processProperty(propertySchema, context);
  }
  return result;
}

async function processArray(schema, context) {
  const arrayData = schema.valueOption ? await resolveValue(schema.valueOption, context) : [];
  if (!schema.items) return arrayData;
  if (arrayData == undefined || arrayData == '') {
    return [];
  }
  if (!Array.isArray(arrayData)) return [arrayData];

  const results = [];
  for (const item of arrayData) {
    const newContext = {
      ...context,
      _$loopItem: item // 当前遍历的数组元素
    };
    if (schema.items.properties == undefined || Object.keys(schema.items.properties).length == 0) {
      results.push(item);
      continue;
    }
    results.push(await processProperty(schema.items, newContext));
  }
  return results;
}

export async function evaluateConvertJsonDataFromSchema(schema, inputData) {
  if (schema.type === 'object') {
    return await processObject(schema, inputData);
  }
  return await processProperty(schema, inputData);
}

/**
 * js 代码处理转换，校验语法是否正确、删除注释等
 * @param {*} code
 * @returns
 */
export function javascriptParser(script, options = {}) {
  return new Promise((resolve, reject) => {
    import('acorn').then(acorn => {
      try {
        const ast = acorn.parse(script, {
          ecmaVersion: 2020,
          sourceType: 'script',
          ...options
        });
        import('astring')
          .then(astring => {
            resolve(astring.generate(ast));
          })
          .catch(error => {
            console.error('astring 错误', error);
            reject();
          });
      } catch (e) {
        console.error('js 语法错误', e);
        reject();
      }
    });
  });
}

export function xorEncrypt(text, key = 'wellsoft') {
  return text
    .split('')
    .map((char, i) => String.fromCharCode(char.charCodeAt(0) ^ key.charCodeAt(i % key.length)))
    .join('');
}

export function encrypt(text, secretKey = 'wellsoft') {
  return CryptoJS.AES.encrypt(text, secretKey).toString();
}

export function decrypt(text, secretKey = 'wellsoft') {
  return CryptoJS.AES.decrypt(text, secretKey).toString(CryptoJS.enc.Utf8);
}

// 时间格式化
Date.prototype.format = function (fmt) {
  var o = {
    'M+': this.getMonth() + 1, //月份
    'DD+': this.getDate(), //日
    'HH+': this.getHours(), //小时
    'm+': this.getMinutes(), //分
    's+': this.getSeconds(), //秒
    'q+': Math.floor((this.getMonth() + 3) / 3), //季度
    S: this.getMilliseconds() //毫秒
  };

  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
  }

  for (var k in o) {
    if (new RegExp('(' + k + ')').test(fmt)) {
      fmt = fmt.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length));
    }
  }

  return fmt;
};

export const isValidJSON = text => {
  try {
    if (!text) {
      return false;
    }
    if (typeof JSON.parse(text) === 'object') {
      return true;
    }
    return false;
  } catch (e) {
    return false;
  }
};

export const asyncLoadLocaleData = locale => {
  let promise = [
    new Promise((resolve, reject) => {
      import(/* webpackChunkName: "ant-locale-[request]" */ `ant-design-vue/lib/locale-provider/${locale}.js`)
        .then(module => {
          resolve(module.default);
        })
        .catch(error => {
          resolve(undefined);
          console.error(`Failed to load antd locale: ${locale}`, error);
        });
    }),

    new Promise((resolve, reject) => {
      import(/* webpackChunkName: "locale-[request]" */ `@locale/${locale}.json`)
        .then(module => {
          resolve(module.default);
        })
        .catch(error => {
          resolve(undefined);
          console.error(`Failed to load antd locale: ${locale}`, error);
        });
    }),

    new Promise((resolve, reject) => {
      let l = locale.replace('_', '-').toLowerCase();
      import(/* webpackChunkName: "moment-locale-[request]" */ `moment/locale/${l}.js`)
        .then(() => {
          moment.locale(l);
          resolve();
        })
        .catch(() => {
          import(/* webpackChunkName: "moment-locale-[request]" */ `moment/locale/${l.split('-')[0]}.js`)
            .then(() => {
              moment.locale(l.split('-')[0]);
              resolve();
            })
            .catch(() => {
              import(/* webpackChunkName: "moment-locale-[request]" */ `moment/locale/zh-cn.js`).then(() => {
                moment.locale('zh-cn');
                resolve();
              });
            });
        });
    })
  ];
  return Promise.all(promise);
};
