<template>
  <div style="padding: 0 12px">
    <button-list-table
      :dataSource="widget.configuration.tabs"
      :showHeader="false"
      :createItem="createItem"
      detailDisplay="drawer"
      :enableTable="configuration.enabledTabs"
      @add="addItem"
    >
      <div style="display: flex; justify-content: space-between; align-items: center" slot="tableTitle">
        <div style="text-align: left">
          <i class="line" />
          标签设置
        </div>
        <a-switch v-model="configuration.enabledTabs" />
      </div>
      <template v-slot:buttonInfo="{ currentItem }">
        <tab-configuration :widget="widget" :designer="designer" :tab="currentItem" />
      </template>
      <template v-slot:titleSlot="{ record, text }">
        <a-input
          :value="text"
          size="small"
          style="width: 180px"
          @change="
            $evt => {
              record.title = $evt.target.value;
              designer.widgetTreeMap[record.id].title = record.title;
            }
          "
        >
          <template slot="suffix">
            <a-icon type="link" v-show="record.configuration.eventHandler.pageUuid" style="margin-right: 5px" />
            <a-switch
              size="small"
              :checked="widget.configuration.defaultActiveKey == record.id"
              @change="checked => onChangeDefaultActive(checked, record.id)"
            />
          </template>
          <template slot="addonAfter">
            <WI18nInput
              :widget="widget"
              :designer="designer"
              :target="record.configuration"
              :code="record.id + '.title'"
              v-model="record.title"
            />
          </template>
        </a-input>
      </template>
    </button-list-table>
  </div>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import ButtonListTable from '../../../widget-carousel/configuration/button-list-table.vue';
import TabConfiguration from '../../../widget-tab/configuration/tab-configuration.vue';

export default {
  name: 'TabsConfiguration',
  props: {
    widget: Object,
    designer: Object,
    configuration: Object
  },
  data() {
    return {};
  },
  components: {
    ButtonListTable,
    TabConfiguration
  },
  methods: {
    addItem(item) {
      this.widget.configuration.tabs.push(item);
    },
    onChangeDefaultActive(checked, id) {
      this.widget.configuration.defaultActiveKey = checked ? id : null;
      if (this.widget.configuration.defaultActiveKey == null && this.widget.configuration.tabs.length) {
        // 默认第一个激活
        this.widget.configuration.defaultActiveKey = this.widget.configuration.tabs[0].id;
      }
    },
    createItem() {
      return {
        id: `tab-${generateId()}`,
        wtype: 'WidgetTabItem',
        title: '页签',
        configuration: {
          icon: undefined,
          closable: true,
          forceRender: true,
          widgets: [],
          eventHandler: {
            actionType: 'redirectPage',
            pageType: 'page',
            trigger: 'click',
            pageUuid: undefined
          }
        }
      };
    }
  }
};
</script>
