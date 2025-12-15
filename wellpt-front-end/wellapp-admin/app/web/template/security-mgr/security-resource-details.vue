<template>
  <div :loading="loading">
    <a-form-model ref="form" :model="formData" :rules="rules" :label-col="{ span: 4 }" :wrapper-col="{ span: 19 }" class="pt-form">
      <a-tabs default-active-key="1" v-model="activeKey" class="pt-tabs" ref="tabsRef">
        <a-tab-pane key="1" tab="基本信息">
          <a-form-model-item label="名称" prop="name">
            <a-input v-model="formData.name" />
          </a-form-model-item>
          <a-form-model-item label="编号" prop="code">
            <a-input v-model="formData.code" />
          </a-form-model-item>
          <a-form-model-item label="URL" prop="url">
            <a-input v-model="formData.url" />
          </a-form-model-item>
          <a-form-model-item label="备注" prop="remark">
            <a-textarea v-model="formData.remark" />
          </a-form-model-item>
        </a-tab-pane>
        <a-tab-pane key="2" tab="按钮">
          <a-space>
            <a-button @click="addButton">
              <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
              新增按钮
            </a-button>
            <a-button @click="deleteButton">
              <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
              删除按钮
            </a-button>
          </a-space>
          <p />
          <a-table
            rowKey="id"
            :pagination="false"
            :bordered="false"
            :columns="buttonTableColumns"
            :locale="locale"
            :data-source="formData.buttons"
            childrenColumnName="null"
            :row-selection="buttonSelection"
            :scroll="{ y: tableScrollHeight }"
            :class="['widget-table-resource-button-table pt-table']"
          >
            <template slot="nameSlot" slot-scope="text, record, index">
              <ResourceEditableCell :ref="`name_${index}`" :model="record" prop="name" :id="'name_' + record.id" :cellMap="buttonCellMap">
                <a-form-model-item :prop="`buttons.${index}.name`" :rules="requiredRule" style="margin: 0">
                  <a-input v-model="record.name" @blur="e => $refs[`name_${index}`].submit()"></a-input>
                </a-form-model-item>
              </ResourceEditableCell>
            </template>
            <template slot="codeSlot" slot-scope="text, record, index">
              <ResourceEditableCell :ref="`code_${index}`" :model="record" prop="code" :id="'code_' + record.id" :cellMap="buttonCellMap">
                <a-form-model-item :prop="`buttons.${index}.code`" :rules="buttonCodeRule" style="margin: 0">
                  <a-input v-model="record.code" @blur="e => $refs[`code_${index}`].submit()"></a-input>
                </a-form-model-item>
              </ResourceEditableCell>
            </template>
            <template slot="targetSlot" slot-scope="text, record, index">
              <ResourceEditableCell :ref="`target_${index}`" :model="record" prop="target" :cellMap="buttonCellMap">
                <a-textarea v-model="record.target" @blur="e => $refs[`target_${index}`].submit()"></a-textarea>
              </ResourceEditableCell>
            </template>
            <template slot="isDefaultSlot" slot-scope="text, record, index">
              <a-switch v-model="record.isDefault"></a-switch>
            </template>
            <template slot="applyToSlot" slot-scope="text, record, index">
              <ResourceEditableCell
                :ref="`applyTo_${index}`"
                :model="record"
                prop="applyTo"
                :cellMap="buttonCellMap"
                :cell="{ attrs: { options: applyToOptions } }"
              >
                <a-select
                  mode="multiple"
                  v-model="record.applyTo"
                  :options="applyToOptions"
                  @blur="e => $refs[`applyTo_${index}`].submit()"
                ></a-select>
              </ResourceEditableCell>
            </template>
            <template slot="classNameSlot" slot-scope="text, record, index">
              <ResourceEditableCell :ref="`className_${index}`" :model="record" prop="className" :cellMap="buttonCellMap">
                <a-input v-model="record.className" @blur="e => $refs[`className_${index}`].submit()"></a-input>
              </ResourceEditableCell>
            </template>
            <template slot="remarkSlot" slot-scope="text, record, index">
              <ResourceEditableCell :ref="`remark_${index}`" :model="record" prop="remark" :cellMap="buttonCellMap">
                <a-textarea v-model="record.remark" @blur="e => $refs[`remark_${index}`].submit()"></a-textarea>
              </ResourceEditableCell>
            </template>
          </a-table>
        </a-tab-pane>
        <a-tab-pane key="3" tab="方法">
          <a-space>
            <a-button @click="addMethod">
              <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
              新增方法
            </a-button>
            <a-button @click="deleteMethod">
              <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
              删除方法
            </a-button>
          </a-space>
          <p />
          <a-table
            rowKey="id"
            :pagination="false"
            :bordered="false"
            :columns="methodTableColumns"
            :locale="locale"
            :data-source="formData.methods"
            childrenColumnName="null"
            :row-selection="methodSelection"
            :scroll="{ y: tableScrollHeight }"
            :class="['widget-table-resource-method-table pt-table']"
          >
            <template slot="nameSlot" slot-scope="text, record, index">
              <ResourceEditableCell :ref="`name_${index}`" :model="record" prop="name" :id="'name_' + record.id" :cellMap="methodCellMap">
                <a-form-model-item :prop="`methods.${index}.name`" :rules="requiredRule" style="margin: 0">
                  <a-input v-model="record.name" @blur="e => $refs[`name_${index}`].submit()"></a-input>
                </a-form-model-item>
              </ResourceEditableCell>
            </template>
            <template slot="codeSlot" slot-scope="text, record, index">
              <ResourceEditableCell :ref="`code_${index}`" :model="record" prop="code" :id="'code_' + record.id" :cellMap="methodCellMap">
                <a-form-model-item :prop="`methods.${index}.code`" :rules="methodCodeRule" style="margin: 0">
                  <a-input v-model="record.code" @blur="e => $refs[`code_${index}`].submit()"></a-input>
                </a-form-model-item>
              </ResourceEditableCell>
            </template>
            <template slot="targetSlot" slot-scope="text, record, index">
              <ResourceEditableCell :ref="`target_${index}`" :model="record" prop="target" :cellMap="methodCellMap">
                <a-textarea v-model="record.target" @blur="e => $refs[`target_${index}`].submit()"></a-textarea>
              </ResourceEditableCell>
            </template>
            <template slot="remarkSlot" slot-scope="text, record, index">
              <ResourceEditableCell :ref="`remark_${index}`" :model="record" prop="remark" :cellMap="methodCellMap">
                <a-textarea v-model="record.remark" @blur="e => $refs[`remark_${index}`].submit()"></a-textarea>
              </ResourceEditableCell>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
    <p />
    <a-row>
      <a-col offset="11">
        <a-button type="primary" @click="save">保存</a-button>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import ResourceEditableCell from '../common/table-editable-cell.vue';
