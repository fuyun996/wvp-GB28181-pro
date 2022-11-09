<template>

  <div id="app" style="width: 100%">
    <div class="page-header">

      <div class="page-title">角色列表</div>
      <div class="page-header-btn">
        <el-button icon="el-icon-plus" size="mini" style="margin-right: 1rem;" type="primary"
          @click="curRoleId = null; showDialog = true">
          添加角色
        </el-button>

      </div>
    </div>
    <!--角色列表-->
    <el-table :data="roleList" style="width: 100%;font-size: 12px;" :height="winHeight"
      header-row-class-name="table-header">
      <el-table-column prop="name" label="角色名" min-width="160" />
      <el-table-column prop="authority" label="权限" min-width="160" />
      <el-table-column label="操作" min-width="450" fixed="right">
        <template slot-scope="scope" v-if="scope.row.id != 1">
          <el-button size="medium" icon="el-icon-edit" type="text" @click="editAuthority(scope.row)">设置菜单权限</el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button size="medium" icon="el-icon-edit" type="text" @click="editChannelAuthority(scope.row)">设置通道权限
          </el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button size="medium" icon="el-icon-edit" type="text" @click="openRoleDialog(scope.row)">修改</el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button size="medium" icon="el-icon-delete" type="text" @click="deleteRole(scope.row)"
            style="color: #f56c6c">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 修改角色 -->
    <el-dialog :title="curRoleId ? '修改角色' : '添加角色'" width="40%" top="2rem" :close-on-click-modal="false"
      :visible.sync="showDialog" :destroy-on-close="true">
      <div id="shared" style="margin-right: 20px;">
        <el-form ref="roleForm" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="角色名" prop="name">
            <el-input v-model="form.name"></el-input>
          </el-form-item>
          <el-form-item>
            <div style="float: right;">
              <el-button type="primary" @click="onSubmit">保存</el-button>
              <el-button @click="showDialog = false">取消</el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>

    <addRolePower ref="addRolePower"></addRolePower>
  </div>
</template>

<script>
import addRolePower from './dialog/addRolePower.vue'

export default {
  name: 'userManager',
  components: {
    addRolePower
  },
  data() {
    return {
      roleList: [],
      winHeight: window.innerHeight - 200,
      getRoleListLoading: false,
      showDialog: false,
      curRoleId: null,
      form: {
        name: ''
      },
      rules: {
        name: [{ required: true, message: "请输入角色名", trigger: "blur" }],
      }
    };
  },
  mounted() {
    this.getRoleList();
  },
  methods: {
    getRoleList: function () {
      let that = this;
      this.getRoleListLoading = true;
      this.$axios({
        method: 'get',
        url: `/api/role/all`
      }).then(function (res) {
        if (res.data.code == 0) {
          that.roleList = res.data.data;
        }
        that.getRoleListLoading = false;
      }).catch(function (error) {
        that.getRoleListLoading = false;
      });

    },
    onSubmit() {
      this.$refs.roleForm.validate((valid) => {
        if (valid) {
          this.$axios({
            method: 'post',
            url: this.curRoleId ? '/api/role/update' : '/api/role/add',
            params: this.curRoleId ? { id: this.curRoleId, name: this.form.name } : { name: this.form.name }
          }).then((res) => {
            if (res.data.code === 0) {
              this.getRoleList()
              this.$message({ showClose: true, message: this.curRoleId ? '修改成功' : '添加成功', type: 'success', });
              this.showDialog = false;
            } else {
              this.$message({ showClose: true, message: res.data.msg, type: 'error' });
            }
          }).catch((error) => {
            console.error(error)
          });
        } else {
          console.log('error submit!!');
          return false;
        }
      });

    },
    openRoleDialog(row) {
      this.curRoleId = row.id
      this.form.name = row.name
      this.showDialog = true
    },
    edit: function (row) {
      this.$refs.changePasswordForAdmin.openDialog(row, () => {
        this.$refs.changePasswordForAdmin.close();
        this.$message({
          showClose: true,
          message: "密码修改成功",
          type: "success",
        });
        setTimeout(this.getRoleList, 200)

      })
    },
    deleteRole: function (row) {
      let msg = "确定删除此角色？"
      if (row.online !== 0) {
        msg = "<strong>确定删除此角色？</strong>"
      }
      this.$confirm(msg, '提示', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        center: true,
        type: 'warning'
      }).then(() => {
        this.$axios({
          method: 'delete',
          url: `/api/role/delete?id=${row.id}`
        }).then((res) => {
          this.getRoleList();
        }).catch((error) => {
          console.error(error);
        });
      }).catch(() => {
      });
    },

    editAuthority: function (row) {
      this.$refs.addRolePower.openDialog(row, () => {
        this.$refs.addRolePower.close();
        this.$message({
          showClose: true,
          message: "修改成功",
          type: "success",
        });
        setTimeout(this.getRoleList, 200)
      })
    },
    editChannelAuthority: function (row) {
      this.$refs.addRolePower.openChannelDialog(row, () => {
        this.$refs.addRolePower.close();
        this.$message({
          showClose: true,
          message: "修改成功",
          type: "success",
        });
        setTimeout(this.getRoleList, 200)
      })
    },

    addRole: function () {
      // this.$refs.addRole.openDialog()
      this.$refs.addRole.openDialog(() => {
        this.$refs.addRole.close();
        this.$message({
          showClose: true,
          message: "角色添加成功",
          type: "success",
        });
        setTimeout(this.getRoleList, 200)
      })
    }
  }
}
</script>
<style>

</style>
