import WidgetDyformSetDevelopment from '@develop/WidgetDyformSetDevelopment';

class BizProcessItemWidgetDyformSetDevelopment extends WidgetDyformSetDevelopment {
  get META() {
    return {
      name: '业务事项办件——表单设置',
      hook: {
        save: '保存',
        submit: '提交',
        startTimer: '开始计时',
        pauseTimer: '暂停计时',
        resumeTimer: '恢复计时',
        cancel: '撤销',
        complete: '办结',
        suspend: '挂起',
        resume: '恢复',
        viewProcess: '办理过程',
        viewFlow: '查看流程',
        printForm: '打印表单'
      }
    };
  }

  /**
   *
   */
  dyformMounted() {
    const _this = this;
    _this.$itemWidget = _this.$widget.parent();
    _this.$itemWidget.setDyformWidget(_this.wDyform);
    _this.$itemWidget.onDyformMounted(_this.wDyform);
  }

  afterFormDataChanged() {
    this.$itemWidget.onDyformDataChanged();
  }

  validateIfRequired($evt, callback) {
    if ($evt && $evt.eventParams && $evt.eventParams.validate == 'true') {
      this.wDyform.validateFormData(valid => {
        if (!valid) {
          return;
        }
        callback();
      });
    } else {
      callback();
    }
  }

  /**
   * 保存
   *
   * @param {*} $evt
   */
  save($evt) {
    this.validateIfRequired($evt, () => {
      this.$itemWidget.save($evt);
    });
  }

  /**
   * 提交
   *
   * @param {*} $evt
   */
  submit($evt) {
    this.validateIfRequired($evt, () => {
      this.$itemWidget.submit($evt);
    });
  }

  /**
   * 开始计时
   *
   * @param {*} $evt
   */
  startTimer($evt) {
    this.validateIfRequired($evt, () => {
      this.$itemWidget.startTimer($evt);
    });
  }

  /**
   * 暂停计时
   *
   * @param {*} $evt
   */
  pauseTimer($evt) {
    this.validateIfRequired($evt, () => {
      this.$itemWidget.pauseTimer($evt);
    });
  }

  /**
   * 恢复计时
   *
   * @param {*} $evt
   */
  resumeTimer($evt) {
    this.validateIfRequired($evt, () => {
      this.$itemWidget.resumeTimer($evt);
    });
  }

  /**
   * 撤销
   *
   * @param {*} $evt
   */
  cancel($evt) {
    const _this = this;
    _this.$widget.$confirm({
      title: '确认',
      content: `确认撤销办件[${_this.$itemWidget.bizData.title}]？`,
      onOk() {
        _this.validateIfRequired($evt, () => {
          _this.$itemWidget.cancel($evt);
        });
      }
    });
  }

  /**
   * 办结
   *
   * @param {*} $evt
   */
  complete($evt) {
    this.validateIfRequired($evt, () => {
      this.$itemWidget.complete($evt);
    });
  }

  /**
   * 挂起
   *
   * @param {*} $evt
   */
  suspend($evt) {
    this.validateIfRequired($evt, () => {
      this.$itemWidget.suspend($evt);
    });
  }

  /**
   * 恢复
   *
   * @param {*} $evt
   */
  resume($evt) {
    this.validateIfRequired($evt, () => {
      this.$itemWidget.resume($evt);
    });
  }

  /**
   * 办理过程
   *
   * @param {*} $evt
   */
  viewProcess($evt) {
    this.$itemWidget.viewProcess($evt);
  }

  /**
   * 查看流程
   *
   * @param {*} $evt
   */
  viewFlow($evt) {
    const _this = this;
    let workflowBusinessIntegration = _this.$itemWidget.workflowBusinessIntegration || {};
    if (workflowBusinessIntegration.bizInstUuid && workflowBusinessIntegration.type == '1') {
      let workflowUrl = `/workflow/work/view/work?flowInstUuid=${workflowBusinessIntegration.bizInstUuid}`;
      window.open(_this.$itemWidget.addSystemPrefix(workflowUrl));
    } else {
      _this.$itemWidget.$message.error('流程实例不存在！');
    }
  }

  /**
   * 打印表单
   */
  printForm($evt) {
    this.$itemWidget.printForm($evt);
  }
}

export default BizProcessItemWidgetDyformSetDevelopment;
