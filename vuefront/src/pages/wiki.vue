<template>
  <div class="wiki-container">


    <!-- 左侧导航 -->
    <nav class="doc-nav">
      <el-button type="success" size="small" @click="addDoc" v-if="house.creatorId==user.id">新增说明</el-button>
      <div
          v-for="doc in docs"
          :key="doc.id"
          class="nav-item"
          :class="{ active: activeDoc.id === doc.id }"
          @click="switchDoc(doc)"
      >
        {{ doc.title }}
      </div>

    </nav>

    <!-- 右侧内容 -->
    <main class="doc-content">
      <el-space direction="horizontal">
        <el-button type="primary" size="small" @click="()=>{this.mode=='editable'?this.mode='preview':this.mode='editable'}" v-if="house.creatorId==user.id">{{this.mode=='preview'?'修改':'预览'}}</el-button>
        <el-button type="warning" size="small" @click="deleteContent()" v-if="house.creatorId==user.id">删除</el-button>
      </el-space>

      <transition name="fade" mode="out-in">

        <v-md-editor

            :mode="mode"
            @save="changeContent"
            :include-level="[1,2,3,4]"  v-model="highlightedContent" style="width: 100%" @upload-image="handleUploadImage" :disabled-menus="[]"></v-md-editor>

      </transition>


    </main>
  </div>
  <el-dialog v-model="show" style="width: 80%;height: 80%">
    <el-input placeholder="标题" v-model="title"></el-input>
    <v-md-editor
        @save="saveBeforeClose"
                 :include-level="[1,2,3,4]"  v-model="content" height="400px" @upload-image="handleUploadImage" :disabled-menus="[]"></v-md-editor>

  </el-dialog>

</template>
<script>
import MarkdownIt from "markdown-it";
import DOMPurify from 'dompurify';
import VueMarkdownEditor, {xss} from '@kangc/v-md-editor';
import { Editor, EditorContent } from '@tiptap/vue-3';
import StarterKit from '@tiptap/starter-kit';
import { Image } from '@tiptap/extension-image';
import axios from "axios";
import VMdPreview from '@kangc/v-md-editor/lib/preview';

import VMdEditor from '@kangc/v-md-editor'
import '@kangc/v-md-editor/lib/style/base-editor.css'
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js'
import '@kangc/v-md-editor/lib/theme/style/github.css'
import hljs from "highlight.js/lib/core";
import {ElMessage} from "element-plus";
import Router from "@/router/index.js";

VMdEditor.use(githubTheme, {
  Hljs: hljs,
})
VMdPreview.use(githubTheme, {
  Hljs: hljs,
});
export default {
  components:{
    VueMarkdownEditor,
    EditorContent,
    VMdEditor,
    VMdPreview
  },

  data() {
    return {
      house:{},
      user:JSON.parse(localStorage.getItem("user")).user,
      ed:null,
      houseId:-1,
      content:'',
      title:'',
      // 文档数据
      docs: [

      ],
      // 当前活动文档
      activeDoc: null,
      show:false,
      mode:'preview'
    };
  },
  computed: {
    // 代码高亮处理
    highlightedContent: {
      get() {
        // 返回 activeDoc 的 content，如果没有 activeDoc，则返回空字符串
        return this.activeDoc ? this.activeDoc.content : '';
      },
      set(newValue) {
        // 当设置新的值时，更新 activeDoc 的 content
        if (this.activeDoc) {
          this.activeDoc.content = newValue;
        }
      }
    },
    compiledMarkdown() {
      const md = new MarkdownIt();
      return DOMPurify.sanitize(md.render(this.activeDoc.content));
    }
  },
  methods: {
    deleteContent(){

      this.$confirm(`确定要删除吗？`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
          .then(() => {
            axios.post("/wiki/delete/"+this.activeDoc.id).then((res)=>{
              if(res.data.status==200){
                ElMessage.success("成功")
                location.reload()
              }
            })

          })
          .catch(() => {
            this.$message.info("已取消删除");
          });



    },

    changeContent(){
      axios.post("/wiki/update",this.activeDoc).then((res)=>{
        if(res.data.status==200){
          ElMessage.success("成功")
          location.reload()
        }
      })
    },
    switchDoc(doc) {
      this.activeDoc = doc;
    },
    addDoc() {
      this.show = true
      this.content = localStorage.getItem("wiki_" + this.houseId) ? localStorage.getItem("wiki_" + this.houseId) : '';
      this.title=localStorage.getItem("wikiTitle_" + this.houseId)? localStorage.getItem("wikiTitle_" + this.houseId) : ''
    },
    saveBeforeClose(text,html) {
      if(!this.title){
        ElMessage.error("标题不能为空")
        return
      }
      axios.post("/wiki/add",{
        houseId:this.houseId,
        title:this.title,
        content:this.content
      }).then((res)=>{
        if(res.data.status==200){
            ElMessage.success("成功")
            location.reload()
        }
      })
      localStorage.setItem("wiki_" + this.houseId, this.content)
      localStorage.setItem("wikiTitle_" + this.houseId, this.title)
    },
    handleUploadImage(event, insertImage, files) {
      // 拿到 files 之后上传到文件服务器，然后向编辑框中插入对应的内容
      const formData = new FormData();
      formData.append('file', files[0]);
      axios.post('/files/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data', // 设置请求头为 multipart/form-data
          'Authorization':localStorage.getItem("token")
        },
      })
          .then(response => {
            // 假设服务器返回的数据格式为 { url: '图片的URL地址', desc: '图片描述' }
            const imageUrl = response.data.data;
            const imageDesc = response.data.desc || 'none'; // 如果没有描述，使用默认值

            // 插入图片到编辑器
            insertImage({
              url: imageUrl,
              desc: imageDesc,
              width: '200px',
              height: '200px',
            });
          })


    },
  },
  created() {
    // 初始化时设置默认文档
    this.activeDoc = this.docs[0];
    const params = new URLSearchParams(window.location.search);
    this.houseId = params.get('id'); // 假设你要获取名为 'param1' 的参数
    axios.post("/house/getHouseById/"+this.houseId).then((res)=>{
      if(res.data.status==200){
        this.house=res.data.data
      }
    })
    axios.post("/wiki/get/"+this.houseId).then((res)=>{
      if(res.data.status==200){
        this.docs=res.data.data
        this.activeDoc=this.docs[0]
      }
    })
  }
};
</script>


<style scoped>
.wiki-container {
  display: grid;
  grid-template-columns: 240px 1fr;
  min-height: 100vh;
  font-family: system-ui;
}

.doc-nav {
  background: #f8fafc;
  border-right: 1px solid #e2e8f0;
  padding: 1rem;
}

.nav-item {
  padding: 12px;
  margin: 4px 0;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.nav-item:hover {
  background: #e2e8f0;
}

.nav-item.active {
  background: #3b82f6;
  color: white;
  font-weight: 500;
}

.doc-content {
  padding: 2rem 3rem;
  max-width: 800px;
  line-height: 1.6;
}

/* Markdown样式增强 */
:deep(code) {
  background: #f1f5f9;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: Monaco, Consolas, monospace;
}

:deep(table) {
  border-collapse: collapse;
  margin: 1rem 0;
  width: 100%;
}

:deep(th) {
  background: #f8fafc;
}

:deep(td), :deep(th) {
  padding: 12px;
  border: 1px solid #e2e8f0;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .wiki-container {
    grid-template-columns: 1fr;
  }

  .doc-nav {
    position: sticky;
    top: 0;
    z-index: 10;
    border-right: none;
    border-bottom: 1px solid #e2e8f0;
  }
}
</style>
