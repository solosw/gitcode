<script>
// 新增响应式布局逻辑
import { useDisplay } from 'vuetify'
import {ElMessage} from "element-plus";
import axios from "axios";
import {Search} from "@element-plus/icons-vue";

export default {
  components: {Search},

  data() {
    return {
      loading:true,
      user:JSON.parse(localStorage.getItem("user")).user,
      searchContent:"",
      userType:-1,
      userIdentity:[


      ],
      count:5,
      myRepositys:[

      ]


    }
  },

  props: {},

  methods: {
    search(){
      location.href="/search?content="+this.searchContent
    },
    load(){
      if(this.count>this.myRepositys.length) return
      this.count++;
    },
    createResposity(){
      location.href="/new"
    },
    handleCommand(command){
      ElMessage(`click on item ${command}`)
    },
    handleChange(value){
      axios.post("/house/list",{
        type:this.userType==-1?0:1,
        creatorId:this.user.id,
        origizationId:this.userType==-1?null:this.userIdentity[this.userType].id
      }).then((res)=>{
        if(res.data.status==200){
          this.myRepositys=res.data.data
          console.log(this.myRepositys)
        }else {
          ElMessage(res.data.message)
        }
      })

    },
    async loadMore() {
      if (this.count <= this.myRepositys.length) {
        this.count += 5
        await this.$nextTick()
        this.$refs.observer.disconnect()
      }
    }

  },
  created() {
    axios.post("/user/getIdentityList/"+this.user.id).then((res)=>{
      if(res.data.status==200){
        this.userIdentity=res.data.data
      }else {
        ElMessage(res.data.message)
      }
    })



    axios.post("/house/list",{
      type:this.userType==-1?0:1,
      creatorId:this.user.id,
      origizationId:this.userType==-1?null:this.userIdentity[this.userType].id
    }).then((res)=>{
      if(res.data.status==200){
        this.myRepositys=res.data.data
      }else {
        ElMessage(res.data.message)
      }
    })
  },

}
</script>

<template>
  <!-- 重构后的响应式布局 -->
  <div class="container" :class="{ 'mobile-view': mobile }">
    <!-- 用户信息侧边栏 -->
    <aside class="sidebar">

      <div class="profile-card">
        <img :src="user.av" class="user-avatar">
        <div class="profile-info">
          <el-select
              v-model="userType"
              popper-class="org-select-dropdown"
              @change="handleChange"
          >
            <el-option
                :label="user.name"
                :value="-1"
                class="profile-option"
            >
              <div class="option-content">
                <span>{{ user.name }}</span>
              </div>
            </el-option>
            <el-option
                v-for="(item,index) in userIdentity"
                :key="index"
                :value="index"
                class="profile-option"
            >
              <div class="option-content">

                <span>{{ item.name }}</span>
              </div>
            </el-option>
          </el-select>
        </div>

      </div>

      <!-- 仓库操作区 -->
      <div class="repo-actions">
        <el-button
            type="primary"
            icon="el-icon-plus"
            @click="createResposity"
            class="new-repo-btn"
            size="small"
            style="margin-top: 10px;margin-bottom: 10px"
        >
          新建仓库
        </el-button>
        <div class="search-box" >
          <el-input
              v-model="searchContent"
              placeholder="搜索仓库..."
              suffix-icon="el-icon-search"
              class="repo-search"
              size="small"
          >
            <template #prefix>
              <el-icon @click="search"><Search ></Search></el-icon>
            </template>
          </el-input>
        </div>
      </div>

      <div v-if="loading" class="loading-container">
        <el-progress
            :percentage="100"
            status="success"
            :indeterminate="true"
            class="custom-progress"
        />
        <p class="loading-text">正在加载活动数据...</p>
      </div>
    </aside>

    <!-- 主内容区 -->
    <main class="content-area">
      <!-- 仓库列表 -->
      <ul class="repo-list">
        <li
            v-for="(item,index) in myRepositys"
            :key="index"
            class="repo-card"
        >
          <el-link
              :href="'/project?id='+item.id"
              class="repo-link"
          >
            <div class="repo-header">
              <h3 class="repo-name">{{ item.name }}</h3>
              <div class="repo-meta">
                <span class="badge">{{item.kind==0?'public':'private'}}</span>
                <span class="meta-item">{{ item.language }}</span>
              </div>
            </div>
            <p class="repo-desc">{{ item.description || '暂无描述' }}</p>
            <div class="repo-stats">
              <div class="stat-item">
                <el-icon class="stat-icon"><Star /></el-icon>
                {{  0 }}
              </div>
              <div class="stat-item">
                <el-icon class="stat-icon"><Share /></el-icon>
                {{  0 }}
              </div>
            </div>
          </el-link>
        </li>
      </ul>

      <!-- 加载状态 -->

    </main>
  </div>
</template>

<style scoped>
/* 现代布局系统 */
.container {
  --primary-color: #2da44e;
  --text-primary: #24292f;
  --border-color: #d0d7de;

  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 32px;
  max-width: 1280px;
  margin: 32px auto;
  padding: 0 24px;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .container {
    grid-template-columns: 1fr;
    padding: 0 16px;
  }

  .mobile-view .sidebar {
    position: sticky;
    top: 0;
    background: white;
    z-index: 100;
    padding: 16px 0;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  }
}

/* 用户信息卡片 */
.profile-card {
  padding: 24px;
  border: 1px solid var(--border-color);
  border-radius: 6px;
  background: white;
}

.user-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  margin-bottom: 16px;
  object-fit: cover;
}

/* 仓库卡片交互效果 */
.repo-card {
  border: 1px solid var(--border-color);
  border-radius: 6px;
  padding: 16px;
  margin-bottom: 16px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.repo-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.repo-name {
  color: var(--primary-color);
  font-size: 20px;
  margin-right: 8px;
}

.repo-desc {
  color: #57606a;
  font-size: 14px;
  margin: 8px 0;
}

/* 加载动画优化 */
.custom-progress {
  --el-progress-text-color: var(--text-primary);
  --el-progress-bar-height: 8px;
}

.loading-text {
  color: #57606a;
  text-align: center;
  font-family: 'SF Mono', monospace;
}

/* 搜索框样式增强 */
.repo-search {
  :deep(.el-input__inner) {
    border-radius: 24px;
    padding-left: 40px;
    background: #f6f8fa;
  }

  :deep(.el-input__suffix) {
    left: 16px;
    right: auto;
  }
}

.badge {
  font-size: 0.75rem;
  padding: 0.15em 0.6em;
  border: 1px solid #d0d7de;
  border-radius: 2em;
  margin-left: 0.5rem;
}
</style>
