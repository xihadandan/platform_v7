<template>
  <div>
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <DyformBasicConfiguration :widget="widget" :designer="designer" />
      </a-tab-pane>
      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>
<style></style>
<script type="text/babel">
import DyformBasicConfiguration from './dyform-basic-configuration.vue';
export default {
  name: 'WidgetDyformSettingConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    configuration: Object
  },
  data() {
    return {};
  },

  beforeCreate() {},
  components: { DyformBasicConfiguration },
  computed: {},
  created() {},
  methods: {
    // 生成组件定义数据保存
    getWidgetDefinitionElements(widget) {
      return [
        {
          wtype: widget.wtype,
          id: widget.id,
          title: widget.title,
          definitionJson: JSON.stringify(widget)
        }
      ];
    },

    getFunctionElements(wgt) {
      let functionElements = {},
        elements = [];
      let configuration = wgt.configuration,
        { formUuid, formName, labelStateFormUuid, editStateFormUuid, enableStateForm, labelStateFormName, editStateFormName } =
          configuration;
      // 引用的定义性资源信息
      elements.push({
        ref: true,
        functionType: 'formDefinition',
        exportType: 'formDefinition',
        configType: '1',
        uuid: formUuid,
        name: formName,
        isProtected: false
      });
      if (enableStateForm) {
        elements.push({
          ref: true,
          functionType: 'formDefinition',
          exportType: 'formDefinition',
          configType: '1',
          uuid: labelStateFormUuid,
          name: labelStateFormName,
          isProtected: false
        });
        elements.push({
          ref: true,
          functionType: 'formDefinition',
          exportType: 'formDefinition',
          configType: '1',
          uuid: editStateFormUuid,
          name: editStateFormName,
          isProtected: false
        });
      }

      functionElements[wgt.id] = elements;
      return functionElements;
    }
  },
  beforeMount() {},
  mounted() {},
  configuration(wgt) {
    return {
      title: undefined,
      titleIcon: undefined,
      formUuid: undefined,
      formName: undefined,
      buttonPosition: 'disable', // disable / top / bottom
      enableStateForm: false, // 按状态设置表单
      editStateFormUuid: undefined, //编辑表单
      labelStateFormUuid: undefined, //查阅表单
      useRequestForm: false, // 是否使用请求数据的表单

      dataUuid: undefined,
      titleVisible: wgt != undefined && wgt.forceRender === false ? true : !location.pathname.endsWith('dyform-designer/index'), //是否展示标题栏
      editStateTitle: undefined, // 编辑状态的标题
      labelStateTitle: undefined, // 查阅状态的标题
      // editStateTitleIcon:undefined,
      // labelStateTitleIcon:''
      titleRenderScript: undefined, // 标题渲染脚本
      formElementRules: [],
      labelStateFormElementRules: [],
      editStateFormElementRules: [],
      button: {
        buttons: [],
        buttonGroup: {
          type: 'notGroup', // notGroup: 不分组  fixedGroup: 固定分组 dynamicGroup: 动态分组
          groups: [
            // 固定分组
            // {name:,buttonIds:[]}
          ],
          dynamicGroupName: '更多', //动态分组名称
          dynamicGroupBtnThreshold: 3 // 分组按钮数阈值，达到该数才触发分组
        },

        buttonAlign: 'center'
      }
      // buttons: [],
      // buttonGroups: [],
      // buttonAlign: 'center',
      // buttonGroupType: 'disable',
      // dynamicGroupBtnName: '更多',
      // dynamicGroupBtnThreshold: 3 // 动态分组阈值
    };
  }
};
</script>
