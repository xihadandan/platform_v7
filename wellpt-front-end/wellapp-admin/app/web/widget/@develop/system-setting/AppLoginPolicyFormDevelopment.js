import WidgetFormDevelopment from '@develop/WidgetFormDevelopment';
import { orderBy } from 'lodash';
import { getElSpacingForTarget } from '@framework/vue/utils/util';

class AppLoginPolicyFormDevelopment extends WidgetFormDevelopment {
  mounted() {
    this.setFormStyle();

    this.refetchLoginPolicies().then(() => {
      // 排序功能
      let $menu = this.getVueWidgetById('gnCKlvPqKFqXMRKUoymnBapTfBrOVSHp');

      if (this.loginPolices.length > 0) {
        let idSeq = {};
        for (let i = 0, len = this.loginPolices.length; i < len; i++) {
          idSeq[this.loginPolices[i].id] = this.loginPolices[i].seq;
        }
        // 重排序
        let menus = $menu.widget.configuration.menus;
        menus = orderBy(
          menus,
          item => {
            return idSeq[item.code];
          },
          ['asc']
        );
        $menu.widget.configuration.menus.splice(0, $menu.widget.configuration.menus.length);
        $menu.widget.configuration.menus.push(...menus);
        $menu.initMenus();
      }
      this.itemDraggable($menu.widget.configuration.menus, $menu.$el.querySelector('ul'), '.menu-left-icon');
      $menu.$nextTick(() => {
        let li = $menu.$el.querySelectorAll('li');
        let codeItemMap = {};
        for (let p of this.loginPolices) {
          codeItemMap[p.id] = p;
        }
        for (let i = 0, len = li.length; i < len; i++) {
          if (this.loginPolices[i].id == 'ACCT_PWD_LOGIN') {
            continue;
          }

          ((item, form) => {
            this.getPageContext().mountVueComponentAsChild(
              {
                template: `<a-button :p-id="item.id"   type="link" :icon="iconType" style="position: absolute;right: 0px;top: 50%;transform: translateY(-50%);" @click.stop="clickVisible"/> `,
                propsData: { item, form },
                computed: {
                  iconType() {
                    return this.form[this.enableKey[item.id]] === true ? 'eye' : 'eye-invisible';
                  }
                },
                data() {
                  return {
                    enableKey: {
                      SSO_LOGIN: 'ssoEnabled',
                      DEVICE_LOGIN: 'deviceLoginEnabled',
                      ELECTRO_KEY_LOGIN: 'electroKeyLoginEnabled',
                      DING_LOGIN: 'dingDingLoginEnabled',
                      FEISHU_LOGIN: 'feishuLoginEnabled',
                      FEISHU_SSO_LOGIN: 'feishuSsoLoginEnabled',
                      WEIXIN_LOGIN: 'weixinLoginEnabled'
                    }
                  };
                },
                methods: {
                  clickVisible() {
                    this.form[this.enableKey[item.id]] = !this.form[this.enableKey[item.id]];
                  }
                }
              },
              li[i],
              false
            );
          })(codeItemMap[li[i].__vue__.value.code] || { id: li[i].__vue__.value.code }, this.$widget.form);
        }
      });
    });
  }

  setFormStyle() {
    let init = false;
    this.handleEvent(`ximSGZxDiKzsUsqrQbstmMlhDzhTlxkX:change`, ({ activeKey }) => {
      if (activeKey == 'tab-ppUpjBCFzBzcmLWSaNTcYzhKVbGEcCtq' && !init) {
        init = true;
        setTimeout(() => {
          // this.$widget.$el.style.cssText = `padding: 12px 20px;`;
          let $row = this.$widget.$el.querySelector('.widget-row');
          $row.style.cssText += `border: 1px solid var(--w-border-color-light);border-radius: 4px;`;
          let $parent = this.$widget.$root.$el.classList.contains('preview')
            ? this.$widget.$root.$el
            : this.$widget.$el.closest('.widget-vpage');
          const { maxHeight, totalBottom, totalNextSibling } = getElSpacingForTarget($row, $parent);
          this.setWidgetColHeight(maxHeight - 16);
        }, 100);
      }
    });
  }

  // 设置栅格内容高度
  setWidgetColHeight(height, callback) {
    let $cols = this.$widget.$el.querySelectorAll('.widget-col');
    if ($cols.length) {
      $cols.forEach((col, index) => {
        let style = col.getAttribute('style');
        col.setAttribute(
          'style',
          style ? style + ';height:' + height + 'px' + ';overflow-y:auto;' : 'height:' + height + 'px' + ';overflow-y:auto;'
        );
      });
      if (typeof callback === 'function') {
        callback();
      }
    }
  }

  clearLoginPageCache() {
    $axios
      .get(`/api/cache/deleteByKey`, {
        params: {
          key: `${this.getSystemID()}:${this.getTenantID()}:systemLoginPagePolicy`
        }
      })
      .then(({ data }) => {})
      .catch(error => {});
  }

  refetchLoginPolicies() {
    //  获取
    return new Promise((resolve, reject) => {
      $axios
        .get(`/proxy/api/system/getTenantSystemLoginPolicies`, {
          params: {
            tenant: this.getUser().tenantId,
            system: this.getSystemID() || ''
          }
        })
        .then(({ data }) => {
          if (data.code == 0) {
            this.loginPolices = data.data;
            if (data.data.length !== 0) {
              for (let i = 0, len = data.data.length; i < len; i++) {
                let defJson = data.data[i].defJson;
                if (defJson != undefined) {
                  defJson = JSON.parse(defJson);
                  this.setFormData(defJson);
                }
              }
            }
            resolve();
          }
        });
    });
  }

