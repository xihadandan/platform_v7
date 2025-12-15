<template>
  <div>
    <template v-if="!displayModal">
      <a-select
        @change="onChange"
        show-search
        :filter-option="filterSelectOption"
        v-model="selectedValue"
        allow-clear
        :style="{ width: selectedValue != undefined && showEditButton ? 'calc(100% - 35px)' : '100%' }"
      >
        <template slot="suffixIcon" v-if="loading">
          <a-icon type="loading" />
        </template>
        <a-select-opt-group v-for="(group, g) in optionGroups" :key="'form_group_opt_' + g">
          <div slot="label">
            <Icon :type="group.icon" @click.native.stop="e => redirectUrl(e, '/module/assemble/' + group.value)" />
            {{ group.label }}
          </div>
          <a-select-option v-for="(opt, i) in group.options" :value="opt.value" :key="'form_option_key_' + i">
            {{ opt.label }}
          </a-select-option>
        </a-select-opt-group>
      </a-select>
      <a-button
        v-show="selectedFormUuid != undefined && showEditButton"
        size="small"
        type="link"
        icon="form"
        @click.stop="e => redirectUrl(e, '/dyform-designer/index?uuid=' + selectedFormUuid)"
      />
    </template>
    <template v-else>
      <span @mouseenter="onmouseenter" @mouseleave="onmouseleave">
        <a-input v-model="selectedFormName" @click="modalVisible = true" :readOnly="true">
          <template slot="suffix">
            <a-icon type="close-circle" theme="filled" v-if="hoverInput && selectedFormUuid != undefined" @click="onClickRemoveSelect" />
            <a-icon :type="loading ? 'loading' : modalVisible ? 'folder-open' : 'folder'" v-else @click="modalVisible = true" />
          </template>
        </a-input>
        <a-modal :maskClosable="false" :z-index="10000" title="请选择" v-model="modalVisible" :footer="null" width="calc(100% - 600px)">
          <a-table
            :columns="columns"
            :data-source="vFilterFormRows"
            rowKey="uuid"
            :pagination="false"
            :customRow="customRow"
            size="small"
            :scroll="{ y: 300 }"
            @change="onTableChange"
          >
            <template slot="title">
              <div style="display: flex; align-items: center; justify-content: space-between">
                <div style="align-self: center; line-height: 30px">
                  <template v-if="rowSelection.selectedRows.length > 0">
                    已选:
                    <a-tag class="primary-color" :closable="true" @close="onClickRemoveSelect">
                      {{ rowSelection.selectedRows[0].name }} (v{{ rowSelection.selectedRows[0].version }})
                    </a-tag>
                  </template>
                </div>

                <a-input-group style="display: inline-block; width: 400px" compact v-if="fetchRelaApp">
                  <a-select
                    placeholder="选择模块"
                    :options="optionGroups"
                    style="width: 200px"
                    :filter-option="filterSelectOption"
                    show-search
                    allow-clear
                    :getPopupContainer="getPopupContainerNearest()"
                    v-model="selectModuleUuid"
                  ></a-select>
                  <a-input style="width: 200px" v-model.trim="searchText" placeholder="搜索表单名称 / 表单ID" allow-clear />
                </a-input-group>
                <a-input v-else style="width: 200px" v-model.trim="searchText" placeholder="搜索表单名称 / 表单ID" allow-clear />
              </div>
            </template>
            <template slot="nameSlot" slot-scope="text, record">
              <!-- <a :href="'/dyform-designer/index?uuid=' + record.uuid" target="_blank">{{ text }} (v{{ record.version }})</a> -->
              {{ text }} (v{{ record.version }})
            </template>
            <!-- <template slot="formTypeSlot" slot-scope="text">
              {{ text == 'P' ? '存储表单' : '展现表单' }}
            </template> -->
            <template slot="moduleNameSlot" slot-scope="text, record">
              <a :href="'/module/assemble/' + record.moduleUuid" target="_blank">{{ text }}</a>
            </template>
            <template slot="operationSlot" slot-scope="text, record, index">
              <a-button size="small" @click="handleJumpDesigner(record)">查看</a-button>
            </template>
          </a-table>
        </a-modal>
      </span>
    </template>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { filterSelectOption } from '@framework/vue/utils/function';
