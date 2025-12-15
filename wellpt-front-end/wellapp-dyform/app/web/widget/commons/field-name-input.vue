<template>
  <a-form-model-item label="字段名称" prop="name">
    <a-input v-model="widget.configuration.name" @change="onInputNameChange">
      <template slot="prefix" v-if="parentIsWidgetFormItem">
        <a-tooltip
          :title="(widget.configuration.syncLabel2FormItem ? '已开启' : '已关闭') + '绑定名称到表单列显示'"
          placement="topRight"
          :arrowPointAtCenter="true"
        >
          <a-icon :type="widget.configuration.syncLabel2FormItem ? 'link' : 'disconnect'" @click.stop="onClickForSyncLabel2FormItem" />
        </a-tooltip>
      </template>
      <template slot="addonAfter">
        <a-tooltip
          :title="(widget.configuration.fieldNameVisible ? '显示' : '隐藏') + '字段名称'"
          placement="topRight"
          :arrowPointAtCenter="true"
        >
          <a-switch v-model="widget.configuration.fieldNameVisible" size="small" @change="onChangeFieldNameVisible" />
        </a-tooltip>
        <WI18nInput
          v-show="widget.configuration.fieldNameVisible"
          :widget="widget"
          :designer="designer"
          :code="widget.id"
          v-model="widget.configuration.name"
          @change="onChangeFieldI18n"
        />
      </template>
    </a-input>
  </a-form-model-item>
</template>
<style></style>
<script type="text/babel">
import { debounce } from 'lodash';

export default {
  name: 'FieldNameInput',
  inject: ['designer'],
  __ANT_NEW_FORM_ITEM: true,
  props: {
    widget: Object
  },
  data() {
    return {
      // parentIsWidgetFormItem: false
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    parentIsWidgetFormItem() {
      let node = this.designer.widgetTreeMap[this.widget.id];
      if (node) {
        let parent = node.parentKey ? this.designer.widgetTreeMap[node.parentKey] : null;
        return parent && parent.wtype == 'WidgetFormItem';
      }
      return false;
    }
  },
  created() {
    if (!this.widget.configuration.hasOwnProperty('syncLabel2FormItem')) {
      this.$set(this.widget.configuration, 'syncLabel2FormItem', true);
    }
    if (!this.widget.configuration.hasOwnProperty('fieldNameVisible')) {
      this.$set(this.widget.configuration, 'fieldNameVisible', true);
    }
    // if (!this.widget.configuration.hasOwnProperty('name')) {
    //   this.$set(this.widget.configuration, 'name', undefined);
    // }
  },
  methods: {
    onChangeFieldNameVisible() {
      if (this.parentIsWidgetFormItem) {
        const label = this.widget.configuration.fieldNameVisible ? this.widget.configuration.name : undefined;
        const parentWidget = this.designer.widgetIdMap[this.designer.widgetTreeMap[this.widget.id]['parentKey']];
        if (parentWidget) {
          parentWidget.configuration.label = label;
        }
      }
    },
    onInputNameChange: debounce(function () {
      this.$emit('change', this.widget.configuration.name);
      this.widget.title = this.widget.configuration.name;
      this.onSyncLabelChange(true);
    }, 300),
    onClickForSyncLabel2FormItem() {
      this.widget.configuration.syncLabel2FormItem = !this.widget.configuration.syncLabel2FormItem;
      this.onSyncLabelChange(true);
    },
    onChangeFieldI18n() {
      this.onSyncLabelChange(true);
    },
    getParentFormItem() {
      if (this.widget.configuration.syncLabel2FormItem && this.parentIsWidgetFormItem) {
        let formItem = this.designer.widgetTreeMap[this.designer.widgetTreeMap[this.widget.id].parentKey];
        if (formItem) {
          let formLayout = this.designer.widgetIdMap[formItem.parentKey];
          if (formLayout) {
            for (let item of formLayout.configuration.items) {
              if (item.id == formItem.key) {
                return { item, formLayout };
              }
            }
          }
        }
      }
      return undefined;
    },
    onSyncLabelChange(force) {
      if (this.widget.configuration.syncLabel2FormItem) {
        let { item, formLayout } = this.getParentFormItem() || {};
        if (item) {
          const label = this.widget.configuration.fieldNameVisible ? this.widget.configuration.name : undefined;
          item.configuration.label = label;
          let fieldI18n = this.widget.configuration.i18n;
          if (fieldI18n != undefined) {
            let syncI18n = {};
            for (let key in fieldI18n) {
              syncI18n[key] = { [formLayout.id + '.' + item.id + '.label']: fieldI18n[key][this.widget.id + '.' + this.widget.id] };
            }
            this.$set(item.configuration, 'i18n', syncI18n);
          }
        }
      }
    }
  },
  mounted() {
    if (this.widget.configuration.syncLabel2FormItem) {
      this.onSyncLabelChange();
    }
  }
};
</script>
