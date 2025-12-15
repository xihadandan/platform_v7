<template>
  <div>
    <a-form-model ref="form" :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }">
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <DeviceVisible :widget="widget" />
          <a-form-model-item label="类型" class="item-lh">
            <a-radio-group size="small" v-model="widget.configuration.tipType" button-style="solid" @change="onTipTypeChange">
              <a-radio-button value="popup">弹出贴士</a-radio-button>
              <a-radio-button value="fixed">固定贴士</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="提示类型" v-show="widget.configuration.tipType === 'fixed'">
            <a-select :style="{ width: '100%' }" :options="typeOptions" v-model="widget.configuration.type" />
          </a-form-model-item>
          <a-form-model-item label="用户看过后不再提示" v-show="widget.configuration.tipType === 'fixed'">
            <a-switch v-model="widget.configuration.noTipAgain" />
          </a-form-model-item>
          <a-form-model-item label="是否关闭按钮" v-show="widget.configuration.tipType === 'fixed'">
            <a-switch v-model="widget.configuration.closable" />
          </a-form-model-item>
          <a-form-model-item v-show="widget.configuration.tipType === 'fixed'">
            <template slot="label">
              显示提示图标
              <a-checkbox v-model="widget.configuration.showIcon" />
            </template>
            <a-radio-group
              v-if="widget.configuration.showIcon"
              size="small"
              v-model="widget.configuration.showIconType"
              button-style="solid"
              @change="onShowIconTypeChange"
            >
              <a-radio-button value="icon">图标</a-radio-button>
              <a-radio-button value="tag">标签</a-radio-button>
            </a-radio-group>
            <div v-if="widget.configuration.showIconType === 'tag'">
              <a-tag :color="tagColorType[widget.configuration.type]">
                <a-input
                  size="small"
                  v-model="widget.configuration.tipTagText"
                  placeholder="请输入"
                  :style="{
                    width: '40px',
                    margin: '0px',
                    padding: '0px',
                    background: 'transparent',
                    'text-align': 'center',
                    height: '22px',
                    border: 'unset !important',
                    color: '#fff'
                  }"
                />
              </a-tag>
            </div>
          </a-form-model-item>
          <a-form-model-item
            v-if="widget.configuration.tipType === 'fixed' && widget.configuration.showIcon && widget.configuration.showIconType === 'icon'"
          >
            <template slot="label">
              自定义图标
              <a-checkbox v-model="widget.configuration.showIconCustom" />
            </template>
            <WidgetDesignModal
              v-if="widget.configuration.showIconCustom"
              title="选择图标"
              :zIndex="1000"
              :width="640"
              dialogClass="pt-modal widget-icon-lib-modal"
              :bodyStyle="{ height: '560px' }"
              :maxHeight="560"
              mask
              bodyContainer
            >
              <IconSetBadge v-model="widget.configuration.bodyIcon"></IconSetBadge>
              <template slot="content">
                <WidgetIconLib v-model="widget.configuration.bodyIcon" />
              </template>
            </WidgetDesignModal>
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">
              <a-select
                v-if="widget.configuration.tipType == 'fixed'"
                v-model="widget.configuration.bodyType"
                size="small"
                style="width: 65px"
              >
                <a-select-option value="static">静态</a-select-option>
                <a-select-option value="dynamic">动态</a-select-option>
              </a-select>
              提示内容
            </template>
            <div style="display: flex" v-if="widget.configuration.bodyType == 'static' || widget.configuration.bodyType == undefined">
              <a-textarea v-model="widget.configuration.body" :maxLength="200" allowClear />
              <WI18nInput
                style="padding: 0px 15px"
                :widget="widget"
                :designer="designer"
                :code="widget.id"
                v-model="widget.configuration.body"
              />
            </div>
            <template v-if="widget.configuration.tipType == 'fixed' && widget.configuration.bodyType == 'dynamic'">
              <!-- 动态内容配置 -->
              <a-radio-group v-model="widget.configuration.dynamicBodyFrom" button-style="solid" size="small" @change="onChangeBodyFrom">
                <a-radio-button value="dataSource">数据仓库</a-radio-button>
                <a-radio-button value="dataModel">数据模型</a-radio-button>
              </a-radio-group>
              <DataModelSelectModal
                v-if="widget.configuration.dynamicBodyFrom == 'dataModel'"
                v-model="widget.configuration.dataModelUuid"
                :dtype="['TABLE', 'VIEW']"
                :displayModal="true"
                ref="dataModelSelect"
                @change="onDataModelChange"
              />
              <DataStoreSelectModal v-else v-model="widget.configuration.dataSourceId" :displayModal="true" @change="changeDataSourceId" />
            </template>
          </a-form-model-item>
          <template
            v-if="
              widget.configuration.tipType == 'fixed' &&
              widget.configuration.bodyType == 'dynamic' &&
              columnIndexOptions.length > 0 &&
              (widget.configuration.dataSourceId != undefined || widget.configuration.dataModelUuid != undefined)
            "
          >
            <a-form-model-item label="内容列">
              <a-select
                v-if="
                  columnIndexOptions.length > 0 &&
                  (widget.configuration.dataSourceId != undefined || widget.configuration.dataModelUuid != undefined)
                "
                :options="columnIndexOptions"
                v-model="widget.configuration.bodyColumn"
                style="width: 100%"
                allow-clear
                placeholder="提示内容来自数据源列"
              />
            </a-form-model-item>
            <a-form-model-item label="默认条件">
              <a-textarea v-model="widget.configuration.defaultCondition" :maxLength="200" allowClear />
            </a-form-model-item>
            <a-form-model-item>
              <template slot="label">
                限制显示条数
                <a-checkbox v-model="widget.configuration.enableLimitSize" />
              </template>
              <a-input-number v-model="widget.configuration.limitSize" :min="1" :max="10000" />
            </a-form-model-item>
            <a-form-model-item label="滚动间隔时长">
              <a-input-number v-model="widget.configuration.interval" :min="3" :max="100" />
              秒
            </a-form-model-item>
          </template>
          <div style="padding: 0 12px">
            <ButtonsConfiguration
              :widget="widget"
              :designer="designer"
              :button="widget.configuration.rightButton"
              title="右侧按钮"
              :limitButtonCount="1"
              :showGroup="false"
            >
              <template slot="eventUrlHelpSlot">
                <a-popover placement="rightTop" :title="null">
                  <template slot="content">
                    支持通过
                    <a-tag>${ TIP_DATA.字段 }</a-tag>
                    设值到地址上, 字段来源动态数据仓库或者数据模型的列值
                  </template>
                  <a-button type="link" size="small"><Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon></a-button>
                </a-popover>
              </template>
              <template slot="eventParamValueHelpSlot">
                支持通过
                <a-tag>${ TIP_DATA.字段 }</a-tag>
                取值, 字段来源动态数据仓库或者数据模型的列值
              </template>
            </ButtonsConfiguration>
          </div>
          <DefaultVisibleConfiguration :designer="designer" :configuration="widget.configuration" :widget="widget" :compact="true" />

          <a-collapse :bordered="false" expandIconPosition="right">
            <a-collapse-panel key="styleSetting" header="样式设置">
              <a-form-model-item v-if="widget.configuration.tipType === 'fixed'">
                <template slot="label">
                  自定义背景色
                  <a-checkbox v-model="widget.configuration.enableCustomBgColor" />
                </template>
                <div v-show="widget.configuration.enableCustomBgColor">
                  <StyleColorTreeSelect
                    v-if="widget.configuration.useBgColorVar"
                    ref="colorTreeSelect"
                    :showPreview="false"
                    :dropdownStyle="{
                      width: 'auto',
                      maxWidth: '400px'
                    }"
                    placeholder="内置颜色变量"
                    :colorConfig="colorConfigOptions"
                    :autoDisplay="true"
                    style="width: 102px"
                    v-model="widget.configuration.backgroundColorVar"
                  />
                  <ColorPicker v-else v-model="widget.configuration.backgroundColor" :width="120" :allowClear="true" />
                  <a-checkbox v-model="widget.configuration.useBgColorVar">使用颜色变量</a-checkbox>
                </div>
              </a-form-model-item>
              <a-form-model-item v-if="widget.configuration.tipType === 'fixed'">
                <template slot="label">
                  自定义字体颜色
                  <a-checkbox v-model="widget.configuration.enableCustomFontColor" />
                </template>
                <div v-show="widget.configuration.enableCustomFontColor">
                  <StyleColorTreeSelect
                    v-if="widget.configuration.useFontColorVar"
                    ref="fontColorTreeSelect"
                    :showPreview="false"
                    :dropdownStyle="{
                      width: 'auto',
                      maxWidth: '400px'
                    }"
                    placeholder="内置颜色变量"
                    :colorConfig="colorConfigOptions"
                    :autoDisplay="true"
                    style="width: 102px"
                    v-model="widget.configuration.fontColorVar"
                  />
                  <ColorPicker v-else v-model="widget.configuration.fontColor" :width="120" :allowClear="true" />
                  <a-checkbox v-model="widget.configuration.useFontColorVar">使用颜色变量</a-checkbox>
                </div>
              </a-form-model-item>
              <a-form-model-item label="边框" v-show="widget.configuration.tipType === 'fixed'">
                <a-switch v-model="widget.configuration.banner" />
              </a-form-model-item>
              <a-form-model-item v-if="widget.configuration.tipType === 'fixed' && widget.configuration.banner">
                <template slot="label">
                  自定义边框颜色
                  <a-checkbox v-model="widget.configuration.enableCustomBorderColor" />
                </template>
                <div v-show="widget.configuration.enableCustomBorderColor">
                  <StyleColorTreeSelect
                    v-if="widget.configuration.useBorderColorVar"
                    ref="colorTreeSelect"
                    :showPreview="false"
                    :dropdownStyle="{
                      width: 'auto',
                      maxWidth: '400px'
                    }"
                    placeholder="内置颜色变量"
                    :colorConfig="colorConfigOptions"
                    :autoDisplay="true"
                    style="width: 102px"
                    v-model="widget.configuration.borderColorVar"
                  />
                  <ColorPicker v-else v-model="widget.configuration.borderColor" :width="120" :allowClear="true" />
                  <a-checkbox v-model="widget.configuration.useBorderColorVar">使用颜色变量</a-checkbox>
                </div>
              </a-form-model-item>
              <BorderRadiusSet label="圆角" field="borderRadius" :target="widget.configuration" hideSilder :max="50" />
              <BorderRadiusSet
                v-if="designer.terminalType == 'mobile'"
                label="内边距"
                field="padding"
                :target="widget.configuration"
                hideSilder
              />
              <BorderRadiusSet label="外边距" field="margin" :target="widget.configuration" hideSilder :min="-50" :max="50" />
            </a-collapse-panel>
          </a-collapse>
        </a-tab-pane>

        <a-tab-pane key="2" tab="事件设置">
          <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
