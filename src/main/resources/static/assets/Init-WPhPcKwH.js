import{_ as c,c as h,o as b,w as A,r as L,a as v,b as a,d as n,e as p,f as l,g as f}from"./index-CL2Ozjlc.js";const k={data(){return{load:!1,formLabelAlign:{name:"",password:"",repeatPassword:"",email:""},statuses:["wait","wait","wait"]}},props:{},methods:{addUser(){if(this.formLabelAlign.password!=this.formLabelAlign.repeatPassword){alert("两次密码不同");return}var s={name:this.formLabelAlign.name,email:this.formLabelAlign.email,password:this.formLabelAlign.password,role:0};this.load=!0,p.post("/addUser",s).then(e=>{e.data.status==200?(this.load=!1,window.location.href="/index"):(alert(e.data.message),this.load=!1)})},initEnv(){if(this.formLabelAlign.password!=this.formLabelAlign.repeatPassword){alert("两次密码不同");return}var s={name:this.formLabelAlign.name,email:this.formLabelAlign.email,password:this.formLabelAlign.password,role:0};this.load=!0,p.post("/initEnv",s).then(e=>{e.data.status==200?(alert("初始化成功"),this.load=!1,window.location.href="/index"):(alert(e.data.message),this.load=!1)})},checkEnv(){p.get("/getEnvStatus").then(s=>{s.data.status==200&&(this.statuses=s.data.data)})}},created(){}},y={class:"form-container"};function V(s,e,x,C,t,d){const m=n("el-step"),g=n("el-steps"),u=n("el-button"),i=n("el-form-item"),r=n("el-input"),w=n("el-form"),_=L("loading");return b(),h("div",y,[A((b(),v(w,{ref:"form","label-position":"top","label-width":"120px",model:t.formLabelAlign,style:{width:"100%","max-width":"600px",margin:"auto"}},{default:a(()=>[l(i,null,{default:a(()=>[l(g,{"finish-status":"success",simple:"",style:{"margin-bottom":"20px"}},{default:a(()=>[l(m,{title:"SSH环境配置",status:t.statuses[0]},null,8,["status"]),l(m,{title:"SSH密钥认证",status:t.statuses[1]},null,8,["status"]),l(m,{title:"Git环境配置",status:t.statuses[2]},null,8,["status"])]),_:1}),l(u,{type:"primary",style:{display:"block",margin:"0 auto 20px"},onClick:d.checkEnv},{default:a(()=>e[4]||(e[4]=[f("检查配置")])),_:1},8,["onClick"])]),_:1}),l(i,{label:"Git超级管理员名称"},{default:a(()=>[l(r,{modelValue:t.formLabelAlign.name,"onUpdate:modelValue":e[0]||(e[0]=o=>t.formLabelAlign.name=o)},null,8,["modelValue"])]),_:1}),l(i,{label:"Git超级管理员密码"},{default:a(()=>[l(r,{modelValue:t.formLabelAlign.password,"onUpdate:modelValue":e[1]||(e[1]=o=>t.formLabelAlign.password=o),"show-password":""},null,8,["modelValue"])]),_:1}),l(i,{label:"重复密码"},{default:a(()=>[l(r,{modelValue:t.formLabelAlign.repeatPassword,"onUpdate:modelValue":e[2]||(e[2]=o=>t.formLabelAlign.repeatPassword=o),"show-password":""},null,8,["modelValue"])]),_:1}),l(i,{label:"Git超级管理员邮箱"},{default:a(()=>[l(r,{modelValue:t.formLabelAlign.email,"onUpdate:modelValue":e[3]||(e[3]=o=>t.formLabelAlign.email=o)},null,8,["modelValue"])]),_:1}),l(i,null,{default:a(()=>[l(u,{type:"primary",style:{display:"block",margin:"0 auto"},onClick:d.initEnv,disabled:t.load},{default:a(()=>e[5]||(e[5]=[f("初始化")])),_:1},8,["onClick","disabled"]),l(u,{type:"primary",style:{display:"block",margin:"0 auto"},onClick:d.addUser,disabled:t.load},{default:a(()=>e[6]||(e[6]=[f("直接添加管理员")])),_:1},8,["onClick","disabled"])]),_:1})]),_:1},8,["model"])),[[_,t.load]])])}const U=c(k,[["render",V],["__scopeId","data-v-89398b53"]]);export{U as default};
