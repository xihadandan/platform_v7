<template>
  <div class="wf-condition-component">
    <div :class="['condition-container']">
      <div class="condition-item flex" v-for="(record, index) in value" :key="index">
        <div class="f_g_1">
          {{ record.pointcut && pointcutOptionObj[record.pointcut] ? pointcutOptionObj[record.pointcut] : '脚本定义' }}
        </div>
        <div class="f_s_0">
          <a-button type="link" size="small" @click="setFormData(record, index)" icon="setting"></a-button>
          <a-button type="link" size="small" @click="delFormData(record, index)" icon="delete"></a-button>
        </div>
      </div>
      <a-button type="link" size="small" @click="addFormData" icon="plus" v-if="!maxLength || value.length < maxLength">
        添加{{ text }}
      </a-button>
    </div>
    <a-modal
      class="wf-eventScripts-component-modal"
      :getContainer="getContainer"
      :maskClosable="false"
      :title="title"
      :visible="visible"
      :okText="okText"
      cancelText="取消"
      :width="800"
      :bodyStyle="{ padding: '12px 20px', height: '600px', 'overflow-y': 'auto' }"
      @cancel="onCancelModal"
      @ok="onOkModal"
      ref="modal"
    >
      <a-form-model
        ref="form"
        :model="formData"
        :colon="false"
        :rules="rules"
        labelAlign="left"
        :label-col="{ flex: '120px' }"
        :wrapper-col="{ flex: 'auto' }"
      >
        <a-form-model-item label="事件节点" prop="pointcut" v-if="pointcutShow">
          <w-select
            :options="pointcutOption"
            placeholder="请选择"
            v-model="formData.pointcut"
            :disabled="this.currentIndex != -1"
          ></w-select>
        </a-form-model-item>
        <a-form-model-item label="脚本类型">
          <w-select :options="typeOptions" placeholder="请选择" v-model="formData.type"></w-select>
        </a-form-model-item>
        <a-form-model-item label="脚本内容">
          <w-select
            :options="contentTypeOptions"
            placeholder="请选择"
            v-model="formData.contentType"
            @change="contentTypeChange"
          ></w-select>
        </a-form-model-item>
        <a-form-model-item label="脚本定义" v-if="formData.contentType == '1'">
          <w-select :options="groovyOptions1" placeholder="请选择" v-model="formData.content"></w-select>
        </a-form-model-item>
        <a-form-model-item label="自定义" v-else>
          <a-textarea v-model="formData.content" placeholder="请输入" :auto-size="{ minRows: 3, maxRows: 5 }" />
        </a-form-model-item>
        <a-form-model-item label="支持的脚本变量">
          <div class="flex f_wrap">
            <div v-for="(item, index) in scriptRemark" :key="'r_' + index" class="remark-item">
              {{ item }}
            </div>
          </div>
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script type="text/babel">
import { filter, map, each, findIndex, isInteger, assignIn } from 'lodash';
import { deepClone } from '@framework/vue/utils/util';
import { pointcut, customPointCut, scriptRemark } from '../designer/constant';
import WSelect from '../components/w-select';

