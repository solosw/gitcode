<template>
  <div class="settings-container">
    <el-row :gutter="20">
      <!-- 侧边导航 -->
      <el-col :span="4">
        <el-menu
            :default-active="activeTab"
            class="settings-menu"
            @select="handleMenuSelect"
        >
          <el-menu-item index="general">
            <i class="el-icon-setting"></i>
            <span>基础设置</span>
          </el-menu-item>
          <el-menu-item index="branches">
            <i class="el-icon-folder-opened"></i>
            <span>分支设置</span>
          </el-menu-item>
          <el-menu-item index="collaborators">
            <i class="el-icon-user"></i>
            <span>协作者</span>
          </el-menu-item>
          <el-menu-item index="danger">
            <i class="el-icon-warning"></i>
            <span>危险操作</span>
          </el-menu-item>
        </el-menu>
      </el-col>

      <!-- 主内容区 -->
      <el-col :span="20">
        <!-- 通用设置 -->
        <el-card v-show="activeTab === 'general'" class="settings-card">
          <div slot="header">
            <span>基础设置</span>
          </div>
          <el-form :model="house" label-width="120px">
            <el-form-item label="名称">
              <el-input
                  v-model="house.name"
                  placeholder="名称"
              ></el-input>
            </el-form-item>

            <el-form-item label="描述">
              <el-input
                  v-model="house.description"
                  type="textarea"
                  :rows="3"
                  placeholder="描述"
              ></el-input>
            </el-form-item>

            <el-form-item label="可见性">
              <el-radio-group v-model="house.kind">
                <el-radio :label="0" :value="0">Public</el-radio>
                <el-radio :label="1" :value="1">Private</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item>
              <el-button
                  type="primary"
                  :loading="saving"
                  @click="saveSettings"
              >
                确认修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 分支管理 -->
        <el-card v-show="activeTab === 'branches'" class="settings-card">
          <div slot="header">
            <span>分支管理</span>
          </div>
          <el-table :data="branchRule" style="width: 100%">
            <el-table-column  label="分支名称" width="200">
              <template #default="scope">
                  <div>{{scope.row.name}}</div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button size="small" @click="protectBranch(scope.row)">{{scope.row.rule!='R'?'保护分支':'解除保护'}}</el-button>
                <el-button size="small" type="danger" @click="deleteBranch(scope.row)">删除分支</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 协作者管理 -->
        <el-card v-show="activeTab === 'collaborators'" class="settings-card">
          <div slot="header">
            <span>协作者管理</span>
          </div>
          <div class="collaborators">
            <div>
              <el-tag
                  v-for="collab in collaborators"
                  :key="collab.id"
                  closable
                  @close="removeCollaborator(collab)"
                  @click="showCollaborator(collab)"
              >
                {{ collab.name }}
              </el-tag>
            </div>
            <el-divider></el-divider>

            <div class="add-collaborator" style="float: left">
              <el-button
                  type="primary"
                  icon="el-icon-plus"
                  @click="addCollaborator"
              >授权</el-button>
            </div>
          </div>
        </el-card>

        <!-- 危险区域 -->
        <el-card v-show="activeTab === 'danger'" class="danger-zone">
          <div slot="header">
            <span class="danger-title">危险区域</span>
          </div>

          <div class="danger-item">
            <el-button type="danger" @click="deleteRepository">Delete Repository</el-button>
            <p>This action cannot be undone...</p>
            <el-popconfirm
                title="确认删除仓库吗？"
                @confirm="deleteRepository"
            >
            </el-popconfirm>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
  <el-dialog v-model="vis">
    <el-form  label-width="120px">
      <el-form-item label="用户名">
        <el-input
            v-model="setting.username"
            placeholder="用户名"
        ></el-input>
      </el-form-item>
      <el-form-item label="授权分支"  v-if="setting.right!='C'">
        <el-select v-model="setting.branch">
          <el-option label="all" :value="'all'"></el-option>
          <el-option v-for="item in branches" :label="item" :value="item"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="权限">
        <el-radio-group v-model="setting.right">
          <el-radio  :value="'RW+'">RW+</el-radio>
          <el-radio  :value="'RW'">RW</el-radio>
          <el-radio  :value="'R'">R</el-radio>
          <el-radio  :value="'C'">Create</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item>
        <el-button
            type="primary"
            @click="sureCollaborator"
        >
          确认
        </el-button>
      </el-form-item>
    </el-form>
  </el-dialog>


  <el-dialog v-model="infovis">
    <el-table :data="showHouseRight.rights" style="width: 100%">
      <el-table-column prop="branch" label="分支" width="180"></el-table-column>
      <el-table-column prop="right" label="权限" width="180"></el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="scope">
            <el-button @click="deleteRights(scope.row)" type="warning">删除</el-button>
            <el-button @click="changeRights(scope.row)" type="primary">修改</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
  <el-dialog title="修改权限" v-model= "rigtVis" width="30%">
    <el-radio-group v-model="selectedRight.right">
      <el-radio label="RW+">RW+</el-radio>
      <el-radio label="RW">RW</el-radio>
      <el-radio label="R">R</el-radio>
    </el-radio-group>
    <div slot="footer" class="dialog-footer">
        <el-button @click="rigtVis = false">取消</el-button>
        <el-button type="primary" @click="confirmChange">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import axios from "axios";
