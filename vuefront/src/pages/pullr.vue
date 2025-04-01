<template>
  <div class="pr-system">
    <!-- 创建PR的表单 -->
    <div class="create-pr">
      <h2>Create New Pull Request</h2>
      <form @submit.prevent="createPR">
        <div class="form-group">
          <label>Title:</label>
          <input v-model="newPR.title" required>
        </div>
        <div class="form-group">
          <label>Description:</label>
          <textarea v-model="newPR.description" required></textarea>
        </div>
        <div class="form-group">
          <label>Base Branch:</label>
          <el-select v-model="newPR.baseBranch" required style="width: 200px">
            <el-option v-for="item in branches" :value="item" :label="item"></el-option>
          </el-select>
        </div>
        <div class="form-group">
          <label>Head Branch:</label>
          <el-select v-model="newPR.headBranch" required style="width: 200px">
            <el-option v-for="item in branches" :value="item" :label="item"></el-option>
          </el-select>
        </div>
        <button type="submit" class="submit-btn">Create Pull Request</button>
      </form>
    </div>

    <!-- PR列表 -->
    <div class="pr-list">
      <h2>Pull Requests ({{ filteredPRs.length }})</h2>

      <!-- 状态过滤 -->
      <div class="filters">
        <button
            v-for="state in states"
            :key="state"
            :class="{ active: currentFilter === state }"
            @click="currentFilter = state"
        >
          {{ getStatus(state) }} ({{ stateCounts[state] }})
        </button>
      </div>

      <!-- PR条目 -->
      <div
          v-for="(pr,idx) in filteredPRs"
          :key="pr.id"
          class="pr-item"
          :class="pr.state"
      >
        <div class="pr-header">
          <span class="pr-title">{{ pr.title }}</span>
          <span class="pr-number">#{{ idx+1 }}</span>
          <span class="pr-state" style="color: #8EF3FA">{{ getStatus(pr.state) }}</span>
        </div>

        <div class="pr-body">
          <div class="pr-meta">
            <span class="author">{{ pr.username }}</span> 想要合并
            <span class="branch">{{ pr.headBranch }}</span> 到
            <span class="branch">{{ pr.baseBranch }}</span>
          </div>

          <!-- 操作按钮 -->
          <div class="pr-actions" v-if="house.creatorId==user.id">
            <el-space direction="horizontal">
              <button @click="sure(pr.id)" class="close-btn">同意</button>
              <button @click="mergePR(pr.id)" class="merge-btn">已合并</button>
              <button @click="closePR(pr.id)" class="close-btn">关闭</button>

            </el-space>


          </div>
          <!-- 详细信息 -->
          <div  class="pr-details">
            <div class="pr-description">{{ pr.description }}</div>

            <!-- 评论部分
            <div class="comments">
              <div v-for="comment in pr.comments" :key="comment.id" class="comment">
                <span class="comment-author">{{ comment.author }}:</span>
                <span class="comment-text">{{ comment.text }}</span>
              </div>
              <div class="new-comment" v-if="false">
                <input
                    v-model="pr.newComment"
                    @keyup.enter="addComment(pr.id)"
                    placeholder="Add a comment"
                >
                <button @click="addComment(pr.id)" class="comment-btn">Comment</button>
              </div>
            </div>
            -->
          </div>
        </div>
      </div>
    </div>
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
    axios.post("/pull/get/"+this.houseId).then((res)=>{
      if(res.data.status==200){
        this.prs=res.data.data
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
      user:JSON.parse(localStorage.getItem("user")).user,
      houseId:-1,
      branches:[

      ],
      newPR: {
        title: '',
        description: '',
        baseBranch: '',
        headBranch: ''
      },
      prs: [],
      states: [-1, 0, 1, 2,3],
      currentFilter: 'all',
      nextId: 1,
      house:{}
    }
  },
  computed: {
    filteredPRs() {
      return this.prs.filter(pr =>
          this.currentFilter === -1 || pr.state === this.currentFilter
      )
    },
    stateCounts() {
      return this.states.reduce((acc, state) => {
        acc[state] = state ==-1
            ? this.prs.length
            : this.prs.filter(pr => pr.state === state).length
        return acc
      }, {})
    }
  },
  methods: {
    getStatus(st){
      if(st==0) return '创建'
      if(st==1) return '已同意'
      if(st==2) return '已合并'
      if(st==3) return '已关闭'
      if(st==-1) return '全部'
      return 'none'
    },
    createPR() {
      axios.post("/pull/add",{
        ...this.newPR,
        houseId:this.houseId,
        username:this.user.name
      }).then((res)=>{
        if(res.data.status==200){
          ElMessage.success("成功")
          location.reload()
        }
      })
    },
    toggleDetails(prId) {
      const pr = this.prs.find(p => p.id === prId)
      if (pr) pr.showDetails = !pr.showDetails
    },
    addComment(prId) {
      const pr = this.prs.find(p => p.id === prId)
      if (pr && pr.newComment.trim()) {
        pr.comments.push({
          id: pr.comments.length + 1,
          author: 'Current User',
          text: pr.newComment
        })
        pr.newComment = ''
      }
    },
    mergePR(prId) {
      const pr = this.prs.find(p => p.id === prId)

      if (pr) {
        pr.state = 2
        axios.post("/pull/update",pr).then((res)=>{
          if(res.data.status==200){
            ElMessage.success("成功")
          }
        })
      }
    },
    closePR(prId) {
      const pr = this.prs.find(p => p.id === prId)

      if (pr) {
        pr.state = 3
        axios.post("/pull/update",pr).then((res)=>{
          if(res.data.status==200){
            ElMessage.success("成功")
          }
        })
      }
    },
    sure(prId){
      const pr = this.prs.find(p => p.id === prId)

      if (pr) {
        pr.state = 1
        axios.post("/pull/update",pr).then((res)=>{
          if(res.data.status==200){
            ElMessage.success("成功")
          }
        })
      }
    }
  }
}
</script>

