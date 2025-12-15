<template>
  <a-card
    id="data-model-detail-body"
    class="preview-card"
    :bodyStyle="{ height: 'calc(100vh - 102px)', position: 'relative' }"
    :bordered="false"
  >
    <template slot="title">
      <span class="title">{{ metadata.name }}</span>
    </template>
    <template slot="extra">
      <template v-if="metadata.type == 'TABLE'">
        <a-button size="small" type="link" @click="toCreateDyform">
          <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          创建表单
        </a-button>
        <Modal title="请选择数据的表单" :ok="e => confirmCreatePageView(e)" :width="600" :maxHeight="300">
          <a-button size="small" type="link" @click="onClickCreatePageViewBtn">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
            创建视图
          </a-button>
          <template slot="content">
            <a-select v-model="selectViewDyform" style="width: 100%">
              <a-select-option v-if="queryDyformOptionLoading">
                <a-spin />
              </a-select-option>
              <a-select-option v-for="(opt, i) in dataModelRelaDyform" :value="opt.value" :key="'dyFormOpt_' + i">
                {{ opt.label }}
              </a-select-option>
            </a-select>
          </template>
        </Modal>
      </template>
      <a-button @click="saveDataModel" type="link" size="small">
        <Icon type="pticon iconfont icon-ptkj-baocun"></Icon>
        保存
      </a-button>
    </template>
    <a-tabs @change="onTabChange" class="pt-tabs">
      <a-tab-pane key="basicInfo" tab="基本信息">
        <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form">
          <a-form-model-item label="名称" prop="name">
            <a-input v-model="form.name" />
          </a-form-model-item>
          <a-form-model-item label="ID" prop="id">
            <a-input :maxLength="24" :disabled="true" v-model="form.id" @change="e => onInputId2CaseFormate(e, 'toUpperCase')" />
          </a-form-model-item>
          <a-form-model-item label="描述">
            <a-textarea v-model="form.remark" />
          </a-form-model-item>
        </a-form-model>
      </a-tab-pane>
      <template v-if="metadata.type == 'TABLE'">
        <a-tab-pane key="modelProps" tab="属性">
          <div style="margin-bottom: 10px; display: flex; align-items: center; justify-content: space-between">
            <div style="display: flex; align-items: center; justify-content: space-between">
              <a-space>
                <Drawer
                  title="添加属性"
                  ref="addModelPropDrawer"
                  :mask="true"
                  :maskClosable="true"
                  :width="380"
                  :container="getDrawerContainer"
                  :destroyOnClose="true"
                  drawerClass="pt-drawer"
                >
                  <a-button type="primary" @click="addColumn">
                    <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                    添加
                  </a-button>
                  <template slot="content">
                    <ColumnDetail ref="columnDetail" :columnRows="columnRows" :preserveCodes="preserveCodes" :key="columnDetailKey" />
                  </template>
                  <template slot="footer">
                    <a-button type="primary" @click="saveAndNewNextColumn">保存并添加下一个</a-button>
                    <a-button
                      type="default"
                      @click="
                        () => {
                          this.$refs.addModelPropDrawer.hide();
                        }
                      "
                    >
                      取消
                    </a-button>
                    <a-button type="primary" @click="saveColumn">保存</a-button>
                  </template>
                </Drawer>
                <a-button @click="e => removeColumns(selectedRows)">
                  <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                  删除
                </a-button>
                <a-switch v-model="showSysDefaultColumn" checked-children="显示内置属性" un-checked-children="显示内置属性"></a-switch>
              </a-space>
            </div>
            <a-input-search placeholder="按显示名称、编码搜索" style="width: 200px" v-model.trim="searchKeyword" allow-clear />
          </div>
          <a-table
            rowKey="column"
            :columns="propColumns"
            :data-source="vColumnRows"
            :pagination="false"
            :row-selection="rowSelection"
            :bordered="false"
            :scroll="{ y: 'calc(100vh - 300px)' }"
            class="pt-table"
          >
            <template slot="sortOrderSlot" slot-scope="text, record, index">
              {{ index + 1 }}
            </template>
            <template slot="dataTypeSlot" slot-scope="text, record">
              <span v-if="text == 'varchar'">字符</span>
              <span v-else-if="text == 'number'">数字</span>
              <span v-else-if="text == 'timestamp'">日期</span>
              <span v-else-if="text == 'clob'">大文本</span>
              <span v-else>-</span>
            </template>
            <template slot="lengthSlot" slot-scope="text, record">
              <template v-if="record.dataType == 'varchar'">
                {{ text }}
              </template>
              <template v-else-if="record.dataType == 'number'">
                {{ record.length }} {{ record.scale != undefined ? ' , ' + record.scale : '' }}
              </template>
              <template v-else>-</template>
            </template>
            <template slot="notNullSlot" slot-scope="text, record">
              <template v-if="text">
                <a-icon type="check-circle" style="color: rgb(82, 196, 26)"></a-icon>
              </template>
              <span v-else>-</span>
            </template>
            <template slot="uniqueSlot" slot-scope="text, record">
              <template v-if="text == 'GLOBAL'">全局唯一</template>
              <template v-else-if="text == 'TENANT'">租户唯一</template>
              <template v-else>-</template>
            </template>
            <template slot="operationSlot" slot-scope="text, record">
              <template v-if="!record.isSysDefault">
                <Drawer
                  title="编辑属性"
                  ref="drawer"
                  :width="380"
                  :mask="true"
                  :maskClosable="true"
                  :container="getDrawerContainer"
                  :ok="saveColumn"
                  :destroyOnClose="true"
                  okText="保存"
                  drawerClass="pt-drawer"
                >
                  <a-button type="link" size="small">
                    <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
                    编辑
                  </a-button>
                  <template slot="content">
                    <ColumnDetail ref="columnDetail" :column="record" :columnRows="columnRows" :preserveCodes="preserveCodes" />
                  </template>
                </Drawer>
                <a-button type="link" size="small" @click="removeColumn(record)" :loading="record.loading">
                  <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                  删除
                </a-button>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
        <a-tab-pane key="rule" tab="校验">
          <a-result title="待开放" />
        </a-tab-pane>
        <!-- <a-tab-pane key="relation" tab="关系"></a-tab-pane> -->
      </template>
      <template v-if="metadata.type == 'VIEW'">
        <a-tab-pane key="designViewObject" tab="视图对象" :style="{ height: 600, padding: 0 }">
          <ViewObjectDesign
            @columnChange="onColumnChange"
            @sqlChange="onSqlChange"
            ref="design"
            :forceRender="true"
            @mounted="onDesignMounted"
            v-if="fetchDetails"
          />
        </a-tab-pane>
        <a-tab-pane key="modelProps" tab="视图属性">
          <ViewObjectProp
            :viewColumns="viewColumns"
            v-if="fetchDetails"
            ref="viewObjectProp"
            @viewObjectPropChange="onViewObjectPropChange"
          />
        </a-tab-pane>
      </template>
      <a-tab-pane key="dataTab" tab="数据">
        <template v-if="fetchDetails">
          <DataModelData v-if="currentDataModel.type == 'TABLE'" :data-model="currentDataModel" ref="previewData" />
          <ViewObjectPreviewData :view-columns="viewColumns" :vid="currentDataModel.id" v-else ref="previewData" />
        </template>
        <a-empty v-else />
      </a-tab-pane>
    </a-tabs>
  </a-card>
