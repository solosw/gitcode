<script>


import axios from "axios";
import {ElMessage} from "element-plus";

export default {

  created() {
    const params = new URLSearchParams(window.location.search);
     var content = params.get('content'); // 假设你要获取名为 'param1' 的参数
      axios.post("/house/getHouseBySearch/"+content).then((res)=>{
        if(res.data.status==200){
          this.repositories=res.data.data
        }else {
          ElMessage.error(res.data.message)
        }
      })
  },
  methods: {
    formatDate(date) {
      // 确保传入的是一个Date对象
      if (!(date instanceof Date)) {
        date = new Date(date);
      }

      // 获取年份、月份和日期，并确保月份和日期始终是两位数
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0'); // 月份从0开始，所以需要加1
      const day = String(date.getDate()).padStart(2, '0');

      // 返回格式化的日期字符串
      return `${year}-${month}-${day}`;
    },

    getLanguageColor(){
      return 'green'
    },
    goProject(id){
      location.href="project?id="+id
    }

  },
  data() {
    return {
      repositories: [
        {
          name: "vue-project",
          description: "A modern Vue 3 application with composition API",
          language: "Vue",
          stars: 1284,
          forks: 302,
          updated: "2025-02-22",
          license: "MIT"
        },
        {
          name: "ai-platform",
          description: "Next-generation machine learning infrastructure",
          language: "Python",
          stars: 24567,
          forks: 5321,
          updated: "2025-02-25",
          license: "Apache-2.0"
        },
        // 可继续添加更多仓库数据...
      ]
    }
  }
}
</script>

<template>
  <div class="container">


    <div class="repository-list">
      <article
          v-for="(repo, index) in repositories"
          :key="index"
          class="repository-card"
          @click="goProject(repo.id)"
      >
        <div class="repo-content">
          <div class="repo-header">
            <a href="#" class="repo-name">{{ repo.name }}</a>
            <span class="badge">{{repo.kind==0?'public':'private'}}</span>
          </div>
          <p class="repo-description">{{ repo.description }}</p>
          <div class="repo-meta">
            <span class="meta-item">
              <span
                  class="language-color"
                  :style="{ backgroundColor: getLanguageColor('null') }"
              ></span>
              {{ 'Java' }}
            </span>
            <span class="meta-item">
              <svg class="octicon" viewBox="0 0 16 16" width="16" height="16">
                <path fill="currentColor" d="M8 .25a.75.75 0 0 1 .673.418l1.882 3.815 4.21.612a.75.75 0 0 1 .416 1.279l-3.046 2.97.719 4.192a.75.75 0 0 1-1.088.791L8 12.347l-3.766 1.98a.75.75 0 0 1-1.088-.79l.72-4.194L.818 6.374a.75.75 0 0 1 .416-1.28l4.21-.611L7.327.668A.75.75 0 0 1 8 .25Z"/>
              </svg>
              {{0 }}
            </span>
            <span class="meta-item">
              <svg class="octicon" viewBox="0 0 16 16" width="16" height="16">
                <path fill="currentColor" d="M3.5 3.25a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0Zm7.5 0a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0ZM15 8a7 7 0 1 1-14 0 7 7 0 0 1 14 0ZM7.25 4.25v2.992l2.028.812a.75.75 0 0 1-.557 1.392l-2.5-1A.75.75 0 0 1 6.25 8V4.25a.75.75 0 0 1 1.5 0Z"/>
              </svg>
              Created <time :datetime="repo.createTime">{{ formatDate(repo.createTime) }}</time>
            </span>
          </div>
        </div>
        <div class="repo-actions">
          <button class="btn btn-star">
            <svg class="octicon" viewBox="0 0 16 16" width="16" height="16">
              <path fill="currentColor" d="M8 .25a.75.75 0 0 1 .673.418l1.882 3.815 4.21.612a.75.75 0 0 1 .416 1.279l-3.046 2.97.719 4.192a.75.75 0 0 1-1.088.791L8 12.347l-3.766 1.98a.75.75 0 0 1-1.088-.79l.72-4.194L.818 6.374a.75.75 0 0 1 .416-1.28l4.21-.611L7.327.668A.75.75 0 0 1 8 .25Zm0 2.445L6.615 5.5a.75.75 0 0 1-.564.41l-3.097.45 2.24 2.184a.75.75 0 0 1 .216.664l-.528 3.084 2.769-1.456a.75.75 0 0 1 .698 0l2.77 1.456-.53-3.084a.75.75 0 0 1 .216-.664l2.24-2.183-3.096-.45a.75.75 0 0 1-.564-.41L8 2.694Z"/>
            </svg>
            Star
          </button>
        </div>
      </article>
    </div>
  </div>
</template>

<style scoped>
.container {
  max-width: 1280px;
  margin: 2rem auto;
  padding: 0 1.5rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.repository-list {
  border: 1px solid #e1e4e8;
  border-radius: 6px;
  background-color: #fff;
}

.repository-card {
  display: flex;
  justify-content: space-between;
  padding: 1.5rem;
  border-bottom: 1px solid #e1e4e8;
  transition: background-color 0.2s;
}

.repository-card:hover {
  background-color: #f6f8fa;
}

.repo-name {
  font-size: 1.25rem;
  font-weight: 600;
  color: #0969da;
  text-decoration: none;
}

.repo-name:hover {
  text-decoration: underline;
}

.badge {
  font-size: 0.75rem;
  padding: 0.15em 0.6em;
  border: 1px solid #d0d7de;
  border-radius: 2em;
  margin-left: 0.5rem;
}

.repo-description {
  color: #57606a;
  margin: 0.5rem 0;
  font-size: 0.875rem;
}

.repo-meta {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  font-size: 0.75rem;
  color: #57606a;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.language-color {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.btn {
  padding: 0.375rem 0.75rem;
  border-radius: 6px;
  font-weight: 500;
  border: 1px solid transparent;
  cursor: pointer;
}

.btn-primary {
  background-color: #2da44e;
  color: white;
}

.btn-star {
  background: #f6f8fa;
  border: 1px solid #d0d7de;
  color: #24292f;
}

.btn-star:hover {
  background-color: #f3f4f6;
}

.octicon {
  vertical-align: text-bottom;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }

  .repository-card {
    flex-direction: column;
    gap: 1rem;
  }

  .repo-actions {
    align-self: flex-start;
  }
}

/* 辅助函数样式 */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>
