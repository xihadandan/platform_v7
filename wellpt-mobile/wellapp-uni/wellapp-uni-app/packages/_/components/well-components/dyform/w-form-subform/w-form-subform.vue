<template>
  <view class="w-form-subform" :class="widgetClass" v-show="deviceVisible">
    <uni-collapse class="w-form-subform-collapse" ref="collapse" v-model="collapseValue" @change="collapseChange">
      <uni-collapse-item
        ref="collapseItem"
        :border="false"
        :disabled="collapseDisabled"
        :show-arrow="!!widget.configuration.isCollapse"
      >
        <view
          slot="title"
          class="collapse-item-title"
          v-if="!!widget.configuration.isCollapse || widget.configuration.title"
        >
          <view class="label">
            <text class="text" v-if="widget.configuration.title">{{ $t("title", widget.configuration.title) }}</text>
            <text class="text" v-else>{{ $t("WidgetSubform.subform", "从表") }}</text>
          </view>
        </view>
        <SubformSearch
          ref="subformSearch"
          :widget="widget"
          :fields="fields"
          v-if="widget.configuration.search != undefined && fields.length > 0"
          @reset="onResetSearch"
          @onFilteredRowKeys="onFilteredRowKeys"
        />
        <!-- 头部按钮 -->
        <view style="margin-bottom: 8px" class="flex">
          <view
            class="subform-checkbox"
            style="margin-right: var(--w-margin-2xs); margin-top: -2px"
            v-if="
              widget.configuration.rowSelectType == 'checkbox' &&
              widget.configuration.uniConfiguration.layout == 'form-inline' &&
              rows.length
            "
          >
            <table-checkbox
              :checked="checkAll"
              :indeterminate="selectIndeterminate"
              @checkboxSelected="onSelectAllRow"
              ref="checkAllRef"
            />
            <view class="subform-checkbox-text" style="white-space: nowrap" @click="onClickCheckAllText">
              {{ checkAll ? $t("global.cancelSelectAll", "取消全选") : $t("global.selectAll", "全选") }}
            </view>
          </view>
          <SubformButton
            v-if="showHeaderButtons"
            position="tableHeader"
            :button="widget.configuration.headerButton"
            :buttonPredicate="headButtonPredicate"
            @button-click="onSubformButtonClick"
            :visibleJudgementData="vTableButtonVisibleJudgementData"
            :parentWidget="getSelf"
          >
          </SubformButton>
        </view>
        <!-- 表格 -->
        <view class="table" v-if="widget.configuration.uniConfiguration.layout == 'table'">
          <uni-w-table
            class="subform-table"
            ref="table"
            :type="widget.configuration.rowSelectType !== 'no' ? widget.configuration.rowSelectType : ''"
            :data="rows"
            :rowKey="rowKey"
            :defaultSelectedRowKeys="defaultSelectedRowKeys"
            :fixed="tableFixedOption"
            @selection-change="selectionChange"
          >
            <uni-w-thead>
              <uni-w-th width="50" v-if="widget.configuration.addSerialNumber">
                {{ $t("WidgetSubform.column.serialNumber", "序号") }}
              </uni-w-th>
              <template v-for="(column, c) in columns">
                <uni-w-th
                  class="th"
                  :width="column.width"
                  :align="column.titleAlign"
                  :ellipsis="column.ellipsis"
                  :key="'th' + column.dataIndex"
                  :sortable="column.sortable.enable"
                  @sort-change="column.sorter"
                >
                  <text v-if="column.required" class="is-required">*</text>{{ column.title }}
                </uni-w-th>
              </template>
              <uni-w-th v-if="showRowOperationButtons"> {{ $t("WidgetSubform.column.operation", "操作") }}</uni-w-th>
            </uni-w-thead>
            <uni-w-tbody>
              <uni-w-tr
                v-for="(row, r) in showRows"
                :key="'row_' + row[rowKey]"
                :keyValue="row.$$id"
                @click="showDetail(row.$$id)"
              >
                <uni-w-td v-if="widget.configuration.addSerialNumber">
                  <template v-if="!widget.configuration.pageResetSerialNumber && hasPagination">
                    {{ (pagination.current - 1) * pagination.pageSize + (r + 1) }}
                  </template>
                  <template v-else>
                    {{ r + 1 }}
                  </template>
                </uni-w-td>
                <uni-w-td
                  v-for="(column, c) in columns"
                  :align="column.contentAlign"
                  :ellipsis="column.ellipsis"
                  :key="'td_' + column.dataIndex + '_' + row[rowKey]"
                >
                  <SubformCell
                    v-if="rowForms[row[rowKey]]"
                    class="subform-cell"
                    :ref="row[rowKey] + '_cell_' + column.dataIndex + '_Ref'"
                    :widget="column.widget"
                    :form="rowForms[row[rowKey]]"
                    :widgetSubform="widget"
                    :parentWidget="getSelf"
                    :row="row"
                    :option="column"
                    :rowIndex="r"
                    :rowKey="row[rowKey]"
                    :visibleJudgementData="vTableButtonVisibleJudgementData"
                    @mounted="($evt) => onCellComponentMounted($evt, row, column.dataIndex)"
                    @button-click="(e, button) => onSubformButtonClick(e, button, row, r, column)"
                  ></SubformCell>
                  <!-- {{ getColumnDisplayValue(row, column.dataIndex) }} -->
                </uni-w-td>
                <uni-w-td v-if="showRowOperationButtons">
                  <SubformButton
                    position="tableRowEnd"
                    :button="filterRowEndButton"
                    @button-click="(e, button) => onSubformButtonClick(e, button, row, r)"
                    :buttonPredicate="(button) => rowEndButtonPredicate(button, r, row)"
                    :visibleJudgementData="vTableButtonVisibleJudgementData"
                    :parentWidget="getSelf"
                  />
                </uni-w-td>
              </uni-w-tr>
            </uni-w-tbody>
          </uni-w-table>
          <!-- 表格底部区域 -->
          <view
            v-if="
              widget.configuration.enableFooter &&
              widget.configuration.footerWidgets != undefined &&
              widget.configuration.footerWidgets.length > 0
            "
            class="subform-table-footer"
          >
            <template v-for="(footerWgt, ii) in widget.configuration.footerWidgets">
              <widget
                :widget="footerWgt"
                :parent="widget"
                :widgetsOfParent="widget.configuration.footerWidgets"
                :key="'footer_wgt_' + footerWgt.id"
              >
              </widget>
            </template>
          </view>
        </view>
        <!-- 卡片 -->
        <view class="card" v-else-if="widget.configuration.uniConfiguration.layout == 'form-inline'">
          <!-- 左滑操作 -->
          <!-- 左滑按钮点击失效问题，详情查看w-list-view描述 -->
          <uni-swipe-action>
            <uni-swipe-action-item
              :autoClose="false"
              class="subform-card"
              :threshold="0"
              :key="row.$$id"
              v-for="(row, r) in showRows"
            >
              <!-- 左滑按钮 -->
              <template v-slot:right v-if="showRowOperationButtons">
                <SubformButton
                  position="tableRowEnd"
                  isSwipeButton
                  :button="widget.configuration.rowButton"
                  @button-click="(e, button) => onSubformButtonClick(e, button, row, r)"
                  :buttonPredicate="(button) => rowEndButtonPredicate(button, r, row)"
                  :visibleJudgementData="vTableButtonVisibleJudgementData"
                  :parentWidget="getSelf"
                />
              </template>
              <view
                :class="{
                  'subform-card-item': true,
                  'item-collapsed': cardRowsExpandMap.indexOf(row[rowKey]) == -1,
                }"
              >
                <view class="subform-card-item__header" @click="showDetail(row.$$id)">
                  <view class="subform-card-item__serial-number" v-if="widget.configuration.addSerialNumber">
                    <template v-if="!widget.configuration.pageResetSerialNumber && hasPagination">
                      {{ (pagination.current - 1) * pagination.pageSize + (r + 1) }}
                    </template>
                    <template v-else>
                      {{ r + 1 }}
                    </template>
                  </view>
                  <view class="subform-card-item__title">
                    <SubformCell
                      v-if="rowForms[row[rowKey]] && columnMap[formInlineTitleField]"
                      class="subform-cell"
                      :ref="row[rowKey] + '_cell_' + formInlineTitleField + '_Ref'"
                      :widget="columnMap[formInlineTitleField].widget"
                      :form="rowForms[row[rowKey]]"
                      :widgetSubform="widget"
                      :parentWidget="getSelf"
                      :row="row"
                      :option="columnMap[formInlineTitleField]"
                      :rowIndex="r"
                      :rowKey="row[rowKey]"
                      :visibleJudgementData="vTableButtonVisibleJudgementData"
                      @mounted="($evt) => onCellComponentMounted($evt, row, formInlineTitleField)"
                      @button-click="
                        (e, button) => onSubformButtonClick(e, button, row, r, columnMap[formInlineTitleField])
                      "
                    ></SubformCell>
                  </view>
                  <view v-if="widget.configuration.rowSelectType && widget.configuration.rowSelectType !== 'no'">
                    <table-checkbox
                      :checked="selectedRowKeys.includes(row[rowKey])"
                      @checkboxSelected="onSelectRow(row)"
                      :radio="widget.configuration.rowSelectType == 'radio'"
                    />
                  </view>
                </view>
                <view class="subform-card-item__column" @click="showDetail(row.$$id)">
                  <template v-for="(column, c) in columns">
                    <view
                      class="column"
                      :key="'col_' + row[rowKey] + '_' + column.dataIndex"
                      v-if="column.dataIndex !== formInlineTitleField"
                      v-show="
                        cardRowsExpandMap.indexOf(row[rowKey]) > -1 ||
                        (cardRowsExpandMap.indexOf(row[rowKey]) == -1 && c < showColumnNum)
                      "
                    >
                      <view class="row-label">{{ columnMap[column.dataIndex].title }}</view>
                      <view class="row-content">
                        <SubformCell
                          v-if="rowForms[row[rowKey]]"
                          class="subform-cell"
                          :ref="row[rowKey] + '_cell_' + column.dataIndex + '_Ref'"
                          :widget="columnMap[column.dataIndex].widget"
                          :form="rowForms[row[rowKey]]"
                          :widgetSubform="widget"
                          :parentWidget="getSelf"
                          :row="row"
                          :option="columnMap[column.dataIndex]"
                          :rowIndex="r"
                          :rowKey="row[rowKey]"
                          :visibleJudgementData="vTableButtonVisibleJudgementData"
                          @mounted="($evt) => onCellComponentMounted($evt, row, column.dataIndex)"
                          @button-click="
                            (e, button) => onSubformButtonClick(e, button, row, r, columnMap[column.dataIndex])
                          "
                        ></SubformCell>
                      </view>
                    </view>
                  </template>
                </view>
                <view
                  class="subform-card-item__arrow"
                  v-if="columns.length - 1 > (widget.configuration.uniConfiguration.mobileShowColumnNum || 3)"
                  @click="cardRowsExpandMapChange(row[rowKey])"
                >
                  <w-icon icon="iconfont icon-ptkj-xianmiaojiantou-xia" :size="12"></w-icon>
                </view>
              </view>
            </uni-swipe-action-item>
          </uni-swipe-action>
          <!-- 暂无数据 -->
          <uni-w-empty noImage v-if="!filteredRows.length" />
        </view>
        <view v-else-if="widget.configuration.uniConfiguration.layout == 'list-item'"> </view>
        <!-- 分页 -->
        <view v-if="hasPagination" class="flex pagination" v-show="showPagination">
          <view class="pagination-text f_s_0" v-if="widget.configuration.pagination.showTotalPage">{{
            $t("global.totalRecord", { total: pagination.total }, `共${pagination.total}条记录`)
          }}</view>
          <uni-pagination
            class="f_g_1"
            :total="pagination.total"
            showIcon
            :prev-text="$t('global.prevPage', '上一页')"
            :next-text="$t('global.nextPage', '下一页')"
            v-model="pagination.current"
            :pageSize="pagination.pageSize"
            @change="paginationChange"
          />
        </view>
      </uni-collapse-item>
    </uni-collapse>
    <!-- 详情 -->
    <uni-popup ref="detail" type="right" safeArea backgroundColor="#fff">
      <view style="width: 100vw">
        <uni-nav-bar
          :shadow="false"
          :border="false"
          left-icon="closeempty"
          class="edit-subform-row-navbar"
          :title="
            isEdit
              ? currentSelectedForm.formData && currentSelectedForm.formData.$$id == null
                ? $t('WidgetSubform.button.new', '新增')
                : $t('WidgetSubform.button.editRow', '编辑')
              : $t('WidgetSubform.button.view', '查看详情')
          "
          :right-text="isEdit ? $t('WidgetSubform.button.save', '保存') : ''"
          @clickLeft="closePopup('detail')"
          @clickRight="onConfirmEditRow"
          color="var(--w-text-color-mobile)"
          style="margin-top: var(--status-bar-height)"
        />
        <view style="padding: 8px 0; background-color: #f5f6f8">
          <scroll-view
            v-if="rowFormRendered"
            scroll-y="true"
            style="height: calc(100vh - var(--status-bar-height) - 60px)"
          >
            <!-- labelPosition: 'top',
                labelWidth: '100%',
                formDefinition, -->
            <w-dyform
              :options="{
                inheritForm: currentSelectedForm,
                formUuid: widget.configuration.formUuid,
                isSubform: true,
                formElementRules: isEdit ? undefined : this.detialFormElementRules,
                formDatas: currentSelectedFormDatas,
                displayState: isEdit ? 'edit' : 'label',
                dataUuid:
                  currentSelectedForm.formData && currentSelectedForm.formData.$$id == null
                    ? null
                    : (currentSelectedForm.formDatas && currentSelectedForm.formDatas.dataUuid) ||
                      (currentSelectedForm.formData && currentSelectedForm.formData.$$id),
              }"
              ref="subDyform"
              @initSuccess="onSubDyformRefInitSuccess"
            />
          </scroll-view>
        </view>
      </view>
    </uni-popup>
    <!-- 代理行数据组件 -->
    <uni-forms :model="proxyForm.formData" ref="proxyFormRef" style="display: none" v-if="renderProxyForm">
      <template v-for="(w, i) in columns">
        <widget
          :widget="w.widget"
          :isSubformCell="true"
          :form="proxyForm"
          :unValidateWidget="true"
          :formModelItemProp="[formUuid, w.widget.configuration.code]"
          @change="(e) => onProxyWidgetChange(e, w.widget)"
          :ref="'proxyField_' + w.widget.configuration.code"
        />
      </template>
    </uni-forms>
  </view>
