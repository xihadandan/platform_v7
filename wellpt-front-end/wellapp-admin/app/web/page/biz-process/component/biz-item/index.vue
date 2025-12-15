<template>
  <a-card size="small" title="事项" :bordered="false">
    <a-collapse :bordered="false" defaultActiveKey="1">
      <a-collapse-panel key="1" header="事项定义">
        <template slot="extra">
          <a-space>
            <a-icon type="plus" title="新增单个事项定义" @click.stop="addItemDefinition('10')"></a-icon>
            <a-icon type="plus-circle" title="新增组合事项定义" @click.stop="addItemDefinition('20')"></a-icon>
          </a-space>
        </template>
        <a-menu mode="vertical" v-model="selectedItemKeys">
          <a-menu-item
            v-for="item in itemDefinitions"
            :key="item.uuid"
            :title="item.type == '10' ? '单个事项' : '组合事项'"
            @click="showItemDefinition(item)"
          >
            {{ item.name }}
          </a-menu-item>
        </a-menu>
      </a-collapse-panel>
      <a-collapse-panel key="2" header="事项定义表单">
        <a-menu mode="vertical">
          <a-menu-item v-for="item in formDefinitions" :key="item.uuid" @click="showItemDyform(item)">
            {{ item.name }}
          </a-menu-item>
        </a-menu>
      </a-collapse-panel>
      <a-collapse-panel key="3" header="事项定义信息">
        <a-menu mode="vertical">
          <a-menu-item v-for="item in formDefinitions" :key="item.uuid" @click="showItemDyformPage(item)">
            {{ item.name }}
          </a-menu-item>
        </a-menu>
      </a-collapse-panel>
    </a-collapse>
  </a-card>
</template>

<script>
import { isEmpty } from 'lodash';
import BizItemDefinition from './biz-item-definition.vue';
import BizItemDyform from './biz-item-dyform.vue';
import BizEntityPage from '../biz-entity/biz-entity-page.vue';

export default {
  props: {
    item: {
      type: Object,
      default() {
        return {};
      }
    }
  },
  components: { BizItemDefinition, BizItemDyform, BizEntityPage },
  inject: ['assemble', 'pageContext'],
  data() {
    return {
      businessId: this.assemble.processDefinition.businessId,
      selectedItemKeys: [],
      itemDefinitions: [],
      formDefinitions: []
    };
  },
  created() {
    this.loadItemDefinition();
  },
  mounted() {
    this.pageContext.handleEvent('loadItemDefinition', itemData => {
      this.loadItemDefinition().then(() => {
        if (itemData) {
          let itemDefinition = this.itemDefinitions.find(item => item.id == itemData.id);
          if (itemDefinition) {
            this.selectedItemKeys = [itemDefinition.uuid];
            this.showItemDefinition(itemDefinition);
          }
        } else if (this.itemDefinitions.length > 0) {
          this.selectedItemKeys = [this.itemDefinitions[0].uuid];
          this.showItemDefinition(this.itemDefinitions[0]);
        }
      });
    });
    // this.pageContext.handleEvent('loadItemFormDefinition', formDefinition => {
    //   if (formDefinition) {
    //     let item = this.formDefinitions.find(item => item.uuid == formDefinition.uuid);
    //     if (item) {
    //       Object.assign(item, formDefinition);
    //     } else {
    //       this.loadItemFormDefinition();
    //     }
    //   } else {
    //     this.loadItemFormDefinition();
    //   }
    // });
  },
  methods: {
    loadItemDefinition() {
      let businessId = this.businessId;
      if (!businessId) {
        return Promise.resolve();
      }

      return $axios.get(`/proxy/api/biz/item/definition/listByBusinessId?businessId=${businessId}`).then(({ data: result }) => {
        if (result.data) {
          this.itemDefinitions = result.data;
          this.loadItemFormDefinition();
        }
      });
    },
    loadItemFormDefinition() {
      const _this = this;
      if (isEmpty(_this.itemDefinitions)) {
        return;
      }

      let formIdSet = new Set();
      _this.itemDefinitions.forEach(item => {
        if (item.formId) {
          formIdSet.add(item.formId);
        }
      });

      let formIds = [...formIdSet.values()];
      $axios.get(`/proxy/api/dyform/definition/listByIds?ids=${formIds}`).then(({ data: result }) => {
        if (result.data) {
          _this.formDefinitions = result.data;
        }
      });
    },
    addItemDefinition(type = '10') {
      this.assemble.showContent({
        component: BizItemDefinition,
        metadata: { eventParams: { type, businessId: this.businessId }, businessIdEditable: false }
      });
    },
    showItemDefinition(itemDefinition) {
      this.assemble.showContent({
        component: BizItemDefinition,
        metadata: { itemDefinition, businessIdEditable: false }
      });
    },
    showItemDyform(formDefinition) {
      this.assemble.showContent({
        component: BizItemDyform,
        metadata: { formDefinition }
      });
    },
    showItemDyformPage(formDefinition) {
      const _this = this;
      let page = _this.item.pages[formDefinition.id] || { pageUuid: '', pageName: '', pageId: '', pageVersion: '' };
      _this.item.pages[formDefinition.id] = page;
      _this.assemble.showContent({
        component: BizEntityPage,
        metadata: { entity: page, defaultTitle: formDefinition.name + '表格' }
      });
    }
  }
};
</script>

<style></style>
