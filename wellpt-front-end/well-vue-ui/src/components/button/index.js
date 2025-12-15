import Button from './src/well-button';
Button.install = Vue => {
  Vue.component(Button.name, Button);
};
export default Button;
