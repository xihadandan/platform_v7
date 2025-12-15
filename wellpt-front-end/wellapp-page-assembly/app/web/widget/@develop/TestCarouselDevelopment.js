import WidgetCarouselDevelopment from '@develop/WidgetCarouselDevelopment';

class TestCarouselDevelopment extends WidgetCarouselDevelopment {
  created() {
    this.$widget.pageContext.handleEvent(`${this.$widget.widget.id}:beforeChange`, (data) => {
      console.log(data)
    })
  }

  mounted() {

  }

  next() {
    this.slickNext()
  }

  prev() {
    this.slickPrev();
    // this.$widget.slickPrev();
    // this.$widget.pageContext.emitEvent('RixGtfCDewAsOCymzDJHIiCpjOxuGbsY:slickPrev');
  }

  goTo() {
    this.slickGoTo(1)
  }

  get META() {
    return {
      name: '测试轮播图二开',
      hook: {
        next: '切换到下一面板',
        prev: '切换到上一面板',
        goTo: '切换到指定面板'
      }
    }
  }
}

export default TestCarouselDevelopment;