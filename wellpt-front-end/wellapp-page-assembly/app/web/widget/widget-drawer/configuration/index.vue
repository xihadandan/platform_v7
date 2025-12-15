<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model-item label="名称">
          <a-input v-model="widget.title"></a-input>
        </a-form-model-item>
        <a-form-model-item label="标题">
          <a-input v-model="widget.configuration.title">
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :designer="designer" :code="widget.id + '_title'" v-model="widget.configuration.title" />
            </template>
          </a-input>
        </a-form-model-item>
        <a-form-model-item label="JS模块">
          <JsModuleSelect v-model="widget.configuration.jsModules" dependencyFilter="WidgetDrawerDevelopment" />
        </a-form-model-item>
        <a-form-model-item label="显示右上角的关闭按钮">
          <a-switch v-model="widget.configuration.closable" />
        </a-form-model-item>
        <a-form-model-item label="隐藏底部">
          <a-switch v-model="widget.configuration.footerHidden" />
        </a-form-model-item>
        <a-form-model-item label="显示遮罩">
          <a-switch v-model="widget.configuration.mask" />
        </a-form-model-item>
        <a-form-model-item label="点击遮罩关闭抽屉">
          <a-switch v-model="widget.configuration.maskClosable" />
        </a-form-model-item>

        <a-table
          v-show="!widget.configuration.footerHidden"
          rowKey="id"
          :pagination="false"
          :bordered="false"
          size="small"
          :showHeader="false"
          :data-source="widget.configuration.footerButton.buttons"
          :columns="buttonColumns"
          class="widget-modal-button-table no-border"
        >
          <template slot="titleSlot" slot-scope="text, record">
            <Icon title="拖动排序" type="pticon iconfont icon-ptkj-tuodong" class="drag-btn-handler" :style="{ cursor: 'move' }" />
            <a-input size="small" v-model="record.title" style="width: 80px" />
            <a-tag v-if="['ok', 'cancel'].includes(record.id)" :color="record.id === 'ok' ? 'blue' : ''">
              {{ record.id === 'ok' ? '确定' : '取消' }}
            </a-tag>
          </template>

          <template slot="operationSlot" slot-scope="text, record, index">
            <a-button
              v-if="!['ok', 'cancel'].includes(record.id)"
              size="small"
              icon="delete"
              title="删除按钮"
              type="link"
              @click="delButton(index)"
            />
            <a-button
              v-if="['ok', 'cancel'].includes(record.id)"
              size="small"
              :icon="record.visible ? 'eye' : 'eye-invisible'"
              :title="record.visible ? '点击隐藏' : '点击显示'"
              type="link"
              @click="clickChangeButtonVisible(record)"
            />

            <WidgetDesignDrawer :id="'modalButtonMoreConfig' + record.id" title="设置" :designer="designer" :zIndex="1001">
              <a-button size="small" title="设置" type="link">
                <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
              </a-button>
              <template slot="content">
                <ButtonConfiguration :button="record" :widget="widget" :designer="designer" />
              </template>
            </WidgetDesignDrawer>
          </template>
          <template slot="footer">
            <a-button type="link" @click="addButton">
              <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
              添加按钮
            </a-button>
          </template>
        </a-table>
        <StyleConfiguration
          :editBlock="false"
          :widget="widget"
          :setMarginPadding="[false, true]"
          :setWidthHeight="[true, false]"
          :widthPersent="false"
        />
      </a-tab-pane>
      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import draggable from '@framework/vue/designer/draggable';
import ButtonConfiguration from '../../widget-modal/configuration/button-configuration.vue';
export default {
  name: 'WidgetDrawerConfiguration',
  mixins: [draggable],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      buttonColumns: [
        {
          title: '名称',
          dataIndex: 'title',
          scopedSlots: { customRender: 'titleSlot' }
        },
        {
          title: '操作',
          dataIndex: 'operation',
          width: 112,
          align: 'right',
          class: 'operation',
          scopedSlots: { customRender: 'operationSlot' }
        }
      ],
      newButton: {
        id: undefined,
        code: undefined,
        title: '按钮',
        visible: true,
        style: { icon: undefined, textHidden: false },
        switch: { checkedText: undefined, UnCheckedText: undefined, defaultChecked: true },
        eventHandler: { eventParams: [] },
        customEventScript: undefined
      }
    };
  },

  beforeCreate() {},
  components: { ButtonConfiguration },
  computed: {},
  created() {},
  methods: {
    clickChangeButtonVisible(btn) {
      btn.visible = !btn.visible;
    },
    addButton() {
      let btn = deepClone(this.newButton);
      btn.id = generateId();
      this.widget.configuration.footerButton.buttons.push(btn);
    },
    delButton(i) {
      this.widget.configuration.footerButton.buttons.splice(i, 1);
    }
  },
  mounted() {
    this.tableDraggable(
      this.widget.configuration.footerButton.buttons,
      this.$el.querySelector('.widget-modal-button-table tbody'),
      '.drag-btn-handler'
    );
  },
  configuration() {
    return {
      title: '标题',
      widgets: [],
      footerHidden: true,
      mask: false,
      maskClosable: false,
      closable: true,
      footerButton: {
        // 底部按钮配置
        buttons: [
          {
            id: 'cancel',
            code: 'cancel',
            title: '取消',
            visible: true,
            style: { icon: undefined, textHidden: false },
            eventHandler: { eventParams: [] },
            customEventScript: undefined
          },
          {
            id: 'ok',
            code: 'ok',
            title: '确定',
            visible: true,
            style: { icon: undefined, textHidden: false, type: 'primary' },
            eventHandler: { eventParams: [] },
            customEventScript: undefined
          }
        ],
        buttonGroup: {
          type: 'notGroup', // notGroup: 不分组  fixedGroup: 固定分组 dynamicGroup: 动态分组
          groups: [
            // 固定分组
            // {name:,buttonIds:[]}
          ],
          dynamicGroupName: '更多', //动态分组名称
          dynamicGroupBtnThreshold: 3 // 分组按钮数阈值，达到该数才触发分组
        }
      },
      style: {
        width: 300,
        padding: [12, 20, 12, 20]
      }
    };
  }
};
</script>
