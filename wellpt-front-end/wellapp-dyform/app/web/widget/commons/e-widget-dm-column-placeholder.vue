<template>
  <div>
    <a-skeleton />
    <a-modal :width="400" :maskClosable="false" title="选择展示组件" :visible="visible" :mask="false" @cancel="onCancel">
      <a-form-model :label-col="{ span: 7 }" :wrapper-col="{ span: 17 }" :model="form" :rules="rules" ref="form">
        <a-form-model-item label="组件类型" prop="wtype">
          <a-select :options="widgetOptions" style="width: 100%" v-model="form.wtype" @change="onChangeWtype" />
        </a-form-model-item>
        <div v-if="form.wtype == 'WidgetFormDatePicker$Range'">
          <a-form-model-item label="开始字段" prop="start">
            <a-select :options="dataRangeStartFieldOptions" style="width: 100%" v-model="form.start" allow-clear />
          </a-form-model-item>
          <a-form-model-item label="结束字段" prop="end">
            <a-select :options="dataRangeEndFieldOptions" style="width: 100%" v-model="form.end" allow-clear />
          </a-form-model-item>
        </div>
      </a-form-model>

      <template slot="footer">
        <a-button type="primary" @click="onConfirmSelectWidget">确定</a-button>
      </template>
    </a-modal>
  </div>
</template>
<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
// import { generateId, deepClone, queryString } from '@framework/vue/utils/util';

