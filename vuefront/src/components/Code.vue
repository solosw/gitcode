<template>
  <div>
    <!-- 代码高亮组件 -->
    <pre class="hljs-container"><code :class="languageClass" ref="codeBlock">{{ formattedCode }}</code></pre>
  </div>
</template>

<script>
import hljs from 'highlight.js';
import 'highlight.js/styles/github.css'; // 选择一种你喜欢的主题
import 'highlightjs-line-numbers.js';

export default {
  name: 'CodeHighlighter',
  props: {
    code: {
      type: String,
      required: true
    },
    language: {
      type: String,
      required: true
    }
  },
  computed: {
    languageClass() {
      return `hljs ${this.language}`;
    },
    formattedCode() {
      return this.code;
    }
  },
  mounted() {
    this.highlightCode();

  },
  created() {
      setTimeout(()=>{
        this.highlightCode();
      },1000)
  },
  methods: {
    highlightCode() {
      const codeBlock = this.$refs.codeBlock;
      hljs.highlightElement(codeBlock);
    },
  }
};
</script>

<style scoped>
.hljs-container {
  position: relative;
}

.hljs {
  padding-left: 30px; /* 确保行号不会覆盖代码 */
}
</style>
