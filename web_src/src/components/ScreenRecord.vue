<template>
  <div id="app" style="width: 100%">
    <div class="page-header">
      <div>
        <span class="page-title">录屏文件：</span>
        <el-input @input="getRecordList" style="margin-right: 1rem; width: auto;" size="mini" placeholder="请输入文件名"
          prefix-icon="el-icon-search" v-model="query.name" clearable></el-input>
        <el-button type="primary" size="mini" @click="getRecordList()">搜索</el-button>
      </div>

      <div class="page-header-btn">
        <el-button icon="el-icon-refresh-right" circle size="mini" :loading="loading"
          @click="getRecordList()"></el-button>
      </div>
    </div>
    <div class="page-content">
      <!--设备列表-->
      <el-table :data="recordList" style="width: 100%" :height="winHeight">
        <el-table-column prop="fileName" label="文件名">
        </el-table-column>
        <el-table-column prop="fileType" label="文件类型">
          <template slot-scope="scope">
            <span v-if="scope.row.fileType == 1">截图</span>
            <span v-else-if="scope.row.fileType == 2">录像</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="用户">
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间">
        </el-table-column>
        <el-table-column label="操作" width="360" fixed="right">
          <template slot-scope="scope">
            <el-button v-if="scope.row.fileType == 1" size="mini" icon="el-icon-view" type="primary"
              @click="showImages(scope.row)">查看</el-button>
            <el-button v-if="scope.row.fileType == 2" size="mini" icon="el-icon-video-play" type="primary"
              @click="palyVideo(scope.row)">播放</el-button>
            <el-button size="mini" icon="el-icon-download" type="primary"
              @click="downLoad(scope.row.fileType)">下载</el-button>
            <el-button size="mini" icon="el-icon-delete" type="danger" @click="deleteRecord(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="float: right" @size-change="handleSizeChange" @current-change="currentChange"
        :current-page="query.page" :page-size="query.count" :page-sizes="[15, 25, 35, 50]"
        layout="total, sizes, prev, pager, next" :total="total">
      </el-pagination>
    </div>

    <!--  -->
    <el-dialog :title="viewObj.name" :visible.sync="dialogVisible" width="50%">
      <div v-loading="dialogLoading">
        <el-image v-if="viewObj.type == 1" :src="viewObj.url" :preview-src-list="[viewObj.url]">
        </el-image>
        <player v-else-if="viewObj.type == 2" ref="player" :videoUrl="viewObj.url" fluent autoplay />

      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
      </span>
    </el-dialog>
  </div>
</template>
  
<script>
import player from './common/jessibuca.vue'
import MediaServer from './service/MediaServer'

export default {
  name: 'ScreenRecord',
  components: {
    player
  },
  data() {
    return {
      winHeight: window.innerHeight - 250,
      mediaServerObj: new MediaServer(),
      loading: false,
      query: {
        page: 1,
        count: 15,
        name: ''
      },
      total: 0,
      recordList: [],
      dialogVisible: false,
      dialogLoading: false,
      viewObj: {
        type: 1,
        name: '',
        url: ''
      }
    };
  },
  computed: {

  },
  mounted() {
    this.getRecordList();
  },
  destroyed() {
    // this.$destroy('videojs');
  },
  methods: {
    currentChange: function (val) {
      this.query.page = val;
      this.getRecordList();
    },
    handleSizeChange: function (val) {
      this.query.count = val;
      this.getRecordList();
    },
    getRecordList: function () {
      this.mediaServerObj.getDeviceScreenRecord(this.query, res => {
        if (res.code === 0) {
          this.query.total = res.data.total;
          this.recordList = res.data.list;
        }
        this.loading = false;
      })
    },
    showImages(row) {
      this.dialogVisible = true
      this.dialogLoading = true
      this.viewObj.type = 1
      this.mediaServerObj.getUploadSnap({ id: row.id }, res => {
        this.viewObj.name = row.fileName
        let blob = new Blob([res]);
        this.viewObj.url = URL.createObjectURL(blob);
        this.dialogLoading = false
      }, error => {
        this.dialogLoading = false
      })
    },
    palyVideo(row) {
      this.dialogVisible = true
      this.dialogLoading = true
      this.viewObj.type = 2
      this.viewObj.name = row.fileName
    },
    downLoad(type) {
      if (type == 1) {
        this.mediaServerObj.getUploadSnap({ id: id }, res => {
          let aLink = document.createElement('a');
          let blob = new Blob([res]);
          let evt = document.createEvent("HTMLEvents");
          evt.initEvent("click", true, true);
          aLink.download = '截图';
          aLink.href = URL.createObjectURL(blob);
          aLink.click();
        })
      }
    },
    deleteRecord(id) {
      this.$axios({
        method: 'delete',
        url: `/api/device/query/deleteSnapScreenRecord `,
        params: { id: id }
      }).then(function (res) {
        if (res.data.code === 0) {

        }
      }).catch(function (error) {
        console.log(error);
      });
    }
  }
};
</script>
  
<style></style>
  