</template>
<script type="text/babel">
import { mapState } from "vuex";
import formElement from "../w-dyform/form-element.mixin";
import SubformButton from "./component/subform-button.vue";
import SubformCell from "./component/subform-cell.vue";
import SubformSearch from "./component/subform-search.vue";
import { debounce, each, findIndex, assign } from "lodash";
import TableCheckbox from "./component/table-checkbox.vue";
const framework = require("wellapp-uni-framework");
export default {
  mixins: [formElement],
  props: {},
  components: {
    "w-dyform": () => import("../w-dyform/w-dyform.vue"),
    SubformButton,
    TableCheckbox,
    SubformCell,
    SubformSearch,
  },
  provide() {
    return {
      widgetSubformContext: this,
    };
  },
  computed: {
    ...mapState(["returnRefresh"]),
    subformWidgetIsReady() {
      return this.isMounted && this.initedColumn && this.initedDefaultSubformRows;
    },
    defaultVisibleJudgementData() {
      return {
        ...this._vShowByDateTime,
        ...this._vShowByUserData,
        ...this._vShowByWorkflowData,
        ...(this.dyform != undefined ? { __DYFORM__: { editable: this.dyform.displayState == "edit" } } : {}),
        _URL_PARAM_: this.vUrlParams,
      };
    },
    vTableButtonVisibleJudgementData() {
      let __TABLE__ = {};
      __TABLE__.selectedRowCount = this.selectedRowKeys.length;
      return {
        MAIN_FORM_DATA: this.form.formData,
        ...this.defaultVisibleJudgementData,
        __TABLE__,
      };
    },
    rowKeys() {
      return this.rows.map((row) => row.$$id);
    },
    columnMap() {
      let map = {};
      for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
        map[this.widget.configuration.columns[i].dataIndex] = this.widget.configuration.columns[i];
      }
      return map;
    },
    colIsLabelValWidget() {
      let result = {};
      for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
        let col = this.widget.configuration.columns[i];
        result[col.dataIndex] = col.widget.configuration.isLabelValueWidget;
      }
      return result;
    },
    selectIndeterminate() {
      return this.selectedRowKeys.length > 0 && this.selectedRowKeys.length < this.rows.length;
    },
    // 有分页
    hasPagination() {
      let hasPagination = this.widget.configuration.pagination && this.widget.configuration.pagination.enable;
      return hasPagination;
    },
    showPagination() {
      // 无数据，不显示页码
      if (this.filteredRows.length == 0) {
        return false;
      }
      if (this.pagination.totalPage == 0) {
        return false;
      }
      if (this.widget.configuration.pagination.hideOnSinglePage && this.pagination.totalPage == 1) {
        return false;
      }
      return this.hasPagination;
    },
    tableFixedOption() {
      let option = {
        prefix: 0,
        suffix: this.showRowOperationButtons ? 1 : 0,
      };
      if (this.widget.configuration.uniConfiguration.layout == "table") {
        if (this.widget.configuration.uniConfiguration.fixedSerialNumber) {
          if (this.widget.configuration.addSerialNumber) {
            option.prefix++;
          }
        }
        if (this.widget.configuration.rowSelectType && this.widget.configuration.rowSelectType !== "no") {
          option.prefix++;
        }
      }
      return option;
    },
    // 是否有按钮显示规则
    hasButtonRules() {
      return (
        this.form.formElementRules &&
        this.form.formElementRules[this.widget.id] &&
        this.form.formElementRules[this.widget.id].buttons
      );
    },
    showHeaderButtons() {
      if (this.hasButtonRules) {
        // 从流程过来的按钮显示规则
        return this.widget.configuration.headerButton.buttons.length > 0;
      }
      return this.widget.configuration.headerButton.buttons.length > 0 && this.editable;
    },
    filterRowEndButton() {
      let rowButton = this.widget.configuration.rowButton,
        filteredRowButton = JSON.parse(JSON.stringify(rowButton));
      filteredRowButton.buttons = [];
      for (let i = 0, len = rowButton.buttons.length; i < len; i++) {
        if (
          rowButton.buttons[i].defaultVisible &&
          rowButton.buttons[i].position !== "underColumn" &&
          rowButton.buttons[i].id !== "viewRow"
        ) {
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
          rowButton.buttons[i].position === "underColumn" &&
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
      if (!this.editable) {
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
    formInlineTitleField() {
      if (
        this.widget.configuration.uniConfiguration.layout == "form-inline" &&
        this.columns &&
        this.columns.length &&
        !this.widget.configuration.formInlineTitleField
      ) {
        return this.columns[0].dataIndex;
      }
      return this.widget.configuration.formInlineTitleField;
    },
    showColumnNum() {
      let num = this.widget.configuration.uniConfiguration.mobileShowColumnNum || 3;
      let hasIndex = findIndex(this.columns, (item) => {
        return item.dataIndex == this.formInlineTitleField;
      });
      if (hasIndex < num) {
        num++;
      }
      return num;
    },
    defaultEvents() {
      return [
        {
          id: "setColumnVisible",
          title: "设置列为显示或者隐藏",
        },
        {
          id: "setColumnEditable",
          title: "设置列为编辑或者不可编辑",
        },
        {
          id: "setColumnRequired",
          title: "设置列为必填或者不必填",
        },
      ];
    },
    filteredRows() {
      if (this.searchKey == undefined) {
        return this.rows;
      }
      let rows = [];
      for (let i = 0, len = this.rows.length; i < len; i++) {
        if (
          this.filteredRowKeys.includes(this.rows[i][this.rowKey]) ||
          !this.waitSearchRowKeys.includes(this.rows[i][this.rowKey])
        ) {
          rows.push(this.rows[i]);
          continue;
        }
      }
      return rows;
    },
  },
  data() {
    if (!this.widget.configuration.hasOwnProperty("uniConfiguration")) {
      this.$set(this.widget.configuration, "uniConfiguration", {
        layout: "form-inline",
        accordion: true,
        mobileShowColumnNum: 3,
      });
    }
    let collapseValue = ["0"];
    if (this.widget.configuration.isCollapse && this.widget.configuration.defaultCollapse) {
      collapseValue = []; //默认折叠
    }
    return {
      collapseDisabled: !this.widget.configuration.isCollapse,
      collapseValue,
      formUuid: this.widget.configuration.formUuid, //从表的表单UUID
      formElementRules: {},
      detialFormElementRules: {}, // 查看详情，显示文本
      columns: [],
      rows: [],
      rowForms: {},
      formDefinitionVjson: undefined,
      formDefinition: {
        widgets: [],
        tableName: this.widget.configuration.tableName,
      },
      fields: [],
      currentForm: null,
      rowFormRendered: false,
      popupRef: [],
      rowKey: "$$id",
      defaultSelectedRowKeys: [],
      selectedRowKeys: [],
      selectedRows: [],
      subformDefaultData: {},
      rowKeyMap: {},
      rowDataAsMD5: "-1",
      checkAll: false,
      editRow: undefined,
      editRowIndex: undefined,
      showRows: [], //实际显示的数据
      pagination: {
        current: 1,
        total: 0,
        pageSize: this.widget.configuration.pagination.pageSize || 5,
        totalPage: 1,
      },
      fieldWaitPreload: [],
      subformJsModule: undefined,
      isEdit: true,
      currentSelectedForm: {},
      currentSelectedFormDatas: {},
      cardRowsExpandMap: [], //折叠状态
      renderProxyForm: false,
      initedColumn: false,
      initedDefaultSubformRows: false,
      searchKey: undefined,
      rowDataAsMD5: this.widget.id,
      waitSearchRowKeys: [],
      filteredRowKeys: [],
    };
  },
  beforeCreate() {},
  created() {
    this.currentSelectedForm = {};
    this.currentSelectedFormDatas = {};
    this.proxyFormWidgets = [];
    this.proxyForm = this.dyform.createTempDyform(this.formUuid, framework.utils.generateId());
    this.proxyForm.formId = this.widget.configuration.formId;
    this.proxyForm.tableName = this.widget.configuration.tableName; // 表名
    this.emitRowFormDataChange = debounce(this.emitRowFormDataChange.bind(this), 500);
    // 从表的字段规则：由外部定义传入
    if (
      this.form.formElementRules &&
      this.form.formElementRules[this.widget.id] &&
      this.form.formElementRules[this.widget.id].children
    ) {
      Object.assign(this.formElementRules, this.form.formElementRules[this.widget.id].children);
      if (this.form.formElementRules[this.widget.id].buttons) {
        // 按钮设置
        each(this.widget.configuration.headerButton.buttons, (item) => {
          if (this.form.formElementRules[this.widget.id].buttons.headerButton.indexOf(item.code || item.id) > -1) {
            item.defaultVisible = true;
          } else {
            item.defaultVisible = false;
          }
        });
        each(this.widget.configuration.rowButton.buttons, (item) => {
          if (this.form.formElementRules[this.widget.id].buttons.rowButton.indexOf(item.code || item.id) > -1) {
            item.defaultVisible = true;
          } else {
            item.defaultVisible = false;
          }
        });
      }
    }
    this.forceUpdateEvent = debounce(this.forceUpdateEvent.bind(this), 500);
  },
  beforeMount() {},
  mounted() {
    if (this.formUuid) {
      this.form.initSubform(this.formUuid);
      this.form.$subform[this.widget.id] = this;
      this.form.$subform[this.widget.configuration.formId] = this;
    }
    this.updateLatestColumnInfo().then(() => {
      if (this.widgetDyformContext.originFormData[this.formUuid] != undefined) {
        // 初始化数据
        for (let row of this.widgetDyformContext.originFormData[this.formUuid]) {
          this.addRow(row);
        }
      }
    });
    this.subformViewShowChange();
  },
  methods: {
    selectionChange({ detail }) {
      this.selectedRowKeys = detail.keys;
      this.selectedRows = detail.value;
      console.log(detail);
    },
    onClickCheckAllText() {
      this.$refs.checkAllRef && this.$refs.checkAllRef.selected();
    },
    onSelectAllRow() {
      this.checkAll = !this.checkAll;
      this.selectedRowKeys.splice(0, this.selectedRowKeys.length);
      this.selectedRows.splice(0, this.selectedRows.length);
      if (this.checkAll) {
        for (let row of this.rows) {
          this.selectedRowKeys.push(row[this.rowKey]);
          this.selectedRows.push(row);
        }
      }
    },
    onSelectRow(row) {
      if (this.widget.configuration.rowSelectType == "radio") {
        let checked = this.selectedRowKeys.includes(row[this.rowKey]);
        this.selectedRowKeys.splice(0, this.selectedRowKeys.length);
        this.selectedRows.splice(0, this.selectedRows.length);
        if (!checked) {
          this.selectedRowKeys.push(row[this.rowKey]);
          this.selectedRows.push(row);
        }

        return;
      }
      if (this.selectedRowKeys.includes(row[this.rowKey])) {
        this.selectedRowKeys.splice(this.selectedRowKeys.indexOf(row[this.rowKey]), 1);
      } else {
        this.selectedRowKeys.push(row[this.rowKey]);
        this.selectedRows.push(row);
      }
      this.checkAll = this.selectedRowKeys.length > 0 && this.selectedRowKeys.length == this.rows.length;
    },
    onProxyWidgetChange(e, wgt) {
      for (let i = 0, len = this.rows.length; i < len; i++) {
        this.setRowDisplayValue(this.rows[i]);
      }
      // console.log("代理组件变更", wgt.configuration.code, this.rows);
    },
    setRowDisplayValue(row) {
      for (let col of this.widget.configuration.columns) {
        if (
          this.$refs["proxyField_" + col.dataIndex] &&
          this.$refs["proxyField_" + col.dataIndex][0] &&
          this.$refs["proxyField_" + col.dataIndex][0].$refs.widget
        ) {
          let displayValue = this.$refs["proxyField_" + col.dataIndex][0].$refs.widget.displayValue(row[col.dataIndex]);
          if (!row.__MODEL__) {
            row.__MODEL__ = { label: {} };
          }
          if (row.__MODEL__.label) {
            this.$set(row.__MODEL__.label, col.dataIndex, displayValue || "");
          }
        }
        let $cellRef = this.$refs[row[this.rowKey] + "_cell_" + col.dataIndex + "_Ref"];
        if ($cellRef && $cellRef.length > 0) {
          $cellRef[0].setMainFormDataValue(row[col.dataIndex]);
        }
      }
    },

    addRow(data = {}, afterIndex, rows = this.rows, subform) {
      console.log("添加从表行", data);
      // 创建从表行级表单实例
      let sf =
        subform == undefined
          ? this.form.createSubform(this.formUuid, data.uuid || framework.utils.generateId())
          : subform;
      sf.formId = this.widget.configuration.formId;
      sf.tableName = this.widget.configuration.tableName; // 表名
      sf.title = this.widget.configuration.title; // 表标题
      sf.displayState = this.dyform.displayState; // 从表状态和主表一致
      sf.formElementRules = framework.utils.deepClone(this.formElementRules); // 表单字段规则传入
      let _data = framework.utils.deepClone(data);
      _data = { ...this.subformDefaultData, ...data };
      sf.formData = { ..._data }; // 表单数据赋值

      sf.dataUuid = _data.uuid;
      sf.subformLazyLoaded = false;
      let row = { ..._data, __MODEL__: { label: {} }, [this.rowKey]: sf.namespace };
      if (_data.uuid) {
        sf.formData.$$id = row.$$id;
      } else if (!sf.formData.$$id) {
        sf.formData.$$id = framework.utils.generateId();
      }
      if (data && data.__MODEL__) {
        row.__MODEL__ = framework.utils.deepClone(data.__MODEL__);
      } else {
        for (let k in data) {
          // 显示值设置：如果是真实值/显示值组件，需要等待获取实际数据
          row.__MODEL__.label[k] = this.colIsLabelValWidget[k] ? "" : data[k];
        }
      }

      if (afterIndex === undefined) {
        rows.push(row);
      } else {
        rows.splice(afterIndex + 1, 0, row);
        this.form.swapSubform(this.formUuid, rows.length - 1, afterIndex + 1);
      }

      this.rowForms[sf.namespace] = sf;
      this.rowKeyMap[sf.namespace] = row;

      return row;
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
      if (!direction) {
        ids = ids.reverse();
      }
      framework.utils.swapArrayElements(
        ids,
        this.rows,
        function (a, b) {
          return a == b["$$id"];
        },
        direction,
        function (from, to) {
          // 交换form顺序，sort_order会在收集从表行数据时候重新计算
          _this.form.swapSubform(_this.formUuid, from, to);
        }
      );
    },
    insertRow(btn, record, index) {
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
              __MODEL__: { label: { [tableGroupField]: record.__MODEL__.label[tableGroupField] } },
            };
            break;
          }
        }
      }
      this.addRow(data, index, targetRows);
    },
    onSubformButtonClick(e, button, row, rowIndex) {
      // 如果是分页的，rowIndex应该是总排序中的位置
      if (this.hasPagination && this.pagination.current > 1 && rowIndex !== undefined) {
        rowIndex = (this.pagination.current - 1) * this.pagination.pageSize + rowIndex;
      }
      if (button.id == "appendRow") {
        button.id = "addRow";
      }
      if ("addRow" == button.id) {
        this.openNewRowPopup();
      } else if ("editRow" == button.id) {
        this.currentSelectedForm = this.rowForms[row.$$id];
        if (!this.currentSelectedForm.formData.$$id) {
          this.currentSelectedForm.formData.$$id = this.currentSelectedForm.namespace;
        }
        this.currentSelectedFormDatas = this.getCurrentSelectedFormDatas();
        console.log("当前编辑从表行", this.currentSelectedForm);
        this.isEdit = true;
        this.rowFormRendered = true;
        this.openPopup("detail");
      } else if ("copyRow" == button.id) {
        let copyRow = framework.utils.deepClone(row);
        delete copyRow.status;
        copyRow.$$id = framework.utils.generateId();
        let formData = framework.utils.deepClone(this.rowForms[row.$$id].formData);
        delete formData.status;
        formData.$$id = copyRow.$$id;
        let sf = this.form.createSubform(this.formUuid, copyRow.$$id);
        sf.formData = formData;
        this.rowForms[formData.$$id] = sf;
        this.rowKeyMap[formData.$$id] = copyRow;
        this.rows.push(copyRow);
      } else if ("moveUpRow" == button.id || "moveDownRow" == button.id) {
        this.swapRows("moveUpRow" == button.id ? "forward" : undefined, rowIndex);
      } else if ("delRow" == button.id) {
        let getUnDeleteDefaultRow = [];
        if (rowIndex != undefined) {
          let $$id = this.rows[rowIndex].$$id;
          let delData = this.removeSelectedRowByKey($$id, rowIndex);
          if (delData.isDel) {
            this.rowForms[$$id] = null;
            this.rows.splice(rowIndex, 1);
            this.form.deleteSubform(this.formUuid, rowIndex);
          } else if (delData.unDelRow) {
            getUnDeleteDefaultRow.push(delData.unDelRow);
          }
        } else {
          // 删除所选的数据行
          for (let i = 0; i < this.rows.length; i++) {
            let delData = this.removeSelectedRowByKey(this.rows[i][this.rowKey]);
            if (delData.isDel) {
              this.form.deleteSubform(this.formUuid, i);
              this.rows.splice(i--, 1);
            } else if (delData.unDelRow) {
              getUnDeleteDefaultRow.push(delData.unDelRow);
            }
          }
          this.checkAll = this.rows.length > 0 && this.selectedRowKeys.length == this.rows.length;
        }
        this.defaultSelectedRowKeys = this.selectedRowKeys;
        if (getUnDeleteDefaultRow.length > 0) {
          uni.showModal({
            content: this.$t(
              "WidgetSubform.message.unDeleteDefaultRow",
              { indexs: getUnDeleteDefaultRow.join("、") },
              `第${getUnDeleteDefaultRow.join("、")}项数据为不可删除的默认数据`
            ),
            showCancel: false,
            confirmText: this.$t("global.confirm", "确认"),
            success: function (res) {},
          });
        }
      } else if (this[button.id] != undefined) {
        this[button.id](button, row, rowIndex);
      }
    },
    getCurrentSelectedFormDatas() {
      let formData = framework.utils.deepClone(this.currentSelectedForm.formData);
      let formDatas = {};
      let nestformDatas = formData.nestformDatas;
      if (this.currentSelectedForm.subform && nestformDatas) {
        nestformDatas = JSON.parse(nestformDatas).formDatas;
        Object.assign(formDatas, nestformDatas);
      }
      formDatas[this.formUuid] = [formData];
      return formDatas;
    },
    setCurrentSelectedFormData(data) {
      let formData = {};
      let formDatas = data.dyFormData.formDatas[this.formUuid][0];
      // let nestformDatas = formDatas.nestformDatas;
      // if (this.currentSelectedForm.subform && nestformDatas) {
      //   let subformDatas = JSON.parse(nestformDatas).formDatas;
      //   each(this.currentSelectedForm.subform, (item, index) => {
      //     formData[index] = subformDatas[index];
      //   });
      // }
      formData.$$subDyformEdit = true;
      Object.assign(formData, formDatas);
      return formData;
    },
    onSubDyformRefInitSuccess(subDyform) {
      this.currentSelectedForm.$subform = subDyform.dyform.$subform;
      this.currentSelectedForm.subform = subDyform.dyform.subform;
    },
    removeSelectedRowByKey(key, index) {
      let getUnDeleteDefaultRow = [];
      let j = index || index === 0 ? index : this.selectedRowKeys.indexOf(key);
      if (j != -1) {
        let _hasIndex = findIndex(this.rows, (item) => item[this.rowKey] == key);
        let hasIndex = findIndex(this.rows, (item) => item[this.rowKey] == key);
        if (_hasIndex > -1) {
          if (this.rows[hasIndex].status == "2") {
            getUnDeleteDefaultRow.push(hasIndex + 1);
          } else {
            this.selectedRows.splice(_hasIndex, 1);
            this.selectedRowKeys.splice(j, 1);
            delete this.rowForms[key];
          }
        }
        if (getUnDeleteDefaultRow.length > 0) {
          return {
            isDel: false,
            unDelRow: getUnDeleteDefaultRow[0],
          };
        }
        return {
          isDel: true,
        };
      }
      return {
        isDel: false,
      };
    },
    rowEndButtonPredicate(button, rowIndex, row) {
      if (button.id == "delRow") {
        // 行级删除按钮，默认不可删除的数据，不展示删除按钮
        if (row.status == "2") {
          return false;
        }
      }
      if (
        button.id == "moveUpRow" &&
        rowIndex == 0 &&
        ((this.hasPagination && this.pagination.current == 1) || !this.hasPagination)
      ) {
        // 有分页时，只有第一页第一个才不显示上移
        return false;
      }
      if (button.id == "moveDownRow" && rowIndex == this.rows.length - 1) {
        return false;
      }
      return true;
    },
    headButtonPredicate(button) {
      if ("delRow" == button.id) {
        // 删除按钮：未选择行数据，不展示
        if (this.selectedRowKeys.length > 0) {
          let num = this.selectedRowKeys.length;
          button.titleFormatter = (title) => {
            return `${this.$t("header_" + button.id, button.title)} (${num})`;
          };
        }
        return this.selectedRowKeys.length > 0;
      } else if (button.id.startsWith("export_") || button.id.startsWith("import_")) {
        // 导出导入按钮不展示
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
            button.defaultVisibleVar.conditions[0].operator == "true" &&
            button.defaultVisibleVar.conditions[0].code == undefined &&
            button.defaultVisibleVar.conditions[0].value == undefined))
      ) {
        return false;
      }
      // 从表头部按钮和行按钮存在相同id的情况，头部按钮做额外处理，行按钮不做处理
      button.titleFormatter = (title) => {
        return this.$t("header_" + button.id, button.title);
      };

      return true;
    },

    onConfirmEditRow() {
      if (!this.isEdit) {
        return false;
      }
      // 校验表单
      let _this = this;
      this.$refs.subDyform.validateFormData(function (vali, msg) {
        if (vali) {
          let data = _this.$refs.subDyform.collectFormData();
          _this.currentSelectedForm.formDatas = data;
          Object.assign(_this.currentSelectedForm.formData, _this.setCurrentSelectedFormData(data));
          if (_this.currentSelectedForm.formData.$$id == null) {
            _this.currentSelectedForm.formData.$$id = _this.currentSelectedForm.namespace;
            _this.rows.push(_this.currentSelectedForm.formData);
            _this.rowForms[_this.currentSelectedForm.formData.$$id] = _this.currentSelectedForm;
            _this.form.pushSubform(_this.formUuid, _this.currentSelectedForm);
            _this.rowKeyMap[_this.currentSelectedForm.namespace] = _this.rows[_this.rows.length - 1];
          } else {
            // 更新表单字段值到行上
            _this.rowForms[_this.currentSelectedForm.formData.$$id] = _this.currentSelectedForm;
            for (let key in _this.currentSelectedForm.formData) {
              _this.rowKeyMap[_this.currentSelectedForm.namespace][key] = _this.currentSelectedForm.formData[key];
            }
          }
          _this.$nextTick(() => {
            _this.setRowDisplayValue(_this.rowKeyMap[_this.currentSelectedForm.namespace]);
            _this.setDyformSubRowData();
            _this.currentSelectedForm = {};
            _this.rowFormRendered = false;
            _this.closePopup("detail");
          });
        }
      });
    },
    // 设置主表中从表行数据
    setDyformSubRowData() {
      let _this = this;
      this.widgetDyformContext.$set(this.widgetDyformContext.dyform.formData, _this.formUuid, _this.rowKeyMap);
    },
    // 表单可编辑时，编辑行；表单不可编辑时，查看行详情
    showDetail(key) {
      this.currentSelectedForm = this.rowForms[key];
      this.currentSelectedFormDatas = this.getCurrentSelectedFormDatas();
      if (this.form.displayState == "edit") {
        this.isEdit = true;
      } else {
        if (this.currentSelectedForm && this.currentSelectedForm.formElementRules) {
          this.detialFormElementRules = framework.utils.deepClone(this.currentSelectedForm.formElementRules);
          for (let i in this.detialFormElementRules) {
            this.detialFormElementRules[i].editable = false;
            this.detialFormElementRules[i].displayAsLabel = true;
          }
        }
        this.isEdit = false;
      }
      this.rowFormRendered = true;
      this.openPopup("detail");
    },

    getColumnDisplayValue(row, column, placeholder) {
      return row.__MODEL__.label[column] || row[column] || placeholder;
    },
    openPopup(ref) {
      this.$refs[ref].open();
    },

    closePopup(ref) {
      this.$refs[ref].close();
      if (ref == "detail") {
        this.rowFormRendered = false;
      }
    },
    openNewRowPopup() {
      this.isEdit = true;
      this.openPopup("detail");
      let sf = this.form.createTempDyform(this.formUuid, framework.utils.generateId());

      sf.formId = this.widget.configuration.formId;
      sf.tableName = this.widget.configuration.tableName; // 表名
      sf.title = this.widget.configuration.title; // 表标题
      sf.displayState = this.dyform.displayState; // 从表状态和主表一致
      sf.formElementRules = framework.utils.deepClone(this.formElementRules); // 表单字段规则传入
      sf.formData = { __MODEL__: { label: {} } };
      // this.rowForms[sf.namespace] = sf;
      this.currentSelectedForm = sf;
      this.currentSelectedFormDatas = {};
      this.rowFormRendered = true;
      // for(let i=0,len = this.widget.configuration.columns.length;i<len;i++){
      //   sf.formData.__MODEL__[this.widget.configuration.columns[i].dataIndex] = null;
      // }
    },
    updateLatestColumnInfo() {
      let p1 = new Promise((resolve, reject) => {
        this.fetchFormDefinition().then((definition) => {
          if (definition) {
            let json = JSON.parse(definition.definitionVjson);
            this.formDefinitionVjson = json;
            if (json.jsModule != undefined) {
              this.subformJsModule = json.jsModule;
            }
            let fieldMap = {};
            for (let i = 0, len = json.fields.length; i < len; i++) {
              fieldMap[json.fields[i].configuration.code] = json.fields[i];
              this.fields.push(json.fields[i]);
            }
            for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
              let c = this.widget.configuration.columns[i];
              if (c.widget != undefined && c.dataIndex && fieldMap[c.dataIndex]) {
                c.widget = fieldMap[c.dataIndex];
                if (
                  c.required &&
                  c.widget.configuration.requiredCondition &&
                  c.widget.configuration.requiredCondition.conditions
                ) {
                  // 列必填的情况下，清空字段必填性判断条件
                  c.widget.configuration.requiredCondition.conditions.splice(
                    0,
                    c.widget.configuration.requiredCondition.conditions.length
                  );
                }
                this.formDefinition.widgets.push(c.widget);
                let wgt = framework.utils.deepClone(c.widget);
                wgt.id = framework.utils.generateId();
                this.proxyFormWidgets.push(wgt);
              }
            }
            this.setColumns();

            this.initDefaultSubformRows();
            this.initedColumn = true;
          }
          resolve();
        });
      });
      let p2 = new Promise((resolve, reject) => {
        this.getDefaultData({ formUuid: this.formUuid }).then((data) => {
          this.defaultDataFetched = true;
          if (data) {
            Object.assign(this.subformDefaultData, data);
          }
          resolve();
        });
      });
      return Promise.all([p1, p2]);
    },
    setColumns() {
      // 移动端仅表单编辑rowEditMode='form'
      let _this = this;
      let configuration = this.widget.configuration;
      this.columns.splice(0, this.columns.length);
      for (let i = 0, len = configuration.columns.length; i < len; i++) {
        let col = configuration.columns[i];
        col.editable = col.defaultDisplayState === "edit";
        col.hidden = col.defaultDisplayState === "hidden";
        col.title = this.$t(col.id, col.title);
        // 字段下按钮
        if (this.filterUnderColButtonMap[col.dataIndex]) {
          col.button = this.filterUnderColButtonMap[col.dataIndex];
        }
        if (this.formElementRules[col.widget.id] === undefined) {
          // 未定义规则的情况下：以字段列配置的显示模式规则
          this.formElementRules[col.widget.id] = {
            displayAsLabel: this.form.displayState == "label" && col.defaultDisplayState !== "edit",
            editable: this.form.displayState == "edit" && col.defaultDisplayState !== "unedit",
            hidden: col.defaultDisplayState === "hidden",
            columnRuleApplyToForm: this.widget.configuration.layout == "table" && col.ruleApplyToForm === true,
          };
        } else {
          if (this.formElementRules[col.widget.id].hidden !== undefined) {
            col.hidden = this.formElementRules[col.widget.id].hidden;
          }

          if (this.formElementRules[col.widget.id].editable != undefined) {
            col.editable = this.formElementRules[col.widget.id].editable;
          }
        }

        if (col.hidden) {
          continue;
        }

        if (this.formElementRules[col.widget.id].editable) {
          // 从表字段有可编辑权限的情况下，对表格列显示为组件模式，直接以编辑模式渲染
          col.editable = this.widget.configuration.cellDefaultDisplayState == "widget";
        }

        col.scopedSlots = {
          customRender: col.id,
        };

        // 从表以文本展示状态，则列不可编辑
        if (this.displayAsLabel) {
          col.editable = false;
        }

        if (col.sortable.enable) {
          col.sorter = function ({ order }) {
            let rows = _this.rows.sort((a, b) => {
              return _this.sorter(a, b, col, order);
            });
            _this.$set(_this, "rows", rows);
          };
        } else {
          col.sorter = function () {};
        }

        // 如果规则有必填设置，则将列的必填改成规则的必填
        if (this.formElementRules[col.widget.id] && this.formElementRules[col.widget.id].required != undefined) {
          col.required = this.formElementRules[col.widget.id].required;
        } else {
          // 如果规则没有必填设置，则规则的必填跟着列的必填
          this.formElementRules[col.widget.id].required = col.required || false;
        }

        //
        let relaOtherFields = [];
        if (col.widget.configuration.displayValueField) {
          // 有关联的显示值字段
          relaOtherFields.push(col.widget.configuration.displayValueField);
        }

        col.relaFields = Array.from(new Set(relaOtherFields.concat(col.widget.configuration.fieldsRelated || []))); // 关联其他字段

        this.columns.push(col);
      }
      if (this.proxyForm && !this.renderProxyForm) {
        this.proxyForm.formElementRules = this.formElementRules;
        this.renderProxyForm = true;
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
    getDefaultData({ formUuid }) {
      return new Promise((resolve, reject) => {
        this.$axios.post(`/api/dyform/data/getDefaultFormData?formUuid=${formUuid}`).then(({ data }) => {
          if (data.code == 0 && data.data) {
            resolve(data.data);
          }
        });
      });
    },
    fetchFormDefinition() {
      return new Promise((resolve, reject) => {
        this.$axios
          .post("/json/data/services", {
            serviceName: "formDefinitionService",
            methodName: "getOne",
            args: JSON.stringify([this.formUuid]),
          })
          .then(({ data }) => {
            if (data.code == 0 && data.data && data.data.definitionVjson) {
              resolve(data.data);
            }
          });
      });
    },

    emitRowFormDataChange() {
      let _row = [];
      for (let i = 0, len = this.rows.length; i < len; i++) {
        let d = framework.utils.deepClone(this.rows[i]);
        delete d.__MODEL__;
        delete d[this.rowKey];
        _row.push(d);
      }
      let md5String = framework.utils.md5(JSON.stringify(_row));
      if (this.rowDataAsMD5 != md5String) {
        this.dyform.emitEvent(`${this.widget.id}:rowChange`, { data: _row, wSubform: this });
        this.rowDataAsMD5 = md5String;
      }
      // 更新分页
      if (this.hasPagination) {
        this.paginationUpdate();
      } else {
        this.showRows = this.filteredRows;
        this.cardRowsExpandMapChange();
      }
      setTimeout(() => {
        this.collapseResize(); // 折叠面板高度重置
      }, 300);
    },
    // 更新分页
    paginationUpdate() {
      this.pagination.total = this.filteredRows.length;
      const totalPage =
        this.pagination.total % this.pagination.pageSize == 0
          ? parseInt(this.pagination.total / this.pagination.pageSize)
          : parseInt(this.pagination.total / this.pagination.pageSize + 1);
      this.pagination.totalPage = totalPage;
      if (this.pagination.current > totalPage) {
        this.pagination.current = totalPage;
      }
      this.paginationChange({ current: this.pagination.current || 1 });
    },
    // 分页后显示数据计算
    paginationChange({ type, current }) {
      this.pagination.current = current;
      let pageSize = this.pagination.pageSize;
      this.showRows = this.filteredRows.filter((v, i) => {
        return i >= (current - 1) * pageSize && i < current * pageSize;
      });
      if (this.$refs.table) {
        setTimeout(() => {
          this.$refs.table.defaultSelectedChange();
        }, 100);
      } else {
        this.cardRowsExpandMapChange();
      }
      console.log(this.showRows);
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
      return typeof fixedNumber == "number" ? parseFloat((total / count).toFixed(fixedNumber)) : total / count;
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
    // 列排序
    sorter(rowa, rowb, col, order) {
      let sortable = col.sortable,
        v1 = rowa[col.dataIndex],
        v2 = rowb[col.dataIndex];
      this.currentEditRowCell = null;
      let formData = [this.rowForms[rowa[this.rowKey]].formData, this.rowForms[rowb[this.rowKey]].formData];
      if (sortable.alogrithmType === "orderByNumber") {
        return order == "descending" ? Number(v2) - Number(v1) : Number(v1) - Number(v2);
      } else if (sortable.alogrithmType === "orderByDate" && sortable.datePattern) {
        let v1Date = moment(v1, sortable.datePattern),
          v2Date = moment(v2, sortable.datePattern);
        if (order == "descending") {
          return v2Date.isAfter(v1Date) ? 1 : v2Date.isSame(v1Date) ? 0 : -1;
        }
        return v1Date.isAfter(v2Date) ? 1 : v1Date.isSame(v2Date) ? 0 : -1;
      } else if (sortable.alogrithmType === "orderByPinYin") {
        // rowa 拼音在  rowb之前，返回负数，否则正数，相等则为0
        if (v1) {
          if (order == "descending") {
            return v2.localeCompare(v1, "pinyin");
          }
          return v1.localeCompare(v2, "pinyin");
        }
      } else if (sortable.alogrithmType === "orderByChar") {
        if (v1) {
          if (order == "descending") {
            return v2.localeCompare(v1);
          }
          return v1.localeCompare(v2);
        }
      } else if (sortable.alogrithmType === "orderByDefine" && sortable.script) {
        let ans = 0;
        if (order == "descending") {
          // 执行自定义代码比较
          this.pageContext.executeCodeSegment(
            sortable.script,
            { value: [v2, v1], row: [rowb, rowa], dataIndex: col.dataIndex, formData },
            this,
            function (rt) {
              ans = rt;
            }
          );
        } else {
          // 执行自定义代码比较
          this.pageContext.executeCodeSegment(
            sortable.script,
            { value: [v1, v2], row: [rowa, rowb], dataIndex: col.dataIndex, formData },
            this,
            function (rt) {
              ans = rt;
            }
          );
        }
        return ans;
      }
      return 0;
    },
    onCellComponentMounted() {
      this.collapseResize();
    },
    collapseResize() {
      if (this.$refs.collapse) {
        if (this.$refs.collapseItem) {
          this.$refs.collapseItem.isOpen = this.collapseValue.length == 1 ? true : false;
        }
        this.$refs.collapse.resize();
        if (this.$refs.table) {
          this.$refs.table.getTableRectInfo();
        }
      }
    },
    collapseChange(val) {
      if (val.length) {
        this.collapseResize();
      }
    },
    validateSubform() {
      let _this = this;
      return new Promise((resolve, reject) => {
        // 当前分页情况下，才需要校验，其他情况下可以在主表校验时候一并触发
        if (_this.hasPagination) {
          let msg = [];
          // 当前为表格布局且表格可以分页展示，校验时需对未渲染的数据同时校验
          _this.proxyForm.displayState = _this.dyform.displayState; // 从表状态和主表一致
          // 组件初始时,将rule设置到主表规则里
          // _this.$refs.proxyFormRef.setRules(this.widgetDyformContext.$refs.form.formRules);
          let validatePromise = [];
          this.$nextTick(() => {
            for (let i = 0; i < _this.rows.length; i++) {
              validatePromise.push(
                new Promise((resolveVali, rejectVali) => {
                  let _data = framework.utils.deepClone(_this.rows[i]);
                  _this.proxyForm.formData = { ..._data }; // 表单数据赋值
                  for (let j = 0; j < _this.columns.length; j++) {
                    let code = _this.columns[j].widget.configuration.code;
                    // 更改uniFormItem组件值
                    if (_this.$refs["proxyField_" + code][0].$refs.widget) {
                      _this.$refs["proxyField_" + code][0].$refs.widget.$refs[code].itemSetValue(_data[code]);
                    }
                  }
                  _this.$refs.proxyFormRef.validate(function (messages, res) {
                    //messages为null时,校验成功
                    let valid = !messages;
                    // console.error("从表校验消息", _this.rows[i][_this.rowKey], messages);
                    resolveVali(messages);
                  });
                })
              );
            }
            if (validatePromise.length) {
              Promise.all(validatePromise).then((res) => {
                for (let i = 0; i < res.length; i++) {
                  if (res[i]) {
                    msg = msg.concat(res[i]);
                  }
                }
                resolve({
                  validate: Object.keys(msg).length == 0,
                  msg,
                });
              });
            } else {
              resolve({
                validate: Object.keys(msg).length == 0,
                msg,
              });
            }
          });
        } else {
          resolve();
        }
      });
    },
    getSelf() {
      return this;
    },
    copyInitDefaultFileToRow(row, uuid) {
      let { id, data } = row;
      for (let key in data) {
        if (
          Array.isArray(data[key]) &&
          data[key].length > 0 &&
          data[key][0] != undefined &&
          data[key][0].fileID != undefined
        ) {
          let promises = [];
          data[key].forEach((f) => {
            promises.push(
              new Promise((resolve, reject) => {
                this.$axios
                  .post("/json/data/services", {
                    serviceName: "mongoFileService",
                    methodName: "copyFileAndRename",
                    args: JSON.stringify([f.fileID, f.filename]),
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
          Promise.all(promises).then((dbFiles) => {
            let files = [];
            dbFiles.forEach((d) => {
              files.push(d.logicFileInfo);
            });
            if (this.rowForms[uuid].$fieldset[key]) {
              this.rowForms[uuid].setFieldValue(key, files);
            } else {
              let row = this.rowKeyMap[uuid];
              row[key] = files;
              this.rowKeyMap[uuid] = row;
              this.rowForms[uuid].formData = row;
            }
          });
          this.$forceUpdate;
        }
      }
    },
    initDefaultSubformRows() {
      let defaultRowDataConfig = this.widget.configuration.defaultRowDataConfig;
      if (
        defaultRowDataConfig != undefined &&
        defaultRowDataConfig.enable &&
        defaultRowDataConfig.rows.length > 0 &&
        (this.form.dataUuid == undefined || this.form.isNewFormData === true) &&
        this.form.formData.nestformDatas == undefined
      ) {
        for (let i = 0, len = defaultRowDataConfig.rows.length; i < len; i++) {
          let row = defaultRowDataConfig.rows[i];
          let dataUuid = framework.utils.generateId();
          this.copyInitDefaultFileToRow(row, dataUuid);
          if (!row.deletable) {
            row.data.status = "2";
          }
          this.addRow(
            row.data,
            undefined,
            this.rows,
            this.form.createSubform(this.widget.configuration.formUuid, dataUuid)
          );
        }
        if (defaultRowDataConfig.rows.length) {
          this.$nextTick(() => {
            this.setDyformSubRowData();
          });
        }

        // if (this.widget.configuration.layout == 'form-tabs' && this.subformActiveTabKey == undefined && this.rows.length > 0) {
        //   this.subformActiveTabKey = this.rows[0][this.rowKey];
        // }
      }
      this.initedDefaultSubformRows = true;
    },
    // 切换卡片内容隐藏显示
    cardRowsExpandMapChange(rowKey) {
      if (!rowKey) {
        let hasInMap = false;
        each(this.showRows, (row) => {
          if (this.cardRowsExpandMap.indexOf(row[this.rowKey]) > -1) {
            hasInMap = true;
          }
        });
        if (hasInMap) {
          // 展开行存在，不做改变
          return false;
        } else if (this.showRows.length) {
          // 展开行存在，显示第一条数据
          rowKey = this.showRows[0][this.rowKey];
        }
      }
      if (rowKey) {
        if (this.widget.configuration.uniConfiguration.accordion) {
          if (this.cardRowsExpandMap.indexOf(rowKey) == -1) {
            // 手风琴效果，仅展开一条数据
            this.cardRowsExpandMap.splice(0, this.cardRowsExpandMap.length);
          }
        }
        if (this.cardRowsExpandMap.indexOf(rowKey) == -1) {
          this.cardRowsExpandMap.push(rowKey);
        } else {
          this.cardRowsExpandMap.splice(this.cardRowsExpandMap.indexOf(rowKey), 1);
        }
        setTimeout(() => {
          this.collapseResize(); // 折叠面板高度重置
        }, 300);
      }
    },
    setColumnVisible(dataIndex, visible = true) {
      if (typeof dataIndex == "object" && dataIndex.eventParams != undefined) {
        // 由事件传递进来的参数
        visible = dataIndex.eventParams.visible !== "false";
        dataIndex =
          typeof dataIndex.eventParams.dataIndex === "string" && dataIndex.eventParams.dataIndex
            ? [dataIndex.eventParams.dataIndex]
            : dataIndex.eventParams.dataIndex;
      }
      if (!Array.isArray(dataIndex)) {
        console.warn("setColumnVisible 字段参数不符合要求");
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
          col.defaultDisplayState = visible ? "edit" : "hidden";
          let defaultDisplayState = visible ? "edit" : "hidden";
          if (visible && this.formElementRules && this.formElementRules[col.widget.id]) {
            let rule = this.formElementRules[col.widget.id];
            // 存在规则不可编辑情况下，以规则为主，显示出来还是不可编辑情况
            if (rule.editable != undefined) {
              defaultDisplayState = rule.editable ? "edit" : "unedit";
            }
          }

          if (
            this.formElementRules &&
            this.formElementRules[col.widget.id] &&
            this.formElementRules[col.widget.id].hidden != undefined
          ) {
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
    setColumnRequired(field, required = true) {
      if (typeof field == "object" && field.eventParams != undefined) {
        // 由事件传递进来的参数
        required = field.eventParams.required !== "false";
        field =
          typeof field.eventParams.dataIndex === "string" && field.eventParams.dataIndex
            ? [field.eventParams.dataIndex]
            : field.eventParams.dataIndex;
      }
      if (!Array.isArray(field)) {
        console.warn("setColumnRequired 字段参数不符合要求");
        return;
      }
      for (let index = 0; index < this.columns.length; index++) {
        if (field && field.length == 0) {
          break;
        }
        const col = this.columns[index];
        if (field && field.includes(col.dataIndex)) {
          field.splice(field.indexOf(col.dataIndex), 1);
          col.required = required;
          this.$set(this.columns[index], "required", required);
          if (this.formElementRules[col.widget.id]) {
            this.formElementRules[col.widget.id].required = required;
            for (let key in this.rowForms) {
              this.rowForms[key].formElementRules[col.widget.id].required = required;
              this.rowForms[key].setFieldRequired(col.dataIndex, required);
            }
          }
        }
      }
      this.forceUpdateEvent();
    },
    setColumnEditable(field, editable = true) {
      if (typeof field == "object" && field.eventParams != undefined) {
        // 由事件传递进来的参数
        editable = field.eventParams.editable !== "false";
        field =
          typeof field.eventParams.dataIndex === "string" && field.eventParams.dataIndex
            ? [field.eventParams.dataIndex]
            : field.eventParams.dataIndex;
      }
      if (!Array.isArray(field)) {
        console.warn("setColumnEditable 字段参数不符合要求");
        return;
      }
      for (let index = 0; index < this.columns.length; index++) {
        const col = this.columns[index];
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
              this.rowForms[key].formElementRules[col.widget.id].displayAsLabel = !editable;
              this.rowForms[key].formElementRules[col.widget.id].editable = editable;
              this.rowForms[key].setFieldEditable(col.dataIndex, editable);
            }
          }
        }
      }
      this.forceUpdateEvent();
    },
    forceUpdateEvent() {
      this.$forceUpdate();
    },
    // 进入可视区域后，计算滚动区域高度, 不包括页面返回显示
    subformViewShowChange() {
      let _this = this;
      const view = uni.createSelectorQuery().in(this).select(".w-form-subform");
      const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            // 元素进入视口时执行的操作
            // 检查view是否可见的逻辑
            view
              .boundingClientRect((data) => {
                if (data.width > 0 && data.height > 0) {
                  // view已经显示，执行你的操作
                  setTimeout(() => {
                    this.collapseResize(); // 折叠面板高度重置
                  }, 300);
                  console.log("subformView is visible");
                  // 你的操作代码
                }
              })
              .exec();
            observer.unobserve(entry.target); // 停止观察该元素，避免重复触发
          }
        });
      });
      observer.observe(view._component.$el);
    },
    onFilteredRowKeys(keys) {
      // 搜索前记录待搜索行标识，避免当新增空行时候，搜索存在时候会导致看不到，即点击搜索或者数据发生变化时候，搜索的方法仅限这批待搜索行数据范围内
      this.waitSearchRowKeys.splice(0, this.waitSearchRowKeys.length);
      for (let i = 0, len = this.rows.length; i < len; i++) {
        this.waitSearchRowKeys.push(this.rows[i][this.rowKey]);
      }
      this.filteredRowKeys.splice(0, this.filteredRowKeys.length);
      this.filteredRowKeys.push(...keys);
      this.searchKey = framework.utils.generateId();
    },
    onResetSearch() {
      this.$set(this, "searchKey", undefined);
      this.filteredRowKeys.splice(0, this.filteredRowKeys.length);
      this.$nextTick(() => {
        if (this.pagination !== false) {
          this.pagination.total = this.filteredRows.length;
        }
      });
    },
  },
  watch: {
    subformWidgetIsReady: {
      handler(v, o) {
        if (v && this.emitReadyEvent == undefined) {
          this.emitReadyEvent = true;
          setTimeout(() => {
            this.dyform.emitEvent(`WidgetSubform:${this.widget.id}:ready`, { $vue: this });
            this.invokeDevelopmentMethod("onWidgetSubformReady", this);
          }, 300);
        }
      },
    },
    // rows: {
    //   deep: true,
    //   handler(v) {
    //     this.emitRowFormDataChange();
    //   },
    // },
    filteredRows: {
      deep: true,
      handler(v) {
        this.emitRowFormDataChange();
      },
    },
    returnRefresh(val) {
      if (val) {
        console.log("returnRefresh", this.widget.configuration.title);
        setTimeout(() => {
          this.collapseResize(); // 折叠面板高度重置
        }, 10);
        setTimeout(() => {
          // 可能存在多个从表或其他情况，延时关闭
          this.$store.commit("setRefreshFlag", false);
        }, 500);
      }
    },
  },
};
</script>
