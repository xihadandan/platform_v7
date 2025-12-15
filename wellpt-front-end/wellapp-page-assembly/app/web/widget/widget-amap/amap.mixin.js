import { generateId, deepClone } from '@framework/vue/utils/util';
import { assign, extend, each } from 'lodash';
export default {

  computed: {
    vContainerHeight() {
      let height = this.configuration ? this.configuration.style.height : undefined;
      if (height != undefined) {
        return typeof height == 'number' ? `${height}px` : height;
      }
      return 'auto';
    },
    vContainerWidth() {
      let width = this.configuration ? this.configuration.style.width : undefined;
      if (width != undefined) {
        return typeof width == 'number' ? `${width}px` : width;
      }
      return '100%';
    },
    // 高德地图仅有英文和中文，所以除中文外其他默认使用英文
    locale() {
      let locale = this.$i18n && this.$i18n.locale ? this.$i18n.locale : 'zh_cn';
      return locale.indexOf('zh') == -1 ? 'en' : locale;
    },
  },
  data() {
    return {
      AMapLoader: undefined,
      AMap: undefined,
      AMapUI: undefined,
      appConfig: undefined,
      map: undefined,
      id: generateId(),
      loading: true,
      customOptions: {},
      geolocation: undefined,
      keyword: '',
      searchMarker: [],
      searchOptions: [],
      placeSearch: undefined,
      autoComplete: undefined,
      searchInfoData: undefined
    };
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      import('@amap/amap-jsapi-loader').then(AMapLoader => {
        this.AMapLoader = AMapLoader;
        this.getAmapConfig(() => {
          this.initAMap();
        });
      })
    },
    getAmapConfig(callback) {
      $axios.get('/webConfig/getAmapConfig').then(({ data }) => {
        this.appConfig = data;
        if (typeof callback === 'function') {
          callback();
        }
      });
    },
    initAMap() {
      window._AMapSecurityConfig = {
        securityJsCode: this.appConfig.securityJsCode
      };
      this.AMapLoader.load({
        key: this.appConfig.key, // 申请好的Web端开发者Key，首次调用 load 时必填
        version: this.appConfig.version || '2.0', // 指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
        plugins: this.customOptions.isDesigner ? [] : this.getMapPlugins(), //需要使用的的插件列表，如比例尺'AMap.Scale'，支持添加多个如：['...','...']
        AMapUI: {
          version: '1.1',
          plugins: this.customOptions.isDesigner ? [] : this.getAMapUIPlugins()
        }
      }).then(AMap => {
        this.AMap = AMap;
        this.AMapUI = AMapUI;
        this.map = new AMap.Map(this.id, this.getMapOptions());
        if (typeof this.invokeDevelopmentMethod === 'function') {
          this.invokeDevelopmentMethod('afterMapFinished');
        }
        if (typeof this.mapLoaded === 'function') {
          this.mapLoaded();
        }
        if (!this.customOptions.isDesigner) {
          this.initSearch();
          this.initGeolocation();
        }
        this.loading = false;
      }).catch(err => {
        throw (new Error('加载高德地图失败：' + err,))
      })
    },
    // 使用插件
    getMapPlugins() {
      let plugins = [];
      if (this.customOptions.showSearch) {
        plugins.push('AMap.PlaceSearch'); //地点搜索服务，提供了关键字搜索、周边搜索、范围内搜索等功能
        plugins.push('AMap.AutoComplete'); //输入提示，提供了根据关键字获得提示信息的功能
        plugins.push('AMap.DistrictSearch'); //行政区查询服务，提供了根据名称关键字、citycode、adcode 来查询行政区信息的功能
        plugins.push('AMap.CitySearch'); //城市获取服务，获取用户所在城市信息或根据给定IP参数查询城市信息        
      }
      if (this.customOptions.toolBar) {
        plugins.push('AMap.ToolBar'); //工具条，控制地图的缩放、平移等
      }
      if (this.customOptions.scale) {
        plugins.push('AMap.Scale'); //比例尺，显示当前地图中心的比例尺
      }
      if (this.customOptions.hawkEye) {
        plugins.push('AMap.HawkEye'); //鹰眼，用于显示缩略地图，显示于地图右下角，可以随主图的视口变化而变化
      }
      if (this.customOptions.controlBar) {
        plugins.push('AMap.ControlBar'); //组合了旋转、倾斜、复位在内的地图控件
      }
      if (this.customOptions.mapType) {
        plugins.push('AMap.MapType'); //图层切换，通过该插件可以进行地图切换
      }
      if (this.customOptions.geolocation) {
        plugins.push('AMap.Geolocation'); //浏览器定位，提供了获取用户当前准确位置、所在城市的方法
      }
      if (this.customOptions.geocoder) {
        plugins.push('AMap.Geocoder'); //地理编码与逆地理编码服务，提供地址与坐标间的相互转换
      }
      if (this.customOptions.busSearch) {
        plugins.push('AMap.LineSearch'); //公交路线服务，提供公交路线相关信息查询服务
        plugins.push('AMap.StationSearch'); //公交路线服务，提供公交路线相关信息查询服务
      }
      if (this.customOptions.routingSearch) {
        plugins.push('AMap.Driving'); //驾车路线规划服务，提供按照起、终点进行驾车路线的功能
        plugins.push('AMap.TruckDriving'); //货车路线规划，提供起、终点坐标的驾车导航路线查询功能
        plugins.push('AMap.Transfer'); //公交路线规划服务，提供按照起、终点进行公交路线的功能
        plugins.push('AMap.Walking'); //步行路线规划服务，提供按照起、终点进行步行路线的功能
        plugins.push('AMap.Riding'); //骑行路线规划服务，提供按照起、终点进行骑行路线的功能
        plugins.push('AMap.DragRoute'); //拖拽导航插件，可拖拽起终点、途经点重新进行路线规划
      }
      if (this.customOptions.indoorMap) {
        plugins.push('AMap.IndoorMap'); //室内地图，用于在地图中显示室内地图
      }
      if (this.customOptions.mouseTool) {
        plugins.push('AMap.MouseTool'); //鼠标工具插件，用于鼠标画标记点、线、多边形、矩形、圆、距离量测、面积量测、拉框放大、拉框缩小等功能
      }
      if (this.customOptions.circleEditor) {
        plugins.push('AMap.CircleEditor'); //	圆编辑插件，用于使用鼠标改变圆半径大小、拖拽圆心改变圆的位置
      }
      if (this.customOptions.polygonEditor) {
        plugins.push('AMap.PolygonEditor'); //多边形编辑插件，用于通过鼠标调整多边形形状
      }
      if (this.customOptions.polylineEditor) {
        plugins.push('AMap.PolylineEditor'); //折线编辑器，用于通过鼠标调整折线的形状
      }
      if (this.customOptions.rectangleEditor) {
        plugins.push('AMap.RectangleEditor'); //矩形编辑器，用于通过鼠标调整矩形形状
      }
      if (this.customOptions.ellipseEditor) {
        plugins.push('AMap.EllipseEditor'); //椭圆编辑器，用于通过鼠标调整椭圆形状
      }
      if (this.customOptions.bezierCurveEditor) {
        plugins.push('AMap.BezierCurveEditor'); //贝塞尔曲线编辑器，用于通过鼠标调整贝塞尔曲线形状
      }
      if (this.customOptions.markerCluster) {
        plugins.push('AMap.MarkerCluster'); //点聚合插件，用于展示大量点标记，将点标记按照距离进行聚合，以提高绘制性能
      }
      if (this.customOptions.rangingTool) {
        plugins.push('AMap.RangingTool'); //测距插件，可以用距离或面积测量
      }
      if (this.customOptions.cloudDataSearch) {
        plugins.push('AMap.CloudDataSearch'); //云图搜索服务，根据关键字搜索云图点信息
      }
      if (this.customOptions.weather) {
        plugins.push('AMap.Weather'); //天气预报插件，用于获取未来的天气信息
      }
      if (typeof this.invokeDevelopmentMethod === 'function') {
        this.invokeDevelopmentMethod('beforeMapPlugins', plugins);
      }
      return plugins;
    },
    getAMapUIPlugins() {
      let plugins = [];
      if (this.customOptions.poiPicker) {
        plugins.push('misc/PoiPicker'); //POI选点 在给定的输入框上集成输入提示和关键字搜索功能，方便用户选取特定地点（即POI）
      }
      return plugins;
    },
    // 获取地图参数
    getMapOptions() {
      let options = {
        zoom: 11, // 初始化地图级别
      }
      if (this.configuration) {
        options = deepClone(this.configuration.mapOption);
      }
      if (options.centerDefault) {
        if (options.centerValue && options.centerValue.length === 2) {
          options.center = options.centerValue;
        }
      }
      if (this.locale) {
        options.lang = this.locale;
      }
      if (typeof this.invokeDevelopmentMethod === 'function') {
        this.invokeDevelopmentMethod('beforeMapOptions', options);
      }
      return options;
    },
    // 添加插件到页面 control='Scale'
    addControl(control) {
      var $control = new AMap[control]();
      this.map.addControl($control);
    },
    handleResize() {
    },
    // 初始化搜索
    initSearch() {
      if (this.customOptions.showSearch) {
        this.placeSearch = new this.AMap.PlaceSearch({
          panel: undefined, // 不使用默认结果面板
          autoFitView: true, // 自动调整视野
          lang: this.locale
        }); //构造地点查询类

        this.autoComplete = new this.AMap.Autocomplete({
          city: '中国',
          lang: this.locale
        });
      }
    },
    handleSearch(value) {
      this.searchOptions.splice(0, this.searchOptions.length);
      if (!value.trim()) {
        return;
      }
      this.autoComplete.search(value, (status, result) => {
        if (status === 'complete') {
          each(result.tips, item => {
            if (item.id) {
              this.searchOptions.push({
                value: item.id + '_' + item.name,
                text: item.name,
                address: item.district + item.address,
                location: item.location,
                key: item.id,
                ...item
              });
            }
          });
        }
      });
    },
    handleSelect(value, option) {
      // 清除之前的标记
      this.map.remove(this.searchMarker);
      this.closeInfoWindow();
      this.searchMarker = [];
      if (value) {
        if (value.indexOf('_') > -1) {
          value = value.split('_')[1];
        }
        this.keyword = value;
        // 执行搜索
        this.placeSearch.search(value, (status, result) => {
          if (status === 'complete' && result.poiList.pois.length > 0) {
            // 添加新的标记和弹框信息
            result.poiList.pois.forEach(poi => {
              this.setSearchMarkers(poi);
            });
            this.map.setFitView();
          }
        });
      }
    },
    handlePoiSelect(value) {
      // 清除之前的标记
      this.map.remove(this.searchMarker);
      this.closeInfoWindow();
      this.searchMarker = [];
      if (value) {
        let PGUID = '';
        if (value.indexOf('_') > -1) {
          PGUID = value.split('_')[0];
          value = value.split('_')[1];
        }
        this.keyword = value;
        // 执行搜索
        this.placeSearch.getDetails(PGUID, (status, result) => {
          if (status === 'complete' && result.poiList.pois.length > 0) {
            // 添加新的标记和弹框信息
            result.poiList.pois.forEach(poi => {
              this.setSearchMarkers(poi);
            });
            this.map.setFitView();
          }
        });
      }
    },
    setSearchMarkers(poi) {
      poi.addressPrefix = poi.pname + poi.cityname + poi.adname;
      let marker = new this.AMap.Marker({
        position: new this.AMap.LngLat(poi.location.lng, poi.location.lat),
        map: this.map,
        zIndex: 101,
        offset: new AMap.Pixel(-9, -31),
        size: new AMap.Size(19, 33),
        content: '<div class="amap_lib_placeSearch_poi"></div>',
        extData: poi // 将poi数据附加到marker上，以便后续使用
      });
      this.searchMarker.push(marker);
      this.AMap.Event.addListener(marker, 'click', e => {
        // 自定义点击事件，例如打开信息窗口或显示详细信息等
        console.log('Clicked:', poi);
        this.searchInfoData = poi;
        this.$nextTick(() => {
          let content = this.$refs.searchInfoDataRef ? this.$refs.searchInfoDataRef.$el.outerHTML : `<div class="ant-card"><div class="ant-card-body">${this.searchInfoData.name}</div></div>`;
          let infoWindow = this.createInfoWindow(content);
          this.openInfoWindow(infoWindow, marker);
        });
      });
    },

    //4.构建自定义窗体
    createInfoWindow(content) {
      let infoWindowData = new this.AMap.InfoWindow({
        isCustom: true, //使用自定义窗体
        content: content,
        offset: new AMap.Pixel(16, -45),
        anchor: 'bottom-center',
      });
      return infoWindowData;
    },

    //5.打开窗体
    openInfoWindow(infoWindow, marker) {
      this.closeInfoWindow();
      infoWindow.open(this.map, marker.getPosition());
    },
    //6.关闭窗体
    closeInfoWindow() {
      this.map.clearInfoWindow();
    },

    // 初始化定位
    initGeolocation() {
      if (this.customOptions.geolocation) {
        this.geolocation = new this.AMap.Geolocation({
          showButton: true, //是否显示定位按钮
          showCircle: false,
          showMarker: this.customOptions.geolocationShowMarker === undefined ? true : this.customOptions.geolocationShowMarker,
        });
        this.map.addControl(this.geolocation);
        // 监听定位成功/失败
        this.geolocation.on("complete", (result) => {
          this.geolocationOnComplete(result);
        }).on("error", (err) => {
          this.geolocationOnError(err);
        });
      }
    },
    //解析定位结果
    geolocationOnComplete(data) {
      console.log('定位成功', data);
      if (typeof this.setMarker === 'function') {
        this.setMarker(data.position);
      } else {
        this.map.setCenter([data.position.lng, data.position.lat]); // 地图中心点移动到当前位置
      }
      if (typeof this.invokeDevelopmentMethod === 'function') {
        this.invokeDevelopmentMethod('geolocationOnComplete', data);
      }
      this.geolocationOnCompleteHandler(data);
    },
    geolocationOnCompleteHandler(data) { },
    //解析定位错误信息
    geolocationOnError(data) {
      console.error(`定位失败:失败原因排查信息:${data.message},浏览器返回信息${data.originMessage}`);
    },
    // 开始实时定位
    startTracking(isWatch) {
      if (this.geolocation) {
        if (isWatch) {
          // 如果需要持续定位，可以使用 watchPosition
          geolocation.watchPosition();
        } else {
          this.geolocation.getCurrentPosition(); // 单次定位
        }
      }
    },
    // 停止定位
    stopTracking() {
      if (this.geolocation) {
        this.geolocation.clearWatch(); // 停止持续定位
      }
    }
  },
  beforeDestroy() {
    this.map && this.map.destroy();
  },
  watch: {
    vSize: {
      handler() {
        this.handleResize()
      }
    }
  }
}
