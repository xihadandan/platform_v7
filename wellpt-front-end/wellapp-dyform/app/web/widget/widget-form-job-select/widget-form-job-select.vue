<template>
  <a-form-model-item
    :style="itemStyle"
    :prop="formModelItemProp"
    :rules="rules"
    :ref="widget.configuration.code"
    :label="itemLabel"
    :colon="displayAsLabel"
    :class="widgetClass"
  >
    <template v-if="!displayAsLabel">
      <a-select
        :disabled="disable || readonly"
        v-model="value"
        :allowClear="!widget.configuration.required"
        :style="{ width: '100%', maxWidth: '1920px' }"
        :options="options"
        @change="onSelectChange"
        :getPopupContainer="getPopupContainerNearestPs()"
        :dropdownClassName="getDropdownClassName()"
      ></a-select>
    </template>
    <span v-show="displayAsLabel" class="textonly" :title="labelValueMap[value]">{{ labelValueMap[value] }}</span>
  </a-form-model-item>
</template>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import formCommonMixin from '../mixin/form-common.mixin';
import { getPopupContainerNearestPs, getDropdownClassName } from '@framework/vue/utils/function.js';
export default {
  extends: FormElement,
  name: 'WidgetFormJobSelect',
  mixins: [widgetMixin, formCommonMixin],
  data() {
    return {
      options: [],
      labelValueMap: {},
      value: undefined
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    jobIdScopes() {
      let ids = [];
      for (let opt of this.options) {
        ids.push(opt.value);
      }
      return ids;
    }
  },
  methods: {
    getPopupContainerNearestPs,
    getDropdownClassName,
    onSelectChange(val, opt) {
      this._setValue(val);
      this.setDisplayValueField();
    },
    setValue(val) {
      // 避免职位控件被表单数据初始化回填修改
      // console.log('unsupported method');
    },
    _setValue(val) {
      this.value = val;
      this.form.formData[this.fieldCode] = val;
      this.setDisplayValueField();
      this.emitChange();
    },
    setDisplayValueField() {
      if (this.widget.configuration.displayValueField) {
        // 设置关联显示值字段
        this.form.setFieldValue(this.widget.configuration.displayValueField, this.labelValueMap[this.value]);
      }
    },

    fetchSelectOptions() {
      let user = this._$USER;
      let full = this.widget.configuration.jobDisplayPattern === 'full';
      if (user && user.userSystemOrgDetails && user.userSystemOrgDetails.details.length) {
        let details = user.userSystemOrgDetails.details,
          currentSysOrgDetail = details[0];
        for (let i = 0, len = details.length; i < len; i++) {
          if (details[i].system == this._$SYSTEM_ID) {
            currentSysOrgDetail = details[i];
            break;
          }
        }
        let { mainJob, otherJobs } = currentSysOrgDetail;
        if (mainJob) {
          let eleNamePath = mainJob.localEleNamePath || mainJob.eleNamePath;
          let label = full ? eleNamePath : eleNamePath.substring(eleNamePath.lastIndexOf('/') + 1);
          this.options.push({
            value: mainJob.eleId,
            label,
            mainJob: true
          });
          this.labelValueMap[mainJob.eleId] = label;
          // this._setValue(mainJob.eleId); // 默认主职
        }
        if (otherJobs && otherJobs.length) {
          for (let i = 0, len = otherJobs.length; i < len; i++) {
            let eleNamePath = otherJobs[i].localEleNamePath || otherJobs[i].eleNamePath;
            let label = full ? eleNamePath : eleNamePath.substring(eleNamePath.lastIndexOf('/') + 1);
            this.options.push({
              value: otherJobs[i].eleId,
              label
            });
            this.labelValueMap[otherJobs[i].eleId] = label;
          }
          // if (!mainJob) {
          //   this._setValue(otherJobs[0].eleId);
          // } else {
          //   this.setDisplayValueField();
          // }
        }
      }
      return Promise.resolve(this.options);
    },
    fetchSelectOptionByJobId(jobId) {
      let full = this.widget.configuration.jobDisplayPattern === 'full';
      let url = full
        ? '/proxy/api/org/organization/element/getNamePathByOrgEleIds'
        : '/proxy/api/org/organization/element/getNameByOrgEleIds';
      $axios.post(url, [jobId]).then(({ data: result }) => {
        if (result.data) {
          let idNameMap = result.data;
          for (let id in idNameMap) {
            this.options.push({ label: idNameMap[id], value: id });
            this.labelValueMap[id] = idNameMap[id];
          }
          this._setValue(jobId); // 默认主职
        }
      });
    },
    // 显示值
    displayValue() {
      return this.labelValueMap[this.form.formData[this.fieldCode]];
    }
  },

  mounted() {
    let jobId = this.form.formData[this.fieldCode];
    if (!this.designMode) {
      this.$watch('formData.' + this.fieldCode, val => {
        // 职位组件数据变化避免被任意修改
        if (val != undefined && !this.jobIdScopes.includes(val) && val !== this.value) {
          this._setValue(this.value);
        }
      });
      // 职位已有值，不再跟随当前用户变化
      if (jobId) {
        if (this.displayAsLabel) {
          this.fetchSelectOptionByJobId(jobId);
        } else {
          // 编辑状态下可取当前用户职位信息作为选项
          this.fetchSelectOptions().then(options => {
            let jobOption = options.find(option => option.value === jobId);
            if (jobOption) {
              this._setValue(jobOption.value);
            } else {
              this.fetchSelectOptionByJobId(jobId);
            }
          });
        }
      } else {
        this.fetchSelectOptions().then(options => {
          if (options && options.length > 0) {
            let mainJobOption = options.find(option => option.mainJob);
            if (mainJobOption) {
              this._setValue(mainJobOption.value);
            } else {
              this._setValue(options[0].value);
            }
          }
        });
      }
    }
  }
};
</script>
