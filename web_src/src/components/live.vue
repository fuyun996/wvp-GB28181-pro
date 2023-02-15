<template>
  <el-container style="width:100%; height: 100%;background: #1F1F2F;" element-loading-text="拼命加载中">
    <!-- 设备列表 -->
    <el-aside v-if="isCollapse" class="deviceMenu">
      <div class="wrapper">
        <div class="title">
          <span>设备列表</span>
          <div style="float: right;margin-right: 5px;">
            <i @click="isCollapse = false" class="el-icon-s-fold" style="font-size: 18px;cursor: pointer;"></i>
          </div>
        </div>
        <DeviceTree :clickEvent="clickEvent" :contextMenuEvent="contextMenuEvent"></DeviceTree>
      </div>
    </el-aside>
    <div v-else class="deviceMenu-collapse" @click="isCollapse = true">
      设备列表
    </div>

    <!-- 视频区域 -->
    <div class="videoarea">
      <el-header height="4vh"
        style="text-align: left;font-size: 17px;line-height:4vh;color:#fff; display: flex;align-items: center;justify-content: space-between;">
        <div>
          <i class="spyfont icon-danshipin btn" :class="{ active: spilt == 1 }" @click="spilt = 1" />
          <i class="spyfont icon-sigongge btn" :class="{ active: spilt == 4 }" @click="spilt = 4" />
          <i class="spyfont icon-jiugongge btn" :class="{ active: spilt == 9 }" @click="spilt = 9" />
        </div>
        <div>
          <i class="spyfont icon-luxiang btn" :class="{ active: operation == 1 }" @click="operation = 1" />
          <i class="spyfont icon-lunxun btn" :class="{ active: isPolling }" @click="openPolling()" />
          <i class="spyfont icon-jietu btn" :class="{ active: operation == 3 }" @click="operation = 3" />
          <i class="spyfont icon-quanping btn" @click="fullScreen()" />
        </div>
      </el-header>
      <div ref="vbox" class="videobox" :class="operation == 4 ? 'videobox-fullscreen' : ''">
        <div v-for="i in spilt" :key="i" class="play-box" :style="liveStyle"
          :class="{ redborder: playerIdx == (i - 1) }" @click="playerIdx = (i - 1)">
          <div v-if="!videoUrl[i - 1]" style="color: #ffffff;font-size: 30px;font-weight: bold;">{{ i }}</div>
          <player ref="player" v-else :videoUrl="videoUrl[i - 1]" fluent autoplay @screenshot="shot"
            @destroy="destroy" />
        </div>
      </div>
    </div>>

    <!-- 轮询选择 -->
    <el-dialog title="选择轮询播放通道" :visible.sync="pollingDialogVisible" width="70%">
      <div>
        <el-form :inline="true" :model="sform">
          <el-form-item label="搜索:">
            <el-input v-model="sform.query" placeholder="关键字"
              @change="currentPage = 1; getDeviceChannelList()"></el-input>
          </el-form-item>
          <el-form-item label="在线状态:">
            <el-select v-model="sform.online" @change="currentPage = 1; getDeviceChannelList()">
              <el-option label="在线" :value="true"></el-option>
              <el-option label="离线" :value="false"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="轮播间隔(秒):" required>
            <el-input v-model="sform.time" type="number"></el-input>
          </el-form-item>
          <el-form-item>
            <el-checkbox v-model="sform.isRead" @change="setTableList">只看已选({{ selectAllList.length }})</el-checkbox>
          </el-form-item>
        </el-form>
        <el-table ref="channelListTable" :data="tableList" style="width: 100%" header-row-class-name="table-header"
          row-key="id" @selection-change="handleSelectionChange">
          <el-table-column type="selection" reserve-selection width="55"></el-table-column>
          <!-- <el-table-column prop="id" label="ID" min-width="180"></el-table-column> -->
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
        <el-button @click="pollingDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="startPolling()">确 定</el-button>
      </span>
    </el-dialog>
  </el-container>
</template>

<script>
import uiHeader from "../layout/UiHeader.vue";
import player from './common/jessibuca.vue'
import DeviceTree from './common/DeviceTree.vue'

