<template>
  <div
    class="draggable-tree-list"
    :style="{
      paddingBottom: draggable ? '0px' : '5px'
    }"
    :key="wKey"
  >
    <vuedraggable
      v-if="draggable"
      v-model="vList"
      :group="{ name: 'tree', pull: draggable, put: draggable }"
      animation="300"
      :handle="dragButton ? '.drag-button-1' : undefined"
      :data-level="1"
      :style="{ paddingBottom: '5px' }"
    >
      <ListItem
        v-for="(item, i) in vList"
        :key="'list_' + i"
        :item="item"
        :childrenField="childrenField"
        :keyField="keyField"
        :titleField="titleField"
        :checkedField="checkedField"
        :noCheckField="noCheckField"
        :noCheckbox="noCheckbox"
        :index="i"
        :items="vList"
        :level="1"
        :dragButton="dragButton"
        :draggable="draggable"
        :showLeaveIcon="showLeaveIcon"
        :expandIcon="expandIcon"
        :indentSize="indentSize"
        :titleWidth="titleWidth"
        :hoverShowButtons="hoverShowButtons"
        :dragButtonPosition="dragButtonPosition"
        :dragButtonType="dragButtonType"
        :itemClass="itemClass"
        @add="onAdd"
        @remove="onRemove"
        @edit="onEdit"
        ref="listItem"
      >
        <template slot="buttons" slot-scope="scope">
          <slot name="operation" :item="scope.item" :index="scope.index" :level="scope.level" :items="scope.items"></slot>
        </template>
        <template slot="title" slot-scope="scope">
          <slot name="title" :item="scope.item" :index="scope.index" :level="scope.level" :items="scope.items"></slot>
        </template>
      </ListItem>
    </vuedraggable>

    <template v-else>
      <ListItem
        v-for="(item, i) in vList"
        :key="'list_' + i"
        :item="item"
        :childrenField="childrenField"
        :keyField="keyField"
        :titleField="titleField"
        :checkedField="checkedField"
        :noCheckField="noCheckField"
        :noCheckbox="noCheckbox"
        :index="i"
        :items="vList"
        :level="1"
        :dragButton="dragButton"
        :draggable="draggable"
        :showLeaveIcon="showLeaveIcon"
        :expandIcon="expandIcon"
        :indentSize="indentSize"
        :titleWidth="titleWidth"
        :hoverShowButtons="hoverShowButtons"
        :dragButtonPosition="dragButtonPosition"
        :dragButtonType="dragButtonType"
        :itemClass="itemClass"
        @add="onAdd"
        @remove="onRemove"
        @edit="onEdit"
        ref="listItem"
      >
        <template slot="buttons" slot-scope="scope">
          <slot name="operation" :item="scope.item" :index="scope.index" :level="scope.level" :items="scope.items"></slot>
        </template>
        <template slot="title" slot-scope="scope">
          <slot name="title" :item="scope.item" :index="scope.index" :level="scope.level" :items="scope.items"></slot>
        </template>
      </ListItem>
    </template>
  </div>
