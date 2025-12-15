<template>
  <div class="designer-configuration-form">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
          <a-form-model-item label="名称">
            <a-input v-model="widget.title" />
          </a-form-model-item>
          <a-form-model-item label="锚点位置">
            <a-radio-group size="small" v-model="widget.configuration.flexDirection" button-style="solid" @change="onChangeFlexDirection">
              <a-radio-button value="row">左侧</a-radio-button>
              <a-radio-button value="row-reverse">右侧</a-radio-button>
              <a-radio-button value="column">顶部</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <!-- <a-form-model-item label="锚点悬浮" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
            <a-switch v-model="widget.configuration.levitate" />
          </a-form-model-item> -->
          <a-form-model-item label="锚点形状">
            <a-radio-group size="small" v-model="widget.configuration.inkShape" button-style="solid" @change="onChangeFlexDirection">
              <a-radio-button value="circle">圆形</a-radio-button>
              <a-radio-button value="line">{{ widget.configuration.flexDirection === 'column' ? '横线' : '竖线' }}</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
        </a-form-model>
        <a-form-model-item label="锚点排序绑定锚点元素顺序" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
          <a-switch v-model="widget.configuration.dependonWgtOrder" />
        </a-form-model-item>

        <a-table
          :key="tableKey"
          id="anchor-table"
          rowKey="id"
          size="small"
          :showHeader="false"
          :pagination="false"
          :data-source="widget.configuration.anchors"
          :columns="anchorColumns"
          childrenColumnName="anchors"
          :expandedRowKeys.sync="expandedRowKeys"
        >
          <template slot="expandIcon" slot-scope="{ expanded, record }">
            <Icon
              title="拖动排序"
              v-show="!widget.configuration.dependonWgtOrder"
              type="pticon iconfont icon-ptkj-tuodong"
              class="drag-handler"
              :data-level="record.level"
              :style="{ cursor: 'move', position: 'relative', left: '-' + 20 * (record.level - 1) + 'px', marginTop: '4px' }"
            />
            <a-icon
              :style="{ color: record.anchors && record.anchors.length === 0 ? '#00000000' : 'inherit' }"
              v-show="record.level <= 2"
              :type="expanded ? 'caret-down' : 'caret-right'"
              :title="expanded ? '收起' : '展开'"
              @click="onExpendRow(expanded, record.id)"
            />
          </template>
          <template slot="title">锚点列表</template>
          <template slot="hrefSlot" slot-scope="text, record">
            <label :data-pid="record.pid" :data-row-key="record.id">{{ record.label }}</label>
          </template>
          <template slot="iconSlot" slot-scope="text, record">
            <a-icon
              type="info-circle"
              :rotate="180"
              :style="{ color: '#f5222d' }"
              title="组件不存在"
              v-show="record.href && !designer.widgetTreeMap.hasOwnProperty(record.href)"
            />
          </template>

          <template slot="operationSlot" slot-scope="text, record">
            <WidgetDesignDrawer :id="'anchorSetConfig' + record.id" title="添加锚点" :designer="designer" v-if="record.level <= 2">
              <a-button type="link" size="small" title="添加锚点" @click="e => addAnchor(e, record)">
                <Icon type="pticon iconfont icon-ptkj-jiahao" />
              </a-button>
              <template slot="content">
                <AnchorSetConfiguration
                  :widget="widget"
                  :anchor="record.anchors[record.anchors.length - 1]"
                  :designer="designer"
                  :anchorScopeWidgetOptions="anchorScopeWidgetOptions"
                  v-if="record.anchors.length > 0"
                />
              </template>
            </WidgetDesignDrawer>
            <WidgetDesignDrawer :id="'anchorSetConfigSet' + record.id" title="修改锚点" :designer="designer">
              <a-button type="link" size="small" title="修改锚点">
                <Icon type="pticon iconfont icon-ptkj-shezhi" />
              </a-button>
              <template slot="content">
                <AnchorSetConfiguration
                  :widget="widget"
                  :anchor="record"
                  :designer="designer"
                  :anchorScopeWidgetOptions="anchorScopeWidgetOptions"
                />
              </template>
            </WidgetDesignDrawer>
            <a-popconfirm placement="topRight" @confirm="delAnchor(record.id)">
              <template slot="title">
                是否删除{{ record.label }}锚点
                <template v-if="record.anchors.length">及子级锚点</template>
              </template>
              <a-button type="link" size="small" title="删除锚点">
                <Icon type="pticon iconfont icon-ptkj-shanchu" />
              </a-button>
            </a-popconfirm>
          </template>

          <template slot="footer">
            <WidgetDesignDrawer :id="'anchorSetConfig' + widget.id" title="添加" :designer="designer">
              <a-button type="link" @click="addAnchor" icon="plus-circle">添加锚点</a-button>
              <template slot="content">
                <AnchorSetConfiguration
                  :widget="widget"
                  :anchor="anchorTarget"
                  :designer="designer"
                  :anchorScopeWidgetOptions="anchorScopeWidgetOptions"
                  v-if="widget.configuration.anchors.length > 0"
                />
              </template>
            </WidgetDesignDrawer>
            <WidgetDesignDrawer :id="'anchorQuickSetConfig' + widget.id" title="快速添加" :designer="designer">
              <a-button type="link" icon="plus-circle">快速添加</a-button>
              <template slot="content">
                <QuickSetAnchorConfiguration
                  v-if="anchorScopeWidgetOptions"
                  :widget="widget"
                  :designer="designer"
                  :anchorScopeWidgetOptions="anchorScopeWidgetOptions"
                  ref="quickSet"
                />
              </template>
              <template slot="footer">
                <a-button size="small" type="primary" @click.stop="onConfirmQuickSetOk">确定</a-button>
                <a-button size="small" @click.stop="onCancelQuickSet">取消</a-button>
              </template>
            </WidgetDesignDrawer>
          </template>
        </a-table>

        <a-form-model-item label="垂直偏移" :wrapper-col="{ style: { textAlign: 'right' } }">
          <a-radio-group size="small" v-model="widget.configuration.offset.type" button-style="solid">
            <a-radio-button value="offsetTop">上偏移</a-radio-button>
            <a-radio-button value="offsetBottom">下偏移</a-radio-button>
          </a-radio-group>
          <div class="input-number-suffix-wrapper" :style="{ 'margin-left': '4px' }">
            <a-input-number v-model="widget.configuration.offset.value" size="small" :style="{ width: '70px' }" />
            <i>px</i>
          </div>
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>
<style></style>
<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import AnchorSetConfiguration from './anchor-set-configuration.vue';
import QuickSetAnchorConfiguration from './quick-set-anchor-configuration.vue';
import Sortable from 'sortablejs';