export default {
  name: 'DyformDefinitionSelect',
  inject: ['appId', 'subAppIds'],
  props: {
    byAppId: String,
    allVersion: {
      type: Boolean,
      default: true
    },

    valuePropKey: {
      type: String,
      default: 'uuid'
    },

    formType: {
      type: [String, Array],
      default: ['P', 'V']
    },

    value: String,

    showEditButton: {
      type: Boolean,
      default: true
    },

    displayModal: {
      type: Boolean,
      default: false
    },

    filterEmptyModule: {
      type: Boolean,
      default: true
    },

    fetchRelaApp: {
      type: Boolean,
      default: true
    }
  },
  components: {},
  computed: {
    vFilterFormRows() {
      let rows = [];
      this.optionGroups.forEach(group => {
        if (this.selectModuleUuid == undefined || this.selectModuleUuid == group.value) {
          group.options.forEach(opt => {
            let data = opt.data;
            if (
              this.searchText == undefined ||
              data.id.toLowerCase().indexOf(this.searchText.toLowerCase()) != -1 ||
              data.name.toLowerCase().indexOf(this.searchText.toLowerCase()) != -1
            ) {
              rows.push(data);
            }
          });
        }
      });
      return rows;
    }
  },
  data() {
    return {
      selectedValue: this.value,
      options: [],
      definitionMap: {},
      optionGroups: [],
      loading: false,
      selectedFormUuid: undefined,
      selectedFormName: undefined,
      selectModuleUuid: undefined,
      modalVisible: false,
      hoverInput: false,
      columns: [
        {
          title: '名称',
          dataIndex: 'name',
          scopedSlots: { customRender: 'nameSlot' },
          customCell: this.customCell
        },
        {
          title: 'ID',
          dataIndex: 'id',
          customCell: this.customCell
        },
        {
          title: '归属模块',
          dataIndex: 'moduleName',
          scopedSlots: { customRender: 'moduleNameSlot' },
          width: 200,
          customCell: this.customCell
        },
        {
          title: '操作',
          dataIndex: 'operation',
          width: 80,
          scopedSlots: { customRender: 'operationSlot' },
          customCell: this.customCell
        }
        // {
        //   title: '表单类型',
        //   width: 100,
        //   dataIndex: 'formType',
        //   scopedSlots: { customRender: 'formTypeSlot' },
        //   customCell: this.customCell
        // }
      ],
      searchText: undefined,
      rowSelection: { selectedRows: [], selectedRowKeys: [], type: 'radio', onChange: this.onChangeRowSelect }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.fetchDyformDefinition();
  },
  methods: {
    customCell(record, rowIndex) {
      return {
        on: {
          click: event => {
            if (event.target.nodeName !== 'A') {
              let lastedSelected = undefined;
              if (this.rowSelection.selectedRowKeys.length) {
                lastedSelected = this.rowSelection.selectedRowKeys[0];
              }
              this.rowSelection.selectedRowKeys.splice(0, this.rowSelection.selectedRowKeys.length);
              this.rowSelection.selectedRows.splice(0, this.rowSelection.selectedRows.length);
              if (lastedSelected != record.uuid) {
                this.rowSelection.selectedRowKeys.push(record.uuid);
                this.rowSelection.selectedRows.push(record);
              }
              this.onModalOkSelectDone(false);
            }
          }
        }
      };
    },
    handleJumpDesigner(record) {
      const url = '/dyform-designer/index?uuid=' + record.uuid;
      window.open(url, '_blank');
    },
    customRow(row, index) {
      return {
        on: {
          // 事件
          click: event => {
            // this.rowSelection.selectedRows = [row];
            // this.rowSelection.selectedRowKeys = [row.uuid];
          }
        }
      };
    },
    onmouseenter() {
      this.hoverInput = true;
    },
    onmouseleave() {
      this.hoverInput = false;
    },
    onTableChange(pagination, filters) {},
    getPopupContainerNearest(triggerNode) {
      return triggerNode => {
        return triggerNode.parentNode;
      };
    },
    onModalOkSelectDone(closeModal = true) {
      this.selectedFormUuid = undefined;
      this.selectedValue = undefined;
      this.selectedFormName = undefined;
      if (this.rowSelection.selectedRows && this.rowSelection.selectedRows.length > 0) {
        this.selectedFormUuid = this.rowSelection.selectedRows[0].uuid;
        this.selectedValue = this.rowSelection.selectedRows[0][this.valuePropKey];
        this.selectedFormName = this.rowSelection.selectedRows[0].name + ` (v${this.rowSelection.selectedRows[0].version})`;
      }
      this.$emit('input', this.selectedValue);
      this.$emit('change', this.selectedValue, this.selectedValue ? this.definitionMap[this.selectedValue] : {});
      if (closeModal) {
        this.modalVisible = false;
      }
    },
    onChangeRowSelect(selectedRowKeys, selectedRows) {
      this.rowSelection.selectedRows = selectedRows;
      this.rowSelection.selectedRowKeys = selectedRowKeys;
    },
    redirectUrl(e, url) {
      window.open(url, '_blank');
    },
    filterSelectOption,
    fetchDyformDefinition() {
      this.loading = true;
      this.optionGroups.splice(0, this.optionGroups.length);
      this.rowSelection.selectedRowKeys.splice(0, this.rowSelection.selectedRowKeys.length);
      this.rowSelection.selectedRows.splice(0, this.rowSelection.selectedRows.length);
      let appId = this.byAppId != undefined ? this.byAppId : this.appId,
        groupMap = {},
        fetchRelaApp = this.fetchRelaApp;
      this.$tempStorage.getCache(
        'rela_modules_' + appId + '_' + fetchRelaApp,
        () => {
          return new Promise((resolve, reject) => {
            $axios[(this.subAppIds != undefined && this.subAppIds.length > 0) || fetchRelaApp ? 'get' : 'post'](
              `/proxy/api/app/module/${
                this.subAppIds != undefined && this.subAppIds.length > 0
                  ? 'listModuleUnderSystem'
                  : fetchRelaApp
                  ? 'queryRelaModuleByModuleId'
                  : 'listById'
              }`,
              (this.subAppIds != undefined && this.subAppIds.length > 0) || fetchRelaApp
                ? {
                    params: {
                      system: appId,
                      moduleId: appId
                    }
                  }
                : [appId]
            ).then(({ data }) => {
              let items = data.data || [];
              resolve(items);
            });
          });
        },
        items => {
          let ids = [];
          items.forEach(item => {
            groupMap[item.id] = { label: item.name, value: item.uuid, icon: item.icon || 'folder', options: [] };
            this.optionGroups.push(groupMap[item.id]);
            ids.push(item.id);
          });

          if (ids.length) {
            this.$tempStorage.getCache(
              'rela_modules_' + appId + '_dyform' + '_' + fetchRelaApp,
              () => {
                return new Promise((resolve, reject) => {
                  $axios.post(`/proxy/api/dyform/definition/queryFormDefinitionNoJsonByModuleIds`, ids).then(({ data }) => {
                    let definitions = [];
                    if (data.code == 0 && data.data) {
                      data.data.forEach(def => {
                        definitions.push({
                          uuid: def.uuid,
                          name: def.name,
                          tableName: def.tableName,
                          version: def.version,
                          id: def.id,
                          formType: def.formType,
                          moduleId: def.moduleId
                        });
                      });
                    }
                    resolve(definitions);
                  });
                });
              },
              list => {
                let max = {},
                  idOption = {};
                this.loading = false;
                if (this.value != undefined) {
                  this.selectedValue = this.value;
                }
                try {
                  for (let i = 0, len = list.length; i < len; i++) {
                    if (
                      this.formType == undefined ||
                      (this.formType != undefined &&
                        (list[i].formType == this.formType || (Array.isArray(this.formType) && this.formType.includes(list[i].formType))))
                    ) {
                      let key = list[i][this.valuePropKey],
                        allVersion = this.allVersion;
                      if ('id' == this.valuePropKey) {
                        // 返回ID的情况下，只取最新版本数据
                        allVersion = false;
                      }
                      if (key == this.selectedValue) {
                        this.selectedFormUuid = list[i].uuid;
                        this.selectedFormName = list[i].name + ` (v${list[i].version})`;
                        this.rowSelection.selectedRowKeys.push(list[i].uuid);
                        this.rowSelection.selectedRows.push(list[i]);
                      }
                      if (allVersion || max[list[i].id] == undefined) {
                        this.definitionMap[key] = list[i];
                        if (groupMap[list[i].moduleId]) {
                          idOption[list[i].id] = {
                            label: list[i].name + ` (v${list[i].version})`,
                            value: key,
                            data: list[i]
                          };
                          list[i].moduleName = groupMap[list[i].moduleId].label;
                          list[i].moduleUuid = groupMap[list[i].moduleId].value;
                          groupMap[list[i].moduleId].options.push(idOption[list[i].id]);
                        }
                        if (!this.allVersion) {
                          max[list[i].id] = list[i];
                        }
                      } else if (Number(max[list[i].id].version) < Number(list[i].version) && idOption[list[i].id]) {
                        max[list[i].id] = list[i];
                        this.definitionMap[key] = list[i];
                        idOption[list[i].id].label = list[i].name + ` (v${list[i].version})`;
                        idOption[list[i].id].value = key;
                        idOption[list[i].id].data = list[i];
                      }
                    }
                  }
                } catch (error) {
                  console.log(error);
                }

                if (this.filterEmptyModule) {
                  for (let x = 0; x < this.optionGroups.length; x++) {
                    if (this.optionGroups[x].options.length == 0) {
                      this.optionGroups.splice(x--, 1);
                    }
                  }
                }
              }
            );
          }
        }
      );
    },
    fetchDyformDefinitionJSON(formUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .post(`/proxy/api/dyform/definition/getFormDefinitionByUuid?formUuid=${formUuid}`, {})
          .then(({ data }) => {
            resolve(data);
          })
          .catch(error => {});
      });
    },
    onClickRemoveSelect() {
      this.onCloseSelectTag();
      this.selectedFormName = undefined;
      this.selectedFormUuid = undefined;
      this.selectedValue = undefined;
      this.$emit('input', undefined);
      this.$emit('change', undefined, {});
    },
    onCloseSelectTag() {
      this.rowSelection.selectedRowKeys.splice(0, this.rowSelection.selectedRowKeys.length);
      this.rowSelection.selectedRows.splice(0, this.rowSelection.selectedRows.length);
    },
    onChange(e) {
      this.selectedValue = e;
      this.selectedFormUuid = this.selectedValue ? this.definitionMap[this.selectedValue].uuid : undefined;
      this.$emit('input', e);
      this.$emit('change', this.selectedValue, this.selectedValue ? this.definitionMap[this.selectedValue] : {});
    }
  },

  watch: {
    value: {
      handler(v, o) {
        if (this.selectedValue == undefined && v != undefined && o == undefined) {
          this.selectedValue = v;
          this.selectedFormUuid = v;
          this.fetchDyformDefinitionJSON(v).then(d => {
            this.selectedFormName = d.name;
            this.rowSelection.selectedRows.splice(0, this.rowSelection.selectedRows.length, {
              name: d.name,
              version: d.version
            });
          });
        }
      }
    }
  }
};
</script>
