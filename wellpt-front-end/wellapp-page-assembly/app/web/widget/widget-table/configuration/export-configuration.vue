<template>
  <div v-if="widget.configuration.export != undefined">
    <a-form-model-item label="启用导出">
      <a-switch v-model="widget.configuration.export.enable" @change="onChangeEnableExport" />
    </a-form-model-item>

    <div v-show="widget.configuration.export.enable">
      <a-table
        rowKey="id"
        :showHeader="false"
        size="small"
        :pagination="false"
        :bordered="false"
        :columns="buttonTableColumn"
        :data-source="filterExportButtons"
        :class="['widget-table-button-table no-border']"
      >
        <template slot="title">
          <i class="line" />
          导出按钮
        </template>
        <template slot="titleSlot" slot-scope="text, record">
          <a-input v-model="record.title" size="small" :style="{ width: '130px' }" class="addon-padding-3xs">
            <Icon slot="prefix" :type="record.style.icon" v-if="record.style.icon" />
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :designer="designer" :code="record.id" :target="record" v-model="record.title" />
            </template>
          </a-input>
          <a-tag color="blue" :style="{ marginRight: '0px' }">数据导出</a-tag>
        </template>

        <template slot="operationSlot" slot-scope="text, record, index">
          <a-button
            type="link"
            size="small"
            @click="record.defaultVisible = !record.defaultVisible"
            :title="record.defaultVisible ? '隐藏' : '显示'"
          >
            <Icon :type="record.defaultVisible ? 'pticon iconfont icon-wsbs-xianshi' : 'pticon iconfont icon-wsbs-yincang'"></Icon>
          </a-button>
          <a-button type="link" size="small" @click="delExportButton(record)" title="删除">
            <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
          </a-button>
          <WidgetDesignDrawer :id="'WidgetTableBtnConfigExport' + record.id" title="编辑导出按钮" :designer="designer">
            <a-button type="link" size="small" :ref="'exportBtnDrawer_' + record.id" title="编辑导出按钮">
              <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
            </a-button>
            <template slot="content">
              <ExportButtonConfiguration
                :button="record"
                :widget="widget"
                :designer="designer"
                :exportRule="widget.configuration.export.exportButtonRule[record.id]"
                :columnRenderOptions="columnRenderOptions"
              ></ExportButtonConfiguration>
            </template>
          </WidgetDesignDrawer>
        </template>
        <template slot="footer">
          <WidgetDesignDrawer
            ref="addButtonDrawerRef"
            :id="'WidgetTableBtnConfigExport' + widget.id"
            title="添加导出按钮"
            :designer="designer"
          >
            <div style="text-align: right">
              <a-button type="link" :style="{ paddingLeft: '7px' }" size="small">
                <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                添加
              </a-button>
            </div>
            <template slot="content">
              <ExportButtonConfiguration
                :button="newButton"
                :widget="widget"
                :designer="designer"
                :exportRule="newButtonExportRule"
                :columnRenderOptions="columnRenderOptions"
                ref="exportButtonConfiguration"
              ></ExportButtonConfiguration>
            </template>
            <template slot="footer">
              <a-button size="small" type="primary" @click.stop="onConfirmAddExportButton">确定</a-button>
            </template>
          </WidgetDesignDrawer>
        </template>
      </a-table>
    </div>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import ExportButtonConfiguration from './export-button-configuration.vue';
