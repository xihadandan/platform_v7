<template>
  <a-tabs default-active-key="1">
    <a-tab-pane key="1" tab="设置">
      <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
        <a-form-model-item label="名称">
          <a-input v-model="widget.title" />
        </a-form-model-item>
        <a-form-model-item label="JS模块">
          <JsModuleSelect v-model="widget.configuration.jsModules" dependencyFilter="WidgetTabDevelopment" />
        </a-form-model-item>
        <a-form-model-item label="页签类型">
          <a-radio-group v-model="widget.configuration.tabStyleType" button-style="solid" size="small" v-if="designer.terminalType == 'pc'">
            <a-radio-button value="line">线性页签</a-radio-button>
            <a-radio-button value="card">卡片页签</a-radio-button>
            <a-radio-button value="editable-card">编辑页签</a-radio-button>
          </a-radio-group>
          <a-radio-group v-else v-model="widget.configuration.uniConfiguration.tabStyleType" button-style="solid" size="small">
            <a-radio-button value="default">默认</a-radio-button>
            <a-radio-button value="subsection">分段</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item
          label="分段样式"
          v-if="designer.terminalType == 'mobile' && widget.configuration.uniConfiguration.tabStyleType == 'subsection'"
        >
          <a-radio-group v-model="widget.configuration.uniConfiguration.subsectionMode" button-style="solid" size="small">
            <a-radio-button value="subsection">默认分段</a-radio-button>
            <a-radio-button value="button">按钮风格</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <template v-if="designer.terminalType == 'pc'">
          <a-form-model-item label="页签位置">
            <a-select :options="tabPositionOptions" v-model="widget.configuration.tabPosition"></a-select>
          </a-form-model-item>
          <a-form-model-item label="作为标签窗口">
            <a-switch v-model="widget.configuration.asWindow" />
          </a-form-model-item>
          <a-form-model-item label="尺寸">
            <a-radio-group v-model="widget.configuration.size" button-style="solid" size="small">
              <a-radio-button
                v-for="(item, i) in [
                  { label: '小', value: 'small' },
                  { label: '常规', value: 'default' },
                  { label: '大', value: 'large' }
                ]"
                :key="i"
                :value="item.value"
              >
                {{ item.label }}
              </a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="显示边框">
            <a-switch v-model="widget.configuration.bordered" />
          </a-form-model-item>
          <StyleConfiguration :widget="widget" :setWidthHeight="[false, true]" :editBlock="false" :setMarginPadding="false" />
        </template>
        <!-- <a-form-model-item label="高度填充父容器">
          <a-switch v-model="widget.configuration.heightFillParent" />
        </a-form-model-item> -->

        <a-divider>页签设置</a-divider>

        <a-table
          :showHeader="false"
          class="widget-tab-table no-border"
          rowKey="id"
          :pagination="false"
          size="small"
          :bordered="false"
          :data-source="widget.configuration.tabs"
          :columns="tabColumns"
        >
          <template slot="titleSlot" slot-scope="text, record">
            <Icon title="拖动排序" type="pticon iconfont icon-ptkj-tuodong" class="drag-column-handler" :style="{ cursor: 'move' }" />
            <a-input
              :value="text"
              size="small"
              style="width: 180px"
              @change="
                $evt => {
                  record.title = $evt.target.value;
                  designer.widgetTreeMap[record.id].title = record.title;
                }
              "
            >
              <template slot="suffix">
                <a-icon type="link" v-show="record.configuration.eventHandler.pageUuid" style="margin-right: 5px" />
                <a-switch
                  size="small"
                  :checked="widget.configuration.defaultActiveKey == record.id"
                  @change="checked => onChangeDefaultActive(checked, record.id)"
                />
              </template>
              <template slot="addonAfter">
                <WI18nInput
                  :widget="widget"
                  :designer="designer"
                  :target="record.configuration"
                  :code="record.id + '.title'"
                  v-model="record.title"
                />
              </template>
            </a-input>
          </template>
          <template slot="operationSlot" slot-scope="text, record">
            <a-button size="small" title="删除页签" type="link" @click="delTab(record.id)">
              <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
            </a-button>
            <WidgetDesignDrawer :id="'tabConfig' + record.id" title="设置" :designer="designer">
              <a-button size="small" title="更多设置" type="link">
                <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
              </a-button>
              <template slot="content">
                <TabConfiguration :widget="widget" :designer="designer" :tab="record" />
              </template>
            </WidgetDesignDrawer>
          </template>
          <template slot="footer">
            <a-button type="link" icon="plus" :style="{ paddingLeft: '7px' }" @click.stop="addTab">添加页签</a-button>
          </template>
        </a-table>
        <!-- <a-form-model-item label="页签宽度">
          <a-input-number v-model="widget.configuration.tabWidth" />
        </a-form-model-item> -->
        <!-- <StyleConfiguration :widget="widget" :setMarginPadding="[true, true]" :setWidthHeight="[false, true]" :editBlock="false" /> -->
      </a-form-model>
    </a-tab-pane>

    <a-tab-pane key="2" tab="事件设置">
      <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
    </a-tab-pane>
  </a-tabs>
</template>

<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import draggable from '@framework/vue/designer/draggable';
import TabConfiguration from './tab-configuration.vue';
import configurationMixin from '@framework/vue/designer/configurationMixin.js';

