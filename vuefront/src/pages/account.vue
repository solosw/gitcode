<template>
  <div>
    <h2>账号设置</h2>
    <el-form label-width="100px" style="width: 500px">
      <el-form-item label="密码">
        <el-input v-model="password" placeholder="新密码" type="password"></el-input>
      </el-form-item>
      <el-form-item label="重复密码">
        <el-input v-model="password" placeholder="重复密码" type="password"></el-input>
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="email" placeholder="请输入邮箱"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      password: '',
      email: ''
    };
  }
};
</script>
