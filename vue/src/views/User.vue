<template>
  <div>

    <div style="margin: 10px 0">
      <el-input style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search" v-model="username"></el-input>
      <el-input style="width: 200px" placeholder="请输入邮箱" suffix-icon="el-icon-message" class="ml-5" v-model="email"></el-input>
      <el-input style="width: 200px" placeholder="请输入地址" suffix-icon="el-icon-position" class="ml-5" v-model="address"></el-input>
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
      <el-upload action="http://localhost:8081/user/import" :show-file-list="false" accept="xlsx" :on-success="handleExcelImportSuccess" style="display: inline-block">
        <el-button type="primary" class="ml-5">导入 <i class="el-icon-bottom"></i></el-button>
      </el-upload>
      <el-button type="primary" @click="exp" class="ml-5">导出 <i class="el-icon-top"></i></el-button>
    </div>

    <el-table :data="tableData" border stripe :header-cell-class-name=" 'headerBg' " @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="username" label="用户名" width="80"></el-table-column>
      <el-table-column prop="role" label="角色">
        <template slot-scope="scope">
          <el-tag type="primary" v-if="scope.row.role === 'ROLE_ADMIN'">管理员</el-tag>
          <el-tag type="warning" v-if="scope.row.role === 'ROLE_TEACHER'">老师</el-tag>
          <el-tag type="success" v-if="scope.row.role === 'ROLE_STUDENT'">学生</el-tag>
          <el-tag type="success" v-if="scope.row.role === 'ROLE_USER'">游客</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="nickname" label="昵称" width="120"></el-table-column>
      <el-table-column prop="email" label="邮箱"></el-table-column>
      <el-table-column prop="phone" label="电话"></el-table-column>
      <el-table-column prop="address" label="地址"></el-table-column>
      <el-table-column label="操作"  width="300" align="center">

        <template slot-scope="scope">
          <el-button type="primary" @click="lookTeaCourse(scope.row.teaCourse)" v-if="scope.row.role === 'ROLE_TEACHER'">查看教授课程 <i class="el-icon-document"></i></el-button>
          <el-button type="warning" @click="lookStuCourse(scope.row.stuCourse)" v-if="scope.row.role === 'ROLE_STUDENT'">查看已选课程 <i class="el-icon-document"></i></el-button>

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

    <el-dialog title="用户信息" :visible.sync="dialogFormVisible" width="35%" >
      <el-form label-width="80px" size="small">
        <el-form-item label="用户名">
          <el-input v-model="form.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select clearable v-model="form.role" placeholder="请选择角色" style="width: 100%">
            <el-option v-for="item in roles" :key="item.name" :label="item.name" :value="item.flag"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="课程信息" :visible.sync="vis" width="30%" >
      <el-table :data="teaCourse" border stripe>
        <el-table-column prop="name" label="课程名称"></el-table-column>
        <el-table-column prop="score" label="学分"></el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog title="课程信息" :visible.sync="stuVis" width="30%" >
      <el-table :data="stuCourse" border stripe>
        <el-table-column prop="name" label="课程名称"></el-table-column>
        <el-table-column prop="score" label="学分"></el-table-column>
      </el-table>
    </el-dialog>

  </div>
</template>

<script>
export default {
  name: "User",
  data(){
    return{
      tableData: [],   //表单数据
      total: 0,   //总页数
      pageNum: 1,
      pageSize: 5,
      username:"",
      email:"",
      address:"",
      form:{},
      dialogFormVisible:false,  //新增弹出框
      multipleSelection: [], //多选框
      roles:[],
      teaCourse: [],
      vis: false,
      stuCourse: [],
      stuVis: false
    }
  },
  created() {
    //请求分页查询数据
    this.load()
  },
  methods:{
    load() {  //分页查询
      this.request.get("/user/page",{
        params:{
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          username: this.username,
          email: this.email,
          address: this.address
        }
      }).then(res => {
        //console.log(res)
        this.tableData=res.data.records
        this.total=res.data.total
      })
      //查询所有角色信息
      this.request.get("/role").then(res=>{
        this.roles=res.data
      })
    },
    handleAdd(){  //打开新增窗口
      this.dialogFormVisible=true
      this.form={}
    },
    save(){  //保存新增用户信息
      this.request.post("/user",this.form).then(res => {
        if(res.code === '200'){
          this.$message.success("保存成功")
          this.dialogFormVisible=false
          this.load()
        }else{
          this.$message.error("保存失败")
        }
      })
    },
    handleEdit(row){  //编辑用户信息
      this.form=row
      this.dialogFormVisible=true
    },
    del(id){  //删除
      this.request.delete("/user/"+id).then(res=>{
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
      this.request.post("/user/del/batch",ids).then(res=>{
        if(res.code === '200'){
          this.$message.success("批量删除成功")
          this.load()
        }else{
          this.$message.error("批量删除失败")
        }
      })
    },
    reset(){  //重置，刷新搜索框
      this.username=""
      this.email=""
      this.address=""
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
    exp(){   //导出文件
      window.open("http://localhost:8081/user/export")
    },
    handleExcelImportSuccess(){  //导入成功
      this.$message.success("导入文件成功")
      this.load()
    },
    lookTeaCourse(teaCourse) {
      this.teaCourse = teaCourse
      this.vis = true
    },
    lookStuCourse(stuCourse) {
      this.stuCourse = stuCourse
      this.stuVis = true
    },
  }
}
</script>

<style>
.headerBg {
  background: #eee!important;
}
</style>