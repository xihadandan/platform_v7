define(['jquery', 'commons', 'vue', 'sortable', 'vuedraggable'],
  function ($, commons, Vue) {
    var UUID = commons.UUID;
    var tabLayoutComponent = {};
    Vue.component('wTab', {
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
        tabDirectionClass: function () {
          if (!this.widgetData.configuration) {
            return '';
          }
          if (this.widgetData.configuration.tabStyle !== 'top') {
            return 'row row-tabs row-tabs-' + this.widgetData.configuration.tabStyle
          } else {
            return ''
          }
        },
        tabBarClass: function () {
          if (!this.widgetData.configuration) {
            return '';
          }
          if (this.widgetData.configuration.tabStyle !== 'top') {
            return 'col-xs-' + this.widgetData.configuration.tabColspan + ' tabs-' + this.widgetData.configuration.tabStyle
          } else {
            return ''
          }
        },
        tabContentClass: function () {
          if (!this.widgetData.configuration) {
            return '';
          }
          if (this.widgetData.configuration.tabStyle !== 'top') {
            return 'col-xs-' + (12 - this.widgetData.configuration.tabColspan)
          } else {
            return ''
          }
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
          let popup = that.$refs.popup
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
          var tabsLen = that.widgetData.configuration.tabs.length;
          var newTab = tabLayoutComponent.addTab(that.widgetData, tabsLen, that.widgetData.level + 1);
          that.$set(that.widgetData.configuration.tabs, tabsLen, newTab);
        },
        moveTabUp: function () {
          var that = this;
          var tabs = that.widgetData.configuration.tabs;
          if (that.checkedTabIndex === 0) {
            appModal.error('已经是第一个tab项,不能上移！');
            return;
          }
          tabs[that.checkedTabIndex] = tabs.splice(that.checkedTabIndex - 1, 1, tabs[that.checkedTabIndex])[0];
          that.checkedTabIndex--;
          that.$set(that.widgetData.configuration, 'tabs', tabs);
        },
        moveTabDown: function () {
          var that = this;
          var tabs = that.widgetData.configuration.tabs;
          if (that.checkedTabIndex === tabs.length - 1) {
            appModal.error('已经是最后一个tab项,不能下移！');
            return;
          }
          tabs[that.checkedTabIndex] = tabs.splice(that.checkedTabIndex + 1, 1, tabs[that.checkedTabIndex])[0];
          that.checkedTabIndex++;
          that.$set(that.widgetData.configuration, 'tabs', tabs);
        },
        deleteTab: function () {
          var that = this;
          var tabs = that.widgetData.configuration.tabs;
          tabs.splice(that.checkedTabIndex, 1);
          that.checkedTabIndex = '';
          that.$set(that.widgetData.configuration, 'tabs', tabs);
        },
        handleMoveEnd: function () {

        },
        handleChoose: function () {
        },
        handleRemove: function (e, index) {
          
        },
        handleWidgetTabAdd($event, row, colIndex) {
          var that = this;
          var newIndex = $event.newIndex;
          var items = that.widgetData.configuration.tabs[colIndex].items || [];
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
              level: that.widgetData.configuration.tabs[colIndex].level + 1,
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
          _data.level = that.widgetData.configuration.tabs[colIndex].level + 1;
          _data.columnIndex = colIndex;
          _data.path = _.cloneDeep(that.widgetData.path);
          _data.path.push(colIndex);
          _data.parentId = that.widgetData.id;

          items[newIndex] = _data;
          that.$set(that.widgetData.configuration.tabs[colIndex], 'items', items);
          that.$set(that.widgetData.configuration.tabs[colIndex].items, newIndex, _data);
        }
      },
      template: `<div class="ui-wBootstrapTabs" :class="tabDirectionClass" :id="widgetData.id" :w-name="widgetData.title">
    <ul class="nav nav-tabs js-nav-tabs" :class="tabBarClass" role="tablist">
      <template v-if="widgetData.configuration && widgetData.configuration.tabs">
        <li role="presentation" @click.stop="changeTab(item.uuid)" :id="item.uuid" :class="{'active': widgetData.activeTabUuid === item.uuid}" v-for="item,index in widgetData.configuration.tabs" :key="item.uuid">
          <a href="#wBootstrapTabs_C94EABC8F15000017136192019C0CFB0-0" role="tab" data-toggle="tab" aria-controls="tab-0" click-refresh="0" aria-expanded="true">
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
            <template v-if="widgetData.configuration && widgetData.configuration.tabs">
              <li v-for="item,index in widgetData.configuration.tabs" :key="item.uuid" :class="{'checked': index === checkedTabIndex, 'active': index === activeTabIndex}" @click.stop="editTabName(item,index)">
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
    <div class="tab-content js-tab-content" :class="tabContentClass">
      <template v-if="widgetData.configuration && widgetData.configuration.tabs">
        <div role="tabpanel" class="tab-pane" :class="{'active': widgetData.activeTabUuid === item.uuid}" :id="item.uuid" v-for="item,index in widgetData.configuration.tabs" :key="item.uuid">
          <draggable v-model="widgetData.configuration.tabs[index].items" :no-transition-on-drag="true" v-bind="{group:'widget', handle: '.move-widget', chosenClass: 'choose-widget', ghostClass: 'ghost'}"
            @end="handleMoveEnd" @remove="handleRemove($event,index)" @choose="handleChoose" @add="handleWidgetTabAdd($event, widgetData.configuration.tabs[index].items, index)">
            <transition-group name="fade" tag="div" class="well-designer-placeholder" :class="!widgetData.configuration.tabs[index].items.length ? 'empty-placeholder' : ''">
              <designer-area-component :class="{'widget-over': curOverWidgetKey === colItem.id,'select-widget': curSelectWidget.id === colItem.id}" v-for="(colItem,index) in widgetData.configuration.tabs[index].items" :component="colItem" :key="colItem.id"></designer-area-component>
            </transition-group>
          </draggable>
        </div>
      </template>
    </div>
  </div>`
    });

    tabLayoutComponent.addTab = function (widget, index, level, isFirst) {
      if (isFirst) {
        widget.configuration.tabs = [];
        widget.configuration.tabStyle = 'top';
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
        widget.configuration.tabs.push(tab);
        return widget;
      }
      return tab;
    }
    return tabLayoutComponent;
  })
