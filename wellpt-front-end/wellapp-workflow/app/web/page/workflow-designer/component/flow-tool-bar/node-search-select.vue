<template>
  <div style="font-weight: normal">
    <a-input v-model="searchKeyword" :allowClear="true" @change="searchNodes">
      <Icon slot="suffix" type="pticon iconfont icon-ptkj-sousuochaxun" />
    </a-input>
    <ul class="ant-select-dropdown-menu" v-if="searchResultNodes.length" style="max-width: 250px">
      <li
        v-for="item in searchResultNodes"
        :key="item.id"
        :class="{ 'ant-select-dropdown-menu-item': true, 'ant-select-dropdown-menu-item-selected': selectedNodeId === item.id }"
        :title="item.data.name"
        @click="handleSelectNode(item)"
      >
        <node-title :node="item" :searchKeyword="value" />
      </li>
    </ul>
    <div v-show="showEmpty" style="font-size: var(--w-font-size); color: var(--w-gray-color-9)">没有搜索到相关环节</div>
  </div>
</template>

<script>
import { debounce } from 'lodash';

export default {
  name: 'NodeSearchSelect',
  props: {
    value: {
      type: [String]
    },
    graphItem: {
      type: Object,
      default: () => {}
    },
    isTree: {
      type: Boolean,
      default: false
    }
  },
  components: {
    NodeTitle: {
      functional: true,
      render: (h, ctx) => {
        const { node, searchKeyword } = ctx.props;
        let title = node.data.name;
        if (title.indexOf(searchKeyword) > -1) {
          return (
            <span>
              {title.substr(0, title.indexOf(searchKeyword))}
              <span style="color: var(--w-primary-color)">{searchKeyword}</span>
              {title.substr(title.indexOf(searchKeyword) + searchKeyword.length)}
            </span>
          );
        } else {
          return <span>{title}</span>;
        }
      }
    }
  },
  data() {
    return {
      searchResultNodes: [],
      selectedNodeId: '',
      searchKeyword: this.value
    };
  },
  computed: {
    showEmpty() {
      let show = true;
      this.setResultNodes(this.value);
      if (this.searchResultNodes.length) {
        show = false;
      } else if (this.isTree && !this.value.trim()) {
        show = false;
      }
      return show;
    }
  },
  methods: {
    // 搜索节点
    searchNodes: debounce(function (event) {
      let value = event.target.value;
      value = value.trim();
      this.$emit('input', value);
      this.$emit('change', value);
    }, 300),
    setResultNodes(value) {
      if (!value) {
        this.searchResultNodes = [];
        return;
      }
      const nodes = this.graphItem.getNodes();
      let resultNodes = [];
      for (let index = 0; index < nodes.length; index++) {
        const node = nodes[index];
        if (node.data && node.data.name) {
          if (node.data.name.indexOf(value) !== -1) {
            resultNodes.push(node);
          }
        }
      }
      this.searchResultNodes = resultNodes;
    },
    // 选中节点
    handleSelectNode(node) {
      this.selectedNodeId = node.id;
      this.graphItem.centerCell(node);
      this.graphItem.resetSelection(node, { save: true });
    }
  }
};
</script>
