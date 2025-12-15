<template>
  <view class="w-form-job-select">
    <uni-forms-item
      :label="itemLabel"
      :name="formModelItemProp"
      :label-position="widget.configuration.labelPosition"
      :class="widgetClass"
      :style="itemStyle"
      :ref="fieldCode"
    >
      <template v-if="!displayAsLabel">
        <uni-w-data-select
          class="job-data-select"
          :clear="!widget.configuration.required"
          :disabled="disable || readonly"
          :localdata="formatOptions"
          :bordered="bordered"
          :dropType="dropType"
          v-model="value"
          @change="onSelectChange"
        />
      </template>
      <view v-else class="textonly">{{ labelValueMap[value] }}</view>
    </uni-forms-item>
  </view>
</template>

<script>
import formElement from "../w-dyform/form-element.mixin";
export default {
  mixins: [formElement],
  data() {
    if (!this.widget.configuration.hasOwnProperty("uniConfiguration")) {
      this.$set(this.widget.configuration, "uniConfiguration", { dropType: "picker", bordered: false });
    }
    return {
      options: [],
      labelValueMap: {},
      value: undefined,
      dropType: this.widget.configuration.uniConfiguration.dropType,
      bordered: this.widget.configuration.uniConfiguration
        ? this.widget.configuration.uniConfiguration.bordered
        : false,
    };
  },
  computed: {
    formatOptions() {
      return (this.options || []).map((item) => {
        return {
          text: item.label,
          value: item.value,
        };
      });
    },
  },
  created() {
    if (!this.widget.configuration.hasOwnProperty("uniConfiguration")) {
      this.$set(this.widget.configuration, "uniConfiguration", { dropType: "picker", bordered: false });
    }
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
      this.emitChange();
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
          let eleNamePath = mainJob.localEleNamePath || mainJob.eleNamePath;
          let label = full ? eleNamePath : eleNamePath.substring(eleNamePath.lastIndexOf("/") + 1);
          this.options.push({
            value: mainJob.eleId,
            label,
          });
          this.labelValueMap[mainJob.eleId] = label;
          this._setValue(mainJob.eleId); // 默认主职
        }
        if (otherJobs && otherJobs.length) {
          for (let i = 0, len = otherJobs.length; i < len; i++) {
            let eleNamePath = otherJobs[i].localEleNamePath || otherJobs[i].eleNamePath;
            let label = full ? eleNamePath : eleNamePath.substring(eleNamePath.lastIndexOf("/") + 1);
            this.options.push({
              value: otherJobs[i].eleId,
              label,
            });
            this.labelValueMap[otherJobs[i].eleId] = label;
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
