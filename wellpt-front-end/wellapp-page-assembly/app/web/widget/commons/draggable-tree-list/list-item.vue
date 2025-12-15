<template>
  <a-row type="flex" :level="level" :data-key="getKeys(item)" :class="['tree-row', itemClass ? getItemClass() : '']">
    <a-col
      flex="100%"
      :class="['tree-row-item-col', selected && hideCheckbox ? 'selected' : undefined]"
      :style="colStyle"
      @click.native.stop="onClickColItem(item)"
    >
      <div>
        <span
          v-show="draggable && dragButton && dragButtonPosition == 'start'"
          :class="['drag-button-' + level, 'ant-btn ant-btn-sm icon-only', 'ant-btn-' + dragButtonType || 'link']"
          @click.stop="() => {}"
          title="拖动排序"
          style="padding-right: 3px; cursor: move"
        >
          <Icon type="pticon iconfont icon-ptkj-tuodong" />
        </span>
        <div @click="onClickLabel" :class="!selected && matched ? 'matched' : ''">
          <template v-if="expandIcon == 'folder'">
            <img
              v-if="item && item[childrenField] && item[childrenField].length > 0"
              class="svg-iconfont"
              :title="expanded ? '收起' : '展开'"
              :src="expanded ? '/static/svg/folder-open.svg' : '/static/svg/folder-close.svg'"
              @click.native.stop="onClickColItem(item)"
            />
            <template v-else-if="showLeaveIcon">
              <Icon :type="item.icon || 'file'"></Icon>
            </template>
          </template>
          <template v-else-if="expandIcon == 'plus'">
            <a-icon
              :title="expanded ? '收起' : '展开'"
              :type="expanded ? 'minus-square' : 'plus-square'"
              v-if="item && item[childrenField] && item[childrenField].length > 0"
              @click.native.stop="onClickColItem(item)"
            />
            <div v-else style="width: 14px; height: 14px"></div>
          </template>
          <a-checkbox :checked="selected" v-if="!hideCheckbox" size="small" @change="onChecked" />
          <label :style="{ maxWidth: vLabelMaxWidth }" :title="item && item[titleField]">
            <template v-if="options.hasTitleSot">
              <slot name="title" :item="item" :index="index" :level="level" :items="items"></slot>
            </template>
            <template v-else>
              {{ item && item[titleField] }}
            </template>
          </label>
        </div>
        <div :class="['buttons', hoverShowButtons ? 'hover-show' : '']" @click.stop="() => {}">
          <slot name="buttons" :item="item" :index="index" :level="level" :items="items"></slot>

          <!-- <a-button size="small" type="link" icon="plus" @click.stop="addChild(item)" v-show="level < maxLevel" />
          <a-button size="small" type="link" icon="edit" @click.stop="editItem(item)" />
          <a-button size="small" type="link" icon="delete" @click.stop="remove(item, index)" /> -->
          <!-- <a-button
            v-show="draggable && dragButton && dragButtonPosition == 'end'"
            size="small"
            :class="'drag-button-' + level"
            type="link"
            icon="menu"
            @click.stop="() => {}"
          /> -->

          <span
            v-show="draggable && dragButton && dragButtonPosition == 'end'"
            :class="['drag-button-' + level, 'ant-btn ant-btn-sm icon-only', 'ant-btn-' + dragButtonType || 'link']"
            @click.stop="() => {}"
            title="拖动排序"
            style="cursor: move"
          >
            <Icon type="pticon iconfont icon-ptkj-tuodong" />
          </span>
        </div>
      </div>
    </a-col>
    <a-col
      v-if="item && item[childrenField] != undefined"
      flex="100%"
      :data-parent-key="getKeys(item)"
      v-show="expanded || (item && item[childrenField].length == 0)"
    >
      <vuedraggable
        v-model="item && item[childrenField]"
        :group="{ name: 'tree', pull: draggable, put: canDragPut }"
        animation="300"
        :handle="dragButton ? '.drag-button-' + (level + 1) : undefined"
        :data-level="level + 1"
        :data-parent-key="getKeys(item)"
        v-if="draggable"
      >
        <ListItem
          v-for="(subItem, i) in item && item[childrenField]"
          :key="'subListItem_' + getKeys(subItem)"
          :parent="item"
          :item="subItem"
          :childrenField="childrenField"
          :titleField="titleField"
          :keyField="keyField"
          :checkedField="checkedField"
          :noCheckField="noCheckField"
          :index="i"
          :items="item && item[childrenField]"
          :level="level + 1"
          :dragButton="dragButton"
          :noCheckbox="noCheckbox"
          :draggable="draggable"
          :expandIcon="expandIcon"
          :indentSize="indentSize"
          :titleWidth="titleWidth"
          :hoverShowButtons="hoverShowButtons"
          :dragButtonPosition="dragButtonPosition"
          :dragButtonType="dragButtonType"
          @add="onAdd"
          @remove="onRemove"
          @edit="onEdit"
          @sub-search-get="subSearchGet"
          ref="listItem"
        >
          <template slot="buttons" slot-scope="scope">
            <slot name="buttons" :item="scope.item" :index="scope.index" :level="scope.level" :items="scope.items"></slot>
          </template>
          <template slot="title" slot-scope="scope">
            <slot name="title" :item="scope.item" :index="scope.index" :level="scope.level" :items="scope.items"></slot>
          </template>
        </ListItem>
      </vuedraggable>
      <template v-else>
        <ListItem
          v-for="(subItem, i) in item && item[childrenField]"
          :key="'subListItem_' + getKeys(subItem)"
          :parent="item"
          :item="subItem"
          :childrenField="childrenField"
          :titleField="titleField"
          :checkedField="checkedField"
          :noCheckField="noCheckField"
          :noCheckbox="noCheckbox"
          :keyField="keyField"
          :index="i"
          :items="item && item[childrenField]"
          :level="level + 1"
          :dragButton="dragButton"
          :draggable="draggable"
          :expandIcon="expandIcon"
          :indentSize="indentSize"
          :titleWidth="titleWidth"
          :hoverShowButtons="hoverShowButtons"
          :dragButtonPosition="dragButtonPosition"
          :dragButtonType="dragButtonType"
          @add="onAdd"
          @remove="onRemove"
          @edit="onEdit"
          @sub-search-get="subSearchGet"
          ref="listItem"
        >
          <template slot="buttons" slot-scope="scope">
            <slot name="buttons" :item="scope.item" :index="scope.index" :level="scope.level" :items="scope.items"></slot>
          </template>
          <template slot="title" slot-scope="scope">
            <slot name="title" :item="scope.item" :index="scope.index" :level="scope.level" :items="scope.items"></slot>
          </template>
        </ListItem>
      </template>
    </a-col>
  </a-row>
