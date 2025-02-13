<script>
import {Delete, Edit, FolderAdd} from "@element-plus/icons-vue";

export default {
  components: {FolderAdd, Delete, Edit},
  data() {
    return {

      dialogVis2:false,
      formLabelWidth:70,
      dialogVis:false,
      chooseData:[],
      datetime:{date:"",time:""},
      realSHowData:[],
      currentPage:1,


    }
  },

  props: {
    bgSrc:{},
    getUpdateItem:{},
    pageSize:{default:7},
    showData:{type:Array},
    isNeedTable:{default:true},
    fliterData:{
      type:Array
    },
    createItem:{

    },
    updateItem:{

    },
    deleteItem:{

    },
    managerTittle:{required:true},
    logoUrl:{required: true},
    returnUrl:{required:true,default:"/"},
    chooseList:{required:true,default:[
        "用户管理",
        "商品管理",
        "订单管理",
      ],
    },
    childTitle:{},
    colName:{
      default:[
        {prop:'id',label:'编号'},{prop:'name',label:'商品名'},{prop:'price',label:'单价'},{prop:'count',label:'数量'},{prop:'unit',label:'单位'},{prop:'description',label:'描述'},

      ],


      },
    tableData:{
      default:[

        {
          date: '2016-05-02',
          name: '王小虎',
          province: '上海',
          city: '普陀区',
          address: '上海市普陀区金沙江路 1518 弄',
          zip: 200333
        }
      ],

    },
    urls:{required:true}
  },

  methods: {
    changePage(val){
      const self=this;
      this.currentPage=val
      this.realSHowData=Array.from(this.fliterData)
      this.realSHowData=this.realSHowData.slice((this.currentPage-1)*this.pageSize, (this.currentPage-1)*this.pageSize+this.pageSize);
    },
    filterChildren(index) {
      location.href=this.urls[index]
    },
    logout(){

      localStorage.clear();
      location.href=this.returnUrl
    },

    getDatetime(){
      setInterval(()=>{
        let date = new Date();
        this.datetime.date = date.getFullYear()+"-"+((date.getMonth()+1)<10?"0"+(date.getMonth()+1):(date.getMonth()+1))+"-"+(date.getDate()<10?"0"+date.getDate():date.getDate());
        this.datetime.time = (date.getHours()<10?"0"+date.getHours():date.getHours())+":"+(date.getMinutes()<10?"0"+date.getMinutes():date.getMinutes())+":"+(date.getSeconds()<10?"0"+date.getSeconds():date.getSeconds());
      },1000);
    },
    handleSelectionChange(val){
      this.chooseData=val
    },
    updateit(val){
      this.updateItem(val);
      this.dialogVis2=true
    },

  },
  mounted(){
    this.realSHowData=Array.from(this.showData)
    this.getDatetime();
  },

  computed:{

    getShowData(){
      return this.showData
    }
  },

  watch:{
    getShowData(nv,ov){
      this.realSHowData=nv;
    }

  }

}
</script>

<template>
  <div class="background" :style="{backgroundImage: 'url(' + bgSrc + ')'}">
    <div class="main" >
      <div class="header">
        <div class="logo" style="display: flex">

          <img :src="logoUrl" style="width: 60px;height: 60px"/>
          <div style=" height: 60px;background: linear-gradient(to right, white, #737c83);">
            <div style="font-size:large;margin-bottom: 10px; color: #3a8ee6;letter-spacing: 3px;">{{managerTittle}}</div>
          </div>

        </div>
        <div style="width: 500px;height: 60px;">
          <span style="color:white;letter-spacing: 5px"></span>
        </div>

        <div  style="margin-right: 10%;display: flex;justify-content: center">
          <el-button type="text" @click="logout" style="margin-top: 12%">退出</el-button>
          <div class="time-box" style="margin-left: 20px">
            <span class="time-time">{{datetime.time}}</span>
          </div>
        </div>
      </div>

    </div>
    <el-container >
      <el-aside>
        <el-menu
            active-text-color="#ffd04b" class="side-menu" :collapse="true" unique-opened >
          <el-menu-item v-for="(item,index) in chooseList" :index="index.toString()" @click="filterChildren(index)">{{ item }}</el-menu-item>
        </el-menu>
      </el-aside>
      <el-main infinite-scroll-disabled="true" >
        <div class="box-card" style="background-color: white">
          <el-divider></el-divider>
          <div style=" font-size: small;display: flex;justify-content: center;align-content: center;align-items: center;justify-items: center">
            <img :src="logoUrl" style="width: 20px;height: 20px"/>
            <div style="font-size: x-large;margin-left: 10px;color: red;font-weight: bold;">{{childTitle}}</div>
          </div>
          <el-divider></el-divider>

          <div style="background-color: white">
            <el-container >
              <el-header style="height: 10%">
                <div style="display: flex;justify-content: space-between;align-content: space-between">
                  <el-space style="width: 40%">
                    <el-button v-if="this.createItem" size="small" type="success"  @click="dialogVis=true"><FolderAdd style="width: 1em; height: 1em;"></FolderAdd>添加</el-button>
                    <el-button v-if="this.updateItem" size="small"  :disabled="chooseData.length>1||chooseData.length==0" @click="updateit(chooseData[0])"> <Edit style="width: 1em; height: 1em; " />编辑</el-button>
                    <el-button v-if="this.deleteItem" size="small" type="danger"  :disabled="chooseData.length==0" @click="deleteItem(this.chooseData)" > <Delete style="width: 1em; height: 1em; " />删除</el-button>
                  </el-space>
                  <el-space style="width: 40%">
                    <slot name="search">

                    </slot>
                  </el-space>
                </div>
              </el-header>
              <el-main style="height: 80%;overflow: auto">
                <el-table v-if="isNeedTable"  @selection-change="handleSelectionChange" :data="realSHowData" style="width: 90%" >

                  <el-table-column
                      type="selection"
                      width="55">
                  </el-table-column>
                  <el-table-column  v-for="item in colName" fixed :prop="item.prop" :label="item.label"  />

                </el-table>
                <slot name="self"></slot>
              </el-main>
              <el-footer v-if="isNeedTable" style="height: 10%;display: flex;justify-content: center;align-content: center">
                <el-pagination
                    small
                    layout="prev, pager, next"
                    :total="fliterData.length"
                    :page-size="pageSize"
                    @current-change="changePage"
                >
                </el-pagination>
              </el-footer>
            </el-container>
          </div>
          <el-drawer
              title="新建"
              v-model="dialogVis"
              direction="ltr"
              custom-class="demo-drawer"
              ref="drawer"
          >
            <slot name="create"></slot>
            <div class="demo-drawer__footer" style="float: right;margin-right: 10px">
              <el-button @click="dialogVis=false">取 消</el-button>
              <el-button type="primary" @click="createItem"> 确 定</el-button>
            </div>
          </el-drawer>
          <el-drawer
              title="编辑"
              v-model="dialogVis2"
              direction="ltr"
              custom-class="demo-drawer"
              ref="drawer"
          >
            <div class="demo-drawer__content" v-if="chooseData.length==1">
              <slot name="update"></slot>
              <div class="demo-drawer__footer" style="float: right;margin-right: 10px">
                <el-button @click="dialogVis2=false">取 消</el-button>
                <el-button type="primary" @click="updateItem(null)"> 确 定</el-button>
              </div>
            </div>
          </el-drawer>
        </div>
      </el-main>
    </el-container>
  </div>

