<template>
  <div id="addMenu" v-loading="isLoging">
    <el-dialog title="添加菜单" width="40%" top="2rem" :close-on-click-modal="false" :visible.sync="showDialog"
      :destroy-on-close="true" @close="close()">
      <div id="shared" style="margin-right: 20px;">
        <el-form ref="menuForm" :model="form" :rules="rules" status-icon label-width="80px">
          <el-form-item label="父节点" prop="name">
            <el-cascader v-model="form.pid" :options="menuTreeData"
              :props="{ checkStrictly: true, label: 'name', value: 'id', children: 'child', disabled: 'disabled', emitPath: false }"
              :show-all-levels="false" filterable style="width: 100%;" />
          </el-form-item>
          <el-form-item label="名称" prop="name">
            <el-input v-model="form.name" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="路由" prop="url">
            <el-input v-model="form.url" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="排序" prop="sort">
            <el-input v-model="form.sort" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item>
            <div style="float: right;">
              <el-button type="primary" @click="onSubmit">保存</el-button>
              <el-button @click="close">取消</el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script>

export default {
  name: "addMenu",
  props: {},
  computed: {},
  data() {

    return {
      showDialog: false,
      isLoging: false,
      isEdit: false,
      form: {
        pid: 0,
        name: '',
        url: '',
        sort: 0,
      },
      menuTreeData: [{
        id: 0,
        name: '根目录'
      }],
      rules: {
        pid: [{ required: true, message: "请选择父节点", trigger: "blur" }],
        name: [{ required: true, message: "请输入菜单名称", trigger: "blur" }],
        url: [{ required: true, message: "请输入菜单路由", trigger: "blur" }],
        sort: [{ required: true, message: "请输入菜单排序", trigger: "blur" }],
      },
    };
  },
  methods: {
    openAddDialog(callback) {
      this.getAllMenu();
      this.isEdit = false
      this.listChangeCallback = callback;
      this.showDialog = true;
    },
    openEditDialog(data, callback) {
      this.getAllMenu();
      this.isEdit = true
      this.form = data
      this.listChangeCallback = callback;
      this.showDialog = true;
    },
    onSubmit() {
      this.$refs.menuForm.validate((valid) => {
        if (valid) {
          this.$axios({
            method: 'post',
            url: this.isEdit ? '/api/menu/update' : '/api/menu/add',
            data: this.form
          }).then((res) => {
            if (res.data.code === 0) {
              this.$message({
                showClose: true,
                message: this.isEdit ? '修改成功' : '添加成功',
                type: 'success',
              });
              this.showDialog = false;
              this.listChangeCallback()
            } else {
              this.$message({
                showClose: true,
                message: res.data.msg,
                type: 'error'
              });
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
    close() {
      this.showDialog = false;
      this.form = {
        pid: 0,
        name: '',
        url: '',
        sort: 0,
      }
    },
    getAllMenu() {
      this.$axios({
        method: 'get',
        url: "/api/menu/listAllMenus"
      }).then((res) => {
        this.loading = true;
        if (res.data.code === 0) {
          this.menuTreeData = [...this.menuTreeData, ...res.data.data]
        }
      }).catch((error) => {
        console.error(error)
      });
    }
  },
};
</script>
