<template>
  <div style="padding: 0 20px">
    <button-list-table :dataSource="widget.configuration.rightButtons" :showHeader="false">
      <div style="text-align: left" slot="tableTitle">按钮设置</div>
      <template v-slot:buttonInfo="{ visible, currentItem, getButtonInfoVm }">
        <button-info v-if="visible" :formData="currentItem" :getButtonInfoVm="getButtonInfoVm">
          <template slot="fieldExtend">
            <a-form-model-item label="按钮结构">
              <a-radio-group v-model="currentItem.displayStyle" :options="displayStyleOptions" />
            </a-form-model-item>
          </template>
          <template slot="buttonEvent">
            <button-event-config
              :eventModel="currentItem.eventHandler"
              :designer="designer"
              :widget="widget"
              :rule="{
                name: false
              }"
              :formLayout="{
                layout: 'horizontal',
                colon: false,
                labelCol: { flex: '120px' },
                wrapperCol: { flex: 'auto' }
              }"
            />
          </template>
        </button-info>
      </template>
    </button-list-table>
  </div>
</template>

<script>
import ButtonListTable from '../../../widget-carousel/configuration/button-list-table.vue';
import ButtonInfo from '../../../widget-carousel/configuration/button-info.vue';
import ButtonEventConfig from './button-event-config.vue';

export default {
  name: 'RightButtonConfiguration',
  inject: ['designer', 'widget'],
  data() {
    return {
      displayStyleOptions: [
        { label: '图标左、文字右', value: 'iconLeft_textRight' },
        { label: '文字左、图标右', value: 'textLeft_IconRight' },
        { label: '图标上、文字下（居中）', value: 'iconTop_textBottom' },
        { label: '文字上居左、图标下居右', value: 'textTop_iconBottom' }
      ]
    };
  },
  components: {
    ButtonListTable,
    ButtonInfo,
    ButtonEventConfig
  }
};
</script>