</template>
<style lang="less">
.draggable-tree-list {
  --w-pt-draggable-tree-list-color: var(--w-text-color-dark);
  --w-pt-draggable-tree-list-color-hover: var(--w-text-color-dark);
  --w-pt-draggable-tree-list-color-selected: var(--w-primary-color);

  --w-pt-draggable-tree-list-bg-color-hover: var(--w-fill-color-light);
  --w-pt-draggable-tree-list-bg-color-selected: var(--w-primary-color-1);

  --w-pt-draggable-tree-list-font-size: var(--w-font-size-base);
  --w-pt-draggable-tree-list-font-weight-hover: normal;
  --w-pt-draggable-tree-list-font-weight-selected: normal;

  --w-pt-draggable-tree-list-broder-radius: var(--w-border-radius-2);

  padding: 0px 3px;
  .tree-row {
    margin-bottom: 2px;
  }
  .tree-row-item-col {
    line-height: 24px;
    padding: var(--w-padding-2xs);
    border-radius: var(--w-pt-draggable-tree-list-broder-radius);
    font-size: var(--w-pt-draggable-tree-list-font-size);
    color: var(--w-pt-draggable-tree-list-color);
    cursor: pointer;
    &.selected {
      background-color: var(--w-pt-draggable-tree-list-bg-color-selected);
      color: var(--w-pt-draggable-tree-list-color-selected);
      font-weight: var(--w-pt-draggable-tree-list-font-weight-selected);
    }
    &:hover {
      background-color: var(--w-pt-draggable-tree-list-bg-color-hover);
      color: var(--w-pt-draggable-tree-list-color-hover);
      font-weight: var(--w-pt-draggable-tree-list-font-weight-hover);
      > div:first-child > div:last-child {
        &.hover-show {
          opacity: 1;
        }
      }
    }
    > div:first-child {
      display: flex;
      align-items: center;
      > div {
        display: flex;
        align-items: center;
        flex: 1 1 auto;
        &.matched {
          color: #bbbb3c;
          box-shadow: 1px 1px 3px 0px #bdcb91;
        }
        label {
          padding-left: 5px;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
          display: inline-block;
        }
      }
      > div:last-child {
        flex: 0 0 32px;
        &.hover-show {
          opacity: 0;
        }
      }
    }
  }
  .sortable-ghost {
    // background-color: var(--w-primary-color-4);
    // > .tree-row-item-col > div {
    content: '';
    font-size: 0;
    height: 0px;
    box-sizing: border-box;
    outline: 2px solid var(--w-primary-color);
    padding: 0;
    overflow: hidden;
    display: block;
    width: 100%;
    // }
  }

  // .sortable-chosen {
  //   outline: 2px solid var(--w-primary-color);
  //   border-radius: 3px;
  // }
}
</style>
<script type="text/babel">
import ListItem from './list-item.vue';
export default {
  name: 'DraggableTreeList',
  props: {
    value: Array,
    selectMode: String, // multiple / single / no
    afterSelected: Function,
    noCheckbox: Boolean,
    draggable: {
      type: Boolean,
      default: false
    },
    dragButton: {
      type: Boolean,
      default: false
    },
    dragButtonPosition: {
      type: String,
      default: 'end'
    },
    dragButtonType: {
      type: String,
      default: 'link'
    },
    keyField: {
      type: String,
      default: 'id'
    },
    checkedField: {
      type: String,
      default: 'checked'
    },
    noCheckField: {
      type: String,
      default: 'nocheck'
    },
    childrenField: {
      type: String,
      default: 'children'
    },
    titleField: {
      type: String,
      default: 'title'
    },
    maxLevel: {
      type: Number,
      default: 1000
    },
    showLeaveIcon: {
      type: Boolean,
      default: true
    },
    expandIcon: {
      type: String,
      default: 'folder'
    },
    indentSize: {
      type: Number,
      default: 14
    },
    titleWidth: {
      type: Number,
      default: 150
    },
    hoverShowButtons: {
      type: Boolean,
      default: false
    },
    defaultExpandAll: {
      type: Boolean,
      default: false
    },
    itemClass: String | Function
  },
  components: { ListItem, vuedraggable: () => import(/* webpackChunkName: "vuedraggable" */ 'vuedraggable') },
  provide() {
    return {
      options: this.options,
      allKeys: this.allKeys
    };
  },
  computed: {},
  data() {
    let options = {
      expandKeys: [],
      maxLevel: this.maxLevel,
      hasTitleSot: this.$scopedSlots.title != undefined,
      selectedKeys: [],
      selectMode: this.selectMode,
      afterSelected: this.afterSelected,
      matchWord: undefined
    };
    return {
      wKey: 'draggable-tree-list' + Math.ceil(Math.random() * 10000000000),
      options,
      vList: this.value,
      allKeys: []
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.$emit('mounted');
    if (this.defaultExpandAll) {
      this.expandAll();
    }
  },

  methods: {
    refresh() {
      this.vList = JSON.parse(JSON.stringify(this.value));
      this.wKey = 'draggable-tree-list' + Math.ceil(Math.random() * 10000000000);
    },
    onRemove() {
      this.$emit('remove', arguments);
    },
    onAdd() {
      this.$emit('add', arguments);
    },
    onEdit() {
      this.$emit('edit', arguments);
    },
    selectAll() {
      this.setSelectedKeys(this.allKeys);
    },
    getSelectedKeys() {
      return this.options.selectedKeys;
    },
    getSelectedItems() {
      let items = [];
      let cascade = list => {
        if (list && list.length) {
          for (let d of list) {
            if (this.options.selectedKeys.includes(this.getKeys(d))) {
              items.push(d);
            }
            cascade.call(this, d[this.childrenField]);
          }
        }
      };
      cascade.call(this, this.vList);
      return items;
    },
    setSelectedKeys(keys) {
      this.options.selectedKeys.splice(0, this.options.selectedKeys.length);
      this.options.selectedKeys.push(...keys);
    },
    expandAll(e) {
      this.options.expandKeys.splice(0, this.options.expandKeys.length);
      if (e === true || e === undefined) {
        this.options.expandKeys.push(...this.allKeys);
      }
    },

    search(v) {
      this.options.matchWord = v;
      if (v == undefined || v == '') {
        this.clearSearch();
        return;
      }
      if (v) {
        this.options.expandKeys.splice(0, this.options.expandKeys.length);
        for (let i = 0; i < this.$refs.listItem.length; i++) {
          this.$refs.listItem[i].search(v);
        }
      }
    },
    clearSearch() {
      this.options.matchWord = undefined;
    },
    getKeys(item) {
      if (typeof item == 'string') {
        return item;
      }
      return item && item[this.keyField];
    }
  },
  watch: {
    vList: {
      deep: true,
      handler(v, o) {
        this.$emit('input', v);
      }
    }
  }
};
</script>
