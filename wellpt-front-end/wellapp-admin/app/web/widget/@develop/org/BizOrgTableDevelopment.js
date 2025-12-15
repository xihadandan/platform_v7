import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import moment from 'moment';

class BizOrgTableDevelopment extends WidgetTableDevelopment {


  onTableRowDataChange(data) {
    for (let item of data.data) {
      if (item.NEVER_EXPIRE == '0' && item.EXPIRE_TIME) {
        if (moment(item.EXPIRE_TIME, 'yyyy-MM-DD HH:mm:ss').add(1, 'd').isBefore(moment())) {
          item.EXPIRED = '1'
        }
      }
    }
  }


  get META() {
    return {

      name: '业务组织表格二开',

    };
  }
}

export default BizOrgTableDevelopment;
