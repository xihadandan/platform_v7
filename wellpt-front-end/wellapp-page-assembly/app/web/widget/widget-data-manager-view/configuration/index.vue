<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model-item label="名称">
          <a-input v-model="widget.title" @change="onChangeTitle"></a-input>
        </a-form-model-item>
        <a-form-model-item label="类型">
          <a-select
            v-model="widget.configuration.type"
            :options="[
              { label: '表格', value: 'table' },
              { label: '导航与表格', value: 'nav-table' }
            ]"
          />
        </a-form-model-item>
        <a-form-model-item label="JS模块">
          <JsModuleSelect v-model="widget.configuration.jsModules" dependencyFilter="WidgetDataManagerViewDevelopment" width="205px" />
        </a-form-model-item>
        <a-collapse :bordered="false" expandIconPosition="right" defaultActiveKey="operationSetting">
          <a-collapse-panel key="operationSetting" header="操作设置">
            <a-form-model-item label="启用已读">
              <a-switch v-model="widget.configuration.readConfig.enable" @change="e => switchChange(e, 'read')" />
            </a-form-model-item>

            <a-form-model-item label="启用关注">
              <a-switch v-model="widget.configuration.attentionConfig.enable" @change="e => switchChange(e, 'attention')" />
            </a-form-model-item>
            <a-form-model-item label="启用收藏">
              <a-switch v-model="widget.configuration.collectConfig.enable" @change="e => switchChange(e, 'collect')" />
            </a-form-model-item>
            <a-form-model-item label="启用置顶">
              <a-switch v-model="widget.configuration.topConfig.enable" @change="e => switchChange(e, 'top')" />
            </a-form-model-item>
            <a-form-model-item label="启用版本">
              <!-- <template slot="label">
                <a-popover>
                  <template slot="content"></template>
                  启用版本
                  <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"  />
                </a-popover>
              </template>-->
              <a-tooltip placement="left">
                <template slot="title"><div style="font-size: 12px">该配置发生变更后, 需要保存才会生效</div></template>
                <a-switch v-model="widget.configuration.dataVersionConfig.enable" @change="e => switchChange(e, 'saveNewVersion')" />
              </a-tooltip>
            </a-form-model-item>

            <a-collapse :bordered="false" expand-icon-position="right">
              <a-collapse-panel key="1" header="启用标签">
                <div slot="extra" style="margin-right: 26px" @click.stop="() => {}">
                  <a-switch
                    v-model="widget.configuration.tagConfig.enable"
                    @click.native.stop="() => {}"
                    @change="e => switchChange(e, 'tag')"
                  />
                </div>
                <a-form-model-item>
                  <template slot="label">
                    <span
                      style="cursor: pointer"
                      :class="widget.configuration.tagConfig.dataModelUuid ? 'ant-btn-link' : ''"
                      @click="redirectDataModelDesign(widget.configuration.tagConfig.dataModelUuid)"
                      title="跳转数据模型"
                    >
                      标签数据模型
                      <a-icon
                        type="environment"
                        v-show="widget.configuration.tagConfig.dataModelUuid"
                        style="color: inherit; line-height: 1"
                      />
                    </span>
                  </template>
                  <DataModelSelectModal
                    v-model="widget.configuration.tagConfig.dataModelUuid"
                    @change="
                      (v, d) =>
                        onChangeSelectDataModel(v, d, 'tagColumnOptions', widget.configuration.tagConfig, ['titleField', 'colorField'])
                    "
                  />
                </a-form-model-item>
                <div v-show="widget.configuration.tagConfig.dataModelUuid != undefined">
                  <a-form-model-item label="标签名称字段">
                    <a-select v-model="widget.configuration.tagConfig.titleField" :options="tagColumnOptions" />
                  </a-form-model-item>
                  <a-form-model-item label="标签颜色字段">
                    <a-select v-model="widget.configuration.tagConfig.colorField" :options="tagColumnOptions" allowClear />
                  </a-form-model-item>
                </div>
              </a-collapse-panel>
              <a-collapse-panel key="2" header="启用分类">
                <div slot="extra" style="margin-right: 26px" @click.stop="() => {}">
                  <a-switch
                    v-model="widget.configuration.classifyConfig.enable"
                    @change="e => switchChange(e, 'classify')"
                    @click.native.stop="() => {}"
                  />
                </div>

                <div v-show="widget.configuration.classifyConfig.enable">
                  <a-form-model-item>
                    <template slot="label">
                      <span
                        style="cursor: pointer"
                        :class="widget.configuration.classifyConfig.dataModelUuid ? 'ant-btn-link' : ''"
                        @click="redirectDataModelDesign(widget.configuration.classifyConfig.dataModelUuid)"
                        title="跳转数据模型"
                      >
                        分类数据模型
                        <a-icon
                          type="environment"
                          v-show="widget.configuration.classifyConfig.dataModelUuid"
                          style="color: inherit; line-height: 1"
                        />
                      </span>
                    </template>

                    <DataModelSelectModal
                      v-model="widget.configuration.classifyConfig.dataModelUuid"
                      @change="
                        (v, d) =>
                          onChangeSelectDataModel(v, d, 'classifyColumnOptions', widget.configuration.classifyConfig, [
                            'parentField',
                            'titleField'
                          ])
                      "
                    />
                  </a-form-model-item>
                  <div v-show="widget.configuration.classifyConfig.dataModelUuid != undefined">
                    <a-form-model-item label="父级字段">
                      <a-select v-model="widget.configuration.classifyConfig.parentField" :options="classifyColumnOptions" allowClear />
                    </a-form-model-item>
                    <a-form-model-item label="节点名称字段">
                      <a-select v-model="widget.configuration.classifyConfig.titleField" :options="classifyColumnOptions" />
                    </a-form-model-item>
                    <a-form-model-item label="数据分类特性">
                      <a-select
                        v-model="widget.configuration.classifyConfig.type"
                        :options="[
                          { label: '数据仅唯一分类', value: 'ONE_TO_ONE' },
                          { label: '数据支持多分类', value: 'ONE_TO_MANY' }
                        ]"
                      />
                    </a-form-model-item>
                    <a-form-model-item label="数据旧分类删除" v-if="widget.configuration.classifyConfig.type === 'ONE_TO_MANY'">
                      <a-switch v-model="widget.configuration.classifyConfig.override" />
                    </a-form-model-item>
                  </div>
                </div>
              </a-collapse-panel>
              <!-- <a-collapse-panel key="3" header="启用数据权限">
                <div slot="extra" style="margin-right: 26px" @click.stop="() => {}">
                  <a-switch v-model="widget.configuration.authConfig.enable" @click.native.stop="() => {}" />
                </div>

                <div v-show="widget.configuration.authConfig.enable">
                  <a-form-model-item label="允许数据被访问"></a-form-model-item>
                   <a-checkbox-group
                    style="width: 100%; margin: 0 85px"
                    v-model="widget.configuration.authConfig.allowAccess"
                    @change="onChangeAccess"
                  >
                    <a-row type="flex">
                      <a-col flex="105px">
                        <a-checkbox value="OWN" :disabled="true">创建者</a-checkbox>
                      </a-col>
                      <a-col flex="105px">
                        <a-checkbox value="JOB">职位</a-checkbox>
                      </a-col>
                      <a-col flex="105px">
                        <a-checkbox value="DEPT">部门</a-checkbox>
                      </a-col>
                      <a-col flex="105px">
                        <a-checkbox value="ORG_LEVEL">同级组织节点</a-checkbox>
                      </a-col>
                      <a-col flex="105px">
                        <a-checkbox value="UP_ORG_LEVEL">上级组织节点</a-checkbox>
                      </a-col>
                      <a-col flex="105px">
                        <a-checkbox value="SUB_ORG_LEVEL">下级组织节点</a-checkbox>
                      </a-col>
                      <a-col flex="105px">
                        <a-checkbox value="ROLE">角色</a-checkbox>
                      </a-col>
                    </a-row>
                  </a-checkbox-group>

                  <a-form-model-item label="角色" v-if="widget.configuration.authConfig.allowAccess.includes('ROLE')">
                    <RoleSelect v-model="widget.configuration.authConfig.allowAccessRoles" width="200px" />
                  </a-form-model-item>
                </div>
              </a-collapse-panel> -->
            </a-collapse>
          </a-collapse-panel>

          <!-- <a-collapse-panel key="styleSetting" header="样式设置"></a-collapse-panel> -->
        </a-collapse>
      </a-tab-pane>
      <a-tab-pane key="2" tab="导航" v-if="widget.configuration.type === 'nav-table'">
        <a-form-model-item label="导航类型">
          <a-radio-group v-model="widget.configuration.nav.type" button-style="solid" size="small">
            <a-radio-button value="treeNav">树形导航</a-radio-button>
            <a-radio-button value="menuNav">菜单导航</a-radio-button>
            <!-- <a-radio-button value="orgNav">组织导航</a-radio-button> -->
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item>
          <template slot="label">
            <span
              style="cursor: pointer"
              :class="widget.configuration.nav.dataModelUuid ? 'ant-btn-link' : ''"
              @click="redirectDataModelDesign(widget.configuration.nav.dataModelUuid)"
              :title="widget.configuration.nav.dataModelUuid ? '打开数据对象' : ''"
            >
              数据对象
              <a-icon type="environment" v-show="widget.configuration.nav.dataModelUuid" style="color: inherit; line-height: 1" />
            </span>
          </template>
          <DataModelSelectModal
            v-model="widget.configuration.nav.dataModelUuid"
            ref="dataModelSelect"
            @change="
              (v, d) => onChangeSelectDataModel(v, d, 'navDataModelColumnOptions', widget.configuration.nav, ['parentField', 'titleField'])
            "
          />
        </a-form-model-item>
        <!-- <a-form-model-item label="数据分类特性">
          <a-select
            v-model="widget.configuration.nav.type"
            :options="[
              { label: '数据仅唯一分类', value: 'ONE_TO_ONE' },
              { label: '数据支持多分类', value: 'ONE_TO_MANY' }
            ]"
          />
        </a-form-model-item> -->
        <!-- <a-form-model-item label="唯一标识字段" >
          <a-select v-model="widget.configuration.nav.primaryField" :options="navDataModelColumnOptions" />
        </a-form-model-item> -->
        <a-form-model-item label="父级字段" v-if="widget.configuration.nav.type === 'treeNav'">
          <a-select v-model="widget.configuration.nav.parentField" :options="navDataModelColumnOptions" allowClear />
        </a-form-model-item>
        <a-form-model-item label="节点名称字段">
          <a-select v-model="widget.configuration.nav.titleField" :options="navDataModelColumnOptions" />
        </a-form-model-item>
        <a-form-model-item
          label="关联表格查询包括子节点"
          v-show="widget.configuration.nav.type === 'treeNav' && widget.configuration.nav.parentField != undefined"
        >
          <a-switch v-model="widget.configuration.nav.cascadeChildQuery" />
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="3" tab="表格" forceRender>
        <TableBasicConfiguration :widget="widget.configuration.WidgetTable" :designer="designer" :editRule="tableEditRule">
          <a-collapse-panel key="tableStylePanel" v-if="designer.terminalType == 'mobile'">
            <div slot="header" style="color: var(--w-primary-color)">样式设置</div>
            <UniListStyleConfiguration
              :target="widget.configuration.WidgetTable.configuration.uniConfiguration.styleConfiguration"
              :configuration="widget.configuration.WidgetTable.configuration.uniConfiguration"
              :displayStyle="widget.configuration.WidgetTable.configuration.uniConfiguration.displayStyle"
            ></UniListStyleConfiguration>
          </a-collapse-panel>
        </TableBasicConfiguration>
      </a-tab-pane>
      <a-tab-pane key="4" tab="表单" forceRender>
        <DyformBasicConfiguration :widget="widget.configuration.WidgetDyformSetting" :designer="designer" :editRule="formSettingEditRule" />
        <a-collapse :bordered="false" expandIconPosition="right">
          <a-collapse-panel key="showSetting" header="显示设置">
            <a-form-model-item label="打开方式">
              <a-select
                v-model="widget.configuration.formOpenType"
                :options="[
                  { label: '弹窗', value: 'widgetModal' },
                  { label: '新窗口', value: 'newWindow' }
                ]"
                @change="onChangeOpenType"
              />
            </a-form-model-item>
            <div v-show="widget.configuration.formOpenType === 'widgetModal'">
              <a-form-model-item label="弹窗规格">
                <a-select
                  :options="[
                    { label: '小', value: 'small' },
                    { label: '中', value: 'middle' },
                    { label: '大', value: 'large' },
                    { label: '自定义', value: 'selfDefine' }
                  ]"
                  v-model="widget.configuration.WidgetModal.configuration.size"
                  :style="{ width: '100%' }"
                ></a-select>
              </a-form-model-item>
              <a-form-model-item :label="null" v-if="widget.configuration.WidgetModal.configuration.size === 'selfDefine'">
                <a-row>
                  <a-col :span="12">
                    宽
                    <a-input-number v-model="widget.configuration.WidgetModal.configuration.width" style="width: 70px" />
                  </a-col>
                  <a-col :span="12">
                    高
                    <a-input-number v-model="widget.configuration.WidgetModal.configuration.height" style="width: 70px" />
                  </a-col>
                </a-row>
              </a-form-model-item>
            </div>
          </a-collapse-panel>
        </a-collapse>
      </a-tab-pane>
      <a-tab-pane key="5" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer" :codeSnippets="[]"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script type="text/babel">
