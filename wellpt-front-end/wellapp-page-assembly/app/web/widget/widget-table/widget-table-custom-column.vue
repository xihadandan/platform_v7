<template>
  <a-popover
    ref="popoverCustomTable"
    v-if="widget.configuration.enableCustomTable"
    trigger="click"
    placement="leftTop"
    :destroyTooltipOnHide="true"
    :arrowPointAtCenter="true"
    @visibleChange="onPopCustomTableVisibleChange"
    v-model="customTablePopoverVisible"
    :getPopupContainer="getPopupContainerNearestPs(500)"
  >
    <template slot="title">
      {{ $t('WidgetTable.customTable.title', '自定义表格') }}
      <a-tooltip placement="right" v-if="this.widget.configuration.isColumnsGroup">
        <template slot="title">
          <ul style="padding-inline-start: 20px; margin-block-end: 0px">
            <template>
              <li>
                {{ $t('WidgetTable.customTable.tip6', '表头分组内的列, 以表头分组配置顺序排列') }}
              </li>
              <li v-if="widget.configuration.isColumnsGroup">
                {{ $t('WidgetTable.customTable.tip7', '分组显示的列中排最前面的列位置为分组插入位置') }}
              </li>
            </template>
          </ul>
        </template>
        <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
      </a-tooltip>
    </template>
    <template slot="content">
      <a-table
        :showHeader="false"
        rowKey="id"
        :pagination="false"
        size="small"
        style="width: 370px"
        :scroll="{ y: 400 }"
        :columns="customColTableColumns"
        :data-source="tempCustomTableConfig.customTableColumns"
        :class="['widget-table-custom-column-table']"
      >
        <template slot="title" v-if="widget.configuration.freezeColumn">
          <a-space>
            <label>{{ $t('WidgetTable.customTable.freezeTableLabel', '冻结表格') }}</label>
            <span>
              {{ $t('WidgetTable.customTable.freezeBeforeLabel', '前') }}
              <a-input-number :min="0" v-model="tempCustomTableConfig.leftFreezeColNum" @change="onChangeCustomFreezeCol" />
              {{ $t('WidgetTable.customTable.columnLabel', '列') }}
            </span>
            <span>
              {{ $t('WidgetTable.customTable.freezeAfterLabel', '后') }}
              <a-input-number :min="0" v-model="tempCustomTableConfig.rightFreezeColNum" @change="onChangeCustomFreezeCol" />
              {{ $t('WidgetTable.customTable.columnLabel', '列') }}
            </span>
            <a-tooltip placement="bottomRight" :arrowPointAtCenter="true">
              <template slot="title">
                <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                  <li>
                    {{ $t('WidgetTable.customTable.tip1', '设置表格的冻结列, 冻结的列不受表格左右拖动影响') }}
                  </li>
                  <li>
                    {{ $t('WidgetTable.customTable.tip2', '冻结列为0时表示不冻结') }}
                  </li>
                  <li>
                    {{ $t('WidgetTable.customTable.tip3', '开启冻结列, 默认冻结操作列') }}
                  </li>
                  <li>
                    {{ $t('WidgetTable.customTable.tip4', '冻结后n列时, n不包含操作列') }}
                  </li>
                  <li>
                    {{ $t('WidgetTable.customTable.tip5', '冻结前n列时, n不包含序号列') }}
                  </li>
                </ul>
              </template>
              <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
            </a-tooltip>
          </a-space>
        </template>
        <template slot="titleSlot" slot-scope="text, record">
          <a-space>
            <Icon
              :title="$t('WidgetTable.customTable.sort', '拖动排序')"
              type="pticon iconfont icon-ptkj-tuodong"
              class="drag-column-handler drag-handler"
              :style="{ cursor: 'move' }"
            ></Icon>
            <a-switch
              size="small"
              :disabled="record.customVisibleType === 'mustVisible'"
              :checked="!record.hidden"
              @change="checked => onChangeCustomColVisible(checked, record)"
            />
            {{ $t(record.id, text) }}
            <span class="ant-tag ant-tag-orange" v-show="widget.configuration.freezeColumn && record.freeze" style="line-height: 18px">
              {{ $t('WidgetTable.customTable.fixedColumnLabel', '固定列') }}
            </span>
          </a-space>
        </template>
        <template slot="footer">
          <div style="text-align: right" class="btn_has_space">
            <a-button @click.stop="onCancelCustomTable">{{ $t('WidgetTable.customTable.cancelButtonText', '取消') }}</a-button>
            <a-button @click.stop="onResetCustomTable">{{ $t('WidgetTable.customTable.resetButtonText', '恢复默认') }}</a-button>
            <a-button @click.stop="onComfirmCustomTable" type="primary">{{ $t('WidgetTable.customTable.okButtonText', '确定') }}</a-button>
          </div>
        </template>
      </a-table>
    </template>
    <a-button :title="$t('WidgetTable.customTable.title', '自定义表格')" class="icon-only">
      <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
    </a-button>
  </a-popover>
</template>

<script type="text/babel">
import { deepClone } from '@framework/vue/utils/util';
import { getPopupContainerNearestPs } from '@framework/vue/utils/function';
import draggable from '@framework/vue/designer/draggable';

