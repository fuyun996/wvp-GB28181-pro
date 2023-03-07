<template>
  <div id="groupTree" class="groupTree">
    <el-tree class="el-scrollbar" ref="tree" id="catalogTree" empty-text="未知节点" node-key="id"
      :highlight-current="false" :expand-on-click-node="false" :props="props" :load="loadNode"
      @node-contextmenu="contextmenuEventHandler" @node-click="nodeClickHandler" lazy>
      <span class="custom-tree-node" slot-scope="{ node, data }" style="width: 100%">
        <span v-if="node.level === 1" class="el-icon-s-home"></span>
        <span v-else-if="node.data.channelId" class="iconfont icon-shexiangtou"></span>
        <span v-else class="iconfont icon-ziyuan"></span>
        <!-- <span v-else-if="node.data.type === 2" class="iconfont icon-zhibo"></span> -->
        <span style="padding-left: 1px">{{ node.label }}</span>
        <span>
          <i style="margin-left: 5rem; color: #9d9d9d; padding-right: 20px"
            v-if="node.data.id === defaultCatalogIdSign">默认</i>
        </span>
      </span>
    </el-tree>
    <groupEdit ref="catalogEdit"></groupEdit>

    <SelectChannelTable ref="SelectChannelTable" :type="2" :title="'选择通道(' + chooseName + ')'" @submit="addChannels">
    </SelectChannelTable>
</div>
</template>

<script>
import groupEdit from './groupEdit.vue'
import SelectChannelTable from './../../common/SelectChannelTable.vue'

