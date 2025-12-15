<template>
  <view class="w-form-subform">
    <uni-collapse ref="collapse" v-model="value">
      <uni-collapse-item :open="unCollapsed">
        <view slot="title" class="collapse-item-title">
          <view class="label">
            <table-checkbox
              v-if="
                widget.configuration.rowSelectType == 'checkbox' &&
                widget.configuration.uniConfiguration.layout == 'form-inline'
              "
              :checked="checkAll"
              :indeterminate="selectIndeterminate"
              @checkboxSelected="onSelectAllRow"
            />

            <text class="text">{{ widget.configuration.title }}</text>
          </view>

          <view @click.stop="onClickPopupHeaderButtons" class="icon">
            <w-icon icon="more-filled" :size="25" color="rgb(187, 187, 187)" />
          </view>
        </view>
        <view class="table" v-if="widget.configuration.uniConfiguration.layout == 'table'">
          <view class="tr">
            <view class="th checkbox-th" v-if="widget.configuration.rowSelectType !== 'no'">
              <table-checkbox
                v-if="widget.configuration.rowSelectType == 'checkbox'"
                :checked="checkAll"
                :indeterminate="selectIndeterminate"
                @checkboxSelected="onSelectAllRow"
              />
            </view>
            <view class="th" v-if="widget.configuration.addSerialNumber" style="flex: 50px; text-align: center"
              >序号</view
            >
            <view class="th-columns">
              <template v-for="column in columns">
                <view class="th" v-if="c < tableShowColumnNum">
                  {{ column.title }}
                </view></template
              >
            </view>
            <view class="th" style="flex: 40px; text-align: center">操作</view>
          </view>
          <view class="tbody">
            <view class="tr" v-for="row in rows">
              <view class="td" v-if="widget.configuration.rowSelectType !== 'no'">
                <table-checkbox
                  :checked="selectedRowKeys.includes(row[rowKey])"
                  @checkboxSelected="onSelectRow(row)"
                  :radio="widget.configuration.rowSelectType == 'radio'"
                />
              </view>
              <view class="td" style="flex: 50px; text-align: center" v-if="widget.configuration.addSerialNumber">{{
                r + 1
              }}</view>
              <view class="td-columns">
                <template v-for="column in columns">
                  <view class="td" v-if="c < tableShowColumnNum">
                    {{ getColumnDisplayValue(row, column.dataIndex) }}
                  </view>
                </template>
              </view>
              <view class="td" style="flex: 40px; text-align: center">
                <w-icon icon="more" @onTap="onClickPopupTableRowButton(row)" />
              </view>
            </view>
          </view>
        </view>

        <template v-else-if="widget.configuration.uniConfiguration.layout == 'form-inline'">
          <view class="card-display">
            <uni-card margin="8px" v-for="row in rows">
              <template v-slot:title>
                <view class="card-title" @click.stop="onSelectRow(row)">
                  <view>
                    <text style="color: #787878; padding-right: 5px" v-if="widget.configuration.addSerialNumber">
                      {{ r + 1 }}.
                    </text>
                    <text>
                      {{
                        getColumnDisplayValue(row, widget.configuration.uniConfiguration.cardDisplay.titleColumn, " ")
                      }}</text
                    ></view
                  >
                  <table-checkbox
                    v-if="widget.configuration.rowSelectType != 'no'"
                    :checked="selectedRowKeys.includes(row[rowKey])"
                    @checkboxSelected="onSelectRow(row)"
                    :radio="widget.configuration.rowSelectType == 'radio'"
                  />
                </view>
              </template>

              <view class="row-extend-columns">
                <view class="column" v-for="col in widget.configuration.uniConfiguration.cardDisplay.extendInfoColumn">
                  <view class="row-label">{{ columnMap[col].title }}</view>
                  <view class="row-content">{{ getColumnDisplayValue(row, col) }}</view>
                </view>
              </view>
              <view slot="actions">
                <SubformButton
                  position="rowEnd"
                  :button="widget.configuration.rowButton"
                  @button-click="(e) => onSubformButtonClick(e, row)"
                  :buttonPredicate="(button) => rowEndButtonPredicate(button, row)"
                />
              </view>
            </uni-card>

            <view
              v-if="rows.length == 0"
              style="padding: 5px; text-align: center; color: var(--text-color-placeholder)"
            >
              <text> 暂无数据 </text>
            </view>
          </view>
        </template>
        <view v-else-if="widget.configuration.uniConfiguration.layout == 'list-item'"> </view>
      </uni-collapse-item>
    </uni-collapse>

    <uni-popup ref="headerButtons" type="bottom" safeArea backgroundColor="#fff">
      <view>
        <SubformButton
          :button="widget.configuration.headerButton"
          :buttonPredicate="headButtonPredicate"
          @button-click="onSubformButtonClick"
        >
        </SubformButton>
      </view>
    </uni-popup>
    <uni-popup ref="rowButtons" type="bottom" safeArea backgroundColor="#fff">
      <view>
        <SubformButton
          v-if="editRowIndex != undefined"
          :button="widget.configuration.rowButton"
          :buttonPredicate="(button) => rowEndButtonPredicate(button, editRowIndex)"
          @button-click="(button) => onSubformButtonClick(button, editRow, editRowIndex)"
        >
        </SubformButton>
      </view>
    </uni-popup>
    <uni-popup ref="detail" type="right" safeArea backgroundColor="#fff">
      <view style="width: 100vw">
        <uni-nav-bar
          shadow
          left-icon="left"
          class="edit-subform-row-navbar"
          :title="currentSelectedForm.formData && currentSelectedForm.formData.$$id == null ? '新增' : '编辑'"
          right-text="保存"
          @clickLeft="closePopup('detail')"
          @clickRight="onConfirmEditRow"
        />
        <scroll-view
          v-if="rowFormRendered"
          scroll-y="true"
          style="height: calc(100vh - var(--status-bar-height) - 80px); background-color: #fff"
        >
          <w-dyform
            :options="{
              labelPosition: 'top',
              formDefinition,
              inheritForm: currentSelectedForm,
              formUuid: widget.configuration.formUuid,
              isSubform: true,
            }"
            ref="subDyform"
          />
        </scroll-view>
      </view>
    </uni-popup>
    <view v-if="proxyFormWidgets.length > 0" style="display: none">
      <template v-for="w in proxyFormWidgets">
        <w-widget
          :widget="w"
          :isSubformCell="true"
          :form="proxyForm"
          @change="(e) => onProxyWidgetChange(e, w)"
          :ref="'proxyField_' + w.configuration.code"
        />
      </template>
    </view>
  </view>