export default {
  name: 'WfEventScripts',
  inject: ['designer', 'graph'],
  components: {
    WSelect
  },
  props: {
    text: {
      type: String,
      default: '事件脚本'
    },
    value: {
      type: Array
    },
    okText: {
      type: String,
      default: '确定'
    },
    showPointcut: {
      type: String
    }
  },
  data() {
    let dataList = deepClone(this.value);
    let initFormData = {
      pointcut: '',
      type: '',
      contentType: '',
      content: ''
    };
    return {
      scriptRemark,
      dataList,
      visible: false,
      initFormData,
      formData: deepClone(initFormData), // 弹框内容
      currentIndex: -1,
      title: '事件脚本',
      typeOptions: [
        {
          id: 'groovy',
          text: 'Groovy'
        }
      ],
      contentTypeOptions: [
        {
          id: '1',
          text: '脚本定义'
        },
        {
          id: '2',
          text: '自定义'
        }
      ],
      groovyOptions: [],
      type: 'groovy',
      pointcutOptionObj: {}
    };
  },
  filters: {},
  computed: {
    pointcutOption() {
      let options = [];
      if (this.showPointcut == 'task') {
        options = deepClone(customPointCut);
      } else {
        options = deepClone(pointcut);
      }
      this.pointcutOptionObj = {};
      each(options, item => {
        this.pointcutOptionObj[item.id] = item.text;
      });
      return options;
    },
    rules() {
      let rules = {
        pointcut: { validator: this.pointcutValidate, trigger: ['blur', 'change'] }
      };
      if (this.showPointcut == 'direction' || this.currentIndex != -1) {
        delete rules.pointcut;
      }
      return rules;
    },
    groovyOptions1() {
      if (this.formData.type == 'groovy') {
        return this.groovyOptions;
      } else {
        return [];
      }
    },
    pointcutShow() {
      return this.showPointcut !== 'direction';
    },
    maxLength() {
      if (this.showPointcut == 'direction') {
        return 1;
      } else {
        return 4;
      }
    }
  },
  beforeCreate() {},
  created() {
    this.getGroovyOptions();
  },
  beforeMount() {},
  mounted() {},
  methods: {
    getContainer() {
      return document.querySelector('.edit-widget-property-container');
    },
    // 下拉选项
    getGroovyOptions() {
      const params = {
        pageSize: 1000,
        pageNo: 1,
        type: this.type,
        methodName: 'loadSelectData',
        serviceName: 'cdScriptDefinitionFacadeService'
      };
      this.$axios
        .post('/common/select2/query', {
          ...params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data) {
              const data = res.data.results;
              this.groovyOptions = data;
            }
          }
        });
    },
    addFormData() {
      // 初始默认第一个
      this.currentIndex = -1;
      this.formData = deepClone(this.initFormData);
      this.title = '添加' + this.text;
      this.visible = true;
    },
    setFormData(data, index) {
      this.currentIndex = index;
      this.formData = data;
      this.title = this.text;
      this.visible = true;
    },
    delFormData(data, index) {
      this.dataList.splice(index, 1);
      this.$emit('input', this.dataList);
      this.$emit('change', this.dataList);
    },
    emitValueChange() {
      if (this.formData.content) {
        if (this.currentIndex == -1) {
          // 脚本排列顺序：创建、启动、结束、删除|完成
          if (this.formData.pointcut == 'created') {
            this.dataList.unshift(this.formData);
          } else if (this.formData.pointcut == 'started') {
            let createdIndex = findIndex(this.dataList, { pointcut: 'created' });
            if (createdIndex > -1) {
              this.dataList.splice(createdIndex + 1, 0, this.formData);
            } else {
              this.dataList.unshift(this.formData);
            }
          } else if (this.formData.pointcut == 'end') {
            let deletedIndex = findIndex(this.dataList, { pointcut: 'deleted' });
            if (deletedIndex > -1) {
              this.dataList.splice(deletedIndex, 0, this.formData);
            } else {
              this.dataList.push(this.formData);
            }
          } else {
            this.dataList.push(this.formData);
          }
        } else {
          this.dataList.splice(this.currentIndex, 1, this.formData);
        }
      }
      this.$emit('input', this.dataList);
      this.$emit('change', this.dataList);
    },
    onCancelModal() {
      let _this = this;
      this.visible = false;
      _this.$emit('cancel');
    },
    onOkModal(callback) {
      this.$refs.form.validate((valid, error) => {
        if (valid) {
          this.visible = false;
          this.emitValueChange();
        }
        if (typeof callback == 'function') {
          callback({ valid, error, data: data });
        }
      });
    },
    pointcutValidate(rule, value, callback) {
      if (value) {
        let hasIndex = findIndex(this.value, { pointcut: value });
        let name = this.pointcutOptionObj[value];
        if (hasIndex > -1 && this.currentIndex != hasIndex) {
          callback(new Error(name + '事件脚本已配置，不能重复配置！'));
        } else {
          callback();
        }
      } else {
        callback();
      }
    },
    contentTypeChange() {
      this.formData.content = '';
    },
    changeValue(v) {
      // this.dataList = deepClone(v);
    }
  },

  watch: {
    value: {
      deep: true,
      handler(v) {
        console.log(v);
        this.changeValue(v);
      }
    }
  }
};
</script>
<style lang="less">
.wf-condition-component {
  .condition-container {
    border: 1px dashed var(--w-border-color-light);
    border-radius: var(--w-border-radius-base);
    padding-top: var(--w-padding-3xs);
  }
  .condition-item {
    background: var(--w-fill-color-light);
    border-radius: var(--w-border-radius-base);
    margin: 0 var(--w-margin-3xs) var(--w-margin-2xs);
    padding-left: var(--w-padding-3xs);
    padding-top: var(--w-padding-3xs);
    padding-bottom: var(--w-padding-3xs);
    line-height: var(--w-line-height);
  }
}
.wf-eventScripts-component-modal {
  .ant-row {
    margin-bottom: 4px;
    display: flex;
    .ant-form-item-control-wrapper {
      flex: 1 1 auto;
    }
    .ant-form-item-label-left {
      white-space: normal;
    }
  }
  .remark-item {
    line-height: var(--w-line-height);
    min-width: 50%;
  }
}
</style>
