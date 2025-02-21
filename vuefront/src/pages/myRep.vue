<script>
export default {
  data() {
    return {publicRepositories: [
        { name: '仓库1', createTime: '2023-01-01 12:00:00', creator: '用户A',path:"solosw/as" },
        { name: '仓库2', createTime: '2023-02-01 13:00:00', creator: '用户B' ,path:"solosw/as"}
      ],
      privateRepositories: [
        { name: '仓库3', createTime: '2023-03-01 14:00:00', creator: '用户C',path:"solosw/as" },
        { name: '仓库4', createTime: '2023-04-01 15:00:00', creator: '用户D',path:"solosw/as" }
      ]}
  },

  props: {},

  methods: {},
  created() {

  }
}
</script>

<template>
  <div style="width: 100%">
    <h2>我的仓库</h2>
    <el-tabs type="card" style="width: 1000px">
      <el-tab-pane label="公开仓库">
        <el-table :data="publicRepositories" style="width: 1000px">
          <el-table-column prop="name" label="仓库名称" width="200"></el-table-column>
          <el-table-column prop="path" label="路径" width="200"></el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="200"></el-table-column>
          <el-table-column prop="creator" label="创建人"></el-table-column>
          <el-table-column  label="操作">
            <template #default>
                <el-button>私有化</el-button>
            </template>
          </el-table-column>
          <el-table-column  label="操作">
            <template #default>
              <el-button>详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="私有仓库">
        <el-table :data="privateRepositories" style="width: 1000px">
          <el-table-column prop="name" label="仓库名称" width="200"></el-table-column>
          <el-table-column prop="path" label="路径" width="200"></el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="200"></el-table-column>
          <el-table-column prop="creator" label="创建人"></el-table-column>
          <el-table-column  label="操作">
            <template #default>
              <el-button>公开</el-button>
            </template>
          </el-table-column>
          <el-table-column  label="操作">
            <template #default>
              <el-button>详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>

</style>