</template>
<style lang="less"></style>
<script type="text/babel">
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import ColumnDetail from './column-detail.vue';
import { generateId, deepClone } from '@framework/vue/utils/util';
import DataModelData from './data-model-data.vue';
import { createDesigner } from './view-object/x6Designer';
import ViewObjectProp from './view-object/view-object-prop.vue';
import ViewObjectPreviewData from './view-object/view-object-preview-data.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import dyformPreview from '../page-resource/dyform-preview.vue';
import moment from 'moment';

export default {
  name: 'DataModelDetail',
  props: {
    metadata: Object
  },
  components: {
    Drawer,
    Modal,
    ColumnDetail,
    DataModelData,
    ViewObjectDesign: () => import('./view-object/view-object-design.vue'),
    ViewObjectProp,
    ViewObjectPreviewData
  },
  computed: {
    vColumnRows() {
      let cols = [];
      for (let i = 0, len = this.columnRows.length; i < len; i++) {
        if (
          (this.searchKeyword != undefined &&
            this.columnRows[i].title.indexOf(this.searchKeyword) == -1 &&
            this.columnRows[i].column.toLowerCase().indexOf(this.searchKeyword.toLowerCase()) == -1) ||
          (!this.showSysDefaultColumn && this.columnRows[i].isSysDefault)
        ) {
          continue;
        }

        cols.push(this.columnRows[i]);
      }
      return cols;
    }
  },
  data() {
    let form = {};
    if (this.metadata != undefined) {
      form = { ...this.metadata };
    }

    return {
      form,
      showSysDefaultColumn: false,
      currentDataModel: {},
      fetchDetails: false,
      columnRows: [],
      ruleRows: [],
      viewColumns: [],
      x6Designer: createDesigner(),
      propColumns: [
        {
          title: '序号',
          width: 60,
          dataIndex: 'sortOrder',
          align: 'center',
          scopedSlots: { customRender: 'sortOrderSlot' }
        },
        {
          title: '显示名称',
          dataIndex: 'title'
        },
        {
          title: '编码',
          dataIndex: 'column'
        },
        { title: '类型', width: 100, dataIndex: 'dataType', scopedSlots: { customRender: 'dataTypeSlot' } },
        {
          title: '长度',
          width: 100,
          dataIndex: 'length',
          scopedSlots: { customRender: 'lengthSlot' }
        },
        {
          title: '是否必填',
          dataIndex: 'notNull',
          width: 100,
          scopedSlots: { customRender: 'notNullSlot' }
        },
        {
          title: '是否唯一',
          width: 100,
          dataIndex: 'unique',
          scopedSlots: { customRender: 'uniqueSlot' }
        },
        {
          title: '描述',
          dataIndex: 'remark'
        },
        {
          title: '操作',
          width: 160,
          dataIndex: '__operation',
          scopedSlots: { customRender: 'operationSlot' }
        }
      ],
      selectViewDyform: undefined,
      dataModelRelaDyform: [],
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      queryDyformOptionLoading: true,
      rules: {
        name: [{ required: true, message: '名称必填', trigger: 'blur' }],
        column: [
          { required: true, message: '编码', trigger: 'blur' },
          { pattern: /^\w+$/, message: '编码只允许包含字母、数字以及下划线', trigger: 'blur' }
        ]
      },
      preserveCodes: [],
      viewDataKey: 'previewData_',
      searchKeyword: undefined,
      selectedRowKeys: [],
      selectedRows: [],
      rowSelection: {
        getCheckboxProps: this.getCheckboxProps,
        onChange: (selectedRowKeys, selectedRows) => {
          this.selectedRowKeys = selectedRowKeys;
          this.selectedRows = selectedRows;
        }
      },
      columnDetailKey: Date.now()
    };
  },
  provide() {
    return {
      x6Designer: this.x6Designer,
      dataModel: this.form,
      viewColumns: this.viewColumns,
      getViewObjectDesign: this.getViewObjectDesign
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.getDataModelDetail();
    this.getPreservedField();
  },
  mounted() {},
  methods: {
    onViewObjectPropChange(item) {},
    getCheckboxProps(item) {
      return item.isSysDefault
        ? {
            // 系统默认字段不可选/不可删除
            props: {
              disabled: true
            },
            style: { display: 'none' }
          }
        : {};
    },
    getViewObjectDesign() {
      return this.$refs.design;
    },
    getPreservedField() {
      $axios
        .get(`/proxy/pt/dyform/definition/getPreservedField`, { params: {} })
        .then(({ data }) => {
          if (data) {
            this.preserveCodes.push(...data);
          }
        })
        .catch(error => {});
    },
    getDataModelRelaDyforms() {
      this.queryDyformOptionLoading = true;
      this.dataModelRelaDyform.splice(0, this.dataModelRelaDyform.length);
      $axios
        .post('/common/select2/query', {
          serviceName: 'formDefinitionService',
          queryMethod: 'queryFormDefinitionSelect',
          tableName: 'UF_' + this.currentDataModel.id,
          includeTypes: 'V',
          pageSize: 1000,
          pageNo: 1
        })
        .then(({ data }) => {
          this.queryDyformOptionLoading = false;
          if (data.results) {
            for (let r of data.results) {
              this.dataModelRelaDyform.push({ label: r.text, value: r.id });
            }
          }
        });
    },
    onClickCreatePageViewBtn() {
      this.getDataModelRelaDyforms();
    },
    confirmCreatePageView(e) {
      if (this.selectViewDyform != undefined) {
        this.$loading('创建视图中');
        dyformPreview.methods
          .createDataModelMisView(
            this.currentDataModel,
            this.selectViewDyform,
            this.currentDataModel.name + '_视图_' + moment().format('yyyyMMDDHHmmss')
          )
          .then(page => {
            this.$loading(false);
            if (page.uuid) {
              window.open(`/page-designer/index?uuid=${page.uuid}`, '_blank');
              e(true);
            } else {
              this.$message.error('创建视图失败');
            }
          })
          .catch(() => {
            this.$message.error('创建视图失败');
            this.$loading(false);
          });
      }
    },
    onDesignMounted() {
      this.$refs.design.init({ id: this.currentDataModel.id, modelJson: this.currentDataModel.modelJson, sql: this.currentDataModel.sql });
    },
    onSqlChange({ sql, sqlParameter, sqlObj }) {
      this.form.sql = sql;
      this.form.sqlParameter = JSON.stringify(sqlParameter);
      this.form.sqlObjJson = JSON.stringify(sqlObj);
    },
    onColumnChange(columns) {
      let oldViewColumns = this.viewColumns;
      let oldMap = {};
      for (let i = 0, len = oldViewColumns.length; i < len; i++) {
        oldMap[oldViewColumns[i].location] = oldViewColumns[i];
      }
      this.viewColumns.splice(0, this.viewColumns.length);
      for (let i = 0, len = columns.length; i < len; i++) {
        let c = columns[i];
        let old = oldMap[c.location];
        if (old) {
          c.remark = old.remark;
          c.hidden = old.hidden;
        }
        this.viewColumns.push(c);
      }

      //处理列变更
      // let viewLocations = [];
      // if (this.viewColumns.length) {
      //   for (let i = 0, len = this.viewColumns.length; i < len; i++) {
      //     viewAlias.push(this.viewColumns[i].alias);
      //   }
      // }
      // let alias = [];
      // for (let i = 0, len = columns.length; i < len; i++) {
      //   alias.push(columns[i].alias);
      //   if (viewAlias.includes(columns[i].alias)) {
      //     continue;
      //   }
      //   this.viewColumns.push(columns[i]);
      // }
    },
    toCreatePageView() {
      // window.open(`/page-designer/index?_temp=${this.currentDataModel.uuid}`, '_blank');
    },
    toCreateDyform() {
      window.open(`/data-model-form/${this.currentDataModel.uuid}`, '_blank');
    },
    addColumn() {},
    saveColumn(callback) {
      let _this = this;
      this.$refs.columnDetail.$refs.form.validate(valid => {
        let formData = this.$refs.columnDetail.form;
        if (valid) {
          if (formData.uuid == undefined) {
            formData.uuid = generateId();
            _this.columnRows.push(deepClone(formData));
          } else {
            // 修改数据
            for (let i = 0, len = _this.columnRows.length; i < len; i++) {
              if (_this.columnRows[i].uuid == formData.uuid) {
                for (let k in formData) {
                  _this.$set(_this.columnRows[i], k, formData[k]);
                }
                break;
              }
            }
          }
          if (callback && typeof callback === 'function') {
            callback();
          } else {
            this.$refs.addModelPropDrawer.hide();
          }
        }
      });
    },
    saveAndNewNextColumn() {
      this.saveColumn(() => {
        this.columnDetailKey = Date.now();
      });
    },
    cancelDrawer() {},
    getDrawerContainer() {
      return document.body;
      return this.$el.querySelector('.ant-card-body');
    },
    removeColumns(selectedRows) {
      const _this = this;
      if (_this.selectedRows.length == 0) {
        _this.$message.error('请选择要删除的记录！');
        return;
      }

      this.$confirm({
        title: '提示',
        content: '确定要删除吗?',
        okText: '确定',
        cancelText: '取消',
        onOk() {
          _this.$loading('删除中');
          let promises = [];
          selectedRows.forEach(item => {
            promises.push(_this.checkColumnCanDrop(item.column));
          });
          Promise.all(promises)
            .then(results => {
              let existsDataFields = [];
              results.forEach((result, index) => {
                if (!result) {
                  existsDataFields.push(_this.selectedRows[index].title);
                }
              });
              if (existsDataFields.length > 0) {
                this.$message.info(`字段[${existsDataFields}]存在数据, 不允许删除`);
              } else {
                selectedRows.forEach(item => {
                  _this.deleteColumn(item.column);
                });
              }
              _this.$loading(false);
            })
            .catch(error => {
              _this.$loading(false);
              _this.$message.error('服务异常！');
            });
        },
        onCancel() {}
      });
    },
    deleteColumn(col) {
      for (let i = 0, len = this.columnRows.length; i < len; i++) {
        if (this.columnRows[i].column == col) {
          this.columnRows.splice(i, 1);
          break;
        }
      }
    },
    removeColumn(item) {
      const _this = this;
      let col = item.column;
      // const deleteCol = () => {
      //   for (let i = 0, len = this.columnRows.length; i < len; i++) {
      //     if (this.columnRows[i].column == col) {
      //       this.columnRows.splice(i, 1);
      //       break;
      //     }
      //   }
      // };

      this.$confirm({
        title: '提示',
        content: '确定要删除吗?',
        okText: '确定',
        cancelText: '取消',
        onOk() {
          if (_this.form.uuid != undefined && _this.form.type == 'TABLE') {
            // 校验字段是否存在数据，存在则不让删除
            _this.$set(item, 'loading', true);
            _this.checkColumnCanDrop(item.column).then(can => {
              if (can) {
                _this.deleteColumn(col);
                // deleteCol.call(this);
              } else {
                _this.$message.info('该字段存在数据, 不允许删除');
                _this.$set(item, 'loading', false);
              }
            });
          } else {
            _this.deleteColumn(col);
            // deleteCol.call(this);
          }
        },
        onCancel() {}
      });
    },
    checkColumnCanDrop(column) {
      // 前端新增的可直接删除
      let originalColumn = this.originalColumns.find(item => item.column == column);
      if (originalColumn == null) {
        return Promise.resolve(true);
      }

      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/dm/canDropColumn/${this.form.uuid}`, { params: { column } })
          .then(({ data }) => {
            resolve(data.data);
          })
          .catch(error => {
            reject();
          });
      });
    },
    getDataModelDetail() {
      $axios.get(`/proxy/api/dm/getDetails`, { params: { uuid: this.metadata.uuid } }).then(({ data }) => {
        if (data.code == 0) {
          this.fetchDetails = true;
          this.currentDataModel = data.data;
          if (data.data.columnJson) {
            this.columnRows.push(...JSON.parse(data.data.columnJson));
            this.originalColumns = deepClone(this.columnRows);
          }
          this.form.remark = data.data && data.data.remark;

          if (this.currentDataModel.type == 'VIEW') {
            if (data.data.columnJson) {
              this.viewColumns.push(...JSON.parse(data.data.columnJson));
              this.form.columnJson = data.data.columnJson;
            } else {
              this.viewColumns = [];
            }

            this.form.sql = this.currentDataModel.sql;
            this.form.sqlObjJson = this.currentDataModel.sqlObjJson;
            this.form.sqlParameter = this.currentDataModel.sqlParameter;
          }
        }
      });
    },
    getDropColumns() {
      let cols = [];
      if (this.originalColumns) {
        let currCols = [],
          currColIds = [];
        for (let i = 0, len = this.columnRows.length; i < len; i++) {
          currCols.push(this.columnRows[i].column);
          currColIds.push(this.columnRows[i].uuid);
        }
        for (let i = 0, len = this.originalColumns.length; i < len; i++) {
          if (!currCols.includes(this.originalColumns[i].column) && !currColIds.includes(this.originalColumns[i].uuid)) {
            cols.push(this.originalColumns[i].column);
          }
        }
      }
      return cols;
    },
    saveDataModel() {
      let _this = this,
        $form = this.$refs.form;

      $form.validate(valid => {
        if (valid) {
          if (_this.form.uuid != undefined && _this.form.type == 'TABLE') {
            let dropCols = _this.getDropColumns();
            if (dropCols.length) {
              _this.preCheckDropColumnUsedByDyform(dropCols).then(() => {
                _this.postSave();
              });
            } else {
              _this.postSave();
            }
          } else {
            _this.postSave();
          }
        }
      });
    },
    postSave() {
      let _this = this;
      let commitData = deepClone(_this.currentDataModel);
      commitData.name = _this.form.name;
      commitData.remark = _this.form.remark;
      if (commitData.type == 'TABLE') {
        commitData.columnJson = JSON.stringify(_this.columnRows);
      } else {
        commitData.columnJson = JSON.stringify(_this.viewColumns);
      }
      commitData.ruleRows = JSON.stringify(_this.ruleRows);

      let graph = _this.x6Designer.graph;
      if (graph != undefined) {
        commitData.modelJson = JSON.stringify(graph.toJSON());
        commitData.sql = _this.form.sql;
        commitData.sqlParameter = _this.form.sqlParameter;
        commitData.sqlObjJson = _this.form.sqlObjJson;
      }

      _this.$loading('保存中');
      $axios
        .post(`/proxy/api/dm/save`, commitData)
        .then(({ data }) => {
          _this.$loading(false);
          if (data.code != 0) {
            _this.$error({
              title: '保存失败',
              content: _this.$createElement('div', {
                domProps: {
                  innerHTML: Array.from(new Set(data.msg.split('\n'))).join('<br>')
                }
              })
            });
            console.error(data);
          } else {
            _this.$message.success('保存成功');
            _this.metadata.name = _this.form.name;
            _this.currentDataModel.name = _this.form.name;
            _this.currentDataModel.remark = _this.form.remark;
            _this.currentDataModel.columnJson = JSON.stringify(_this.columnRows);
            _this.originalColumns = deepClone(_this.columnRows);
            _this.currentDataModel.ruleRows = JSON.stringify(_this.ruleRows);
          }
        })
        .catch(() => {
          _this.$message.error('保存失败');
          _this.$loading(false);
        });
    },
    preCheckDropColumnUsedByDyform(dropCols) {
      return new Promise((resolve, reject) => {
        this.$loading('删除字段校验中');
        this.getRelaDyformDefinitions().then(list => {
          this.$loading(false);
          if (list) {
            let result = [];
            for (let i = 0, len = list.length; i < len; i++) {
              let def = list[i],
                json = def.definitionVjson;
              if (json) {
                json = JSON.parse(json);
                let fields = json.fields;
                let use = { uuid: def.uuid, name: def.name, cols: [] };
                for (let i = 0, len = fields.length; i < len; i++) {
                  if (dropCols.includes(fields[i].configuration.code.toUpperCase())) {
                    use.cols.push(fields[i].configuration.code.toUpperCase());
                  }
                }
                if (use.cols.length) {
                  result.push(use);
                }
              }
            }

            if (result.length) {
              this.$confirm({
                title: '存在删除字段被使用',
                content: h => {
                  return (
                    <div>
                      {result.map(item => (
                        <a-row type="flex" gutter="8" style="margin-bottom:3px;">
                          <a-col>
                            <a href={'/dyform-designer/index?uuid=' + item.uuid} target="_blank">
                              {item.name}{' '}
                            </a>
                            :
                          </a-col>
                          <a-col style="text-align:left">
                            {item.cols.map(c => (
                              <a-tag>{c}</a-tag>
                            ))}
                          </a-col>
                        </a-row>
                      ))}
                    </div>
                  );
                },
                okText: '继续',
                onOk() {
                  resolve();
                },
                onCancel() {}
              });
            } else {
              resolve();
            }
          } else {
            resolve();
          }
        });
      });
    },
    getRelaDyformDefinitions() {
      return new Promise((resolve, reject) => {
        $axios
          .post('/json/data/services', {
            serviceName: 'formDefinitionService',
            methodName: 'findDyFormFormDefinitionByTblName',
            args: JSON.stringify([`UF_${this.form.id}`])
          })
          .then(({ data }) => {
            resolve(data.data);
          });
      });
    },
    onInputId2CaseFormate(e, caseType) {
      if (this.form.id != undefined) {
        if (caseType === 'toUpperCase' || caseType === 'toLowerCase') {
          // 自动转大写
          this.form.id = this.form.id[caseType]();
          let start = e.target.selectionStart;
          this.$nextTick(() => {
            e.target.setSelectionRange && e.target.setSelectionRange(start, start);
          });
        }
      }
    },
    onTabChange(key) {
      if (key == 'dataTab' && this.$refs.previewData) {
        // this.viewDataKey = 'previewData_' + new Date().getTime();
        this.$refs.previewData.refresh();
      }
      // let $drawers = this.$el.querySelector('.ant-card-body').querySelectorAll('.ant-drawer');
      // if ($drawers) {
      //   for (let d of $drawers) {
      //     d.__vue__.$parent.$parent.$parent.hide();
      //   }
      // }
    }
  }
};
</script>
