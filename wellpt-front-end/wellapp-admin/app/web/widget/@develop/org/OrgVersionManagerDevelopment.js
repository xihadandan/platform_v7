import VuePageDevelopment from '@develop/VuePageDevelopment';

class OrgVersionManagerDevelopment extends VuePageDevelopment {
  mounted() {
    // 挂载版本名称
    this.getPageContext().mountVueComponent(
      {
        template: `<a-tag :color="color" v-show="color!=undefined">{{name}}</a-tag>`,
        propsData: {
          vPageState: this.getPageState()
        },
        computed: {
          color() {
            if (!this.vPageState.currentOrgVersion.state) {
              return undefined;
            }
            if (this.vPageState.currentOrgVersion.state === 'PUBLISHED') {
              return 'blue';
            }
            if (this.vPageState.currentOrgVersion.state === 'DESIGNING') {
              return 'green';
            }
            return 'orange';
          },
          name() {
            if (!this.vPageState.currentOrgVersion.state) {
              return '';
            }
            if (this.vPageState.currentOrgVersion.state === 'PUBLISHED') {
              return '正式版';
            }
            if (this.vPageState.currentOrgVersion.state === 'DESIGNING') {
              return '设计版';
            }
            return '历史版 v' + this.vPageState.currentOrgVersion.ver.toFixed(1);
          }
        }
      },
      document.querySelector("button[code='versionName']")
    );
  }

  publishVersion(event) {
    console.log('publishVersion');
    let wTemplateWidget = this.getVueWidgetById('dZaaDELKFvlMCBZCadmFjesLJdLgspPn'); // 定时发布的组件
    let pageState = this.getPageState();
    let $widget = this.$widget;
    if (wTemplateWidget) {
      let wTemplate = wTemplateWidget.wTemplate;
      wTemplate.$refs.form.validate(valid => {
        if (valid) {
          $axios
            .post('/proxy/api/org/organization/version/setPublishTime', {
              uuid: pageState.uuid,
              publishTime: wTemplate.form.publishTime.toDate().getTime()
            })
            .then(({ data }) => {
              if (data.code == 0) {
                $widget.$message.success('发布设置成功');
                event.$evtWidget.closeModal();
              }
            })
            .catch(error => {
              $widget.$message.error('服务异常');
            });
        }
      });
    } else {
      $axios
        .get('/proxy/api/org/organization/version/publish', { params: { uuid: pageState.uuid } })
        .then(({ data }) => {
          if (data.code == 0) {
            $widget.$message.success('发布成功');
            event.$evtWidget.closeModal();
          }
        })
        .catch(error => {
          $widget.$message.error('服务异常');
        });
    }

    if (event.$evt.target.textContent === '发布') {
    } else if (event.$evt.target.textContent === '定时发布') {
    }
  }

  createNewVersion(event) {
    // 获取数据
    let wTemplate = this.getVueWidgetById('EArPqyIBZkqDbUzkmViXWmNYdvUxXcgq').wTemplate;
    let $widget = this.$widget;
    let uuid = this.getPageState().uuid;
    let _this = this;
    $axios
      .get('/proxy/api/org/organization/version/new', {
        params: { uuid, copyUser: wTemplate.copyUser, copyVersion: wTemplate.createType == 'createCopy' }
      })
      .then(({ data }) => {
        if (data.code == 0) {
          $widget.$message.success('创建新版本成功');
          $widget.pageContext.emitEvent('OrgVersionChange', data.data);
          _this.commitPageState({ designing: true });
          event.$evtWidget.closeModal();
        } else if (!data.success && data.data) {
          $widget.$message.error('创建新版本失败, 已存在设计版');
        }
      })
      .catch(error => {
        $widget.$message.error('创建新版本失败');
      });
  }


  deleteOrgVersion(evt) {
    let _this = this,
      uuid = this.getPageState().uuid;
    this.$widget.$confirm({
      title: '确认要删除设计版吗?',
      content: undefined,
      onOk() {
        $axios
          .get('/proxy/api/org/organization/version/delete', { params: { uuid } })
          .then(({ data }) => {
            if (data.code == 0) {
              _this.$widget.$message.success('删除成功');
              _this.$widget.pageContext.emitEvent('OrgVersionChange', undefined);
            }
          })
          .catch(error => {
            _this.$widget.$message.error('删除失败');
          });
      },
      onCancel() { }
    });
  }

  get META() {
    return {
      name: '组织版本管理',
      hook: {
        deleteOrgVersion: '删除组织版本',
        createNewVersion: '创建新版本',
        publishVersion: '发布版本'
      }
    };
  }
}



export default OrgVersionManagerDevelopment;
