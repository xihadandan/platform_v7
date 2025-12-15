define(['jquery', 'commons', 'vue', 'tabLayoutComponent', 'panelLayoutComponent', 'splitPaneComponent', 'sortable', 'vuedraggable'],
  function ($, commons, Vue, tabLayoutComponent, panelLayoutComponent) {
    var UUID = commons.UUID;
    var appPageVueComponents = {

    };
    appPageVueComponents.designerMixin = {
      data: {
        componentCounter: 1,
        designerAreaData: {
          items: []
        },
        selectWidget: {
          style: {}
        },
        curOverWidgetKey: '',
        moveEndStatus: false,
        defaultUnitStyle: {
          name: '布局单元1',
          width: 33.33,
          widthUnit: '%',
          fixedWidth: false,
          useMargin: true,
          expand: true,
          margin: {
            direction: [{
              name: '上',
              field: 'top',
              value: 20,
              show: false
            }, {
              name: '下',
              field: 'bottom',
              value: 0,
              show: false
            }, {
              name: '左',
              field: 'left',
              value: 20,
              show: false
            }, {
              name: '右',
              field: 'right',
              value: 20,
              show: false
            }],
            isSame: false,
            show: false,
            sameValue: 20,
            sameShow: false
          },
          usePadding: false,
          padding: {
            direction: [{
              name: '上',
              field: 'top',
              value: 0,
              show: false
            }, {
              name: '下',
              field: 'bottom',
              value: 0,
              show: false
            }, {
              name: '左',
              field: 'left',
              value: 0,
              show: false
            }, {
              name: '右',
              field: 'right',
              value: 0,
              show: false
            }],
            isSame: false,
            show: false,
            sameValue: 0,
            sameShow: false
          }
        },
        defaultStyle: {
          width: 100,
          widthUnit: '%',
          height: 84,
          heightUnit: 'px',
          fixedHeight: true,
          autoSetByContent: true,
          customCSS: '',
          units: []
        }
      },
      methods: {
        createComponent: function (component) {
          var that = this;
          if (!that.leftPopupFixed) {
            that.curSidebarType = '';
          }
          this.$nextTick(function () {
            var $widget = $('#' + component.id);
            if (!component.defineJs) {
              console.log('加载配置的JS模块为空.');
              return;
            }
            var wcomponent = require(component.defineJs);
            var _component = new wcomponent($widget, component);
            that.appPageDesigner[component.id] = _component;
            _component.component = component;
            _component.pageDesigner = that.appPageDesigner;
            if (component.wtype === 'wBootgrid_Custom') {
              _component.callback = function () {
                for (var i = 0; i < component.configuration.columns.length; i++) {
                  component.colData[i] = {};
                  component.colData[i].items = [];
                  component.colData[i].type = 'unit';
                  component.colData[i].title = '布局单元' + (i + 1);
                  component.colData[i].level = component.level + 1;
                  component.colData[i].noDrag = true;
                  component.colData[i].id = 'layoutUnit_' + UUID.createUUID();
                }
              }
            }
            _component.create.call(_component, $widget, component.initOptions);
          })
        },
        handleMoveStart(e) {
          console.log('start', e.oldIndex)
        },
        handleMove(e) {
          return true
        },
        handleMoveEnd: function (evt) {
          var that = this;
          that.designerAreaData.items.forEach(function (data) {
            that.loopTurnData(data);
          });
        },
        //递归转换数据
        loopTurnData: function (widget) {
          var that = this;
          if (widget.items) {
            widget.colData.forEach(function (item, index) {
              widget.colData[index].items = [];
              widget.colData[index].type = 'unit';
              widget.colData[index].title = '布局单元' + (index + 1);
              widget.colData[index].level = widget.level + 1;
              widget.colData[index].noDrag = true;
              widget.colData[index].id = 'layoutUnit_' + UUID.createUUID();
            })
            if (widget.wtype.indexOf('wBootgrid') > -1) {
              widget.items.forEach(function (item) {
                widget.colData[item.columnIndex].items.push(item);
                that.loopTurnData(item);
              })
            }
          }
        },
        handleWidgetAdd: function (e,items) {
          var that = this;
          var newIndex = e.newIndex;
          var _data = _.cloneDeep(items[newIndex]);
          //为拖拽到容器的元素添加唯一 key
          if (!_data.title) {
            var title = (that.componentCounter < 10 ? '0' + that.componentCounter : that.componentCounter) + '_' + _data.name;
            var initOptions = _.cloneDeep(WebApp.widgetDefaults[_data.id]);
            initOptions.title = title;
            var data = {
              initOptions: initOptions,
              configuration: initOptions.configuration,
              wtype: _data.id,
              title: title,
              id: _data.id + '_' + UUID.createUUID(),
              defineJs: _data.defineJs
            }
            if (_data.layout) {
              data.layout = _data.layout;
            }
            if (_data.items) {
              data.items = [];
            }
            //栅格布局
            if (data.wtype.indexOf('wBootgrid') > -1) {
              data.colData = [];
              for (var i = 0; i < data.configuration.columns.length; i++) {
                data.colData[i] = {};
                data.colData[i].items = [];
                data.colData[i].type = 'unit';
                data.colData[i].title = '布局单元' + (i + 1);
                data.colData[i].level = 2;
                data.colData[i].noDrag = true;
                data.colData[i].id = 'layoutUnit_' + UUID.createUUID();
              }
            } else if (data.wtype === 'wSecondPageLayout') { //二级页面布局
              data.colData = [];
              for (var i = 0; i < 2; i++) {
                data.colData[i] = {};
                data.colData[i].items = [];
                data.colData[i].type = 'unit';
                data.colData[i].title = '布局单元-' + (i > 0 ? '视图' : '左导航');
                data.colData[i].level = 2;
                data.colData[i].noDrag = true;
                data.colData[i].id = 'layoutUnit_' + UUID.createUUID();
              }
            } else if (data.wtype === 'wBootstrapTabs') { //标签页
              data = tabLayoutComponent.addTab(data, 0, 2, true);
            }

            _data = data;
            that.componentCounter++;
            that.createComponent(_data);
          }
          _data.level = 1;
          _data.path = [newIndex];
          that.$set(that.designerAreaData.items, newIndex, _data);
          that.updateData();
        }
      }
    }
    appPageVueComponents.updateWidgetData = {
      methods: {
        updateData: function () {
          var that = this;
          that.$root.designerAreaData.items.forEach(function (data) {
            that.loopTurnData(data);
          });
        },
        //递归转换数据
        loopTurnData: function (widget) {
          var that = this;
          if (widget.items && widget.wtype.indexOf('wBootgrid') > -1) {
            widget.colData.forEach(function (item, index) {
              item.items = [];
              // item.type = 'unit';
              // item.title = '布局单元' + (index + 1);
              item.level = widget.level + 1;
              // item.noDrag = true;
              // item.id = 'layoutUnit_' + UUID.createUUID();
            })
            widget.items.forEach(function (item) {
              widget.colData[item.columnIndex].items.push(item);
              that.loopTurnData(item);
            })
          } else {
            if (widget.wtype.indexOf('wBootgrid') > -1) {
              widget.colData.forEach(function (item) {
                item.level = widget.level + 1;
              })
            }
          }
        },
      }
    }
    appPageVueComponents.selectWidgetMixin = {
      methods: {
        setSelectWidget: function (component) {
          var that = this;
          if (!component.style) {
            var style = _.cloneDeep(that.$root.defaultStyle);
            if (component.layout) {
              var units = [];
              if (component.wtype.indexOf('wBootgrid') > -1) {
                for (var i = 0; i < component.configuration.columns.length; i++) {
                  var column = component.configuration.columns[i];
                  var unitStyle = _.cloneDeep(that.$root.defaultUnitStyle);
                  unitStyle.name = '布局单元' + (i + 1);
                  unitStyle.width = (column.colspan / 12 * 100).toFixed(2);
                  units[i] = unitStyle;
                }
              } else if (component.wtype === 'wSecondPageLayout') {
                for (var i = 0; i < 2; i++) {
                  var unitStyle = _.cloneDeep(that.$root.defaultUnitStyle);
                  unitStyle.name = '布局单元-' + (i > 0 ? '视图' : '左导航');
                  unitStyle.width = (i > 0 ? 'auto' : 230);
                  units[i] = unitStyle;
                }
              }
              if (component.level === 1) {
                if (units.length !== 1) {
                  for (var i = 0, len = units.length; i < len; i++) {
                    units[i].margin.direction[2].value = 10;
                    units[i].margin.direction[3].value = 10;
                    if (i === 0) {
                      units[i].margin.direction[2].value = 20;
                    }
                    if (i === len - 1) {
                      units[i].margin.direction[3].value = 20;
                    }
                  }
                }
              } else {
                for (var i = 0, len = units.length; i < len; i++) {
                  units[i].margin.direction[0].value = 0;
                  units[i].margin.direction[2].value = 10;
                  units[i].margin.direction[3].value = 10;
                  if (i === 0) {
                    units[i].margin.direction[2].value = 0;
                  }
                  if (i === len - 1) {
                    units[i].margin.direction[3].value = 0;
                  }
                }
              }
            }
            this.$root.$set(this.$root, 'selectWidget', component);
            this.$root.$set(this.$root.selectWidget, 'style', style);
            this.$root.$set(this.$root.selectWidget.style, 'units', units);
          } else {
            this.$root.$set(this.$root, 'selectWidget', component);
          }
        }
      }
    }

    Vue.component('wBootgrid', {
      props: ['widgetData'],
      data: function () {
        return {
          moveEnd: false
        }
      },
      mounted: function () {

      },
      computed: {
        curOverWidgetKey: function () {
          return this.$root.curOverWidgetKey;
        },
        curSelectWidget: function () {
          return this.$root.selectWidget;
        },
        colDataLength: function () {
          var len = 0;
          if (!this.widgetData.colData) return len;
          for (var i = 0; i < this.widgetData.colData.length; i++) {
            len += this.widgetData.colData[i].items.length;
          }
          console.log(len,'-----------')
          return len;
        },
      },
      methods: {
        handleMoveEnd: function () {

        },
        handleChoose: function () {
        },
        handleRemove: function (e, index) {
          var that = this;
          var path = _.cloneDeep(that.widgetData.path);
          path.push(index);
          that.widgetData.items = [];
          that.widgetData.colData.forEach(function (item) {
            item.items.forEach(function (item2) {
              that.widgetData.items.push(item2);
            })
          });
          that.$root.moveEndStatus = true;
          this.$root.curOverWidgetKey = ''
        },
        handleWidgetColAdd($event, row, colIndex) {
          var that = this;
          var newIndex = $event.newIndex;
          var items = that.widgetData.items || [];
          var _data = _.cloneDeep(row[newIndex]);
          if (!_data.title) {
            var title = (that.$root.componentCounter < 10 ? '0' + that.$root.componentCounter : that.$root.componentCounter) + '_' + _data.name;
            var initOptions = WebApp.widgetDefaults[_data.id];
            initOptions.title = title;
            var data = {
              initOptions: initOptions,
              configuration: initOptions.configuration,
              wtype: _data.id,
              title: title,
              level: that.widgetData.colData[colIndex].level + 1,
              id: _data.id + '_' + UUID.createUUID(),
              defineJs: _data.defineJs
            }
            if (data.wtype.indexOf('wBootgrid') > -1) {
              data.colData = [];
              for (var i = 0; i < data.configuration.columns.length; i++) {
                data.colData[i] = {};
                data.colData[i].items = [];
                data.colData[i].type = 'unit';
                data.colData[i].title = '布局单元' + (i + 1);
                data.colData[i].level = that.widgetData.colData[colIndex].level + 1,
                  data.colData[i].noDrag = true;
                data.colData[i].id = 'layoutUnit_' + UUID.createUUID();
              }
            } else if (data.wtype.indexOf('wBootstrapTabs') > -1) { //标签页
              data = tabLayoutComponent.addTab(data, 0, that.widgetData.colData[colIndex].level + 2, true);
            }
            _data = data;
            that.$root.componentCounter++;
            that.$root.createComponent(_data);
          }
          _data.level = that.widgetData.colData[colIndex].level + 1;
          _data.columnIndex = colIndex;
          _data.path = _.cloneDeep(that.widgetData.path);
          _data.path.push(colIndex);
          _data.parentId = that.widgetData.id;
          var isSameLayout = that.checkIsSameLayout(items, _data);
          if (isSameLayout) return;

          items.push(_data);
          that.$set(that.widgetData, 'items', items);
          appPageDesigner[_data.id] = _data;
        },
        //检查当前移动组件是否是在同一个布局内移动
        checkIsSameLayout: function (items, data) {
          return items.some(function (item) {
            return item.id === data.id;
          })
        },
      },
      template: `<div class="well-row flex" :colDataCount="colDataLength" :style="{width: (widgetData.style && widgetData.style.width + widgetData.style.widthUnit),
                                                     height: (widgetData.style && widgetData.style.fixedHeight && !widgetData.style.autoSetByContent && widgetData.style.height + widgetData.style.heightUnit)}">
                    <template v-if="widgetData.configuration && widgetData.configuration.columns">
                      <draggable :class="'well-col well-col-' + col.colspan" :style="{
                        marginTop: (widgetData.style && widgetData.style.units[index].useMargin && !widgetData.style.units[index].margin.isSame ? widgetData.style.units[index].margin.direction[0].value + 'px' : ''),
                        marginBottom: (widgetData.style && widgetData.style.units[index].useMargin && !widgetData.style.units[index].margin.isSame ? widgetData.style.units[index].margin.direction[1].value + 'px' : ''),
                        marginLeft: (widgetData.style && widgetData.style.units[index].useMargin && !widgetData.style.units[index].margin.isSame ? widgetData.style.units[index].margin.direction[2].value + 'px' : ''),
                        marginRight: (widgetData.style && widgetData.style.units[index].useMargin && !widgetData.style.units[index].margin.isSame ? widgetData.style.units[index].margin.direction[3].value + 'px' : ''),
                        margin: (widgetData.style && widgetData.style.units[index].useMargin && widgetData.style.units[index].margin.isSame ? widgetData.style.units[index].margin.sameValue + 'px' : ''),
                        paddingTop: (widgetData.style && widgetData.style.units[index].usePadding && !widgetData.style.units[index].padding.isSame ? widgetData.style.units[index].padding.direction[0].value + 'px' : ''),
                        paddingBottom: (widgetData.style && widgetData.style.units[index].usePadding && !widgetData.style.units[index].padding.isSame ? widgetData.style.units[index].padding.direction[1].value + 'px' : ''),
                        paddingLeft: (widgetData.style && widgetData.style.units[index].usePadding && !widgetData.style.units[index].padding.isSame ? widgetData.style.units[index].padding.direction[2].value + 'px' : ''),
                        paddingRight: (widgetData.style && widgetData.style.units[index].usePadding && !widgetData.style.units[index].padding.isSame ? widgetData.style.units[index].padding.direction[3].value + 'px' : ''),
                        padding: (widgetData.style && widgetData.style.units[index].usePadding && widgetData.style.units[index].padding.isSame ? widgetData.style.units[index].padding.sameValue + 'px' : ''),
                        boxShadow: ((widgetData.style && widgetData.style.units[index].useMargin && widgetData.style.units[index].margin.direction[0].show ? '0 -' + widgetData.style.units[index].margin.direction[0].value + 'px 0 0 #f9cc9d' : '') +
                                    (widgetData.style && widgetData.style.units[index].useMargin && widgetData.style.units[index].margin.direction[1].show ? '0 ' + widgetData.style.units[index].margin.direction[1].value + 'px 0 0 #f9cc9d' : '') +
                                    (widgetData.style && widgetData.style.units[index].useMargin && widgetData.style.units[index].margin.direction[2].show ? '-' + widgetData.style.units[index].margin.direction[2].value + 'px 0 0 0 #f9cc9d' : '') +
                                    (widgetData.style && widgetData.style.units[index].useMargin && widgetData.style.units[index].margin.direction[3].show ? widgetData.style.units[index].margin.direction[3].value + 'px 0 0 0 #f9cc9d' : '') +
                                    (widgetData.style && widgetData.style.units[index].useMargin && widgetData.style.units[index].margin.isSame && widgetData.style.units[index].margin.show ? '0 0 0 ' + widgetData.style.units[index].margin.sameValue + 'px #f9cc9d' : '') +
                                    (widgetData.style && widgetData.style.units[index].usePadding && widgetData.style.units[index].padding.direction[0].show ? 'inset 0 ' + widgetData.style.units[index].padding.direction[0].value + 'px 0 0 #c3d08b' : '') +
                                    (widgetData.style && widgetData.style.units[index].usePadding && widgetData.style.units[index].padding.direction[1].show ? 'inset 0 -' + widgetData.style.units[index].padding.direction[1].value + 'px 0 0 #c3d08b' : '') +
                                    (widgetData.style && widgetData.style.units[index].usePadding && widgetData.style.units[index].padding.direction[2].show ? 'inset ' + widgetData.style.units[index].padding.direction[2].value + 'px 0 0 0 #c3d08b' : '') +
                                    (widgetData.style && widgetData.style.units[index].usePadding && widgetData.style.units[index].padding.direction[3].show ? 'inset -' + widgetData.style.units[index].padding.direction[3].value + 'px 0 0 0 #c3d08b' : '') +
                                    (widgetData.style && widgetData.style.units[index].usePadding && widgetData.style.units[index].padding.isSame && widgetData.style.units[index].padding.show ? 'inset 0 0 0 ' + widgetData.style.units[index].padding.sameValue + 'px #c3d08b' : ''))
                      }" v-for="col,index in widgetData.configuration.columns" :key="widgetData.colData[index].id" v-model="widgetData.colData[index].items" :no-transition-on-drag="true" v-bind="{group:'widget', handle: '.move-widget', chosenClass: 'choose-widget', ghostClass: 'ghost'}"
                        @end="handleMoveEnd" @remove="handleRemove($event,index)" @choose="handleChoose" @add="handleWidgetColAdd($event, widgetData.colData[index].items, index)">
                          <transition-group name="fade" tag="div" class="well-designer-placeholder" :class="!widgetData.colData[index].items.length ? 'empty-placeholder' : ''">
                            <template v-if="widgetData.colData[index].items.length">
                              <designer-area-component :class="{'widget-over': curOverWidgetKey === colItem.id,'select-widget': curSelectWidget.id === colItem.id}" v-for="(colItem,index) in widgetData.colData[index].items" :component="colItem" :key="colItem.id"></designer-area-component>
                            </template>
                          </transition-group>
                        </draggable>
                    </template>
                  </div>`
    });
    Vue.component('designerAreaComponent', {
      props: ['component'],
      mixins: [appPageVueComponents.selectWidgetMixin, appPageVueComponents.updateWidgetData],
      data: function () {
        return {}
      },
      computed: {
        curOverWidgetKey: function () {
          return this.$root.curOverWidgetKey;
        },
        curSelectWidget: function () {
          return this.$root.selectWidget;
        },
        json_viewer_mode: function () {
          return this.$root.json_viewer_mode;
        }
      },
      mounted: function () {

      },
      methods: {
        getRealItems: function (parent) {
          var items = null;
          if (parent.component.wtype === 'wBootstrapTabs') {
            var tabs = parent.component.configuration.tabs;
            for (var i = 0; i < tabs.length; i++) {
              if (tabs[i].uuid === parent.component.activeTabUuid) {
                items = tabs[i].items;
                break;
              }
            }
          }
          return items;
        },
        //删除组件
        deleteWidget: function (component) {
          var that = this;
          that.$refs.widget.remove();
          var parent = component.parentId ? appPageDesigner[component.parentId] : that.$root.designerAreaData;
          var items = parent.items || parent.component.items || that.getRealItems(parent);
          for (var i = 0; i < items.length; i++) {
            if (items[i].id === component.id) {
              items.splice(i, 1);
              break;
            }
          }
          delete appPageDesigner[component.id];
          if (that.$root.selectWidget.id === component.id) {
            that.$set(that.$root, 'selectWidget', {})
          }
          that.updateData();
        },
        configure: function (component) {
          var that = this;
          var _component = appPageDesigner[component.id];
          appPageDesigner.configure.call(appPageDesigner, _component);
        },
        //json查看器
        jsonView: function (component) {
          var jsonViewerContent = $("#jsonview_template").clone().html();
          var $dialog = appModal.dialog({
            title: "JSON查看器——" + component.title,
            message: jsonViewerContent,
            backdrop: null,
            size: "large",
            maxHeight: "800px",
            dialogPosition: 'center',
            shown: function (_$dialog) {
              var $templateHtml = _$dialog.find(".row-jsonview");
              var $jsonview = $(".jsonview", $templateHtml);
              var definitionJsonString = JSON.stringify(component);
              // JSONView对undefined无法解析问题
              definitionJsonString = definitionJsonString.replace("undefined", "null");
              $jsonview.JSONView(JSON.parse(definitionJsonString), {
                collapsed: true
              });
              $(".btn-collapse", $templateHtml).on("click", function () {
                $jsonview.JSONView("collapse");
              });
              $(".btn-expand", $templateHtml).on("click", function () {
                $jsonview.JSONView("expand");
              });
              $(".btn-toggle", $templateHtml).on("click", function () {
                $jsonview.JSONView("toggle");
              });
              $(".btn-toggle-level1", $templateHtml).on("click", function () {
                $jsonview.JSONView("toggle", 1);
              });
              $(".btn-toggle-level2", $templateHtml).on("click", function () {
                $jsonview.JSONView("toggle", 2);
              });
              $(".btn-toggle-level3", $templateHtml).on("click", function () {
                $jsonview.JSONView("toggle", 3);
              });
            }
          });
        },
        getComponentParent: function (path, index, parent) {
          var that = this;
          if (path.length === index) {
            return parent;
          }
          var _parent = parent[index];
          return that.getComponentParent(path, index + 1, _parent.items);
        },
        handleSelectWidget: function (component) {
          var that = this;
          that.setSelectWidget(component)
        },
        mouseenter: function (e) {
          if (this.$root.moveEndStatus) {
            this.$root.moveEndStatus = false;
            return;
          }
          this.$root.curOverWidgetKey = $(e.target).attr('widgetKey') || '';
        },
        mouseleave: function (e) {
          if (this.$root.moveEndStatus) {
            this.$root.moveEndStatus = false;
            return;
          }
          var $toElement = $(e.toElement).closest('.widget');
          if ($toElement.length) {
            this.$root.curOverWidgetKey = $($toElement).attr('widgetKey') || '';
          } else {
            this.$root.curOverWidgetKey = '';
          }
        }
      },
      template: `<div class="widget designer-component-border" ref="widget" :widgetKey="component.id" @mouseenter="mouseenter" @mouseleave="mouseleave" @click.stop="handleSelectWidget(component)">
        <template v-if="component.wtype.indexOf('wBootgrid') > -1">
          <w-bootgrid :widget-data="component"></w-bootgrid>
        </template>
        <template v-if="component.wtype.indexOf('wSecondPageLayout') > -1">
          <w-second-page :widget-data="component"></w-second-page>
        </template>
        <template v-if="component.wtype.indexOf('wSplit') > -1">
          <w-split :widget-data="component"></w-split>
        </template>
        <template v-else-if="component.wtype.indexOf('wNewHeader') > -1">
          <div :id="component.id">
            <img class="placeholder-img" src="/static/image/header.png">
          </div>
        </template>
        <template v-else-if="component.wtype.indexOf('wFooter') > -1">
          <div :id="component.id">
            <img class="placeholder-img" src="/static/image/footer.png">
          </div>
        </template>
        <template v-else-if="component.wtype.indexOf('wLeftSidebar') > -1">
          <div :id="component.id">
            <img class="placeholder-img" src="/static/image/leftSidebar.png">
          </div>
        </template>
        <template v-else-if="component.wtype.indexOf('wBootstrapTable') > -1">
          <div :id="component.id">
            <img class="placeholder-img" src="/static/image/table2.png">
          </div>
        </template>
        <template v-else-if="component.wtype.indexOf('wCarousel') > -1">
          <div :id="component.id">
            <img class="placeholder-img" src="/static/image/carousel.png">
          </div>
        </template>
        <template v-else-if="component.wtype.indexOf('wFullCalendar') > -1">
          <div :id="component.id">
            <img class="placeholder-img" src="/static/image/calendar.png">
          </div>
        </template>
        <template v-else-if="component.wtype.indexOf('wBootstrapTabs') > -1">
          <w-tab :widget-data="component"></w-tab>
        </template>
        <template v-else-if="component.wtype.indexOf('wPanel') > -1">
          <w-panel :widget-data="component"></w-panel>
        </template>
        <template v-else v-html="component.previewHtml">
        </template>
        <div class="widget-handles clear">
          <div v-show="json_viewer_mode" class="widget-handle-btn" style="background: #3BB4CC" @click.stop="jsonView(component)">
            <i class="iconfont icon-ptkj-jichengkaifa"></i>
            <span>Json</span>
          </div>
          <div class="widget-handle-btn move-widget" style="background: #4BB633">
            <i class="iconfont icon-xmch-pingyi"></i>
            <span>拖动</span>
          </div>
          <div class="widget-handle-btn" style="background: #EAA900" @click="configure(component)">
            <i class="iconfont icon-ptkj-shezhi"></i>
            <span>设置</span>
          </div>
          <div class="widget-handle-btn" style="background: #e33033" @click.stop="deleteWidget(component)">
            <i class="iconfont icon-ptkj-shanchu"></i>
            <span>删除</span>
          </div>
        </div>
      </div>`
    });
    return appPageVueComponents;
  });
