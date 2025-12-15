<template>
  <div>
    <a-button-group style="margin-bottom: 12px">
      <a-button type="primary" @click="viewRecord">流水号记录</a-button>
      <a-button type="primary" @click="checkSerialNumber">跳号检测</a-button>
      <a-button type="danger" @click="onDelete">删除</a-button>
    </a-button-group>
    <a-table
      rowKey="uuid"
      :pagination="false"
      :bordered="false"
      :columns="recordTableColumns"
      :locale="locale"
      :data-source="maintains"
      :row-selection="maintainSelection"
      :scroll="{ y: 'calc(100vh - 300px)' }"
      :class="['widget-table-serial-number-maintain-table no-border pt-table']"
    >
      <template slot="pointerResetTypeSlot" slot-scope="text, record">
        <span v-if="record.pointerResetType == '1'">按周期重置</span>
        <span v-else-if="record.pointerResetType == '2'">按变量重置</span>
        <span v-else>不重置</span>
      </template>
      <template slot="pointerResetRuleSlot" slot-scope="text, record">
        <template v-if="record.pointerResetType == '1'">
          <span v-if="record.pointerResetRule == '10'">按年重置</span>
          <span v-else-if="record.pointerResetRule == '20'">按月重置</span>
          <span v-else-if="record.pointerResetRule == '30'">按周重置</span>
          <span v-else-if="record.pointerResetRule == '40'">按日重置</span>
          <span v-else>{{ text }}</span>
        </template>
        <span v-else>{{ text }}</span>
      </template>
      <template slot="pointerSlot" slot-scope="text, record, index">
        <a-form-model
          :ref="'form_' + record.uuid"
          :model="record"
          :rules="pointerRule"
          :label-col="{ span: 0 }"
          :wrapper-col="{ span: 19 }"
          @submit="
            e => {
              record.pointerCell.valid = true;
            }
          "
          @validate="
            (prop, valid) => {
              record.pointerCell.valid = valid;
            }
          "
        >
          <TableEditableCell :model="record" prop="pointer" :cell="record.pointerCell"></TableEditableCell>
        </a-form-model>
      </template>
      <template slot="operationSlot" slot-scope="text, record">
        <a-button
          type="primary"
          :disabled="record.initPointer == record.pointer"
          @click="updatePointer(record.uuid, record.pointer, 'form_' + record.uuid, record)"
        >
          保存
        </a-button>
      </template>
    </a-table>
    <a-modal
      title="跳号检测结果"
      :width="800"
      :visible="checkSerialNumberVisible"
      :destroyOnClose="true"
      @ok="updateInitialValue"
      @cancel="e => (checkSerialNumberVisible = false)"
    >
      <a-table
        rowKey="uuid"
        size="small"
        :pagination="false"
        :bordered="true"
        :columns="skipPointersTableColumns"
        :locale="locale"
        :data-source="skipPointers"
        :scroll="{ y: 600 }"
        :class="['widget-table-serial-number-skip-pointer-table no-border']"
      >
        <template slot="pointerResetTypeSlot" slot-scope="text, record">
          <span v-if="record.pointerResetType == '1'">按周期重置</span>
          <span v-else-if="record.pointerResetType == '2'">按变量重置</span>
          <span v-else>不重置</span>
        </template>
        <template slot="pointerResetRuleSlot" slot-scope="text, record">
          <template v-if="record.pointerResetType == '1'">
            <span v-if="record.pointerResetRule == '10'">按年重置</span>
            <span v-else-if="record.pointerResetRule == '20'">按月重置</span>
            <span v-else-if="record.pointerResetRule == '30'">按周重置</span>
            <span v-else-if="record.pointerResetRule == '40'">按日重置</span>
            <span v-else>{{ text }}</span>
          </template>
          <span v-else>{{ text }}</span>
        </template>
        <template slot="initialValueSlot" slot-scope="text, record, index">
          <a-form-model
            :ref="'skip_form_' + record.uuid"
            :model="record"
            :rules="initialValueRule"
            :label-col="{ span: 0 }"
            :wrapper-col="{ span: 19 }"
            @submit="
              e => {
                record.initialValueCell.valid = true;
              }
            "
            @validate="
              (prop, valid) => {
                record.initialValueCell.valid = valid;
              }
            "
          >
            <TableEditableCell :model="record" prop="initialValue" :cell="record.initialValueCell"></TableEditableCell>
          </a-form-model>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script>
