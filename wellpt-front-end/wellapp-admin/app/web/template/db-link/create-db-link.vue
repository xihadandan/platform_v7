<template>
  <div class="create-db-link-container">
    <a-row type="flex" v-show="currentStep == 0">
      <!-- <a-col flex="150px">
        <a-menu mode="vertical" @click="handleSelectDbTypeClick" v-model="selectDbCategoryType">
          <a-menu-item v-for="(item, i) in dbTypeOptions" :key="item.value">
            {{ item.label }}
          </a-menu-item>
        </a-menu>
      </a-col> -->
      <a-col flex="calc(100% )" style="flex-wrap: wrap; padding-left: 12px; height: 400px; overflow-y: auto">
        <div v-for="(type, t) in dbTypeOptions">
          <div v-show="selectDbCategoryType.includes(type.value)">
            <template v-for="(item, i) in type.options">
              <div
                :key="'dbtype_' + t + '_' + i"
                :class="['db-type-icon-wrapper', dbTypeSelected == item.value ? 'selected' : undefined]"
                @click="dbTypeSelected = item.value"
              >
                <i class="iconfont icon-ptkj-zuoshangjiao-xuanzhong selected-icon" />
                <DbIcons :type="item.value" />
                <label>{{ item.label }}</label>
              </div>
            </template>
          </div>
        </div>
      </a-col>
    </a-row>
    <Scroll style="height: 400px" v-show="currentStep == 1">
      <div>
        <DbLinkInfo ref="dbLinkInfo" />
      </div>
    </Scroll>

    <div style="text-align: right" v-show="dbTypeSelected != undefined">
      <a-divider />
      <a-button type="primary" @click="onClickNextStep" v-show="currentStep == 0">下一步</a-button>
      <a-button @click="backToLastStep" v-show="currentStep == 1">上一步</a-button>
      <a-button @click="testConnect" v-show="currentStep == 1">测试连接</a-button>
      <a-button type="primary" :icon="saving ? 'loading' : 'save'" @click="onSave" v-show="currentStep == 1">保存</a-button>
    </div>
  </div>
</template>
<style lang="less">
.create-db-link-container {
  margin-bottom: 24px;
  .db-type-icon-wrapper {
    height: 140px;
    width: 150px;
    display: inline-flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin-right: 12px;
    margin-bottom: 12px;
    border-radius: 4px;
    cursor: pointer;
    position: relative;
    &.selected {
      background-color: var(--w-primary-color-3) !important;
      > .selected-icon {
        opacity: 1;
      }
    }
    .selected-icon {
      position: absolute;
      top: -6px;
      left: 1px;
      font-size: 26px;
      color: var(--w-primary-color);
      opacity: 0;
    }
    &:hover {
      background-color: #f9f9f9;
    }
    > label {
      color: #000;
      font-weight: bolder;
    }
  }
}
</style>
<script type="text/babel">
import DbIcons from './icons/db-icons.vue';
import DbLinkInfo from './db-link-info.vue';
export default {
  name: 'CreateDbLink',
  inject: ['currentWindow', 'pageContext'],
  props: {},
  components: { DbIcons, DbLinkInfo },
  computed: {},
  data() {
    return {
      selectDbCategoryType: ['sql'],
      dbTypeSelected: undefined,
      currentStep: 0,
      dbTypeOptions: [
        {
          label: 'SQL',
          value: 'sql',
          options: [
            { label: 'MySQL', value: 'mysql' },
            { label: 'Oracle', value: 'oracle' },
            { label: '达梦', value: 'dameng' },
            { label: '人大金仓', value: 'kingbase' }
          ]
        },
        {
          label: 'NoSQL',
          value: 'nosql',
          options: [
            { label: 'MongoDB', value: 'mongodb' },
            { label: 'Redis', value: 'redis' }
          ]
        }
      ]
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onClickNextStep() {
      this.currentStep = 1;
      this.$refs.dbLinkInfo.form.dbType = this.dbTypeSelected;
      this.$refs.dbLinkInfo.resetRules();
    },
    testConnect() {
      this.$refs.dbLinkInfo.testConnect();
    },
    onSave() {
      this.$refs.dbLinkInfo.save().then(() => {
        if (this.currentWindow != undefined) {
          this.currentWindow.close();
        }
        this.pageContext.emitEvent('uUhtVipLFFhzEXIqolDfcwrsFyqRnvbC:refetch');
      });
    },
    backToLastStep() {
      this.currentStep = 0;
      this.$refs.dbLinkInfo.resetFormData();
      this.$refs.dbLinkInfo.$refs.form.clearValidate();
    }
  }
};
</script>
