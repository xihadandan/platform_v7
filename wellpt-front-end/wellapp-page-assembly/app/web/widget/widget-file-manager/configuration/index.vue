<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="文件库">
        <a-form-model-item label="名称">
          <a-input v-model="widget.title"></a-input>
        </a-form-model-item>
        <a-form-model-item label="所在库">
          <a-select
            v-model="widget.configuration.belongToFolderUuid"
            showSearch
            allow-clear
            :options="fileLibraryOptions"
            :filter-option="filterSelectOption"
            @change="onLibraryChange"
          ></a-select>
        </a-form-model-item>
        <a-form-model-item label="类型">
          <a-select
            v-model="widget.configuration.type"
            :options="[
              { label: '表格', value: 'table' },
              { label: '目录树与表格', value: 'tree-table' }
            ]"
          />
        </a-form-model-item>
        <a-form-model-item label="目录树可搜索" v-if="widget.configuration.type == 'tree-table'">
          <a-switch v-model="widget.configuration.showSearch" />
        </a-form-model-item>
        <a-form-model-item v-if="widget.configuration.type == 'tree-table'" label="回收站">
          <a-switch v-model="widget.configuration.enabledRecycleBin" checked-children="启用" un-checked-children="禁用" />
        </a-form-model-item>
        <template v-if="widget.configuration.type == 'tree-table' && widget.configuration.enabledRecycleBin">
          <a-form-model-item label="回收站名称">
            <a-input v-model="widget.configuration.recycleBinName" />
          </a-form-model-item>
          <a-form-model-item label="回收站图标">
            <WidgetDesignModal
              title="选择图标"
              :zIndex="1000"
              :width="640"
              dialogClass="pt-modal widget-icon-lib-modal"
              :bodyStyle="{ height: '560px' }"
              :maxHeight="560"
              mask
              bodyContainer
            >
              <IconSetBadge v-model="widget.configuration.recycleBinIcon"></IconSetBadge>
              <template slot="content">
                <WidgetIconLib v-model="widget.configuration.recycleBinIcon" />
              </template>
            </WidgetDesignModal>
          </a-form-model-item>
          <a-form-model-item label="回收站表格显示列">
            <a-select
              mode="multiple"
              v-model="widget.configuration.recycleBinTableDisplayColumns"
              showSearch
              allow-clear
              :options="tableWidgetColumnOptions"
              :filter-option="filterSelectOption"
            ></a-select>
          </a-form-model-item>
        </template>
        <a-form-model-item label="显示面包屑导航">
          <a-switch v-model="widget.configuration.showBreadcrumbNav"></a-switch>
        </a-form-model-item>
        <a-form-model-item label="列表数据获取方式">
          <a-radio-group size="small" v-model="widget.configuration.listFileModeType" button-style="solid">
            <a-radio-button value="auto" title="根据夹权限自动获取">自动获取</a-radio-button>
            <a-radio-button value="custom">自定义</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item v-if="widget.configuration.listFileModeType == 'custom'" label="列出当前夹下的">
          <a-select
            v-model="widget.configuration.listFileMode"
            showSearch
            allow-clear
            :options="listFileModeOptions"
            :filter-option="filterSelectOption"
            @change="onLibraryChange"
          ></a-select>
        </a-form-model-item>
        <a-form-model-item label="文件全文检索">
          <a-switch disabled v-model="widget.configuration.enabledFulltextIndex" checked-children="启用" un-checked-children="禁用" />
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="2" tab="表格">
        <TableBasicConfiguration :widget="widget.configuration.WidgetTable" :designer="designer" :editRule="tableEditRule" />
      </a-tab-pane>
      <a-tab-pane key="3" tab="表单">
        <DyformBasicConfiguration :widget="widget.configuration.WidgetDyformSetting" :designer="designer" :editRule="formSettingEditRule" />
      </a-tab-pane>
      <a-tab-pane key="6" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer" :codeSnippets="codeSnippets"></WidgetEventConfiguration>
      </a-tab-pane>

      <a-tab-pane key="7" tab="样式设置">
        <StyleCodeConfiguration :configuration="widget.configuration" />
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script type="text/babel">
import TableBasicConfiguration from '../../widget-table/configuration/table-basic-configuration.vue';
import DyformBasicConfiguration from '../../widget-dyform-setting/configuration/dyform-basic-configuration.vue';
import { generateId } from '@framework/vue/utils/util';
import WidgetTableConfiguration from '../../widget-table/configuration/index.vue';
import WidgetDyformSettingConfiguration from '../../widget-dyform-setting/configuration/index.vue';
export default {
  name: 'WidgetFileManagerConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object
  },
  components: { TableBasicConfiguration, DyformBasicConfiguration },
  data() {
    return {
      fileLibraryOptions: [],
      tableEditRule: {
        title: {
          hidden: true
        },
        rowDataFrom: {
          hidden: true
        },
        dataModelType: {
          hidden: true
        },
        button: {}
      },
      formSettingEditRule: {
        title: {
          hidden: true
        },
        button: {}
      },
      codeSnippets: [],
      listFileModeOptions: [
        { label: '子夹', value: 'listFolder' },
        { label: '所有子夹(包含子夹)', value: 'listAllFolder' },
        { label: '文件', value: 'listFiles' },
        { label: '文件(包含子夹)', value: 'listAllFiles' },
        { label: '子夹及文件', value: 'listFolderAndFiles' },
        { label: '子夹及文件(包含子夹)', value: 'listAllFolderAndFiles' }
      ]
    };
  },
  computed: {
    tableWidgetColumnOptions() {
      return this.widget.configuration.WidgetTable.configuration.columns.map(item => ({ label: item.title, value: item.dataIndex }));
    }
  },
  created() {
    this.loadFileLibrary();
    if (this.widget.configuration.belongToFolderUuid) {
      this.onLibraryChange(this.widget.configuration.belongToFolderUuid, true);
    }
    if (!this.widget.configuration.hasOwnProperty('enabledRecycleBin')) {
      this.$set(this.widget.configuration, 'enabledRecycleBin', false);
      this.$set(this.widget.configuration, 'recycleBinName', '回收站');
    }
  },
  methods: {
    loadFileLibrary() {
      const _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'fileManagerComponentService',
          methodName: 'listFileLibrary'
        })
        .then(({ data: result }) => {
          if (result.data) {
            _this.fileLibraryOptions = result.data.map(item => ({ label: item.name, value: item.uuid }));
            if (_this.widget.configuration.belongToFolderUuid) {
              let fileLibrary = _this.fileLibraryOptions.find(item => item.value == _this.widget.configuration.belongToFolderUuid);
              _this.widget.configuration.belongToFolderName = fileLibrary && fileLibrary.label;
            }
          }
        });
    },
    filterSelectOption(inputValue, option) {
      return (
        (option.componentOptions.propsData.value &&
          option.componentOptions.propsData.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0) ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
      );
    },
    onLibraryChange(value, showInfo = false) {
      if (value) {
        let fileLibrary = this.fileLibraryOptions.find(item => item.value == value);
        this.widget.configuration.belongToFolderName = fileLibrary && fileLibrary.label;

        this.getFolderConfiguration(value).then(folderConfiguration => {
          if (folderConfiguration) {
            if (
              showInfo &&
              this.widget.configuration.belongToFolderName &&
              this.widget.configuration.enabledFulltextIndex != null &&
              this.widget.configuration.enabledFulltextIndex != (folderConfiguration.enabledFulltextIndex || false)
            ) {
              this.$message.info(
                `文件库[${this.widget.configuration.belongToFolderName}]，全文检索配置已变更，需保存页面同步默认按钮配置！`
              );
            }
            this.widget.configuration.enabledFulltextIndex = folderConfiguration.enabledFulltextIndex || false;
          } else {
            this.widget.configuration.enabledFulltextIndex = false;
          }
          let tableHeaderButtons = this.widget.configuration.WidgetTable.configuration.headerButton.buttons;
          let fulltextQueryBtn = tableHeaderButtons.find(btn => btn.code == 'fulltextQuery');
          if (fulltextQueryBtn) {
            fulltextQueryBtn.defaultVisible = this.widget.configuration.enabledFulltextIndex;
          }
        });
      }
    },
    getFolderConfiguration(folderUuid) {
      return $axios
        .post('/json/data/services', {
          serviceName: 'dmsFileManagerService',
          methodName: 'getFolderConfiguration',
          args: JSON.stringify([folderUuid])
        })
        .then(({ data: result }) => {
          return result.data;
        });
    },
    // 生成组件定义数据保存
    getWidgetDefinitionElements(widget) {
      return [
        {
          wtype: widget.wtype,
          id: widget.id,
          title: widget.title,
          definitionJson: JSON.stringify(widget)
        },
        {
          wtype: widget.configuration.WidgetTable.wtype,
          title: widget.configuration.WidgetTable.title,
          id: widget.configuration.WidgetTable.id,
          definitionJson: JSON.stringify(widget.configuration.WidgetTable)
        },
        {
          wtype: widget.configuration.WidgetDyformSetting.wtype,
          title: widget.configuration.WidgetDyformSetting.title,
          id: widget.configuration.WidgetDyformSetting.id,
          definitionJson: JSON.stringify(widget.configuration.WidgetDyformSetting)
        }
      ];
    },
    getFunctionElements(wgt) {
      let functionElements = {},
        elements = [];

      let temp = WidgetDyformSettingConfiguration.methods.getFunctionElements(wgt.configuration.WidgetDyformSetting);
      if (temp[wgt.configuration.WidgetDyformSetting.id]) {
        elements = elements.concat(temp[wgt.configuration.WidgetDyformSetting.id]);
      }
      temp = WidgetTableConfiguration.methods.getFunctionElements(wgt.configuration.WidgetTable);
      if (temp[wgt.configuration.WidgetTable.id]) {
        elements = elements.concat(temp[wgt.configuration.WidgetTable.id]);
      }

      functionElements[wgt.id] = elements;

      return functionElements;
    }
  },
  configuration() {
    console.log('configuration', this);
    let generateTableButton = (id, title, code, eventId, styleType) => {
      return {
        id,
        code,
        title,
        defaultVisible: true,
        required: true,
        defaultVisibleVar: {
          enable: true,
          code: '__TABLE__.dataPermission',
          operator: 'contain',
          value: code
        },
        eventHandler: {
          trigger: 'click',
          actionType: 'widgetEvent',
          actionTypeName: '触发组件事件',
          eventWid: `${this.widget.id}`,
          eventId,
          eventParams: []
        },
        customBtnVisibleOptions: [{ label: '文件库数据权限', value: '__TABLE__.dataPermission' }],
        switch: {},
        style: {
          textHidden: false,
          type: styleType || 'default'
        }
      };
    };
    let getDefaultButtons = () => {
      let buttons = [];
      // buttons.push({
      //   title: '新建文件夹',
      //   id: generateId(),
      //   eventHandler: {
      //     trigger: 'click',
      //     actionType: 'widgetEvent',
      //     actionTypeName: '触发组件事件',
      //     eventWid: `${this.widget.id}`,
      //     eventId: 'createFolder',
      //     eventParams: []
      //   },
      //   defaultVisible: true,
      //   style: { type: 'primary' }
      // });
      buttons.push(generateTableButton(generateId(), '新建文件夹', 'createFolder', 'createFolder', 'primary'));
      buttons.push(generateTableButton(generateId(), '上传', 'uploadFile', 'uploadFile', 'primary'));
      buttons.push(generateTableButton(generateId(), '新建', 'createDocument', 'createDocument', 'primary'));
      buttons.push(generateTableButton(generateId(), '下载', 'download', 'download', 'primary'));
      buttons.push(generateTableButton(generateId(), '删除', 'delete', 'delete', 'danger'));
      buttons.push(generateTableButton(generateId(), '恢复', 'restore', 'restore', 'primary'));
      buttons.push(generateTableButton(generateId(), '重命名', 'rename', 'rename', 'primary'));
      buttons.push(generateTableButton(generateId(), '复制', 'copy', 'copy', 'primary'));
      buttons.push(generateTableButton(generateId(), '移动', 'move', 'move', 'primary'));
      buttons.push(generateTableButton(generateId(), '查看属性', 'viewAttributes', 'viewAttributes', 'default'));
      let fulltextQueryBtn = generateTableButton(generateId(), '全文检索', 'fulltextQuery', 'fulltextQuery', 'default');
      fulltextQueryBtn.defaultVisible = false;
      fulltextQueryBtn.defaultVisibleVar.enable = false;
      buttons.push(fulltextQueryBtn);
      return buttons;
    };
    let generateTableColumn = (dataIndex, title, hidden = false, primaryKey = false, renderFunction) => {
      return {
        id: generateId(),
        dataIndex,
        title,
        primaryKey,
        hidden,
        customVisibleType: 'chooseVisible',
        titleAlign: 'left',
        contentAlign: 'left',
        ellipsis: true,
        renderFunction: renderFunction || { options: {} },
        exportFunction: { options: {} }
      };
    };
    let generateWidgetTableConfiguration = () => {
      return {
        id: generateId(),
        wtype: 'WidgetTable',
        title: '文件管理 - 表格',
        configuration: {
          supportsBeforeTableHeaderWidget: false,
          bordered: true,
          columnTitleHidden: false,
          rowDataFrom: 'dataSource',
          dataSourceId: 'CD_DS_DMS_FILE_MANAGER',
          dataSourceName: '数据管理_文件库',
          dataModelType: 'TABLE',
          columns: [
            generateTableColumn('uuid', 'UUID', true, true),
            generateTableColumn('name', '名称'),
            generateTableColumn('sourcePath', '源路径', true),
            generateTableColumn('deletedTime', '删除时间', true),
            generateTableColumn('deleteUserName', '删除人', true),
            generateTableColumn('code', '编号'),
            generateTableColumn('contentType', '类型', false, false, {
              type: 'customRenderer',
              options: { customType: 'fileTypeRenderer' }
            }),
            generateTableColumn('fileSize', '大小', false, false, { type: 'customRenderer', options: { customType: 'fileSizeRenderer' } }),
            generateTableColumn('creatorName', '创建人'),
            generateTableColumn('createTime', '创建时间'),
            generateTableColumn('modifyTime', '修改时间')
          ],
          rowSelectAble: true,
          rowSelectType: 'checkbox',
          displayCardList: true,
          cardColumnNum: 8,
          useCardTemplate: true,
          cardTemplateConfig: { templateFrom: 'projectCode', templateName: 'file-card-view' },
          toggleDisplay: true,
          defaultDisplayState: 'table',
          enableDefaultCondition: false,
          defaultCondition: undefined,
          enableCustomTable: false,
          rowClickEvent: {
            enable: true,
            eventHandlers: [
              {
                actionType: 'widgetEvent',
                actionTypeName: '触发组件事件',
                eventId: 'viewFile',
                eventParams: [],
                eventWid: this.widget.id,
                name: '查看文件',
                trigger: 'click'
              }
            ]
          },
          headerButton: {
            enable: true,
            buttons: [...getDefaultButtons()],
            buttonGroup: {
              type: 'dynamicGroup', // notGroup: 不分组  fixedGroup: 固定分组 dynamicGroup: 动态分组
              groups: [
                // 固定分组
                // {name:,buttonIds:[]}
              ],
              dynamicGroupName: '更多', //动态分组名称
              dynamicGroupBtnThreshold: 7 // 分组按钮数阈值，达到该数才触发分组
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
              dynamicGroupName: '更多', //动态分组名称
              dynamicGroupBtnThreshold: 3 // 分组按钮数阈值，达到该数才触发分组
            }
          },
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
            keywordSearchEnable: true,
            keywordSearchColumns: ['name'],
            columnSearchGroupEnable: false,
            advanceSearchEnable: false,
            columnSearchGroup: [],
            columnAdvanceSearchGroup: [],
            advanceSearchPerRowColNumber: 3
          },
          defaultSort: [{ id: generateId(), dataIndex: 'code', sortType: 'asc' }] // 默认排序
        }
      };
    };

    let generateDyformSettingButton = (id, title, code, visibleType, formStateConditions, styleType, eventParams = []) => {
      return {
        id,
        code,
        title,
        visibleType: visibleType || 'visible-condition',
        required: true,
        visibleCondition: {
          defineCondition: { cons: [], operator: 'and' },
          enableDefineCondition: false,
          formStateConditions,
          userRoleConditions: []
        },
        style: {
          type: styleType || 'primary',
          textHidden: false
        },
        eventHandler: [
          {
            id: generateId(),
            name: title,
            trigger: 'click',
            actionType: 'jsFunction',
            jsFunction: `FileManagerWidgetDyformSetDevelopment.${code}`,
            eventParams
          }
        ]
      };
    };
    let generateWidgetDyformSettingConfiguration = () => {
      return {
        id: generateId(),
        wtype: 'WidgetDyformSetting',
        title: '文件管理 - 表单',
        configuration: {
          // authConfig: {
          //   enable: false,
          //   allowAccess: ['OWN'],
          //   allowAccessRoles: []
          // },
          title: undefined,
          titleIcon: undefined,
          formUuid: undefined,
          formName: undefined,
          jsModules: [{ key: 'FileManagerWidgetDyformSetDevelopment', label: '文件管理——表单设置' }],
          buttonPosition: 'top', // disable / top / bottom
          enableStateForm: false, // 按状态设置表单
          editStateFormUuid: undefined, //编辑表单
          labelStateFormUuid: undefined, //查阅表单
          useRequestForm: true, // 使用请求参数中的表单定义UUID
          titleVisible: true, //是否展示标题栏
          editStateTitle: undefined, // 编辑状态的标题
          labelStateTitle: undefined, // 查阅状态的标题
          // editStateTitleIcon:undefined,
          // labelStateTitleIcon:''
          titleRenderScript: undefined, // 标题渲染脚本
          formElementRules: [],
          labelStateFormElementRules: [],
          editStateFormElementRules: [],
          button: {
            buttons: [
              generateDyformSettingButton(generateId(), '保存草稿', 'saveAsDraft', 'visible-condition', ['createForm'], 'primary'),
              generateDyformSettingButton(generateId(), '保存', 'save', 'visible-condition', ['createForm', 'edit'], 'primary'),
              generateDyformSettingButton(generateId(), '编辑', 'edit', 'visible-condition', ['label'], 'primary'),
              generateDyformSettingButton(generateId(), '删除', 'delete', 'visible-condition', ['label'], 'danger'),
              generateDyformSettingButton(generateId(), '套打', 'print', 'visible-condition', ['label'], 'default', [
                {
                  id: generateId(),
                  paramKey: 'printTemplateIds',
                  remark: '套打模板ID，多个以分号隔开。没有配置套打模板时，用户可选择有使用权限的模板'
                }
              ])
            ],
            buttonGroup: {
              type: 'notGroup', // notGroup: 不分组  fixedGroup: 固定分组 dynamicGroup: 动态分组
              groups: [
                // 固定分组
                // {name:,buttonIds:[]}
              ],
              dynamicGroupName: '更多', //动态分组名称
              dynamicGroupBtnThreshold: 3 // 分组按钮数阈值，达到该数才触发分组
            },

            buttonAlign: 'center'
          }
        }
      };
    };

    return {
      belongToFolderUuid: '',
      belongToFolderName: '',
      type: 'tree-table',
      showBreadcrumbNav: true,
      enabledFulltextIndex: null,
      showSearch: false,
      listFileModeType: 'auto', // auto根据夹权限配置，custom自定义
      listFileMode: 'listFolderAndFiles',
      enabledRecycleBin: false,
      recycleBinName: '回收站',
      recycleBinIcon:
        '{"iconClass":"ant-iconfont delete","shape":"rect","style":"outlined","showBackground":false,"cssStyle":{"borderRadius":"20%"}}',
      recycleBinTableDisplayColumns: ['name', 'sourcePath', 'deletedTime', 'deleteUserName', 'fileSize', 'contentType'],
      // 表格设置
      WidgetTable: generateWidgetTableConfiguration(),

      // 表单设置
      WidgetDyformSetting: generateWidgetDyformSettingConfiguration()
    };
  }
};
</script>

<style></style>
