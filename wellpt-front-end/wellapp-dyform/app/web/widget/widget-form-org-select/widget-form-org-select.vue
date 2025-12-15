<template>
  <a-form-model-item
    :style="itemStyle"
    :prop="formModelItemProp"
    :rules="rules"
    :ref="widget.configuration.code"
    :label="itemLabel"
    :colon="displayAsLabel"
    :class="widgetClass"
  >
    <OrgSelect
      :orgUuid="widget.configuration.orgUuid"
      :isPathValue="widget.configuration.isPathValue"
      :readonly="readonly"
      :textonly="displayAsLabel"
      :displayStyle="displayAsLabel ? 'Label' : widget.configuration.inputDisplayStyle"
      :separator="widget.configuration.separator"
      :title="$t('modalTitle', widget.configuration.modalTitle)"
      :multiSelect="widget.configuration.multiSelect"
      :titleDisplay="widget.configuration.choosenTitleDisplay"
      :checkableTypes="widget.configuration.checkableTypes"
      :orgIdOptions="orgIdOptions"
      :orgType="enableOrgSelectTypes"
      v-model="formData[widget.configuration.code]"
      :onlyShowBizOrg="widget.configuration.onlyShowBizOrg === true"
      :filterNodeData="widget.configuration.filterNode"
      :refetchDataOnOpenModal="widget.configuration.refetchDataOnOpenModal"
      enableCache
      @change="onChange"
      @checkedValueInitFinish="onCheckedValueInitFinish"
      ref="orgSelect"
    />
    <!-- <span v-show="displayAsLabel">{{ displayValue() }}</span> -->
  </a-form-model-item>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import formMixin from '../mixin/form-common.mixin';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
