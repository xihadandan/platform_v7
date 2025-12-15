import WidgetStepsDevelopment from '@develop/WidgetStepsDevelopment';

class TestStepsDevelopment extends WidgetStepsDevelopment {
  next() {
    this.stepNext()
  }

  prev() {
    this.stepPrev();
  }

  get META() {
    return {
      name: '测试步骤条二开',
      hook: {
        next: '切换到下一步骤',
        prev: '切换到上一步骤',
      }
    }
  }
}

export default TestStepsDevelopment;