<template>
  <a-tabs default-active-key="1">
    <a-tab-pane key="1" tab="设置">
      <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
        <DeviceVisible :widget="widget" />
        <a-form-model-item label="名称">
          <a-input v-model="widget.title" />
        </a-form-model-item>
        <a-form-model-item label="选择表单">
          <DyformDefinitionSelect
            v-model="widget.configuration.formUuid"
            :displayModal="true"
            :style="`${widget.configuration.formUuid ? 'width: calc(100% - 30px); display: inline-block' : 'width:100%'}`"
            @change="onSelectFormChange"
          />
          <a-button
            v-show="widget.configuration.formUuid"
            :type="widget.configuration.formUuid ? 'link' : 'text'"
            size="small"
            @click="redirectFormDesign(widget.configuration.formUuid)"
            :title="widget.configuration.formUuid ? '打开表单设计器' : ''"
          >
            <a-icon type="form"></a-icon>
          </a-button>
        </a-form-model-item>

        <a-form-model-item label="显示标题">
          <a-input v-model="widget.configuration.title">
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :designer="designer" code="title" v-model="widget.configuration.title" />
            </template>
          </a-input>
        </a-form-model-item>
        <a-form-model-item label="从表可折叠">
          <a-switch v-model="widget.configuration.isCollapse" />
        </a-form-model-item>
        <template v-if="widget.configuration.isCollapse">
          <a-form-model-item label="默认折叠从表">
            <a-switch v-model="widget.configuration.defaultCollapse" />
          </a-form-model-item>
        </template>
        <a-form-model-item label="默认状态" class="item-lh" v-if="designer.terminalType == 'pc'">
          <a-radio-group size="small" v-model="widget.configuration.defaultDisplayState" button-style="solid">
            <a-radio-button value="edit">可编辑</a-radio-button>
            <a-radio-button value="unedit">不可编辑</a-radio-button>
            <a-radio-button value="hidden">隐藏</a-radio-button>
          </a-radio-group>
        </a-form-model-item>

        <a-form-model-item label="展示方式" class="item-lh" v-if="designer.terminalType == 'pc'">
          <a-radio-group size="small" v-model="widget.configuration.layout" button-style="solid" @change="onChangeLayout">
            <a-radio-button value="table">表格</a-radio-button>
            <a-radio-button value="form-inline">表单卡片</a-radio-button>
            <a-radio-button value="form-tabs">表单标签页</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="展示方式" class="item-lh" v-if="designer.terminalType == 'mobile'">
          <a-radio-group size="small" v-model="widget.configuration.uniConfiguration.layout" button-style="solid">
            <a-radio-button value="table">表格展示</a-radio-button>
            <a-radio-button value="form-inline">表单卡片</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <template v-if="widget.configuration.uniConfiguration.layout == 'form-inline' && designer.terminalType == 'mobile'">
          <a-form-model-item>
            <template slot="label">标题字段</template>
            <a-select
              v-model="widget.configuration.formInlineTitleField"
              :options="fieldSelectOptions"
              :style="{ width: '100%' }"
              allow-clear
              :getPopupContainer="getPopupContainerByPs()"
            ></a-select>
          </a-form-model-item>
          <a-form-model-item label="手风琴效果">
            <a-switch v-model="widget.configuration.uniConfiguration.accordion" />
          </a-form-model-item>
          <a-form-model-item
            label="折叠显示字段数"
            class="item-lh"
            v-if="widget.configuration.uniConfiguration.layout == 'form-inline' && designer.terminalType == 'mobile'"
          >
            <a-input-number
              v-model="widget.configuration.uniConfiguration.mobileShowColumnNum"
              :min="1"
              :max="widget.configuration.columns.length"
            />
          </a-form-model-item>
        </template>

        <a-form-model-item label="表格底部区域" v-if="widget.configuration.layout == 'table' && designer.terminalType == 'pc'">
          <a-switch v-model="widget.configuration.enableFooter" @change="onChangeEnableFooter" />
        </a-form-model-item>
        <template v-if="widget.configuration.layout !== 'table' && designer.terminalType == 'pc'">
          <a-form-model-item>
            <template slot="label">
              {{ widget.configuration.layout == 'form-inline' ? '行标题字段' : '页签标题字段' }}
            </template>
            <a-select
              v-model="widget.configuration.formInlineTitleField"
              :options="fieldSelectOptions"
              :style="{ width: '100%' }"
              allow-clear
              :getPopupContainer="getPopupContainerByPs()"
            ></a-select>
          </a-form-model-item>
          <a-form-model-item label="页签标题缺省" v-show="widget.configuration.layout == 'form-tabs'">
            <a-input v-model="widget.configuration.defaultTabPaneTitle">
              <template slot="addonAfter">
                <WI18nInput
                  :widget="widget"
                  :designer="designer"
                  code="defaultTabPaneTitle"
                  v-model="widget.configuration.defaultTabPaneTitle"
                />
                <a-tooltip title="缺省标题后是否显示序号" placement="bottomRight">
                  <a-checkbox v-model="widget.configuration.tabPaneTitleOrderSuffix" />
                </a-tooltip>
              </template>
            </a-input>
          </a-form-model-item>
          <a-form-model-item label="页签编辑原表单" v-show="widget.configuration.layout == 'form-tabs'">
            <a-switch v-model="widget.configuration.tabEditOriginalForm" />
          </a-form-model-item>
        </template>

        <a-collapse :bordered="false" expandIconPosition="right" defaultActiveKey="columnSetting">
          <a-collapse-panel key="columnSetting">
            <template slot="header">
              {{ widget.configuration.layout === 'table' ? '列设置' : '字段设置' }}
            </template>

            <SubformColumnConfiguration :widget="widget" ref="column" :columnIndexOptions="columnIndexOptions" :designer="designer" />

            <a-form-model-item label="表格分组" v-show="widget.configuration.layout === 'table' && designer.terminalType == 'pc'">
              <a-switch v-model="widget.configuration.enableTableGroup" />
            </a-form-model-item>
            <a-form-model-item
              label="分组字段"
              v-show="widget.configuration.enableTableGroup && widget.configuration.layout === 'table' && designer.terminalType == 'pc'"
            >
              <a-select
                v-model="widget.configuration.tableGroupField"
                :options="fieldSelectOptions"
                :style="{ width: '100%' }"
                :getPopupContainer="getPopupContainerByPs()"
              ></a-select>
            </a-form-model-item>
            <template v-if="designer.terminalType == 'pc'">
              <a-form-model-item v-show="widget.configuration.layout === 'table'">
                <template slot="label">
                  列冻结
                  <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                    <template slot="title">请设置冻结范围内的列宽度，建议留一列不设宽度以适应弹性布局</template>
                    <a-icon type="info-circle" style="padding-top: 0; vertical-align: middle; line-height: 16px" />
                  </a-tooltip>
                </template>
                <a-switch v-model="widget.configuration.enableColumnFreeze" />
              </a-form-model-item>
              <div v-show="widget.configuration.enableColumnFreeze && widget.configuration.layout === 'table'">
                <a-form-model-item>
                  <template slot="label">
                    冻结前
                    <a-input-number v-model="widget.configuration.leftFreezeColNum" />
                    列
                  </template>
                </a-form-model-item>
                <a-form-model-item>
                  <template slot="label">
                    冻结后
                    <a-input-number v-model="widget.configuration.rightFreezeColNum" />
                    列
                  </template>
                </a-form-model-item>
              </div>
            </template>
            <a-form-model-item label="显示序列号">
              <a-switch v-model="widget.configuration.addSerialNumber" />
            </a-form-model-item>
            <div v-show="widget.configuration.layout === 'table' && widget.configuration.addSerialNumber">
              <a-form-model-item label="分组序号重置" v-if="designer.terminalType == 'pc'">
                <a-switch v-model="widget.configuration.groupResetSerialNumber" />
              </a-form-model-item>
              <a-form-model-item label="分页序号重置">
                <a-switch v-model="widget.configuration.pageResetSerialNumber" />
              </a-form-model-item>
              <a-form-model-item label="序号列固定" v-if="designer.terminalType == 'mobile'">
                <a-switch v-model="widget.configuration.uniConfiguration.fixedSerialNumber" />
              </a-form-model-item>
            </div>
          </a-collapse-panel>
          <a-collapse-panel key="operationSetting" header="操作设置" forceRender>
            <a-form-model-item label="选择模式" class="item-lh">
              <a-radio-group size="small" v-model="widget.configuration.rowSelectType" button-style="solid">
                <a-radio-button value="checkbox">多选</a-radio-button>
                <a-radio-button value="radio">单选</a-radio-button>
                <a-radio-button value="no">不选中</a-radio-button>
              </a-radio-group>
            </a-form-model-item>

            <div v-show="widget.configuration.layout == 'table' && designer.terminalType == 'pc'">
              <a-form-model-item label="编辑模式" class="item-lh">
                <a-radio-group size="small" v-model="widget.configuration.rowEditMode" button-style="solid" @change="onRowEditModeChange">
                  <a-radio-button value="cell">行编辑</a-radio-button>
                  <a-radio-button value="form">表单编辑</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item
                label="单元格默认状态"
                class="item-lh"
                v-show="widget.configuration.layout == 'table' && widget.configuration.rowEditMode == 'cell'"
              >
                <a-radio-group size="small" v-model="widget.configuration.cellDefaultDisplayState" button-style="solid">
                  <a-radio-button value="label">显示文本</a-radio-button>
                  <a-radio-button value="widget">显示组件</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <template v-if="widget.configuration.rowEditMode === 'form'">
                <a-form-model-item label="状态表单">
                  <a-switch v-model="widget.configuration.enableStateForm" />
                </a-form-model-item>
                <a-form-model-item>
                  <template slot="label">
                    <span
                      :class="widget.configuration.editFormUuid ? 'ant-btn-link' : ''"
                      @click="redirectFormDesign(widget.configuration.editFormUuid)"
                    >
                      {{ widget.configuration.enableStateForm ? '新建表单' : '选择表单' }}
                    </span>
                  </template>
                  <DyformDefinitionSelect
                    v-model="widget.configuration.editFormUuid"
                    :displayModal="true"
                    @change="(e, opt) => onSelectChangeStateForm(e, opt, 'editFormUuid')"
                  />
                </a-form-model-item>
                <div v-show="widget.configuration.enableStateForm">
                  <a-form-model-item>
                    <template slot="label">
                      <span
                        :class="widget.configuration.editStateFormUuid ? 'ant-btn-link' : ''"
                        @click="redirectFormDesign(widget.configuration.editStateFormUuid)"
                      >
                        编辑表单
                      </span>
                    </template>
                    <DyformDefinitionSelect
                      v-model="widget.configuration.editStateFormUuid"
                      :displayModal="true"
                      @change="(e, opt) => onSelectChangeStateForm(e, opt, 'editStateFormUuid')"
                    />
                  </a-form-model-item>
                  <a-form-model-item>
                    <template slot="label">
                      <span
                        :class="widget.configuration.labelStateFormUuid ? 'ant-btn-link' : ''"
                        @click="redirectFormDesign(widget.configuration.labelStateFormUuid)"
                      >
                        查阅表单
                      </span>
                    </template>
                    <DyformDefinitionSelect
                      v-model="widget.configuration.labelStateFormUuid"
                      :displayModal="true"
                      @change="(e, opt) => onSelectChangeStateForm(e, opt, 'labelStateFormUuid')"
                    />
                  </a-form-model-item>
                </div>
                <a-form-model-item label="字段设置" v-if="widget.configuration.editFormUuid">
                  <WidgetDesignDrawer
                    :id="'subformformFieldConfigureDrawer' + widget.id"
                    title="字段设置"
                    :designer="designer"
                    :zIndex="1001"
                  >
                    <a-button type="link" size="small" title="设置">
                      <Icon type="pticon iconfont icon-ptkj-shezhi" />
                      设置
                    </a-button>
                    <template slot="content">
                      <FormFieldConfiguration :widget="widget" :designer="designer" :columnsIdMap="columnsIdMap" />
                    </template>
                  </WidgetDesignDrawer>
                </a-form-model-item>
              </template>

              <a-form-model-item label="支持弹窗编辑" v-show="widget.configuration.rowEditMode === 'cell'">
                <a-switch v-model="widget.configuration.supportModalEdit" />
              </a-form-model-item>

              <div
                v-show="
                  widget.configuration.rowEditMode == 'form' ||
                  (widget.configuration.rowEditMode === 'cell' && widget.configuration.supportModalEdit)
                "
              >
                <a-form-model-item label="弹窗尺寸">
                  <a-select
                    v-model="widget.configuration.editModal.style.size"
                    :style="{ width: '100%' }"
                    :options="modalSizeOptions"
                    :getPopupContainer="getPopupContainerByPs()"
                  ></a-select>
                </a-form-model-item>
                <a-form-model-item label="自定义宽" v-show="widget.configuration.editModal.style.size === 'selfDefine'">
                  <div class="input-number-suffix-wrapper">
                    <a-input-number v-model="widget.configuration.editModal.style.width" />
                    <i>px</i>
                  </div>
                </a-form-model-item>
                <a-form-model-item label="自定义高" v-show="widget.configuration.editModal.style.size === 'selfDefine'">
                  <div class="input-number-suffix-wrapper">
                    <a-input-number v-model="widget.configuration.editModal.style.height" />
                    <i>px</i>
                  </div>
                </a-form-model-item>

                <a-form-model-item label="弹窗确认前">
                  <WidgetCodeEditor
                    @save="value => (widget.configuration.editModal.scriptBeforeOk = value)"
                    :value="widget.configuration.editModal.scriptBeforeOk"
                  >
                    <a-button icon="code">编写代码</a-button>
                    <template slot="help">
                      <div>弹窗确认前的逻辑代码，无入参，可以通过返回 false 终止关闭弹窗行为</div>
                    </template>
                  </WidgetCodeEditor>
                </a-form-model-item>
                <a-form-model-item label="弹窗确认后">
                  <WidgetCodeEditor
                    @save="value => (widget.configuration.editModal.scriptAfterOk = value)"
                    :value="widget.configuration.editModal.scriptAfterOk"
                  >
                    <a-button icon="code">编写代码</a-button>
                    <template slot="help">
                      <div>入参说明：</div>
                      <ul>
                        <li>
                          <a-tag>row</a-tag>
                          : 表示将要添加到表格内的行数据
                        </li>
                      </ul>
                    </template>
                  </WidgetCodeEditor>
                </a-form-model-item>
              </div>
            </div>

            <SubformButtonConfiguration
              title="表头按钮"
              :button="widget.configuration.headerButton"
              :widget="widget"
              position="header"
              :designer="designer"
            />
            <a-divider />

            <SubformButtonConfiguration
              title="行按钮"
              :button="widget.configuration.rowButton"
              :widget="widget"
              :designer="designer"
              :columnIndexOptions="columnIndexOptions"
              position="rowEnd"
            />
          </a-collapse-panel>

          <a-collapse-panel key="searchSetting" header="查询设置">
            <!-- <a-form-model-item label="查询方式">
              <a-radio-group v-model="widget.configuration.search.type" button-style="solid" size="small">
                <a-radio-button value="keywordAdvanceSearch">关键字/高级搜索</a-radio-button>
                <a-radio-button value="attrSearch">属性搜索</a-radio-button>
              </a-radio-group>
            </a-form-model-item> -->

            <div
              v-if="widget.configuration.formId && columnIndexOptions.length > 0"
              v-show="widget.configuration.search.type == 'keywordAdvanceSearch'"
            >
              <KeywordSearchConfiguration
                :widget="widget"
                ref="keywordSearch"
                :columnIndexOptions="columnIndexOptions"
                :designer="designer"
              />
              <AdvanceSearchConfiguration
                :widget="widget"
                ref="advanceSearch"
                :columnIndexOptions="columnIndexOptions"
                :designer="designer"
              />
            </div>
          </a-collapse-panel>
          <a-collapse-panel
            key="pageSetting"
            header="分页设置"
            v-show="widget.configuration.layout === 'table' || designer.terminalType == 'mobile'"
          >
            <a-form-model-item label="开启分页">
              <a-switch v-model="widget.configuration.pagination.enable" />
            </a-form-model-item>
            <div v-show="widget.configuration.pagination.enable">
              <a-form-model-item label="默认每页条数">
                <a-input-number v-model="widget.configuration.pagination.pageSize" />
              </a-form-model-item>
              <a-form-model-item label="显示总数/页数">
                <a-switch v-model="widget.configuration.pagination.showTotalPage" />
              </a-form-model-item>
              <template v-if="designer.terminalType == 'pc'">
                <a-form-model-item label="显示切换分页">
                  <a-switch v-model="widget.configuration.pagination.showSizeChanger" />
                </a-form-model-item>
                <a-form-model-item label="分页数组" v-show="widget.configuration.pagination.showSizeChanger">
                  <a-select
                    :options="vPageSizeOptions"
                    :style="{ width: '100%' }"
                    mode="multiple"
                    v-model="widget.configuration.pagination.pageSizeOptions"
                    @change="onChangePageSizeOptions"
                    :getPopupContainer="getPopupContainerByPs()"
                    :dropdownClassName="getDropdownClassName()"
                  ></a-select>
                </a-form-model-item>
                <a-form-model-item label="显示跳页">
                  <a-switch v-model="widget.configuration.pagination.showQuickJumper" />
                </a-form-model-item>
              </template>
              <a-form-model-item label="页码为1隐藏分页">
                <a-switch v-model="widget.configuration.pagination.hideOnSinglePage" />
              </a-form-model-item>
            </div>
          </a-collapse-panel>
          <template v-if="designer.terminalType == 'pc'">
            <a-collapse-panel key="importSetting" header="导入设置" :forceRender="true">
              <SubformImportConfiguration :widget="widget" :designer="designer" />
            </a-collapse-panel>
            <a-collapse-panel key="exportSetting" header="导出设置" :forceRender="true">
              <SubformExportConfiguration :widget="widget" :designer="designer" />
            </a-collapse-panel>
          </template>
          <a-collapse-panel key="defaultSort" header="默认排序" :forceRender="true">
            <a-form-model-item>
              <template slot="label">
                开启默认排序
                <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                  <template slot="title">
                    <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                      <li>开启默认排序后，初始从表时从表数据会按照默认排序设值排序</li>
                      <li>字段排序，数据将按照排序字段的顺序从上到下依次进行排序</li>
                    </ul>
                  </template>
                  <a-icon type="info-circle" style="padding-top: 0; vertical-align: middle; line-height: 16px" />
                </a-tooltip>
              </template>
              <a-switch v-model="widget.configuration.defaultSort.enable" />
            </a-form-model-item>
            <template v-if="widget.configuration.defaultSort.enable">
              <a-form-model-item>
                <template slot="label">默认排序设置</template>
                <a-radio-group size="small" v-model="widget.configuration.defaultSort.type" button-style="solid">
                  <a-radio-button value="columns">字段</a-radio-button>
                  <a-radio-button value="script">自定义函数</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="排序函数" v-if="widget.configuration.defaultSort.type == 'script'">
                <WidgetCodeEditor
                  @save="value => (widget.configuration.defaultSort.script = value)"
                  :value="widget.configuration.defaultSort.script"
                >
                  <a-button icon="code">编写代码</a-button>
                  <template slot="help">
                    <div>
                      入参说明
                      <ul>
                        <li>
                          <a-tag>row</a-tag>
                          : 比较的两行显示列的数据
                        </li>
                      </ul>
                    </div>
                    <div>
                      返回说明：如果row[0]的某个字段数据比row[1]同个字段数据小则为负数(-1),
                      row[0]的某个字段数据比row[1]同个字段数据大则为正数(1), 相等的时候返回 0
                    </div>
                  </template>
                </WidgetCodeEditor>
              </a-form-model-item>
              <SubformColumnSortConfiguration
                v-else
                :widget="widget"
                ref="columnSortRef"
                :columnIndexOptions="columnIndexOptions"
                :designer="designer"
              ></SubformColumnSortConfiguration>
            </template>
          </a-collapse-panel>
          <a-collapse-panel key="defaultRowsSetting" header="默认行数据设置">
            <DefaultRowDataConfiguration :widget="widget" :designer="designer" />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="事件设置">
      <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
    </a-tab-pane>
  </a-tabs>
