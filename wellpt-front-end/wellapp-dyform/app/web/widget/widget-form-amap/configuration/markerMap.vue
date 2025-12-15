<template>
  <div class="marker-map-container">
    <div class="flex f_x_s" style="margin-bottom: 12px">
      <div>
        <!-- 搜索框 -->
        <a-auto-complete
          v-if="showSearch"
          v-model="keyword"
          :data-source="searchOptions"
          style="width: 100%"
          placeholder="输入地点关键词"
          optionLabelProp="label"
          @search="handleSearch"
          @select="handleSelect"
        >
          <template #option="item">
            <div class="tip-item">
              <span class="tip-name">{{ item.name }}</span>
              <span class="tip-address">{{ item.address }}</span>
            </div>
          </template>
        </a-auto-complete>
      </div>
      <div>
        <a-button type="primary" @click="clearClick">清除</a-button>
      </div>
    </div>
    <div class="marker-map" :id="id"></div>
    <div class="spin-center" v-if="loading">
      <a-spin />
    </div>
  </div>
</template>
<script>
import amapMixin from '@pageAssembly/app/web/widget/widget-amap/amap.mixin.js';
import { upperFirst, isArray, each } from 'lodash';
import { deepClone } from '@framework/vue/utils/util';

export default {
  name: 'markerMap',
  mixins: [amapMixin],
  props: {
    value: String | Object | Array,
    drawType: {
      type: String,
      default: 'marker'
    },
    showSearch: {
      type: Boolean,
      default: true
    },
    city: {
      type: String,
      default: ''
    },
    valueString: Boolean
  },
  data() {
    return {
      marker: undefined,
      circle: undefined,
      polygon: undefined,
      rectangle: undefined,
      circleEditor: undefined,
      polygonEditor: undefined,
      rectangleEditor: undefined,
      mouseTool: undefined,
      overlays: [],
      customOptions: {
        showSearch: this.showSearch,
        circleEditor: !this.isMarker,
        polygonEditor: !this.isMarker,
        rectangleEditor: !this.isMarker,
        mouseTool: !this.isMarker
      }
    };
  },
  computed: {
    isMarker() {
      return this.drawType === 'marker';
    }
  },
  created() {},
  mounted() {},
  methods: {
    initArea() {
      if (this.value) {
        let value = deepClone(this.value);
        if (typeof value === 'string') {
          value = JSON.parse(value);
        }
        switch (this.drawType) {
          case 'marker': {
            this.setMarker(value);
            break;
          }
          case 'polygon': {
            this.setPolygon(value);
            break;
          }
          case 'rectangle': {
            this.setRectangle(value);
            break;
          }
          case 'circle': {
            this.setCircle(value);
            break;
          }
          default: {
            break;
          }
        }
        let zoom = this.map.getZoom();
        this.map.setFitView(null, false, null, zoom);
        this.overlays = this.map.getAllOverlays();
      }
      this.initMouseTool();
    },
    mapLoaded() {
      this.overlays = [];
      this.initArea();
      this.map.on('click', e => {
        this.mapClick(e);
      });
    },
    initMouseTool() {
      if (!this[this.drawType] && !this.isMarker) {
        if (!this.mouseTool) {
          this.mouseTool = new this.AMap.MouseTool(this.map);
          //监听draw事件可获取画好的覆盖物
          this.mouseTool.on('draw', e => {
            this.overlays.push(e.obj);
            this[this.drawType] = e.obj;
            this.initEditor();
            this.mouseTool.close();
            this.emitChange();
          });
        }
        this.draw();
      }
    },
    // 初始化编辑器
    initEditor() {
      if (this[this.drawType + 'Editor']) {
        this[this.drawType + 'Editor'].off('move');
        this[this.drawType + 'Editor'].off('adjust');
        this[this.drawType + 'Editor'].destroy();
      }
      this[this.drawType + 'Editor'] = new this.AMap[upperFirst(this.drawType) + 'Editor'](this.map, this[this.drawType]);
      this[this.drawType + 'Editor'].open();
      this[this.drawType + 'Editor'].on('adjust', event => {
        this.emitChange();
      });
      this[this.drawType + 'Editor'].on('move', event => {
        this.emitChange();
      });
    },
    mapClick(e) {
      switch (this.drawType) {
        case 'marker': {
          this.setMarker(e.lnglat);
          break;
        }
        // case 'circle': {
        //   if (this.circle) {
        //     this.circle.setCenter(e.lnglat);
        //     this.initEditor();
        //     this.emitChange();
        //   }
        //   break;
        // }
        // case 'rectangle': {
        //   if (this.rectangle) {
        //     this.moveRectangleTo(e.lnglat);
        //     this.initEditor();
        //   }
        //   break;
        // }
        // case 'polygon': {
        //   if (this.polygon) {
        //     this.movePolygonTo(e.lnglat);
        //   }
        //   break;
        // }
        default: {
          break;
        }
      }
    },
    // 移动矩形到新的中心点
    moveRectangleTo(newCenter) {
      // 获取当前矩形的边界
      let currentBounds = this.rectangle.getBounds();

      // 计算当前矩形的宽度和高度
      let width = currentBounds.northEast.lng - currentBounds.southWest.lng;
      let height = currentBounds.northEast.lat - currentBounds.southWest.lat;

      // 计算新的边界坐标
      let newSouthWest = [newCenter.lng - width / 2, newCenter.lat - height / 2];
      let newNorthEast = [newCenter.lng + width / 2, newCenter.lat + height / 2];

      // 更新矩形位置
      this.rectangle.setBounds(new this.AMap.Bounds(newSouthWest, newNorthEast));
      this.initEditor();
      this.emitChange();
    },
    // 计算多边形的中心点
    getPolygonCenter() {
      var path = this.polygon.getPath();
      var lngSum = 0,
        latSum = 0;
      var pointCount = path.length;

      // 多边形可能有洞，这里简单处理只计算外层路径
      if (Array.isArray(path[0])) {
        // 处理多边形的洞情况
        path = path[0];
        pointCount = path.length;
      }

      for (var i = 0; i < pointCount; i++) {
        lngSum += path[i].lng;
        latSum += path[i].lat;
      }

      return new AMap.LngLat(lngSum / pointCount, latSum / pointCount);
    },

    // 移动多边形到新的中心点
    movePolygonTo(newCenter) {
      var currentCenter = this.getPolygonCenter();
      var deltaLng = newCenter.lng - currentCenter.lng;
      var deltaLat = newCenter.lat - currentCenter.lat;

      var path = this.polygon.getPath();
      var newPath = [];

      for (var i = 0; i < path.length; i++) {
        newPath.push([path[i].lng + deltaLng, path[i].lat + deltaLat]);
      }

      this.polygon.setPath(newPath);
      this.initEditor();
      this.emitChange();
    },
    setMarker(value) {
      if (!this.marker) {
        this.marker = new this.AMap.Marker({
          position: value,
          draggable: true
          // icon: '/static/js/theme/images/v1_alert.png'
        });
        this.map.add(this.marker);
        this.marker.on('dragend', () => {
          this.emitChange();
        });
      } else {
        this.marker.setPosition(value);
      }
      this.emitChange();
    },
    setPolygon(value) {
      if (!this.polygon && isArray(value) && value.length) {
        this.polygon = new this.AMap.Polygon({
          path: value,
          fillColor: '#00b0ff',
          strokeColor: '#80d8ff',
          draggable: true
        });
        this.map.add(this.polygon);
        this.initEditor();
        this.polygon.on('dragend', () => {
          this.emitChange();
        });
      }
    },
    setRectangle(value) {
      if (!this.rectangle && value && value.northEast) {
        var bounds = new this.AMap.Bounds(value.southWest, value.northEast);
        this.rectangle = new this.AMap.Rectangle({
          bounds: bounds,
          fillColor: '#00b0ff',
          strokeColor: '#80d8ff',
          draggable: true
        });
        this.map.add(this.rectangle);
        this.initEditor();
        this.rectangle.on('dragend', () => {
          this.emitChange();
        });
      }
    },
    setCircle(value) {
      if (!this.circle) {
        this.circle = new this.AMap.Circle({
          center: value && value.center ? value.center : value && value.lat ? value : null,
          radius: value && value.radius ? value.radius : 1000, //半径
          fillColor: '#00b0ff', //不能用var(主题色)
          strokeColor: '#80d8ff'
        });
        this.map.add(this.circle);
        this.initEditor();
      } else {
        this.circle.setCenter(value);
        this.initEditor();
      }
    },
    // mouseTool画矢量图
    draw() {
      switch (this.drawType) {
        case 'polygon': {
          this.mouseTool.polygon({
            fillColor: '#00b0ff',
            strokeColor: '#80d8ff',
            draggable: true
            //同Polygon的Option设置
          });
          break;
        }
        case 'rectangle': {
          this.mouseTool.rectangle({
            fillColor: '#00b0ff',
            strokeColor: '#80d8ff',
            draggable: true
            //同Polygon的Option设置
          });
          break;
        }
        case 'circle': {
          this.mouseTool.circle({
            fillColor: '#00b0ff',
            strokeColor: '#80d8ff',
            draggable: true
            //同Circle的Option设置
          });
          break;
        }
        default: {
          break;
        }
      }
    },
    clearOverlays() {
      this.map.remove(this.overlays);
      if (this[this.drawType]) {
        this.map.remove(this[this.drawType]);
      }
      this.overlays = [];
    },
    clearClick() {
      this.clearOverlays();
      this.removeOldData(this.drawType);
      this.initMouseTool();
    },
    removeOldData(oldType) {
      if (this[oldType] && this[oldType].destroy) {
        this[oldType].destroy();
      }
      if (this[oldType + 'Editor'] && this[oldType + 'Editor'].destroy) {
        this[oldType + 'Editor'].off('move');
        this[oldType + 'Editor'].off('adjust');
        this[oldType + 'Editor'].destroy();
      }
      this[oldType + 'Editor'] = null;
      this[oldType] = null;
    },
    emitChange() {
      let value = null;
      if (this.drawType == 'marker' && this.marker) {
        value = this.marker.getPosition();
      } else if (this.drawType == 'circle' && this.circle) {
        value = {
          center: this.circle.getCenter(),
          radius: this.circle.getRadius()
        };
      } else if (this.drawType == 'rectangle' && this.rectangle) {
        let _bounds = this.rectangle.getBounds();
        value = {
          northEast: _bounds.northEast,
          southWest: _bounds.southWest
        };
      } else if (this.drawType == 'polygon' && this.polygon) {
        value = this[this.drawType].getPath();
      }
      if (value && this.valueString) {
        value = JSON.stringify(value);
      }
      console.log('value', value);
      this.$emit('input', value);
    }
  },
  watch: {
    drawType(val, oldValue) {
      this.clearOverlays();
      this.removeOldData(oldValue);
      this.initMouseTool();
      this.$emit('input', null);
    }
  }
};
</script>
<style scoped lang="less">
.marker-map-container {
  position: relative;
  width: 100%;
  height: 450px;
  .marker-map {
    width: 100%;
    height: e('calc(100% - 54px)');
  }
}
</style>
