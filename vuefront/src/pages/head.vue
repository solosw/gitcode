<script>
export default {
  data() {
    return {
      searchContent:"",
      user:JSON.parse(localStorage.getItem("user")).user,
    }
  },

  props: {},

  methods: {

    handleCommand(command){
        location.href=command
    },
    search(){
      location.href="/search?content="+this.searchContent
    }
  },
  created() {

  }
}
</script>

<template>
  <div class="head">
    <div class="head-img-first" @click="handleCommand('/index')">
      <img class="head-img-first-github" src="/images/github-white.png">

    </div>

    <div class="search">

    </div>
    <div class="head-title" style="margin-left: 60%;margin-bottom: 10px">
      <el-input
          v-model="searchContent"
          style="width: 240px"
          placeholder="search something....."
          size="small"
      >
        <template #prefix>
          <el-icon class="el-input__icon" @click="search"><search /></el-icon>
        </template>
      </el-input>
    </div>
    <div class="head-img-four">
      <a href="/info">
        <img class="head-img-four-touxiang" :src="user.av">

      </a>

    </div>
    <div class="head-img-third">

      <el-dropdown @command="handleCommand">
        <span class="el-dropdown-link">
      <img class="head-img-third-plus" src="/images/plus.png">
          <img class="head-img-third-trangle" src="/images/down-trangle-white.png">
    </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="/createOri">创建组织</el-dropdown-item>
            <el-dropdown-item command="/new">创建仓库</el-dropdown-item>
            <el-dropdown-item command="/myOri">我的组织</el-dropdown-item>
            <el-dropdown-item command="/myRep">我的仓库</el-dropdown-item>
            <el-dropdown-item command="/account">个人设置</el-dropdown-item>
            <el-dropdown-item command="/login">登出</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

  </div>
  <router-view></router-view>
</template>

<style scoped>
body{
  margin: 0px;
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


.search-box:hover > .search-txt {
  width: 110px;
  padding: 0 6px;
}
.search-box:hover > .search-btn {
  background: white;
}

</style>