</template>
<style lang="less"></style>
<script type="text/babel">
import ListItem from './list-item.vue';

export default {
  name: 'ListItem',
  inject: ['options', 'allKeys'],
  props: {
    item: Object | String,
    parent: Object,
    items: Array,
    index: Number,
    dragButton: Boolean,
    noCheckbox: Boolean,
    dragButtonPosition: String,
    dragButtonType: String,
    draggable: Boolean,
    childrenField: String,
    keyField: String,
    titleField: String,
    checkedField: String,
    noCheckField: String,
    level: Number,
    showLeaveIcon: Boolean,
    expandIcon: String,
    indentSize: Number,
    titleWidth: Number,
    hoverShowButtons: Boolean,
    itemClass: String | Function
  },
  components: { ListItem, vuedraggable: () => import(/* webpackChunkName: "vuedraggable" */ 'vuedraggable') },
  computed: {
    hideCheckbox() {
      return (
        this.noCheckbox === true ||
        (this.options.selectMode != 'multiple' && this.options.selectMode != 'single') ||
        (this.item && this.item[this.noCheckField] === true)
      );
    },
    expanded() {
      return this.options.expandKeys.indexOf(this.getKeys(this.item)) != -1;
    },
    selected() {
      return this.options.selectedKeys.indexOf(this.getKeys(this.item)) != -1;
    },
    matched() {
      return this.item && this.item[this.titleField] && this.item[this.titleField].indexOf(this.options.matchWord) != -1;
    },
    vLabelMaxWidth() {
      let level = this.level;
      return this.titleWidth - (this.level - 1) * 20 + 'px';
    },
    colStyle() {
      return {
        paddingLeft: this.indentSize * (this.level - 1) + 5 + 'px'
        // paddingRight: '5px'
      };
    }
  },
  data() {
    return { titleSlot: this.$scopedSlots.title != undefined };
  },
  beforeCreate() {},
  created() {
    if (typeof this.item == 'object' && this.item[this.childrenField] == undefined) {
      this.$set(this.item, this.childrenField, []);
    }
    this.allKeys.push(this.getKeys(this.item));
    if (this.options.selectMode == 'multiple') {
      if (this.item && this.item[this.checkedField] === true) {
        this.options.selectedKeys.push(this.getKeys(this.item));
      }
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    getKeys(item) {
      if (typeof item == 'string') {
        return item;
      }
      return item && item[this.keyField];
    },
    onChecked(e) {
      let checked = e.target.checked;
      let index = this.options.selectedKeys.indexOf(this.getKeys(this.item));
      if (checked && index == -1) {
        this.options.selectedKeys.push(this.getKeys(this.item));
      }
      if (!checked && index != -1) {
        this.options.selectedKeys.splice(index, 1);
      }
      this.$emit('checkedItem', {
        checked,
        item: this.item
      });
    },
    subSearchGet() {
      let idx = this.options.expandKeys.indexOf(this.getKeys(this.item));
      if (idx == -1) {
        this.options.expandKeys.push(this.getKeys(this.item));
      }
      this.$emit('sub-search-get', true);
    },
    search(v) {
      if (this.matched) {
        // 匹配到该级符合，展开上级
        this.$emit('sub-search-get', true);
      }
      if (this.$refs.listItem) {
        for (let i = 0; i < this.$refs.listItem.length; i++) {
          // 搜索下级
          this.$refs.listItem[i].search(v);
        }
      }
    },
    onClickLabel(e) {
      if (this.options.selectMode == 'multiple' || this.options.selectMode == 'single') {
        if (this.options.selectMode == 'single') {
          this.options.selectedKeys.splice(0, this.options.selectedKeys.length);
          this.options.selectedKeys.push(this.getKeys(this.item));
        } else {
          let index = this.options.selectedKeys.indexOf(this.getKeys(this.item));
          if (index == -1) {
            this.options.selectedKeys.push(this.getKeys(this.item));
          } else {
            this.options.selectedKeys.splice(index, 1);
          }
        }
        if (typeof this.options.afterSelected == 'function') {
          this.options.afterSelected(this.item, this.options.selectedKeys);
        }
        e.stopPropagation();
      }
    },
    canDragPut(putOnDraggable, pullOff, dragItem) {
      // console.log(arguments);
      let draggedMenu = dragItem._underlying_vm_;
      // 判断拖入的元素是否满足拖入后导航层级不超过3级
      let putOnLevel = parseInt(putOnDraggable.el.dataset.level);
      if (putOnLevel > this.options.maxLevel) {
        // 拖入的等级已经是最大级，则不允许拖入了
        return false;
      }

      // 从拖入级向下判断是否层级会超过最大限制层级
      try {
        let maxLevel = this.options.maxLevel;
        let calcLevel = (item, level) => {
          if (putOnLevel + level > maxLevel) {
            throw new Error('limit max level');
          }
          if (item && item[this.childrenField] && item[this.childrenField].length) {
            for (let i = 0, len = item[this.childrenField].length; i < len; i++) {
              calcLevel(item[this.childrenField][i], level + 1);
            }
          }
        };
        calcLevel(draggedMenu, 0);
      } catch (error) {
        console.warn('层级将超过最大层级限制, 无法拖入');
        // console.error(error);
        return false;
      }
      this.options.expandKeys.push(putOnDraggable.el.dataset.parentKey);
      return true;
    },
    onEdit() {
      this.$emit('edit', arguments);
    },
    onRemove() {
      this.$emit('remove', arguments);
    },
    onAdd() {
      this.$emit('add', arguments);
    },
    addChild(item) {
      this.$emit('add', item);
    },
    editItem() {
      this.$emit('edit', arguments);
    },
    remove(item, index) {
      this.items.splice(index, 1);
      this.$emit('remove', arguments);
    },
    onClickColItem(item) {
      let idx = this.options.expandKeys.indexOf(this.getKeys(item));
      if (idx == -1) {
        this.options.expandKeys.push(this.getKeys(item));
      } else {
        this.options.expandKeys.splice(idx, 1);
      }
    },
    getItemClass() {
      if (this.itemClass) {
        if (typeof this.itemClass == 'function') {
          return this.itemClass({
            item: this.item,
            children: this.items,
            parent: this.parent,
            index: this.index
          });
        } else {
          return this.itemClass;
        }
      }
      return undefined;
    }
  }
};
</script>
