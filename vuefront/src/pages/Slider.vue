<template>
  <div style="display: flex;width: 30%;justify-content: center;justify-items: center;align-content: center;">
    <el-menu
        :default-active="value"
        class="el-menu-vertical-demo"
        @select="selectMenu"
        style="width:300px;height: 100vh"
    >
      <el-menu-item index="0">
        <span slot="title">账号设置</span>
      </el-menu-item>
      <el-menu-item index="1">
        <span slot="title">个人信息设置</span>
      </el-menu-item>
      <el-menu-item index="2">
        <span slot="title">我的组织</span>
      </el-menu-item>
      <el-menu-item index="3">
        <span slot="title">我的仓库</span>
      </el-menu-item>
      <el-menu-item index="4">
        <span slot="title">公钥设置</span>
      </el-menu-item>
      <el-menu-item index="5" :disabled="this.user.role==1">
        <span slot="title" v-if="this.user.role==0">用户管理</span>
      </el-menu-item>
    </el-menu>
    <div style="margin-left: 10%;width: 60%">
      <router-view></router-view>
    </div>

  </div>

</template>

<script>
export default {
  emits: ['select-menu'],
  data() {
    return {
      user:JSON.parse(localStorage.getItem("user")).user,
        value:0,
        paths:["/account","/info","/myOri","/myRep","/ssh","/user"]
    }
  },
  methods: {
    selectMenu(index) {
        this.value=index
        location.href=this.paths[index]
    }
  }
};
</script>



