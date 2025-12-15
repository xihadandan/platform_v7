import { debounce, kebabCase } from 'lodash';
import md5 from '@framework/vue/utils/md5';

export default {
  inject: ['pageContext', 'designItemContext'],
  props: {
    widget: Object, // 组件定义
    widgetsOfParent: Array, // 父组件的所有子组件
    children: Array,
    designer: Object, // 设计器实例
    parent: Object, // 父组件
    index: Number, //当前组件在父组件的子组件列表的序号
    dragGroup: {
      type: String,
      default: 'dragGroup'
    } // 当前页面可拖拽的组名，可以通过设置不同的组名实现页面多层级的允许拖拽范围
  },
  provide() {
    return {
      widgetContext: this
    };
  },
  data() {
    return {
      widgetMd5Key: this.widget.id
    };
  },

  components: {},
  computed: {
    selected() {
      return !!this.designer && this.widget.id === this.designer.selectedId;
    },

    hasParent() {
      return this.parent != null;
    },

    defaultEvents() {
      // 定义默认的组件事件
      return [];
    },
    vPadding() {
      if (this.widget.configuration.style && this.widget.configuration.style.padding) {
        let p = [];
        this.widget.configuration.style.padding.forEach(v => {
          if (v && v.toString().indexOf('var') > -1) {
            p.push(v);
          } else {
            p.push((v || 0) + 'px');
          }
        });
        return p.join(' ');
      }
      return '0px';
    },
    wtypeKebabCase() {
      return kebabCase(this.widget.wtype);
    }
  },
  beforeCreate() {},
  created() {},

  methods: {
    deleteSelf() {
      if (this.widgetsOfParent != undefined && this.index != undefined) {
        this.widgetsOfParent.splice(this.index, 1);
        this.$delete(this.designer.widgetIdMap, this.widget.id);
        this.$delete(this.designer.widgetTreeMap, this.widget.id);
      }
    },

    resolveWidgetType(widget) {
      return !!this.designer ? `E${widget.wtype}` : widget.wtype;
    },
    selectWidget(widget, parent) {
      this.designer.setSelected(widget || this.widget, parent || this.parent);
    },
    showWidgetJsonDetail() {
      this.designer.widgetJsonDrawerVisible = true;
    },

    refreshWidget: debounce(function (v) {
      this.widgetMd5Key = md5(JSON.stringify(this.widget));
      // this.wKey = this.widget.id + '_' + new Date().getTime();
    }, 500)
  },

  mounted() {
    this.$emit('mounted', this);
  },

  watch: {}
};
