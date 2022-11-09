<template>
  <div id="addUser" v-loading="isLoging">
    <el-dialog title="添加用户" width="40%" top="2rem" :close-on-click-modal="false" :visible.sync="showDialog"
      :destroy-on-close="true" @close="close()">
      <div id="shared" style="margin-right: 20px;">
        <el-form ref="passwordForm" :model="form" :rules="rules" status-icon label-width="80px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username"></el-input>
          </el-form-item>
          <el-form-item label="用户角色" prop="roleId">
            <el-select v-model="form.roleId" placeholder="请选择" style="width: 100%">
              <el-option v-for="item in options" :key="item.id" :label="item.name" :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password"></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="form.confirmPassword"></el-input>
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
  name: "addUser",
  props: {},
  computed: {},
  created() {
    this.getAllRole();
  },
  data() {
    let validatePass1 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入新密码'));
      } else {
        if (this.form.confirmPassword !== '') {
          this.$refs.passwordForm.validateField('confirmPassword');
        }
        callback();
      }
    };
    let validatePass2 = (rule, value, callback) => {
      if (this.form.confirmPassword === '') {
        callback(new Error('请再次输入密码'));
      } else if (this.form.confirmPassword !== this.form.password) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      value: "",
      options: [],
      loading: false,
      form: {
        username: '',
        roleId: null,
        password: '',
        confirmPassword: '',
      },
      listChangeCallback: null,
      showDialog: false,
      isLoging: false,
      rules: {
        username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
        roleId: [{ required: true, message: "请选择用户角色", trigger: "blur" }],
        password: [{ required: true, validator: validatePass1, trigger: "blur" }, {
          pattern: /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[~!@#$%^&*()_+`\-={}:";'<>?,.\/]).{8,20}$/,
          message: "密码长度在8-20位之间,由字母+数字+特殊字符组成",
        },],
        confirmPassword: [{ required: true, validator: validatePass2, trigger: "blur" }],
      },
    };
  },
  methods: {
    openDialog(callback) {
      this.listChangeCallback = callback;
      this.showDialog = true;
    },
    onSubmit() {
      this.$refs.passwordForm.validate((valid) => {
        if (valid) {
          this.$axios({
            method: 'post',
            url: "/api/user/add",
            params: this.form
          }).then((res) => {
            if (res.data.code === 0) {
              this.$message({ showClose: true, message: '添加成功', type: 'success', });
              this.showDialog = false;
              this.listChangeCallback()
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
    close() {
      this.showDialog = false;
      this.form = {
        username: '',
        roleId: null,
        password: '',
        confirmPassword: '',
      }
    },
    getAllRole() {
      this.$axios({
        method: 'get',
        url: "/api/role/all"
      }).then((res) => {
        this.loading = true;
        if (res.data.code === 0) {
          this.options = res.data.data
        }
      }).catch((error) => {
        console.error(error)
      });
    }
  },
};
</script>
