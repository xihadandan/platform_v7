<template>
  <div v-show="!hidden && deviceVisible" class="widget-subform">
    <a-row type="flex" v-if="hastableTitleRow">
      <a-col flex="0 1 fit-content">
        <div class="subform-title" :style="{ margin: '16px 0', paddingRight: '10px' }">
          <span
            @click.stop="onTableExpand"
            v-if="widget.configuration.isCollapse"
            :style="{ display: 'inline-block', transform: tableExpand ? 'rotate(0deg)' : 'rotate(-90deg)', cursor: 'pointer' }"
          >
            <Icon
              type="pticon iconfont icon-ptkj-zhankai"
              :title="tableExpand ? $t('orgSelect.collapse', '折叠') : $t('orgSelect.expand', '展开')"
            />
          </span>
          <label>{{ $t('title', widget.configuration.title) }}</label>
        </div>
      </a-col>
      <a-col flex="auto">
        <a-divider orientation="right" class="subform-header-divider">
          <span @click.stop="() => {}">
            <WidgetTableButtons
              v-if="showHeaderButtons"
              :button="widget.configuration.headerButton"
              :meta="vTableSelectedData"
              :buttonPredicate="e => headButtonPredicate(e)"
              :visibleJudgementData="vTableButtonVisibleJudgementData"
              size="small"
              @button-click="onButtonClick"
            >
              <template
                slot="prefix"
                v-if="
                  (widget.configuration.layout == 'form-tabs' || widget.configuration.layout == 'form-inline') &&
                  widget.configuration.rowSelectType === 'checkbox'
                "
              >
                <a-checkbox
                  class="check-all"
                  :indeterminate="formInlineIndeterminate"
                  :checked="formInlineCheckAll"
                  @change="onFormInlineCheckAllChange"
                  v-if="widget.configuration.rowSelectType === 'checkbox'"
                >
                  {{ $t('WidgetSubform.selectAll', '全选') }}
                </a-checkbox>
              </template>
            </WidgetTableButtons>
          </span>
        </a-divider>
      </a-col>
    </a-row>

    <div v-show="tableExpand">
      <SubformSearch
        ref="subformSearch"
        :widget="widget"
        :fields="fields"
        v-if="widget.configuration.search != undefined && fields.length > 0"
        @reset="onResetSearch"
        @onFilteredRowKeys="onFilteredRowKeys"
      />
      <template v-if="widget.configuration.layout === 'table'">
        <div class="widget-subform-table">
          <a-table
            :loading="loading"
            class="pt-empty"
            tableLayout="fixed"
            bordered
            :columns="columns"
            :data-source="filteredRows"
            :pagination="pagination"
            :row-selection="rowSelection"
            :rowKey="rowKey"
            :customRow="customRow"
            :scroll="{ x: scrollX }"
            :childrenColumnName="rowChildKey"
            :expandedRowKeys.sync="expandedRowKeys"
            :locale="{
              emptyText
            }"
            :indentSize="0"
            :class="[
              tableExpand ? '' : 'collapsed',
              (enableColumnFreeze || modifyScrollPosition) && !rows.length && !designMode ? 'modify-scroll-position' : ''
            ]"
            @change="onTableChange"
            ref="table"
          >
            <template v-for="(slotOption, i) in customColTitleSlotOptions" :slot="slotOption.slotName">
              <span :class="[slotOption.required ? 'ant-form-item-required' : '']" :key="'require-col-title' + i" :title="slotOption.title">
                {{ slotOption.title }}
              </span>
            </template>
            <template v-for="(slotOption, i) in customRenderSlotOptions" :slot="slotOption.slotName" slot-scope="text, record, index">
              <div :key="'colslot-' + slotOption.dataIndex" :data-index="slotOption.dataIndex">
                <SubformTableCell
                  :widget="slotOption.widget"
                  :form="rowForms[record[rowKey]]"
                  :widgetSubform="widget"
                  :row="record"
                  :option="slotOption"
                  :rowIndex="index"
                  :jsModule="subformJsModule"
                  :formDefaultData="subformDefaultData"
                  @change="$evt => onCellComponentChange($evt, record, slotOption.dataIndex)"
                  @mounted="$evt => onCellComponentMounted($evt, record, slotOption.dataIndex)"
                />
              </div>
            </template>

            <div slot="__operationSlot" class="table-row-end-operation" slot-scope="text, record, index">
              <WidgetTableButtons
                v-if="!record.__MODEL__.isGroupRow"
                :button="filterRowEndButton"
                @buttonMounted="e => buttonMounted(e)"
                @buttonUpdated="e => buttonMounted(e)"
                :meta="record"
                :visibleJudgementData="vTableButtonVisibleJudgementData"
                :buttonPredicate="e => rowButtonPredicate(e, record)"
                size="small"
                buttonDefaultType="link"
                @button-click="btn => onButtonClick(btn, index, record)"
              >
                <template slot="suffix" v-if="widget.configuration.supportModalEdit && widget.configuration.rowEditMode == 'cell'">
                  <a-button
                    size="small"
                    type="link"
                    :icon="record.uuid != undefined && record.__MODEL__.loading === true ? 'loading' : 'credit-card'"
                    :title="$t('WidgetSubform.modalEditButtonTitle', '弹窗编辑数据')"
                    @click.stop="onModalEdit(record)"
                  ></a-button>
                </template>
              </WidgetTableButtons>
            </div>

            <template slot="footer" v-if="designMode && widget.configuration.enableFooter">
              <slot name="designFooter"></slot>
            </template>
            <template
              slot="footer"
              v-if="
                !designMode &&
                widget.configuration.enableFooter &&
                widget.configuration.footerWidgets != undefined &&
                widget.configuration.footerWidgets.length > 0
              "
            >
              <template v-for="(footerWgt, ii) in widget.configuration.footerWidgets">
                <component
                  :key="'footer_wgt_' + footerWgt.id"
                  :is="footerWgt.wtype"
                  :widget="footerWgt"
                  :index="ii"
                  :widgetsOfParent="widget.configuration.footerWidgets"
                  :parent="widget"
                ></component>
              </template>
            </template>
          </a-table>
          <!-- 代理行数据组件(用于分页数据校验) -->
          <a-form-model
            v-if="tableHasPagination && proxyRowDyform"
            ref="proxyRowDyformRef"
            :model="proxyRowDyform.formData"
            style="display: none"
          >
            <template v-for="(wgt, i) in proxyTableColWidgets">
              <SubformProxyFormCell :widget="wgt" :form="proxyRowDyform" :widgetSubform="widget" />
            </template>
          </a-form-model>
        </div>
      </template>
      <template v-else-if="widget.configuration.layout == 'form-tabs' && (designMode || (!designMode && formDefinitionVjson != undefined))">
        <a-tabs v-show="tableExpand" class="widget-subform-tabs" v-model="subformActiveTabKey" :hideAdd="true" size="small">
          <template v-for="(r, index) in filteredRows">
            <a-tab-pane :key="r[rowKey]" :forceRender="true">
              <template slot="tab">
                <template v-if="showHeaderButtons">
                  <template v-if="widget.configuration.rowSelectType === 'checkbox'">
                    <a-checkbox
                      :style="{ float: 'left', marginRight: '8px' }"
                      @click.stop="onFormInlineCheck(r[rowKey], r)"
                      :checked="selectedRowKeys.indexOf(r[rowKey]) != -1"
                    />
                  </template>
                  <template v-else-if="widget.configuration.rowSelectType === 'radio'">
                    <a-radio
                      :style="{ float: 'left', marginRight: '5px' }"
                      @click.stop="onFormInlineCheck(r[rowKey], r)"
                      :checked="selectedRowKeys.indexOf(r[rowKey]) != -1"
                    />
                  </template>
                </template>

                <a-tooltip :key="rowDataAsMD5 + '_tabPane_title_'" :title="tabPanelTitle(r)">
                  <label class="tab-title-label">
                    {{ tabPanelTitle(r) }}
                  </label>
                </a-tooltip>
                <template v-if="tabCloseVisible(r)">
                  <template
                    v-if="
                      rowDeleteButton != undefined &&
                      rowDeleteButton.confirmConfig &&
                      rowDeleteButton.confirmConfig.enable &&
                      rowDeleteButton.confirmConfig.popType == 'popconfirm'
                    "
                  >
                    <a-popconfirm
                      :title="resolvePopconfirmTitle(rowDeleteButton.confirmConfig.title, r)"
                      :ok-text="rowDeleteButton.confirmConfig.okText"
                      :cancel-text="rowDeleteButton.confirmConfig.cancelText"
                      :zIndex="10000"
                      @confirm="delRowById(r[rowKey])"
                    >
                      <a-icon class="close-icon" type="close" />
                    </a-popconfirm>
                  </template>
                  <a-icon class="close-icon" type="close" v-else @click.stop="subformTabClose(r)" />
                </template>
                <!-- <a-badge status="error" class="validate-error" v-if="r.__MODEL__.validateStatus === 'error'" /> -->
              </template>

              <WidgetDyform
                v-if="widget.configuration.tabEditOriginalForm"
                :inheritForm="rowForms[r[rowKey]]"
                :formUuid="widget.configuration.formUuid"
                :definitionVjson="formDefinitionVjson"
                :isSubform="true"
                :displayState="rowForms[r[rowKey]].displayState"
                :row="r"
                ref="subformDyform"
                @beforeMount="e => onSubformTabDyformBeforeMount(e, r)"
                @mounted="e => onSubformTabDyformMounted(r, e)"
              />
              <div v-else :class="['widget-form-layout']" style="margin: 0px">
                <table>
                  <tbody>
                    <template v-for="(col, ii) in columns">
                      <tr :key="col.id + '-' + index" v-if="col.dataIndex != '__operation'">
                        <td>
                          <a-row type="flex">
                            <a-col class="label-col" style="width: 150px">
                              <label
                                :class="[
                                  (
                                    r.__MODEL__[col.dataIndex + '_Required'] != undefined
                                      ? r.__MODEL__[col.dataIndex + '_Required']
                                      : customDataIndexSlotOptionMaps[col.dataIndex] &&
                                        customDataIndexSlotOptionMaps[col.dataIndex].required
                                  )
                                    ? 'ant-form-item-required'
                                    : ''
                                ]"
                              >
                                {{ col._title }}
                              </label>
                            </a-col>
                            <a-col flex="auto" class="content-col">
                              <SubformFormCell
                                :widget="customDataIndexSlotOptionMaps[col.dataIndex].widget"
                                :form="rowForms[r[rowKey]]"
                                :widgetSubform="widget"
                                :row="r"
                                :option="customDataIndexSlotOptionMaps[col.dataIndex]"
                                :rowIndex="index"
                                :jsModule="subformJsModule"
                                :formDefaultData="subformDefaultData"
                                @change="$evt => onCellComponentChange($evt, r, col.dataIndex)"
                                @mounted="$evt => onCellComponentMounted($evt, r, col.dataIndex)"
                              />
                            </a-col>
                          </a-row>
                        </td>
                      </tr>
                    </template>
                  </tbody>
                </table>
              </div>
            </a-tab-pane>
          </template>
          <template slot="tabBarExtraContent">
            <WidgetTableButtons
              v-if="showRowOperationButtons && subformActiveTabKey != undefined && subformActiveTabRowData != undefined"
              :key="subformActiveTabKey"
              :button="filterRowEndButton"
              :buttonPredicate="e => rowButtonPredicate(e, subformActiveTabRowData.row)"
              :visibleJudgementData="vTableButtonVisibleJudgementData"
              :meta="subformActiveTabRowData.row"
              size="small"
              @button-click="btn => onButtonClick(btn, subformActiveTabRowData.index, subformActiveTabRowData.row)"
            />
          </template>
        </a-tabs>
        <a-empty v-if="tableExpand && filteredRows.length == 0" :description="emptyText" />
      </template>
      <template v-else-if="widget.configuration.layout === 'form-inline'">
        <!-- 内联表单 -->
        <div class="widget-subform-inline">
          <div :class="['widget-subform-inline-wrapper', tableExpand ? '' : 'collapsed']">
            <a-empty :image="simpleImage" v-if="filteredRows.length == 0" class="widget-subform-inline-empty" :description="emptyText" />
            <div
              v-for="(r, index) in filteredRows"
              :key="'subforminline-' + r[rowKey]"
              :class="['widget-subform-inline-container', hoverRowIndex == index ? 'selected' : '']"
              @mouseenter="hoverRowIndex = index"
            >
              <a-card
                :bordered="false"
                :bodyStyle="{ padding: '0px' }"
                hoverable
                :class="[formInlineTitle(r) != undefined ? 'show-title' : undefined]"
              >
                <template slot="title">
                  {{ formInlineTitle(r) }}
                </template>
                <div :class="['widget-form-layout']" style="margin: 0px">
                  <table>
                    <tbody>
                      <tr v-for="(col, ii) in columns" :key="col.id + '-' + index">
                        <td>
                          <a-row type="flex">
                            <a-col class="label-col" style="width: 150px">
                              <label
                                :style="{ marginLeft: ii > 0 && widget.configuration.rowSelectType !== 'no' ? '18px' : '0px' }"
                                :class="[
                                  (
                                    r.__MODEL__[col.dataIndex + '_Required'] != undefined
                                      ? r.__MODEL__[col.dataIndex + '_Required']
                                      : customDataIndexSlotOptionMaps[col.dataIndex] &&
                                        customDataIndexSlotOptionMaps[col.dataIndex].required
                                  )
                                    ? 'ant-form-item-required'
                                    : ''
                                ]"
                              >
                                <template v-if="widget.configuration.rowSelectType === 'checkbox'">
                                  <a-checkbox
                                    :style="{ float: 'left', marginRight: '3px' }"
                                    v-if="ii == 0"
                                    @click="onFormInlineCheck(r[rowKey], r)"
                                    :checked="selectedRowKeys.indexOf(r[rowKey]) != -1"
                                  />
                                </template>
                                <template v-else-if="widget.configuration.rowSelectType === 'radio'">
                                  <a-radio
                                    :style="{ float: 'left', marginRight: '3px' }"
                                    v-if="ii == 0"
                                    @click="onFormInlineCheck(r[rowKey], r)"
                                    :checked="selectedRowKeys.indexOf(r[rowKey]) != -1"
                                  />
                                </template>
                                <template v-if="col.dataIndex == '__operation'">
                                  {{ $t('WidgetSubform.column.operation', '操作') }}
                                </template>
                                <template v-else>{{ col._title }}</template>
                              </label>
                            </a-col>
                            <a-col flex="auto" class="content-col">
                              <template v-if="col.dataIndex == '__operation'">
                                <WidgetTableButtons
                                  :button="filterRowEndButton"
                                  :buttonPredicate="e => rowButtonPredicate(e, r)"
                                  :visibleJudgementData="vTableButtonVisibleJudgementData"
                                  :meta="r"
                                  size="small"
                                  @button-click="btn => onButtonClick(btn, index, rows[index])"
                                />
                              </template>
                              <template v-else-if="customDataIndexSlotOptionMaps[col.dataIndex] != undefined">
                                <SubformFormCell
                                  :widget="customDataIndexSlotOptionMaps[col.dataIndex].widget"
                                  :form="rowForms[r[rowKey]]"
                                  :widgetSubform="widget"
                                  :row="r"
                                  :option="customDataIndexSlotOptionMaps[col.dataIndex]"
                                  :rowIndex="index"
                                  :jsModule="subformJsModule"
                                  :formDefaultData="subformDefaultData"
                                  @change="$evt => onCellComponentChange($evt, r, col.dataIndex)"
                                  @mounted="$evt => onCellComponentMounted($evt, r, col.dataIndex)"
                                />
                              </template>
                            </a-col>
                          </a-row>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </a-card>
            </div>
          </div>
        </div>
      </template>
    </div>
    <a-modal
      :title="modalTitle"
      :width="vModalWidth"
      :dialogStyle="{ top: modalTop, paddingBottom: '0px' }"
      :bodyStyle="vModalBodyStyle"
      :visible="formModalVisible"
      @cancel="formModalVisible = false"
      :mask="false"
      :destroyOnClose="true"
      :maskClosable="false"
    >
      <template v-if="widget.configuration.layout === 'table' && widget.configuration.rowEditMode === 'form' && editFormUuid">
        <!-- 指定表单编辑数据 -->
        <WidgetDyform
          :inheritForm="currentSelectedForm"
          :formUuid="editFormUuid"
          :formDatas="currentSelectedFormDatas"
          :displayState="isViewForm ? 'label' : 'edit'"
          ref="wDyform"
          :isSubform="true"
          @mounted="onSubformWidgetFormMounted"
        />
      </template>
      <template v-else>
        <a-form-model :model="currentSelectedForm.formData" ref="formModal" v-if="currentSelectedForm.namespace">
          <div :class="['widget-form-layout']">
            <table>
              <tbody>
                <template v-for="(col, ii) in columns">
                  <tr :key="col.id + '-' + index" v-if="col.key != undefined">
                    <td>
                      <a-row type="flex">
                        <a-col class="label-col" style="width: 150px">
                          <label
                            :class="[
                              (
                                currentEditRow != undefined && currentEditRow.__MODEL__[col.dataIndex + '_Required'] != undefined
                                  ? currentEditRow.__MODEL__[col.dataIndex + '_Required']
                                  : customDataIndexSlotOptionMaps[col.dataIndex].required
                              )
                                ? 'ant-form-item-required'
                                : ''
                            ]"
                          >
                            {{ col._title }}
                          </label>
                        </a-col>
                        <a-col flex="auto" class="content-col">
                          <SubformFormCell
                            :widget="customDataIndexSlotOptionMaps[col.dataIndex].widget"
                            :form="currentSelectedForm"
                            :widgetSubform="widget"
                            editMode="modalEdit"
                            :row="currentEditRow"
                            :option="customDataIndexSlotOptionMaps[col.dataIndex]"
                            :jsModule="subformJsModule"
                            :formDefaultData="subformDefaultData"
                          />
                        </a-col>
                      </a-row>
                    </td>
                  </tr>
                </template>
              </tbody>
            </table>
          </div>
        </a-form-model>
      </template>
      <template slot="footer">
        <a-button @click="formModalVisible = false">{{ $t('WidgetSubform.cancel', '取消') }}</a-button>
        <a-button type="primary" v-if="!isViewForm" @click="onFormModalOk">{{ $t('WidgetSubform.confirm', '确定') }}</a-button>
      </template>
    </a-modal>

    <a-modal
      v-if="importModalInfo.modalTemplate == undefined"
      v-model="importModalInfo.visible"
      :width="importModalInfo.width"
      :title="importModalInfo.title"
      :okText="importModalInfo.okText"
      :maskClosable="false"
      :okButtonProps="{
        props: { icon: 'upload', loading: importModalInfo.importing, disabled: importModalInfo.importFile.fileID == undefined }
      }"
      :body-style="{ height: '235px' }"
      @ok="onConfirmImportData"
      @cancel="onCancelImportData"
    >
      <div style="height: 155px">
        <a-upload-dragger
          ref="uploader"
          name="file"
          :multiple="false"
          :showUploadList="false"
          :before-upload="e => beforeUpload(e, 100)"
          :customRequest="e => customRequest(e)"
          :fileList="importModalInfo.fileList"
        >
          <p class="ant-upload-drag-icon">
            <a-icon :type="importModalInfo.uploading ? 'loading' : 'inbox'" />
          </p>
          <p class="ant-upload-text">{{ $t('WidgetSubform.uploadFilePlaceMessage', '点击或者拖拽文件到此区域上传') }}</p>
          <p class="ant-upload-hint">
            {{ $t('WidgetSubform.downloadImportTemplateFilePrefixText', '下载导入模板') }}
            <a-button size="small" icon="link" type="link" @click.stop="downloadLink(importModalInfo.importFileTemplate.url)">
              {{ importModalInfo.importFileTemplate.name }}
            </a-button>
          </p>
        </a-upload-dragger>
        <div style="display: flex; justify-content: space-between; align-items: center; line-height: 52px">
          <div v-if="importModalInfo.importFile.fileID != undefined">
            <label @click.stop="() => {}">
              <a-icon style="color: var(--w-success-color)" type="check-circle" theme="filled" />
              {{ $t('WidgetSubform.uploadDoneImportFilePrefixText', '已上传') }}:
            </label>
            <a-tag
              @click.stop="() => {}"
              closable
              @close.stop="importModalInfo.importFile.fileID = undefined"
              v-show="importModalInfo.importFile.fileID != undefined"
              class="primary-color"
            >
              {{ importModalInfo.importFile.fileName }}
            </a-tag>
          </div>
          <div v-if="importModalInfo.importFile.fileID != undefined" style="color: #999">
            <a-space>
              <ExcelImportDataPreviewer
                :file="importModalInfo.importFile.File"
                ref="excelImportDataPreviewer"
                :sheetRules="importModalInfo.sheetRules"
              >
                <a-button icon="profile" size="small" type="link">
                  {{ importModalInfo.successCount != undefined ? '导入数据结果' : '导入数据预览' }}
                </a-button>
              </ExcelImportDataPreviewer>

              <template v-if="importModalInfo.successCount != undefined">
                <a-divider type="vertical" />
                <div>
                  <a-tag color="green">
                    {{ $t('WidgetTable.importModal.importSuccessText', '成功') }} {{ importModalInfo.successCount }}
                  </a-tag>
                  <a-tag color="red">{{ $t('WidgetTable.importModal.importFailText', '失败') }} {{ importModalInfo.failCount }}</a-tag>
                </div>
              </template>
            </a-space>
          </div>
        </div>
      </div>
    </a-modal>

    <!-- 导入模板弹出 -->
    <component
      v-else-if="importModalInfo.modalTemplate != undefined && importModalInfo.visible"
      :is="importModalInfo.modalTemplate"
      :key="importModalInfo.key"
      :widget="widget"
      :file-template="importModalInfo.importFileTemplate"
      :confirm-import-file="onConfirmImportData"
      :customFileUploadRequest="customRequest"
      :importModalInfo="importModalInfo"
      @close="importModalClose"
    />

    <a-modal
      v-model="exportColumnDefModalVisible"
      :title="$t('WidgetSubform.selectImportColumnModalTitle', '选择导出列')"
      @ok="onConfirmExportColumnRow"
      :getContainer="getExportModalContainer"
    >
      <div>
        <a-checkbox
          :indeterminate="userCheckedExportColumn.indeterminate"
          :checked="userCheckedExportColumn.checkAll"
          @change="onExportColCheckAll"
        >
          {{ $t('WidgetSubform.selectAll', '全选') }}
        </a-checkbox>
        <a-divider style="margin: 6px 0px" />
        <a-checkbox-group
          class="export-col-checkbox-group"
          v-model="userCheckedExportColumn.key"
          :options="userCheckedExportColumn.options"
          @change="onExportColCheckChange"
        ></a-checkbox-group>
      </div>
    </a-modal>
  </div>