import {ElMessage} from "element-plus";

export default {
  created() {
    const params = new URLSearchParams(window.location.search);
    var id = params.get('id'); // 假设你要获取名为 'param1' 的参数
    this.setting.houseId=id
    axios.post("/house/getHouseById/"+id).then((res)=>{
      if(res.data.status==200) this.house=res.data.data
    })
    axios.post("/house/getBranch/"+id).then((res)=>{
      if(res.data.status==200) this.branches=res.data.data
    })

    axios.post("/right/getUser/"+id).then((res)=>{
      if(res.data.status==200) {
        this.collaborators=res.data.data

      }else {
        ElMessage.error(res.data.message)
      }
    })

    axios.post("/branch/getBranchRule/"+id).then((res)=>{
      if(res.data.status==200) this.branchRule=res.data.data
    })
  },
  data() {
    return {
      rigtVis:false,
      selectedRight:{},
      infovis:false,
      showHouseRight:{},
      setting:{
          right:"R",
          username:"",
          branch:"",
          houseId:-1
      },
      activeTab: 'general',
      vis:false,
      house: {
        name: '',
        description: '',
        visibility: 'public',
        defaultBranch: 'main'
      },
      branches: ['main', 'dev'],
      collaborators: ['user1', 'user2'],
      newCollaborator: '',
      saving: false,
      isOwner: true,
      branchRule:[]
    }
  },
  methods: {
    deleteBranch(branchName) {
      this.$confirm(`确定要删除分支 "${branchName}" 吗？`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
          .then(() => {

            axios.post("/branch/deleteBranch",{
              houseId:this.setting.houseId,
              branchName:branchName.name
            }).then((res)=>{
              if(res.data.status==200){
                this.branchRule = this.branchRule.filter((branch) => branch !== branchName);
                this.$message.success(`已删除`);
              }
            })


            // TODO: 实现删除分支的后端逻辑
          })
          .catch(() => {
            this.$message.info("已取消删除");
          });
    },
    protectBranch(branchName) {

      if(branchName.rule!='R'){

        this.$confirm(`确定要保护分支 "${branchName.name}" 吗？`, "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "success",
        })
            .then(() => {
              axios.post("/branch/add",{
                houseId:this.setting.houseId,
                rule:"R",
                branchName:branchName.name
              }).then((res)=>{
                if(res.data.status==200){
                  branchName.rule='R'
                  this.$message.success(`成功`);
                }
              })

              // TODO: 实现删除分支的后端逻辑
            })
            .catch(() => {
              this.$message.info("成功");
            });

      }else {
        this.$confirm(`确定解除保护分支 "${branchName.name}" 吗？`, "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "success",
        })
            .then(() => {
              axios.post("/branch/deleteBranchRule",{
                houseId:this.setting.houseId,
                branchName:branchName.name
              }).then((res)=>{
                if(res.data.status==200){
                  branchName.rule='none'
                  this.$message.success(`成功`);
                }
              })

              // TODO: 实现删除分支的后端逻辑
            })
            .catch(() => {
              this.$message.info("成功");
            });
      }

    },

    confirmChange(){
        axios.post("/right/changeRight",
          {id:this.showHouseRight.id,rights:JSON.stringify(this.showHouseRight.rights)}).then((res)=>{
        if(res.data.status==200){
          location.reload()
        }else {
          ElMessage.error(res.data.message)
        }
      })
    },
    deleteRights(right){
        this.showHouseRight.rights=this.showHouseRight.rights.filter(
            (data) => data !== right
        );
        axios.post("/right/changeRight",
            {id:this.showHouseRight.id,rights:JSON.stringify(this.showHouseRight.rights)}).then((res)=>{
              if(res.data.status==200){
                  location.reload()
              }else {
                ElMessage.error(res.data.message)
              }
        })
    },
    changeRights(right){
        this.rigtVis=true
        this.selectedRight=right
    },
    showCollaborator(user){
        this.infovis=true
        const params = new URLSearchParams(window.location.search);
        var id = params.get('id'); // 假设你要获取名为 'param1' 的参数
        axios.post("/right/getUserDetail/"+id+"/"+user.id).then((res)=>{
            if(res.data.status==200){
              this.showHouseRight=res.data.data
              this.showHouseRight.rights=JSON.parse(this.showHouseRight.rights)
            }else{
              ElMessage.error(res.data.message)
            }
        })
    },
    sureCollaborator(){
      axios.post("/right/addUser",this.setting).then((res)=>{
        if(res.data.status==200) {
          ElMessage.success("成功");
          location.reload()
        }else {
          ElMessage.error(res.data.message)
        }
      })
    },
    handleMenuSelect(index) {
      this.activeTab = index;
    },
    saveSettings() {
      this.saving = true;
      axios.post("/house/changeType",{
        id:this.house.id,
        kind:this.house.kind,
        name:this.house.name,
        des:this.house.description
      }).then((res)=>{
        if(res.data.status==200){
          setTimeout(() => {
            this.saving = false;
            this.$message.success('设置保存成功');
            location.reload()
          }, 1000);

        }else {
          ElMessage.error(res.data.message)
        }
      })


    },
    addCollaborator() {
      this.vis=true
    },
    removeCollaborator(collab) {
      this.$confirm('此操作将删除协作者，是否继续？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        axios.post("/right/deleteUser/"+collab.id+"/"+this.setting.houseId).then((res)=>{
          if(res.data.status==200) {
            ElMessage.success("成功");
            location.reload()
          }else {
            ElMessage.error(res.data.message)
          }
        })
      });

    },
    deleteRepository() {
      this.$confirm('此操作将永久删除仓库，是否继续？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        const params = new URLSearchParams(window.location.search);
        var id = params.get('id'); // 假设你要获取名为 'param1' 的参数

          axios.post("/house/deleteRep/"+id).then((res)=>{
                if(res.data.status==200){
                  ElMessage.success("删除成功")
                  location.href="/"
                }else {
                  ElMessage.error(res.data.message)
                }

          })
      });
    }
  }
}
</script>

<style scoped>
.settings-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.settings-menu {
  min-height: 500px;
}

.settings-card {
  margin-bottom: 20px;
}

.collaborators {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.add-collaborator {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.danger-zone {
  border: 1px solid #f56c6c;
}

.danger-title {
  color: #f56c6c;
}

.danger-item {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.danger-item:last-child {
  border-bottom: none;
}

.danger-item h4 {
  color: #f56c6c;
  margin-bottom: 10px;
}

.danger-item p {
  color: #666;
  margin-bottom: 15px;
}
</style>
