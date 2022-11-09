<template>

  <div id="app" style="width: 100%">
    <div class="page-header">

      <div class="page-title">菜单列表</div>
      <div class="page-header-btn">
        <el-button icon="el-icon-plus" size="mini" style="margin-right: 1rem;" type="primary" @click="addMenu">
          添加菜单
        </el-button>

      </div>
    </div>
    <!--菜单列表-->
    <el-table :data="menuList" style="width: 100%;font-size: 12px;" :height="winHeight"
      header-row-class-name="table-header" row-key="id" :tree-props="{ children: 'child' }">
      <el-table-column prop="id" label="ID" min-width="160" />
      <el-table-column prop="name" label="菜单名称" min-width="160" />
      <el-table-column prop="url" label="路由" min-width="160" />
      <el-table-column prop="sort" label="排序" min-width="160" />
      <el-table-column label="操作" min-width="450" fixed="right">
        <template slot-scope="scope">
          <el-button size="medium" icon="el-icon-edit" type="text" @click="edit(scope.row)">修改</el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button size="medium" icon="el-icon-delete" type="text" @click="deleteMenu(scope.row)"
            style="color: #f56c6c">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <addMenu ref="addMenu"></addMenu>
  </div>
</template>

<script>
import addMenu from './dialog/addMenu.vue'

export default {
  name: 'menuManager',
  components: {
    addMenu
  },
  data() {
    return {
      menuList: [],
      winHeight: window.innerHeight - 200,
      getMenuListLoading: false
    };
  },
  mounted() {
    this.getMenuList();
  },
  methods: {
    getMenuList() {
      let that = this;
      this.getMenuListLoading = true;
      this.$axios({
        method: 'get',
        url: `/api/menu/listAllMenus`
      }).then(function (res) {
        if (res.data.code == 0) {
          that.menuList = res.data.data;
        }
        that.getMenuListLoading = false;
      }).catch(function (error) {
        that.getMenuListLoading = false;
      });

    },
    addMenu() {
      // this.$refs.addMenu.openDialog()
      this.$refs.addMenu.openAddDialog(() => {
        this.$refs.addMenu.close();
        this.$message({
          showClose: true,
          message: "菜单添加成功",
          type: "success",
        });
        setTimeout(this.getMenuList, 200)
      })
    },

    edit(row) {
      this.$refs.addMenu.openEditDialog(row, () => {
        this.$refs.addMenu.close();
        this.$message({
          showClose: true,
          message: "菜单修改成功",
          type: "success",
        });
        setTimeout(this.getMenuList, 200)
      })
    },

    deleteMenu(row) {
      let msg = "确定删除此菜单？"
      if (row.online !== 0) {
        msg = "<strong>确定删除此菜单？</strong>"
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
          url: `/api/menu/delete?menuId=${row.id}`
        }).then((res) => {
          this.getMenuList();
        }).catch((error) => {
          console.error(error);
        });
      }).catch(() => {
      });
    }
  }
}
</script>
<style>

</style>
