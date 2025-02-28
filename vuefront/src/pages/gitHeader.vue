<template>
  <el-tabs type="card" class="demo-tabs" >
    <el-tab-pane>
      <template #label>
        <span class="custom-tabs-label">
          <el-icon><document /></el-icon>
          <span>Code</span>
        </span>
      </template>
    </el-tab-pane>
    <el-tab-pane label="Config">
      <template #label>
        <span class="custom-tabs-label">
          <el-icon><collection /></el-icon>
          <span>Issues</span>
        </span>
    </template>
    </el-tab-pane>
    <el-tab-pane label="Role">
      <template #label>
        <span class="custom-tabs-label">
          <el-icon><notebook /></el-icon>
          <span>Wiki</span>
        </span>
      </template>
    </el-tab-pane>
    <el-tab-pane label="Task">
      <template #label>
        <span class="custom-tabs-label">
          <el-icon><Timer /></el-icon>
          <span>History</span>
        </span>
      </template>
    </el-tab-pane>
    <el-tab-pane label="Task">
      <template #label>
        <span class="custom-tabs-label">
          <el-icon><Brush /></el-icon>
          <span>pull Requests</span>
        </span>
      </template>
    </el-tab-pane>
    <el-tab-pane label="Task">
      <template #label>
        <span class="custom-tabs-label">
          <el-icon><setting /></el-icon>
          <span>Setting</span>
        </span>
      </template>
    </el-tab-pane>
  </el-tabs>

  <router-view></router-view>
</template>

<style  scoped>
.github-nav {
  padding: 0 24px;
  border-bottom: 1px solid #30363d;

  .el-menu-item {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 0 12px;
    font-size: 14px;
    transition: all 0.2s;

    &:hover {
      background-color: rgba(255,255,255,0.08) !important;
    }

    i {
      font-size: 16px;
      margin-right: 0;
    }
  }

  .settings-item {
    margin-left: auto;
    border-left: 1px solid #30363d;
  }
}
.demo-tabs > .el-tabs__content {
  padding: 32px;
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
}
</style>
<script setup>
import {Brush, Collection, Document, Notebook, Setting, Timer} from "@element-plus/icons-vue";
</script>
