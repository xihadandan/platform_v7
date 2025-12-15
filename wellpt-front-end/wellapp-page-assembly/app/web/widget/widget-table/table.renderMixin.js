import moment from 'moment';
import CellRender from './cell-render/index';
import { trim, isEmpty } from 'lodash';
import { expressionCompare } from '@framework/vue/utils/util';

// 表格渲染器方法
export default {
  inject: ['widgetTableContext'],
  components: { ...CellRender },
  methods: {
    isCellRender(name) {
      return Object.keys(CellRender).includes(name);
    },
    moment,
    disabledDate(current, options) {
      // Can not select days before today and today
      if (options && options.selectRangeLimitType == 'beforeTodayUnselect') {
        return current && current <= moment().add(-1, 'day').endOf('day');
      }
      if (options && options.selectRangeLimitType == 'afterTodayUnselect') {
        return current && current > moment.endOf('day');
      }
      return false;
    },

    invokeRenderMethod: function (mehtodName, value, row, options) {
      return this[mehtodName] ? this[mehtodName](value, row, options) : value;
    },

    getRenderMethodOptions: function () {
      return [
        {
          label: 'vue模板',
          value: 'vueTemplateDataRender',
          scope: ['pc', 'mobile']
        },
        {
          label: '日期格式化',
          value: 'formateDateString',
          scope: ['pc', 'mobile']
        }

        // , {
        //   label: '脚本代码',
        //   value: 'executeDevJsFunction'
        // }
      ];
    },

    /**
     * 格式化时间字符串，从原来的模式格式化为另外的模式
     * @param {*} value 时间字符串值
     * @param {*} row  行数据记录
     * @param {*} options
     * @returns
     */
    formateDateString: function (value, row, options) {
      // 只有配置的时间格式有完整的年月日，才会进行不显示当前年和时间间隔处理
      let hasTimeExtraSetting =
        (options.destPattern || (options.datePatternJson && options.datePatternJson.contentFormat.indexOf('yyyy-MM-DD') > -1)) &&
        options.destPattern.startsWith('yyyy');
      let val = value ? moment(moment(value, options.sourcePattern || 'YYYY-MM-DD HH:mm:ss')).format(options.destPattern) : '';
      if (
        hasTimeExtraSetting &&
        options.hideYear &&
        val &&
        moment().isSame(moment(moment(value, options.sourcePattern || 'YYYY-MM-DD HH:mm:ss')), 'year')
      ) {
        val = val.substring(5);
      }
      if (val && options.isRange && options.endTimeParams) {
        if (row[options.endTimeParams]) {
          let end = moment(moment(row[options.endTimeParams], options.sourcePattern || 'YYYY-MM-DD HH:mm:ss')).format(
            options.endDestPattern
          );

          let hasEndHideYearSetting =
            options.endDestPattern &&
            options.endDatePatternJson.contentFormat.indexOf('yyyy-MM-DD') > -1 &&
            options.endDestPattern.startsWith('yyyy');
          if (hasTimeExtraSetting && hasEndHideYearSetting && options.hideYear && val && moment().isSame(end, 'year')) {
            end = end.substring(5);
          }
          val += ' ~ ' + end;
        }
      } else if (hasTimeExtraSetting && options.showTimeInterval && value) {
        // 时间间隔
        let _value = moment(moment(value, options.sourcePattern || 'YYYY-MM-DD HH:mm:ss'));
        let isformat = true;
        if (options.timeInterval) {
          let diff = Math.abs(_value.diff(moment(), 'days'));
          if (diff >= options.timeInterval) {
            isformat = false;
          }
        }
        if (isformat) {
          val = _value.fromNow();
        }
      }
      return val;
    },

    executeDevJsFunction: function (value, row, options) {},

    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },
    forceUpdateTable() {
      if (this.reUpdateIndex > 0) {
        this.reUpdateIndex--;
        if (this.reUpdateIndex == 0) {
          this.$refs.mianTableRef && this.$refs.mianTableRef.$forceUpdate();
        }
      }
    },
    /**
     * @desc 动态指定那一列 进行合并行
     * @param {Array} dataSource 表格数据
     * @param {Array} mergeKeys 需要合并的列字段数组
     * @param {Object} config 合并配置项
     */
    mergeRows(dataSource, mergeKeys, config) {
      const mergeInfoArr = {};
      let mergeIfNull = config.mergeIfNull;
      let isLevelMerge = false;
      if (config.mergeCellMode == 'merge' && config.mergeCellType == 'level') {
        isLevelMerge = true;
      }
      for (let i = 0; i < mergeKeys.length; i++) {
        const mergeInfo = [];
        const key = mergeKeys[i];
        // 遍历数据源，计算合并信息
        for (let j = 0; j < dataSource.length; j++) {
          const currentValue = dataSource[j][key];
          if (j === 0) {
            mergeInfo[j] = {
              rowSpan: 1,
              value: currentValue
            };
          } else {
            const prevValue = dataSource[j - 1][key];
            // 空值合并逻辑处理，空值不合并时，则跳过空值
            let isNullMerge =
              mergeIfNull || (!mergeIfNull && currentValue !== null && currentValue !== undefined && trim(currentValue) !== '');
            if (isNullMerge && currentValue === prevValue) {
              if (isLevelMerge && i > 0) {
                // 逐级合并，如遇到前一个合并字段，rowspan值为0时，跳过合并
                if (mergeInfoArr[mergeKeys[i - 1]][j].rowSpan > 0) {
                  mergeInfo[j] = {
                    rowSpan: 1,
                    value: currentValue,
                    isStart: true
                  };
                  continue;
                }
              }
              // 参与合并
              mergeInfo[j] = {
                rowSpan: 0,
                value: currentValue
              };
              // 向前查找第一个  并更新连续相同值的最早单元格的rowSpan
              let start = j - 1;
              let hasStart = false;
              while (start >= 0 && mergeInfo[start].value === currentValue && !hasStart) {
                if (mergeInfo[start].hasOwnProperty('isStart')) {
                  hasStart = true;
                }
                start--;
              }
              const firstIndex = start + 1; // 连续值的起始位置
              mergeInfo[firstIndex].rowSpan++;
            } else {
              mergeInfo[j] = {
                rowSpan: 1,
                value: currentValue
              };
            }
          }
        }
        mergeInfoArr[key] = mergeInfo;
      }
      // console.log(mergeInfoArr);
      this.forceUpdateTable();
      return mergeInfoArr;
    },
    async mergeColumns(dataSource, columns, config) {
      this.mergeColumnsData = {};
      // 遍历数据源
      for (let j = 0; j < dataSource.length; j++) {
        if (config.rowMergeType == 'default') {
          for (let i = 0; i < config.rowMergeData.length; i++) {
            let controlConfig = config.rowMergeData[i];
            try {
              const results = await this.calculateRowDataConditionControlResult(dataSource[j], controlConfig);
              if (results) {
                const mergeInfo = this.mergeColumn(controlConfig, columns, config.rowMergeType);
                if (mergeInfo) {
                  let startIndex = controlConfig.startIndex;
                  if (this.configuration.addSerialNumber) {
                    // 默认情况下，序号列不在索引内
                    startIndex++;
                  }
                  if (!this.mergeColumnsData[j]) {
                    this.mergeColumnsData[j] = mergeInfo;
                  } else {
                    Object.assign(this.mergeColumnsData[j], mergeInfo);
                  }
                  if (controlConfig.isFunction) {
                    if (controlConfig.function) {
                      // 运行函数
                      try {
                        let func = new Function('row', 'index', controlConfig.function);
                        let content = func(dataSource[j], j);
                        if (content) {
                          dataSource[j][columns[startIndex].dataIndex] = content;
                        }
                      } catch (error) {
                        console.error(error);
                      }
                    }
                  } else if (controlConfig.content) {
                    dataSource[j][columns[startIndex].dataIndex] = this.$t(controlConfig.id, controlConfig.content);
                  }
                }
              }
            } catch (error) {
              console.error(error);
            }
          }
        } else if (config.rowMergeType == 'function' && config.rowMergeFunction) {
          // 运行函数
          try {
            let func = new Function('row', 'index', 'columns', config.rowMergeFunction);
            let results = func(dataSource[j], j, columns);
            if (results && !isEmpty(results)) {
              for (let i = 0; i < results.length; i++) {
                const mergeInfo = this.mergeColumn(results[i], columns, config.rowMergeType);
                if (mergeInfo) {
                  if (!this.mergeColumnsData[j]) {
                    this.mergeColumnsData[j] = mergeInfo;
                  } else {
                    Object.assign(this.mergeColumnsData[j], mergeInfo);
                  }
                  if (results[i].content) {
                    dataSource[j][columns[results[i].startIndex].dataIndex] = results[i].content;
                  }
                }
              }
            }
          } catch (error) {
            console.error(error);
          }
        }
      }
      this.forceUpdateTable();
    },
    mergeColumn(controlConfig, columns, rowMergeType) {
      let startIndex = controlConfig.startIndex;
      if (this.configuration.addSerialNumber) {
        // 默认情况下，序号列不在索引内
        startIndex++;
      }
      let count = controlConfig.count;
      let endIndex = startIndex + count - 1;
      if (endIndex > columns.length - 1) {
        endIndex = columns.length - 1;
        count = endIndex - startIndex + 1;
      }
      if (startIndex > -1 && count > 1 && startIndex < columns.length - 1 && startIndex < endIndex) {
        const mergeInfo = {};
        mergeInfo[columns[startIndex].dataIndex] = { colSpan: count };
        for (let k = 1; k < count; k++) {
          mergeInfo[columns[startIndex + k].dataIndex] = { colSpan: 0 };
        }
        return mergeInfo;
      }
      return undefined;
    },
    async renderRowsByState(dataSource, config) {
      // 遍历数据源
      for (let j = 0; j < dataSource.length; j++) {
        if (config.type == 'default' && config.stateData.length) {
          for (let i = 0; i < config.stateData.length; i++) {
            let controlConfig = config.stateData[i];
            await this.calculateRowDataConditionControlResult(dataSource[j], controlConfig).then(results => {
              if (results) {
                let style = {};
                for (let key in controlConfig.style) {
                  let value = controlConfig.style[key];
                  if (value) {
                    if (key.indexOf('color') > -1 || key.indexOf('Color') > -1) {
                      style[key] = value.startsWith('#') ? value : `var(${value})`;
                    } else if (key == 'fontSize') {
                      style[key] = `${value}px`;
                    } else {
                      style[key] = value;
                    }
                  }
                }
                this.rowStyle[j] = style;
              }
            });
          }
        } else if (config.type == 'function' && config.function) {
          // 运行函数
          try {
            let func = new Function('row', 'index', config.function);
            let results = func(dataSource[j], j);
            if (results && !isEmpty(results)) {
              let style = {};
              for (let key in results) {
                let value = results[key];
                if (value) {
                  if (key.indexOf('color') > -1 || key.indexOf('Color') > -1) {
                    style[key] = value.startsWith('--') ? `var(${value})` : value;
                  } else if (key == 'fontSize') {
                    style[key] = `${value}px`;
                  } else {
                    style[key] = value;
                  }
                }
              }
              this.rowStyle[j] = style;
            }
          } catch (error) {
            console.error(error);
          }
        }
      }
      this.forceUpdateTable();
    },
    calculateRowDataConditionControlResult(_compareData, controlConfig) {
      return new Promise((resolve, reject) => {
        // 根据数据决定是否控制
        if (controlConfig && controlConfig.enable) {
          let promise = [];
          if (controlConfig.match != undefined && controlConfig.conditions != undefined) {
            // 多组条件判断
            let match = controlConfig.match == 'all';
            for (let i = 0, len = controlConfig.conditions.length; i < len; i++) {
              let { code, codeValue, operator, value } = controlConfig.conditions[i];
              if (code.endsWith('.') && codeValue) {
                code = `${code}${codeValue}`;
              }
              let result = expressionCompare(_compareData, code, operator, value, true);
              promise.push(result);
            }
            Promise.all(promise)
              .then(results => {
                for (let result of results) {
                  if (controlConfig.match == 'all' && !result) {
                    match = false;
                    break;
                  }
                  if (controlConfig.match == 'any' && result) {
                    match = true;
                    break;
                  }
                }

                resolve(match);
              })
              .catch(() => {
                reject();
              });
          } else {
            let { code, codeValue, value, operator } = controlConfig;
            if (code.endsWith('.') && codeValue) {
              code = `${code}${codeValue}`;
            }
            expressionCompare(_compareData, code, operator, value, true)
              .then(result => {
                resolve(result);
              })
              .catch(() => {
                reject();
              });
          }
        } else {
          // 无启用则直接返回true
          resolve(true);
        }
      });
    }
  },
  mounted() {}
};
