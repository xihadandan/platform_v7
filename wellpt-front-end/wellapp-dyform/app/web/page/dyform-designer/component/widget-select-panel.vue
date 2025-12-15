<template>
  <div class="widget-design-drawer-container">
    <a-tabs tab-position="left" size="small" class="widget-select-tabs">
      <a-tab-pane key="1">
        <span slot="tab" @mouseenter="mouseenter">
          <i class="iconfont icon-a-icleftbuju"></i>
          <br />
          布局
        </span>

        <PerfectScrollbar class="sider-select-panel-scroll">
          <a-collapse :bordered="false" expandIconPosition="right" :defaultActiveKey="defaultCollapseActiveKey">
            <template v-for="(cat, i) in containerCategory">
              <a-collapse-panel :key="cat.key" v-if="filterCategory.length == 0 || filterCategory.includes(cat.key)">
                <template slot="header">
                  <i class="line" />
                  <label class="">{{ cat.title }}</label>
                </template>
                <draggable
                  tag="ul"
                  :list="vDesignWidgets[cat.key]"
                  :group="{ name: draggableConfig.dragGroup, pull: 'clone', put: false }"
                  :clone="handleWidgetClone"
                  @end="onDragWidgetEnd"
                  :sort="false"
                >
                  <template v-for="(select, index) in vDesignWidgets[cat.key]">
                    <li :key="cat.key + '_' + index" class="widget-select-item" :title="select.name">
                      <template v-if="select.iconClass">
                        <i :class="['iconfont', select.iconClass]"></i>
                      </template>
                      <template v-else>
                        <a-icon type="control" />
                      </template>
                      {{ select.name }}
                    </li>
                  </template>
                </draggable>
              </a-collapse-panel>
            </template>
          </a-collapse>
        </PerfectScrollbar>
      </a-tab-pane>
      <a-tab-pane key="2">
        <span slot="tab">
          <i class="iconfont icon-a-icleftzujian"></i>
          <br />
          组件
        </span>
        <PerfectScrollbar class="sider-select-panel-scroll">
          <a-collapse :bordered="false" expandIconPosition="right" :defaultActiveKey="defaultCollapseActiveKey">
            <template v-for="(cat, i) in componentCategory">
              <a-collapse-panel :key="cat.key" v-if="filterCategory.length == 0 || filterCategory.includes(cat.key)">
                <template slot="header">
                  <i class="line" />
                  <label class="">{{ cat.title }}</label>
                </template>
                <draggable
                  tag="ul"
                  :list="vDesignWidgets[cat.key]"
                  :group="{ name: draggableConfig.dragGroup, pull: 'clone', put: false }"
                  :clone="handleWidgetClone"
                  @end="onDragWidgetEnd"
                  :sort="false"
                >
                  <li v-for="(select, index) in vDesignWidgets[cat.key]" :key="index" class="widget-select-item" :title="select.name">
                    <template v-if="select.iconClass">
                      <i :class="['iconfont', select.iconClass]"></i>
                    </template>
                    <template v-else>
                      <a-icon type="control" />
                    </template>
                    {{ select.name }}
                  </li>
                </draggable>
              </a-collapse-panel>
            </template>
          </a-collapse>
        </PerfectScrollbar>
      </a-tab-pane>
      <a-tab-pane v-if="designFields.fields || designFields.subforms" key="3">
        <span slot="tab" @mouseenter="mouseenter">
          <a-icon type="build" />
          <br />
          字段
        </span>

        <a-collapse :bordered="false" expandIconPosition="right" defaultActiveKey="1">
          <a-collapse-panel key="1">
            <template slot="header">
              <i class="line" />
              <label class="">基础字段</label>
            </template>
            <draggable
              tag="ul"
              filter=".selected"
              :list="designFields.fields"
              :group="{ name: draggableConfig.dragGroup, pull: 'clone', put: false }"
              :clone="handleWidgetClone"
              @end="onDragWidgetEnd"
              :sort="false"
            >
              <li
                v-for="(select, index) in designFields.fields"
                :key="index"
                :class="{ 'widget-select-item': true, selected: selectedFieldIdMap[select.id] != null }"
                :title="select.name"
              >
                {{ (select.configuration && select.configuration.name) || select.name }}
              </li>
            </draggable>
          </a-collapse-panel>
        </a-collapse>
        <a-collapse :bordered="false" expandIconPosition="right" defaultActiveKey="1">
          <a-collapse-panel key="1">
            <template slot="header">
              <i class="line" />
              <label class="">从表</label>
            </template>
            <draggable
              tag="ul"
              filter=".selected"
              :list="designFields.subforms"
              :group="{ name: draggableConfig.dragGroup, pull: 'clone', put: false }"
              :clone="handleWidgetClone"
              @end="onDragWidgetEnd"
              :sort="false"
            >
              <li
                v-for="(select, index) in designFields.subforms"
                :key="index"
                :class="{ 'widget-select-item': true, selected: selectedSubformIdMap[select.id] != null }"
                :title="select.name"
              >
                {{ (select.configuration && select.configuration.formName) || select.name }}
              </li>
            </draggable>
          </a-collapse-panel>
        </a-collapse>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script type="text/babel">
