<template>
  <!-- 标签组 -->
  <a-form-model-item
    v-show="!hidden"
    :prop="formModelItemProp"
    :rules="rules"
    :ref="widget.configuration.code"
    :label="itemLabel"
    :colon="displayAsLabel"
    :class="['widget-form-tag', widgetClass]"
  >
    <div v-show="!displayAsLabel">
      <div
        v-show="widget.configuration.tagEditMode === 'select'"
        :class="[readonly ? 'readonly' : '', disable ? 'disabled' : '']"
        class="tag-container"
      >
        <template v-for="(item, index) in checkedOptions">
          <a-tag :key="index" :visible="true" :closable="!readonly" :class="[tagClass]" @close="() => handleCloseTag(item)">
            {{ item.label }}
          </a-tag>
        </template>
        <a-popover
          overlayClassName="tag-popover"
          title=""
          trigger="click"
          placement="bottomLeft"
          :arrowPointAtCenter="true"
          :getPopupContainer="getPopupContainerNearestPs()"
        >
          <template slot="content">
            <a-checkbox
              v-if="widget.configuration.selectCheckAll"
              :indeterminate="indeterminate"
              :checked="checkAll"
              @change="onCheckAllChange"
            >
              {{ $t('WidgetFormSelect.selectAll', '全选') }}
            </a-checkbox>
            <a-checkbox-group v-model="checkedValue" @change="changeTag">
              <a-checkbox v-for="(opt, i) in options" :key="i" :value="opt.value">
                {{ opt.label }}
              </a-checkbox>
            </a-checkbox-group>
          </template>
          <a-tag class="add-tags" v-if="!readonly"><a-icon type="plus" /></a-tag>
        </a-popover>
      </div>
      <TagInput
        ref="tagInput"
        :tagClass="tagClass"
        @updateValue="updateCheckeValue"
        v-show="widget.configuration.tagEditMode === 'input'"
      />
    </div>
    <span v-show="displayAsLabel" class="textonly" :title="checkedLabel">{{ checkedLabel }}</span>
  </a-form-model-item>
</template>

