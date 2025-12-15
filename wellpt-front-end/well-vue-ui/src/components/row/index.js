import Row from './src/well-row';
Row.install = Vue => {
  Vue.component(Row.name, Row);
};
export default Row;
