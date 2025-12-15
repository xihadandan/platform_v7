<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <FieldNameInput :widget="widget" />
        <FieldCodeInput :widget="widget" />
        <FieldDefaultValue :configuration="widget.configuration">
          <WidgetDesignModal title="默认值设置" :zIndex="1000" :width="600" slot="define">
            <a-button title="默认值设置">
              <Icon type="pticon iconfont icon-ptkj-shezhi" />
            </a-button>
            <template slot="content">
              <MarkerMap v-model="widget.configuration.defaultValue" valueString />
            </template>
          </WidgetDesignModal>
        </FieldDefaultValue>
        <a-form-model-item></a-form-model-item>
        <a-form-model-item label="默认状态">
          <a-radio-group size="small" v-model="widget.configuration.defaultDisplayState">
            <a-radio-button value="edit">可编辑</a-radio-button>
            <a-radio-button value="unedit">不可编辑</a-radio-button>
            <a-radio-button value="hidden">隐藏</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-collapse :bordered="false" expandIconPosition="right">
          <!-- 编辑模式属性 -->
          <a-collapse-panel key="editMode" header="编辑模式属性">
            <a-form-model-item label="显示搜索框">
              <a-switch v-model="widget.configuration.showSearch" />
            </a-form-model-item>
            <a-form-model-item label="显示清空按钮">
              <a-switch v-model="widget.configuration.allowClear" />
            </a-form-model-item>
            <a-form-model-item label="定位按钮">
              <a-switch v-model="widget.configuration.showGeolocation" />
            </a-form-model-item>
            <a-form-model-item label="弹框打开">
              <a-switch v-model="widget.configuration.showModal" />
            </a-form-model-item>
            <template v-if="widget.configuration.showModal">
              <a-form-model-item label="按钮配置">
                <WidgetDesignDrawer :id="'fieldMappingConfigure' + widget.id" title="事件处理" :designer="designer">
                  <a-button>按钮配置</a-button>
                  <template slot="content">
                    <AdminSettingButtonConfiguration
                      :button="widget.configuration.modalBtn"
                      :hasHandler="false"
                      ref="modalBtnConfigurationRef"
                      hideParams="code"
                    >
                      <template #textAfter>
                        <w-i18n-input :widget="widget" :designer="designer" code="btnText" v-model="widget.configuration.modalBtn.text" />
                      </template>
                    </AdminSettingButtonConfiguration>
                  </template>
                </WidgetDesignDrawer>
              </a-form-model-item>
            </template>
          </a-collapse-panel>
          <!-- 不可编辑模式属性 -->
          <a-collapse-panel key="unEditMode" header="不可编辑模式属性">
            <a-form-model-item label="不可编辑状态" :label-col="{ span: 8 }" :wrapper-col="{ span: 16 }">
              <a-radio-group size="small" v-model="widget.configuration.uneditableDisplayState">
                <a-radio-button value="label">纯文本</a-radio-button>
                <a-radio-button value="readonly">只读(显示组件样式)</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
          </a-collapse-panel>
          <a-collapse-panel key="mapOptions" header="地图配置">
            <a-form-model-item label="中心点">
              <a-radio-group size="small" v-model="widget.configuration.mapOption.centerDefault">
                <a-radio-button :value="false">默认</a-radio-button>
                <a-radio-button :value="true">自定义</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
            <template v-if="widget.configuration.mapOption.centerDefault">
              <a-form-model-item label="中心点设置">
                <WidgetDesignModal title="中心点设置" :zIndex="1000" :width="600">
                  <a-button title="中心点设置">
                    <Icon type="pticon iconfont icon-ptkj-shezhi" />
                  </a-button>
                  <template slot="content">
                    <MarkerMap v-model="widget.configuration.mapOption.centerValue" />
                  </template>
                </WidgetDesignModal>
              </a-form-model-item>
            </template>
            <a-form-model-item label="缩放">
              <a-input-number v-model="widget.configuration.mapOption.zoom" style="width: 100px" :min="4" :max="20" />
            </a-form-model-item>
            <!-- <LocationSelect v-model="widget.configuration.city" formLabel="城市" /> -->
            <a-form-model-item label="高度">
              <a-input-group compact>
                <a-input-number v-model="widget.configuration.style.height" style="width: 100px" :min="1" />
                <a-button>px</a-button>
              </a-input-group>
            </a-form-model-item>
          </a-collapse-panel>
          <a-collapse-panel key="markerOptions" header="点标记配置">
            <a-form-model-item label="可拖拽">
              <a-switch v-model="widget.configuration.markerOption.draggable" />
            </a-form-model-item>
            <a-form-model-item label="标记区域">
              <a-switch v-model="widget.configuration.markerOption.hasMarkerArea" />
            </a-form-model-item>
            <template v-if="widget.configuration.markerOption.hasMarkerArea">
              <a-form-model-item label="标记区域图形">
                <a-radio-group size="small" v-model="widget.configuration.markerOption.markerAreaType">
                  <a-radio-button value="circle">圆形</a-radio-button>
                  <a-radio-button value="rectangle">矩形</a-radio-button>
                  <a-radio-button value="polygon">多边形</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="标记区域设置">
                <WidgetDesignModal title="标记区域设置" :zIndex="1000" :width="600">
                  <a-button title="标记区域设置">
                    <Icon type="pticon iconfont icon-ptkj-shezhi" />
                  </a-button>
                  <template slot="content">
                    <MarkerMap
                      v-model="widget.configuration.markerOption.markerArea"
                      :drawType="widget.configuration.markerOption.markerAreaType"
                    />
                  </template>
                </WidgetDesignModal>
              </a-form-model-item>
            </template>
          </a-collapse-panel>
          <!-- 其它属性 -->
          <a-collapse-panel key="otherProp" header="其它属性">
            <a-form-model-item label="字段映射">
              <WidgetDesignDrawer :id="'fieldMappingConfigure' + widget.id" title="事件处理" :designer="designer">
                <a-button>添加字段映射</a-button>
                <template slot="content">
                  <WidgetFormSelectFieldMappingConfiguration
                    :widget="widget"
                    ref="fieldMapping"
                    :columnIndexOptions="geocoderAddressParams"
                    :designer="designer"
                  />
                </template>
              </WidgetDesignDrawer>
            </a-form-model-item>
            <a-form-model-item label="描述" :wrapper-col="{ style: { marginTop: '2px' } }">
              <a-textarea :rows="4" placeholder="请输入内容" v-model="widget.configuration.note" :maxLength="200" />
              <span class="textLengthShow">{{ widget.configuration.note | textLengthFilter }}/200</span>
            </a-form-model-item>
          </a-collapse-panel>
        </a-collapse>
      </a-tab-pane>
      <a-tab-pane key="2" tab="校验规则">
        <ValidateRuleConfiguration
          ref="validateRef"
          :widget="widget"
          :trigger="true"
          :required="true"
          :unique="true"
          :regExp="true"
          :validatorFunction="true"
        >
          <template slot="validatorFunctionHelp">
            <div>
              必须使用
              <a-tag>callback</a-tag>
              函数返回校验结果，例如：
            </div>
            <ul>
              <li>
                校验通过，
                <a-tag>callback();</a-tag>
              </li>
              <li>
                校验不通过，
                <a-tag>callback('这是校验不通过的提示信息');</a-tag>
              </li>
            </ul>
          </template>
        </ValidateRuleConfiguration>
      </a-tab-pane>
      <a-tab-pane key="3" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer">
          <template slot="eventParamValueHelpSlot"><FormEventParamHelp /></template>
        </WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import formConfigureMixin from '../../mixin/formConfigure.mixin';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
