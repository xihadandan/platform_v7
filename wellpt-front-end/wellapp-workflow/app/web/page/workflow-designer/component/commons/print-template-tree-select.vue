<template>
  <!-- 打印模板 -->
  <w-tree-select
    v-model="formData.printTemplateUuid"
    :treeData="treeData"
    :treeCheckable="true"
    :treeDefaultExpandAll="true"
    treeNodeFilterProp="title"
    :replaceFields="{
      children: 'children',
      title: 'name',
      key: 'id',
      value: 'id'
    }"
    @change="changePrintTemplate"
  />
</template>
<script>
import WTreeSelect from '../components/w-tree-select';
import { each, filter, findIndex } from 'lodash';
export default {
  name: 'PrintTemplateTreeSelect',
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    WTreeSelect
  },
  data() {
    return {
      treeData: []
    };
  },
  created() {
    this.getPrintTemplateTree();
  },
  methods: {
    // 获取打印模板
    getPrintTemplateTree(searchValue) {
      const params = {
        args: JSON.stringify([]),
        serviceName: 'printTemplateService',
        methodName: 'getPrintTemplateTree'
      };
      this.$axios
        .post('/json/data/services', {
          ...params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.code === 0) {
              const data = res.data.data;
              this.treeData = data;
            }
          }
        });
    },
    changePrintTemplate(value, label, extra) {
      this.formData.printTemplate = label.join(';');
      this.formData.printTemplateId = this.getIds(value, extra.allCheckedNodes);
    },
    getIds(value, allCheckedNodes) {
      let ids = [],
        idsJson = {};
      each(allCheckedNodes, (item, index) => {
        if (item.children) {
          each(item.children, (citem, cindex) => {
            idsJson[citem.node.key] = citem.node.data.props.data.id;
          });
        } else {
          idsJson[item.node.key] = item.node.data.props.data.id;
        }
      });
      if (value) {
        each(value.split(';'), item => {
          if (idsJson[item]) {
            ids.push(idsJson[item]);
          }
        });
      }
      return ids.join(';');
    }
  }
};
</script>
