'use strict';
import moment from 'moment';
import lodash from 'lodash';

class JsFormulaEngine {
  constructor(extFunctions) {
    this.functionLibrary = new FormulaFunctionLibrary();
    if (extFunctions) {
      // 添加扩展函数
      extFunctions.forEach(item => {
        this.functionLibrary.register('extFunction', item.key, item.fn);
      });
    }
    this.functions = {};
    this.functionLibrary.getFunctions().forEach(item => {
      this.functions[item.key] = item.fn;
    });
  }

  async execute(formula, contextData) {
    if (formula == undefined || formula == '') {
      return;
    }
    const imports = Object.keys(this.functions).join(', ');
    const utils = ['moment', 'lodash'];
    const functionBody = `
      "use strict";

      const { ${imports} } =  ___FUNCTIONS___;
      const {${utils.join(', ')}}  = ___UTILS___;
      ${contextData != undefined ? `const {${Object.keys(contextData).join(', ')}} = ___CTX_DATA___ ; ` : ''}

      return (async () => {
        try {
          return ${formula};
        } catch (err) {
          throw new Error('Evaluation error: ' + err.message);
        }
      })();
    `;

    const fn = new Function('___FUNCTIONS___', '___CTX_DATA___', '___UTILS___', functionBody);
    const result = await fn(this.functions, contextData, {
      moment,
      lodash
    });
    return result;
  }
}

