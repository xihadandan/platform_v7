<template>
  <div>
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
          <a-form-model-item label="名称">
            <a-input v-model="widget.title" allowClear />
          </a-form-model-item>
          <a-form-model-item label="标题">
            <a-input v-model="widget.configuration.title" allow-clear>
              <template slot="addonAfter">
                <WI18nInput
                  :widget="widget"
                  :designer="designer"
                  :target="widget.configuration"
                  code="title"
                  v-model="widget.configuration.title"
                />
              </template>
            </a-input>
          </a-form-model-item>

          <a-form-model-item label="每行列数">
            <a-input-number v-model="widget.configuration.column" allowClear :min="1" />
          </a-form-model-item>
          <a-form-model-item label="列间距">
            <div style="display: flex">
              <a-slider v-model="widget.configuration.gutter" :min="0" :max="24" style="width: 270px" />
              <a-input-number v-model="widget.configuration.gutter" :min="0" :max="24" allowClear />
            </div>
          </a-form-model-item>
          <a-form-model-item label="内边距">
            <div style="display: flex">
              <a-input v-model="widget.configuration.padding" allowClear />
            </div>
          </a-form-model-item>

          <a-form-model-item label="网格样式">
            <WidgetDesignDrawer :id="'widget-grid-info-item-style' + widget.id" title="编辑样式" :designer="designer">
              <a-button size="small" type="link">
                <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                设置
              </a-button>
              <template slot="content">
                <ItemStyleConfiguration :widget="widget" :designer="designer" :colorConfig="colorConfig" />
              </template>
            </WidgetDesignDrawer>
          </a-form-model-item>
          <a-form-model-item label="数据内容样式">
            <WidgetDesignDrawer :id="'widget-grid-info-avatar-style' + widget.id" title="编辑样式" :designer="designer">
              <a-button size="small" type="link">
                <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                设置
              </a-button>
              <template slot="content">
                <a-collapse
                  accordion
                  :defaultActiveKey="['contentStyle_1']"
                  :bordered="false"
                  expandIconPosition="right"
                  style="background: #fff"
                >
                  <a-collapse-panel key="contentStyle_1" header="头像样式">
                    <TitleAvatarStyleConfiguration :widget="widget" :designer="designer" :colorConfig="colorConfig" />
                  </a-collapse-panel>
                  <a-collapse-panel key="contentStyle_3" header="标题样式">
                    <TitleStyleConfiguration :widget="widget" :designer="designer" :colorConfig="colorConfig" />
                  </a-collapse-panel>
                  <a-collapse-panel key="contentStyle_2" header="徽章样式">
                    <BadgeStyleConfiguration :widget="widget" :designer="designer" :colorConfig="colorConfig" />
                  </a-collapse-panel>
                </a-collapse>
              </template>
            </WidgetDesignDrawer>
          </a-form-model-item>
          <a-form-model-item label="数据源">
            <a-radio-group v-model="widget.configuration.buildType" button-style="solid" size="small" @change="onChangeBuildType(true)">
              <a-radio-button value="define">自定义</a-radio-button>
              <a-radio-button value="dataSource">数据仓库</a-radio-button>
              <a-radio-button value="dataModel">数据模型</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <div v-if="widget.configuration.buildType == 'define'" style="padding: 0 12px">
            <GridItemInfoTableConfiguration :widget="widget" :designer="designer" />
          </div>
          <template v-else>
            <a-form-model-item label="自动国际化翻译">
              <a-switch v-model="widget.configuration.autoTranslate" />
            </a-form-model-item>
            <div v-show="widget.configuration.buildType == 'dataModel'">
              <a-form-model-item label="数据模型选择">
                <DataModelSelectModal
                  ref="dataModelSelect"
                  v-model="widget.configuration.dataModelUuid"
                  :dtype="['TABLE', 'VIEW']"
                  @change="onChangeDataModelSelect"
                />
              </a-form-model-item>
            </div>
            <a-form-model-item label="数据仓库查询" v-if="widget.configuration.buildType == 'dataSource'">
              <DataStoreSelectModal v-model="widget.configuration.dataSourceId" :displayModal="true" @change="onChangeSelectDataSource" />
            </a-form-model-item>
            <a-form-model-item>
              <template slot="label">
                <a-popover title="支持以下系统变量" placement="left" :mouseEnterDelay="0.5">
                  <template slot="content">
                    <ol>
                      <li>:currentUserName : 当前用户名</li>
                      <li>:currentLoginName : 当前用户登录名</li>
                      <li>:currentUserId : 当前用户ID</li>
                      <li>:currentSystem : 当前系统</li>
                      <li>:currentUserDepartmentId : 当前用户主部门ID</li>
                      <li>:currentUserDepartmentName : 当前用户主部门名称</li>
                      <li>:currentUserUnitId : 当前用户归属单位ID</li>
                      <li>:sysdate : 系统当前时间</li>
                    </ol>
                  </template>
                  <label>
                    默认查询条件
                    <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                  </label>
                </a-popover>
              </template>
              <a-textarea
                placeholder="默认查询条件"
                v-model="widget.configuration.defaultCondition"
                allowClear
                :autoSize="{ minRows: 3, maxRows: 6 }"
              />
            </a-form-model-item>
            <template v-for="(option, i) in columnMappingOptions">
              <a-form-model-item :label="option.label" :key="'colMap_' + i">
                <a-select
                  v-model="widget.configuration[option.value]"
                  show-search
                  style="width: 100%"
                  :filter-option="filterSelectOption"
                  :options="columnIndexOptions"
                  allow-clear
                  @change="onChangeColMappingSelect"
                ></a-select>
              </a-form-model-item>
            </template>
          </template>
        </a-form-model>
      </a-tab-pane>
      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer">
          <template slot="eventParamValueHelpSlot">
            <div style="width: 600px">
              <p>
                1. 支持通过
                <a-tag>${ 参数 }</a-tag>
                表达式解析以下对象值:
              </p>
              <ul>
                <li>title : 当前点击的数据块标题</li>
                <li>subTitle : 当前点击的二级标题</li>
                <li>data : 数据项源数据。当数据源为数据仓库或者数据模型时有意义</li>
              </ul>
            </div>
          </template>
        </WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import GridItemInfoTableConfiguration from './grid-item-info-table-configuration.vue';
