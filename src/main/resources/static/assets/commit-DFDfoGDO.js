import{_ as w,w as m,j as v,r as D,o,a as g,b as l,d as i,e as E,E as k,c as x,F as N,m as M,n as y,h as r,f as B,p as F,t as h}from"./index-CeHthxjz.js";const I={created(){const t=new URLSearchParams(window.location.search);var s=t.get("id");this.houseId=s;var c=t.get("commit");this.loading=!0,E.post("/git/getDifference/"+s+"/"+c).then(n=>{n.data.status==200&&(this.files=n.data.data),this.files.length==0&&k.info("首次提交不展示修改"),this.loading=!1})},data(){return{commitHash:"",files:[],loading:!1,error:null,statusMap:{ADD:"新增",MODIFY:"修改",DELETE:"删除",COPY:"复制",RENAME:"重命名"},houseId:-1}},methods:{getlanague(t){var s=t.split(".");return s.length==2?s[1]:""},lookContent(t){}}},L={class:"status-tag"},S={class:"file-path"},V={style:{width:"100%"}};function b(t,s,c,n,a,d){const u=i("code-diff"),_=i("el-collapse-item"),p=i("el-collapse"),f=D("loading");return a.files.length?m((o(),g(p,{key:0,onChange:d.lookContent},{default:l(()=>[(o(!0),x(N,null,M(a.files,(e,C)=>(o(),g(_,{name:C,class:y(["file-item",e.status])},{title:l(()=>[r("span",L,h(a.statusMap[e.status]),1),r("span",S,h(e.fileName),1)]),default:l(()=>[m(r("div",V,[B(u,{"old-string":e.oldContent,"new-string":e.newContent,"output-format":"side-by-side",lanauge:d.getlanague(e.fileName)},null,8,["old-string","new-string","lanauge"])],512),[[F,e.newContent||e.oldContent]])]),_:2},1032,["name","class"]))),256))]),_:1},8,["onChange"])),[[f,a.loading]]):v("",!0)}const O=w(I,[["render",b]]);export{O as default};
