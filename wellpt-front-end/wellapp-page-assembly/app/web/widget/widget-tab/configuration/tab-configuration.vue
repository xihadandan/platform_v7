<template>
  <div>
    <a-form-model>
      <a-form-model-item label="页签名称">
        <a-input v-model="tab.title">
          <template slot="addonAfter">
            <WI18nInput :widget="widget" :designer="designer" :target="tab.configuration" :code="tab.id + '.title'" v-model="tab.title" />
          </template>
        </a-input>
      </a-form-model-item>

      <a-form-model-item label="默认激活">
        <a-switch :checked="widget.configuration.defaultActiveKey == tab.id" @change="checked => onChangeDefaultActive(checked, tab.id)" />
      </a-form-model-item>

      <a-form-model-item
        v-if="designer.terminalType == 'pc'"
        label="是否可关闭"
        v-show="widget.configuration.tabStyleType === 'editable-card'"
      >
        <a-switch v-model="tab.configuration.closable" />
      </a-form-model-item>

      <a-form-model-item label="初始时渲染">
        <a-switch v-model="tab.configuration.forceRender" />
      </a-form-model-item>
      <a-form-model-item label="切换时刷新">
        <a-switch v-model="tab.configuration.refresh" />
      </a-form-model-item>

      <a-form-model-item label="页签图标" v-if="designer.terminalType == 'pc'">
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
          <IconSetBadge v-model="tab.configuration.icon"></IconSetBadge>
          <template slot="content">
            <WidgetIconLib v-model="tab.configuration.icon" />
          </template>
        </WidgetDesignModal>
      </a-form-model-item>
      <keyword-search-configuration
        :widget="tab"
        :designer="designer"
        :configuration="tab.configuration"
        v-if="designer.terminalType == 'pc'"
      />

      <BadgeConfiguration :designer="designer" :widget="widget" :configuration="tab.configuration" />

      <a-form-model-item label="页签附加按钮" v-if="tab.configuration.tabButton != undefined && designer.terminalType == 'pc'">
        <ButtonConfiguration :widget="widget" :button="tab.configuration.tabButton" :designer="designer" title="按钮列表" />
      </a-form-model-item>

      <DefaultVisibleConfiguration
        compact
        :designer="designer"
        :configuration="tab.configuration"
        :widget="widget"
        :hasDyformField="true"
      ></DefaultVisibleConfiguration>
    </a-form-model>

    <WidgetEventHandler
      :widget="widget"
      :eventModel="tab.configuration.eventHandler"
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
  </div>
</template>
<script type="text/babel">
import ButtonConfiguration from '../../commons/buttons-configuration/index.vue';
import KeywordSearchConfiguration from '../../widget-card/components/keyword-search-configuration.vue';

export default {
  name: 'TabConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    tab: Object
  },
  data() {
    return {};
  },
  components: {
    ButtonConfiguration,
    KeywordSearchConfiguration
  },
  computed: {
    fieldVarOptions() {
      let opt = [];
      for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
        let info = this.designer.SimpleFieldInfos[i];
        opt.push({
          label: info.name,
          value: info.code
        });
      }
      return opt;
    }
  },
  created() {
    if (!this.tab.configuration.hasOwnProperty('tabButton')) {
      this.$set(this.tab.configuration, 'tabButton', {
        enable: false,
        buttons: [],
        buttonGroup: {
          type: 'notGroup', // notGroup: 不分组  fixedGroup: 固定分组 dynamicGroup: 动态分组
          groups: [
            // 固定分组
            // {name:,buttonIds:[]}
          ],
          style: { textHidden: false, type: 'default', icon: undefined, rightDownIconVisible: false },
          dynamicGroupName: '更多', //动态分组名称
          dynamicGroupBtnThreshold: 3 // 分组按钮数阈值，达到该数才触发分组
        }
      });
    }
    if (!this.tab.configuration.hasOwnProperty('forceRender')) {
      this.$set(this.tab.configuration, 'forceRender', true);
    }
    if (!this.tab.configuration.hasOwnProperty('refresh')) {
      this.$set(this.tab.configuration, 'refresh', false);
    }
  },
  methods: {
    onChangeDefaultActive(checked, id) {
      this.widget.configuration.defaultActiveKey = checked ? id : null;
      if (this.widget.configuration.defaultActiveKey == null && this.widget.configuration.tabs.length) {
        // 默认第一个激活
        this.widget.configuration.defaultActiveKey = this.widget.configuration.tabs[0].id;
      }
    }
  }
};
</script>
