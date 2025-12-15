<template>
  <EditWrapper
    :widget="widget"
    :showWidgetName="false"
    :index="index"
    :widgetsOfParent="widgetsOfParent"
    :designer="designer"
    :parent="parent"
    :title="widget.configuration.title"
    class="e-widget-subform"
  >
    <WidgetSubform :widget="widget" :widgetsOfParent="widgetsOfParent" :index="index" v-if="designer.terminalType == 'pc'">
      <template slot="designFooter">
        <div
          v-if="widget.configuration != undefined && widget.configuration.footerWidgets != undefined && widget.configuration.enableFooter"
          :class="widget.configuration.enableFooter ? 'footer-draggable' : undefined"
        >
          <draggable
            :list="widget.configuration.footerWidgets"
            v-bind="{ group: dragGroup, ghostClass: 'ghost', animation: 200 }"
            handle=".widget-drag-handler"
            :style="{ width: '100%', 'min-height': '30px', outline: '1px dotted #e8e8e8' }"
            :move="onDragMove"
            @add="e => onDragAdd(e, widget.configuration.footerWidgets)"
            @paste.native="onDraggablePaste"
          >
            <template v-for="(wgt, index) in widget.configuration.footerWidgets">
              <WDesignItem
                :widget="wgt"
                :key="wgt.id"
                :index="index"
                :widgetsOfParent="widget.configuration.footerWidgets"
                :designer="designer"
                :parent="widget"
                :ref="'item_label_' + index"
                :dragGroup="dragGroup"
              />
            </template>
          </draggable>
        </div>
      </template>
    </WidgetSubform>
    <template v-else>
      <div class="e-widget-subform-mobile">
        <div>
          <div class="title">
            {{ widget.configuration.title }}
          </div>
        </div>
        <div v-if="widget.configuration.uniConfiguration.layout == 'form-inline'" class="form-inline-card">
          <div class="card-body">
            <div class="row-title">{{ vRowTitleColumnDisplayName }}</div>
            <div class="row-columns">
              <div class="column" v-for="(col, c) in vMobileShowColumnsExceptTitle">
                {{ col }}
              </div>
            </div>
          </div>
        </div>
        <a-table
          v-if="widget.configuration.uniConfiguration.layout == 'table'"
          rowKey="id"
          :pagination="false"
          size="small"
          :bordered="false"
          :columns="vMobileShowColumns"
          :data-source="[]"
        ></a-table>
      </div>
      <div
        v-if="widget.configuration != undefined && widget.configuration.footerWidgets != undefined && widget.configuration.enableFooter"
        :class="widget.configuration.enableFooter ? 'footer-draggable' : undefined"
        v-show="widget.configuration.uniConfiguration.layout == 'table'"
      >
        <draggable
          :list="widget.configuration.footerWidgets"
          v-bind="{ group: dragGroup, ghostClass: 'ghost', animation: 200 }"
          handle=".widget-drag-handler"
          :style="{ width: '100%', 'min-height': '30px', outline: '1px dotted #e8e8e8' }"
          :move="onDragMove"
          @add="e => onDragAdd(e, widget.configuration.footerWidgets)"
          @paste.native="onDraggablePaste"
        >
          <template v-for="(wgt, index) in widget.configuration.footerWidgets">
            <WDesignItem
              :widget="wgt"
              :key="wgt.id"
              :index="index"
              :widgetsOfParent="widget.configuration.footerWidgets"
              :designer="designer"
              :parent="widget"
              :ref="'item_label_' + index"
              :dragGroup="dragGroup"
            />
          </template>
        </draggable>
      </div>
    </template>
  </EditWrapper>
</template>
<style lang="less">
.e-widget-subform-mobile {
  padding: 0 var(--w-padding-xs);
  .title {
    font-weight: normal;
    color: var(--w-text-color-dark);
    font-size: var(--w-font-size-lg);
    padding: 12px 0;
    .line {
      width: 5px;
      height: 10px;
      background-color: var(--w-primary-color);
      display: inline-block;
      border-radius: 2px;
    }
  }

  .form-inline-card {
    min-height: 100px;

    .card-body {
      background-image: e('linear-gradient(to bottom, rgba(var(--w-primary-color-rgb), 0.12), rgba(var(--w-primary-color-rgb), 0.02))');
      border-radius: 4px;
      border: 1px solid var(--w-primary-color-1);
      padding: var(--w-padding-sm) var(--w-padding-xs);
      color: var(--w-text-color-mobile);
      font-size: var(--w-font-size-base);
      height: auto;
      min-height: 50px;

      .row-title {
        color: var(--w-text-color-mobile);
        font-weight: bold;
        margin-bottom: var(--w-margin-sm);
      }

      .row-columns {
        .column {
          color: var(--w-text-color-base);
          line-height: 24px;
        }
      }
    }
  }
}

