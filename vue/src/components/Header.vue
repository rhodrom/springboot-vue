<template>
  <div style="line-height: 60px; display: flex">
    <div style="flex: 1">
      <span :class="collapseBtnClass" style="cursor: pointer;font-size: 18px" @click="asideCollapse"></span>
      <el-breadcrumb separator="/" style="display: inline-block;margin-left: 10px" >
        <el-breadcrumb-item :to="'/'">首页</el-breadcrumb-item>
        <el-breadcrumb-item>{{ currentPathName }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <el-dropdown style="width: 100px; cursor: pointer">

      <div style="display: inline-block" class="mr-5">
        <img :src="user.avatarUrl" alt="" style="width: 30px; border-radius: 50%; position: relative; top: 10px; right: 5px">
        <span >{{ user.nickname }}</span><i class="el-icon-arrow-down" style="margin-left: 5px"></i>
      </div>

      <el-dropdown-menu slot="dropdown" style="width: 100px;text-align: center">
        <el-dropdown-item>
          <span style="text-decoration: none" @click="toPerson">个人信息</span>
        </el-dropdown-item>
        <el-dropdown-item>
          <span style="text-decoration: none" @click="toPassword">修改密码</span>
        </el-dropdown-item>
        <el-dropdown-item>
          <span style="text-decoration: none" @click="logout">退出</span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>
export default {
  name: "Header",
  props: {
    collapseBtnClass: String,
    //collapse: Boolean
    user: Object
  },
  computed: {
    currentPathName () {
      return this.$store.state.currentPathName;　　//需要监听的数据
    }
  },
  data(){
    return {
    }
  },
  methods:{
    asideCollapse(){
      this.$emit("asideCollapse")  //调用Manage中的collapse方法
    },
    logout(){
      this.$store.commit("logout")
      this.$message.success("退出成功")
    },
    toPerson(){
      this.$router.push("/person")
    },
    toPassword(){
      this.$router.push("/password")
    }

  }
}
</script>

<style scoped>

</style>