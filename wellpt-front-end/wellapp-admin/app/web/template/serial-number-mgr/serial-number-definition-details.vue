<template>
  <div :loading="loading">
    <a-form-model
      class="pt-form"
      ref="form"
      :model="formData"
      :rules="rules"
      :label-col="{ span: 4 }"
      :wrapper-col="{ span: 19 }"
      :colon="false"
    >
      <a-tabs default-active-key="1" v-model="activeKey" class="pt-tabs">
        <a-tab-pane key="1" tab="基本信息">
          <a-form-model-item label="名称" prop="name">
            <a-input v-model="formData.name">
              <template slot="addonAfter">
                <w-i18n-input :target="formData" code="name" v-model="formData.name" :key="formData.uuid" />
              </template>
            </a-input>
          </a-form-model-item>
          <a-form-model-item label="ID" prop="id">
            <a-input v-model="formData.id" :disabled="!!formData.uuid" />
          </a-form-model-item>
          <a-form-model-item label="编号" prop="code">
            <a-input v-model="formData.code" />
          </a-form-model-item>
          <a-form-model-item label="分类" prop="categoryUuid">
            <a-select
              :showSearch="true"
              v-model="formData.categoryUuid"
              :options="categoryOptions"
              :filter-option="filterOption"
              :style="{ width: '100%' }"
            ></a-select>
          </a-form-model-item>
          <a-form-model-item label="所属模块" prop="moduleId">
            <a-select
              :showSearch="true"
              v-model="formData.moduleId"
              :options="moduleOptions"
              :filter-option="filterOption"
              :style="{ width: '100%' }"
            ></a-select>
          </a-form-model-item>
          <a-form-model-item label="使用人" prop="ownerIds">
            <OrgSelect v-model="formData.ownerIds" />
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">
              <a-space>
                格式
                <a-popover>
                  <template slot="content">
                    流水号由头部、计数、尾部三部分组成，其中头部、尾部可选
                    <br />
                    样例1、由头部、计数组成的流水号
                    <br />
                    <img src="/static/images/serial-number/serial-number-sample1.jpg" />
                    <br />
                    样例2、由头部、计数、尾部组成的流水号
                    <br />
                    <img src="/static/images/serial-number/serial-number-sample2.jpg" />
                  </template>
                  <a-icon type="info-circle" />
                </a-popover>
              </a-space>
            </template>
            <a-form-model-item label="头部" prop="prefix" :wrapper-col="{ span: 20 }">
              <SerialNumberVariableDefine ref="prefix" :formData="formData" prop="prefix"></SerialNumberVariableDefine>
              <!-- <a-input v-model="formData.prefix" @click="openVariableDefine('prefix')" /> -->
            </a-form-model-item>
            <a-form-model-item label="计数" :wrapper-col="{ span: 20 }" style="margin-bottom: 0">
              <a-form-model-item label="初始值" prop="initialValue" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
                <a-input-number v-model="formData.initialValue" :min="1" :precision="0" style="width: 100%" />
              </a-form-model-item>
              <a-form-model-item label="增量" prop="incremental" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
                <a-input-number v-model="formData.incremental" :min="1" :precision="0" style="width: 100%" />
              </a-form-model-item>
              <a-form-model-item prop="defaultDigits" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
                <template slot="label">
                  <a-space>
                    默认位数
                    <a-popover>
                      <template slot="content">
                        开启：计数的位数不足配置的位数时将自动补0
                        <br />
                        关闭：计数的位数按实际数值显示
                      </template>
                      <a-icon type="info-circle" />
                    </a-popover>
                  </a-space>
                </template>
                <a-switch
                  v-model="formData.enableDefaultDigits"
                  checked-children="开启"
                  un-checked-children="关闭"
                  @change="enableDefaultDigitsChange"
                ></a-switch>
                <a-input-number
                  v-show="formData.enableDefaultDigits"
                  v-model="formData.defaultDigits"
                  :min="1"
                  :precision="0"
                  style="width: calc(100% - 80px); float: right"
                />
              </a-form-model-item>
              <a-form-model-item label="尾部" prop="suffix" :wrapper-col="{ span: 20 }">
                <SerialNumberVariableDefine ref="suffix" :formData="formData" prop="suffix"></SerialNumberVariableDefine>
                <!-- <a-input v-model="formData.suffix" /> -->
              </a-form-model-item>
              <a-form-model-item label="完整格式" :wrapper-col="{ span: 20 }" style="margin-bottom: 0">{{ fullFormat }}</a-form-model-item>
            </a-form-model-item>
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">
              <a-space>
                计数重置
                <a-popover>
                  <template slot="content">
                    开启：当满足任一计数重置规则时，将自动从初始值重新开始计数
                    <br />
                    按周期重置：计数周期变化时，自动从初始值重新开始计数
                    <br />
                    按变量重置: 指定的变量值变化时，自动从初始值重新开始计数
                    <br />
                    关闭：计数不重置，一直递增
                  </template>
                  <a-icon type="info-circle" />
                </a-popover>
              </a-space>
            </template>
            <a-row>
              <a-col span="5">
                <a-switch v-model="formData.enablePointerReset" checked-children="开启" un-checked-children="关闭"></a-switch>
              </a-col>
              <a-col span="7">
                <a-select
                  v-show="formData.enablePointerReset"
                  v-model="formData.pointerResetType"
                  :options="pointerResetTypeOptions"
                  @change="pointerResetTypeChange"
                ></a-select>
              </a-col>
              <a-col span="11" offset="1">
                <div v-show="formData.enablePointerReset">
                  <a-select
                    v-show="formData.pointerResetType == '1'"
                    v-model="formData.pointerResetRule"
                    :options="pointerResetRuleOptions"
                  ></a-select>
                  <SerialNumberVariableDefine
                    v-show="formData.pointerResetType == '2'"
                    ref="pointerResetRule"
                    :formData="formData"
                    prop="pointerResetRule"
                  ></SerialNumberVariableDefine>
                  <!-- <a-input v-else v-model="formData.pointerResetRule"></a-input> -->
                </div>
              </a-col>
            </a-row>
          </a-form-model-item>
          <a-form-model-item label="新年度开始时间" prop="nextYearStartDate" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
            下一年
            <a-date-picker
              v-model="formData.nextYearStartDate"
              format="MM-DD"
              valueFormat="MM-DD"
              style="width: calc(100% - 80px); float: right"
            />
          </a-form-model-item>
          <a-form-model-item label="备注" prop="remark">
            <a-textarea v-model="formData.remark" />
          </a-form-model-item>
          <a-row v-if="!$widgetDrawerContext">
            <a-col offset="11"><a-button type="primary" @click="save">保存</a-button></a-col>
          </a-row>
        </a-tab-pane>
        <a-tab-pane v-if="formData.uuid" key="2" tab="流水号维护">
          <SerialNumberMaintain :definition="formData"></SerialNumberMaintain>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>

