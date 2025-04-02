<template>

    <el-collapse v-if="files.length"  v-loading="loading" >
      <el-collapse-item :name="idx+''"
          v-for="(file,idx) in files"
          :class="['file-item', file.status]"
      >
        <template #title>
          <span class="status-tag">{{ statusMap[file.status] }}</span>
          <span class="file-path">{{ file.fileName }}</span>
        </template>
       <template #default>
         <div style="width:100%">
           <code-diff
               :old-string="file.oldContent"
               :new-string="file.newContent"
               output-format="side-by-side"
               language="js"
           />
         </div>
       </template>
      </el-collapse-item>
    </el-collapse>
</template>

<script>
import axios from 'axios';

export default {

  created() {
    const params = new URLSearchParams(window.location.search);
    var id = params.get('id'); // 假设你要获取名为 'param1' 的参数
    var hash=params.get('commit')
    this.loading=true
    axios.post("/git/getDifference/"+id+"/"+hash).then((res)=>{
      if(res.data.status==200) this.files=res.data.data
      this.loading=false
    })
  },

  data() {
    return {
      commitHash: '',
      files: [],
      loading: false,
      error: null,
      statusMap: {
        ADD: '新增',
        MODIFY: '修改',
        DELETE: '删除',
        COPY:'复制',
        RENAME:'重命名'
      }
    };
  },
  methods: {
    getlanague(name){
        var t=name.split(".");
        return t.length==2?t[1]:''
    }




  }
};
</script>

<style>


@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}


.file-item:last-child {
  border-bottom: none;
}

.status-tag {
  width: 50px;
  padding: 2px 6px;
  border-radius: 4px;
  text-align: center;
  font-size: 12px;
  margin-right: 15px;
}

.file-path {
  color: #333;
}

.ADD .status-tag {
  background: #e6ffe6;
  color: #2a6b2a;
}

.MODIFY .status-tag {
  background: #fff9e6;
  color: #8a6d2a;
}

.DELETE .status-tag {
  background: #ffe6e6;
  color: #8a2a2a;
}
.COPY .status-tag {
  background: #00ffff;
  color: #a8abb2;
}
</style>