<script>
import { findIndex, each, debounce } from 'lodash';
import { deepClone } from '@framework/vue/utils/util';
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import formMixin from '../mixin/form-common.mixin';
import TagInput from './components/tagInput';
import { getPopupContainerNearestPs } from '@framework/vue/utils/function.js';
import './css/index.less';
export default {
  name: 'WidgetFormTag',
  extends: FormElement,
  mixins: [widgetMixin, formMixin],
  props: {
    defaultTagOptions: Array
  },
  data() {
    let tagOptions = this.defaultTagOptions || [];
    return {
      initOptions: tagOptions, // 初始时标签选项
      options: tagOptions,
      checkedValue: [], // 选中标签
      indeterminate: false,
      checkAll: false
    };
  },
  components: {
    TagInput
  },
  computed: {
    // 选中的标签显示值
    checkedLabel() {
      let checkedLabel = '';
      const value = this.formData[this.fieldCode];
      if (value) {
        this.checkedValue = value.split(this.configuration.tokenSeparators);
      }
      if (this.widget.configuration.tagEditMode === 'input') {
        return this.arrJoinStr(this.checkedValue);
      }
      if (this.checkedValue.length) {
        let labelArr = [];
        this.options.forEach(item => {
          if (this.checkedValue.includes(item.value)) {
            labelArr.push(item.label);
          }
        });
        checkedLabel = this.arrJoinStr(labelArr);
      }
      return checkedLabel;
    },
    // 选中的标签选项
    checkedOptions() {
      let checkedTags = [];
      if (this.checkedValue.length) {
        checkedTags = this.options.filter(item => this.checkedValue.includes(item.value));
      } else {
        if (this.designMode) {
          checkedTags = this.defaultTagOptions;
        }
      }
      return checkedTags;
    },
    tagClass() {
      let classval = ['tag-no-border'];
      if (this.widget.configuration.hasBorder) {
        classval.splice(0, classval.length);
      }
      return classval.join(' ');
    },
    defaultEvents() {
      let eventParams = [];
      if (this.designMode) {
        if (this.optionType === 'selfDefine' || this.optionType === 'dataDictionary') {
          let eventParamKeyOptions = {
            paramKey: 'options',
            remark: '选项',
            valueSource: {
              inputType: 'multi-select',
              options: this.options
            }
          };
          eventParams.push(eventParamKeyOptions);
        }
        if (this.optionType == 'dataSource' || this.optionType == 'dataModel') {
          eventParams.push({
            paramKey: 'removeCache',
            remark: '在从表里，该值为true会清除缓存；主表里默认清除缓存,该参数无效',
            valueSource: {
              inputType: 'select',
              options: [
                { label: '是', value: true },
                { label: '否', value: false }
              ]
            }
          });
        }
      }
      return [
        {
          id: 'refreshOptions',
          title: '重新加载备选项',
          eventParams
        }
      ];
    },
    tagValueOptionMap() {
      let map = {};
      for (let i = 0, len = this.options.length; i < len; i++) {
        map[this.options[i].value] = this.options[i];
      }
      return map;
    },
    value() {
      return this.checkedValue;
    }
  },
  mounted() {
    this.init();
  },
  methods: {
    getPopupContainerNearestPs,
    init() {
      if (!this.designMode) {
        if (this.optionDataAutoSet && this.relateKey) {
          this.listenRelateChanged(() => {
            this.checkRelateValue({
              relateValue: this.relateValue,
              configuration: this.widget.configuration,
              callback: this.optionsChangeAfter
            });
          });
        }
      }
      this.addSelectedNumRules({
        min: this.widget.configuration.minCount,
        max: this.widget.configuration.maxCount
      });
      if (this.formData[this.fieldCode]) {
        this.setValue(this.formData[this.fieldCode]);
      }
      if (!this.optionDataAutoSet || this.designMode || this.formData[this.fieldCode]) {
        this.getOptions();
      }
    },

    //重新加载备选项
    refetchOption() {
      // 不是联动获取备选项
      if (!this.optionDataAutoSet || this.formData[this.fieldCode]) {
        this.getOptions();
      }
    },
    // 通过事件配置的选项值，筛选常量和字典备选项
    getSelectOptionByValue(values) {
      this.options.splice(0, this.options.length);
      each(this.initOptions, opt => {
        if (values.indexOf(opt.value) > -1) {
          this.options.push(opt);
        }
      });
    },
    // 判断联动方式
    checkRelateValue(arg) {
      if (this.optionType === 'selfDefine') {
        this.relateSelfDefine(arg);
      }
      if (this.optionType === 'dataDictionary') {
        this.fetchOptionByDataDic(this.relateValue);
      }
    },
    optionsChangeAfter(options) {
      this.options = options;
      const value = this.formData[this.fieldCode];
      if (value) {
        const index = findIndex(this.options, { value });
        if (index == -1) {
          this.checkedValue = [];
          this.updateValue();
        }
      }
    },
    // 设置值
    setValue(value) {
      if (!value) {
        this.formData[this.fieldCode] = '';
        this.checkedValue.splice(0, this.checkedValue.length);
        this.clearValidate();
      }
      if (this.widget.configuration.tagEditMode === 'input') {
        if (value) {
          this.checkedValue = value.split(this.configuration.tokenSeparators);
          this.$refs.tagInput.setValue(this.checkedValue);
          this.clearValidate();
        }
      } else {
        this.getOptions(() => {
          if (value) {
            this.checkedValue = value.split(this.configuration.tokenSeparators);
            this.changeTag(this.checkedValue);
          }
          this.clearValidate();
        });
      }
    },
    displayValue(value, template) {
      return this.checkedLabel;
    },
    // 获取选项
    getOptions(callback) {
      const optionsType = this.widget.configuration.options.type;
      if (optionsType === 'selfDefine' && this.widget.configuration.options.defineOptions.length) {
        this.options = this.widget.configuration.options.defineOptions;
        for (let o of this.options) {
          o.label = this.$t(o.id, o.label);
        }
        this.initOptions = deepClone(this.options);
        this.emitChange({}, false);
        if (typeof callback === 'function') {
          callback();
        }
      } else if (optionsType === 'dataDictionary') {
        const dataDicUuid = this.configuration.options.dataDictionaryUuid;
        this.fetchOptionByDataDic(dataDicUuid, () => {
          if (typeof callback === 'function') {
            callback();
          }
        });
      } else if (optionsType === 'dataSource') {
        const options = this.widget.configuration.options;
        this.fetchOptionByDataSource(options, () => {
          if (typeof callback === 'function') {
            callback();
          }
        });
      } else if (optionsType == 'dataModel') {
        this.fetchOptionByDataModel(this.widget.configuration.options, () => {
          if (typeof callback === 'function') {
            callback();
          }
        });
      } else if (optionsType == 'apiLinkService') {
        this.fetchOptionByApiLink(this.widget.configuration.options, () => {
          if (typeof callback === 'function') {
            callback();
          }
        });
      }
    },

    fetchOptionByApiLink(options, callback) {
      let _this = this;
      _this.options.splice(0, _this.options.length);
      this.fetchDataByApiLinkInvocation(options.apiInvocationConfig).then(result => {
        if (Array.isArray(result)) {
          _this.options = result;
          _this.initOptions = deepClone(_this.options);
          _this.emitChange({}, false);
          if (typeof callback === 'function') {
            callback();
          }
        }
      });
    },
    fetchOptionByDataModel(options) {
      let _this = this;
      this.getLabelValueOptionByDataModel(options, function (result) {
        _this.options = result;
        _this.initOptions = deepClone(this.options);
        _this.emitChange({}, false);
        if (typeof callback === 'function') {
          callback();
        }
      });
    },
    // 获取数据字典
    fetchOptionByDataDic(dataDicUuid, callback) {
      this.getLabelValueOptionByDataDic(dataDicUuid, result => {
        this.options = result;
        this.initOptions = deepClone(this.options);
        this.emitChange({}, false);
        if (typeof callback === 'function') {
          callback();
        }
      });
    },
    // 获取数据仓库
    fetchOptionByDataSource(options, callback) {
      this.getLabelValueOptionByDataSource(options, result => {
        this.options = result;
        this.initOptions = deepClone(this.options);
        this.emitChange({}, false);
        if (typeof callback === 'function') {
          callback();
        }
      });
    },
    // 关闭标签
    handleCloseTag(removedTag) {
      this.checkedValue = this.checkedValue.filter(item => item !== removedTag.value);
      this.updateValue();
    },
    onCheckAllChange(e) {
      const checked = e.target.checked;
      this.checkAll = checked;
      this.indeterminate = false;
      this.checkedValue = [];
      if (checked) {
        this.options.forEach(item => {
          this.checkedValue.push(item.value);
        });
      }
      this.updateValue();
    },
    // 选择标签
    changeTag(checkedList) {
      this.indeterminate = !!checkedList.length && checkedList.length < this.options.length;
      this.checkAll = checkedList.length === this.options.length;
      this.updateValue();
    },
    // 更新值
    updateValue(values = this.checkedValue) {
      this.formData[this.fieldCode] = this.arrJoinStr(values);
      this.setDisplayValueField();
      this.emitValidate();
      this.emitChange();
    },
    // 设置显示值
    setDisplayValueField() {
      if (this.widget.configuration.displayValueField) {
        // 设置关联显示值字段
        this.form.setFieldValue(this.widget.configuration.displayValueField, this.checkedLabel);
      }
    },
    updateCheckeValue(values) {
      this.checkedValue = values;
      this.updateValue();
    },
    arrJoinStr(values) {
      values = values || [];
      return values.join(this.configuration.tokenSeparators);
    },
    onFilter({ searchValue, comparator, source, ignoreCase }) {
      return new Promise((resolve, reject) => {
        if (source != undefined) {
          // 由外部提供数据源进行判断
          let sources = source.split(';');
          let searchValues = searchValue.split(';');
          for (let i = 0, len = searchValues.length; i < len; i++) {
            if (comparator == 'like') {
              // 模糊匹配
              for (let j = 0, jlen = sources.length; j < jlen; j++) {
                let s = sources[j];
                if (this.widget.configuration.tagEditMode === 'select') {
                  if (this.tagValueOptionMap[s] != undefined) {
                    s = this.tagValueOptionMap[s].label;
                  }
                }

                if (ignoreCase ? s.toLowerCase().indexOf(searchValues[i].toLowerCase()) != -1 : s.indexOf(searchValues[i]) != -1) {
                  resolve(true);
                  return;
                }
              }
            } else {
              if (sources.includes(searchValues[i])) {
                resolve(true);
                return;
              }
            }
          }
          resolve(false);
          return;
        }

        //TODO: 判断本组件值是否匹配
        resolve(false);
      });
    }
  }
};
</script>

<style lang="less">
.tag-popover {
  .ant-popover-inner-content {
    display: flex;
    flex-direction: column;
    min-width: 140px;
  }
}
</style>
<style lang="less" scoped>
::v-deep .ant-checkbox-wrapper {
  display: block;
  margin-left: 0;
}
</style>
