<template>
  <EditWrapper
    :widget="widget"
    :index="index"
    :widgetsOfParent="widgetsOfParent"
    :designer="designer"
    :parent="parent"
    :widgetDisplayAsReadonly="true"
    v-bind="editControl"
  >
    <template slot="extra-buttons" v-if="widget.configuration.enableStateForm">
      <a-select v-model="switchDisplayState" @change="onChangeDisplayState" size="small" style="width: 100px">
        <a-select-option value="create">新建时</a-select-option>
        <a-select-option value="edit">编辑时</a-select-option>
        <a-select-option value="label">查阅时</a-select-option>
      </a-select>
    </template>
    <a-layout
      :class="['widget-dyform-setting', !widget.configuration.titleVisible ? 'no-title' : undefined]"
      @click.stop="selectWidget(widget)"
    >
      <a-layout-header class="widget-dyform-setting-head flex" v-if="showHead">
        <h1 v-if="widget.configuration.titleVisible" class="f_g_1 w-ellipsis">
          <Icon :type="widget.configuration.titleIcon" />
          {{ title }}
        </h1>
        <div
          :class="{ 'widget-dyform-setting-buttons': true, f_s_0: widget.configuration.titleVisible }"
          :style="!widget.configuration.titleVisible ? { width: '100%', 'text-align': 'right' } : ''"
        >
          <WidgetTableButtons :key="wButtonKey" :button="button" v-if="widget.configuration.buttonPosition === 'top'" />
        </div>
      </a-layout-header>
      <a-layout-content class="widget-dyform-setting-content">
        <PerfectScrollbar>
          <div class="mask"></div>
          <a-empty v-show="!vFormUuid" description="暂无表单" :style="{ 'margin-top': '20%' }" />

          <a-skeleton active :loading="dyformComponentLoading" :paragraph="{ rows: 15 }">
            <component
              is="WidgetDyform"
              :formUuid="vFormUuid"
              :definitionVjson="definitionVjson"
              v-show="vFormUuid"
              :formElementRules="formElementRules"
              :key="widgetDyformKey"
              ref="widgetDyform"
              :dyformStyle="{ padding: inContainer ? 'unset' : 'var(--w-padding-md)' }"
            />
          </a-skeleton>
        </PerfectScrollbar>
      </a-layout-content>
      <a-layout-footer
        v-if="widget.configuration.buttonPosition === 'bottom'"
        :class="['widget-dyform-setting-footer', 'align-' + widget.configuration.button.buttonAlign]"
      >
        <WidgetTableButtons :key="wButtonKey" :button="button" v-if="widget.configuration.buttonPosition === 'bottom'" />
      </a-layout-footer>
    </a-layout>
  </EditWrapper>
</template>
<style lang="less">
.widget-build-dyform-set-modal-wrap {
  top: 0;
  position: absolute;
}
.widget-build-dyform-set-modal-wrap .ant-modal-body {
  padding: 0;
}
.widget-build-dyform-set-modal-wrap .ant-modal-content {
  height: e('calc(100vh - 92px)');
  outline: 2px solid var(--w-primary-color) !important;
  border-radius: 0;
}
.widget-build-dyform-set-modal-wrap .widget-dyform-setting-content {
  padding: 0;
  min-height: auto;
}
.edit-widget-dyform-setting-container {
  background-color: #fff;
  margin: 0 2px 2px 2px;
  overflow: hidden;
}

.edit-widget-dyform-setting-container .edit-widget-dyform-setting-head {
  height: 80px;
}
.edit-widget-dyform-setting-container .edit-widget-dyform-setting-body {
  position: relative;
}
.edit-widget-dyform-setting-head {
  padding: 25px 10px 0px 10px;
}
.edit-widget-dyform-setting-head > h1 {
  display: inline;
}
.edit-widget-dyform-setting-container.selected {
  outline: 2px solid var(--w-primary-color) !important;
}
.edit-widget-dyform-setting-container .widget-operation-buttons {
  position: absolute;
  right: 12px;
  z-index: 2;
}

.edit-widget-dyform-setting-footer {
  text-align: center;
  padding-top: 15px;
  height: 60px;
}
.widget-build-dyform-set-modal-wrap .widget-operation-buttons {
  position: absolute;
  right: 0;
  z-index: 1;
}

.widget-build-dyform-set-modal-wrap .widget-dyform-setting-footer {
  padding: 10px;
}
.widget-build-dyform-set-modal-wrap .ps {
  height: e('calc(100vh - 208px)');
  padding: 0 15px;
}
.widget-build-dyform-set-modal-wrap .ps .mask {
  position: fixed;
  width: calc(100vh + 400px);
  height: e('calc(100vh - 208px)');
  z-index: 1;
}
.widget-edit-container .widget-dyform-setting-content {
  min-height: e('calc(100vh - 180px)');
}
</style>
<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import draggable from '@framework/vue/designer/draggable';

import { deepClone } from '@framework/vue/utils/util';
import md5 from '@framework/vue/utils/md5';