</template>
<style></style>
<script type="text/babel">
import SubformButtonConfiguration from './subform-button-configuration.vue';
import SubformColumnConfiguration from './subform-column-configuration.vue';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
import { generateId, deepClone } from '@framework/vue/utils/util';
import { debounce } from 'lodash';
import SubformImportConfiguration from './import-configuration/subform-import-configuration.vue';
import SubformExportConfiguration from './export-configuration/subform-export-configuration.vue';
import DefaultRowDataConfiguration from './default-row-data-configuration.vue';
import MobileCardConfiguration from './mobile-card-configuration.vue';
import KeywordSearchConfiguration from './search/keyword-search-configuration.vue';
import AdvanceSearchConfiguration from './search/advance-search-configuration.vue';
import SubformColumnSortConfiguration from './subform-column-sort-configuration.vue';
import FormFieldConfiguration from './form-field-configuration.vue';
import DyformDefinitionSelect from '@pageAssembly/app/web/widget/commons/dyform-definition-select.vue';
export default {
  name: 'WidgetSubformConfiguration',
  mixins: [],
  inject: ['appId'],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      formDefinitionOptions: this.widget.configuration.formUuid
        ? [
            {
              label: this.widget.configuration.formName,
              value: this.widget.configuration.formUuid,
              name: this.widget.configuration.tableName
            }
          ]
        : [],
      fetchingFormDefinition: true,
      columnIndexOptions: [],
      dyformDefinitionMap: {},
      formFieldOptions: [],
      modalSizeOptions: [
        { label: '正常', value: 'normal' },
        { label: '中', value: 'middle' },
        { label: '大', value: 'large' },
        { label: '全屏', value: 'fullscreen' },
        { label: '自定义', value: 'selfDefine' }
      ]
    };
  },

  beforeCreate() {},
  components: {
    SubformButtonConfiguration,
    SubformColumnConfiguration,
    SubformImportConfiguration,
    SubformExportConfiguration,
    DefaultRowDataConfiguration,
    MobileCardConfiguration,
    KeywordSearchConfiguration,
    AdvanceSearchConfiguration,
    SubformColumnSortConfiguration,
    FormFieldConfiguration,
    DyformDefinitionSelect
  },
  computed: {
    editButtonName() {
      for (let i = 0, len = this.widget.configuration.rowButton.buttons.length; i < len; i++) {
        if (this.widget.configuration.rowButton.buttons[i].id == 'editRow' && this.widget.configuration.rowButton.buttons[i].default) {
          return this.widget.configuration.rowButton.buttons[i].title;
        }
      }
    },
    vPageSizeOptions() {
      let sizes = ['5', '10', '15', '20', '50', '100', '200', '500', '1000', '5000'];
      let options = [];
      for (let i = 0, len = sizes.length; i < len; i++) {
        options.push({
          label: sizes[i],
          value: sizes[i]
        });
      }
      return options;
    },
    fieldSelectOptions() {
      let fieldSelectOptions = [];
      for (let i = 0, len = this.columnIndexOptions.length; i < len; i++) {
        fieldSelectOptions.push({
          label: this.columnIndexOptions[i].configuration.name,
          value: this.columnIndexOptions[i].configuration.code
        });
      }
      return fieldSelectOptions;
    },
    columnsIdMap() {
      let map = {};
      this.widget.configuration.columns.forEach(column => {
        map[column.dataIndex] = column;
      });
      return map;
    }
  },
  created() {
    if (!this.widget.configuration.hasOwnProperty('uniConfiguration')) {
      this.$set(this.widget.configuration, 'uniConfiguration', {
        layout: 'form-inline',
        mobileShowColumnNum: 3,
        accordion: true
      });
    }
    if (!this.widget.configuration.hasOwnProperty('search')) {
      this.$set(this.widget.configuration, 'search', {
        type: 'keywordAdvanceSearch',
        keywordSearchPlaceholder: '请输入关键字',
        keywordSearchEnable: false,
        advanceSearchLabelColWidth: 120,
        advanceSearchWrapperColWidth: 200,
        keywordSearchColumns: [],
        columnSearchGroupEnable: false,
        advanceSearchEnable: false,
        columnSearchGroup: [],
        columnAdvanceSearchGroup: []
        // advanceSearchPerRowColNumber: 3
      });
    }
    if (!this.widget.configuration.hasOwnProperty('defaultSort')) {
      this.$set(this.widget.configuration, 'defaultSort', {
        enable: false,
        type: 'columns',
        script: '',
        columns: []
      });
    }
    if (this.widget.configuration.rowButton.buttons.findIndex(item => item.id == 'viewRow') == -1) {
      this.widget.configuration.rowButton.buttons.unshift({
        id: 'viewRow',
        code: 'viewRow',
        title: '查看',
        default: true,
        defaultVisible: true,
        style: { icon: '', type: 'link' },
        eventHandler: { eventParams: [] }
      });
    }
    if (!this.widget.configuration.hasOwnProperty('enableStateForm')) {
      const newConfig = {
        enableStateForm: false, // 按状态设置表单
        editStateFormUuid: undefined, //编辑表单
        labelStateFormUuid: undefined, //查阅表单
        newFormTitle: '新增', // 新建状态的标题
        editStateTitle: '编辑', // 编辑状态的标题
        labelStateTitle: '查看详情', // 查阅状态的标题
        editFormElementRules: [],
        labelStateFormElementRules: [],
        editStateFormElementRules: []
      };

      this.$set(this.widget, 'configuration', {
        ...this.widget.configuration,
        ...newConfig
      });
    }
  },
  methods: {
    onChangeLayout() {
      if (this.widget.configuration.layout == 'form-tabs') {
        this.widget.configuration.headerButton.buttons.forEach(b => {
          if (b.id.startsWith('move')) {
            b.defaultVisible = false;
          }
        });
        this.widget.configuration.rowButton.buttons.forEach(b => {
          if (b.id.startsWith('move') || b.id == 'editRow' || b.id == 'copyRow') {
            b.defaultVisible = false;
          }
          if (b.id == 'delRow') {
            b.defaultVisible = true;
            this.$set(b, 'displayAsTabClose', true);
          }
        });
      }
    },
    getPopupContainerByPs,
    getDropdownClassName,
    filterOption(inputValue, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0;
    },
    onRowEditModeChange() {},
    onChangePageSizeOptions() {
      this.widget.configuration.pagination.pageSizeOptions.sort((a, b) => {
        return Number(a) - Number(b);
      });
    },
    onSelectFormChange(value, option) {
      let _this = this;
      if (option) {
        this.widget.configuration.columns.splice(0, this.widget.configuration.columns.length);
        this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
        // this.widget.configuration.columns.length = 0;
        // this.columnIndexOptions.length = 0;
        if (option.data && option.data.props) {
          this.widget.configuration.tableName = option.data.props.name; // 表名
        } else {
          this.widget.configuration.tableName = option.tableName;
        }
      }
      if (value == undefined) {
        this.widget.configuration.formId = undefined;
        this.widget.configuration.formName = undefined;
        return;
      }

      if (option != undefined && this.widget.configuration.import && this.widget.configuration.import.importButtonRule) {
        let notify = false;
        for (let k in this.widget.configuration.import.importButtonRule) {
          let r = this.widget.configuration.import.importButtonRule[k];
          if (r.sheetConfig.length > 0) {
            r.sheetConfig[0].params.formUuid = value;
            notify = true;
          }
        }
        if (this.widget.configuration.import.enable && notify) {
          this.$message.info('选择表单变更, 请检查导入设置的相关配置');
        }
      }

      this.fetchDyformDefinitionJSON(value).then(def => {
        this.widget.configuration.formId = def.id;
        this.widget.configuration.formName = def.name;
        if (def.definitionVjson) {
          let _fields = JSON.parse(def.definitionVjson).fields;
          if (_fields) {
            _this.columnIndexOptions = _fields;
            if (option) {
              for (let i = 0, len = _this.columnIndexOptions.length; i < len; i++) {
                let id = generateId(),
                  colI18n = _this.columnIndexOptions[i].configuration.i18n;
                let i18n = {
                  zh_CN: {
                    [_this.widget.id + '.' + id]: _this.columnIndexOptions[i].configuration.name
                  }
                };
                if (colI18n) {
                  for (let key in colI18n) {
                    i18n[key] = {
                      [_this.widget.id + '.' + id]: colI18n[key][_this.columnIndexOptions[i].id + '.' + _this.columnIndexOptions[i].id]
                    };
                  }
                }
                _this.widget.configuration.columns.push({
                  id,
                  title: _this.columnIndexOptions[i].configuration.name,
                  dataIndex: _this.columnIndexOptions[i].configuration.code,
                  defaultDisplayState: _this.columnIndexOptions[i].configuration.defaultDisplayState,
                  sortable: {
                    enable: false,
                    alogrithmType: 'orderByChar',
                    datePattern: undefined,
                    script: null
                  },
                  // hidden: false,
                  width: null,
                  required: _this.columnIndexOptions[i].configuration.required,
                  titleAlign: 'left',
                  contentAlign: 'left',
                  widget: _this.columnIndexOptions[i],
                  ellipsis: true,
                  i18n
                });
                if (i == 0) {
                  _this.widget.configuration.uniConfiguration.rowTitleColumn = _this.columnIndexOptions[i].configuration.code;
                }
              }
            }
          }
        }
      });
    },
    fetchDyformDefinitionJSON(formUuid) {
      if (!this.dyformDefinitionMap[formUuid]) {
        this.dyformDefinitionMap[formUuid] = {};
      }
      return new Promise((resolve, reject) => {
        if (this.dyformDefinitionMap[formUuid] && this.dyformDefinitionMap[formUuid].definitionVjson) {
          resolve(this.dyformDefinitionMap[formUuid]);
        } else {
          $axios
            .post(`/proxy/api/dyform/definition/getFormDefinitionByUuid?formUuid=${formUuid}`, {})
            .then(({ data }) => {
              this.dyformDefinitionMap[formUuid].definitionVjson = data.definitionVjson;
              resolve(data);
            })
            .catch(error => {});
        }
      });
    },
    fetchDyformDefinition() {
      $axios
        .get(`/proxy/api/app/module/queryRelaModuleIds`, {
          params: {
            moduleId: this.appId
          }
        })
        .then(({ data }) => {
          let ids = data.data || [];
          ids.push(this.appId);
          $axios
            .post(`/proxy/api/dyform/definition/queryFormDefinitionNoJsonByModuleIds`, ids)
            .then(({ data }) => {
              this.formDefinitionOptions.splice(0, this.formDefinitionOptions.length);
              this.fetchingFormDefinition = false;
              if (data.code == 0 && data.data) {
                for (let i = 0, len = data.data.length; i < len; i++) {
                  // 主表不能作为从表使用
                  if (
                    this.designer.vueInstance &&
                    this.designer.vueInstance.definition &&
                    this.designer.vueInstance.definition.uuid == data.data[i].uuid
                  ) {
                    continue;
                  }

                  this.formDefinitionOptions.push({
                    label: data.data[i].name + ` (v${data.data[i].version})`,
                    value: data.data[i].uuid,
                    name: data.data[i].tableName
                  });
                  // 处理旧表单没存表名问题
                  if (
                    this.widget.configuration.formUuid &&
                    this.widget.configuration.formUuid == data.data[i].uuid &&
                    !this.widget.configuration.tableName
                  ) {
                    this.widget.configuration.tableName = data.data[i].tableName;
                  }
                }
              }
            })
            .catch(error => {});
        });
    },

    // 生成后端的功能元素数据
    getFunctionElements(widget) {
      let functionElements = {};
      let elements = [];
      if (widget.configuration.formUuid) {
        elements.push({
          ref: true,
          functionType: 'formDefinition',
          exportType: 'formDefinition',
          configType: '1',
          uuid: widget.configuration.formUuid,
          id: widget.configuration.formId,
          name: widget.configuration.formName
        });
      }
      functionElements[widget.id] = elements;
      return functionElements;
    },

    onChangeEnableFooter() {
      if (this.widget.configuration.footerWidgets == undefined) {
        this.$set(this.widget.configuration, 'footerWidgets', []);
      }
    },
    redirectFormDesign(uuid) {
      if (uuid) {
        window.open(`/dyform-designer/index?uuid=${uuid}`, '_blank');
      }
    },
    onSelectChangeStateForm(v, opt, key) {
      let nameField = key == 'editFormUuid' ? 'editFormName' : key == 'labelStateFormUuid' ? 'labelStateFormName' : 'editStateFormName';
      let ruleField =
        key == 'editFormUuid'
          ? 'editFormElementRules'
          : key == 'labelStateFormUuid'
          ? 'labelStateFormElementRules'
          : 'editStateFormElementRules';
      this.widget.configuration[nameField] = null;
      if (!this.widget.configuration[ruleField]) {
        this.widget.configuration[ruleField] = [];
      }
      this.widget.configuration[ruleField].splice(0, this.widget.configuration[ruleField].length);
      if (v) {
        this.widget.configuration[nameField] = opt.name;
      }
    }
  },
  beforeMount() {
    if (this.widget.configuration.formUuid) {
      this.onSelectFormChange(this.widget.configuration.formUuid);
    }
    this.fetchDyformDefinition();
  },
  mounted() {},
  configuration() {
    let defaultButton = (id, title, defaultVisible, icon) => {
      return {
        id,
        code: id,
        title,
        default: true,
        defaultVisible,
        style: { icon, type: 'link' },
        eventHandler: { eventParams: [] }
      };
    };
    let buttonGroup = () => {
      return {
        type: 'notGroup', // notGroup: 不分组  fixedGroup: 固定分组 dynamicGroup: 动态分组
        groups: [
          // 固定分组
          // {name:,buttonIds:[]}
        ],
        dynamicGroupName: '更多', //动态分组名称
        dynamicGroupBtnThreshold: 3 // 分组按钮数阈值，达到该数才触发分组
      };
    };
    return {
      formId: '', // 测试数据
      title: '', // 表格标题
      layout: 'table', // table / form-inline
      formInlineTitleField: undefined,
      defaultDisplayState: 'edit',
      isDatabaseField: false,
      isCollapse: true, // 默认可折叠
      defaultCollapse: true, // 默认折叠从表
      addSerialNumber: false, // 序号
      groupResetSerialNumber: false,
      pageResetSerialNumber: false,
      rowSelectType: 'checkbox', // 行选择类型：checkbox / radio / no
      // editMode: 'cellEdit', // 行编辑： cellEdit  表单编辑：formEdit
      cellDefaultDisplayState: 'label', // 单元格默认状态： Label / widget
      editFormUuid: undefined, // 编辑表单（状态设值开启后的新建表单）
      enableStateForm: false, // 按状态设置表单
      editStateFormUuid: undefined, //编辑表单
      labelStateFormUuid: undefined, //查阅表单
      newFormTitle: '新增', // 新建状态的标题
      editStateTitle: '编辑', // 编辑状态的标题
      labelStateTitle: '查看详情', // 查阅状态的标题
      editFormElementRules: [],
      labelStateFormElementRules: [],
      editStateFormElementRules: [],
      supportModalEdit: false, // 支持弹窗编辑
      editModal: {
        // 弹窗配置
        style: {
          size: 'normal', // normal / middle / large / fullscreen / selfDefine
          width: 700,
          height: 600
        },
        scriptBeforeOk: undefined, // 弹窗确认前执行脚本
        scriptAfterOk: undefined // 弹窗确认后执行脚本
      },
      pagination: {
        // 分页配置
        enable: false,
        pageSize: 10, // 默认分页数
        showTotalPage: true, // 显示总页数
        showSizeChanger: true, // 显示分页数切换
        showQuickJumper: true, // 显示跳页
        hideOnSinglePage: true, // 一页时隐藏分页
        pageSizeOptions: ['10', '20', '50', '100', '200']
      },
      rowEditMode: 'cell', // 行编辑模式：cell 单元格编辑 form 表单编辑
      enableTableGroup: false,
      tableGroupField: undefined,
      enableColumnFreeze: false,
      leftFreezeColNum: 0,
      rightFreezeColNum: 0,
      columns: [],
      footerWidgets: [],
      search: {
        type: 'keywordAdvanceSearch',
        advanceSearchLabelAlign: 'right',
        keywordSearchPlaceholder: '请输入关键字',
        advanceSearchLabelColWidth: 120,
        advanceSearchWrapperColWidth: 200,
        keywordSearchEnable: false,
        keywordSearchColumns: [],
        columnSearchGroupEnable: false,
        advanceSearchEnable: false,
        columnSearchGroup: [],
        columnAdvanceSearchGroup: [],
        advanceSearchPerRowColNumber: 3
      },
      headerButton: {
        buttons: [
          defaultButton('addRow', '添加', true, 'iconfont icon-a-icshuxingshezhitianjia'),
          defaultButton('delRow', '删除', true, 'iconfont icon-ptkj-shanchu'),
          // defaultButton('importRow', '导入', false),
          // defaultButton('exportRow', '导出', false),
          defaultButton('moveUpRow', '上移', true, 'iconfont icon-ptkj-shangyi'),
          defaultButton('moveDownRow', '下移', true, 'iconfont icon-ptkj-xiayi')
        ],
        buttonGroup: buttonGroup()
      }, //表头按钮
      rowButton: {
        buttons: [
          defaultButton('viewRow', '查看', true),
          defaultButton('editRow', '编辑', true),
          defaultButton('copyRow', '复制行', true),
          defaultButton('insertRow', '插入行', false),
          defaultButton('appendRow', '添加行', false),
          defaultButton('delRow', '删除', false),
          defaultButton('moveUpRow', '上移', true),
          defaultButton('moveDownRow', '下移', true)
        ],
        buttonGroup: buttonGroup()
      },
      uniConfiguration: {
        layout: 'form-inline',
        accordion: true
      }
    };
  }
};
</script>
