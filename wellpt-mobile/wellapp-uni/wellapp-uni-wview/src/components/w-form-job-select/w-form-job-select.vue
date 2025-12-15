<template>
  <uni-forms-item :label="itemLabel" :name="formModelItemProp" :label-position="widget.configuration.labelPosition">
    <template v-if="!displayAsLabel">
      <uni-data-select
        :clear="!widget.configuration.required"
        :disabled="disable || readonly"
        :localdata="formatOptions"
        v-model="value"
        @change="onSelectChange"
      />
    </template>
    <view v-else>{{ labelValueMap[value] }}</view>
  </uni-forms-item>
</template>

<script>
import formElement from "../w-dyform/form-element.mixin";
export default {
  mixins: [formElement],
  data() {
    return {
      options: [],
      labelValueMap: {},
      value: undefined,
    };
  },
  computed: {
    formatOptions() {
      return this.options.map((item) => {
        return {
          text: item.label,
          value: item.value,
        };
      });
    },
  },
  mounted() {
    if (!this.designMode) {
      this.fetchSelectOptions();
    }
  },
  methods: {
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
      this.formData[this.fieldCode] = val;
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
      let full = this.widget.configuration.jobDisplayPattern === "full";
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
          this.options.push({
            value: mainJob.eleId,
            label: full ? mainJob.eleNamePath : mainJob.eleName,
          });
          this.labelValueMap[mainJob.eleId] = full ? mainJob.eleNamePath : mainJob.eleName;
          this._setValue(mainJob.eleId); // 默认主职
        }
        if (otherJobs && otherJobs.length) {
          for (let i = 0, len = otherJobs.length; i < len; i++) {
            this.options.push({
              value: otherJobs[i].eleId,
              label: full ? otherJobs[i].eleNamePath : otherJobs[i].eleName,
            });
            this.labelValueMap[otherJobs[i].eleId] = full ? otherJobs[i].eleNamePath : otherJobs[i].eleName;
          }
          if (!mainJob) {
            this._setValue(otherJobs[0].eleId);
          } else {
            this.setDisplayValueField();
          }
        }
      }
    },
    // 显示值
    displayValue() {
      return this.labelValueMap[this.formData[this.fieldCode]];
    },
  },
};
</script>
