<template>
  <div class="widget-design-select-sider">
    <a-icon
      type="double-left"
      :class="['left-collapse', collapse ? 'collapsed' : '']"
      @click.stop="onClickCollapse"
      :title="collapse ? '点击展开' : '点击收缩'"
    />
    <div class="sider-drawer-container" @click="onClickWgtEvtDrawer"></div>
    <a-tabs tab-position="left" size="small" class="widget-select-tabs" :activeKey="activeKey" @tabClick="onTabClick">
      <slot name="addonBeforeTabs"></slot>
      <a-tab-pane key="1">
        <span slot="tab">
          <Icon type="pticon iconfont icon-ptkj-zhagebuju" />
          <br />
          布局
        </span>
        <PerfectScrollbar class="sider-select-panel-scroll">
          <a-collapse :bordered="false" expandIconPosition="right" :defaultActiveKey="defaultCollapseActiveKey">
            <template v-for="(cat, i) in containerCategory">
              <a-collapse-panel
                :key="cat.key"
                v-if="
                  vDesignWidgets[cat.key] != undefined &&
                  vDesignWidgets[cat.key].length > 0 &&
                  (filterCategory.length == 0 || filterCategory.includes(cat.key))
                "
              >
                <template slot="header">
                  <!-- <i class="line" /> -->
                  <label class="">{{ cat.title }}</label>
                </template>
                <draggable
                  tag="ul"
                  :list="vDesignWidgets[cat.key]"
                  :group="{ name: draggableConfig.dragGroup, pull: 'clone', put: false }"
                  :clone="handleWidgetClone"
                  @end="onDragWidgetEnd"
                  filter=".filter"
                  :sort="false"
                  class="select-component-3-cube"
                >
                  <template v-for="(select, index) in vDesignWidgets[cat.key]">
                    <li
                      :key="cat.key + '_' + index"
                      :class="['widget-select-item', draggableConfig.filterWtype.includes(select.wtype) ? 'filter' : '']"
                      :title="select.name"
                    >
                      <PcMobileIcon v-if="vMultiTerminalWTypes.includes(select.wtype)" />
                      <template v-if="select.iconClass">
                        <Icon :type="select.iconClass"></Icon>
                      </template>
                      <template v-else-if="select.icon">
                        <img :src="select.icon" style="width: 16px; height: 16px" />
                      </template>
                      <template v-else>
                        <Icon type="pticon iconfont icon-ptkj-zhagebuju" />
                      </template>
                      <label>{{ select.name }}</label>
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
          <Icon type="pticon iconfont icon-a-icleftzujian" />
          <br />
          组件
        </span>
        <PerfectScrollbar class="sider-select-panel-scroll">
          <a-collapse :bordered="false" expandIconPosition="right" :defaultActiveKey="defaultCollapseActiveKey">
            <template v-for="(cat, i) in componentCategory">
              <a-collapse-panel
                :key="cat.key"
                v-if="
                  vDesignWidgets[cat.key] != undefined &&
                  vDesignWidgets[cat.key].length > 0 &&
                  (filterCategory.length == 0 || filterCategory.includes(cat.key))
                "
              >
                <template slot="header">
                  <!-- <i class="line" /> -->
                  <label class="">{{ cat.title }}</label>
                </template>
                <draggable
                  tag="ul"
                  class="select-component-3-cube"
                  :list="vDesignWidgets[cat.key]"
                  :group="{ name: draggableConfig.dragGroup, pull: 'clone', put: false }"
                  :clone="handleWidgetClone"
                  :move="onDragWidgetMove"
                  @end="onDragWidgetEnd"
                  filter=".filter"
                  :sort="false"
                >
                  <li
                    v-for="(select, index) in vDesignWidgets[cat.key]"
                    :key="index"
                    :class="['widget-select-item', draggableConfig.filterWtype.includes(select.wtype) ? 'filter' : '']"
                    :title="select.name"
                  >
                    <PcMobileIcon v-if="vMultiTerminalWTypes.includes(select.wtype)" />
                    <template v-if="select.iconClass">
                      <Icon :type="select.iconClass"></Icon>
                    </template>
                    <template v-else-if="select.icon">
                      <img :src="select.icon" style="width: 16px; height: 16px" />
                    </template>
                    <template v-else>
                      <Icon type="pticon iconfont icon-a-icleftzujian" />
                    </template>
                    <label>{{ select.name }}</label>
                  </li>
                </draggable>
              </a-collapse-panel>
            </template>
          </a-collapse>
        </PerfectScrollbar>
      </a-tab-pane>
      <a-tab-pane key="3" v-if="designType === 'page'">
        <span slot="tab">
          <Icon type="pticon iconfont icon-ptkj-zujian" />
          <br />
          模型
        </span>
        <PerfectScrollbar class="sider-select-panel-scroll">
          <a-collapse :bordered="false" expandIconPosition="right" :defaultActiveKey="defaultCollapseActiveKey">
            <a-collapse-panel v-for="(cat, i) in modelCategory" :key="cat.key">
              <template slot="header">
                <!-- <i class="line" /> -->
                <label class="">{{ cat.title }}</label>
              </template>
              <draggable
                tag="ul"
                class="select-component-3-cube"
                :list="vDesignWidgets[cat.key]"
                :group="{ name: draggableConfig.dragGroup, pull: 'clone', put: false }"
                :clone="handleWidgetClone"
                @end="onDragWidgetEnd"
                :sort="false"
              >
                <li v-for="(select, index) in vDesignWidgets[cat.key]" :key="index" class="widget-select-item" :title="select.name">
                  <template v-if="select.iconClass">
                    <Icon :type="select.iconClass"></Icon>
                  </template>
                  <template v-else-if="select.icon">
                    <img :src="select.icon" style="width: 16px; height: 16px" />
                  </template>
                  <template v-else>
                    <Icon type="pticon iconfont icon-ptkj-zujian" />
                  </template>
                  <label>{{ select.name }}</label>
                </li>
              </draggable>
            </a-collapse-panel>
          </a-collapse>
        </PerfectScrollbar>
      </a-tab-pane>
      <a-tab-pane key="4" v-if="allowTemplate">
        <span slot="tab">
          <Icon type="pticon iconfont icon-ptkj-yemian" />
          <br />
          模板
        </span>
        <PerfectScrollbar class="sider-select-panel-scroll">
          <a-collapse :bordered="false" expandIconPosition="right" defaultActiveKey="1" class="design-template">
            <a-collapse-panel key="1">
              <template slot="header">
                <!-- <i class="line" /> -->
                <label class="">模板</label>
              </template>
              <a-tabs class="radio-group-style" :animated="false">
                <a-tab-pane key="1" tab="个人模板">
                  <draggable
                    tag="ul"
                    :list="designTemplates"
                    :group="{ name: draggableConfig.dragGroup, pull: 'clone', put: false }"
                    :clone="handleTemplateClone"
                    :sort="false"
                    class="template-ul"
                  >
                    <li
                      v-for="(select, index) in designTemplates"
                      :key="index"
                      :title="select.title"
                      :class="['widget-select-item']"
                      style="width: 100%"
                    >
                      <a-popover
                        placement="right"
                        :arrowPointAtCenter="true"
                        :zIndex="1000"
                        :mouseEnterDelay="0.5"
                        overlayClassName="no-arrow"
                        :ref="select.uuid + '_popover'"
                      >
                        <template slot="content">
                          <img :src="select.thumbnail" style="height: 600px" />
                        </template>
                        <div class="item-img" style="background: #ffffff00">
                          <img :src="select.thumbnail" style="object-fit: contain" />
                        </div>
                      </a-popover>
                      <a-row type="flex" class="item-detail">
                        <a-col flex="1px" style="padding: 12px 0px 12px 12px"></a-col>
                        <a-col flex="auto">
                          <div class="template-label-title" style="max-width: 200px">
                            {{ select.title }}
                          </div>
                        </a-col>
                        <a-col flex="32px">
                          <a-popconfirm
                            title="确认要删除吗?"
                            ok-text="删除"
                            cancel-text="取消"
                            @confirm="deleteTemplate(select.uuid, index)"
                          >
                            <a-button size="small" type="link" class="delete-template" title="删除">
                              <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                            </a-button>
                          </a-popconfirm>
                        </a-col>
                      </a-row>
                    </li>
                  </draggable>
                </a-tab-pane>
                <a-tab-pane key="2" tab="公共模板">
                  <a-empty style="margin-top: 100px"></a-empty>
                </a-tab-pane>
              </a-tabs>
            </a-collapse-panel>
          </a-collapse>
        </PerfectScrollbar>
      </a-tab-pane>
      <slot name="addonAfterTabs"></slot>

      <div slot="tabBarExtraContent" style="position: absolute; bottom: 8px; width: 48px">
        <slot name="extraAddonBefore"></slot>
        <a-tooltip placement="right">
          <template slot="title">查看国际化</template>
          <a-button type="icon" class="icon-only" @click="openWidgetI18View" style="--w-button-height: 48px">
            <Icon type="ant-iconfont global" title="查看国际化"></Icon>
          </a-button>
        </a-tooltip>
        <a-tooltip placement="right">
          <template slot="title">查看组件交互</template>
          <a-button type="icon" class="icon-only" @click="openWidgetEvtTree" style="--w-button-height: 48px">
            <Icon type="iconfont icon-luojizujian-luojibianpai" title="查看组件交互"></Icon>
          </a-button>
        </a-tooltip>
        <a-tooltip placement="right">
          <template slot="title">组件树</template>
          <a-button type="icon" class="icon-only" @click="openWidgetTree" style="--w-button-height: 48px">
            <Icon type="pticon iconfont icon-ptkj-zuzhijiagoufenjishitu" title="组件树"></Icon>
          </a-button>
        </a-tooltip>
      </div>
    </a-tabs>

    <a-drawer
      title="国际化"
      placement="left"
      :closable="true"
      :mask="false"
      width="calc(100vw - 445px)"
      :wrap-style="{ position: 'absolute', left: '65px', top: '0px', height: 'calc(100vh - 54px)' }"
      :visible="widgetI18nDrawerVisible"
      :zIndex="2"
      @close="openWidgetI18View"
      :getContainer="getWgtEvtTreeContainer"
      :destroyOnClose="true"
      class="pt-drawer"
    >
      <WidgetI18nView :designer="designer" />
    </a-drawer>

    <a-drawer
      title="查看组件交互"
      placement="left"
      :closable="true"
      :mask="false"
      width="calc(100vw - 445px)"
      :wrap-style="{ position: 'absolute', left: '65px', top: '0px', height: 'calc(100vh - 54px)' }"
      :visible="widgetEvtTreeDrawerVisible"
      :zIndex="2"
      @close="openWidgetEvtTree"
      :getContainer="getWgtEvtTreeContainer"
      :destroyOnClose="true"
      class="pt-drawer"
    >
      <WidgetEvtDevView :designer="designer" />
    </a-drawer>
    <a-drawer
      title="组件树"
      placement="left"
      :closable="true"
      :mask="false"
      :width="280"
      :wrap-style="{ position: 'absolute' }"
      :visible="widgetTreeDrawerVisible"
      :zIndex="1"
      @close="openWidgetTree"
      :getContainer="getTreeContainer"
      class="pt-drawer"
    >
      <a-empty v-if="widgetTree.length == 0" description="暂无组件" />
      <a-tree
        v-else
        :tree-data="widgetTree"
        @select="selectTreeNode"
        :selectable="true"
        :showLine="true"
        :key="treeMd5"
        :defaultExpandAll="true"
      ></a-tree>
    </a-drawer>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import draggable from '@framework/vue/designer/draggable';
