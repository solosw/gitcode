<template>
  <div class="repo-create">
    <el-form :model="form" label-width="120px" ref="repoForm" style="height: 100%;" label-position="top">
      <el-form-item label="Repository Name" prop="name" :rules="[{ required: true, message: 'Please input repository name', trigger: 'blur' }]">
        <el-input v-model="form.name"></el-input>
      </el-form-item>
      <el-form-item label="Belongs to" prop="belongsTo" :rules="[{ required: true, message: 'Please select a belonging', trigger: 'change' }]">
        <el-select v-model="form.belongsTo" placeholder="Select">
          <el-option :label="user.name" :value="-1"></el-option>
          <el-option v-for="(item,index) in userIdentity" :label="item.name" :value="index"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Path" :rules="[{ required: true, message: 'Please input Path', trigger: 'blur' }]">
        <el-input v-model="form.path">
          <template #prefix>
              {{getPrefixPath()}}
          </template>
        </el-input>
      </el-form-item>

      <el-form-item label="Description (optional)">
        <el-input type="textarea" v-model="form.description"></el-input>
      </el-form-item>

      <el-form-item label="Visibility">
        <el-radio-group v-model="form.visibility">
          <el-radio-button label="0" >Public</el-radio-button>
          <el-radio-button label="1" >Private</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">Create Repository</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>


import axios from "axios";
import {ElMessage} from "element-plus";

export default {
  name: 'RepoCreate',
  data() {
    return {
      user:JSON.parse(localStorage.getItem("user")).user,
      form: {
        name: '',
        path: '',
        belongsTo: -1,
        description: '',
        readme: false,
        visibility: '0'
      },
      userIdentity:[


      ]
    };
  },
  created() {
    axios.post("/user/getIdentityList/"+this.user.id).then((res)=>{
      if(res.data.status==200){
        this.userIdentity=res.data.data
      }else {
        ElMessage(res.data.message)
      }
    })

  },
  methods: {

    getPrefixPath(){
      if(this.form.belongsTo==-1) return this.user.name+"/"
      return this.userIdentity[this.form.belongsTo].name+"/"+this.user.name+"/"
    },

    onSubmit() {
      this.$refs.repoForm.validate((valid) => {
        if (valid) {

         var data={
           name:this.form.name,
           path: this.getPrefixPath()+this.form.path,
           creatorId:this.user.id,
           kind:this.form.visibility,
           type:this.form.belongsTo==-1?0:1,
           origizationId:this.form.belongsTo==-1?null:this.userIdentity[this.form.belongsTo].id,
           description: this.form.description
         }
        axios.post("/house/create",data).then((res)=>{
          if(res.data.status==200){
              alert("创建成功")
              location.href="/index"
          }else {
            ElMessage(res.data.message)
          }
        })

        } else {
          ElMessage('请检查填写类容');
          return false;
        }
      });
    }
  }
};
</script>

<style scoped>
.repo-create {

  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  margin: 10vh;
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
