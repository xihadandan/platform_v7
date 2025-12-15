<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <TableBasicConfiguration :widget="widget" :designer="designer" />
      </a-tab-pane>

      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer" :codeSnippets="codeSnippets"></WidgetEventConfiguration>
      </a-tab-pane>

      <a-tab-pane key="3" tab="样式设置">
        <UniListStyleConfiguration
          v-if="designer.terminalType == 'mobile'"
          :target="widget.configuration.uniConfiguration.styleConfiguration"
          :configuration="widget.configuration.uniConfiguration"
          :displayStyle="widget.configuration.uniConfiguration.displayStyle"
        ></UniListStyleConfiguration>
        <StyleCodeConfiguration v-else :configuration="widget.configuration" />
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>
<style></style>
<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import TableBasicConfiguration from './table-basic-configuration.vue';
import configurationMixin from '@framework/vue/designer/configurationMixin.js';
import UniListStyleConfiguration from '../../mobile/widget-uni-list-view/configuration/style-configuration.vue';

export default {
  name: 'WidgetTableConfiguration',
  mixins: [configurationMixin],
  props: {
    widget: Object,
    designer: Object
  },
  provide() {
    let widgetTableContext;
    if (EASY_ENV_IS_BROWSER) {
      const tableEl = document.querySelector(`#${this.widget.id}`);
      if (tableEl) {
        widgetTableContext = tableEl.__vue__;
      }
    }
    return {
      widgetTableContext,
      widgetContext: widgetTableContext
    };
  },
  data() {
    return {
      codeSnippets: []
    };
  },

  beforeCreate() {},
  components: {
    TableBasicConfiguration,
    UniListStyleConfiguration
  },
  computed: {},
  created() {
    // 代码片段注入:
    this.codeSnippets = [{ name: '重新刷新表格', content: 'this.refetch();', tabTrigger: 'fetch', trigger: 'fetch|刷新|refresh' }];
    if (!this.widget.configuration.hasOwnProperty('uniConfiguration')) {
      this.$set(this.widget.configuration, 'uniConfiguration', {
        displayStyle: '2', //1列表2卡片
        readMarker: false,
        showSortOrder: true,
        rowButtonPosition: undefined,
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
      });
    }
    if (!this.widget.configuration.uniConfiguration.hasOwnProperty('styleConfiguration')) {
      this.$set(this.widget.configuration.uniConfiguration, 'styleConfiguration', {
        backgroundStyle: this.widget.configuration.uniConfiguration.backgroundStyle,
        mainStyle: this.widget.configuration.uniConfiguration.mainStyle
      });
    }
    if (!this.widget.configuration.uniConfiguration.hasOwnProperty('rowButtonPosition')) {
      this.$set(this.widget.configuration.uniConfiguration, 'rowButtonPosition', undefined);
      this.$set(this.widget.configuration.uniConfiguration, 'rowBottomMoreButtonType', 'link');
    }
  },
  methods: {
    // 生成组件定义数据保存
    getWidgetDefinitionElements(widget) {
      return [
        {
          wtype: widget.wtype,
          id: widget.id,
          title: widget.title,
          definitionJson: JSON.stringify(widget)
        }
      ];
    },

    getWidgetActionElements(wgt, designer) {
      let actions = [];
      let configuration = wgt.configuration;
      actions.push(...this.resolveJsModuleAsActionElement(configuration.jsModules, wgt, designer));
      let btnKeys = ['headerButton', 'rowButton'];
      for (let i = 0, len = btnKeys.length; i < len; i++) {
        let button = configuration[btnKeys[i]];
        if (button && button.buttons) {
          for (let btn of button.buttons) {
            let e = btn.eventHandler;
            actions.push(
              ...this.resolveEventHandlerToActionElement(
                {
                  elementName: btn.title,
                  elementTypeName: '按钮'
                },
                e,
                wgt,
                designer
              )
            );
          }
        }
      }

      actions.push(...this.resolveDefineEventToActionElement(wgt, designer));
      let resolveTemplate = (element, actionSource) => {
        let templateFrom = actionSource.templateFrom;
        return {
          ...element,
          actionType: 'template', // 动作类型
          actionSource, // 动作事件源
          actionObject: {
            // 执行对象
            type: 'template',
            name: templateFrom == 'codeEditor' ? '自定义模板' : actionSource.templateName,
            codeEditor: templateFrom == 'codeEditor',
            sourcePath: templateFrom == 'codeEditor' ? 'template' : 'templateName' // 对象路径，用于从事件源解析对象值，可以在管理端同步修改对应值
          }
        };
      };
      let columns = wgt.configuration.columns;
      for (let c of columns) {
        if (c.clickEvent && c.clickEvent.enable && c.clickEvent.eventHandler) {
          actions.push(
            ...this.resolveEventHandlerToActionElement(
              {
                elementName: c.title,
                elementTypeName: '列'
              },
              c.clickEvent.eventHandler,
              wgt,
              designer
            )
          );
        }
        if (
          c.renderFunction &&
          c.renderFunction.type == 'vueTemplateDataRender' &&
          (c.renderFunction.options.template || c.renderFunction.options.templateName)
        ) {
          actions.push(
            resolveTemplate(
              {
                elementName: c.title, // 元素名称
                elementTypeName: '列' // 元素类型名称
              },
              c.renderFunction.options
            )
          );
        }
      }

      if (configuration.displayCardList) {
        if (configuration.cardTemplateConfig) {
          actions.push(
            resolveTemplate(
              {
                elementName: '表格行', // 元素名称
                elementTypeName: '表格行' // 元素类型名称
              },
              configuration.cardTemplateConfig
            )
          );
        }

        if (configuration.prefixCardTemplateConfig && configuration.prefixCardTemplateConfig.enable) {
          actions.push(
            resolveTemplate(
              {
                elementName: '表格行(前置卡片内容)', // 元素名称
                elementTypeName: '表格行' // 元素类型名称
              },
              configuration.prefixCardTemplateConfig
            )
          );
        }

        if (configuration.suffixCardTemplateConfig && configuration.suffixCardTemplateConfig.enable) {
          actions.push(
            resolveTemplate(
              {
                elementName: '表格行(后置卡片内容)', // 元素名称
                elementTypeName: '表格行' // 元素类型名称
              },
              configuration.suffixCardTemplateConfig
            )
          );
        }
      }

      return actions;
    },

    getFunctionElements(wgt) {
      let functionElements = {},
        elements = [];

      let configuration = wgt.configuration;
      let btnKeys = ['headerButton', 'rowButton'];
      for (let i = 0, len = btnKeys.length; i < len; i++) {
        let button = configuration[btnKeys[i]];
        if (button && button.enable && button.buttons) {
          for (let btn of button.buttons) {
            elements.push({
              id: btn.id,
              uuid: btn.id,
              name: btn.title,
              type: 'button',
              code: btn.id
            });
          }
        }
      }
      // if (configuration.rowClickEvent && configuration.rowClickEvent.enable) {
      //   elements.push({
      //     id: wgt.id + '_ROW_CLICK',
      //     uuid: wgt.id + '_ROW_CLICK',
      //     name: '行点击',
      //     type: 'event',
      //     code: wgt.id + '_ROW_CLICK'
      //   });
      // }

      if (configuration.rowDataFrom == 'dataSource') {
        if (configuration.dataSourceId) {
          elements.push({
            ref: true,
            functionType: 'dataStoreDefinition',
            exportType: 'dataStoreDefinition',
            configType: '1',
            id: configuration.dataSourceId,
            name: configuration.dataSourceName
          });
        }
      } else if (configuration.rowDataFrom == 'dataModel') {
        if (configuration.dataModelUuid) {
          elements.push({
            ref: true,
            functionType: 'dataModel',
            exportType: 'dataModel',
            configType: '1',
            uuid: configuration.dataModelUuid,
            id: configuration.dataModelUuid,
            name: configuration.dataModelName
          });
        }
      }
      functionElements[wgt.id] = elements;

      // 搜索选项内的字典、数据仓库等数据
      let search = configuration.search,
        columnAdvanceSearchGroup = search.columnAdvanceSearchGroup;
      if (columnAdvanceSearchGroup && columnAdvanceSearchGroup.length) {
        for (let i = 0, len = columnAdvanceSearchGroup.length; i < len; i++) {
          let grp = columnAdvanceSearchGroup[i],
            searchInputType = grp.searchInputType;
          if (searchInputType && searchInputType.options) {
            if (searchInputType.options.type == 'dataSource' && searchInputType.options.dataSourceId) {
              elements.push({
                ref: true,
                functionType: 'dataStoreDefinition',
                exportType: 'dataStoreDefinition',
                configType: '1',
                id: searchInputType.options.dataSourceId,
                name: searchInputType.options.dataSourceName
              });
            }
            if (searchInputType.options.type == 'dataDictionary' && searchInputType.options.dataDictionaryUuid) {
              elements.push({
                ref: true,
                functionType: 'cdDataDictionary',
                exportType: 'cdDataDictionary',
                configType: '1',
                id: searchInputType.options.dataDictionaryUuid,
                name: searchInputType.options.dataDictionaryName
              });
            }
          }
        }
      }

      // 导出配置里的数据仓库等数据
      if (configuration.export && configuration.export.exportButtonRule) {
        let exportRule = configuration.export.exportButtonRule;
        for (let id in exportRule) {
          let rule = exportRule[id];
          if (!rule.useTableDataSource) {
            if (rule.dataSourceType == 'dataSource' && rule.dataSourceId) {
              elements.push({
                ref: true,
                functionType: 'dataStoreDefinition',
                exportType: 'dataStoreDefinition',
                configType: '1',
                id: rule.dataSourceId
              });
            }

            if (rule.dataSourceType == 'dataModel' && rule.dataModelUuid) {
              elements.push({
                ref: true,
                functionType: 'dataModel',
                exportType: 'dataModel',
                configType: '1',
                uuid: rule.dataModelUuid,
                id: rule.dataModelUuid
              });
            }
          }
        }
      }

      // 导入配置里的表单与数据模型
      if (configuration.import && configuration.import.importButtonRule) {
        let importRule = configuration.export.importButtonRule;
        for (let id in importRule) {
          let { sheetConfig, importFileTemplate } = importRule[id];
          let formUuids = [],
            dataModelUuids = [];
          sheetConfig.forEach(sheet => {
            if (sheet.params.formUuid) {
              formUuids.push(sheet.params.formUuid);
              dataModelUuids.push(sheet.params.dataModelUuid);
            }
            if (sheet.join && sheet.join.formUuid) {
              formUuids.push(sheet.join.formUuid);
            }
          });
          formUuids.forEach(u => {
            elements.push({
              ref: true,
              functionType: 'formDefinition',
              exportType: 'formDefinition',
              configType: '1',
              uuid: u,
              isProtected: false
            });
          });
          dataModelUuids.forEach(u => {
            elements.push({
              ref: true,
              functionType: 'dataModel',
              exportType: 'dataModel',
              configType: '1',
              uuid: u,
              isProtected: false
            });
          });

          elements.push({
            ref: true,
            functionType: 'logicFileInfo',
            exportType: 'logicFileInfo',
            configType: '1',
            uuid: importFileTemplate.fileID,
            name: importFileTemplate.fileName,
            isProtected: false
          });
        }
      }

      return functionElements;
    }
  },
  mounted() {},
  configuration(widget) {
    let config = {
      bordered: true,
      columnTitleHidden: false,
      rowDataFrom: 'dataSource',
      dataModelUuid: undefined,
      dataModelType: 'TABLE',
      dataSourceId: null,
      dataSourceName: null,
      columns: [],
      isColumnsGroup: false, //表头列分组
      columnsGroupNodes: [],
      rowSelectAble: true,
      rowSelectType: 'checkbox',
      displayCardList: false,
      cardColumnNum: 3,
      toggleDisplay: true,
      defaultDisplayState: 'table',
      enableDefaultCondition: false,
      defaultCondition: undefined,
      enableCustomTable: false,
      rowClickEvent: {
        enable: false,
        eventHandlers: []
      },
      headerButton: {
        enable: false,
        buttons: [],
        buttonGroup: {
          type: 'notGroup', // notGroup: 不分组  fixedGroup: 固定分组 dynamicGroup: 动态分组
          groups: [
            // 固定分组
            // {name:,buttonIds:[]}
          ],
          style: { textHidden: false, type: 'default', icon: undefined, rightDownIconVisible: false },
          dynamicGroupName: '更多', //动态分组名称
          dynamicGroupBtnThreshold: 3 // 分组按钮数阈值，达到该数才触发分组
        }
      },
      rowButton: {
        enable: false,
        buttons: [],
        buttonGroup: {
          type: 'notGroup', // notGroup: 不分组  fixedGroup: 固定分组 dynamicGroup: 动态分组
          groups: [
            // 固定分组
            // {name:,buttonIds:[]}
          ],
          style: { textHidden: false, type: 'default', icon: undefined, rightDownIconVisible: false },
          dynamicGroupName: '更多', //动态分组名称
          dynamicGroupBtnThreshold: 3 // 分组按钮数阈值，达到该数才触发分组
        }
      },
      columns: [],
      pagination: {
        type: 'default',
        pageSizeOptions: ['10', '20', '50', '100', '200'],
        showTotalPage: true,
        showSizeChanger: true,
        pageSize: 10,
        hideOnSinglePage: false
      },
      search: {
        type: 'keywordAdvanceSearch',
        keywordSearchEnable: false,
        keywordSearchColumns: [],
        columnSearchGroupEnable: false,
        advanceSearchEnable: false,
        columnSearchGroup: [],
        columnAdvanceSearchGroup: [],
        advanceSearchPerRowColNumber: 3
      },
      defaultSort: [], // 默认排序,
      mergeCell: {
        isMergeCell: false, // 启用行合并
        mergeCellMode: 'sort', //合并模式
        mergeCellType: 'auto', //合并方式
        mergeCellCols: [],
        mergeIfNull: false,
        isRowMergeCell: false, // 启用列合并
        rowMergeType: 'default',
        rowMergeData: [], // 行合并数据
        rowMergeFunction: undefined
      },
      renderRowByState: {
        // 根据状态值渲染行数据
        enable: false,
        type: 'default', //渲染方式
        stateData: [], //状态值及其对应的渲染样式
        function: undefined
      },
      uniConfiguration: {
        displayStyle: '2', //1列表2卡片
        showSortOrder: true,
        readMarker: false,
        rowButtonPosition: undefined,
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
      }
    };
    if (widget != undefined && widget.useScope === 'bigScreen') {
      // 大屏设计，需要默认相关配置
      config.rowSelectAble = false;
      config.rowSelectType = 'no';
      config.pagination.type = 'no';
      config.pagination.onlyFetchCount = 10;
      config.bordered = false;
    }
    return config;
  }
};
</script>
