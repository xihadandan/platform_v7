<template>
  <div>
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
          <a-form-model-item label="名称">
            <a-input v-model="widget.title" />
          </a-form-model-item>
          <ColorSelectConfiguration
            label="导航风格"
            v-model="widget.configuration"
            onlyValue
            colorField="backgroundColor"
            radioSize="small"
            :responsive="true"
          />
          <!-- <a-form-item label="导航风格">
            <a-radio-group v-model="widget.configuration.bgColorType" button-style="solid">
              <a-radio-button value="light">浅色</a-radio-button>
              <a-radio-button value="dark">深色</a-radio-button>
              <a-radio-button value="primary-color">主题色</a-radio-button>
            </a-radio-group>
          </a-form-item> -->
          <a-form-model-item label="JS模块">
            <JsModuleSelect v-model="widget.configuration.jsModules" dependencyFilter="WidgetNavBarDevelopment" />
          </a-form-model-item>
          <a-form-model-item label="左侧容器">
            <a-switch v-model="widget.configuration.enabledLeftContainer"></a-switch>
          </a-form-model-item>
          <!-- 左侧容器配置 -->
          <template v-if="widget.configuration.enabledLeftContainer">
            <a-form-model-item label="启用logo">
              <a-switch v-model="widget.configuration.enabledLogo"></a-switch>
            </a-form-model-item>
            <template v-if="widget.configuration.enabledLogo">
              <a-form-model-item label="logo配置">
                <ImageLibrary
                  v-model="widget.configuration.logoIcon"
                  width="100%"
                  height="60px"
                  :allowSelectType="['icon', 'commonImages', 'mongoImages']"
                />
              </a-form-model-item>
            </template>
            <a-form-model-item label="返回按钮">
              <a-switch v-model="widget.configuration.enabledBack"></a-switch>
            </a-form-model-item>
            <template v-if="widget.configuration.enabledBack">
              <a-form-model-item label="按钮图标">
                <WidgetDesignModal
                  title="选择图标"
                  :zIndex="10000"
                  :width="640"
                  dialogClass="pt-modal widget-icon-lib-modal"
                  :bodyStyle="{ height: '560px' }"
                  :maxHeight="560"
                  mask
                  bodyContainer
                >
                  <IconSetBadge v-model="widget.configuration.backButtonIcon" />
                  <template slot="content">
                    <WidgetIconLib v-model="widget.configuration.backButtonIcon" />
                  </template>
                </WidgetDesignModal>
              </a-form-model-item>
              <a-form-model-item label="显示按钮名称">
                <a-switch v-model="widget.configuration.enabledBackButton"></a-switch>
              </a-form-model-item>
              <a-form-model-item label="按钮名称" v-show="widget.configuration.enabledBackButton">
                <a-input v-model="widget.configuration.backButtonName"></a-input>
              </a-form-model-item>
            </template>
          </template>
          <a-form-model-item label="中间容器">
            <a-switch v-model="widget.configuration.enabledMiddleContainer"></a-switch>
          </a-form-model-item>
          <!-- 中间容器配置 -->
          <template v-if="widget.configuration.enabledMiddleContainer">
            <a-form-model-item label="显示标题">
              <a-switch v-model="widget.configuration.enabledMiddleTitle"></a-switch>
            </a-form-model-item>
            <template v-if="widget.configuration.enabledMiddleTitle">
              <a-form-model-item label="设置标题">
                <a-input v-model="widget.configuration.middleTitle"></a-input>
              </a-form-model-item>
              <a-form-model-item label="标题前置图标">
                <WidgetDesignModal
                  title="选择图标"
                  :zIndex="10000"
                  :width="640"
                  dialogClass="pt-modal widget-icon-lib-modal"
                  :bodyStyle="{ height: '560px' }"
                  :maxHeight="560"
                  mask
                  bodyContainer
                >
                  <IconSetBadge v-model="widget.configuration.addonBeforeIcon" />
                  <template slot="content">
                    <WidgetIconLib v-model="widget.configuration.addonBeforeIcon" />
                  </template>
                </WidgetDesignModal>
              </a-form-model-item>
              <a-form-model-item label="标题后置图标">
                <WidgetDesignModal
                  title="选择图标"
                  :zIndex="10000"
                  :width="640"
                  dialogClass="pt-modal widget-icon-lib-modal"
                  :bodyStyle="{ height: '560px' }"
                  :maxHeight="560"
                  mask
                  bodyContainer
                >
                  <IconSetBadge v-model="widget.configuration.addonAfterIcon" />
                  <template slot="content">
                    <WidgetIconLib v-model="widget.configuration.addonAfterIcon" />
                  </template>
                </WidgetDesignModal>
              </a-form-model-item>
            </template>
            <tabs-configuration :widget="widget.configuration.widgetTabs" :designer="designer" :configuration="widget.configuration" />
          </template>
          <a-form-model-item label="右侧容器">
            <a-switch v-model="widget.configuration.enabledRightContainer"></a-switch>
          </a-form-model-item>
          <!-- 右侧容器配置 -->
          <template v-if="widget.configuration.enabledRightContainer">
            <ButtonsConfiguration
              :widget="widget"
              :designer="designer"
              :button="widget.configuration.rightButtonConfig"
              title="右侧按钮"
              style="padding: 0 12px"
            />
            <!-- <a-form-model-item label="开启右侧拖拽组件">
              <a-switch v-model="widget.configuration.enabledRightWidgets"></a-switch>
            </a-form-model-item> -->
            <a-form-model-item label="显示下拉框">
              <a-switch v-model="widget.configuration.enabledRightSelect"></a-switch>
            </a-form-model-item>
            <template v-if="widget.configuration.enabledRightSelect">
              <select-configuration :widget="widget.configuration.widgetSelect" :designer="designer" />
            </template>
          </template>
        </a-form-model>
      </a-tab-pane>
      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script>
