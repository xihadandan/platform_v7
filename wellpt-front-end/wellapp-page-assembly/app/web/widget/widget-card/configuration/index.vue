<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <!-- 仅有一个tab时加上class="one-tab",不是请移除 -->
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <DeviceVisible :widget="widget" />
        <a-form-model-item label="名称">
          <a-input v-model="widget.title"></a-input>
        </a-form-model-item>
        <a-form-model-item label="编码">
          <a-input v-model="widget.configuration.code"></a-input>
        </a-form-model-item>
        <a-form-model-item label="标题">
          <a-input v-model="widget.configuration.title">
            <template slot="addonAfter">
              <a-switch
                :checked="!widget.configuration.hideTitle"
                size="small"
                @change="widget.configuration.hideTitle = !widget.configuration.hideTitle"
                title="显示标题"
              />
              <WI18nInput
                v-show="!widget.configuration.hideTitle"
                :widget="widget"
                :designer="designer"
                code="title"
                v-model="widget.configuration.title"
              />
            </template>
          </a-input>
        </a-form-model-item>
        <template v-if="!widget.configuration.hideTitle">
          <a-form-model-item label="标题图标">
            <WidgetDesignDrawer
              :id="'widgetCardTitleIconset' + widget.id"
              title="选择图标"
              :designer="designer"
              :width="640"
              :bodyStyle="{ height: '100%' }"
            >
              <IconSetBadge v-model="widget.configuration.titleIcon"></IconSetBadge>

              <template slot="content">
                <WidgetIconLib v-model="widget.configuration.titleIcon" />
              </template>
            </WidgetDesignDrawer>
          </a-form-model-item>
          <a-form-model-item label="副标题">
            <a-input v-model="widget.configuration.subTitle">
              <template slot="addonAfter">
                <a-switch
                  :checked="!widget.configuration.hideSubTitle"
                  size="small"
                  @change="widget.configuration.hideSubTitle = !widget.configuration.hideSubTitle"
                  title="显示副标题"
                />
                <WI18nInput
                  v-show="!widget.configuration.hideSubTitle"
                  :widget="widget"
                  :designer="designer"
                  code="subTitle"
                  v-model="widget.configuration.subTitle"
                />
              </template>
            </a-input>
          </a-form-model-item>
        </template>
        <a-form-model-item label="卡片尺寸">
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
        <keyword-search-configuration :widget="widget" :designer="designer" :configuration="widget.configuration" />
        <a-form-model-item label="JS模块">
          <JsModuleSelect v-model="widget.configuration.jsModules" dependencyFilter="WidgetCardDevelopment" />
        </a-form-model-item>
        <a-collapse :bordered="false" expandIconPosition="right">
          <a-collapse-panel key="mobileSetting" header="移动端设置" v-if="designer.terminalType == 'mobile'">
            <a-form-model-item label="隐藏卡片样式">
              <a-switch
                :checked="widget.configuration.uniConfiguration.hideCard"
                @change="widget.configuration.uniConfiguration.hideCard = !widget.configuration.uniConfiguration.hideCard"
              />
            </a-form-model-item>
            <a-form-model-item label="隐藏卡片头部" v-if="!widget.configuration.uniConfiguration.hideCard">
              <a-switch
                :checked="widget.configuration.uniConfiguration.hideCardHeader"
                @change="widget.configuration.uniConfiguration.hideCardHeader = !widget.configuration.uniConfiguration.hideCardHeader"
              />
            </a-form-model-item>
            <a-form-model-item label="隐藏卡片内容边距" v-if="!widget.configuration.uniConfiguration.hideCard">
              <a-switch
                :checked="widget.configuration.uniConfiguration.hideCardBody"
                @change="widget.configuration.uniConfiguration.hideCardBody = !widget.configuration.uniConfiguration.hideCardBody"
              />
            </a-form-model-item>
          </a-collapse-panel>
          <a-collapse-panel key="operationSetting" header="操作设置">
            <ButtonsConfiguration :widget="widget" :designer="designer" :button="widget.configuration.headerButton" title="头部按钮" />
          </a-collapse-panel>
          <a-collapse-panel key="visibleSetting" header="显示设置">
            <a-form-model-item label="显示边框">
              <a-switch v-model="widget.configuration.bordered" />
            </a-form-model-item>
            <DefaultVisibleConfiguration :designer="designer" :configuration="widget.configuration" :widget="widget" />
          </a-collapse-panel>
          <a-collapse-panel key="badgeSetting" header="徽标设置" v-if="!widget.configuration.hideTitle">
            <BadgeConfiguration :designer="designer" :widget="widget" :configuration="widget.configuration" />
          </a-collapse-panel>
          <a-collapse-panel key="styleSetting" header="样式设置">
            <a-form-model-item label="组件填充父级容器">
              <a-switch v-model="widget.configuration.fillContainer" />
            </a-form-model-item>

            <StyleConfiguration :widget="widget" :setWidthHeight="[false, true]" :editBlock="false" :setMarginPadding="false" />
          </a-collapse-panel>
        </a-collapse>
      </a-tab-pane>
      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>
