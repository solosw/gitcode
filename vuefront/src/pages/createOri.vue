<template>
  <div class="create-organization">
    <el-form :model="form" label-width="120px" ref="organizationForm" style="height: 100%;" label-position="top">
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
export default {
  data() {
    return {
      form:{
        name: '',
        description:'',
        userId:JSON.parse(localStorage.getItem("user")).user.id
      }
    }
  },

  props: {},

  methods: {
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
