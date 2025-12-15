export default {
  data() {
    return {
      childrenRenderedTotal: 0, // 已经渲染的计数
      childrenWidgets: [],
      childrenElHeights: [],
      childrenRenderedCallBack: {},
      colElHeightMap: {}, // 列id:列里面组件高度
      colChildrenIdMap: {}, // 列id:列里面组件id
      childrenIdHeightMap: {}, // 列里面组件id:行最大高度
      childrenIdTabpaneActiveMap: {},
      colsLength: this.widget.configuration.cols.length,
    }
  },
  created() {
    this.onChildrenWidgetRendered();
  },
  methods: {
    onChildrenWidgetRendered() {
      if (!EASY_ENV_IS_BROWSER) {
        return;
      }
      if (this.widget.configuration.colsEqualHeight) {
        let temp = document.createElement('style');
        (document.head || document.body).appendChild(temp);
        let css = `
          .widget-tabs-container>.widget-tab>.ant-tabs-bar {
            margin-top: 0;
            margin-bottom: 0;
          }
        `;
        temp.innerHTML = css;
        this.pageContext.handleEvent(`widgetGrid:${this.widget.id}:children:rendered`, this.getChildrenWidgetHeight);
      }
    },
    getChildrenWidgetHeight({ wgt, widgetHeight, callBack }) {
      // if (this.childrenRenderedTotal >= this.colsLength) {
      //   return;
      // }
      let validate = true;
      const tabsEl = wgt.$el.closest('.widget-tabs-container');
      if (tabsEl && tabsEl.__vue__) {
        const activeKey = tabsEl.__vue__.widget.configuration.defaultActiveKey;
        const tabpaneActiveEl = wgt.$el.closest(`#${activeKey}`);
        this.pageContext.handleEvent(`${tabsEl.__vue__.widget.id}:change`, ({ activeKey }) => {
          this.$nextTick(() => { });
        });
        if (tabpaneActiveEl) {
          this.childrenIdTabpaneActiveMap[wgt.widget.id] = activeKey;
        } else {
          validate = false;
        }
      }

      if (validate) {
        this.childrenRenderedTotal++;
        this.childrenWidgets.push(wgt);
        if (callBack && typeof callBack === 'function') {
          this.childrenRenderedCallBack[wgt.widget.id] = callBack;
        }

        let elHeight;
        if (widgetHeight) {
          elHeight = widgetHeight;
        } else if (wgt.widget) {
          elHeight = wgt.$el.offsetHeight;
        }
        this.childrenElHeights.push(elHeight);

        if (this.widget.configuration.rowType === 'default') {
          const colEl = wgt.$el.closest('div[id^="grid-col-"]');
          if (colEl) {
            const colId = colEl.getAttribute('id');
            this.colElHeightMap[colId] = elHeight; // 列id:列里面组件高度
            this.colChildrenIdMap[colId] = wgt.widget.id; // 列id:列里面组件id
          }
        }
      }
      // if (this.childrenRenderedTotal === this.colsLength) {
      // 子组件都渲染完成
      let maxHeight;
      if (this.widget.configuration.rowType === 'default') {
        for (const key in this.defaultRowsData) {
          const row = this.defaultRowsData[key];
          if (row && Array.isArray(row)) {
            const colsHeight = row.map(r => {
              return this.colElHeightMap[r.id];
            });
            let maxRowHeight = Math.max(...colsHeight); // 行最大高度
            row.forEach(r => {
              this.childrenIdHeightMap[this.colChildrenIdMap[r.id]] = maxRowHeight;
              row.maxRowHeight = maxRowHeight;
            });
          }
        }
      } else {
        maxHeight = Math.max(...this.childrenElHeights);
      }
      for (let index = 0; index < this.childrenWidgets.length; index++) {
        const item = this.childrenWidgets[index];
        const { wtype, configuration } = item.widget;
        const wgtId = item.widget.id;
        if (wtype === 'WidgetTable') {
          let maxElHeight;
          if (this.widget.configuration.rowType === 'default') {
            maxElHeight = this.childrenIdHeightMap[wgtId];
          } else {
            maxElHeight = maxHeight;
          }
          if (!maxElHeight) {
            continue
          }
          item.$el.style.cssText += `;margin-bottom:0px; height:${maxElHeight}px`;

          if (this.childrenIdTabpaneActiveMap[wgtId]) {
            // 设置其他标签页下的高度
            const tabesContentEl = item.$el.closest('.ant-tabs-content');
            if (tabesContentEl) {
              const tableEls = tabesContentEl.querySelectorAll('.widget-table');
              for (let i = 0; i < tableEls.length; i++) {
                const tableEl = tableEls[i];
                if (tableEl.getAttribute('id') === wgtId) {
                  continue;
                }
                tableEl.style.cssText += `;margin-bottom:0px; height:${maxElHeight}px`;
                tableEl.parentElement.parentElement;
                if (tableEl.parentElement && tableEl.parentElement.parentElement) {
                  if (tableEl.parentElement.parentElement.getAttribute('class') === 'ps') {
                    // 卡片套表格
                    tableEl.parentElement.parentElement.style.cssText += `;height:${maxElHeight}px`;
                  }
                }

                if (tableEl.__vue__.setContentByDisplayState) {
                  tableEl.__vue__.setContentByDisplayState();
                }
              }
            }
          }
        }
        if (this.childrenRenderedCallBack[wgtId]) {
          this.childrenRenderedCallBack[wgtId]();
        }
      }
      // this.resetChildrenRendered();
      // }
    },
    resetChildrenRendered() {
      this.childrenRenderedTotal = 0;
      this.childrenWidgets = [];
      this.childrenElHeights = [];
      this.childrenRenderedCallBack = {};
      this.childrenIdTabpaneActiveMap = {};
    },
  }
}