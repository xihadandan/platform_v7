define(['jquery', 'commons', 'vue', 'sortable', 'vuedraggable'],
  function ($, commons, Vue) {
    var UUID = commons.UUID;
    var wSecondPageComponent = {};
    Vue.component('wSplit', {
      props: ['widgetData'],
      data: function () {
        return {

        }
      },
      mounted: function () {

      },
      destroyed() {

      },
      computed: {

      },
      methods: {
        handleMoveEnd: function () {
        },
        handleChoose: function () {
        },
        handleRemove: function (e, index) {
        },
        handleWidgetAdd($event, row, colIndex) {
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
      template: `<split-pane style="height: 400px" :min-percent='20' :default-percent='30' split="vertical">
      <template slot="paneL">
        我是左边
      </template>
      <template slot="paneR">
        我是右边
      </template>
    </split-pane>`
    });

    Vue.component('resizer', {
      props: {
        split: {
          validator(value) {
            return ['vertical', 'horizontal'].indexOf(value) >= 0
          },
          required: true
        },
        className: String
      },
      computed: {
        classes() {
          var classes = ['splitter-pane-resizer', this.split, this.className]
          return classes.join(' ')
        }
      },
      template: '<div class="mid" :class="classes"></div>'
    });

    Vue.component('pane', {
      props: {
        split: {
          validator(value) {
            return ['vertical', 'horizontal'].indexOf(value) >= 0
          },
          required: true
        },
        className: String
      },
      computed: {
        classes() {
          var classes = ['splitter-pane-resizer', this.split, this.className]
          return classes.join(' ')
        }
      },
      template: `<div :class="classes">
        <slot></slot>
      </div>`
    });

    Vue.component('splitPane', {
      props: {
        minPercent: {
          type: Number,
          default: 10
        },
        defaultPercent: {
          type: Number,
          default: 50
        },
        split: {
          validator(value) {
            return ['vertical', 'horizontal'].indexOf(value) >= 0
          },
          required: true
        },
        className: String
      },
      computed: {
        userSelect() {
          return this.active ? 'none' : ''
        },
        cursor() {
          return this.active ? (this.split === 'vertical' ? 'col-resize' : 'row-resize') : ''
        }
      },
      watch: {
        defaultPercent(newValue,oldValue){
          this.percent = newValue
        }
      },
      data() {
        return {
          active: false,
          hasMoved: false,
          height: null,
          percent: this.defaultPercent,
          type: this.split === 'vertical' ? 'width' : 'height',
          resizeType: this.split === 'vertical' ? 'left' : 'top'
        }
      },
      methods: {
        onClick() {
          if (!this.hasMoved) {
            this.percent = 50
            this.$emit('resize', this.percent)
          }
        },
        onMouseDown() {
          this.active = true
          this.hasMoved = false
        },
        onMouseUp() {
          this.active = false
        },
        onMouseMove(e) {
          if (e.buttons === 0 || e.which === 0) {
            this.active = false
          }

          if (this.active) {
            let offset = 0
            let target = e.currentTarget
            if (this.split === 'vertical') {
              while (target) {
                offset += target.offsetLeft
                target = target.offsetParent
              }
            } else {
              while (target) {
                offset += target.offsetTop
                target = target.offsetParent
              }
            }

            const currentPage = this.split === 'vertical' ? e.pageX : e.pageY
            const targetOffset = this.split === 'vertical' ? e.currentTarget.offsetWidth : e.currentTarget.offsetHeight
            const percent = Math.floor(((currentPage - offset) / targetOffset) * 10000) / 100

            // if (percent > this.minPercent && percent < 100 - this.minPercent) {
            //   this.percent = percent
            // }

            this.percent = percent;
            this.$emit('resize', this.percent)
            this.hasMoved = true
          }
        }
      },
      template: `<div :style="{ cursor, userSelect}" class="vue-splitter-container clearfix" @mouseup="onMouseUp" @mousemove="onMouseMove">
        <pane class="splitter-pane splitter-paneL" :split="split" :style="{ [type]: percent+'%'}">
          <slot name="paneL"></slot>
        </pane>
        <resizer :className="className" :style="{ [resizeType]: percent+'%'}" :split="split" @mousedown.native="onMouseDown" @click.native="onClick"></resizer>
        <pane class="splitter-pane splitter-paneR" :split="split" :style="{ [type]: 100-percent+'%'}">
          <slot name="paneR"></slot>
        </pane>
        <div class="vue-splitter-container-mask" v-if="active"></div>
      </div>`
    });


    return wSecondPageComponent;
  })
