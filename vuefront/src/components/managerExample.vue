<script>
import MyManager from "@/pages/myManager.vue";
import {Search} from "@element-plus/icons-vue";
import axios from "axios";
export default {
  components:{
    Search,
    MyManager
  },
  data() {
    return {
      pageSize:8,
      showData:[],
      filterData:[],
      colNames:[
        {prop:'id',label:'编号'},{prop:'username',label:'用户名'},{prop:'password',label:'密码'}
      ],
      tableData:[
        {id:1,username:"111",password:"111"},
        {id:1,username:"111",password:"111"},
        {id:1,username:"111",password:"111"},
        {id:1,username:"111",password:"111"},
        {id:1,username:"222",password:"111"},
        {id:1,username:"222",password:"111"},
        {id:1,username:"1222",password:"111"},
        {id:1,username:"111",password:"111"},
        {id:1,username:"111",password:"111"},
        {id:1,username:"13331",password:"111"},
        {id:1,username:"1333",password:"111"},
        {id:1,username:"1333",password:"111"},
        {id:1,username:"1333",password:"111"},
        {id:1,username:"131",password:"111"},
        {id:1,username:"13",password:"111"},
      ],

      userInput:"",
    }
  },

  props: {},

  methods: {

    deleteItem(val){
      console.log(val)
    },
    createItem(){

    },
    updateItem(val){

    },
    searchData(){

      this.filterData=Array.from(this.tableData)
      this.filterData=this.filterData.filter(item=>item.username.includes(this.userInput))
      this.showData=Array.from(this.filterData);
      this.showData=this.showData.slice(0,this.pageSize)
    }

  },
  created() {

    axios.post("")

    this.filterData=Array.from(this.tableData)
    this.showData=Array.from(this.filterData);
    this.showData=this.showData.slice(0,this.pageSize)
  }
}
</script>

<template>
  <my-manager :page-size="pageSize" :show-data="showData" :create-item="createItem" :update-item="updateItem" :delete-item="deleteItem" :filter-data="filterData" :col-name="colNames" child-title="用户管理" :table-data="tableData">
    <template #search>
      <el-space>
        <el-input placeholder="用户名" v-model="userInput">
          <template #append>
            <search style="width: 1em" @click="searchData"></search>
          </template>
        </el-input>
      </el-space>
    </template>
  </my-manager>
</template>

<style scoped>

</style>
