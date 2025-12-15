<template>
  <div v-if="widget.configuration.import != undefined">
    <a-form-model-item label="启用导入">
      <a-switch v-model="widget.configuration.import.enable" @change="onChangeEnableImport" />
    </a-form-model-item>

    <div v-show="widget.configuration.import.enable">
      <a-table
        rowKey="id"
        :showHeader="false"
        size="small"
        :pagination="false"
        :bordered="false"
        :columns="buttonTableColumn"
        :data-source="filterImportButtons"
        :class="['widget-table-button-table no-border']"
      >
        <template slot="title">
          <i class="line" />
          导入按钮
        </template>
        <template slot="titleSlot" slot-scope="text, record">
          <a-input v-model="record.title" size="small" :style="{ width: '130px' }" class="addon-padding-3xs">
            <Icon slot="prefix" :type="record.style.icon" v-if="record.style.icon" />
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :designer="designer" :code="record.id" :target="record" v-model="record.title" />
            </template>
          </a-input>
          <a-tag color="blue" :style="{ marginRight: '0px' }">数据导入</a-tag>
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
          <WidgetDesignDrawer :id="'WidgetTableBtnConfigImport' + record.id" title="编辑导入按钮" :designer="designer">
            <a-button type="link" size="small" :ref="'importBtnDrawer_' + record.id" title="编辑导入按钮">
              <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
            </a-button>
            <template slot="content">
              <ImportButtonConfiguration
                :button="record"
                :widget="widget"
                :designer="designer"
                :importRule="widget.configuration.import.importButtonRule[record.id]"
              ></ImportButtonConfiguration>
            </template>
          </WidgetDesignDrawer>
        </template>
        <template slot="footer">
          <WidgetDesignDrawer
            ref="addButtonDrawerRef"
            :id="'WidgetTableBtnConfigImport' + widget.id"
            title="添加导入按钮"
            :designer="designer"
          >
            <div style="text-align: right">
              <a-button type="link" :style="{ paddingLeft: '7px' }" size="small">
                <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                添加
              </a-button>
            </div>
            <template slot="content">
              <ImportButtonConfiguration
                :button="newButton"
                :widget="widget"
                :designer="designer"
                :importRule="newButtonImportRule"
                ref="importButtonConfiguration"
              ></ImportButtonConfiguration>
            </template>
            <template slot="footer" slot-scope="{ close }">
              <a-button size="small" type="primary" @click.stop="e => onConfirmAddImportButton(e, close)">确定</a-button>
            </template>
          </WidgetDesignDrawer>
        </template>
      </a-table>
    </div>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import ImportButtonConfiguration from './import-button-configuration.vue';
import { generateId, deepClone } from '@framework/vue/utils/util';

export default {
  name: 'ImportConfiguration',
  inject: ['pageContext'],
  props: {
    widget: Object,
    designer: Object,
    columnIndexOptions: Array
  },
  components: { ImportButtonConfiguration },
  computed: {
    filterImportButtons() {
      let buttons = [];
      for (let i = 0, len = this.widget.configuration.headerButton.buttons.length; i < len; i++) {
        if (this.widget.configuration.headerButton.buttons[i].id.startsWith('import_')) {
          // 导入按钮
          buttons.push(this.widget.configuration.headerButton.buttons[i]);
        }
      }
      return buttons;
    }
  },
  data() {
    return {
      newButton: {
        id: 'import_' + generateId(6),
        code: undefined,
        title: '导入',
        role: [],
        style: { type: 'default', icon: JSON.stringify({ iconClass: 'iconfont icon-ptkj-daoru' }), textHidden: false },
        defaultVisible: true,
        defaultVisibleVar: { operator: 'true', code: undefined, value: undefined }
      },
      newButtonImportRule: {
        importFileTemplate: {
          fileID: undefined,
          fileName: undefined,
          url: undefined
        },
        importModalType: 'default',
        importLog: false,
        strict: true,
        importCode: undefined,
        importService: 'com.wellsoft.pt.dms.ext.excel.listener.DyformDataExcelImportListener',
        sheetConfig: []
      },
      buttonTableColumn: [
        { title: '名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 110, scopedSlots: { customRender: 'operationSlot' } }
      ]
    };
  },
  beforeCreate() {},
  created() {
    if (this.widget.configuration.import == undefined) {
      this.$set(this.widget.configuration, 'import', {
        enable: false,
        importButtonRule: {}
      });
    }
  },
  beforeMount() {},
  mounted() {
    this.pageContext.offEvent(`${this.widget.id}:openImportButtonConfig`).handleEvent(`${this.widget.id}:openImportButtonConfig`, id => {
      if (this.$refs['importBtnDrawer_' + id]) {
        this.$refs['importBtnDrawer_' + id].$el.click();
      }
    });
  },
  methods: {
    onChangeEnableImport() {
      let buttons = this.widget.configuration.headerButton.buttons;
      buttons.forEach(b => {
        if (b.id.startsWith('import_')) {
          b.defaultVisible = this.widget.configuration.import.enable;
        }
      });
      if (this.filterImportButtons.length == 0 && this.widget.configuration.import.enable) {
        this.$refs.addButtonDrawerRef.openDrawer();
      }
    },
    onConfirmAddImportButton(e, close) {
      this.$refs.importButtonConfiguration.validate().then(() => {
        if (!this.widget.configuration.headerButton.enable) {
          this.widget.configuration.headerButton.enable = true;
        }
        this.widget.configuration.headerButton.buttons.push(deepClone(this.newButton));
        this.$set(this.widget.configuration.import.importButtonRule, this.newButton.id, deepClone(this.newButtonImportRule));
        this.newButtonImportRule = deepClone({
          importFileTemplate: {
            fileID: undefined,
            fileName: undefined,
            url: undefined
          },
          importLog: undefined,
          importModalType: 'default',
          importService: 'com.wellsoft.pt.dms.ext.excel.listener.DyformDataExcelImportListener',
          importCode: undefined,
          strict: true,
          sheetConfig: []
        });

        this.newButton = deepClone({
          id: 'import_' + generateId(6),
          code: undefined,
          title: '导入',
          role: [],
          style: { type: 'default', icon: JSON.stringify({ iconClass: 'iconfont icon-ptkj-daoru' }), textHidden: false },
          defaultVisible: true,
          defaultVisibleVar: { operator: 'true', code: undefined, value: undefined }
        });
        close();
      });
    },
    delExportButton(button) {
      for (let i = 0, len = this.widget.configuration.headerButton.buttons.length; i < len; i++) {
        if (this.widget.configuration.headerButton.buttons[i].id == button.id) {
          this.widget.configuration.headerButton.buttons.splice(i, 1);
          delete this.widget.configuration.import.importButtonRule[button.id];
          break;
        }
      }
    }
  }
};
</script>
