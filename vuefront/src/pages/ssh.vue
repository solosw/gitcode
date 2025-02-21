<script>
export default {
  data() {
    return {
      publicKey:""
    }
  },

  props: {},

  methods: {},
  created() {

  }
}
</script>

<template>
  <div>
    <h2>公钥设置</h2>
    <el-form label-width="100px" style="width: 600px;height: 500px">
      <el-form-item label="公钥">
        <el-input type="textarea" v-model="publicKey" placeholder="支持ssh-rsa开头" rows="5"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>

</style>
