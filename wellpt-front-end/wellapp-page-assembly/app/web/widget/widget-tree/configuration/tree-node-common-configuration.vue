<template>
  <div>
    <a-form-model-item label="节点图标" v-show="widget.configuration.showIcon">
      <WidgetDesignModal
        title="选择图标"
        :zIndex="1000"
        :width="640"
        dialogClass="pt-modal widget-icon-lib-modal"
        :bodyStyle="{ height: '560px' }"
        :maxHeight="560"
        mask
        bodyContainer
      >
        <IconSetBadge v-model="widget.configuration.globalSetting.icon"></IconSetBadge>
        <template slot="content">
          <WidgetIconLib v-model="widget.configuration.globalSetting.icon" />
        </template>
      </WidgetDesignModal>
    </a-form-model-item>
    <a-form-model-item label="节点操作">
      <a-table
        rowKey="id"
        :pagination="false"
        :dataSource="widget.configuration.globalSetting.operations"
        :columns="operationCols"
        :showHeader="false"
        size="small"
      >
        <template slot="nameSlot" slot-scope="text, record">
          <a-input v-model="record.name" size="small">
            <template slot="addonAfter">
              <WI18nInput
                v-show="!record.nameHidden"
                :widget="widget"
                :target="record"
                :designer="designer"
                :code="record.id"
                v-model="record.name"
              />
            </template>
          </a-input>
        </template>
        <template slot="moreSlot" slot-scope="text, record, index">
          <a-button type="link" size="small" @click="widget.configuration.globalSetting.operations.splice(index, 1)" title="删除">
            <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
          </a-button>
          <WidgetDesignDrawer id="treeNodeOperations" title="设置" :designer="designer">
            <a-button type="link" size="small" title="设置"><Icon type="pticon iconfont icon-ptkj-shezhi"></Icon></a-button>
            <template slot="content">
              <TreeNodeOperation :widget="widget" :designer="designer" :operation="record" :columnOptions="columnOptions" />
            </template>
          </WidgetDesignDrawer>
        </template>
        <template slot="footer">
          <a-button type="link" @click="addNodeOperation">添加操作</a-button>
        </template>
      </a-table>
    </a-form-model-item>
    <!-- <a-form-model-item label="节点点击事件">
      <WidgetDesignDrawer :id="'WidgetTreeNodeClickConfig' + widget.id" title="节点点击事件" :designer="designer">
        <a-button type="link" icon="setting">设置</a-button>
        <template slot="content">
          <WidgetEventHandler
            :eventModel="widget.configuration.nodeClickEventHandler"
            :designer="designer"
            :widget="widget"
            :rule="{ triggerSelectable: false }"
          >
            <template slot="eventParamValueHelpSlot">
              <div style="width: 600px">
                <p>
                  1. 支持通过
                  <a-tag>${ 参数 }</a-tag>
                  表达式解析以下对象值:
                </p>
                <ul>
                  <li>selected : 节点是否选中</li>
                  <li>
                    data : 选中节点的数据, 可以通过
                    <a-tag>${data.属性}</a-tag>
                    解析数据对象的属性值
                  </li>
                </ul>
                <p>
                  2. 支持通过模板字符串逻辑输出内容值, 模板字符串内通过
                  <a-tag><% %></a-tag>
                  编写javaScript代码, 例如:
                  <code
                    style="
                      display: block;
                      padding: 10px 25px;
                      border-radius: 4px;
                      background: rgb(250 250 250);
                      outline: 1px solid #dedede;
                      margin-top: 8px;
                    "
                  >
                    <% if (selected) { %> 输出内容 <% } else { %> 输出内容 <% } %>
                  </code>
                </p>
              </div>
            </template>
          </WidgetEventHandler>
        </template>
      </WidgetDesignDrawer>
    </a-form-model-item> -->
  </div>
</template>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
export default {
  name: 'TreeNodeCommonConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    columnOptions: Array
  },
  data() {
    return {
      operationCols: [
        { dataIndex: 'name', title: '名称', scopedSlots: { customRender: 'nameSlot' } },
        { dataIndex: 'more', title: '更多', scopedSlots: { customRender: 'moreSlot' }, width: '90px' }
      ]
    };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {
    // if (this.widget.configuration.nodeClickEventHandler == undefined) {
    //   this.$set(this.widget.configuration, 'nodeClickEventHandler', {
    //     eventParams: []
    //   });
    // }
    if (this.widget.configuration.nodeClickEventHandler != undefined) {
      // 改为交互事件配置
      this.widget.configuration.domEvents[0].widgetEvent.push(
        Object.assign(
          {
            condition: {
              enable: false,
              conditions: [],
              match: 'all'
            }
          },
          JSON.parse(JSON.stringify(this.widget.configuration.nodeClickEventHandler))
        )
      );
      delete this.widget.configuration.nodeClickEventHandler;
    }
  },
  methods: {
    addNodeOperation() {
      this.widget.configuration.globalSetting.operations.push({
        id: generateId(),
        name: undefined,
        eventHandler: { eventParams: [] }
      });
    }
  },
  mounted() {}
};
</script>
