<template>
  <a-tree-select
    showSearch
    allowClear
    :defaultValue="value"
    style="width: 100%"
    :labelInValue="true"
    :treeCheckable="true"
    treeDataSimpleMode
    :multiple="true"
    treeNodeFilterProp="title"
    :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
    :tree-data="treeData"
    :load-data="fetchTreeData"
    @change="onChange"
    :getPopupContainer="getPopupContainerByPs()"
    :dropdownClassName="getDropdownClassName()"
    :dropdownMatchSelectWidth="true"
    :showArrow="true"
  ></a-tree-select>
</template>
<style></style>
<script type="text/babel">
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
export default {
  name: 'FieldApplySelect',
  mixins: [],
  props: {
    value: Array
  },
  data() {
    return { treeData: [] };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
    fetchTreeData(treeNode) {
      let _this = this;

      return new Promise(resolve => {
        setTimeout(() => {
          $axios
            .post('/json/data/services', {
              serviceName: 'dyFormFacade',
              methodName: 'getFormFieldApplyToRootDicts',
              args: JSON.stringify([treeNode ? treeNode.dataRef.id : -1])
            })
            .then(({ data }) => {
              let nodes = data.data;
              // 根节点
              for (let i = 0, len = nodes.length; i < len; i++) {
                _this.treeData.push({
                  id: nodes[i].id,
                  pId: treeNode ? treeNode.dataRef.id : 0,
                  title: nodes[i].name,
                  value: nodes[i].data,
                  checkable: !!treeNode,
                  isLeaf: !!treeNode
                });
              }
            });

          resolve();
        }, 300);
      });
    },
    onChange(value, label) {
      this.$emit('input', value);
    }
  },
  mounted() {
    this.fetchTreeData();
  }
};
</script>
