<template>
  <div>
    <h2>账号设置</h2>
    <el-form label-width="100px" style="width: 500px">

      <el-form-item label="头像">
        <div style="display: flex; align-items: center">
          <!-- 当前头像展示 -->
          <el-avatar
              :size="80"
              :src="avatarUrl"
              v-if="avatarUrl"
              style="margin-right: 20px"
          ></el-avatar>
          <el-avatar
              :size="80"
              :icon="UserFilled"
              v-else
              style="margin-right: 20px"
          ></el-avatar>

          <!-- 上传组件 -->
          <el-upload
              action="/back/files/upload"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
              :headers="uploadHeaders "
          >
            <el-button type="primary">更换头像</el-button>
          </el-upload>
        </div>
      </el-form-item>



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
import {UserFilled} from "@element-plus/icons-vue";

export default {
  computed: {
    UserFilled() {
      return UserFilled
    }
  },
  data() {
    return {
      user:JSON.parse(localStorage.getItem("user")).user,
      password: '',
      email: '',
      name:'',
      avatarUrl:'',
      uploadHeaders:{Authorization:'none'}
    };
  },
  methods:{

     handleAvatarSuccess (response)  {
      this.avatarUrl = response.data
      ElMessage.success('头像上传成功')
    },

     beforeAvatarUpload (file)  {
      const isImage = ['image/jpeg', 'image/png','image/jpg'].includes(file.type)
      const isLt2M = file.size / 1024 / 1024 < 5

      if (!isImage) {
        ElMessage.error('仅支持 JPG/PNG 格式!')
      }
      if (!isLt2M) {
        ElMessage.error('图片大小不能超过 5MB!')
      }
      return isImage && isLt2M
    },


    submit(){
        axios.post("/user/changeInfo",{
          id:this.user.id,
          email:this.email,
          password:this.password,
          url:this.avatarUrl
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
    this.avatarUrl=this.user.av
    this.uploadHeaders.Authorization=localStorage.getItem("token")
  }
};
</script>