export default {
  name: "live",
  components: {
    uiHeader, player, DeviceTree
  },
  data() {
    return {
      videoUrl: [''],
      spilt: 1,//分屏
      operation: -1, //操作
      playerIdx: 0,//激活播放器
      curIdx: 0,// 

      updateLooper: 0, //数据刷新轮训标志
      count: 10,
      total: 0,

      loading: false,
      isCollapse: true,

      pollingDialogVisible: false,
      deviceChannelList: [],
      tableList: [],
      currentPage: 1,
      count: 10,
      total: 0,
      sform: { query: '', online: true, time: '', isRead: false },
      selectAllList: [],
      pollingTimer: null,
      isPolling: false
    };
  },
  mounted() {
  },
  created() {
    this.checkPlayByParam()
  },
  computed: {
    liveStyle() {
      let style = { width: '100%', height: '100%' }
      switch (this.spilt) {
        case 4:
          style = { width: '49%', height: '49%' }
          break
        case 9:
          style = { width: '32%', height: '32%' }
          break
      }
      this.$nextTick(() => {
        for (let i = 0; i < this.spilt; i++) {
          const player = this.$refs.player
          player && player[i] && player[i].updatePlayerDomSize()
        }
      })
      return style
    }
  },
  watch: {
    spilt(newValue) {
      console.log("切换画幅;" + newValue)
      let that = this
      for (let i = 1; i <= newValue; i++) {
        if (!that.$refs['player' + i]) {
          continue
        }
        this.$nextTick(() => {
          if (that.$refs['player' + i] instanceof Array) {
            that.$refs['player' + i][0].resize()
          } else {
            that.$refs['player' + i].resize()
          }
        })

      }
      window.localStorage.setItem('split', newValue)
    },
    '$route.fullPath': 'checkPlayByParam'
  },
  destroyed() {
    this.updateLooper && clearTimeout(this.updateLooper);
    this.pollingTimer && clearInterval(this.pollingTimer);
  },
  methods: {
    destroy(idx) {
      console.log(idx);
      this.clear(idx.substring(idx.length - 1))
    },
    clickEvent: function (device, data, isCatalog) {
      if (data.channelId && !isCatalog) {
        if (device.online === 0) {
          this.$message.error('设备离线!不允许点播');
        } else {
          this.sendDevicePush(data)
        }
      }
    },
    contextMenuEvent: function (device, event, data, isCatalog) {

    },
    //通知设备上传媒体流
    sendDevicePush: function (itemData, ispolling) {
      // if (itemData.status === 0) {
      //   this.$message.error('设备离线!');
      //   return
      // }
      this.save(itemData)
      let deviceId = itemData.deviceId;
      // this.isLoging = true;
      let channelId = itemData.channelId;
      console.log("通知设备推流1：" + deviceId + " : " + channelId);
      let idxTmp = this.playerIdx
      this.loading = true
      this.$axios({
        method: 'get',
        url: '/api/play/start/' + deviceId + '/' + channelId
      }).then((res) => {
        if (res.data.code === 0 && res.data.data) {
          let vUrl = location.protocol === "https:" ? res.data.data.wss_flv.url : res.data.data.ws_flv.url
          itemData.playUrl = vUrl;
          // 是否是轮询视频
          if (ispolling) {
            this.$set(this.videoUrl, this.spilt - 1, vUrl)
            setTimeout(() => {
              window.localStorage.setItem('videoUrl', JSON.stringify(this.videoUrl))
            }, 100)
          } else {
            this.setPlayUrl(vUrl, idxTmp);
          }
        } else {
          this.$message.error(res.data.msg);
        }
      }).catch(function (e) {
      }).finally(() => {
        this.loading = false
      });
    },
    setPlayUrl(url, idx) {
      this.spilt == 1 && this.stopPolling() // 停止轮播
      let max = this.isPolling ? this.spilt - 1 : this.spilt
      if (this.curIdx >= max) {
        this.curIdx = 0
      }
      console.log('播放索引', this.curIdx)
      this.$set(this.videoUrl, this.curIdx, url)
      this.curIdx++
      setTimeout(() => {
        window.localStorage.setItem('videoUrl', JSON.stringify(this.videoUrl))
      }, 100)
    },
    checkPlayByParam() {
      let { deviceId, channelId } = this.$route.query
      if (deviceId && channelId) {
        this.sendDevicePush({ deviceId, channelId })
      }
    },
    shot(e) {
      // console.log(e)
      // send({code:'image',data:e})
      var base64ToBlob = function (code) {
        let parts = code.split(';base64,');
        let contentType = parts[0].split(':')[1];
        let raw = window.atob(parts[1]);
        let rawLength = raw.length;
        let uInt8Array = new Uint8Array(rawLength);
        for (let i = 0; i < rawLength; ++i) {
          uInt8Array[i] = raw.charCodeAt(i);
        }
        return new Blob([uInt8Array], {
          type: contentType
        });
      };
      let aLink = document.createElement('a');
      let blob = base64ToBlob(e); //new Blob([content]);
      let evt = document.createEvent("HTMLEvents");
      evt.initEvent("click", true, true); //initEvent 不加后两个参数在FF下会报错  事件类型，是否冒泡，是否阻止浏览器的默认行为
      aLink.download = '截图';
      aLink.href = URL.createObjectURL(blob);
      aLink.click();
    },
    save(item) {
      let dataStr = window.localStorage.getItem('playData') || '[]'
      let data = JSON.parse(dataStr);
      data[this.playerIdx] = item
      window.localStorage.setItem('playData', JSON.stringify(data))
    },
    clear(idx) {
      let dataStr = window.localStorage.getItem('playData') || '[]'
      let data = JSON.parse(dataStr);
      data[idx - 1] = null;
      console.log(data);
      window.localStorage.setItem('playData', JSON.stringify(data))
    },
    // 打开视频轮播设置弹窗
    openPolling () {
      if(this.isPolling){
        this.stopPolling()
        return false
      }
      this.sform = { query: '', online: true, time: '', isRead: false }
      this.selectAllList = []
      this.currentPage = 1
      this.getDeviceChannelList()
      this.pollingDialogVisible = true
      this.$refs.channelListTable && this.$refs.channelListTable.clearSelection();
    },
    // 视频开始轮播
    startPolling() {
      if (this.selectAllList.length < 1) {
        this.$message.warning('请选择轮询播放通道');
        return false
      }
      if (!this.sform.time) {
        this.$message.warning('请填写轮播间隔，单位(秒)');
        return false
      }
      this.isPolling = true
      let count = 1
      this.sendDevicePush(this.selectAllList[0], true)
      this.pollingTimer && clearInterval(this.pollingTimer)
      this.pollingTimer = setInterval(() => {
        if (!this.isPolling) clearInterval(this.pollingTimer)
        if (count >= this.selectAllList.length) {
          count = 0
        }
        this.sendDevicePush(this.selectAllList[count], true)
        count++
      }, Number(this.sform.time * 1000))

      this.pollingDialogVisible = false
    },
    // 视频停止轮播
    stopPolling() {
      clearInterval(this.pollingTimer)
      this.isPolling = false
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

      }).catch(function (error) {
        console.log(error);
      });
    },
    // 全屏
    fullScreen() {
      let element = this.$refs.vbox;

      if (element.requestFullscreen) {
        element.requestFullscreen();
      } else if (element.webkitRequestFullScreen) {
        element.webkitRequestFullScreen();
      } else if (element.mozRequestFullScreen) {
        element.mozRequestFullScreen();
      } else if (element.msRequestFullscreen) {
        element.msRequestFullscreen();
      }
      this.fullscreen = !this.fullscreen
    }
  }
};
</script>
<style>
.deviceMenu .wrapper {
  margin: 3px 10px 15px 3px;
  border-radius: 3px;
  background: #15171D;
  overflow: hidden;
}

