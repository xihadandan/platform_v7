<template>
  <a-form-model-item
    v-show="!hidden"
    :prop="formModelItemProp"
    :rules="rules"
    :ref="widget.configuration.code"
    :label="itemLabel"
    :colon="displayAsLabel"
    :class="widgetClass"
  >
    <template v-if="!displayAsLabel">
      <template v-if="widget.configuration.showModal">
        <Modal :title="$t('btnText', modalBtn.text)" forceRender>
          <template slot="content">
            <div class="flex f_x_s" style="margin-bottom: 8px" v-if="showHeader">
              <div>
                <!-- 搜索框 -->
                <a-auto-complete
                  v-if="showSearch"
                  v-model="keyword"
                  backfill
                  allowClear
                  style="width: 100%"
                  :placeholder="$t('WidgetFormAmap.searchPlaceholder', '输入地点关键词')"
                  optionLabelProp="label"
                  :dropdownMatchSelectWidth="false"
                  dropdownClassName="amap-search-input-dropdown ps__child--consume"
                  :getPopupContainer="getPopupContainer()"
                  @search="handleSearch"
                  @select="handlePoiSelect"
                  class="amap-search-input"
                >
                  <template slot="dataSource">
                    <a-select-option v-for="item in searchOptions" :key="item.key" :value="item.value">
                      <div class="tip-item">
                        <span class="tip-name">{{ item.text }}</span>
                        <span class="tip-address">{{ item.district }}</span>
                      </div>
                    </a-select-option>
                  </template>
                </a-auto-complete>
              </div>
              <div>
                <a-button type="primary" @click="clearClick" v-if="allowClear">{{ $t('WidgetFormAmap.clear', '清除') }}</a-button>
              </div>
            </div>
            <div
              :style="{
                height: vContainerHeight,
                width: vContainerWidth
              }"
              :id="id"
            ></div>
            <div class="spin-center" v-if="loading">
              <a-spin />
            </div>
            <div v-if="$refs[fieldCode] && $refs[fieldCode].validateMessage" class="has-error">
              <div class="ant-form-explain">{{ $refs[fieldCode] && $refs[fieldCode].validateMessage }}</div>
            </div>
          </template>
          <a-button
            :shape="modalBtn.style.shape"
            :type="modalBtn.style.type"
            :size="modalBtn.style.size"
            :title="modalBtn.style.textHidden == true ? $t('btnText', modalBtn.text) : ''"
          >
            <Icon :type="modalBtn.style.icon" v-if="modalBtn.style.icon" />
            {{ modalBtn.style.textHidden == true ? '' : $t('btnText', modalBtn.text) }}
          </a-button>
        </Modal>
      </template>
      <template v-else>
        <div class="flex f_x_s" style="margin-bottom: 8px" v-if="showHeader">
          <div>
            <!-- 搜索框 -->
            <a-auto-complete
              v-if="showSearch"
              v-model="keyword"
              backfill
              allowClear
              style="width: 100%"
              :placeholder="$t('WidgetFormAmap.searchPlaceholder', '输入地点关键词')"
              optionLabelProp="label"
              :dropdownMatchSelectWidth="false"
              dropdownClassName="amap-search-input-dropdown ps__child--consume"
              :getPopupContainer="getPopupContainer()"
              @search="handleSearch"
              @select="handlePoiSelect"
              class="amap-search-input"
            >
              <template slot="dataSource">
                <a-select-option v-for="item in searchOptions" :key="item.key" :value="item.value">
                  <div class="tip-item">
                    <span class="tip-name">{{ item.text }}</span>
                    <span class="tip-address">{{ item.district }}</span>
                  </div>
                </a-select-option>
              </template>
            </a-auto-complete>
          </div>
          <div>
            <a-button type="primary" @click="clearClick" v-if="allowClear">{{ $t('WidgetFormAmap.clear', '清除') }}</a-button>
          </div>
        </div>
        <div
          :style="{
            height: vContainerHeight,
            width: vContainerWidth
          }"
          :id="id"
        ></div>
        <div class="spin-center" v-if="loading">
          <a-spin />
        </div>
      </template>
      <div v-show="false">
        <a-card v-if="searchInfoData" ref="searchInfoDataRef" class="widget-map-info-card" :title="searchInfoData.name">
          <div>{{ $t('WidgetFormAmap.address', '地址') }}:{{ searchInfoData.address }}</div>
        </a-card>
      </div>
    </template>
    <span v-if="displayAsLabel" class="textonly" :title="displayValue()">{{ displayValue() }}</span>
  </a-form-model-item>
</template>
<script>
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import formMixin from '../mixin/form-common.mixin';
import amapMixin from '@pageAssembly/app/web/widget/widget-amap/amap.mixin.js';
import { deepClone } from '@framework/vue/utils/util';
import { isArray, each } from 'lodash';
import Modal from '@pageAssembly/app/web/lib/modal.vue';

