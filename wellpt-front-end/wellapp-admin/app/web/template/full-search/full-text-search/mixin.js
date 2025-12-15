import { DispatchEvent } from '@framework/vue/lib/dispatchEvent';
import { fetchByDataModeUuid } from "../api";

export default {
  inject: ['pageContext', 'widgetLayoutContext', 'searchInstance'],
  props: {
    setting: {
      type: Object,
      default: () => { }
    }
  },
  computed: {
    categoryOptions() {
      return this.getCategoryOptions();
    },
    categoryMap() {
      let categoryMap = {};
      this.categoryOptions.forEach(item => {
        categoryMap[item.value] = item;
      });

      return categoryMap;
    }
  },
  methods: {
    // 获取所有分类
    getCategoryOptions() {
      let options = [];
      if (this.setting.enabledCategory) {
        options.push({ label: '全部', value: '' });
        this.setting.scopeList.forEach(item => {
          if (item.visible) {
            item.label = item.title;
            options.push(item);
          }
        });
      }
      return options;
    },
    // 打开详情
    handleOpenDetail() {
      let item;
      if (this.itemData.resultSource) {
        item = this.itemData.resultSource;
      } else {
        item = this.itemData;
      }
      if (!item || (item && !item.url)) {
        this.$message.error('没有详情地址');
        return;
      }
      if (this.setting && this.setting.resultOpenMode) {
        let mode = this.setting.resultOpenMode;
        mode = `${mode.charAt(0).toUpperCase()}${mode.slice(1)}`;
        if (mode !== 'Tab') {
          if (item.url.indexOf(this._$SYSTEM_ID) === -1) {
            item.url = `/sys/${this._$SYSTEM_ID}/_${item.url}` // 加上系统id
          }
        }
        if (item.index === 'form_data') {
          fetchByDataModeUuid({
            dataModeUuid: item.dataModeUuid
          }).then(res => {
            if (res.viewDataFormSource === 'custom') {
              item.url = `/dyform-viewer/data?formUuid=${res.viewDataFormUuid}&dataUuid=${item.dataUuid}`
            }
            item.url = item.url + '&displayState=label'
            this[`openDetailAs${mode}`](item);
          })
        } else {
          this[`openDetailAs${mode}`](item);
        }
      }
    },
    openDetailAsTab(item) {
      if (!this.widgetLayoutContext) {
        return;
      }
      const containerWid = this.widgetLayoutContext.widget.configuration.content.id;
      let options = {
        actionType: 'redirectPage',
        containerWid,
        eventParams: [],
        key: '_full-search-result-' + item.uuid,
        meta: {},
        pageType: 'url',
        targetPosition: 'widgetLayout',
        title: item.titleStr,
        trigger: 'click',
        url: item.url
      };

      this.dispatchEventHandler(options);
    },
    dispatchEventHandler(eventHandler) {
      if (eventHandler.pageContext == undefined) {
        eventHandler.pageContext = this.pageContext;
      }

      new DispatchEvent(eventHandler).dispatch();
    },
    openDetailAsWindow(item) {
      window.open(`${item.url}`, '_blank');
    },
    openDetailAsDraw(item) {
      this.pageContext.emitEvent('openSearchDetailDrawer', item);
    }
  }
};
