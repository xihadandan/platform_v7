<template>
  <Modal title="导入结果" :container="getContainer" :width="800">
    <slot></slot>
    <template slot="content">
      <a-table
        v-if="sheet != undefined && result != undefined"
        :columns="columns"
        rowKey="rowIndex"
        :data-source="sheetResultMap[sheet][status]"
        :scroll="{ y: 250 }"
      >
        <template slot="title">
          <div style="display: flex; justify-content: space-between; align-items: center">
            <a-input-group compact style="width: 500px">
              <a-select allow-clear v-model="status" style="width: 100px" @change="onChangeStatusSelect">
                <a-select-option value="success">导入成功</a-select-option>
                <a-select-option value="fail">导入错误</a-select-option>
              </a-select>
              <a-select :options="sheetOptions" v-model="sheet" style="width: 200px" @change="onChangeStatusSelect" />
            </a-input-group>
            <div style="padding-right: 12px">
              成功
              <a-badge
                :showZero="true"
                :overflowCount="9999999"
                :count="sheetResultMap[sheet].success.length"
                :number-style="{ backgroundColor: '#52c41a' }"
              />
              失败
              <a-badge :showZero="true" :overflowCount="9999999" :count="sheetResultMap[sheet].fail.length" />
            </div>
          </div>
        </template>
        <template slot="statusSlot" slot-scope="text, record">
          <a-tag v-if="text" color="green">成功</a-tag>
          <a-tag v-else color="red">错误</a-tag>
        </template>
      </a-table>
    </template>
  </Modal>
</template>
<style lang="less"></style>
<script type="text/babel">
import Modal from '@pageAssembly/app/web/lib/modal.vue';

export default {
  name: 'TableExcelImportResultModal',
  props: {
    result: Object
  },
  components: { Modal },
  computed: {
    sheetResultMap() {
      let map = {};
      let sheetResults = this.result.sheetResults;
      sheetResults.forEach(sr => {
        map[sr.sheetName] = { success: [], fail: [] };
        sr.results.forEach(r => {
          map[sr.sheetName][r.ok ? 'success' : 'fail'].push(r);
        });
      });
      return map;
    }
  },
  data() {
    return {
      dataSource: [],
      status: 'success',
      sheet: undefined,
      sheetOptions: [],
      columns: [
        { title: '行', dataIndex: 'rowIndex', width: 100 },
        { title: '状态', dataIndex: 'ok', width: 100, scopedSlots: { customRender: 'statusSlot' } },
        { title: '信息', dataIndex: 'msg', ellipsis: true }
      ],
      sheetResult: {}
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    if (this.result != undefined) {
      this.result.sheetResults.forEach(sr => {
        this.sheetOptions.push({
          label: sr.sheetName,
          value: sr.sheetName
        });
        // 优先展示错误的sheet导入信息
        if (this.sheet == undefined && this.sheetResultMap && this.sheetResultMap[sr.sheetName].fail.length) {
          this.sheet = sr.sheetName;
          this.status = 'fail';
        }
      });

      if (this.sheet == undefined) {
        this.sheet = this.sheetOptions[0].value;
      }
    }
  },
  mounted() {
    // if (this.result) {
    //   let sheetResult = this.result.sheetResults;
    //   if (this.result.fail.length > 0) {
    //     // 优先展示错误的sheet导入信息
    //     this.status = 'fail';
    //     for (let i = 0, len = sheetResult.length; i < len; i++) {
    //       for (let j = 0, jlen = sheetResult[i].results.length; j < jlen; j++) {
    //         if (!sheetResult[i].results[j].ok) {
    //           this.sheet = sheetResult[i].sheetName;
    //           sheetResult[i].results.forEach(r => {
    //             if (!r.ok) {
    //               this.dataSource.push(r);
    //             }
    //           });
    //           sheetResult.forEach(sr => {
    //             this.sheetOptions.push({
    //               label: sr.sheetName,
    //               value: sr.sheetName
    //             });
    //           });
    //           return;
    //         }
    //       }
    //     }
    //   }
    //   sheetResult.forEach(sr => {
    //     this.sheetOptions.push({
    //       label: sr.sheetName,
    //       value: sr.sheetName
    //     });
    //     if (this.sheet == undefined) {
    //       this.sheet = sr.sheetName;
    //       sr.results.forEach(r => {
    //         if ((this.status == 'success' && r.ok) || (this.status == 'fail' && !r.ok)) {
    //           this.dataSource.push(r);
    //         }
    //       });
    //     }
    //   });
    // }
  },
  methods: {
    // onChangeStatusSelect() {
    //   this.dataSource.splice(0, this.dataSource.length);
    //   let sheetResults = this.result.sheetResults;
    //   for (let i = 0, len = sheetResults.length; i < len; i++) {
    //     if (this.sheet == sheetResults[i].sheetName) {
    //       if (this.status == undefined) {
    //         this.dataSource.push(...sheetResults[i].results);
    //         return;
    //       }
    //       sheetResults[i].results.forEach(r => {
    //         if ((this.status == 'success' && r.ok) || (this.status == 'fail' && !r.ok)) {
    //           this.dataSource.push(r);
    //         }
    //       });
    //       return;
    //     }
    //   }
    // },
    getContainer() {
      return this.$el;
    }
  }
};
</script>