export default {
  name: 'WidgetAnchorConfiguration',
  mixins: [],
  inject: ['widgetMeta'],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      selectedSubAnchorRowKeys: [],
      selectedAnchorRowKeys: [],
      expandedRowKeys: [],
      anchorTarget: {},
      tableKey: 0,
      anchorColumns: [
        {
          title: '锚点',
          dataIndex: 'href',
          scopedSlots: { customRender: 'hrefSlot' }
        },
        {
          title: '提示',
          dataIndex: 'icon',
          width: 30,
          scopedSlots: { customRender: 'iconSlot' }
        },
        {
          title: '操作',
          dataIndex: 'operation',
          width: 110,
          scopedSlots: { customRender: 'operationSlot' }
        }
      ]
    };
  },
  components: { AnchorSetConfiguration, QuickSetAnchorConfiguration },
  computed: {
    anchorKeyMap() {
      let map = {};
      let cascadeSetKeyMap = function (anchors) {
        if (anchors)
          for (let i = 0, len = anchors.length; i < len; i++) {
            let anchor = anchors[i];
            map[anchor.id] = anchor;
            cascadeSetKeyMap.call(this, anchor.anchors);
          }
      };

      for (let i = 0, len = this.widget.configuration.anchors.length; i < len; i++) {
        let anchor = this.widget.configuration.anchors[i];
        map[anchor.id] = anchor;
        cascadeSetKeyMap.call(this, anchor.anchors);
      }
      return map;
    },

    hasGroupType() {
      return this.widget.configuration.groupType != null;
    },

    allowWtypes() {
      let types = Object.keys(this.widgetMeta);
      return types;
    },
    // 锚点域里面的组件集合
    anchorScopeWidgetOptions() {
      // 获取锚点下的所有子元素组件
      let widgetTree = this.designer.getWidgetTree();
      if (!widgetTree.length) {
        return;
      }

      let findAnchorWidget = list => {
        for (let i = 0, len = list.length; i < len; i++) {
          if (list[i].key == this.widget.id) {
            return list[i];
          } else if (list[i].children != undefined) {
            let _w = findAnchorWidget(list[i].children);
            if (_w) {
              return _w;
            }
          }
        }
      };
      const curAnchor = findAnchorWidget(widgetTree);

      let children = curAnchor.children,
        // let children = this.designer.widgetTreeMap[this.widget.id].children,
        _this = this,
        result = {
          selectOptions: [],
          wtypeIds: {}
        };

      if (children) {
        let cascadeChildren = (obj, array, ids) => {
          if (obj.children) {
            for (let i = 0, len = obj.children.length; i < len; i++) {
              if (obj.children[i].wtype && _this.allowWtypes.includes(obj.children[i].wtype)) {
                const value = obj.children[i].key;
                const widget = this.designer.widgetIdMap[value];
                const fieldName = widget.configuration.name || '';
                const label = fieldName ? widget.name + ' - ' + fieldName : widget.name + ' - ' + widget.title;
                array.push({ label, value });
                if (ids[obj.children[i].wtype] === undefined) {
                  ids[obj.children[i].wtype] = [];
                }
                ids[obj.children[i].wtype].push(obj.children[i].key);
              }

              cascadeChildren(obj.children[i], array, ids);
            }
          }
        };
        for (let i = 0, len = children.length; i < len; i++) {
          if (children[i].wtype && result.wtypeIds[children[i].wtype] === undefined) {
            result.wtypeIds[children[i].wtype] = [];
          }

          if (children[i].wtype && this.allowWtypes.includes(children[i].wtype)) {
            result.wtypeIds[children[i].wtype].push(children[i].key);
            const value = children[i].key;
            const widget = this.designer.widgetIdMap[value];
            const fieldName = widget.configuration.name || '';
            const label = fieldName ? widget.name + ' - ' + fieldName : widget.name + ' - ' + widget.title;
            result.selectOptions.push({ label, value });
          }

          cascadeChildren(children[i], result.selectOptions, result.wtypeIds);
        }
      }
      return result;
    }
  },
  created() {},
  methods: {
    onConfirmQuickSetOk() {
      this.$refs.quickSet.onConfirmOk();
    },
    onCancelQuickSet() {
      this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
    },
    onExpendRow(expanded, id) {
      if (expanded) {
        this.expandedRowKeys.splice(this.expandedRowKeys.indexOf(id), 1);
      } else {
        this.expandedRowKeys.push(id);
      }
    },
    onChangeFlexDirection(e) {
      this.widget.configuration.flex = ['50px', '150px'][this.widget.configuration.flexDirection === 'top' ? 0 : 1];
      // 通知编辑组件修改布局
      //this.designer.emitEvent(`WidgetAnchor:${this.widget.id}:PositionChange`, e.target.value);
    },
    onSelectedSubAnchorRowChange(selectedRowKeys) {
      this.selectedSubAnchorRowKeys = selectedRowKeys;
    },
    onSelectedAnchorRowChange(selectedRowKeys) {
      this.selectedAnchorRowKeys = selectedRowKeys;
    },
    onSelectAnchorWidget(v, opt, record) {
      if (v && !record.label) {
        record.label = opt.componentOptions.children[0].text.trim();
      }
    },
    addAnchor(e, record) {
      let tar = record === undefined ? this.widget.configuration.anchors : record.anchors;
      if (record !== undefined) {
        this.expandedRowKeys.push(record.id);
      }
      let anchor = {
        id: generateId(),
        pid: record === undefined ? undefined : record.id,
        level: record ? record.level + 1 : 1,
        label: '',
        href: null
      };
      if (anchor.level <= 2) {
        anchor.anchors = [];
      }
      this.anchorTarget = anchor;
      tar.push(anchor);
    },

    delAnchor(id) {
      let _this = this;
      let delSubAnchor = function (anchors) {
        if (anchors) {
          for (let i = 0, len = anchors.length; i < len; i++) {
            let subAnchor = anchors[i];
            if (subAnchor.id === id) {
              anchors.splice(i, 1);
              return true;
            }
            if (delSubAnchor(subAnchor.anchors)) {
              return true;
            }
          }
        }
      };
      for (let i = 0, len = this.widget.configuration.anchors.length; i < len; i++) {
        let anchor = this.widget.configuration.anchors[i];
        if (anchor.id === id) {
          this.widget.configuration.anchors.splice(i, 1);
          break;
        }
        if (delSubAnchor(anchor.anchors)) {
          break;
        }
      }
    },
    tableDraggable() {
      let _this = this;
      this.$nextTick(() => {
        this.drop = Sortable.create(this.$el.querySelector('#anchor-table tbody'), {
          handle: '.drag-handler',
          onStart: e => {
            console.log('onStart', e);
            // _this.expandedRowKeys.splice(_this.expandedRowKeys.indexOf(e.item.dataset.rowKey), 1);
            return false;
          },
          onEnd: e => {
            let item = _this.anchorKeyMap[e.item.dataset.rowKey],
              newAnchors = [],
              tar = null,
              nodes = [];
            // 查找其下的子锚点，重排顺序
            if (item.pid) {
              nodes = _this.$el.querySelectorAll("#anchor-table label[data-pid='" + item.pid + "']");
              tar = _this.anchorKeyMap[item.pid];
            } else {
              nodes = _this.$el.querySelectorAll('#anchor-table .ant-table-row-level-0');
              tar = _this.widget.configuration;
            }

            for (let i = 0, len = nodes.length; i < len; i++) {
              newAnchors.push(_this.anchorKeyMap[nodes[i].dataset.rowKey]);
            }
            tar.anchors = newAnchors;

            _this.tableKey++;
            _this.tableDraggable();
          },
          onMove: (/**Event*/ evt, /**Event*/ originalEvent) => {
            // 只能同级内拖拽
            let { related, dragged } = evt;
            let relatedAnchor = _this.anchorKeyMap[related.dataset.rowKey],
              draggedAnchor = _this.anchorKeyMap[dragged.dataset.rowKey];
            if (relatedAnchor.pid === draggedAnchor.pid) {
              return true;
            }
            return false;
          }
        });
      });
    }
  },
  beforeMount() {},
  mounted() {
    this.tableDraggable();
  },
  configuration() {
    return {
      affix: true,
      flexDirection: 'row', // row 、 row-reverse 、column
      dependonWgtOrder: true, // 依赖组件顺序
      inkShape: 'line',
      anchors: [],
      widgets: [], // 左右位置情况下，才有子组件集合
      levitate: false, // 悬浮状态
      offset: {
        type: 'offsetTop', // offsetTop offsetBottom
        value: 100
      }
    };
  }
};
</script>
