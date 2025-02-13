<script>
export default {
  data() {
    return {

    }
  },
  methods:{
    cellMouseEnter(row,col,cell,event){

      localStorage.setItem("tableRow",JSON.stringify(row));

    }
  },

  props: {
    tableData:{
      required:true,
      default:[
        {
          date: '2016-05-03',
          name: 'Tom',

        },
        {
          date: '2016-05-02',
          name: 'Tom',

        },

      ]
    },
    colName:{
      required: true,
      default: [{prop:'日期',label:'date'},{prop:'名称',label:'name'}]
    },
    needOperation:{
      default:true
    }

  },

}
</script>

<template>

    <div style="display: flex;justify-content: center;align-items: center">
      <el-container>
        <el-header>
          <slot name="header"></slot>
        </el-header>
        <el-main >
          <el-table :data="tableData" style="width: 90%" @cell-mouse-enter="cellMouseEnter" >
            <el-table-column v-for="item in colName" fixed :prop="item.prop" :label="item.label"  />
            <el-table-column v-if="needOperation" fixed="right" label="操作" >
              <template #default>
                <slot name="operation"></slot>
              </template>
            </el-table-column>
          </el-table>
        </el-main>
        <el-footer>
          <slot name="footer"></slot>
        </el-footer>
      </el-container>
    </div>
</template>

<style scoped>

</style>
