<template>
  <div>
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model ref="form" :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }">
          <div v-show="designer.terminalType == 'pc'">
            <a-form-model-item label="合并单元格">
              <a-input-number v-model="span" :min="1" :max="maxColumn" @change="onChangeMergeCol" />
            </a-form-model-item>
            <a-form-model-item label="合并行数">
              <a-input-number v-model="rowspan" :min="1" @change="onChangeMergeRow" />
            </a-form-model-item>
          </div>
          <a-form-model-item label="单元标题">
            <a-input v-model="widget.configuration.label" @keyup.tab.native="onTabKeyup" ref="inputLabel" allow-clear>
              <template slot="addonAfter">
                <a-switch size="small" :checked="!widget.configuration.labelHidden" @change="onChangeLabelHidden" title="显示标题" />
                <WI18nInput
                  v-show="!widget.configuration.labelHidden"
                  :widget="parent"
                  :designer="designer"
                  :target="widget.configuration"
                  :code="widget.id + '.label'"
                  v-model="widget.configuration.label"
                />
              </template>
            </a-input>
          </a-form-model-item>
          <template v-if="designer.terminalType == 'mobile'">
            <a-form-model-item label="单元格布局">
              <a-radio-group v-model="widget.configuration.uniConfiguration.layout" button-style="solid" size="small">
                <a-radio-button value="">自动</a-radio-button>
                <a-radio-button value="horizontal">水平</a-radio-button>
                <a-radio-button value="vertical">垂直</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
          </template>
          <DefaultVisibleConfiguration
            compact
            :designer="designer"
            :configuration="widget.configuration"
            :widget="widget"
          ></DefaultVisibleConfiguration>
        </a-form-model>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>
<style></style>
<script type="text/babel">
import { debounce } from 'lodash';
import { generateId } from '@framework/vue/utils/util';

