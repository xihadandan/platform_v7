<template>
  <div>
    <a-form-model-item label="启用查询">
      <a-switch v-model="configuration.enableKeywordSearch"></a-switch>
    </a-form-model-item>
    <template v-if="configuration.enableKeywordSearch">
      <a-form-model-item label="查询图标">
        <WidgetDesignModal
          title="选择图标"
          :zIndex="10000"
          :width="640"
          dialogClass="pt-modal widget-icon-lib-modal"
          :bodyStyle="{ height: '560px' }"
          :maxHeight="560"
          mask
          bodyContainer
        >
          <IconSetBadge v-model="configuration.keywordSearchIcon" />
          <template slot="content">
            <WidgetIconLib v-model="configuration.keywordSearchIcon" />
          </template>
        </WidgetDesignModal>
      </a-form-model-item>
      <a-form-model-item label="查询组件">
        <a-select
          v-model="configuration.keywordSearchWidgetIds"
          mode="multiple"
          :options="searchWidgetOptions"
          @dropdownVisibleChange="dropdownVisibleChange"
        />
      </a-form-model-item>
      <a-form-model-item label="关键字输入框提示语">
        <a-input v-model.trim="configuration.keywordSearchPlaceholder">
          <template slot="addonAfter">
            <WI18nInput
              :widget="widget"
              :designer="designer"
              code="keywordSearchPlaceholder"
              v-model="configuration.keywordSearchPlaceholder"
              :target="configuration"
            />
          </template>
        </a-input>
      </a-form-model-item>
    </template>
  </div>
</template>

<script>
export default {
  name: 'KeywordSearchConfiguration',
  props: {
    designer: Object,
    widget: {
      type: Object,
      default: () => {}
    },
    configuration: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      searchWidgetOptions: []
    };
  },
  created() {
    this.addProperty();
    this.setSearchWidgetOptions();
  },
  methods: {
    addProperty() {
      if (this.widget.configuration.keywordSearchIcon === undefined) {
        this.$set(this.widget.configuration, 'keywordSearchIcon', 'iconfont icon-ptkj-sousuochaxun');
      }
      if (this.widget.configuration.keywordSearchPlaceholder === undefined) {
        this.$set(this.widget.configuration, 'keywordSearchPlaceholder', '请输入关键字');
      }
    },
    dropdownVisibleChange(open) {
      if (open) {
        this.setSearchWidgetOptions();
      }
    },
    setSearchWidgetOptions() {
      this.searchWidgetOptions = this.getDescendantWidgets();
    },
    // 获取子孙组件
    getDescendantWidgets() {
      let targetWidgets = [];
      const dfs = wgt => {
        if (wgt && wgt.configuration) {
          const { widgets, cols, gridItems, tabs, statePanels, steps } = wgt.configuration;
          let wgts;
          if (widgets) {
            wgts = widgets;
          } else if (cols) {
            wgts = cols;
          } else if (gridItems) {
            wgts = gridItems;
          } else if (tabs) {
            wgts = tabs;
          } else if (statePanels) {
            wgts = statePanels;
          } else if (steps) {
            wgts = steps;
          }

          if (wgts) {
            wgts.map(item => {
              if (item.wtype === 'WidgetDataManagerView') {
                item = item.configuration.WidgetTable;
              }
              if (item.wtype === 'WidgetTable' || item.wtype === 'WidgetSubform') {
                targetWidgets.push({
                  label: item.title,
                  value: item.id,
                  wtype: item.wtype,
                  configuration: item.configuration
                });
              }
              dfs(item);
            });
          }
        }
      };
      dfs(this.widget);
      return targetWidgets;
    }
  }
};
</script>