.deviceMenu .title {
  width: 100%;
  height: 26px;
  color: #fff;
  font-family: PingFang SC;
  font-weight: bold;
  font-size: 16px;
  line-height: 26px;
  background: #E45252;
  text-align: center;
}

.deviceMenu-collapse {
  width: 30px;
  height: 131px;
  padding-top: 20px;
  box-sizing: border-box;
  background: #E45252;
  cursor: pointer;

  color: #FFFFFF;
  font-family: PingFang SC;
  font-weight: bold;
  font-size: 16px;
  line-height: 20px;
  letter-spacing: 0px;
  text-align: center;
}

.videoarea {
  flex: 1;
}

.btn {
  margin: 0 8px;
  cursor: pointer;
}

.btn:hover {
  color: #409EFF;
}

.btn.active {
  color: #409EFF;

}

.redborder {
  border: 2px solid red !important;
}

.play-box {
  margin: 0 0.5% 1%;
  box-sizing: border-box;
  background-color: #000000;
  border: 2px solid #505050;
  display: flex;
  align-items: center;
  justify-content: center;
}

.videobox {
  height: calc(96vh - 66px);
  display: flex;
  flex-wrap: wrap;
  background-color: #000;
}

.videobox-fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}
</style>
<style>
.videoList {
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
}

.video-item {
  position: relative;
  width: 15rem;
  height: 10rem;
  margin-right: 1rem;
  background-color: #000000;
}

.video-item-img {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  margin: auto;
  width: 100%;
  height: 100%;
}

.video-item-img:after {
  content: "";
  display: inline-block;
  position: absolute;
  z-index: 2;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  margin: auto;
  width: 3rem;
  height: 3rem;
  background-image: url("../assets/loading.png");
  background-size: cover;
  background-color: #000000;
}

.video-item-title {
  position: absolute;
  bottom: 0;
  color: #000000;
  background-color: #ffffff;
  line-height: 1.5rem;
  padding: 0.3rem;
  width: 14.4rem;
}

.baidumap {
  width: 100%;
  height: 100%;
  border: none;
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  margin: auto;
}

/* 去除百度地图版权那行字 和 百度logo */
.baidumap>.BMap_cpyCtrl {
  display: none !important;
}

.baidumap>.anchorBL {
  display: none !important;
}
</style>
