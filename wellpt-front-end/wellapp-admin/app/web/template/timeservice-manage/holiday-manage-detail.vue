<template>
  <div>
    <a-form-model class="pt-form" :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="formRules" ref="form">
      <a-form-model-item label="名称" prop="name">
        <a-input v-model="form.name" allow-clear />
      </a-form-model-item>
      <a-form-model-item label="id" prop="id">
        <a-input v-model="form.id" allow-clear />
      </a-form-model-item>
      <a-form-model-item label="历法类型">
        <a-radio-group v-model="form.calendarType">
          <a-radio value="1" name="calendarType">阳历</a-radio>
          <a-radio value="2" name="calendarType">阴历</a-radio>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="日期" prop="holidayDate">
        <a-cascader
          :options="dateOptions"
          v-model="holidayDate"
          placeholder="请选择日期"
          :fieldNames="fieldNames"
          @change="holidayDateChange"
          :getPopupContainer="getPopupContainerByPs()"
          :popupClassName="getDropdownClassName()"
        >
          <template slot="displayRender" slot-scope="{ labels }">
            {{ labels.join('') }}
          </template>
        </a-cascader>
      </a-form-model-item>
      <a-form-model-item label=" " :colon="false">
        <template v-for="tag in tags">
          <a-checkable-tag :key="tag" :checked="selectedTags.indexOf(tag) > -1" @change="checked => tagChange(tag, checked)">
            {{ tag }}
          </a-checkable-tag>
        </template>
      </a-form-model-item>
      <a-form-model-item label="描述">
        <QuillEditor v-model="form.remark" ref="quillEditor" :hiddenButtons="[]" />
      </a-form-model-item>
    </a-form-model>
  </div>
</template>

<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import QuillEditor from '@pageAssembly/app/web/lib/quill-editor';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
import { getDateData } from '../common/constant';
import { map, assignIn } from 'lodash';
export default {
  name: 'HolidayManageDetail',
  props: {
    uuid: String,
    displayState: {
      type: String,
      default: 'edit' // edit、label
    }
  },
  components: { QuillEditor },
  inject: ['pageContext', 'vPageState', '$event'],
  data() {
    return {
      $evtWidget: undefined,
      $dialogWidget: undefined,
      form: {
        calendarType: '1'
      },
      labelCol: { span: 4 },
      wrapperCol: { span: 18 },
      rules: {
        name: { required: true, message: '名称必填', trigger: ['blur', 'change'] },
        id: { required: true, message: 'id必填', trigger: ['blur', 'change'] },
        holidayDate: { required: true, trigger: ['blur', 'change'], validator: this.validateHolidayDate }
      },
      tags: [],
      holidayDate: [],
      selectedTags: [],
      dateData: [],
      fieldNames: {
        label: 'name',
        value: 'id',
        children: 'children'
      },
      wTemplate: {
        $options: {
          methods: {
            saveForm: this.saveForm
          },
          META: {
            method: {
              saveForm: '保存表单'
            }
          }
        }
      }
    };
  },
  META: {
    method: {
      saveForm: '保存表单'
    }
  },
  computed: {
    formRules() {
      let rules = assignIn({}, this.rules);
      return rules;
    },
    dateOptions() {
      if (this.form.calendarType == '1') {
        return this.dateData.yang.data;
      } else {
        return this.dateData.ying.data;
      }
    }
  },
  watch: {},
  beforeCreate() {},
  created() {
    this.getDataDictionariesByTypeCode();
    this.dateData = this.getDateData();
  },
  beforeMount() {
    let _this = this;
  },
  mounted() {
    let $event = this._provided.$event;
    this.$evtWidget = $event && $event.$evtWidget;
    this.$dialogWidget = this._provided && this._provided.dialogContext;
    if ($event && $event.eventParams) {
      this.form.uuid = $event.eventParams.uuid || '';
    }
    if (this.form.uuid) {
      this.getFormData();
    }
  },
  methods: {
    getDateData,
    getPopupContainerByPs,
    getDropdownClassName,
    getFormData() {
      let _this = this;
      $axios
        .get('/api/ts/holiday/get', {
          params: {
            uuid: this.form.uuid
          }
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            _this.form = data.data;
            _this.holidayDate = data.data.holidayDate ? data.data.holidayDate.split('-') : [];
            _this.selectedTags = data.data.tag ? data.data.tag.split(';') : [];
            _this.$nextTick(() => {
              if (_this.$refs.quillEditor) {
                _this.$refs.quillEditor.setHtml(data.data.remark);
              }
            });
          }
        });
    },
    getDataDictionariesByTypeCode() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'dataDictionaryService',
          methodName: 'getDataDictionariesByTypeCode',
          args: JSON.stringify(['computer_rank', '']),
          validate: false
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            _this.tags = map(data.data, 'name');
          }
        });
    },
    holidayDateChange(value, selectedOptions) {
      if (value && value.length == 2) {
        this.form.holidayDate = value.join('-');
        this.form.holidayDateName = selectedOptions[0].name + selectedOptions[1].name;
      } else {
        this.form.holidayDate = '';
        this.form.holidayDateName = '';
      }
    },
    tagChange(tag, checked) {
      if (checked && this.selectedTags.indexOf(tag) == -1) {
        this.selectedTags.push(tag);
      } else if (!checked && this.selectedTags.indexOf(tag) > -1) {
        this.selectedTags.splice(this.selectedTags.indexOf(tag), 1);
      }
    },
    validateHolidayDate(rule, value, callback) {
      if (!this.form.holidayDate) {
        callback('日期必填');
        return false;
      }
      callback();
    },
    saveForm() {
      console.log('保存表单');
      this.$refs.form.validate(valid => {
        if (valid) {
          this.beforeSaveReq();
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    beforeSaveReq() {
      let bean = JSON.parse(JSON.stringify(this.form));
      bean.tag = this.selectedTags.join(';');
      bean.remark = this.$refs.quillEditor.getHtml();
      this.saveFormData(bean);
    },
    saveFormData(bean) {
      let _this = this;
      $axios
        .post('/api/ts/holiday/save', bean)
        .then(({ data }) => {
          if (data.code == 0) {
            this.$message.success('保存成功');
            if (this.$evtWidget) {
              let options = this.$evtWidget.getDataSourceProvider().options;
              this.$evtWidget.refetch && this.$evtWidget.refetch(options);
            }
            if (this.$dialogWidget) {
              this.$dialogWidget.close();
            }
          } else {
            this.$message.error(data.msg || '保存失败');
          }
        })
        .catch(err => {
          let data = err && err.response && err.response.data;
          this.$message.error(data.msg || '保存失败');
        });
    }
  }
};
</script>
