import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import { isEmpty } from 'lodash';
import WorkflowDelegationSettings from "./WorkflowDelegationSettings.js";

class WorkflowDelegationSettingsTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '平台应用_工作流程_委托设置_表格二开',
      hook: {
        add: '新增委托',
        active: '激活',
        deactive: '终止',
        delete: '删除',
        saveAs: '保存为常用',
        agreen: '同意',
        refuse: '拒绝'
      }
    };
  }

  /**
   * 新增委托
   *
   * @param {*} evt
   */
  add(evt) { }

  /**
   * 激活
   *
   * @param {*} evt
   */
  active(evt) {
    let _this = this;
    let selection = _this.getSelection(evt);
    if (selection.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    let errorMsg = '';
    selection.forEach(item => {
      if (item.status == 1) {
        errorMsg = '只可激活状态为终止或征求受托人意见的委托！';
      }
    });

    if (!isEmpty(errorMsg)) {
      _this.$widget.$message.error(errorMsg);
      return;
    }

    let uuids = _this.getSelectedUuids(evt);
    _this.showLoading('激活中...');
    $axios
      .post('/api/workflow/delegation/settiongs/activeAll', { uuids })
      .then(({ data: result }) => {
        _this.hideLoading();
        if (result.code == 0) {
          _this.$widget.$message.success('激活成功！');
          _this.$widget.refetch();
        } else {
          _this.$widget.$message.success('激活失败！');
        }
      })
      .catch(error => {
        _this.hideLoading();
        _this.$widget.$message.success('激活失败！');
      });
  }

  /**
   * 终止
   *
   * @param {*} evt
   */
  deactive(evt) {
    let _this = this;
    let selection = _this.getSelection(evt);
    if (selection.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    let errorMsg = '';
    selection.forEach(item => {
      if (item.status == 0) {
        errorMsg = '只可终止状态为生效或征求意见中的委托！';
      }
    });

    if (!isEmpty(errorMsg)) {
      _this.$widget.$message.error(errorMsg);
      return;
    }

    let uuids = _this.getSelectedUuids(evt);
    _this.showLoading('终止中...');
    $axios
      .post('/api/workflow/delegation/settiongs/deactiveAll', { uuids })
      .then(({ data: result }) => {
        _this.hideLoading();
        if (result.code == 0) {
          _this.$widget.$message.success('终止成功！');
          _this.$widget.refetch();
        } else {
          _this.$widget.$message.success('终止失败！');
        }
      })
      .catch(error => {
        _this.hideLoading();
        _this.$widget.$message.success('终止失败！');
      });
  }

  /**
   * 删除
   *
   * @param {*} evt
   */
  delete(evt) {
    let _this = this;
    let uuids = _this.getSelectedUuids(evt);
    if (uuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    let rows = _this.$widget.getRowsByKeys(uuids);
    for (let index = 0; index < rows.length; index++) {
      if (rows[index].status == 1) {
        _this.$widget.$message.error("生效状态的委托不能删除！");
        return;
      }
    }

    _this.$widget.$confirm({
      title: '确认框',
      content: '确认要删除吗？',
      onOk() {
        _this.showLoading('删除中...');
        $axios
          .post(`/api/workflow/delegation/settiongs/deleteAll`, { uuids })
          .then(({ data: result }) => {
            _this.hideLoading();
            if (result.code == 0) {
              _this.$widget.$message.success('删除成功！');
              _this.$widget.refetch();
            } else {
              _this.$widget.$message.error(result.msg || '删除失败！');
            }
          })
          .catch(({ response }) => {
            _this.hideLoading();
            _this.$widget.$message.error((response && response.data && response.data.msg) || '系统服务异常！');
          });
      }
    });
  }

  /**
   *
   * @param {*} evt
   */
  saveAs(evt) {
    let _this = this;
    let uuids = _this.getSelectedUuids(evt);
    if (uuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    let settings = new WorkflowDelegationSettings(this.$widget);
    settings.getFlowDelegationSettings(uuids[0]).then((formData => {
      if (formData) {
        settings.saveAs({
          formData: {
            name: '流程委托',
            definitionJson: JSON.stringify(formData),
            usedCount: 0
          }
        });
      }
    }));
  }

  /**
   * 同意
   * 
   * @param {*} evt 
   */
  agreen(evt) {
    const _this = this;
    let uuid = evt.meta.uuid;
    let settings = new WorkflowDelegationSettings(_this.$widget);
    settings.agreen(uuid).then((success) => {
      if (success) {
        _this.$widget.refetch();
      }
    });
  }

  /**
   * 拒绝
   * 
   * @param {*} evt 
   */
  refuse(evt) {
    const _this = this;
    let uuid = evt.meta.uuid;
    let settings = new WorkflowDelegationSettings(_this.$widget);
    settings.refuse(uuid).then((success) => {
      if (success) {
        _this.$widget.refetch();
      }
    });
  }

  /**
   * 获取选择的uuid列表
   *
   * @param {*} evt
   * @returns
   */
  getSelectedUuids(evt) {
    const _this = this;
    let uuid = evt.meta.uuid;
    let uuids = _this.$widget.getSelectedRowKeys();
    return uuid && typeof uuid == 'string' ? [uuid] : uuids;
  }

  /**
   * 获取选择的数据列表
   *
   * @param {*} evt
   * @returns
   */
  getSelection(evt) {
    const _this = this;
    let uuid = evt.meta.uuid;
    let rowData = evt.meta;
    let selection = _this.$widget.getSelectedRows();
    return uuid && typeof uuid == 'string' ? [rowData] : selection;
  }
}

export default WorkflowDelegationSettingsTableDevelopment;
