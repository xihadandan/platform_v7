<template>
  <div :class="['widget-form-layout']" v-show="vShow" v-if="deviceVisible">
    <div class="layout-header" v-if="widget.configuration.titleHidden !== true">
      <label class="title">
        <span v-if="widget.configuration.titleIcon" style="margin-right: var(--w-margin-3xs)">
          <Icon :type="widget.configuration.titleIcon" :size="24" color="var(--w-primary-color)" />
        </span>
        {{ $t('title', widget.configuration.title) }}
      </label>
    </div>

    <table :class="[!widget.configuration.bordered ? 'no-border' : '']">
      <colgroup>
        <col
          v-for="(col, i) in vColgroup"
          :style="{
            width: widget.configuration.columnWidthAvg
              ? undefined /** 不需要设置百分比 */
              : col.width != undefined
              ? col.width + col.widthUnit
              : undefined
          }"
          :key="'col_' + i"
        />
      </colgroup>
      <tbody>
        <template v-for="(row, r) in vGrid">
          <tr :key="'row_' + r" :style="{}">
            <template v-for="(item, i) in row">
              <td
                v-if="item.id != undefined"
                :key="'td_' + r + '_' + i"
                :colspan="item.configuration.span"
                :rowspan="item.configuration.rowspan"
              >
                <a-row
                  type="flex"
                  :style="{}"
                  :class="[
                    widget.configuration.layout,
                    widget.configuration.colLabelContentMergeShow && widget.configuration.layout !== 'vertical'
                      ? 'label-content-merged'
                      : ''
                  ]"
                >
                  <a-col
                    v-if="!item.configuration.labelHidden"
                    :flex="
                      widget.configuration.layout !== 'vertical' && !widget.configuration.colLabelContentMergeShow
                        ? labelColumnWidth
                        : undefined
                    "
                    :class="['label-col', widget.configuration.labelAlign == 'right' ? 'right' : 'left']"
                    :style="{}"
                  >
                    <a-row type="flex">
                      <a-col>
                        <label
                          v-show="item.configuration.label"
                          :class="[requireItemChildren[item.id].length > 0 ? 'required' : '', widget.configuration.colon ? 'colon' : '']"
                        >
                          {{ $t(item.id + '.label', item.configuration.label) }}
                        </label>
                      </a-col>
                      <a-col
                        :style="{ 'padding-left': '4px', width: widget.configuration.layout !== 'vertical' ? '0' : '' }"
                        flex="auto"
                        v-show="item.configuration.labelWidgets && item.configuration.labelWidgets.length > 0"
                      >
                        <template v-for="(wgt, ii) in item.configuration.labelWidgets">
                          <component
                            :key="'label_' + wgt.id"
                            :is="wgt.wtype"
                            :widget="wgt"
                            :index="ii"
                            :widgetsOfParent="item.configuration.labelWidgets"
                            :parent="item"
                          ></component>
                        </template>
                      </a-col>
                    </a-row>
                  </a-col>
                  <a-col
                    flex="auto"
                    class="content-col"
                    :data-item-id="item.id"
                    ref="contentCol"
                    :style="{ width: widget.configuration.layout !== 'vertical' ? '0' : '' }"
                  >
                    <template v-for="(wgt, ii) in item.configuration.widgets">
                      <component
                        :key="wgt.id"
                        :is="wgt.wtype"
                        :widget="wgt"
                        :index="ii"
                        :widgetsOfParent="item.configuration.widgets"
                        :parent="item"
                      ></component>
                    </template>
                  </a-col>
                </a-row>
              </td>

              <!-- 合并占位列如果前一个存在跨行合并，则需要占位一个td -->
              <td
                :key="'td_' + r + '_' + i"
                v-if="
                  row[i - 1] != undefined &&
                  row[i - 1].configuration != undefined &&
                  row[i - 1].configuration.rowspan != undefined &&
                  row[i - 1].configuration.rowspan > 1 &&
                  item.merged
                "
                :style="{}"
              >
                <div></div>
              </td>
            </template>
          </tr>
        </template>
      </tbody>
    </table>
    <!-- 隐藏字段校验代理项 -->
    <template v-for="(obj, i) in proxyHiddenFieldWidgets">
      <FormItemHiddenField
        :widget="obj.widget"
        :key="'proxySubHField' + i"
        :parent="obj.item"
        @fieldStateChange="e => onHideFieldStateChange(e, obj)"
      ></FormItemHiddenField>
    </template>
  </div>