.e-widget-subform {
  .footer-draggable {
    &::before {
      content: '该区域可拖拽组件';
      position: absolute;
      left: 50%;
      transform: translateX(-50%);
      font-size: 12px;
      color: #cecece;
      bottom: 5px;
    }
  }
}
</style>
<script type="text/babel">
import formEditWidgetMixin from '../../mixin/formEditWidgetMixin';
import draggable from '@framework/vue/designer/draggable';
import { generateId, deepClone } from '@framework/vue/utils/util';
import WidgetSubform from '../widget-subform.vue';
export default {
  mixins: [formEditWidgetMixin, draggable],
  name: 'EWidgetSubform',
  props: {},
  components: { WidgetSubform },
  computed: {
    columnLabelValueOptions() {
      let options = [];
      if (this.widget.configuration.columns) {
        for (let c of this.widget.configuration.columns) {
          options.push({
            label: c.title,
            value: c.dataIndex
          });
        }
      }
      return options;
    },
    defaultEvents() {
      return [
        {
          id: 'setVisible',
          title: '设置为显示或者隐藏',
          eventParams: [
            {
              paramKey: 'visible',
              remark: '是否显示',
              valueSource: {
                inputType: 'select', // multi-select , checkbox , radio, input
                options: [
                  { label: '显示', value: 'true' },
                  { label: '隐藏', value: 'false' }
                ]
              }
            }
          ]
        },
        {
          id: 'setColumnVisible',
          title: '设置列为显示或者隐藏',
          eventParams: [
            {
              paramKey: 'visible',
              remark: '是否显示',
              valueSource: {
                inputType: 'select', // multi-select , checkbox , radio, input
                options: [
                  { label: '显示', value: 'true' },
                  { label: '隐藏', value: 'false' }
                ]
              }
            },
            {
              paramKey: 'dataIndex',
              remark: '列字段',
              valueSource: {
                inputType: 'multi-select', // multi-select , checkbox , radio, input
                options: this.columnLabelValueOptions
              }
            }
          ]
        },
        {
          id: 'setColumnEditable',
          title: '设置列为编辑或者不可编辑',
          eventParams: [
            {
              paramKey: 'editable',
              remark: '是否可编辑',
              valueSource: {
                inputType: 'select', // multi-select , checkbox , radio, input
                options: [
                  { label: '可编辑', value: 'true' },
                  { label: '不可编辑', value: 'false' }
                ]
              }
            },
            {
              paramKey: 'dataIndex',
              remark: '列字段',
              valueSource: {
                inputType: 'multi-select', // multi-select , checkbox , radio, input
                options: this.columnLabelValueOptions
              }
            }
          ]
        },

        {
          id: 'setColumnRequired',
          title: '设置列为必填或者不必填',
          eventParams: [
            {
              paramKey: 'required',
              remark: '是否必填',
              valueSource: {
                inputType: 'select', // multi-select , checkbox , radio, input
                options: [
                  { label: '必填', value: 'true' },
                  { label: '非必填', value: 'false' }
                ]
              }
            },
            {
              paramKey: 'dataIndex',
              remark: '列字段',
              valueSource: {
                inputType: 'multi-select', // multi-select , checkbox , radio, input
                options: this.columnLabelValueOptions
              }
            }
          ]
        }
      ];
    },
    vMobileShowColumns() {
      let cols = [];
      for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
        if (this.widget.configuration.columns[i].defaultDisplayState !== 'hidden') {
          cols.push(this.widget.configuration.columns[i]);
        }
      }
      return cols;
    },
    vMobileShowColumnsExceptTitle() {
      let cols = [];
      for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
        if (
          this.widget.configuration.columns[i].defaultDisplayState !== 'hidden' &&
          this.widget.configuration.columns[i].dataIndex !== this.widget.configuration.formInlineTitleField
        ) {
          cols.push(this.widget.configuration.columns[i].title);
        }
      }
      return cols;
    },
    vRowTitleColumnDisplayName() {
      if (this.widget.configuration.columns.length > 0) {
        for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
          if (this.widget.configuration.columns[i].dataIndex == this.widget.configuration.formInlineTitleField) {
            return this.widget.configuration.columns[i].title;
          }
        }
      }
      return '请选择标题列';
    }
  },
  data() {
    return {};
  },
  beforeCreate() {},
  created() {
    if (!this.widget.configuration.hasOwnProperty('uniConfiguration')) {
      this.$set(this.widget.configuration, 'uniConfiguration', {
        layout: 'form-inline'
      });
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    getColumnTitle(dataIndex) {
      if (this.widget.configuration.columns.length > 0) {
        for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
          if (this.widget.configuration.columns[i].dataIndex == dataIndex) {
            return this.widget.configuration.columns[i].title;
          }
        }
      }
      return '列标题';
    }
  }
};
</script>
