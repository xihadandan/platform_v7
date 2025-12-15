import createClientCommonApi from '@pageAssembly/app/web/framework/vue/clientCommonApi';

export const subformFormulaApi = {
  sumColumnValue: ({ $subform, formula }) => {
    return {
      [formula.varName]: $subform.calculateColumnTotalValue(formula.params.dataIndex)
    };
  },
  avgColumnValue: ({ $subform, formula }) => {
    return {
      [formula.varName]: $subform.calculateColumnAvgValue(formula.params.dataIndex, formula.params.fixedNumber)
    };
  },
  maxColumnValue: ({ $subform, formula }) => {
    return {
      [formula.varName]: $subform.calculateColumnMaxValue(formula.params.dataIndex)
    };
  },
  minColumnValue: ({ $subform, formula }) => {
    return {
      [formula.varName]: $subform.calculateColumnMinValue(formula.params.dataIndex)
    };
  },
  concatColumnValue: ({ $subform, formula }) => {
    return {
      [formula.varName]: $subform.calculateColumnConcatValue(formula.params.dataIndex)
    };
  }
};

export const dateFormulaApi = {
  dateDiff: ({ form, subform, formula }) => {
    return new Promise((resolve, reject) => {
      let { dataIndex, params, varName } = formula,
        result = { [varName]: undefined };
      if (
        dataIndex != undefined &&
        Array.isArray(dataIndex) &&
        dataIndex.length == 2 &&
        dataIndex[0] != undefined &&
        dataIndex[1] != undefined
      ) {
        let fromFieldForm = form;
        let toFieldForm = form;
        if (dataIndex[0].startsWith('SUBFORM.')) {
          // 从表字段
          dataIndex[0] = dataIndex[0].replace('SUBFORM.', '');
          fromFieldForm = subform;
        }
        if (dataIndex[1].startsWith('SUBFORM.')) {
          dataIndex[1] = dataIndex[1].replace('SUBFORM.', '');
          toFieldForm = subform;
        }
        let fromTimeField = fromFieldForm.$fieldset[dataIndex[0]],
          toTimeField = toFieldForm.$fieldset[dataIndex[1]];
        if (
          fromTimeField != undefined &&
          toTimeField != undefined &&
          fromFieldForm.formData[dataIndex[0]] != undefined &&
          toFieldForm.formData[dataIndex[1]] != undefined
        ) {
          let toTimeMoment = toTimeField.getFormattedMoment(); // 获取格式化后的 moment
          if (toTimeMoment && params.includeEndDay && toTimeField.contentFormat.endsWith('D')) {
            // 结束日期当天包含在计算内（仅在日期格式为年月日时候才会有该配置）
            toTimeMoment = toTimeMoment.add(1, 'd');
          }
          // 计算自然日
          if (params.diffBy == 'natureDay') {
            let fromMoment = fromTimeField.getFormattedMoment();
            if (fromMoment != undefined && toTimeMoment != undefined) {
              let timestamp = toTimeMoment.valueOf() - fromMoment.valueOf();
              result[varName] = timestamp;
              if (params.unit == 'day') {
                result[varName] = timestamp / (1000 * 60 * 60 * 24);
              } else if (params.unit == 'hour') {
                result[varName] = timestamp / (1000 * 60 * 60);
              } else if (params.unit == 'minute') {
                result[varName] = timestamp / (1000 * 60);
              } else if (params.unit == 'second') {
                result[varName] = timestamp / 1000;
              }
            }
            resolve(result);
          } else {
            // 计算工作日
            let fromTimeField = fromFieldForm.$fieldset[dataIndex[0]],
              toTimeField = toFieldForm.$fieldset[dataIndex[1]];
            let toTime = toFieldForm.formData[dataIndex[1]],
              fromTime = fromFieldForm.formData[dataIndex[0]];
            if (fromTime && toTime) {
              let toTimeMoment = toTimeField.getFormattedMoment();
              if (toTimeMoment != undefined) {
                toTime = toTimeMoment.format('yyyy-MM-DD HH:mm:ss');
              }
              if (params.includeEndDay && toTimeField.contentFormat.endsWith('D') && toTimeMoment) {
                toTimeMoment = toTimeMoment.add(1, 'd');
                toTime = toTimeMoment.format('yyyy-MM-DD');
              }

              let fromTimeMoment = fromTimeField.getFormattedMoment();
              fromTime = fromTimeMoment.format('yyyy-MM-DD HH:mm:ss');

              createClientCommonApi()
                .getWorkTimePeriod({
                  workTimePlanUuid: params.workTimePlanUuid,
                  fromTime,
                  toTime
                })
                .then(res => {
                  if (res) {
                    let { effectiveWorkMinuteBy24Rule, effectiveWorkMinute, effectiveWorkDay } = res;
                    if (params.unit == 'day') {
                      resolve({
                        [varName]: effectiveWorkDay != null ? parseFloat(effectiveWorkDay.toFixed(2)) : 0
                      });
                    } else if (params.unit == 'hour') {
                      resolve({
                        [varName]: parseFloat((effectiveWorkMinute / 60).toFixed(2))
                      });
                    } else {
                      resolve(result);
                    }
                  } else {
                    resolve(result);
                  }
                });
            } else {
              resolve(result);
            }
          }
        }
      }
    });
  },
  getDay: ({ form, formula }) => {
    let { dataIndex, params, varName } = formula,
      result = {};
    if (dataIndex != undefined && form.$fieldset[dataIndex] != undefined) {
      let m = form.$fieldset[dataIndex].getFormattedMoment();
      if (m) {
        result[varName] = m.date();
      }
    }
    return result;
  },
  getMonth: ({ form, formula }) => {
    let { dataIndex, params, varName } = formula,
      result = {};
    if (dataIndex != undefined && form.$fieldset[dataIndex] != undefined) {
      let m = form.$fieldset[dataIndex].getFormattedMoment();
      if (m) {
        result[varName] = m.month() + 1;
      }
    }
    return result;
  },
  getHour: ({ form, formula }) => {
    let { dataIndex, params, varName } = formula,
      result = {};
    if (dataIndex != undefined && form.$fieldset[dataIndex] != undefined) {
      let m = form.$fieldset[dataIndex].getFormattedMoment();
      if (m) {
        result[varName] = m.hour();
      }
    }
    return result;
  },
  getMinute: ({ form, formula }) => {
    let { dataIndex, params, varName } = formula,
      result = {};
    if (dataIndex != undefined && form.$fieldset[dataIndex] != undefined) {
      let m = form.$fieldset[dataIndex].getFormattedMoment();
      if (m) {
        result[varName] = m.minute();
      }
    }
    return result;
  },
  getWeekday: ({ form, formula }) => {
    let { dataIndex, params, varName } = formula,
      result = {};
    if (dataIndex != undefined && form.$fieldset[dataIndex] != undefined) {
      let m = form.$fieldset[dataIndex].getFormattedMoment();
      if (m) {
        let day = m.day();
        result[varName] = day == 0 ? 7 : day;
      }
    }
    return result;
  },
  getYear: ({ form, formula }) => {
    let { dataIndex, params, varName } = formula,
      result = {};
    if (dataIndex != undefined && form.$fieldset[dataIndex] != undefined) {
      let m = form.$fieldset[dataIndex].getFormattedMoment();
      if (m) {
        result[varName] = m.year();
      }
    }
    return result;
  },
  getQuarter: ({ form, formula }) => {
    let { dataIndex, params, varName } = formula,
      result = {};
    if (dataIndex != undefined && form.$fieldset[dataIndex] != undefined) {
      let m = form.$fieldset[dataIndex].getFormattedMoment();
      if (m) {
        result[varName] = m.quarter();
      }
    }
    return result;
  },
  getWeek: ({ form, formula }) => {
    let { dataIndex, params, varName } = formula,
      result = {};
    if (dataIndex != undefined && form.$fieldset[dataIndex] != undefined) {
      let m = form.$fieldset[dataIndex].getFormattedMoment();
      if (m) {
        result[varName] = m.week();
      }
    }
    return result;
  }
};

