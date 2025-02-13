<script>
import axios from "axios";

export default {
  data() {
    return {
      formLabelAlign: {
        name: '',
        password: '',
        repeatPassword: '',
        email: '',

      },
      statuses:["wait","wait","wait"]

    }
  },

  props: {},

  methods: {
    initEnv(){
      if(this.formLabelAlign.password!=this.formLabelAlign.repeatPassword){
        alert("两次密码不同")
        return;
      }
      var data={
        name:this.formLabelAlign.name,
        email:this.formLabelAlign.email,
        password: this.formLabelAlign.password
      }

      axios.post("/initEnv",data).then((res)=>{
        if(res.data.status==200){
          alert("初始化成功");
          window.location.href="/index";
        }else alert(res.data.message)
      })
    },
    checkEnv(){
      axios.get("/getEnvStatus").then((res)=>{

        if(res.data.status==200){
          this.statuses=res.data.data;
        }

      })
    }
  },
  created() {

  }
}
</script>

<template>
  <div class="form-container">
    <el-form ref="form" :label-position="'top'" label-width="120px" :model="formLabelAlign" style="width: 100%; max-width: 600px; margin: auto;">
      <el-form-item>
        <el-steps  finish-status="success" simple style="margin-bottom: 20px;">
          <el-step title="SSH环境配置" :status="statuses[0]"></el-step>
          <el-step title="SSH密钥认证" :status="statuses[1]"></el-step>
          <el-step title="Git环境配置" :status="statuses[2]"></el-step>
        </el-steps>
        <el-button type="primary" style="display: block; margin: 0 auto 20px;" @click="checkEnv">检查配置</el-button>
      </el-form-item>
      <el-form-item label="Git超级管理员名称">
        <el-input v-model="formLabelAlign.name"></el-input>
      </el-form-item>
      <el-form-item label="Git超级管理员密码">
        <el-input v-model="formLabelAlign.password" show-password></el-input>
      </el-form-item>
      <el-form-item label="重复密码">
        <el-input v-model="formLabelAlign.repeatPassword" show-password></el-input>
      </el-form-item>
      <el-form-item label="Git超级管理员邮箱">
        <el-input v-model="formLabelAlign.email"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" style="display: block; margin: 0 auto;" @click="initEnv">初始化</el-button>
      </el-form-item>
    </el-form>
  </div>

</template>

<style scoped>
.form-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.el-form {
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  background-color: white;
}
</style>
