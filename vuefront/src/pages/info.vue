<template>
  <div>
    <h2>个人信息设置</h2>
    <el-form label-width="100px" style="width: 600px;">
      <el-form-item label="姓名">
        <el-input v-model="form.name" placeholder="请输入姓名"></el-input>
      </el-form-item>
      <el-form-item label="职务">
        <el-input v-model="form.job" placeholder="请输入职务"></el-input>
      </el-form-item>
      <el-form-item label="公司">
        <el-input v-model="form.company" placeholder="请输入公司名"></el-input>
      </el-form-item>
      <el-form-item label="QQ">
        <el-input v-model="form.qq" placeholder="请输入qq"></el-input>
      </el-form-item>
      <el-form-item label="WeChat">
        <el-input v-model="form.wx" placeholder="请输入微信"></el-input>
      </el-form-item>
      <el-form-item label="简介">
        <el-input type="textarea" v-model="form.des" placeholder="请输入简介"></el-input>
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
      form:{
        name: '',
        des: '',
        job:"",
        wx:"",
        qq:"",
        company:"",
        userId:""
      }

    };
  },
  methods:{
    submit(){
      this.form.userId=this.user.id
      axios.post("/user/changeRealInfo",this.form).then((res)=>{
          if(res.data.status==200){
            ElMessage("SUCCESS")
            this.getInfo()
          }else {
            ElMessage(res.data.message)
          }
      })
    },
    getInfo(){
      axios.post("/user/getRealInfo/"+this.user.id).then((res)=>{
        if(res.data.status==200){
          this.form=res.data.data
        }else {
          ElMessage(res.data.message)
        }
      })
    }
  },
  created() {

      this.getInfo()
  }
};
</script>