  itemDraggable(rowData, tableSelector, dragHandleClass, onEvent) {
    let _this = this;
    import('sortablejs').then(Sortable => {
      Sortable.default.create(tableSelector, {
        handle: dragHandleClass,
        onUnchoose: (onEvent && onEvent.onUnchoose) || function () {},
        onMove: (onEvent && onEvent.onMove) || function () {},
        onEnd:
          (onEvent && onEvent.onEnd) ||
          function (e) {
            let temp = rowData.splice(e.oldIndex, 1)[0];
            rowData.splice(e.newIndex, 0, temp);
            _this.itemDraggable(rowData, tableSelector, dragHandleClass, onEvent);
            if (onEvent && onEvent.afterOnEnd) {
              onEvent.afterOnEnd();
            }
          }
      });
    });
  }

  submitData({ $widget, form }) {
    console.log(form);
    let $menu = this.getVueWidgetById('gnCKlvPqKFqXMRKUoymnBapTfBrOVSHp');
    let menus = $menu.widget.configuration.menus,
      empty = this.loginPolices.length == 0,
      idSeq = {};
    for (let i = 0, len = menus.length; i < len; i++) {
      idSeq[menus[i].code] = i + 1;
      let loginPolicy = this.loginPolices.find(item => item.id == menus[i].code);
      if (empty || loginPolicy == null) {
        this.loginPolices.push({
          name: menus[i].title,
          id: menus[i].code,
          defJson: undefined,
          seq: i + 1,
          tenant: this.getUser().tenantId,
          system: this.getSystemID()
        });
      }
    }
    let idKeys = {
        ACCT_PWD_LOGIN: [
          'loginCheckEnable',
          'loginCheckType',
          'picturePattern',
          'letterCase',
          'errorCountRefresh',
          'loginSmsCodeLength',
          'loginCheckTimeout',
          'acctLoginWinTitle',
          'rememberAcct',
          'rememberPwd',
          'enableForgetPwd',
          'enableUserRegister',
          'userRegPageType',
          'userRegPageId',
          'userRegUrl',
          'userRegPageTitle',
          'userRegTarget',
          'userForgetPwdPageType',
          'userForgetPwdPageId',
          'userForgetPwdUrl',
          'userForgetPwdPageTitle',
          'userForgetPwdTarget'
        ],
        SSO_LOGIN: ['ssoEnabled', 'appUrl', 'ssoUrl'],
        DEVICE_LOGIN: ['deviceLoginEnabled', 'deviceRemark', 'deviceWinTitle'],
        ELECTRO_KEY_LOGIN: ['electroKeyLoginEnabled', 'electroKeyLoginRemark', 'electroKeyLoginWinTitle'],
        DING_LOGIN: ['dingDingLoginEnabled', 'qrCodeDivStyle', 'qrCodeDivWidth', 'qrCodeDivHeight', 'dingDingLoginWinTitle'],
        FEISHU_LOGIN: ['feishuLoginEnabled'],
        FEISHU_SSO_LOGIN: ['feishuSsoLoginEnabled'],
        WEIXIN_LOGIN: ['weixinLoginEnabled']
      },
      enableKey = {
        SSO_LOGIN: 'ssoEnabled',
        DEVICE_LOGIN: 'deviceLoginEnabled',
        ELECTRO_KEY_LOGIN: 'electroKeyLoginEnabled',
        DING_LOGIN: 'dingDingLoginEnabled',
        FEISHU_LOGIN: 'feishuLoginEnabled',
        FEISHU_SSO_LOGIN: 'feishuSsoLoginEnabled',
        WEIXIN_LOGIN: 'weixinLoginEnabled'
      };
    for (let i = 0, len = this.loginPolices.length; i < len; i++) {
      let p = this.loginPolices[i];
      p.seq = idSeq[p.id];
      p.defJson = typeof p.defJson == 'object' ? p.defJson : JSON.parse(p.defJson || '{}');
      let keys = idKeys[p.id];
      if (keys != undefined) {
        for (let k in form) {
          if (keys.includes(k)) {
            p.defJson[k] = form[k];
            if (k == enableKey[p.id]) {
              p.enabled = form[enableKey[p.id]];
            }
          }
        }
        p.defJson = JSON.stringify(p.defJson);
      } else {
        p.defJson = JSON.stringify(p.defJson || {});
      }
    }
    this.$widget.$loading();
    $axios
      .post(`/proxy/api/system/saveTenantSystemLoginPolicies`, this.loginPolices)
      .then(({ data }) => {
        this.$widget.$loading(false);
        if (data.data) {
          this.$widget.$message.success('保存成功');
          this.clearLoginPageCache();
          this.refetchLoginPolicies();
        } else {
          this.$widget.$message.error('保存失败');
        }
      })
      .catch(() => {
        this.$widget.$loading(false);
        this.$widget.$message.error('保存失败');
      });
  }

  get META() {
    return {
      name: '登录方式表单二开',
      hook: {
        submitData: '提交表单'
      }
    };
  }
}
export default AppLoginPolicyFormDevelopment;
