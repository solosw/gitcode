<script>
import axios from "axios";
import {ElMessage, ElMessageBox} from "element-plus";

export default {
  data() {
    return {
      users: [], // 用户列表
      newUser: {name: "", password: ""}, // 新用户信息
      editUser: null, // 正在编辑的用户信息
    }
  },
  methods: {
    fetchUsers() {
      // 假设后端有一个获取所有用户的API
      axios.post("/user/all").then((res) => {
        if (res.data.status === 200) {
          this.users = res.data.data;
        }
      });
    },
    addUser() {
      axios.post("/user/add", this.newUser).then((res) => {
        if (res.data.status === 200) {
          ElMessage.success("添加成功");
          this.fetchUsers(); // 添加成功后重新获取用户列表
          this.newUser = {name: "", password: ""}; // 清空输入框
        } else {
          ElMessage.error(res.data.message);
        }
      });
    },
    deleteUser(user) {
      ElMessageBox.confirm('确定要删除该用户吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        axios.post("/user/delete", user).then((res) => {
          if (res.data.status === 200) {
            ElMessage.success("删除成功");
            this.fetchUsers(); // 删除成功后重新获取用户列表
          } else {
            ElMessage.error(res.data.message);
          }
        });
      }).catch(() => {
        ElMessage.info('已取消删除');
      });
    },
    startEdit(user) {
      this.editUser = {...user};
    },
    updateUser() {
      axios.post("/user/update", this.editUser).then((res) => {
        if (res.data.status === 200) {
          ElMessage.success("更新成功");
          this.fetchUsers(); // 更新成功后重新获取用户列表
          this.editUser = null; // 取消编辑状态
        } else {
          ElMessage.error(res.data.message);
        }
      });
    }
  },
  created() {
    this.fetchUsers();
  }
}
</script>
<template>
  <div class="modal" style="width: 1000px;">
    <h2>用户管理</h2>
    <!-- 添加新用户 -->
    <el-form style="width: 600px;">
      <el-form-item label="用户名">
        <el-input v-model="newUser.name" required />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="newUser.password" type="password" required />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="small" @click="addUser">添加</el-button>
      </el-form-item>
    </el-form>

    <!-- 用户列表 -->
    <el-table :data="users" style="width: 1000px">
      <el-table-column prop="id" label="编号"></el-table-column>
      <el-table-column prop="name" label="用户名"></el-table-column>
      <el-table-column prop="email" label="邮箱"></el-table-column>
      <el-table-column prop="password" label="密码"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" type="danger" @click="deleteUser(scope)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

  </div>
</template>