export default {
  extends: FormElement,
  name: 'WidgetFormOrgSelect',
  mixins: [widgetMixin, formMixin],
  props: {
    orgSelected: [Object, String, Array],
    showAsLabel: Boolean
  },
  data() {
    return {};
  },
  beforeCreate() {},
  components: { OrgSelect },
  computed: {
    orgIdOptions() {
      let map = {};
      if (this.widget.configuration.orgId != undefined) {
        map[this.widget.configuration.orgId] = this.widget.configuration.bizOrgIds || [];
      }
      return map;
    },
    enableOrgSelectTypes() {
      let opt = [];
      for (let i = 0, len = this.widget.configuration.orgSelectTypes.length; i < len; i++) {
        if (this.widget.configuration.orgSelectTypes[i].enable !== false) {
          opt.push(this.widget.configuration.orgSelectTypes[i]);
        }
      }
      return opt;
    }
  },
  watch: {
    orgSelected: {
      deep: true,
      handler(v) {
        this.$refs.orgSelect.setValue(v);
      }
    }
  },
  created() {},
  methods: {
    displayValue() {
      if (this.$refs.orgSelect) {
        let nodes = this.$refs.orgSelect.valueNodes;
        if (nodes != undefined && nodes.length > 0) {
          let labels = [];
          nodes.forEach(n => {
            labels.push(n.title);
          });
          return labels.join(';');
        }
      }
      return '';
    },
    setValue(v) {
      this.formData[this.widget.configuration.code] = v;
      if (this.$refs.orgSelect) {
        this.$refs.orgSelect.setValue(v);
      }
      this.clearValidate();
    },
    onCheckedValueInitFinish() {
      this.emitChange();
    },
    onChange({ value, label, nodes }) {
      console.log('组织弹出框选择变更：', arguments);
      let _keyEntities = this.$refs.orgSelect.getTreeKeyEntities();
      if (this.widget.configuration.displayValueField) {
        // 设置关联显示值字段
        this.form.setFieldValue(this.widget.configuration.displayValueField, label);
      }
      if (this.widget.configuration.fieldMapping) {
        let targetFieldValues = {};
        for (let field of this.widget.configuration.fieldMapping) {
          let { attrKeyType, attrKey, targetField } = field;
          if (targetFieldValues[targetField] == undefined) {
            targetFieldValues[targetField] = [];
          }
          if (attrKeyType == 'orgField' && ['key', 'keyPath', 'title', 'titlePath', 'shortTitle'].includes(attrKey)) {
            for (let n of nodes) {
              if (n[attrKey] != undefined) {
                targetFieldValues[targetField].push(n[attrKey]);
              }
            }
          } else {
            for (let n of nodes) {
              if (
                attrKeyType == 'orgField' &&
                [
                  'jobName',
                  'jobId',
                  'deptName',
                  'deptId',
                  'unitName',
                  'unitId',
                  'bizOrgRoleId',
                  'bizOrgRoleName',
                  'bizOrgDimId',
                  'bizOrgDimName'
                ].includes(attrKey) &&
                (n.key.startsWith('S_') ||
                  n.key.startsWith('D_') ||
                  n.key.startsWith('J_') ||
                  n.type == 'bizRole' ||
                  n.key.startsWith('BIZ_') ||
                  n.key.startsWith('BOD_'))
              ) {
                // 解析出最近的类型节点值
                let keys = n.keyPath.split('/').reverse(),
                  titles = n.titlePath.split('/').reverse();

                for (let i = 0, len = keys.length; i < len; i++) {
                  if (['jobName', 'jobId'].includes(attrKey)) {
                    if (keys[i].startsWith('J_')) {
                      targetFieldValues[targetField].push(attrKey == 'jobName' ? titles[i] : keys[i]);
                      break;
                    }
                  }
                  if (['deptName', 'deptId'].includes(attrKey)) {
                    if (keys[i].startsWith('D_')) {
                      targetFieldValues[targetField].push(attrKey == 'deptName' ? titles[i] : keys[i]);
                      break;
                    } else if (keys[i].startsWith('BIZ_')) {
                      let treeNode = _keyEntities.get(keys[i]);
                      let d = treeNode.node.componentOptions.propsData.dataRef;
                      if (d.type == 'dept') {
                        // 业务组织部门
                        targetFieldValues[targetField].push(attrKey == 'deptName' ? titles[i] : keys[i]);
                        break;
                      }
                    }
                  }
                  if (['unitName', 'unitId'].includes(attrKey)) {
                    if (keys[i].startsWith('S_')) {
                      targetFieldValues[targetField].push(attrKey == 'unitName' ? titles[i] : keys[i]);
                      break;
                    } else if (keys[i].startsWith('BIZ_')) {
                      let treeNode = _keyEntities.get(keys[i]);
                      let d = treeNode.node.componentOptions.propsData.dataRef;
                      if (d.type == 'unit') {
                        // 业务组织单位
                        targetFieldValues[targetField].push(attrKey == 'unitName' ? titles[i] : keys[i]);
                        break;
                      }
                    }
                  }
                  if (
                    ['bizOrgRoleName', 'bizOrgRoleId'].includes(attrKey) &&
                    (n.type == 'bizRole' || n.key.startsWith('BIZ_') || n.key.startsWith('BOD_'))
                  ) {
                    if (!keys[i].startsWith('BOD_') && !keys[i].startsWith('BIZ_')) {
                      // 非业务组织节点类型
                      targetFieldValues[targetField].push(attrKey == 'bizOrgRoleName' ? titles[i] : keys[i]);
                      break;
                    }
                  }

                  if (['bizOrgDimId', 'bizOrgDimName'].includes(attrKey)) {
                    if (keys[i].startsWith('BOD_')) {
                      targetFieldValues[targetField].push(attrKey == 'bizOrgDimName' ? titles[i] : keys[i]);
                      break;
                    }
                  }
                }
              } else if (n.data && n.data[attrKey] != undefined) {
                targetFieldValues[targetField].push(n.data[attrKey]);
              }
            }
          }
        }
        for (let f in targetFieldValues) {
          this.form.setFieldValue(f, Array.from(new Set(targetFieldValues[f])).join(';'));
        }
      }
      this.emitChange();
    },

    // setValueByLabel(labels) {
    //   return new Promise((resolve, reject) => {
    //     if (labels != undefined) {
    //       let titles = Array.isArray(labels) ? JSON.parse(JSON.stringify(labels)) : labels.split(';');
    //       this.$refs.orgSelect.getNodesByTitles(titles).then(nodes => {
    //         if (nodes) {
    //           let values = [],
    //             labels = [];
    //           nodes.forEach(n => {
    //             values.push(n.key);
    //             labels.push(n.title);
    //           });

    //           if (values.length) {
    //             this.setValue(values.join(';'));
    //             resolve({
    //               value: values.join(';'),
    //               label: labels.join(';')
    //             });
    //           }
    //         }
    //       });
    //     } else {
    //       resolve();
    //     }
    //   });
    // }
    onFilter({ searchValue, comparator, source, ignoreCase }) {
      return new Promise((resolve, reject) => {
        if (source != undefined) {
          // 由外部提供数据源进行判断
          let sources = source.split(';');
          if (comparator == 'like') {
            this.$refs.orgSelect.fetchAllOrgNodesByKeys(sources).then(nodes => {
              for (let i = 0, len = nodes.length; i < len; i++) {
                if (ignoreCase ? nodes[i].title.toLowerCase().indexOf(searchValue) != -1 : nodes[i].title.indexOf(searchValue) != -1) {
                  resolve(true);
                  return;
                }
              }
              resolve(false);
            });
            return;
          } else {
            let searchValues = searchValue.split(';');
            for (let i = 0, len = sources.length; i < len; i++) {
              if (searchValues.includes(sources[i])) {
                resolve(true);
                return;
              }
            }
            resolve(false);
          }
        }
        //TODO: 判断本组件值是否匹配
        resolve(false);
      });
    }
  },
  mounted() {
    this.emitChange({}, false);
    if (this.$refs.orgSelect && this.orgSelected != undefined) {
      this.$refs.orgSelect.setValue(this.orgSelected);
    }
    if (!this.widget.configuration.hasOwnProperty('isPathValue')) {
      this.widget.configuration.isPathValue = true;
    }
  }
};
</script>
<style lang="less" scoped>
.org-select-component {
  white-space: normal;
}
</style>
