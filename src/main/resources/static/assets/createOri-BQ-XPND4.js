import{_ as v,c as y,o as m,f as o,b as r,d as l,e as x,E as i,N as U,h as k,a as _,g}from"./index-CeHthxjz.js";const V={computed:{UserFilled(){return U}},data(){return{form:{name:"",description:"",userId:JSON.parse(localStorage.getItem("user")).user.id,avatarUrl:""},uploadHeaders:{Authorization:"none"}}},props:{},methods:{handleAvatarSuccess(t){this.form.avatarUrl=t.data,i.success("头像上传成功")},beforeAvatarUpload(t){const e=["image/jpeg","image/png","image/jpg"].includes(t.type),d=t.size/1024/1024<5;return e||i.error("仅支持 JPG/PNG 格式!"),d||i.error("图片大小不能超过 5MB!"),e&&d},onSubmit(){x.post("/user/createOrganization",this.form).then(t=>{t.data.status==200?location.href="/index":i(t.data.message)})}},created(){this.uploadHeaders.Authorization=localStorage.getItem("token")}},z={class:"create-organization"},S={style:{display:"flex","align-items":"center"}};function A(t,e,d,N,a,s){const p=l("el-avatar"),c=l("el-button"),b=l("el-upload"),n=l("el-form-item"),f=l("el-input"),h=l("el-form");return m(),y("div",z,[o(h,{model:a.form,"label-width":"120px",ref:"organizationForm",style:{height:"100%"},"label-position":"top"},{default:r(()=>[o(n,{label:"头像"},{default:r(()=>[k("div",S,[a.form.avatarUrl?(m(),_(p,{key:0,size:80,src:a.form.avatarUrl,style:{"margin-right":"20px"}},null,8,["src"])):(m(),_(p,{key:1,size:80,icon:s.UserFilled,style:{"margin-right":"20px"}},null,8,["icon"])),o(b,{action:"/back/files/upload","show-file-list":!1,"on-success":s.handleAvatarSuccess,"before-upload":s.beforeAvatarUpload,headers:a.uploadHeaders},{default:r(()=>[o(c,{type:"primary"},{default:r(()=>e[2]||(e[2]=[g("组织头像")])),_:1})]),_:1},8,["on-success","before-upload","headers"])])]),_:1}),o(n,{label:"名称",prop:"name",rules:[{required:!0,message:"请输入组织名称",trigger:"blur"}]},{default:r(()=>[o(f,{modelValue:a.form.name,"onUpdate:modelValue":e[0]||(e[0]=u=>a.form.name=u)},null,8,["modelValue"])]),_:1}),o(n,{label:"描述",prop:"description"},{default:r(()=>[o(f,{modelValue:a.form.description,"onUpdate:modelValue":e[1]||(e[1]=u=>a.form.description=u),type:"textarea"},null,8,["modelValue"])]),_:1}),o(n,null,{default:r(()=>[o(c,{type:"primary",onClick:s.onSubmit},{default:r(()=>e[3]||(e[3]=[g("创建组织")])),_:1},8,["onClick"])]),_:1})]),_:1},8,["model"])])}const I=v(V,[["render",A],["__scopeId","data-v-e37c3093"]]);export{I as default};