<style>
.widget-card-configuration .ant-form-item-label {
  width: 35%;
}
</style>
<script type="text/babel">
import ButtonsConfiguration from '../../commons/buttons-configuration/index.vue';
import configurationMixin from '@framework/vue/designer/configurationMixin.js';
import keywordSearchConfiguration from '../components/keyword-search-configuration.vue';

export default {
  name: 'WidgetCardConfiguration',
  mixins: [configurationMixin],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    let height = undefined,
      heightUnit = 'px';
    if (this.widget.configuration.height) {
      height = parseInt(this.widget.configuration.height);
      heightUnit = this.widget.configuration.height.indexOf('%') != -1 ? '%' : heightUnit;
    }
    return { height, heightUnit };
  },

  beforeCreate() {},
  components: { ButtonsConfiguration, keywordSearchConfiguration },
  computed: {},
  created() {
    if (!this.widget.configuration.headerButton) {
      this.$set(this.widget.configuration, 'headerButton', {
        enable: false,
        buttons: [],
        buttonGroup: {
          type: 'notGroup', // notGroup: 不分组  fixedGroup: 固定分组 dynamicGroup: 动态分组
          groups: [
            // 固定分组
            // {name:,buttonIds:[]}
          ],
          dynamicGroupName: '更多', //动态分组名称
          dynamicGroupBtnThreshold: 3 // 分组按钮数阈值，达到该数才触发分组
        }
      });
    }
    if (!this.widget.configuration.hasOwnProperty('uniConfiguration')) {
      this.$set(this.widget.configuration, 'uniConfiguration', {
        hideCard: false,
        hideCardHeader: false,
        hideCardBody: false
      });
    }
  },
  methods: {
    onInputHeight(e) {
      this.widget.configuration.height = this.height + this.heightUnit;
    },
    getWidgetActionElements(wgt, designer) {
      let actionElements = [];
      actionElements.push(...this.resolveDefineEventToActionElement(wgt, designer));
      if (wgt.configuration.jsModules) {
        actionElements.push(...this.resolveJsModuleAsActionElement(wgt.configuration.jsModules, wgt, designer));
      }
      if (wgt.configuration.headerButton.enable) {
        for (let btn of wgt.configuration.headerButton.buttons) {
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

      return actionElements;
    }
  },
  mounted() {},
  configuration() {
    return {
      title: undefined,
      code: undefined,
      hideTitle: true,
      titleIcon: undefined,
      subTitle: undefined,
      hideSubTitle: true,
      size: 'default',
      hidden: false,
      bordered: true,
      widgets: [],
      height: 'auto', // 默认高度自动
      headerButton: {
        enable: false,
        buttons: [],
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
      uniConfiguration: {
        hideCard: false,
        hideCardHeader: false,
        hideCardBody: false
      },
      style: {
        margin: [0, 0, 0, 0],
        padding: [10, 10, 10, 10]
      }
    };
  }
};
</script>