// 公式函数库
class FormulaFunctionLibrary {
  constructor() {
    this.functions = [];
    this.categories = {
      // math: { name: "数学函数", description: "基础数学运算" },
      string: { name: '字符串函数', description: '字符串操作函数' },
      datetime: { name: '日期时间函数', description: '日期和时间处理函数' },
      orgUser: { name: '组织用户函数', description: '组织用户处理函数' },
      workflow: { name: '工作流函数', description: '工作流处理函数' },
      logic: { name: '逻辑判断', description: '条件判断函数' }
    };

    // 默认注册的函数:
    this.register('string', 'upper', str => str.toUpperCase(), {
      name: '转为大写',
      description: '将字符串转为大写',
      example: '转为大写("hello") → "HELLO"',
      args: [{ name: 'string', description: '待转换的字符串' }],
      returnType: 'string'
    });

    this.register('string', 'lower', str => str.toLowerCase(), {
      name: '转为小写',
      description: '将字符串转为小写',
      example: '转为小写("HELLO") → "hello"',
      args: [{ name: 'string', description: '待转换的字符串' }],
      returnType: 'string'
    });

    this.register('string', 'camelCase', str => lodash.camelCase(str), {
      name: '转为驼峰写法',
      description: '将字符串转为驼峰写法',
      example: '转为驼峰写法("hello world") → "helloWorld"',
      args: [{ name: 'string', description: '待转换的字符串' }],
      returnType: 'string'
    });

    this.register('string', 'capitalize', str => lodash.capitalize(str), {
      name: '转为首字母大写',
      description: '将字符串转为首字母大写',
      example: '转为首字母大写("HELLO") → "Hello"',
      args: [{ name: 'string', description: '待转换的字符串' }],
      returnType: 'string'
    });

    this.register(
      'datetime',
      'currentDateTime',
      () => {
        return new Date();
      },
      {
        name: '获取当前日期时间',
        description: '获取当前日期时间',
        example: `获取当前日期时间 → Date 实例`,
        returnType: 'Date'
      }
    );

    this.register(
      'datetime',
      'currentYear',
      () => {
        const year = new Date().getFullYear();
        return year;
      },
      {
        name: '获取当前年份',
        description: '获取当前年份，可返回完整年份日期字符串',
        example: `获取当前年份() → 2023`,
        returnType: 'number'
      }
    );

    this.register(
      'datetime',
      'currentYearBegin',
      format => {
        const date = new Date(new Date().getFullYear(), 0, 1);
        return format ? moment(date).format(format) : date;
      },
      {
        name: '获取当前年初日期',
        description: '获取当前年初日期，可返回完整年份日期字符串',
        example: `获取当前年初日期() → Date 实例`,
        args: [{ name: 'format', description: '日期格式', optional: true }],
        returnType: ['Date', 'string']
      }
    );

    this.register(
      'datetime',
      'currentYearEnd',
      format => {
        const date = new Date(new Date().getFullYear(), 11, 31, 23, 59, 59, 999);
        return format ? moment(date).format(format) : date;
      },
      {
        name: '获取当前年末日期',
        description: '获取当前年末日期，可返回完整年份日期字符串',
        example: `获取当前年末日期() → Date 实例`,
        args: [{ name: 'format', description: '日期格式', optional: true }],
        returnType: ['Date', 'string']
      }
    );

    this.register(
      'datetime',
      'currentMonth',
      () => {
        const month = new Date().getMonth() + 1; // 0-11 → 1-12
        return month;
      },
      {
        name: '获取当前月份',
        description: '获取当前月份',
        example: `获取当前月份() → 8`,
        returnType: 'number'
      }
    );
    this.register(
      'datetime',
      'currentMonthBegin',
      format => {
        const now = new Date();
        const date = new Date(now.getFullYear(), now.getMonth(), 1, 0, 0, 0);
        return format ? moment(date).format(format) : date;
      },
      {
        name: '获取当前月初日期',
        description: '获取当前日期所在月份的第一天完整日期或字符串(时间部分为00:00:00)',
        example: ['获取当前月初日期() → Date 实例', , `获取当前月初日期('YYYY-MM-DD HH:mm:ss') → "2023-02-28 00:00:00"`],
        args: [{ name: 'format', description: '日期格式', optional: true }],
        returnType: ['string', 'Date']
      }
    );
    this.register(
      'datetime',
      'currentMonthEnd',
      format => {
        const date = new Date();
        const year = date.getFullYear();
        const month = date.getMonth() + 1;

        // 获取当月最后一天
        const lastDay = new Date(year, month, 0).getDate();
        const endDate = new Date(year, month - 1, lastDay, 23, 59, 59, 999);
        return format ? moment(endDate).format(format) : endDate;
      },
      {
        name: '获取当前月末日期',
        description: '获取当前日期所在月份的最后一天完整日期或字符串(时间部分为23:59:59)',
        example: [`获取当前月末日期() → Date 实例`, `获取当前月末日期('YYYY-MM-DD HH:mm:ss') → "2023-02-28 23:59:59"`],
        args: [{ name: 'format', description: '日期格式', optional: true }],
        returnType: ['string', 'Date']
      }
    );

    this.register(
      'datetime',
      'currentWeekDayDate',
      (index = 1, format) => {
        if (index < 1 || index > 7) {
          throw new Error('请输入1-7之间的数字，1代表星期一，7代表星期日');
        }
        const today = new Date();
        const currentDay = today.getDay(); // 0（周日）到6（周六）
        const currentDate = today.getDate();
        let date = undefined;
        if (index === 7) {
          // 7 → 下周周日（即下个周期的第0天）
          const daysUntilNextSunday = currentDay == 0 ? 0 : (7 - currentDay) % 7 || 7; // 如果今天是周日，则 +7 天
          date = new Date(today);
          date.setDate(currentDate + daysUntilNextSunday);
        } else {
          // 1-6 → 本周一到周六
          const diff = index - (currentDay == 0 ? 7 : currentDay);
          date = new Date(today);
          date.setDate(currentDate + diff);
        }
        date.setHours(0, 0, 0, 0);
        return format ? moment(date).format(format) : date;
      },
      {
        name: '获取本周星期几日期',
        description: '获取本周星期几日期，可返回完整日期字符串',
        example: `获取本周星期几日期() → Date 实例`,
        args: [
          {
            name: 'index',
            type: 'number',
            default: '1',
            description: '星期几，1-7，1为周一，默认为 1'
          },
          {
            name: 'format',
            type: 'string',
            optional: true,
            description: '返回日期字符串的格式'
          }
        ],
        returnType: ['Date', 'string']
      }
    );

    this.register(
      'datetime',
      'currentWeekDay',
      () => {
        const currentDay = new Date().getDay();
        return currentDay == 0 ? 7 : currentDay;
      },
      {
        name: '获取本周星期几',
        description: '获取本周星期几，返回星期几数字, 1-7',
        example: `获取本周星期几() → 1`,
        returnType: ['number']
      }
    );
    this.register(
      'datetime',
      'currentQuarter',
      () => {
        return Math.floor(new Date().getMonth() / 3) + 1; // 返回 1-4
      },
      {
        name: '获取当前季度',
        description: '获取当前季度，返回 1-4',
        example: `获取当前季度() → 1`,
        returnType: ['number']
      }
    );
    this.register(
      'datetime',
      'currentDay',
      () => {
        return new Date().getDate();
      },
      {
        name: '获取当前日',
        description: '获取当前月的几号日期，返回 1-31',
        example: `获取当前日() → 1`,
        returnType: ['number']
      }
    );

    this.register(
      'datetime',
      'currentHour',
      () => {
        return new Date().getHours();
      },
      {
        name: '获取当前小时',
        description: '获取当前月的几号日期，返回 1-23',
        example: `获取当前小时() → 23`,
        returnType: ['number']
      }
    );

    this.register(
      'datetime',
      'formatDateTimeString',
      (format = 'YYYY-MM-DD HH:mm:ss', date = new Date()) => {
        return moment(date).format(format);
      },
      {
        name: '获取日期字符串',
        description: '格式化当前日期为指定格式的字符串',
        example: `获取日期字符串('YYYY-MM-DD HH:mm:ss') → 2025-12-31 00:00:00`,
        args: [
          { name: 'format', type: 'string', description: '格式' },
          { name: 'date', optional: true, type: 'Date', description: '待格式化的日期，默认为当前时间' }
        ],
        returnType: ['string']
      }
    );

    // this.register("logic", "if", (condition, trueVal, falseVal) => {
    //   return condition ? trueVal : falseVal;
    // }, {
    //   name: "条件判断",
    //   description: "根据条件返回不同值",
    //   example: 'if(1 > 2, "yes", "no") → "no"',
    //   args: [
    //     { name: "condition", type: "boolean" },
    //     { name: "trueValue", type: "any" },
    //     { name: "falseValue", type: "any" }
    //   ]
    // });
  }

