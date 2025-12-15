<template>
  <a v-show="vShow" :href="href" :target="target" @click="onClickHref" :class="['no-form-item-widget', widgetClass]">
    {{ text }}
  </a>
</template>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { DispatchEvent } from '@framework/vue/lib/dispatchEvent';

export default {
  name: 'WidgetHyperlink',
  mixins: [widgetMixin],
  data() {
    return {};
  },
  beforeCreate() {},
  components: {},
  computed: {
    vUrl() {
      if (this.widget.configuration.protocol === '/') {
        let url = this.widget.configuration.url.startsWith('/')
          ? this.widget.configuration.url.substring(1)
          : this.widget.configuration.url;
        // 服务路径下
        let contextPath = '/';
        if (this._$SYSTEM_ID) {
          contextPath = '/sys/' + this._$SYSTEM_ID + '/_/';
        }
        return contextPath + url;
      }
      return this.widget.configuration.protocol + this.widget.configuration.url;
    },

    text() {
      return this.$t('text', this.widget.configuration.text || '超链接');
    },
    target() {
      return ['_blank', '_self'].includes(this.widget.configuration.targetPosition) ? this.widget.configuration.targetPosition : null;
    },
    href() {
      if (this.widget.configuration.linkType === 'url') {
        return this.widget.configuration.targetPosition != '_dialog' ? this.vUrl : '#';
      } else {
        // 邮件地址
        let href = `mailto:${this.widget.configuration.mail.to}?`;
        if (this.widget.configuration.mail.subject) {
          href += `&subject=${encodeURIComponent(this.widget.configuration.mail.subject)}`;
        }
        if (this.widget.configuration.mail.body) {
          href += `&body=${encodeURIComponent(this.widget.configuration.mail.body)}`;
        }
        return href;
      }
    }
  },
  created() {},
  methods: {
    onClickHref() {
      if (this.widget.configuration.targetPosition === '_dialog') {
        let dispatchEvent = new DispatchEvent({ actionType: 'openModalDialog', pageContext: this.pageContext });
        let renderOption = {
          widgets: [
            {
              wtype: 'WidgetIframe',
              id: new Date().getTime(),
              configuration: { url: this.vUrl }
            }
          ],
          containerWid: this.widget.configuration.widgetModalId,
          targetPosition: 'widgetModal'
        };
        let calculateDataSource = {
          ...(this.widgetDependentVariableDataSource() || {})
        };
        this._logger.commitBehaviorLog({
          type: 'click',
          element: {
            tag: 'A',
            text: this.widget.configuration.text || '超链接',
            id: this.widget.id
          },
          page: {
            url: window.location.href,
            title: this.vPage != undefined ? this.vPage.title || document.title : undefined,
            id: this.vPage != undefined ? this.vPage.pageId || this.vPage.pageUuid : undefined
          },
          businessCode: this.widget.configuration.behaviorLogConfig.businessCode
            ? executeJSFormula(this.widget.configuration.behaviorLogConfig.businessCode.value, calculateDataSource)
            : undefined,
          description: this.widget.configuration.behaviorLogConfig.description
            ? executeJSFormula(this.widget.configuration.behaviorLogConfig.description.value, calculateDataSource)
            : `访问链接: ${this.vUrl}`,
          extraInfo: this.widget.configuration.behaviorLogConfig.extraInfo
            ? executeJSFormula(this.widget.configuration.behaviorLogConfig.extraInfo.value, calculateDataSource)
            : undefined
        });
        dispatchEvent.event.render(renderOption);
      }
    }
  },
  mounted() {}
};
</script>
