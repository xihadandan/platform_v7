define(['jquery', 'commons', 'vue', 'sortable', 'vuedraggable'],
  function ($, commons, Vue) {
    var UUID = commons.UUID;
    var wSecondPageComponent = {};
    Vue.component('wSecondPage', {
      props: ['widgetData'],
      data: function () {
        return {
          height: 400
        }
      },
      mounted: function () {
        this.height = this.setSecondPageLayoutHeight();
        console.log(this.height,'height');
      },
      destroyed() {

      },
      computed: {
        curOverWidgetKey: function () {
          return this.$root.curOverWidgetKey;
        },
        curSelectWidget: function () {
          return this.$root.selectWidget;
        },
        // widgetHeight: function () {
        //   if (this.$root.needResetSecondPageHeight) {
        //     this.$set(this.$root, 'needResetSecondPageHeight', false);
        //     return this.setSecondPageLayoutHeight();
        //   }
        // },
        leftData: {
          get: function () {
            return this.widgetData.colData ? this.widgetData.colData[0].items : [];
          },
          set: function (v) {
            this.$set(this.widgetData.colData[0], 'items', v);
          }
        },
        rightData: {
          get: function () {
            return this.widgetData.colData ? this.widgetData.colData[1].items : [];
          },
          set: function (v) {
            this.$set(this.widgetData.colData[1], 'items', v);
          }
        },
        leftEmpty: function () {
          return !(this.widgetData.colData && this.widgetData.colData[0].items && this.widgetData.colData[0].items.length) ? 'empty-placeholder' : '';
        },
        rightEmpty: function () {
          return !(this.widgetData.colData && this.widgetData.colData[1].items && this.widgetData.colData[1].items.length) ? 'empty-placeholder' : '';
        }
      },
      methods: {
        setSecondPageLayoutHeight: function () {
          var that = this;
          var designHeight = that.$root.$refs.designerArea.$el.offsetHeight - 40;
          var siblings = that.getSiblings(that.$refs.secondPageLayout.parentNode);
            var widgetHeight = designHeight;
            siblings.forEach(function (item) {
              widgetHeight -= item.offsetHeight;
            })
            return widgetHeight;
        },
        getSiblings: function (elm) {
          var a = [];
          var p = elm.parentNode.children;
          for (var i = 0, pl = p.length; i < pl; i++) {
            if (p[i] !== elm && p[i].getAttribute('widgetKey')) a.push(p[i]);
          }
          return a;
        },
        handleMoveEnd: function () {
        },
        handleChoose: function () {
        },
        handleRemove: function (e, index) {
        },
        handleWidgetAdd($event, row, colIndex) {
          var that = this;
          var newIndex = $event.newIndex;
          var items = row;
          var _data = row[newIndex];
          if (colIndex === 0 && _data.wtype !== 'wLeftSidebar') {
            row.splice(newIndex, 1);
            that.$set(that.widgetData.colData[colIndex], 'items', items);
            appModal.error('该位置只能放置左导航组件！');
            return;
          }
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
              level: that.widgetData.colData[colIndex].level + 1,
              id: _data.id + '_' + UUID.createUUID(),
              defineJs: _data.defineJs
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

          items[newIndex] = _data;
          that.$set(that.widgetData.colData[colIndex], 'items', items);
          that.$set(that.widgetData.colData[colIndex].items, newIndex, _data);
        }
      },
      template: `<section :id="widgetData.id" ref="secondPageLayout" class="second-page clearfix ui-wSecondPageLayout ui-wBootgrid"
      style="overflow: auto;" :style="{height: (height || 400) + 'px'}">
          <draggable v-model="leftData" tag="aside" class="menu-sidebar col-idx-0 column" :no-transition-on-drag="true"
          v-bind="{group:'widget', handle: '.move-widget', chosenClass: 'choose-widget', ghostClass: 'ghost'}"
            @end="handleMoveEnd" @remove="handleRemove($event)" @choose="handleChoose" @add="handleWidgetAdd($event, leftData, 0)">
            <transition-group name="fade" tag="div" class="well-designer-placeholder" :class="leftEmpty">
              <designer-area-component :class="{'widget-over': curOverWidgetKey === colItem.id,'select-widget': curSelectWidget.id === colItem.id}"
              v-for="(colItem,index) in leftData" :component="colItem" :key="colItem.id"></designer-area-component>
            </transition-group>
          </draggable>
          <draggable v-model="rightData" tag="aside" class="content col-idx-1 column" :no-transition-on-drag="true"
           v-bind="{group:'widget', handle: '.move-widget', chosenClass: 'choose-widget', ghostClass: 'ghost'}"
            @end="handleMoveEnd" @remove="handleRemove($event)" @choose="handleChoose" @add="handleWidgetAdd($event, rightData, 1)">
            <transition-group name="fade" tag="div" class="well-designer-placeholder" :class="rightEmpty">
              <designer-area-component :class="{'widget-over': curOverWidgetKey === colItem.id,'select-widget': curSelectWidget.id === colItem.id}"
               v-for="(colItem,index) in rightData" :component="colItem" :key="colItem.id"></designer-area-component>
            </transition-group>
          </draggable>
      </section>`
    });
    return wSecondPageComponent;
  })
