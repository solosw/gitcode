<script>
import { ElCard, ElTag, ElTimeline, ElTimelineItem } from 'element-plus';
import axios from "axios";
export default {
  components: {
    ElCard,
    ElTag,
    ElTimeline,
    ElTimelineItem
  },
  computed:{
    filteredCommits() {
      const commitsCopy = JSON.parse(JSON.stringify(this.mockData));
      return commitsCopy.filter(commit => {
        // 作者匹配
        const authorMatch = !this.selectedAuthor ||
            commit.author === this.selectedAuthor

        // 分支匹配
        const branchMatch = !this.selectedBranch ||
            commit.branch === this.selectedBranch

        // 日期范围匹配
        let dateMatch = true
        if (this.dateRange?.length === 2) {
          const commitDate = new Date(commit.date)
          const startDate = new Date(this.dateRange[0])
          const endDate = new Date(this.dateRange[1])
          dateMatch = commitDate >= startDate && commitDate <= endDate
        }

        return authorMatch && branchMatch && dateMatch
      })
    },
    // 生成作者选项
    authorOptions() {
      return [...new Set(this.mockData.map(c => c.author))]

    },
    // 生成分支选项
    branchOptions() {
      return [...new Set(this.mockData.map(c => c.branch))]
    }
  },
  created() {
    const params = new URLSearchParams(window.location.search);
    this.houseId = params.get('id'); // 假设你要获取名为 'param1' 的参数
      axios.post("/git/getHistory/"+this.houseId).then((res)=>{
        if(res.data.status==200) {
            this.mockData=res.data.data
            this.processCommitData(this.mockData);
        }
      })
  },
  data() {
    return {
      mockData:[],
      commits: [],
      branchColors: {
        'main': '#67C23A',
        'feature/login': '#E6A23C'
      },
      houseId:-1,
      selectedAuthor: '',
      selectedBranch: '',
      dateRange:[],
    };
  },
  mounted() {

  },
  methods: {
    handleFilterChange() {
      // 可以添加筛选动画效果
      this.processCommitData(this.filteredCommits)
    },
    handleDateChange(range) {
      this.dateRange = range
      this.handleFilterChange()
    },
    processCommitData(commits) {
      // 按时间排序并标记合并提交
      this.commits = commits
          .map(commit => ({
            ...commit,
            isMerge: commit.parents.length > 1,
            timestamp: new Date(commit.date).getTime()
          }))
          .sort((a, b) => b.timestamp - a.timestamp);
    },

    getBranchTagType(branch) {
      return branch === 'master' ? 'success' : 'warning';
    }
  }
};
</script>

<template>
  <div class="git-history-timeline">
    <el-form :inline="true" class="filter-bar">
      <el-form-item label="作者筛选">
        <el-select
            v-model="selectedAuthor"
            placeholder="选择作者"
            clearable
            @change="handleFilterChange"
            style="width: 200px;"
        >
          <el-option
              v-for="author in authorOptions"
              :key="author"
              :label="author"
              :value="author"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="分支筛选">
        <el-select
            v-model="selectedBranch"
            placeholder="选择分支"
            clearable
            @change="handleFilterChange"
            style="width: 200px"
        >
          <el-option
              v-for="branch in branchOptions"
              :key="branch"
              :label="branch"
              :value="branch"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="时间范围">
        <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            @change="handleDateChange"
        />
      </el-form-item>
    </el-form>
    <el-timeline>
      <el-timeline-item
          v-for="commit in commits"
          :key="commit.hash"
          :timestamp="commit.date"
          :color="branchColors[commit.branch]"
          placement="top"
          class="timeline-node"
          :class="{ 'merge-commit': commit.isMerge }"
      >
        <el-card shadow="hover" class="commit-card">
          <div class="commit-header">
            <el-tag :type="getBranchTagType(commit.branch)">
              {{ commit.branch }}
            </el-tag>
            <el-link :href="'/commit?id='+houseId+'&&commit='+commit.hash">{{ commit.hash }}</el-link>
          </div>
          <p class="message">{{ commit.message }}</p>
          <div class="commit-footer">
            <span>👤 {{ commit.author }}</span>
            <span class="commit-date">📅 {{ commit.date }}</span>
          </div>
          <div>
            <el-divider></el-divider>
            <el-link v-for="cc in commit.parents" :href="'/commit?id='+houseId+'&&commit='+cc">{{ cc }}</el-link>
          </div>
        </el-card>
      </el-timeline-item>
    </el-timeline>
  </div>
</template>

<style scoped>
.git-history-timeline {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.timeline-node {
  min-height: 100px;
}

.commit-card {
  margin: 10px 0;
  transition: transform 0.2s;
}

.commit-card:hover {
  transform: translateX(10px);
}

.commit-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.hash {
  font-family: monospace;
  font-size: 0.9em;
  color: #666;
}

.message {
  margin: 8px 0;
  color: #333;
  font-weight: 500;
}

.commit-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 12px;
  color: #888;
  font-size: 0.9em;
}

.merge-commit :deep(.el-timeline-item__node) {
  width: 18px;
  height: 18px;
}

:deep(.el-timeline-item__timestamp) {
  font-size: 0.9em;
  color: #666;
  padding-bottom: 8px;
}
</style>
