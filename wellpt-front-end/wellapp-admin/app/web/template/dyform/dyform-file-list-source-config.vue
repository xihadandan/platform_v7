<template>
  <div>
    <a-space>
      <a-button type="primary" icon="plus" @click="add">新增</a-button>
      <a-button type="danger" icon="delete" @click="remove">删除</a-button>
      <a-button type="default" icon="arrow-up" @click="moveUp">上移</a-button>
      <a-button type="default" icon="arrow-down" @click="moveDown">下移</a-button>
      <span style="color: var(--w-text-color-light)">通过移动调整顺序，对应附件控件中显示的附件来源顺序</span>
    </a-space>
    <p />
    <Scroll style="height: calc(100vh - 290px); margin-right: -20px; padding-right: 12px">
      <a-form-model ref="form" :model="formData">
        <a-table
          rowKey="id"
          :pagination="false"
          :bordered="false"
          :columns="fileListSourceTableColumns"
          :locale="locale"
          :data-source="dataSource"
          :row-selection="{ selectedRowKeys: selectedRowKeys, selectedRows: selectedRows, onChange: onSelectChange }"
          :components="components"
          :class="['widget-table-file-list-source-table  pt-table']"
        >
          <template slot="orderIndexSlot" slot-scope="text, record, index">
            {{ index + 1 }}
          </template>
          <template slot="sourceNameSlot" slot-scope="text, record, index">
            <a-form-model-item :prop="'dataSource.' + index + '.sourceName'" :rules="requiredRule" style="margin: 0">
              <a-input v-model="record.sourceName">
                <template slot="addonAfter">
                  <WI18nInput :code="record.id" :target="record" v-model="record.sourceName" />
                </template>
              </a-input>
            </a-form-model-item>
          </template>
          <template slot="codeSlot" slot-scope="text, record, index">
            <span v-if="text == 'local_upload'">{{ text }}</span>
            <a-form-model-item v-else :prop="'dataSource.' + index + '.code'" :rules="requiredRule" style="margin: 0">
              <a-input v-model="record.code"></a-input>
            </a-form-model-item>
          </template>
          <template slot="iconSlot" slot-scope="text, record, index">
            <WidgetIconLibModal :zIndex="1000" v-model="record.icon" :onlyIconClass="true">
              <a-button type="link" size="small">
                <Icon :type="record.icon || 'plus-square'" />
              </a-button>
            </WidgetIconLibModal>
          </template>
          <template slot="jsModuleSlot" slot-scope="text, record, index">
            <a-form-model-item
              v-if="record.code != 'local_upload'"
              :prop="'dataSource.' + index + '.jsModuleValue'"
              :rules="requiredRule"
              style="margin: 0"
            >
              <JsModuleSelect
                :multiSelect="false"
                :labelInValue="false"
                v-model="record.jsModuleValue"
                @input="(value, options) => jsModuleChange(value, options, record)"
                dependencyFilter="WidgetFileSourceDevelopment"
              />
            </a-form-model-item>
          </template>
          <template slot="defaultFlagSlot" slot-scope="text, record, index">
            <a-checkbox
              v-model="record.defaultChecked"
              @change="
                e => {
                  record.defaultFlag = record.defaultChecked ? '1' : '0';
                }
              "
            ></a-checkbox>
          </template>
        </a-table>
      </a-form-model>
    </Scroll>
    <p />
    <a-row>
      <a-col :span="2" :offset="11">
        <a-button type="primary" @click="saveAll">保存</a-button>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';
