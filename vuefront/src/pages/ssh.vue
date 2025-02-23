<script>
import axios from "axios";
import {ElMessage} from "element-plus";

export default {
  data() {
    return {
      publicKey:"",
      user:JSON.parse(localStorage.getItem("user")).user,
      key:null
    }
  },

  props: {},

  methods: {
    submit(){
        axios.post("/ssh/createPublicKey",{
            creatorId:this.user.id,
            publicKey:this.publicKey
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
          this.key=res.data.data
          this.publicKey=res.data.data.publicKey

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
    <el-form label-width="100px" style="width: 600px;height: 500px">
      <el-form-item label="公钥">
        <el-input type="textarea" v-model="publicKey" placeholder="支持ssh-rsa开头" rows="5"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>

</style>