import TitleAvatarStyleConfiguration from './title-avatar-style-configuration.vue';
import ItemStyleConfiguration from './item-style-configuration.vue';
import BadgeStyleConfiguration from './badge-style-configuration.vue';
import TitleStyleConfiguration from './title-style-configuration.vue';
import { filterSelectOption } from '@framework/vue/utils/function';
export default {
  name: 'WidgetGridInfoBlockConfiguration',
  props: { widget: Object, designer: Object },
  components: {
    TitleStyleConfiguration,
    GridItemInfoTableConfiguration,
    TitleAvatarStyleConfiguration,
    ItemStyleConfiguration,
    BadgeStyleConfiguration
  },
  computed: {},
  data() {
    return {
      colorConfig: {},
      columnIndexOptions: [],
      dataSourceOptions: [],
      dsLoading: true,
      columnMappingOptions: [
        { label: '主标题字段', value: 'itemTitleColumn' },
        { label: '二级标题字段', value: 'itemSubTitleColumn' },
        { label: '头像字段', value: 'itemAvatarIconColumn' },
        { label: '徽章数量字段', value: 'itemBadgeNumColumn' }
      ],
      colMappingHistory: {},
      lastDsBuildTypeId: undefined
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.fetchThemeSpecify();
    this.onChangeColMappingSelect();
    this.onChangeBuildType(false);
  },
  methods: {
    onChangeColMappingSelect() {
      let id = this.widget.configuration[this.widget.configuration.buildType == 'dataSource' ? 'dataSourceId' : 'dataModelUuid'];
      if (id) {
        let map = {};
        for (let opt of this.columnMappingOptions) {
          map[opt.value] = this.widget.configuration[opt.value];
        }
        this.colMappingHistory[id] = map;
      }
      console.log('onChangeColMappingSelect', this.colMappingHistory);
    },
    fetchDataSourceOptions(value) {
      let _this = this;
      this.$tempStorage.getCache(
        `viewComponentService.loadSelectData`,
        () => {
          return new Promise((resolve, reject) => {
            this.$axios
              .post('/common/select2/query', {
                serviceName: 'viewComponentService',
                queryMethod: 'loadSelectData'
              })
              .then(({ data }) => {
                resolve(data.results);
              });
          });
        },
        results => {
          _this.dsLoading = false;
          _this.dataSourceOptions = results;
        }
      );
    },
    onChangeBuildType(clearSelected) {
      if (clearSelected == true) {
        this.clearColumnSelect();
      }
      if (this.widget.configuration.buildType == 'dataSource' && this.dsLoading) {
        this.fetchDataSourceOptions();
      }
      if (this.widget.configuration.buildType == 'dataSource' && this.widget.configuration.dataSourceId) {
        this.fetchColumns(this.widget.configuration.dataSourceId);
      } else if (this.widget.configuration.buildType == 'dataModel' && this.widget.configuration.dataModelUuid) {
        this.fetchDataModelColumnOptions();
      }
      if (['dataSource', 'dataModel'].includes(this.widget.configuration.buildType)) {
        let id = this.widget.configuration[this.widget.configuration.buildType == 'dataSource' ? 'dataSourceId' : 'dataModelUuid'];
        if (id && this.colMappingHistory[id] != undefined) {
          for (let opt of this.columnMappingOptions) {
            this.$set(this.widget.configuration, opt.value, this.colMappingHistory[id][opt.value]);
          }
        }
      }
    },
    filterSelectOption,
    onChangeDataModelSelect() {
      this.fetchDataModelColumnOptions();
    },
    fetchDataModelColumnOptions() {
      this.$nextTick(() => {
        if (this.widget.configuration.dataModelUuid == undefined) {
          this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
        } else {
          this.$refs.dataModelSelect.fetchDataModelColumns(this.widget.configuration.dataModelUuid).then(list => {
            if (list) {
              this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
              this.columnIndexOptions.push(...list);
            }
          });
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
            this.colorConfig = specifyDefJson.colorConfig;
          }
        })
        .catch(error => {});
    },
    onChangeSelectDataSource() {
      this.clearColumnSelect();
      this.fetchColumns(this.widget.configuration.dataSourceId);
    },
    clearColumnSelect() {
      this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
      for (let opt of this.columnMappingOptions) {
        this.widget.configuration[opt.value] = undefined;
      }
    },
    fetchColumns(dataSourceId) {
      let _this = this;
      if (dataSourceId) {
        this.$tempStorage.getCache(
          `viewComponentService.getColumnsById:${dataSourceId}`,
          () => {
            return new Promise((resolve, reject) => {
              this.$axios
                .post(`/json/data/services`, {
                  serviceName: 'viewComponentService',
                  methodName: 'getColumnsById',
                  args: JSON.stringify([dataSourceId])
                })
                .then(({ data }) => {
                  resolve(data.data);
                });
            });
          },
          data => {
            _this.columnIndexOptions.splice(0, _this.columnIndexOptions.length);
            for (let i = 0, len = data.length; i < len; i++) {
              _this.columnIndexOptions.push({
                value: data[i].columnIndex,
                label: data[i].title
              });
            }
          }
        );
      }
    }
  },
  configuration() {
    let infoItems = [];
    for (let i = 0; i < 3; i++) {
      infoItems.push({
        id: generateId(),
        title: '主标题',
        subTitle: '子标题',
        avatar: {
          icon: 'folder'
        }
      });
    }
    return {
      title: undefined,
      column: 3,
      gutter: 12,
      padding: '0 8px',
      buildType: 'define',
      infoItems,
      dataModelUuid: undefined,
      dataSourceId: undefined,
      itemTitleColumn: undefined,
      itemSubTitleColumn: undefined,
      itemBadgeNumColumn: undefined,
      itemAvatarIconColumn: undefined,
      itemStyle: {
        backgroundColor: '#FFF',
        borderRadius: 4,
        borderWidth: 0,
        borderColor: undefined,
        displayStyle: 'iconLeft_textRight',
        backgroundStyle: {
          backgroundColor: '', // 白底
          backgroundImage: undefined,
          backgroundImageInput: undefined,
          bgImageUseInput: false,
          backgroundRepeat: undefined,
          backgroundPosition: undefined
        },
        padding: '',
        cursorPointer: true,
        hoverStyle: {
          backgroundColor: '',
          borderColor: undefined,
          boxShadow: '0px 0px 4px 1px #d9d9d9',
          type: ['boxShadow']
        }
      },
      badgeStyle: {
        color: undefined,
        colorType: 'default',
        badgeDisplayType: 'text',
        badgeOverflowCountShowType: 'limitless',
        badgeOverflowCount: undefined,
        badgeShowZero: 'no',
        left: null,
        top: null,
        right: 12,
        bottom: null,
        leftUnit: 'px',
        topUnit: 'px',
        bottomUnit: 'px',
        rightUnit: 'px'
      },
      avatarStyle: {
        backgroundColor: '--w-primary-color-2',
        fontSize: 18,
        color: '--w-primary-color',
        width: 40,
        borderRadius: 20,
        borderWidth: 2,
        borderColor: '--w-primary-color-2'
      },
      titleStyle: {
        margin: '',
        padding: '',
        size: '',
        color: '',
        subColor: '',
        subSize: '',
        titleEllipsis: '',
        subTitleEllipsis: '',
        weight: '',
        subMargin: ''
      },
      domEvents: [
        {
          id: 'onClickTitle',
          title: '点击标题区域时候触发',
          codeSource: 'codeEditor',
          jsFunction: undefined,
          widgetEvent: [],
          customScript: undefined // 事件脚本
        }
      ]
    };
  }
};
</script>