</template>
<style lang="less">
.widget-form-layout {
  > table > colgroup > col:not(:first-child) {
    width: e('calc( 50% - 200px )');
  }
}
</style>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import { expressionCompare, deepClone } from '@framework/vue/utils/util';
import { debounce, throttle } from 'lodash';
import './css/index.less';
import FormItemHiddenField from './form-item-hidden-field.vue';
export default {
  name: 'WidgetFormLayout',
  extends: FormElement,
  mixins: [widgetMixin],
  data() {
    let requireItemChildren = {};
    for (let i = 0, len = this.widget.configuration.items.length; i < len; i++) {
      requireItemChildren[this.widget.configuration.items[i].id] = [];
    }
    return {
      hiddenWidgets: new Set(),
      itemSubWidgetAllHidden: [],
      hiddenItems: [],
      requireItemChildren,
      proxyHiddenFieldWidgets: [],
      calcVisibleItemIds: []
    };
  },

  beforeCreate() {},
  components: { FormItemHiddenField },
  computed: {
    labelColumnWidth() {
      return (this.widget.configuration.labelColumnWidth || 120) + 'px';
    },

    vGrid() {
      let column = this.widget.configuration.column,
        items = deepClone(this.widget.configuration.items);

      // 预先生成行/列二维数组
      let grid = Array.from({ length: items.length }, () => {
          return Array.from({ length: column }, () => {
            return {};
          });
        }),
        index = 0,
        itemLength = items.length;

      for (let i = 0; i < grid.length; i++) {
        let row = grid[i];
        let unusedSpan = 0;
        for (let j = 0; j < row.length; j++) {
          // 空载列数大于总列数时，重新计数空载列数
          if (unusedSpan >= column) {
            unusedSpan = 0;
          }
          if (index < itemLength) {
            if (row[j].merged) {
              // 被合并占用，不能放置控件
              // 不可见，占位列数分配给同行兄弟节点
              if (row[j].visible === false) {
                if (j > 0) {
                  // 往前分配
                  let prev = 1;
                  while (j - prev >= 0) {
                    if (row[j - prev] && row[j - prev].configuration) {
                      row[j - prev].configuration.span = row[j - prev].configuration.span + row[j].span;
                      break;
                    }
                    prev++;
                  }
                } else if (j == 0 && item.configuration.span != column) {
                  // 往后分配
                  unusedSpan += item.configuration.span || 1;
                }
              }

              continue;
            }
            let item = items[index++];
            let visible = !this.hiddenItems.includes(item.id) && this.calcVisibleItemIds.includes(item.id);

            let span = (item.configuration.span || 1) + unusedSpan; // 当前预计占位数为 当前准备放置的组件占位数 + 未使用的占位数
            span = Math.min(column, span); // span 不能超过最大值
            if (!visible) {
              /**
               * 同行不可见的单元格，把隐藏单元格的占格数给兄弟节点
               */
              // 不可见，占位列数分配给同行兄弟节点
              if (j > 0) {
                // 往前分配
                let prev = 1;
                while (j - prev >= 0) {
                  if (row[j - prev] && row[j - prev].configuration) {
                    // 前一个存在组件，则加长其占位数
                    row[j - prev].configuration.span = row[j - prev].configuration.span + span;
                    break;
                  }
                  prev++;
                }
              } else if (j == 0) {
                if (item.configuration.span != column) {
                  // 当前隐藏的是第一个元素，那么往后分配
                  unusedSpan += item.configuration.span || 1;
                }
              }

              /**
               * 跨行不可见的单元格，需要往下标记合并单元格为隐藏，并记录span，等待该行装载组件时候处理占位数据
               *
               */

              let rowspan = item.configuration.rowspan || 1;
              if (rowspan > 1) {
                // 向下合并行
                for (let r = 1; r < rowspan; r++) {
                  if (grid[i + r] != undefined) {
                    grid[i + r][j] = { merged: true, visible: false, span: item.configuration.span || 1 };
                  } else {
                    break;
                  }
                }
              }
              if (j == 0) {
                // 列标前移，继续使用该格进行分配
                j = -1;
              }
              continue;
            } else {
              item.configuration.span = span;
            }

            // 修正占位: 如果占位数超过被合并预占的单元格，则需要减小占位数
            if (span > 1 && row[j + (span - 1)] && row[j + (span - 1)].merged) {
              span -= 1;
              item.configuration.span = span;
            }

            if (span > 1 && j == row.length - 1 && unusedSpan == 0) {
              // 最后一个要修正 span 只能为 1 ，否则会多出一列 （兼容旧版的配置 span 没有做填写限制的情况）
              span = 1;
              item.configuration.span = span;
            }

            row[j] = item;
            unusedSpan = 0;

            // 行合并
            let rowspan = item.configuration.rowspan || 1;
            if (rowspan > 1) {
              // 向下合并行
              for (let r = 1; r < rowspan; r++) {
                if (grid[i + r] != undefined) {
                  grid[i + r][j] = { merged: true };
                  if (span > 1) {
                    // 往右合并
                    for (let s = 1; s < span; s++) {
                      if (grid[i + r][j + s] != undefined) {
                        grid[i + r][j + s] = { merged: true };
                      }
                    }
                  }
                } else {
                  break;
                }
              }
            }

            if (span > 1) {
              j = j + (span - 1); // 位移格子，表示合并占用格子
            }
          } else {
            if (row[j].merged) {
              // 被合并占用，不能放置控件
              // 不可见，占位列数分配给同行兄弟节点
              if (row[j].visible === false) {
                if (j > 0) {
                  // 往前分配
                  let prev = 1;
                  while (j - prev >= 0) {
                    if (row[j - prev] && row[j - prev].configuration) {
                      row[j - prev].configuration.span = row[j - prev].configuration.span + row[j].span;
                      break;
                    }
                    prev++;
                  }
                } else if (j == 0 && item.configuration.span != column) {
                  // 往后分配
                  unusedSpan += item.configuration.span || 1;
                }
              }
              continue;
            }
          }
        }
        if (index < itemLength && i == grid.length - 1) {
          // 还有控件未放置，补充行
          grid.push(
            Array.from({ length: column }, () => {
              return {};
            })
          );
        }
      }

      for (let i = 0; i < grid.length; i++) {
        let emptyRow = true;
        for (let j = 0, jlen = grid[i].length; j < jlen; j++) {
          if (grid[i][j] && grid[i][j].id) {
            emptyRow = false;
            break;
          }
        }
        if (emptyRow) {
          grid.splice(i--, 1);
        }
      }

      // console.log('生成表单布局网格: ', grid);
      return grid;
    },
    vColgroup() {
      let colgroup = deepClone(this.widget.configuration.colgroup || []);
      if (colgroup.length == 0) {
        for (let i = 0, len = this.widget.configuration.column; i < len; i++) {
          colgroup.push({
            width: undefined,
            widthUnit: undefined
          });
        }
      }
      // if (this.vGrid.length > 0) {
      //   let firstRow = this.vGrid[0];
      //   // 为了更好呈现列宽度效果，如果第一行其中一个列被合并了，就不使用设置的宽度
      //   for (let i = 0, len = firstRow.length; i < len; i++) {
      //     if (Object.keys(firstRow[i]).length == 0) {
      //       colgroup[i].width = undefined;
      //     }
      //   }
      // }
      return colgroup;
    },
    // 相关方法暴露为事件，提供外部调用
    defaultEvents() {
      return [
        { id: 'showFormLayout', title: '显示表单布局' },
        { id: 'hideFormLayout', title: '隐藏表单布局' }
      ];
    }
  },
  created() {
    if (!this.hidden) {
      // 非隐藏的情况下，要判断其下的子元素是否都是隐藏的
      let len = this.widget.configuration.items.length,
        hiddenItemCnt = 0;
      for (let i = 0; i < len; i++) {
        let item = this.widget.configuration.items[i];
        item.configuration.hidden = this.isItemSubWidgetAllHide(item);
        hiddenItemCnt += item.configuration.hidden ? 1 : 0;
        if (item.configuration.hidden) {
          this.hiddenItems.push(item.id);
        }
        this.setItemRequired(item);
      }

      if (hiddenItemCnt === len && len > 0) {
        this.hidden = true;
      }
    }

    // 初始计算显示的单元格集合
    for (let item of this.widget.configuration.items) {
      let configuration = item.configuration,
        visible = !configuration.hidden;
      if (
        // 无计算显隐性
        visible &&
        configuration.defaultVisible &&
        configuration.defaultVisibleVar &&
        (!configuration.defaultVisibleVar.enable || configuration.defaultVisibleVar.conditions.length == 0)
      ) {
        this.calcVisibleItemIds.push(item.id);
      }
    }

    this.proxySubFieldEvent = debounce(this.proxySubFieldEvent.bind(this), 200);
  },
  methods: {
    // 判断是否为表单基础组件
    isDyformElement(item) {
      return (
        (item.category == 'basicComponent' && (!item.scope || (item.scope && item.scope.indexOf('dyform') > -1))) ||
        item.category == 'basic'
      );
    },
    setItemRequired(rowItem) {
      let setRequiredItem = item => {
        if (item.configuration.widgets) {
          let jlen = item.configuration.widgets.length,
            requiredCnt = 0;
          for (let j = 0; j < jlen; j++) {
            let itemWidget = deepClone(item.configuration.widgets[j]);
            if (this.isDyformElement(itemWidget)) {
              if (itemWidget.configuration) {
                if (this.form.formElementRules && this.form.formElementRules[item.configuration.widgets[j].id]) {
                  if (
                    this.form.formElementRules[item.configuration.widgets[j].id].required &&
                    !this.requireItemChildren[rowItem.id].includes(item.configuration.widgets[j].id)
                  ) {
                    this.requireItemChildren[rowItem.id].push(item.configuration.widgets[j].id);
                  }
                  break;
                }
                if (
                  item.configuration.widgets[j].configuration.required &&
                  !this.requireItemChildren[rowItem.id].includes(item.configuration.widgets[j].id)
                ) {
                  this.requireItemChildren[rowItem.id].push(item.configuration.widgets[j].id);
                  break;
                }
              }
            } else {
              if (itemWidget.configuration.cols || itemWidget.configuration.widgets) {
                if (itemWidget.configuration.cols && itemWidget.configuration.cols.length > 0) {
                  // 栅格
                  itemWidget.configuration.widgets = itemWidget.configuration.cols;
                }
                if (itemWidget.configuration.widgets.length) {
                  setRequiredItem(itemWidget);
                }
              }
            }
          }

          rowItem.configuration.required = requiredCnt > 0; // 至少一个必填字段存在
        }
      };
      setRequiredItem(rowItem);
    },
    isItemSubWidgetAllHide(item) {
      if (item.configuration.widgets) {
        let jlen = item.configuration.widgets.length,
          hiddenCnt = 0;
        if (this.itemSubWidgetAllHidden.includes(item.id)) {
          return true;
        }
        for (let j = 0; j < jlen; j++) {
          let subWidget = item.configuration.widgets[j];
          if (this.form.formElementRules && this.form.formElementRules[subWidget.id]) {
            if (this.form.formElementRules[subWidget.id].hidden) {
              hiddenCnt++;
            }
            continue;
          }
          if (
            subWidget.configuration.defaultVisible === false &&
            (subWidget.configuration.defaultVisibleVar == undefined ||
              !subWidget.configuration.defaultVisibleVar.enable ||
              (subWidget.configuration.defaultVisibleVar.enable && subWidget.configuration.defaultVisibleVar.conditions.length == 0))
          ) {
            hiddenCnt++;
            continue;
          }

          // 组件默认的配置
          if (subWidget.configuration.defaultDisplayState === 'hidden') {
            hiddenCnt++;
          }
        }
        return hiddenCnt == jlen && jlen > 0;
      }
    },
    tdWidth(item, i, row) {
      // if(row[i+1]!=undefined &&)
    },
    showFormLayout() {
      this.hidden = false;
      this.hiddenByRule = false;
      this.calculateWidgetVisible = true;
    },
    hideFormLayout() {
      this.hidden = true;
      this.hiddenByRule = true;
      this.calculateWidgetVisible = false;
    },
    childElementHiddenChange($evt, item) {
      item.configuration.hidden = $evt.hidden;
    },
    handleFormItemChildVisibleChange() {
      for (let i = 0, len = this.widget.configuration.items.length; i < len; i++) {
        // 绑定处理单元格内的子元素显示变更
        let item = this.widget.configuration.items[i];
        this.pageContext
          .offEvent(`Widget:${item.id}:Child:VisibleChange`)
          .handleEvent(`Widget:${item.id}:Child:VisibleChange`, ({ id, visible }) => {
            let subHiddenCount = 0;
            for (let i = 0, wLen = item.configuration.widgets.length; i < wLen; i++) {
              if (item.configuration.widgets[i].id == id) {
                if (visible) {
                  this.hiddenWidgets.delete(id);
                } else {
                  this.hiddenWidgets.add(id);
                }
              }
              if (this.hiddenWidgets.has(item.configuration.widgets[i].id)) {
                subHiddenCount++;
              }
            }
            // 单元格下子元素都隐藏了，则此单元格也需要隐藏
            item.configuration.hidden = subHiddenCount == item.configuration.widgets.length;
            let index = this.itemSubWidgetAllHidden.indexOf(item.id);
            if (item.configuration.hidden && index == -1) {
              this.itemSubWidgetAllHidden.push(item.id);
            } else if (!item.configuration.hidden && index != -1) {
              this.itemSubWidgetAllHidden.splice(index, 1);
            }
            if (item.configuration.hidden && !this.hiddenItems.includes(item.id)) {
              this.hiddenItems.push(item.id);
            } else if (!item.configuration.hidden && this.hiddenItems.includes(item.id)) {
              this.hiddenItems.splice(this.hiddenItems.indexOf(item.id), 1);
            }

            // 判断当前布局下的单元格是否的隐藏状况
            subHiddenCount = 0;
            for (let i = 0; i < len; i++) {
              if (this.widget.configuration.items[i].configuration.hidden) {
                subHiddenCount++;
              }
            }

            if ((this.hidden && this.hiddenWidgets.size !== len) || (!this.hidden && this.hiddenWidgets.size == len)) {
              this.hidden = this.hiddenWidgets.size == len;
              // 状态发生变更，向上通知
              this.emitVisibleChange(!this.hidden);
            }
          });
      }
    },
    requiredHook() {
      let _this = this;
      for (let i = 0, len = this.widget.configuration.items.length; i < len; i++) {
        let item = this.widget.configuration.items[i];
        this.dyform.handleEvent('onChildRequiredChanged.' + item.id, function (required, id) {
          if (required && !_this.requireItemChildren[item.id].includes(id)) {
            _this.requireItemChildren[item.id].push(id);
          }
          if (!required && _this.requireItemChildren[item.id].includes(id)) {
            _this.requireItemChildren[item.id].splice(_this.requireItemChildren[item.id].indexOf(id), 1);
          }
          item.configuration.required = required;
        });
      }
    },

    proxySubFieldEvent() {
      let _this = this;
      this.proxyHiddenFieldWidgets.splice(0, this.proxyHiddenFieldWidgets.length);
      for (let i = 0, len = this.widget.configuration.items.length; i < len; i++) {
        // FIXME: 通过栅格进行布局的字段需要处理
        let item = this.widget.configuration.items[i];
        if (this.hiddenItems.includes(item.id) || !this.calcVisibleItemIds.includes(item.id)) {
          let subWidgets = item.configuration.widgets;
          subWidgets.forEach(wgt => {
            if (wgt.configuration.isDatabaseField) {
              this.proxyHiddenFieldWidgets.push({ widget: wgt, item: item });
            }
          });
        }
      }
    },
    onHideFieldStateChange(e, record) {
      let { currentState, oldState } = e,
        { widget, item } = record;
      widget.configuration.defaultDisplayState = currentState.editable ? 'edit' : 'unedit';
      // 通过事件触发的显隐，要删除对应的规则，否则组件初始化还是以规则为主
      if (oldState.hidden !== currentState.hidden && this.form.formElementRules && this.form.formElementRules[widget.id]) {
        delete this.form.formElementRules[widget.id].hidden;
      }

      // 通过事件触发的编辑，要删除对应的规则，否则组件初始化还是以规则为主
      if (oldState.editable !== currentState.editable && this.form.formElementRules && this.form.formElementRules[widget.id]) {
        delete this.form.formElementRules[widget.id].editable;
        delete this.form.formElementRules[widget.id].hidden;
      }

      item.configuration.hidden = this.isItemSubWidgetAllHide(item);
      if (!item.configuration.hidden && this.hiddenItems.includes(item.id)) {
        this.hiddenItems.splice(this.hiddenItems.indexOf(item.id), 1);
      }
      if (item.configuration.hidden && !this.hiddenItems.includes(item.id)) {
        this.hiddenItems.push(item.id);
      }
    },
    reCalculateVisibleItemIds() {
      let ids = [],
        promises = [];
      if (!this.vShow) {
        this.calcVisibleItemIds.splice(0, this.calcVisibleItemIds.length);
        return;
      }
      for (let i = 0, len = this.widget.configuration.items.length; i < len; i++) {
        let item = this.widget.configuration.items[i];
        let configuration = item.configuration,
          visible = !configuration.hidden;
        if (visible && !this.designMode && configuration.defaultVisible != undefined) {
          promises.push(this.calculateVisibleByCondition(configuration.defaultVisibleVar, configuration.defaultVisible));
        } else {
          promises.push(Promise.resolve(true));
        }
      }
      Promise.all(promises).then(results => {
        for (let i = 0, len = results.length; i < len; i++) {
          if (results[i]) {
            ids.push(this.widget.configuration.items[i].id);
          }
          this.calcVisibleItemIds.splice(0, this.calcVisibleItemIds.length, ...ids);
        }
      });
    },
    afterChangeableDependDataChanged() {
      this.reCalculateVisibleItemIds();
    }
  },
  beforeMount() {},
  mounted() {
    // 发布锚点显示变更通知
    this.pageContext.registerMountedInst(this, this.widget.id);
    this.requiredHook();
    this.handleFormItemChildVisibleChange();
  },

  watch: {
    hiddenItems: {
      handler(v, o) {
        this.proxySubFieldEvent();
      }
    },
    calcVisibleItemIds: {
      handler(v, o) {
        this.proxySubFieldEvent();
      }
    }
  }
};
</script>
