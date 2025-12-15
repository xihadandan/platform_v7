<template>
  <div>
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
          <a-form-model-item label="名称">
            <a-input v-model="widget.title" @change="evt => onChangeModalName(evt)" />
          </a-form-model-item>
          <a-form-model-item label="JS模块">
            <JsModuleSelect v-model="widget.configuration.jsModules" dependencyFilter="WidgetModalDevelopment" />
          </a-form-model-item>

          <a-form-model-item label="弹窗标题">
            <a-input v-model="widget.configuration.title">
              <template slot="addonAfter">
                <a-switch
                  checked-children="显示"
                  un-checked-children="显示"
                  :checked="!widget.configuration.hiddenTitle"
                  @change="widget.configuration.hiddenTitle = !widget.configuration.hiddenTitle"
                />
                <WI18nInput
                  v-show="!widget.configuration.hiddenTitle"
                  :widget="widget"
                  :designer="designer"
                  :code="widget.id + '_title'"
                  v-model="widget.configuration.title"
                />
              </template>
            </a-input>
          </a-form-model-item>
          <a-form-model-item label="引用内容页">
            <a-switch v-model="widget.configuration.contentFromPage" />
          </a-form-model-item>
          <WidgetEventHandler
            v-if="widget.configuration.contentFromPage"
            :widget="widget"
            :eventModel="widget.configuration.eventHandler"
            :designer="designer"
            :rule="{
              name: false,
              triggerSelectable: false,
              actionTypeSelectable: false,
              pageTypeSelectable: true,
              positionSelectable: false
            }"
            :allowEventParams="false"
          />

          <a-form-model-item label="弹窗规格">
            <a-select
              :options="[
                { label: '小', value: 'small' },
                { label: '中', value: 'middle' },
                { label: '大', value: 'large' },
                { label: '自定义', value: 'selfDefine' }
              ]"
              v-model="widget.configuration.size"
              :style="{ width: '100%' }"
            ></a-select>
          </a-form-model-item>
          <StyleConfiguration
            v-if="widget.configuration.size == 'selfDefine'"
            :widget="widget"
            :setWidthHeight="[true, true]"
          ></StyleConfiguration>
          <a-form-model-item label="默认全屏">
            <a-switch v-model="widget.configuration.fullscreen" />
          </a-form-model-item>
          <a-form-model-item label="全屏切换">
            <a-switch v-model="widget.configuration.switchFullscreen" />
          </a-form-model-item>

          <a-form-model-item label="默认展示">
            <a-switch v-model="widget.configuration.defaultVisible" />
          </a-form-model-item>
          <a-form-model-item label="关闭时销毁内容">
            <a-switch v-model="widget.configuration.destroyOnClose" />
          </a-form-model-item>
          <a-form-model-item label="显示右上角关闭按钮">
            <a-switch v-model="widget.configuration.closable" />
          </a-form-model-item>
          <!-- <a-form-model-item label="开启遮罩">
            <a-switch v-model="widget.configuration.mask" />
          </a-form-model-item> -->
          <a-form-model-item label="隐藏底部">
            <a-switch v-model="widget.configuration.footerHidden" />
          </a-form-model-item>
        </a-form-model>
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
            <a-input size="small" v-model="record.title" style="width: 150px">
              <template slot="addonAfter">
                <WI18nInput :widget="widget" :designer="designer" :code="record.id" :target="record" v-model="record.title" />
              </template>
            </a-input>
            <a-tag v-if="['ok', 'cancel'].includes(record.id)" :color="record.id === 'ok' ? 'blue' : ''">
              {{ record.id === 'ok' ? '确定' : '取消' }}
            </a-tag>
          </template>

          <template slot="operationSlot" slot-scope="text, record, index">
            <a-button v-if="!['ok', 'cancel'].includes(record.id)" size="small" title="删除按钮" type="link" @click="delButton(index)">
              <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
            </a-button>
            <a-button
              v-if="['ok', 'cancel'].includes(record.id)"
              size="small"
              :icon="record.visible ? 'eye' : 'eye-invisible'"
              :title="record.visible ? '点击隐藏' : '点击显示'"
              type="link"
              @click="clickChangeButtonVisible(record)"
            />

            <WidgetDesignDrawer :id="'modalButtonMoreConfig' + record.id" title="设置" :designer="designer" :zIndex="1001">
              <a-button size="small" title="更多设置" type="link"><Icon type="pticon iconfont icon-ptkj-shezhi"></Icon></a-button>
              <template slot="content">
                <ButtonConfiguration :button="record" :widget="widget" :designer="designer" />
              </template>
            </WidgetDesignDrawer>
          </template>
          <template slot="footer">
            <a-button type="link" @click="addButton" icon="plus-circle">添加按钮</a-button>
          </template>
        </a-table>
        <a-form-model-item label="弹窗关闭后执行事件" :wrapper-col="{ style: { 'text-align': 'right' } }">
          <WidgetDesignDrawer :id="'WidgetModalAfterCloseHandler' + widget.id" title="事件设置" :designer="designer">
            <a-button type="link" size="small">
              <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
              设置
            </a-button>
            <template slot="content">
              <WidgetEventHandler
                :eventModel="widget.configuration.afterCloseEventHandler"
                :designer="designer"
                :widget="widget"
                :rule="{ triggerSelectable: false, name: false }"
              />
            </template>
          </WidgetDesignDrawer>
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
import draggable from '@framework/vue/designer/draggable';
import { generateId, deepClone } from '@framework/vue/utils/util';
import ButtonConfiguration from './button-configuration.vue';
import configurationMixin from '@framework/vue/designer/configurationMixin.js';
import StyleConfiguration from './style-configuration.vue';