// import { cloneDeep } from 'lodash';
import { generateId } from '@framework/vue/utils/util';
import ButtonsConfiguration from '../../../commons/buttons-configuration/index.vue';
import WidgetTabConfiguration from '../../../widget-tab/configuration/index.vue';
import TabsConfiguration from '../components/tabs-configuration.vue';
// import WidgetSelectMetaInfo from '@dyform/app/web/widget/widget-form-select/META-INF';
import WidgetSelectConfiguration from '../../../widget-form/configuration/widget-select/configuration/index.vue';
import SelectConfiguration from '../components/select-configuration.vue';

import RightButtonConfiguration from '../components/right-button-configuration.vue';

// 创建按钮配置
const createButtonConfig = () => {
  return {
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
  };
};

// 生成标签组件配置
const generateWidgetTabConfiguration = () => {
  return {
    wtype: 'WidgetTab',
    name: '标签页',
    id: generateId(),
    configuration: WidgetTabConfiguration.configuration()
  };
};

const generateWidgetSelectConfiguration = () => {
  // let widget = cloneDeep(WidgetSelectMetaInfo[0]);
  // widget.id = generateId();
  // return widget;
  let configuration = WidgetSelectConfiguration.configuration();
  configuration.titleHidden = true;
  return {
    wtype: 'WidgetSelect',
    name: '下拉框',
    id: generateId(),
    configuration
  };
};

export default {
  name: 'WidgetUniNavBarConfiguration',
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {};
  },
  provide() {
    return {
      designer: this.designer,
      widget: this.widget,
      injectEventActionType: [{ label: '显示抽屉', value: 'showDrawer' }]
    };
  },
  components: {
    ButtonsConfiguration,
    TabsConfiguration,
    SelectConfiguration,
    RightButtonConfiguration
  },
  created() {},
  methods: {},
  createButtonConfig,
  generateWidgetTabConfiguration,
  generateWidgetSelectConfiguration,
  configuration() {
    return {
      enabledLeftContainer: true,
      enabledLogo: false,
      logoIcon: '',
      enabledBack: true,
      backButtonIcon: 'ant-iconfont left',
      enabledBackButton: false,
      backButtonName: '返回',

      enabledMiddleContainer: true,
      enabledMiddleTitle: true,
      middleTitle: '',
      addonBeforeIcon: '',
      addonAfterIcon: '',
      enabledMiddleWidgets: false,
      middleWidgets: [],
      enabledTabs: false,
      widgetTabs: generateWidgetTabConfiguration(),

      enabledRightContainer: false,
      enabledRightButton: false,
      rightButtonConfig: createButtonConfig(),
      enabledRightWidgets: false,
      rightWidgets: [],
      enabledRightSelect: false,
      widgetSelect: generateWidgetSelectConfiguration()
    };
  }
};
</script>
