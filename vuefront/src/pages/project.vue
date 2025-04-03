<template>
  <div class="code-page-container">


    <!-- 右侧主内容 -->
    <el-main class="main-content">
      <!-- 顶部操作栏 -->
      <div class="operation-bar">
        <el-space :size="20">
          <el-select
              v-model="selectedBranch"
              placeholder="Select branch"
              style="width: 180px"
              @change="changeBranch"
          >
            <el-option
                v-for="item in branches"
                :key="item"
                :label="item"
                :value="item"
            />
          </el-select>

          <el-button
              type="primary"
              @click="handleClone"
              icon="DocumentCopy"
          >
            Clone
          </el-button>

          <el-statistic title="Branches" :value="branches.length"/>
          <el-statistic title="Tags" :value="tags.length"/>
        </el-space>
      </div>

      <!-- 文件目录 + 简介双栏布局 -->
      <el-row :gutter="20">
        <!-- 文件树 -->
        <el-col :span="16">
          <el-card shadow="never">
            <el-tree
                :data="fileTree"
                :props="treeProps"
                node-key="path"
                default-expand-all
                @node-click="handleFileClick"
            >
              <template #default="{ data }">
                <el-icon class="file-icon">
                  <Folder v-if="data.dir"/>
                  <Document v-else/>
                </el-icon>
                <span style="margin-right: 30%">{{ data.name }}</span>
                <el-link :href="'/commit?id='+houseId+'&&commit='+data.submitHash">{{ data.message }}</el-link>
                <span style="margin-left: 30%">{{ data.updateTime }}</span>

              </template>
            </el-tree>
          </el-card>
        </el-col>

        <!-- 简介面板 -->
        <el-col :span="8">
          <el-card shadow="never">
            <template #header>
              <el-tooltip
                  class="box-item"
                  effect="dark"
                  :content="user.name"
                  placement="top"
              >
                <el-avatar :src="user.av"></el-avatar>
              </el-tooltip>

            </template>
            <div class="description">
              <h3>{{ project.name }}</h3>
              <p class="update-time">Last updated: {{ project.updateTime }}</p>
              <el-divider/>
              <p>{{ project.description }}</p>
              <el-tag
                  v-for="tag in tags"
                  :key="tag"
                  type="info"
                  class="m-2"
              >
                {{ tag }}
              </el-tag>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="20">
          <el-col :span="16" >
            <vue-markdown-editor v-model="readme" :mode="'preview'" v-if="readme"></vue-markdown-editor>
          </el-col>

      </el-row>
    </el-main>


  </div>
  <el-dialog title="克隆仓库" v-model="showCloneDialog" width="40%">
    <h2>SSH:</h2>
    <el-input v-model="cloneUrl" readonly>
      <template #append>
        <el-button slot="append" @click="copyToClipboard(this.cloneUrl)">复制</el-button>
      </template>

    </el-input>
    <h2>Http:</h2>
    <el-input v-model="httpUrl" readonly>
      <template #append>
        <el-button slot="append" @click="copyToClipboard(this.httpUrl)">复制</el-button>
      </template>

    </el-input>
  </el-dialog>
  <el-dialog v-model="showCode" width="70%" style="height: 80%;overflow: scroll">
    <my-code :language="language" :code="chooseFileContent" v-if="showCode" :type="type"></my-code>
  </el-dialog>
</template>

<script>
import {Message, MessageBox} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import axios from "axios";
import VueMarkdownEditor, {xss} from '@kangc/v-md-editor';
import '@kangc/v-md-editor/lib/style/base-editor.css';
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js';
import 'highlight.js/styles/github.css'; // 选择一种你喜欢的主题
import myCode from "@/components/Code.vue"

