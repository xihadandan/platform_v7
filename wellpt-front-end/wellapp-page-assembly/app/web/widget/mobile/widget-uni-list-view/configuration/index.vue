<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <!-- 仅有一个tab时加上class="one-tab",不是请移除 -->
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model-item label="标题">
          <a-input v-model="widget.title" />
        </a-form-model-item>
        <a-form-model-item label="JS模块">
          <JsModuleSelect v-model="widget.configuration.jsModules" />
        </a-form-model-item>
        <a-form-model-item label="数据来源">
          <a-radio-group v-model="widget.configuration.rowDataFrom" button-style="solid" size="small" @change="onChangeRowDataFrom">
            <a-radio-button value="dataSource">数据仓库</a-radio-button>
            <a-radio-button value="developSource">数据脚本</a-radio-button>
            <a-radio-button value="dataModel">数据模型</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item
          label="数据仓库"
          v-if="widget.configuration.rowDataFrom === 'dataSource' || widget.configuration.rowDataFrom == undefined"
        >
          <DataStoreSelectModal v-model="widget.configuration.dataSourceId" :displayModal="true" @change="changeDataSourceId" />
        </a-form-model-item>

        <template v-if="widget.configuration.rowDataFrom == 'dataModel'">
          <a-form-model-item label="模型类型">
            <a-radio-group v-model="widget.configuration.dataModelType" button-style="solid" size="small" @change="onChangeDataModelType">
              <a-radio-button value="TABLE">存储对象</a-radio-button>
              <a-radio-button value="VIEW">视图对象</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">
              <span
                style="cursor: pointer"
                :class="widget.configuration.dataModelUuid ? 'ant-btn-link' : ''"
                @click="redirectDataModelDesign(widget.configuration.dataModelUuid)"
                :title="widget.configuration.dataModelUuid ? '打开数据模型' : ''"
              >
                数据模型
                <a-icon type="environment" v-show="widget.configuration.dataModelUuid" style="color: inherit; line-height: 1" />
              </span>
            </template>
            <DataModelSelectModal
              v-model="widget.configuration.dataModelUuid"
              :dtype="widget.configuration.dataModelType"
              :displayModal="true"
              ref="dataModelSelect"
              @change="onDataModelChange"
            />
          </a-form-model-item>
        </template>
        <a-form-model-item :colon="false">
          <span slot="label"></span>
          <a-textarea
            placeholder="默认查询条件"
            v-model="widget.configuration.defaultCondition"
            allowClear
            :autoSize="{ minRows: 3, maxRows: 6 }"
          />
        </a-form-model-item>
        <a-form-model-item label="主键字段">
          <a-select v-model="widget.configuration.primaryField">
            <a-select-option v-for="column in widget.configuration.columns" :key="column.id" :value="column.dataIndex">
              {{ column.title }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-collapse :bordered="false" expandIconPosition="right" defaultActiveKey="1">
          <a-collapse-panel key="1" header="显示设置">
            <a-form-model-item label="数据展示风格">
              <a-radio-group size="small" v-model="widget.configuration.displayStyle" button-style="solid">
                <a-radio-button value="1">列表</a-radio-button>
                <a-radio-button value="2">卡片</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
            <template v-if="false">
              <a-form-model-item>
                <template slot="label">
                  <a-tooltip placement="bottomRight">
                    <template slot="title">
                      <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                        <li>模板切换后需要重新配置字段定义</li>
                        <li>内置模板切换后，自定义模板会重置为内置模板的html代码</li>
                        <li>模板为自定义时，列表实际渲染以自定义模板为准</li>
                      </ul>
                    </template>
                    模板
                    <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                  </a-tooltip>
                </template>
                <a-radio-group
                  size="small"
                  v-model="widget.configuration.itemContentType"
                  button-style="solid"
                  @change="itemContentTypeChange"
                >
                  <a-radio-button value="default">默认</a-radio-button>
                  <a-radio-button value="2">自定义</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <template v-if="widget.configuration.itemContentType == '2'">
                <a-form-model-item label="内置模板">
                  <a-select
                    :options="templateOptions"
                    v-model="widget.configuration.template"
                    :getPopupContainer="getPopupContainerNearestPs()"
                    :dropdownClassName="getDropdownClassName()"
                    @change="onChangeTemplate"
                  ></a-select>
                </a-form-model-item>
                <a-form-model-item label="自定义模板">
                  <WidgetCodeEditor
                    :value="widget.configuration.customTemplateHtml"
                    lang="html"
                    width="700px"
                    height="500px"
                    :hideError="true"
                    ref="templateRef"
                    @save="changeColumnTemplateOptions"
                  >
                    <div slot="help">数据项内容引用的变量用大括号标识，如数据仓库返回的行数据title字段值通过{title}引用</div>
                    <a-button icon="code">编写代码</a-button>
                  </WidgetCodeEditor>
                </a-form-model-item>
              </template>
            </template>
            <a-form-model-item label="字段定义">
              <WidgetDesignDrawer :id="'columnConfigureSet' + widget.id" title="字段定义" :width="1000" :designer="designer">
                <a-button size="small" title="字段定义">
                  <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                </a-button>
                <template slot="content">
                  <WidgetUniListViewColumnConfiguration
                    :configuration="widget.configuration"
                    :widget="widget"
                    :designer="designer"
                    ref="columns"
                    :columnIndexOptions="columnIndexOptions"
                    :columnTemplateOptions="columnTemplateOptions"
                  ></WidgetUniListViewColumnConfiguration>
                </template>
              </WidgetDesignDrawer>
            </a-form-model-item>
            <a-form-model-item label="阅读状态">
              <a-switch v-model="widget.configuration.readMarker" />
            </a-form-model-item>
            <a-form-model-item label="标示字段" v-if="widget.configuration.readMarker">
              <a-select v-model="widget.configuration.readMarkerField">
                <a-select-option v-for="column in widget.configuration.columns" :key="column.id" :value="column.dataIndex">
                  {{ column.title }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
            <a-form-model-item label="每页条数">
              <a-input-number v-model="widget.configuration.pageSize" />
            </a-form-model-item>
            <a-form-model-item>
              <template slot="label">
                <a-tooltip placement="bottomRight">
                  <template slot="title">
                    <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                      <li>排序字段是字段定义内有排序设置的字段</li>
                    </ul>
                  </template>
                  显示排序
                  <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                </a-tooltip>
              </template>
              <a-switch v-model="widget.configuration.showSortOrder" />
            </a-form-model-item>
          </a-collapse-panel>
          <a-collapse-panel key="2" header="查询设置">
            <!-- <a-form-model-item label="搜索">
                <a-switch v-model="widget.configuration.hasSearch" />
              </a-form-model-item> -->
            <!-- <template v-if="widget.configuration.hasSearch"> -->
            <a-form-model-item>
              <template slot="label">
                <a-tooltip placement="bottomRight">
                  <template slot="title">
                    <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                      <li>关键字字段是字段定义的映射字段</li>
                    </ul>
                  </template>
                  关键字搜索
                  <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                </a-tooltip>
              </template>
              <a-switch v-model="widget.configuration.keyword" @change="widget.configuration.hasSearch = widget.configuration.keyword" />
            </a-form-model-item>
            <FieldSearchConfiguration
              :widget="widget"
              :designer="designer"
              :columnIndexOptions="columnIndexOptions"
            ></FieldSearchConfiguration>
            <!-- </template> -->
          </a-collapse-panel>
          <a-collapse-panel key="3" header="操作设置">
            <a-form-model-item label="行点击">
              <a-switch v-model="widget.configuration.rowClickEvent.enable"></a-switch>
            </a-form-model-item>
            <a-form-model-item label="行点击事件" v-if="widget.configuration.rowClickEvent.enable">
              <!-- <a-input v-model="widget.configuration.detailPageUrl" /> -->
              <WidgetDesignDrawer
                :closeOpenDrawer="false"
                :id="'widgetListItemClickConfig' + widget.id"
                title="事件管理"
                :designer="designer"
                width="600px"
              >
                <a-button title="事件管理设置">
                  <Icon key="setting" type="pticon iconfont icon-ptkj-shezhi" />
                </a-button>
                <template slot="content">
                  <WidgetEventHandler :widget="widget" :eventModel="widget.configuration.rowClickEvent.option" :designer="designer" />
                </template>
              </WidgetDesignDrawer>
            </a-form-model-item>
            <ListButtonsConfiguration :widget="widget" :designer="designer"></ListButtonsConfiguration>
          </a-collapse-panel>
        </a-collapse>
      </a-tab-pane>
      <a-tab-pane key="2" tab="样式设置">
        <UniListStyleConfiguration
          :target="widget.configuration.styleConfiguration"
          :configuration="widget.configuration"
          :displayStyle="widget.configuration.displayStyle"
        ></UniListStyleConfiguration>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>
<style></style>
<script type="text/babel">
import { map, each, findIndex } from 'lodash';
import { generateId, deepClone } from '@framework/vue/utils/util';
import WidgetUniListViewColumnConfiguration from './column-configuration.vue';
import FieldSearchConfiguration from './field-search-configuration.vue';
import ListButtonsConfiguration from './list-buttons-configuration.vue';
import { getPopupContainerNearestPs, getDropdownClassName } from '@framework/vue/utils/function.js';
import UniListStyleConfiguration from './style-configuration.vue';
export default {
  name: 'WidgetUniListViewConfiguration',
  components: { WidgetUniListViewColumnConfiguration, FieldSearchConfiguration, ListButtonsConfiguration, UniListStyleConfiguration },
  mixins: [],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    let columnIndexOptions = [];
    if (this.widget.configuration.columns.length && this.widget.configuration.columns[0].id) {
      for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
        columnIndexOptions.push({
          value: this.widget.configuration.columns[i].dataIndex,
          label: this.widget.configuration.columns[i].title
        });
      }
    }
    let defaultTemplateOptions = [
      { label: '标题', value: 'title' },
      { label: '小标题', value: 'subtitle' },
      { label: '备注', value: 'note' },
      { label: '右侧文本', value: 'rightText' },
      { label: '底部左侧', value: 'bottomLeft' },
      { label: '底部右侧', value: 'bottomRight' }
    ];
    return {
      dataSourceOptions: [],
      columnIndexOptions,
      templateOptions: [],
      columnTemplateOptions: undefined,
      defaultTemplateOptions
    };
  },

  beforeCreate() {},
  computed: {},
  created() {
    if (!this.widget.configuration.rowClickEvent) {
      this.$set(this.widget.configuration, 'rowClickEvent', { enable: false, option: {} });
    }
    if (!this.widget.configuration.hasOwnProperty('styleConfiguration')) {
      this.$set(this.widget.configuration, 'styleConfiguration', {
        backgroundStyle: this.widget.configuration.backgroundStyle,
        mainStyle: this.widget.configuration.mainStyle
      });
    }
  },
  methods: {
    redirectDataModelDesign(uuid) {
      if (uuid) {
        window.open(`/data-model-design/index?uuid=${uuid}`, '_blank');
      }
    },
    getPopupContainerNearestPs,
    getDropdownClassName,
    fetchDataSourceOptions(value) {
      let _this = this;
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'viewComponentService',
          queryMethod: 'loadSelectData'
        })
        .then(({ data }) => {
          if (data.results) {
            _this.dataSourceOptions = data.results;
          }
        });
    },
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },
    changeDataSourceId(value) {
      this.fetchColumns();
      // 数据源变更，影响
    },
    fetchColumns() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'viewComponentService',
          methodName: 'getColumnsById',
          args: JSON.stringify([this.widget.configuration.dataSourceId])
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            _this.widget.configuration.columns.length = 0;
            _this.columnIndexOptions.length = 0;
            for (let i = 0, len = data.data.length; i < len; i++) {
              _this.columnIndexOptions.push({
                value: data.data[i].columnIndex,
                label: data.data[i].title
              });
              _this.widget.configuration.columns.push({
                id: generateId(),
                title: data.data[i].title,
                dataIndex: data.data[i].columnIndex,
                primaryKey: false,
                ellipsis: true,
                sortable: false,
                hidden: false,
                keywordQuery: false,
                renderFunction: {}
              });
            }
          }
        });
    },

    onChangeDataModelType() {
      this.$refs.dataModelSelect.reset(this.widget.configuration.dataModelType);
    },
    fetchDataModelDetails(uuid, callback) {
      if (uuid) {
        $axios.get(`/proxy/api/dm/getDetails`, { params: { uuid } }).then(({ data, headers }) => {
          if (data.code == 0) {
            this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
            let detail = data.data,
              columns = JSON.parse(detail.columnJson);
            for (let col of columns) {
              if (detail.type == 'TABLE' || (detail.type == 'VIEW' && col.hidden !== true && col.return === true)) {
                this.columnIndexOptions.push({
                  value: col.alias || col.column,
                  label: col.title,
                  isSysDefault: col.isSysDefault
                });
              }
            }
            if (typeof callback == 'function') {
              callback.call(this);
            }
          }
        });
      }
    },
    onDataModelOptionsReady(optionsMap) {
      if (this.widget.configuration.dataModelUuid && optionsMap[this.widget.configuration.dataModelUuid]) {
        this.widget.configuration.dataModelName = optionsMap[this.widget.configuration.dataModelUuid].name;
      }
    },
    onDataModelChange(uuid, { id, name }) {
      this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
      this.widget.configuration.dataModelName = name;
      this.fetchDataModelDetails(uuid, () => {
        this.widget.configuration.columns.splice(0, this.widget.configuration.columns.length);
        this.widget.configuration.dataModelId = id;
        for (let col of this.columnIndexOptions) {
          if (col.isSysDefault && col.value != 'UUID') {
            continue;
          }
          this.widget.configuration.columns.push({
            id: generateId(),
            title: col.label,
            dataIndex: col.value,
            primaryKey: col.value == 'UUID',
            ellipsis: false,
            sortable: false,
            hidden: col.value == 'UUID',
            keywordQuery: false,
            renderFunction: {}
          });
        }
      });
    },
    // 重置字段定义列表
    resetTemplateProperties() {
      // 字段定义默认先添加需要定义的字段
      each(this.columnTemplateOptions, item => {
        this.widget.configuration.templateProperties.push({
          uuid: generateId(),
          title: item.label,
          name: item.value,
          mapColumn: '',
          mapColumnName: '',
          renderer: {},
          sortOrder: ''
        });
      });
    },
    // 模板默认/自定义切换
    itemContentTypeChange() {
      // 清除已配置的字段定义
      this.widget.configuration.templateProperties.splice(0, this.widget.configuration.templateProperties.length);
      this.columnTemplateOptions = undefined;
      if (this.widget.configuration.itemContentType == '2') {
        if (this.widget.configuration.template) {
          this.getMobileListTemplateByBeanName(this.widget.configuration.template, data => {
            this.widget.configuration.customTemplateHtml = data.html;
            if (this.$refs.templateRef && this.$refs.templateRef.editor) {
              this.$refs.templateRef.editor.setValue(this.widget.configuration.customTemplateHtml);
            }
            this.resetTemplateProperties();
          });
        }
      } else if (this.widget.configuration.itemContentType == 'default') {
        this.columnTemplateOptions = deepClone(this.defaultTemplateOptions);
        this.resetTemplateProperties();
      }
    },
    // 获取模板
    getTemplateOptions() {
      let _this = this;
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'mobileListService',
          queryMethod: 'getMobileListTemplate',
          pageSize: 1000,
          pageNo: 1
        })
        .then(({ data }) => {
          if (data.results) {
            _this.templateOptions = map(data.results, (item, index) => {
              return {
                value: item.id,
                label: item.text
              };
            });
          }
        });
    },
    // 选择模板
    onChangeTemplate() {
      // 清除已配置的字段定义
      this.widget.configuration.templateProperties.splice(0, this.widget.configuration.templateProperties.length);
      this.columnTemplateOptions = undefined;
      this.widget.configuration.customTemplateHtml = '';
      if (this.widget.configuration.template) {
        this.getMobileListTemplateByBeanName(this.widget.configuration.template, data => {
          this.widget.configuration.customTemplateHtml = data.html;
          if (this.$refs.templateRef && this.$refs.templateRef.editor) {
            this.$refs.templateRef.editor.setValue(this.widget.configuration.customTemplateHtml);
          }
          this.resetTemplateProperties();
        });
      } else {
        if (this.$refs.templateRef && this.$refs.templateRef.editor) {
          this.$refs.templateRef.editor.setValue(this.widget.configuration.customTemplateHtml);
        }
        this.resetTemplateProperties();
      }
    },
    // 获取模板数据
    getMobileListTemplateByBeanName(id, callback) {
      let _this = this;
      this.$axios
        .post('/json/data/services', {
          argTypes: [],
          args: `[${id}]`,
          methodName: 'getMobileListTemplateByBeanName',
          serviceName: 'mobileListService',
          validate: false
        })
        .then(({ data }) => {
          if (data.data) {
            _this.columnTemplateOptions = map(data.data.properties, (item, index) => {
              return {
                value: index,
                label: item
              };
            });
            if (typeof callback == 'function') {
              callback(data.data);
            }
          }
        });
    },
    // 根据模板修改字段定义中的选项
    changeColumnTemplateOptions(value) {
      this.widget.configuration.customTemplateHtml = value;
      let data = this.getCurlyBracketContent(value);
      each(data, item => {
        let hasIndex = findIndex(this.columnTemplateOptions, { value: item });
        if (hasIndex == -1) {
          this.columnTemplateOptions.push({
            value: item,
            label: item
          });
        }
      });
    },
    //获取字符串中花括号{}内部的内容
    getCurlyBracketContent(str) {
      const regex = /{([^}]+)}/g;
      let matches = [];
      let match;
      while ((match = regex.exec(str))) {
        matches.push(match[1]); // 第一个括号中的内容是整个匹配，第二个括号中的是捕获组的内容
      }
      return matches;
    }
  },
  mounted() {
    // this.fetchDataSourceOptions();
    this.getTemplateOptions();
    if (this.widget.configuration.template && this.widget.configuration.itemContentType == '2') {
      this.getMobileListTemplateByBeanName(this.widget.configuration.template);
    }
    // 初始为默认模板，且字段定义为空时，根据默认属性添加行
    if (this.widget.configuration.itemContentType == 'default' && this.widget.configuration.templateProperties.length == 0) {
      this.columnTemplateOptions = deepClone(this.defaultTemplateOptions);
      this.resetTemplateProperties();
    }
  },
  configuration() {
    return {
      hasSearch: false,
      pageSize: 10,
      readMarker: false,
      columns: [],
      templateProperties: [],
      itemContentType: 'default',
      displayStyle: '1',
      query: {
        fields: [],
        fieldSearch: false
      },
      rowClickEvent: {
        enable: false,
        option: {}
      },
      buttons: [],
      rowBottomMoreButtonType: 'link',
      styleConfiguration: {
        backgroundStyle: {
          backgroundColor: '#ffffff', // 白底
          backgroundImage: undefined,
          backgroundImageInput: undefined,
          bgImageUseInput: false,
          backgroundRepeat: undefined,
          backgroundPosition: undefined
        }
      }
    };
  }
};
</script>
