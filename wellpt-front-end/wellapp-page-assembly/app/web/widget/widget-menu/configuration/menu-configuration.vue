<template>
  <div>
    <a-form-model
      :layout="formLayout != undefined ? formLayout.layout : undefined"
      :colon="formLayout != undefined ? formLayout.colon : false"
      :label-col="formLayout && formLayout.labelCol ? formLayout.labelCol : undefined"
      :wrapper-col="formLayout && formLayout.labelCol ? formLayout.wrapperCol : undefined"
      class="pt-form"
    >
      <a-form-model-item label="菜单名称">
        <a-input v-model="menu.title">
          <template slot="addonAfter">
            <a-switch
              v-if="menu.titleHidden != undefined && menu.icon != undefined"
              v-model="menu.titleHidden"
              checked-children="隐藏名称"
              un-checked-children="隐藏名称"
            />
            <WI18nInput
              v-show="menu.titleHidden !== true"
              :widget="widget"
              :target="menu"
              :designer="designer"
              :code="menu.id"
              v-model="menu.title"
            />
          </template>

          <a-popover :arrowPointAtCenter="true" trigger="click" title="编码" slot="suffix" placement="bottomRight">
            <template slot="content">
              <a-input v-model.trim="menu.code" />
            </template>
            <a-icon type="code" title="编码" />
          </a-popover>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="隐藏">
        <a-switch v-model="menu.hidden" />
      </a-form-model-item>
      <a-form-model-item label="禁用">
        <a-switch v-model="menu.disabled" />
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">
          菜单图标
          <a-checkbox :checked="!menu.iconHidden" @change="onChangeMenuIconHidden" />
        </template>
        <WidgetDesignModal
          title="选择图标"
          :zIndex="1000"
          :width="640"
          dialogClass="pt-modal widget-icon-lib-modal"
          :bodyStyle="{ height: '560px' }"
          :maxHeight="560"
          mask
          bodyContainer
        >
          <IconSetBadge v-model="menu.icon" v-show="!menu.iconHidden" onlyIconClass />
          <template slot="content">
            <WidgetIconLib v-model="menu.icon" :required="menu.level === 1" onlyIconClass />
          </template>
        </WidgetDesignModal>
      </a-form-model-item>
      <template v-if="menu.badge && menu.index == undefined">
        <BadgeConfiguration :widget="widget" :designer="designer" :configuration="menu" />
      </template>
      <a-form-model-item>
        <template slot="label">
          动态导航来源
          <a-checkbox v-model="menu.dynamic" />
        </template>

        <a-radio-group
          v-show="menu.dynamic"
          v-model="menu.dynamicDataSourceType"
          button-style="solid"
          size="small"
          @change="onChangeDynamicDataSourceType"
        >
          <a-radio-button value="dataSource">数据仓库</a-radio-button>
          <a-radio-button value="dataModel">数据模型</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <div v-show="menu.dynamic && menu.dynamicDataSourceType">
        <div v-show="menu.dynamicDataSourceType == 'dataSource'">
          <a-form-model-item label="数据仓库查询">
            <DataStoreSelectModal v-model="menu.dataSourceId" :displayModal="true" @change="onChangeDataSource" />
          </a-form-model-item>
        </div>
        <div v-show="menu.dynamicDataSourceType == 'dataModel'">
          <a-form-model-item>
            <template slot="label">
              <span
                style="cursor: pointer"
                :class="menu.dataModelUuid ? 'ant-btn-link' : ''"
                @click="redirectDataModelDesign(menu.dataModelUuid)"
                :title="menu.dataModelUuid ? '打开数据模型' : ''"
              >
                数据模型
                <a-icon type="environment" v-show="menu.dataModelUuid" style="color: inherit; line-height: 1" />
              </span>
            </template>
            <DataModelSelectModal
              :dtype="['TABLE', 'VIEW']"
              v-model="menu.dataModelUuid"
              ref="dataModelSelect"
              @change="onDataModelChange"
            />
          </a-form-model-item>
        </div>
        <a-form-model-item label="菜单名称字段">
          <a-select :options="columnIndexOptions" allow-clear v-model="menu.menuTitleField" />
        </a-form-model-item>
        <a-form-model-item label="菜单值字段">
          <a-select :options="columnIndexOptions" allow-clear v-model="menu.menuKeyField" />
        </a-form-model-item>
        <a-form-model-item>
          <template slot="label">
            <span style="padding-right: 3px">菜单图标字段</span>
            <a-tooltip placement="topRight" :arrowPointAtCenter="true">
              <span slot="title">未定义时候将使用菜单图标统一设置</span>
              <a-icon type="exclamation-circle" style="vertical-align: middle" />
            </a-tooltip>
          </template>

          <a-select :options="columnIndexOptions" allow-clear v-model="menu.menuIconField" />
        </a-form-model-item>
        <a-form-model-item label="菜单父级字段">
          <a-select :options="columnIndexOptions" allow-clear v-model="menu.menuParentKeyField" />
        </a-form-model-item>
      </div>
    </a-form-model>
    <!-- menu.appId 代表模块导航 、menu.index 代表首页导航 -->
    <template v-if="menu.appId == undefined && menu.index == undefined && (menu.menus == undefined || menu.menus.length == 0)">
      <WidgetEventHandler
        :eventModel="menu.eventHandler"
        :designer="designer"
        :widget="widget"
        :widgetSource="widgetSource"
        :formLayout="formLayout"
        :rule="eventRule"
      >
        <template slot="eventParamValueHelpSlot">
          <div style="width: 600px">
            <p>
              1. 支持通过
              <a-tag>${ menu.属性 }</a-tag>
              表达式解析菜单数据, 例如 ${ menu.id }
            </p>

            <p>2. 动态导航项包含 data 属性，可以通过 ${ menu.data.属性名 } 解析值</p>
          </div>
        </template>
      </WidgetEventHandler>
    </template>
  </div>
