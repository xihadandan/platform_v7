<template>
  <div>
    <div>
      <a-form-model
        class="pt-form"
        :model="form"
        :label-col="labelCol"
        :wrapper-col="wrapperCol"
        :rules="formRules"
        ref="form"
        :colon="false"
      >
        <a-tabs v-model="activeTab" class="pt-tabs">
          <a-tab-pane :key="1" tab="基本信息" :style="{ height: 'calc(100vh - 110px)', overflowY: 'auto' }">
            <a-form-model-item label="名称" prop="name">
              <a-input v-model="form.name" allow-clear />
            </a-form-model-item>
            <a-form-model-item label="ID" prop="id">
              <a-input v-model="form.id" readOnly />
            </a-form-model-item>
            <a-form-model-item label="所属模块">
              <a-select
                v-model="form.moduleId"
                showSearch
                allow-clear
                :filter-option="filterFormOption"
                :getPopupContainer="getPopupContainerNearestPs()"
                :dropdownClassName="getDropdownClassName()"
              >
                <a-select-option v-for="d in allModuleOptions" :key="d.id">
                  {{ d.text }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
            <a-form-model-item label="编号" prop="code">
              <a-input v-model="form.code" allow-clear />
            </a-form-model-item>
            <a-form-model-item label="类型" prop="type">
              <a-select
                v-model="form.type"
                showSearch
                allow-clear
                @change="typeChange"
                :getPopupContainer="getPopupContainerNearestPs()"
                :dropdownClassName="getDropdownClassName()"
              >
                <a-select-option v-for="d in allTypeOptions" :key="d.id">
                  {{ d.text }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
            <a-form-model-item
              label="数据库来源"
              v-if="
                ['DATA_STORE_TYPE_TABLE', 'DATA_STORE_TYPE_VIEW', 'DATA_STORE_TYPE_NAME_QUERY', 'DATA_STORE_TYPE_SQL'].includes(form.type)
              "
            >
              <a-radio-group v-model="form.dbSourceType" button-style="solid" size="small" @change="onChangeDbSourceType">
                <a-radio-button value="INNER_DS">内部数据库</a-radio-button>
                <a-radio-button value="EXTERNAL_DS">外部数据库</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
            <a-form-model-item label="数据库连接" v-if="form.dbSourceType == 'EXTERNAL_DS'">
              <a-select
                v-model="form.dbLinkConfUuid"
                showSearch
                allow-clear
                :options="dbLinkOptions"
                :getPopupContainer="getPopupContainerNearestPs()"
                :dropdownClassName="getDropdownClassName()"
                @change="onChangeDbLinkConfigSelect"
              ></a-select>
            </a-form-model-item>
            <template v-if="form.type == 'DATA_STORE_TYPE_DATA_INTERFACE'">
              <a-form-model-item label="数据接口">
                <a-select
                  v-model="form.dataInterfaceName"
                  showSearch
                  allow-clear
                  @change="dataInterfaceNameChange"
                  :filter-option="filterFormOption"
                  :getPopupContainer="getPopupContainerNearestPs()"
                  :dropdownClassName="getDropdownClassName()"
                >
                  <a-select-option v-for="d in allDataInterfaceNameOptions" :key="d.id">
                    {{ d.text }}
                  </a-select-option>
                </a-select>
              </a-form-model-item>
              <a-form-model-item label="接口使用说明" v-if="form.interfaceDesc">
                {{ form.interfaceDesc }}
              </a-form-model-item>
              <a-form-model-item :label="item.name" v-for="item in dataInterfaceParam" :key="item.id">
                <template v-if="item.domType == 'INPUT'">
                  <a-input v-model="initParams[item.id]" allow-clear :placeholder="item.placeholder" />
                </template>
                <template v-else-if="item.domType == 'SELECT' || item.domType == 'MULTI_SELECT'">
                  <a-select
                    v-model="initParams[item.id]"
                    showSearch
                    allow-clear
                    :mode="item.domType == 'MULTI_SELECT' ? 'multiple' : 'default'"
                    :placeholder="item.placeholder"
                    :filter-option="filterFormOption"
                    :getPopupContainer="getPopupContainerNearestPs()"
                    :dropdownClassName="getDropdownClassName()"
                  >
                    <a-select-option v-for="d in item.options" :key="d.id">
                      {{ d.text }}
                    </a-select-option>
                  </a-select>
                </template>
                <template v-else-if="item.domType == 'RADIO'">
                  <a-radio-group v-model="initParams[item.id]">
                    <a-radio v-for="d in item.options" :key="d.id">{{ d.text }}</a-radio>
                  </a-radio-group>
                </template>
                <template v-else-if="item.domType == 'CHECKBOX'">
                  <a-checkbox-group v-model="initParams[item.id]">
                    <a-checkbox v-for="d in item.options" :key="d.id">{{ d.text }}</a-checkbox>
                  </a-checkbox-group>
                </template>
                <template v-else-if="item.domType == 'CUSTOM'">
                  <component
                    :ref="'custom_' + form.id"
                    :is="item.defaultValue"
                    :key="form.id"
                    :datastore="form"
                    :item="item"
                    :params="initParams"
                  ></component>
                </template>
              </a-form-model-item>
            </template>
            <template v-if="form.type == 'DATA_STORE_TYPE_ENTITY'">
              <a-form-model-item label="实体名">
                <a-select
                  v-model="form.entityName"
                  showSearch
                  allow-clear
                  :filter-option="filterFormOption"
                  @change="entityNameChange"
                  :getPopupContainer="getPopupContainerNearestPs()"
                  :dropdownClassName="getDropdownClassName()"
                >
                  <a-select-option v-for="d in allEntityNameOptions" :key="d.id">
                    {{ d.text }}
                  </a-select-option>
                </a-select>
              </a-form-model-item>
            </template>
            <template v-if="form.type == 'DATA_STORE_TYPE_NAME_QUERY'">
              <a-form-model-item label="SQL命名查询">
                <a-select
                  v-model="form.sqlName"
                  showSearch
                  allow-clear
                  :filter-option="filterFormOption"
                  @change="sqlNameChange"
                  :getPopupContainer="getPopupContainerNearestPs()"
                  :dropdownClassName="getDropdownClassName()"
                >
                  <a-select-option v-for="d in allSqlNameOptions" :key="d.id">
                    {{ d.text }}
                  </a-select-option>
                </a-select>
              </a-form-model-item>
            </template>
            <template v-if="form.type == 'DATA_STORE_TYPE_SQL'">
              <a-form-model-item label="SQL语句">
                <a-textarea v-model="form.sqlStatement" :auto-size="{ minRows: 3 }" allow-clear @blur="sqlStatementChange" />
              </a-form-model-item>
            </template>
            <template v-if="form.type == 'DATA_STORE_TYPE_TABLE'">
              <a-form-model-item label="数据表">
                <a-select
                  v-model="form.tableName"
                  showSearch
                  allow-clear
                  :filter-option="filterFormOption"
                  @search="tableNameSearch"
                  @change="tableNameChange"
                  :getPopupContainer="getPopupContainerNearestPs()"
                  :dropdownClassName="getDropdownClassName()"
                >
                  <a-icon type="loading" slot="suffixIcon" v-if="tableNameSearching" />
                  <a-select-option v-for="d in allTableNameOptions" :key="d.id">
                    {{ d.text }}
                  </a-select-option>
                </a-select>
              </a-form-model-item>
            </template>
            <template v-if="form.type == 'DATA_STORE_TYPE_VIEW'">
              <a-form-model-item label="视图">
                <a-select
                  v-model="form.viewName"
                  showSearch
                  allow-clear
                  :filter-option="filterFormOption"
                  @change="viewNameChange"
                  :getPopupContainer="getPopupContainerNearestPs()"
                  :dropdownClassName="getDropdownClassName()"
                >
                  <a-select-option v-for="d in allViewNameOptions" :key="d.id">
                    {{ d.text }}
                  </a-select-option>
                </a-select>
              </a-form-model-item>
            </template>

            <a-form-model-item
              label="列索引"
              v-if="!form.uuid && form.type && ['DATA_STORE_TYPE_DATA_INTERFACE', 'DATA_STORE_TYPE_ENTITY'].indexOf(form.type) == -1"
            >
              <a-checkbox v-model="form.camelColumnIndex" @change="camelColumnIndexChange">使用驼峰风格</a-checkbox>
              <div>
                注：用于兼容旧数据，对新增的SQL命名查询、SQL语句、数据库表、数据库视图有效，建议使用驼峰风格的列索引，列索引的变更会影响已使用该数据的视图配置
              </div>
            </a-form-model-item>
            <a-form-model-item>
              <template slot="label">
                <a-popover trigger="hover">
                  <template slot="content">
                    <div>默认条件，用于数据查询时的默认条件，支持SQL语法，且支持以下系统变量：</div>
                    <div>
                      <div>:currentUserName : 当前用户名</div>
                      <div>:currentLoginName : 当前用户登录名</div>
                      <div>:currentUserId : 当前用户ID</div>
                      <div>:currentUserDepartmentId : 当前用户主部门ID</div>
                      <div>:currentUserDepartmentName : 当前用户主部门名称</div>
                      <div>:sysdate : 系统当前时间</div>
                    </div>
                  </template>
                  默认条件
                  <a-icon type="question-circle" />
                </a-popover>
              </template>
              <a-textarea v-model="form.defaultCondition" :auto-size="{ minRows: 3 }" allow-clear />
            </a-form-model-item>
            <a-form-model-item
              label="默认排序"
              v-if="form.type && ['DATA_STORE_TYPE_DATA_INTERFACE', 'DATA_STORE_TYPE_NAME_QUERY'].indexOf(form.type) == -1"
            >
              <a-textarea v-model="form.defaultOrder" :auto-size="{ minRows: 3 }" allow-clear />
            </a-form-model-item>
          </a-tab-pane>
          <a-tab-pane :key="2">
            <template slot="tab">列定义</template>
            <a-space style="margin-bottom: 12px">
              <a-button @click="changeDefaultShownColumns" :disabled="!selectedRowKeys.length">默认显示</a-button>
              <a-button @click="changeDefaultHiddenColumns" :disabled="!selectedRowKeys.length">默认隐藏</a-button>
            </a-space>
            <a-table
              :data-source="form.columnDefinitionBeans"
              :pagination="false"
              :row-key="(record, index) => index"
              :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange, columnWidth: 46 }"
              style="margin-bottom: 20px"
              :scroll="{ y: 'calc(100vh - 248px)' }"
            >
              <a-table-column key="title" data-index="title" title="标题">
                <template slot-scope="text, record">
                  <a-input v-model="record.title" allow-clear />
                </template>
              </a-table-column>
              <a-table-column key="columnName" data-index="columnName" title="列名"></a-table-column>
              <a-table-column key="columnIndex" data-index="columnIndex" title="列索引" :width="160"></a-table-column>
              <a-table-column key="dataType" data-index="dataType" title="数据类型" :width="88"></a-table-column>
              <a-table-column key="hidden" data-index="hidden" title="默认隐藏" :width="88">
                <template slot-scope="text, record">
                  <a-switch v-model="record.hidden" />
                </template>
              </a-table-column>
            </a-table>
          </a-tab-pane>
        </a-tabs>
        <a-modal v-model="modalVisible" title="预览数据仓库" :width="800">
          <a-table
            :data-source="previewTableData"
            :columns="previewTableColumns"
            :pagination="previewTablePagination"
            :row-key="(record, index) => index"
            :scroll="{ x: 600, y: 600 }"
            :bordered="true"
          ></a-table>
        </a-modal>
        <div
          class="btn_has_space"
          :style="{
            textAlign: $widgetDrawerContext ? 'right' : 'center',
            borderTop: $widgetDrawerContext ? '1px solid var(--w-border-color-light)' : '',
            padding: '8px 20px'
          }"
        >
          <a-button @click="saveAndNewNextData" v-if="!form.uuid" type="primary" :disabled="!editable">保存并添加下一个</a-button>
          <a-button @click="closeDrawer" v-if="$widgetDrawerContext">取消</a-button>
          <a-button @click="saveForm" type="primary" :disabled="!editable">保存</a-button>
          <a-button @click="preview" v-if="form.uuid" :disabled="!editable">预览</a-button>
          <a-button @click="update" v-if="form.uuid" :disabled="!editable">更新列定义</a-button>
        </div>
      </a-form-model>
    </div>
  </div>