import TableEditableCell from '../../common/table-editable-cell.vue';
import { deepClone } from '@framework/vue/utils/util';
export default {
  props: {
    definition: {
      type: Object,
      default() {
        return {};
      }
    }
  },
  components: { TableEditableCell },
  inject: ['pageContext', 'namespace', 'vPageState', '$pageJsInstance'],
  data() {
    const _this = this;
    return {
      recordTableColumns: [
        { title: '计数重置类型', dataIndex: 'pointerResetType', scopedSlots: { customRender: 'pointerResetTypeSlot' } },
        { title: '计数重置规则', dataIndex: 'pointerResetRule', scopedSlots: { customRender: 'pointerResetRuleSlot' } },
        { title: '计数重置规则值', dataIndex: 'pointerResetRuleValue' },
        { title: '计数', dataIndex: 'pointer', scopedSlots: { customRender: 'pointerSlot' } },
        { title: '操作', dataIndex: 'operation', scopedSlots: { customRender: 'operationSlot' } }
      ],
      maintains: [],
      maintainSelection: {
        selectedRowKeys: [],
        selectedRows: [],
        onChange(selectedRowKeys, selectedRows) {
          _this.maintainSelection.selectedRowKeys = selectedRowKeys;
          _this.maintainSelection.selectedRows = selectedRows;
        }
      },
      pointerRule: {
        pointer: [
          { required: true, message: '不能为空', trigger: 'blur' },
          {
            // validator: (rule, value, callback) => {
            //   return value && /^\d+$/g.test(value);
            // },
            pattern: /^\d+$/g,
            message: '请输入整数！',
            trigger: 'blur'
          }
        ]
      },
      initialValueRule: {
        initialValue: [
          { required: true, message: '不能为空', trigger: 'blur' },
          {
            pattern: /^\d+$/g,
            message: '请输入整数！',
            trigger: 'blur'
          }
        ]
      },
      locale: {
        emptyText: <span>暂无数据</span>
      },
      checkSerialNumberVisible: false,
      skipPointersTableColumns: [
        { title: '计数重置类型', dataIndex: 'pointerResetType', scopedSlots: { customRender: 'pointerResetTypeSlot' } },
        { title: '计数重置规则', dataIndex: 'pointerResetRule', scopedSlots: { customRender: 'pointerResetRuleSlot' } },
        { title: '计数重置规则值', dataIndex: 'pointerResetRuleValue' },
        { title: '当前计数', dataIndex: 'pointer' },
        { title: '跳号', dataIndex: 'skipPointers', ellipsis: true },
        { title: '可补号起始值', dataIndex: 'initialValue', scopedSlots: { customRender: 'initialValueSlot' } }
      ],
      skipPointers: []
    };
  },
  watch: {
    'definition.uuid': function (newVal) {
      if (newVal) {
        this.loadMaintain(newVal);
      }
    }
  },
  created() {
    if (this.definition.uuid) {
      this.loadMaintain(this.definition.uuid);
    }
  },
  methods: {
    loadMaintain(serialNumberDefUuid) {
      $axios.get(`/proxy/api/sn/serial/number/maintain/list?serialNumberDefUuid=${serialNumberDefUuid}`).then(({ data: result }) => {
        if (result.data) {
          result.data.forEach(item => {
            item.initPointer = item.pointer;
            item.pointerCell = { type: 'a-input', attrs: { value: item.pointer } };
          });
          this.maintains = result.data;
        }
      });
    },
    viewRecord() {
      const _this = this;
      if (_this.maintainSelection.selectedRowKeys.length === 0) {
        _this.$message.error('请选择记录！');
        return;
      }

      // 流水号记录出框
      let Modal = Vue.extend({
        template: `<a-modal title="流水号记录" :visible="visible" :width="900" class="pt-modal" :maxHeight="600" :bodyStyle="{ height: '600px' }" @ok="handleOk" @cancel="handleCancel">
                    <div style="height: 600px; overflow:auto"><div class="widget"></div></div>
                    <template slot="footer">
                      <a-button type="primary" @click="handleOk">关闭</a-button>
                    </template>
                  </a-modal>`,
        data: function () {
          return { visible: true };
        },
        mounted() {
          this.$nextTick(() => {
            _this.getTableWidget().then(recordWidget => {
              recordWidget.$mount(modal.$el.querySelector('.widget'));
            });
          });
        },
        methods: {
          handleCancel() {
            this.visible = false;
            this.$destroy();
          },
          handleOk() {
            this.visible = false;
            this.$destroy();
          }
        }
      });
      let modal = new Modal();
      modal.$mount();
    },
    getTableWidget() {
      const _this = this;
      return _this
        .getTableDefinition()
        .then(widgetDefinition => {
          // 创建构造器
          let WidgetTable = Vue.extend({
            template: '<WidgetTable ref="widgetTable" :widget="widget" @beforeLoadData="beforeLoadData"></WidgetTable>', //
            provide() {
              return {
                pageContext: _this.pageContext,
                namespace: _this.namespace,
                vPageState: _this.vPageState,
                $pageJsInstance: _this.$pageJsInstance,
                locale: _this.pageContext.getVueWidgetById('zPmiDyJHShrwBUdFqaNIsPzJUIgxzcJn').locale // 获取流水号中间表格的locale值
              };
            },
            i18n: _this.$i18n,
            inject: {},
            data: function () {
              return { widget: widgetDefinition };
            },
            methods: {
              beforeLoadData() {
                let tableWidget = this.$refs.widgetTable;
                let dataSource = tableWidget.getDataSourceProvider();
                dataSource.addParam('serialNumberDefUuid', _this.definition.uuid);
                dataSource.addParam('maintainUuids', _this.maintainSelection.selectedRowKeys);
              }
            }
          });
          return new WidgetTable();
        })
        .catch(res => {
          _this.$message.error('流水号记录加载失败！');
        });
    },
    getTableDefinition() {
      let widgetTableId = 'LDUenHBpkHdqdoHgAupiszwvfgAzgdPF';
      return new Promise((resolve, reject) => {
        $axios
          .post('/json/data/services', {
            serviceName: 'appContextService',
            methodName: 'getAppWidgetDefinitionById',
            args: JSON.stringify([widgetTableId, false])
          })
          .then(({ data: { data = {} } }) => {
            if (data.definitionJson) {
              resolve(JSON.parse(data.definitionJson));
            } else {
              reject(data);
            }
          })
          .catch(res => {
            reject(res);
          });
      });
    },
    checkSerialNumber() {
      const _this = this;
      if (_this.maintainSelection.selectedRowKeys.length === 0) {
        _this.$message.error('请选择记录！');
        return;
      }

      $axios
        .post('/json/data/services', {
          serviceName: 'snSerialNumberMaintainFacadeService',
          methodName: 'checkSerialNumber',
          args: JSON.stringify([_this.definition.id, _this.maintainSelection.selectedRowKeys])
        })
        .then(({ data: result }) => {
          if (result.data) {
            let resultMap = result.data;
            let selectedRows = deepClone(_this.maintains.filter(item => _this.maintainSelection.selectedRowKeys.includes(item.uuid)));
            selectedRows.forEach(item => {
              let checkResult = resultMap[item.uuid];
              if (checkResult) {
                item.skipPointers = checkResult.skipPointers.join('、');
                item.fillSerialNos = checkResult.fillSerialNos.join('、');
              }
              item._initialValue = item.initialValue;
              item.initialValueCell = { type: 'a-input', attrs: { value: item.pointer } };
              _this.skipPointers = selectedRows;
            });
          }
        });

      this.checkSerialNumberVisible = true;
    },
    updateInitialValue() {
      const _this = this;
      let updatedPointers = _this.skipPointers.filter(item => item._initialValue != item.initialValue);
      if (updatedPointers.length > 0) {
        let promises = [];
        let initialValueMap = {};
        updatedPointers.forEach(item => {
          initialValueMap[item.uuid] = item.initialValue;
          promises.push(_this.$refs['skip_form_' + item.uuid].validate());
        });
        Promise.all(promises).then(() => {
          $axios
            .post('/json/data/services', {
              serviceName: 'snSerialNumberMaintainFacadeService',
              methodName: 'updateInitialValue',
              args: JSON.stringify([initialValueMap])
            })
            .then(({ data: result }) => {
              if (result.success) {
                _this.$message.success('保存成功！');
                _this.loadMaintain(_this.definition.uuid);
                this.checkSerialNumberVisible = false;
              } else {
                _this.$message.error(result.msg || '保存失败！');
              }
            });
        });
      } else {
        _this.checkSerialNumberVisible = false;
      }
    },
    onDelete() {
      const _this = this;
      if (_this.maintainSelection.selectedRowKeys.length === 0) {
        _this.$message.error('请选择记录！');
        return;
      }

      _this.$confirm({
        title: '确认框',
        content: '确定要修改流水号维护吗？',
        onOk() {
          $axios
            .post('/json/data/services', {
              serviceName: 'snSerialNumberMaintainFacadeService',
              methodName: 'deleteAllByUuids',
              args: JSON.stringify([_this.maintainSelection.selectedRowKeys])
            })
            .then(({ data: result }) => {
              if (result.success) {
                _this.$message.success('删除成功！');
                _this.maintainSelection.selectedRowKeys = [];
                _this.maintainSelection.selectedRows = [];
                _this.loadMaintain(_this.definition.uuid);
              } else {
                _this.$message.error(result.msg || '保存失败！');
              }
            })
            .catch(({ response }) => {
              _this.$message.error(
                response.data && typeof response.data.data == 'string'
                  ? response.data.data
                  : (response.data && response.data.msg) || '服务异常！'
              );
            });
        }
      });
    },
    updatePointer(uuid, pointer, refForm, record) {
      const _this = this;
      _this.$refs[refForm].validate(valid => {
        if (valid) {
          _this.$confirm({
            title: '确认框',
            content: '确定要修改流水号维护吗？',
            onOk() {
              $axios
                .post('/json/data/services', {
                  serviceName: 'snSerialNumberMaintainFacadeService',
                  methodName: 'updatePointerByUuid',
                  args: JSON.stringify([uuid, pointer])
                })
                .then(({ data: result }) => {
                  if (result.success) {
                    _this.$message.success('保存成功！');
                    _this.loadMaintain(_this.definition.uuid);
                  } else {
                    _this.$message.error(result.msg || '保存失败！');
                  }
                })
                .catch(({ response }) => {
                  _this.$message.error(
                    response.data && typeof response.data.data == 'string'
                      ? response.data.data
                      : (response.data && response.data.msg) || '服务异常！'
                  );
                });
            }
          });
        }
      });
    }
  }
};
</script>

<style></style>
