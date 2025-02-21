<script>
import {ElMessage} from "element-plus";
import axios from "axios";

export default {
  data() {
    return {
      user:JSON.parse(localStorage.getItem("user")).user,
      searchContent:"",
      userType:-1,
      userIdentity:[


      ],
      count:5,
      myRepositys:[

      ]


    }
  },

  props: {},

  methods: {
    load(){
      if(this.count>this.myRepositys.length) return
      this.count++;
    },
    createResposity(){
        location.href="/new"
    },
    handleCommand(command){
      ElMessage(`click on item ${command}`)
    },
    handleChange(value){
      axios.post("/house/list",{
        type:this.userType==-1?0:1,
        creatorId:this.user.id,
        origizationId:this.userType==-1?null:this.userIdentity[this.userType].id
      }).then((res)=>{
        if(res.data.status==200){
          this.myRepositys=res.data.data
          console.log(this.myRepositys)
        }else {
          ElMessage(res.data.message)
        }
      })

    }

  },
  created() {
      axios.post("/user/getIdentityList/"+this.user.id).then((res)=>{
        if(res.data.status==200){
          this.userIdentity=res.data.data
        }else {
          ElMessage(res.data.message)
        }
      })



    axios.post("/house/list",{
        type:this.userType==-1?0:1,
        creatorId:this.user.id,
        origizationId:this.userType==-1?null:this.userIdentity[this.userType].id
    }).then((res)=>{
      if(res.data.status==200){
        this.myRepositys=res.data.data
      }else {
        ElMessage(res.data.message)
      }
    })
  }


}
</script>

<template>
  <div class="index-manu">

    <div class="body">

    </div>

  </div>
  <div class="main-left">
    <div class="tx_name">
      <div class="tx-left">
        <img src="/images/36727057.jpg" class="tx-left">
      </div>
      <div class="name-left">
        <el-select name="name" style="width: 100px" class="name-select" v-model="userType" size="small" @change="handleChange">
          <el-option :label="user.name" :value="-1"></el-option>
          <el-option v-for="(item,index) in userIdentity" :value="index" :label="item.name"></el-option>

        </el-select>
      </div>
    </div>
    <hr class="hhr">
    <div class="repositories">
      <div class="rep-first">
        <div class="rep">
          <p>仓库</p>
          <!--<br>-->
        </div>
        <div class="rep-new">
          <el-button class="newrep-btn"  @click="createResposity" size="small" type="success">新建</el-button>
        </div>
      </div>
      <div class="rep-find">
        <input type="text" placeholder="寻找仓库" name="" value="" size="20" class="rep-find-input">
      </div>
      <div class="my-rep">
        <ul class="infinite-list" style="list-style-type:none">
          <li  v-for="(item,index) in  myRepositys.slice(0, 10)"  class="infinite-list-item" style="color: black">
            <el-link  :href="'/project?id='+item.id">{{item.name}}</el-link>
          </li>
        </ul>
      </div>
      <hr class="hhr">
    </div>

  </div>
  <div class="loading-box">
    <div class="loading-body">
      <div class="loading">
        <span class="lod-span">Loading...</span>
      </div>
    </div>
    <div class="loading-p">
      <p>Loading activity.</p>
    </div>
  </div>
  <div class="main-right">
    <div class="main-right-title">
        <span>
            Explore repositories
        </span>
    </div>
    <div class="">
      <div class="main-right-a">
        <a class="main-right-aa" href="#">
          elixir-ecto/ecto
        </a>
      </div>
      <div class="main-right-span">
            <span>
                A database wrapper and language integrated query for Elixir
            </span>
      </div>
      <div class="main-right-images">
        <img class="main-right-images-first" src="/images/point_darkblue.png"> <span class="main-right-images-span">Elixir</span>
        <img class="main-right-images-second" src="/images/star.png"> <span class="main-right-images-span">4.3k</span>
      </div>
    </div>
    <hr>
    <div class="">
      <div class="main-right-a">
        <a class="main-right-aa" href="#">
          elixir-ecto/ecto
        </a>
      </div>
      <div class="main-right-span">
            <span>
                A database wrapper and language integrated query for Elixir
            </span>
      </div>
      <div class="main-right-images">
        <img class="main-right-images-first" src="/images/point_blue.png"> <span class="main-right-images-span">Elixir</span>
        <img class="main-right-images-second" src="/images/star.png"> <span class="main-right-images-span">4.3k</span>
      </div>
    </div>
    <hr>
    <div class="">
      <div class="main-right-a">
        <a class="main-right-aa" href="#">
          elixir-ecto/ecto
        </a>
      </div>
      <div class="main-right-span">
            <span>
                A database wrapper and language integrated query for Elixir
            </span>
      </div>
      <div class="main-right-images">
        <img class="main-right-images-first" src="/images/point_yellow.png"> <span class="main-right-images-span">Elixir</span>
        <img class="main-right-images-second" src="/images/star.png"> <span class="main-right-images-span">4.3k</span>
      </div>
    </div>
    <div class="main-right-more">
      <a href="#">Explor more-></a>
    </div>
  </div>
</template>

<style scoped>
body{
  margin: 0px;
}
.name-select {
  /*Chrome和Firefox里面的边框是不一样的，所以复写了一下*/
  border: solid 1px white;
  /*很关键：将默认的select选择框样式清除*/
  appearance: none;
  -moz-appearance: none;
  -webkit-appearance: none;
  /*将背景改为红色*/
  background: white;
  /*加padding防止文字覆盖*/
  padding-right: 14px;
}

