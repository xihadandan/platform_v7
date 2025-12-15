import { debounce } from 'lodash';
export default {
  props: {
    dividingLine: Boolean,
    colHeight: undefined,
    collaspeType: Array
  },
  data() {
    return {
      leftCollapsed: false,
      rightCollapsed: false,
      collapseDirection: []
    };
  },
  computed: {
    // 是否显示折叠按钮，左折叠和右折叠
    isCollapse() {
      let types = this.collaspeType.filter(item => item.index == this.index);
      this.collapseDirection = [];
      if (types[0]) {
        this.collapseDirection.push(types[0].type);
      }
      if (types[1]) {
        this.collapseDirection.push(types[1].type);
      }
      return types.length > 0;
    },
    // 是否是被折叠的列
    isCollapseCol() {
      if (this.isCollapse) {
        if (this.index == 0) {
          let hasIndex = this.collaspeType.filter(item => item.type == 'left');
          if (hasIndex) {
            return true;
          }
        } else if (this.index == this.widgetsOfParent.length - 1) {
          let hasIndex = this.collaspeType.filter(item => item.type == 'right');
          if (hasIndex) {
            return true;
          }
        }
      }
      return false;
    },
    isLastCol() {
      return this.index == this.widgetsOfParent.length - 1;
    }
  },
  methods: {
    collapsedType(collapseDirection) {
      return collapseDirection == 'left' ? 'double-right' : 'double-left';
    },
    expendedType(collapseDirection) {
      return collapseDirection == 'left' ? 'double-left' : 'double-right';
    },
    changeCollapsed: debounce(function (direction) {
      this[direction + 'Collapsed'] = !this[direction + 'Collapsed'];
      this.$emit('colsChange', direction, this.leftCollapsed, this.rightCollapsed);
    }, 300)
  }
};
