import VueWidgetDevelopment from '@develop/VueWidgetDevelopment';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';

class DefIexportDevelopment extends VueWidgetDevelopment {
  exportDef(e) {
    let type = e.eventParams.iexportType,
      title = e.eventParams.title,
      uuid = e.meta.selectedRowKeys,
      exportDependency = e.eventParams.exportDependency === 'true';
    this.getPageContext().mountVueComponentAsChild(
      {
        template: '<ExportDef :title="title" :type="type" :uuid="uuid"  ref="export" :exportDependency="exportDependency"/>',
        components: { ExportDef },
        data() {
          return {
            title,
            type,
            uuid,
            exportDependency
          };
        },
        mounted() {
          this.$refs.export.show();
        }
      },
      this.getWidget().$el,
      false
    );
  }

  importDef(e) {
    let type = e.eventParams.iexportType,
      title = e.eventParams.title,
      filterType = e.eventParams.filterType ? e.eventParams.filterType.split(/,|;/) : undefined;
    this.getPageContext().mountVueComponentAsChild(
      {
        template: '<ImportDef :title="title" :filterType="filterType" :type="type" @importDone="importDone"  ref="import"/>',
        components: { ImportDef },
        data() {
          return {
            title,
            type,
            filterType
          };
        },
        methods: {
          importDone() {
            if (e.$evtWidget && e.$evtWidget.refetch) {
              e.$evtWidget.refetch();
            }
          }
        },
        mounted() {
          this.$refs.import.show();
        }
      },
      this.getWidget().$el,
      false
    );
  }

  get META() {
    return {
      name: '数据定义导入导出',
      hook: {
        exportDef: {
          title: '定义导出',
          eventParams: [
            {
              paramKey: 'iexportType',
              remark: '导出业务编码'
            },
            {
              paramKey: 'title',
              remark: '弹窗标题'
            },
            {
              paramKey: 'exportDependency',
              remark: '是否导出依赖数据',
              valueScope: (() => {
                return ['true', 'false'];
              })()
            }
          ]
        },
        importDef: {
          title: '定义导入',
          eventParams: [
            {
              paramKey: 'iexportType',
              remark: '导入业务编码'
            },
            {
              paramKey: 'title',
              remark: '弹窗标题'
            },
            {
              paramKey: 'filterType',
              remark: '导入限定类型'
            }
          ]
        }
      }
    };
  }
}

export default DefIexportDevelopment;