export default {
  name: 'EWidgetDyformSetting',
  mixins: [editWgtMixin, draggable],
  inject: ['dyform'],
  props: {
    editControl: Object
  },
  data() {
    //FIXME：测试数据
    return {
      autoRefresh: false,
      dyformComponentLoading: true,
      widgetDyformKey: this.widget.id + '_Dyform_' + new Date().getTime(),
      definitionVjson: null,
      originalVjson: null,
      inContainer: this.dyform != undefined,
      visible: true,
      modalVisible: false,
      switchDisplayState: 'create',
      cloneWidget: null
    };
  },

  beforeCreate() {
    import('@dyform/app/web/framework/vue/install').then(() => {
      this.dyformComponentLoading = false;
    });
  },
  components: {},
  computed: {
    vFormUuid() {
      if (this.widget.configuration.enableStateForm) {
        if (this.switchDisplayState == 'edit') {
          return this.widget.configuration.editStateFormUuid;
        } else if (this.switchDisplayState == 'label') {
          return this.widget.configuration.labelStateFormUuid;
        } else {
          return this.widget.configuration.formUuid;
        }
      }
      return this.widget.configuration.formUuid;
    },
    formElementRules() {
      let formElementRules = {},
        key = 'formElementRules';
      if (this.switchDisplayState === 'edit') {
        key = 'editStateFormElementRules';
      } else if (this.switchDisplayState === 'label') {
        key = 'labelStateFormElementRules';
      }

      for (let i = 0, len = this.widget.configuration[key].length; i < len; i++) {
        let rule = this.widget.configuration[key][i];
        formElementRules[rule.id] = {
          displayAsLabel: rule.displayAsLabel,
          editable: rule.editable,
          hidden: rule.hidden,
          required: rule.required
        };
        if (rule.children) {
          for (let j = 0, jlen = rule.children.length; j < jlen; j++) {
            formElementRules[rule.children[j].id] = {
              displayAsLabel: rule.children[j].displayAsLabel,
              editable: rule.children[j].editable,
              hidden: rule.children[j].hidden,
              required: rule.children[j].required
            };
          }
        }
      }
      this.widgetDyformKey = this.widget.id + new Date().getTime();
      this.definitionVjson = JSON.parse(JSON.stringify(this.originalVjson));
      console.log('表单组件规则: ', formElementRules);
      return formElementRules;
    },

    title() {
      if (this.switchDisplayState === 'create') {
        return this.widget.configuration.title;
      } else if (this.switchDisplayState === 'edit') {
        return this.widget.configuration.editStateTitle;
      } else if (this.switchDisplayState === 'label') {
        return this.widget.configuration.labelStateTitle;
      }
      return null;
    },
    showHead() {
      return this.widget.configuration.buttonPosition === 'top' || this.widget.configuration.titleVisible;
    },
    wButtonKey() {
      return md5(JSON.stringify(this.button));
    },
    button() {
      let buttons = this.widget.configuration.button.buttons,
        visibleButtons = [];

      for (let i = 0, len = buttons.length; i < len; i++) {
        if (buttons[i].visibleType === 'visible') {
          buttons[i].visible = true;
          visibleButtons.push(buttons[i]);
          continue;
        }

        if (buttons[i].visibleType === 'hidden') {
          continue;
        }

        // 根据条件判断展示:
        if (buttons[i].visibleType === 'visible-condition') {
          if (
            (this.switchDisplayState === 'create' && buttons[i].visibleCondition.formStateConditions.indexOf('createForm') != -1) ||
            (this.switchDisplayState !== 'create' && buttons[i].visibleCondition.formStateConditions.indexOf(this.switchDisplayState) != -1)
          ) {
            buttons[i].visible = true;
            visibleButtons.push(buttons[i]);
          }
        }
      }

      let btn = deepClone(this.widget.configuration.button);
      btn.buttons = visibleButtons;
      return btn;
    }
  },
  created() {},
  methods: {
    getContainer() {
      return document.querySelector('#design-main');
    },

    deleteSelf() {
      this.modalVisible = false;
      this.widgetsOfParent.splice(this.index, 1);
      delete this.designer.widgetIdMap[this.widget.id];
      this.designer.widgetTree.splice(this.index, 1);
      delete this.designer.widgetTreeMap[this.widget.id];
    },
    showWidgetJsonDetail() {
      this.designer.widgetJsonDrawerVisible = true;
    },

    hangWidgetTree() {
      this.modalVisible = this.designer.secondarySelectedId == this.widget.id;
    },

    onHideDyform() {
      this.modalVisible = false;
    },
    onChangeDisplayState() {}
  },
  beforeMount() {},
  mounted() {
    let _this = this;
    this.designer.handleEvent(`${this.widget.id}:DyformSettingChange`, function (data) {
      _this.switchDisplayState = data.state;
      _this.definitionVjson = data.json;
      _this.originalVjson = JSON.parse(JSON.stringify(data.json));
      _this.widgetDyformKey = _this.widget.id + new Date().getTime();
    });
  },
  watch: {
    'widget.configuration.enableStateForm': {
      handler(v) {
        if (!v) {
          this.switchDisplayState = 'create';
        }
      }
    }
  }
};
</script>
