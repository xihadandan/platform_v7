<template>
  <uni-forms-item v-if="!hidden" :label="itemLabel" :name="formModelItemProp"
    :label-position="widget.configuration.labelPosition" :class="widgetClass" :style="itemStyle" :ref="fieldCode">
    <w-org-select :orgUuid="widget.configuration.orgUuid" :isPathValue="widget.configuration.isPathValue"
      :readonly="readonly" :textonly="displayAsLabel || isSubformCell"
      :displayStyle="displayAsLabel || isSubformCell ? 'Label' : widget.configuration.inputDisplayStyle"
      :separator="widget.configuration.separator" :title="$t('modalTitle', widget.configuration.modalTitle)"
      :multiSelect="widget.configuration.multiSelect" :titleDisplay="widget.configuration.choosenTitleDisplay"
      :checkableTypes="widget.configuration.checkableTypes" :orgIdOptions="orgIdOptions" :orgType="enableOrgSelectTypes"
      :bordered="
        !isSubformCell && widget.configuration.uniConfiguration ? widget.configuration.uniConfiguration.bordered : false
      " :onlyShowBizOrg="widget.configuration.onlyShowBizOrg === true"
      :filterNodeData="widget.configuration.filterNode"
      :refetchDataOnOpenModal="widget.configuration.refetchDataOnOpenModal" v-model="formData[widget.configuration.code]"
      @change="onChange" ref="orgSelect" />
  </uni-forms-item>
</template>
<style lang="scss"></style>
<script>
import wOrgSelect from "../../page/w-org-select/w-org-select.vue";
import formElement from "../w-dyform/form-element.mixin";
import formCommonMixin from "../w-dyform/form-common.mixin";

export default {
  mixins: [formElement, formCommonMixin],
  props: {},
  components: { wOrgSelect },
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
        if (this.widget.configuration.orgSelectTypes[i].enable) {
          opt.push(this.widget.configuration.orgSelectTypes[i]);
        }
      }
      return opt;
    },
  },
  data() {
    if (!this.widget.configuration.hasOwnProperty("uniConfiguration")) {
      this.$set(this.widget.configuration, "uniConfiguration", { bordered: false });
    }
    return {};
  },
  beforeCreate() { },
  created() { },
  beforeMount() { },
  mounted() { },
  methods: {
    onChange({ value, label, nodes }) {
      console.log("组织弹出框选择变更：", arguments);
      if (this.widget.configuration.displayValueField) {
        if (label && typeof label !== "string") {
          label = label.join(this.widget.configuration.separator || ";");
        }
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
    onFilter({ searchValue, comparator, source, ignoreCase }) {
      return new Promise((resolve, reject) => {
        if (source != undefined) {
          // 由外部提供数据源进行判断
          let sources = source.split(";");
          if (comparator == "like") {
            this.$refs.orgSelect.fetchAllOrgNodesByKeys(sources).then((nodes) => {
              for (let i = 0, len = nodes.length; i < len; i++) {
                if (
                  ignoreCase
                    ? nodes[i].title.toLowerCase().indexOf(searchValue) != -1
                    : nodes[i].title.indexOf(searchValue) != -1
                ) {
                  resolve(true);
                  return;
                }
              }
              resolve(false);
            });
            return;
          } else {
            let searchValues = searchValue.split(";");
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
    },
  },
};
</script>