</template>

<style scoped>
#footer {
  background-color: #3498db; /* 设置蓝色背景 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
  text-align: center; /* 让内容居中显示 */
  padding: 20px; /* 添加一些内边距 */
  color: #fff; /* 设置字体颜色为白色 */
  font-size: 24px; /* 设置字体大小 */
  font-weight: bold; /* 设置字体加粗 */
  width: 100%;
  height: 50px;
}
.side-menu {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1), 0 3px 10px rgba(0, 0, 0, 0.15);
  width: 200px;
  height: 800px;
  border-right: 1px solid #000; /* 设置右边框样式为实线 */
  border-left: 1px solid #000; /* 设置右边框样式为实线 */
  border-radius: 5px; /* 设置边框圆角半径 */
  padding: 10px; /* 添加一些内边距 */
}

.side-menu::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, #545c64, #333);
  z-index: -1;
}

.box-card{
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
  border-radius: 10px;
  height: 90%;
  width: 90%;
}


.main {
  display: flex;
  .app {
    width: 100%;
  }
  .aside {
    width: 100%;
    margin-top: 60px;
    z-index: 10;
    transition: all 0.3s ease-in-out;
    height: calc(100% - 60px);
    overflow-x: auto;
    .index{
      width: 100%;
      font-size: 20px;
    }
  }
}

.header {
  width: 100%;
  display: flex;
  height: 60px;
  line-height: 60px;
  background-color: #737c83;
  justify-content: space-between;
  z-index: 10;
  .logo {
    display: flex;
    flex-direction: row;
    width: 300px;
    height: 60px;
    text-align: center;
    line-height: 60px;
    color: #FFF;
    font-weight: 600;
    -webkit-transition: width 0.35s;
    transition: all 0.3s ease-in-out;
  }

}
.time-box{
  display: flex;
  flex-direction: column;
  float: right;
  .time-time{
    font-size: 20px;
    color: #8EF3FA;
    letter-spacing: 1.66px;
  }
  .time-date{
    opacity: 0.48;
    font-size: 10px;
    -webkit-transform: scale(0.83333333) translate(-8.33333333%, 0);
    transform: scale(0.83333333) translate(-8.33333333%, 0);
    color: #8EF3FA;
    letter-spacing: 0.83px;
    text-indent: 34px;
  }
}


.el-menu--vertical {
  min-width: 190px;
}
.setting-category{
  padding:10px 0;
  border-bottom: 1px solid #eee;
}
::-webkit-scrollbar
{
  width: 5px;
  height: 5px;
  background-color: #F5F5F5;
}
/*定义滚动条轨道 内阴影+圆角*/
::-webkit-scrollbar-track
{
  -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
  border-radius: 10px;
  background-color: #F5F5F5;
}
/*定义滑块 内阴影+圆角*/
::-webkit-scrollbar-thumb
{
  border-radius: 10px;
  -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,.3);
  background-color: #bdbdbd;
}
/*滑块效果*/
::-webkit-scrollbar-thumb:hover
{
  border-radius: 5px;
  -webkit-box-shadow: inset 0 0 5px rgba(0,0,0,0.2);
  background: rgba(0,0,0,0.4);
}
/*IE滚动条颜色*/
html {
  scrollbar-face-color:#bfbfbf;/*滚动条颜色*/
  scrollbar-highlight-color:#000;
  scrollbar-3dlight-color:#000;
  scrollbar-darkshadow-color:#000;
  scrollbar-Shadow-color:#adadad;/*滑块边色*/
  scrollbar-arrow-color:rgba(0,0,0,0.4);/*箭头颜色*/
  scrollbar-track-color:#eeeeee;/*背景颜色*/
}

.background {


  background-size: cover;
  background-position: center;
}
</style>
