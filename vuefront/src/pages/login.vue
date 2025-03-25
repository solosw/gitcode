<template>
  <div class="div_container_top">
    <div class="div_container_top_center">
      <div class="div_logo">
        <img src="/git.png" style="width:53px;height:53px;margin-top:30px;">
      </div>
      <div class="div_word" style="margin-top:50px;margin-bottom: 10px">Sign in to GitUnity</div>
      <div class="div_container_main">
        <div class="div_container_main_center">
          <form @submit.prevent="login">
            <div class="div_main_first">
              <div class="div_first_word">Username or email address</div>
            </div>
            <div class="div_main_second">
              <input v-model="username" type="text" placeholder="Username or email address" style="width:270px;height:30px; border:1px #d1d5da solid;border-radius:3px;">
            </div>
            <div class="div_main_third">
              <div class="div_chat_top_word1">Password</div>
            </div>
            <div class="div_main_fouth">
              <input v-model="password" type="password" placeholder="Password" style="width:270px;height:30px; border:1px #d1d5da solid;border-radius:3px;">
            </div>
            <div class="div_main_fifth">
              <button type="submit" class="div_fifth_button" style="background-color:#269f42;background-image: linear-gradient(-180deg,#34d058 0%,#28a745 90%);color:white;text-decoration:none;padding:8px 12px;font-size:14px;font-weight:600;line-height:20px;border-radius: 0.25em;">
                Sign in
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

  </div>


</template>

<script>
import axios from "axios";
import {ElMessage} from "element-plus";

export default {
  data() {
    return {
      username: '',
      password: ''
    }
  },
  methods: {
    login() {
      // 在这里处理登录逻辑
      axios.post("/user/login",{username:this.username,password:this.password}).then((res)=>{
        if(res.data.status==200){

          localStorage.setItem("user",JSON.stringify(res.data.data))
          localStorage.setItem("token",res.data.data.token)
          location.href="/index"
        }else
        {
          ElMessage(res.data.message)
        }

      })
      // 可以添加API调用等逻辑
    },

  }
}
</script>

<style scoped>
/* 将原来的CSS样式复制到这里，并根据需要调整 */
.div_container_top {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50vh;


}
.div_container_top_center { width: 310px; height: 150px; margin: 0 auto; }
.div_logo { width: 53px; height: 53px; margin: 0 auto; }
.div_word { width: 310px; margin: 0 auto; font-size: 24px; font-weight: 300; letter-spacing: -0.5px; text-align: center; }
.div_container_main { width: 100%; }
.div_container_main_center { width: 310px; height: 235px; margin: 0 auto; background-color: #FFF; border: 1px solid #d8dee2; border-radius: 5px; padding: 20px; box-sizing: border-box; }
.div_main_first { width: 100%; height: 20px; }
.div_first_word { font-size: 13px; color: black; font-weight: bold; float: left; }
.div_main_second { width: 100%; height: 40px; }
.div_main_third { width: 100%; height: 30px; margin-top: 10px; }
.div_chat_top_word1 { font-size: 16px; font-weight: 600; float: left; }
.div_chat_top_word2 { font-size: 12px; color: #0366d6; font-weight: 600; float: right; }
.div_main_fouth { width: 100%; height: 40px; }
.div_main_fifth { width: 100%; height: 40px; }
.div_fifth_button { margin-top: 8px; padding: 8px 12px; font-size: 14px; font-weight: 600; line-height: 20px; border: 1px solid rgba(27,31,35,0.2); width: 248px; background-color: #269f42; background-image: linear-gradient(-180deg,#34d058 0%,#28a745 90%); border-radius: 0.25em; text-align: center; }
.div_foot { margin-top: 20px; width: 100%; height: 100px; }
.div_foot_main { width: 310px; height: 90px; margin: 0 auto; background-color: #F9F9F9; }
.div_sixth { width: 310px; height: 50px; background-color: #F9F9F9; color: #0366D6; border: 1px solid #D8DEE2; border-radius: 0.25em; text-align: center; }
.div_sixth_word1 { width: 150px; text-align: center; color: #D8DEE2; float: left; margin-top: 10px; }
.div_sixth_word2 { width: 150px; text-align: center; color: #0366d6; float: right; margin-top: 10px; }
.div_foot_tail { width: 310px; height: 30px; background-color: #F9F9F9; text-align: center; margin: 0 auto; }
.div_foot_word { margin-top: 10px; width: 50px; height: 15px; text-align: center; font-size: 6px; margin-left: 10px; float: left; }
.div_foot_word2 { margin-top: 10px; width: 90px; height: 15px; text-align: center; font-size: 6px; margin-left: 20px; float: left; }
</style>
