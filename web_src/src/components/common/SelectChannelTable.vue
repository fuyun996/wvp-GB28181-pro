<template>
  <el-dialog :title="title" :visible.sync="dialogVisible" width="70%">
    <div>
      <el-form :inline="true" :model="sform" style="text-align: left;">
        <el-form-item label="搜索:">
          <el-input v-model="sform.query" placeholder="关键字" @change="currentPage = 1; getDeviceChannelList()"></el-input>
        </el-form-item>
        <el-form-item v-if="type == 1" label="在线状态:">
          <el-select v-model="sform.online" @change="currentPage = 1; getDeviceChannelList()">
            <el-option label="在线" :value="true"></el-option>
            <el-option label="离线" :value="false"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="type == 2" label="通道类型:">
          <el-select v-model="sform.type" @change="currentPage = 1; getDeviceChannelList()">
            <el-option label="未知" :value="0"></el-option>
            <el-option label="球机" :value="1"></el-option>
            <el-option label="半球" :value="2"></el-option>
            <el-option label="固定枪机" :value="3"></el-option>
            <el-option label="遥控枪机" :value="4"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="type == 1" label="轮播间隔(秒):" required>
          <el-input v-model="sform.time" type="number"></el-input>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="sform.isRead" @change="setTableList">只看已选({{ selectAllList.length }})</el-checkbox>
        </el-form-item>
      </el-form>
      <el-table ref="channelListTable" :data="tableList" style="width: 100%" header-row-class-name="table-header"
        row-key="id" @selection-change="handleSelectionChange">
        <el-table-column type="selection" reserve-selection width="55"></el-table-column>
        <el-table-column prop="deviceId" label="设备国际编号" min-width="180">
        </el-table-column>
        <el-table-column prop="channelId" label="通道国际编号" min-width="180">
        </el-table-column>
        <!-- <el-table-column prop="name" label="设备名称" :show-overflow-tooltip="true" width="300">
                                                                </el-table-column> -->
        <el-table-column prop="name" label="通道名称" :show-overflow-tooltip="true" width="300">
        </el-table-column>
        <el-table-column label="在线状态" min-width="80">
          <template slot-scope="scope">
            <div slot="reference" class="name-wrapper">
              <el-tag size="medium" v-if="scope.row.status === 1">在线</el-tag>
              <el-tag size="medium" type="info" v-if="scope.row.status === 0">离线</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="manufacture" label="厂家" min-width="120"></el-table-column>
      </el-table>
      <el-pagination style="float: right" @size-change="handleSizeChange" @current-change="currentChange"
        :current-page="currentPage" :page-size="count" :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next" :total="total">
      </el-pagination>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="onSubmit()">确 定</el-button>
    </span>
</el-dialog>
</template>

<script>


export default {
  name: 'SelectChannelTable',
  props: ['title', 'type'],
  data() {
    return {
      dialogVisible: false,
      deviceChannelList: [],
      tableList: [],
      currentPage: 1,
      count: 10,
      total: 0,
      sform: { query: '', online: true, time: '', isRead: false },
      selectAllList: [],
    };
  },
  methods: {
    openDialog() {
      this.selectAllList = []
      this.currentPage = 1
      this.getDeviceChannelList()
      this.dialogVisible = true
      this.$refs.channelListTable && this.$refs.channelListTable.clearSelection();
    },
    closeDialog() {
      this.dialogVisible = false
      this.sform = { query: '', online: true, time: '', isRead: false }
    },
    handleSelectionChange(e) {
      this.selectAllList = e
    },
    setTableList(e) {
      this.currentPage = 1
      if (e) {
        this.tableList = this.selectAllList.slice(0, this.count)
        this.total = this.selectAllList.length
        this.$nextTick(() => {
          this.$refs.channelListTable.doLayout();
        })
      } else {
        this.getDeviceChannelList()
      }
    },
    onSubmit() {
      this.$emit('submit', this.selectAllList, this.sform.time)
    },
    currentChange: function (val) {
      this.currentPage = val;
      if (this.sform.isRead) {
        this.tableList = this.selectAllList.slice((this.currentPage - 1) * this.count, this.currentPage * this.count)
      } else {
        this.getDeviceChannelList();
      }
    },
    handleSizeChange: function (val) {
      this.count = val;
      this.getDeviceChannelList();
    },
    getDeviceChannelList: function () {
      let that = this
      this.$axios({
        method: 'get',
        url: `/api/device/query/devices/50122700002000000123/channels`,
        params: {
          page: that.currentPage,
          count: that.count,
          query: that.sform.query,
          online: that.sform.online,
          channelType: true
        }
      }).then(function (res) {
        if (res.data.code === 0) {
          that.total = res.data.data.total
          that.tableList = that.deviceChannelList = res.data.data.list
          // 防止出现表格错位
          that.$nextTick(() => {
            that.$refs.channelListTable.doLayout();
          })
        }
      }).catch(function (error) { console.log(error); });
    },

  },
  destroyed() {
    // if (this.jessibuca) {
    //   this.jessibuca.destroy();
    // }
    // this.playing = false;
    // this.loaded = false;
    // this.performance = "";
  },
}
</script>