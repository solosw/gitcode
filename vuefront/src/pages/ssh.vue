<script>
import axios from "axios";
import {ElMessage} from "element-plus";

export default {
  data() {
    return {
      publicKey:"",
      user:JSON.parse(localStorage.getItem("user")).user,
      key:null,
      name:"",
      myPublicKeys:[
        { keyName: '工作用公钥', publicKey: 'ssh-rsa AAA...' },
        { keyName: '个人用公钥', publicKey: 'ssh-rsa BBB...' }
      ]
    }
  },

  props: {},

  methods: {
    submit(){
        axios.post("/ssh/createPublicKey",{
            creatorId:this.user.id,
            publicKey:this.publicKey,
            keyName:this.name
        }).then((res)=>{
            if(res.data.status==200){
              ElMessage("Success")
              this.getUserKey()
            }else {
              ElMessage(res.data.message)
            }
        })
    },
    getUserKey(){
      axios.post("/ssh/getUserKey/"+this.user.id).then((res)=>{
        if(res.data.status==200){
          this.myPublicKeys=res.data.data
        }else {
          ElMessage(res.data.message)
        }
      })
    },
    handleDelete(row){
      axios.post("/ssh/deleteKey/"+this.user.id+"/"+row.id).then((res)=>{
        if(res.data.status==200){
          ElMessage.success("成功");
          location.reload()
        }else {
          ElMessage(res.data.message)
        }
      })
    }

  },
  created() {
    this.getUserKey()
  }
}
</script>

<template>
  <div>
    <h2>公钥设置</h2>
    <el-form label-width="100px" style="width: 600px;">
      <el-form-item label="名称">
        <el-input  v-model="name" placeholder="公钥名称" ></el-input>
      </el-form-item>
      <el-form-item label="公钥">
        <el-input type="textarea" v-model="publicKey" placeholder="支持ssh-rsa开头" rows="5"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">提交</el-button>
      </el-form-item>
    </el-form>
  </div>
  <h2>我的公钥</h2>
  <el-table :data="myPublicKeys" width="600">
    <el-table-column prop="name" label="名称" ></el-table-column>
    <el-table-column prop="publicKey" label="公钥" show-overflow-tooltip ></el-table-column>
    <el-table-column label="操作" >
      <template #default="scope">
        <el-button @click="handleDelete(scope.row)" type="text" size="small">删除</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<style scoped>

</style>
