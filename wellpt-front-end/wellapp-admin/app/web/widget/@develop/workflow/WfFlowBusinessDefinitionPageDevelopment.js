import VuePageDevelopment from '@develop/VuePageDevelopment';

class WfFlowBusinessDefinitionPageDevelopment extends VuePageDevelopment {

  get META() {
    return {
      name: '流程业务定义页面二开',
      hook: {
        saveFlowBusinessDefinition: '保存流程业务定义',
      }
    };
  }

  mounted() {
  }

  saveFlowBusinessDefinition(event) {
    this.getVueWidgetById('DnDKApVgimsfybVkCKjvlzraGUnlKJmQ').$children[0].save(event);
  }
}

export default WfFlowBusinessDefinitionPageDevelopment;
