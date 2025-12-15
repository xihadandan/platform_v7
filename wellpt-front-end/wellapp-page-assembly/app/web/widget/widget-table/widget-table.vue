<template>
  <div class="widget-table" :key="key" v-show="vShow">
    <template v-if="widget.configuration.enableTitle">
      <div
        class="_header-title"
        :style="{
          color: widget.configuration.titleColor ? widget.configuration.titleColor : ''
        }"
      >
        {{ $t('title', this.widget.title) }}
      </div>
    </template>
    <WidgetTableSearchForm :widget="widget" ref="searchForm" @changeShowKeywordSearch="changeShowKeywordSearch" />

    <!-- 表头设计区 -->
    <div v-if="designMode && widget.configuration.enableBeforeTableHeaderWidget" style="margin-bottom: 12px">
      <slot name="designBeforeTableHeaderSlot"></slot>
    </div>

    <div
      v-if="
        !designMode &&
        widget.configuration.enableBeforeTableHeaderWidget &&
        widget.configuration.beforeTableHeaderWidgets != undefined &&
        widget.configuration.beforeTableHeaderWidgets.length > 0
      "
      style="margin-bottom: 12px"
    >
      <template v-for="(wgt, ii) in widget.configuration.beforeTableHeaderWidgets">
        <component
          ref="headerWidgets"
          :key="'before_header_wgt_' + wgt.id"
          :is="wgt.wtype"
          :widget="wgt"
          :index="ii"
          :widgetsOfParent="widget.configuration.beforeTableHeaderWidgets"
          :parent="widget"
          :parentWidget="getSelf"
        ></component>
      </template>
    </div>

    <div>
      <a-row type="flex" class="widget-table-header-button" v-if="hasTableHeaderRow">
        <a-col flex="auto">
          <WidgetTableButtons
            v-if="widget.configuration.headerButton.enable"
            :button="widget.configuration.headerButton"
            :mask="designMode || loading"
            :developJsInstance="developJsInstance"
            :meta="getTableHeaderMeta"
            :visibleJudgementData="vTableHeadButtonVisibleJudgementData"
            position="tableHeader"
            :eventWidget="getSelf"
            ref="headerButtonRef"
            @button-click="onTableButtonClick"
            @buttonDataBeforeInit="button => buttonDataBeforeInit(button, {}, 'tableHeader')"
            :jsFormulaFunctions="jsFormulaFunctions"
          />
        </a-col>
        <a-col flex="100px" class="widget-table-header-button-right" :style="{ textAlign: 'right' }">
          <a-button
            v-if="widget.configuration.displayCardList && widget.configuration.toggleDisplay"
            :icon="displayStateTable ? 'credit-card' : 'table'"
            :title="
              displayStateTable
                ? $t('WidgetTable.switchToCardView', '切换为卡片视图')
                : $t('WidgetTable.switchToTableView', '切换为表格视图')
            "
            @click.stop="switchDisplayState"
          ></a-button>
          <WidgetTableCustomColumn :widget="widget" @onCustomColumnChanged="onCustomColumnChanged" />
        </a-col>
      </a-row>
      <slot name="afterTableHeader"></slot>
      <a-config-provider :locale="locale">
        <keep-alive>
          <a-table
            ref="mianTableRef"
            class="pt-empty"
            tableLayout="fixed"
            v-if="displayStateTable"
            :columns="columns"
            :data-source="rows"
            :pagination="widget.configuration.pagination.type == 'default' ? pagination : false"
            :bordered="widget.configuration.bordered"
            @change="onTableChange"
            :rowKey="primaryColumnKey"
            :row-selection="rowSelection"
            :customRow="customRow"
            :loading="loading"
            :scroll="{ x: scrollX, y: computeTableScrollY }"
            :showHeader="!widget.configuration.columnTitleHidden"
            :components="components"
            :expandIcon="expandIcon"
            :expandedRowKeys.sync="expandedRowKeys"
            :class="[
              !widget.configuration.pagination.showTotalPage ? 'table-hide-pagination-total' : '',
              widget.configuration.pagination.type == 'waterfall' ? 'waterfall-page-table' : '',
              widget.configuration.rowClickEvent.enable ? 'row-click-enable' : ''
            ]"
          >
            <template v-for="(slotOption, i) in customColumnTitles">
              <span :slot="slotOption.slotName" :key="'customColTitle_' + i">
                <a-tooltip>
                  <template slot="title">
                    {{ slotOption.tipContent }}
                  </template>
                  {{ slotOption.hidden ? '' : slotOption.title }}
                </a-tooltip>
              </span>
            </template>

            <template slot="serialNumberSlot" slot-scope="text, record, index" v-if="widget.configuration.addSerialNumber">
              <!-- <slot name="firstColumnPrefixSlot" :record="record" :index="index" :data-index="__serialnumber"></slot> -->
              <template v-if="dataShowAsWaterfall">
                {{ index + 1 }}
              </template>
              <template v-else>
                {{
                  widget.configuration.serialNumberPageIncrease && typeof pagination !== 'boolean'
                    ? (pagination.current - 1) * pagination.pageSize + index + 1
                    : index + 1
                }}
              </template>
            </template>

            <template v-for="(slotOption, i) in customRenderSlotOptions" :slot="slotOption.slotName" slot-scope="text, record, index">
              <template v-if="i == 0">
                <slot name="firstColumnPrefixSlot" :record="record" :index="index" :data-index="slotOption.dataIndex"></slot>
              </template>
              <!-- 前端提供的渲染函数 -->
              <template v-if="slotOption.serverRender === false">
                <ColumnValueClientRender
                  :key="slotOption.key"
                  :row="record"
                  :text="text"
                  :rowIndex="index"
                  :slotOption="slotOption"
                  :invokeJsFunction="invokeJsFunction"
                  :invokeDevelopmentMethod="invokeDevelopmentMethod"
                  @onInvokeDevelopmentMethod="columnRenderInvokeDevelopmentMethod"
                />
              </template>

              <!-- 后端服务直接返回的渲染值 -->
              <template v-else-if="record[slotOption.dataIndex + 'RenderValue'] != undefined">
                <div v-if="slotOption.titleDisplayTop" class="cell-title">{{ slotOption.title }}</div>
                <span
                  v-html="record[slotOption.dataIndex + 'RenderValue'] || slotOption.defaultContentIfNull"
                  :key="slotOption.slotName"
                  :style="{ lineHeight: '24px' }"
                ></span>
              </template>
              <template v-else>
                {{ text || slotOption.defaultContentIfNull }}
              </template>

              <template v-if="slotOption.subContent">
                <!-- 子内容 -->
                <ColumnValueClientRender
                  :row="record"
                  :slotOption="slotOption.subContent"
                  :rowIndex="index"
                  :invokeJsFunction="invokeJsFunction"
                  :invokeDevelopmentMethod="invokeDevelopmentMethod"
                />
              </template>

              <span
                v-if="slotOption.hoverRowButton"
                :key="'button-' + slotOption.slotName"
                :style="{ float: 'right', opacity: rowVar[rowVarKey + index] ? 1 : 0 }"
              >
                <WidgetTableButtons
                  :button="slotOption.hoverRowButton"
                  size="small"
                  :developJsInstance="developJsInstance"
                  :meta="record"
                  :eventWidget="getSelf"
                  :visibleJudgementData="defaultVisibleJudgementData"
                  @buttonDataBeforeInit="button => buttonDataBeforeInit(button, record, 'rowCol')"
                  :jsFormulaFunctions="jsFormulaFunctions"
                ></WidgetTableButtons>
              </span>

              <div v-if="slotOption.fixedRowButton" :key="'fixedbutton-' + slotOption.slotName">
                <WidgetTableButtons
                  :developJsInstance="developJsInstance"
                  :button="slotOption.fixedRowButton"
                  size="small"
                  :meta="record"
                  :eventWidget="getSelf"
                  :visibleJudgementData="defaultVisibleJudgementData"
                  @buttonDataBeforeInit="button => buttonDataBeforeInit(button, record, 'rowCol')"
                  :jsFormulaFunctions="jsFormulaFunctions"
                ></WidgetTableButtons>
              </div>

              <template v-if="i == 0">
                <slot name="firstColumnSuffixSlot" :record="record" :index="index" :data-index="slotOption.dataIndex"></slot>
              </template>
            </template>

            <div slot="__operationSlot" class="table-row-end-operation" slot-scope="text, record, index">
              <WidgetTableButtons
                :developJsInstance="developJsInstance"
                v-if="rowEndButton.buttons.length"
                :button="rowEndButton"
                @buttonDataBeforeInit="button => buttonDataBeforeInit(button, record, 'rowEnd')"
                @buttonMounted="($btnEl, style) => buttonMounted($btnEl, 'rowEnd')"
                size="small"
                :meta="record"
                :visibleJudgementData="defaultVisibleJudgementData"
                :eventWidget="getSelf"
              />
            </div>
          </a-table>
        </keep-alive>
      </a-config-provider>
      <template v-if="displayStateCardList">
        <PerfectScrollbar
          ref="cardListScroll"
          :style="{
            maxHeight: widget.configuration.enableScrollY && widget.configuration.scrollY ? widget.configuration.scrollY + 'px' : undefined
          }"
        >
          <div class="widget-table-card-list-container">
            <a-list
              :grid="{ gutter: 0, column: widget.configuration.cardColumnNum || 3 }"
              :pagination="pagination"
              :data-source="rows"
              :loading="loading"
              class="widget-table-card-list"
              :class="[widget.configuration.pagination.type == 'waterfall' ? 'waterfall-list' : '']"
            >
              <!-- 因为需要添加前置/后置卡片数据，卡片列表数据项不通过 listItem 渲染-->
              <a-row :gutter="[gutter, gutter]" style="margin: 0">
                <a-col
                  :span="24 / (widget.configuration.cardColumnNum || 3)"
                  v-if="
                    widget.configuration.prefixCardTemplateConfig != undefined &&
                    widget.configuration.prefixCardTemplateConfig.enable &&
                    (widget.configuration.prefixCardTemplateConfig.template ||
                      widget.configuration.prefixCardTemplateConfig.templateName != undefined)
                  "
                >
                  <a-card :hoverable="true" :bodyStyle="{ padding: '0px' }">
                    <WidgetListItemCardRender
                      :row="{}"
                      :rowIndex="-1"
                      :invokeJsFunction="invokeJsFunction"
                      :invokeDevelopmentMethod="invokeDevelopmentMethod"
                      :cardTemplateConfig="widget.configuration.prefixCardTemplateConfig"
                      :eventWidget="getSelf"
                      ref="itemCardRef_-1"
                    >
                      <template slot="actions">
                        <WidgetTableButtons
                          :button="rowEndButton"
                          size="small"
                          :meta="{}"
                          :eventWidget="getSelf"
                          @buttonDataBeforeInit="button => buttonDataBeforeInit(button, {}, 'rowEnd')"
                          :developJsInstance="developJsInstance"
                          :visibleJudgementData="defaultVisibleJudgementData"
                          :jsFormulaFunctions="jsFormulaFunctions"
                        ></WidgetTableButtons>
                      </template>
                    </WidgetListItemCardRender>
                  </a-card>
                </a-col>
                <template v-for="(item, index) in rows">
                  <a-col :span="24 / (widget.configuration.cardColumnNum || 3)">
                    <template v-if="widget.configuration.useCardTemplate">
                      <a-card :hoverable="true" :bodyStyle="{ padding: '0px' }" @click="onRowClick($event, item, index)">
                        <WidgetListItemCardRender
                          :row="item"
                          :rowIndex="index"
                          :invokeJsFunction="invokeJsFunction"
                          :invokeDevelopmentMethod="invokeDevelopmentMethod"
                          :cardTemplateConfig="widget.configuration.cardTemplateConfig"
                          :eventWidget="getSelf"
                          :ref="'itemCardRef_' + index"
                        >
                          <template slot="selection">
                            <a-checkbox
                              :checked="selectedRowKeys.includes(item[primaryColumnKey])"
                              @click.stop="e => onClickSelectCheckbox(e, item)"
                            />
                          </template>
                          <template slot="actions">
                            <WidgetTableButtons
                              :button="rowEndButton"
                              size="small"
                              :meta="item"
                              :eventWidget="getSelf"
                              @buttonDataBeforeInit="button => buttonDataBeforeInit(button, item, 'rowEnd')"
                              :developJsInstance="developJsInstance"
                              :visibleJudgementData="defaultVisibleJudgementData"
                              :jsFormulaFunctions="jsFormulaFunctions"
                            ></WidgetTableButtons>
                          </template>
                          <template v-for="(c, i) in columns">
                            <template v-if="c.dataIndex != '__serialnumber' && c.dataIndex != '__operation'" :slot="c.dataIndex + 'Slot'">
                              <template v-if="c.scopedSlots.customRender != undefined">
                                <!-- 前端提供的渲染函数 -->
                                <ColumnValueClientRender
                                  v-if="!customRenderSlotOptionMaps[c.id].serverRender"
                                  :row="item"
                                  :rowIndex="index"
                                  :key="customRenderSlotOptionMaps[c.id].key"
                                  :text="item[c.dataIndex]"
                                  :slotOption="customRenderSlotOptionMaps[c.id]"
                                  :invokeJsFunction="invokeJsFunction"
                                  :invokeDevelopmentMethod="invokeDevelopmentMethod"
                                  @onInvokeDevelopmentMethod="columnRenderInvokeDevelopmentMethod"
                                />

                                <!-- 后端服务直接返回的渲染值 -->
                                <div
                                  v-else
                                  v-html="item[customRenderSlotOptionMaps[c.id].dataIndex + 'RenderValue']"
                                  :key="customRenderSlotOptionMaps[c.id].slotName"
                                ></div>
                              </template>
                              <template>
                                {{ item[c.dataIndex] }}
                              </template>
                            </template>
                          </template>
                        </WidgetListItemCardRender>
                      </a-card>
                    </template>
                    <a-card :hoverable="true" v-else>
                      <div
                        @mouseenter="mouseenterCardListItem(item, index)"
                        @mouseleave="mouseleaveCardListItem(item, index)"
                        @click="onRowClick($event, item, index)"
                      >
                        <a-descriptions :column="1">
                          <template v-for="(c, i) in columns">
                            <a-descriptions-item
                              v-if="c.dataIndex != '__serialnumber' && c.dataIndex != '__operation'"
                              :key="i"
                              :label="
                                c.titleHidden || (c.showTip && c.tipContent) || widget.configuration.columnTitleHidden ? undefined : c.title
                              "
                            >
                              <a-tooltip
                                v-if="!c.titleHidden && !widget.configuration.columnTitleHidden && c.showTip && c.tipContent"
                                slot="label"
                              >
                                <template slot="title">
                                  {{ c.tipContent }}
                                </template>
                                {{ c.titleHidden ? '' : c._title }}
                              </a-tooltip>
                              <template v-if="c.scopedSlots.customRender">
                                <!-- 前端提供的渲染函数 -->

                                <ColumnValueClientRender
                                  v-if="!customRenderSlotOptionMaps[c.id].serverRender"
                                  :row="item"
                                  :key="customRenderSlotOptionMaps[c.id].key"
                                  :text="item[c.dataIndex]"
                                  :slotOption="customRenderSlotOptionMaps[c.id]"
                                  :invokeJsFunction="invokeJsFunction"
                                  :invokeDevelopmentMethod="invokeDevelopmentMethod"
                                  @onInvokeDevelopmentMethod="columnRenderInvokeDevelopmentMethod"
                                />

                                <!-- 后端服务直接返回的渲染值 -->
                                <div
                                  v-else
                                  v-html="item[customRenderSlotOptionMaps[c.id].dataIndex + 'RenderValue']"
                                  :key="customRenderSlotOptionMaps[c.id].slotName"
                                ></div>

                                <div
                                  v-if="customRenderSlotOptionMaps[c.id].hoverRowButton"
                                  :key="'button-' + customRenderSlotOptionMaps[c.id].slotName"
                                  v-show="rowVar[rowVarKey + index]"
                                  style="position: absolute"
                                >
                                  <WidgetTableButtons
                                    :button="customRenderSlotOptionMaps[c.id].hoverRowButton"
                                    size="small"
                                    :meta="item"
                                    :eventWidget="getSelf"
                                    @buttonDataBeforeInit="button => buttonDataBeforeInit(button, item, 'rowCol')"
                                    :developJsInstance="developJsInstance"
                                    :visibleJudgementData="defaultVisibleJudgementData"
                                    :jsFormulaFunctions="jsFormulaFunctions"
                                  ></WidgetTableButtons>
                                </div>
                                <div
                                  v-if="customRenderSlotOptionMaps[c.id].fixedRowButton"
                                  :key="'fixedbutton-' + customRenderSlotOptionMaps[c.id].slotName"
                                >
                                  <WidgetTableButtons
                                    :button="customRenderSlotOptionMaps[c.id].fixedRowButton"
                                    size="small"
                                    :meta="item"
                                    :eventWidget="getSelf"
                                    @buttonDataBeforeInit="button => buttonDataBeforeInit(button, item, 'rowCol')"
                                    :developJsInstance="developJsInstance"
                                    :visibleJudgementData="defaultVisibleJudgementData"
                                    :jsFormulaFunctions="jsFormulaFunctions"
                                  ></WidgetTableButtons>
                                </div>
                              </template>
                              <div v-else v-html="item[c.dataIndex]"></div>
                            </a-descriptions-item>
                          </template>
                        </a-descriptions>
                      </div>

                      <template slot="actions" v-if="rowEndButton.buttons.length > 0">
                        <WidgetTableButtons
                          :button="rowEndButton"
                          size="small"
                          :meta="item"
                          :eventWidget="getSelf"
                          @buttonDataBeforeInit="button => buttonDataBeforeInit(button, item, 'rowEnd')"
                          :developJsInstance="developJsInstance"
                          :visibleJudgementData="defaultVisibleJudgementData"
                          :jsFormulaFunctions="jsFormulaFunctions"
                        ></WidgetTableButtons>
                      </template>
                    </a-card>
                  </a-col>
                </template>

                <a-col
                  :span="24 / (widget.configuration.cardColumnNum || 3)"
                  v-if="
                    widget.configuration.suffixCardTemplateConfig != undefined &&
                    widget.configuration.suffixCardTemplateConfig.enable &&
                    (widget.configuration.suffixCardTemplateConfig.template ||
                      widget.configuration.suffixCardTemplateConfig.templateName != undefined)
                  "
                >
                  <a-card :hoverable="true" :bodyStyle="{ padding: '0px' }">
                    <WidgetListItemCardRender
                      :row="{}"
                      :rowIndex="-2"
                      :invokeJsFunction="invokeJsFunction"
                      :invokeDevelopmentMethod="invokeDevelopmentMethod"
                      :cardTemplateConfig="widget.configuration.suffixCardTemplateConfig"
                      :eventWidget="getSelf"
                      ref="itemCardRef_-2"
                    >
                      <template slot="actions">
                        <WidgetTableButtons
                          :button="rowEndButton"
                          size="small"
                          :meta="{}"
                          :eventWidget="getSelf"
                          @buttonDataBeforeInit="button => buttonDataBeforeInit(button, {}, 'rowEnd')"
                          :developJsInstance="developJsInstance"
                          :visibleJudgementData="defaultVisibleJudgementData"
                          :jsFormulaFunctions="jsFormulaFunctions"
                        ></WidgetTableButtons>
                      </template>
                    </WidgetListItemCardRender>
                  </a-card>
                </a-col>
              </a-row>
              <a-empty v-show="rows.length == 0 && noPrefixSuffixCardItem" class="pt-empty" style="margin-top: 20px" />
            </a-list>
          </div>
        </PerfectScrollbar>
      </template>
      <div v-if="widget.configuration.pagination.type === 'waterfall'" class="waterfall-pagination">
        <a-button v-show="hasMoreData" type="link" size="small" :loading="loading" @click="loadMore">
          {{ $t('WidgetTable.loadMore', '加载更多') }}
        </a-button>
      </div>
    </div>
    <a-modal
      v-if="exportRowsBtnKey != undefined"
      v-model="exportColumnDefModalVisible"
      :title="$t('WidgetTable.selectExportColumn', '选择导出列')"
      @ok="onConfirmExportColumnRow"
      :getContainer="getExportModalContainer"
    >
      <div>
        <a-checkbox
          :indeterminate="userCheckedExportColumn[exportRowsBtnKey].indeterminate"
          :checked="userCheckedExportColumn[exportRowsBtnKey].checkAll"
          @change="onExportColCheckAll"
        >
          {{ $t('WidgetTable.selectAll', '全选') }}
        </a-checkbox>
        <a-divider style="margin: 6px 0px" />
        <a-checkbox-group
          class="export-col-checkbox-group"
          v-model="userCheckedExportColumn[exportRowsBtnKey].key"
          :options="userCheckedExportColumn[exportRowsBtnKey].options"
          @change="onExportColCheckChange"
        >
          <span slot="label" slot-scope="{ _label }" :title="_label">{{ _label }}</span>
        </a-checkbox-group>
      </div>
    </a-modal>

    <a-modal
      v-if="importModalInfo && importModalInfo.modalTemplate == undefined"
      v-model="importModalInfo.visible"
      :width="importModalInfo.width"
      :title="importModalInfo.title"
      :okText="importModalInfo.okText"
      :maskClosable="false"
      :okButtonProps="{
        props: { icon: 'upload', loading: importModalInfo.importing, disabled: importModalInfo.importFile.fileID == undefined }
      }"
      :body-style="{ height: '235px' }"
      :getContainer="getExportModalContainer"
    >
      <div style="height: 155px">
        <a-upload-dragger
          name="file"
          :multiple="false"
          :showUploadList="false"
          :before-upload="e => beforeUpload(e, 100)"
          :customRequest="e => customRequest(e)"
        >
          <p class="ant-upload-drag-icon">
            <a-icon :type="importModalInfo.uploading ? 'loading' : 'inbox'" />
          </p>
          <p class="ant-upload-text">{{ $t('WidgetTable.clickOrDragToUpload', '点击或者拖拽文件到此区域上传') }}</p>
          <p class="ant-upload-hint">
            {{ $t('WidgetTable.importModal.downloadImportTemplate', '下载导入模板') }}
            <a-button size="small" icon="link" type="link" @click.stop="downloadImportFileTemplate">
              {{ importModalInfo.importFileTemplate.name }}
            </a-button>
          </p>
        </a-upload-dragger>
        <div style="display: flex; justify-content: space-between; align-items: center; line-height: 52px">
          <div v-if="importModalInfo.importFile.fileID != undefined">
            <label @click.stop="() => {}">
              <a-icon style="color: var(--w-success-color)" type="check-circle" theme="filled" />
              {{ $t('WidgetTable.importModal.uploadFinished', '已上传') }}:
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
      <template slot="footer">
        <a-button @click="onCancelImportData">取消</a-button>
        <a-button
          type="primary"
          icon="upload"
          @click="onConfirmImportData"
          :loading="importModalInfo.importing"
          :disabled="importModalInfo.importFile.fileID == undefined"
        >
          {{ importModalInfo.okText }}
        </a-button>
      </template>
    </a-modal>
    <!-- 导入模板弹出 -->
    <component
      v-else-if="importModalInfo && importModalInfo.modalTemplate != undefined && importModalInfo.visible"
      :is="importModalInfo.modalTemplate"
      :key="importModalInfo.key"
      :widget="widget"
      :file-template="importModalInfo.importFileTemplate"
      :confirm-import-file="onConfirmImportData"
      :customFileUploadRequest="customRequest"
      :importModalInfo="importModalInfo"
      @close="importModalClose"
    />
  </div>