export default {
  name: 'WidgetFormAmap',
  extends: FormElement,
  mixins: [widgetMixin, formMixin, amapMixin],
  components: {
    Modal
  },
  data() {
    let showSearch = this.widget.configuration.showSearch;
    let showGeolocation = this.widget.configuration.showGeolocation;
    return {
      marker: undefined,
      geocoder: undefined,
      customOptions: {
        isDesigner: this.designMode,
        geocoder: true, //  逆地理编码
        showSearch,
        geolocation: showGeolocation,
        geolocationShowMarker: false // 定位不显示点标记
      },
      showSearch,
      showGeolocation,
      hasMarkerArea: this.widget.configuration.markerOption.hasMarkerArea,
      markerAreaType: this.widget.configuration.markerOption.markerAreaType,
      areaBounds: undefined //  区域范围
    };
  },
  computed: {
    modalBtn() {
      return this.widget.configuration.modalBtn || { text: '选择', style: {} };
    },
    showHeader() {
      return this.allowClear || this.showSearch;
    },
    allowClear() {
      let draggable = this.markerDraggable;
      return this.disable || this.readonly ? false : this.widget.configuration.allowClear;
    },
    dataSourceDataMapping() {
      return this.widget.configuration.options.dataSourceDataMapping;
    },
    markerDraggable() {
      let draggable = this.disable || this.readonly ? false : this.widget.configuration.markerOption.draggable;
      if (this.marker) {
        this.marker.setDraggable(draggable);
      }
      return draggable;
    },
    defaultEvents() {
      return [
        {
          id: 'getLocation',
          title: '根据详细地址定位',
          eventParams: [
            {
              paramKey: 'address',
              remark: '详细地址'
            }
          ]
        }
      ];
    }
  },
  created() {},
  mounted() {},
  methods: {
    // 地图加载完成
    mapLoaded() {
      if (!this.designMode) {
        this.geocoder = new AMap.Geocoder({
          city: this.widget.configuration.city || '中国', // city 指定进行编码查询的城市，支持传入城市名、adcode 和 citycode
          lang: this.locale
        });
        this.initMarkerArea();
        if (this.formData[this.fieldCode]) {
          this.setMarker(this.formData[this.fieldCode]);
        }
        this.map.on('click', e => {
          this.mapClick(e);
        });
      }
    },
    // 点击地图事件
    mapClick(e) {
      // 不可编辑
      if (!(this.disable || this.readonly)) {
        this.setMarker(e.lnglat);
        this.closeInfoWindow();
      }
    },
    setValue(value) {
      this.formData[this.fieldCode] = value;
      if (this.AMap) {
        this.setMarker(value);
      }
    },
    // 设置点标记
    setMarker(value) {
      if (value) {
        if (typeof value === 'string') {
          value = JSON.parse(value);
        }
        if (!this.marker) {
          this.marker = new this.AMap.Marker({
            position: value,
            draggable: this.markerDraggable
            // icon: '/static/js/theme/images/v1_alert.png'
          });
          this.map.add(this.marker);
          this.marker.on('dragend', e => {
            this.setMarkerAfter(e.lnglat);
            this.closeInfoWindow();
          });
          this.marker.on('click', e => {
            let lnglat = this.marker.getPosition();
            let content = `<div class="ant-card"><div class="ant-card-body">
              ${this.$t('WidgetFormAmap.lng', '经度')}：${lnglat.lng}
                ${this.$t('WidgetFormAmap.lat', '纬度')}：${lnglat.lat}
            </div></div>`;
            let infoWindow = this.createInfoWindow(content);
            this.openInfoWindow(infoWindow, this.marker);
          });
        } else {
          this.marker.setPosition(value);
        }
        this.setMarkerAfter(value);
        this.map.setCenter(value);
      }
    },
    setMarkerAfter(value) {
      if (typeof value === 'string') {
        value = JSON.parse(value);
      }
      this.formData[this.fieldCode] = JSON.stringify(value);
      this.getAddress();
      this.emitChange();
    },
    // 通过详细地址获取坐标
    getLocation(options) {
      if (options) {
        let address = '';
        if (typeof options === 'object' && options.eventParams) {
          address = options.eventParams.address;
        } else if (typeof options === 'string') {
          address = options;
        }
        if (address) {
          this.geocoder.getLocation(address, (status, result) => {
            if (status === 'complete' && result.info === 'OK') {
              // result中对应详细地理坐标信息
              console.log(result);
              if (result.geocodes.length > 0) {
                this.setMarker(result.geocodes[0].location);
              }
            }
          });
        }
      }
    },
    // 通过坐标获取地址
    getAddress() {
      if (this.marker) {
        let lnglat = this.marker.getPosition();
        this.geocoder.getAddress(lnglat, (status, result) => {
          if (status === 'complete' && result.info === 'OK') {
            // result为对应的地理位置详细信息
            result.regeocode.lnglat = lnglat;
            console.log(result);
            this.setDataMaping(result.regeocode);
          }
        });
      }
    },
    // 给映射字段设值
    setDataMaping(regeocode) {
      // 设置关联值
      if (this.dataSourceDataMapping.length > 0) {
        for (let i = 0, len = this.dataSourceDataMapping.length; i < len; i++) {
          let sourceField = this.dataSourceDataMapping[i].sourceField;
          let targetField = this.dataSourceDataMapping[i].targetField;
          let value = '';
          if (sourceField) {
            if (sourceField.indexOf('_') > -1) {
              sourceField = sourceField.split('_');
              value = regeocode ? regeocode[sourceField[0]][sourceField[1]] : null;
            } else {
              if (sourceField == 'lnglat') {
                value = regeocode ? [regeocode.lnglat.lng, regeocode.lnglat.lat] : null;
              } else {
                value = regeocode ? regeocode[sourceField] : null;
              }
            }
          }
          if (value && typeof value === 'object') {
            value = JSON.stringify(value);
          }
          if (targetField) {
            this.form.setFieldValue(targetField, value || null);
          }
        }
      }
    },
    // 清空地图覆盖物
    clearClick() {
      if (this.marker) {
        this.map.remove(this.marker);
        this.marker = null;
      }
      this.formData[this.fieldCode] = null;
      this.setDataMaping();
      this.emitChange();
    },
    initMarkerArea() {
      if (this.hasMarkerArea) {
        let value = deepClone(this.widget.configuration.markerOption.markerArea);
        if (typeof value === 'string') {
          value = JSON.parse(value);
        }
        switch (this.markerAreaType) {
          case 'rectangle': {
            if (value && value.northEast) {
              // 创建矩形边界对象
              this.areaBounds = new this.AMap.Bounds(value.southWest, value.northEast);
            }
            break;
          }
          case 'circle': {
            if (value && value.center && value.radius) {
              // 创建圆形对象
              this.areaBounds = new this.AMap.Circle({
                center: value.center,
                radius: value.radius
              });
              this.map.add(this.areaBounds);
              this.areaBounds.hide();
            }
            break;
          }
          case 'polygon': {
            if (value && isArray(value) && value.length) {
              // 创建多边形对象
              this.areaBounds = new this.AMap.Polygon({
                path: value
              });
              this.map.add(this.areaBounds);
              this.areaBounds.hide();
            }
            break;
          }
          default: {
            break;
          }
        }
      }
    },
    addCustomRules() {
      return this.addCustomErrorRules();
    },
    // 增加附件上传失败校验
    addCustomErrorRules() {
      let custom_rule = {
        trigger: 'change',
        validator: (rule, value, callback) => {
          if (value && this.hasMarkerArea && this.areaBounds) {
            let _value = JSON.parse(value);
            // 要判断的点
            const point = new this.AMap.LngLat(_value[0], _value[1]);

            const isInside = this.areaBounds.contains(point);
            // 判断点是否在区域内
            if (!isInside) {
              callback(this.$t('WidgetFormAmap.noInside', '当前位置不在范围内'));
              return;
            }
          }
          callback();
        }
      };
      if (this.rules) {
        this.rules.push(custom_rule);
      }
      return custom_rule;
    },
    displayValue() {
      return this.formData[this.widget.configuration.code];
    },
    getPopupContainer() {
      return triggerNode => {
        if (triggerNode.closest('.ps')) {
          if (triggerNode.closest('.ps').clientHeight < 300) {
            // 页面高度小于400时，挂载到body下
            return document.body;
          } else {
            return triggerNode.closest('.ps');
          }
        } else if (triggerNode.closest('.widget-subform')) {
          return triggerNode.closest('.widget-subform');
        }
        return triggerNode.parentNode;
      };
    }
  },
  watch: {
    displayAsLabel(val, oldval) {
      if (oldval && !val) {
        // 从文本转为地图
        this.initAMap();
      } else if (!oldval && val) {
        // 从地图转为文本
        this.marker = null;
      }
    }
  }
};
</script>
<style scoped lang="less">
#container {
  width: 100%;
  height: 800px;
}
.amap-search-input {
  --w-input-error-border-color: var(--w-input-border-color);
  --w-input-error-border-color-focus: var(--w-input-border-color-focus);
  --w-input-error-border-color-hover: var(--w-input-border-color-hover);
}
.amap-search-input-dropdown {
  .tip-address {
    font-size: 12px;
    color: var(--w-text-color-light);
  }
}
</style>
