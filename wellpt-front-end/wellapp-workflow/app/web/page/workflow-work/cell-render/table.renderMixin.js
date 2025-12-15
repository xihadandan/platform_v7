import moment from 'moment';
import CellRender from './index';

// 表格渲染器方法
export default {
  inject: [],
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

    invokeRenderMethod: function (mehtodName, value, row, options, slotOption) {
      return this[mehtodName] ? this[mehtodName](value, row, options, slotOption) : value;
    },

    getRenderMethodOptions: function () {
      return [
        {
          label: 'vue模板',
          value: 'vueTemplateDataRender'
        },
        {
          label: '日期格式化',
          value: 'formateDateString'
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
      let val = value ? moment(moment(value, options.sourcePattern || 'YYYY-MM-DD HH:mm:ss')).format(options.destPattern) : '';
      if (val && options.isRange && options.endTimeParams) {
        if (row[options.endTimeParams]) {
          let endTime = moment(row[options.endTimeParams], options.sourcePattern || 'YYYY-MM-DD HH:mm:ss');
          if (endTime.isValid()) {
            let end = moment(endTime).format(
              options.endDestPattern
            );
            val += ' ~ ' + end;
          }
        }
      }
      return val;
    },

    executeDevJsFunction: function (value, row, options) { },

    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    }
  }
};