import MarkerMap from './markerMap.vue';
import LocationSelect from '../../commons/location-select.vue';
import { geocoderAddressParams } from './contants';
import AdminSettingButtonConfiguration from '@admin/app/web/template/common/button-configuration-admin';

export default {
  mixins: [formConfigureMixin],
  name: 'WidgetFormAmapConfiguration',
  props: { widget: Object, designer: Object },
  components: { MarkerMap, LocationSelect, AdminSettingButtonConfiguration },
  filters: {
    textLengthFilter(text) {
      return text ? text.length : 0;
    }
  },
  computed: {},
  data() {
    return {
      geocoderAddressParams
    };
  },
  beforeCreate() {},
  created() {
    if (!this.widget.configuration.modalBtn) {
      this.$set(this.widget.configuration, 'modalBtn', {
        text: '选择',
        code: '',
        style: {
          icon: '',
          textHidden: false,
          type: ''
        }
      });
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
    cityTreeDataGet() {}
  },
  watch: {},
  configuration(widget) {
    let conf = {
      code: '',
      name: '',
      length: 120,
      isLabelValueWidget: true,
      isDatabaseField: true,
      syncLabel2FormItem: true,
      optionDataAutoSet: false,
      defaultDisplayState: 'edit',
      uneditableDisplayState: 'label',
      validateRule: { trigger: 'change', regExp: {} },
      displayValueField: undefined,
      style: {
        height: 300
      },
      showModal: false,
      modalBtn: {
        text: '选择',
        code: '',
        style: {
          icon: '',
          textHidden: false,
          type: ''
        }
      },
      showSearch: false,
      allowClear: false,
      showGeolocation: false,
      city: '中国',
      options: {
        dataSourceDataMapping: []
      },
      mapOption: {
        zoom: 11, // 地图缩放级别，取值范围为3-20（2D视图）或3-18（3D视图），默认11级
        centerValue: undefined,
        centerDefault: false, // false当前位置，true为中心经纬度
        viewMode: '2D' // 地图视图模式，可选值："2D"（默认）或"3D"
      },
      markerOption: {
        addMarker: true,
        delMarker: true,
        hasMarkerArea: false,
        markerAreaType: 'circle',
        markerArea: null,
        draggable: true
      }
    };

    if (widget != undefined && widget.useScope == 'bigScreen') {
      conf.style.width = 600;
      conf.legendConfig.textStyle.color = '#fff';
    }
    return conf;
  }
};
</script>