export const textFormulaApi = {
  concatString({ form, formula, $subform }) {
    let widgetDyformContext = this.widgetDyformContext,
      rootWidgetDyformContext = this.rootWidgetDyformContext;
    let { dataIndex, params, varName } = formula,
      result = {};
    if (dataIndex != undefined) {
      let text = [];
      for (let obj of dataIndex) {
        let list = obj.dataIndex,
          widgetId = obj.widgetId;
        if (widgetId) {
          let $subform = widgetDyformContext.dyform.$subform[widgetId] || rootWidgetDyformContext.dyform.$subform[widgetId];
          if ($subform) {
            // 从表计算
            let t = $subform.concatColumnValues(list, params.distinct, params.separator);
            if (t && (!params.distinct || (params.distinct && !text.includes(t)))) {
              text.push(t);
            }
          }
        } else {
          for (let d of list) {
            let field = widgetDyformContext.dyform.$fieldset[d];
            let txt = field.getValue();
            if (txt != undefined) {
              if (typeof txt == 'object') {
                txt = JSON.stringify(txt);
              } else {
                txt = (txt + '').trim();
              }
              if (txt && (!params.distinct || (params.distinct && !text.includes(txt)))) {
                text.push(txt);
              }
            }
          }
        }
      }
      result[varName] = text.join(params.separator || '');
    }
    return result;
  }
};