export default {
  name: 'YourComponentName',
  components: {
    VueMarkdownEditor,
    myCode
  },
  data() {
    return {
      httpUrl:"",
      user:JSON.parse(localStorage.getItem("user")).user,
      readme:"",
      type:0,
      language: 'py',
      showCode: false,
      chooseFileContent: "",
      tags: [],
      houseId: -1,
      showCloneDialog: false,
      cloneUrl: 'git@github.com:vue-gitee-example.git',
      activeMenu: 'code',
      selectedBranch: 'main',
      project: {
        name: 'Vue-Gitee-Example',
        description: 'A demo repository to demonstrate Gitee-like interface implementation using Vue3 + Element Plus',
        lastUpdate: '2025-02-27',

      },
      fileTree: [
        {
          name: 'src',
          path: '/src',
          dir: true,
          children: [
            {name: 'main.js', path: '/src/main.js'},
            {name: 'App.vue', path: '/src/App.vue'},
            {
              name: 'kkk',
              path: '/src/kkk',
              dir: true,
              children: [
                {name: 'main.js', path: '/src/main.js'},
                {name: 'App.vue', path: '/src/App.vue'},
              ]

            }
          ]
        },
        {
          name: 'public',
          path: '/public',
          isDir: true,
          children: [
            {name: 'index.html', path: '/public/index.html'}
          ]
        }
      ],
      branches: [
        {name: 'main'},
        {name: 'dev'},
        {name: 'feat/login'}
      ],
      treeProps: {
        label: 'name',
        children: 'children'
      }
    };
  },
  methods: {
    changeBranch(branch){
      var readmeFiles=null
      axios.post("/git/changeBranch" ,{
          id:this.houseId,
          branch:branch
      }).then((res) => {
        if (res.data.status == 200) {

          this.fileTree = res.data.data
          this.fileTree.sort((a, b) => {
            // 如果 a.dir 为 true 而 b.dir 不是，则 a 应排在前面
            if (a.dir && !b.dir) return -1;
            // 如果 b.dir 为 true 而 a.dir 不是，则 b 应排在前面
            if (!a.dir && b.dir) return 1;
            // 如果两者相同，则保持原有顺序不变
            return 0;
          });
          readmeFiles = this.fileTree.filter(item =>
              !item.dir && item.name.toLowerCase() === 'readme.md'
          );


          if(readmeFiles&&readmeFiles.length>0){
            axios.post("/git/catFile/" + this.houseId + "/" + readmeFiles[0].fileHash + "/" + 0).then((res) => {

              if (res.data.status == 200) {
                this.readme = res.data.data
              } else {
                ElMessage.error(res.data.message)
              }
            })
          }else {
            this.readme=null
          }

        } else {
          ElMessage.error(res.data.message);
        }
      })


    },
    handleClone() {
      this.showCloneDialog = true;
    },
    handleFileClick(data) {
      if (data.dir) {
        if (data.show) return
        var data11 = {
          houseId: this.houseId,
          fileHash: data.fileHash,
          path: data.path
        }
        axios.post("/git/openDir", data11).then((res) => {
          var d = data.name.split(".")
          if (d.length == 2) this.language = d[1]
          else this.language = "java"

          data.children = res.data.data
          data.children.sort((a, b) => {
            // 如果 a.dir 为 true 而 b.dir 不是，则 a 应排在前面
            if (a.dir && !b.dir) return -1;
            // 如果 b.dir 为 true 而 a.dir 不是，则 b 应排在前面
            if (!a.dir && b.dir) return 1;
            // 如果两者相同，则保持原有顺序不变
            return 0;
          });

          data.show = true
        })
      } else {
        var type = 0;
        if (this.checkIfBinary(data.name)) {
          type = 1
          if(!this.allowFile(data.name)){
              ElMessage.error("不支持此文件类型查看");
              return;
          }
        }
        axios.post("/git/catFile/" + this.houseId + "/" + data.fileHash + "/" + type).then((res) => {

          if (res.data.status == 200) {
            if (type == 0) this.chooseFileContent = res.data.data
            if (type == 1) this.chooseFileContent = 'data:image/png;base64,' + res.data.data
            this.showCode = true
            this.type=type
          } else {
            ElMessage.error(res.data.message)
          }


        })
      }
    },
    copyToClipboard(d) {
      const el = document.createElement('textarea');
      el.value = d;
      document.body.appendChild(el);
      el.select();
      document.execCommand('copy');
      document.body.removeChild(el);
      this.$message({
        message: '克隆地址已复制到剪贴板',
        type: 'success'
      });
    },
    checkIfBinary(fileName) {
      // 常见的二进制文件扩展名列表
      const binaryExtensions = [
        'png', 'jpg', 'jpeg', 'gif', 'bmp', 'tiff', 'ico', // 图片格式
        'mp4', 'avi', 'mov', 'mkv', 'flv', 'wmv', // 视频格式
        'mp3', 'wav', 'ogg', 'flac', 'aac', // 音频格式
        'pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', // 文档格式
        'zip', 'rar', '7z', 'tar', 'gz', // 压缩格式
        'exe', 'dll', 'so', 'class', // 可执行文件或库
        'psd', 'ai', 'eps', 'svgz', // 设计和矢量图格式
        'obj', 'fbx', 'stl', 'glb', 'gltf' // 3D模型格式
      ];

      const extension = fileName.split('.').pop().toLowerCase();
      return binaryExtensions.includes(extension);
    },
    allowFile(fileName) {
      const binaryExtensions = [
        'png', 'jpg', 'jpeg', 'gif', 'bmp', 'tiff', 'ico', // 图片格式
      ];

      const extension = fileName.split('.').pop().toLowerCase();
      return binaryExtensions.includes(extension);
    }
  },
  created() {
    const params = new URLSearchParams(window.location.search);
    this.houseId = params.get('id'); // 假设你要获取名为 'param1' 的参数
    var readmeFiles=null
    axios.post("/git/getProject/" + this.houseId+"/"+this.user.id).then((res) => {
      if (res.data.status == 200) {

        this.fileTree = res.data.data.fileTree
        this.branches = res.data.data.branches
        this.project = res.data.data.house
        this.tags = res.data.data.tags
        this.cloneUrl=res.data.data.clone
        this.httpUrl=res.data.data.httpClone
        this.selectedBranch = this.branches[this.branches.length-1]
        this.fileTree.sort((a, b) => {
          // 如果 a.dir 为 true 而 b.dir 不是，则 a 应排在前面
          if (a.dir && !b.dir) return -1;
          // 如果 b.dir 为 true 而 a.dir 不是，则 b 应排在前面
          if (!a.dir && b.dir) return 1;
          // 如果两者相同，则保持原有顺序不变
          return 0;
        });
         readmeFiles = this.fileTree.filter(item =>
            !item.dir && item.name.toLowerCase() === 'readme.md'
        );
        if(readmeFiles&&readmeFiles.length>0){
          axios.post("/git/catFile/" + this.houseId + "/" + readmeFiles[0].fileHash + "/" + 0).then((res) => {

            if (res.data.status == 200) {
              this.readme = res.data.data
            } else {
              ElMessage.error(res.data.message)
            }
          })
        }else {
          this.readme=""
        }
      } else {
        ElMessage.error(res.data.message);
      }
    })

  }
};
</script>


<style scoped>
.code-page-container {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
}

.nav-menu {
  background: #ffffff;
  border-right: 1px solid #ebeef5;
}

.main-content {
  padding: 20px 30px;
}

.operation-bar {
  margin-bottom: 20px;
  padding: 15px;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, .1);
}

.file-icon {
  margin-right: 8px;
  color: #909399;
}

.description {
  line-height: 1.8;
  color: #606266;
}

.update-time {
  color: #909399;
  font-size: 0.9em;
}
</style>