</template>

<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import { getPopupContainerNearestPs, getDropdownClassName, psScrollResize } from '@framework/vue/utils/function.js';
import { each, assignIn, map, find, isEmpty, debounce } from 'lodash';
import '@modules/.webpack.runtime.wtemplate.js';
export default {
  name: 'DatabaseDetail',
  props: {
    uuid: String,
    displayState: {
      type: String,
      default: 'edit' // edit、label
    }
  },
  components: {},
  inject: ['pageContext', 'vPageState', '$event'],
  data() {
    return {
      activeTab: 1,
      form: { columnDefinitionBeans: [], camelColumnIndex: true, dbSourceType: 'INNER_DS' },
      labelCol: { span: 4, style: 'text-align:right;padding-right:4px;' },
      wrapperCol: { span: 19 },
      rules: {
        name: [{ required: true, message: '名称必填', trigger: ['blur', 'change'] }],
        id: { required: true, message: 'id必填', trigger: ['blur', 'change'] },
        code: { required: true, message: '编号必填', trigger: ['blur', 'change'] },
        type: { required: true, message: '类型必填', trigger: ['blur', 'change'] }
      },
      allModuleOptions: [],
      allTypeOptions: [],
      allTableNameOptions: [],
      allViewNameOptions: [],
      allEntityNameOptions: [],
      allSqlNameOptions: [],
      allDataInterfaceNameOptions: [],
      dataInterfaceParam: [],
      initParams: {},
      typeParams: {
        DATA_STORE_TYPE_DATA_INTERFACE: 'dataInterfaceName',
        DATA_STORE_TYPE_ENTITY: 'entityName',
        DATA_STORE_TYPE_NAME_QUERY: 'sqlName',
        DATA_STORE_TYPE_SQL: 'sqlStatement',
        DATA_STORE_TYPE_TABLE: 'tableName',
        DATA_STORE_TYPE_VIEW: 'viewName'
      },
      modalVisible: false,
      previewTableData: [],
      previewTableColumns: [],
      previewTablePagination: {
        pageSize: 25,
        current: 1,
        total: 0,
        showTotal: total => `共 ${total} 条数据`,
        onChange: current => {
          this.getDsData(current);
        }
      },
      columnDefinitionBeans: [], //列定义
      wTemplate: {
        $options: {
          methods: {
            initFormData: this.initFormData
          },
          META: {
            method: {
              initFormData: '新增/编辑表单'
            }
          }
        }
      },
      selectedRowKeys: [],
      selectedRows: [],
      dbLinkOptions: [],
      tableNameSearching: false,
      columnLoading: false,
      $widgetDrawerContext: undefined
    };
  },
  META: {
    method: {
      initFormData: '新增/编辑表单',
      saveAndNewNextData: '保存并添加下一个'
    }
  },
  computed: {
    formRules() {
      let rules = assignIn({}, this.rules);
      return rules;
    },
    editable() {
      return this.form.isRef != 1;
    }
  },
  watch: {},
  beforeCreate() {},
  created() {
    this.getAllOptions();
  },
  beforeMount() {
    let _this = this;
    this.fetchDbLinkConfOptions();
  },
  mounted() {
    if (!this.form.id) {
      this.$set(this.form, 'id', 'CD_DS_' + generateId('SF'));
    }
    let $event = this._provided.$event;
    this.$widgetDrawerContext = this._provided && this._provided.widgetDrawerContext;
    if ($event && $event.meta) {
      this.initFormData($event);
    }
  },
  methods: {
    // 保存并添加下一个
    saveAndNewNextData() {
      this.saveForm(() => {
        this.initFormData(this._provided.$event);
      });
    },
    onChangeDbSourceType() {
      this.onChangeDbLinkConfigSelect();
    },
    onChangeDbLinkConfigSelect() {
      this.allTableNameOptions.splice(0, this.allTableNameOptions.length);
      this.form.tableName = undefined;
      this._columnTableDataRefresh([]);
      if (this.form.dbLinkConfUuid != undefined) {
        // 数据表
        this.tableNameSearching = true;
        this.getSelectOptions(
          {
            serviceName: 'cdDataStoreDefinitionService',
            queryMethod: 'loadSelectDataByTable',
            pageSize: 100,
            tableName: this.form.tableName,
            paramsJSONString: JSON.stringify({
              dbLinkConfUuid: this.form.dbLinkConfUuid
            })
          },
          'allTableNameOptions',
          () => {
            this.tableNameSearching = false;
          }
        );
      }
    },
    fetchDbLinkConfOptions() {
      this.$axios
        .get(`/proxy/api/basicdata/datasource/dblink/getAllDblinkConfig`, { params: {} })
        .then(({ data }) => {
          if (data.code == 0 && data.data.length) {
            for (let d of data.data) {
              this.dbLinkOptions.push({
                label: d.name,
                value: d.uuid
              });
            }
          }
        })
        .catch(error => {});
    },
    getPopupContainerNearestPs,
    getDropdownClassName,
    initFormData(data) {
      this.$refs.form.clearValidate();
      this.form.id = '';
      this.selectedRowKeys = [];
      this.selectedRows = [];
      this.dataInterfaceParam = [];
      this.initParams = {};
      if (data && data.eventParams) {
        this.form.id = data.eventParams.id || '';
      }
      if (!this.form.id) {
        let form = {
          id: 'CD_DS_' + generateId('SF'),
          columnDefinitionBeans: [],
          camelColumnIndex: true,
          dbSourceType: 'INNER_DS'
        };
        this.$set(this, 'form', form);
      } else {
        this.getFormData();
      }
    },
    getFormData() {
      let _this = this;
      $axios.get('/proxy/api/datastore/getBeanById/' + this.form.id).then(({ data }) => {
        if (data.code == 0 && data.data) {
          this.form = data.data;
          if (data.data.dataInterfaceParam) {
            this.initParams = JSON.parse(data.data.dataInterfaceParam);
          }
          if (data.data.type) {
            this.typeChange(data.data.type, '', { isInit: true });
          }
        }
      });
    },
    getAllOptions() {
      // 模块
      this.getSelectOptions(
        {
          serviceName: 'appModuleMgr',
          queryMethod: 'loadSelectData',
          searchValue: '',
          params: {}
        },
        'allModuleOptions'
      );
      // 分类
      this.getSelectOptions(
        {
          serviceName: 'dataDictionaryService',
          queryMethod: '',
          type: 'APP_PT_DATA_STORE_TYPE'
        },
        'allTypeOptions'
      );
      // 数据表
      this.tableNameSearching = true;
      this.getSelectOptions(
        {
          serviceName: 'cdDataStoreDefinitionService',
          queryMethod: 'loadSelectDataByTable',
          pageSize: 100,
          tableName: this.form.tableName,
          paramsJSONString: JSON.stringify({
            dbLinkConfUuid: this.form.dbLinkConfUuid
          })
        },
        'allTableNameOptions',
        () => {
          this.tableNameSearching = false;
        }
      );
      // 视图
      this.getSelectOptions(
        {
          serviceName: 'cdDataStoreDefinitionService',
          queryMethod: 'loadSelectDataByView'
        },
        'allViewNameOptions'
      );
      //实体名
      this.getSelectOptions(
        {
          serviceName: 'cdDataStoreDefinitionService',
          queryMethod: 'loadSelectDataByEntity'
        },
        'allEntityNameOptions'
      );
      //SQL命名查询
      this.getSelectOptions(
        {
          serviceName: 'cdDataStoreDefinitionService',
          queryMethod: 'loadSelectDataBySqlName'
        },
        'allSqlNameOptions'
      );
      //数据接口
      this.getSelectOptions(
        {
          serviceName: 'cdDataStoreDefinitionService',
          queryMethod: 'loadSelectDataByDataInterface'
        },
        'allDataInterfaceNameOptions'
      );
    },
    // 下拉框选项
    getSelectOptions(arg, param, callback) {
      let _this = this;
      let params = assignIn({}, { pageSize: arg.pageSize || 1000, pageNo: 1 }, arg);
      _this.$axios.post('/common/select2/query', params).then(({ data }) => {
        if (data.results) {
          if (param) {
            _this[param] = data.results;
          }
          if (typeof callback == 'function') {
            callback(data.results);
          }
        }
      });
    },
    camelColumnIndexChange() {
      if (!this.form.uuid && this.form.type && ['DATA_STORE_TYPE_DATA_INTERFACE', 'DATA_STORE_TYPE_ENTITY'].indexOf(this.form.type) == -1) {
        this.typeChange(this.form.type);
      }
    },
    typeChange(value, option, arg) {
      if (this.form.dbSourceType == undefined) {
        this.$set(this.form, 'dbSourceType', 'INNER_DS');
      }
      if (arg && value) {
        if (arg.isInit || arg.isUpdateCol) {
          let param = this.typeParams[value];
          if (param && this.form[param]) {
            this[param + 'Change'](this.form[param], '', arg);
          }
        }
      } else {
        this._columnTableDataRefresh([]);
        if (value) {
          let param = this.typeParams[value];
          if (param && this.form[param]) {
            this[param + 'Change'](this.form[param], '', arg);
          }
        }
      }
    },
    dataInterfaceNameChange(value, option, arg) {
      if (arg && value) {
        if (arg.isInit) {
          this.getQueryInterfaceParams(value, arg);
        }
        if (arg.isUpdateCol) {
          this._columnTableDataRefresh([]);
          this.loadDataInterfaceColumns({
            dataInterfaceName: value,
            dataInterfaceParam: this.form.dataInterfaceParam
          });
        }
      } else {
        this.form.interfaceDesc = '';
        this._columnTableDataRefresh([]);
        this.dataInterfaceParam = [];
        this.initParams = {};
        if (value) {
          this.loadDataInterfaceColumns({
            dataInterfaceName: value,
            dataInterfaceParam: this.form.dataInterfaceParam
          });
          this.getQueryInterfaceParams(value, arg);
        }
      }
    },
    viewNameChange(value, option, arg) {
      if (arg && value) {
        if (arg.isUpdateCol) {
          this._columnTableDataRefresh([]);
          if (value) {
            this.loadColumns({
              url: `/proxy/api/datastore/loadViewColumns/${value}?camelColumnIndex=${this.form.camelColumnIndex || false}`
            });
          }
        }
      } else {
        this._columnTableDataRefresh([]);
        if (value) {
          this.loadColumns({
            url: `/proxy/api/datastore/loadViewColumns/${value}?camelColumnIndex=${this.form.camelColumnIndex || false}`
          });
        }
      }
    },
    tableNameSearch: debounce(function (searchValue) {
      // 数据表
      this.tableNameSearching = true;
      this.getSelectOptions(
        {
          serviceName: 'cdDataStoreDefinitionService',
          queryMethod: 'loadSelectDataByTable',
          searchValue,
          pageSize: 100,
          paramsJSONString:
            this.form.dbSourceType == 'EXTERNAL_DS'
              ? JSON.stringify({
                  dbLinkConfUuid: this.form.dbLinkConfUuid
                })
              : undefined
        },
        'allTableNameOptions',
        () => {
          this.tableNameSearching = false;
        }
      );
    }, 500),
    tableNameChange(value, option, arg) {
      if (arg && value) {
        if (arg.isUpdateCol) {
          this._columnTableDataRefresh([]);
          if (value) {
            this.loadColumns({
              url: `/proxy/api/datastore/loadTableColumns/${value}?camelColumnIndex=${this.form.camelColumnIndex || false}${
                this.form.dbSourceType == 'EXTERNAL_DS' ? `&dbLinkConfUuid=${this.form.dbLinkConfUuid}` : ``
              }`
            });
          }
        }
      } else {
        this._columnTableDataRefresh([]);
        if (value) {
          this.loadColumns({
            url: `/proxy/api/datastore/loadTableColumns/${value}?camelColumnIndex=${this.form.camelColumnIndex || false}${
              this.form.dbSourceType == 'EXTERNAL_DS' ? `&dbLinkConfUuid=${this.form.dbLinkConfUuid}` : ``
            }`
          });
        }
      }
    },
    sqlNameChange(value, option, arg) {
      if (arg && value) {
        if (arg.isUpdateCol) {
          this._columnTableDataRefresh([]);
          if (value) {
            this.loadColumns({
              url: `/proxy/api/datastore/loadSqlNameColumns/${value}?camelColumnIndex=${this.form.camelColumnIndex || false}${
                this.form.dbSourceType == 'EXTERNAL_DS' ? `&dbLinkConfUuid=${this.form.dbLinkConfUuid}` : ``
              }`
            });
          }
        }
      } else {
        this._columnTableDataRefresh([]);
        if (value) {
          this.loadColumns({
            url: `/proxy/api/datastore/loadSqlNameColumns/${value}?camelColumnIndex=${this.form.camelColumnIndex || false}${
              this.form.dbSourceType == 'EXTERNAL_DS' ? `&dbLinkConfUuid=${this.form.dbLinkConfUuid}` : ``
            }`
          });
        }
      }
    },
    sqlStatementChange(value, option, arg) {
      if (arg && value) {
        if (arg.isUpdateCol) {
          this._columnTableDataRefresh([]);
          if (value) {
            this.loadColumns({
              method: 'post',
              url: `/proxy/api/datastore/loadSqlColumns?sqlStatement=${this.form.sqlStatement}&camelColumnIndex=${
                this.form.camelColumnIndex || false
              }${this.form.dbSourceType == 'EXTERNAL_DS' ? `&dbLinkConfUuid=${this.form.dbLinkConfUuid}` : ``}`,
              data: {
                sqlStatement: this.form.sqlStatement
              }
            });
          }
        }
      } else {
        this._columnTableDataRefresh([]);
        if (value) {
          this.loadColumns({
            method: 'post',
            url: `/proxy/api/datastore/loadSqlColumns?sqlStatement=${this.form.sqlStatement}&camelColumnIndex=${
              this.form.camelColumnIndex || false
            }${this.form.dbSourceType == 'EXTERNAL_DS' ? `&dbLinkConfUuid=${this.form.dbLinkConfUuid}` : ``}`,
            data: {
              sqlStatement: this.form.sqlStatement
            }
          });
        }
      }
    },
    entityNameChange(value, option, arg) {
      if (arg && value) {
        if (arg.isUpdateCol) {
          this._columnTableDataRefresh([]);
          if (value) {
            this.loadColumns({
              url: `/proxy/api/datastore/loadEntityColumns?entityName=${value}`
            });
          }
        }
      } else {
        this._columnTableDataRefresh([]);
        if (value) {
          this.loadColumns({
            url: `/proxy/api/datastore/loadEntityColumns?entityName=${value}`
          });
        }
      }
    },
    getQueryInterfaceParams(value, arg) {
      $axios.get('/proxy/api/datastore/getQueryInterfaceParams', { params: { interfaceName: value } }).then(({ data }) => {
        if (data.data) {
          this.dataInterfaceParam = map(data.data, (item, index) => {
            item.options = [];
            if (item.dataJSON) {
              var dataJSON = JSON.parse(item.dataJSON);
              item.options = map(dataJSON, (citem, cindex) => {
                return {
                  id: cindex,
                  text: citem
                };
              });
            } else if (item.service) {
              //数据接口
              this.getSelectOptions(
                {
                  serviceName: item.service.split('.')[0],
                  queryMethod: item.service.split('.')[1]
                },
                '',
                options => {
                  this.dataInterfaceParam[index].options = options;
                }
              );
            }
            return item;
          });
        } else {
          if (!arg.isInit) {
            this.getInterfaceDesc(value);
          }
        }
      });
    },
    getInterfaceDesc(value) {
      $axios.get('/proxy/api/datastore/getInterfaceDesc', { params: { interfaceName: value } }).then(({ data }) => {
        this.form.interfaceDesc = data.data;
      });
    },
    loadColumns(options, callback) {
      this.columnLoading = true;
      $axios[options.method || 'get'](options.url, options.data)
        .then(({ data }) => {
          this.columnLoading = false;
          if (data.data) {
            this.setColumns(data.data);
          }
        })
        .catch(() => {
          this.columnLoading = false;
        });
    },
    loadDataInterfaceColumns(data) {
      this.columnLoading = true;
      $axios
        .post(`/proxy/api/datastore/postLoadDataInterfaceColumns`, data)
        .then(({ data: result }) => {
          this.columnLoading = false;
          if (result.data) {
            this.setColumns(result.data);
          }
        })
        .catch(() => {
          this.columnLoading = false;
        });
    },
    setColumns(columns) {
      if (this.columnDefinitionBeans.length) {
        each(columns, item => {
          let citem = find(this.columnDefinitionBeans, { columnName: item.columnName });
          if (citem) {
            item.hidden = citem.hidden;
          } else {
            item.hidden = false;
          }
        });
      }
      this._columnTableDataRefresh(columns);
      this.columnDefinitionBeans = [];
    },
    _columnTableDataRefresh(rows) {
      this.form.columnDefinitionBeans = rows;
    },
    filterFormOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },
    preview() {
      if (this.form.columnDefinitionBeans && this.form.columnDefinitionBeans.length) {
        let previewTableColumns = [];
        each(this.form.columnDefinitionBeans, item => {
          if (!item.hidden) {
            previewTableColumns.push({
              title: item.columnIndex,
              dataIndex: item.columnIndex,
              width: 100,
              customRender: (text, record) => {
                return text || '--';
              }
            });
          }
        });
        this.previewTableColumns = previewTableColumns;
        this.getDsData(1);
        this.modalVisible = true;
      }
    },
    getDsData(current) {
      this.previewTablePagination.current = current;
      let arg = [
        {
          dataStoreId: this.form.id,
          proxy: { storeId: this.form.id },
          pagingInfo: {
            pageSize: this.previewTablePagination.pageSize,
            currentPage: this.previewTablePagination.current,
            autoCount: true
          },
          params: { keyword: '' },
          criterions: [],
          renderers: [],
          orders: []
        }
      ];
      $axios
        .post('/json/data/services', {
          serviceName: 'cdDataStoreService',
          methodName: 'loadData',
          args: JSON.stringify(arg),
          validate: false
        })
        .then(({ data }) => {
          if (data.code == 0) {
            this.previewTableData = data.data.data;
            this.previewTablePagination.total = data.data.pagination.totalCount;
          } else {
            this.previewTableData = [];
            this.previewTablePagination.total = 0;
          }
        })
        .catch(e => {
          console.log(e);
          this.previewTableData = [];
          this.previewTablePagination.total = 0;
        });
    },
    update() {
      if (this.form.type) {
        this.activeTab = 2;
        this.columnDefinitionBeans = JSON.parse(JSON.stringify(this.form.columnDefinitionBeans));
        this.typeChange(this.form.type, '', { isUpdateCol: true });
        psScrollResize(this, 0);
      }
    },
    saveForm(callback) {
      console.log('保存表单');
      this.$refs.form.validate(valid => {
        if (valid) {
          this.beforeSaveReq(callback);
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    beforeSaveReq(callback) {
      let promises = [];
      let customFields = this.$refs['custom_' + this.form.id];
      if (customFields) {
        customFields.forEach(customField => {
          promises.push(customField.collectData());
        });
      }
      Promise.all(promises).then(collectDatas => {
        let bean = JSON.parse(JSON.stringify(this.form));
        let initParams = this.initParams;
        collectDatas.forEach(customData => {
          Object.assign(initParams, customData);
        });
        if (isEmpty(initParams)) {
          bean.dataInterfaceParam = null;
        } else {
          bean.dataInterfaceParam = JSON.stringify(initParams);
        }
        if (this.columnLoading) {
          if (this.columnWait == undefined) {
            this.columnWait = this.$message.loading('列定义更新中, 请稍后...', 0);
          }
          setTimeout(() => {
            this.saveForm(callback);
          }, 500);
        } else {
          if (this.form.columnDefinitionBeans && this.form.columnDefinitionBeans.length) {
            bean['columnsDefinition'] = JSON.stringify(this.form.columnDefinitionBeans);
          } else {
            this.$message.error('列定义不允许为空!');
            return false;
          }
          if (!bean.systemUnitId) {
            bean.systemUnitId = this._$USER.systemUnitId;
          }
          this.saveFormData(bean, callback);
        }
      });
    },
    saveFormData(bean, callback) {
      let _this = this;
      $axios
        .post('/proxy/api/datastore/saveBean', bean)
        .then(({ data }) => {
          if (data.code == 0) {
            this.activeTab = 1;
            this.initFormData();
            this.$message.success('保存成功');
            this.pageContext.emitEvent(`refetchDatastoreManageTable`, {});
            if (this.$widgetDrawerContext && typeof callback !== 'function') {
              this.$widgetDrawerContext.close();
            }
            if (typeof callback == 'function') {
              callback();
            }
          } else {
            this.$message.error(data.msg || '保存失败');
          }
        })
        .catch(err => {
          let data = err && err.response && err.response.data;
          this.$message.error(data.msg || '保存失败');
        });
    },
    onSelectChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys;
      this.selectedRows = selectedRows;
    },
    changeDefaultShownColumns() {
      this.selectedRows.forEach(item => (item.hidden = false));
    },
    changeDefaultHiddenColumns() {
      this.selectedRows.forEach(item => (item.hidden = true));
    },
    closeDrawer() {
      if (this.$widgetDrawerContext) {
        this.$widgetDrawerContext.close();
      }
    }
  },
  watch: {
    columnLoading: {
      handler(v) {
        if (!v && this.columnWait) {
          this.columnWait();
          this.columnWait = undefined;
        }
      }
    }
  }
};
</script>
<style lang="less" scoped>
.help-block {
  display: block;
  margin-top: 5px;
  margin-bottom: 10px;
  color: #a6a6a6;
  font-size: 12px;
  line-height: var(--w-line-height);
}
</style>
