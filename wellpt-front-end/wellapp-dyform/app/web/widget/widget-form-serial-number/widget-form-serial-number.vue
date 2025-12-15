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
      <!-- 流水号 -->
      <a-input
        v-model="formData[widget.configuration.code]"
        :disabled="disable || isAutoGenerate()"
        :readOnly="readonly"
        v-show="!displayAsLabel"
        :placeholder="placeholder"
        @click="onClick"
        @blur="onBlur"
        @change="onChange"
      />
    </template>
    <span v-show="displayAsLabel" class="textonly" :title="displayValue()">{{ displayValue() }}</span>
    <a-modal
      class="serial-number-modal"
      :title="dialogTitle"
      :visible="serialNumberModalVisible"
      @ok="onChooseSerialNumberOk"
      @cancel="serialNumberModalVisible = false"
    >
      <a-descriptions :column="1">
        <a-descriptions-item :label="dialogSnDefSelectLabel">
          <div class="flex">
            <a-select
              :options="serialNumberDefinitionSelectOptions"
              v-model="serialNumberDefinitionSelectId"
              @change="onSerialNumberDefinitionSelectIdChange"
              class="serial-number-def-select f_g_1"
              :notFoundContent="$t('WidgetFormSerialNumber.noUsableSerialNumber', '没有可使用的流水号')"
              style="margin-right: var(--w-margin-2xs)"
            ></a-select>
            <a-button v-if="widget.configuration.serialNumberFill == 'custom'" @click="onFillSerialNumberClick">
              {{ $t('WidgetFormSerialNumber.fillNumber', '补号') }}
            </a-button>
          </div>
        </a-descriptions-item>
        <a-descriptions-item
          v-if="widget.configuration.serialNumberFill == 'custom' && serialNumberSelectOptions.length"
          :label="dialogSnSelectLabel"
        >
          <a-select :options="serialNumberSelectOptions" v-model="serialNumberSelectValue" class="serial-number-select"></a-select>
        </a-descriptions-item>
        <a-descriptions-item :label="dialogSnLabel">
          <div class="flex f_wrap">
            <div style="margin-right: 16px" v-show="serialNumberSelectRecord.prefix">
              <label class="serial-number-label">{{ serialNumberSelectRecord.prefix }}</label>
            </div>
            <div style="margin-right: 8px" v-show="pointerFormatValuePrefix">
              <label class="serial-number-label">{{ pointerFormatValuePrefix }}</label>
            </div>
            <div style="margin-right: 8px"><a-input v-model="serialNumberSelectInputPointer"></a-input></div>
            <div>
              <label class="serial-number-label">{{ serialNumberSelectRecord.suffix }}</label>
            </div>
          </div>
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </a-form-model-item>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import { isEmpty, map, each as forEach } from 'lodash';
export default {
  extends: FormElement,
  name: 'WidgetFormSerialNumber',
  mixins: [widgetMixin],
  data() {
    return {
      serialnumber: undefined,
      serialNumberModalVisible: false, // 流水号选择弹出框是否可见
      serialNumberDefinitionSelectOptions: [], // 流水号定义列表
      serialNumberDefinitionSelectId: '', // 流水号定义选择的ID
      serialNumberSelectOptions: [], // 流水号列表
      serialNumberSelectValue: '', // 流水号补号选择的记录指针
      serialNumberSelectRecord: { uuid: '', prefix: '', pointer: '', pointerFormatValue: '', subfix: '' }, //流水号补号选择的记录
      serialNumberSelectInputPointer: ''
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    placeholder() {
      const _this = this;
      if (_this.isAutoGenerate()) {
        return '';
      }
      return this.widget.configuration.placeholder
        ? this.$t('placeholder', this.widget.configuration.placeholder)
        : this.$t('WidgetFormSerialNumber.clickToEdit', '点击编辑流水号');
    },
    dialogTitle() {
      return this.widget.configuration.dialogTitle
        ? this.$t('placeholder', this.widget.configuration.dialogTitle)
        : this.$t('WidgetFormSerialNumber.editable', '可编辑流水号');
    },
    dialogSnDefSelectLabel() {
      return this.widget.configuration.dialogSnDefSelectLabel
        ? this.$t('dialogSnDefSelectLabel', this.widget.configuration.dialogSnDefSelectLabel)
        : this.$t('WidgetFormSerialNumber.serialNumberName', '流水号名称');
    },
    dialogSnSelectLabel() {
      return this.widget.configuration.dialogSnSelectLabel
        ? this.$t('dialogSnSelectLabel', this.widget.configuration.dialogSnSelectLabel)
        : this.$t('WidgetFormSerialNumber.selectable', '可选择流水号');
    },
    dialogSnLabel() {
      return this.widget.configuration.dialogSnLabel
        ? this.$t('dialogSnLabel', this.widget.configuration.dialogSnLabel)
        : this.$t('WidgetFormSerialNumber.serialNumber', '流水号');
    },
    // 指针格式化前缀
    pointerFormatValuePrefix() {
      const _this = this;
      let pointer = _this.serialNumberSelectRecord.pointer + '';
      let pointerFormatValue = _this.serialNumberSelectRecord.pointerFormatValue;
      if (isEmpty(pointer) || isEmpty(pointerFormatValue) || pointerFormatValue.indexOf(pointer) == -1) {
        return '';
      }
      return pointerFormatValue.substring(0, pointerFormatValue.indexOf(pointer));
    }
  },
  created() {
    const _this = this;
    let value = _this.getValue();
    if (!_this.designMode && isEmpty(value) && _this.isAutoGenerate() && EASY_ENV_IS_BROWSER) {
      _this.autoGenerateSerialNumberIfRequired();
    }
  },
  mounted() {},
  updated() {},
  watch: {
    serialNumberSelectValue: function (newVal, oldVal) {
      var _this = this;
      var selectRecord = {};
      // 根据选择的指针值设置对应的记录
      forEach(_this.serialNumberRecordList, function (record) {
        if (record.pointer == _this.serialNumberSelectValue) {
          selectRecord = record;
          _this.serialNumberSelectInputPointer = record.pointer;
        }
      });
      _this.serialNumberSelectRecord = selectRecord;
    }
  },
  methods: {
    afterSaveDataError(params) {
      const data = params.res.data;
      if (data.fieldName === this.fieldCode) {
        const h = this.$createElement;
        this.$confirm({
          title: this.$t('WidgetFormSerialNumber.prompt', '提示'),
          content: h('div', {}, [
            h('p', `${this.$t('WidgetFormSerialNumber.fieldName', '字段名称')}：${data.displayName}`),
            h(
              'p',
              `${data.title}，${this.$t('WidgetFormSerialNumber.isResubmit', '是否重新提交')}` ||
                `${this.$t('WidgetFormSerialNumber.error', '报错失败')}`
            )
          ]),
          onOk: () => {
            this.formData[this.fieldCode] = data.newValue;
            let option = this.getFieldDataOptions().getOptions('serialNumberConfirm');
            option = JSON.parse(option);
            option.value = data.newValue;
            option.pointer = data.newValue.substr(-2);
            this.getFieldDataOptions().putOptions('serialNumberConfirm', option);
            params.callback();
          }
        });
      }
    },
    onBlur() {
      this.emitBlur();
    },
    onChange() {
      this.emitChange();
    },
    // 可编辑流水号点击
    onClick() {
      const _this = this;
      if (_this.isAutoGenerate() || this.disable || this.readonly) {
        return;
      }
      _this.serialNumberModalVisible = true;
      // 加载流水号定义
      if (isEmpty(_this.serialNumberDefinitionSelectOptions)) {
        _this.loadSerialNumberDefinitionList();
      }
    },
    // 加载流水号定义列表
    loadSerialNumberDefinitionList() {
      const _this = this;
      let categoryUuidOrIds = _this.widget.configuration.serialNumberDefinitionId;
      if (!Array.isArray(categoryUuidOrIds)) {
        categoryUuidOrIds = [categoryUuidOrIds];
      }
      $axios
        .post('/json/data/services', {
          serviceName: 'snSerialNumberFacadeService',
          methodName: 'listByCategoryUuidOrId',
          args: JSON.stringify([categoryUuidOrIds])
        })
        .then(result => {
          _this.serialNumberDefinitionList = result.data.data;
          _this.serialNumberDefinitionSelectOptions = map(_this.serialNumberDefinitionList, function (definition) {
            return {
              label: definition.name,
              value: definition.id
            };
          });
          if (this.serialNumberDefinitionSelectOptions.length == 1) {
            this.serialNumberDefinitionSelectId = this.serialNumberDefinitionSelectOptions[0]['value'];
            this.onSerialNumberDefinitionSelectIdChange();
          }
        });
    },
    // 流水号定义选择变更
    onSerialNumberDefinitionSelectIdChange() {
      const _this = this;
      // 无选择流水号定义，清空关联选择数据
      if (isEmpty(_this.serialNumberDefinitionSelectId)) {
        _this.serialNumberSelectOptions = [];
        _this.serialNumberSelectValue = '';
        _this.serialNumberSelectRecord = {};
        return;
      }
      let configuration = _this.widget.configuration;
      // 不补号
      if (configuration.serialNumberFill == 'none') {
        _this.noFillSerialNumber();
      } else if (configuration.serialNumberFill == 'auto' || configuration.serialNumberFill == 'custom') {
        // 自动补号
        _this.autoFillSerialNumber();
      } else {
        // 点击补号按钮选择补号
      }
    },
    // 不补号
    noFillSerialNumber() {
      const _this = this;
      let params = _this.getSerialNumberBuildParams();
      params.serialNumberId = _this.serialNumberDefinitionSelectId;
      // 生成时占用
      params.occupied = _this.isAutoOccupy();
      $axios
        .post('/json/data/services', {
          serviceName: 'snSerialNumberFacadeService',
          methodName: 'generateSerialNumber',
          args: JSON.stringify([params])
        })
        .then(result => {
          var serialNumberInfo = result.data.data || {};
          _this.serialNumberRecordList = [serialNumberInfo];
          _this.serialNumberSelectRecord = serialNumberInfo;
          _this.serialNumberSelectInputPointer = serialNumberInfo.pointer;
        });
    },
    // 自动补号
    autoFillSerialNumber() {
      const _this = this;
      let params = _this.getSerialNumberBuildParams();
      params.serialNumberId = _this.serialNumberDefinitionSelectId;
      $axios
        .post('/json/data/services', {
          serviceName: 'snSerialNumberFacadeService',
          methodName: 'listAvailableSerialNumber',
          args: JSON.stringify([params])
        })
        .then(result => {
          var recordList = result.data.data;
          _this.serialNumberRecordList = recordList;
          // 自动补最小号
          if (!isEmpty(recordList)) {
            _this.serialNumberSelectRecord = recordList[0];
            _this.serialNumberSelectInputPointer = recordList[0].pointer;
          }
        });
    },
    // 补号
    onFillSerialNumberClick() {
      const _this = this;
      if (isEmpty(_this.serialNumberDefinitionSelectId)) {
        _this.$message.error(this.$t('WidgetFormSerialNumber.selectDefinition', '请选择流水号定义！'));
        return;
      }
      let params = _this.getSerialNumberBuildParams();
      params.serialNumberId = _this.serialNumberDefinitionSelectId;
      $axios
        .post('/json/data/services', {
          serviceName: 'snSerialNumberFacadeService',
          methodName: 'listAvailableSerialNumber',
          args: JSON.stringify([params])
        })
        .then(result => {
          _this.serialNumberRecordList = result.data.data;
          _this.serialNumberSelectOptions = map(_this.serialNumberRecordList, function (record) {
            return {
              label: record.serialNo,
              value: record.pointer
            };
          });
          // 清空流水号
          _this.serialNumberSelectRecord = {};
          this.serialNumberSelectInputPointer = '';
        });
    },
    // 选择流水号
    onChooseSerialNumberOk() {
      const _this = this;
      let selectRecord = _this.serialNumberSelectRecord;
      if (isEmpty(selectRecord.serialNo)) {
        _this.$message.error(this.$t('WidgetFormSerialNumber.selectSerialNumber', '请选择流水号'));
        return;
      }

      // 判断用户是否更改指针
      if (_this.isUserCustomPointer()) {
        _this.userCustomSerialNumberOk();
      } else {
        _this.serialNumberOk();
      }
    },
    // 用户更改指针的补号
    userCustomSerialNumberOk() {
      const _this = this;
      let selectRecord = _this.serialNumberSelectRecord;
      var pointer = parseInt(_this.serialNumberSelectInputPointer);
      if (!pointer) {
        _this.$message.error(this.$t('WidgetFormSerialNumber.enterPointer', '请输入有效的指针!'));
        return;
      }

      let params = _this.getSerialNumberBuildParams();
      params.snValue = selectRecord.serialNo;
      params.uuid = selectRecord.maintainUuid;
      params.serialNumberId = _this.serialNumberDefinitionSelectId;
      $axios
        .post('/json/data/services', {
          serviceName: 'snSerialNumberFacadeService',
          methodName: 'generateSerialNumberWithPointer',
          args: JSON.stringify([pointer, params])
        })
        .then(result => {
          let serialNumberInfo = result.data.data;
          if (serialNumberInfo == null) {
            _this.$message.error(this.$t('WidgetFormSerialNumber.pointerOccupied', '该指针无效或已经被占用!'));
          } else {
            _this.setValue(serialNumberInfo.serialNo);
            let serialnumber = {
              snid: _this.serialNumberDefinitionSelectId, //流水号定义ID
              headPart: serialNumberInfo.prefix,
              pointer: serialNumberInfo.pointer,
              lastPart: serialNumberInfo.suffix,
              value: serialNumberInfo.serialNo,
              occupied: false,
              automaticNumberSupplement: params.automaticNumberSupplement,
              uuid: serialNumberInfo.maintainUuid || '', // 流水号维护UUID
              recordUuid: serialNumberInfo.recordUuid || '', // 流水号记录UUID，补号时传入对应的记录UUID
              occupiedPointer: pointer
            };
            let dyformDataOptions = _this.getFieldDataOptions();
            _this.serialnumber = serialnumber;
            dyformDataOptions.putOptions('serialNumberConfirm', JSON.stringify(serialnumber));

            _this.serialNumberModalVisible = false;
          }
        })
        .catch(error => {
          if (error.response.data && error.response.data.msg) {
            if (error.response.status === 417) {
              _this.$message.error(this.$t('WidgetFormSerialNumber.alreadyHasSerialNumber', error.response.data.msg));
            } else {
              _this.$message.error(error.response.data.msg);
            }
          } else {
            _this.$message.error(this.$t('WidgetFormSerialNumber.requestException', '服务请求异常！'));
          }
        });
    },
    // 用户选择的补号
    serialNumberOk() {
      const _this = this;
      let configuration = _this.widget.configuration;
      let selectRecord = _this.serialNumberSelectRecord;
      let requiredRemoteCheck = true;
      // 生成时占用
      if (_this.isAutoOccupy()) {
        // 不补号
        if (configuration.serialNumberFill == 'none') {
          requiredRemoteCheck = false;
        } else if (configuration.serialNumberFill == 'auto') {
          // 自动补号
          requiredRemoteCheck = false;
        } else {
          // 点击补号按钮选择补号
        }
      } else {
        // 保存时占用
        // 不补号
        if (configuration.serialNumberFill == 'none') {
          requiredRemoteCheck = true;
        } else if (configuration.serialNumberFill == 'auto') {
          // 自动补号
          requiredRemoteCheck = true;
        } else {
          // 点击补号按钮选择补号
        }
      }

      var checkOk = function () {
        _this.setValue(selectRecord.serialNo);
        let serialnumber = {
          snid: _this.serialNumberDefinitionSelectId, //流水号定义ID
          headPart: selectRecord.prefix,
          pointer: selectRecord.pointer,
          lastPart: selectRecord.suffix,
          value: selectRecord.serialNo,
          occupied: false,
          automaticNumberSupplement: _this.getSerialNumberBuildParams().automaticNumberSupplement,
          uuid: selectRecord.maintainUuid || '', // 流水号维护UUID
          recordUuid: selectRecord.recordUuid || selectRecord.uuid || '', // 流水号记录UUID，补号时传入对应的记录UUID
          occupiedPointer: selectRecord.skip ? selectRecord.pointer : '' // 跳号记录，占用指针
        };
        let dyformDataOptions = _this.getFieldDataOptions();
        _this.serialnumber = serialnumber;
        dyformDataOptions.putOptions('serialNumberConfirm', JSON.stringify(serialnumber));

        _this.serialNumberModalVisible = false;
      };

      // 保存时占用检查是否已经被占用
      if (requiredRemoteCheck) {
        let params = _this.getSerialNumberBuildParams();
        params.snValue = selectRecord.serialNo;
        params.uuid = selectRecord.maintainUuid;
        params.serialNumberId = _this.serialNumberDefinitionSelectId;
        $axios
          .post('/json/data/services', {
            serviceName: 'snSerialNumberFacadeService',
            methodName: 'checkIsOccupied',
            args: JSON.stringify([selectRecord.pointer, params])
          })
          .then(result => {
            let isOccupied = result.data.data;
            if (isOccupied == true) {
              _this.$message.error(this.$t('WidgetFormSerialNumber.serialNumberOccupied', '该流水号已经被占用'));
            } else {
              checkOk(false);
            }
          });
      } else {
        checkOk(true);
      }
    },
    isUserCustomPointer() {
      return this.serialNumberSelectRecord.pointer != this.serialNumberSelectInputPointer;
    },
    // 是否自动生成
    isAutoGenerate() {
      return this.widget.configuration.generateMode == 'auto';
    },
    // 是否生成时占用
    isAutoOccupy() {
      // 不补号才有生成时占用，其他都为保存时占用
      return this.widget.configuration.serialNumberFill == 'none' && this.widget.configuration.occupyMode == 'auto';
    },
    // 生成流水号
    autoGenerateSerialNumberIfRequired() {
      const _this = this;
      let params = _this.getSerialNumberBuildParams();
      // 生成时占用
      params.occupied = _this.isAutoOccupy();
      if (this.configuration.occupyMode == 'auto') {
        params.occupied = true;
      }
      if (Array.isArray(params.serialNumberId)) {
        if (params.serialNumberId.length) {
          params.serialNumberId = params.serialNumberId[0];
        } else {
          params.serialNumberId = '';
        }
      }
      $axios
        .post('/json/data/services', {
          serviceName: 'snSerialNumberFacadeService',
          methodName: 'generateSerialNumber',
          args: JSON.stringify([params])
        })
        .then(result => {
          // 表单数据异步加载时有设置值不更新
          var value = _this.getValue();
          var serialNumberInfo = result.data.data || {};
          if (isEmpty(value)) {
            _this.setValue(serialNumberInfo.serialNo);

            let serialnumber = {
              snid: _this.widget.configuration.serialNumberDefinitionId, //流水号定义ID
              headPart: serialNumberInfo.prefix,
              pointer: serialNumberInfo.pointer,
              lastPart: serialNumberInfo.suffix,
              value: serialNumberInfo.serialNo,
              occupied: params.occupied,
              automaticNumberSupplement: params.automaticNumberSupplement,
              uuid: serialNumberInfo.maintainUuid || '', // 流水号维护UUID
              recordUuid: serialNumberInfo.recordUuid || '' // 占用的流水号记录UUID
            };
            let dyformDataOptions = _this.getFieldDataOptions();
            _this.serialnumber = serialnumber;
            dyformDataOptions.putOptions('serialNumberConfirm', JSON.stringify(serialnumber));
          }
        });
    },
    getSerialNumberBuildParams() {
      const _this = this;
      var configuration = _this.widget.configuration;
      return {
        serialNumberId: configuration.serialNumberDefinitionId,
        formUuid: _this.dyform.formUuid,
        dataUuid: _this.formData.uuid || '',
        formField: configuration.code,
        occupied: false,
        automaticNumberSupplement: configuration.serialNumberFill == 'auto'
      };
    },
    setValue(value) {
      this.form.formData[this.fieldCode] = value;
      this.emitChange();
    }
  }
};
</script>
<style scoped>
.serial-number-modal >>> .ant-descriptions-item-label {
  width: 100px;
}
.serial-number-modal >>> .ant-descriptions-item-content {
  width: 360px;
  .serial-number-def-select {
    max-width: 240px;
  }
}
/* .serial-number-modal .serial-number-def-select, */
.serial-number-modal .serial-number-select {
  width: 285px;
}
.serial-number-modal .serial-number-label {
  line-height: 32px;
}
.serial-number-modal >>> .ant-descriptions-row > td {
  display: flex;
  align-items: center;
}
</style>