export default {
  name: 'WidgetTableCustomColumn',
  mixins: [draggable],
  inject: ['widgetTableContext'],
  props: {
    widget: Object
  },
  data() {
    let data = {
      customRenderSlotOptionMaps: {},
      customColumnTitles: [],
      customTableColumns: [],
      leftFreezeColNum: undefined,
      rightFreezeColNum: undefined,
      tempCustomTableConfig: {
        customTableColumns: [],
        leftFreezeColNum: undefined,
        rightFreezeColNum: undefined
      },
      customTablePopoverVisible: false,
      customColTableColumns: [
        { title: this.$t('WidgetTable.customTable.nameColumn', '名称'), dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } }
      ]
    };

    return data;
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {
    this.setDefaultColumns();
  },
  methods: {
    $t() {
      if (this.widgetTableContext != undefined) {
        return this.widgetTableContext.$t(...arguments);
      }
      return this.$i18n.t(...arguments);
    },
    getPopupContainerNearestPs,
    getPopupContainer() {
      return getPopupContainerNearestPs(500, this.widgetTableContext.$el);
    },
    setDefaultColumns() {
      if (this.widget.configuration.enableCustomTable) {
        this.defaultColumns = [];
        for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
          if (!this.widget.configuration.columns[i].hidden) {
            this.defaultColumns.push(deepClone(this.widget.configuration.columns[i]));
          }
        }
        this.customTableColumns = deepClone(this.defaultColumns); //初始用户自定义列数据为表格默认配置的
      }

      if (this.widget.configuration.freezeColumn) {
        this.leftFreezeColNum = this.widget.configuration.leftFreezeColNum;
        this.rightFreezeColNum = this.widget.configuration.rightFreezeColNum;
      }
    },

    saveUserCustomTable(callback) {
      this.$loading(this.$t('WidgetTable.customTable.savingTip', '保存中'));
      let params = { moduleId: this.widget.wtype, functionId: 'WIDGET_TABLE_CUSTOM_COLUMNS', dataKey: `${this.widget.id}` };
      $axios
        .post(`/proxy/api/user/preferences/saveUserPreference`, {
          ...params,
          dataValue: JSON.stringify({
            columns: this.tempCustomTableConfig.customTableColumns,
            freezeColumn: {
              leftFreezeColNum: this.tempCustomTableConfig.leftFreezeColNum,
              rightFreezeColNum: this.tempCustomTableConfig.rightFreezeColNum
            }
          }),
          remark: '用户自定义表格列'
        })
        .then(({ data }) => {
          this.$loading(false);
          callback(data);
        })
        .catch(() => {
          this.$loading(false);
        });
    },
    fetchUserCustomTable(callback) {
      let params = { moduleId: this.widget.wtype, functionId: 'WIDGET_TABLE_CUSTOM_COLUMNS', dataKey: `${this.widget.id}` };
      $axios
        .get(`/api/user/preferences/get`, {
          params: { ...params }
        })
        .then(({ data }) => {
          if (data.code == 0) {
            callback(data.data ? JSON.parse(data.data.dataValue) : null);
          }
        });
    },
    onChangeCustomFreezeCol() {
      if (this.tempCustomTableConfig.leftFreezeColNum != undefined || this.tempCustomTableConfig.rightFreezeColNum != undefined) {
        let cols = this.tempCustomTableConfig.customTableColumns,
          leftFreezeColNum = this.tempCustomTableConfig.leftFreezeColNum,
          rightFreezeColNum = this.tempCustomTableConfig.rightFreezeColNum;
        let visibleIndexes = [];
        for (let i = 0, len = cols.length; i < len; i++) {
          this.$set(cols[i], 'freeze', false);
          if (!cols[i].hidden) {
            visibleIndexes.push(i);
          }
        }

        for (let i = 0, len = visibleIndexes.length; i < len; i++) {
          if (
            (leftFreezeColNum != undefined && i <= leftFreezeColNum - 1) ||
            (rightFreezeColNum != undefined && i >= len - rightFreezeColNum)
          ) {
            cols[visibleIndexes[i]].freeze = true;
          }
        }
      }
    },
    onPopCustomTableVisibleChange(visible) {
      let _this = this,
        timeout;
      if (visible) {
        this.tempCustomTableConfig.customTableColumns = deepClone(this.customTableColumns);
        this.tempCustomTableConfig.leftFreezeColNum = this.leftFreezeColNum;
        this.tempCustomTableConfig.rightFreezeColNum = this.rightFreezeColNum;
        this.onChangeCustomFreezeCol();
        let initTableDraggable = () => {
          if (_this.$refs.popoverCustomTable.$slots.content[0].elm) {
            clearTimeout(timeout);
            _this.tableDraggable(
              _this.tempCustomTableConfig.customTableColumns,
              _this.$refs.popoverCustomTable.$slots.content[0].elm.querySelector('.widget-table-custom-column-table tbody'),
              '.drag-column-handler',
              {
                afterOnEnd: () => {
                  _this.onChangeCustomFreezeCol();
                }
              }
            );
          } else {
            timeout = setTimeout(() => {
              initTableDraggable();
            }, 300);
          }
        };
        initTableDraggable();
      }
    },

    onResetCustomTable() {
      let cols = [];
      for (let i = 0, len = this.defaultColumns.length; i < len; i++) {
        let col = deepClone(this.defaultColumns[i]);
        if (this.defaultColumns[i].customVisibleType === 'defaultHidden') {
          // 默认隐藏
          col.hidden = true;
        }
        cols.push(col);
      }

      this.tempCustomTableConfig.customTableColumns = deepClone(cols);
      this.tempCustomTableConfig.leftFreezeColNum = this.widget.configuration.leftFreezeColNum;
      this.tempCustomTableConfig.rightFreezeColNum = this.widget.configuration.rightFreezeColNum;
      this.onChangeCustomFreezeCol();
    },
    emitColumns(fetched) {
      this.$emit(
        'onCustomColumnChanged',
        deepClone({
          customTableColumns: this.customTableColumns,
          leftFreezeColNum: this.leftFreezeColNum,
          rightFreezeColNum: this.rightFreezeColNum,
          fetched
        })
      );
    },
    save() {
      this.customTableColumns = deepClone(this.tempCustomTableConfig.customTableColumns);
      this.leftFreezeColNum = this.tempCustomTableConfig.leftFreezeColNum;
      this.rightFreezeColNum = this.tempCustomTableConfig.rightFreezeColNum;
    },
    onComfirmCustomTable() {
      let _this = this;
      if (this.designMode) {
        return;
      }
      this.saveUserCustomTable(data => {
        if (data.code == 0) {
          _this.save();
          _this.emitColumns();
          _this.customTablePopoverVisible = false;
        } else {
          _this.$message.error(this.$t('WidgetTable.customTable.saveServerError', '自定义表格数据服务异常'));
        }
      });
    },
    onChangeCustomColVisible(checked, record) {
      record.hidden = !checked;
      this.onChangeCustomFreezeCol();
    },
    onCancelCustomTable() {
      this.customTablePopoverVisible = false;
    },
    compareResetTableColumns(customCols) {
      let colMap = {},
        colIds = [];
      for (let i = 0, len = this.defaultColumns.length; i < len; i++) {
        colMap[this.defaultColumns[i].id] = this.defaultColumns[i];
        colIds.push(this.defaultColumns[i].id);
      }
      let newAddColIds = [].concat(colIds),
        customColIds = [];
      //剔除用户自定义列已经在默认表格列中不再存在的情况下
      for (let i = 0; i < customCols.length; i++) {
        let id = customCols[i].id;
        if (!colIds.includes(id)) {
          customCols.splice(i, 1);
          i--;
        } else {
          // 取最新的表格列定义的配置
          customColIds.push(id);
          let hidden = customCols[i].hidden; // 获取用户自定义时候是否显示
          customCols[i] = deepClone(colMap[id]);
          customCols[i].hidden = hidden; // 用户设置的隐藏
          newAddColIds.splice(newAddColIds.indexOf(id), 1); // 移出
        }
      }

      // 新增的列
      if (newAddColIds) {
        for (let i = newAddColIds.length - 1; i >= 0; i--) {
          let id = newAddColIds[i],
            idx = colIds.indexOf(id);
          let _col = deepClone(colMap[id]);
          if (_col.customVisibleType === 'defaultHidden') {
            // 默认不显示的情况
            _col.hidden = true;
          }
          if (idx == 0) {
            // 插入第一列前
            customCols.splice(0, 0, _col);
          } else if (idx == colIds.length - 1) {
            customCols.push(_col);
          } else {
            let j = customColIds.indexOf(colIds[idx - 1]);
            customCols.splice(j === 0 ? 0 : j - 1, 0, _col);
          }
        }
      }
      this.customTableColumns = deepClone(customCols);
    }
  },
  beforeMount() {
    let _this = this;
    // 用户自定义表格时候，需要计算展示列
    if (this.widget.configuration.enableCustomTable) {
      this.fetchUserCustomTable(customCols => {
        // console.log('返回用户自定义列信息: ', customCols);
        if (customCols) {
          if (this.widget.configuration.freezeColumn && customCols.freezeColumn) {
            this.leftFreezeColNum = customCols.freezeColumn.leftFreezeColNum;
            this.rightFreezeColNum = customCols.freezeColumn.rightFreezeColNum;
          }
          // 重新设置列
          _this.compareResetTableColumns(customCols.columns);
        } else {
          // 无用户自定义表格时候，使用默认的表格列展示
          for (let i = 0, len = _this.customTableColumns.length; i < len; i++) {
            if (_this.customTableColumns[i].customVisibleType === 'defaultHidden') {
              // 默认隐藏
              _this.customTableColumns[i].hidden = true;
            }
          }
        }
        _this.emitColumns(true);
      });
    }
  },
  mounted() {}
};
</script>
<style lang="less" scoped>
.drag-handler {
  color: var(--w-text-color-light);
  &:hover {
    color: var(--w-text-color-dark);
  }
}
</style>