import draggable from '@framework/vue/designer/draggable';
import { each, isEmpty } from 'lodash';
export default {
  name: 'WidgetSelectPanel',
  inject: ['draggableConfig'],
  props: {
    designer: Object,
    designWidgets: Object,
    designFields: {
      type: Object,
      default: function () {
        return {};
      }
    },
    designType: {
      type: String,
      default: 'dyform'
    },
    filterCategory: {
      type: Array,
      default: function () {
        return [];
      }
    }
  },
  mixins: [draggable],
  data() {
    let containerCategory = [
        { key: 'basicContainer', title: '基础布局' },
        { key: 'advanceContainer', title: '高级布局' }
      ],
      componentCategory = [
        { key: 'basicComponent', title: '基础组件' },
        { key: 'advanceComponent', title: '高级组件' }
      ],
      modelCategory = [{ key: 'msModelComponent', title: 'MS模型' }];
    let defaultCollapseActiveKey = [];
    for (let list of [containerCategory, componentCategory, modelCategory]) {
      for (let l of list) {
        defaultCollapseActiveKey.push(l.key);
      }
    }
    return {
      selectedFieldIdMap: {},
      selectedSubformIdMap: {},
      cloneIdMap: {},
      defaultCollapseActiveKey,
      containerCategory,
      componentCategory
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    vDesignWidgets() {
      let _designWidgets = [];
      for (let k in this.designWidgets) {
        let list = this.designWidgets[k];
        _designWidgets[k] = [];
        for (let l of list) {
          if (l.scope == undefined || (l.scope && (l.scope == this.designType || l.scope.includes(this.designType)))) {
            _designWidgets[k].push(l);
          }
        }
      }
      return _designWidgets;
    }
  },
  created() {},
  methods: {
    onDragWidgetEnd(e) {
      if (e.pullMode == 'clone') {
        this.designer.undoOrRedo = true;
        this.designer.setSelected(e.item._underlying_vm_);
      }
    },
    addByDbClick(wgt) {
      this.designer.addToSelectedWidget(wgt);
    },
    mouseenter(e) {
      this.designer.widgetSelectDrawerVisible = true;
    },
    handleWidgetClone(origin) {
      let newWidget = this.designer.copyNewWidget(origin);
      if (origin.id) {
        this.cloneIdMap[newWidget.id] = origin.id;
      }
      // 字段或从表ID、标题保持不变
      if (this.designFields && this.designFields.useNewWidgetId == false && this.isFieldOrSubform(origin)) {
        newWidget.id = origin.id;
        newWidget.title = origin.title;
        this.cloneIdMap[origin.id] = origin.id;
      }
      return newWidget;
    },

    addFieldByDbClick(widget) {
      this.designer.addFieldByDbClick(widget);
    },

    isFieldOrSubform(widget) {
      let _self = this;
      let fields = (_self.designFields && _self.designFields.fields) || [];
      for (var i = 0; i < fields.length; i++) {
        if (fields[i].id == widget.id) {
          return true;
        }
      }

      let subforms = (_self.designFields && _self.designFields.subforms) || [];
      for (var i = 0; i < subforms.length; i++) {
        if (subforms[i].id == widget.id) {
          return true;
        }
      }
      return false;
    },
    updateSelectedFieldIdMap(newWidgets) {
      let _self = this;
      if (isEmpty(_self.designFields) || isEmpty(_self.designFields.fields)) {
        return;
      }

      let selectedIdMap = {};
      each(newWidgets, function (widet) {
        let selectedId = _self.cloneIdMap[widet.id];
        if (selectedId) {
          selectedIdMap[selectedId] = selectedId;
        } else {
          selectedIdMap[widet.id] = widet.id;
        }
      });
      _self.selectedFieldIdMap = selectedIdMap;
    },
    updateSelectedSubformIdMap(newWidgets) {
      let _self = this;
      if (isEmpty(_self.designFields) || isEmpty(_self.designFields.subforms)) {
        return;
      }

      let selectedIdMap = {};
      each(newWidgets, function (widet) {
        let selectedId = _self.cloneIdMap[widet.id];
        if (selectedId) {
          selectedIdMap[selectedId] = selectedId;
        } else {
          selectedIdMap[widet.id] = widet.id;
        }
      });
      _self.selectedSubformIdMap = selectedIdMap;
    }
  },
  mounted() {
    // 字段组件组件结合
    if (!this.designer.hasOwnProperty('FieldWidgets')) {
      this.$set(this.designer, 'FieldWidgets', []);
    }
    // 字段组件组件结合
    if (!this.designer.hasOwnProperty('WidgetSubforms')) {
      this.$set(this.designer, 'WidgetSubforms', []);
    }
  },
  watch: {
    'designFields.fields': function () {
      this.updateSelectedFieldIdMap(this.designer.FieldWidgets || []);
    },
    'designFields.subforms': function () {
      this.updateSelectedSubformIdMap(this.designer.WidgetSubforms || []);
    },
    'designer.FieldWidgets': function (oldWidgets, newWidgets) {
      this.updateSelectedFieldIdMap(newWidgets);
    },
    'designer.WidgetSubforms': function (oldWidgets, newWidgets) {
      this.updateSelectedSubformIdMap(newWidgets);
    }
  }
};
</script>
