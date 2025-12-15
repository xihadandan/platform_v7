define(['jquery', 'commons', 'vue', 'sortable', 'vuedraggable'],
  function ($, commons, Vue) {
    var UUID = commons.UUID;
    var panelLayoutComponent = {};
    Vue.component('wPanel', {
      props: ['widgetData'],
      data: function () {
        return {
          showPopup: false,
          checkedTabIndex: '',
          activeTabIndex: '',
          editTabIndex: ''
        }
      },
      mounted: function () {
        document.addEventListener("click", this.hidePopup);
      },
      destroyed() {
        //组件销毁时移除事件
        document.removeEventListener("click", this.hidePopup);
      },
      computed: {
        curOverWidgetKey: function () {
          return this.$root.curOverWidgetKey;
        },
        curSelectWidget: function () {
          return this.$root.selectWidget;
        },
        showPanelHeader: function () {
          if (this.widgetData.configuration && (this.widgetData.configuration.hideHeader === undefined || !this.widgetData.configuration.hideHeader)) {
            return true;
          }
          return false;
        },
        showPanelHeaderTab: function () {
          if (this.widgetData.configuration && this.widgetData.configuration.body.contentType === 'contentTab' && this.widgetData.configuration.body.tabs.length) {
            return true;
          }
          return false;
        },
        showPanelBodyTab: function () {
          if (this.widgetData.configuration && this.widgetData.configuration.body && this.widgetData.configuration.body.contentType === 'contentTab' && this.widgetData.configuration.body.tabs.length) {
            return true;
          }
          return false;
        },
        showPanelMore: function () {
          if (this.widgetData.configuration && this.widgetData.configuration.header && this.widgetData.configuration.header.enableViewMore) {
            return true;
          }
          return false;
        },
        showPanelSearch: function () {
          if (this.widgetData.configuration && this.widgetData.configuration.header && this.widgetData.configuration.header.enableQuery) {
            return true;
          }
          return false;
        }
      },
      methods: {
        changeTab: function (uuid) {
          this.showPopup = false;
          this.$set(this.widgetData, 'activeTabUuid', uuid);
        },
        openPopup: function () {
          this.showPopup = true;
        },
        hidePopup: function (e) {
          var that = this;
          if (!that.showPopup) return;
          let popup = that.$refs.popup;
          if (popup) {
            if (!popup.contains(e.target)) {
              that.showPopup = false;
            }
          }
        },
        checkTab: function (index, check) {
          if (check) {
            this.checkedTabIndex = index;
          } else {
            this.checkedTabIndex = '';
          }
        },
        editTabName: function (item, index) {
          var that = this;
          if (that.activeTabIndex !== index) {
            that.activeTabIndex = index;
            return;
          }
          that.editTabIndex = index;
        },
        editTabNameInput: function (e, item) {
          var _value = e.target.value;
          this.$set(item, 'name', _value);
          this.$set(item, 'title', _value);
        },
        blur: function () {
          this.editTabIndex = '';
        },
        addTab: function () {
          var that = this;
          var tabsLen = that.widgetData.configuration.body.tabs.length;
          var newTab = panelLayoutComponent.addTab(that.widgetData, tabsLen, that.widgetData.level + 1);
          that.$set(that.widgetData.configuration.body.tabs, tabsLen, newTab);
        },
        moveTabUp: function () {
          var that = this;
          var tabs = that.widgetData.configuration.body.tabs;
          if (that.checkedTabIndex === 0) {
            appModal.error('已经是第一个tab项,不能上移！');
            return;
          }
          tabs[that.checkedTabIndex] = tabs.splice(that.checkedTabIndex - 1, 1, tabs[that.checkedTabIndex])[0];
          that.checkedTabIndex--;
          that.$set(that.widgetData.configuration.body, 'tabs', tabs);
        },
        moveTabDown: function () {
          var that = this;
          var tabs = that.widgetData.configuration.body.tabs;
          if (that.checkedTabIndex === tabs.length - 1) {
            appModal.error('已经是最后一个tab项,不能下移！');
            return;
          }
          tabs[that.checkedTabIndex] = tabs.splice(that.checkedTabIndex + 1, 1, tabs[that.checkedTabIndex])[0];
          that.checkedTabIndex++;
          that.$set(that.widgetData.configuration.body, 'tabs', tabs);
        },
        deleteTab: function () {
          var that = this;
          var tabs = that.widgetData.configuration.body.tabs;
          tabs.splice(that.checkedTabIndex, 1);
          that.checkedTabIndex = '';
          that.$set(that.widgetData.configuration.body, 'tabs', tabs);
        },
        handleMoveEnd: function () {
          console.log(arguments, '--------handleMoveEnd----wBootgrid');
        },
        handleChoose: function () {
        },
        handleRemove: function (e, index) {
          console.log(arguments, '--------handleRemove----wBootgrid');
        },
        handleWidgetAdd($event, row, colIndex, isTab) {
          var that = this;
          var newIndex = $event.newIndex;
          var items = isTab ? that.widgetData.configuration.body.tabs[colIndex].items : that.widgetData.items;
          var _data = row[newIndex];
          if (!_data.title) {
            var title = (that.$root.componentCounter < 10 ? '0' + that.$root.componentCounter : that.$root.componentCounter) + '_' + _data.name;
            var initOptions = WebApp.widgetDefaults[_data.id];
            initOptions.title = title;
            var data = {
              key: key,
              initOptions: initOptions,
              configuration: initOptions.configuration,
              wtype: _data.id,
              title: title,
              level: isTab ? that.widgetData.configuration.body.tabs[colIndex].level + 1 : that.widgetData.level + 1,
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
                data.colData[i].level = data.level + 1;
                data.colData[i].noDrag = true;
                data.colData[i].id = 'layoutUnit_' + UUID.createUUID();
              }
            } else if (data.wtype.indexOf('wBootstrapTabs') > -1) { //标签页
              data = tabLayoutComponent.addTab(data, 0, data.level + 1, true);
            }
            _data = data;
            that.$root.componentCounter++;
            that.$root.createComponent(_data);
          }
          _data.level = isTab ? that.widgetData.configuration.body.tabs[colIndex].level + 1 : that.widgetData.level + 1;
          _data.columnIndex = colIndex;
          _data.path = _.cloneDeep(that.widgetData.path);
          _data.path.push(colIndex);
          _data.parentId = that.widgetData.id;

          items[newIndex] = _data;
          if (isTab) {
            that.$set(that.widgetData.configuration.body.tabs[colIndex], 'items', items);
            that.$set(that.widgetData.configuration.body.tabs[colIndex].items, newIndex, _data);
          } else {
            that.$set(that.widgetData, 'items', items);
            that.$set(that.widgetData.items, newIndex, _data);
          }
        }
      },
      template: `<div :id="widgetData.id" class="panel panel-default ui-wPanel">
                  <div class="panel-heading" v-if="showPanelHeader">
                    <h3 class="panel-title">
                      <span class="panel-icon iconfont icon-ptkj-qukuaibiaotitubiao"></span>
                      <span class="title-text">{{widgetData.configuration.name || widgetData.title}}</span>
                    </h3>
                    <div class="panel-header-right">
                      <div class="pull-right panel-view-more ml-20" v-if="showPanelMore">
                        <a>更多<i class="iconfont icon-ptkj-xianmiaojiantou-you"></i></a>
                      </div>
                      <div class="pull-right search-wrapper" v-if="showPanelSearch">
                        <div class="panel-search-icon">
                          <i class="iconfont icon-ptkj-sousuochaxun"></i>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="panel-body">
                    <template v-if="!showPanelBodyTab">
                      <draggable v-model="widgetData.items" :no-transition-on-drag="true" v-bind="{group:'widget', handle: '.move-widget', chosenClass: 'choose-widget', ghostClass: 'ghost'}"
                        @end="handleMoveEnd" @remove="handleRemove($event)" @choose="handleChoose" @add="handleWidgetAdd($event, widgetData.items)">
                        <transition-group name="fade" tag="div" class="well-designer-placeholder" :class="!(widgetData.items && widgetData.items.length) ? 'empty-placeholder' : ''">
                          <designer-area-component :class="{'widget-over': curOverWidgetKey === colItem.id,'select-widget': curSelectWidget.id === colItem.id}" v-for="(colItem,index) in widgetData.items" :component="colItem" :key="colItem.id"></designer-area-component>
                        </transition-group>
                      </draggable>
                    </template>
                    <div class="panel-tab-header clearfix" :class="{'has-panel-header': showPanelHeader}" v-if="showPanelBodyTab">
                      <ul class="nav nav-tabs pull-left" role="tablist">
                        <template v-if="widgetData.configuration && widgetData.configuration.body.tabs">
                        <li role="presentation" @click.stop="changeTab(item.uuid)" :id="item.uuid" :class="{'active': (widgetData.activeTabUuid ? widgetData.activeTabUuid === item.uuid : index === 0)}" v-for="item,index in widgetData.configuration.body.tabs" :key="item.uuid">
                          <a>
                            <span :class="item.icon && item.icon.className"></span>{{item.name}}
                          </a>
                        </li>
                      </template>
                      <li class="tab-handle">
                        <div class="tab-handle-btn" @click.stop="openPopup">
                          <i class="iconfont icon-ptkj-bianji"></i>
                        </div>
                        <div class="tab-handle-popup" ref="popup" v-show="showPopup">
                          <div class="top-bar">
                            <div>
                              <i class="iconfont icon-ptkj-jiahao" @click.stop="addTab"></i>
                            </div>
                            <div>
                              <i class="iconfont icon-ptkj-shangyi" @click.stop="moveTabUp"></i>
                              <i class="iconfont icon-ptkj-xiayi" @click.stop="moveTabDown"></i>
                              <i class="iconfont icon-ptkj-shanchu" @click.stop="deleteTab"></i>
                            </div>
                          </div>
                          <ul>
                            <template v-if="widgetData.configuration && widgetData.configuration.body.tabs">
                              <li v-for="item,index in widgetData.configuration.body.tabs" :key="item.uuid" :class="{'checked': index === checkedTabIndex, 'active': index === activeTabIndex}" @click.stop="editTabName(item,index)">
                                <i v-show="index !== checkedTabIndex" @click.stop="checkTab(index, true)" class="iconfont icon-ptkj-danxuan-weixuan"></i>
                                <i v-show="index === checkedTabIndex" @click.stop="checkTab(index)" class="iconfont icon-ptkj-danxuan-xuanzhong"></i>
                                <input v-if="index === editTabIndex" class="form-control" v-model="item.name" @input="editTabNameInput($event,item)" @blur="blur">
                                <span v-else class="ellipsis" @click.stop="editTabName(item,index)">{{item.name}}</span>
                              </li>
                            </template>
                          </ul>
                        </div>
                      </li>
                      </ul>
                      <div class="panel-header-right">
                        <div class="pull-right panel-view-more ml-20" v-if="showPanelMore">
                          <a>更多<i class="iconfont icon-ptkj-xianmiaojiantou-you"></i></a>
                        </div>
                        <div class="pull-right search-wrapper" v-if="showPanelSearch">
                          <div class="panel-search-icon">
                            <i class="iconfont icon-ptkj-sousuochaxun"></i>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div class="tab-content" style="" v-if="showPanelBodyTab">
                      <template v-if="widgetData.configuration && widgetData.configuration.body && widgetData.configuration.body.tabs">
                        <div role="tabpanel" class="tab-pane" :class="{'active': (widgetData.activeTabUuid ? widgetData.activeTabUuid === item.uuid : index === 0)}" :id="item.uuid" v-for="item,index in widgetData.configuration.body.tabs" :key="item.uuid">
                          <draggable v-model="widgetData.configuration.body.tabs[index].items" :no-transition-on-drag="true" v-bind="{group:'widget', handle: '.move-widget', chosenClass: 'choose-widget', ghostClass: 'ghost'}"
                            @end="handleMoveEnd" @remove="handleRemove($event,index)" @choose="handleChoose" @add="handleWidgetAdd($event, widgetData.configuration.body.tabs[index].items, index, true)">
                            <transition-group name="fade" tag="div" class="well-designer-placeholder" :class="!widgetData.configuration.body.tabs[index].items.length ? 'empty-placeholder' : ''">
                              <designer-area-component :class="{'widget-over': curOverWidgetKey === colItem.id,'select-widget': curSelectWidget.id === colItem.id}" v-for="(colItem,index) in widgetData.configuration.body.tabs[index].items" :component="colItem" :key="colItem.id"></designer-area-component>
                            </transition-group>
                          </draggable>
                        </div>
                    </template>
                    </div>
                  </div>
                </div>`,
    });

    panelLayoutComponent.addTab = function (widget, index, level, isFirst) {
      if (isFirst) {
        widget.configuration.body.tabs = [];
      }
      var _uuid = UUID.createUUID();
      var name = 'Tab' + (index + 1);
      var tab = {
        name: name,
        title: name,
        type: 'tab',
        level: level,
        uuid: _uuid,
        id: 'tab_' + _uuid,
        items: []
      }
      if (isFirst) {
        widget.activeTabUuid = tab.uuid;
        widget.configuration.body.tabs.push(tab);
        return widget;
      }
      return tab;
    }
    return panelLayoutComponent;
  })
