import WidgetChartDevelopment from '@develop/WidgetChartDevelopment';

import { getSetting } from '@workflow/app/web/template/report/utils.js';

class FlowChartDevelopmentBase extends WidgetChartDevelopment {

  getSetting() {
    return getSetting();
  }

}

export default FlowChartDevelopmentBase;