/*清除ie的默认选择框样式清除，隐藏下拉箭头*/
.name-select::-ms-expand {
  display: none;
}
.main-left{
  width: 20%;
  margin: 0px 0px 0px 27px;
  padding: 0px 0px;
  display: inline-block;
  float: left;
}
.tx_name{
  margin: 16px 0;
  height: 30px;
}
.tx-left{
  margin-left: 5%;
  height: 25px;
  float: left;
}
.name-left{
  margin-left: 5%;
  float: left;

}
.repositories{

}
.rep-first{
  height: 30%;
}
.rep{
  margin-left: 5%;
  float: left;
}
.rep-new{
  padding: 16px 0;
  float: right;
}
.rep-find{
  margin-left: 5%;
  width: 100%;
  float: left;
}
.rep-find-input{
  height: 30px;
  margin: 16px 0;
  width: 100%;
}
.my-rep{
  margin-top: -10px;
  margin-left: 5%;
  width: 100%;
  float: left;
}
.teams{
  margin-left: 5%;
  float: left;
}
.hhr{
  width: 100%;
  margin: 2% 5%;
}

.loading-box{
  float: left;
  width: 50%;
  height: 130px;
  margin-top: 25px;
  margin-left: 35px;
  border: 2px solid black;
  /*    display: flex;*/
  /*align-items: center;*/
  justify-content: center;
}
.loading-body{
  margin: 0;
  padding: 0;
  width: 100%;
  height: 70px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: "monospace",sans-serif;
}
.loading{
  width: 60px;
  height: 60px;
  box-sizing: border-box;
  border-radius: 50%;
  border-top: 5px solid black;
  position: relative;
  animation: a1 2s linear infinite;
}
.loading::before,.loading::after{
  content: '';
  width: 60px;
  height: 60px;
  position: absolute;
  left: 0;
  top: -5px;
  box-sizing: border-box;
  border-radius: 50%;
}
.loading::before{
  border-top: 5px solid black;
  transform: rotate(120deg);
}
.loading::after{
  border-top: 5px solid black;
  transform: rotate(240deg);
}
.loading .lod-span{
  position: absolute;
  width: 60px;
  height: 60px;
  color: black;
  text-align: center;
  line-height: 60px;
  font-size: 10px;
  animation: a2 2s linear infinite;
}
@keyframes a1 {
  to{
    transform: rotate(360deg);
  }
}

@keyframes a2 {
  to{
    transform: rotate(-360deg);
  }
}
.loading-p{
  text-align: center;
  width: 100%;
}

.head{
  background-color: black;
  width: 100%;
  height: 50px;
}
.head-img-first{
  margin: 0px;
  padding: 0px;
  width: 30px;
  height: 30px;
  float: left;
}
.head-img-first-github{
  width: 30px;
  height: 30px;
  margin: 10px 15px;
}
.head-title{
  float: left;
  margin: 15px 35px;
}
.head-title-a{
  text-decoration: none;
  margin: 0px 10px;
}
.head-title-a:link{
  color: white;
}
.head-title-a:visited{
  color: white;
}
.head-img-second{
  width: 30px;
  height: 30px;
  margin: 10px 25px 0px 0px;
  padding: 0px;
  float: right;
}
.head-img-second-bell{
  width: 25px;
  height: 25px;
}
.head-img-third{
  width: 60px;
  height: 30px;
  margin: 10px 25px 0px 0px;
  padding: 0px;
  float: right;
}
.head-img-third-plus,.head-img-third-trangle{
  float: left;
  width: 20px;
  height: 20px;
  margin: 4px 0px 0px 0px;
}
.head-img-four{
  width: 60px;
  height: 30px;
  margin: 10px 25px 0px 0px;
  padding: 0px;
  float: right;
}
.head-img-four-touxiang,.head-img-four-trangle{
  float: left;
  width: 20px;
  height: 20px;
  margin: 4px 2px 0px 0px;
}

.index-manu{
  top: 50%;
  left: 50%;
  width: 100%;
}


.search-box {
  position: absolute;
  left: 860px;
  /*right: 200px;*/
  bottom: 0px;
  top: 10px;
  background: white;
  height: 40px;
  border-radius: 40px;
  padding: 10px;
}
.search-btn {
  color: #e84118;
  float: right;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: white;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: 0.4s;
  text-decoration: none;
}
.search-txt {
  border: none;
  background: none;
  outline: none;
  float: left;
  padding: 0;
  color: white;
  font-size: 16px;
  transition: 0.4s;
  line-height: 40px;
  width: 0;
}
.search-box:hover > .search-txt {
  width: 110px;
  padding: 0 6px;
}
.search-box:hover > .search-btn {
  background: white;
}
.index-manu-right{
  height: 80px;
  margin-right: 5%;
  margin-bottom: 5%;
  margin-top: 1%;
  padding: 1px 1px 1px 1px;
  float: right;
}
.index-manu{
  width: 100%;
  height: 80px;
  justify-content: center;
}
.Sign-up{
  height: 80px;
  border: 1px solid white;
  padding: 10px 10px;
  margin: auto;
  font-size: 30px;
  color: white;
}
.Sign-in{
  height: 80px;
  padding: 10px 10px;
  margin: auto;
  font-size: 30px;
  color: white;
}
.main-right{
  margin: 0px 30px 0px 0px;
  padding: 20px;
  width: 19%;
  float: right;
}
.main-right-images-first,.main-right-images-second{
  width: 20px;
  height: 20px;
}
.main-right-images-span{
  margin-bottom: 2px;
}
.main-right-aa:link{
  color: black;
}
.main-right-aa:visited{
  color: black;
}
.main-right-title{
  margin: 10px 0px;
}
.main-right-a{
  margin: 5px 0px;
}
.main-right-span{
  font-size: 12px;
  margin: 0px 0px 10px 0px;
}
.main-right-more{
  margin: 20px 0px;
}

</style>
