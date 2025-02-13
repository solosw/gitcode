<template>
  <el-container>
    <el-aside>
      <el-menu   background-color="#545c64"
                 text-color="#fff"
                 active-text-color="#ffd04b" class="side-menu" :collapse="true" unique-opened style="height: 800px">
        <el-menu-item v-for="(item,index) in chooseList" :index="index.toString()" @click="filterChildren(index)">{{ item }}</el-menu-item>
      </el-menu>
    </el-aside>
    <el-main infinite-scroll-disabled="true">
      <slot></slot>
    </el-main>
  </el-container>

</template>

<script>


export default {

  mounted() {

  },
  methods: {
    filterChildren(index) {
     location.href=this.urls[index]
    }
  },
  props:{
    title:{
      required:true
    },
    chooseList:{
      required: true,
      default:[
        "收藏列表",
        "我加入的频道",
        "我管理的频道",
        "创建频道"

      ]
    },
    urls:{
      required:true,
    }



  },

  data(){
    return{
      htmlContent:'',
      selectMenu:0,
    }
  }
};
</script>

<style scoped>
.side-menu {
  width: 200px;
}

.box-card{
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
  border-radius: 10px;

}
</style>
