<template>
  <div>
    <a-space>
      <a-button type="primary" icon="plus" @click="add">新增</a-button>
      <a-button type="danger" icon="delete" @click="remove">删除</a-button>
      <a-button type="default" icon="arrow-up" @click="moveUp">上移</a-button>
      <a-button type="default" icon="arrow-down" @click="moveDown">下移</a-button>
      <span style="color: var(--w-text-color-light)">通过移动调整顺序，对应附件控件中显示的附件按钮顺序</span>
    </a-space>
    <p />
    <Scroll style="height: calc(100vh - 290px); margin-right: -20px; padding-right: 12px">
      <a-form-model ref="form" :model="formData">
        <a-table
          rowKey="id"
          :pagination="false"
          :bordered="false"
          :columns="fileListButtonTableColumns"
          :locale="locale"
          :data-source="dataSource"
          :row-selection="{ selectedRowKeys: selectedRowKeys, selectedRows: selectedRows, onChange: onSelectChange }"
          :components="components"
          :class="['widget-table-file-list-button-table pt-table']"
        >
          <template slot="orderIndexSlot" slot-scope="text, record, index">
            {{ index + 1 }}
          </template>
          <template slot="buttonNameSlot" slot-scope="text, record, index">
            <span v-if="record.btnType == '1'">{{ text }}</span>
            <a-form-model-item v-else :prop="'dataSource.' + index + '.buttonName'" :rules="requiredRule" style="margin: 0">
              <a-input v-model="record.buttonName">
                <template slot="addonAfter">
                  <WI18nInput :code="record.id" :target="record" v-model="record.buttonName" />
                </template>
              </a-input>
            </a-form-model-item>
          </template>
          <template slot="codeSlot" slot-scope="text, record, index">
            <span v-if="record.btnType == '1'">{{ text }}</span>
            <a-form-model-item v-else :prop="'dataSource.' + index + '.code'" :rules="requiredRule" style="margin: 0">
              <a-input v-model="record.code"></a-input>
            </a-form-model-item>
          </template>
          <template slot="btnTypeSlot" slot-scope="text, record, index">
            <span v-if="record.btnType == '1'">内置按钮</span>
            <span v-if="record.btnType == '0'">扩展按钮</span>
          </template>
          <template slot="btnShowTypeSlot" slot-scope="text, record, index">
            <span v-if="record.btnType == '1'">{{ getBtnShowTypeDisplayValue(text) }}</span>
            <span v-if="record.btnType == '0'">
              <a-select v-model="record.btnShowType" :options="btnShowTypeOptions" style="width: 100%"></a-select>
            </span>
          </template>
          <template slot="btnLibSlot" slot-scope="text, record, index">
            <Icon v-if="record.button.style.icon" :type="record.button.style.icon"></Icon>
            <a-button :type="record.button.style.type" :shape="record.button.style.shape">
              {{ record.button.style.textHidden ? '' : record.buttonName || '按钮' }}
            </a-button>
            <Modal title="按钮配置">
              <a-icon type="setting" />
              <template slot="content">
                <AdminSettingButtonConfiguration
                  :button="record.button"
                  :handler="record.eventManger"
                  ref="msgEventConfigurationRef"
                  hideParams="text,code"
                />
              </template>
            </Modal>
          </template>
          <template slot="fileExtensionsSlot" slot-scope="text, record, index">
            <a-input v-if="record.btnType == '0'" v-model="record.fileExtensions"></a-input>
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
import AdminSettingButtonConfiguration from '../common/button-configuration-admin';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';
import { getElSpacingForTarget } from '@framework/vue/utils/util';
const ColumnExtend = Vue.extend({
  template: `<span>
                  <font v-if="requiredColumns.indexOf(dataIndex) != -1" color="red">*</font>
                     <a-popover v-if="dataIndex == 'btnShowType'">
                      <template slot="content">
                        <span>在操作按钮选择时会区分显示，如流程环节-表单设置</span><br>
                        <span>显示类操作：不会影响数据存储的操作</span><br>
                        <span>编辑类操作：会影响数据存储的操作</span>
                      </template>
                      <a-icon type="info-circle" />
                    </a-popover>
                    <a-popover v-if="dataIndex == 'fileExtensions'">
                      <template slot="content">
                        <span>如果值为空，表示支持任意扩展名的附件<br/>值的格式必须符合JSON数组规范，除非允许所有格式时则为空</span>
                      </template>
                      <a-icon type="info-circle" />
                    </a-popover>
                    <a-popover v-if="dataIndex == 'defaultFlag'">
                      <template slot="content">
                        <span>关联附件控件中，附件按钮的默认选中状态</span>
                      </template>
                      <a-icon type="info-circle" />
                    </a-popover>
                 </span>`,
  props: {
    dataIndex: String
  },
  data() {
    return {
      requiredColumns: ['buttonName', 'code']
    };
  }
});
import { generateId, deepClone } from '@framework/vue/utils/util';
export default {
  components: { Modal, AdminSettingButtonConfiguration, WI18nInput },
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
      fileListButtonTableColumns: [
        { title: '序号', dataIndex: 'orderIndex', width: '70px', align: 'center', scopedSlots: { customRender: 'orderIndexSlot' } },
        { title: '名称', dataIndex: 'buttonName', width: '200px', scopedSlots: { customRender: 'buttonNameSlot' } },
        { title: '编码', dataIndex: 'code', width: '160px', scopedSlots: { customRender: 'codeSlot' } },
        { title: '内置/扩展按钮', dataIndex: 'btnType', width: '140px', scopedSlots: { customRender: 'btnTypeSlot' } },
        { title: '显示/编辑类操作', dataIndex: 'btnShowType', width: '160px', scopedSlots: { customRender: 'btnShowTypeSlot' } },
        { title: '按钮库', dataIndex: 'button', width: '160px', scopedSlots: { customRender: 'btnLibSlot' } },
        { title: '支持的文件扩展名', dataIndex: 'fileExtensions', scopedSlots: { customRender: 'fileExtensionsSlot' } },
        { title: '默认选中', dataIndex: 'defaultFlag', scopedSlots: { customRender: 'defaultFlagSlot' } }
      ],
      formData: {
        dataSource: []
      },
      dataSource: [],
      selectedRowKeys: [],
      selectedRows: [],
      deletedRows: [],
      btnShowTypeOptions: [
        { label: '显示类操作', value: 'show' },
        { label: '编辑类操作', value: 'edit' }
      ],
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
          serviceName: 'dyformFileListButtonConfigService',
          methodName: 'getAllBean',
          args: JSON.stringify([])
        })
        .then(({ data: result }) => {
          let dataList = result.data || [];
          dataList.forEach(item => {
            this.parseBtnLib(item);
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
    getBtnShowTypeDisplayValue(showType) {
      let item = this.btnShowTypeOptions.find(item => item.value === showType);
      return item ? item.label : '';
    },
    onSelectChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys;
      this.selectedRows = selectedRows;
    },
    parseBtnLib(record) {
      let btnLibJson = JSON.parse(record.btnLib || '{}');
      btnLibJson.style = btnLibJson.style || { icon: '' };
      record.iconType = btnLibJson.style.icon || '';
      this.$set(record, 'button', btnLibJson);
      // record.button = btnLibJson;
      this.$set(record, 'eventManger', JSON.parse(record.eventManger || '{}'));
      if (!Array.isArray(record.eventManger.eventParams)) {
        record.eventManger.eventParams = [];
      }
      // record.eventManger = JSON.parse(record.eventManger || '{}');
    },
    add() {
      this.dataSource.push({
        id: generateId(),
        orderIndex: this.dataSource.length,
        btnType: '0',
        btnShowType: 'show',
        button: { style: {} },
        eventManger: {
          eventParams: []
        }
      });
    },
    remove() {
      const _this = this;
      if (_this.selectedRowKeys.length == 0) {
        _this.$message.error('请选择记录！');
        return;
      }
      let localUploadIndex = _this.selectedRows.findIndex(item => item.btnType == '1');
      if (localUploadIndex != -1) {
        _this.$message.error('内置按钮不允许删除，请重新选择！');
        return;
      }

      _this.$confirm({
        title: `确认框`,
        content: `确定删除选择的附件按钮吗？
                  删除后，在附件控件和已上传附件中，
                  将删除附件按钮，请谨慎操作！`,
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
          let dataList = deepClone(this.dataSource.concat(this.deletedRows));
          dataList.forEach(item => {
            item.btnLib = JSON.stringify(item.button);
            item.eventManger = JSON.stringify(item.eventManger);
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
              serviceName: 'dyformFileListButtonConfigService',
              methodName: 'saveAllBean',
              args: JSON.stringify([dataList])
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
