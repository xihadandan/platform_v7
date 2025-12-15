class currentWindow {
  constructor(w) {
    this.winObj = w;
  }
  close() {
    return new Promise((resolve, reject) => {
      let r = this.winObj.close();
      if (r != undefined && r instanceof Promise) {
        r.then(() => {
          resolve();
        })
      }
    })
  }

}

export default {
  provide() {
    return {
      currentWindow: new currentWindow(this)
    }
  },

  methods: {
    close() {

    }
  }

}