</template>
<style lang="less"></style>
<script type="text/babel">
import formElement from "../w-dyform/form-element.mixin";
import "./index.scss";
import SubformButton from "./component/subform-button.vue";
import { debounce } from "lodash";
import TableCheckbox from "./component/table-checkbox.vue";
const framework = require("wellapp-uni-framework");
export default {
  mixins: [formElement],
  props: {},
  components: {
    "w-dyform": () => import("../w-dyform/w-dyform.vue"),
    "w-widget": () => import("../w-widget/w-widget.vue"),
    SubformButton,
    TableCheckbox,
  },
  computed: {
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
  },
  data() {
    return {
      formUuid: this.widget.configuration.formUuid, //从表的表单UUID
      formElementRules: {},
      columns: [],
      rows: [],
      rowForms: {},
      formDefinitionVjson: undefined,
      formDefinition: {
        widgets: [],
      },
      currentForm: null,
      rowFormRendered: false,
      popupRef: [],
      rowKey: "$$id",
      selectedRowKeys: [],
      selectedRows: [],
      subformDefaultData: {},
      rowKeyMap: {},
      unCollapsed: !(this.widget.configuration.defaultCollapse && this.widget.configuration.isCollapse),
      rowDataAsMD5: "-1",
      checkAll: false,
      editRow: undefined,
      editRowIndex: undefined,
      tableShowColumnNum: this.widget.configuration.uniConfiguration.mobileShowColumnNum || 2,
    };
  },
  beforeCreate() {},
  created() {
    this.currentSelectedForm = {};
    this.proxyFormWidgets = [];
    this.proxyForm = this.dyform.createTempDyform(this.formUuid, framework.utils.generateId());
    this.emitRowFormDataChange = debounce(this.emitRowFormDataChange.bind(this), 500);
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
  },
  methods: {
    getRowIndex(row) {
      let index = -1;
      for (let i = 0, len = this.rows.length; i < len; i++) {
        if ((this.rows[i].$$id = row.$$id)) {
          return index;
        }
      }
      return index;
    },
    onClickPopupTableRowButton(row) {
      this.editRow = row;
      this.editRowIndex = this.getRowIndex(row);
      this.$refs.rowButtons.open();
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
    onCollapsed() {
      this.unCollapsed = !this.unCollapsed;
    },
    onProxyWidgetChange(e, wgt) {
      for (let i = 0, len = this.rows.length; i < len; i++) {
        this.setRowDisplayValue(this.rows[i]);
      }
      // console.log("代理组件变更", wgt.configuration.code, this.rows);
    },
    setRowDisplayValue(row) {
      for (let col of this.widget.configuration.columns) {
        let displayValue = this.$refs["proxyField_" + col.dataIndex][0].$refs.widget.displayValue(row[col.dataIndex]);
        if (displayValue != undefined) {
          this.$set(row.__MODEL__.label, col.dataIndex, displayValue);
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
      }
      if (data && data.__MODEL__) {
        row.__MODEL__ = deepClone(data.__MODEL__);
      } else {
        for (let k in data) {
          // 显示值设置：如果是真实值/显示值组件，需要等待获取实际数据
          // row.__MODEL__.label[k] = this.colIsLabelValWidget[k] ? "" : data[k];
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
    onSubformButtonClick(button, row, rowIndex) {
      if ("addRow" == button.id) {
        this.openNewRowPopup();
      } else if ("editRow" == button.id) {
        this.currentSelectedForm = this.rowForms[row.$$id];
        console.log("当前编辑从表行", this.currentSelectedForm);
        this.rowFormRendered = true;
        this.openPopup("detail");
      } else if ("copyRow" == button.id) {
        let copyRow = framework.utils.deepClone(row);
        copyRow.$$id = framework.utils.generateId();
        let formData = framework.utils.deepClone(this.rowForms[row.$$id].formData);
        formData.$$id = copyRow.$$id;
        let sf = this.form.createSubform(this.formUuid, framework.utils.generateId());
        sf.formData = formData;
        this.rowForms[formData.$$id] = sf;
        this.rows.push(copyRow);
      } else if ("moveUpRow" == button.id || "moveDownRow" == button.id) {
        this.swapRows("moveUpRow" == button.id ? "forward" : undefined, rowIndex);
      } else if ("delRow" == button.id) {
        if (rowIndex != undefined) {
          let $$id = this.rows[rowIndex].$$id;
          this.removeSelectedRowByKey($$id);
          this.rowForms[$$id] = null;
          this.rows.splice(rowIndex, 1);
          this.form.deleteSubform(this.formUuid, rowIndex);
        } else {
          // 删除所选的数据行
          for (let i = 0; i < this.rows.length; i++) {
            this.removeSelectedRowByKey(this.rows[i][this.rowKey]);
            this.form.deleteSubform(this.formUuid, i);
            this.rows.splice(i--, 1);
          }
          this.checkAll = this.rows.length > 0 && this.selectedRowKeys.length == this.rows.length;
          this.$refs.headerButtons.close();
        }
      }
      this.$refs.rowButtons.close();
    },

    removeSelectedRowByKey(key) {
      let j = this.selectedRowKeys.indexOf(key);
      if (j != -1) {
        for (let x = 0, len = this.selectedRows.length; x < len; x++) {
          if (this.selectedRowKeys.includes(this.selectedRows[x][this.rowKey])) {
            this.selectedRows.splice(x--, 1);
            break;
          }
        }
        this.selectedRowKeys.splice(j, 1);
      }
    },
    rowEndButtonPredicate(button, row) {
      let rowIndex = this.getRowIndex(row);
      if (button.id == "moveUpRow" && rowIndex == 0) {
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
          if (button._title == undefined) {
            button._title = button.title;
          }
          button.title = button._title + ` (${this.selectedRowKeys.length})`;
        }
        return this.selectedRowKeys.length > 0;
      }

      if (
        ["moveUpRow", "moveDownRow"].includes(button.id) &&
        ((this.widget.configuration.layout == "form-tabs" && this.rows.length < 1) ||
          (this.widget.configuration.layout !== "form-tabs" && this.selectedRowKeys.length == 0))
      ) {
        return false;
      }

      return true;
    },

    onConfirmEditRow() {
      // 校验表单
      let _this = this;
      this.$refs.subDyform.validateFormData(function () {
        if (_this.currentSelectedForm.formData.$$id == null) {
          _this.currentSelectedForm.formData.$$id = _this.currentSelectedForm.namespace;
          _this.rows.push(_this.currentSelectedForm.formData);
          _this.rowForms[_this.currentSelectedForm.formData.$$id] = _this.currentSelectedForm;
          _this.form.pushSubform(_this.formUuid, _this.currentSelectedForm);
          _this.rowKeyMap[_this.currentSelectedForm.namespace] = _this.rows[_this.rows.length - 1];
        } else {
          // 更新表单字段值到行上
          for (let key in _this.currentSelectedForm.formData) {
            _this.rowKeyMap[_this.currentSelectedForm.namespace][key] = _this.currentSelectedForm.formData[key];
          }
        }

        _this.setRowDisplayValue(_this.rowKeyMap[_this.currentSelectedForm.namespace]);
        _this.currentSelectedForm = {};
        _this.rowFormRendered = false;
        _this.closePopup("detail");
      });
      // this.$refs.rowForm
      //   .validate()
      //   .then((valid) => {
      //     if (_this.currentSelectedForm.formData.$$id == null) {
      //       _this.currentSelectedForm.formData.$$id = framework.utils.generateId();
      //       _this.rows.push(_this.currentSelectedForm.formData);
      //       _this.rowForms[_this.currentSelectedForm.formData.$$id] = _this.currentSelectedForm;
      //     }

      //     _this.currentSelectedForm = {};
      //     _this.rowFormRendered = false;
      //     _this.closePopup("detail");
      //   })
      //   .catch(() => {});
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
      this.openPopup("detail");
      this.closePopup("headerButtons");
      let sf = this.form.createTempDyform(this.formUuid, framework.utils.generateId());

      sf.displayState = this.dyform.displayState;
      sf.formId = this.widget.configuration.formId;
      sf.formData = { __MODEL__: { label: {} } };
      // this.rowForms[sf.namespace] = sf;
      this.currentSelectedForm = sf;
      this.rowFormRendered = true;
      // for(let i=0,len = this.widget.configuration.columns.length;i<len;i++){
      //   sf.formData.__MODEL__[this.widget.configuration.columns[i].dataIndex] = null;
      // }
    },

    onClickPopupHeaderButtons() {
      this.$refs.headerButtons.open();
    },
    updateLatestColumnInfo() {
      let p1 = new Promise((resolve, reject) => {
        this.fetchFormDefinition().then((definition) => {
          if (definition) {
            let json = JSON.parse(definition.definitionVjson);
            this.formDefinitionVjson = json;
            let fieldMap = {};
            for (let i = 0, len = json.fields.length; i < len; i++) {
              fieldMap[json.fields[i].configuration.code] = json.fields[i];
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
      for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
        let col = this.widget.configuration.columns[i];
        if (col.defaultDisplayState === "hidden") {
          continue;
        }
        this.columns.push(col);
      }
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
  },
  watch: {
    rows: {
      deep: true,
      handler(v) {
        this.emitRowFormDataChange();
      },
    },
  },
};
</script>