</template>
<script type="text/babel">
import WidgetEventHandler from '@pageAssembly/app/web/widget/commons/widget-event-handler.vue';
import JsHookSelect from '@pageAssembly/app/web/widget/commons/js-hook-select.vue';
import WidgetDesignModal from '@pageAssembly/app/web/widget/commons/widget-design-modal.vue';
import BadgeConfiguration from '@pageAssembly/app/web/widget/commons/badge-configuration.vue';
import DataModelSelect from '@pageAssembly/app/web/widget/commons/data-model-select.vue';
import IconSetBadge from '@pageAssembly/app/web/widget/commons/icon-set-badge.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'MenuConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    menu: Object,
    formLayout: Object,
    widgetSource: Array,
    eventRule: Object
  },
  data() {
    return {
      dataSourceOptions: [],
      columnIndexOptions: [],
      badgeGetNumTypeOptions: [
        {
          label: '执行脚本方法',
          value: 'jsFunction'
        }
      ]
    };
  },

  beforeCreate() {},
  components: { WidgetEventHandler, JsHookSelect, WidgetDesignModal, BadgeConfiguration, DataModelSelect, WI18nInput, IconSetBadge },
  computed: {},
  created() {
    if (this.menu.badge == undefined) {
      this.$set(this.menu, 'badge', {
        enable: false
      });
    }
    if (this.menu.eventHandler && this.menu.eventHandler.eventParams == undefined) {
      this.$set(this.menu.eventHandler, 'eventParams', []);
    }
  },
  methods: {
    fetchColumns(dataSourceId) {
      if (dataSourceId) {
        $axios
          .post('/json/data/services', {
            serviceName: 'viewComponentService',
            methodName: 'getColumnsById',
            args: JSON.stringify([dataSourceId])
          })
          .then(({ data }) => {
            this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
            let columns = data.data;
            if (data.code == 0 && columns) {
              columns.forEach(column => {
                this.columnIndexOptions.push({
                  value: column.dataIndex || column.columnIndex,
                  label: column.title,
                  isSysDefault: false
                });
              });
            }
          });
      }
    },
    fetchDataSourceOptions(value, params) {
      let _this = this;
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'viewComponentService',
          queryMethod: 'loadSelectData'
        })
        .then(({ data }) => {
          if (data.results) {
            _this.dataSourceOptions = data.results;
          }
        });
    },
    onChangeMenuIconHidden() {
      this.$set(this.menu, 'iconHidden', !this.menu.iconHidden);
    },
    onChangeDefaultActive(checked, id) {
      this.widget.configuration.defaultActiveKey = checked ? id : null;
      if (this.widget.configuration.defaultActiveKey == null && this.widget.configuration.tabs.length) {
        // 默认第一个激活
        this.widget.configuration.defaultActiveKey = this.widget.configuration.tabs[0].id;
      }
    },
    redirectDataModelDesign(uuid) {
      window.open(`/data-model-design/index?uuid=${uuid}`, '_blank');
    },
    onDataModelChange(uuid, { id, name }) {
      this.fetchDataModelDetails(uuid);
    },
    onChangeDataSource() {
      this.fetchColumns(this.menu.dataSourceId);
    },
    fetchDataModelDetails(uuid) {
      return new Promise((resolve, reject) => {
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
            }
          });
        } else {
          resolve(undefined);
        }
      });
    },
    onChangeDynamicDataSourceType() {
      if (this.menu.dynamicDataSourceType == 'dataSource') {
        this.fetchDataSourceOptions(undefined, this.menu.dataSourceId ? { id: this.menu.dataSourceId } : undefined);
        this.fetchColumns(this.menu.dataSourceId);
      } else if (this.menu.dynamicDataSourceType == 'dataModel') {
        this.fetchDataModelDetails(this.menu.dataModelUuid);
      }
    }
  },
  mounted() {
    this.onChangeDynamicDataSourceType();
  }
};
</script>
