<template>
  <div>
    <a-space>
      <a-button @click="onAddExtAttr" type="link">
        <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
        新建
      </a-button>
    </a-space>
    <a-table rowKey="id" :pagination="false" :data-source="extAttrs" :columns="extAttrColumns" class="pt-table">
      <template slot="attrNameSlot" slot-scope="text, record, index">
        <a-input v-model="record.attrName" />
      </template>
      <template slot="attrKeySlot" slot-scope="text, record, index">
        <a-input v-model="record.attrKey" />
      </template>
      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button type="link" @click.stop="onDeleteExtAttr(index)">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
          删除
        </a-button>
      </template>
    </a-table>
  </div>
</template>

<script>
import { generateId, deepClone } from '@framework/vue/utils/util';
import { isEmpty, trim } from 'lodash';
export default {
  props: {
    dictionaryExtDefinitionJson: {
      type: Object,
      default() {
        return { extAttrs: [] };
      }
    }
  },
  data() {
    let extAttrs = deepClone(this.dictionaryExtDefinitionJson.extAttrs || []);
    return {
      extAttrs,
      extAttrColumns: [
        {
          title: '属性名称',
          dataIndex: 'attrName',
          scopedSlots: { customRender: 'attrNameSlot' }
        },
        {
          title: '属性编码',
          dataIndex: 'attrKey',
          scopedSlots: { customRender: 'attrKeySlot' }
        },
        {
          title: '操作',
          dataIndex: 'operation',
          width: 70,
          align: 'right',
          class: 'operation',
          scopedSlots: { customRender: 'operationSlot' }
        }
      ]
    };
  },
  methods: {
    onAddExtAttr() {
      this.extAttrs.push({ id: generateId(), attrName: null, attrKey: null });
    },
    onDeleteExtAttr(index) {
      this.extAttrs.splice(index, 1);
    },
    validate() {
      for (let index = 0; index < this.extAttrs.length; index++) {
        if (isEmpty(this.extAttrs[index].attrName)) {
          this.$message.error('属性名称不能为空！');
          return false;
        }
        if (isEmpty(this.extAttrs[index].attrKey)) {
          this.$message.error('属性编码不能为空！');
          return false;
        }
      }
      return true;
    },
    confirm() {
      for (let index = 0; index < this.extAttrs.length; index++) {
        let extAttr = this.extAttrs[index];
        extAttr.attrName = trim(extAttr.attrName);
        extAttr.attrKey = trim(extAttr.attrKey);
      }
      this.dictionaryExtDefinitionJson.extAttrs = this.extAttrs;
      return this.dictionaryExtDefinitionJson;
    }
  }
};
</script>

<style></style>