import StyleColorTreeSelect from '@pageAssembly/app/web/page/theme-designer/component/design/lib/style-color-tree-select.vue';
import ButtonsConfiguration from '../../commons/buttons-configuration/index.vue';

export default {
  name: 'WidgetTipConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      colorConfigJson: undefined,
      columnIndexOptions: [],
      tagColorType: {
        info: '#108ee9',
        success: '#87d068',
        warning: '#fa8c16',
        error: '#f5222d'
      },
      typeOptions: [
        { label: '信息', value: 'info' },
        { label: '成功', value: 'success' },
        { label: '警告', value: 'warning' },
        { label: '错误', value: 'error' }
      ]
    };
  },
  components: { StyleColorTreeSelect, ButtonsConfiguration },
  computed: {
    colorConfigOptions() {
      if (this.colorConfig) {
        return this.colorConfig;
      }
      return this.colorConfigJson;
    }
  },
  created() {
    if (!this.widget.configuration.hasOwnProperty('rightButton')) {
      this.$set(this.widget.configuration, 'rightButton', {});
    }
  },
  methods: {
    onShowIconTypeChange() {
      // if (!this.widget.configuration.hasOwnProperty('tipTagText')) {
      //   this.$set(
      //     this.widget.configuration,
      //     'tipTagText',
      //     { info: '信息', success: '成功', warning: '警告', error: '错误' }[this.widget.configuration.type]
      //   );
      // }
    },
    onChangeBodyFrom() {
      this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
    },
    changeDataSourceId() {
      this.fetchColumns();
    },
    fetchDataModelDetails(uuid, callback) {
      if (uuid) {
        $axios.get(`/proxy/api/dm/getDetails`, { params: { uuid } }).then(({ data, headers }) => {
          if (data.code == 0) {
            this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
            let detail = data.data,
              columns = JSON.parse(detail.columnJson);
            for (let col of columns) {
              if (detail.type == 'TABLE' || (detail.type == 'VIEW' && col.hidden !== true && col.return === true)) {
                this.columnIndexOptions.push({
                  value: col.alias || col.column,
                  label: col.title,
                  isSysDefault: col.isSysDefault
                });
              }
            }
            if (typeof callback == 'function') {
              callback.call(this);
            }
          }
        });
      }
    },
    onDataModelChange(uuid, { id, name }) {
      this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
      this.fetchDataModelDetails(uuid, () => {});
    },
    fetchColumns() {
      this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
      $axios
        .post('/json/data/services', {
          serviceName: 'viewComponentService',
          methodName: 'getColumnsById',
          args: JSON.stringify([this.widget.configuration.dataSourceId])
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
            for (let i = 0, len = data.data.length; i < len; i++) {
              this.columnIndexOptions.push({
                value: data.data[i].columnIndex,
                label: data.data[i].title
              });
            }
          }
        });
    },
    fetchThemeSpecify() {
      $axios
        .get(`/proxy/api/theme/specify/getEnabled`, { params: {} })
        .then(({ data }) => {
          console.log('获取主题规范', data);
          if (data.code == 0) {
            let specifyDefJson = JSON.parse(data.data.defJson);
            this.colorConfigJson = specifyDefJson.colorConfig;
          }
        })
        .catch(error => {});
    },
    onTipTypeChange() {
      this.widget.configuration.style.block = this.widget.configuration.tipType !== 'popup';
      if (this.widget.configuration.tipType == 'fixed') {
        if (!this.widget.configuration.hasOwnProperty('bodyType')) {
          this.$set(this.widget.configuration, 'bodyType', 'static');
        }
      }
    }
  },
  beforeMount() {},
  mounted() {
    this.fetchThemeSpecify();
    if (this.widget.configuration.dataSourceId) {
      this.fetchColumns();
    }
    if (this.widget.configuration.dataModelUuid && this.widget.configuration.dynamicBodyFrom == 'dataModel') {
      this.fetchDataModelDetails(this.widget.configuration.dataModelUuid);
    }
  },
  configuration() {
    return {
      tipType: 'popup',
      body: '',
      type: 'warning',
      noTipAgain: false,
      closable: false,
      showIcon: true,
      enableCustomBgColor: false,
      useBgColorVar: false,
      backgroundColor: undefined,
      backgroundColorVar: undefined,
      dataSourceId: undefined,
      dataModelUuid: undefined,
      dynamicBody: false,
      showIconType: 'icon',
      dynamicBodyFrom: 'dataSource', // dataSource: 数据源  dataModel: 数据模型
      style: {
        block: false
      }
    };
  }
};
</script>
