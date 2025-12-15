<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model-item label="名称">
          <a-input v-model="widget.title" />
        </a-form-model-item>
        <a-form-model-item label="JS模块">
          <JsModuleSelect v-model="widget.configuration.jsModules" dependencyFilter="WidgetChartDevelopment" width="205px" />
        </a-form-model-item>
        <a-collapse :bordered="false" expandIconPosition="right">
          <a-collapse-panel key="mapOptions" header="地图配置">
            <a-form-model-item label="中心点">
              <a-radio-group size="small" v-model="widget.configuration.mapOption.centerDefault">
                <a-radio-button :value="false">默认</a-radio-button>
                <a-radio-button :value="true">自定义</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
            <template v-if="widget.configuration.mapOption.centerDefault">
              <a-form-model-item label="中心点经度">
                <a-input-number style="width: 120px" v-model="widget.configuration.mapOption.centerLng" placeholder="经度" :min="0" />
              </a-form-model-item>
              <a-form-model-item label="中心点纬度">
                <a-input-number style="width: 120px" v-model="widget.configuration.mapOption.centerLat" placeholder="纬度" :min="0" />
              </a-form-model-item>
            </template>
            <a-form-model-item label="是否缩放">
              <a-switch v-model="widget.configuration.mapOption.zoomEnable" />
            </a-form-model-item>
            <template v-if="widget.configuration.mapOption.zoomEnable">
              <a-form-model-item label="缩放区间">
                <a-input-group compact>
                  <a-input-number
                    v-model="widget.configuration.mapOption.zooms[0]"
                    :min="2"
                    :max="widget.configuration.mapOption.zooms[1]"
                  />
                  <a-input-number
                    v-model="widget.configuration.mapOption.zooms[1]"
                    :min="widget.configuration.mapOption.zooms[0]"
                    :max="26"
                  />
                </a-input-group>
              </a-form-model-item>
              <a-form-model-item label="缩放级别">
                <a-input-number
                  style="width: 120px"
                  v-model="widget.configuration.mapOption.zoom"
                  :min="widget.configuration.mapOption.zooms[0]"
                  :max="widget.configuration.mapOption.zooms[1]"
                />
              </a-form-model-item>
            </template>
            <a-form-model-item label="地图样式">
              <a-select
                :allowClear="true"
                placeholder="请选择"
                :options="styleOptions"
                :style="{ width: '100%' }"
                v-model="widget.configuration.mapOption.mapStyle"
                :getPopupContainer="getPopupContainerByPs()"
                :dropdownClassName="getDropdownClassName()"
              />
            </a-form-model-item>
            <a-form-model-item label="高度">
              <a-input-group compact>
                <a-input-number v-model="widget.configuration.style.height" style="width: 100px" :min="1" />
                <a-button>px</a-button>
              </a-input-group>
            </a-form-model-item>
          </a-collapse-panel>
          <a-collapse-panel key="markerOptions" header="点标记配置">
            <a-form-model-item label="点击添加标记">
              <a-switch v-model="widget.configuration.mapOption.zoomEnable" />
            </a-form-model-item>
          </a-collapse-panel>
        </a-collapse>
      </a-tab-pane>

      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration
          :widget="widget"
          :designer="designer"
          :varOptions="[
            { label: '图形名称', value: 'componentType' },
            { label: '系列类型', value: 'seriesType' },
            { label: '类目名', value: 'name' },
            { label: '数据值', value: 'value' }
          ]"
        >
          <template slot="eventParamValueHelpSlot">
            <div style="width: 600px">
              <p>
                1. 支持通过
                <a-tag>${ 参数 }</a-tag>
                表达式解析以下对象值:
              </p>
              <ul>
                <li>componentType : 当前点击的图形元素所属的组件名称, 其值如 series 、markLine、markPoint、timeLine 等</li>
                <li>seriesType : 系列类型。值可能为 line、bar、pie 等。当 componentType 为 series 时有意义</li>
                <li>seriesIndex : 当前系列下标。当 componentType 为 series 时有意义</li>
                <li>seriesName : 当前系列名称。当 componentType 为 series 时有意义</li>
                <li>name : 数据名, 类目名</li>
                <li>data : 数据项</li>
                <li>value : 数据值</li>
              </ul>
            </div>
          </template>
        </WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';

export default {
  name: 'WidgetAmapConfiguration',
  props: { widget: Object, designer: Object },
  components: {},
  computed: {},
  data() {
    return {
      styleOptions: [
        {
          label: '标准',
          value: 'amap://styles/normal',
          comment: '默认标准地图样式'
        },
        {
          label: '幻影黑',
          value: 'amap://styles/dark',
          comment: '深色主题，适合夜间模式'
        },
        {
          label: '月光银',
          value: 'amap://styles/light',
          comment: '浅色主题，简洁风格'
        },
        {
          label: '远山黛',
          value: 'amap://styles/whitesmoke',
          comment: '蓝绿色调，清新风格'
        },
        {
          label: '草色青',
          value: 'amap://styles/fresh',
          comment: '灰绿色调，柔和风格'
        },
        {
          label: '雅士灰',
          value: 'amap://styles/grey',
          comment: '灰绿色调，柔和风格'
        },
        {
          label: '涂鸦',
          value: 'amap://styles/graffiti',
          comment: '卡通涂鸦风格'
        },
        {
          label: '马卡龙',
          value: 'amap://styles/macaron',
          comment: '明亮色块风格'
        },
        {
          label: '极夜蓝',
          value: 'amap://styles/darkblue',
          comment: '深蓝色调，科技感'
        },
        {
          label: '酱籽',
          value: 'amap://styles/wine',
          comment: '深蓝紫色调'
        },
        {
          label: '靛青蓝',
          value: 'amap://styles/blue',
          comment: '暗色系，低光污染'
        }
      ]
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    getPopupContainerByPs,
    getDropdownClassName
  },

  configuration(widget) {
    let conf = {
      jsModules: [],
      style: {
        height: 300
      },
      showSearch: false,
      toolBar: true,
      scale: true,
      geolocation: false,
      geocoder: false,
      mapOption: {
        zoom: 11, // 地图缩放级别，取值范围为3-20（2D视图）或3-18（3D视图），默认11级
        centerValue: undefined,
        centerDefault: false, // false当前位置，true为中心经纬度
        viewMode: '2D', // 地图视图模式，可选值："2D"（默认）或"3D"
        mapStyle: 'amap://styles/normal', // 地图样式，默认"normal"（标准样式），可选"dark"等
        autoFitView: false, // 是否自动适配视野（包含所有覆盖物），默认false
        zooms: [2, 20], // 支持的缩放级别范围，默认[2,20]（2D视图）
        dragEnable: true, // 是否允许地图拖拽，默认true
        zoomEnable: true, // 是否允许缩放，默认true
        features: ['bg', 'point', 'road', 'building'], // 显示的地图要素，默认显示背景、道路和建筑物
        defaultCursor: 'pointer' // 默认鼠标指针样式，默认"pointer"
      },
      markerOption: {}
    };

    if (widget != undefined && widget.useScope == 'bigScreen') {
      conf.style.width = 600;
      conf.legendConfig.textStyle.color = '#fff';
    }
    return conf;
  }
};
</script>
