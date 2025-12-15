<template>
  <a-table
    rowKey="id"
    :showHeader="false"
    size="small"
    :pagination="false"
    :bordered="false"
    :locale="locale"
    :columns="eventColumns"
    :data-source="widget.configuration.rowClickEvent.eventHandlers"
    style="--pt-table-td-padding: var(--w-padding-2xs)"
    :class="['pt-table widget-table-row-click-table no-border', !widget.configuration.rowClickEvent.enable ? 'hidden-table-content' : '']"
  >
    <template slot="title">
      <i class="line" />
      行点击
      <a-switch v-model="widget.configuration.rowClickEvent.enable" :style="{ float: 'right', 'margin-right': '4px;' }" />
    </template>
    <template slot="nameSlot" slot-scope="text, record">
      {{ text }}
      <a-tag color="blue" v-show="record.actionTypeName" :style="{ marginRight: '0px' }">
        {{ record.actionTypeName }}
      </a-tag>
      <a-tag title="自定义代码" color="blue" v-show="record.customScript" :style="{ marginRight: '0px' }">
        <a-icon type="code" />
      </a-tag>
    </template>

    <template slot="operationSlot" slot-scope="text, record, index">
      <a-button type="link" size="small" @click="delEvent(index)" title="删除">
        <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
      </a-button>
      <WidgetDesignDrawer :id="'WidgetTableRowClickConfig' + record.id" title="编辑事件" :designer="designer">
        <a-button type="link" size="small" title="编辑事件">
          <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
        </a-button>
        <template slot="content">
          <div>
            <a-form-model>
              <BehaviorLogConfiguration
                :widget="widget"
                :configuration="widget.configuration"
                configCode="rowClickBehaviorLog"
                :designer="designer"
                :extFormulaVariableOptions="extFormulaVariableOptions"
              />
            </a-form-model>
            <WidgetEventHandler :eventModel="record" :designer="designer" :widget="widget" :rule="{ triggerSelectable: false }">
              <template slot="pageTypeSlot">
                <a-form-model-item v-show="record.pageType === 'url'">
                  <template slot="label">
                    <a-space>
                      移动端地址
                      <a-popover>
                        <template slot="content">未填写移动端url地址时，按url地址跳转</template>
                        <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                      </a-popover>
                    </a-space>
                  </template>
                  <a-input v-model="record.mobileUrl" />
                </a-form-model-item>
              </template>
            </WidgetEventHandler>
          </div>
        </template>
      </WidgetDesignDrawer>
    </template>
    <template slot="footer">
      <WidgetDesignDrawer :id="'WidgetTableRowClickConfig' + widget.id" title="添加事件" :designer="designer">
        <a-button
          type="link"
          size="small"
          :style="{ paddingLeft: '7px' }"
          v-show="widget.configuration.rowClickEvent.eventHandlers.length == 0"
        >
          <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          添加
        </a-button>
        <template slot="content">
          <div>
            <a-form-model>
              <BehaviorLogConfiguration
                :widget="widget"
                :configuration="widget.configuration"
                configCode="rowClickBehaviorLog"
                :designer="designer"
                :extFormulaVariableOptions="extFormulaVariableOptions"
              />
            </a-form-model>
            <WidgetEventHandler
              :eventModel="newRowClickEventHandler"
              :designer="designer"
              :widget="widget"
              :rule="{ triggerSelectable: false }"
            />
          </div>
        </template>
        <template slot="footer">
          <a-button size="small" type="primary" @click.stop="onConfirmOk">确定</a-button>
        </template>
      </WidgetDesignDrawer>
    </template>
  </a-table>
</template>

<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';

export default {
  name: 'TableRowClickConfiguration',
  inject: ['columnIndexOptions'],
  mixins: [],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      newRowClickEventHandler: { eventParams: [] },
      eventColumns: [
        { title: '事件', dataIndex: 'name', scopedSlots: { customRender: 'nameSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' } }
      ]
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    extFormulaVariableOptions() {
      let groups = [];
      let colDataOptions = {
        title: '列字段数据',
        options: []
      };
      for (let i of this.columnIndexOptions) {
        colDataOptions.options.push({
          label: i.label,
          value: `BUTTON_META_DATA.${i.value}`
        });
      }
      groups.push(colDataOptions);

      return groups;
    }
  },
  created() {},
  methods: {
    delEvent(index) {
      this.widget.configuration.rowClickEvent.eventHandlers.splice(index, 1);
    },
    onConfirmOk() {
      this.newRowClickEventHandler.id = generateId();
      this.widget.configuration.rowClickEvent.eventHandlers.push(deepClone(this.newRowClickEventHandler));
      this.newRowClickEventHandler = { eventParams: [] };
      this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
    }
  },
  beforeMount() {},
  mounted() {}
};
</script>
