<script>
import ItemDyformSettingConfiguration from './item-dyform-setting-configuration.vue';
import { generateId } from '@framework/vue/utils/util';
export default {
  extends: ItemDyformSettingConfiguration,
  methods: {
    getJsModule() {
      return { key: 'BizProcessNodeWidgetDyformSetDevelopment', label: '业务阶段办件——表单设置' };
    },
    getButtons() {
      return [
        this.generateDyformSettingButton(generateId(), '保存', 'save', 'visible-condition', ['createForm', 'edit'], 'primary'),
        this.generateDyformSettingButton(generateId(), '打印表单', 'printForm', 'visible-condition', ['edit', 'label'], 'default')
      ];
    }
  }
};
</script>
