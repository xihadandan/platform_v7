<template>
  <div>
    <a-card
      :bordered="false"
      :style="cardStyle"
      :bodyStyle="{
        padding: '30px 20px'
      }"
    >
      <div :style="{ display: 'flex', alignItems: 'center' }">
        <a-avatar
          icon="user"
          :src="'/proxy/org/user/view/photo/' + user.userId"
          :size="80"
          style="border: 2px solid #ffffff; background-color: var(--w-primary-color)"
        />
        <div :style="{ marginLeft: '24px' }">
          <div style="font-weight: bold; font-size: 28px; color: #ffffff; line-height: 28px; margin-bottom: 16px">{{ userName }}</div>
          <div style="font-weight: 400; font-size: 18px; color: #ffffff; line-height: 18px">{{ jobNamePath }}</div>
        </div>
      </div>
    </a-card>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';

export default {
  name: 'WidgetUserInfoCard',
  mixins: [widgetMixin],
  props: {},
  components: {},
  computed: {
    autoRefresh() {
      return false;
    },
    cardStyle() {
      let style = { border: '2px solid #fff' };
      let { backgroundColor, borderRadius, backgroundImage, backgroundRepeat, backgroundPosition, backgroundSize } =
        this.widget.configuration;
      if (backgroundColor != undefined) {
        style.backgroundColor = backgroundColor.startsWith('--w-') ? `var(${backgroundColor})` : backgroundColor;
      }

      if (borderRadius != undefined) {
        if (Array.isArray(borderRadius)) {
          style.borderRadius = borderRadius
            .map(item => {
              return item + 'px';
            })
            .join(' ');
        } else {
          style.borderRadius = borderRadius + 'px';
        }
      }

      if (backgroundImage != undefined) {
        let isUrl =
          backgroundImage.startsWith('data:') ||
          backgroundImage.startsWith('http') ||
          backgroundImage.startsWith('/') ||
          backgroundImage.startsWith('../') ||
          backgroundImage.startsWith('./');
        style.backgroundImage = isUrl ? `url("${backgroundImage}")` : backgroundImage;
      }
      if (backgroundPosition != undefined) {
        style.backgroundPosition = backgroundPosition;
      }
      if (backgroundRepeat != undefined) {
        style.backgroundRepeat = backgroundRepeat;
      }
      if (backgroundSize != undefined) {
        style.backgroundSize = backgroundSize;
      }

      return style;
    },
    user() {
      return this._$USER;
    },
    userName() {
      let name = this.user.userName;
      if (this.$i18n.locale !== 'zh_CN' && this.user.localUserName) {
        name = this.user.localUserName;
      }
      return name;
    },
    jobNamePath() {
      if (this._$USER) {
        let { userSystemOrgDetails } = this._$USER;
        if (userSystemOrgDetails != undefined && userSystemOrgDetails.details != undefined) {
          for (let i = 0, len = userSystemOrgDetails.details.length; i < len; i++) {
            if (userSystemOrgDetails.details[i].system == this._$SYSTEM_ID || this._$SYSTEM_ID == undefined) {
              let { otherJobs, mainJob } = userSystemOrgDetails.details[i];
              if (mainJob != undefined) {
                if (this.$i18n.locale !== 'zh_CN') {
                  return mainJob.localEleNamePath.replace(/\//g, ' - ');
                }
                return mainJob.eleNamePath.replace(/\//g, ' - ');
              } else if (otherJobs != undefined) {
                if (this.$i18n.locale !== 'zh_CN') {
                  return otherJobs[0].localEleNamePath.replace(/\//g, ' - ');
                }
                return otherJobs[0].eleNamePath.replace(/\//g, ' - ');
              }
            }
          }
        }
      }
      return undefined;
    }
  },
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