export default {
  name: 'ExportConfiguration',
  props: { widget: Object, designer: Object, columnIndexOptions: Array },
  inject: ['appId', 'subAppIds', 'designConfigPropertyPopupContainer', 'pageContext'],
  components: { ExportButtonConfiguration },
  computed: {
    filterExportButtons() {
      let buttons = [];
      for (let i = 0, len = this.widget.configuration.headerButton.buttons.length; i < len; i++) {
        if (this.widget.configuration.headerButton.buttons[i].id.startsWith('export_')) {
          // 导出按钮
          buttons.push(this.widget.configuration.headerButton.buttons[i]);
        }
      }
      return buttons;
    }
  },
  data() {
    return {
      newButton: {
        id: 'export_' + generateId(6),
        code: undefined,
        title: '导出',
        role: [],
        style: { type: 'default', icon: JSON.stringify({ iconClass: 'iconfont icon-ptkj-daochu' }), textHidden: false },
        defaultVisible: true,
        defaultVisibleVar: { operator: 'true', code: undefined, value: undefined }
      },
      newButtonExportRule: {
        useTableDataSource: true,
        exportColumnSelectable: false,
        exportRowRangeSelectable: false,
        useSelfDefineExportService: false,
        dataSourceType: 'dataSource',
        exportRange: 'all',
        columns: [],
        exportServiceType: 'excel_ooxml_column',
        exportFileName: '<%= fileName %>'
      },
      columnRenderOptions: [],
      buttonTableColumn: [
        { title: '名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 110, scopedSlots: { customRender: 'operationSlot' } }
      ]
    };
  },
  beforeCreate() {},
  created() {
    if (this.widget.configuration.export == undefined) {
      this.$set(this.widget.configuration, 'export', {
        enable: false,
        exportButtonRule: {}
      });
    }
  },
  beforeMount() {
    this.getRenderFunctionOptions();
  },
  mounted() {
    this.pageContext.offEvent(`${this.widget.id}:openExportButtonConfig`).handleEvent(`${this.widget.id}:openExportButtonConfig`, id => {
      if (this.$refs['exportBtnDrawer_' + id]) {
        this.$refs['exportBtnDrawer_' + id].$el.click();
      }
    });
  },
  methods: {
    onChangeEnableExport() {
      let buttons = this.widget.configuration.headerButton.buttons;
      buttons.forEach(b => {
        if (b.id.startsWith('export_')) {
          b.defaultVisible = this.widget.configuration.export.enable;
        }
      });
      if (this.filterExportButtons.length == 0 && this.widget.configuration.export.enable) {
        this.$refs.addButtonDrawerRef.openDrawer();
      }
    },
    onConfirmAddExportButton() {
      this.$refs.exportButtonConfiguration.validate().then(() => {
        if (!this.widget.configuration.headerButton.enable) {
          this.widget.configuration.headerButton.enable = true;
        }
        this.widget.configuration.headerButton.buttons.push(deepClone(this.newButton));
        this.$set(this.widget.configuration.export.exportButtonRule, this.newButton.id, deepClone(this.newButtonExportRule));
        this.newButtonExportRule = deepClone({
          useTableDataSource: true,
          exportColumnSelectable: false,
          exportRowRangeSelectable: false,
          useSelfDefineExportService: false,
          dataSourceType: 'dataSource',
          exportRange: 'all',
          columns: [],
          exportServiceType: 'excel_ooxml_column',
          exportFileName: '<%= fileName %>'
        });
        this.newButton = deepClone({
          id: 'export_' + generateId(6),
          code: undefined,
          title: '导出',
          role: [],
          style: { type: 'default', icon: JSON.stringify({ iconClass: 'iconfont icon-ptkj-daochu' }), textHidden: false },
          defaultVisible: true,
          defaultVisibleVar: { operator: 'true', code: undefined, value: undefined }
        });

        this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
      });
    },
    delExportButton(button) {
      for (let i = 0, len = this.widget.configuration.headerButton.buttons.length; i < len; i++) {
        if (this.widget.configuration.headerButton.buttons[i].id == button.id) {
          this.widget.configuration.headerButton.buttons.splice(i, 1);
          delete this.widget.configuration.export.exportButtonRule[button.id];
          break;
        }
      }
    },
    getRenderFunctionOptions() {
      var _this = this;
      if (this.columnRenderOptions.length == 0) {
        $axios
          .post('/common/select2/query', {
            serviceName: 'viewComponentService',
            queryMethod: 'loadRendererSelectData',
            pageNo: 1,
            type: 2,
            pageSize: 10000
          })
          .then(({ data }) => {
            if (data.results && data.results.length) {
              for (let i = 0, len = data.results.length; i < len; i++) {
                _this.columnRenderOptions.push({
                  value: data.results[i].id,
                  label: data.results[i].text
                });
              }
              // console.log('服务端渲染器: ', _this.serverRenderOptions);
            }
          });
      }
    }
  }
};
</script>
