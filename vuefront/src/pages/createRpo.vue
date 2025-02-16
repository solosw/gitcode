<template>
  <div class="repo-create">
    <el-form :model="form" label-width="120px" ref="repoForm" style="height: 100%;" label-position="top">
      <el-form-item label="Repository Name" prop="name" :rules="[{ required: true, message: 'Please input repository name', trigger: 'blur' }]">
        <el-input v-model="form.name"></el-input>
      </el-form-item>
      <el-form-item label="Path (optional)">
        <el-input v-model="form.path"></el-input>
      </el-form-item>
      <el-form-item label="Belongs to" prop="belongsTo" :rules="[{ required: true, message: 'Please select a belonging', trigger: 'change' }]">
        <el-select v-model="form.belongsTo" placeholder="Select">
          <el-option label="Personal Account" value="personal"></el-option>
          <el-option label="Organization" value="organization"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Description (optional)">
        <el-input type="textarea" v-model="form.description"></el-input>
      </el-form-item>

      <el-form-item label="Visibility">
        <el-radio-group v-model="form.visibility">
          <el-radio-button label="public">Public</el-radio-button>
          <el-radio-button label="private">Private</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">Create Repository</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { ref } from 'vue';

export default {
  name: 'RepoCreate',
  setup() {
    const form = ref({
      name: '',
      path: '',
      belongsTo: '',
      description: '',
      readme: false,
      visibility: 'public'
    });

    const repoForm = ref(null);

    const onSubmit = () => {
      repoForm.value.validate((valid) => {
        if (valid) {
          alert('submit!');
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    };

    return {
      form,
      repoForm,
      onSubmit
    };
  }
};
</script>

<style scoped>
.repo-create {

  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  margin: 10vh;
  padding: 0;
}

.el-form {
  width: 50%;
  padding: 20px;
  border: 1px solid #dcdcdc;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>