export default {
  name: 'EWidgetDmColumnPlaceholder',
  mixins: [editWgtMixin],
  inject: ['widgetMeta', 'dataModelColumns', 'fieldCodeUsed'],
  data() {
    return {
      widgetOptions: [],
      visible: true,
      autoRefresh: false,
      form: {
        wtype: undefined,
        start: undefined,
        end: undefined
      }
    };
  },
  beforeDestroy() {},
  beforeCreate() {},
  components: {},
  computed: {
    dataModelColMap() {
      let map = {};
      for (let i = 0, len = this.dataModelColumns.length; i < len; i++) {
        map[this.dataModelColumns[i].column] = this.dataModelColumns[i];
      }
      return map;
    },
    rules() {
      if (this.form.wtype == 'WidgetFormDatePicker$Range') {
        return {
          start: [{ required: true, message: '请选择开始字段', trigger: ['blur', 'change'] }],
          end: [{ required: true, message: '请选择结束字段', trigger: ['blur', 'change'] }]
        };
      }
      return [];
    },
    dataRangeStartFieldOptions() {
      let opt = [];
      for (let i = 0, len = this.dataModelColumns.length; i < len; i++) {
        if (
          this.dataModelColumns[i].dataType == 'timestamp' &&
          this.dataModelColumns[i].column != this.form.end &&
          !this.fieldCodeUsed.includes(this.dataModelColumns[i].column)
        ) {
          opt.push({
            label: this.dataModelColumns[i].title,
            value: this.dataModelColumns[i].column
          });
        }
      }
      return opt;
    },
    dataRangeEndFieldOptions() {
      let opt = [];
      for (let i = 0, len = this.dataModelColumns.length; i < len; i++) {
        if (
          this.dataModelColumns[i].dataType == 'timestamp' &&
          this.dataModelColumns[i].column != this.form.start &&
          !this.fieldCodeUsed.includes(this.dataModelColumns[i].column)
        ) {
          opt.push({
            label: this.dataModelColumns[i].title,
            value: this.dataModelColumns[i].column
          });
        }
      }
      return opt;
    }
  },
  created() {
    this.designer.clearSelected();

    let dataTypeWidgets = {
      varchar: [
        'WidgetFormInput',
        'WidgetFormInput$Textarea',
        'WidgetFormSelect',
        'WidgetFormSelect$SelectTree',
        'WidgetFormCheckbox',
        'WidgetFormRadio',
        'WidgetFormSerialNumber',
        'WidgetFormTag',
        'WidgetFormSwitch',
        'WidgetFormFileUpload',
        'WidgetFormFileUpload$picture',
        'WidgetFormRichTextEditor',
        'WidgetFormJobSelect',
        'WidgetFormOrgSelect',
        'WidgetFormCascader'
      ],
      timestamp: ['WidgetFormDatePicker', 'WidgetFormDatePicker$Range'],
      number: ['WidgetFormInputNumber'],
      clob: ['WidgetFormInput$Textarea', 'WidgetFormRichTextEditor']
    };
    this.widgetMap = {};
    let includes = dataTypeWidgets[this.widget.configuration.dataType];
    this.form.wtype = includes.length > 0 ? includes[0] : undefined;

    // 获取关联字段
    let relaCols = [];
    for (let i = 0, len = this.dataModelColumns.length; i < len; i++) {
      if (this.dataModelColumns[i].relaColumns) {
        for (let j = 0, jlen = this.dataModelColumns[i].relaColumns.length; j < jlen; j++) {
          relaCols.push(this.dataModelColumns[i].relaColumns[j].column.toLowerCase());
        }
      }
    }
    if (includes.length) {
      for (let w in this.widgetMeta) {
        if (includes.includes(w)) {
          // 日期范围关联字段处理
          // FIXME: 是否限制标记了关联、被关联字段的使用
          // if (w == 'WidgetFormDatePicker$Range') {
          //   if (this.widget.configuration.relaColumns == undefined || relaCols.includes(this.widget.configuration.column)) {
          //     // 日期字段无关联字段，或者该字段为关联字段的情况下，都不允许单独使用日期范围
          //     continue;
          //   }
          //   let relaUsed = false;
          //   if (this.widget.configuration.relaColumns) {
          //     for (let r of this.widget.configuration.relaColumns) {
          //       // 被使用了关联字段，则不允许使用日期范围组件显示
          //       if (this.fieldCodeUsed.includes(r.column.toLowerCase())) {
          //         relaUsed = true;
          //         break;
          //       }
          //     }
          //   }
          //   if (relaUsed) {
          //     continue;
          //   }
          // }
          this.widgetOptions.push({
            label: this.widgetMeta[w].name,
            value: w
          });
          this.widgetMap[w] = this.widgetMeta[w];
        }
      }
    }
  },
  methods: {
    onChangeWtype() {
      if (this.form.wtype == 'WidgetFormDatePicker$Range' && this.form.start == undefined) {
        this.form.start = this.widget.configuration.column;
      }
    },
    onCancel() {
      this.visible = false;
      if (this.widgetsOfParent != undefined) {
        this.widgetsOfParent.splice(this.index, 1); // 移除该占位组件
        this.designer.clearSelected();
      }
    },
    replaceWidget() {
      if (this.widgetsOfParent != undefined) {
        this.widgetsOfParent.splice(this.index, 1); // 移除该占位组件
        this.designer.clearSelected();
      }
      let { title, column, notNull, dataType, scale, remark, unique, length, relaColumns } =
        this.form.wtype == 'WidgetFormDatePicker$Range' ? this.dataModelColMap[this.form.start] : this.widget.configuration;
      let field = {
        ...this.widgetMap[this.form.wtype],
        column: { title, code: column, notNull, dataType, length, scale, unique, relaColumns }
      };
      let widget = this.designer.copyNewWidget(field);
      // if (widget.configuration != undefined) {
      //   widget.configuration.code = column;
      //   if (dataType == 'clob') {
      //     widget.configuration.dbDataType = '16';
      //   }
      // }
      if (this.form.wtype == 'WidgetFormDatePicker$Range') {
        widget.column.relaColumns = [{ column: this.form.end, name: this.dataModelColMap[this.form.end].title }];
      }

      this.widgetsOfParent.push(widget);
      let _this = this;
      this.$nextTick(() => {
        this.designer.setSelected(widget, _this.parent);
      });
      // setTimeout(() => {
      //   // _this.designer.setSelected(widget, _this.parent);
      // }, 200);
    },
    onConfirmSelectWidget() {
      let _this = this;
      this.$refs.form.validate(pass => {
        if (pass) {
          _this.replaceWidget();
        }
      });
    }
  },
  mounted() {}
};
</script>
