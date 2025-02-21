<template>
  <div style="width: 1000px">
    <h2>我的组织</h2>
    <el-table :data="organizations" style="width: 1000px" @row-dblclick="handleRowClick" >
      <el-table-column prop="name" label="名称" width="200"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="200"></el-table-column>
      <el-table-column prop="description" label="描述"></el-table-column>
      <el-table-column label="操作">
        <template #default>
          <el-button>退出</el-button>
        </template>
      </el-table-column>

      <el-table-column label="操作">
        <template #default>
          <el-button>添加成员</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="成员信息" v-model="dialogVisible" width="50%">
      <el-table :data="selectedOrganization.members" style="width: 100%">
        <el-table-column prop="name" label="成员名称" width="200"></el-table-column>
        <el-table-column prop="role" label="角色"></el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      organizations: [
        {
          name: '组织1',
          createTime: '2023-01-01 12:00:00',
          description: '这是一个公开的组织',
          members: [
            { name: '用户A', role: '管理员' },
            { name: '用户B', role: '成员' }
          ]
        },
        {
          name: '组织2',
          createTime: '2023-02-01 13:00:00',
          description: '这是一个私有的组织',
          members: [
            { name: '用户C', role: '管理员' },
            { name: '用户D', role: '成员' }
          ]
        }
      ],
      dialogVisible: false,
      selectedOrganization: {}
    };
  },
  methods: {
    handleRowClick(row) {
      this.selectedOrganization = row;
      this.dialogVisible = true;
    }
  }
};
</script>

<style scoped>
.el-table {
  margin-top: 20px;
}
</style>



