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
          <a-input v-model="record.title" size="small" :style="{ width: '130px' }">
            <Icon slot="prefix" :type="record.style.icon" v-if="record.style.icon" />
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :designer="designer" :code="record.id" v-model="record.title" :target="record" />
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
              <SubformExportButtonConfiguration
                :button="record"
                :widget="widget"
                :designer="designer"
                :exportRule="widget.configuration.export.exportButtonRule[record.id]"
              ></SubformExportButtonConfiguration>
            </template>
          </WidgetDesignDrawer>
        </template>
        <template slot="footer">
          <WidgetDesignDrawer :id="'WidgetTableBtnConfigExport' + widget.id" title="添加导出按钮" :designer="designer">
            <div style="text-align: right">
              <a-button type="link" :style="{ paddingLeft: '7px' }" size="small">
                <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                添加
              </a-button>
            </div>
            <template slot="content">
              <SubformExportButtonConfiguration
                :button="newButton"
                :widget="widget"
                :designer="designer"
                :exportRule="newButtonExportRule"
                ref="exportButtonConfiguration"
              ></SubformExportButtonConfiguration>
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
import SubformExportButtonConfiguration from './subform-export-button-configuration.vue';
export default {
  name: 'ExportConfiguration',
  props: { widget: Object, designer: Object, columnIndexOptions: Array },
  inject: ['appId', 'subAppIds', 'designConfigPropertyPopupContainer', 'pageContext'],
  components: { SubformExportButtonConfiguration },
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
        id: 'export_' + generateId(),
        code: undefined,
        title: undefined,
        role: [],
        style: { icon: 'export', textHidden: false, type: 'link' },
        defaultVisible: true,
        defaultVisibleVar: { operator: 'true', code: undefined, value: undefined }
      },
      newButtonExportRule: {
        exportColumnSelectable: false,
        exportRowRangeSelectable: false,
        exportRange: 'all',
        exportColumnConfig: {}
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
  beforeMount() {},
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
    },
    onConfirmAddExportButton() {
      if (!this.widget.configuration.headerButton.enable) {
        this.widget.configuration.headerButton.enable = true;
      }
      this.widget.configuration.headerButton.buttons.push(deepClone(this.newButton));
      this.$set(this.widget.configuration.export.exportButtonRule, this.newButton.id, deepClone(this.newButtonExportRule));
      this.newButtonExportRule = deepClone({
        exportColumnSelectable: false,
        exportRowRangeSelectable: false,
        exportRange: 'all',
        exportColumnConfig: {}
      });
      this.newButton = deepClone({
        id: 'export_' + generateId(),
        code: undefined,
        title: undefined,
        role: [],
        style: { icon: 'export', textHidden: false, type: 'link' },
        defaultVisible: true,
        defaultVisibleVar: { operator: 'true', code: undefined, value: undefined }
      });

      this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
    },
    delExportButton(button) {
      for (let i = 0, len = this.widget.configuration.headerButton.buttons.length; i < len; i++) {
        if (this.widget.configuration.headerButton.buttons[i].id == button.id) {
          this.widget.configuration.headerButton.buttons.splice(i, 1);
          delete this.widget.configuration.export.exportButtonRule[button.id];
          break;
        }
      }
    }
  }
};
</script>
