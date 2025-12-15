<template>
  <!--签署意见校验设置 get_opinionCheckSet_dialog_html  -->
  <PerfectScrollbar :style="{ height: '480px' }" ref="scroll">
    <a-form-model
      ref="form"
      :model="formData"
      :colon="false"
      :label-col="{ span: 3 }"
      :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
      class="opinion-check-scene-form"
    >
      <!-- 校验规则 -->
      <div v-for="(sitem, sindex) in opinionCheckScene" :key="'s_' + sindex" class="opinion-check-scene-div">
        <div class="title">{{ sitem.label }}</div>
        <div v-for="(ditem, dindex) in formData[sitem.value]" :key="'d_' + dindex" class="flex f_y_c opinion-check-scene-item">
          <div class="f_g_1">
            <a-form-model-item
              label="校验规则"
              :prop="`${sitem.value}:${dindex}:opinionRuleUuid`"
              :name="[`${sitem.value}`, dindex, 'opinionRuleUuid']"
              :rules="rules.opinionRuleUuid"
            >
              <w-select
                v-model="ditem.opinionRuleUuid"
                :options="opinionRuleList"
                :replaceFields="{
                  title: 'opinionRuleName',
                  key: 'uuid',
                  value: 'uuid'
                }"
              />
            </a-form-model-item>
            <a-form-model-item label="应用环节">
              <!-- 应用环节 -->
              <a-select
                mode="multiple"
                v-model="ditem.taskIds"
                optionFilterProp="title"
                @select="value => selectTaskIds(value, sitem.value, dindex)"
                :showSearch="true"
                :showArrow="true"
                :allowClear="true"
                :getPopupContainer="getPopupContainerByPs()"
                :dropdownClassName="getDropdownClassName()"
              >
                <a-select-option
                  v-for="item in flowTasks"
                  :key="item.id"
                  :value="item.id"
                  :title="item.name"
                  :disabled="item.id !== 'all' && ditem.taskIds == 'all'"
                >
                  {{ item.name }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </div>
          <div class="f_s_0">
            <a-button type="link" size="small" @click="del(sitem.value, dindex)">
              <Icon type="pticon iconfont icon-ptkj-shanchu" />
              <span>删除</span>
            </a-button>
          </div>
        </div>
        <a-button type="link" size="small" @click="add(sitem.value)" icon="plus">添加校验规则</a-button>
      </div>
    </a-form-model>
  </PerfectScrollbar>
</template>

<script>
import { opinionCheckScene } from '../designer/constant';
import { deepClone } from '@framework/vue/utils/util';
import { filter, map, each, findIndex } from 'lodash';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
import WSelect from '../components/w-select';
export default {
  name: 'OpinionCheckRules',
  inject: ['designer', 'workFlowData'],
  props: {
    list: {
      type: Array,
      default: () => []
    }
  },
  data() {
    let formData = {};
    each(opinionCheckScene, item => {
      if (!formData[item.value]) {
        formData[item.value] = [];
      }
    });
    each(this.list, item => {
      formData[item.id].push({
        opinionRuleUuid: item.opinionRuleUuid,
        taskIds: item.taskIds ? item.taskIds.split(';') : []
      });
    });
    return {
      opinionRuleList: [],
      flowTasks: [
        {
          id: 'all',
          name: '全部'
        }
      ],
      rules: {
        opinionRuleUuid: { required: true, message: '校验规则不能为空！', trigger: ['blur'] }
      },
      formData,
      opinionCheckScene //签署意见校验场景
    };
  },
  components: {
    WSelect
  },
  created() {
    this.getOpinionRuleList();
    if (this.workFlowData.id) {
      this.getFlowTasks();
    }
  },
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
    // 获取签署意见校验规则
    getOpinionRuleList() {
      const params = {
        args: JSON.stringify([this.workFlowData.id]),
        serviceName: 'opinionRuleFacadeService',
        methodName: 'getCurrentUserBelongOpinionRuleList'
      };
      this.$axios
        .post('/json/data/services', {
          ...params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.code === 0) {
              const data = res.data.data;
              this.opinionRuleList = data;
            }
          }
        });
    },
    // 获取流程环节
    getFlowTasks() {
      const params = {
        args: JSON.stringify([this.workFlowData.id]),
        serviceName: 'flowSchemeService',
        methodName: 'getFlowTasks'
      };
      this.$axios
        .post('/json/data/services', {
          ...params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.code === 0) {
              const data = res.data.data;
              this.flowTasks = this.flowTasks.concat(data);
            }
          }
        });
    },
    del(sceneId, index) {
      this.formData[sceneId].splice(index, 1);
    },
    add(sceneId) {
      if (!this.formData[sceneId]) {
        this.formData[sceneId] = [];
      }
      this.formData[sceneId].push({
        opinionRuleUuid: '',
        taskIds: ['all']
      });
    },
    selectTaskIds(value, sceneId, index) {
      if (value == 'all') {
        this.formData[sceneId][index].taskIds = ['all'];
      }
    },
    save(callback) {
      this.$refs.form.validate((valid, error) => {
        let data = [];
        each(this.formData, (item, index) => {
          data = data.concat(
            map(item, citem => {
              return {
                id: index,
                opinionRuleUuid: citem.opinionRuleUuid,
                taskIds: citem.taskIds.length ? citem.taskIds.join(';') : 'all'
              };
            })
          );
        });
        callback({ valid, error, data: data });
      });
    }
  }
};
</script>
<style lang="less" scoped>
.opinion-check-scene {
  &-form {
    .title {
      font-size: var(--w-font-size-lg);
      color: var(--w-text-color-dark);
      margin-bottom: var(--w-margin-2xs);
    }
  }
  &-div {
    margin-bottom: var(--w-margin-xs);
  }
  &-item {
    background: var(--w-fill-color-light);
    padding-top: var(--w-padding-3xs);
    padding-right: var(--w-padding-xs);
    border-radius: var(--w-border-radius-base);
    margin-bottom: var(--w-margin-2xs);
  }
}
</style>
