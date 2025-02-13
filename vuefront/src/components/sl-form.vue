<script>

export default {
  data() {
    return {

    }
  },

  props: {

    forms:{
      required:true,
      default:[
        {label:'one',flied:''},
        {label:'one',flied:''},
        {label:'one',flied:''},
      ]
    },
    placeholder:{
      type:Array,
      default:[]
    },
    sumbitFun:{
      type:Function,
      default: ()=>{

      },
      required: true
    },

    isNeedCancel:{
      default:true
    },
    cancelFun:{
      type:Function
    }

  },

  methods: {
    onSubmit(){
      this.sumbitFun();
    },
    onCancel(){

    }
  },
  created() {

  },

}
</script>

<template>
    <el-form  label-width="120px">
      <el-form-item v-for="(item,index) in forms" :label="item.label">
        <el-input v-model="item.flied" :placeholder="placeholder[index]"/>
      </el-form-item>
      <slot></slot>
      <el-form-item style="display: flex;">
        <el-button type="primary" @click="onSubmit()">确认</el-button>
        <el-button v-if="isNeedCancel" @click="onCancel">Cancel</el-button>
      </el-form-item>
    </el-form>
</template>

<style scoped>

</style>
