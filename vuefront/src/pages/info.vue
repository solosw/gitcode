<template>
  <div>
    <h2>个人信息设置</h2>
    <el-form label-width="100px" style="width: 600px;">
      <el-form-item label="姓名">
        <el-input v-model="fullName" placeholder="请输入姓名"></el-input>
      </el-form-item>
      <el-form-item label="职务">
        <el-input v-model="job" placeholder="请输入职务"></el-input>
      </el-form-item>
      <el-form-item label="公司">
        <el-input v-model="company" placeholder="请输入公司名"></el-input>
      </el-form-item>
      <el-form-item label="QQ">
        <el-input v-model="qq" placeholder="请输入qq"></el-input>
      </el-form-item>
      <el-form-item label="WeChat">
        <el-input v-model="qq" placeholder="请输入微信"></el-input>
      </el-form-item>
      <el-form-item label="简介">
        <el-input type="textarea" v-model="bio" placeholder="请输入简介"></el-input>
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
      fullName: '',
      bio: '',
      job:"",
      wx:"",
      qq:"",
      company:""
    };
  }
};
</script>