import JsModuleSelect from '@pageWidget/commons/js-module-select.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';
const ColumnExtend = Vue.extend({
  template: `<span>
                  <font v-if="requiredColumns.indexOf(dataIndex) != -1" color="red">*</font>
                     <a-popover v-if="dataIndex == 'jsModule'">
                      <template slot="content">
                        <span>附件来源的JS模块，需要继承WidgetFileSourceDevelopment.js，如DemoFileSourceDevelopment.js</span>
                      </template>
                      <a-icon type="info-circle" />
                    </a-popover>
                    <a-popover v-if="dataIndex == 'defaultFlag'">
                      <template slot="content">
                        <span>关联附件控件中，附件来源的默认选中状态</span>
                      </template>
                      <a-icon type="info-circle" />
                    </a-popover>
                 </span>`,
  props: {
    dataIndex: String
  },
  data() {
    return {
      requiredColumns: ['sourceName', 'code', 'jsModule']
    };
  }
});
import { generateId } from '@framework/vue/utils/util';
export default {
  components: { Modal, WidgetIconLibModal, JsModuleSelect, WI18nInput },
  data() {
    this.components = {
      header: {
        cell: (h, props, children) => {
          return (
            <th {...props}>
              {children}
              <ColumnExtend dataIndex={props.key}></ColumnExtend>
            </th>
          );
        }
      }
    };
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      fileListSourceTableColumns: [
        { title: '序号', dataIndex: 'orderIndex', width: '70px', align: 'center', scopedSlots: { customRender: 'orderIndexSlot' } },
        { title: '名称', dataIndex: 'sourceName', width: '300px', scopedSlots: { customRender: 'sourceNameSlot' } },
        { title: '编码', dataIndex: 'code', width: '300px', scopedSlots: { customRender: 'codeSlot' } },
        { title: '图标', dataIndex: 'icon', scopedSlots: { customRender: 'iconSlot' } },
        { title: 'JS模块', dataIndex: 'jsModule', width: '300px', scopedSlots: { customRender: 'jsModuleSlot' } },
        { title: '默认选中', dataIndex: 'defaultFlag', scopedSlots: { customRender: 'defaultFlagSlot' } }
      ],
      formData: {
        dataSource: []
      },
      dataSource: [],
      selectedRowKeys: [],
      selectedRows: [],
      deletedRows: [],
      requiredRule: { required: true, message: '不能为空', trigger: 'blur' }
    };
  },
  watch: {
    dataSource: {
      deep: true,
      handler(newVal) {
        this.formData.dataSource = newVal;
      }
    }
  },
  created() {
    this.loadDataSource();
  },
  methods: {
    loadDataSource() {
      $axios
        .post('/json/data/services', {
          serviceName: 'dyformFileListSourceConfigService',
          methodName: 'getAllBean',
          args: JSON.stringify([])
        })
        .then(({ data: result }) => {
          let dataList = result.data || [];
          dataList.forEach(item => {
            this.parseJsModule(item);
            item.id = item.uuid;
            item.defaultChecked = item.defaultFlag == '1';
            if (item.i18ns && item.i18ns.length > 0) {
              item.i18n = {};
              item.i18ns.forEach(i18n => {
                item.i18n[i18n.locale] = {};
                item.i18n[i18n.locale][item[i18n.code]] = i18n.content;
              });
            }
          });
          this.dataSource = dataList;
        });
    },
    onSelectChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys;
      this.selectedRows = selectedRows;
    },
    parseJsModule(record) {
      let jsModuleJSON = JSON.parse(record.jsModule || '{}');
      record.jsModuleName = jsModuleJSON.jsModuleName;
      record.jsModuleValue = jsModuleJSON.jsModule;
      record.refreshIfExists = jsModuleJSON.refreshIfExists;
    },
    jsModuleChange(value, options, record) {
      console.log(value, options, record);
      let item = options.find(item => item.value === value) || {};
      let jsModuleJSON = JSON.parse(record.jsModule || '{}');
      jsModuleJSON.jsModule = item.value;
      jsModuleJSON.jsModuleName = item.label;
      record.jsModule = JSON.stringify(jsModuleJSON);
    },
    add() {
      this.dataSource.push({ id: generateId(), orderIndex: this.dataSource.length });
    },
    remove() {
      const _this = this;
      if (_this.selectedRowKeys.length == 0) {
        _this.$message.error('请选择记录！');
        return;
      }
      let localUploadIndex = _this.selectedRows.findIndex(item => item.code == 'local_upload');
      if (localUploadIndex != -1) {
        _this.$message.error('以下附件来源不允许删除：本地文件，请重新选择！');
        return;
      }

      _this.$confirm({
        title: `确认框`,
        content: `确定删除选择的附件来源吗？
                  删除后，在附件控件和已上传附件中，
                  将删除附件来源，请谨慎操作！`,
        onOk() {
          for (let index = 0; index < _this.dataSource.length; index++) {
            if (_this.selectedRowKeys.includes(_this.dataSource[index].id)) {
              let deletedConfig = _this.dataSource.splice(index--, 1);
              deletedConfig[0].rowStatus = 'deleted';
              _this.deletedRows.push(deletedConfig[0]);
            }
          }
          _this.selectedRowKeys = [];
          _this.selectedRows = [];
        },
        onCancel() {}
      });
    },
    moveUp() {
      const _this = this;
      if (_this.selectedRowKeys.length == 0) {
        _this.$message.error('请选择记录！');
        return;
      }

      for (let index = 0; index < _this.dataSource.length; index++) {
        if (_this.selectedRowKeys.includes(_this.dataSource[index].id)) {
          if (index > 0) {
            let tmp = _this.dataSource[index - 1];
            _this.dataSource[index - 1] = _this.dataSource[index];
            _this.dataSource[index] = tmp;
          }
        }
      }
      _this.dataSource = [..._this.dataSource];
    },
    moveDown() {
      const _this = this;
      if (_this.selectedRowKeys.length == 0) {
        _this.$message.error('请选择记录！');
        return;
      }

      for (let index = _this.dataSource.length - 1; index >= 0; index--) {
        if (_this.selectedRowKeys.includes(_this.dataSource[index].id)) {
          if (index < _this.dataSource.length - 1) {
            let tmp = _this.dataSource[index + 1];
            _this.dataSource[index + 1] = _this.dataSource[index];
            _this.dataSource[index] = tmp;
          }
        }
      }
      _this.dataSource = [..._this.dataSource];
    },
    saveAll() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.dataSource.forEach(item => {
            let i18ns = [];
            if (item.i18n) {
              for (let key in item.i18n) {
                for (let code in item.i18n[key]) {
                  i18ns.push({
                    content: item.i18n[key][code],
                    locale: key,
                    code: 'uuid'
                  });
                }
              }
            }
            item.i18ns = i18ns;
          });
          $axios
            .post('/json/data/services', {
              serviceName: 'dyformFileListSourceConfigService',
              methodName: 'saveAllBean',
              args: JSON.stringify([this.dataSource.concat(this.deletedRows)])
            })
            .then(({ data: result }) => {
              if (result.success) {
                this.$message.success('保存成功！');
                this.deletedRows = [];
                this.loadDataSource();
              } else {
                this.$message.error(result.msg || '保存失败！');
              }
            });
        }
      });
    }
  }
};
</script>

<style></style>
