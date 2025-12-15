<template>
  <!-- 环节属性-表单设置-表单字段-从表按钮设置 -->
  <modal
    v-model="visible"
    :title="title"
    :width="750"
    :ok="saveButton"
    okText="保存"
    wrapperClass="form-settings-modal-wrap"
    :destroyOnClose="true"
  >
    <template slot="content">
      <a-tabs v-model="tabKey">
        <a-tab-pane key="0" tab="显示类操作">
          <w-checkbox v-model="showCheckAll" :checkedValue="true" :unCheckedValue="false" @change="event => changeCheckAll(event, 'show')">
            全选
          </w-checkbox>
          <a-row type="flex" style="margin-top: 7px; margin-bottom: 7px">
            <div class="form-button-label">表头按钮:</div>
            <a-checkbox-group v-model="checkedValue.show.headerButton" @change="event => onChange(event, 'show')">
              <a-checkbox v-for="item in buttonOptions.show.headerButton" :key="item.id" :value="item.id">
                {{ item.title || item.buttonName }}
              </a-checkbox>
            </a-checkbox-group>
          </a-row>
          <a-row type="flex">
            <div class="form-button-label">行按钮:</div>
            <a-checkbox-group v-model="checkedValue.show.rowButton" @change="event => onChange(event, 'show')">
              <a-checkbox v-for="item in buttonOptions.show.rowButton" :key="item.id" :value="item.id">
                {{ item.title || item.buttonName }}
              </a-checkbox>
            </a-checkbox-group>
          </a-row>
        </a-tab-pane>
        <a-tab-pane key="1" tab="编辑类操作" v-if="showEditTab">
          <w-checkbox v-model="editCheckAll" :checkedValue="true" :unCheckedValue="false" @change="event => changeCheckAll(event, 'edit')">
            全选
          </w-checkbox>
          <a-row type="flex" style="margin-top: 7px; margin-bottom: 7px">
            <div class="form-button-label">表头按钮:</div>
            <a-checkbox-group v-model="checkedValue.edit.headerButton" @change="event => onChange(event, 'edit')">
              <a-checkbox v-for="item in buttonOptions.edit.headerButton" :key="item.id" :value="item.id">
                {{ item.title || item.buttonName }}
              </a-checkbox>
            </a-checkbox-group>
          </a-row>
          <a-row type="flex">
            <div class="form-button-label">行按钮:</div>
            <a-checkbox-group v-model="checkedValue.edit.rowButton" @change="event => onChange(event, 'edit')">
              <a-checkbox v-for="item in buttonOptions.edit.rowButton" :key="item.id" :value="item.id">
                {{ item.title || item.buttonName }}
              </a-checkbox>
            </a-checkbox-group>
          </a-row>
        </a-tab-pane>
      </a-tabs>
    </template>
    <div>
      <a-button type="link" size="small" class="setSubForm">
        <Icon type="pticon iconfont icon-ptkj-shezhi" />
      </a-button>
    </div>
  </modal>
</template>

<script>
import WCheckbox from '../components/w-checkbox';
import Modal from '@pageAssembly/app/web/lib/modal.vue';

