import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import './assets/gloable.css'
import request from "@/utils/request";
import store from "@/store/store";

Vue.config.productionTip = false

Vue.use(ElementUI,{size:"small"});

Vue.prototype.request=request

import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
//use
Vue.use(mavonEditor)

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
