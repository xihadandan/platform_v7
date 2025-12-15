'use strict';
import Vue from 'vue';
import Vuex from 'vuex';
// import mutations from '...'
// import actions from '...';
Vue.use(Vuex);

export default function createStore(initState = {}) {
  const state = {
    ...initState
  };

  return new Vuex.Store({
    state
    // actions,
    // mutations
  });
}