export default {
  name: 'WidgetFormItemConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    parent: Object,
    configuration: Object
  },
  data() {
    return {
      span: this.widget.configuration.span,
      rowspan: this.widget.configuration.rowspan
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    maxColumn() {
      let column = this.parent ? this.parent.configuration.column : 1;
      return column;
    },
    fieldVarOptions() {
      let opt = [];
      for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
        let info = this.designer.SimpleFieldInfos[i];
        opt.push({
          label: info.name,
          value: info.code
        });
      }
      return opt;
    }
  },
  created() {
    if (this.widget.configuration && this.widget.configuration.uniConfiguration == undefined) {
      this.$set(this.widget.configuration, 'uniConfiguration', { layout: '' });
    }
  },
  methods: {
    onTabKeyup: debounce(function () {
      if (this.parent != undefined) {
        let items = this.parent.configuration.items;
        for (let i = 0, len = items.length; i < len; i++) {
          if (items[i].id == this.widget.id) {
            if (i < len - 1) {
              // 自动切换到下一个选中
              this.designer.setSelected(items[i + 1], this.parent);
            } else {
              // 自动扩充
              document.querySelector(`#design-item_${this.parent.id}`).__vue__.$refs.component.addItem();
              this.$nextTick(() => {
                this.designer.setSelected(this.parent.configuration.items[this.parent.configuration.items.length - 1], this.parent);
              });
            }
          }
        }
      }
    }, 200),
    onChangeLabelHidden() {
      this.$set(this.widget.configuration, 'labelHidden', !this.widget.configuration.labelHidden);
    },
    onChangeMergeCol() {
      let remove = [],
        currentRow = {},
        currentIndex = 0;
      try {
        let el = document.querySelector(`#design-item_${this.parent.id}`);
        if (el && el.__vue__.$refs.component) {
          let vGrid = el.__vue__.$refs.component.vGrid;
          for (let i = 0, len = vGrid.length; i < len; i++) {
            let row = vGrid[i];
            for (let j = 0, jlen = row.length; j < jlen; j++) {
              if (row[j] && row[j].id == this.widget.id) {
                currentRow = row;
                currentIndex = j;
                /**
                 * 需要修正正确的 span ，避免乱填超过该行可容纳的 span
                 */
                let span = this.span - 1,
                  from = j + 1;
                while (span >= 1) {
                  if (row[from] == undefined || row[from].supplement_span) {
                    this.span = from - j; // 可以合并的上限 span 值
                    break;
                  }
                  if (row[from].merged) {
                    this.span = from - j;
                    break;
                  }
                  if (row[from].id) {
                    span--;
                    if (
                      (row[from].configuration.labelWidgets == undefined || row[from].configuration.labelWidgets.length == 0) &&
                      row[from].configuration.widgets.length == 0
                    ) {
                      // 单元格无放置任何组件，则直接删掉作为合并空格使用
                      remove.push(row[from++].id);
                    }
                    continue;
                  }
                  if (Object.keys(row[from]).length == 0) {
                    // 代表空格子，是可以占位的
                    span--;
                    from++;
                  }
                }
                return;
              }
            }
          }
        }
      } catch (error) {
        console.log(error);
      } finally {
        this.removeItemsByIds(remove);
        this.$set(this.widget.configuration, 'span', this.span);
        // 补充单元格，避免从合并数减少时候单元格上移动
        let emptyCount = 0,
          _span = this.span;
        if (this.span < currentRow.length && currentIndex != currentRow.length - 1) {
          for (let i = currentIndex + _span, len = currentRow.length; i < len; i++) {
            if (Object.keys(currentRow[i]).length == 0) {
              // 补充一个单元格
              emptyCount++;
            }
          }

          if (emptyCount > 0) {
            this.insertItemAfterById(this.widget.id, emptyCount);
          }
        }

        /**
         * 把同行的包含组件的单元格下移
         */
        // let remainSpan = currentRow.length - this.span;
        // for (let i = currentIndex + 1; i < currentRow.length; i++) {
        //   if (currentRow[i].id && (currentRow[i].configuration.labelWidgets.length > 0 || currentRow[i].configuration.widgets.length > 0)) {
        //     // 该行剩余组件要判断是否另其一行，避免合并造成其余行元素发生大范围变更
        //     if (remainSpan >= currentRow[i].configuration.span) {
        //       remainSpan = remainSpan - currentRow[i].configuration.span;
        //     } else {
        //       // 增加 span 达到另起一行的效果
        //       remainSpan = currentRow.length;
        //       for (let j = i; j < currentRow.length; j++) {
        //         if (
        //           currentRow[j].id &&
        //           (currentRow[j].configuration.labelWidgets.length > 0 || currentRow[j].configuration.widgets.length > 0)
        //         ) {
        //           if (j == currentRow.length - 1) {
        //             currentRow[j].configuration.span = remainSpan;
        //           } else {
        //             remainSpan = remainSpan - currentRow[j].configuration.span;
        //           }
        //         }
        //       }

        //       break;
        //     }
        //   }
        // }
      }
    },

    insertItemAfterById(id, count) {
      let items = this.parent.configuration.items;
      for (let i = 0; i < items.length; i++) {
        if (items[i].id == id) {
          while (count-- > 0) {
            items.splice(i + 1, 0, this.defaultEmptyItem());
          }
          break;
        }
      }
    },

    defaultEmptyItem() {
      return {
        wtype: 'WidgetFormItem',
        title: '单元格',
        id: `form-item-${generateId()}`,
        configuration: {
          label: '',
          span: 1,
          hidden: false,
          widgets: [],
          labelWidgets: [],
          required: false
        }
      };
    },
    removeItemsByIds(ids) {
      if (ids.length > 0) {
        let items = this.parent.configuration.items;
        for (let i = 0; i < items.length; i++) {
          if (ids.length == 0) {
            break;
          }
          let j = ids.indexOf(items[i].id);
          if (j != -1) {
            items.splice(i--, 1);
            ids.splice(j, 1);
          }
        }
      }
    },
    onChangeMergeRow() {
      let remove = [],
        currentRowIndex = null,
        currentColIndex = null,
        oldRowspan = this.widget.configuration.rowspan || 1;
      let el = document.querySelector(`#design-item_${this.parent.id}`);
      let vGrid = el.__vue__.$refs.component.vGrid;
      if (this.rowspan > vGrid.length) {
        this.rowspan = vGrid.length;
      }
      try {
        let rowspan = this.rowspan - 1;
        if (rowspan >= 1) {
          for (let i = 0, len = vGrid.length; i < len; i++) {
            let row = vGrid[i];
            for (let j = 0, jlen = row.length; j < jlen; j++) {
              if (row[j] && row[j].id == this.widget.id) {
                currentColIndex = j;
                currentRowIndex = i;
                let from = i + 1;
                while (rowspan >= 1) {
                  let nextRow = vGrid[from];
                  if (nextRow == undefined) {
                    return;
                  }
                  let col = nextRow[j];
                  if (col.merged || Object.keys(col).length == 0) {
                    rowspan--;
                    from++;
                    continue;
                  }
                  if (col && col.id) {
                    // 判断当前单元格是否存在组件配置，如果不存在则直接删除，达到合并不下移的效果
                    if (
                      col.configuration.widgets.length == 0 &&
                      (col.configuration.labelWidgets == undefined || col.configuration.labelWidgets.length == 0)
                    ) {
                      // 单元格无放置任何组件，则直接删掉作为合并空格使用
                      remove.push(col.id);
                      from++;
                      rowspan--;
                    } else {
                      return;
                    }
                  }
                }
                if (rowspan == 0) {
                  return;
                }
                return;
              }
            }
          }
        }
      } catch (error) {
      } finally {
        this.removeItemsByIds(remove);
        this.$set(this.widget.configuration, 'rowspan', this.rowspan);
        // 补充取消合并的单元格，避免单元格因为减少合并导致移动
        if (oldRowspan > this.rowspan) {
          if (currentRowIndex == null) {
            for (let i = 0, len = vGrid.length; i < len; i++) {
              let row = vGrid[i];
              for (let j = 0, jlen = row.length; j < jlen; j++) {
                if (row[j] && row[j].id == this.widget.id) {
                  currentRowIndex = i;
                  currentColIndex = j;
                  break;
                }
              }
              if (currentRowIndex != null) {
                break;
              }
            }
          }
          let _span = this.widget.configuration.span,
            _rowspan = oldRowspan - this.rowspan;
          for (let i = currentRowIndex + 1, len = vGrid.length; i < len && _rowspan-- > 0; i++) {
            let row = vGrid[i];
            while (_span-- > 0) {
              row[currentColIndex++] = {};
            }
            for (let j = currentColIndex - 1; j >= 0; j--) {
              if (row[j].id) {
                this.insertItemAfterById(row[j].id, this.widget.configuration.span);
                break;
              }
            }
          }
        }
      }
    }
  },
  activated() {
    if (this.widget.configuration) {
      this.span = this.widget.configuration.span;
      this.rowspan = this.widget.configuration.rowspan;
    }
  },
  beforeMount() {
    if (this.widget.title != '单元格') {
      this.widget.title = '单元格'; // 修正名称 (旧名为表单列存在歧义)
    }
  },
  mounted() {
    // this.$refs.inputLabel.focus();
  },
  activated() {
    // this.$refs.inputLabel.focus();
  },
  watch: {
    'widget.configuration.span': {
      handler(v) {
        this.span = v;
      }
    }
  }
};
</script>
