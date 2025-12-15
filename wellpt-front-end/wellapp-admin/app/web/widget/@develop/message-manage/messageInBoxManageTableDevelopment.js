import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import { messageDetailModal, readMsgService, messageNotificationOpen } from '@admin/app/web/template/message-manage/message-utils.js';

class messageInBoxManageTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '消息列表管理收件箱表格二开',
      hook: {
        delete: '删除选中项',
        deleteItem: '删除行数据',
        setAllRead: '全部标记已读',
        setAllUnRead: '全部标记未读',
        setUnRead: '标记未读',
        setRead: '标记已读',
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
    _this.criterions = [];
    let WidgetTitle = widget.widget.title; //配置时必填
    this.tableType = '';
    if (WidgetTitle) {
      this.tableType = WidgetTitle.indexOf('已读') > -1 ? 'read' : 'unRead';
    }
    // 监听表格刷新事件
    widget.pageContext.handleEvent('refetchMessageInBoxManangeTable_' + this.tableType, () => {
      _this.refreshDataByCurrent();
      _this.refreshMessageData();
    });
    // 监听分类点击事件变化
    widget.pageContext.handleEvent('refetchMessageInBoxManangeTable', ({ classifyUuid, hasParamRefetch }) => {
      if (hasParamRefetch) {
        _this.refreshDataByCurrent();
      } else {
        let criterions = [];
        this.classifyUuid = classifyUuid == 'all' ? '' : classifyUuid;
        if (this.classifyUuid == 'user') {
          criterions.push({
            columnIndex: 'SENDER',
            value: 'system',
            type: 'ne'
          });
        } else if (this.classifyUuid) {
          criterions.push({
            columnIndex: 'CLASSIFY_UUID',
            value: this.classifyUuid,
            type: 'eq'
          });
        }
        widget.clearOtherConditions(_this.criterions);
        _this.criterions = criterions;
        widget.addOtherConditions(criterions);
        widget.refetch(true);
      }
    });
    this.classifyQueryList();
  }

  classifyQueryList() {
    let msgClassifyList = window.localStorage.getItem('msgClassifyList');
    if (!msgClassifyList) {
      $axios.get('/proxy/api/message/classify/facadeQueryList', { params: { name: '' } }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          window.localStorage.setItem('msgClassifyList', JSON.stringify(data.data));
        }
      });
    }
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
        title: '来自 ' + args.meta.SENDER_NAME + '(' + args.meta.RECEIVED_TIME + ')',
        readOnly: true,
        pageContext: this.widget.pageContext
      });
      if (args.meta['ISREAD'] == 0) {
        readMsgService([args.meta['UUID']], () => {
          this.refreshDataByCurrent(true);
          this.widget.pageContext.emitEvent(`refetchMessageBoxClassifyData`);
          // 刷新主导航徽标
          window.top.$app.pageContext.emitEvent(`WidgetMenu:refreshMenuBadgeNumberByCode`, { code: 'HeaderOnlineMsg' });
          window.top.$app.pageContext.emitEvent(`WidgetMenu:refreshHeaderOnlineMsgList`);
        });
      }
    }
  }

  refreshDataByCurrent(refreshOther) {
    let options = this.widget.getDataSourceProvider().options;
    this.widget.refetch && this.widget.refetch(options);
    if (refreshOther && this.tableType) {
      let type = this.tableType == 'read' ? 'unRead' : 'read';
      this.widget.pageContext.emitEvent(`refetchMessageInBoxManangeTable_` + type);
    }
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
        $axios.post('/message/content/deleteInboxMessage', { uuids: ids }).then(({ data }) => {
          if (data.code == 0) {
            widget.$message.success('删除成功');
            _this.refreshDataByCurrent();
            _this.refreshMessageData();
          }
        });
      },
      onCancel() { }
    });
  }
  setUnRead() {
    const ids = this.getSelectedRowsBeforeHandle();
    if (ids) {
      this.setReadReq(ids, '1');
    }
  }
  setRead() {
    const ids = this.getSelectedRowsBeforeHandle();
    if (ids) {
      this.setReadReq(ids, '0');
    }
  }
  setReadReq(ids, tabIndex) {
    let _this = this;
    let title = tabIndex == '0' ? '已读' : '未读';
    let url = tabIndex == '0' ? '/message/content/read' : '/message/content/unread';

    this.widget.$confirm({
      title: '确定要标记为' + title,
      onOk() {
        $axios.post(url, { uuids: ids }).then(({ data }) => {
          if (data.code == 0) {
            _this.widget.$message.success('标记成功');
            _this.refreshDataByCurrent(true);
            _this.refreshMessageData();
          }
        });
      },
      onCancel() { }
    });
  }
  setAllUnRead() {
    let _this = this;
    $axios.put('/message/inbox/updateToUnReadStateByclass?classifyUuid=' + this.classifyUuid).then(({ data }) => {
      if (data.code == 0) {
        this.widget.$message.success('标记成功！');
        _this.refreshDataByCurrent(true);
        _this.refreshMessageData();
      }
    });
  }
  setAllRead() {
    let _this = this;
    $axios.put('/message/inbox/updateToReadStateByclass?classifyUuid=' + this.classifyUuid).then(({ data }) => {
      if (data.code == 0) {
        this.widget.$message.success('标记成功！');
        _this.refreshDataByCurrent(true);
        _this.refreshMessageData();
      }
    });
  }
  refreshMessageData() {
    this.widget.pageContext.emitEvent(`refetchMessageBoxClassifyData`);
    // 刷新主导航徽标
    window.top.$app.pageContext.emitEvent(`WidgetMenu:refreshMenuBadgeNumberByCode`, { code: 'HeaderOnlineMsg' });
    window.top.$app.pageContext.emitEvent(`WidgetMenu:refreshHeaderOnlineMsgList`);
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
    let batchBtn = ['delete', 'setUnRead', 'setRead', 'outBatch'];
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

export default messageInBoxManageTableDevelopment;