<script>
import moment from 'moment';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import SerialNumberVariableDefine from './component/serial-number-variable-define.vue';
import SerialNumberMaintain from './component/serial-number-maintain.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';
export default {
  components: { OrgSelect, SerialNumberVariableDefine, SerialNumberMaintain, WI18nInput },
  data() {
    return {
      activeKey: '1',
      loading: false,
      formData: this.defaultData(),
      categoryOptions: [],
      moduleOptions: [],
      rules: {
        name: { required: true, message: '不能为空', trigger: 'blur' },
        id: { required: true, message: '不能为空', trigger: 'blur' },
        initialValue: { required: true, message: '不能为空', trigger: 'blur' },
        incremental: { required: true, message: '不能为空', trigger: 'blur' }
      },
      pointerResetTypeOptions: [
        { label: '按周期重置', value: '1' },
        { label: '按变量重置', value: '2' }
      ],
      pointerResetRuleOptions: [
        { label: '按年重置', value: '10' },
        { label: '按月重置', value: '20' },
        { label: '按周重置', value: '30' },
        { label: '按日重置', value: '40' }
      ],
      variableDefine: {
        title: '表达式配置',
        visible: false,
        expression: ''
      },
      $widgetDrawerContext: undefined
    };
  },
  computed: {
    fullFormat() {
      let formData = this.formData;
      let prefix = formData.prefix || '';
      let initialValue = formData.initialValue || '';
      let suffix = formData.suffix || '';
      let fillString = '';
      let enableDefaultDigits = formData.enableDefaultDigits || false;
      let defaultDigits = formData.defaultDigits;
      if (enableDefaultDigits && defaultDigits && defaultDigits > initialValue.length) {
        for (let i = initialValue.length; i < defaultDigits; i++) {
          fillString += '0';
        }
      }
      return prefix + fillString + initialValue + suffix;
    }
  },
  created() {
    this.loadCategoryOptions();
    this.loadModuleOptions();

    let $event = this._provided && this._provided.$event;
    this.$widgetDrawerContext = this._provided && this._provided.widgetDrawerContext;
    if (this.$widgetDrawerContext) {
      if ($event && $event.meta) {
        if ($event.meta.uuid) {
          // 编辑状态不显示“保存并添加下一个”
          let buttons = [];
          this.$widgetDrawerContext.widget.configuration.footerButton.buttons.map(item => {
            let button = JSON.parse(JSON.stringify(item));
            if (button.code === 'saveAndNewNextData') {
              button.defaultVisible = false;
            }
            buttons.push(button);
          });
          this.$widgetDrawerContext.setFooterButton(buttons);
        }
      }
    }
  },
  mounted() {
    let $event = this._provided.$event;
    this.$widgetDrawerContext = this._provided && this._provided.widgetDrawerContext;
    if ($event && $event.meta) {
      if ($event.meta.uuid) {
        this.viewDetails($event);
      } else {
        this.add($event);
      }
    }
  },
  methods: {
    defaultData() {
      return {
        name: '',
        code: '',
        categoryUuid: '',
        moduleId: '',
        prefix: '',
        initialValue: '1',
        incremental: 1,
        enableDefaultDigits: true,
        defaultDigits: 3,
        suffix: '',
        enablePointerReset: false,
        pointerResetType: '1',
        pointerResetRule: '10',
        nextYearStartDate: '01-01',
        ownerIds: '',
        ownerNames: '',
        remark: ''
      };
    },
    // 保存并添加下一个
    saveAndNewNextData() {
      this.save(() => {
        this.add(this._provided.$event);
      });
    },
    loadSerialNumberDefinition(uuid) {
      const _this = this;
      _this.loading = true;
      $axios
        .get(`/proxy/api/sn/serial/number/definition/get?uuid=${uuid}`)
        .then(({ data: result }) => {
          if (result.data) {
            _this.formData = result.data;
            if (this.formData.i18ns) {
              let i18n = {};
              for (let item of this.formData.i18ns) {
                if (i18n[item.locale] == undefined) {
                  i18n[item.locale] = {};
                }
                i18n[item.locale][item.code] = item.content;
              }
              this.formData.i18n = i18n;
            }
            // for (let key in _this.formData) {
            //   _this.$set(_this.formData, key, _this.formData[key]);
            // }
            if (_this.formData.defaultDigits) {
              // this.formData.enableDefaultDigits = true;
              _this.$set(_this.formData, 'enableDefaultDigits', true);
            } else {
              // this.formData.enableDefaultDigits = false;
              _this.$set(_this.formData, 'enableDefaultDigits', false);
            }
            _this.$refs.prefix.update(_this.formData.prefix);
            _this.$refs.suffix.update(_this.formData.suffix);
            _this.$refs.pointerResetRule.update(_this.formData.pointerResetRule);
          } else {
            _this.formData = _this.defaultData();
            _this.$message.error(result.msg || '加载失败');
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
      $axios.post(`/proxy/api/sn/serial/number/category/getAllBySystemUnitIdsLikeName`, { name: '' }).then(({ data: result }) => {
        if (result.data) {
          this.categoryOptions = result.data.map(item => ({ label: item.name, value: item.uuid }));
        }
      });
    },
    loadModuleOptions() {
      const _this = this;
      $axios
        .post('/common/select2/query', {
          serviceName: 'appModuleMgr',
          queryMethod: 'loadSelectData',
          idProperty: 'id',
          includeSuperAdmin: 'true'
        })
        .then(({ data }) => {
          if (data.results) {
            _this.moduleOptions = data.results.map(item => ({ label: item.text, value: item.id }));
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
    pointerResetTypeChange(value) {
      if (value == '1') {
        if (this.pointerResetRuleOptions.findIndex(item => item.value == this.formData.pointerResetRule) == -1) {
          this.latestPointerResetRule = this.formData.pointerResetRule;
          this.formData.pointerResetRule = '10';
        }
      } else if (this.latestPointerResetRule) {
        this.formData.pointerResetRule = this.latestPointerResetRule;
        this.$refs.pointerResetRule.update(this.formData.pointerResetRule);
      }
    },
    enableDefaultDigitsChange(checked) {
      if (checked) {
        if (this.latestDefaultDigits) {
          this.formData.defaultDigits = this.latestDefaultDigits;
        }
      } else {
        this.latestDefaultDigits = this.formData.defaultDigits;
        this.formData.defaultDigits = '';
      }
    },
    openVariableDefine(prop) {
      this.variableDefine.visible = true;
    },
    add(evt) {
      this.clear();
      this.$tableWidget = evt.$evtWidget;
      this.formData = Object.assign(this.defaultData(), {
        id: 'SN_' + moment().format('yyyyMMDDHHmmss'),
        code: moment().format('yyyyMMDDHHmmss'),
        initialValue: '1',
        incremental: 1,
        enableDefaultDigits: true,
        defaultDigits: 3,
        nextYearStartDate: '01-01'
      });
    },
    clear() {
      this.activeKey = '1';
      this.formData = this.defaultData();
      this.$refs.prefix.update(this.formData.prefix);
      this.$refs.suffix.update(this.formData.suffix);
      this.$refs.pointerResetRule.update(this.formData.pointerResetRule);
    },
    viewDetails(evt) {
      this.clear();
      this.$tableWidget = evt.$evtWidget;
      this.formData = (evt.meta.uuid && evt.meta) || {};
      if (this.formData.uuid) {
        this.loadSerialNumberDefinition(this.formData.uuid);
      }
    },
    save(callback) {
      const _this = this;
      _this.$refs.form.validate(valid => {
        if (valid) {
          let formData = _this.formData;
          let item = this.formData;
          if (item.i18n) {
            let i18ns = [];
            for (let locale in item.i18n) {
              for (let key in item.i18n[locale]) {
                if (item.i18n[locale][key]) {
                  i18ns.push({
                    locale: locale,
                    content: item.i18n[locale][key],
                    code: key
                  });
                }
              }
            }
            item.i18ns = i18ns;
          }
          $axios.post('/proxy/api/sn/serial/number/definition/save', formData).then(({ data: result }) => {
            if (result.code == 0) {
              _this.$message.success('保存成功！');
              _this.clear();
              if (formData.uuid && typeof callback !== 'function') {
                _this.loadSerialNumberDefinition(formData.uuid);
              }
              if (_this.$tableWidget) {
                _this.$tableWidget.refetch(true);
              }
              if (_this.$widgetDrawerContext && typeof callback !== 'function') {
                _this.$widgetDrawerContext.close();
              }
              if (typeof callback === 'function') {
                callback();
              }
            } else {
              _this.$message.error(result.msg || '保存失败！');
            }
          });
        } else {
          this.activeKey = '1';
        }
      });
    }
  },
  META: {
    method: {
      add: '新增流水号定义',
      viewDetails: '查看流水号定义详情',
      save: '保存流水号定义',
      saveAndNewNextData: '保存并添加下一个'
    }
  }
};
</script>

<style></style>
