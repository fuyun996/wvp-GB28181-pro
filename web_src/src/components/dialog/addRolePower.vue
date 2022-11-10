<template>
  <div v-loading="isLoging">
    <el-dialog title="添加角色菜单权限" width="40%" top="2rem" :close-on-click-modal="false" :visible.sync="showDialog"
      :destroy-on-close="true" @close="close()">
      <div id="shared" style="margin-right: 20px;">
        <el-form status-icon label-width="80px">
          <el-form-item prop="name">
            <el-tree :data="menuTreeData" show-checkbox node-key="id" :props="defaultProps"
              :default-checked-keys="menuIds" @check="handelCheck">
            </el-tree>
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

    <el-dialog v-if="showChannelDialog" title="添加角色通道权限" width="40%" top="2rem" :close-on-click-modal="false"
      :visible.sync="showChannelDialog" :destroy-on-close="true" @close="close()">
      <div id="shared" style="margin-right: 20px;">
        <el-form>
          <el-form-item>
            <el-tree :data="channelTreeData" show-checkbox node-key="channelId" :props="channelDefaultProps"
              :default-checked-keys="channelIds" @check="handelChannelCheck">
            </el-tree>
          </el-form-item>

          <el-form-item>
            <div style="float: right;">
              <el-button type="primary" @click="onChannelSubmit">保存</el-button>
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
  name: "addRolePower",
  props: {},
  computed: {},
  created() {
    this.getAllMenu();
  },
  data() {
    return {
      isLoging: false,

      roleId: null,

      showDialog: false,
      menuTreeData: [],
      menuIds: [],
      defaultProps: {
        children: 'child',
        label: 'name'
      },

      showChannelDialog: false,
      channelTreeData: [],
      channelIds: [],
      channelDefaultProps: {
        children: 'child',
        label: 'name'
      }

    };
  },
  methods: {
    openDialog(data, callback) {
      this.getMyMenu(data.id)
      this.roleId = data.id
      this.listChangeCallback = callback
      this.showDialog = true
      this.showChannelDialog = false
    },
    openChannelDialog(data, callback) {
      this.channelTreeData = []
      this.channelIds = []
      this.getAllChannel()
      this.roleId = data.id
      this.listChangeCallback = callback
      this.showDialog = false
      this.showChannelDialog = true
    },
    handelCheck(checkedNodes, checked) {
      this.menuIds = [...checked.checkedKeys, ...checked.halfCheckedKeys]
      console.log(this.menuIds)
    },
    onSubmit() {
      this.$axios.post("/api/role/setRoleMenuAuthority", {
        roleId: this.roleId,
        menuIds: this.menuIds
      }).then((res) => {
        if (res.data.code === 0) {
          this.$message({ showClose: true, message: '添加成功', type: 'success', });
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
    },
    close() {
      this.showDialog = false;
      this.menuIds = []
      this.showChannelDialog = false
    },
    /*
    * 菜单 
    */
    getAllMenu() {
      this.$axios({
        method: 'get',
        url: "/api/menu/listAllMenus"
      }).then((res) => {
        this.loading = true;
        if (res.data.code === 0) {
          this.menuTreeData = [...res.data.data]
        }
      }).catch((error) => {
        console.error(error)
      });
    },
    getMyMenu(roleId) {
      this.$axios({
        method: 'get',
        url: "/api/role/getMenuByRole",
        params: { roleId }
      }).then((res) => {
        this.loading = true;
        if (res.data.code === 0) {
          this.menuIds = []
          this.getAllLeaf(res.data.data)
        }
      }).catch((error) => {
        console.error(error)
      });
    },

    /*
     * 通道 
     */
    getAllChannel() {
      this.$axios({
        method: 'get',
        url: `/api/device/query/devices`,
        params: { page: 1, count: 10000 }
      }).then(result => {
        if (result.data.code === 0) {
          this.channelTreeData = result.data.data.list

          result.data.data.list.map((item, inx) => {
            this.$axios({
              method: 'get',
              url: `/api/device/query/devices/${item.deviceId}/channels`,
              params: { page: 1, count: 10000 }
            }).then(res => {
              if (res.data.code === 0 && res.data.data.list && res.data.data.list.length > 0) {
                this.$set(this.channelTreeData[inx], 'child', res.data.data.list)
              }
              if (inx == (this.channelTreeData.length - 1)) {
                this.getMyChannel(this.roleId)
              }
            }).catch(error => {
              console.log(error);
            });
          })
        }
      }).catch(error => {
        console.log(error);
      })
    },
    getMyChannel(roleId) {
      this.$axios({
        method: 'get',
        url: "/api/role/getChannelByRole",
        params: { roleId }
      }).then((res) => {
        this.loading = true;
        if (res.data.code === 0) {
          this.channelIds = []
          res.data.data.map(item => {
            this.channelIds.push(item.channelId)
          })
          console.log('my', this.channelIds)
        }
      }).catch((error) => {
        console.error(error)
      });
    },
    handelChannelCheck(checkedNodes, checked) {
      console.log(checked.checkedKeys)
      this.channelIds = checked.checkedKeys
      console.log(this.channelIds)
    },
    onChannelSubmit() {
      let channelIds = this.channelIds.filter((item, index) => {
        return this.channelIds.indexOf(item) === index;
      });
      channelIds = channelIds.filter(o => o != undefined && o != null)
      this.$axios.post("/api/role/setRoleChannelAuthority", {
        roleId: this.roleId,
        channelIds: channelIds
      }).then((res) => {
        if (res.data.code === 0) {
          this.$message({ showClose: true, message: '添加成功', type: 'success', });
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
    },

    getAllLeaf(tree) {
      tree.map(item => {
        if (item.child && item.child.length > 0) {
          this.getAllLeaf(item.child)
        } else {
          this.menuIds.push(item.id)
          if (item.pid) {
            this.menuIds.push(item.pid)
          }
        }
      })
    },
    treeToArray(tree) {
      const obj = []
      tree.forEach((item) => {
        if (item.children) {
          obj.push(item, ...item.children)
          Reflect.deleteProperty(item, 'children')
        } else {
          obj.push(item)
        }
      })
      return obj
    },
  },
};
</script>
