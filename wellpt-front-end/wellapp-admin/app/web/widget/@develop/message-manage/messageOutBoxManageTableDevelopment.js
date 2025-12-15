import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import { messageDetailModal, readMsgService } from '@admin/app/web/template/message-manage/message-utils.js';

class messageOutBoxManageTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '消息列表管理发件箱表格二开',
      hook: {
        delete: '删除选中项',
        deleteItem: '删除行数据',
        setBatch: '批量操作',
        // sendMsg: '发消息',(弃用)
        // showDetail: '查看消息',(弃用)
        outBatch: '退出批量操作'
      }
    };
  }

  mounted() {
    let dataSourceProvider = this.getWidget().getDataSourceProvider();
    let widget = this.getWidget();
    this.widget = widget;
    this.classifyUuid = '';
    let _this = this;
    this.widget.pageType = 'outBox';
    // 监听表格刷新事件
    widget.pageContext.handleEvent('refetchMessageOutBoxManangeTable', () => {
      _this.refreshDataByCurrent();
    });
  }

  sendMsg() {
    messageDetailModal({
      messageData: {},
      title: '发消息',
      type: 'sendMsg',
      getTableSelf: this.widget.getSelf,
      pageContext: this.widget.pageContext
    });
  }

  showDetail(args) {
    if (!this.widget.isBatchHandle) {
      messageDetailModal({
        messageData: args.meta,
        title: '发件消息 ' + args.meta.RECEIVED_TIME,
        readOnly: true,
        pageContext: this.widget.pageContext,
        pageType: 'outBox'
      });
    }
  }

  refreshDataByCurrent() {
    let options = this.widget.getDataSourceProvider().options;
    this.widget.refetch && this.widget.refetch(options);
  }

  getSelectedRowsBeforeHandle() {
    const ids = _.map(this.$widget.selectedRows, 'UUID');
    if (ids.length == 0) {
      this.getWidget().$message.error('请选择记录！');
      return false;
    }
    return ids;
  }
  delete() {
    const ids = this.getSelectedRowsBeforeHandle();
    if (ids) {
      this.deleteReq(ids);
    }
  }

  deleteItem($event) {
    this.deleteReq([$event.meta.UUID]);
  }

  deleteReq(ids) {
    let _this = this;
    let widget = this.getWidget();
    widget.$confirm({
      title: '确定要删除所选记录吗？',
      onOk() {
        $axios.post('/message/content/deleteOutboxMessage', { uuids: ids }).then(({ data }) => {
          if (data.code == 0) {
            widget.$message.success('删除成功');
            _this.refreshDataByCurrent();
          }
        });
      },
      onCancel() { }
    });
  }

  outBatch() {
    this.setBatchHandle(true);
    this.widget.selectedRowKeys = [];
    this.widget.selectedRows = [];
  }
  setBatch() {
    this.setBatchHandle(false);
  }

  setBatchHandle(isOut) {
    this.widget.isBatchHandle = !isOut;
    let headerButton = this.widget.configuration.headerButton;
    let batchBtn = ['delete', 'outBatch'];
    _.each(headerButton.buttons, (item, index) => {
      if (batchBtn.indexOf(item.eventHandler.name) > -1) {
        item.defaultVisible = !isOut;
      } else {
        item.defaultVisible = !!isOut;
      }
    });
    // 触发按钮组件重组
    this.widget.$set(this.widget.configuration, 'headerButton', {});
    this.widget.$refs.headerButtonRef.$nextTick(() => {
      this.widget.$set(this.widget.configuration, 'headerButton', headerButton);
    });
    this.widget.$set(this.widget.configuration.rowClickEvent, 'enable', isOut); //行点击
    // 触发列表卡片的复选框显示隐藏
    this.setCardCheckboxShow(!isOut);
  }
  setCardCheckboxShow(isShow) {
    _.each(this.widget.rows, (item, index) => {
      this.widget.$refs['itemCardRef_' + index][0].$refs['itemCardTemRef'].isCheckbox = isShow;
    });
  }
}

export default messageOutBoxManageTableDevelopment;
