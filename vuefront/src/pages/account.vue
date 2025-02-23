<template>
  <div>
    <h2>账号设置</h2>
    <el-form label-width="100px" style="width: 500px">
      <el-form-item label="用户名">
        <el-input v-model="name" disabled></el-input>
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="password" placeholder="新密码" type="password"></el-input>
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="email" placeholder="请输入邮箱"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import axios from "axios";
import {ElMessage} from "element-plus";

export default {
  data() {
    return {
      user:JSON.parse(localStorage.getItem("user")).user,
      password: '',
      email: '',
      name:''
    };
  },
  methods:{
    submit(){
        axios.post("/user/changeInfo",{
          id:this.user.id,
          email:this.email,
          password:this.password
        }).then((res)=>{
          if(res.data.status==200){
            console.log(res.data.data)
            this.user=res.data.data
            this.name=this.user.name
            this.email=this.user.email
            this.password=this.user.password
            ElMessage("Success")
            var data={
              user:this.user
            }
            localStorage.setItem("user",JSON.stringify(data))
          }else {
            ElMessage(res.data.message)
          }
        })
    }
  },

  created() {

    this.user=JSON.parse(localStorage.getItem("user")).user
      this.name=this.user.name
      this.email=this.user.email
      this.password=this.user.password
  }
};
</script>
