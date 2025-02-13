<script>
export default {
  data() {
    return {
      isChange:false
    }
  },

  props: {
    title:{
      required:true
    },
    items:{
      required: true,
      default:[
        {label:'label',content:"content"},
        {label:'label',content:"content"},
        {label:'label',content:"content"}
      ]
    },
    sumbitFun: {

    },
    isNeedChange:{
      default: false
    }

  },

  methods: {

    onSumbit(){
      this.sumbitFun();
    }
  },
  created() {

  }
}
</script>

<template>
  <el-descriptions
      class="el-main"
      :title="title"
      :column="1"
      border
  >
    <template #extra>
      <div v-if="isNeedChange">
        <el-button v-show="!isChange" @click="isChange=true" type="success">开启修改</el-button>
        <el-button v-show="isChange" @click="isChange=false" type="danger">关闭修改</el-button>
        <el-button v-show="isChange" type="primary" @click="onSumbit">提交</el-button>

      </div>
      <slot name="operation"></slot>
    </template>

    <slot name="front"></slot>
    <el-descriptions-item v-for="item in items" :label="item.label"  @click="isChange=true"><el-input :disabled="!isChange" v-model="item.content" ></el-input></el-descriptions-item>
    <slot name="append"></slot>
  </el-descriptions>
</template>

<style scoped>

</style>
