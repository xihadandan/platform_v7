import { debounce } from 'lodash';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
import configurationMixin from '@framework/vue/designer/configurationMixin.js';

export default {
  mixins: [configurationMixin],
  props: {
    parent: Object
  },
  data() {
    let validateMin = (rule, value, callback) => {
      const max = this.widget.configuration.maxCount;
      if (value && max) {
        //最小值大于最大值时报错
        if (value > max) {
          callback(<a-icon type="close-circle" theme="filled" />);
          this.$message.error('最小值应小于最大值');
        }
      }
      callback();
    };
    let validateMax = (rule, value, callback) => {
      const min = this.widget.configuration.minCount;
      if (min && value) {
        //最小值大于最大值时报错
        if (value < min) {
          callback(<a-icon type="close-circle" theme="filled" />);
          this.$message.error('最大值应大于最小值');
        }
      }
      callback();
    };
    return {
      rules: {
        name: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true },
        code: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true },
        minCount: { validator: validateMin, trigger: ['change'] },
        maxCount: { validator: validateMax, trigger: ['change'] }
      }
    };
  },
  computed: {
    configuration() {
      return this.widget.configuration;
    },
    inputFieldOptions() {
      let opt = [];
      if (this.designer.WidgetFormInputs && this.designer.WidgetFormInputs.length) {
        for (let k = 0, len = this.designer.WidgetFormInputs.length; k < len; k++) {
          let _WidgetFormInput = this.designer.WidgetFormInputs[k];
          if (_WidgetFormInput.configuration.code) {
            opt.push({
              label: _WidgetFormInput.configuration.name || _WidgetFormInput.configuration.code,
              value: _WidgetFormInput.configuration.code
            });
          }
        }
      }
      return opt;
    },

    textWidgetOptions() {
      let opt = [];
      if (this.designer.WidgetFormTexts && this.designer.WidgetFormTexts.length) {
        for (let k = 0, len = this.designer.WidgetFormTexts.length; k < len; k++) {
          let _Widget = this.designer.WidgetFormTexts[k];
          if (_Widget.configuration.name) {
            opt.push({
              label: _Widget.configuration.name,
              value: _Widget.id
            });
          }
        }
      }
      return opt;
    },

    datePickerWidgetOptions() {
      let opt = [];
      if (this.designer.WidgetFormDatePickers && this.designer.WidgetFormDatePickers.length) {
        for (let k = 0, len = this.designer.WidgetFormDatePickers.length; k < len; k++) {
          let _Widget = this.designer.WidgetFormDatePickers[k];
          if (_Widget.configuration.code && _Widget.id != this.widget.id) {
            opt.push({
              label: _Widget.configuration.name || _Widget.configuration.code,
              value: _Widget.configuration.code
            });
          }
        }
      }
      return opt;
    }
  },
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
    validateConfigure() {
      return new Promise((resolve, reject) => {
        if (this.$refs.form) {
          this.$refs.form.validate((vali, msg) => {
            if (vali) {
              resolve({ vali, msg });
            } else {
              reject({ vali, msg });
            }
          });
        } else {
          resolve({ vali: true });
        }
      });
    },
    getLabelValueOptionByDataDic(dataDicUuid, callback) {
      let key = `LabelOptionByDataDic:${dataDicUuid}`;
      this.$tempStorage.getCache(
        key,
        () => {
          return new Promise((resolve, reject) => {
            $axios
              .post('/json/data/services', {
                serviceName: 'cdDataDictionaryFacadeService',
                methodName: 'listLocaleItemByDictionaryCode',
                args: JSON.stringify([dataDicUuid])
              })
              .then(({ data }) => {
                let options = [];
                if (data.code == 0 && data.data) {
                  options = data.data;
                }
                resolve(options, key);
              });
          });
        },
        data => {
          if (typeof callback === 'function') {
            callback(data, key);
          }
        }
      );
    },
    // 获取下拉框数据字典
    fetchSelectOptionByDataDic(dataDicUuid, callback) {
      let _this = this;
      let afterFetchData = data => {
        let selectOptions = [];
        for (let i = 0, len = data.length; i < len; i++) {
          if (_this.widget.configuration.type === 'select-group') {
            for (let j = 0, len = data[i].children.length; j < len; j++) {
              let children = data[i].children[j];
              let opt = {
                label: children.label,
                value: children.value,
                uuid: children.uuid
              };
              selectOptions.push(opt);
            }
          } else {
            let opt = {
              label: data[i].label,
              value: data[i].value,
              uuid: data[i].uuid
            };
            selectOptions.push(opt);
          }
        }
        return selectOptions;
      };
      this.getLabelValueOptionByDataDic(dataDicUuid, (results, key) => {
        let options = afterFetchData.call(_this, results);
        if (typeof callback === 'function') {
          callback(options);
        }
      });
    }
  }
};
