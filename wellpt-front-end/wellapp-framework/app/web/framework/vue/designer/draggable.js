import { generateId } from '@framework/vue/utils/util';
import _ from 'lodash';

export default {
  components: { draggable: () => import(/* webpackChunkName: "vuedraggable" */ 'vuedraggable') }, //  FIXME: webpackChunkName 无效?
  // components: { draggable: resolve => require.ensure([], () => resolve(require('vuedraggable')), 'vuedraggable') },
  inject: ['draggableConfig', 'designWidgetTypes', 'designer'],
  methods: {
    // onPasteWidget(e, into) {
    //   console.log('粘贴', arguments)
    //   if (into && Array.isArray(into) && this.designer.cutWidgetInfo.widget != undefined) {
    //     into.push(this.designer.cutWidgetInfo.widget);
    //     if (this.designer.cutWidgetInfo.widgetsOfParent) {
    //       this.designer.cutWidgetInfo.widgetsOfParent.splice(this.designer.cutWidgetInfo.index, 1);
    //     }
    //     this.designer.cutWidgetInfo.widget = undefined;
    //   }
    // },

    onDragChoose(evt) { },
    onDragUnchoose(evt) {
      evt.item.style.display = '';
    },
    onDragClone(evt) {
      // console.log('onDragClone', evt);
    },
    onDragChange(evt) { },
    onDragSort(evt) {
      // console.log('onDragSort', evt);
    },
    onDragStart(evt) {
      // console.log('onDragStart', evt);
      this.designer.dragging = true;
      this.designer.draggedWidget = evt.item._underlying_vm_;
    },
    onDragEnd(evt, afterOnEnd) {
      // console.log('onDragEnd', evt);
      this.designer.dragging = false;
      this.designer.draggedWidget = null;
      this.designer.undoOrRedo = true;
      let dragToLeftOrRight =
        evt.to.classList.contains('design-item-draggable-left') || evt.to.classList.contains('design-item-draggable-right');
      let _item = evt.item._underlying_vm_;
      if (_item && _item.line) {
        let top = evt.item.offsetTop,
          previousElementSibling = evt.item.previousElementSibling,
          nextElementSibling = evt.item.nextElementSibling;
        let dealElementWidth = (element, line) => {
          if (element) {
            let widget = element.__vue__.widget;
            if (element && widget.line == line && (dragToLeftOrRight || (!dragToLeftOrRight && element.offsetTop != top))) {
              let lines = [];
              for (let i = 0, len = element.__vue__.widgetsOfParent.length; i < len; i++) {
                let w = element.__vue__.widgetsOfParent[i];
                if (w.line == widget.line && (dragToLeftOrRight || (!dragToLeftOrRight && w.id != _item.id))) {
                  lines.push(w);
                }
              }
              if (!dragToLeftOrRight) {
                _item.configuration.style.width = '100%';
                delete _item.line;
              } else {
                delete _item.fromLine;
              }
              let _width = parseFloat(100 / lines.length).toFixed(2) + '%';
              for (let i = 0, len = lines.length; i < len; i++) {
                this.$set(lines[i].configuration.style, 'width', _width);
              }
              return true;
            }
          }
          return false;
        };
        if (!dealElementWidth(previousElementSibling, dragToLeftOrRight ? _item.fromLine : _item.line)) {
          dealElementWidth(nextElementSibling, dragToLeftOrRight ? _item.fromLine : _item.line);
        }
      }
      if (typeof afterOnEnd === 'function') {
        afterOnEnd(evt, _item);
      }
    },
    onDragMove(evt) {
      // console.log('onDragMove', evt)
      let relatedContext = evt.relatedContext,
        list = relatedContext.list,
        index = relatedContext.index; // 目标停靠对象的下标
      // console.log('onDragMove', evt, JSON.parse(JSON.stringify(list)), relatedContext.index);
      let prev = evt.willInsertAfter ? index : index - 1,
        next = evt.willInsertAfter ? index + 1 : index;
      if (prev >= 0 && next <= list.length - 1 && list[prev].line != undefined && list[prev].line === list[next].line) {
        // 拖动元素不能放于行组件中间
        return false;
      }
    },

    onDragAdd(evt, widgets, parent) {
      this.designer.dragging = false;
      if (this.designer.onceDisableAutoSelected) {
        delete this.designer.onceDisableAutoSelected;
        return;
      }
      // let wgts = widgets || this.designer.widgets;
      // if (!!wgts[evt.newIndex]) {
      //   this.designer.setSelected(wgts[evt.newIndex], parent);
      // }
    },

    onDragUpdate(evt) { },

    tableDraggable(rowData, tableSelector, dragHandleClass, onEvent) {
      let _this = this;
      this.$nextTick(() => {
        import('sortablejs').then(Sortable => {
          Sortable.default.create(tableSelector, {
            handle: dragHandleClass,
            onUnchoose: (onEvent && onEvent.onUnchoose) || function () { },
            onMove: (onEvent && onEvent.onMove) || function () { },
            onEnd:
              (onEvent && onEvent.onEnd) ||
              function (e) {
                let temp = rowData.splice(e.oldIndex, 1)[0];
                rowData.splice(e.newIndex, 0, temp);
                _this.tableDraggable(rowData, tableSelector, dragHandleClass, onEvent);
                if (onEvent && onEvent.afterOnEnd) {
                  onEvent.afterOnEnd();
                }
              }
          });
        });
      });
    },

    onDraggablePaste(e) {
      console.log('粘贴', e);
      if (this.designer.selectedId == null) {
        return;
      }
      let _this = this;
      let list = e.currentTarget.__vue__.list;
      if (list == undefined) {
        return;
      }
      let txt = e.clipboardData.getData('text');
      if (txt) {
        try {
          let wgt = JSON.parse(txt),
            ids = [];
          if (!this.designWidgetTypes.has(wgt.wtype)) {
            return;
          }

          if (wgt.wtype != undefined && wgt.id && wgt.configuration) {
            // 找到组件ID ，进行替换
            let findIds = w => {
              if (typeof w == 'object' && Object.prototype.toString.call(w).toLowerCase() == '[object object]' && !w.length) {
                if (w.id && w.wtype && w.configuration) {
                  ids.push(w.id);
                  // 遍历对象配置
                  for (let field in w.configuration) {
                    findIds(w.configuration[field]);
                  }
                }
              } else if (Array.isArray(w)) {
                for (let i = 0, len = w.length; i < len; i++) {
                  findIds(w[i]);
                }
              }
            };
            findIds(wgt);
            // 替换掉ID
            for (let i = 0, len = ids.length; i < len; i++) {
              txt = txt.replaceAll(ids[i], generateId());
            }
            wgt = JSON.parse(txt);
            // 如果是锁定状态下组件，去除锁定的column属性
            if (wgt.hasOwnProperty('column')) {
              delete wgt.column;
            }
            _this.designer.undoOrRedo = true;
            list.push(wgt);
            e.clipboardData.clearData();
          }
        } catch (error) {
          console.log(error);
        }
      }

      e.stopPropagation();
    }
  }
};