</template>

<script type="text/babel">
import { Empty } from 'ant-design-vue';
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import { generateId, deepClone, swapArrayElements, addWindowResizeHandler, addElementResizeDetector } from '@framework/vue/utils/util';
import formMixin from '../mixin/form-common.mixin';
import SubformTableCell from './subform-table-cell.vue';
import SubformFormCell from './subform-form-cell.vue';
import SubformProxyFormCell from './subform-proxy-form-cell.vue';
import SubformSearch from './subform-search.vue';
import WidgetTableButtons from '@pageAssembly/app/web/widget/widget-table/widget-table-buttons.vue';
import md5 from '@framework/vue/utils/md5';
import { download } from '@framework/vue/utils/util';
import { downloadLink, psScrollResize } from '@framework/vue/utils/function';
import { groupBy, debounce, each, filter, map, isEmpty, set, merge, findIndex } from 'lodash';
import ExcelImportDataPreviewer from '@pageAssembly/app/web/widget/widget-table/excel-import-data-previewer.vue';
import moment from 'moment';
import './css/index.less';
export default {
  extends: FormElement,
  name: 'WidgetSubform',
  inject: ['antdLocale'],
  mixins: [widgetMixin, formMixin],
  props: {},
  provide() {
    return {
      widgetSubformContext: this
    };
  },
  data() {
    let data = {
      formUuid: this.widget.configuration.formUuid, //从表的表单UUID
      operationColWidth: 70, // 操作列宽度由实际按钮挂载后计算出来
      scrollX: 0,
      columsScrollX: 1200,
      rowKey: '$$id',
      rowChildKey: '$$children',
      displayValuePreload: false,
      formModalVisible: false, // 控制从表行数据的弹窗是否展示
      currentEditRowCell: '',
      hoverRowIndex: null,
      customRenderSlotOptions: [],
      customDataIndexSlotOptionMaps: {},
      customColTitleSlotOptions: [],
      fields: [],
      selectedRowKeys: [],
      selectedRows: [],
      expandedRowKeys: [],
      fieldWaitPreload: [],
      rows: [], // 行数据 : [ { ...表单字段数据 , __MODEL__: { ... 控制变量}} , ... ]
      rowForms: {},
      rowKeyMap: {},
      columns: [],
      loading: false,
      tableExpand: this.designMode ? true : this.widget.configuration.isCollapse ? !this.widget.configuration.defaultCollapse : true, // 从表不可折叠默认展开，可折叠时看是否默认折叠
      allowCellRenderTemplate: false, // 是否允许单元格渲染模板（几百行数据的时候会卡）
      formElementRules: {},
      formInlineCheckAll: false,
      formInlineIndeterminate: false,
      modalTitle: '',
      initedColumn: false,
      initedDefaultSubformRows: false,
      clientScale: {
        width: 0,
        height: 0
      },
      modalTop: '100px',
      rowComponentRenderQueue: [],
      subformDefaultData: { form_uuid: this.widget.configuration.formUuid }, // 从表默认值
      enableColumnFreeze: this.widget.configuration.enableColumnFreeze,
      modifyScrollPosition: false,
      importModalInfo: {
        title: '数据导入',
        okText: '导入',
        modalTemplate: undefined,
        width: 700,
        visible: false,
        importing: false,
        strict: false,
        uploading: false,
        sheetRules: undefined,
        importCode: undefined,
        importLog: false,
        importFileTemplate: {
          url: undefined,
          name: undefined
        },
        successCount: undefined,
        failCount: undefined,
        importFile: {
          fileID: undefined,
          fileName: undefined
        },
        fileList: [],
        key: generateId(),
        importResult: {},
        importService: undefined
      },
      exportColumnDefModalVisible: false,
      userCheckedExportColumn: { indeterminate: false, checkAll: false, key: [], options: [] },
      searchForm: { keyword: undefined, column: {} },
      searchKey: undefined,
      proxyRowDyform: undefined,
      editFormUuid: undefined,
      isViewForm: false,
      fieldMap: {}, //  从表所有字段组件集合
      requiredConditionMap: {}, // 从表字段必填条件集合
      subformJsModule: undefined
    };

    let pagination = this.widget.configuration.pagination;
    if (pagination.enable) {
      let pageSizeOptions = pagination.pageSizeOptions || [];
      if (!this.designMode) {
        if (!pageSizeOptions.includes(pagination.pageSize + '')) {
          pageSizeOptions.push(pagination.pageSize + '');
        }
      }
      data.pagination = {
        current: 1,
        total: 0,
        pageSize: pagination.pageSize,
        pageSizeOptions: pageSizeOptions,
        showQuickJumper: !!pagination.showQuickJumper,
        hideOnSinglePage: pagination.hideOnSinglePage,
        showSizeChanger: !!pagination.showSizeChanger
      };

      data.defaultHideOnSinglePage = pagination.hideOnSinglePage;
      if (pagination.showTotalPage) {
        data.pagination.showTotal = function (total, range) {
          const totalPages = total % this.pageSize == 0 ? parseInt(total / this.pageSize) : parseInt(total / this.pageSize + 1);
          return pagination.showTotalPage
            ? `${this.$t('WidgetSubform.totalPages', { totalPages }, `共${totalPages}页`)}/${this.$t(
                'WidgetSubform.totalRecords',
                { total },
                `${total}条记录`
              )}`
            : `${this.$t('WidgetSubform.totalRecords', { total }, `${total}条记录`)}`;
        };
      }
    } else {
      data.pagination = false;
    }

    data.rowDataAsMD5 = this.widget.id;
    data.formDefinitionVjson = undefined;
    data.subformActiveTabKey = undefined;
    data.rowDeleteButton = undefined;
    data.waitSearchRowKeys = [];
    data.filteredRowKeys = [];
    return data;
  },
  components: { WidgetTableButtons, SubformTableCell, SubformFormCell, SubformSearch, SubformProxyFormCell, ExcelImportDataPreviewer },
  computed: {
    subformWidgetIsReady() {
      return this.isMounted && this.initedColumn && this.initedDefaultSubformRows;
    },
    defaultEvents() {
      return [
        {
          id: 'setColumnVisible',
          title: '设置列为显示或者隐藏'
        },
        {
          id: 'setColumnEditable',
          title: '设置列为编辑或者不可编辑'
        },
        {
          id: 'setColumnRequired',
          title: '设置列为必填或者不必填'
        }
      ];
    },
    defaultVisibleJudgementData() {
      return {
        ...this._vShowByDateTime,
        ...this._vShowByUserData,
        ...this._vShowByWorkflowData,
        ...(this.dyform != undefined ? { __DYFORM__: { editable: this.dyform.displayState == 'edit' } } : {}),
        _URL_PARAM_: this.vUrlParams
      };
    },
    vTableButtonVisibleJudgementData() {
      let __TABLE__ = {};
      __TABLE__.selectedRowCount = this.selectedRowKeys.length;
      return {
        MAIN_FORM_DATA: this.form.formData,
        ...this.defaultVisibleJudgementData,
        __TABLE__
      };
    },
    vTableSelectedData() {
      return {
        selectedRowKeys: this.selectedRowKeys,
        selectedRows: this.selectedRows
      };
    },
    rowSelection() {
      return this.widget.configuration.rowSelectType == 'no'
        ? null
        : {
            type: this.widget.configuration.rowSelectType,
            selectedRowKeys: this.selectedRowKeys,
            onChange: this.selectRowChange,
            columnWidth: 60
          };
    },
    subformActiveTabRowData() {
      if (this.subformActiveTabKey != undefined) {
        for (let i = 0, len = this.rows.length; i < len; i++) {
          if (this.rows[i][this.rowKey] == this.subformActiveTabKey) {
            return {
              row: this.rows[i],
              index: i
            };
          }
        }
      }
      return undefined;
    },
    // 是否有按钮显示规则
    hasButtonRules() {
      return this.form.formElementRules && this.form.formElementRules[this.widget.id] && this.form.formElementRules[this.widget.id].buttons;
    },
    showHeaderButtons() {
      if (this.hasButtonRules) {
        // 从流程过来的按钮显示规则
        return this.widget.configuration.headerButton.buttons.length > 0;
      }
      // 不可编辑情况下，判断是否头部按钮有配置显隐条件
      if (!this.editable && this.widget.configuration.headerButton.buttons.length > 0) {
        for (let b of this.widget.configuration.headerButton.buttons) {
          if (b.defaultVisibleVar && b.defaultVisibleVar.enable && b.defaultVisibleVar.conditions.length > 0) {
            return true;
          }
        }
      }
      return this.widget.configuration.headerButton.buttons.length > 0 && this.editable;
    },
    filterRowEndButton() {
      let rowButton = this.widget.configuration.rowButton,
        filteredRowButton = JSON.parse(JSON.stringify(rowButton));
      filteredRowButton.buttons = [];
      for (let i = 0, len = rowButton.buttons.length; i < len; i++) {
        if (rowButton.buttons[i].defaultVisible && rowButton.buttons[i].position !== 'underColumn') {
          filteredRowButton.buttons.push(rowButton.buttons[i]);
        }
      }
      return filteredRowButton;
    },

    filterUnderColButtonMap() {
      let rowButton = this.widget.configuration.rowButton;
      let map = {};
      for (let i = 0, len = rowButton.buttons.length; i < len; i++) {
        if (
          rowButton.buttons[i].defaultVisible &&
          rowButton.buttons[i].position === 'underColumn' &&
          rowButton.buttons[i].displayUnderColumn
        ) {
          if (map[rowButton.buttons[i].displayUnderColumn] == undefined) {
            map[rowButton.buttons[i].displayUnderColumn] = JSON.parse(JSON.stringify(rowButton));
            map[rowButton.buttons[i].displayUnderColumn].buttons = [];
          }
          map[rowButton.buttons[i].displayUnderColumn].buttons.push(rowButton.buttons[i]);
        }
      }
      return map;
    },
    showRowOperationButtons() {
      if (!this.designMode && !this.editable) {
        if (!this.hasButtonRules) {
          return false;
        }
      }
      let buttons = this.filterRowEndButton.buttons;
      for (let i = 0, len = buttons.length; i < len; i++) {
        if (buttons[i].defaultVisible) {
          return true;
        }
      }
      return false;
    },
    isCellEdit() {
      return this.widget.configuration.rowEditMode == 'cell';
    },

    colIsLabelValWidget() {
      let result = {};
      for (let i = 0, len = this.configuration.columns.length; i < len; i++) {
        let col = this.configuration.columns[i];
        result[col.dataIndex] = col.widget.configuration.isLabelValueWidget;
      }
      return result;
    },
    vModalBodyStyle() {
      let style = { 'overflow-y': 'auto' };
      style.height = this.vModalHeight + 'px';
      if (this.widget.configuration.editModal.style.size !== 'fullscreen') {
        style.maxHeight = 'calc(100vh - 250px)';
      }
      return style;
    },
    vModalHeight() {
      if (EASY_ENV_IS_BROWSER) {
        let clientHeight = this.clientScale.height,
          size = this.widget.configuration.editModal.style.size,
          hfHeight = 108; // 弹窗头部、底部高度综合
        // 常见屏幕高度： 1440 、1080、900、768
        if (size === 'small') {
          if (clientHeight <= 768) {
            return 520;
          } else if (clientHeight > 768 && clientHeight <= 900) {
            return 580;
          } else if (clientHeight > 900 && clientHeight <= 1080) {
            return 640;
          } else if (clientHeight > 1080 && clientHeight <= 1440) {
            return 700;
          }
          return 760;
        } else if (size === 'middle') {
          if (clientHeight <= 768) {
            return 580;
          } else if (clientHeight > 768 && clientHeight <= 900) {
            return 700;
          } else if (clientHeight > 900 && clientHeight <= 1080) {
            return 900;
          } else if (clientHeight > 1080 && clientHeight <= 1440) {
            return 1100;
          }
          return 820;
        } else if (size === 'large') {
          if (clientHeight <= 768) {
            return 760;
          } else if (clientHeight > 768 && clientHeight <= 900) {
            return 1100;
          } else if (clientHeight > 900 && clientHeight <= 1080) {
            return 1200;
          } else if (clientHeight > 1080 && clientHeight <= 1440) {
            return 1400;
          }
          return 1200;
        } else if (size === 'fullscreen') {
          return clientHeight - hfHeight;
        } else {
          return this.widget.configuration.editModal.style.height;
        }
      }
      return 0;
    },
    vModalWidth() {
      if (EASY_ENV_IS_BROWSER) {
        let clientWidth = this.clientScale.width,
          size = this.widget.configuration.editModal.style.size;
        // 常见屏幕： 1920 、1440 、 1366 、1280
        if (size === 'small') {
          if (clientWidth <= 1280) {
            return 520;
          } else if (clientWidth > 1280 && clientWidth <= 1366) {
            return 580;
          } else if (clientWidth > 1366 && clientWidth <= 1440) {
            return 640;
          } else if (clientWidth > 1440 && clientWidth <= 1920) {
            return 700;
          }
          return 760;
        } else if (size === 'middle') {
          if (clientWidth <= 1280) {
            return 580;
          } else if (clientWidth > 1280 && clientWidth <= 1366) {
            return 700;
          } else if (clientWidth > 1366 && clientWidth <= 1440) {
            return 900;
          } else if (clientWidth > 1440 && clientWidth <= 1920) {
            return 1100;
          }
          return 820;
        } else if (size === 'large') {
          if (clientWidth <= 1280) {
            return 760;
          } else if (clientWidth > 1280 && clientWidth <= 1366) {
            return 1100;
          } else if (clientWidth > 1366 && clientWidth <= 1440) {
            return 1200;
          } else if (clientWidth > 1440 && clientWidth <= 1920) {
            return 1400;
          }
          return 1200;
        } else if (size === 'fullscreen') {
          this.modalTop = '0px';
          return clientWidth;
        } else {
          return this.widget.configuration.editModal.style.width;
        }
      }
      return 0;
    },
    columnDataIndexes() {
      let x = [];
      this.widget.configuration.columns.forEach(c => {
        if (c.dataIndex != undefined) {
          x.push(c.dataIndex);
        }
      });
      return x;
    },
    rowLength() {
      return this.rows.length;
    },
    // 用于判断是否显示从表头部
    hastableTitleRow() {
      if (this.widget.configuration.title) {
        // 有标题，显示
        return true;
      } else if (this.showHeaderButtons) {
        // 有头部按钮，显示
        return true;
      } else if (this.widget.configuration.isCollapse) {
        // 从表可折叠，显示
        return true;
      }
      return false;
    },

    filteredRows() {
      if (this.searchKey == undefined) {
        return this.rows;
      }
      let rows = [];
      for (let i = 0, len = this.rows.length; i < len; i++) {
        if (this.filteredRowKeys.includes(this.rows[i][this.rowKey]) || !this.waitSearchRowKeys.includes(this.rows[i][this.rowKey])) {
          rows.push(this.rows[i]);
          continue;
        }
      }
      return rows;
    },

    // 从表是否表格布局且有分页
    tableHasPagination() {
      return (
        !this.designMode &&
        this.widget.configuration.layout == 'table' &&
        this.widget.configuration.pagination &&
        this.widget.configuration.pagination.enable
      );
    },
    proxyTableColWidgets() {
      let colWidget = [];
      if (this.tableHasPagination) {
        for (let i = 0; i < this.customRenderSlotOptions.length; i++) {
          colWidget.push(this.cloneWidget(this.customRenderSlotOptions[i].widget));
        }
      }
      return colWidget;
    },
    emptyText() {
      return this.$t(this.searchKey ? 'WidgetSubform.notFoundData' : 'WidgetSubform.noData', this.searchKey ? '查无数据' : '暂无数据');
    }
  },
  beforeCreate() {
    this.simpleImage = Empty.PRESENTED_IMAGE_SIMPLE;
  },
  created() {
    this.currentSelectedForm = {};
    this.currentSelectedFormDatas = {};
    this.currentEditRow = undefined;
    this.rowForms = {};
    this.emitRowFormDataChange = debounce(this.emitRowFormDataChange.bind(this), 500);
    this.columnWidthResize = debounce(this.columnWidthResize.bind(this), 300);
    this.setFixedTrHeightByKey = debounce(this.setFixedTrHeightByKey.bind(this), 200);
    // 从表的字段规则：由外部定义传入
    if (this.form.formElementRules && this.form.formElementRules[this.widget.id] && this.form.formElementRules[this.widget.id].children) {
      Object.assign(this.formElementRules, this.form.formElementRules[this.widget.id].children);
      if (this.form.formElementRules[this.widget.id].buttons) {
        // 按钮设置
        each(this.widget.configuration.headerButton.buttons, item => {
          //  从表按钮规则传入，要关闭按钮的显隐条件，以规则配置显隐为主
          if (item.defaultVisibleVar) {
            item.defaultVisibleVar.enable = false;
          }
          if (this.form.formElementRules[this.widget.id].buttons.headerButton.indexOf(item.code || item.id) > -1) {
            item.defaultVisible = true;
          } else {
            item.defaultVisible = false;
          }
        });
        each(this.widget.configuration.rowButton.buttons, item => {
          //  从表按钮规则传入，要关闭按钮的显隐条件，以规则配置显隐为主
          if (item.defaultVisibleVar) {
            item.defaultVisibleVar.enable = false;
          }
          if (this.form.formElementRules[this.widget.id].buttons.rowButton.indexOf(item.code || item.id) > -1) {
            item.defaultVisible = true;
          } else {
            item.defaultVisible = false;
          }
        });
      }
    }

    if (!this.designMode && this.widget.configuration.layout == 'form-tabs' && this.editable) {
      // 标签页编辑从表，默认的行删除按钮是否转换为页签的关闭操作
      let buttons = this.widget.configuration.rowButton.buttons;
      for (let i = 0, len = buttons.length; i < len; i++) {
        if (buttons[i].id == 'delRow' && buttons[i].defaultVisible && buttons[i].displayAsTabClose) {
          this.rowDeleteButton = buttons[i];
          buttons.splice(i, 1);
          break;
        }
      }
    }

    if (this.formUuid) {
      if (!this.designMode) {
        this.getDefaultData({ formUuid: this.formUuid }).then(data => {
          if (data) {
            Object.assign(this.subformDefaultData, data);
          }
        });
      }
      if (EASY_ENV_IS_BROWSER) {
        if (!this.designMode) {
          this.fetchLatestFormColumns();
        } else {
          this.setColumns();
          for (let col of this.widget.configuration.columns) {
            if (col.widget) {
              this.fields.push(col.widget);
            }
          }
        }
      }
    }
  },
  methods: {
    callSubformSearchFilter(c, option) {
      return this.$refs.subformSearch.onFilter(c, option);
    },
    tabPanelTitle(r) {
      let value = this.rowForms[r[this.rowKey]].formData[this.widget.configuration.formInlineTitleField];
      if (value != undefined && value != null && value != '') {
        return value;
      } else {
        let index = 1;
        for (let i = 0, len = this.rows.length; i < len; i++) {
          if (this.rows[i][this.rowKey] == r[this.rowKey]) {
            index = i + 1;
            break;
          }
        }
        return (
          (this.widget.configuration.defaultTabPaneTitle
            ? this.$t('defaultTabPaneTitle', this.widget.configuration.defaultTabPaneTitle)
            : this.$t('WidgetSubform.defaultTabPaneTitle', '页签')) + (this.widget.configuration.tabPaneTitleOrderSuffix ? ' ' + index : '')
        );
      }
    },
    formInlineTitle(r) {
      let value = this.rowForms[r[this.rowKey]].formData[this.widget.configuration.formInlineTitleField];
      return value != undefined && value != null && value != '' ? value : undefined;
    },

    subformTabClose(record) {
      if (this.rowDeleteButton != undefined) {
        if (
          this.rowDeleteButton.confirmConfig &&
          this.rowDeleteButton.confirmConfig.enable &&
          this.rowDeleteButton.confirmConfig.popType == 'confirm'
        ) {
          let _this = this;
          let _title = this.resolvePopconfirmTitle(this.rowDeleteButton.confirmConfig.title, record),
            _content = this.resolvePopconfirmTitle(this.rowDeleteButton.confirmConfig.content, record);
          this.$confirm({
            title: _title,
            okText: this.rowDeleteButton.confirmConfig.okText,
            cancelText: this.rowDeleteButton.confirmConfig.cancelText,
            content: _content,
            onOk() {
              _this.delRowById(record[_this.rowKey]);
            },
            onCancel() {}
          });
        } else {
          this.delRowById(record[this.rowKey]);
        }
      }
    },
    tabCloseVisible(row) {
      if (this.rowDeleteButton != undefined) {
        if (row.status == '2') {
          return false;
        }
        return true;
      }
      return false;
    },
    delRowById(rid) {
      for (let i = 0, len = this.rows.length; i < len; i++) {
        if (this.rows[i][this.rowKey] == rid) {
          this.delRow(undefined, undefined, i);
          return;
        }
      }
    },
    headButtonPredicate(button) {
      if ('delRow' == button.id) {
        // 删除按钮：未选择行数据，不展示
        if (this.selectedRowKeys.length > 0) {
          let num = this.selectedRowKeys.length;
          button.titleFormatter = title => {
            return `${this.$t('header_' + button.id, button.title)} (${num})`;
          };
        }
        return this.selectedRowKeys.length > 0;
      }

      if (
        ['moveUpRow', 'moveDownRow'].includes(button.id) &&
        ((this.widget.configuration.layout == 'form-tabs' && this.rows.length < 1) ||
          (this.widget.configuration.layout !== 'form-tabs' && this.selectedRowKeys.length == 0))
      ) {
        return false;
      }

      // 无按钮规则的前提下，不可编辑情况下，要判断默认的头部操作按钮是否有配置显隐条件，如果没有则按不可编辑默认不展示
      if (
        !this.hasButtonRules &&
        !this.editable &&
        button.defaultVisibleVar &&
        (button.defaultVisibleVar.enable === false ||
          button.defaultVisibleVar.conditions.length == 0 ||
          // 兼容旧的错误配置情况
          (button.defaultVisibleVar.conditions.length == 0 &&
            button.defaultVisibleVar.conditions[0].operator == 'true' &&
            button.defaultVisibleVar.conditions[0].code == undefined &&
            button.defaultVisibleVar.conditions[0].value == undefined))
      ) {
        return false;
      }
      // 从表头部按钮和行按钮存在相同id的情况，头部按钮做额外处理，行按钮不做处理
      button.titleFormatter = title => {
        return this.$t('header_' + button.id, button.title);
      };

      return true;
    },
    rowButtonPredicate(button, row) {
      if (button.id == 'delRow') {
        // 行级删除按钮，默认不可删除的数据，不展示删除按钮
        if (row.status == '2') {
          return false;
        }
      } else if (
        button.id == 'viewRow' &&
        !(this.widget.configuration.layout === 'table' && this.widget.configuration.rowEditMode == 'form')
      ) {
        return false;
      }
      // if (['moveUpRow', 'moveDownRow'].includes(button.id) && this.rows.length <= 1) {
      //   return false;
      // }
      // if (this.widget.configuration.layout == 'form-tabs' && button.id == 'editRow') {
      //   return false;
      // }

      return true;
    },
    onExportColCheckChange() {
      let userChecked = this.userCheckedExportColumn;
      userChecked.indeterminate = userChecked.key.length > 0 && userChecked.key.length < userChecked.options.length;
      userChecked.checkAll = userChecked.key.length > 0 && userChecked.key.length == userChecked.options.length;
    },
    onExportColCheckAll(e) {
      let userChecked = this.userCheckedExportColumn;
      userChecked.indeterminate = false;
      userChecked.checkAll = e.target.checked;
      if (e.target.checked) {
        userChecked.key.splice(0, userChecked.key.length);
        userChecked.options.forEach(opt => {
          userChecked.key.push(opt.value);
        });
      } else {
        userChecked.key.splice(0, userChecked.key.length);
      }
    },
    getDefaultData({ formUuid }) {
      return new Promise((resolve, reject) => {
        $axios.post(`/proxy/api/dyform/data/getDefaultFormData?formUuid=${formUuid}`).then(({ data }) => {
          if (data.code == 0 && data.data) {
            resolve(data.data);
          }
        });
      });
    },
    clearTableColumns() {
      this.columns.splice(0, this.columns.length);
      this.customRenderSlotOptions.splice(0, this.customRenderSlotOptions.length);
      this.customColTitleSlotOptions.splice(0, this.customColTitleSlotOptions.length);
      this.customDataIndexSlotOptionMaps = {};
    },
    // 计算表格列
    setColumns() {
      let configuration = this.widget.configuration,
        _this = this,
        leftFreezeColNum = this.widget.configuration.leftFreezeColNum,
        rightFreezeColNum = this.widget.configuration.rightFreezeColNum,
        enableFreezed = !this.designMode && this.widget.configuration.enableColumnFreeze;
      this.clearTableColumns();
      let scrollX = 0, // 所有数据列宽度的和
        fixedWidth = 0; // 有固定列时，固定列宽度的和
      if (configuration.rowSelectType && configuration.rowSelectType !== 'no') {
        scrollX += 60; // 选择列宽度
        if (enableFreezed && leftFreezeColNum > 0) {
          fixedWidth += 60;
        }
      }
      if (configuration.addSerialNumber && configuration.layout == 'table') {
        this.columns.push({
          title: this.$t('WidgetSubform.column.serialNumber', '序号'),
          dataIndex: '__serialnumber',
          ellipsis: true, //序号默认省略
          align: 'center',
          fixed: enableFreezed && leftFreezeColNum > 0 ? 'left' : false,
          width: 70,
          initWidth: 70,
          customRender: (text, record, index) => {
            // 分组行数据的情况下：
            // 1. 默认情况下，每一组的行序号都是重置的
            // 2. 序号不重置的情况下 , autoGroupRows 会自动设置seq
            if (configuration.enableTableGroup) {
              if (!configuration.groupResetSerialNumber || record.__MODEL__.isGroupRow) {
                return record.__MODEL__.seq;
              }
            }
            let serialnumber = `${index + 1}`;
            if (!configuration.pageResetSerialNumber && _this.pagination) {
              serialnumber = `${(_this.pagination.current - 1) * _this.pagination.pageSize + index + 1}`;
            }
            return serialnumber;
          }
        });
        // leftFreezeColNum++;
        scrollX += 70;
        if (enableFreezed && leftFreezeColNum > 0) {
          fixedWidth += 70; // 左边序号宽度
        }
      }
      for (let i = 0, len = configuration.columns.length; i < len; i++) {
        let col = deepClone(configuration.columns[i]);
        // 列如果没有配置组件，则跳过
        if (!col.widget || (!this.designMode && !this.fieldMap[col.dataIndex])) {
          continue;
        }
        col.title = this.$t(`Widget.${this.widget.id}.${col.id}`, col.title);
        col._title = col.title;
        if (this.designMode) {
          // 设计模式下，删除宽度，避免表格列头在设计模式下滚动隐藏了部分列头
          delete col.width;
        }

        let slotOptions = {
          dataIndex: col.dataIndex,
          slotName: col.id,
          widget: col.widget,
          required: col.required === true,
          editable: col.defaultDisplayState === 'edit',
          ellipsis: col.ellipsis,
          relaFields: [],
          enableColRenderSlot: col.enableColRenderSlot,
          colRenderTemplateName: col.colRenderTemplateName
        };
        if (col.formula != undefined && col.formula.enable && col.formula.items.length > 0) {
          slotOptions.formula = col.formula;
        }
        if (this.filterUnderColButtonMap[col.dataIndex]) {
          slotOptions.button = this.filterUnderColButtonMap[col.dataIndex];
        }

        col.hidden = col.defaultDisplayState === 'hidden';

        if (this.formElementRules[col.widget.id] === undefined) {
          // 未定义规则的情况下：以字段列配置的显示模式规则
          this.formElementRules[col.widget.id] = {
            dataIndex: col.dataIndex,
            displayAsLabel: this.form.displayState == 'label' && col.defaultDisplayState !== 'edit',
            editable: this.form.displayState == 'edit' && col.defaultDisplayState !== 'unedit',
            hidden: col.defaultDisplayState === 'hidden',
            columnRuleApplyToForm:
              this.widget.configuration.layout == 'table' && this.widget.configuration.rowEditMode == 'form' && col.ruleApplyToForm === true
          };
        } else {
          this.formElementRules[col.widget.id].dataIndex = col.dataIndex;
          if (this.formElementRules[col.widget.id].hidden !== undefined) {
            col.hidden = this.formElementRules[col.widget.id].hidden;
          }

          if (this.formElementRules[col.widget.id].editable != undefined) {
            slotOptions.editable = this.formElementRules[col.widget.id].editable;
            col.editable = slotOptions.editable;
          }
        }

        if (this.formElementRules[col.widget.id].editable) {
          // 从表字段有可编辑权限的情况下，对表格列显示为组件模式，直接以编辑模式渲染
          slotOptions.editable = this.configuration.cellDefaultDisplayState == 'widget';
        }

        col.scopedSlots = {
          customRender: col.id
        };

        // 从表以文本展示状态，则列不可编辑
        if (this.displayAsLabel) {
          slotOptions.editable = false;
          col.editable = false;
        }

        if (col.sortable.enable) {
          col.sorter = function (a, b) {
            return _this.sorter(a, b, col);
          };
        }

        // 如果规则有必填设置，则将列的必填改成规则的必填
        if (this.formElementRules[col.widget.id] && this.formElementRules[col.widget.id].required != undefined) {
          slotOptions.required = this.formElementRules[col.widget.id].required;
        } else {
          // 如果规则没有必填设置，则规则的必填跟着列的必填
          this.formElementRules[col.widget.id].required =
            // 必填条件规则优先
            col.requiredCondition && col.requiredCondition.enable && col.requiredCondition.conditions.length > 0
              ? undefined
              : slotOptions.required;
        }
        // if (col.requiredCondition && col.requiredCondition.enable && col.requiredCondition.conditions.length > 0) {
        //   if (slotOptions.required) {
        //     this.formElementRules[col.widget.id].requiredCondition = col.requiredCondition;
        //   }
        //   this.requiredConditionMap[col.widget.id] = col.requiredCondition;
        // }

        slotOptions.widget.configuration.required = slotOptions.required;

        let relaOtherFields = [];
        /**
         *  表格单元格编辑模式情况：组件有真实值、显示值的前提下，实现第一行的单元格内的组件实例化预加载。
         *  待第一行的所有单元格需要组件预加载的字段全部加载完成，则其他行对于的单元格显示值可以通过第一
         *  行的单元格组件获取
         */
        if (
          !col.hidden &&
          configuration.layout == 'table' &&
          configuration.rowEditMode == 'cell' &&
          (col.widget.configuration.isLabelValueWidget || col.widget.configuration.displayValueField)
        ) {
          this.fieldWaitPreload.push(slotOptions.dataIndex);
          slotOptions.getDisplayValue = true;
        }

        if (col.widget.configuration.displayValueField) {
          // 有关联的显示值字段
          relaOtherFields.push(col.widget.configuration.displayValueField);
        }

        slotOptions.relaFields = Array.from(new Set(relaOtherFields.concat(col.widget.configuration.fieldsRelated || []))); // 关联其他字段

        if (!col.hidden) {
          // 隐藏列不展示
          this.customRenderSlotOptions.push(slotOptions);
          this.customDataIndexSlotOptionMaps[col.dataIndex] = slotOptions;

          col.scopedSlots.title = `columnTitle-${col.id}`;
          this.customColTitleSlotOptions.push({
            title: col.title,
            slotName: col.scopedSlots.title,
            required: slotOptions.required,
            dataIndex: col.dataIndex
          });
          delete col.title;
          col.customHeaderCell = this.customHeaderCell;
          col.customCell = (record, rowIndex) => {
            return _this.customCell(record, rowIndex, col);
          };
          col.key = `${col.dataIndex}_${i}`; // 避免使用同一个字段渲染多个列导致的冲突
          col.editable = slotOptions.editable;

          //  左列冻结
          if (enableFreezed) {
            if (leftFreezeColNum > 0) {
              col.fixed = 'left';
              if (col.width == undefined) {
                // 如果未设置列宽度，则默认设置冻结列的宽度，避免出现表格内容列错位
                col.width = 120;
                col.initWidth = 'auto';
              }
              leftFreezeColNum--;
              fixedWidth += col.width;
            }
          }

          this.columns.push(col);

          if (col.width) {
            scrollX += col.width;
          }
        }
      }
      // 右列冻结
      if (enableFreezed) {
        for (let i = this.columns.length - 1; i >= 0 && rightFreezeColNum > 0; i--) {
          if (this.columns[i].fixed !== 'left') {
            this.columns[i].fixed = 'right';
            if (this.columns[i].width == undefined) {
              // 如果未设置列宽度，则默认设置冻结列的宽度，避免出现表格内容列错位
              this.columns[i].width = 120;
              this.columns[i].initWidth = 'auto';
              scrollX += this.columns[i].width;
            }
            rightFreezeColNum--;
            fixedWidth += this.columns[i].width;
          }
        }
      }
      this.columsScrollX = scrollX; // 不包含操作列宽度
      // 行末按钮
      if (this.showRowOperationButtons) {
        this.columns.push({
          title: this.$t('WidgetSubform.column.operation', '操作'),
          dataIndex: '__operation',
          ellipsis: true, //操作默认省略
          align: 'center',
          fixed: enableFreezed && rightFreezeColNum >= 0 ? 'right' : false,
          width: `${this.operationColWidth}px`,
          initWidth: `${this.operationColWidth}px`, //记录初始时列宽度
          scopedSlots: { customRender: '__operationSlot' }
        });
        scrollX += this.operationColWidth;
        if (this.configuration.freezeColumn) {
          fixedWidth += this.operationColWidth; // 右侧操作列宽度
        }
      }
      this.$nextTick(() => {
        this.columnWidthResize();
        this.resetformElement();
      });
    },
    getExportModalContainer() {
      return this.$el;
    },
    onButtonClick(btn, index, record) {
      let btnId = btn.id;
      if (btnId == 'appendRow') {
        btnId = 'addRow';
      }
      if (btn.default && this[btnId] != undefined) {
        if (btn.eventHandler && btn.eventHandler.actionType != undefined) {
          // 执行事件处理，则不执行以下默认逻辑
          return;
        }
        if (btnId === 'addRow' && this.widget.configuration.layout == 'table' && this.widget.configuration.rowEditMode == 'form') {
          // 表格方式展示且行编辑模式为表单编辑情况下，添加行数据是打开弹窗表单编辑
          this.editFormUuid = this.formUuid;
          let title = this.$t(btnId, btn.title);
          if (this.widget.configuration.newFormTitle) {
            title = this.$t('newFormTitle', this.widget.configuration.newFormTitle);
          }
          if (this.widget.configuration.editFormUuid) {
            this.editFormUuid = this.widget.configuration.editFormUuid;
          }
          this.modalTitle = title;
          this.isViewForm = false; // 非查看表单
          this.formModalVisible = true;
          this.currentEditRowKey = undefined;
          this.currentSelectedForm = this.form.createTempDyform(this.editFormUuid, 'new_' + generateId());
          this.currentSelectedForm.modalEditFormNeedConfirm = true;

          this.currentSelectedForm.displayState = this.dyform.displayState; // 从表状态和主表一致
          let rules = {};
          if (this.widget.configuration.hasOwnProperty('enableStateForm')) {
            let stateFormElementRules = this.getStateFormRules('new');
            Object.assign(rules, stateFormElementRules);
            if (this.formElementRules != undefined) {
              for (let id in this.formElementRules) {
                let r = this.formElementRules[id];
                // 可编辑情况下，非隐藏且必填时必须跟列配置一致，且不可修改
                // 规则有加必填条件，该字段必填就有开启
                if ((r.required || r.requiredCondition) && !r.hidden) {
                  if (rules[id]) {
                    rules[id] = deepClone(r);
                  } else {
                    for (let key in rules) {
                      if (rules[key].dataIndex == r.dataIndex) {
                        rules[key] = deepClone(r);
                        break;
                      }
                    }
                  }
                } else if (!rules[id]) {
                  rules[id] = deepClone(r);
                } else if (rules[id].required && this.requiredConditionMap[id]) {
                  // 表单规则，该字段必填，如果从表有必填条件，传入
                  rules[id].requiredCondition = this.requiredConditionMap[id];
                }
              }
            }
          } else {
            if (this.formElementRules != undefined) {
              for (let id in this.formElementRules) {
                let r = this.formElementRules[id];
                if (r.columnRuleApplyToForm == true || r.columnRuleApplyToForm == undefined) {
                  // 列规则：根据配置判断是否传入到表单内
                  rules[id] = deepClone(r);
                  if (r.columnRuleApplyToForm) {
                    // 表格列规则的隐藏不用来控制编辑表单的显隐性（可以实现表格列不展示相关字段，而在编辑表单时候显示）
                    delete rules[id].hidden;
                  }
                }
              }
            }
          }
          this.currentSelectedForm.formElementRules = rules;
          this.currentSelectedFormDatas = {};
          this.currentEditRow = { __MODEL__: {} };
          return;
        }

        if (btnId === 'addRow' && this.widget.configuration.rowEditMode !== 'form') {
          this.tableExpand = true;
        }

        if (btnId == 'addRow') {
          this.addRow();
        } else if (btnId == 'delRow') {
          // 查询从表行数据时候下标非真实rows下标，要按id进行删除行
          if (record == undefined) {
            let deleteKeys = [].concat(this.selectedRowKeys);
            for (let k of deleteKeys) {
              this.delRowById(k);
            }
          } else {
            this.delRowById(record[this.rowKey]);
          }
        } else {
          this[btnId](record, btn, index);
        }
      }
      if (btnId == 'addRow' && this.searchKey == undefined && this.widget.configuration.layout == 'form-tabs') {
        this.subformActiveTabKey = this.rows[this.rows.length - 1][this.rowKey];
      }

      if (btn.id.startsWith('import_')) {
        this.tableExpand = true;
        // 导入
        let importButtonRule = this.widget.configuration.import.importButtonRule[btn.id];
        if (importButtonRule) {
          this.importModalInfo.importFile.fileID = undefined;
          this.importModalInfo.visible = true;
          this.importModalInfo.strict = importButtonRule.strict === true;
          this.importModalInfo.modalTemplate = undefined;
          this.importModalInfo.importService = importButtonRule.importService;
          this.importModalInfo.importFileTemplate.url = importButtonRule.importFileTemplate.url;
          this.importModalInfo.importFileTemplate.name = importButtonRule.importFileTemplate.fileName;
          this.importModalInfo.sheetRules = importButtonRule.sheetConfig;
          this.importModalInfo.importLog = importButtonRule.importLog;
          this.importModalInfo.importCode = importButtonRule.importCode;
          this.importModalInfo.key = generateId();
          if (importButtonRule.importModalType == 'template') {
            this.importModalInfo.modalTemplate = importButtonRule.importModalTemplateName;
          } else {
            if (importButtonRule.modalWidth) {
              this.importModalInfo.width = !isNaN(Number(importButtonRule.modalWidth))
                ? importButtonRule.modalWidth + 'px'
                : importButtonRule.modalWidth;
            }
            if (importButtonRule.modalTitle) {
              this.importModalInfo.title = importButtonRule.modalTitle;
            }
            if (importButtonRule.modalOkText) {
              this.importModalInfo.okText = importButtonRule.modalOkText;
            }
          }
        }
      } else if (btn.id.startsWith('export_')) {
        let exportButtonRule = this.widget.configuration.export.exportButtonRule;
        if (exportButtonRule[btn.id]) {
          let rule = exportButtonRule[btn.id];
          if (this.rows.length == 0) {
            this.$message.info(this.$t('WidgetSubform.message.nodataExport', '暂无数据导出'));
            return;
          }
          if (rule.exportRange == 'rowSelected') {
            if (this.selectedRowKeys.length == 0) {
              this.$message.info(this.$t('WidgetSubform.message.unSelectedExport', '未选择数据进行导出'));
              return;
            }
          }
          rule.exportFileName = this.$t(btn.id + '_fileName', rule.exportFileName);
          if (rule.exportColumnSelectable) {
            this.exportColumnDefModalVisible = true;
            this.userCheckedExportColumn.rule = rule;
            if (this.userCheckedExportColumn.options.length == 0) {
              this.userCheckedExportColumn.checkAll = true;
              for (let i = 0; i < this.widget.configuration.columns.length; i++) {
                let column = this.widget.configuration.columns[i],
                  exportConfig = exportButtonRule[btn.id].exportColumnConfig[column.dataIndex];
                let colIndex = findIndex(this.customColTitleSlotOptions, { dataIndex: column.dataIndex });
                let title = colIndex > -1 ? this.customColTitleSlotOptions[colIndex].title : column.title;
                if (exportConfig.exportable) {
                  this.userCheckedExportColumn.options.push({
                    label: title,
                    value: column.dataIndex
                  });
                  this.userCheckedExportColumn.key.push(column.dataIndex);
                }
                if (exportConfig.subExportColumns) {
                  exportConfig.subExportColumns.forEach(col => {
                    if (col.exportable) {
                      let _colIndex = findIndex(this.customColTitleSlotOptions, { dataIndex: col.dataIndex });
                      let _title = _colIndex > -1 ? this.customColTitleSlotOptions[_colIndex].title : col.title;
                      this.userCheckedExportColumn.key.push(col.dataIndex + '$VALUE');
                      this.userCheckedExportColumn.options.push({
                        label: _title,
                        value: col.dataIndex + '$VALUE'
                      });
                    }
                  });
                }
              }
            }
            return;
          }

          this.exportSubformRowsByRule(rule);
        }
      }
    },
    getStateFormRules(state) {
      let formElementRules = {};
      let ruleKeys = { new: 'editFormElementRules', edit: 'editStateFormElementRules', label: 'labelStateFormElementRules' };
      let ruleKey = ruleKeys[state];
      if (ruleKey) {
        if (this.widget.configuration[ruleKey]) {
          for (let i = 0, len = this.widget.configuration[ruleKey].length; i < len; i++) {
            formElementRules[this.widget.configuration[ruleKey][i].id] = {
              dataIndex: this.widget.configuration[ruleKey][i].code,
              readonly: this.widget.configuration[ruleKey][i].readonly,
              editable: this.widget.configuration[ruleKey][i].editable,
              disable: this.widget.configuration[ruleKey][i].disable,
              hidden: this.widget.configuration[ruleKey][i].hidden,
              displayAsLabel: this.widget.configuration[ruleKey][i].displayAsLabel,
              required: this.widget.configuration[ruleKey][i].required === true
            };
            let children = this.widget.configuration[ruleKey][i].children;
            if (children) {
              let childrenRules = {};
              formElementRules[this.widget.configuration[ruleKey][i].id].children = childrenRules;
              for (let j = 0, jlen = children.length; j < jlen; j++) {
                childrenRules[children[j].id] = {
                  readonly: children[j].readonly,
                  editable: children[j].editable,
                  disable: children[j].disable,
                  hidden: children[j].hidden,
                  displayAsLabel: children[j].displayAsLabel,
                  required: children[j].required === true
                };
              }
            }
          }
        }
      }
      return formElementRules;
    },
    onConfirmExportColumnRow() {
      if (this.userCheckedExportColumn.key.length == 0) {
        this.$message.info(this.$t('WidgetSubform.message.unSelectedColumnExport', '未选择导出列'));
        return;
      }
      this.exportColumnDefModalVisible = false;
      this.exportSubformRowsByRule(this.userCheckedExportColumn.rule);
    },
    exportSubformRowsByRule(rule) {
      let exportData = {
        fileName: rule.exportFileName,
        dataList: [],
        titles: []
      };

      let attachments = [],
        jobSelect = [],
        richText = [];
      for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
        let attachment = this.widget.configuration.columns[i].widget.wtype === 'WidgetFormFileUpload';
        let column = this.widget.configuration.columns[i];
        let exportConfig = rule.exportColumnConfig[column.dataIndex];
        if (exportConfig == undefined) {
          continue;
        }
        let colIndex = findIndex(this.customColTitleSlotOptions, { dataIndex: column.dataIndex });
        let title = colIndex > -1 ? this.customColTitleSlotOptions[colIndex].title : column.title;
        if (
          (rule.exportColumnSelectable === false && exportConfig.exportable) ||
          (rule.exportColumnSelectable && this.userCheckedExportColumn.key.includes(column.dataIndex))
        ) {
          exportData.titles.push({
            title: title,
            code: column.dataIndex,
            dataType: exportConfig.dataType,
            exportAttachment: exportConfig.exportAttachment
          });
        }

        if (exportConfig.subExportColumns.length > 0) {
          exportConfig.subExportColumns.forEach(subCol => {
            if (
              (rule.exportColumnSelectable === false && subCol.exportable) ||
              (rule.exportColumnSelectable && this.userCheckedExportColumn.key.includes(subCol.dataIndex + '$VALUE'))
            ) {
              let _colIndex = findIndex(this.customColTitleSlotOptions, { dataIndex: subCol.dataIndex });
              let _title = _colIndex > -1 ? this.customColTitleSlotOptions[_colIndex].title : title || subCol.title;
              exportData.titles.push({
                title: _title,
                code: subCol.dataIndex + '$VALUE',
                dataType: subCol.dataType,
                realValue: true
              });
            }
          });
        }

        if (attachment) {
          attachments.push(this.widget.configuration.columns[i].dataIndex);
        }
        if (this.widget.configuration.columns[i].widget.wtype == 'WidgetFormJobSelect') {
          jobSelect.push(this.widget.configuration.columns[i].dataIndex);
        }
        if (this.widget.configuration.columns[i].widget.wtype == 'WidgetFormRichTextEditor') {
          richText.push(this.widget.configuration.columns[i].dataIndex);
        }
      }
      let firstRowForm = undefined;
      for (let i = 0, len = this.rows.length; i < len; i++) {
        if (rule.exportRange == 'rowSelected' && !this.selectedRowKeys.includes(this.rows[i][this.rowKey])) {
          continue;
        }
        if (firstRowForm == undefined) {
          firstRowForm = this.rowForms[this.rows[i][this.rowKey]];
        }
        let d = {};
        exportData.titles.forEach(t => {
          let rowForm = this.rowForms[this.rows[i][this.rowKey]];
          if (t.realValue) {
            // 真实值列
            d[t.code] = rowForm.formData[t.code.split('$VALUE')[0]];
          } else if (attachments.includes(t.code)) {
            // 附件
            let values = undefined;
            if (this.rows[i].hasOwnProperty(t.code) && this.rows[i][t.code] != undefined && Array.isArray(this.rows[i][t.code])) {
              values = this.rows[i][t.code];
            } else if (rowForm.formData[t.code] != undefined && Array.isArray(rowForm.formData[t.code])) {
              values = rowForm.formData[t.code];
            }
            if (values != undefined) {
              let files = [];
              values.forEach(f => {
                files.push(f.fileID);
              });
              d[t.code] = files.join(';');
            }
          } else if (jobSelect.includes(t.code)) {
            d[t.code] = rowForm.$fieldset[t.code]
              ? rowForm.$fieldset[t.code].displayValue()
              : firstRowForm.$fieldset[t.code].displayValue(rowForm.formData[t.code]);
          } else if (richText.includes(t.code)) {
            // 富文本导出
            // let div = document.createElement('div');
            // div.innerHTML = rowForm.formData[t.code];
            // d[t.code] = div.innerText;
            d[t.code] = this.regexHtmlToText(rowForm.formData[t.code]);
          } else {
            let label = this.rows[i].__MODEL__.label;
            if (label.hasOwnProperty(t.code) && !isEmpty(this.rows[i][t.code]) && !isEmpty(label[t.code])) {
              d[t.code] = label[t.code];
            } else if (rowForm.$fieldset != undefined && rowForm.$fieldset[t.code]) {
              d[t.code] = rowForm.$fieldset[t.code].displayValue();
            } else if (
              rowForm.$fieldset[t.code] == undefined &&
              firstRowForm.$fieldset[t.code] != undefined &&
              firstRowForm.$fieldset[t.code].displayValue != undefined
            ) {
              // 翻页情况下，未渲染组件，由首行表单组件代替解析出显示值
              d[t.code] = firstRowForm.$fieldset[t.code].displayValue(rowForm.formData[t.code]);
            }
          }
        });
        exportData.dataList.push(d);
      }
      // console.log('计划导出从表数据', exportData);

      download({
        url: '/proxy/file/download/services',
        data: {
          jsonData: JSON.stringify({ serviceName: 'dmsDataExportService', methodName: 'exportRows' }),
          args: JSON.stringify([exportData])
        }
      });
    },
    regexHtmlToText(html) {
      if (html) {
        const text = html.replace(/(<img[^>]*>)|<[^>]+>/g, (match, imgTag) => (imgTag ? '[图片]' : ''));
        return text;
      }
      return '';
    },
    onFormInlineCheckAllChange(e) {
      this.formInlineCheckAll = e.target.checked;
      this.formInlineIndeterminate = false;
      this.selectedRows = [];
      this.selectedRowKeys = [];
      if (this.formInlineCheckAll) {
        for (let i = 0, len = this.rows.length; i < len; i++) {
          this.selectedRowKeys.push(this.rows[i][this.rowKey]);
        }
        this.selectedRows = deepClone(this.rows);
      }
    },
    updateFormInlineCheckAllState() {
      this.$nextTick(() => {
        if (this.widget.configuration.layout != 'table') {
          this.formInlineIndeterminate =
            this.rows.length > 0 && !!this.selectedRowKeys.length && this.selectedRowKeys.length < this.rows.length;
          this.formInlineCheckAll =
            this.rows.length > 0 && !!this.selectedRowKeys.length && this.selectedRowKeys.length === this.rows.length;
        }
      });
    },
    onFormInlineCheck(id, r) {
      let i = this.selectedRowKeys.indexOf(id);
      if (i == -1) {
        //  选中的情况
        if (this.widget.configuration.rowSelectType === 'checkbox') {
          this.selectedRowKeys.push(id);
          this.selectedRows.push(r);
        } else {
          this.selectedRowKeys = [id];
          this.selectedRows = [r];
        }
      } else {
        if (this.widget.configuration.rowSelectType === 'checkbox') {
          this.selectedRowKeys.splice(i, 1);
          this.selectedRows.splice(i, 1);
        } else {
          this.selectedRowKeys = [];
          this.selectedRows = [];
        }
      }

      if (this.widget.configuration.rowSelectType === 'checkbox') {
        this.updateFormInlineCheckAllState();
      }
    },
    onModalEdit(record) {
      let rowFormData = this.rowForms[record[this.rowKey]].formData;
      let modalTitle = this.$t('WidgetSubform.edit', '编辑');
      let _continue = rules => {
        this.isViewForm = false; // 非查看表单
        this.formModalVisible = true;
        this.currentSelectedForm = this.form.createTempDyform(this.formUuid, generateId());
        this.currentSelectedForm.modalEditFormNeedConfirm = true;
        this.currentSelectedForm.dataUuid = rowFormData.uuid;
        this.currentSelectedForm.formData = deepClone(rowFormData);
        this.currentSelectedForm.formElementRules = rules;
        this.currentEditRowKey = record[this.rowKey];
        this.currentEditRow = record;
        this.modalTitle = modalTitle;
      };
      this.editFormUuid = this.formUuid;
      if (this.widget.configuration.layout == 'table' && this.widget.configuration.rowEditMode == 'form') {
        if (this.widget.configuration.editStateTitle) {
          title = this.$t('editStateTitle', this.widget.configuration.editStateTitle);
        }
        if (this.widget.configuration.enableStateForm && this.widget.configuration.editStateFormUuid) {
          this.editFormUuid = this.widget.configuration.editStateFormUuid;
        } else if (this.widget.configuration.editFormUuid) {
          this.editFormUuid = this.widget.configuration.editFormUuid;
        }
        let rules = {};
        if (this.widget.configuration.hasOwnProperty('enableStateForm')) {
          let stateFormElementRules = this.getStateFormRules('edit');
          Object.assign(rules, stateFormElementRules);
          if (this.formElementRules != undefined) {
            for (let id in this.formElementRules) {
              let r = this.formElementRules[id];
              // 可编辑情况下，非隐藏且必填时必须跟列配置一致，且不可修改
              // 规则有加必填条件，该字段必填就有开启
              if ((r.required || r.requiredCondition) && !r.hidden) {
                if (rules[id]) {
                  rules[id] = deepClone(r);
                } else {
                  for (let key in rules) {
                    if (rules[key].dataIndex == r.dataIndex) {
                      rules[key] = deepClone(r);
                      break;
                    }
                  }
                }
              } else if (!rules[id]) {
                rules[id] = deepClone(r);
              } else if (rules[id].required && this.requiredConditionMap[id]) {
                // 表单规则，该字段必填，如果从表有必填条件，传入
                rules[id].requiredCondition = this.requiredConditionMap[id];
              }
            }
          }
        } else if (this.widget.configuration.editFormUuid != undefined) {
          // 兼容旧逻辑，无状态表单配置
          if (this.formElementRules != undefined) {
            for (let id in this.formElementRules) {
              let r = this.formElementRules[id];
              if (r.columnRuleApplyToForm == true || r.columnRuleApplyToForm == undefined) {
                // 列规则：根据配置判断是否传入到表单内
                rules[id] = deepClone(r);
                if (r.columnRuleApplyToForm) {
                  // 表格列规则的隐藏不用来控制编辑表单的显隐性（可以实现表格列不展示相关字段，而在编辑表单时候显示）
                  delete rules[id].hidden;
                }
              }
            }
          }
        } else {
          rules = deepClone(this.formElementRules); // 表单字段规则传入
        }
        this.currentSelectedForm.formElementRules = rules;
        this.currentSelectedForm.dataUuid = record.uuid || undefined;
        if (record.uuid != undefined && record.__MODEL__.dataFetched == undefined) {
          // 表单加载数据时候，只会对从表有配置列的字段进行数据加载
          // 因此从表行以编辑表单形态打开时候，第一次要重新去服务端拉取从表详细数据，补充未加载字段的数据
          this.$set(record.__MODEL__, 'loading', true);
          record.__MODEL__.dataFetched = true;
          this.fetchFormData(this.editFormUuid, record.uuid).then(formDatas => {
            if (formDatas != undefined && formDatas[this.editFormUuid] != undefined && formDatas[this.editFormUuid].length > 0) {
              let _data = formDatas[this.editFormUuid][0];
              let keys = Object.keys(rowFormData);
              for (let k in _data) {
                if (!keys.includes(k)) {
                  this.$set(rowFormData, k, _data[k]);
                }
              }
            }
            this.$set(record.__MODEL__, 'loading', false);
            _continue(rules);
          });
        } else {
          _continue(rules);
        }
      } else {
        // 未指定表单编辑单的情况下，需要将表格列生成的字段规则传入
        _continue(deepClone(this.formElementRules));
      }
    },
    onFormModalCancel() {
      this.currentEditRowKey = undefined;
      this.currentEditRow = undefined;
    },

    onFormModalOk: debounce(
      function () {
        let _this = this,
          editFormUuid = this.editFormUuid || this.widget.configuration.editFormUuid;
        let saveOrUpdate = (formData, $fieldset) => {
          let row = { ...formData, __MODEL__: { label: {} } };
          for (let k in $fieldset) {
            let $v = $fieldset[k];
            // if ($v.setValue) {
            //   $v.setValue(row[k]);
            // }
            row.__MODEL__[k + '_ShowAsLabel'] = true;
            row.__MODEL__.label[k] = $v.displayValue(undefined, _this.allowCellRenderTemplate);
          }
          // 执行数据返回前脚本
          this.pageContext.executeCodeSegment(this.widget.configuration.editModal.scriptAfterOk, { row }, this);
          let rowFormData = deepClone(row);
          delete rowFormData.__MODEL__;
          if (this.currentEditRowKey != undefined) {
            const curRowIndex = this.rows.findIndex(row => {
              return row[this.rowKey] === this.currentEditRowKey;
            });
            // 更新行
            for (let key in row) {
              if (key != '__MODEL__' && key != 'uuid') {
                let cellDisplayValue = row['__MODEL__']['label'][key];
                if (cellDisplayValue) {
                  cellDisplayValue = cellDisplayValue + '';
                }
                this.setRowCellDisplayValue(this.rows[curRowIndex], key, cellDisplayValue);
                this.$set(this.rowKeyMap[this.currentEditRowKey], key, row[key]);
                if (this.rowForms[this.currentEditRowKey].$fieldset[key]) {
                  let $widget = this.rowForms[this.currentEditRowKey].$fieldset[key];
                  if ($widget && $widget.widget && $widget.widget.wtype == 'WidgetFormDatePicker' && $widget.widget.subtype == 'Range') {
                    // 时间区间选择，开始时间setValue前，表单值需先有结束时间
                    this.rowForms[this.currentEditRowKey].formData[$widget.widget.configuration.endDateField] =
                      row[$widget.widget.configuration.endDateField];
                  }
                  const _setValue = this.rowForms[this.currentEditRowKey].$fieldset[key].setValue;
                  if (_setValue) {
                    _setValue(row[key]);
                  }
                  this.rowForms[this.currentEditRowKey].formData[key] = row[key];
                  this.dyform.emitEvent(`${this.widget.id}:${key}:valueChange`, {
                    $subform: this,
                    dataIndex: key,
                    newValue: row[key],
                    rowData: row,
                    rowKey: this.currentEditRowKey
                  });
                } else {
                  if (key == 'nestformDatas') {
                    // console.log('----------- 收集从表的嵌套从表数据: ', row[key]);
                    // 从表的从表，要把子从表实例设值到从表表单上，收集子从表数据时候才会正常收集到
                    _this.rowForms[this.currentEditRowKey].subform = _this.currentSelectedForm.subform;
                    if (_this.rowForms[this.currentEditRowKey].deletedSubformData == undefined) {
                      _this.rowForms[this.currentEditRowKey].deletedSubformData = {};
                    }
                    Object.assign(_this.rowForms[this.currentEditRowKey].deletedSubformData, _this.currentSelectedForm.deletedSubformData);
                  }
                  this.rowForms[this.currentEditRowKey].formData[key] = row[key];
                }
              }
            }
          } else {
            // 创建从表行级表单实例
            let sf = this.currentSelectedForm;
            sf.formElementRules = deepClone(this.formElementRules); // 从表列字段规则
            this.form.pushSubform(this.formUuid, sf);
            sf.formData = rowFormData; // 表单数据赋值
            row[this.rowKey] = sf.namespace;
            this.rows.push(row);
            this.rowForms[row[this.rowKey]] = sf;
            this.rowKeyMap[row[this.rowKey]] = row;
            sf.row = row;

            for (let key in rowFormData) {
              this.dyform.emitEvent(`${this.widget.id}:${key}:valueChange`, {
                $subform: this,
                dataIndex: key,
                newValue: rowFormData[key],
                oldValue: undefined,
                rowData: row,
                rowKey: row[this.rowKey]
              });
            }
          }
          this.formModalVisible = false;
          this.currentSelectedForm = {};
          this.currentEditRow = undefined;
          this.currentEditRowKey = undefined;
        };
        if (this.widget.configuration.editModal.scriptBeforeOk) {
          let ans;
          this.pageContext.executeCodeSegment(this.widget.configuration.editModal.scriptBeforeOk, {}, this, function (rt) {
            ans = rt;
          });
          if (ans === false) {
            // 阻止关闭弹窗，返回false
            return;
          }
        }
        if (this.widget.configuration.rowEditMode === 'form' && editFormUuid) {
          this.$refs.wDyform.collectFormData(true, function (legal, msg, result) {
            if (legal) {
              saveOrUpdate.call(_this, deepClone(result.dyFormData.formDatas[editFormUuid][0]), _this.$refs.wDyform.dyform.$fieldset);
            }
          });
        } else {
          this.$refs.formModal.validate(function (legal, results) {
            if (legal) {
              saveOrUpdate.call(_this, deepClone(_this.currentSelectedForm.formData), _this.currentSelectedForm.$fieldset);
            }
          });
        }
      },
      1000,
      { leading: true, trailing: false }
    ),

    editRow(record, btn, index) {
      let _this = this;
      // 行编辑情况
      if (this.widget.configuration.layout === 'table') {
        /**
         * 表格形态下的编辑按钮
         * 1. 行编辑模式情况下，编辑按钮只是用来切换整行的单元格为组件展示状态
         * 2. 表单编辑模式情况下，会弹窗展示指定的表单来进行数据编辑
         */
        if (this.widget.configuration.rowEditMode == 'cell') {
          // 各单元格切换为组件状态
          let dataIndexes = Object.keys(this.customDataIndexSlotOptionMaps);
          dataIndexes.forEach(dataIndex => {
            _this.$set(record.__MODEL__, dataIndex + '_ComponentInit', true);
            _this.$set(record.__MODEL__, dataIndex + '_ShowAsLabel', false);
          });
        } else if (this.widget.configuration.rowEditMode == 'form') {
          let title = this.$t(btn.id, btn.title);
          if (this.widget.configuration.editStateTitle) {
            title = this.$t('editStateTitle', this.widget.configuration.editStateTitle);
          }
          this.modalTitle = title;
          this.currentEditRowKey = record[this.rowKey];
          let _index = index;
          if (this.tableHasPagination) {
            _index = (this.pagination.current - 1) * this.pagination.pageSize + index;
          }
          let sourceForm = this.getRowForm(_index);
          this.editFormUuid = sourceForm.formUuid;
          if (this.widget.configuration.enableStateForm && this.widget.configuration.editStateFormUuid) {
            this.editFormUuid = this.widget.configuration.editStateFormUuid;
          } else if (this.widget.configuration.editFormUuid) {
            this.editFormUuid = this.widget.configuration.editFormUuid;
          }
          this.currentSelectedForm = this.form.createTempDyform(this.editFormUuid, 'tempDyform_' + generateId());
          let rules = {};
          if (this.widget.configuration.hasOwnProperty('enableStateForm')) {
            let stateFormElementRules = this.getStateFormRules('edit');
            Object.assign(rules, stateFormElementRules);
            if (this.formElementRules != undefined) {
              for (let id in this.formElementRules) {
                let r = this.formElementRules[id];
                // 可编辑情况下，非隐藏且必填时必须跟列配置一致，且不可修改
                // 规则有加必填条件，该字段必填就有开启
                if ((r.required || r.requiredCondition) && !r.hidden) {
                  if (rules[id]) {
                    rules[id] = deepClone(r);
                  } else {
                    for (let key in rules) {
                      if (rules[key].dataIndex == r.dataIndex) {
                        rules[key] = deepClone(r);
                        break;
                      }
                    }
                  }
                } else if (!rules[id]) {
                  rules[id] = deepClone(r);
                } else if (rules[id].required && this.requiredConditionMap[id]) {
                  // 表单规则，该字段必填，如果从表有必填条件，传入
                  rules[id].requiredCondition = this.requiredConditionMap[id];
                }
              }
            }
          } else if (this.widget.configuration.editFormUuid != undefined) {
            // 兼容旧逻辑，无状态表单配置
            if (this.formElementRules != undefined) {
              for (let id in this.formElementRules) {
                let r = this.formElementRules[id];
                if (r.columnRuleApplyToForm == true || r.columnRuleApplyToForm == undefined) {
                  // 列规则：根据配置判断是否传入到表单内
                  rules[id] = deepClone(r);
                  if (r.columnRuleApplyToForm) {
                    // 表格列规则的隐藏不用来控制编辑表单的显隐性（可以实现表格列不展示相关字段，而在编辑表单时候显示）
                    delete rules[id].hidden;
                  }
                }
              }
            }
          } else {
            rules = deepClone(this.formElementRules); // 表单字段规则传入
          }
          let rowFormData = sourceForm.formData;
          let _continue = () => {
            this.isViewForm = false; // 非查看表单
            this.formModalVisible = true;
            let tempData = deepClone(rowFormData);
            if (this.currentSelectedForm.subform) {
              let nestformDatas = this.getSubFormData(sourceForm);
              tempData.nestformDatas = JSON.stringify(nestformDatas);
            }
            this.currentSelectedFormDatas = {
              [this.editFormUuid]: [tempData]
            };
            this.currentEditRow = tempData;
          };
          this.currentSelectedForm.formElementRules = rules;
          this.currentSelectedForm.dataUuid = record.uuid || undefined;
          if (record.uuid != undefined && record.__MODEL__.dataFetched == undefined) {
            // 表单加载数据时候，只会对从表有配置列的字段进行数据加载
            // 因此从表行以编辑表单形态打开时候，第一次要重新去服务端拉取从表详细数据，补充未加载字段的数据
            this.$loading();
            record.__MODEL__.dataFetched = true;
            this.fetchFormData(this.editFormUuid, record.uuid).then(formDatas => {
              if (formDatas != undefined && formDatas[this.editFormUuid] != undefined && formDatas[this.editFormUuid].length > 0) {
                let _data = formDatas[this.editFormUuid][0];
                let keys = Object.keys(rowFormData);
                for (let k in _data) {
                  if (!keys.includes(k)) {
                    this.$set(rowFormData, k, _data[k]);
                  }
                }
              }
              this.$loading(false);
              _continue();
            });
          } else {
            _continue();
          }
        }
      }
    },
    // 查看行
    viewRow(record, btn, index) {
      if (this.widget.configuration.layout === 'table' && this.widget.configuration.rowEditMode == 'form') {
        let title = this.$t(btn.id, btn.title);
        if (this.widget.configuration.labelStateTitle) {
          title = this.$t('labelStateTitle', this.widget.configuration.labelStateTitle);
        }
        this.modalTitle = title;
        this.currentEditRowKey = record[this.rowKey]; // 当前行的key
        let _index = index;
        if (this.tableHasPagination) {
          _index = (this.pagination.current - 1) * this.pagination.pageSize + index;
        }
        let sourceForm = this.getRowForm(_index);
        this.editFormUuid = sourceForm.formUuid;
        if (this.widget.configuration.enableStateForm && this.widget.configuration.labelStateFormUuid) {
          this.editFormUuid = this.widget.configuration.labelStateFormUuid;
        } else if (this.widget.configuration.editFormUuid) {
          this.editFormUuid = this.widget.configuration.editFormUuid;
        }
        this.currentSelectedForm = this.form.createTempDyform(this.editFormUuid, 'tempDyform_' + generateId());
        this.currentSelectedForm.displayState = 'label'; // 查看状态

        let rules = {};
        if (this.widget.configuration.hasOwnProperty('enableStateForm')) {
          let stateFormElementRules = this.getStateFormRules('label');
          Object.assign(rules, stateFormElementRules);
          if (this.formElementRules != undefined) {
            for (let id in this.formElementRules) {
              let r = this.formElementRules[id];
              if (!rules[id]) {
                rules[id] = deepClone(r);
              }
            }
          }
        } else if (this.widget.configuration.editFormUuid != undefined) {
          if (this.formElementRules != undefined) {
            for (let id in this.formElementRules) {
              let r = this.formElementRules[id];
              if (r.columnRuleApplyToForm == true || r.columnRuleApplyToForm == undefined) {
                // 列规则：根据配置判断是否传入到表单内
                rules[id] = deepClone(r);
                if (r.columnRuleApplyToForm) {
                  // 表格列规则的隐藏不用来控制编辑表单的显隐性（可以实现表格列不展示相关字段，而在编辑表单时候显示）
                  delete rules[id].hidden;
                }
              }
            }
          }
        } else {
          rules = deepClone(this.formElementRules); // 表单字段规则传入
        }
        each(rules, (rule, index) => {
          if (rule.editable) {
            rule.editable = false;
            rule.displayAsLabel = true;
          }
        });
        this.currentSelectedForm.formElementRules = rules;

        let rowFormData = sourceForm.formData;
        let _continue = () => {
          this.isViewForm = true; // 查看表单
          this.formModalVisible = true;
          let tempData = deepClone(rowFormData);
          if (this.currentSelectedForm.subform) {
            let nestformDatas = this.getSubFormData(sourceForm);
            tempData.nestformDatas = JSON.stringify(nestformDatas);
          }
          this.currentSelectedFormDatas = {
            [this.editFormUuid]: [tempData]
          };
          this.currentEditRow = tempData;
        };
        this.currentSelectedForm.dataUuid = record.uuid || undefined;
        if (record.uuid != undefined && record.__MODEL__.dataFetched == undefined) {
          // 表单加载数据时候，只会对从表有配置列的字段进行数据加载
          // 因此从表行以编辑表单形态打开时候，第一次要重新去服务端拉取从表详细数据，补充未加载字段的数据
          this.$loading();
          record.__MODEL__.dataFetched = true;
          this.fetchFormData(this.editFormUuid || this.formUuid, record.uuid).then(formDatas => {
            if (formDatas != undefined && formDatas[this.editFormUuid] != undefined && formDatas[this.editFormUuid].length > 0) {
              let _data = formDatas[this.editFormUuid][0];
              let keys = Object.keys(rowFormData);
              for (let k in _data) {
                if (!keys.includes(k)) {
                  this.$set(rowFormData, k, _data[k]);
                }
              }
            }
            this.$loading(false);
            _continue();
          });
        } else {
          _continue();
        }
      }
    },
    // 获取从表里面的从表数据
    getSubFormData(form) {
      let dyFormData = {};
      if (form.subform) {
        // 从表嵌套从表的情况下
        dyFormData = {
          formUuid: form.formUuid,
          formDatas: {},
          deletedFormDatas: {},
          addedFormDatas: {},
          updatedFormDatas: {}
        };
        each(form.subform, (item, index) => {
          dyFormData.formDatas[index] = map(item, (citem, cindex) => {
            return citem.formData;
          });
        });
      }
      return dyFormData;
    },
    getRowForm(index) {
      if (index != undefined) {
        return this.rowForms[this.rows[index][this.rowKey]];
      }
      return {};
    },

    addRow(data = {}, afterIndex, rows = this.rows, subform, arg = {}) {
      // 创建从表行级表单实例
      let sf = subform == undefined ? this.form.createSubform(this.formUuid, 'new_' + generateId()) : subform;
      sf.formId = this.widget.configuration.formId;
      sf.tableName = this.widget.configuration.tableName; // 表名
      sf.displayState = this.dyform.displayState; // 从表状态和主表一致
      sf.formElementRules = deepClone(this.formElementRules); // 表单字段规则传入
      let _data = deepClone(data);
      _data = { ...this.subformDefaultData, ...data };
      sf.formData = { ..._data }; // 表单数据赋值
      if (_data.nestformDatas) {
        let formDatas = typeof _data.nestformDatas == 'string' ? JSON.parse(_data.nestformDatas).formDatas : _data.nestformDatas.formDatas;
        if (formDatas) {
          for (let subformUuid in formDatas) {
            for (let r of formDatas[subformUuid]) {
              let sb = sf.createSubform(subformUuid);
              if (arg.isCopy && r.uuid) {
                // 复制从表内从表数据是，需要清除对应的uuid值
                delete r.uuid;
              }
              sb.formData = r;
            }
          }
        }
      }
      if (!arg.isCopy) {
        sf.dataUuid = _data.uuid;
      }
      sf.subformLazyLoaded = false;
      let row = { ..._data, __MODEL__: { label: {} }, [this.rowKey]: sf.namespace };
      if (data && data.__MODEL__) {
        row.__MODEL__ = deepClone(data.__MODEL__);
      } else {
        for (let k in data) {
          // 显示值设置：如果是真实值/显示值组件，需要等待获取实际数据
          row.__MODEL__.label[k] = this.colIsLabelValWidget[k] ? '' : data[k];
        }
      }

      if (afterIndex === undefined) {
        rows.push(row);
      } else {
        rows.splice(afterIndex + 1, 0, row);
        this.form.swapSubform(this.formUuid, rows.length - 1, afterIndex + 1);
      }
      for (let index = 0; index < this.customRenderSlotOptions.length; index++) {
        const col = this.customRenderSlotOptions[index];
        this.$set(
          row['__MODEL__'],
          `${col.dataIndex}_ShowAsLabel`,
          // 列不可编辑 ，或者表格形态下以表单编辑行数据的情况下，列初始以文本形态展示
          (this.widget.configuration.layout == 'table' && this.widget.configuration.rowEditMode == 'form') || !col.editable
        );
        this.$set(row['__MODEL__'], `${col.dataIndex}_Required`, col.required === true);
      }
      this.rowForms[sf.namespace] = sf;
      this.rowKeyMap[sf.namespace] = row;
      sf.row = row;
      this.autoGroupRows();

      this.$nextTick(() => {
        this.columnWidthResize();
        if (this.enableColumnFreeze) {
          this.addRowHeightDetector(row);
        }
        if (arg && typeof arg.callback == 'function') {
          arg.callback();
        }
      });
      return row;
      // this.$nextTick(() => {
      //   // this.rowComponentRenderQueue.push(sf.namespace);
      //   console.log('render ...');
      // });
    },

    // 删除行
    delRow(record, btn, index) {
      if (index == undefined) {
        let getUnDeleteDefaultRow = [];
        for (let i = 0; i < this.rows.length; i++) {
          let j = this.selectedRowKeys.indexOf(this.rows[i][this.rowKey]);
          if (j != -1) {
            if (this.rows[i].status == '2') {
              getUnDeleteDefaultRow.push(i + 1);
              continue;
            }
            this.selectedRowKeys.splice(j, 1);
            this.form.deleteSubform(this.formUuid, i);
            delete this.rowForms[this.rows[i][this.rowKey]];
            delete this.rowKeyMap[this.rows[i][this.rowKey]];
            this.rows.splice(i--, 1);
            if (!this.selectedRowKeys.includes(this.subformActiveTabKey)) {
              if (j > 0) {
                this.subformActiveTabKey = this.rows[this.rows.length > j ? j : j - 1][this.rowKey];
              } else if (j == 0 && this.rows.length > 0) {
                this.subformActiveTabKey = this.rows[0][this.rowKey];
              } else {
                this.subformActiveTabKey = undefined;
              }
            }
          }
        }
        if (getUnDeleteDefaultRow.length > 0) {
          this.$message.info(
            this.$t(
              'WidgetSubform.message.unDeleteDefaultRow',
              { indexs: getUnDeleteDefaultRow.join('、') },
              `第${getUnDeleteDefaultRow.join('、')}项数据为不可删除的默认数据`
            )
          );
        }
      } else {
        if (this.rows[index].status == '2') {
          this.$message.info(
            this.$t('WidgetSubform.message.unDeleteDefaultRow', { indexs: index + 1 }, `第${index + 1}项数据为不可删除的默认数据`)
          );
          return;
        }
        this.form.deleteSubform(this.formUuid, index);
        this.selectedRowKeys.splice(this.selectedRowKeys.indexOf(this.rows[index][this.rowKey]), 1);
        delete this.rowForms[this.rows[index][this.rowKey]];
        delete this.rowKeyMap[this.rows[index][this.rowKey]];
        this.rows.splice(index, 1);

        if (!this.selectedRowKeys.includes(this.subformActiveTabKey)) {
          if (index > 0) {
            this.subformActiveTabKey = this.rows[this.rows.length > index ? index : index - 1][this.rowKey];
          } else if (index == 0 && this.rows.length > 0) {
            this.subformActiveTabKey = this.rows[0][this.rowKey];
          } else {
            this.subformActiveTabKey = undefined;
          }
        }
      }
      // 从表有分页，删除后要判断分页的当前页是否存在
      if (this.tableHasPagination) {
        this.$nextTick(() => {
          const totalPage = this.pagination.total
            ? this.pagination.total % this.pagination.pageSize == 0
              ? parseInt(this.pagination.total / this.pagination.pageSize)
              : parseInt(this.pagination.total / this.pagination.pageSize + 1)
            : 1;
          if (this.pagination.current > totalPage) {
            this.pagination.current = totalPage;
          }
        });
      }
    },
    // 复制行
    copyRow(record) {
      let copyPromises = [];
      /**
       *
       * @param copyData 待获取从表数据的主表或从表
       * @param formUuid 主表或从表的表单uuid
       * @param subformData 标签页使用原表单时，从表数据值
       */
      let getData = (copyData, formUuid, subformData) => {
        return new Promise((resolve, reject) => {
          if (this.widget.configuration.layout == 'form-tabs' && subformData) {
            copyData.uuid = null;
            if (formUuid == this.formUuid) {
              copyData.__MODEL__ = deepClone(record.__MODEL__);
            }
            let formData = {
              formUuid: formUuid,
              subform: {}
            };
            let subPromises = [];
            each(subformData, (item, index) => {
              each(item, citem => {
                subPromises.push(getData(citem.formData, index));
              });
            });
            if (subPromises.length) {
              Promise.all(subPromises).then(res => {
                for (let i = 0, len = res.length; i < len; i++) {
                  if (!formData.subform[res[i].formUuid]) {
                    formData.subform[res[i].formUuid] = [];
                  }
                  if (res[i].copyData) {
                    res[i].copyData.uuid = null;
                    formData.subform[res[i].formUuid].push({
                      formData: res[i].copyData
                    });
                  }
                }
                let nestformDatas = this.getSubFormData(formData);
                copyData.nestformDatas = JSON.stringify(nestformDatas);
                copyData.uuid = null;
                if (formUuid == this.formUuid) {
                  copyData.__MODEL__ = deepClone(record.__MODEL__);
                }
                resolve({ copyData, formUuid });
              });
            } else {
              copyData.nestformDatas = JSON.stringify({ formDatas: {} });
              resolve({ copyData, formUuid });
            }
          } else if (copyData.uuid && copyData.uuid.indexOf('new_') == -1 && !copyData.nestformDatas) {
            // 不是新数据，没有nestformDatas从表数据，通过接口获取改行表单数据
            $axios
              .post('/json/data/services', {
                serviceName: 'dyFormFacade',
                methodName: 'getFormData',
                args: JSON.stringify([formUuid, copyData.uuid])
              })
              .then(({ data }) => {
                if (data.code == 0 && data.data) {
                  Object.assign(copyData, data.data[formUuid][0]);
                  delete data.data[formUuid]; // 移除主表数据
                  let formData = {
                    formUuid: formUuid,
                    subform: {}
                  };
                  let subPromises = [];
                  each(data.data, (item, index) => {
                    each(item, citem => {
                      subPromises.push(getData(citem, index));
                    });
                  });
                  if (subPromises.length) {
                    Promise.all(subPromises).then(res => {
                      for (let i = 0, len = res.length; i < len; i++) {
                        if (!formData.subform[res[i].formUuid]) {
                          formData.subform[res[i].formUuid] = [];
                        }
                        if (res[i].copyData) {
                          res[i].copyData.uuid = null;
                          formData.subform[res[i].formUuid].push({
                            formData: res[i].copyData
                          });
                        }
                      }
                      let nestformDatas = this.getSubFormData(formData);
                      copyData.nestformDatas = JSON.stringify(nestformDatas);
                      copyData.uuid = null;
                      if (formUuid == this.formUuid) {
                        copyData.__MODEL__ = deepClone(record.__MODEL__);
                      }
                      resolve({ copyData, formUuid });
                    });
                  } else {
                    resolve({ copyData, formUuid });
                  }
                }
              });
          } else {
            copyData.uuid = null;
            if (formUuid == this.formUuid) {
              copyData.__MODEL__ = deepClone(record.__MODEL__);
            }
            if (copyData.nestformDatas) {
              let nestformDatas = typeof copyData.nestformDatas == 'string' ? JSON.parse(copyData.nestformDatas) : copyData.nestformDatas;
              let formDatas = nestformDatas.formDatas;
              if (formDatas) {
                let subPromises = [];
                for (let subformUuid in formDatas) {
                  for (let r of formDatas[subformUuid]) {
                    subPromises.push(getData(r, subformUuid));
                  }
                }
                if (subPromises.length) {
                  Promise.all(subPromises).then(res => {
                    formDatas = {};
                    for (let i = 0, len = res.length; i < len; i++) {
                      if (!formDatas[res[i].formUuid]) {
                        formDatas[res[i].formUuid] = [];
                      }
                      if (res[i].copyData) {
                        res[i].copyData.uuid = null;
                        formDatas[res[i].formUuid].push(res[i].copyData);
                      }
                    }
                    nestformDatas.formDatas = formDatas;
                    copyData.nestformDatas = JSON.stringify(nestformDatas);
                    resolve({ copyData, formUuid });
                  });
                } else {
                  resolve({ copyData, formUuid });
                }
              } else {
                resolve({ copyData, formUuid });
              }
            } else {
              resolve({ copyData, formUuid });
            }
          }
        });
      };
      if (record != undefined) {
        let c = deepClone(this.rowForms[record[this.rowKey]].formData);
        copyPromises.push(getData(c, this.formUuid, this.rowForms[record[this.rowKey]].subform));
      } else {
        for (let i = 0; i < this.rows.length; i++) {
          if (this.selectedRowKeys.indexOf(this.rows[i][this.rowKey]) != -1) {
            let c = deepClone(this.rowForms[this.rows[i][this.rowKey]].formData);
            copyPromises.push(getData(c, this.formUuid, this.rowForms[record[this.rowKey]].subform));
          }
        }
      }
      if (copyPromises.length) {
        Promise.all(copyPromises).then(res => {
          for (let i = 0, len = res.length; i < len; i++) {
            this.addRow(res[i].copyData, undefined, undefined, undefined, { isCopy: true });
          }
        });
      }
    },

    insertRow(record, btn, index) {
      // 获取父类
      let targetRows = this.rows,
        data = {};
      if (record.__MODEL__.pid != undefined) {
        targetRows = this.rowKeyMap[record.__MODEL__.pid][this.rowChildKey];
        let tableGroupField = this.widget.configuration.tableGroupField;
        for (let i = 0, len = targetRows.length; i < len; i++) {
          if (targetRows[i][this.rowKey] == record[this.rowKey]) {
            index = i;
            data = {
              [tableGroupField]: record[tableGroupField],
              __MODEL__: { label: { [tableGroupField]: record.__MODEL__.label[tableGroupField] } }
            };
            break;
          }
        }
      }
      this.addRow(data, index, targetRows);
    },

    // 上移
    moveUpRow(record, btn, index) {
      if (this.widget.configuration.layout == 'form-tabs' && this.subformActiveTabKey != undefined) {
        for (let i = 0, len = this.rows.length; i < len; i++) {
          if (this.rows[i][this.rowKey] == this.subformActiveTabKey) {
            index = i;
            break;
          }
        }
      }
      this.swapRows('forward', index);
    },
    // 下移
    moveDownRow(record, btn, index) {
      if (this.widget.configuration.layout == 'form-tabs' && this.subformActiveTabKey != undefined) {
        for (let i = 0, len = this.rows.length; i < len; i++) {
          if (this.rows[i][this.rowKey] == this.subformActiveTabKey) {
            index = i;
            break;
          }
        }
      }
      this.swapRows(undefined, index);
    },

    getRowData() {
      let rowData = [];
      this.form.subform[this.formUuid];
      for (let i = 0, len = this.form.subform[this.formUuid]; i < len; i++) {
        rowData.push(this.form.subform[this.formUuid].formData);
      }
      return rowData;
    },

    swapRows(direction, index) {
      let ids = [],
        _this = this;
      if (index == undefined) {
        for (let i = 0, len = this.selectedRows.length; i < len; i++) {
          ids.push(this.selectedRows[i][this.rowKey]);
        }
      } else {
        ids.push(this.rows[index][this.rowKey]);
      }
      swapArrayElements(
        ids,
        this.rows,
        function (a, b) {
          return a == b[_this.rowKey];
        },
        direction,
        function (from, to) {
          // 交换form顺序，sort_order会在收集从表行数据时候重新计算
          _this.form.swapSubform(_this.formUuid, from, to);
        }
      );
    },
    // 列排序
    sorter(rowa, rowb, col) {
      let sortable = col.sortable,
        v1 = rowa[col.dataIndex],
        v2 = rowb[col.dataIndex];
      this.currentEditRowCell = null;
      let formData = this.rowForms[rowa[this.rowKey]]
        ? [this.rowForms[rowa[this.rowKey]].formData, this.rowForms[rowb[this.rowKey]].formData]
        : [];
      if (sortable.alogrithmType === 'orderByNumber') {
        return Number(v1) - Number(v2);
      } else if (sortable.alogrithmType === 'orderByDate' && sortable.datePattern) {
        let v1Date = moment(v1, sortable.datePattern),
          v2Date = moment(v2, sortable.datePattern);
        return v1Date.isAfter(v2Date) ? 1 : v1Date.isSame(v2Date) ? 0 : -1;
      } else if (sortable.alogrithmType === 'orderByPinYin') {
        // rowa 拼音在  rowb之前，返回负数，否则正数，相等则为0
        if (v1) {
          return v1.localeCompare(v2, 'pinyin');
        }
      } else if (sortable.alogrithmType === 'orderByChar') {
        if (v1) {
          return v1.localeCompare(v2);
        }
      } else if (sortable.alogrithmType === 'orderByDefine' && sortable.script) {
        let ans = 0;
        // 执行自定义代码比较
        this.pageContext.executeCodeSegment(
          sortable.script,
          { value: [v1, v2], row: [rowa, rowb], dataIndex: col.dataIndex, formData },
          this,
          function (rt) {
            ans = rt;
          }
        );
        return ans;
      }
      return 0;
    },
    sortRows(data) {
      let _this = this;
      this.$loading();
      let type = this.widget.configuration.defaultSort.type;
      let sortCol = [];
      if (type == 'columns') {
        // 按字段排序
        this.widget.configuration.defaultSort.columns.forEach(scol => {
          let col = this.widget.configuration.columns.find(c => c.dataIndex == scol.dataIndex);
          if (col) {
            col.sortType = scol.sortType || 'asc';
          }
          sortCol.push(col);
        });
        sortCol.reverse();
        for (let i = 0; i < sortCol.length; i++) {
          data.sort((item1, item2) => {
            let compare = 0;
            const _compare = _this.sorter(item1, item2, sortCol[i]);
            if (_compare !== 0) {
              compare = sortCol[i].sortType == 'asc' ? _compare : -_compare;
            }
            return compare;
          });
        }
      } else if (type == 'script' && widget.configuration.defaultSort.script) {
        data.sort((item1, item2) => {
          let compare = 0;
          // 执行自定义代码比较
          this.pageContext.executeCodeSegment(sortable.script, { row: [item1, item2] }, this, function (rt) {
            compare = rt;
          });
          return compare;
        });
      }
      this.$loading(false);
      return data;
    },

    selectRowChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys;
      this.selectedRows = selectedRows;
    },

    showRowCellAsLabel(key, dataIndex) {
      let required = this.customDataIndexSlotOptionMaps[dataIndex].required;
      if (this.rowKeyMap[key]) {
        let val = this.rowKeyMap[key][dataIndex];
        if (required && (val == null || val == undefined || val == '' || val.length == 0)) {
          return;
        }
        this.$set(this.rowKeyMap[key].__MODEL__, `${dataIndex}_ShowAsLabel`, true); // 用于控制展示从表的文本状态
      }
    },
    onCellComponentMounted(data, record, dataIndex) {
      if (
        record.__MODEL__[`${dataIndex}_ShowAsLabel`] === false &&
        this.widget.configuration.rowEditMode == 'cell' &&
        this.currentEditRowCell == `${record[this.rowKey]}:${dataIndex}`
      ) {
        data.$vue.focus();
      }
    },

    onCellComponentChange(data, record, dataIndex) {
      if (dataIndex === this.widget.configuration.tableGroupField) {
        this.autoGroupRows();
      }
      if (this.enableColumnFreeze) {
        this.$nextTick(() => {
          this.setFixedTrHeightByKey(record, dataIndex);
        });
      }
    },

    customRow(row, index) {
      return {
        on: {
          // 事件
          click: event => {}
        }
      };
    },

    customHeaderCell(col) {
      return {
        style: {
          textAlign: col.titleAlign
        }
      };
    },

    customCell(record, rowIndex, col) {
      let _this = this;
      return {
        style: {
          textAlign: col.contentAlign
        },
        on: {
          // keyup: event => {
          // },
          click: event => {
            if (record.__MODEL__.isGroupRow) {
              // 分组行
              return;
            }
            if (this.displayAsLabel) {
              return;
            }
            let dataIndex = col.dataIndex;
            _this.$set(record.__MODEL__, `${dataIndex}_ComponentInit`, true);
            this.currentEditRowKey = record[_this.rowKey];
            let temp = `${record[_this.rowKey]}:${dataIndex}`;
            if (temp == _this.currentEditRowCell) {
              // 点击了和上次相同的单元格
              // currentEditRowCell：上次点击的单元格、 一个单元格有行和列组成
              return;
            }
            //  单元格默认状态为组件，则不做设置为文本处理
            if (_this.currentEditRowCell && this.configuration.cellDefaultDisplayState !== 'widget') {
              // 修改失焦的单元格
              let split = _this.currentEditRowCell.split(':');
              //FIXME：根据行数据唯一值修改行单元格状态
              _this.showRowCellAsLabel(split[0], split[1]);
            }
            _this.currentEditRowCell = temp;
            if (
              _this.customDataIndexSlotOptionMaps[dataIndex].relaFields.length &&
              !record.__MODEL__.hasOwnProperty(`${_this.customDataIndexSlotOptionMaps[dataIndex].relaFields[0]}_ComponentInit`)
            ) {
              // 关联字段的控件也进行初始化
              for (let i = 0, len = _this.customDataIndexSlotOptionMaps[dataIndex].relaFields.length; i < len; i++) {
                _this.$set(record.__MODEL__, `${_this.customDataIndexSlotOptionMaps[dataIndex].relaFields[i]}_ShowAsLabel`, true);
                _this.$set(record.__MODEL__, `${_this.customDataIndexSlotOptionMaps[dataIndex].relaFields[i]}_ComponentInit`, true);
              }
            }
            // 当前组件不可编辑时，不做转化
            if (_this.widget.configuration.rowEditMode == 'cell' && col.defaultDisplayState != 'unedit') {
              // _this.showRowCellAsLabel(rowIndex, dataIndex);
              _this.$set(record.__MODEL__, `${dataIndex}_ShowAsLabel`, false);
            }
            // if (this.enableColumnFreeze) {
            //   this.$nextTick(() => {
            //     this.setFixedTrHeightByKey(record);
            //   });
            // }
          }
        }
      };
    },
    // 有冻结列，在添加行时，对该行大小变化做监听，同步修改主表高度
    addRowHeightDetector(row) {
      let _this = this;
      _this.setFixedTrHeightByKey();
    },
    // 错过监听，再次触发主表高度变化
    setFixedTrHeightByKey(record, dataIndex) {
      let tableFixedLeft = this.$el.querySelector('.ant-table-fixed-left'),
        tableFixedRight = this.$el.querySelector('.ant-table-fixed-right');
      if (tableFixedRight || tableFixedLeft) {
        let scrollTable = this.$el.querySelector('.ant-table');
        if (scrollTable && scrollTable.__vue__ && scrollTable.__vue__._provided.table) {
          scrollTable.__vue__._provided.table.syncFixedTableRowHeight();
        }
      }
    },
    buttonMounted(operationELement) {
      this.$nextTick(() => {
        if (operationELement != null && operationELement.nodeName != '#comment') {
          let realOperationColWidth = operationELement.clientWidth;
          if (operationELement != null && realOperationColWidth + 32 > this.operationColWidth) {
            // operationELement.style.width = realOperationColWidth + 'px';
            this.columns[this.columns.length - 1]['width'] = `${realOperationColWidth + 32}px`;
            this.operationColWidth = realOperationColWidth;
          } else if (operationELement != null) {
            operationELement.style.width = '';
          }
        }
      });
    },

    setRowCellDisplayValue(record, dataIndex, displayValue) {
      if (displayValue != undefined && displayValue != null) {
        this.$set(record.__MODEL__.label, dataIndex, displayValue); // 设置显示值
      }
    },
    afterDisplayStateChanged(oldState, newState) {
      if (this.widget.configuration.layout == 'table' && oldState.editable !== newState.editable) {
        this.clearTableColumns();
        // 编辑状态变更，重新计算列
        this.setColumns();
      }
    },

    onTableChange(page, filters, sorter, { currentDataSource }) {
      // 当开启页面不超过1的隐藏选项时候，变更分页时候，不隐藏分页选项，不然下次就变不回来了
      if (Object.keys(page).length) {
        if (
          this.defaultHideOnSinglePage &&
          page.pageSize != this.pagination.pageSize &&
          this.pagination.showQuickJumper &&
          page.current === 1 &&
          this.pagination.total <= page.pageSize
        ) {
          this.pagination.hideOnSinglePage = false;
        }
        this.pagination.current = page.current;
        this.pagination.pageSize = page.pageSize;
      }
      setTimeout(() => {
        psScrollResize(this);
      }, 500);
    },
    onTableExpand() {
      if (!this.designMode) {
        this.tableExpand = !this.tableExpand;
        if (this.rows.length > 0) {
          let $rowEndOperationNodes = this.$el.querySelectorAll('.table-row-end-operation');
          if ($rowEndOperationNodes.length > 0) {
            $rowEndOperationNodes.forEach(n => {
              this.buttonMounted(n.firstChild);
            });
          }
        }

        this.$nextTick(() => {
          this.columnWidthResize();
        });
      }
    },
    adjustScrollX() {
      let wrapperWidth, cententWidth;
      if (this.$el && this.$el.parentNode) {
        if (this.widget.configuration.layout === 'table' && this.$el.querySelector) {
          wrapperWidth = this.$el.parentNode.offsetWidth;
          cententWidth = this.$el.querySelector('.ant-table-body .ant-table-tbody').clientWidth;
          if (cententWidth > wrapperWidth) {
            this.modifyScrollPosition = true;
          } else {
            this.modifyScrollPosition = false;
          }
        }
      }
    },
    autoGroupRows() {
      if (!this.customRenderSlotOptions[0]) {
        return;
      }
      // 自动分组数据
      let tableGroupField = this.widget.configuration.tableGroupField,
        showGroupNameDataIndex = this.customRenderSlotOptions[0].dataIndex,
        groupResetSerialNumber = this.widget.configuration.groupResetSerialNumber,
        pageResetSerialNumber = this.widget.configuration.pageResetSerialNumber,
        i = 0;
      if (this.widget.configuration.layout === 'table' && this.widget.configuration.enableTableGroup && tableGroupField) {
        let groupRowMap = {},
          ungroupRows = [];

        // 重新分组
        let _rows = [];
        for (let i = 0, len = this.rows.length; i < len; i++) {
          _rows = _rows.concat(this.rows[i].__MODEL__.isGroupRow ? this.rows[i][this.rowChildKey] : [this.rows[i]]);
        }
        let index = 0,
          seq = !pageResetSerialNumber ? 0 : (this.pagination.current - 1) * this.pagination.pageSize;

        for (let i = 0, len = _rows.length; i < len; i++) {
          let r = _rows[i],
            groupName = r.__MODEL__.label[tableGroupField];

          if (groupName === undefined || groupName.trim() === '') {
            ungroupRows.push(r);
            continue;
          }
          if (groupRowMap[groupName] === undefined) {
            let groupRow = {
              [this.rowKey]: 'tr_group_' + ++index,
              __MODEL__: { label: { [showGroupNameDataIndex]: groupName }, isGroupRow: true, groupName, seq: '' },
              [this.rowChildKey]: [r]
            };
            r.__MODEL__.pid = groupRow[this.rowKey]; // 设置父级的行ID，以便行按钮操作数据
            this.rowKeyMap[r.__MODEL__.pid] = groupRow;
            groupRowMap[groupName] = groupRow;
          } else {
            this.rowKeyMap[r[this.rowKey]] = r;
            r.__MODEL__.pid = groupRowMap[groupName][this.rowKey]; // 设置父级的行ID，以便行按钮操作数据

            groupRowMap[groupName][this.rowChildKey].push(r);
          }
          // 分组序号重置，自动递增序号值
          if (!groupResetSerialNumber) {
            r.__MODEL__.seq = ++seq;
          }

          if (this.currentEditRowKey === r[this.rowKey]) {
            this.expandedRowKeys.push(r.__MODEL__.pid);
          }
        }
        this.rows = Object.values(groupRowMap);
        this.rows = this.rows.concat(ungroupRows);
        this.expandedRowKeys = Array.from(new Set(this.expandedRowKeys));
      }
    },

    onSubformWidgetFormMounted() {
      // 加载从表的从表
      let _this = this;
      if (this.currentSelectedForm.hasSubform()) {
        let nestformDatas = this.currentSelectedForm.formData.nestformDatas;
        if (nestformDatas) {
          nestformDatas = typeof nestformDatas == 'string' ? JSON.parse(nestformDatas) : nestformDatas;
          let addedFormDatas = nestformDatas.addedFormDatas;
          for (let k in nestformDatas.formDatas) {
            if (nestformDatas.formDatas[k] && nestformDatas.formDatas[k].length > 0) {
              _this.currentSelectedFormDatas[k] = nestformDatas.formDatas[k];
              if (addedFormDatas && addedFormDatas[k] && addedFormDatas[k].length > 0) {
                _this.currentSelectedFormDatas[k].forEach(d => {
                  if (addedFormDatas[k].includes(d.uuid)) {
                    delete d.uuid;
                  }
                });
              }
            }
          }
          _this.$refs.wDyform.setFormData(_this.currentSelectedFormDatas, false);
        } else if (this.currentSelectedForm.formData.uuid) {
          $axios
            .post('/json/data/services', {
              serviceName: 'dyFormFacade',
              methodName:
                _this.rootWidgetDyformContext && _this.rootWidgetDyformContext.isVersionDataView ? 'getVersionFormData' : 'getFormData',
              args: JSON.stringify([this.widget.configuration.editFormUuid, this.currentSelectedForm.formData.uuid])
            })
            .then(({ data }) => {
              // console.log('=========> 懒加载从表的从表数据', data);
              if (data.code == 0 && data.data) {
                delete data.data[_this.widget.configuration.editFormUuid];
                for (let k in data.data) {
                  _this.currentSelectedFormDatas[k] = data.data[k];
                  _this.pageContext.emitEvent('WidgetDyform:originFormDataChange', k, data.data[k]);
                }
                _this.$refs.wDyform.setFormData(_this.currentSelectedFormDatas);
              }
            });
        }
      }
    },
    // 计算列宽
    columnWidthResize() {
      if (this.columns && this.columns.length && !this.designMode) {
        if (this.$refs.table) {
          if (this.widget.configuration.enableColumnFreeze) {
            let tableWidth = this.$el.querySelector('.ant-table-wrapper').getBoundingClientRect().width;
            // 非固定区的自适应列
            let autoWidthCols = this.columns.filter(col => !col.fixed && !col.width && !col.hasOwnProperty('isColumns'));
            let groupColumnsAutoWidthWidthCols = []; //this.inGroupColumns.filter(col => !col.width);
            let autoWidthColsLength = autoWidthCols.length + groupColumnsAutoWidthWidthCols.length;
            this.scrollX = this.columsScrollX + autoWidthColsLength * 200;
            if (this.filterRowEndButton.buttons && this.filterRowEndButton.buttons.length > 0) {
              this.scrollX += this.operationColWidth;
            }
            this.scrollX = Math.max(this.scrollX, tableWidth);
            // this.scrollX -= 200;
            // if (this.designMode) {
            //   this.scrollX += 600;
            // }
            let tableFixedLeft = this.$el.querySelector('.ant-table-fixed-left'),
              tableFixedRight = this.$el.querySelector('.ant-table-fixed-right');
            if (tableFixedRight || tableFixedLeft) {
              let scrollTable = this.$el.querySelector('.ant-table');
              if (scrollTable && scrollTable.__vue__ && scrollTable.__vue__._provided.table) {
                scrollTable.__vue__._provided.table.syncFixedTableRowHeight();
              }
            }
          } else if (this.$el) {
            // 未冻结列情况下，设置横向滚动长度为初始化宽度（避免导致产生页面级滚动）
            let thead = this.$el && this.$el.querySelector('.ant-table-thead');
            let tbodyWidth = thead ? thead.clientWidth : this.$el.querySelector('.ant-table-wrapper').getBoundingClientRect().width;
            // 非固定区的自适应列
            let autoWidthCols = this.columns.filter(col => !col.width && !col.hasOwnProperty('isColumns'));
            let groupColumnsAutoWidthWidthCols = []; //this.inGroupColumns.filter(col => !col.width);
            let autoWidthColsLength = autoWidthCols.length + groupColumnsAutoWidthWidthCols.length;
            let offsetWidth = tbodyWidth - this.columsScrollX - this.operationColWidth - autoWidthColsLength * 50;
            if (offsetWidth < 0) {
              this.scrollX = tbodyWidth;
              this.scrollX += autoWidthColsLength * 50;
            }
          }
          this.adjustScrollX();
        }
      }
    },

    setSubformWidth() {
      if (this.$el && this.$el.parentNode) {
        const parentElStyle = window.getComputedStyle(this.$el.parentNode);
        const paddingL = parseFloat(parentElStyle.getPropertyValue('padding-left'));
        const paddingR = parseFloat(parentElStyle.getPropertyValue('padding-right'));
        const parentElWidth = parseFloat(parentElStyle.getPropertyValue('width'));
        if (!isNaN(parseFloat(parentElWidth)) && isFinite(parentElWidth) && this.$el.style) {
          const subformWidth = parentElWidth - paddingL - paddingR;
          this.$el.style.cssText += `;width: ${subformWidth}px`;
        }
      }
    },
    fetchFormDefinition() {
      return new Promise((resolve, reject) => {
        this.$tempStorage.getCache(
          this.formUuid,
          () => {
            return new Promise((resolve, reject) => {
              $axios
                .post(`/proxy/api/dyform/definition/getFormDefinitionByUuid?formUuid=${this.formUuid}&i18n=true`, {})
                .then(({ data }) => {
                  if (data && data.definitionVjson) {
                    this.$tempStorage.setItem(this.formUuid, data);
                    resolve(data);
                  }
                })
                .catch(error => {});
            });
          },
          data => {
            resolve(data);
          }
        );
      });
    },

    fetchLatestFormColumns() {
      this.fetchFormDefinition().then(definition => {
        if (definition) {
          let json = JSON.parse(definition.definitionVjson);
          this.formDefinitionVjson = json;
          this.version = json.version || '1.0';
          this.setI18nMessage(definition.i18ns);
          if (json.jsModule != undefined) {
            this.subformJsModule = json.jsModule;
          }
          let fieldMap = {};
          for (let i = 0, len = json.fields.length; i < len; i++) {
            fieldMap[json.fields[i].configuration.code] = json.fields[i];
            this.fields.push(json.fields[i]);
          }
          this.fieldMap = fieldMap;
          for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
            let c = this.widget.configuration.columns[i];
            if (c.widget != undefined && c.dataIndex && fieldMap[c.dataIndex]) {
              c.widget = fieldMap[c.dataIndex];
              if (c.required) {
                if (c.widget.configuration.requiredCondition && c.widget.configuration.requiredCondition.conditions) {
                  // 列必填的情况下，清空字段组件必填性判断条件
                  c.widget.configuration.requiredCondition.conditions.splice(0, c.widget.configuration.requiredCondition.conditions.length);
                }
                // 由列的必填条件带入字段组件的必填条件
                if (c.requiredCondition && c.requiredCondition.enable && c.requiredCondition.conditions.length > 0) {
                  c.widget.configuration.requiredCondition = c.requiredCondition;
                }
              }
            }
          }
          this.setColumns();

          this.initDefaultSubformRows();

          if (this.tempDataQueue != undefined && this.tempDataQueue.length > 0) {
            for (let i = 0, len = this.tempDataQueue.length; i < len; i++) {
              this.addRow(this.tempDataQueue[i]);
            }

            this.$nextTick(() => {
              this.columnWidthResize();
            });

            if (this.widget.configuration.layout == 'form-tabs' && this.subformActiveTabKey == undefined && this.rows.length > 0) {
              this.subformActiveTabKey = this.rows[0][this.rowKey];
            }
          }

          this.initedColumn = true;
        }
      });
    },
    setI18nMessage(i18ns) {
      if (i18ns && i18ns.length && this.$i18n.messages && this.$i18n.messages[this.$i18n.locale]) {
        let _widgetI18ns = this.$i18n.messages[this.$i18n.locale].Widget;
        let widgetI18ns = {};
        for (let item of i18ns) {
          set(widgetI18ns, item.code, item.content);
        }
        merge(_widgetI18ns, widgetI18ns);
        this.$i18n.mergeLocaleMessage(this.$i18n.locale, { Widget: _widgetI18ns });
      }
    },
    emitRowFormDataChange() {
      let _row = [];
      for (let i = 0, len = this.rows.length; i < len; i++) {
        let d = deepClone(this.rows[i]);
        delete d.__MODEL__;
        delete d[this.rowKey];
        _row.push(d);
      }
      let md5String = md5(JSON.stringify(_row));
      if (this.rowDataAsMD5 != md5String) {
        this.$set(this.dyform.subformDataMD5, this.widget.configuration.formUuid, md5String);
        this.dyform.emitEvent(`WidgetDyform:${this.namespace}:subformDataChange`, { data: _row, wSubform: this, dataMd5: md5String });
        this.rowDataAsMD5 = md5String;
      }
    },
    // 表格列配置后，对已有行组件再次做显示处理
    resetformElement() {
      for (let dindex = 0; dindex < this.rows.length; dindex++) {
        for (let index = 0; index < this.customRenderSlotOptions.length; index++) {
          const col = this.customRenderSlotOptions[index];
          this.$set(
            this.rows[dindex]['__MODEL__'],
            `${col.dataIndex}_ShowAsLabel`,
            // 列不可编辑 ，或者表格形态下以表单编辑行数据的情况下，列初始以文本形态展示
            (this.widget.configuration.layout == 'table' && this.widget.configuration.rowEditMode == 'form') || !col.editable
          );
        }
      }
    },

    setColumnRequired(field, required = true) {
      if (typeof field == 'object' && field.eventParams != undefined) {
        // 由事件传递进来的参数
        required = field.eventParams.required !== 'false';
        field =
          typeof field.eventParams.dataIndex === 'string' && field.eventParams.dataIndex
            ? [field.eventParams.dataIndex]
            : field.eventParams.dataIndex;
      }
      if (!Array.isArray(field)) {
        console.warn('setColumnRequired 字段参数不符合要求');
        return;
      }
      for (let index = 0; index < this.customRenderSlotOptions.length; index++) {
        if (field && field.length == 0) {
          break;
        }
        const col = this.customRenderSlotOptions[index];
        if (field && field.includes(col.dataIndex)) {
          field.splice(field.indexOf(col.dataIndex), 1);
          col.required = required;
          this.customColTitleSlotOptions[index].required = required;
          if (this.formElementRules[col.widget.id]) {
            this.formElementRules[col.widget.id].required = required;
            for (let key in this.rowForms) {
              this.rowForms[key].setFieldRequired(col.dataIndex, required);
            }
          }
        }
      }
    },
    setColumnEditable(field, editable = true) {
      if (typeof field == 'object' && field.eventParams != undefined) {
        // 由事件传递进来的参数
        editable = field.eventParams.editable !== 'false';
        field =
          typeof field.eventParams.dataIndex === 'string' && field.eventParams.dataIndex
            ? [field.eventParams.dataIndex]
            : field.eventParams.dataIndex;
      }
      if (!Array.isArray(field)) {
        console.warn('setColumnEditable 字段参数不符合要求');
        return;
      }
      for (let index = 0; index < this.customRenderSlotOptions.length; index++) {
        const col = this.customRenderSlotOptions[index];
        if (field && field.length == 0) {
          break;
        }
        if (field && field.includes(col.dataIndex)) {
          field.splice(field.indexOf(col.dataIndex), 1);
          col.editable = editable;
          if (this.formElementRules[col.widget.id]) {
            this.formElementRules[col.widget.id].displayAsLabel = !editable;
            this.formElementRules[col.widget.id].editable = editable;
            for (let key in this.rowForms) {
              this.rowForms[key].setFieldEditable(col.dataIndex, editable);
            }
          }
        }
      }
    },

    deleteSubformByFilter(filter) {
      for (let i = 0; i < this.rows.length; i++) {
        let remove = true;
        if (typeof filter == 'function') {
          remove = filter(this.rows[i]);
        }
        if (remove) {
          let row = this.rows[i];
          delete this.rowForms[row[this.rowKey]];
          delete this.rowKeyMap[row[this.rowKey]];
          this.rows.splice(i--, 1);
          continue;
        }
      }
    },
    downloadLink,
    onCancelImportData() {
      this.importModalInfo.successCount = undefined;
      this.importModalInfo.importFile.fileID = undefined;
      this.importModalInfo.importFile.file = undefined;
    },
    onConfirmImportData(e) {
      return new Promise((resolve, reject) => {
        this.importModalInfo.importing = true;
        this.importModalInfo.successCount = undefined;
        const submitFormData = new FormData();
        submitFormData.append('file', this.importModalInfo.fileList[0]);
        submitFormData.append('importService', this.importModalInfo.importService);
        submitFormData.append(
          'rule',
          JSON.stringify({
            importLog: this.importModalInfo.importLog,
            importCode: this.importModalInfo.importCode,
            strict: this.importModalInfo.strict,
            sheetImportRules: this.importModalInfo.sheetRules
          })
        );

        $axios
          .post('/proxy/api/dms/excel/importData', submitFormData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          })
          .then(({ data }) => {
            this.importModalInfo.importing = false;
            if (data.code == 0) {
              this.importModalInfo.successCount = 0;
              this.importModalInfo.failCount = data.data.fail.length;
              this.importModalInfo.importResult = data.data;
              try {
                if (this.$refs.excelImportDataPreviewer != undefined) {
                  this.$refs.excelImportDataPreviewer.updateImportResult(
                    this.importModalInfo.importResult.sheetResults,
                    this.importModalInfo.sheetRules
                  );
                }
              } catch (error) {
                console.error('更新导入结果信息异常: ', error);
              }
              console.log('导入处理数据返回', data.data);
              let sheetResults = data.data.sheetResults,
                sheetRules = this.importModalInfo.sheetRules;
              sheetResults.forEach(sheetRst => {
                let results = sheetRst.results,
                  sheetRule = sheetRules[sheetRst.sheetIndex],
                  { header, duplicateStrategy, duplicateDataHeader } = sheetRule,
                  headerMap = {},
                  duplicateDataIndex = [];
                header.forEach(h => {
                  headerMap[h.code] = h;
                  headerMap[h.title] = h;
                });
                if (duplicateDataHeader.length > 0) {
                  for (let i = 0, len = header.length; i < len; i++) {
                    if (duplicateDataHeader.includes(header[i].title)) {
                      duplicateDataIndex.push(header[i].code);
                    }
                  }
                }

                for (let i = 0, len = results.length; i < len; i++) {
                  let rst = results[i];
                  if (rst.ok) {
                    let dataAnalysed = rst.dataAnalysed;
                    let row = {};
                    for (let key in dataAnalysed) {
                      row[key] = dataAnalysed[key];
                    }

                    if (sheetRst.sheetIndex == 0) {
                      if (duplicateDataIndex.length > 0 && duplicateStrategy) {
                        let ignore = false;
                        for (let k in this.rowKeyMap) {
                          let r = this.rowKeyMap[k];
                          let eql = 0;
                          for (let d = 0, dlen = duplicateDataIndex.length; d < dlen; d++) {
                            if (row[duplicateDataIndex[d]] != undefined && r[duplicateDataIndex[d]] == row[duplicateDataIndex[d]]) {
                              eql++;
                            }
                          }
                          if (eql == duplicateDataIndex.length) {
                            if (duplicateStrategy == 'update') {
                              this.importModalInfo.successCount++;
                              // 更新行字段数据
                              for (let c in row) {
                                r.__MODEL__.label[c] = row[c];
                                if (row[`${c}$LABEL`] != undefined) {
                                  r.__MODEL__.label[c] = row[`${c}$LABEL`];
                                }
                                r[c] = row[c];
                                this.rowForms[k].setFieldValue(c, row[c]);
                              }
                            }
                            ignore = true;
                            break;
                          }
                        }
                        if (ignore) {
                          continue;
                        }
                      }
                      this.addRow(row);
                      this.importModalInfo.successCount++;
                    } else {
                      let { enable, formUuid, joinColumn, joinHeader } = sheetRule.join;
                      if (enable && formUuid != undefined && joinColumn != undefined && joinHeader != undefined) {
                        // 从表的从表数据解析
                        for (let formKey in this.rowForms) {
                          let formData = this.rowForms[formKey].formData,
                            ignore = false;
                          if (formData[joinColumn] == row[headerMap[joinHeader].code || headerMap[joinHeader].title]) {
                            if (duplicateDataIndex.length > 0) {
                              let underSubform = this.rowForms[formKey].subform;
                              if (underSubform != undefined && underSubform[sheetRule.params.formUuid] != undefined) {
                                let _underDyforms = underSubform[sheetRule.params.formUuid];
                                for (let u = 0, ulen = _underDyforms.length; u < ulen; u++) {
                                  let eql = 0;
                                  for (let d = 0, dlen = duplicateDataIndex.length; d < dlen; d++) {
                                    if (
                                      _underDyforms[u].formData[duplicateDataIndex[d]] != undefined &&
                                      _underDyforms[u].formData[duplicateDataIndex[d]] == row[duplicateDataIndex[d]]
                                    ) {
                                      eql++;
                                    }
                                  }
                                  if (eql == duplicateDataIndex.length) {
                                    if (duplicateStrategy == 'update') {
                                      this.importModalInfo.successCount++;
                                      // 更新行字段数据
                                      for (let c in row) {
                                        _underDyforms[u].formData[c] = row[c];
                                      }
                                    }
                                    ignore = true;
                                    break;
                                  }
                                }
                              }
                            }
                            if (ignore) {
                              break;
                            }
                            // 插入从表的从表
                            let subform = this.rowForms[formKey].createSubform(sheetRule.params.formUuid);
                            subform.formData = row;
                            this.importModalInfo.successCount++;
                            break;
                          }
                        }
                      }
                    }
                  }
                }

                if (this.subformActiveTabKey == undefined && this.rows.length > 0) {
                  this.subformActiveTabKey = this.rows[0][this.rowKey];
                }
              });
              resolve(data.data);
            } else {
              this.$error({
                title: '导入错误',
                content: '导入文件解析异常, 该文件已被加密或格式内容有误, 请检查后再导入'
              });
              console.error(data);
            }
          })
          .catch(err => {
            this.importModalInfo.importing = false;
            if (this.importModalInfo.fileList.length > 0) {
              this.importModalInfo.fileList[0]
                .slice(0, 1)
                .arrayBuffer()
                .then(() => {
                  this.$error({
                    title: '导入错误',
                    content: '导入文件解析异常, 该文件已被加密或格式内容有误, 请检查后再导入'
                  });
                })
                .catch(() => {
                  this.importModalInfo.importFile.fileID = undefined;
                  this.importModalInfo.fileList.splice(0, this.importModalInfo.fileList.length);
                  this.$error({
                    title: '导入错误',
                    content: '检测到文件被修改, 请重新上传文件'
                  });
                });
            } else {
              this.$error({
                title: '导入错误',
                content: '导入文件解析异常, 该文件已被加密或格式内容有误, 请检查后再导入'
              });
            }

            reject();
          });
      });
    },
    importModalClose() {
      this.importModalInfo.visible = false;
    },
    customRequest(options, afterUpload) {
      this.importModalInfo.uploading = false;
      this.importModalInfo.importFile.fileID = undefined;
      this.importModalInfo.successCount = undefined;
      this.importModalInfo.importFile.fileName = options.file.name;
      this.importModalInfo.importFile.fileID = options.file.uid;
      this.importModalInfo.fileList.splice(0, this.importModalInfo.fileList.length);
      this.importModalInfo.fileList.push(options.file);
      this.importModalInfo.importFile.File = options.file;
    },
    beforeUpload(file, limitSize) {
      return new Promise((resolve, reject) => {
        if (
          [
            'application/vnd.ms-excel',
            'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
            'application/wps-office.xlsx',
            'application/wps-office.xls'
          ].includes(file.type)
        ) {
          resolve(file);
        } else {
          this.$message.error('只允许上传 xls 或者 xlsx 文件格式');
          reject();
        }
      });
    },
    copyInitDefaultFileToRow(row) {
      let { id, data } = row;
      for (let key in data) {
        if (Array.isArray(data[key]) && data[key].length > 0 && data[key][0] != undefined && data[key][0].fileID != undefined) {
          let promises = [];
          data[key].forEach(f => {
            promises.push(
              new Promise((resolve, reject) => {
                $axios
                  .post('/json/data/services', {
                    serviceName: 'mongoFileService',
                    methodName: 'copyFileAndRename',
                    args: JSON.stringify([f.fileID, f.filename])
                  })
                  .then(({ data }) => {
                    if (data.data) {
                      resolve(data.data);
                    }
                  });
              })
            );
          });
          data[key] = undefined;
          Promise.all(promises).then(dbFiles => {
            let files = [];
            dbFiles.forEach(d => {
              files.push(d.logicFileInfo);
            });
            this.rowForms[id].setFieldValue(key, files);
          });
        }
      }
    },
    initDefaultSubformRows() {
      let defaultRowDataConfig = this.widget.configuration.defaultRowDataConfig;
      if (
        this.rootWidgetDyformContext._copyFormDataFlag == undefined &&
        defaultRowDataConfig != undefined &&
        defaultRowDataConfig.enable &&
        defaultRowDataConfig.rows.length > 0 &&
        (this.form.dataUuid == undefined || this.form.isNewFormData === true) &&
        this.form.formData.nestformDatas == undefined
      ) {
        let data = defaultRowDataConfig.rows;
        if (this.widget.configuration.defaultSort && this.widget.configuration.defaultSort.enable) {
          data = this.sortRows(data);
        }
        for (let i = 0, len = data.length; i < len; i++) {
          let row = data[i];
          this.copyInitDefaultFileToRow(row);
          if (!row.deletable) {
            row.data.status = '2';
          }
          this.addRow(row.data, undefined, this.rows, this.form.createSubform(this.widget.configuration.formUuid, row.id));
        }

        if (this.widget.configuration.layout == 'form-tabs' && this.subformActiveTabKey == undefined && this.rows.length > 0) {
          this.subformActiveTabKey = this.rows[0][this.rowKey];
        }
      }
      this.initedDefaultSubformRows = true;
    },

    setColumnVisible(dataIndex, visible = true) {
      if (typeof dataIndex == 'object' && dataIndex.eventParams != undefined) {
        // 由事件传递进来的参数
        visible = dataIndex.eventParams.visible !== 'false';
        dataIndex =
          typeof dataIndex.eventParams.dataIndex === 'string' && dataIndex.eventParams.dataIndex
            ? [dataIndex.eventParams.dataIndex]
            : dataIndex.eventParams.dataIndex;
      }
      if (!Array.isArray(dataIndex)) {
        console.warn('setColumnVisible 字段参数不符合要求');
        return;
      }
      let resetColumns = false;
      for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
        let col = this.widget.configuration.columns[i];
        if (dataIndex && dataIndex.length == 0) {
          break;
        }
        if (dataIndex && dataIndex.includes(col.dataIndex)) {
          dataIndex.splice(dataIndex.indexOf(col.dataIndex), 1);
          resetColumns = true;
          col.defaultDisplayState = visible ? 'edit' : 'hidden';
          let defaultDisplayState = visible ? 'edit' : 'hidden';
          if (visible && this.formElementRules && this.formElementRules[col.widget.id]) {
            let rule = this.formElementRules[col.widget.id];
            // 存在规则不可编辑情况下，以规则为主，显示出来还是不可编辑情况
            if (rule.editable != undefined) {
              defaultDisplayState = rule.editable ? 'edit' : 'unedit';
            }
          }

          if (this.formElementRules && this.formElementRules[col.widget.id] && this.formElementRules[col.widget.id].hidden != undefined) {
            this.formElementRules[col.widget.id].hidden = !visible;
          }
          col.defaultDisplayState = defaultDisplayState;
          break;
        }
      }
      if (resetColumns) {
        this.setColumns();
      }
    },

    setFieldOptionsRefresh(field, options, rowKey) {
      for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
        let col = this.widget.configuration.columns[i];
        if (col.dataIndex == field) {
          if (rowKey) {
            if (this.rowForms[rowKey]) {
              this.rowForms[rowKey].refreshOptions(field, options);
            }
          } else {
            for (let j = 0; j < this.rows.length; j++) {
              let rowForm = this.getRowForm(j);
              if (j == 0) {
                rowForm.setFieldOptionsRefresh(field, options);
              } else {
                rowForm.setFieldOptionsRefresh(field, { unRemoveCache: true, ...options });
              }
            }
          }
          break;
        }
      }
    },

    isColumnWidget(id) {
      for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
        if (this.widget.configuration.columns[i] && this.widget.configuration.columns[i].widget.id == id) {
          return true;
        }
      }
      return false;
    },

    resolvePopconfirmTitle(title, record) {
      try {
        let compiler = stringTemplate(title);
        return compiler(record || {});
      } catch (error) {}
      return title;
    },

    validateSubform() {
      let _this = this;
      return new Promise((resolve, reject) => {
        // 当前仅需要在从表标签布局情况下，才需要校验，其他情况下可以在主表校验时候一并触发
        if (_this.widget.configuration.layout == 'form-tabs' && _this.$refs.subformDyform && _this.$refs.subformDyform.length) {
          let msg = {};
          let validatePromise = [];
          _this.$refs.subformDyform.forEach(s => {
            validatePromise.push(
              new Promise((resolveVali, rejectVali) => {
                s.$refs.form.validate(function (valid, messages) {
                  // _this.$set(s.$attrs.row.__MODEL__, 'validateStatus', !valid ? 'error' : undefined);
                  if (!valid) {
                    _this.tableExpand = true;
                  }
                  resolveVali(messages);
                });
              })
            );
          });
          if (validatePromise.length) {
            Promise.all(validatePromise).then(res => {
              for (let i = 0; i < res.length; i++) {
                Object.assign(msg, res[i]);
              }
              resolve({
                validate: Object.keys(msg).length == 0,
                msg
              });
            });
          } else {
            resolve({
              validate: Object.keys(msg).length == 0,
              msg
            });
          }
        } else if (_this.tableHasPagination) {
          let msg = {};
          // 当前为表格布局且表格可以分页展示，校验时需对未渲染的数据同时校验
          if (!_this.proxyRowDyform) {
            // 创建从表行级表单实例
            _this.proxyRowDyform = _this.form.createSubform(_this.formUuid, generateId(), true);
            _this.proxyRowDyform.formId = _this.widget.configuration.formId;
            _this.proxyRowDyform.tableName = _this.widget.configuration.tableName; // 表名
          }
          _this.proxyRowDyform.displayState = _this.dyform.displayState; // 从表状态和主表一致
          _this.proxyRowDyform.formElementRules = deepClone(_this.formElementRules); // 表单字段规则传入
          let validatePromise = [];
          let minIndex = (_this.pagination.current - 1) * _this.pagination.pageSize;
          let maxIndex = (_this.pagination.current - 1) * _this.pagination.pageSize + _this.pagination.pageSize;
          this.$nextTick(async () => {
            for (let i = 0; i < _this.rows.length; i++) {
              // 当前页不算
              if (i >= minIndex && i < maxIndex) {
                continue;
              }
              // 当前在第一页，第二页第一行数据不延迟，当前在其他页，第一行数据不延迟
              if ((minIndex == 0 && i > maxIndex) || (minIndex > 0 && i > 0)) {
                // 该延迟是为了确保从表上一行表单渲染完成
                await _this.delay(550);
              }
              validatePromise.push(
                new Promise((resolveVali, rejectVali) => {
                  let _data = deepClone(_this.rows[i]);
                  _this.proxyRowDyform.formData = { ..._data }; // 表单数据赋值
                  for (let j = 0; j < _this.proxyTableColWidgets.length; j++) {
                    let code = _this.proxyTableColWidgets[j].configuration.code;
                    _this.proxyRowDyform.setFieldValue(code, _data[code]);
                  }
                  setTimeout(() => {
                    // 该延迟是为了确保表单渲染完成，且字段必填等逻辑执行完成
                    _this.$refs.proxyRowDyformRef.validate(function (valid, messages) {
                      resolveVali(messages);
                    });
                  }, 500);
                })
              );
            }
            if (validatePromise.length) {
              Promise.all(validatePromise).then(res => {
                for (let i = 0; i < res.length; i++) {
                  Object.assign(msg, res[i]);
                }
                resolve({
                  validate: Object.keys(msg).length == 0,
                  msg
                });
              });
            } else {
              resolve({
                validate: Object.keys(msg).length == 0,
                msg
              });
            }
          });
        } else {
          resolve();
        }
      });
    },
    delay(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
    },
    cloneWidget(widget) {
      let colWidget = deepClone(widget);
      colWidget._id = colWidget.id;
      colWidget.id = generateId();
      // 删除默认值
      delete colWidget.configuration.defaultValue;
      delete colWidget.configuration.defaultValueOption;
      colWidget.configuration.defaultDisplayState = 'edit'; // 强制为编辑状态

      delete colWidget.configuration.placeholder;

      if (colWidget.wtype == 'WidgetFormDatePicker') {
        colWidget.configuration.maxValueSetting.valueType = 'no';
        colWidget.configuration.minValueSetting.valueType = 'no';
      }

      return colWidget;
    },

    fetchFormData(formUuid, dataUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .post('/json/data/services', {
            serviceName: 'dyFormFacade',
            methodName:
              this.rootWidgetDyformContext && this.rootWidgetDyformContext.isVersionDataView ? 'getVersionFormData' : 'getFormData',
            args: JSON.stringify([formUuid, dataUuid])
          })
          .then(({ data }) => {
            let formDatas = data.data;
            resolve(formDatas);
          });
      });
    },
    onSubformTabDyformBeforeMount(e, row) {
      //  绑定事件派发的实例对象为当前tab实例，使得派发事件在从表tab级表单域内派发
      this.rowForms[row[this.rowKey]].vueInstance = e.$parent;
    },
    onSubformTabDyformMounted(row, e) {
      if (row.uuid != undefined) {
        let _this = this;
        // 加载从表数据
        this.fetchFormData(this.widget.configuration.formUuid, row.uuid).then(formDatas => {
          if (formDatas) {
            for (let key in formDatas) {
              if (_this.rootWidgetDyformContext != undefined) {
                _this.rootWidgetDyformContext.originFormData[key] = formDatas[key];
                for (let i = 0, len = formDatas[key].length; i < len; i++) {
                  _this.rootWidgetDyformContext.originFormData[formDatas[key][i].uuid] = formDatas[key][i];
                }
              }
            }
            e.setFormData(formDatas);
          }
        });
      } else {
        if (row.nestformDatas) {
          setTimeout(() => {
            let formDatas = {};
            let nestformDatas = typeof row.nestformDatas == 'string' ? JSON.parse(row.nestformDatas) : deepClone(row.nestformDatas);
            delete row.nestformDatas;
            formDatas[this.formUuid] = [row];
            for (let k in nestformDatas.formDatas) {
              if (nestformDatas.formDatas[k] && nestformDatas.formDatas[k].length > 0) {
                formDatas[k] = nestformDatas.formDatas[k];
              }
            }
            e.setFormData(formDatas, false);
          }, 100);
        }
      }
    },

    calculateColumnTotalValue(dataIndex) {
      let total = 0;
      if (this.rowForms != undefined) {
        for (let uid in this.rowForms) {
          if (this.rowForms[uid] == undefined) {
            continue;
          }
          let formData = this.rowForms[uid].formData;
          if (formData[dataIndex] != undefined) {
            total += Number(formData[dataIndex]);
          }
        }
      }
      return total;
    },
    calculateColumnAvgValue(dataIndex, fixedNumber) {
      let total = 0,
        count = 0;
      if (this.rowForms != undefined) {
        for (let uid in this.rowForms) {
          if (this.rowForms[uid] == undefined) {
            continue;
          }
          let formData = this.rowForms[uid].formData;
          if (formData[dataIndex] != undefined) {
            total += Number(formData[dataIndex]);
            count++;
          }
        }
      }
      return typeof fixedNumber == 'number' ? parseFloat((total / count).toFixed(fixedNumber)) : total / count;
    },
    calculateColumnMaxValue(dataIndex) {
      let value = [];
      if (this.rowForms != undefined) {
        for (let uid in this.rowForms) {
          if (this.rowForms[uid] == undefined) {
            continue;
          }
          let formData = this.rowForms[uid].formData;
          if (formData[dataIndex] != undefined) {
            value.push(Number(formData[dataIndex]));
          }
        }
      }
      return value.length > 0 ? Math.max(...value) : undefined;
    },
    calculateColumnMinValue(dataIndex) {
      let value = [];
      if (this.rowForms != undefined) {
        for (let uid in this.rowForms) {
          if (this.rowForms[uid] == undefined) {
            continue;
          }
          let formData = this.rowForms[uid].formData;
          if (formData[dataIndex] != undefined) {
            value.push(Number(formData[dataIndex]));
          }
        }
      }
      return value.length > 0 ? Math.min(...value) : undefined;
    },
    concatColumnValues(dataIndex, distinct, separator) {
      let text = [];
      if (this.rowForms != undefined) {
        for (let uid in this.rowForms) {
          if (this.rowForms[uid] == undefined) {
            continue;
          }
          let formData = this.rowForms[uid].formData;
          dataIndex = Array.isArray(dataIndex) ? dataIndex : [dataIndex];
          for (let d of dataIndex) {
            if (formData[d] != undefined) {
              let txt = formData[d];
              if (typeof txt == 'object') {
                txt = JSON.stringify(txt);
              } else {
                txt = (txt + '').trim();
              }
              if (txt && ((distinct && !text.includes(txt)) || !distinct)) {
                text.push(txt);
              }
            }
          }
        }
      }
      return text.length > 0 ? text.join(separator || '') : '';
    },
    onFilteredRowKeys(keys) {
      this.searchKey = generateId();
      // 搜索前记录待搜索行标识，避免当新增空行时候，搜索存在时候会导致看不到，即点击搜索或者数据发生变化时候，搜索的方法仅限这批待搜索行数据范围内
      this.waitSearchRowKeys.splice(0, this.waitSearchRowKeys.length);
      for (let i = 0, len = this.rows.length; i < len; i++) {
        this.waitSearchRowKeys.push(this.rows[i][this.rowKey]);
      }
      this.filteredRowKeys.splice(0, this.filteredRowKeys.length);
      this.filteredRowKeys.push(...keys);

      this.$nextTick(() => {
        if (this.pagination !== false) {
          this.pagination.total = this.filteredRows.length;
        }

        if (this.widget.configuration.layout == 'form-tabs') {
          this.subformActiveTabKey = this.filteredRows.length > 0 ? this.filteredRows[0][this.rowKey] : undefined;
        }
      });
    },

    onResetSearch() {
      this.searchKey = undefined;
      this.filteredRowKeys.splice(0, this.filteredRowKeys.length);
      this.$nextTick(() => {
        if (this.widget.configuration.layout == 'form-tabs') {
          this.subformActiveTabKey = this.filteredRows.length > 0 ? this.filteredRows[0][this.rowKey] : undefined;
        }
        if (this.pagination !== false) {
          this.pagination.total = this.filteredRows.length;
        }
      });
    },
    clearSubform() {
      this.rows.splice(0, this.rows.length);
      this.rowForms = {};
      this.rowKeyMap = {};
      this.selectedRowKeys.splice(0, this.selectedRowKeys.length);
      this.form.deleteSubform(this.formUuid);
      if (this.$refs.subformSearch) {
        this.$refs.subformSearch.handleReset();
      }
    }
  },
  beforeMount() {},
  mounted() {
    let _this = this;

    if (!this.designMode) {
      _this.tempDataQueue = [];
      if (this.formUuid) {
        this.form.initSubform(this.formUuid);
        this.form.$subform[this.widget.id] = this;
        this.form.$subform[this.widget.configuration.formId] = this;
        this.form.$subform[this.formUuid] = this;
        this.widgetDyformContext.$off(`WidgetSubform:${this.formUuid}:addRow`);
        this.widgetDyformContext.$on(`WidgetSubform:${this.formUuid}:addRow`, function (data, isInit) {
          if (data != undefined) {
            if (_this.formDefinitionVjson == undefined) {
              // 定义未加载，添加到临时数据集，等定义加载后再从临时数据添加到从表
              if (Array.isArray(data)) {
                if (isInit && _this.widget.configuration.defaultSort && _this.widget.configuration.defaultSort.enable) {
                  data = _this.sortRows(data);
                }
                _this.tempDataQueue.push(...data);
              } else {
                _this.tempDataQueue.push(data);
              }
              return;
            } else {
              if (!Array.isArray(data)) {
                _this.addRow(data);
              } else {
                if (isInit && _this.widget.configuration.defaultSort && _this.widget.configuration.defaultSort.enable) {
                  data = _this.sortRows(data);
                }
                for (let i = 0, len = data.length; i < len; i++) {
                  _this.addRow(data[i]);
                }
              }
              _this.$nextTick(() => {
                _this.columnWidthResize();
              });

              if (_this.widget.configuration.layout == 'form-tabs' && _this.subformActiveTabKey == undefined && _this.rows.length > 0) {
                _this.subformActiveTabKey = _this.rows[0][_this.rowKey];
              }
            }
          }
        });

        this.dyform.emitEvent(`WidgetSubform:${this.formUuid}:mounted`);
      }
      this.clientScale.width = window.innerWidth;
      this.clientScale.height = window.innerHeight;
    }
    if (!this.designMode) {
      this.setSubformWidth();
      this.$nextTick(() => {
        this.columnWidthResize();
      });
      this.pageContext.handleEvent('subformColumnWidthResize', () => {
        this.setSubformWidth();
        this.$nextTick(() => {
          this.clientScale.width = window.innerWidth;
          this.clientScale.height = window.innerHeight;
          this.columnWidthResize();
        });
      });
      addElementResizeDetector(this.$el.parentNode, () => {
        // 表单内存在多个从表时，只会触发一个从表事件，所以增加监听来触发各从表的事件
        this.pageContext.emitEvent('subformColumnWidthResize');
      });

      // if (this.widget.configuration.layout == 'form-tabs' && this.widget.configuration.tabEditOriginalForm && this.form.dataUuid) {
      //   $axios
      //     .post('/json/data/services', {
      //       serviceName: 'dyFormFacade',
      //       methodName: 'getSubformDatas',
      //       args: JSON.stringify([[this.widget.configuration.formUuid], this.form.formUuid, this.form.dataUuid])
      //     })
      //     .then(({ data }) => {
      //       console.log('=========> 懒加载从表的从表数据', data);
      //       if (data.code == 0 && data.data) {
      //       }
      //     });
      // }
    }
  },
  beforeDestroy() {
    this.dyform.offEvent(`WidgetSubform:${this.formUuid}:addRow`);
  },

  updated() {},

  watch: {
    subformWidgetIsReady: {
      handler(v, o) {
        if (v && this.emitReadyEvent == undefined) {
          this.emitReadyEvent = true;
          setTimeout(() => {
            this.dyform.emitEvent(`WidgetSubform:${this.widget.id}:ready`, { $vue: this });
            this.invokeDevelopmentMethod('onWidgetSubformReady', this);
          }, 300);
        }
      }
    },
    rows: {
      deep: true,
      handler(v, o) {
        if (this.pagination !== false) {
          this.pagination.total = this.filteredRows.length;
          // 补偿当开启页面不超过1的隐藏选项时候，变更分页时候，不隐藏分页选项的场景
          if (this.defaultHideOnSinglePage && v.length <= this.pagination.pageSize) {
            this.pagination.hideOnSinglePage = true;
          }
        }
        this.emitRowFormDataChange();
        this.updateFormInlineCheckAllState();
      }
    },
    rowLength: {
      handler(v, o) {
        this.dyform.emitEvent(`${this.widget.id}:rowLengthChange`, { $subform: this });
      }
    }
  }
};
</script>

<style lang="less"></style>
