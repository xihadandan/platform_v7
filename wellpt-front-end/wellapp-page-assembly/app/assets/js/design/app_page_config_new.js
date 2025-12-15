define([
  'jquery',
  'jquery-ui',
  'bootstrap',
  'server',
  'commons',
  'appContext',
  'appWindowManager',
  'appModal',
  'AppPageDesigner',
  'vue',
  'velocity',
  'appPageVueComponents',
  'sortable',
  'vuedraggable',
  'jsonview',
], function ($, ui, bootstrap, server, commons, appContext, appWindowManager, appModal, AppPageDesigner, Vue, Velocity, appPageVueComponents) {
  // 页面引用资源的视图组件ID
  var pageResourceListWidgetDefId = null;
  var UUID = commons.UUID;
  var Browser = commons.Browser;
  var StringUtils = commons.StringUtils;

  // 1、创建页面设计器对象并初始化
  var pageDesigner = new AppPageDesigner({
    onCreate: function () {
      var _self = this;
      if (!_self.initDefinitionJson) {
        _self.pageDesignVue.designerAreaData = {
          wtype: "wPage",
          uuid: _self.appPageDefinition.uuid,
          id: _self.appPageDefinition.id,
          title: _self.appPageDefinition.name,
          items: [],
          level: 0,
          icon: 'icon-ptkj-yemian',
          open: true,
          isParent: true,
          noDrag: true,
        }
      } else {
        _self.pageDesignVue.designerAreaData = _self.initDefinitionJson;
      }
    }
  });
  Vue.component('draggable', window.vuedraggable);
  var bus = new Vue();
  Vue.component('treeNode', {
    mixins: [appPageVueComponents.selectWidgetMixin],
    props: ['nodeData', 'curTreeNode', 'editTreeNode'],
    watch: {
      curTreeNode: function (newVal, oldVal) {
        this.curNode = newVal;
      },
      editTreeNode: function (newVal, oldVal) {
        this.curEditNode = newVal;
      }
    },
    data: function () {
      return {
        root: null,
        parentNodeData: {},
        curNode: this.curNode,
        curEditNode: this.editTreeNode,
        dragOverClass: '',
        dragClasses: ''
      }
    },
    created: function () {
      var parent = this.$parent;
      if (parent.isRoot) {
        this.root = parent;
      } else {
        this.root = parent.root;
      }
      this.parentNodeData = parent.nodeData;
      this.init();
    },
    mounted: function () {
      console.log(this.nodeData.title)
      if (!this.nodeData.title && this.nodeData.name) {
        console.log(this.nodeData.name);
      }
      //绑定拖拽事件
      this.$refs.draggAbleDom.draggable = !this.nodeData.noDrag;
      this.$refs.draggAbleDom.ondragstart = this.onDragStart;
      this.$refs.draggAbleDom.ondragend = this.onDragEnd;

      this.$refs.dropTarget.ondragenter = this.onDragEnter;
      this.$refs.dropTarget.ondragover = this.onDragOver;
      this.$refs.dropTarget.ondragleave = this.onDragLeave;
      this.$refs.dropTarget.ondrop = this.onDrop;
    },
    methods: {
      treeOpen: function (node) {
        if (node.level === 0 || ((!node.items || (node.items && !node.items.length)) && !node.colData && (node.configuration && node.configuration.tabs && !node.configuration.tabs.length))) return;
        node.open = !node.open;
      },
      nodeClick: function (node) {
        if (node.level === 0) {
          return;
        }
        if (this.curNode !== node.id) {
          if (node.type !== 'unit') {
            this.setSelectWidget(node);
          }
          bus.$emit('nodeClick', node.id);
          return;
        }
        bus.$emit('nodeEdit', node.id);
      },
      init() {
        this.nodeData._hash = this.generateHash();
        this.setInitNodeValue();
      },
      setInitNodeValue() {
        this.setPropValue("open", this.nodeData, true);
        this.setPropValue("noDrag", this.nodeData, false);
        this.setPropValue("noDrop", this.nodeData, false);
      },
      //设置默认值
      setPropValue(prop, Obj, initValue) {
        if (!(prop in Obj)) {
          this.$set(Obj, prop, initValue);
        }
      },
      setDragOverClass() {
        var pos = this.root.dragOverStatus.dropPosition;
        if (this.root.dragOverStatus.overNodeKey !== this.nodeData._hash) {
          return
        }
        var disabled = false;
        if (this.nodeData.type === 'unit') {
          disabled = true;
        }
        if (pos === 0) {
          return "tree-drag-over";
        } else if (pos === -1) {
          return "tree-drag-over-top" + (disabled ? ' tree-drop-disabled' : '');
        } else if (pos === 1) {
          return "tree-drag-over-bottom" + (disabled ? ' tree-drop-disabled' : '');
        }
        return "";
      },
      //拖拽开始
      onDragStart(e) {
        e.stopPropagation();
        if (this.nodeData.noDrag) {
          return;
        }
        this.dragNodeHighlight = true;
        e.dataTransfer.effectAllowed = "move";
        this.nodeData.open = false;
        this.root.onDragStart(e, this);
      },

      //进入目标节点
      onDragEnter(e) {
        e.preventDefault();
        e.stopPropagation();
        this.root.onDragEnter(e, this);
      },

      onDragOver(e) {
        e.preventDefault();
        e.stopPropagation();
        this.root.onDragOver(e, this);
        this.dragOverClass = this.setDragOverClass();
      },

      onDragLeave(e) {
        e.stopPropagation();
        this.dragOverClass = "";
        this.root.onDragLeave(e, this);
      },

      onDrop(e) {
        e.preventDefault();
        e.stopPropagation();
        this.dragOverClass = "";
        //当异步加载子节点时不允许放置
        if (this.showLoading) {
          return;
        }
        var pos = this.root.dragOverStatus.dropPosition;
        if (this.nodeData.type === 'unit' && pos !== 0) {
          return;
        }
        this.root.onDrop(e, this);
      },

      onDragEnd(e) {
        e.stopPropagation();
        e.preventDefault();
        this.dragNodeHighlight = false;
        this.root.onDragEnd(e, this);
      },

      generateHash() {
        var num = 10;
        var collectStr = "abcdefghijklmnopqrstuvwxyz0123456789",
          i = 0,
          str = "";
        while (i < num) {
          str += collectStr[Math.round(Math.random() * 35)];
          i++;
        }
        return str;
      }
    },
    computed: {
      showSwitch: function () {
        if (((this.nodeData.items && this.nodeData.items.length) ||
          (this.nodeData.configuration && this.nodeData.configuration.tabs) ||
          this.nodeData.colData)
          && this.nodeData.level !== 0) {
          return true;
        }
        return false;
      },
      switchClass: function () {
        var _class = '';
        if (this.nodeData.open) {
          _class += 'open';
        }
        if (!((this.nodeData.items && this.nodeData.items.length) ||
          (this.nodeData.configuration && this.nodeData.configuration.tabs) ||
          (this.nodeData.colData && this.nodeData.colData.length))) {
            _class += ' disabled';
        }
        return _class;
      },
      nodeTitle: function () {
        return this.nodeData.title || this.nodeData.name;
      },
      treeIcon: function () {
        if (typeof this.nodeData.icon === 'string' && this.nodeData.icon) {
          return this.nodeData.icon;
        }
        return 'icon-ptkj-zujian';
      }
    },
    template: `<li ref="dropTarget">
                <div class="tree-node-row flex" ref="draggAbleDom" :class="[dragClasses,dragOverClass,curNode === nodeData.id ? 'active' : '']" :style="{'padding-left': ((nodeData.level - 1) * 20 + 18) + 'px'}">
                  <div class="tree-node-switch" :class="switchClass" @click="treeOpen(nodeData)">
                    <i class="iconfont icon-ptkj-shixinjiantou-you" v-if="showSwitch"></i>
                  </div>
                  <div class="icon">
                    <i class="iconfont" :class="treeIcon"></i>
                  </div>
                  <div class="tree-node-name" @click="nodeClick(nodeData)">
                    <input v-if="curEditNode === nodeData.id" class="form-control" v-model="nodeData.title">
                    <span v-else>{{nodeTitle}}</span>
                  </div>
                </div>
                <transition>
                  <ul class="tree-node-child" v-show="nodeData.open">
                    <template v-if="nodeData.colData">
                      <tree-node :node-data="item" :cur-tree-node="curNode" :edit-tree-node="curEditNode" :key="item.id" v-for="(item,index) in nodeData.colData"></tree-node>
                    </template>
                    <template v-else-if="nodeData.configuration && nodeData.configuration.tabs">
                      <tree-node :node-data="item" :cur-tree-node="curNode" :edit-tree-node="curEditNode" :key="item.id" v-for="(item,index) in nodeData.configuration.tabs"></tree-node>
                    </template>
                    <template v-else-if="nodeData.configuration && nodeData.configuration.body && nodeData.configuration.body.tabs">
                      <tree-node :node-data="item" :cur-tree-node="curNode" :edit-tree-node="curEditNode" :key="item.id" v-for="(item,index) in nodeData.configuration.body.tabs"></tree-node>
                    </template>
                    <template v-else-if="nodeData.items">
                      <tree-node :node-data="item" :cur-tree-node="curNode" :edit-tree-node="curEditNode" :key="item.id" v-for="(item,index) in nodeData.items"></tree-node>
                    </template>
                  </ul>
                </transition>
              </li>`
  });
  pageDesigner.pageDesignVue = new Vue({
    el: '#newPageDesigner',
    mixins: [appPageVueComponents.designerMixin, appPageVueComponents.updateWidgetData],
    delimiters: ['${', '}'], // {{}}跟nj模版冲突,替换为${}
    data: {
      appPageDesigner: pageDesigner,
      json_viewer_mode: pageDesigner.options.isJsonViewer,
      isRoot: true,
      designerWidth: window.innerWidth + 'px',
      sidebarInfo: {
        layout: {
          grid: {
            list: []
          },
          specific: {
            list: []
          }
        },
        component: {
          techComponent: {
            unit: {
              list: []
            },
            report: {
              list: []
            }
          }
        }
      },
      curSidebarType: '',
      leftPopupFixed: false,
      expandItems: ['layout', 'techComponent', 'businessComponent', 'dataComponent'],
      editWidgetNameStatus: true,
      defaultMargin: 20,
      defaultPadding: 20,
      curTreeNode: null,
      curEditTreeNode: null,
      dragOverStatus: {}
    },
    watch: {

    },

    mounted: function () {
      var that = this;
      bus.$on('nodeClick', that.treeNodeClick);
      bus.$on('nodeEdit', that.treeNodeEdit);
    },
    methods: {
      //保存
      save: function () { },
      //保存新版本
      saveNew: function () { },
      //预览
      preview: function () { },
      //查看引用资源
      view_ref_resource: function () { },
      //打开json查看器
      change_json_viewer_mode: function () {
        this.json_viewer_mode = !this.json_viewer_mode;
      },
      //选择左侧类型
      chooseSidebarType: function (type) {
        this.curSidebarType = type;
      },
      //变更左侧弹窗钉住状态
      changeLeftPopupFixed: function () {
        this.leftPopupFixed = !this.leftPopupFixed;
      },
      //左侧菜单移入
      mouseenter: function (e) {
        var that = this;
        var toElement = e.toElement;
        if (that.leftPopupFixed) return;
        that.curSidebarType = toElement.dataset.type;
      },
      //左侧菜单移出
      mouseleave: function (e) {
        var that = this;
        var toElement = e.toElement;
        if (that.leftPopupFixed) return;
        var $leftSidebarPopup = $(toElement).closest('.left-sidebar-popup');
        if (!$leftSidebarPopup.length) {
          that.curSidebarType = '';
        }
      },
      //变更左侧组件/布局 收起/展开状态
      changeExpandStatus: function (type) {
        var that = this;
        var index = that.expandItems.indexOf(type);
        if (index > -1) {
          that.expandItems.splice(index, 1);
        } else {
          that.expandItems.push(type);
        }
      },
      //左侧组件/布局 展开
      expand: function (el, done) {
        $(el).velocity('stop');
        Velocity(el, { height: el.dataset.height + 'px' }, { duration: 300, complete: done })
      },
      //左侧组件/布局 收起
      collapse: function (el, done) {
        var elHeight = el.offsetHeight;
        if (!el.dataset.height) {
          el.dataset.height = elHeight;
        }
        el.style.height = elHeight + 'px';
        $(el).velocity('stop');
        Velocity(el, { height: 0 }, { duration: 300, complete: done })
      },
      editWidgetTitle: function () {
        var that = this;
        that.editWidgetNameStatus = true;
        that.$nextTick(function () {
          that.$refs.widgetName.focus();
        })
      },
      resetMargin: function (item) {
        item.margin.top = item.margin.bottom = item.margin.left = item.margin.right = this.defaultMargin;
      },
      resetPadding: function (item) {
        item.padding.top = item.padding.bottom = item.padding.left = item.padding.right = this.defaultPadding;
      },
      changeMarginSame: function (item) {
        item.margin.isSame = !item.margin.isSame;
      },
      changePaddingSame: function (item) {
        item.padding.isSame = !item.padding.isSame;
      },
      changeUnitExpand: function (item) {
        item.expand = !item.expand;
      },
      plus: function (e, item) {
        $(e.target).closest('.count').prev().focus();
        if (item.show) {
          this.stopBlur = true;
        }
        ++item.value;
      },
      minus: function (e, item) {
        if (item.value <= 0) {
          return;
        };
        --item.value;
      },
      directionFocus: function (direction) {
        var that = this;
        that.showMargin = true;
        direction.show = true;
      },
      directionBlur: function (e, direction) {
        var that = this;
        setTimeout(() => {
          if (that.stopBlur) {
            that.stopBlur = false;
            return;
          }
          that.showMargin = false;
          direction.show = false;
        }, 300);
      },
      channelInputLimit(e, otherKey) {
        var key = e.key;
        // 默认不允许输入'e'、'-'、'+'
        var defaultKey = ['e', '+', '-'];
        if (otherKey) {
          defaultKey = defaultKey.concat(otherKey);
        }
        if (defaultKey.indexOf(key) > -1) {
          e.returnValue = false
          return false
        }
        return true
      },
      channelInputLimitIncludeDot: function (e) {
        return this.channelInputLimit(e, ['.']);
      },
      treeNodeClick: function (treeNodeId) {
        this.curTreeNode = treeNodeId;
        if (this.curEditTreeNode && this.curEditTreeNode !== this.curTreeNode) {
          this.curEditTreeNode = null;
        }
      },
      treeNodeEdit: function (treeNodeId) {
        this.curEditTreeNode = treeNodeId;
      },
      getDropData(info) {
        var dragData = info.dragNode.nodeData;
        var dragParent = info.dragNode.parentNode;
        var dropData = info.dropNode.nodeData;
        var dropParent = info.dropNode.parentNode;
        var dropPosition = info.dropPosition; //0作为子级，-1放在目标节点前面，1放在目标节点后面

        //把拖拽元素从父节点中删除
        dragParent.items.splice(dragParent.items.indexOf(dragData), 1);
        if (!dragParent.items.length) {
          dragParent.isParent = false;
          dragParent.open = false;
        }
        if (dropPosition === 0) {
          if (!dropData.items) {
            dropData.items = [];
          }
          dragData.level = dropData.level + 1;
          dropData.isParent = true;
          dropData.open = true;
          dropData.items.push(dragData);
        } else {
          var index = dropParent.items.indexOf(dropData);
          if (dropPosition === -1) {
            dragData.level = dropParent.level + 1;
            dropParent.items.splice(index, 0, dragData);
          } else {
            dragData.level = dropParent.level + 1;
            dropParent.items.splice(index + 1, 0, dragData);
          }
        }
      },
      //是否有拖拽节点
      hasDragNode() {
        return this.dragOverStatus.dragNode && this.dragOverStatus.dragNode.nodeData._hash;
      },
      //获取元素到文档顶部和左边的距离
      getOffset(ele) {
        if (!ele.getClientRects().length) {
          return { top: 0, left: 0 };
        }
        var rect = ele.getBoundingClientRect();
        if (rect.width || rect.height) {
          var doc = ele.ownerDocument;
          var win = doc.defaultView;
          var docElem = doc.documentElement;
          return {
            //元素距离视窗顶部距离，滚动高度，元素边框厚度
            top: rect.top + win.pageYOffset - docElem.clientTop,
            left: rect.left + win.pageXOffset - docElem.clientLeft
          };
        }
        return rect;
      },
      //计算拖拽节点的放置方式0（作为目标节点的子节点），-1（放置在目标节点的前面）,1（放置在目标节点的后面）
      calDropPosition(e) {
        var offsetTop = this.getOffset(e.target).top;
        var offsetHeight = e.target.offsetHeight;
        var pageY = e.pageY;
        var gapHeight = 0.2 * offsetHeight;
        if (pageY > offsetTop + offsetHeight - gapHeight) {
          //放在目标节点后面-同级
          return 1;
        }
        if (pageY < offsetTop + gapHeight) {
          //放在目标节点前面-同级
          return -1;
        }
        //放在目标节点里面-作为子节点
        return 0;
      },
      onDragStart(e, treeNode) {
        this.dragOverStatus.dragNode = {
          nodeData: treeNode.nodeData,
          parentNode: treeNode.parentNodeData
        };
      },
      onDragEnter(e, treeNode) {
        var that = this;
        //当没有设置拖拽节点时，禁止作为目标节点
        if (!that.hasDragNode()) {
          return;
        }
        that.dragOverStatus.overNodeKey = "";
        //拖拽节点与目标节点是同一个，return掉
        if (treeNode.nodeData._hash === that.dragOverStatus.dragNode.nodeData._hash) {
          return;
        }
        that.dragOverStatus.overNodeKey = treeNode.nodeData._hash; //当前经过的可放置的节点的key
        //当前节点禁止做为放置节点时
        if (treeNode.nodeData.noDrop) {
          return;
        }
        //设置dragEnter定时器，停留250毫秒后触发事件
        if (!that.delayedDragEnterLogic) {
          that.delayedDragEnterLogic = {};
        }
        Object.keys(that.delayedDragEnterLogic).forEach(function (key) {
          clearTimeout(that.delayedDragEnterLogic[key]);
        });
        that.delayedDragEnterLogic[
          treeNode.nodeData._hash
        ] = setTimeout(function () {
          if (!treeNode.nodeData.open) {
            treeNode.nodeData.open = true;
          }
        }, 250);
      },

      onDragOver(e, treeNode) {
        //当没有设置拖拽节点时，禁止作为目标节点
        if (!this.hasDragNode()) {
          return;
        }

        if (this.dragOverStatus.overNodeKey === treeNode.nodeData._hash) {
          this.dragOverStatus.dropPosition = this.calDropPosition(e); //放置标识0，-1,1
        }
      },

      onDragLeave(e, treeNode) {
        //当没有设置拖拽节点时，禁止作为目标节点
        if (!this.hasDragNode()) {
          return;
        }
      },

      onDrop(e, treeNode) {
        //当没有设置拖拽节点时，禁止作为目标节点
        if (!this.hasDragNode()) {
          return;
        }
        //当前节点禁止拖拽时
        if (treeNode.nodeData.noDrop) {
          return;
        }
        //拖拽节点与目标节点是同一个，不做任何操作
        if (this.dragOverStatus.dragNode.nodeData._hash === treeNode.nodeData._hash) {
          return;
        }
        //没有父节点
        if (!treeNode.parentNodeData) {
          return;
        }

        var res = {
          event: e,
          dragNode: this.dragOverStatus.dragNode,
          dropNode: {
            nodeData: treeNode.nodeData,
            parentNode: treeNode.parentNodeData
          },
          dropPosition: this.dragOverStatus.dropPosition
        };
        this.getDropData(res)
      },

      onDragEnd(e, treeNode) {
        //当没有设置拖拽节点时，禁止作为目标节点
        if (!this.hasDragNode()) {
          return;
        }
        //当前节点禁止拖拽时
        if (treeNode.nodeData.noDrop) {
          return true;
        }
        this.dragOverStatus.dragNode = null;
        this.dragOverStatus.overNodeKey = "";

        this.$emit("on-dragEnd", {
          treeNode: treeNode.nodeData,
          parentNode: treeNode.parentNodeData,
          event: e
        });
      }
    }
  });
  window.appPageDesigner = pageDesigner;
  return appPageDesigner;
});
