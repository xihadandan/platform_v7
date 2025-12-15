<template>
  <a-tabs v-model="currentKey" class="holiday_arrangement radio-group-style">
    <a-tab-pane v-for="(item, index) in tabs" :key="index" :tab="item.text">
      <PerfectScrollbar style="height: 100%; margin-right: -20px; padding-right: 12px">
        <div
          v-for="(citem, cindex) in item.list"
          :key="cindex"
          :class="['year-item', activeYear == citem.year ? 'active' : '']"
          @click="yearClick(citem.year)"
        >
          <span>{{ citem.year }}</span>
          <span>{{ citem.count }}</span>
        </div>
      </PerfectScrollbar>
    </a-tab-pane>
  </a-tabs>
</template>

<script type="text/babel">
import './timeservice.less';
import { each } from 'lodash';
import moment from 'moment';
export default {
  name: 'HolidayArrangeNav',
  props: {},
  components: {},
  inject: ['pageContext', 'vPageState', '$event'],
  data() {
    return {
      activeYear: '',
      currentKey: 0,
      tabs: [
        {
          text: '当年',
          list: []
        },
        {
          text: '历年',
          list: []
        }
      ],
      wTemplate: {
        $options: {
          methods: {
            saveForm: this.saveForm
          },
          META: {
            method: {
              saveForm: '保存表单'
            }
          }
        }
      }
    };
  },
  META: {
    method: {
      saveForm: '保存表单'
    }
  },
  computed: {},
  watch: {},
  beforeCreate() {},
  created() {
    this.getListAllYear();
  },
  beforeMount() {
    let _this = this;
  },
  mounted() {
    this.activeYear = moment().year();

    this.pageContext.handleEvent('refreshHolidayArrangeYear', () => {
      this.getListAllYear();
    });
  },
  methods: {
    moment,
    getListAllYear() {
      let _this = this;
      let currentList = [];
      let historyList = [];
      $axios.get('/api/ts/holiday/schedule/listAllYear').then(({ data }) => {
        if (data.code == 0 && data.data) {
          each(data.data, (item, index) => {
            if (data.data.length - index <= 5) {
              currentList.push(item);
            } else {
              historyList.push(item);
            }
          });
          each(this.tabs, (item, index) => {
            if (index == 1) {
              item.list = historyList;
            } else {
              item.list = currentList;
            }
          });
        }
      });
    },
    yearClick(year) {
      this.activeYear = year;
      this.pageContext.emitEvent(`SetHolidayArrangeYear`, { year: year });
    }
  }
};
</script>