</template>

<style lang="less">
@import './css/index.less';
</style>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import DataSourceBase from '../../assets/js/commons/dataSource.base';
import TableRenderMixin from './table.renderMixin';
import {
  deepClone,
  generateId,
  getElMaxHeightFromViewport,
  getElSpacingForTarget,
  findParentVNodeByName,
  executeJSFormula
} from '@framework/vue/utils/util';
import md5 from '@framework/vue/utils/md5';
import { DispatchEvent } from '@framework/vue/lib/dispatchEvent';
import draggable from '@framework/vue/designer/draggable';
import ResizeableTitle from './resizeable.title.js';
import moment from 'moment';
import { psScrollResize } from '@framework/vue/utils/function';
import { debounce, sortBy, template as stringTemplate, get, cloneDeep, orderBy, trim } from 'lodash';
import ExcelImportDataPreviewer from './excel-import-data-previewer.vue';

export default {
  name: 'WidgetTable',
  mixins: [widgetMixin, TableRenderMixin, draggable],
  inject: ['parentLayContentId', 'locale', 'unauthorizedResource', '$event'],
  props: {
    customRowStyle: Function,
    customCellStyle: Function
  },
  provide() {
    return {
      widgetTableContext: this
    };
  },
  data() {
    const defaultPageSize = 10;
    let pageSize = this.widget.configuration.pagination.pageSize || defaultPageSize;
    let pageSizeOptions = this.widget.configuration.pagination.pageSizeOptions || [];
    if (!this.designMode && this.widget.configuration.pagination.type !== 'no') {
      if (!pageSizeOptions.includes(pageSize + '')) {
        pageSizeOptions.push(pageSize + '');
      }
    }
    let data = {
      key: this.widget.id,
      columns: [],
      realColumns: [],
      components: {
        header: {
          cell: ResizeableTitle
        }
      },
      operationColWidth: this.widget.configuration.operationColWidth || 75,
      tdPadding: null,
      rowVarKey: null,
      rowVar: {}, // 行变量，用于控制行行为的参数
      rowStyle: {},
      hoverBtnVisible: false,
      customRenderSlotOptions: [],
      customRenderSlotOptionMaps: {},
      customColumnTitles: [],
      selectedRows: [],
      selectedRowKeys: [],
      serverDataRenders: [], // 后端接口渲染函数
      sortOrders: [],
      primaryColumnKey: null,
      scrollX: 0,
      columsScrollX: 1200,
      inGroupColumns: [], // 记录到表头分组里的列
      loading: false,
      leftFreezeColNum: this.widget.configuration.leftFreezeColNum,
      rightFreezeColNum: this.widget.configuration.rightFreezeColNum,
      rowEndButton: { buttons: [] },
      displayState: (this.widget.configuration.displayCardList && this.widget.configuration.defaultDisplayState) || 'table',
      defaultPageSize,
      pagination:
        this.widget.configuration.pagination.type === 'no'
          ? false
          : {
              current: 1, //当前页
              pageSize: pageSize, //每页条数
              showQuickJumper: !!this.widget.configuration.pagination.showQuickJumper, //是否允许跳页
              pageSizeOptions: pageSizeOptions,
              showSizeChanger: !!this.widget.configuration.pagination.showSizeChanger,
              hideOnSinglePage: this.widget.configuration.pagination.hideOnSinglePage,
              total: 0, //总条数
              showLessItems: !!this.widget.configuration.pagination.showLessItems,
              onChange: page => {
                if (this.displayState == 'cardList') {
                  this.pagination.current = page;
                  this.fetch({});
                }
              },
              onShowSizeChange: (current, size) => {
                if (this.displayState == 'cardList') {
                  this.pagination.pageSize = size;
                  this.fetch({});
                }
              }
            },
      rows: [], // 行数据
      mergeCell: this.widget.configuration.mergeCell || {},
      isMergeCell: this.widget.configuration.mergeCell ? this.widget.configuration.mergeCell.isMergeCell : false,
      mergeRowsData: [], // 列合并数据，
      mergeColumnsData: {}, // 行合并数据
      reUpdateIndex: 0, // 记录表格组件等待重新渲染数
      renderRowByState: this.widget.configuration.renderRowByState || {},
      hasMoreData: false,
      expandIcon: undefined,
      expandedRowKeys: [],
      dataSourceParams: {},
      userCheckedExportColumn: {},
      exportRowsBtnKey: undefined,
      exportColumnDefModalVisible: false,
      __TABLE__: { selectedRowCount: undefined },
      importModalInfo: {
        title: this.$t('WidgetTable.importModal.importModalTitle', '数据导入'),
        okText: this.$t('WidgetTable.importModal.importModalOkText', '导入'),
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
        key: generateId(),
        importResult: {},
        importService: undefined
      },
      gutter: 16,
      dynamicVariables: [],
      fileName: '' // 默认的导入导出文件名
    };

    // 扩展按钮变量
    if (this.widget.configuration.headerButton.enable && this.widget.configuration.headerButton.buttons.length) {
      this.widget.configuration.headerButton.buttons.forEach(button => {
        if (button.defaultVisibleVar && button.defaultVisibleVar.enable) {
          if (button.defaultVisibleVar.code && button.defaultVisibleVar.code.startsWith('__TABLE__.')) {
            data.__TABLE__[button.defaultVisibleVar.code.substring('__TABLE__.'.length)] = undefined;
          }
          if (
            // 组合条件
            button.defaultVisibleVar.match != undefined &&
            button.defaultVisibleVar.conditions &&
            button.defaultVisibleVar.conditions.length > 0
          ) {
            button.defaultVisibleVar.conditions.forEach(cond => {
              if (cond.code && cond.code.startsWith('__TABLE__')) {
                data.__TABLE__[cond.code.substring('__TABLE__.'.length)] = undefined;
              }
            });
          }
        }
      });
    }
    data.displayTableContentHeight = 0;
    return data;
  },
  beforeCreate() {},
  components: {
    ColumnValueClientRender: () => import('./column-value-client-render.js'),
    WidgetListItemCardRender: () => import('./widget-list-item-card-render.js'),
    TableExcelImportResultModal: () => import('./table-excel-import-result-modal.vue'),
    ExcelImportDataPreviewer
  },
  computed: {
    computeTableScrollY() {
      let scrollY;
      if (this.widget.configuration.enableScrollY && this.widget.configuration.scrollY) {
        scrollY = this.widget.configuration.scrollY;
      }
      if (this.displayStateTable) {
        if (this.configuration.fixedTableHeader && this.configuration.scrollCalculateContainer === 'viewport') {
          scrollY = this.displayTableContentHeight;
        }
      }
      return scrollY;
    },
    defaultVisibleJudgementData() {
      return {
        ...this._vShowByDateTime,
        ...this._vShowByUserData,
        ...this._vShowByWorkflowData,
        ...(this.dyform != undefined ? { __DYFORM__: { editable: this.dyform.displayState == 'edit' } } : {}),
        ...(this.dyform != undefined ? this.dyform.formData || {} : this._showByData || {}),
        _URL_PARAM_: this.vUrlParams
      };
    },
    vTableHeadButtonVisibleJudgementData() {
      let __TABLE__ = this.$data.__TABLE__;
      __TABLE__.selectedRowCount = this.selectedRowKeys.length;
      return {
        __TABLE__: {
          ...__TABLE__
        },
        ...this.defaultVisibleJudgementData
      };
    },
    vTableMeta() {
      return {
        selectedRowKeys: this.selectedRowKeys,
        selectedRows: this.selectedRows
      };
    },
    // 相关方法暴露为事件，提供外部调用
    defaultEvents() {
      return [
        { id: 'refetch', title: '重新加载数据', information: '可通过事件参数传递查询参数进行加载数据' },
        { id: 'deleteByRowKey', title: '删除行数据', eventParams: [{ paramKey: 'key', required: true, remark: '行主键值' }] }
      ];
    },
    hasTableHeaderRow() {
      return (
        (this.widget.configuration.headerButton.enable && this.widget.configuration.headerButton.buttons.length) ||
        (this.widget.configuration.displayCardList && this.widget.configuration.toggleDisplay) ||
        this.widget.configuration.enableCustomTable
      );
    },
    displayStateCardList() {
      return this.displayState == 'cardList';
    },
    displayStateTable() {
      return this.displayState == 'table';
    },
    // 默认视图是表格或者可以切换视图，表示有表格视图
    hasTableDisplayState() {
      return this.widget.configuration.defaultDisplayState == 'table' || this.widget.configuration.toggleDisplay;
    },
    dataShowAsWaterfall() {
      return this.widget.configuration.pagination.type == 'waterfall';
    },
    rowSelection() {
      return this.configuration.rowSelectType == 'no'
        ? null
        : {
            type: this.configuration.rowSelectType,
            selectedRowKeys: this.selectedRowKeys,
            onChange: this.selectRowChange,
            getCheckboxProps: this.getCheckboxProps,
            columnWidth: 60,
            onSelect: this.onRowSelect
          };
    },
    noPrefixSuffixCardItem() {
      return !(
        (this.widget.configuration.prefixCardTemplateConfig != undefined &&
          this.widget.configuration.prefixCardTemplateConfig.enable &&
          (this.widget.configuration.prefixCardTemplateConfig.template ||
            this.widget.configuration.prefixCardTemplateConfig.templateName != undefined)) ||
        (this.widget.configuration.suffixCardTemplateConfig != undefined &&
          this.widget.configuration.suffixCardTemplateConfig.enable &&
          (this.widget.configuration.suffixCardTemplateConfig.template ||
            this.widget.configuration.suffixCardTemplateConfig.templateName != undefined))
      );
    },

    // 参与列合并的列dataIndex
    mergeCols() {
      let columns = [];
      if (this.displayStateTable && this.isMergeCell && this.mergeCell.mergeCellCols) {
        for (let i = 0; i < this.mergeCell.mergeCellCols.length; i++) {
          let column = this.mergeCell.mergeCellCols[i];
          let hasIndex = this.realColumns.findIndex(item => item.dataIndex == column.dataIndex);
          if (hasIndex > -1) {
            columns.push(column);
          }
        }
      }
      return columns;
    },
    jsFormulaFunctions() {
      return [
        {
          key: 'getSelectedRowColValueString',
          fn: (col, separator) => {
            return this.getSelectedRowColValueString(col, separator);
          }
        }
      ];
    }
  },
  created() {
    this.setColumns(); // 初始化展示的列
    if (!this.designMode && EASY_ENV_IS_BROWSER) {
      this.getDataSourceProvider();
    }

    this.adjustScrollFixedTableWidth = debounce(this.adjustScrollFixedTableWidth.bind(this), 300);
    this.resizeObserverHandler = debounce(this.resizeObserverHandler.bind(this), 300);
    this.setContentByDisplayState = debounce(this.setContentByDisplayState.bind(this), 300);
  },
  methods: {
    getTableHeaderMeta() {
      return { ...this.vTableMeta, dataSourceParams: this.dataSourceParams };
    },
    downloadImportFileTemplate() {
      window.open(this.importModalInfo.importFileTemplate.url, '_blank');
    },

    onCancelImportData() {
      this.importModalInfo.successCount = undefined;
      this.importModalInfo.importFile.fileID = undefined;
      this.importModalInfo.visible = false;
    },
    onConfirmImportData(e) {
      if (e.fileID) {
        this.importModalInfo.importFile.fileID = e.fileID;
      }
      return new Promise((resolve, reject) => {
        this.importModalInfo.importing = true;
        this.importModalInfo.successCount = undefined;
        $axios
          .post('/json/data/services', {
            serviceName: 'dmsDataImportService',
            methodName: 'importByListener',
            args: JSON.stringify([
              this.importModalInfo.importFile.fileID,
              this.importModalInfo.importService,
              {
                importLog: this.importModalInfo.importLog,
                importCode: this.importModalInfo.importCode,
                strict: this.importModalInfo.strict,
                sheetImportRules: this.importModalInfo.sheetRules
              }
            ])
          })
          .then(({ data }) => {
            this.importModalInfo.importing = false;
            this.refetch();
            if (data.code == 0) {
              this.importModalInfo.successCount = data.data.success.length;
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

              resolve(data.data);
            } else {
              this.$error({
                title: this.$t('WidgetTable.importModal.importErrorTitle', '导入错误'),
                content: this.$t(
                  'WidgetTable.importModal.importErrorContent',
                  '导入文件解析异常, 该文件已被加密或格式内容有误, 请检查后再导入'
                )
              });
              console.error(data);
            }
          })
          .catch(err => {
            this.importModalInfo.importing = false;
            console.error(err);
            this.$error({
              title: this.$t('WidgetTable.importModal.importErrorTitle', '导入错误'),
              content: this.$t(
                'WidgetTable.importModal.importErrorContent',
                '导入文件解析异常, 该文件已被加密或格式内容有误, 请检查后再导入'
              )
            });
            reject();
          });
      });
    },
    importModalClose() {
      this.importModalInfo.visible = false;
    },
    getExportModalContainer() {
      return this.$el;
    },
    onExportColCheckChange() {
      let userChecked = this.userCheckedExportColumn[this.exportRowsBtnKey];
      userChecked.indeterminate = userChecked.key.length > 0 && userChecked.key.length < userChecked.options.length;
      userChecked.checkAll = userChecked.key.length > 0 && userChecked.key.length == userChecked.options.length;
    },
    onExportColCheckAll(e) {
      let userChecked = this.userCheckedExportColumn[this.exportRowsBtnKey];
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
    onTableButtonClick(button) {
      if (button.id.startsWith('export_')) {
        let exportButtonRule = this.widget.configuration.export.exportButtonRule;
        if (exportButtonRule[button.id]) {
          // 导出按钮
          this.exportColumnDefModalVisible = exportButtonRule[button.id].exportColumnSelectable;
          if (this.userCheckedExportColumn[button.id] == undefined) {
            let map = {
              checkAll: true,
              indeterminate: false,
              key: [],
              options: []
            };
            let rule = exportButtonRule[button.id],
              columns = rule.useTableDataSource ? this.widget.configuration.columns : rule.columns;
            for (let i = 0, len = columns.length; i < len; i++) {
              let c = columns[i];
              if (c.exportable !== true) {
                continue;
              }
              map.key.push(c.dataIndex);
              map.options.push({
                _label: this.$t(c.id, c.title),
                value: c.dataIndex
              });
            }
            this.$set(this.userCheckedExportColumn, button.id, map);
            this.exportRowsBtnKey = button.id;
          }
          if (!this.exportColumnDefModalVisible) {
            this.exportTableRows(button.id);
          }
        }
      } else if (button.id.startsWith('import_')) {
        let importButtonRule = this.widget.configuration.import.importButtonRule[button.id];
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
      }
    },
    onConfirmExportColumnRow() {
      this.exportColumnDefModalVisible = false;
      this.exportTableRows(this.exportRowsBtnKey);
    },
    getFileName() {
      let name = this.$t('title', this.widget.title);
      if (this.widget.configuration.rowDataFrom === 'dataSource' && this.widget.configuration.dataSourceName) {
        name = this.widget.configuration.dataSourceName;
      } else if (this.widget.configuration.rowDataFrom == 'dataModel' && this.widget.configuration.dataModelName) {
        name = this.widget.configuration.dataModelName;
      }
      return name ? name + '_' + moment().format('YYYYMMDDHHmmssSSS') : moment().format('YYYYMMDDHHmmssSSS');
    },
    exportTableRows(key) {
      let exportColumns = [],
        exportButtonRule = this.widget.configuration.export.exportButtonRule[key],
        userChecked = this.userCheckedExportColumn[key],
        columns = exportButtonRule.useTableDataSource ? this.widget.configuration.columns : exportButtonRule.columns,
        extras = undefined;

      let exportFileName = null;
      if (exportButtonRule.exportFileName) {
        try {
          this.fileName = this.$t('WidgetTable.exportModal.exportFileTitleHead', '导出数据') + '_' + this.getFileName();
          let compiler = stringTemplate(this.$t(key + '_fileName', exportButtonRule.exportFileName));
          exportFileName = compiler(this.$data);
        } catch (error) {
          console.error('解析导出文件名模板字符串错误: ', error);
        }
      } else {
        exportFileName = this.$t(key + '_fileName', exportButtonRule.exportFileName);
      }
      let pagination =
        this.pagination !== false
          ? {
              currentPage: exportButtonRule.exportRange == 'all' ? 1 : this.pagination.current,
              pageSize: exportButtonRule.exportRange == 'all' ? -1 : this.pagination.pageSize
            }
          : { currentPage: 1, pageSize: -1 };

      for (let i = 0, len = columns.length; i < len; i++) {
        let column = columns[i];
        if (userChecked.key.includes(columns[i].dataIndex)) {
          let title = this.$t(column.id, column.title);
          if (!exportButtonRule.useTableDataSource) {
            title = this.$t(key + '_' + column.dataIndex, column.title);
          }
          exportColumns.push({
            columnIndex: column.dataIndex,
            title: title,
            renderer: Object.assign(
              {
                rendererType: column.exportFunction.type
              },
              column.exportFunction.options
            )
          });
        }
        // 导出选择的数据行
        if (exportButtonRule.useTableDataSource && column.primaryKey && exportButtonRule.exportRange == 'rowSelected') {
          extras = { selectedBy: { [column.dataIndex]: [] } };
          let selectedRows = this.getSelectedRows();
          if (selectedRows.length == 0) {
            this.$message.info(this.$t('WidgetTable.exportModal.pleaseSelectExportRow', '请选择导出行'));
            return;
          }
          selectedRows.forEach(r => {
            extras.selectedBy[column.dataIndex].push(r[column.dataIndex]);
          });
        }
      }
      this.$message.info(this.$t('WidgetTable.exportModal.exportingTip', '数据正在导出'));
      if (exportButtonRule && exportButtonRule.useTableDataSource) {
        let searchCriterionParams = this.$refs.searchForm.getSearchCriterionParams();
        this.dataSourceProvider.exportData({
          fileName:
            exportFileName || this.$t('WidgetTable.exportModal.exportFileTitleHead', '导出数据') + '_' + moment().format('yyyyMMDDHHmmss'),
          pagination,
          exportColumns,
          type: exportButtonRule.exportServiceType,
          extras,
          orders:
            this.sorter && this.sorter.columnKey != null
              ? [{ sortName: this.sorter.field, sortOrder: this.sorter.order == 'ascend' ? 'asc' : 'desc' }]
              : [],
          ...searchCriterionParams
        });
      } else {
        // 创建数据源
        let dsOptions = {
          params: {},
          onDataChange: function () {},
          defaultCriterions: exportButtonRule.defaultCondition
            ? [
                {
                  sql: exportButtonRule.defaultCondition
                }
              ]
            : []
        };
        if (exportButtonRule.dataSourceId && exportButtonRule.dataSourceType == 'dataSource') {
          dsOptions.dataStoreId = exportButtonRule.dataSourceId;
        } else if (exportButtonRule.dataSourceType == 'dataModel' && exportButtonRule.dataModelUuid) {
          dsOptions.loadDataUrl = '/proxy/api/dm/loadData/' + exportButtonRule.dataModelUuid;
        }
        new DataSourceBase(dsOptions).exportData({
          pagination,
          extras,
          fileName:
            exportFileName || this.$t('WidgetTable.exportModal.exportFileTitleHead', '导出数据') + '_' + moment().format('yyyyMMDDHHmmss'),
          exportColumns,
          type: exportButtonRule.exportServiceType
        });
      }
    },
    getDataSourceProvider() {
      if (this.dataSourceProvider == undefined) {
        let _this = this;
        // 创建数据源
        let dsOptions = {
          onDataChange: function (data, count, params) {
            _this.onDataChange(data, count, params);
          },
          receiver: this,
          params: this.configuration.dataSourceParams || {},
          // exportColumns: _this.getExportColumns(),
          // renderers: _this.getRenderers(),
          defaultOrders: this.getDefaultSort(),
          pageSize: this.pagination.pageSize || this.defaultPageSize
        };
        if (
          this.configuration.dataSourceId &&
          (this.configuration.rowDataFrom == 'dataSource' || this.configuration.rowDataFrom == undefined)
        ) {
          dsOptions.dataStoreId = this.configuration.dataSourceId;
          this.dataSourceProvider = new DataSourceBase(dsOptions);
        } else if (this.configuration.rowDataFrom == 'dataModel' && this.configuration.dataModelUuid) {
          dsOptions.loadDataUrl = '/proxy/api/dm/loadData/' + this.configuration.dataModelUuid;
          dsOptions.loadDataCntUrl = '/proxy/api/dm/loadDataCount/' + this.configuration.dataModelUuid;
          // dsOptions.dataStoreId = this.configuration.dataModelUuid;
          this.dataSourceProvider = new DataSourceBase(dsOptions);
        }
        if (this.dataSourceProvider) {
          this.dataSourceProvider.setDefaultCriterions(this.getDefaultCondition());
        }
      }
      return this.dataSourceProvider;
    },
    getSelf() {
      return this;
    },
    setColumns(columnSource) {
      let _this = this;
      this.serverDataRenders.splice(0, this.serverDataRenders.length);
      this.columns.splice(0, this.columns.length);
      this.customColumnTitles.splice(0, this.customColumnTitles.length);
      this.realColumns.splice(0, this.realColumns.length);

      this.leftFreezeColNum = this.widget.configuration.leftFreezeColNum;
      this.rightFreezeColNum = this.widget.configuration.rightFreezeColNum;
      let rowButtonMap = this.getRowButtons();
      let scrollX = 0, // 所有数据列宽度的和
        fixedWidth = 0; // 有固定列时，固定列宽度的和
      if (this.configuration.rowSelectType && this.configuration.rowSelectType !== 'no') {
        scrollX += 60; // 选择列宽度
        if (this.configuration.freezeColumn && this.leftFreezeColNum > 0) {
          fixedWidth += 60;
        }
      }
      // 设计器模式下，未配置数据仓库的情况下
      if (this.designMode && this.widget.configuration.columns.length === 0) {
        this.columns = [
          { title: '列名称', dataIndex: 'title1' },
          { title: '列名称', dataIndex: 'title2' },
          { title: '列名称', dataIndex: 'title3' },
          { title: '列名称', dataIndex: 'title4' }
        ];
        return;
      }
      let leftFreezeColNum = this.leftFreezeColNum,
        rightFreezeColNum = this.rightFreezeColNum;
      if (this.configuration.addSerialNumber) {
        let serialNumberColumn = {
          title: this.$t('WidgetTable.column.serialNumber', '序号'),
          dataIndex: '__serialnumber',
          align: 'center',
          fixed: this.configuration.freezeColumn && this.leftFreezeColNum > 0 ? 'left' : false,
          width: 80,
          scopedSlots: { customRender: 'serialNumberSlot' }
        };
        this.columns.push(serialNumberColumn);
        this.realColumns.push(serialNumberColumn);
        scrollX += 70;
        if (this.configuration.freezeColumn && this.leftFreezeColNum > 0) {
          fixedWidth += 70; // 左边序号宽度
        }
      }

      if (columnSource == undefined) {
        columnSource = this.configuration.columns;
      }
      for (let i = 0, len = columnSource.length; i < len; i++) {
        let col = deepClone(columnSource[i]);
        // if (col.role && !this.designMode) {
        //   // 判断是否有权限
        //   if (!(Array.isArray(col.role) ? this._hasAnyRole(col.role) : this._hasRole(col.role))) {
        //     continue;
        //   }
        // }

        col.scopedSlots = {};
        if (
          !col.hidden &&
          ((col.renderFunction != null && col.renderFunction.type) ||
            (rowButtonMap && rowButtonMap[col.id]) ||
            col.subContent ||
            col.titleDisplayTop === true ||
            col.defaultContentIfNull != undefined)
        ) {
          let slotOptions = {
            dataIndex: col.dataIndex,
            slotName: col.id,
            title: this.$t(col.id, col.title),
            serverRender: false,
            titleDisplayTop: col.titleDisplayTop,
            clickEvent: col.clickEvent,
            key: col.id // 刷新使用
          };
          if (col.defaultContentIfNull) {
            slotOptions.defaultContentIfNull = this.$t(col.id + '_defaultContentIfNull', col.defaultContentIfNull);
          }
          slotOptions.subContent = col.subContent; // 子内容
          if (col.renderFunction != null && col.renderFunction.type) {
            slotOptions.options = col.renderFunction.options || {};
            slotOptions.type = col.renderFunction.type;
            slotOptions.serverRender = !this.isWebRender({ type: col.renderFunction.type });
            if (slotOptions.serverRender) {
              //后端接口服务的渲染函数
              this.serverDataRenders.push({
                columnIndex: col.dataIndex,
                param: Object.assign(
                  {
                    rendererType: col.renderFunction.type
                  },
                  col.renderFunction.options
                )
              });
            }
          }
          if (rowButtonMap && rowButtonMap[col.id]) {
            slotOptions.hoverRowButton = rowButtonMap[col.id].hoverRow || null;
            slotOptions.fixedRowButton = rowButtonMap[col.id].underRow || null;
          }

          this.customRenderSlotOptions.push(slotOptions);
          this.customRenderSlotOptionMaps[col.id] = slotOptions;
          col.scopedSlots.customRender = col.id;
        }

        if (col.sortable) {
          col.sorter = true;
          this.sortOrders.push({ sortName: col.dataIndex });
        }

        // 定义列标题提示内容
        if (col.titleHidden) {
          col.title = ''; // 隐藏标题
        } else if (col.showTip && col.tipContent) {
          this.customColumnTitles.push({
            title: this.$t(col.id, col.title),
            slotName: 'title_' + col.id,
            tipContent: this.$t(col.id + '_tipContent', col.tipContent),
            hidden: !!col.titleHidden
          });
          col.slots = { title: 'title_' + col.id };
          col._title = this.$t(col.id, col.title);
          delete col.title;
        } else if (col.title) {
          // 表头提示文字
          let title_val = this.$t(col.id, col.title);
          let title_prop = {
            attrs: {
              title: title_val
            }
          };
          col.title = <span {...title_prop}>{title_val}</span>;
        }

        // 自定义列头展示
        col.customHeaderCell = this.customHeaderCell;
        col.customCell = (record, rowIndex) => {
          return _this.customCell(record, rowIndex, col);
        };
        col.widthResizable = !!this.configuration.colWidthDrag;

        if (col.primaryKey) {
          this.primaryColumnKey = col.dataIndex;
        }

        if (!col.hidden) {
          //  左列冻结
          if (this.configuration.freezeColumn) {
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

          col.key = col.dataIndex + '_' + i; // 避免使用同一个字段渲染多个列导致的冲突

          // 隐藏列不展示
          this.columns.push(col);
          this.realColumns.push(col);
          if (col.width) {
            scrollX += col.width;
          }
        }
      }
      // 右列冻结
      if (this.configuration.freezeColumn) {
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

      this.columns = this.getColumnsGroup();
      // console.log('columns', this.columns);
      // 行末按钮
      if (this.rowEndButton.buttons && this.rowEndButton.buttons.length > 0) {
        this.columns.push({
          title: this.$t('WidgetTable.column.operation', '操作'),
          dataIndex: '__operation',
          align: 'center',
          fixed: this.configuration.freezeColumn ? 'right' : false, //&& this.rightFreezeColNum > 0,有列冻结，默认冻结操作列
          width: this.operationColWidth + 'px',
          scopedSlots: { customRender: '__operationSlot' }
        });

        scrollX += this.operationColWidth;
        if (this.configuration.freezeColumn) {
          fixedWidth += this.operationColWidth; // 右侧操作列宽度
        }
      }
      if (this.configuration.freezeColumn) {
        this.adjustScrollFixedTableWidth();
      }
    },
    addColumn(col) {
      if (this.rowEndButton.buttons && this.rowEndButton.buttons.length) {
        this.columns.splice(this.columns.length - 1, 0, col);
      } else {
        this.columns.push(col);
      }
    },
    // 表头分组处理
    getColumnsGroup() {
      let columns = [];
      Object.assign(columns, this.columns);
      this.inGroupColumns.splice(0, this.inGroupColumns.length); // 分组里面的列
      // 表头分组
      if (
        this.displayState == 'table' &&
        this.widget.configuration.isColumnsGroup &&
        this.widget.configuration.columnsGroupNodes &&
        this.widget.configuration.columnsGroupNodes.length
      ) {
        let inGroupColumnsIndex = []; // 分组里面的所有列的dataIndex
        let groupColumnsIndex = {}; // 记录列索引在哪个组中
        let groupColumns = {}; // 记录列在哪个组中
        let deleteNodes = []; // 删除的节点
        let columnsGroupNodes = deepClone(this.widget.configuration.columnsGroupNodes);
        // 删除没有列配置的节点
        let deleteGroupItem = (pid, id) => {
          if (pid) {
            let pGroup = this.getTreeDataByKey(columnsGroupNodes, pid);
            if (pGroup) {
              let hasIndex = pGroup.children.findIndex(item => item.id == id);
              if (hasIndex > -1) {
                pGroup.children.splice(hasIndex, 1);
              }
              let filterChildren = pGroup.children.filter(item => {
                return (item.isColumns && item.dataIndex) || (item.children && item.children.length);
              });
              if (filterChildren.length == 0) {
                deleteGroupItem(pGroup.pid, pGroup.id);
              }
            }
          } else if (id) {
            let hasIndex = columnsGroupNodes.findIndex(item => item.id == id);
            if (hasIndex > -1) {
              columnsGroupNodes.splice(hasIndex, 1);
            }
          }
        };
        // 遍历树
        let foreachColumnsGroup = (group, levelIds) => {
          if (Array.isArray(group)) {
            for (let i = 0; i < group.length; i++) {
              if (!group[i].pid) {
                levelIds = [];
              }
              group[i].key = group[i].id;
              group[i].ellipsis = true;
              foreachColumnsGroup(group[i], levelIds);
            }
          } else if (group) {
            if (group.children && group.children.length) {
              levelIds.push(group.id);
              if (group.showTip && group.tipContent) {
                this.customColumnTitles.push({
                  title: this.$t(group.id, group.title),
                  slotName: 'title_' + group.id,
                  tipContent: this.$t(group.id + '_tipContent', group.tipContent)
                });
                group.slots = { title: 'title_' + group.id };
                group._title = this.$t(group.id, group.title);
                delete group.title;
              } else if (group.title) {
                // 表头提示文字
                let title_val = this.$t(group.id, group.title);
                let title_prop = {
                  attrs: {
                    title: title_val
                  }
                };
                group.title = <span {...title_prop}>{title_val}</span>;
              }
              foreachColumnsGroup(group.children, levelIds);
            } else if (group.isColumns && group.dataIndex) {
              let hasIndex = -1;
              if (group.dataIndex) {
                hasIndex = columns.findIndex(item => item.dataIndex === group.dataIndex);
              }
              if (hasIndex > -1) {
                if (columns[hasIndex].fixed && columns[hasIndex].initwidth == 'auto') {
                  this.columsScrollX = this.columsScrollX - 120;
                  columns[hasIndex].width = null;
                }
                delete columns[hasIndex].fixed;
                delete group.title; // 使用列配置的title
                Object.assign(group, columns[hasIndex]);
                if (!groupColumnsIndex[levelIds[0]]) {
                  groupColumnsIndex[levelIds[0]] = [];
                }
                if (!groupColumns[levelIds[0]]) {
                  groupColumns[levelIds[0]] = [];
                }
                groupColumnsIndex[levelIds[0]].push(hasIndex);
                groupColumns[levelIds[0]].push(group);
                this.inGroupColumns.push(group);
                inGroupColumnsIndex.push(group.dataIndex);
              } else {
                deleteNodes.push({ pid: group.pid, id: group.id });
              }
            } else {
              deleteNodes.push({ pid: group.pid, id: group.id });
            }
          }
        };
        foreachColumnsGroup(columnsGroupNodes, []);
        // 删除没有列配置的节点
        for (let i = 0; i < deleteNodes.length; i++) {
          deleteGroupItem(deleteNodes[i].pid, deleteNodes[i].id);
        }
        // console.log(columnsGroupNodes);
        if (columnsGroupNodes.length) {
          for (let i = 0; i < columnsGroupNodes.length; i++) {
            let indexs = groupColumnsIndex[columnsGroupNodes[i].id];
            if (indexs && indexs.length) {
              indexs.sort((a, b) => a - b);
              let delCol = columns.splice(indexs[0], 1, columnsGroupNodes[i]); // 替换表头组中第一个字段在列里面的位置
              inGroupColumnsIndex.splice(inGroupColumnsIndex.indexOf(delCol[0].dataIndex), 1); // 删除表头组中要移除的列
            }
          }
          // 移除已在分组里的列
          for (let i = 0; i < inGroupColumnsIndex.length; i++) {
            let hasIndex = columns.findIndex(item => item.dataIndex === inGroupColumnsIndex[i]);
            if (hasIndex > -1) {
              columns.splice(hasIndex, 1);
            }
          }
          // 有冻结列时，分组不参与冻结列。
          if (this.configuration.freezeColumn) {
            let min = -1,
              max = -1;
            let leftFreezeColNum = this.configuration.addSerialNumber
              ? this.widget.configuration.leftFreezeColNum + 1
              : this.widget.configuration.leftFreezeColNum;
            let rightFreezeColNum = columns.length - this.widget.configuration.rightFreezeColNum;
            // 分组在实际列中的位置
            for (let i = 0; i < columns.length; i++) {
              if (columns[i].hasOwnProperty('isColumns')) {
                // 分组在前冻结列范围内
                if (min == -1 && leftFreezeColNum > i) {
                  min = i;
                }
                // 分组在后冻结列范围内
                if (rightFreezeColNum < i) {
                  max = i;
                }
              }
            }
            this.realColumns.splice(0, this.realColumns.length);
            for (let i = 0; i < columns.length; i++) {
              if (columns[i].fixed == 'left' && i > min && min != -1) {
                if (columns[i].initwidth == 'auto') {
                  this.columsScrollX = this.columsScrollX - 120;
                }
                delete columns[i].fixed;
              }
              if (columns[i].fixed == 'right' && i < max && max != -1) {
                if (columns[i].initwidth == 'auto') {
                  this.columsScrollX = this.columsScrollX - 120;
                }
                delete columns[i].fixed;
              }
              if (columns[i].hasOwnProperty('isColumns')) {
                this.realColumns = this.realColumns.concat(groupColumns[columns[i].id]);
              } else {
                this.realColumns.push(columns[i]);
              }
            }
          }
          // console.log(columns);
          // console.log(this.realColumns);
        }
      }
      return columns;
    },
    // 根据key获取与之相等的数据对象
    getTreeDataByKey(childs = [], findKey, parent = {}) {
      let finditem = null;
      for (let i = 0, len = childs.length; i < len; i++) {
        let item = childs[i];
        if (item.id !== findKey && item.children && item.children.length > 0) {
          finditem = this.getTreeDataByKey(item.children, findKey, item);
        }
        if (item.id == findKey) {
          finditem = item;
        }
        if (finditem != null) {
          break;
        }
      }
      return finditem;
    },
    onDataChange(data, count, params) {
      let _this = this;
      if (this.dataShowAsWaterfall) {
        if (this.pagination.current == 1) {
          //瀑布式分页，当前页是第一页时，返回值为所有值
          this.rows = data.data;
        } else if (data.data.length > 0) {
          this.rows = this.rows.concat(data.data);
        }
      } else {
        this.rows = data.data;
      }
      this.reUpdateIndex = 0;
      if (this.displayStateTable) {
        if (this.mergeCols.length) {
          this.reUpdateIndex++;
          // 行合并
          if (this.mergeCell.mergeCellMode == 'merge') {
            // 合并模式
            for (let i = this.mergeCols.length - 1; i > -1; i--) {
              let column = this.mergeCols[i];
              this.rows = orderBy(this.rows, [column.dataIndex], [column.sortType || 'asc']);
            }
          }
          this.mergeRowsData = this.mergeRows(
            this.rows,
            this.mergeCols.map(item => item.dataIndex),
            this.mergeCell
          );
        }
        if (this.mergeCell.isRowMergeCell) {
          // 列合并
          this.mergeColumns(this.rows, this.realColumns, this.mergeCell);
          this.reUpdateIndex++;
        }
      }
      this.rowStyle = {}; // 清空行样式属性
      if (this.displayStateTable && this.renderRowByState.enable) {
        this.reUpdateIndex++;
        this.renderRowsByState(this.rows, this.renderRowByState);
      }
      if (this.widget.configuration.notCancelSelectedAfterPage !== true) {
        this.cancelSelectedRows();
      }

      if (this.hasHoverRowButtons) {
        for (let i = 0, len = this.rows.length; i < len; i++) {
          this.rows[i].__TableRowHoverd = false;
        }
      }

      for (let i = 0, len = _this.customRenderSlotOptions.length; i < len; i++) {
        _this.customRenderSlotOptions[i].key = generateId();
      }

      this.invokeDevelopmentMethod('onTableRowDataChange', data);
      if (typeof this.pagination !== 'boolean') {
        this.pagination.total = count;
        this.pagination.current = data.pagination.currentPage;
        this.pagination.totalPages = data.pagination.totalPages;
        if (this.pagination.showTotal == undefined) {
          //显示总页数文案
          this.pagination.showTotal = function (total, range) {
            const totalPages =
              total % _this.pagination.pageSize == 0
                ? parseInt(total / _this.pagination.pageSize)
                : parseInt(total / _this.pagination.pageSize + 1);
            return _this.configuration.pagination.showTotalPage
              ? this.$t(
                  'WidgetTable.message.totalPageContent',
                  {
                    totalPages,
                    total
                  },
                  `共${totalPages}页/${total}条记录`
                )
              : this.$t(
                  'WidgetTable.message.totalCountContent',
                  {
                    total
                  },
                  `${total}条记录`
                );
          };
        }
        // 没有更多数据不显示加载更多按钮
        if ((data.data && data.data.length < this.pagination.pageSize) || this.rows.length == this.pagination.total) {
          this.hasMoreData = false;
        } else {
          this.hasMoreData = true;
        }
        // 瀑布分页时，数据会被截掉----当前未发现该情况,先注释
        // if (this.dataShowAsWaterfall && this.rows.length > 0) {
        //   this.pagination.pageSize = this.rows.length;
        // }
      }
      this.loading = false;
      this.$nextTick(() => {
        if (this.checkWidgetGridNoticed) {
          this.setContentByDisplayState();
        } else {
          this.emitEventWidgetGrid();
        }

        this.$emit('tbodyRendered', { rows: this.rows });
        if (this.displayState !== 'cardList') {
          this.adjustScrollFixedTableWidth();
        }
        psScrollResize(this);
      });
    },
    deleteByRowKey(e) {
      if (e != undefined) {
        if (typeof e == 'string') {
          this.deleteRowsByKeys(e.split(';'));
        } else if (typeof e !== 'string' && e.eventParams != undefined) {
          // 由事件传递进来的参数
          this.deleteRowsByKeys(e.eventParams.key.split(';'));
        }
      }
    },
    deleteRowsByKeys(keys) {
      for (let i = 0; i < this.rows.length; i++) {
        if (keys.includes(this.rows[i][this.primaryColumnKey])) {
          this.rows.splice(i--, 1);
        }
      }
    },
    loadMore() {
      this.$refs.searchForm.handleKeywordSearch(this.pagination.current + 1);
    },
    fetch(params) {
      this.$emit('beforeLoadData', params);
      this.invokeDevelopmentMethod('beforeLoadData', params);
      let tableParams = Object.assign(
          {
            pagination:
              this.pagination === false
                ? { currentPage: 1, pageSize: 2000 } // 默认不分页的情况下，只抽取最大2000条数据
                : {
                    currentPage: this.pagination.current,
                    pageSize: this.dataShowAsWaterfall ? this.widget.configuration.pagination.pageSize : this.pagination.pageSize
                  },
            renderers: this.serverDataRenders,
            criterions: [],
            orders:
              this.sorter && this.sorter.columnKey != null
                ? [{ sortName: this.sorter.field, sortOrder: this.sorter.order == 'ascend' ? 'asc' : 'desc' }]
                : [],
            keyword: null
          },
          params
        ),
        _this = this;
      if (this.widget.configuration.pagination.type == 'no') {
        tableParams.pagination.pageSize = this.widget.configuration.pagination.onlyFetchCount || -1;
      }

      let otherConditions = this.otherConditions || [];
      if (otherConditions.length) {
        otherConditions.forEach(condition => {
          tableParams.criterions.push(condition);
        });
      }

      if (this.dataSourceProvider) {
        // 通过数据源拉取数据
        this.loading = true;
        this.dataSourceProvider.load(tableParams, params != undefined ? params.params || {} : {}).catch(error => {
          this.loading = false;
          if (error.response) {
            this.$message.error(error.response.data.msg, 3);
          }
        });
      } else if (this.widget.configuration.rowDataFrom === 'developSource' && this.widget.configuration.developSourceMethod) {
        let parts = this.widget.configuration.developSourceMethod.split('.');
        if (this.$developJsInstance[parts[0]] && typeof this.$developJsInstance[parts[0]][parts[1]] === 'function') {
          // 执行二开脚本的方法
          let result = this.invokeDevelopmentMethod(parts[1], {
            tableParams,
            $widget: this,
            callback: (rows, count) => {
              _this.onDataChange(rows, count, {});
            }
          });
          if (result instanceof Promise) {
            result.then(({ rows, count }) => {
              _this.onDataChange(rows, count, {});
            });
          }
        } else if (this.$pageJsInstance && typeof this.$pageJsInstance[parts[1] === 'function']) {
          let result = this.$pageJsInstance[parts[1]]({
            tableParams,
            $widget: this,
            callback: (rows, count) => {
              _this.onDataChange(rows, count, {});
            }
          });
          if (result instanceof Promise) {
            result.then(({ rows, count }) => {
              _this.onDataChange(rows, count, {});
            });
          }
        }
      }
    },

    onTableChange(pagination, filters, sorter) {
      this.sorter = sorter;
      if (typeof this.pagination !== 'boolean') {
        this.pagination.current = pagination.current;
        this.pagination.pageSize = pagination.pageSize;
      }
      this.$refs.searchForm.handleKeywordSearch();
    },
    getCheckboxProps(record) {
      return {
        // style : { ... 可以设置样式 }
        on: {
          click: e => {
            if (this.configuration.rowSelectType == 'radio') {
              // 单选反选取消选中效果
              if (this.selectedRowKeys.includes(record[this.primaryColumnKey])) {
                this.selectedRowKeys.splice(0, this.selectedRowKeys.length);
                this.selectedRows.splice(0, this.selectedRows.length);
              }
            }
          }
        }
      };
    },
    onRowSelect(record, selected) {
      console.log('选择', arguments);
    },
    selectRowChange(selectedRowKeys, selectedRows, append = false) {
      if (this.widget.configuration.notCancelSelectedAfterPage !== true) {
        // 翻页后取消已选行数据
        this.selectedRowKeys.splice(0, this.selectedRowKeys.length);
        this.selectedRows.splice(0, this.selectedRows.length);

        for (let i = 0, len = selectedRows.length; i < len; i++) {
          this.selectedRows.push(selectedRows[i]);
          this.selectedRowKeys.push(selectedRows[i][this.primaryColumnKey]);
        }
      } else {
        // 翻页后不取消已选行数据

        if (!append) {
          this.selectedRowKeys.splice(0, this.selectedRowKeys.length);
        }
        let keys = [];
        for (let i = 0, len = this.selectedRows.length; i < len; i++) {
          keys.push(this.selectedRows[i][this.primaryColumnKey]);
        }
        this.selectedRowKeys.push(...selectedRowKeys);
        for (let i = 0, len = selectedRows.length; i < len; i++) {
          if (!keys.includes(selectedRows[i][this.primaryColumnKey])) {
            this.selectedRows.push(selectedRows[i]);
          }
        }
        for (let i = 0; i < this.selectedRows.length; i++) {
          if (!selectedRowKeys.includes(this.selectedRows[i][this.primaryColumnKey])) {
            this.selectedRows.splice(i--, 1);
          }
        }
      }
      this.selectedRowKeys = [...this.selectedRowKeys];
      this.$emit('onSelectRowChanged', {
        selectedRowKeys: this.selectedRowKeys,
        selectedRows: this.selectedRows
      });

      // console.log('当前表格选中行数据: ', this.selectedRowKeys, this.selectedRows);
    },

    addSelectedRows(rows = []) {
      rows.forEach(row => {
        let key = row[this.primaryColumnKey];
        let keyIndex = this.selectedRowKeys.indexOf(key);
        let rowKey = this.selectedRows.findIndex(selected => selected[this.primaryColumnKey] == key);
        if (keyIndex == -1 && rowKey == -1) {
          this.selectedRowKeys.push(key);
          this.selectedRows.push(row);
        }
      });
      this.$emit('onSelectRowChanged', {
        selectedRowKeys: this.selectedRowKeys,
        selectedRows: this.selectedRows
      });
    },

    cancelSelectedRows() {
      this.selectedRowKeys.splice(0, this.selectedRowKeys.length);
      this.selectedRows.splice(0, this.selectedRows.length);
      this.$emit('onSelectRowChanged', {
        selectedRowKeys: this.selectedRowKeys,
        selectedRows: this.selectedRows
      });
    },

    cancelSelectRowByIndex(index) {
      let key = this.rows[index][this.primaryColumnKey];
      let keyIndex = this.selectedRowKeys.indexOf(key);
      let rowIndex = this.selectedRows.findIndex(row => row[this.primaryColumnKey] == key);
      if (keyIndex != -1 && rowIndex != -1) {
        this.selectedRowKeys.splice(keyIndex, 1);
        this.selectedRows.splice(rowIndex, 1);
      }

      this.$emit('onSelectRowChanged', {
        selectedRowKeys: this.selectedRowKeys,
        selectedRows: this.selectedRows
      });
    },

    getSelectedRowKeys() {
      return [].concat(this.selectedRowKeys || []);
    },

    getRowsByKeys(keys) {
      let result = [];
      for (let r of this.rows) {
        if (keys.includes(r[this.primaryColumnKey])) {
          result.push(r);
        }
      }
      return result;
    },

    selectRowByIndex(index, append = false) {
      let selectedRowKeys = [],
        selectedRows = [];

      for (let i = 0, len = this.rows.length; i < len; i++) {
        if (typeof index == 'number' && index == i) {
          if (!this.selectedRowKeys.includes(this.rows[i][this.primaryColumnKey])) {
            selectedRowKeys.push(this.rows[i][this.primaryColumnKey]);
            selectedRows.push(this.rows[i]);
          }

          break;
        } else if (Array.isArray(index) && index.includes(i)) {
          if (!this.selectedRowKeys.includes(this.rows[i][this.primaryColumnKey])) {
            selectedRowKeys.push(this.rows[i][this.primaryColumnKey]);
            selectedRows.push(this.rows[i]);
          }
        }
      }
      if (selectedRowKeys.length > 0) {
        this.selectRowChange(selectedRowKeys, selectedRows, append);
      }
    },

    getSelectedRows() {
      return this.selectedRows;
    },

    isWebRender(options) {
      return (
        options.type === 'vueTemplateDataRender' ||
        TableRenderMixin.methods[options.type] != undefined ||
        TableRenderMixin.methods.isCellRender(options.type)
      );
    },

    getDefaultCondition() {
      if (this.configuration.defaultCondition && this.configuration.enableDefaultCondition === true) {
        let compileDefaultCondition = condition => {
          let sql = condition;
          try {
            // 通过 ${} 符号引用的变量编译
            let data = this.widgetDependentVariableDataSource();
            if (this.dyform) {
              Object.assign(data, {
                FORM_DATA: this.dyform.formData
              });
            }
            sql = stringTemplate(condition)(data);
          } catch (error) {
            console.error('解析模板字符串错误: ', error);
            throw new Error('表格默认条件变量解析异常');
          }
          return sql != undefined && sql.trim() !== '' ? [{ sql: sql.trim() }] : [];
        };
        let defaultCondition = this.configuration.defaultCondition;
        if (defaultCondition.includes('FORM_DATA.') && this.dyform != undefined) {
          // 根据表单字段数据联动的条件
          let extractFormDataVariables = function (string) {
            const regex = /FORM_DATA\.([a-zA-Z_][a-zA-Z0-9_]*)/g;
            const matches = [...string.matchAll(regex)];
            return [...new Set(matches.map(match => match[1]))];
          };
          let variables = extractFormDataVariables(defaultCondition),
            _this = this;
          if (variables.length) {
            let resolveVariables = formData => {
              let values = [];
              if (formData) {
                for (let v of variables) {
                  if (formData[v] != '' && formData[v] != null && formData[v] != undefined) {
                    values.push(formData[v]);
                  }
                }
              }
              let hash = md5(JSON.stringify(values));
              let originalCondition = this.configuration.defaultCondition;
              if (_this.defaultConditionVariableHash != hash) {
                _this.defaultConditionVariableHash = hash;
                for (let v of variables) {
                  // 传递表单变量命名参数
                  let field = _this.dyform.getField(v);
                  let paramKey = 'FORM_DATA_' + v;
                  if (field) {
                    if (field.widget.wtype == 'WidgetFormDatePicker') {
                      // 传递格式化
                      paramKey = paramKey + `#DATE(${field.getFixedFormat})`;
                    }
                  }
                  originalCondition = originalCondition.replace(new RegExp(':FORM_DATA\.' + v, 'g'), ':FORM_DATA_' + v);
                  _this.dataSourceProvider.addParam(paramKey, formData[v]);
                }
              }
              return originalCondition;
            };
            const dataSourceProviderWatchDepFormDataChange = debounce(function (formData) {
              _this.dataSourceProvider.setDefaultCriterions(compileDefaultCondition(resolveVariables(formData)));
              _this.refetch(_this.getSearchFormParams(true));
            }, 600);
            if (this.watchedFormDataChange == undefined) {
              this.watchedFormDataChange = true;
              this.$watch(
                'dyform.formData',
                (newValue, oldValue) => {
                  dataSourceProviderWatchDepFormDataChange(newValue);
                },
                { deep: true }
              );
            }
            defaultCondition = resolveVariables(this.dyform.formData);
          }
        }
        return compileDefaultCondition(defaultCondition);
      }
      return [];
    },

    getDefaultSort() {
      var defaultSort = [];
      if (this.widget.configuration.defaultSort) {
        for (let i = 0, len = this.widget.configuration.defaultSort.length; i < len; i++) {
          defaultSort.push({
            sortName: this.widget.configuration.defaultSort[i].dataIndex,
            sortOrder: this.widget.configuration.defaultSort[i].sortType
          });
        }
      }
      return defaultSort;
    },
    customHeaderCell(col) {
      return {
        style: {
          textAlign: col.titleAlign
        }
      };
    },

    customCell(record, rowIndex, col) {
      let styleObject = {};
      styleObject['text-align'] = `${col.contentAlign}`;
      if (col.clickEvent && col.clickEvent.enable) {
        styleObject.color = 'var(--w-primary-color)';
        styleObject.cursor = 'pointer';
      }
      // 行数据样式，字体大小和字体颜色
      if (this.rowStyle[rowIndex] != undefined) {
        if (this.rowStyle[rowIndex].fontSize) {
          styleObject['font-size'] = this.rowStyle[rowIndex].fontSize;
        }
        if (this.rowStyle[rowIndex].color) {
          styleObject.color = this.rowStyle[rowIndex].color;
        }
      }

      // 行合并
      let rowSpan = 1;
      let mergeInfo = this.mergeRowsData[col.dataIndex];
      if (mergeInfo && mergeInfo.length) {
        rowSpan = mergeInfo[rowIndex].rowSpan;
      }
      // 列合并，仅未进行行合并的行可进行列合并，否则会导致表格错位
      let colSpan = 1;
      if (rowSpan == 1 && this.mergeColumnsData[rowIndex] && this.mergeColumnsData[rowIndex].hasOwnProperty(col.dataIndex)) {
        colSpan = this.mergeColumnsData[rowIndex][col.dataIndex].colSpan;
      }
      if (rowSpan === 0 || colSpan === 0) {
        styleObject.display = 'none';
      }
      if (typeof this.customCellStyle == 'function') {
        this.customCellStyle(styleObject, col, record, rowIndex);
      }
      let style = '';
      for (let key in styleObject) {
        style += `${key}:${styleObject[key]};`;
      }
      if (col.customStyle != undefined) {
        style += col.customStyle;
      }

      return {
        style,
        attrs: {
          rowSpan,
          colSpan
        },
        on: {
          click: event => {
            if (col.clickEvent && col.clickEvent.enable) {
              // 执行单元格点击事件
              let eventHandler = col.clickEvent.eventHandler;
              if (eventHandler.trigger === 'click') {
                eventHandler.meta = record;
                eventHandler.$evt = event;
                this.dispatchEventHandler(eventHandler);
              }
              event.stopPropagation();
            }
          }
        }
      };
    },
    clearRowStyleByRowKey(rowKey) {
      for (let i = 0, len = this.rows.length; i < len; i++) {
        if (this.rows[i][this.primaryColumnKey] === rowKey) {
          this.clearRowStyle(i);
        }
      }
    },
    clearRowStyle(index) {
      this.$set(this.rowStyle, index, {});
    },
    setRowStyle(index, style) {
      this.$set(this.rowStyle, index, style);
    },
    addDataSourceParams(params) {
      if (this.dataSourceProvider) {
        for (let k in params) {
          this.dataSourceProvider.addParam(k, params[k]);
          this.$set(this.dataSourceParams, k, params[k]);
        }
      }
    },
    clearDataSourceParams() {
      if (this.dataSourceProvider) {
        this.dataSourceProvider.clearParams();
        this.dataSourceParams = {};
      }
    },
    deleteDataSourceParams(...key) {
      if (this.dataSourceProvider) {
        for (let k of key) {
          this.dataSourceProvider.removeParam(k);
          this.$delete(this.dataSourceParams, k);
        }
      }
    },
    /**
     * 添加额外的查询条件
     */
    addOtherConditions: function (conditions) {
      const _this = this;
      let otherConditions = _this.otherConditions || [];
      let addConditions = conditions.filter(condition => {
        let index = otherConditions.findIndex(otherCondition => JSON.stringify(condition) == JSON.stringify(otherCondition));
        return index == -1;
      });
      _this.otherConditions = [...otherConditions, ...addConditions];
    },
    /**
     * 情况额外查询条件,condition为空是清楚全部，否则清楚等于condition的一条额外查询条件
     */
    clearOtherConditions: function (condition) {
      const _this = this;
      let otherConditions = _this.otherConditions || [];
      if (Array.isArray(condition)) {
        condition.forEach(cond => {
          _this.otherConditions = otherConditions.filter(otherCondition => JSON.stringify(cond) != JSON.stringify(otherCondition));
        });
      } else if (condition) {
        _this.otherConditions = otherConditions.filter(otherCondition => JSON.stringify(condition) != JSON.stringify(otherCondition));
      } else {
        _this.otherConditions = [];
      }
    },
    // addRowStyle(index, style) {
    //   this.$set(this.rowStyle, index, { ...(this.rowStyle[index] || {}), ...style });
    // },
    // deleteRowStyleProps(index, props) {
    //   if(this.rowStyle[index]!=undefined ){
    //     if(props == undefined){

    //     }
    //     if(Array.isArray(props)){

    //     }
    //   }
    // },
    customRow(row, index) {
      let rowStyle = {};
      let rowStyleConfig = this.widget.configuration.rowStyle;
      if (rowStyleConfig != undefined && rowStyleConfig.enable && rowStyleConfig.bgStyleType == 'stripeStyle' && (index + 1) % 2 == 0) {
        let { backgroundColor, backgroundColorType } = rowStyleConfig;
        if (backgroundColor != undefined) {
          if (backgroundColorType == 'value') {
            rowStyle.backgroundColor = backgroundColor.startsWith('--w-') ? `var(${backgroundColor})` : backgroundColor;
          } else {
            // 运行函数
            try {
              let func = new Function('row', 'index', backgroundColor);
              rowStyle.backgroundColor = func(row, index);
            } catch (error) {
              console.error(error);
            }
          }
        }
      }

      if (this.rowStyle[index] != undefined) {
        Object.assign(rowStyle, this.rowStyle[index]);
      }
      if (typeof this.customRowStyle == 'function') {
        this.customRowStyle(rowStyle, row, index, this);
      }
      return {
        style: rowStyle,
        on: {
          // 事件
          click: event => {
            if (event.path == undefined || event.path[0].tagName != 'BUTTON') {
              // 排除操作按钮
              this.$emit('rowClick', { row, index });
            }
            this.onRowClick(event, row, index);
          },
          // dblclick: event => {},
          // contextmenu: event => {},
          mouseenter: event => {
            this.rowVarKey = 'HOVER';
            this.rowVar[this.rowVarKey + index] = true;
          }, // 鼠标移入行
          mouseleave: event => {
            this.rowVar[this.rowVarKey + index] = false;
            this.rowVarKey = null;
          }
        }
      };
    },

    onRowClick(event, row, index) {
      // 行单击选中
      if (this.configuration.keywordAdvanceSearchScope != 'no' && this.configuration.clickRowSelect) {
        if (this.configuration.rowSelectType == 'radio') {
          this.selectedRowKeys.splice(0, this.selectedRowKeys.length);
          this.selectedRows.splice(0, this.selectedRows.length);
        }
        let value = this.primaryColumnKey ? row[this.primaryColumnKey] : index;
        let i = this.selectedRowKeys.indexOf(value);
        if (i != -1) {
          this.selectedRowKeys.splice(i, 1);
          this.selectedRows.splice(i, 1);
        } else {
          this.selectedRowKeys.push(value);
          this.selectedRows.push(row);
        }
        this.$emit('onSelectRowChanged', {
          selectedRowKeys: this.selectedRowKeys,
          selectedRows: this.selectedRows
        });
      }

      if (this.configuration.rowClickEvent.enable) {
        for (let i = 0, len = this.configuration.rowClickEvent.eventHandlers.length; i < len; i++) {
          let eventHandler = this.configuration.rowClickEvent.eventHandlers[i];
          if (this.configuration.rowClickBehaviorLog && this.configuration.rowClickBehaviorLog.enable) {
            let calculateDataSource = {
              BUTTON_META_DATA: row,
              ...(this.widgetDependentVariableDataSource() || {})
            };
            this._logger.commitBehaviorLog({
              type: 'click',
              element: {
                tag: 'TR',
                text: '表格行点击'
              },
              page: {
                url: window.location.href,
                title: this.vPage != undefined ? this.vPage.title || document.title : undefined,
                id: this.vPage != undefined ? this.vPage.pageId || this.vPage.pageUuid : undefined
              },
              businessCode:
                this.configuration.rowClickBehaviorLog.businessCode && this.configuration.rowClickBehaviorLog.businessCode.value
                  ? executeJSFormula(this.configuration.rowClickBehaviorLog.businessCode.value, calculateDataSource)
                  : undefined,
              description:
                this.configuration.rowClickBehaviorLog.description && this.configuration.rowClickBehaviorLog.description.value
                  ? executeJSFormula(this.configuration.rowClickBehaviorLog.description.value, calculateDataSource)
                  : '行点击',
              extraInfo:
                this.configuration.rowClickBehaviorLog.extraInfo && this.configuration.rowClickBehaviorLog.extraInfo.value
                  ? executeJSFormula(this.configuration.rowClickBehaviorLog.extraInfo.value, calculateDataSource)
                  : undefined
            });
          }
          if (eventHandler.trigger === 'click') {
            eventHandler.meta = row;
            eventHandler.$evt = event;
            this.dispatchEventHandler(eventHandler);
          }
        }
      }
    },

    mouseenterCardListItem(item, index) {
      this.rowVarKey = 'HOVER_CARD_ITEM';
      this.rowVar[this.rowVarKey + index] = true;
    },
    mouseleaveCardListItem(item, index) {
      this.rowVar[this.rowVarKey + index] = false;
      this.rowVarKey = null;
    },
    getRowButtons() {
      if (this.widget.configuration.rowButton.enable) {
        let buttonMap = {};
        this.rowEndButton = { buttons: [], buttonGroup: this.widget.configuration.rowButton.buttonGroup };
        for (let i = 0, len = this.widget.configuration.rowButton.buttons.length; i < len; i++) {
          let btn = this.widget.configuration.rowButton.buttons[i];
          if (this.unauthorizedResource && this.unauthorizedResource.includes(btn.id)) {
            continue;
          }
          if (btn.displayColumnId && btn.displayPosition != 'rowEnd') {
            if (buttonMap[btn.displayColumnId] == undefined) {
              buttonMap[btn.displayColumnId] = {
                hoverRow: { buttons: [], buttonGroup: this.widget.configuration.rowButton.buttonGroup },
                underRow: { buttons: [], buttonGroup: this.widget.configuration.rowButton.buttonGroup }
              };
            }
            buttonMap[btn.displayColumnId][btn.displayPosition].buttons.push(btn);
            this.hasHoverRowButtons = true;
          }

          if (btn.displayPosition == 'rowEnd') {
            this.rowEndButton.buttons.push(btn);
          }
        }

        return buttonMap;
      }
      return null;
    },

    switchDisplayState() {
      if (this.displayState == 'cardList') {
        this.operationColWidth = this.latestOperationColWidth || 100;
        this.displayState = 'table';
      } else {
        this.displayState = 'cardList';
      }
      this.$nextTick(() => {
        this.setContentByDisplayState();
      });
    },

    eventSourceData() {
      return { wTableId: this.widget.id };
    },
    buttonDataBeforeInit(button, row, position) {
      this.invokeDevelopmentMethod('onButtonDataBeforeInit', { button, row, position });
    },

    columnRenderInvokeDevelopmentMethod() {
      this.invokeDevelopmentMethod.apply(this, Array.from(arguments));
    },
    buttonMounted($buttonsEl, position) {
      this.$nextTick(() => {
        if (position === 'rowEnd') {
          // 表格td的左右内边距12+20
          if (this.tdPadding == null) {
            this.tdPadding = 0;
            let padding = window.getComputedStyle($buttonsEl.parentElement.parentElement).padding;
            if (padding) {
              let paddingArr = padding.split(' ');
              if (paddingArr.length == 1 && paddingArr[0] != 'auto') {
                this.tdPadding = parseFloat(paddingArr[0]) * 2;
              } else if ((paddingArr.length == 2 || paddingArr.length == 3) && paddingArr[1] != 'auto') {
                this.tdPadding = parseFloat(paddingArr[1]) * 2;
              } else if (paddingArr.length == 4 && paddingArr[1] != 'auto' && paddingArr[3] != 'auto') {
                this.tdPadding = parseFloat(paddingArr[1]) + parseFloat(paddingArr[3]);
              }
            }
          }
          // 行末按钮列的宽度更新：
          if ($buttonsEl.clientWidth != undefined && $buttonsEl.clientWidth + this.tdPadding > this.operationColWidth) {
            var col = this.configuration.freezeColumn //&& this.rightFreezeColNum > 0
              ? this.$el.querySelector('.ant-table-scroll').querySelector('col:last-child')
              : this.$el.querySelector('col:last-child');
            if (this.tdPadding == null) {
              this.tdPadding = parseFloat(window.getComputedStyle($buttonsEl.parentElement.parentElement).padding);
            }
            col.style.width = $buttonsEl.clientWidth + this.tdPadding + 30 + 'px';
            this.operationColWidth = parseInt(col.style.width);
            this.latestOperationColWidth = this.operationColWidth;
            // 生产环境需更新对应列宽
            let operationColumn = this.columns.find(column => column.dataIndex == '__operation');
            if (operationColumn) {
              operationColumn.width = this.operationColWidth + 'px';
            }

            if (this.configuration.freezeColumn) {
              // && this.rightFreezeColNum > 0
              // 修改固定列表格的操作列
              let _col = this.$el.querySelector('.ant-table-fixed-right').querySelector('col:last-child');
              let width = $buttonsEl.clientWidth + this.tdPadding + 30 + 'px';
              _col.style.width = width;
              _col = this.$el.querySelector('.ant-table-fixed-right .ant-table-body-inner').querySelector('col:last-child');
              _col.style.width = width;
            }
            if (this.configuration.enableScrollY) {
              let _col = this.$el.querySelector('div.ant-table-body').querySelector('col:last-child');
              _col.style.width = $buttonsEl.clientWidth + this.tdPadding + 30 + 'px';
            }
          }
        }
      });
    },

    dispatchEventHandler(eventHandler) {
      eventHandler.pageContext = this.pageContext;
      eventHandler.$evtWidget = this;
      let developJs = typeof this.developJsInstance === 'function' ? this.developJsInstance() : this.developJsInstance;
      if (developJs == undefined) {
        developJs = {};
      }
      if (this.$pageJsInstance != undefined) {
        developJs[this.$pageJsInstance._JS_META_] = this.$pageJsInstance;
      }
      eventHandler.$developJsInstance = developJs;
      if (eventHandler.actionType) {
        new DispatchEvent(eventHandler).dispatch();
      } else if (eventHandler.customScript) {
        this.pageContext.executeCodeSegment(
          eventHandler.customScript,
          { meta: eventHandler.meta || {}, eventParams: eventHandler.eventParams || {} },
          this
        );
      }
    },
    onCustomColumnChanged(data) {
      if (this.designMode) {
        return null;
      }
      this.leftFreezeColNum = data.leftFreezeColNum;
      this.rightFreezeColNum = data.rightFreezeColNum;
      // 仅对列进行重排序、显隐设置，其他配置以实际页面配置为准
      let sortMap = {},
        i = 1;
      for (let o of data.customTableColumns) {
        sortMap[o.dataIndex] = {
          data: o,
          seq: i++
        };
      }
      this.setColumns(
        sortBy(this.widget.configuration.columns, function (o) {
          let item = sortMap[o.dataIndex];
          if (item) {
            o.hidden = item.data.hidden;
            return item.seq;
          } else {
            o.hidden = true;
          }
          return -1;
        })
      );

      if (data.fetched) {
        this.$nextTick(() => {
          const searchFormParams = this.getSearchFormParams(true);
          this.fetch(searchFormParams);
        });
      }

      setTimeout(() => {
        this.adjustScrollFixedTableWidth();
      }, 300);
    },
    // 根据虚拟出来的固定表格中实际固定列内容的宽度，调整滚动表格的固定列宽度
    adjustScrollFixedTableWidth() {
      // 没有表格组件时，不处理该方法
      if (!this.hasTableDisplayState || this.displayState == 'cardList') {
        return false;
      }
      if (
        this.widget.configuration.freezeColumn &&
        this.$el
        // &&((this.leftFreezeColNum != undefined && this.leftFreezeColNum > 0) ||(this.rightFreezeColNum != undefined && this.rightFreezeColNum > 0))
      ) {
        let tableWidth = this.$el.querySelector('.ant-table-wrapper').getBoundingClientRect().width;
        // 非固定区的自适应列
        let autoWidthCols = this.columns.filter(col => !col.fixed && !col.width && !col.hasOwnProperty('isColumns'));
        let groupColumnsAutoWidthWidthCols = this.inGroupColumns.filter(col => !col.width);
        let autoWidthColsLength = autoWidthCols.length + groupColumnsAutoWidthWidthCols.length;
        this.scrollX = this.columsScrollX + autoWidthColsLength * 200;
        if (this.rowEndButton.buttons && this.rowEndButton.buttons.length > 0) {
          this.scrollX += this.operationColWidth;
        }
        this.scrollX = Math.max(this.scrollX, tableWidth);
        // if (this.designMode) {
        //   this.scrollX += 600;
        // }
        let tableFixedLeft = this.$el.querySelector('.ant-table-fixed-left'),
          tableFixedRight = this.$el.querySelector('.ant-table-fixed-right'),
          scrollTable = this.$el.querySelector('.ant-table-scroll');
        if (tableFixedRight || tableFixedLeft) {
          // setTimeout(() => {
          let scrollTable = this.$el.querySelector('.ant-table');
          if (scrollTable && scrollTable.__vue__ && scrollTable.__vue__._provided.table) {
            scrollTable.__vue__._provided.table.syncFixedTableRowHeight();
            // scrollTable.__vue__._provided.table.store.fixedColumnsBodyRowsHeight = {};
          }
          // }, 200);
        }
      } else if (this.$el) {
        // 未冻结列情况下，设置横向滚动长度为初始化宽度（避免导致产生页面级滚动）
        let thead = this.$el && this.$el.querySelector('.ant-table-thead');
        let tbodyWidth = thead ? thead.clientWidth : this.$el.querySelector('.ant-table-wrapper').getBoundingClientRect().width;
        // 非固定区的自适应列
        let autoWidthCols = this.columns.filter(col => !col.width && !col.hasOwnProperty('isColumns'));
        let groupColumnsAutoWidthWidthCols = this.inGroupColumns.filter(col => !col.width);
        let autoWidthColsLength = autoWidthCols.length + groupColumnsAutoWidthWidthCols.length;
        let offsetWidth = tbodyWidth - this.columsScrollX - this.operationColWidth - autoWidthColsLength * 50;
        if (offsetWidth < 0) {
          this.scrollX = tbodyWidth;
          this.scrollX += autoWidthColsLength * 50;
        }
      }
    },

    // 重取数据
    refetch(options) {
      if (this.dataShowAsWaterfall) {
        if (typeof this.pagination !== 'boolean') {
          this.pagination.current = 1;
          this.rows = [];
        }
      } else if ((options === true || (options && options.force == true)) && this.pagination !== false) {
        this.pagination.current = 1;
      }
      if (options != undefined && options.params != undefined) {
        this.addDataSourceParams(options.params);
      }
      if (options != undefined && options.$evtWidget) {
        // 通过组件事件派发进来的逻辑
        if (options.eventParams) {
          this.addDataSourceParams(options.eventParams);
        }

        if (options.wEventParams) {
          // 修改默认查询条件
          if (options.wEventParams.defaultCondition != undefined) {
            this.dataSourceProvider.setDefaultCriterions([
              {
                sql: options.wEventParams.defaultCondition
              }
            ]);
          }
        }
      }

      this.fetch(options != undefined && options.$evtWidget ? options.eventParams || {} : options || {});
    },

    onClickSelectCheckbox(e, item) {
      let i = this.selectedRowKeys.indexOf(item[this.primaryColumnKey]);
      if (i != -1) {
        this.selectedRowKeys.splice(i, 1);
        this.selectedRows.splice(i, 1);
      } else {
        this.selectedRowKeys.push(item[this.primaryColumnKey]);
        this.selectedRows.push(item);
      }
    },

    emitSelectChange: debounce(function () {
      this.$emit('onSelect', {
        selectedRowKeys: this.selectedRowKeys,
        selectedRows: this.selectedRows
      });
      this.invokeDevelopmentMethod('onRowSelectChange', {
        selectedRowKeys: this.selectedRowKeys,
        selectedRows: this.selectedRows
      });
    }, 300),

    customFileUploadRequest(options) {
      return new Promise((resolve, reject) => {
        let file = options.file,
          fileSize = file.size,
          fileName = file.name,
          formData = new FormData();
        formData.set('frontUUID', file.uid);
        formData.set('localFileSourceIcon', '');
        formData.set('size', fileSize);
        let headers = {
          'Content-Disposition': `attachment; filename="${encodeURIComponent(fileName)}"`,
          'Content-Type': 'multipart/form-data'
        };
        formData.set('file', file);
        $axios
          .post('/proxy-repository/repository/file/mongo/savefilesChunk', formData, {
            headers: headers
          })
          .then(({ data }) => {
            if (data.code == 0 && data.data) {
              options.onSuccess();
              resolve(data.data[0]);
            }
          });
      });
    },
    customRequest(options, afterUpload) {
      this.importModalInfo.uploading = true;
      this.importModalInfo.importFile.fileID = undefined;
      this.importModalInfo.successCount = undefined;
      this.customFileUploadRequest(options).then(dbFile => {
        this.importModalInfo.uploading = false;
        this.importModalInfo.importFile.fileID = dbFile.fileID;
        this.importModalInfo.importFile.fileName = options.file.name;
        this.importModalInfo.importFile.File = options.file;
      });
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

    resizeObserverHandler() {
      this.adjustScrollFixedTableWidth();
      this.setContentByDisplayState();
    },
    changeShowKeywordSearch() {
      this.$nextTick(() => {
        if (this.designMode) {
          this.updateWidgetGridHeight();
        } else {
          this.setContentByDisplayState();
        }
      });
    },
    updateWidgetGridHeight() {
      if (this.colsEqualHeight) {
        const timer = setTimeout(() => {
          clearTimeout(timer);
          let widgetHeight = 0;
          for (let index = 0; index < this.$el.children.length; index++) {
            widgetHeight = widgetHeight + this.$el.children[index].offsetHeight;
          }
          this.pageContext.emitEvent(`widgetGrid:${this.WidgetGridVNode.widget.id}:children:rendered`, {
            wgt: this,
            widgetHeight,
            callBack: () => {
              this.setContentByDisplayState();
            }
          });
        }, 300);
      }
    },
    emitEventWidgetGrid() {
      if (!EASY_ENV_IS_BROWSER) {
        return;
      }
      this.checkWidgetGridNoticed = true;
      const findVNode = findParentVNodeByName(this, this.designMode ? 'EWidgetGrid' : 'WidgetGrid');
      this.WidgetGridVNode = findVNode;
      if (findVNode) {
        if (findVNode.widget.configuration.colsEqualHeight) {
          this.colsEqualHeight = true;

          let allColHasWidgets = true;
          for (let index = 0; index < findVNode.widget.configuration.cols.length; index++) {
            const col = findVNode.widget.configuration.cols[index];
            if (col.configuration && !col.configuration.widgets.length) {
              allColHasWidgets = false;
              break;
            }
          }
          if (allColHasWidgets) {
            const timer = setTimeout(() => {
              clearTimeout(timer);
              // let widgetHeight = this.$el.querySelector('.ant-table').offsetHeight;
              // if (this.rows.length) {
              //   widgetHeight += 65;
              // }
              let widgetHeight = this.$el.offsetHeight;
              // const elStyle = window.getComputedStyle(this.$el);
              // widgetHeight = widgetHeight + parseFloat(elStyle.marginBottom);
              this.pageContext.emitEvent(`widgetGrid:${findVNode.widget.id}:children:rendered`, {
                wgt: this,
                widgetHeight,
                callBack: () => {
                  this.setContentByDisplayState();
                }
              });
            }, 300);
          } else {
            if (!this.designMode) {
              this.setContentByDisplayState();
            }
          }
        } else {
          this.setContentByDisplayState();
        }
      } else {
        this.setContentByDisplayState();
      }
    },
    setContentByDisplayState() {
      if (!EASY_ENV_IS_BROWSER) {
        return;
      }
      if (this.configuration.fixedTableHeader || this.configuration.fixedPagination) {
        if (this.displayStateTable) {
          this.setTableContent();
        } else if (this.displayStateCardList) {
          this.setCardContent();
        }
      }
    },
    setFixedPaginationFromParent(el) {
      let needSetHeight = false;
      if (this.parent && this.parent.wtype && this.parent.configuration) {
        if (this.parent.configuration.height && this.parent.configuration.height !== 'auto') {
          needSetHeight = true;
        } else if (this.parent.wtype == 'GridCol') {
          const findVNode = findParentVNodeByName(this, 'WidgetGrid');
          if (findVNode && findVNode.widgetHeight && findVNode.widgetHeight !== 'auto') {
            needSetHeight = true;
          }
        }
      }
      if (this.colsEqualHeight) {
        const { maxHeight } = getElSpacingForTarget(el, el.closest('.widget-table'));
        el.style.cssText += `;overflow-y:auto; height:${maxHeight}px`;
      } else {
        if (needSetHeight) {
          const { maxHeight } = getElSpacingForTarget(el, el.closest(`#${this.parent.id}`));
          el.style.cssText += `;overflow-y:auto; height:${maxHeight}px`;
        }
      }
    },
    setTableContent(el = this.$el.querySelector('.ant-table-content')) {
      if (!el) return;
      if (this.configuration.scrollCalculateContainer === 'viewport') {
        this.setTableContentFromViewport(el);
      } else {
        this.setFixedPaginationFromParent(el);
      }
    },
    setTableContentFromViewport(el) {
      if (!el) return;
      const { maxHeight, totalBottom, totalNextSibling } = getElMaxHeightFromViewport(el);
      console.log(maxHeight);
      const setFixedTableHeader = () => {
        let tableTheadEl = el.querySelector('.ant-table-body .ant-table-thead');
        if (!tableTheadEl) {
          tableTheadEl = el.querySelector('.ant-table-header .ant-table-thead');
        }
        if (!tableTheadEl) {
          this.displayTableContentHeight = maxHeight;
          // this.$set(this.widget.configuration, 'scrollY', maxHeight);
          // this.$set(this.widget.configuration, 'enableScrollY', true);
        } else {
          const tableTheadStyle = window.getComputedStyle(tableTheadEl);
          const scrollY =
            maxHeight -
            tableTheadEl.offsetHeight -
            (parseFloat(tableTheadStyle.marginTop) || 0) -
            (parseFloat(tableTheadStyle.marginBottom) || 0);
          this.displayTableContentHeight = scrollY;
          // this.$set(this.widget.configuration, 'scrollY', scrollY);
          // this.$set(this.widget.configuration, 'enableScrollY', true);
        }
      };
      const setFixedPagination = () => {
        el.style.cssText += `;overflow-y:auto; height:${maxHeight}px`;
      };
      if (this.configuration.freezeColumn) {
        if (this.configuration.fixedTableHeader) {
          setFixedTableHeader();
        }
      } else {
        if (this.configuration.fixedTableHeader) {
          setFixedTableHeader();
        }
        if (this.configuration.fixedPagination) {
          setFixedPagination();
        }
      }
    },
    setCardContent(el = this.$el.querySelector('.widget-table-card-list')) {
      if (!el) return;
      if (this.configuration.fixedPagination) {
        if (this.configuration.scrollCalculateContainer === 'viewport') {
          this.setCardFixedPaginationFromViewport(el);
        } else {
          this.setFixedPaginationFromParent(el);
        }
      }
    },
    setCardFixedPaginationFromViewport(el) {
      if (!el) return;
      const { maxHeight } = getElMaxHeightFromViewport(el);
      el.style.cssText += `;height:${maxHeight}px`;
      const spinContainerEl = el.querySelector('.ant-spin-container');
      const paginationEl = el.querySelector('.ant-list-pagination');
      const paginationStyle = window.getComputedStyle(paginationEl);
      const scrollY =
        maxHeight -
        paginationEl.offsetHeight -
        (parseFloat(paginationStyle.marginTop) || 0) -
        (parseFloat(paginationStyle.marginBottom) || 0);
      spinContainerEl.style.cssText += `;overflow-y:auto; height:${scrollY - 1}px`;
      this.$refs.cardListScroll.update();
    },
    execDynamicVariables() {
      if (this.widget.configuration.defaultCondition) {
        const regex = /\$\{([^}]+)\}/g;
        let match;
        while ((match = regex.exec(this.widget.configuration.defaultCondition)) !== null) {
          this.defaultConditionHasVariable = true;
          this.dynamicVariables.push(match[1]);
        }
      }
    },
    // 获取搜索表单参数
    getSearchFormParams(init) {
      return new Promise((resolve, reject) => {
        let searchFormParams = {};
        if (this.$refs.searchForm) {
          if (init) {
            //初始时，高级搜索可能存在默认值
            if (this.$refs.searchForm.hasDefaultValue && this.$refs.searchForm.enableAdvanceSearch) {
              searchFormParams = this.$refs.searchForm.getSearchCriterionParams(init);
              if (searchFormParams.criterions.length == 0) {
                setTimeout(() => {
                  searchFormParams = this.$refs.searchForm.getSearchCriterionParams(init);
                  resolve(searchFormParams);
                }, 300);
                return false;
              }
            }
          } else {
            searchFormParams = this.$refs.searchForm.getSearchCriterionParams();
          }
        }
        resolve(searchFormParams);
      });
    },
    getSelectedRowColValueString(col, separator = ',') {
      if (this.getSelectedRows().length > 0) {
        let colValues = [];
        for (let r of this.getSelectedRows()) {
          colValues.push(r[col] || '');
        }
        return colValues.join(separator);
      }
      return '';
    }
  },
  beforeMount() {
    this.execDynamicVariables();
  },
  async mounted() {
    if (this.widget.configuration.tbodyMinHeight) {
      this.$el.querySelector('.ant-table-content').setAttribute('style', 'min-height:' + this.widget.configuration.tbodyMinHeight + 'px');
    }
    if (!this.designMode) {
      let _this = this;
      // 跨页面处理
      this.pageContext.handleCrossTabEvent(`${this.widget.id}:refetch`, function (options) {
        _this.refetch(options);
      });
      this.pageContext.handleCrossTabEvent(`${this.namespace}:${this.widget.id}:refetch`, function (options) {
        _this.refetch(options);
      });
      // 事件传入的参数
      if (this.$event && this.$event.eventParams) {
        this.addDataSourceParams(this.$event.eventParams);
      }

      const searchFormParams = await this.getSearchFormParams(true);

      if (!this.widget.configuration.enableCustomTable) {
        if (this.widget.configuration.enableBeforeTableHeaderWidget && this.widget.configuration.beforeTableHeaderWidgets.length) {
          if (this.$refs.headerWidgets && this.$refs.headerWidgets.length) {
            let headerWidget = this.$refs.headerWidgets[0];
            if (headerWidget.hasOwnProperty('wTemplate') && !headerWidget.wTemplate) {
              headerWidget.templateWidgetMounted = templateWidget => {
                this.fetch(searchFormParams);
              };
            } else {
              this.fetch(searchFormParams);
            }
          } else {
            this.fetch(searchFormParams);
          }
        } else {
          this.fetch(searchFormParams);
        }
      }
    }

    if (this.widget.configuration.enableScrollY && this.dataShowAsWaterfall && this.widget.configuration.scrollY) {
      let _this = this;
      this.$el.querySelector('.ant-table-body') &&
        this.$el.querySelector('.ant-table-body').addEventListener('scroll', e => {
          if (e.target.scrollTop + e.target.clientHeight == e.target.scrollHeight) {
            _this.loadMore();
          }
        });
    }

    if (!this.widget.configuration.freezeColumn && this.$el.querySelector('.ant-table-thead')) {
      // 未冻结列情况下，设置横向滚动长度为初始化宽度（避免导致产生页面级滚动）
      let tbodyWidth = this.$el.querySelector('.ant-table-thead').clientWidth,
        tableClientWidth = this.$el.clientWidth;
      if (tbodyWidth > tableClientWidth) {
        this.scrollX = tableClientWidth;
      }
    }

    if (!this.designMode) {
      // 监听表格高度变化
      let tbody = this.$el.querySelector('.ant-table-scroll .ant-table-body');
      if (tbody) {
        new ResizeObserver(
          this.resizeObserverHandler
          // entries => {
          //   this.adjustScrollFixedTableWidth();
          // }
        ).observe(this.$el.querySelector('.ant-table-scroll .ant-table-body'));
      }
    } else {
      this.adjustScrollFixedTableWidth();
    }
    if (this.designMode) {
      this.emitEventWidgetGrid();
    }
  },

  beforeDestroy() {
    this.pageContext.offEvent(`WidgetTable:${this.widget.id}:Refresh`);
  },
  updated() {},

  watch: {
    'widget.configuration': {
      deep: true,
      handler(v, o) {
        // 用户页面端可以通过变更配置重新渲染列定义
        this.setColumns();

        if (this.designMode) {
          // 设计模式情况下，默认视图变更
          this.displayState = v.defaultDisplayState;
        }
      }
    },
    selectedRowKeys: {
      handler() {
        this.emitSelectChange();
      }
    },
    rows: {
      deep: true,
      handler(v) {
        console.log('rows change', v.length);
      }
    }
  },
  // 组件元数据
  META: {
    // 对设计器暴露的方法集合
    method: {
      refetch: '重新加载数据'
    }
  }
};
</script>
