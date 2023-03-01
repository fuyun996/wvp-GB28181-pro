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
        <el-tabs v-model="activeName">
          <el-tab-pane label="设备树" name="first">
            <DeviceTree :clickEvent="clickEvent" :contextMenuEvent="contextMenuEvent"></DeviceTree>
          </el-tab-pane>
          <el-tab-pane label="分组" name="second">
            <GroupTree :clickEvent="clickEvent"></GroupTree>
          </el-tab-pane>
        </el-tabs>
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
        <div v-for="i in spilt" :key="i" class="play-box" :style="liveStyle" :class="{ redborder: playerIdx == (i - 1) }"
          @click="playerIdx = (i - 1)">
          <div v-if="!videoUrl[i - 1]" style="color: #ffffff;font-size: 30px;font-weight: bold;">{{ i }}</div>
          <player ref="player" v-else :videoUrl="videoUrl[i - 1]" fluent autoplay @screenshot="shot" @destroy="destroy" />
        </div>
      </div>
    </div>

    <!-- 轮询选择 -->
    <SelectChannelTable ref="SelectChannelTable" :type="1" :title="'选择轮询播放通道'" @submit="startPolling">
    </SelectChannelTable>
  </el-container>
</template>

<script>
import uiHeader from "../layout/UiHeader.vue";
import player from './common/jessibuca.vue'
import DeviceTree from './common/DeviceTree.vue'
import GroupTree from './dialog/deviceGroup/groupTree.vue'
import SelectChannelTable from './common/SelectChannelTable.vue'
import DeviceService from "./service/DeviceService.js";

export default {
  name: "live",
  components: {
    uiHeader, player, DeviceTree, GroupTree, SelectChannelTable
  },
  data() {
    return {
      deviceService: new DeviceService(),
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

      activeName: 'first',

      // 视频轮询
      selectAllList: [],
      isPolling: false,
      pollingTimer: null,

      // 分组
      groupDialogVisible: false,
      groupOperationType: 1, // 1-刷新节点， 2-新增，3-编辑， 4-删除， 5-导入设备，6-导入通道
      groupForm: {},
      groupFormRule: {
        catalogId: [
          { required: true, message: '请输入节点编号', trigger: 'blur' },
          { min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
        ],
      }
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
    shot(file) {
      this.$prompt('请输入图片名', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        // inputPattern: //,
        inputErrorMessage: '名称格式不正确'
      }).then(({ value }) => {
        let param = new FormData()
        param.append('file', file, value)
        this.$axios({
          method: 'post',
          url: `/api/device/query/snap`,
          headers: { 'Content-Type': 'multipart/form-data' },
          data: param
        }).then((res) => {
          if (res.data.code == 0) {
            this.$message.success(res.data.msg);
          } else {
            this.$message.error(res.data.msg);
          }
        }).catch(function (error) {
          console.log(error)
        })
      }).catch(() => {

      });

      // var base64ToBlob = function (code) {
      //   let parts = code.split(';base64,');
      //   let contentType = parts[0].split(':')[1];
      //   let raw = window.atob(parts[1]);
      //   let rawLength = raw.length;
      //   let uInt8Array = new Uint8Array(rawLength);
      //   for (let i = 0; i < rawLength; ++i) {
      //     uInt8Array[i] = raw.charCodeAt(i);
      //   }
      //   return new Blob([uInt8Array], {
      //     type: contentType
      //   });
      // };
      // let aLink = document.createElement('a');
      // let blob = base64ToBlob(e); //new Blob([content]);
      // let evt = document.createEvent("HTMLEvents");
      // evt.initEvent("click", true, true); //initEvent 不加后两个参数在FF下会报错  事件类型，是否冒泡，是否阻止浏览器的默认行为
      // aLink.download = '截图';
      // aLink.href = URL.createObjectURL(blob);
      // aLink.click();
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
    openPolling() {
      if (this.isPolling) {
        this.stopPolling()
        return false
      }
      this.selectAllList = []
      this.$refs.SelectChannelTable.openDialog()
    },
    // 视频开始轮播
    startPolling(data, time) {
      this.selectAllList = data
      if (this.selectAllList.length < 1) {
        this.$message.warning('请选择轮询播放通道');
        return false
      }
      if (!time) {
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
      }, Number(time * 1000))

      this.pollingDialogVisible = false
    },
    // 视频停止轮播
    stopPolling() {
      clearInterval(this.pollingTimer)
      this.isPolling = false
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
