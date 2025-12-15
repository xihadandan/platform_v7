<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
    <a-form-model class="pt-form" ref="form" :model="formData" :rules="rules" :label-col="{ span: 4 }" :wrapper-col="{ span: 18 }">
      <a-form-model-item label="名称" prop="name">
        <a-input v-model="formData.name" :maxLength="100" :disabled="disabled" />
      </a-form-model-item>
      <a-form-model-item label="ID" prop="id">
        <a-input v-model="formData.id" :maxLength="50" :disabled="disabled" />
      </a-form-model-item>
      <a-form-model-item label="编号" prop="code">
        <a-input v-model="formData.code" :maxLength="50" :disabled="disabled" />
      </a-form-model-item>
      <a-form-model-item label="分类" prop="categoryUuid">
        <a-select
          :disabled="disabled"
          :showSearch="true"
          v-model="formData.categoryUuid"
          :options="categoryOptions"
          :filter-option="filterOption"
          :style="{ width: '100%' }"
        ></a-select>
      </a-form-model-item>
      <a-form-model-item prop="timingModeType">
        <template slot="label">
          <a-space>
            计时方式
            <a-popover>
              <template slot="content">
                <strong>工作日：</strong>
                根据工作时间中的工作日，以及每个工作日的工作时段进行计时
                <br />
                <strong>工作日（一天24小时）：</strong>
                根据工作时间中的工作日，每个工作日固定按24小时进行计时
                <br />
                <strong>自然日：</strong>
                根据自然日，每天固定按24小时进行计时
              </template>
              <a-icon type="info-circle" />
            </a-popover>
          </a-space>
        </template>
        <a-select
          :disabled="disabled"
          :showSearch="true"
          v-model="formData.timingModeType"
          :options="timingModeTypeOptions"
          :filter-option="filterOption"
          :style="{ width: '100%' }"
        ></a-select>
        <div>
          启动后
          <a-radio v-model="formData.startDelay" @change="onStartDelayChange" :disabled="disabled">从下一</a-radio>
          <a-select
            :disabled="disabled"
            :showSearch="true"
            v-model="formData.timingModeUnit"
            :options="timingModeUnitOptions"
            :filter-option="filterOption"
            :style="{ width: '150px' }"
          ></a-select>
          开始计时
          <a-radio v-model="formData.startRightNow" @change="onStartRightNowChange" :disabled="disabled">立即开始计时</a-radio>
          <br />
          <div v-show="formData.timingModeType == '1' || formData.timingModeType == '2'">
            到期时间在非工作时间时
            <a-switch v-model="formData.autoDelay" :disabled="disabled"></a-switch>
            <span v-show="formData.autoDelay">自动推迟到下一工作时间的起始点前</span>
          </div>
        </div>
      </a-form-model-item>
      <a-form-model-item label="时限类型" prop="timeLimitType">
        <a-select
          :disabled="disabled"
          :showSearch="true"
          v-model="formData.timeLimitType"
          :options="timeLimitTypeOptions"
          :filter-option="filterOption"
          :style="{ width: '100%' }"
        ></a-select>
      </a-form-model-item>
      <div v-if="formData.timeLimitType == '10'">
        <a-form-model-item label="时限" prop="timeLimit" :rules="timeLimitUnitRule1">
          <a-input-number v-model="formData.timeLimit" :min="0" :precision="1" style="width: 65%" :disabled="disabled" />
          <a-select
            :disabled="disabled"
            :showSearch="true"
            v-model="formData.timeLimitUnit"
            :options="timeLimitUnitOptions1"
            :filter-option="filterOption"
            :style="{ width: '35%' }"
          ></a-select>
        </a-form-model-item>
      </div>
      <div v-if="formData.timeLimitType == '20'">
        <a-form-model-item label="时限单位" prop="timeLimitUnit" :rules="timeLimitUnitRule2">
          <a-select
            :disabled="disabled"
            :showSearch="true"
            v-model="formData.timeLimitUnit"
            :options="timeLimitUnitOptions2"
            :filter-option="filterOption"
          ></a-select>
        </a-form-model-item>
        <a-form-model-item label="截止时间" prop="timeLimit" :rules="timeLimitRule">
          <a-date-picker v-model="formData.timeLimit" format="YYYY-MM-DD HH:mm" valueFormat="YYYY-MM-DD HH:mm" :disabled="disabled" />
        </a-form-model-item>
      </div>
      <a-form-model-item label="备注" prop="remark">
        <a-textarea v-model="formData.remark" :maxLength="500" :disabled="disabled" />
      </a-form-model-item>
    </a-form-model>
  </a-skeleton>
</template>

<script>
import moment from 'moment';

const timingModeOptions = [
  [3, 2, 1],
  [13, 12, 11],
  [86400, 3600, 60]
];