export default {
  name: 'FormSettingsSubformButton',
  props: {
    title: {
      type: String,
      default: '从表扩展设置'
    },
    field: {
      type: Object,
      default: () => {}
    },
    formData: {
      type: Object,
      default: () => {}
    },
    fieldItem: Function
  },
  components: {
    Modal,
    WCheckbox
  },
  data() {
    let showCheckAll = false;
    let editCheckAll = false;
    let showEditTab = this.formData.canEditForm === '1' ? true : false;
    const { widget } = this.field;
    const { configuration } = widget;
    const { formBtnRightSettings } = this.formData;
    const initCheckedValue = () => {
      return {
        show: {
          headerButton: [],
          rowButton: []
        },
        edit: {
          headerButton: [],
          rowButton: []
        }
      };
    };
    let buttonOptions = initCheckedValue();
    let checkedValue = initCheckedValue();
    return {
      visible: false,
      widget,
      configuration,
      formBtnRightSettings,
      tabKey: '0',
      showEditTab,
      buttonOptions,
      checkedValue,
      showCheckAll,
      editCheckAll,
      initCheckedValue: () => {
        return initCheckedValue();
      },
      flowButtonIndex: -1 // 记录流程中索引，不用遍历
    };
  },
  watch: {
    visible: {
      handler(visible) {
        if (visible) {
          const { canEditForm } = this.formData;
          if (canEditForm === '1') {
            // 显示编辑类按钮
            this.showEditTab = true;
          } else {
            this.showEditTab = false;
          }
          this.initButtons();
        }
      }
    }
  },
  created() {
    this.getButtonOptions();
    this.initButtons();
  },
  methods: {
    initButtons() {
      const { exist, checkedValue } = this.getFlowButton();
      if (this.flowButtonIndex != -1) {
        // 流程已存在数据，以流程设置的为主
        this.checkedValue = this.checkedValueStr2Arr(checkedValue);
        this.setCheckAll('show');
        this.setCheckAll('edit');
      } else {
        const { exist, checkedValue } = this.getFormButton();
        if (exist) {
          if (this.field.isSubWidget) {
            // 从表显示类按钮默认都不显示
            checkedValue.show.headerButton = [];
            checkedValue.show.rowButton = [];
          }
          this.checkedValue = checkedValue;
          this.setCheckAll('show');
          this.setCheckAll('edit');
          this.setFlowButton(checkedValue); // 把表单数据添加到流程中
        }
      }
    },
    // 获取按钮选项
    getButtonOptions() {
      this.buttonOptions.show = {
        headerButton: this.configuration.headerButton.buttons,
        rowButton: this.configuration.rowButton.buttons
      };
      this.buttonOptions.edit = this.buttonOptions.show;
    },
    // 获取流程上按钮数据
    getFlowButton() {
      let exist = false;
      let checkedValue = this.initCheckedValue();
      this.formBtnRightSettings.forEach((item, index) => {
        if (item.value) {
          const value = item.value.split('=');
          const prefix = value[0];
          const buttons = value[1];
          if (prefix === this.field.subformUuid) {
            checkedValue = JSON.parse(buttons);
            exist = true;
            this.flowButtonIndex = index;
          }
        }
      });
      return { exist, checkedValue };
    },
    // 获取表单上按钮数据
    getFormButton() {
      const buttonOptions = this.buttonOptions;
      let exist = false;
      let checkedValue = this.initCheckedValue();
      for (const key in buttonOptions) {
        for (const k in buttonOptions[key]) {
          for (let index = 0; index < buttonOptions[key][k].length; index++) {
            const item = buttonOptions[key][k][index];
            if (item.defaultVisible) {
              checkedValue[key][k].push(item.id);
              exist = true;
            }
          }
        }
      }
      return { exist, checkedValue };
    },
    // 把表单数据添加到流程中
    setFlowButton(checkedValue) {
      let buttonIndex = this.flowButtonIndex;
      if (buttonIndex !== -1) {
        this.formBtnRightSettings.splice(buttonIndex, 1);
      }
      this.flowButtonIndex = this.formBtnRightSettings.length;
      checkedValue = this.checkedValueArr2Str(checkedValue);
      let fieldItem = this.fieldItem();
      fieldItem.value = `${this.field.subformUuid}=${JSON.stringify(checkedValue)}`;
      this.formBtnRightSettings.push(fieldItem);
    },
    checkedValueStr2Arr(checkedValue) {
      let checkedValueArr = this.initCheckedValue();
      for (const key in checkedValue) {
        for (const k in checkedValue[key]) {
          if (checkedValue[key][k]) {
            checkedValueArr[key][k] = checkedValue[key][k].split(';');
          } else {
            checkedValueArr[key][k] = [];
          }
        }
      }
      return checkedValueArr;
    },
    checkedValueArr2Str(checkedValue) {
      let checkedValueStr = this.initCheckedValue();
      for (const key in checkedValue) {
        for (const k in checkedValue[key]) {
          if (checkedValue[key][k].length) {
            checkedValueStr[key][k] = checkedValue[key][k].join(';');
          } else {
            checkedValueStr[key][k] = '';
          }
        }
      }
      return checkedValueStr;
    },
    // 设置全选状态
    setCheckAll(type) {
      const checkValueHeader = this.checkedValue[type]['headerButton']['length'];
      const checkValueRow = this.checkedValue[type]['rowButton']['length'];
      const checkValueCount = checkValueHeader + checkValueRow;

      const optionsHeader = this.buttonOptions[type]['headerButton']['length'];
      const optionsRow = this.buttonOptions[type]['rowButton']['length'];
      const optionsCount = optionsHeader + optionsRow;
      if (checkValueCount === optionsCount) {
        this[`${type}CheckAll`] = true;
      } else {
        this[`${type}CheckAll`] = false;
      }
    },
    // 全选按钮
    changeCheckAll(event, type = 'show') {
      const targetChecked = event.target.checked;
      if (targetChecked) {
        const buttonOptions = this.buttonOptions;
        for (const key in buttonOptions[type]) {
          this.checkedValue[type][key] = [];
          let buttons = [];
          for (let index = 0; index < buttonOptions[type][key].length; index++) {
            const item = buttonOptions[type][key][index];
            buttons.push(item.id);
          }
          this.checkedValue[type][key] = buttons;
        }
      } else {
        this.checkedValue[type] = {
          headerButton: [],
          rowButton: []
        };
      }
    },
    onChange(event, type = 'show') {
      this.setCheckAll(type);
    },
    // 保存按钮
    saveButton(callback) {
      this.setFlowButton(this.checkedValue);
      callback(true);
    }
  }
};
</script>