export default {
  name: 'groupTree',
  components: { groupEdit, SelectChannelTable },
  created() {
    this.initData();
  },
  props: ['clickEvent'],
  data() {
    return {
      props: {
        label: 'name',
        children: 'children',
        isLeaf: 'leaf'
      },
      defaultCatalogIdSign: null,
      chooseNode: null,
      chooseId: "",
      chooseName: "",
      catalogTree: null,
      contextmenuShow: false
    };
  },
  methods: {
    initData: function () {
      this.getCatalog();
    },

    getCatalog: function (parentId, callback) {
      this.$axios({
        method: "get",
        url: `/api/device/query/channelCatalog/getChannelCatalogByUserId/`,
        params: { parentId: parentId }
      }).then((res) => {
        if (res.data.code === 0) {
          if (typeof (callback) === 'function') { callback(res.data.data) }
        }
      }).catch(function (error) { console.log(error); });
      this.getChannelCatalog
    },

    addCatalog: function (otype, parentId, node) {
      console.log(node)
      // 打开添加弹窗
      this.$refs.catalogEdit.openDialog(otype, { parentId: parentId, parentName: node.data.name, level: node.level }, () => {
        node.loaded = false;
        node.expand();
      });

    },
    addChannels: function (data) {
      const channelIds = data.map(o => { return o.channelId })
      this.$axios({
        method: "post",
        url: `/api/device/query/channelCatalog/chooseChannelCatalog/`,
        params: {
          parentId: this.chooseId,
          channelIds: channelIds.toString()
        }
      }).then((res) => {
        if (res.data.code === 0) {
          this.chooseNode.parent.loaded = false;
          this.chooseNode.parent.expand();
          this.$refs.SelectChannelTable.closeDialog()
        } else {
          this.$message({ showClose: true, message: res.data.msg, type: "error", });
        }
      }).catch((error) => { console.log(error); });
    },
    refreshCatalog: function (node) {
      node.loaded = false
      node.expand();
    },
    refreshCatalogById: function (id) {
      if (id) {
        let node = this.$refs.tree.getNode(id);
        this.refreshCatalog(node);
      }
    },
    editCatalog: function (otype, data, node) {
      // 打开添加弹窗
      this.$refs.catalogEdit.openDialog(otype, data, (newData) => {
        node.parent.loaded = false;
        node.parent.expand();
      });

    },
    removeGroup: function (id, node) {
      this.$axios({
        method: "delete",
        url: `/api/device/query/channelCatalog/deleteChannelCatalogById/`,
        params: {
          id: id
        }
      }).then((res) => {
        if (res.data.code === 0) {
          console.log("移除成功")
          node.parent.loaded = false
          node.parent.expand();
          if (res.data.data) {
            this.defaultCatalogIdSign = res.data.data;
          }
        }
      })
        .catch(function (error) {
          console.log(error);
        });
    },
    setDefaultCatalog: function (id) {
      this.$axios({
        method: "post",
        url: `/api/platform/catalog/default/update`,
        params: {
          catalogId: id
        }
      }).then((res) => {
        if (res.data.code === 0) {
          this.defaultCatalogIdSign = id;
        }
      })
        .catch(function (error) {
          console.log(error);
        });
    },
    loadNode: function (node, resolve) {
      if (node.level === 0) {
        resolve([{ name: "本域", id: '', type: 0 }
        ]);
      }
      if (node.level >= 1) {
        this.getCatalog(node.data.catalogId || '', resolve)
      }
    },
    contextmenuEventHandler: function (event, data, node, element) {
      console.log('node', node)
      if (node.data.channelId) {
        data.parentId = node.parent.data.id;
        this.$contextmenu({
          items: [
            {
              label: "移除通道",
              icon: "el-icon-delete",
              disabled: false,
              onClick: () => {
                this.removeGroup(data.id, node)
              }
            }
          ],
          event, // 鼠标事件信息
          customClass: "custom-class", // 自定义菜单 class
          zIndex: 3000, // 菜单样式 z-index
        });
      } else {
        this.$contextmenu({
          items: [
            {
              label: "刷新节点",
              icon: "el-icon-refresh",
              disabled: false,
              onClick: () => {
                this.refreshCatalog(node);
              }
            },
            {
              label: "新建节点",
              icon: "el-icon-plus",
              disabled: false,
              onClick: () => {
                this.addCatalog(1, data.catalogId, node);
              }
            },
            {
              label: "导入设备",
              icon: "el-icon-right",
              disabled: node.level === 1,
              onClick: () => {
                this.addCatalog(3, data.catalogId, node);
              }
            },
            {
              label: "选择通道",
              icon: "el-icon-check",
              disabled: node.level === 1,
              onClick: () => {
                this.chooseId = data.catalogId
                this.chooseName = data.name
                this.chooseNode = node
                this.$refs.SelectChannelTable.openDialog()
              }
            },
            {
              label: "修改节点",
              icon: "el-icon-edit",
              disabled: node.level === 1,
              onClick: () => {
                this.editCatalog(2, data, node);
              }
            },
            {
              label: "删除节点",
              icon: "el-icon-delete",
              disabled: node.level === 1,
              divided: true,
              onClick: () => {
                this.$confirm('确定删除?', '提示', {
                  confirmButtonText: '确定',
                  cancelButtonText: '取消',
                  type: 'warning'
                }).then(() => {
                  this.removeGroup(data.id, node)
                }).catch(() => { });
              }
            }
          ],
          event, // 鼠标事件信息
          customClass: "custom-class", // 自定义菜单 class
          zIndex: 3000, // 菜单样式 z-index
        });
      }
      return false;
    },
    nodeClickHandler: function (data, node, tree) {
      if (typeof (this.clickEvent) == "function") {
        this.clickEvent(data, data, !node.isLeaf)
      }
    }
  }
};
</script>

<style>
.groupTree {
  padding: 8px 10px 0 3px;
  box-sizing: border-box;
  color: #fff;
  
  height: calc(100vh - 150px);
  overflow-y: scroll;
}

.groupTree .el-tree {
  font-size: 12px;
  background-color: transparent;
  color: #fff;
  display: block;
  text-align: left;
}

.deviceMenu .el-tabs__item {
  color: #fff;
}

.deviceMenu .el-tabs__item.is-active {
  color: #E45252;
}

.deviceMenu .el-tabs__active-bar {
  background-color: #E45252;
}

.deviceMenu .el-tree-node__content:hover {
  background-color: #E45252 !important;
}

.deviceMenu .el-input-group__append,
.deviceMenu .el-input-group__prepend {
  background-color: #E45252;
  border: 1px solid #E45252;
}

.device-online {
  color: #252525;
}

.device-offline {
  color: #727272;
}

.deviceMenu .el-tree-node__expand-icon {
  color: #6691FF;
}

.deviceMenu .device-online {
  color: #fff;
}
</style>