import { generateId, deepClone } from '@framework/vue/utils/util';
import { getElSpacingForTarget } from '@framework/vue/utils/util';

export default {
  components: {
    ResourceEditableCell
  },
  inject: ['pageContext'],
  data() {
    const _this = this;
    return {
      loading: false,
      activeKey: '1',
      formData: {
        buttons: [],
        methods: []
      },
      rules: {
        name: { required: true, message: '不能为空', trigger: 'blur' },
        code: { required: true, message: '不能为空', trigger: 'blur' }
      },
      locale: {
        emptyText: <span>暂无数据</span>
      },
      buttonTableColumns: [
        { title: '名称', dataIndex: 'name', scopedSlots: { customRender: 'nameSlot' } },
        { title: '编号', dataIndex: 'code', scopedSlots: { customRender: 'codeSlot' } },
        { title: 'JS代码', dataIndex: 'target', scopedSlots: { customRender: 'targetSlot' } },
        { title: '默认显示', dataIndex: 'isDefault', scopedSlots: { customRender: 'isDefaultSlot' } },
        { title: '应用于', dataIndex: 'applyTo', scopedSlots: { customRender: 'applyToSlot' } },
        { title: 'class样式', dataIndex: 'className', width: '200px', scopedSlots: { customRender: 'classNameSlot' } },
        { title: '备注', dataIndex: 'remark', scopedSlots: { customRender: 'remarkSlot' } }
      ],
      buttonSelection: {
        selectedRowKeys: [],
        selectedRows: [],
        onChange(selectedRowKeys, selectedRows) {
          _this.buttonSelection.selectedRowKeys = selectedRowKeys;
          _this.buttonSelection.selectedRows = selectedRows;
        }
      },
      buttonCellMap: {},
      applyToOptions: [{}],
      methodTableColumns: [
        { title: '名称', dataIndex: 'name', scopedSlots: { customRender: 'nameSlot' } },
        { title: '编号', dataIndex: 'code', scopedSlots: { customRender: 'codeSlot' } },
        { title: '服务调用', dataIndex: 'target', scopedSlots: { customRender: 'targetSlot' } },
        { title: '备注', dataIndex: 'remark', scopedSlots: { customRender: 'remarkSlot' } }
      ],
      methodSelection: {
        selectedRowKeys: [],
        selectedRows: [],
        onChange(selectedRowKeys, selectedRows) {
          _this.methodSelection.selectedRowKeys = selectedRowKeys;
          _this.methodSelection.selectedRows = selectedRows;
        }
      },
      methodCellMap: {},
      requiredRule: { required: true, message: '不能为空', trigger: 'blur' },
      buttonCodeRule: [
        { required: true, message: '不能为空', trigger: 'blur' },
        {
          validator: (rule, value, callback) => {
            return value && value.startsWith('B') ? true : false;
          },
          message: '按钮编号需要以B开头！',
          trigger: 'blur'
        }
      ],
      methodCodeRule: [
        { required: true, message: '不能为空', trigger: 'blur' },
        {
          validator: (rule, value, callback) => {
            return value && value.startsWith('M') ? true : false;
          },
          message: '方法编号需要以M开头！',
          trigger: 'blur'
        }
      ],
      tableScrollHeight: 500
    };
  },
  created() {
    this.loadApplyToOptions();
  },
  mounted() {
    this.pageContext.handleEvent('resource-tree:click', resource => {
      this.loadResource(resource.id);
    });
    this.pageContext.handleEvent('resource-tree:add', parentUuid => {
      this.add(parentUuid);
    });
    this.getTabContentHeight();
  },
  methods: {
    loadApplyToOptions() {
      $axios.get('/proxy/api/dict/getDataDictionariesByType/SECURITY_DYBTN').then(({ data: result }) => {
        if (result.data) {
          this.applyToOptions = result.data.map(item => ({ label: item.name, value: item.code }));
        }
      });
    },
    loadResource(resourceUuid) {
      this.loading = true;
      $axios
        .get(`/proxy/api/security/resource/getBean/${resourceUuid}`)
        .then(({ data: result }) => {
          if (result.data) {
            this.formData = result.data;
            this.formData.buttons.forEach(item => {
              item.id = item.uuid;
              if (item.applyTo) {
                item.applyTo = item.applyTo.split(';');
              } else {
                item.applyTo = [];
              }
            });
            this.formData.methods.forEach(item => (item.id = item.uuid));
          } else {
            this.$message.error(result.msg || '加载失败');
          }
        })
        .catch(({ response }) => {
          this.$message.error((response.data && response.data.msg) || '服务异常！');
        })
        .finally(() => {
          this.loading = false;
        });
    },
    addButton() {
      this.formData.buttons.push({
        id: generateId('SF'),
        code: 'B_' + generateId('SF'),
        applyTo: [],
        isDefault: false
      });
    },
    deleteButton() {
      const _this = this;
      if (_this.buttonSelection.selectedRowKeys.length === 0) {
        _this.$message.error('请选择记录！');
        return;
      }

      _this.formData.deletedButtons = _this.formData.deletedButtons || [];
      let dataList = _this.formData.buttons || [];
      for (let index = 0; index < dataList.length; index++) {
        if (_this.buttonSelection.selectedRowKeys.includes(dataList[index].id)) {
          let deletedButtons = dataList.splice(index--, 1);
          _this.formData.deletedButtons = _this.formData.deletedButtons.concat(deletedButtons.filter(item => item.uuid));
        }
      }
      _this.buttonSelection.selectedRowKeys = [];
      _this.buttonSelection.selectedRows = [];
    },
    addMethod() {
      this.formData.methods.push({
        id: generateId('SF'),
        code: 'M_' + generateId('SF')
      });
    },
    deleteMethod() {
      const _this = this;
      if (_this.methodSelection.selectedRowKeys.length === 0) {
        _this.$message.error('请选择记录！');
        return;
      }

      _this.formData.deletedMethods = _this.formData.deletedMethods || [];
      let dataList = _this.formData.methods || [];
      for (let index = 0; index < dataList.length; index++) {
        if (_this.methodSelection.selectedRowKeys.includes(dataList[index].id)) {
          let deletedMethods = dataList.splice(index--, 1);
          _this.formData.deletedMethods = _this.formData.deletedMethods.concat(deletedMethods.filter(item => item.uuid));
        }
      }
      _this.methodSelection.selectedRowKeys = [];
      _this.methodSelection.selectedRows = [];
    },
    add(parentUuid = '') {
      this.clear();
      this.formData.parentUuid = parentUuid;
    },
    clear() {
      this.formData = { buttons: [], methods: [] };
      this.buttonSelection.selectedRowKeys = [];
      this.buttonSelection.selectedRows = [];
      this.methodSelection.selectedRowKeys = [];
      this.methodSelection.selectedRows = [];
      this.buttonCellMap = {};
      this.methodCellMap = {};
    },
    save() {
      const _this = this;
      _this.validateButtonAndMethods().then(() => {
        _this.$refs.form.validate((valid, error) => {
          if (valid) {
            let formData = deepClone(_this.formData);
            formData.buttons &&
              formData.buttons.forEach(item => {
                if (Array.isArray(item.applyTo)) {
                  item.applyTo = item.applyTo.join(';');
                }
              });
            formData.deletedButtons &&
              formData.deletedButtons.forEach(item => {
                if (Array.isArray(item.applyTo)) {
                  item.applyTo = item.applyTo.join(';');
                }
              });
            formData.changedButtons = formData.buttons || [];
            formData.changedMethods = formData.methods || [];
            $axios
              .post('/proxy/api/security/resource/saveBean', formData)
              .then(({ data: result }) => {
                if (result.code == 0) {
                  _this.$message.success('保存成功！');
                  _this.clear();
                  _this.loadResource(result.data);
                  _this.pageContext.emitEvent('resource-tree:reload');
                } else {
                  _this.$message.error(result.msg || '保存失败！');
                }
              })
              .catch(({ response }) => {
                this.$message.error((response.data && response.data.msg) || '服务异常！');
              });
          } else {
            _this.activeKey = '1';
            let baseInfoKeys = [];
            let hasButtonKey = false;
            let hasMethodKey = false;
            for (let key in error) {
              if (key == 'name' || key == 'code') {
                baseInfoKeys.push(key);
              } else if (key.startsWith('buttons.')) {
                hasButtonKey = true;
              } else if (key.startsWith('methods.')) {
                hasMethodKey = true;
              }
            }
            if (baseInfoKeys.length > 0) {
            } else if (hasButtonKey) {
              _this.activeKey = '2';
            } else if (hasMethodKey) {
              _this.activeKey = '3';
            }
          }
        });
      });
    },
    validateButtonAndMethods() {
      const _this = this;
      return new Promise((resolve, reject) => {
        let buttons = _this.formData.buttons || [];
        let methods = _this.formData.methods || [];
        // 验证不通过设置为可编辑，进入后续验证
        buttons.forEach(item => {
          if (!item.name) {
            let cell = _this.buttonCellMap[`name_${item.id}`];
            cell ? (cell.editable = true) : '';
          }
          if (!item.code || !item.code.startsWith('B')) {
            let cell = _this.buttonCellMap[`code_${item.id}`];
            cell ? (cell.editable = true) : '';
          }
        });
        methods.forEach(item => {
          if (!item.name) {
            let cell = _this.methodCellMap[`name_${item.id}`];
            cell ? (cell.editable = true) : '';
          }
          if (!item.code || !item.code.startsWith('M')) {
            let cell = _this.methodCellMap[`code_${item.id}`];
            cell ? (cell.editable = true) : '';
          }
        });
        _this.$nextTick(() => {
          resolve(true);
        });
      });
    },
    getTabContentHeight() {
      setTimeout(() => {
        let $el = this.$refs.tabsRef.$el.querySelector('.ant-tabs-content');
        let { maxHeight } = getElSpacingForTarget($el, this.$el.closest('.widget-col'));
        if (maxHeight) {
          maxHeight = maxHeight - 90;
          $el.style.cssText += `; height:${maxHeight}px;`;
          this.tableScrollHeight = maxHeight - 120;
        }
      }, 100);
    }
  }
};
</script>

<style lang="less" scoped></style>
