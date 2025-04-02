<template>

  <div class="issues-container">
    <!-- 添加按钮 -->
    <div style="margin-bottom: 20px;">
      <el-button type="primary" @click="()=>{dialogVisible = true}">
        <i class="el-icon-plus"></i> 新建 Issue
      </el-button>
    </div>

    <!-- 新建对话框 -->
    <el-dialog
        title="新建 Issue"
        v-model="dialogVisible"
        width="600px"
        @closed="resetForm"
    >
      <el-form
          :model="newIssue"
          :rules="rules"
          ref="issueForm"
          label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input
              v-model="newIssue.title"
              placeholder="请输入标题"
              clearable
          ></el-input>
        </el-form-item>

        <el-form-item label="描述">
          <el-input
              type="textarea"
              :rows="4"
              v-model="newIssue.description"
              placeholder="请输入详细描述"
          ></el-input>
        </el-form-item>

        <el-form-item label="标签">
          <el-select
              v-model="newIssue.labels"
              multiple
              style="width: 100%"
              placeholder="选择标签"
          >
            <el-option
                v-for="label in allLabels"
                :key="label"
                :label="label"
                :value="label"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="newIssue.state">
            <el-radio label="open" >Open</el-radio>
            <el-radio label="closed">Closed</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitIssue">提交</el-button>
      </span>
    </el-dialog>
  </div>


  <div class="issues-container">
    <!-- 筛选工具栏 -->
    <div class="filters">
      <el-select v-model="stateFilter" placeholder="State" style="margin-right: 10px;">
        <el-option label="All" value="all"></el-option>
        <el-option label="Open" value="open"></el-option>
        <el-option label="Closed" value="closed"></el-option>
      </el-select>
      <el-select v-model="labelFilter" placeholder="Label" style="margin-right: 10px;">
        <el-option label="All" value="all"></el-option>
        <el-option
            v-for="label in allLabels"
            :key="label"
            :label="label"
            :value="label"
        ></el-option>
      </el-select>
      <el-select v-model="sortBy" placeholder="Sort by">
        <el-option label="Newest" value="newest"></el-option>
        <el-option label="Oldest" value="oldest"></el-option>
      </el-select>
    </div>

    <!-- Issues 列表 -->
    <el-table :data="filteredIssues" style="margin-top: 20px;">
      <el-table-column label="" width="50">
        <template #default="scope">
          <span
              :class="['state-dot', scope.row.state]"
              style="display: inline-block; width: 12px; height: 12px; border-radius: 50%;"
          ></span>
        </template>
      </el-table-column>
      <el-table-column label="Title">
        <template #default="scope">
          <div>
            {{ scope.row.title }}
            <el-tag
                v-for="label in scope.row.labels"
                :key="label"
                :style="{ backgroundColor: getLabelColor(label), color: '#fff' }"
                size="small"
                style="margin-left: 8px;"
            >
              {{ label }}
            </el-tag>
          </div>
          <div class="meta">
            #{{ scope.row.number }} opened {{ formatDate(scope.row.createTime) }} by
            {{ scope.row.username }}
          </div>

        </template>
      </el-table-column>
      <el-table-column label="state" >
        <template #default="scope">
          <div style="display: flex; align-items: center;">
            <i class="el-icon-chat-dot-round" style="margin-right: 4px;"></i>
            <div>
              <el-tag>{{scope.row.state}}</el-tag>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="content" width="120">
        <template #default="scope">
          <div style="display: flex; align-items: center;">
            <i class="el-icon-chat-dot-round" style="margin-right: 4px;"></i>
            <div>
              {{scope.row.description}}
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="operation"  v-if="user.id==house.creatorId">
        <template #default="scope">
          <div style="display: flex; align-items: center;">
              <el-button size="small" type="info" @click="changeState(scope.row)">{{scope.row.state=='open'?'close':'open'}}</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import axios from "axios";
import {ElMessage} from "element-plus";

export default {
  created() {
    const params = new URLSearchParams(window.location.search);
    var id = params.get('id'); // 假设你要获取名为 'param1' 的参数
    this.houseId=id
    axios.post("/house/getBranch/"+id).then((res)=>{
      if(res.data.status==200) this.branches=res.data.data
    })
    axios.post("/issues/get/"+this.houseId).then((res)=>{
      if(res.data.status==200){
        this.issues=res.data.data
      }
    })
    axios.post("/house/getHouseById/"+this.houseId).then((res)=>{
      if(res.data.status==200){
        this.house=res.data.data
      }
    })

  },
  data() {


    return {
      house:{},
      user:JSON.parse(localStorage.getItem("user")).user,
      houseId:-1,
      dialogVisible: false,
      newIssue: {
        title: '',
        description: '',
        labels: [],
        state: 'open'
      },
      rules: {
        title: [
          { required: true, message: '标题不能为空', trigger: 'blur' }
        ]
      },
      // 模拟数据
      issues: [

      ],
      searchQuery: '',
      stateFilter: 'all',
      labelFilter: 'all',
      sortBy: 'newest',
    };
  },
  computed: {
    nextNumber() {
      return this.issues.length > 0
          ? Math.max(...this.issues.map(i => i.number)) + 1
          : 1000
    },
    // 可用标签
    allLabels() {
      return ["feature",'bug','task','any'];
    },
    // 过滤和排序后的 issues
    filteredIssues() {
      return this.issues
          .filter(issue => {
            const matchesSearch = issue.title.toLowerCase().includes(this.searchQuery.toLowerCase());
            const matchesState = this.stateFilter === 'all' || issue.state === this.stateFilter;
            const matchesLabel =
                this.labelFilter === 'all' || issue.labels.includes(this.labelFilter);
            return matchesSearch && matchesState && matchesLabel;
          })
          .sort((a, b) => {
            return this.sortBy === 'newest'
                ? new Date(b.createTime) - new Date(a.createTime)
                : new Date(a.createTime) - new Date(b.createTime);
          });
    },
  },
  methods: {
    changeState(row){
        if(row.state=='open'){row.state='closed'}
        else row.state='open'
        axios.post("/issues/update",row).then((res)=>{
          if(res.data.status==200){
            ElMessage.success("成功");
          }
        })
    },
    submitIssue() {
      this.$refs.issueForm.validate(valid => {
        if (valid) {
          const newItem = {
            title: this.newIssue.title,
            state: this.newIssue.state,
            labels: [...this.newIssue.labels],
            username: this.user.name, // 可替换为实际登录用户
            description: this.newIssue.description,
            houseId:this.houseId
          }
          axios.post("/issues/add",newItem).then((res)=>{
            if(res.data.status==200){
              this.issues.unshift(newItem) // 添加到数组开头
              this.dialogVisible = false
              this.$message.success('Issue 创建成功!')
              location.reload()
            }
          })

        }
      })
    },

    resetForm() {
      this.$refs.issueForm.resetFields()
      this.newIssue.labels = []
      this.newIssue.state = 'open'
    },
    // 格式化日期
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      });
    },
    // 获取标签颜色
    getLabelColor(label) {
      const colors = {
        bug: '#d73a4a',
        feature: '#a2eeef',
        task: '#7057ff',
      };
      return colors[label] || '#cccccc';
    },
  },
};
</script>

<style scoped>
.issues-container {
  max-width: 880px;
  margin: 0 auto;
  padding: 20px;
}

.filters {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.meta {
  color: #586069;
  font-size: 12px;
}
</style>
