import Col from './src/well-col';
Col.install = Vue => {
  Vue.component(Col.name, Col);
};
export default Col;