  /**
   * 注册函数（自动包装为异步）
   * @param {string} category - 函数分类
   * @param {string} name - 函数名
   * @param {Function} func - 函数实现
   * @param {object} meta - 函数元数据
   */
  register(category, name, func, meta = {}) {
    if (!this.categories[category] && category !== 'extFunction') {
      throw new Error(`分类 ${category} 不存在`);
    }

    let funcKeys = this.functions.filter(f => f.key);
    if (funcKeys.includes(name)) {
      throw new Error(`函数名 [${name}] 已存在, 禁止定义重名函数`);
    }

    // 自动包装为异步函数
    const asyncFunc = async (...args) => {
      const start = performance.now();
      try {
        const r = func(...args);
        const result = await Promise.resolve(r);
        const duration = performance.now() - start;
        console.log(`[函数执行] ${name} 耗时 ${duration.toFixed(2)}ms`);
        return result;
      } catch (error) {
        console.error(`[函数执行错误] ${name}:`, error);
        throw new Error(`${meta.name || name} 执行失败: ${error.message}`);
      }
    };

    this.functions.push({
      fn: asyncFunc,
      key: name,
      meta: {
        name: meta.name || name,
        description: meta.description || '',
        example: meta.example || `${name}()`,
        args: meta.args || [],
        returnType: meta.returnType || 'any',
        category
      }
    });
  }

  getFunctions() {
    return this.functions;
  }
  groupCategoryFunctions() {
    let group = [];
    for (let key in this.categories) {
      let category = this.categories[key];
      let functions = this.getCategoryFunctions(key);
      group.push({
        category,
        functions
      });
    }
    return group;
  }

  getAllCategories() {
    return Object.values(this.categories);
  }

  /**
   * 获取分类函数
   */
  getCategoryFunctions(category) {
    category = Array.isArray(category) ? category : [category];
    return this.getFunctions().filter(f => category.includes(f.meta ? f.meta.category : ''));
  }
}

export { JsFormulaEngine, FormulaFunctionLibrary };