export default {
  name: 'WidgetTabConfiguration',
  mixins: [draggable, configurationMixin],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      tabPositionOptions: [
        { label: '顶部', value: 'top' },
        { label: '左侧', value: 'left' },
        { label: '右侧', value: 'right' },
        { label: '底部', value: 'bottom' }
      ],

      tabColumns: [
        {
          title: '名称',
          dataIndex: 'title',
          scopedSlots: { customRender: 'titleSlot' }
        },
        {
          title: '操作',
          dataIndex: 'operation',
          width: 90,
          align: 'right',
          scopedSlots: { customRender: 'operationSlot' }
        }
      ]
    };
  },

  beforeCreate() {},
  components: { TabConfiguration },
  computed: {},
  created() {
    if (!this.widget.configuration.hasOwnProperty('uniConfiguration')) {
      this.$set(this.widget.configuration, 'uniConfiguration', { tabStyleType: 'default', subsectionMode: 'subsection' });
    }
    this.addProperty();
  },
  methods: {
    addProperty() {
      if (this.widget.configuration.size === undefined) {
        this.$set(this.widget.configuration, 'size', 'default');
      }
      if (this.widget.configuration.bordered === undefined) {
        this.$set(this.widget.configuration, 'bordered', false);
      }
      if (this.widget.configuration.height === undefined) {
        this.$set(this.widget.configuration, 'height', 'auto');
      }
    },
    getWidgetActionElements(wgt, designer) {
      let actionElements = [];
      actionElements.push(...this.resolveDefineEventToActionElement(wgt, designer));
      if (wgt.configuration.jsModules) {
        actionElements.push(...this.resolveJsModuleAsActionElement(wgt.configuration.jsModules, wgt, designer));
      }
      if (wgt.configuration.tabs) {
        for (let tab of wgt.configuration.tabs) {
          let tabButton = tab.configuration.tabButton;
          if (tabButton && tabButton.enable) {
            for (let btn of tabButton.buttons) {
              let e = btn.eventHandler;
              actionElements.push(
                ...this.resolveEventHandlerToActionElement(
                  {
                    elementName: btn.title,
                    elementTypeName: '按钮'
                  },
                  e,
                  wgt,
                  designer
                )
              );
            }
          }

          let e = tab.configuration.eventHandler;
          if (e.pageId || e.url) {
            actionElements.push(
              ...this.resolveEventHandlerToActionElement(
                {
                  elementName: tab.title,
                  elementTypeName: '页签'
                },
                e,
                wgt,
                designer
              )
            );
          }
        }
      }

      return actionElements;
    },
    onChangeDefaultActive(checked, id) {
      this.widget.configuration.defaultActiveKey = checked ? id : null;
      if (this.widget.configuration.defaultActiveKey == null && this.widget.configuration.tabs.length) {
        // 默认第一个激活
        this.widget.configuration.defaultActiveKey = this.widget.configuration.tabs[0].id;
      }
    },

    addTab() {
      this.widget.configuration.tabs.push({
        id: `tab-${generateId()}`,
        title: '页签',
        wtype: 'WidgetTabItem',
        configuration: {
          icon: undefined,
          closable: true,
          forceRender: true,
          widgets: [],
          eventHandler: {
            actionType: 'redirectPage',
            pageType: 'page',
            trigger: 'click',
            pageUuid: undefined
          }
        }
      });
    },
    delTab(id) {
      let tabs = this.widget.configuration.tabs;
      for (var i = 0, len = tabs.length; i < len; i++) {
        if (tabs[i].id === id) {
          tabs.splice(i, 1);
          this.$delete(this.designer.widgetIdMap, id);
          this.$delete(this.designer.widgetTreeMap, id);
          break;
        }
      }
    },

    setLanguage(evt, menu) {
      //FIXME: 测试国际化语言
      if (!this.widget.configuration.i18n) {
        this.widget.configuration.i18n = {
          zh_CN: {},
          en_US: {}
        };
      }
    },
    getFunctionElements(wgt, EWidgets) {
      let functionElements = {};
      functionElements[wgt.id] = [];
      if (wgt.configuration.tabs.length != 0) {
        for (let i = 0, len = wgt.configuration.tabs.length; i < len; i++) {
          let tab = wgt.configuration.tabs[i];
          let m = {
            id: tab.id,
            uuid: tab.id,
            name: `页签_${tab.title}`,
            code: 'tab_' + (i + 1),
            type: 'tab'
          };
          if (
            tab.configuration.eventHandler &&
            tab.configuration.eventHandler.pageId != undefined &&
            tab.configuration.eventHandler.actionType == 'redirectPage' &&
            tab.configuration.eventHandler.pageType == 'page'
          ) {
            m.resourceId = tab.configuration.eventHandler.pageId;
            m.resourceType = 'appPageDefinition';
          }
          functionElements[wgt.id].push(m);
        }
      }
      return functionElements;
    },

    getWidgetDefinitionElements(wgt, EWidgets) {
      let wgtDefinitionElements = [
        {
          wtype: wgt.wtype,
          id: wgt.id,
          title: wgt.title,
          definitionJson: JSON.stringify(wgt)
        }
      ];

      return wgtDefinitionElements;
    }
  },
  mounted() {
    this.tableDraggable(this.widget.configuration.tabs, this.$el.querySelector('.widget-tab-table tbody'), '.drag-column-handler');
  },
  configuration() {
    let activeKey = `tab-${generateId()}`;
    return {
      style: {},
      tabWidth: 100,
      tabs: [
        {
          id: activeKey,
          wtype: 'WidgetTabItem',
          title: '页签',
          configuration: {
            icon: undefined,
            closable: true,
            forceRender: true,
            widgets: [],
            eventHandler: {
              actionType: 'redirectPage',
              pageType: 'page',
              trigger: 'click',
              pageUuid: undefined
            }
          }
        }
      ],
      tabPosition: 'top',
      tabStyleType: 'line',
      defaultActiveKey: activeKey,
      asWindow: false, // 作为标签窗口，
      uniConfiguration: {
        tabStyleType: 'default',
        subsectionMode: 'subsection'
      }
    };
  }
};
</script>