<style scoped>
.pr-system {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.create-pr {
  margin-bottom: 40px;
  padding: 20px;
  background: #f6f8fa;
  border-radius: 6px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: 600;
}

input, textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #d0d7de;
  border-radius: 6px;
}

.submit-btn {
  background: #2da44e;
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.pr-list {
  margin-top: 30px;
}

.filters {
  margin-bottom: 20px;
}

.filters button {
  margin-right: 10px;
  padding: 6px 12px;
  border: 1px solid #d0d7de;
  border-radius: 6px;
  background: #f6f8fa;
  cursor: pointer;
}

.filters button.active {
  background: #0969da;
  color: white;
  border-color: #0969da;
}

.pr-item {
  margin-bottom: 15px;
  padding: 15px;
  border: 1px solid #d0d7de;
  border-radius: 6px;
}

.pr-item.open {
  border-left: 4px solid #2da44e;
}

.pr-item.merged {
  border-left: 4px solid #6f42c1;
}

.pr-item.closed {
  border-left: 4px solid #cf222e;
}

.pr-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.pr-title {
  font-weight: 600;
  margin-right: 8px;
}

.pr-number {
  color: #57606a;
  margin-right: 8px;
}

.pr-state {
  padding: 2px 8px;
  border-radius: 14px;
  font-size: 12px;
  font-weight: 500;
}

.open .pr-state { background: #e3fcef; color: #22863a; }
.merged .pr-state { background: #f5f0ff; color: #6f42c1; }
.closed .pr-state { background: #ffeef0; color: #cf222e; }

.pr-actions {
  margin-top: 10px;


  gap: 8px;
}

.merge-btn {
  background: #2da44e;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.close-btn {
  background: #cf222e;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.toggle-btn {
  background: none;
  border: 1px solid #d0d7de;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
}

.pr-details {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #e1e4e8;
}

.comments {
  margin-top: 15px;
}

.comment {
  margin: 8px 0;
  padding: 8px;
  background: #f6f8fa;
  border-radius: 6px;
}

.new-comment {
  margin-top: 15px;
  display: flex;
  gap: 8px;
}

.new-comment input {
  flex: 1;
  padding: 8px;
}

.comment-btn {
  background: #2da44e;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
}

.branch {
  font-family: monospace;
  background: #f6f8fa;
  padding: 2px 4px;
  border-radius: 4px;
}
</style>