import TableBasicConfiguration from '../../widget-table/configuration/table-basic-configuration.vue';
import DyformBasicConfiguration from '../../widget-dyform-setting/configuration/dyform-basic-configuration.vue';
import { generateId } from '@framework/vue/utils/util';
import WidgetTableConfiguration from '../../widget-table/configuration/index.vue';
import UniListStyleConfiguration from '../../mobile/widget-uni-list-view/configuration/style-configuration.vue';
import WidgetDyformSettingConfiguration from '../../widget-dyform-setting/configuration/index.vue';
export default {
  name: 'WidgetDataManagerViewConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object
  },

  data() {
    return {
      navDataModelColumnOptions: [],
      classifyColumnOptions: [],
      tagColumnOptions: [],
      // 定义配置限制规则
      formSettingEditRule: {
        title: {
          hidden: true
        },
        button: {}
      },
      tableEditRule: {
        title: {
          hidden: false
        },
        rowDataFrom: {
          hidden: true
        },
        dataModelType: {
          hidden: true
        },
        button: {}
      }
    };
  },

  beforeCreate() {},
  components: { TableBasicConfiguration, DyformBasicConfiguration, UniListStyleConfiguration },
  computed: {},
  created() {
    if (this.widget.configuration.jsModules == undefined) {
      this.$set(this.widget.configuration, 'jsModules', []);
    }
    this.reCreateTableEditRule();
    if (this.widget.configuration.hasOwnProperty('WidgetTable')) {
      if (!this.widget.configuration.WidgetTable.configuration.hasOwnProperty('uniConfiguration')) {
        this.$set(this.widget.configuration.WidgetTable.configuration, 'uniConfiguration', {
          displayStyle: '2', //1列表2卡片
          showSortOrder: true,
          readMarker: false,
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
      if (!this.widget.configuration.WidgetTable.configuration.uniConfiguration.hasOwnProperty('styleConfiguration')) {
        this.$set(this.widget.configuration.WidgetTable.configuration.uniConfiguration, 'styleConfiguration', {
          backgroundStyle: this.widget.configuration.WidgetTable.configuration.uniConfiguration.backgroundStyle,
          mainStyle: this.widget.configuration.WidgetTable.configuration.uniConfiguration.mainStyle
        });
      }
    }
  },
  methods: {
    redirectDataModelDesign(uuid) {
      if (uuid) {
        window.open(`/data-model-design/index?uuid=${uuid}`, '_blank');
      }
    },
    onChangeTitle() {
      this.widget.configuration.WidgetModal.title = `${this.widget.title} - 弹窗`;
      this.widget.configuration.WidgetTable.title = `${this.widget.title} - 表格`;
      this.widget.configuration.WidgetDyformSetting.title = `${this.widget.title} - 表单设置`;
    },
    onChangeAccess() {},
    reCreateTableEditRule() {
      let headerButtons = this.widget.configuration.WidgetTable.configuration.headerButton.buttons,
        rowButtons = this.widget.configuration.WidgetTable.configuration.rowButton.buttons;
      for (let btn of [...headerButtons, ...rowButtons]) {
        if (btn.required) {
          this.tableEditRule.button[btn.id] = {
            deleteHidden: true,
            // eventHandlerHidden: true
            eventHandlerRule: {
              name: false,
              triggerSelectable: false,
              actionTypeSelectable: false,
              jsFunction: false,
              actionSelect: false,
              dyformSetable: false,
              displayState: false,
              pageSelect: false,
              targetPosition: false,
              widgetEvent: false
            }
          };
        }
      }
      let formButtons = this.widget.configuration.WidgetDyformSetting.configuration.button.buttons;
      for (let btn of formButtons) {
        if (btn.required) {
          this.formSettingEditRule.button[btn.id] = {
            deleteHidden: true,
            // eventHandlerHidden: true
            eventHandlerRule: {
              name: false,
              triggerSelectable: false,
              actionTypeSelectable: false,
              jsFunction: false,
              actionSelect: false,
              dyformSetable: false,
              displayState: false,
              pageSelect: false,
              targetPosition: false,
              widgetEvent: false
            }
          };
        }
      }
    },
    generateButton(id, title, code, eventHandler, displayPosition) {
      return {
        id,
        code,
        title,
        defaultVisible: true,
        displayPosition,
        required: true,
        role: [],
        defaultVisibleVar: {
          enable: false,
          operator: 'true'
        },
        eventHandler: {
          trigger: 'click',
          ...eventHandler
        },
        switch: {},
        style: {
          textHidden: false,
          type: 'default'
        }
      };
    },
    switchChange(checked, key) {
      let id = this.widget.id,
        headerButtons = this.widget.configuration.WidgetTable.configuration.headerButton.buttons,
        formButtons = this.widget.configuration.WidgetDyformSetting.configuration.button.buttons;
      let removeBtns = eventIds => {
        for (let i = 0; i < headerButtons.length; i++) {
          if (eventIds.includes(headerButtons[i].eventHandler.eventId) && headerButtons[i].eventHandler.eventWid == id) {
            headerButtons.splice(i--, 1);
          }
        }
      };
      if (key === 'attention') {
        if (checked) {
          headerButtons.push(
            ...[
              this.generateButton(`${generateId()}`, '关注', key, {
                actionType: 'widgetEvent',
                eventId: 'setAsAttent',
                eventWid: id
              }),
              this.generateButton(`${generateId()}`, '取消关注', 'cancel' + key, {
                actionType: 'widgetEvent',
                eventId: 'cancelAttent',
                eventWid: id
              })
            ]
          );
        } else {
          removeBtns(['setAsAttent', 'cancelAttent']);
        }
      } else if (key === 'collect') {
        if (checked) {
          headerButtons.push(
            ...[
              this.generateButton(`${generateId()}`, '收藏', key, {
                actionType: 'widgetEvent',
                eventId: 'setAsCollect',
                eventWid: id
              }),
              this.generateButton(`${generateId()}`, '取消收藏', 'cancel' + key, {
                actionType: 'widgetEvent',
                eventId: 'cancelCollect',
                eventWid: id
              })
            ]
          );
        } else {
          removeBtns(['setAsCollect', 'cancelCollect']);
        }
      } else if (key === 'top') {
        if (checked) {
          headerButtons.push(
            ...[
              this.generateButton(`${generateId()}`, '置顶', key, {
                actionType: 'widgetEvent',
                eventId: 'setAsTop',
                eventWid: id
              }),
              this.generateButton(`${generateId()}`, '取消置顶', 'cancel' + key, {
                actionType: 'widgetEvent',
                eventId: 'cancelTop',
                eventWid: id
              })
            ]
          );
        } else {
          removeBtns(['setAsTop', 'cancelTop']);
        }
      } else if (key === 'saveNewVersion') {
        if (checked) {
          formButtons.push({
            id: `${generateId()}`,
            code: key,
            title: '保存为新版本',
            visibleType: 'visible',
            required: true,
            visibleCondition: {
              defineCondition: { cons: [], operator: 'and' },
              enableDefineCondition: false,
              formStateConditions: [],
              userRoleConditions: []
            },
            style: {
              type: 'default',
              textHidden: false
            },
            eventHandler: [
              {
                action: 'BasicWidgetDyformDevelopment.saveNewVersion',
                actionType: 'dataManager',
                id: '1',
                name: '保存为新版本',
                trigger: 'click'
              }
            ]
          });

          formButtons.push({
            id: `${generateId()}`,
            code: 'showVersionDataList',
            title: '查看版本列表',
            visibleType: 'visible',
            required: true,
            visibleCondition: {
              defineCondition: { cons: [], operator: 'and' },
              enableDefineCondition: false,
              formStateConditions: [],
              userRoleConditions: []
            },
            style: {
              type: 'default',
              textHidden: false
            },
            eventHandler: [
              {
                action: 'BasicWidgetDyformDevelopment.showVersionDataList',
                actionType: 'dataManager',
                id: '1',
                name: '查看版本列表',
                trigger: 'click'
              }
            ]
          });
        } else {
          for (let i = 0; i < formButtons.length; i++) {
            if (formButtons[i].required && (formButtons[i].code == 'saveNewVersion' || formButtons[i].code == 'showVersionDataList')) {
              formButtons.splice(i--, 1);
            }
          }
        }
      } else if (key == 'tag') {
        if (checked) {
          headerButtons.push(
            this.generateButton(`${generateId()}`, '设置标签', key, {
              actionType: 'widgetEvent',
              eventId: 'setAsTag',
              eventWid: id
            })
          );
        } else {
          removeBtns(['setAsTag']);
        }
      } else if (key == 'classify') {
        if (checked) {
          headerButtons.push(
            this.generateButton(`${generateId()}`, '分类', key, {
              actionType: 'widgetEvent',
              eventId: 'setAsClassify',
              eventWid: id
            })
          );
        } else {
          removeBtns(['setAsClassify']);
        }
      }

      this.reCreateTableEditRule();
    },
    onChangeOpenType() {
      // 修改数据管理操作按钮的目标位置
      let openType = this.widget.configuration.formOpenType;
      let headerButtons = this.widget.configuration.WidgetTable.configuration.headerButton.buttons,
        rowButtons = this.widget.configuration.WidgetTable.configuration.rowButton.buttons;
      let dmsId = this.widget.configuration.WidgetDyformSetting.id;
      for (let btn of [...headerButtons, ...rowButtons]) {
        if (btn.eventHandler && btn.eventHandler.dmsId == dmsId) {
          btn.eventHandler.targetPosition = openType;
        }
      }
    },
    getDataModelColumns(uuid, callback) {
      let _this = this;
      $axios.get(`/proxy/api/dm/getDetails`, { params: { uuid } }).then(({ data, headers }) => {
        if (data.code == 0) {
          let detail = data.data,
            columns = JSON.parse(detail.columnJson);
          if (typeof callback === 'function') {
            callback.call(_this, columns);
          }
        }
      });
    },
    onChangeSelectDataModel(v, data, columnOptionKey, tar, relaFields) {
      if (v) {
        this.getDataModelColumns(v, columns => {
          this[columnOptionKey] = [];
          tar.dataModelId = data.id;

          for (let col of columns) {
            // 排除系统默认字段
            if (!col.isSysDefault) {
              this[columnOptionKey].push({
                label: col.title,
                value: col.column
              });
            }
          }
        });
      } else {
        this[columnOptionKey] = [];
        tar.dataModelId = undefined;
      }
      if (relaFields != undefined) {
        for (let f of relaFields) {
          this.$set(tar, f, undefined);
        }
      }
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

      for (let key of ['classifyConfig', 'tagConfig', 'nav']) {
        if (wgt.configuration[key].dataModelUuid) {
          elements.push({
            ref: true,
            functionType: 'dataModel',
            exportType: 'dataModel',
            configType: '1',
            uuid: wgt.configuration[key].dataModelUuid,
            id: wgt.configuration[key].dataModelUuid
          });
        }
      }

      functionElements[wgt.id] = elements;

      return functionElements;
    }
  },
  mounted() {
    if (this.widget.configuration.classifyConfig.enable && this.widget.configuration.classifyConfig.dataModelUuid) {
      this.onChangeSelectDataModel(
        this.widget.configuration.classifyConfig.dataModelUuid,
        { id: this.widget.configuration.classifyConfig.dataModelId },
        'classifyColumnOptions',
        this.widget.configuration.classifyConfig
      );
    }
    if (this.widget.configuration.tagConfig.enable && this.widget.configuration.tagConfig.dataModelUuid) {
      this.onChangeSelectDataModel(
        this.widget.configuration.tagConfig.dataModelUuid,
        { id: this.widget.configuration.tagConfig.dataModelId },
        'tagColumnOptions',
        this.widget.configuration.tagConfig
      );
    }
    if (this.widget.configuration.type == 'nav-table' && this.widget.configuration.nav.dataModelUuid) {
      this.onChangeSelectDataModel(
        this.widget.configuration.nav.dataModelUuid,
        { id: this.widget.configuration.nav.dataModelId },
        'navDataModelColumnOptions',
        this.widget.configuration.nav
      );
    }
  },
  configuration() {
    let generateWidgetModalConfiguration = () => {
      return {
        id: generateId(),
        wtype: 'WidgetModal',
        name: '数据模型 - 弹窗',
        title: '数据模型 - 弹窗',
        configuration: {
          size: 'small',
          width: 600,
          height: 300,
          footerHidden: false,
          defaultVisible: false,
          widgets: [],
          footerButton: {
            buttons: [
              {
                id: generateId(),
                code: 'cancel',
                title: '取消',
                CANCEL_BUTTON: true,
                visible: true,
                style: {
                  textHidden: false
                },
                eventHandler: {
                  eventParams: []
                }
              },
              {
                id: generateId(),
                OK_BUTTON: true,
                code: 'ok',
                title: '确定',
                visible: true,
                style: {
                  textHidden: false,
                  type: 'primary'
                },
                eventHandler: {
                  eventParams: []
                }
              }
            ],
            buttonGroup: {
              type: 'notGroup',
              groups: [],
              dynamicGroupName: '更多',
              dynamicGroupBtnThreshold: 3
            }
          },
          style: {},
          afterCloseEventHandler: {
            eventParams: []
          }
        }
      };
    };
    let generateWidgetDyformSettingConfiguration = () => {
      return {
        id: generateId(),
        wtype: 'WidgetDyformSetting',
        title: '数据模型 - 表单',
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
          buttonPosition: 'bottom', // disable / top / bottom
          enableStateForm: false, // 按状态设置表单
          editStateFormUuid: undefined, //编辑表单
          labelStateFormUuid: undefined, //查阅表单
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
              {
                id: `${generateId()}`,
                code: 'save',
                title: '保存',
                visibleType: 'visible',
                required: true,
                visibleCondition: {
                  defineCondition: { cons: [], operator: 'and' },
                  enableDefineCondition: false,
                  formStateConditions: [],
                  userRoleConditions: []
                },
                style: {
                  type: 'primary',
                  textHidden: false
                },
                eventHandler: [
                  {
                    action: 'BasicWidgetDyformDevelopment.save',
                    actionType: 'dataManager',
                    id: generateId(),
                    name: '保存',
                    trigger: 'click'
                  }
                ]
              }
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
    let WidgetModal = generateWidgetModalConfiguration();
    let WidgetDyformSetting = generateWidgetDyformSettingConfiguration();

    let generateWidgetTableConfiguration = (wModalId, wDyformSettingId) => {
      return {
        id: generateId(),
        wtype: 'WidgetTable',
        title: '数据模型 - 表格',
        configuration: {
          bordered: true,
          columnTitleHidden: false,
          rowDataFrom: 'dataModel',
          dataModelType: 'TABLE',
          dataSourceId: null,
          dataSourceName: null,
          columns: [],
          rowSelectAble: true,
          rowSelectType: 'checkbox',
          displayCardList: false,
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
            enable: true,
            buttons: [...getDefaultButtons(wModalId, wDyformSettingId)],
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
          rowButton: {
            enable: true,
            buttons: [
              generateButton(
                `${generateId()}`,
                '编辑',
                'edit',
                {
                  action: 'BasicWidgetDyformDevelopment.modify',
                  actionType: 'dataManager',
                  containerWid: `${wModalId}`,
                  displayState: 'edit',
                  dmsId: `${wDyformSettingId}`,
                  targetPosition: 'widgetModal'
                },
                'rowEnd'
              ),
              generateButton(
                `${generateId()}`,
                '删除',
                'delete',
                {
                  action: 'BasicWidgetDyformDevelopment.delete',
                  actionType: 'dataManager',
                  dmsId: `${wDyformSettingId}`
                },
                'rowEnd'
              )
            ],
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
          defaultSort: [], // 默认排序
          uniConfiguration: {
            displayStyle: '2', //1列表2卡片
            showSortOrder: true,
            readMarker: false,
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
        }
      };
    };
    let generateButton = (id, title, code, eventHandler, displayPosition) => {
      return {
        id,
        code,
        title,
        defaultVisible: true,
        displayPosition,
        required: true,
        defaultVisibleVar: {
          enable: false,
          operator: 'true'
        },
        eventHandler: {
          trigger: 'click',
          ...eventHandler
        },
        switch: {},
        style: {
          textHidden: false,
          type: 'default'
        }
      };
    };
    let getDefaultButtons = (wModalId, wDyformSettingId) => {
      return [
        generateButton(`${generateId()}`, '新增', 'add', {
          action: 'BasicWidgetDyformDevelopment.open',
          actionType: 'dataManager',
          containerWid: `${wModalId}`,
          displayState: 'edit',
          dmsId: `${wDyformSettingId}`,
          targetPosition: 'widgetModal',
          eventParams: []
        }),
        generateButton(`${generateId()}`, '删除', 'batchDelete', {
          action: 'BasicWidgetDyformDevelopment.delete',
          actionType: 'dataManager',
          dmsId: `${wDyformSettingId}`
        })
      ];
    };

    return {
      code: undefined,
      type: 'table', // table \ nav-table
      nav: {
        type: 'treeNav',
        dataModelUuid: undefined,
        primaryField: 'UUID',
        parentField: undefined,
        titleField: undefined
      },
      formOpenType: 'widgetModal',
      dataVersionConfig: {
        enable: false
      },
      readConfig: {
        enable: false
      },
      attentionConfig: {
        enable: false
      },
      collectConfig: {
        enable: false
      },
      topConfig: {
        enable: false
      },
      tagConfig: {
        enable: false,
        primaryField: 'UUID'
      },
      classifyConfig: {
        enable: false,
        type: 'ONE_TO_ONE',
        primaryField: 'UUID'
      },
      authConfig: {
        enable: false,
        allowAccess: ['OWN'],
        allowAccessRoles: []
      },
      // 表格设置
      WidgetTable: generateWidgetTableConfiguration(WidgetModal.id, WidgetDyformSetting.id),

      // 表单设置
      WidgetDyformSetting,

      // 弹窗
      WidgetModal
    };
  }
};
</script>
