class WorkflowDelegationSettings {

  constructor($widget) {
    this.$widget = $widget;
  }

  getFlowDelegationSettings(uuid) {
    const _this = this;
    return $axios
      .get(`/api/workflow/delegation/settiongs/get?uuid=${uuid}`)
      .then(({ data: result }) => {
        if (result.code === 0) {
          return result.data;
        } else {
          return null;
        }
      });
  }

  saveAs({ formData, ok }) {
    const _this = this;
    // 材料选择弹出框
    let Modal = Vue.extend({
      template: `<a-config-provider :locale="locale">
        <a-modal
          title="保存常用委托"
          :visible="visible"
          okText="保存"
          @cancel="handleCancel"
          @ok="handleOk"
        >
          <a-form-model ref="saveAsForm" :model="saveAsFormData" :rules="saveAsRules" :label-col="labelCol" :wrapper-col="wrapperCol">
            <a-form-model-item label="名称" prop="name">
              <a-input v-model="saveAsFormData.name" :maxLength="100"></a-input>
            </a-form-model-item>
          </a-form-model>
        </a-modal>
      </a-config-provider>`,
      data: function () {
        return {
          visible: true,
          locale: _this.$widget.locale,
          labelCol: { span: 4 },
          wrapperCol: { span: 19 },
          saveAsFormData: formData,
          saveAsRules: {
            name: [{ required: true, message: '不能为空！', trigger: 'change' }]
          },
        };
      },
      methods: {
        handleCancel() {
          this.visible = false;
          this.$destroy();
        },
        handleOk() {
          const _modal = this;
          _modal.$loading('保存中...');
          _modal.$refs.saveAsForm.validate(valid => {
            if (valid) {
              $axios
                .post('/proxy/api/workflow/delegation/settiongs/saveCommon', _modal.saveAsFormData)
                .then(({ data: result }) => {
                  _modal.$loading(false);
                  if (result.code == 0) {
                    _modal.$message.success('保存成功');
                    _modal.saveAsModalVisible = false;
                    ok && ok();
                    _modal.visible = false;
                    _modal.$destroy();
                  } else {
                    _modal.$message.error(result.msg || '系统服务异常！');
                  }
                })
                .catch(({ response }) => {
                  _modal.$loading(false);
                  _modal.$message.error((response && response.data && response.data.msg) || '系统服务异常！');
                });
            } else {
              _modal.$loading(false);
            }
          });
        }
      }
    });
    let modal = new Modal();
    modal.$mount();
  }

  agreen(uuid) {
    const _this = this;
    _this.$widget.$loading('委托生效中...');
    return $axios
      .post(`/proxy/api/workflow/delegation/settiongs/delegationActive?uuid=${uuid}`)
      .then(({ data: result }) => {
        _this.$widget.$loading(false);
        if (result.code === 0) {
          _this.$widget.$message.success('委托已生效！');
          return true;
        } else {
          _this.$widget.$message.error(result.msg || '服务异常！');
        }
      })
      .catch(({ response }) => {
        _this.$widget.$loading(false);
        _this.$widget.$message.error((response && response.data && response.data.msg) || '服务异常！');
      });
  }

  refuse(uuid) {
    const _this = this;
    _this.$widget.$loading('拒绝委托中...');
    return $axios
      .post(`/proxy/api/workflow/delegation/settiongs/delegationRefuse?uuid=${uuid}`)
      .then(({ data: result }) => {
        _this.$widget.$loading(false);
        if (result.code === 0) {
          _this.$widget.$message.success('委托已拒绝!');
          return true;
        } else {
          _this.$widget.$message.error(result.msg || '服务异常！');
        }
      })
      .catch(({ response }) => {
        _this.$widget.$loading(false);
        _this.$widget.$message.error((response && response.data && response.data.msg) || '服务异常！');
      });
  }

}

export default WorkflowDelegationSettings;
