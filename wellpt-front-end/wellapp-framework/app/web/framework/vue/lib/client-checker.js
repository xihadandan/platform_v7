import { debounce, throttle } from 'lodash';
import { getCookie } from '@framework/vue/utils/util';

class ClientChecker {
  constructor($app, options) {
    this.options = options;
    const worker = new SharedWorker('/static/js/shared-worker.js', {
      name: 'wellsoft',
      type: 'module'
    });
    // 启动连接
    worker.port.start();
    $app.SharedWorker = worker;
  }
  checkPassword() {
    if (window.__INITIAL_STATE__._CONTEXT_STATE_.ENV !== 'local') {
      let code = getCookie('force_modify_password_code');
      let notified = false;
      try {
        // 避免内嵌iframe重复提醒
        notified = window.top.force_modify_password_notified !== undefined;
      } catch (error) {}
      if (window.__INITIAL_STATE__._CONTEXT_STATE_.USER && code && !notified) {
        let _div = document.createElement('div');
        document.querySelector('#app').appendChild(_div);
        try {
          window.top.force_modify_password_notified = true;
        } catch (error) {}
        const locale = this.options.locale;
        new (window.Vue.extend({
          template: `<a-config-provider :locale="locale">
                    <component is="HeaderModifyPassword" ref="modifyPassword" :closable="closable" :cancelButtonProps="cancelButtonProps" :visible="visible" :mask="mask"
                     :modifyCode="modifyCode"></component>
                  </a-config-provider>`,
          i18n: { locale: window.$app.$i18n.locale, messages: { [window.$app.$i18n.locale]: window.$app.$i18n.messages } },
          data: function () {
            return {
              locale,
              modifyCode: code,
              visible: true,
              mask: true,
              closable: false,
              cancelButtonProps: {
                style: {
                  display: 'none'
                }
              }
            };
          },
          mounted() {}
        }))().$mount(_div);
      }
    }
  }

  checkUserActivity() {
    // 监听用户活动事件
    if ($app._$USER) {
      let throttleSendHeartbeat = throttle(
        () => {
          let formData = new FormData();
          formData.set('reason', 'user focus');
          navigator.sendBeacon('/heartbeat', formData);
        },
        1000 * 60 * 3,
        { maxWait: 1000 * 60 * 5 }
      );
      ['mousemove', 'keydown', 'scroll', 'click'].forEach(event => {
        window.addEventListener(event, throttleSendHeartbeat, { passive: true, capture: true });
      });

      document.addEventListener('visibilitychange', function () {
        if (!document.hidden) {
          let formData = new FormData();
          formData.set('reason', 'user focus');
          navigator.sendBeacon('/heartbeat', formData);
        }
      });
    }
  }
}

export default ClientChecker;
