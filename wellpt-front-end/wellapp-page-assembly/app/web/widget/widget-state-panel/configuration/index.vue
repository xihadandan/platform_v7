<template>
  <div>
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
          <a-form-model-item label="名称">
            <a-input v-model="widget.title" />
          </a-form-model-item>

          <a-divider>状态设置</a-divider>
          <a-table
            :showHeader="false"
            class="widget-state-table no-border"
            rowKey="uuid"
            :pagination="false"
            size="small"
            :bordered="false"
            :data-source="widget.configuration.statePanels"
            :columns="columns"
          >
            <template slot="titleSlot" slot-scope="text, record">
              <Icon title="拖动排序" type="pticon iconfont icon-ptkj-tuodong" class="drag-column-handler" :style="{ cursor: 'move' }" />
              <a-input v-model="record.title" size="small" style="width: 140px">
                <!-- <template slot="suffix">
                  <a-switch
                    size="small"
                    :checked="widget.configuration.defaultActiveState == record.id"
                    @change="checked => onChangeDefaultActive(checked, record.id)"
                  />
                </template> -->
              </a-input>
              <a-input
                v-model="record.id"
                size="small"
                title="编码"
                style="width: 140px; margin: 5px 0px 0px 18px"
                @change="e => onChangeStateId(e, record)"
              >
                <a-icon type="code" slot="prefix" />
              </a-input>
            </template>
            <template slot="operationSlot" slot-scope="text, record, index">
              <a-switch
                size="small"
                :checked="defaultStateUuid == record.uuid"
                title="默认激活"
                @change="checked => onChangeDefaultActive(checked, record)"
              />
              <a-button size="small" title="删除状态" type="link" @click="delState(index)">
                <Icon type="pticon iconfont icon-ptkj-shanchu" />
              </a-button>
            </template>
            <template slot="footer">
              <a-button type="link" icon="plus" :style="{ paddingLeft: '7px' }" @click.stop="addState">添加状态</a-button>
            </template>
          </a-table>
        </a-form-model>
      </a-tab-pane>
      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import draggable from '@framework/vue/designer/draggable';

export default {
  name: 'WidgetStatePanelConfiguration',
  mixins: [draggable],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    let defaultStateUuid = undefined;
    if (this.widget.configuration.statePanels) {
      for (let i = 0, len = this.widget.configuration.statePanels.length; i < len; i++) {
        if (this.widget.configuration.defaultActiveState == this.widget.configuration.statePanels[i].id) {
          defaultStateUuid = this.widget.configuration.statePanels[i].uuid;
          break;
        }
      }
    }
    return {
      defaultStateUuid,
      columns: [
        {
          title: '名称',
          dataIndex: 'title',
          scopedSlots: { customRender: 'titleSlot' }
        },
        {
          title: '操作',
          dataIndex: 'operation',
          width: 100,
          align: 'right',
          scopedSlots: { customRender: 'operationSlot' }
        }
      ]
    };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {
    if (this.widget.configuration != null && this.widget.configuration.defaultActiveState == undefined) {
      this.widget.configuration.defaultActiveState = this.widget.configuration.statePanels[0].id;
    }
  },
  methods: {
    onChangeStateId(e, item) {
      if (this.defaultStateUuid == item.uuid) {
        this.widget.configuration.defaultActiveState = item.id;
      }
    },
    onChangeDefaultActive(checked, { uuid, id }) {
      this.widget.configuration.defaultActiveState = checked ? id : null;
      this.defaultStateUuid = uuid;
      if (this.widget.configuration.defaultActiveState == null && this.widget.configuration.statePanels.length) {
        // 默认第一个激活
        this.widget.configuration.defaultActiveState = this.widget.configuration.statePanels[0].id;
        this.defaultStateUuid = this.widget.configuration.statePanels[0].uuid;
      }
    },
    delState(i) {
      this.widget.configuration.statePanels.splice(i, 1);
    },
    addState() {
      this.widget.configuration.statePanels.push({
        uuid: generateId(),
        id: `${generateId(4)}`,
        title: '状态' + (this.widget.configuration.statePanels.length + 1),
        configuration: {
          widgets: []
        }
      });
    }
  },
  mounted() {
    this.tableDraggable(this.widget.configuration.statePanels, this.$el.querySelector('.widget-state-table tbody'), '.drag-column-handler');
  },
  configuration() {
    return {
      defaultActiveState: undefined,
      statePanels: [
        {
          uuid: generateId(),
          id: generateId(4),
          wtype: 'WidgetStatePanelItem',
          title: '状态1',
          configuration: {
            widgets: []
          }
        }
      ]
    };
  }
};
</script>