export default {
  inject: ['pageContext', '$event', 'vPageState'],
  props: {
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data() {
    let $event = this.$event || {};
    let formData = $event.meta || {};
    let uuid = formData.uuid;
    if (!uuid) {
      formData = {
        id: 'TS_' + moment().format('yyyyMMDDHHmmss'),
        timingModeType: '1',
        timingModeUnit: '1',
        startDelay: true,
        startRightNow: false,
        includeStartTimePoint: false,
        timeLimitType: '10',
        timeLimitUnit: '1'
      };
    }
    return {
      loading: !!uuid,
      uuid,
      formData,
      rules: {
        name: { required: true, message: '不能为空', trigger: ['blur', 'change'] },
        id: { required: true, message: '不能为空', trigger: ['blur', 'change'] },
        timingModeType: { required: true, message: '不能为空', trigger: ['change'] },
        timeLimitType: { required: true, message: '不能为空', trigger: ['change'] }
      },
      timeLimitUnitRule1: {
        required: true,
        message: '不能为空',
        trigger: ['blur', 'change']
      },
      timeLimitUnitRule2: {
        required: true,
        message: '不能为空',
        trigger: ['blur', 'change']
      },
      timeLimitRule: {
        required: true,
        message: '不能为空',
        trigger: ['change']
      },
      categoryOptions: [],
      timingModeTypeOptions: [
        { value: '1', label: '工作日' },
        { value: '2', label: '工作日（一天24小时）' },
        { value: '3', label: '自然日' }
      ],
      timingModeUnitOptions1: [
        { value: '1', label: '工作日' },
        { value: '2', label: '工作小时' },
        { value: '3', label: '工作分钟' }
      ],
      timingModeUnitOptions2: [
        { value: '1', label: '工作日' },
        { value: '2', label: '小时（工作日）' },
        { value: '3', label: '分钟（工作日）' }
      ],
      timingModeUnitOptions3: [
        { value: '1', label: '天' },
        { value: '2', label: '小时' },
        { value: '3', label: '分钟' }
      ],
      timeLimitTypeOptions: [
        { value: '10', label: '固定时限' },
        { value: '20', label: '固定截止时间' },
        { value: '30', label: '动态时限' },
        { value: '40', label: '动态截止时间' }
      ],
      timeLimitUnitOptions1: [
        { value: '1', label: '天' },
        { value: '2', label: '小时' },
        { value: '3', label: '分钟' }
      ],
      timeLimitUnitOptions2: [
        { value: '1', label: '天（日期 2000-01-01）' },
        { value: '2', label: '小时（日期到时 2000-01-01 12）' },
        { value: '3', label: '分钟（日期到分 2000-01-01 12:00）' }
      ]
    };
  },
  computed: {
    timingModeUnitOptions() {
      if (this.formData.timingModeType == '2') {
        return this.timingModeUnitOptions2;
      } else if (this.formData.timingModeType == '3') {
        return this.timingModeUnitOptions3;
      }
      return this.timingModeUnitOptions1;
    }
  },
  created() {
    if (this.uuid) {
      this.loadFormData();
    }
    this.loadCategoryOptions();
  },
  methods: {
    loadFormData() {
      const _this = this;
      $axios
        .get(`/api/ts/timer/config/get?uuid=${this.uuid}`)
        .then(({ data: result }) => {
          if (result.data) {
            let formData = result.data;
            formData.startDelay = !formData.includeStartTimePoint;
            formData.startRightNow = formData.includeStartTimePoint;
            _this.formData = formData;
          } else {
            _this.$message.error(result.msg || '加载失败！');
          }
        })
        .catch(({ response }) => {
          _this.$message.error((response.data && response.data.msg) || '服务异常！');
        })
        .finally(() => {
          _this.loading = false;
        });
    },
    loadCategoryOptions() {
      $axios.post(`/api/ts/timer/category/getAllBySystemUnitIdsLikeName`, { name: '' }).then(({ data: result }) => {
        if (result.data) {
          this.categoryOptions = result.data.map(item => ({ label: item.name, value: item.uuid }));
        }
      });
    },
    filterOption(inputValue, option) {
      return (
        (option.componentOptions.propsData.value &&
          option.componentOptions.propsData.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0) ||
        (option.componentOptions.children[0] &&
          option.componentOptions.children[0].text.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0)
      );
    },
    onStartDelayChange(e) {
      this.formData.startRightNow = !this.formData.startDelay;
      this.formData.includeStartTimePoint = !this.formData.startDelay;
    },
    onStartRightNowChange(e) {
      this.formData.startDelay = !this.formData.startRightNow;
      this.formData.includeStartTimePoint = this.formData.startRightNow;
    },
    save(evt) {
      const _this = this;
      _this.$refs.form.validate(valid => {
        if (valid) {
          let formData = _this.formData;
          let timingModeType = formData.timingModeType || '1';
          let timingModeUnit = formData.timingModeUnit || '1';
          let timingModeArray = timingModeOptions[parseInt(timingModeType) - 1];
          formData.timingMode = timingModeArray[parseInt(timingModeUnit) - 1];
          $axios.post('/api/ts/timer/config/save', formData).then(({ data: result }) => {
            if (result.code == 0) {
              _this.$message.success('保存成功！');
              _this.pageContext.emitEvent('rFeTZkaBczwAXkkQjIIHWGGIjChPooqv:refetch');
              evt.$evtWidget.closeModal();
            } else {
              _this.$message.error(result.msg || '保存失败！');
            }
          });
        }
      });
    }
  },
  META: {
    method: {
      save: '保存计时服务配置'
    }
  }
};
</script>

<style></style>