import { generateId } from '@framework/vue/utils/util';
import md5 from '@framework/vue/utils/md5';
import { debounce, sortBy } from 'lodash';
import PcMobileIcon from './pc-mobile-icon.vue';
import WidgetEvtDevView from './widget-evt-dev-view.vue';
import WidgetI18nView from './widget-i18n-view.vue';
export default {
  name: 'WidgetSelectPanel',
  props: {
    designer: Object,
    designWidgets: Object,
    designType: {
      type: String,
      default: 'page'
    },
    filterCategory: {
      type: Array,
      default: function () {
        return [];
      }
    },
    defaultActiveTab: String,
    allowTemplate: {
      type: Boolean,
      default: false
    }
  },
  mixins: [draggable],
  inject: ['pageContext', 'layoutFixed'],
  data() {
    let containerCategory = [
        { key: 'basicContainer', title: '基础布局' },
        { key: 'advanceContainer', title: '高级布局' }
      ],
      componentCategory = [
        { key: 'basicComponent', title: '基础组件' },
        { key: 'advanceComponent', title: '高级组件' },
        { key: 'displayComponent', title: '展示' },
        { key: 'formComponent', title: '表单' },
        { key: 'navComponent', title: '导航' },
        { key: 'feedbackComponent', title: '反馈' },
        { key: 'chartComponent', title: '图表组件' }
      ],
      modelCategory = [{ key: 'msModelComponent', title: 'MS模型' }];
    let defaultCollapseActiveKey = [];
    for (let list of [containerCategory, componentCategory, modelCategory]) {
      for (let l of list) {
        defaultCollapseActiveKey.push(l.key);
      }
    }
    return {
      collapse: false,
      designTemplates: [],
      activeKey: this.defaultActiveTab || '1',
      containerCategory,
      componentCategory,
      modelCategory,
      defaultCollapseActiveKey,
      widgetTreeDrawerVisible: false,
      widgetEvtTreeDrawerVisible: false,
      widgetI18nDrawerVisible: false,
      widgetTree: [],
      treeMd5: 'designWidgetTree'
    };
  },

  beforeCreate() {},
  components: { PcMobileIcon, WidgetEvtDevView, WidgetI18nView },
  computed: {
    vMultiTerminalWTypes() {
      let wtypes = [];
      for (let k in this.designWidgets) {
        let list = this.designWidgets[k];
        for (let l of list) {
          if (l.scope == undefined || (l.scope && (l.scope == this.designType || l.scope.includes(this.designType)))) {
            let scopeStr = Array.isArray(l.scope) ? l.scope.join(' , ') : l.scope;
            if (scopeStr.indexOf('mobile') != -1 && (scopeStr.indexOf('page') != -1 || scopeStr.indexOf('dyform') != -1)) {
              wtypes.push(l.wtype);
            }
          }
        }
      }
      return wtypes;
    },
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
  created() {
    this.rebuildTreeNode = debounce(this.rebuildTreeNode.bind(this), 500);
  },
  methods: {
    onClickWgtEvtDrawer() {
      // document.querySelector('#design-main').style.zIndex = 1;
    },
    openWidgetI18View() {
      this.widgetI18nDrawerVisible = !this.widgetI18nDrawerVisible;
      this.widgetEvtTreeDrawerVisible = false;
      this.widgetTreeDrawerVisible = false;
    },
    openWidgetEvtTree() {
      this.widgetEvtTreeDrawerVisible = !this.widgetEvtTreeDrawerVisible;
      this.widgetTreeDrawerVisible = false;
      this.widgetI18nDrawerVisible = false;
    },
    openWidgetTree() {
      this.widgetTreeDrawerVisible = !this.widgetTreeDrawerVisible;
      this.widgetEvtTreeDrawerVisible = false;
      this.widgetI18nDrawerVisible = false;
      if (this.widgetTreeDrawerVisible) {
        this.collapse = false;
        this.collapseOrExpand();
      }
    },
    getWgtEvtTreeContainer() {
      return this.$el.querySelector('.sider-drawer-container');
    },
    getTreeContainer() {
      return this.$el.querySelector('.widget-select-tabs .ant-tabs-content');
    },
    selectFirstTab() {
      this.activeKey = '1';
    },
    setTabActiveKey(key) {
      this.activeKey = key;
    },
    onTabClick(key) {
      this.collapse = false;
      this.activeKey = key;
      this.widgetTreeDrawerVisible = false;
      this.widgetI18nDrawerVisible = false;
      this.widgetEvtTreeDrawerVisible = false;
      this.collapseOrExpand();
    },
    collapseOrExpand(width) {
      let style = this.$el.parentElement.parentElement.style;
      if (this.collapse) {
        style.flex = '0 0 ' + width + 'px';
        style.width = width + 'px';
        style.minWidth = style.width;
      } else {
        style.flex = this.defaultSiderStyle.flex;
        style.width = this.defaultSiderStyle.width;
        style.minWidth = this.defaultSiderStyle.width;
      }
    },
    onClickCollapse(e) {
      this.collapse = !this.collapse;
      // let _target = null;
      // if (e.target.nodeName === 'I') {
      //   _target = e.target.nextElementSibling.firstChild;
      // } else if (e.target.nodeName === 'svg') {
      //   _target = e.target.parentElement.nextElementSibling.firstChild;
      // } else {
      //   _target = e.target.parentElement.parentElement.nextElementSibling.firstChild;
      // }

      this.collapseOrExpand(
        64 //_target.offsetWidth
      );
    },
    onDragWidgetMove(e) {
      // console.log('onDragWidgetMove', e);
      let related = e.related;
      // console.log('nextEle', related, nextElementSibling, e.willInsertAfter);
      if (e.willInsertAfter) {
        let nextElementSibling = related.nextElementSibling;
        if (nextElementSibling) {
          if (nextElementSibling && nextElementSibling.__vue__ && related.__vue__ && related.__vue__.widget) {
            if (
              nextElementSibling.__vue__.widget &&
              nextElementSibling.__vue__.widget.line != undefined &&
              related.__vue__.widget.line != undefined &&
              nextElementSibling.__vue__.widget.line == related.__vue__.widget.line
            ) {
              return false;
            }
          }
        }
      } else {
        let previousElementSibling = related.previousElementSibling;
        if (previousElementSibling) {
          if (previousElementSibling && previousElementSibling.__vue__ && related.__vue__ && related.__vue__.widget) {
            if (
              previousElementSibling.__vue__.widget &&
              previousElementSibling.__vue__.widget.line != undefined &&
              related.__vue__.widget.line != undefined &&
              previousElementSibling.__vue__.widget.line == related.__vue__.widget.line
            ) {
              return false;
            }
          }
        }
      }

      return true;
    },
    onDragWidgetEnd(e) {
      if (e.pullMode == 'clone') {
        this.designer.undoOrRedo = true;
        this.designer.setSelected(e.item._underlying_vm_);
      }
      this.designer.dragging = false;
      this.designer.draggingWidgetId = null;
    },
    handleWidgetClone(origin) {
      this.designer.dragging = true;
      let widget = this.designer.copyNewWidget(origin);
      this.designer.draggingWidgetId = widget.id;
      // console.log('组件拷贝: ', widget);
      return widget;
    },
    handleTemplateClone(origin) {
      this.$refs[origin.uuid + '_popover'][0].$children[0].onVisibleChange(false);
      // 替换ID
      let json = origin.json;
      for (let id of origin.wids) {
        let reg = new RegExp(id, 'g');
        json = json.replace(reg, generateId());
      }
      return {
        id: '-1',
        wtype: 'WidgetCloneTemplatePlaceholder',
        configuration: { items: [...JSON.parse(json)] }
      };
    },
    deleteTemplate(uuid, index) {
      let _this = this;
      $axios.get(`/proxy/api/user/widgetDef/delete/${uuid}`, { params: {} }).then(({ data }) => {
        if (data.code == 0) {
          _this.designTemplates.splice(index, 1);
        }
      });
    },
    fetchDesignTemplate() {
      let _this = this;
      $axios.get(`/proxy/api/user/widgetDef/getUserWidgetByType`, { params: { type: 'WIDGET_AS_TEMPLATE' } }).then(({ data }) => {
        let wgts = data.data;
        if (wgts.length) {
          _this.designTemplates = [];
          for (let wgt of wgts) {
            let json = JSON.parse(wgt.definitionJson);
            if (wgt.widgetId !== _this.designType + '_user_template') {
              continue;
            }
            _this.designTemplates.push({
              uuid: wgt.uuid,
              title: json.title,
              wids: json.wids,
              json: json.json, // 定义json
              thumbnail: json.thumbnail // 缩略图，用于预览
            });
          }
        }
      });
    },

    rebuildTreeNode() {
      let v = this.designer.widgetTreeMap;
      let str = md5(JSON.stringify(v));
      if (str != this.treeMd5) {
        let widgetTree = [];
        // 重新构建树型数据
        let map = {};
        let parentKey = '';
        for (let k in v) {
          let node = {
            key: k,
            title: v[k].title,
            index: v[k].index || 0,
            wtype: v[k].wtype
          };
          map[k] = node;
          if (v[k].parentKey == undefined) {
            // 一级节点
            parentKey = k;
            widgetTree.push(node);
          }
        }
        widgetTree = sortBy(widgetTree, a => {
          return a.index;
        });
        for (let k in v) {
          if (v[k].parentKey && map[v[k].parentKey]) {
            let parent = map[v[k].parentKey];
            if (parent.children == undefined) {
              parent.children = [];
            }
            parent.children.push(map[k]);
            parent.children = sortBy(parent.children, a => {
              return a.index;
            });
          }
        }
        this.treeMd5 = str;
        this.widgetTree.splice(0, this.widgetTree.length);
        this.widgetTree.push(...widgetTree);
      }
    },
    selectTreeNode(selectedKeys, info) {
      let wgt = this.designer.widgetIdMap[selectedKeys[0]];
      if (this.layoutFixed) {
        if (wgt.main) {
          return;
        }
        if (
          wgt.wtype == 'WidgetLayoutHeader' ||
          wgt.wtype == 'WidgetLayoutSider' ||
          wgt.wtype == 'WidgetLayoutContent' ||
          wgt.wtype == 'WidgetLayoutFooter'
        ) {
          let parent = this.designer.widgetIdMap[this.designer.widgetTreeMap[wgt.id].parentId];
          if (parent.main) {
            return;
          }
        }
      }
      if (wgt) {
        this.designer.setSelected(wgt);
      }
    }
  },
  beforeMount() {
    this.fetchDesignTemplate();
  },
  mounted() {
    let _this = this;
    this.defaultSiderStyle = {
      flex: this.$el.parentElement.parentElement.style.flex,
      width: this.$el.parentElement.parentElement.style.width
    };
    this.pageContext.handleEvent('afterSaveTemplateSuccess', function () {
      _this.fetchDesignTemplate();
    });
    this.rebuildTreeNode();
  },
  watch: {
    'designer.widgets': {
      deep: true,
      handler(v, o) {
        this.rebuildTreeNode();
      }
    }
  }
};
</script>
