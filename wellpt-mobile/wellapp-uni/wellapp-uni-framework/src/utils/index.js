import Genid from "./genid";
import jmespath from "jmespath";
import md5 from "./md5";
import jsonata from "jsonata";
import CryptoJS from "crypto-js";
import { JsFormulaEngine } from "./formula/JsFormulaEngine";
const genid = new Genid({ ...{ Method: 2, WorkerId: 15, WorkerIdBitLength: 15 } });
const jsonataCustomFunctions = {};
let Vuei18n = null;

export default {
  md5,
  generateId: function (arg) {
    let id = genid.NextId();
    return typeof id === "bigint" ? id.toString() : id + "";
  },

  deepClone: function (data) {
    return JSON.parse(JSON.stringify(data));
  },

  executeJSFormula(formula, data, extFunctions) {
    return new JsFormulaEngine(extFunctions).execute(formula, data);
  },
  jsonataEvaluate: function (expr, data, bindings, callback) {
    try {
      const expression = jsonata(expr);
      Object.entries(jsonataCustomFunctions).forEach(([name, fn]) => {
        expression.registerFunction(name, fn);
      });
      if (typeof callback == "function") {
        expression.evaluate(data, bindings || {}, function (error, result) {
          if (error) {
            console.error("jsonata 表达式解析异常", error);
          }
          callback(result);
        });
      } else {
        // 会返回 promise 结果
        return expression.evaluate(data, bindings || {});
      }
    } catch (error) {
      console.error("jsonata 表达式解析异常", error);
    }
  },
  jsonValue: function (obj, expression) {
    return jmespath.search(obj, expression);
  },
  expressionCompare: function (obj, keypath, operator, value, async) {
    // 空数据和参数不做判断
    if (!(obj && keypath)) {
      return false;
    }

    obj.TERMINALTYPE = { MOBILE: true }; //移动端
    let _switchCompare = (val, operator) => {
      let ans = undefined;
      switch (operator) {
        case "==":
          ans = val == value;
          break;
        case "!=":
          ans = val != value;
          break;
        // case 'is null':
        //   ans = val == undefined || val == null || val == '';
        //   break;
        // case 'is not null':
        //   ans = val !== undefined || val !== null || val !== '';
        //   break;
        case "true":
          ans =
            val === "1" ||
            val === 1 ||
            val === true ||
            (val !== 0 && val !== false && val != undefined && val != null && val !== ""); // 默认按变量的真假判断是否显示
          break;
        case "false":
          ans = val === "0" || val === "" || val === null || val === undefined || val === 0 || val === false; // 默认按变量的真假判断是否显示
          break;
        case ">=":
          ans = val != undefined && value != undefined ? Number(val) >= Number(value) : false;
          break;
        case "<=":
          ans = val != undefined && value != undefined ? Number(val) <= Number(value) : false;
          break;
        case ">":
          ans = val != undefined && value != undefined ? Number(val) > Number(value) : false;
          break;
        case "<":
          ans = val != undefined && value != undefined ? Number(val) < Number(value) : false;
          break;
        case "in":
          ans =
            value != undefined
              ? Array.isArray(value)
                ? (value + "").includes(val)
                : (value + "").indexOf(val) != -1
              : false;
          break;
        case "not in":
          ans =
            value != undefined
              ? Array.isArray(value)
                ? !(value + "").includes(val)
                : (value + "").indexOf(val) == -1
              : false;
          break;
        case "contain":
          ans = Array.isArray(val)
            ? val.includes(value)
            : value != undefined && val != null
            ? (val + "").indexOf(value) != -1
            : false;
          break;
        case "not contain":
          ans = Array.isArray(val)
            ? !val.includes(value)
            : value != undefined && val != null
            ? (val + "").indexOf(value) == -1
            : true;
          break;
        default:
          ans = false;
      }
      return ans;
    };
    if (async) {
      return new Promise((resolve, reject) => {
        this.jsonataEvaluate(keypath, obj, {})
          .then((val) => {
            resolve(_switchCompare(val, operator));
          })
          .catch(() => {
            reject();
          });
      });
    }
    return _switchCompare(this.jsonValue(obj, keypath), operator);
  },

  swapArrayElements: function (swaps, sources, equal, direction, swapCallback) {
    let _func = function (value) {
      for (let i = 0, len = sources.length; i < len; i++) {
        if (typeof equal === "function") {
          if (equal(value, sources[i])) {
            // 找到目标元素
            let j = direction == "forward" ? (i == 0 ? sources.length : i - 1) : i == sources.length - 1 ? 0 : i + 1;
            let temp = sources.splice(i, 1)[0];
            sources.splice(j, 0, temp);
            if (typeof swapCallback === "function") {
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
  },

  setI18n(i18n) {
    Vuei18n = i18n;
  },

  $t(key, value) {
    var params = {};
    if (value && value != undefined && typeof value !== "string") {
      params = value;
    }
    let result = Vuei18n.t.apply(Vuei18n, [key, params]);
    if (!result) {
      return value || "";
    }
    return result;
  },

  // 可用于获取主题颜色值，例getStringJsonValue(this.theme, '--w-primary-color')
  getStringJsonValue(str, key) {
    if (str && key) {
      const regex = new RegExp(`${key}:([^;]*)`);
      const match = str.match(regex);
      return match ? match[1] : null;
    }
    return null;
  },
  isValidJSON(text) {
    try {
      if (!text) {
        return false;
      }
      JSON.parse(text);
      return true;
    } catch (e) {
      return false;
    }
  },

  valueFormatter() {
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
          if (typeof value == "string") {
            return value.split("").reverse().join("");
          }
        }
        return value;
      },
    };
  },
  async resolveValue(valueOption, context) {
    if (!valueOption) return null;
    if (valueOption.valueType === "variable" || valueOption.valueType === "jsonpath") {
      try {
        const result = await jsonataEvaluate(valueOption.value, context);
        try {
          if (valueOption.formatter != undefined) {
            if (valueOption.formatter.use != undefined && valueFormatter()[valueOption.formatter.use] != undefined) {
              return valueFormatter()[valueOption.formatter.use](result, valueOption.formatter.options);
            } else if (valueOption.formatter.function != undefined) {
              return new Function("value,lodash", valueOption.formatter.function)(result, lodash);
            }
          }
        } catch (error) {
          console.error("值格式化异常", error);
        }
        return result === undefined ? null : result;
      } catch (e) {
        console.error(`JSONata 解析错误: ${e.message}`);
        return null;
      }
    } else if (valueOption.valueType === "constant") {
      return valueOption.value;
    }
    return null;
  },
  async processProperty(schema, context) {
    if (!schema) return null;

    if (schema.type === "object") {
      return await processObject(schema, context);
    } else if (schema.type === "array") {
      return await processArray(schema, context);
    } else {
      if (schema.valueOption) {
        return await resolveValue(schema.valueOption, context);
      }
      return null;
    }
  },
  async processObject(schema, context) {
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
  },

  async processArray(schema, context) {
    const arrayData = schema.valueOption ? await resolveValue(schema.valueOption, context) : [];
    if (!schema.items) return arrayData;
    if (arrayData == undefined || arrayData == "") {
      return [];
    }
    if (!Array.isArray(arrayData)) return [arrayData];

    const results = [];
    for (const item of arrayData) {
      const newContext = {
        ...context,
        _$loopItem: item, // 当前遍历的数组元素
      };
      if (schema.items.properties == undefined || Object.keys(schema.items.properties).length == 0) {
        results.push(item);
        continue;
      }
      results.push(await processProperty(schema.items, newContext));
    }
    return results;
  },
  async evaluateConvertJsonDataFromSchema(schema, inputData) {
    if (schema.type === "object") {
      return await processObject(schema, inputData);
    }
    return await processProperty(schema, inputData);
  },

  /**
   * js 代码处理转换，校验语法是否正确、删除注释等
   * @param {*} code
   * @returns
   */
  javascriptParser(script, options = {}) {
    return new Promise((resolve, reject) => {
      import("acorn").then((acorn) => {
        try {
          const ast = acorn.parse(script, {
            ecmaVersion: 2020,
            sourceType: "script",
            ...options,
          });
          import("astring")
            .then((astring) => {
              resolve(astring.generate(ast));
            })
            .catch((error) => {
              console.error("astring 错误", error);
              reject();
            });
        } catch (e) {
          console.error("js 语法错误", e);
          reject();
        }
      });
    });
  },

  xorEncrypt(text, key = "wellsoft") {
    return text
      .split("")
      .map((char, i) => String.fromCharCode(char.charCodeAt(0) ^ key.charCodeAt(i % key.length)))
      .join("");
  },
  encrypt(text, secretKey = "wellsoft") {
    return CryptoJS.AES.encrypt(text, secretKey).toString();
  },
  decrypt(text, secretKey = "wellsoft") {
    return CryptoJS.AES.decrypt(text, secretKey).toString(CryptoJS.enc.Utf8);
  },
};
