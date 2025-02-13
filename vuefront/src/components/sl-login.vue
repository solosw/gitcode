<template>
  <div class="common-layout" :style="{backgroundImage: 'url(' + bgSrc + ')'}">
    <el-container>
      <el-aside width="50%">
      </el-aside>
      <el-main>
        <div class="register-box">
          <el-container>
            <el-header style="text-align: center; display: flex;flex-direction: column;">
              <div  style="color: #ff0000; font-weight: bold; font-style: italic;">{{title}}</div>
              <div><h2>欢迎登陆</h2></div>
            </el-header>
            <el-main style="height: 300px; display: flex; flex-direction: column; justify-content: center;">
              <el-space direction="vertical" :fill="true" wrap size="large">
                <el-input placeholder="请输入账户" v-model="username"></el-input>
                <el-input type="password" placeholder="请输入密码" v-model="password"></el-input>
                <slot></slot>
              </el-space>
              <el-button type="primary" style="margin-top: 20px;" @click="login">登录</el-button>
            </el-main>
            <el-footer style="text-align: center;" v-if="isHasRegister">
              <router-link  :to="toPageUrl" style="text-decoration: none;">注册页面跳转</router-link>
            </el-footer>
          </el-container>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>


import axios from "axios";
import router from "@/router/index.js";

export default {
  data() {
    return {
      username:'',
      password:'',
    };
  },
  props: {
    bgSrc:{


    },
    otherData:{
      type:Object
    },
    title:{
      required:true
    },
    requestLoginUrl: {
      required:true
    },
    toPageUrl: {
      default:"/register"
    },
    isHasRegister:{
      default: true
    },
    checkFun:{
      type:Function
    },
    successPage:{
      required:true
    },
    afterFun:{
      type:Function
    }


  },
  methods: {
     login() {

       const self=this;
       const allData =Object.assign({
         username:self.username,
         password:self.password,
       },this.otherData)
       if(!self.checkFun()) return
       axios.post(self.requestLoginUrl,allData

       )
           .then((res)=>{
             if(res.data){
               this.afterFun(res.data);
             }else {
               this.afterFun(null);
             }

           })
           .catch(function (error) { // 请求失败处理
             alert("请检查账号密码是否正确")
           });


    }


  },
  created() {}
};
</script>

<style scoped>
.common-layout{
  height: 800px;
  width: 100%;
  background-repeat: repeat;
  background-size: cover;
}

.register-box {
  background-color: white;
  border-radius: 5px;
  height: 400px;
  margin-top: 10%;
  box-shadow: 10px 0 10px rgba(0, 0, 0, 0.2);
}
</style>
