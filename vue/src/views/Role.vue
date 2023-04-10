<template>
  <div>

    <div style="margin: 10px 0">
      <el-input style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search" v-model="name"></el-input>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button type="primary" @click="handleAdd">新增 <i class="el-icon-circle-plus-outline"></i></el-button>
      <el-popconfirm
          class="ml-5"
          confirm-button-text='确定'
          cancel-button-text='我再想想'
          icon="el-icon-info"
          icon-color="red"
          title="您确定批量删除这些数据吗？"
          @confirm="delBatch"
      >
        <el-button type="danger" slot="reference">批量删除 <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>
    </div>

    <el-table :data="tableData" border stripe :header-cell-class-name=" 'headerBg' " @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="140"></el-table-column>
      <el-table-column prop="name" label="名称" ></el-table-column>
      <el-table-column prop="flag" label="唯一标识" ></el-table-column>
      <el-table-column prop="description" label="描述" ></el-table-column>
      <el-table-column label="操作"  width="300" align="center">

        <template slot-scope="scope">
          <el-button type="info" @click="selectMenu(scope.row)">分配菜单 <i class="el-icon-menu"></i></el-button>
          <el-button type="success" @click="handleEdit(scope.row)">编辑 <i class="el-icon-edit"></i></el-button>
          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='我再想想'
              icon="el-icon-info"
              icon-color="red"
              title="您确定删除吗？"
              @confirm="del(scope.row.id)"
          >
            <el-button type="danger" slot="reference">删除 <i class="el-icon-remove-outline"></i></el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div style="padding: 10px 0">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <el-dialog title="角色信息" :visible.sync="dialogFormVisible" width="35%" >
      <el-form label-width="80px" size="small">
        <el-form-item label="名称">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="唯一标识">
          <el-input v-model="form.flag" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="菜单分配" :visible.sync="menuDialogVis" width="30%">
      <el-tree
          :props="props"
          :data="menuData"
          show-checkbox
          node-key="id"
          ref="tree"
          :default-expanded-keys="expends"
          :default-checked-keys="checks">
         <span class="custom-tree-node" slot-scope="{ node, data }">
            <span><i :class="data.icon"></i> {{ data.name }}</span>
         </span>
      </el-tree>
      <div slot="footer" class="dialog-footer">
        <el-button @click="menuDialogVis = false">取 消</el-button>
        <el-button type="primary" @click="saveRoleMenu">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import {resetRouter} from "@/router";

export default {
  name: "Role",
  data(){
    return{
      tableData: [],   //表单数据
      total: 0,   //总页数
      pageNum: 1,
      pageSize: 5,
      name:"",
      form:{},
      dialogFormVisible:false,  //新增弹出框
      menuDialogVis:false, //分配菜单弹出框
      multipleSelection: [], //多选框
      /*菜单分配相关*/
      menuData:[],
      props: {
        label: 'name',  //分配菜单时显示对应的菜单名称
      },
      expends: [],
      checks: [],
      roleId: 0,
      roleFlag: ''
    }
  },
  created() {
    //请求分页查询数据
    this.load()
  },
  methods:{
    load() {  //分页查询
      this.request.get("/role/page",{
        params:{
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        //console.log(res)
        this.tableData=res.data.records
        this.total=res.data.total
      })
    },
    handleAdd(){  //打开新增窗口
      this.dialogFormVisible=true
      this.form={}
    },
    save(){  //保存新增用户信息
      this.request.post("/role",this.form).then(res => {
        if(res.code === '200'){
          this.$message.success("保存成功")
          this.dialogFormVisible=false
          this.load()
        }else{
          this.$message.error("保存失败")
        }
      })
    },
    saveRoleMenu(){  //保存角色和菜单的关系
      this.request.post("/role/roleMenu/" + this.roleId, this.$refs.tree.getCheckedKeys()).then(res=>{
        if(res.code==='200'){
          this.$message.success("绑定成功")
          this.menuDialogVis = false

          //操作管理员角色后需要重新登录
          if(this.roleFlag==='ROLE_ADMIN'){
            this.$store.commit("logout")
          }
        }else {
          this.$message.error(res.msg)
        }
      })
    },
    handleEdit(row){  //编辑用户信息
      this.form=row
      this.dialogFormVisible=true
    },
    del(id){  //删除
      this.request.delete("/role/"+id).then(res=>{
        if(res.code === '200'){
          this.$message.success("删除成功")
          this.load()
        }else{
          this.$message.error("删除失败")
        }
      })
    },
    handleSelectionChange(val){  //批量选择
      //console.log(val)  对象形式：[{}, {}, {}]
      this.multipleSelection=val
    },
    delBatch(){  //批量删除
      let ids=this.multipleSelection.map(v=>v.id) // [{}, {}, {}] => [1,2,3]
      this.request.post("/role/del/batch",ids).then(res=>{
        if(res.code === '200'){
          this.$message.success("批量删除成功")
          this.load()
        }else{
          this.$message.error("批量删除失败")
        }
      })
    },
    reset(){  //重置，刷新搜索框
      this.name=""
      this.load()
    },
    handleSizeChange(pageSize){
      this.pageSize=pageSize
      this.load()
    },
    handleCurrentChange(pageNum){
      this.pageNum=pageNum
      this.load()
    },
    selectMenu(role){  //分配菜单
      this.roleId=role.id
      this.roleFlag=role.flag
      //请求菜单数据
      this.request.get("/menu").then(res => {
        this.menuData=res.data

        // 把后台返回的菜单数据处理成 id数组
        this.expends=this.menuData.map(v=>v.id);
      })

      this.request.get("/role/roleMenu/" + this.roleId).then(res=>{
          this.checks=res.data

          this.request.get("/menu/ids").then(r=>{
            const ids=r.data
            ids.forEach(id=>{
              if(!this.checks.includes(id)){
                this.$nextTick(()=>{
                  // 可能会报错：Uncaught (in promise) TypeError: Cannot read properties of undefined (reading 'setChecked')
                  this.$refs.tree.setChecked(id,false)
                })
              }
            })
          })
          this.menuDialogVis = true
      })
    }
  }
}
</script>

<style>
.headerBg {
  background: #eee!important;
}
</style>