export default {
  name: 'WidgetModalConfiguration',
  mixins: [draggable, configurationMixin],
  props: {
    widget: Object,
    designer: Object,
    configuration: Object
  },
  data() {
    return {
      defaultTitle: this.widget.title,
      buttonColumns: [
        {
          title: '名称',
          dataIndex: 'title',
          scopedSlots: { customRender: 'titleSlot' }
        },
        {
          title: '操作',
          dataIndex: 'operation',
          width: 80,
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
  components: { ButtonConfiguration, StyleConfiguration },
  computed: {},
  created() {
    if (this.widget.configuration.eventHandler == undefined) {
      this.$set(this.widget.configuration, 'eventHandler', {
        actionType: 'redirectPage',
        pageType: 'page',
        trigger: 'click',
        pageUuid: undefined
      });
    }

    if (this.widget.configuration.hiddenTitle == undefined) {
      this.$set(this.widget.configuration, 'hiddenTitle', false);
    }
    if (this.widget.configuration.closable == undefined) {
      this.$set(this.widget.configuration, 'closable', true);
    }
    if (!this.widget.configuration.hasOwnProperty('fullscreen')) {
      this.$set(this.widget.configuration, 'fullscreen', false);
      this.$set(this.widget.configuration, 'switchFullscreen', false);
    }
  },
  methods: {
    getWidgetActionElements(wgt, designer) {
      let actionElements = [];
      actionElements.push(...this.resolveDefineEventToActionElement(wgt, designer));
      if (wgt.configuration.jsModules) {
        actionElements.push(...this.resolveJsModuleAsActionElement(wgt.configuration.jsModules, wgt, designer));
      }
      if (!wgt.configuration.footerHidden) {
        for (let btn of wgt.configuration.footerButton.buttons) {
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
      if (wgt.configuration.afterCloseEventHandler) {
        actionElements.push(
          ...this.resolveEventHandlerToActionElement(
            {
              // elementName: btn.title,
              // elementTypeName: '按钮'
              triggerName: '弹窗关闭后触发'
            },
            wgt.configuration.afterCloseEventHandler,
            wgt,
            designer
          )
        );
      }

      if (wgt.configuration.contentFromPage && (wgt.configuration.eventHandler.pageId || wgt.configuration.eventHandler.url)) {
        actionElements.push(
          ...this.resolveEventHandlerToActionElement({ triggerName: '打开弹窗' }, wgt.configuration.eventHandler, wgt, designer)
        );
      }
      return actionElements;
    },
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
    },
    onChangeModalName(evt) {
      let v = evt.target.value || this.defaultTitle;
      this.designer.widgetTreeMap[this.widget.id].title = v.trim();
    }
  },
  mounted() {
    this.tableDraggable(
      this.widget.configuration.footerButton.buttons,
      this.$el.querySelector('.widget-modal-button-table tbody'),
      '.drag-btn-handler'
    );
  },

  // MD5(widget) {
  //   // 计算组件MD5值用于判断是否发生变更
  //   let config = deepClone(widget.configuration);
  //   delete config.widgets;
  //   return md5(JSON.stringify(config));
  // },
  configuration() {
    return {
      title: undefined,
      size: 'small',
      width: 600,
      height: 500,
      footerHidden: false,
      defaultVisible: false,
      destroyOnClose: true,
      widgets: [],
      afterCloseEventHandler: { eventParams: [] },
      eventHandler: { actionType: 'redirectPage', pageType: 'page', trigger: 'click', pageUuid: undefined },
      footerButton: {
        // 底部按钮配置
        buttons: [
          {
            id: generateId(),
            code: 'cancel',
            CANCEL_BUTTON: true,
            title: '取消',
            visible: true,
            style: { icon: undefined, textHidden: false },
            eventHandler: { eventParams: [] },
            customEventScript: undefined
          },
          {
            id: generateId(),
            OK_BUTTON: true,
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
        padding: [12, 20, 12, 20]
      }
    };
  }
};
</script>
