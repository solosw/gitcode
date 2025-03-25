<script>
import axios from "axios";
import {ElMessage} from "element-plus";

export default {
  data() {
    return {
      user:JSON.parse(localStorage.getItem("user")).user,

      publicRepositories: [
        { name: '仓库1', createTime: '2023-01-01 12:00:00', creator: '用户A',path:"solosw/as" },
        { name: '仓库2', createTime: '2023-02-01 13:00:00', creator: '用户B' ,path:"solosw/as"}
      ],
      privateRepositories: [
        { name: '仓库3', createTime: '2023-03-01 14:00:00', creator: '用户C',path:"solosw/as" },
        { name: '仓库4', createTime: '2023-04-01 15:00:00', creator: '用户D',path:"solosw/as" }
      ],
      orzRepositories:[],
      partRes:[]
    }
  },

  props: {},

  methods: {

    fetch(){
      axios.post("/house/Ownlist",{
        creatorId:this.user.id,
      }).then((res)=>{
        if(res.data.status==200){
              this.publicRepositories=res.data.data.public
              this.privateRepositories=res.data.data.private
              this.orzRepositories=res.data.data.orz;
              this.partRes=res.data.data.part
        }else {
          ElMessage.error(res.data.message)
        }
      })
    },
    lookFor(id){
      location.href="/project?id="+id;
    },
    changeType(id,type){
      axios.post("/house/changeType",{
        id:id,
        kind:type,
      }).then((res)=>{
        if(res.data.status==200){
            this.fetch()
        }else {
          ElMessage.error(res.data.message)
        }
      })
    }

  },
  created() {
    this.fetch();
  }
}
</script>

<template>
  <div style="width: 100%">
    <h2>我的仓库</h2>
    <el-tabs type="card" style="width: 1000px">
      <el-tab-pane label="公开仓库">
        <el-table :data="publicRepositories" style="width: 1000px" size="small">

          <el-table-column prop="name" label="仓库名称" width="200"></el-table-column>
          <el-table-column prop="path" label="路径" width="200"></el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="200"></el-table-column>

          <el-table-column  label="操作">
            <template #default="row">
                <el-button @click="changeType(row.row.id,1)">私有化</el-button>
            </template>
          </el-table-column>
          <el-table-column  label="操作">
            <template #default="row">
              <el-button @click="lookFor(row.row.id)"> 详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="私有仓库">
        <el-table :data="privateRepositories" style="width: 1000px" size="small">
          <el-table-column prop="name" label="仓库名称" width="200"></el-table-column>
          <el-table-column prop="path" label="路径" width="200"></el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="200"></el-table-column>
          <el-table-column  label="操作">
            <template #default="row">
              <el-button @click="changeType(row.row.id,0)">公开</el-button>
            </template>
          </el-table-column>
          <el-table-column  label="操作">
            <template #default="row">
              <el-button @click="lookFor(row.row.id)"> 详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="组织仓库">
        <el-table :data="orzRepositories" style="width: 1000px" size="small">
          <el-table-column prop="name" label="仓库名称" width="200"></el-table-column>
          <el-table-column prop="path" label="路径" width="200"></el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="200"></el-table-column>
          <el-table-column  label="操作">
            <template #default="row">
              <el-button @click="lookFor(row.row.id)"> 详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="我的参与">
        <el-table :data="partRes" style="width: 1000px" size="small">
          <el-table-column prop="name" label="仓库名称" width="200"></el-table-column>
          <el-table-column prop="path" label="路径" width="200"></el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="200"></el-table-column>
          <el-table-column  label="操作">
            <template #default="row">
              <el-button @click="lookFor(row.row.id)"> 详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>

</style>
