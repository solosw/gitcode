<template>
  <div class="create-organization">
    <el-form :model="form" label-width="120px" ref="organizationForm" style="height: 100%;" label-position="top">

      <el-form-item label="头像">
        <div style="display: flex; align-items: center">
          <!-- 当前头像展示 -->
          <el-avatar
              :size="80"
              :src="form.avatarUrl"
              v-if="form.avatarUrl"
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
              :headers="uploadHeaders"
          >
            <el-button type="primary">组织头像</el-button>
          </el-upload>
        </div>
      </el-form-item>


      <el-form-item label="名称" prop="name" :rules="[{ required: true, message: '请输入组织名称', trigger: 'blur' }]">
        <el-input v-model="form.name"></el-input>
      </el-form-item>
      <el-form-item label="描述" prop="description" >
        <el-input v-model="form.description" type="textarea"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">创建组织</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { ref } from 'vue';
import axios from 'axios';
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
      form:{
        name: '',
        description:'',
        userId:JSON.parse(localStorage.getItem("user")).user.id,
        avatarUrl:''
      },
      uploadHeaders:{Authorization:'none'}
    }
  },

  props: {},

  methods: {
    handleAvatarSuccess (response)  {
      this.form.avatarUrl = response.data
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
    onSubmit(){


        axios.post("/user/createOrganization",this.form).then((res)=>{
          if(res.data.status==200){
            location.href="/index"
          }else {
            ElMessage(res.data.message)
          }
        })

    },

  },
  created() {
      this.uploadHeaders.Authorization=localStorage.getItem("token")
  }
}

</script>

<style scoped>
.create-organization {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0;
  padding: 0;
}

.el-form {
  width: 50%;
  padding: 20px;
  border: 1px solid #dcdcdc;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>
