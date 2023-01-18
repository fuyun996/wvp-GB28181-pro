<template>
  <div class="wrapper" id="UiHeader">
    <div class="logo" :class="isCollapse ? 'logo-collapse' : ''">
      <img src="../../static/images/logo.png" />
      <div v-if="!isCollapse">中科曙光视频云</div>
    </div>
    <div class="page-left-menu">
      <el-menu router :default-active="activeIndex" background-color="transparent" text-color="#fff"
        active-text-color="#fff" @open="handleOpen" @close="handleClose" :collapse="isCollapse">
        <template v-for="(item, index) in menuList">
          <template v-if="item.child">
            <el-submenu :index="item.url" :key="index">
              <template slot="title">
                <i class="el-icon-platform-eleme"></i>
                <span slot="title">{{ item.name }}</span>
              </template>
              <template v-for="(subItem, inx) in item.child">
                <el-submenu v-if="subItem.child" :index="subItem.url" :key="inx">
                  <template slot="title"><i class="el-icon-platform-eleme"></i>{{ subItem.title }}</template>
                  <el-menu-item v-for="(threeItem, i) in subItem.subs" :key="i" :index="i">{{
                    threeItem.name
                  }}</el-menu-item>
                </el-submenu>
                <el-menu-item v-else :index="subItem.url" :key="inx">{{ subItem.name }}</el-menu-item>
              </template>
            </el-submenu>
          </template>
          <template v-else>
            <el-menu-item :index="item.url" :key="index">
              <i class="el-icon-platform-eleme"></i>
              <span slot="title">{{ item.name }}</span>
            </el-menu-item>
          </template>
        </template>
        <!-- <el-switch v-model="alarmNotify" active-text="报警信息推送" @change="alarmNotifyChannge"></el-switch>-->
        <template>
          <el-menu-item @click="changePassword">
            <i class="el-icon-platform-eleme"></i>
            <span slot="title">修改密码</span></el-menu-item>
          <el-menu-item @click="loginout">
            <i class="el-icon-platform-eleme"></i>
            <span slot="title">注销</span></el-menu-item>
        </template>
      </el-menu>
    </div>
    <div class="user-box" :class="isCollapse ? 'user-collapse' : ''">
      <div class="portrait"></div>
      <span class="txt">欢迎，{{ this.$cookies.get("session").username }}</span>

      <i v-if="isCollapse" @click="isCollapse = false" class="el-icon-s-unfold"
        style="font-size: 24px;cursor: pointer;"></i>
      <i v-else @click="isCollapse = true" class="el-icon-s-fold" style="font-size: 24px;cursor: pointer;"></i>
    </div>

    <changePasswordDialog ref="changePasswordDialog"></changePasswordDialog>
  </div>
</template>

<script>
import changePasswordDialog from '../components/dialog/changePassword.vue'

export default {
  name: "UiHeader",
  components: { Notification, changePasswordDialog },
  data() {
    return {
      isCollapse: false,
      alarmNotify: false,
      sseSource: null,
      activeIndex: this.$route.path,
      editUser: this.$cookies.get("session").roleId == 1,
      menuList: []
    };
  },
  created() {
    if (this.$route.path.startsWith("/channelList")) {
      this.activeIndex = "/deviceList"
    }
  },
  mounted() {
    window.addEventListener('beforeunload', e => this.beforeunloadHandler(e))
    // window.addEventListener('unload', e => this.unloadHandler(e))
    this.alarmNotify = this.getAlarmSwitchStatus() === "true";
    this.sseControl();
    this.getMenuList()
  },
  methods: {
    handleOpen(key, keyPath) {
      console.log(key, keyPath);
    },
    handleClose(key, keyPath) {
      console.log(key, keyPath);
    },
    getMenuList() {
      this.$axios({
        method: 'get',
        url: "/api/role/getMenuByRole"
      }).then((res) => {
        if (res.data.code == 0) {
          this.menuList = res.data.data
        }
        console.log(res)
      }).catch((error) => {
        console.error("登出失败")
        console.error(error)
      });
    },
    loginout() {
      this.$axios({
        method: 'get',
        url: "/api/user/logout"
      }).then((res) => {
        // 删除cookie，回到登录页面
        this.$cookies.remove("session");
        this.$router.push('/login');
        this.sseSource.close();
      }).catch((error) => {
        console.error("登出失败")
        console.error(error)
      });
    },
    changePassword() {
      this.$refs.changePasswordDialog.openDialog()
    },
    gotoManager() {
      this.$router.push('/roleManager');
    },
    openDoc() {
      console.log(process.env.BASE_API)
      window.open(!!process.env.BASE_API ? process.env.BASE_API + "/doc.html" : "/doc.html")
    },
    beforeunloadHandler() {
      this.sseSource.close();
    },
    alarmNotifyChannge() {
      this.setAlarmSwitchStatus()
      this.sseControl()
    },
    sseControl() {
      let that = this;
      if (this.alarmNotify) {
        console.log("申请SSE推送API调用，浏览器ID: " + this.$browserId);
        this.sseSource = new EventSource('/api/emit?browserId=' + this.$browserId);
        this.sseSource.addEventListener('message', function (evt) {
          that.$notify({
            title: '收到报警信息',
            dangerouslyUseHTMLString: true,
            message: evt.data,
            type: 'warning'
          });
          console.log("收到信息：" + evt.data);
        });
        this.sseSource.addEventListener('open', function (e) {
          console.log("SSE连接打开.");
        }, false);
        this.sseSource.addEventListener('error', function (e) {
          if (e.target.readyState == EventSource.CLOSED) {
            console.log("SSE连接关闭");
          } else {
            console.log(e.target.readyState);
          }
        }, false);
      } else {
        if (this.sseSource != null) {
          this.sseSource.removeEventListener('open', null);
          this.sseSource.removeEventListener('message', null);
          this.sseSource.removeEventListener('error', null);
          this.sseSource.close();
        }

      }
    },
    getAlarmSwitchStatus() {
      if (localStorage.getItem("alarmSwitchStatus") == null) {
        localStorage.setItem("alarmSwitchStatus", false);
      }
      return localStorage.getItem("alarmSwitchStatus");
    },
    setAlarmSwitchStatus() {
      localStorage.setItem("alarmSwitchStatus", this.alarmNotify);
    }
  },
  destroyed() {
    window.removeEventListener('beforeunload', e => this.beforeunloadHandler(e))
    if (this.sseSource != null) {
      this.sseSource.removeEventListener('open', null);
      this.sseSource.removeEventListener('message', null);
      this.sseSource.removeEventListener('error', null);
      this.sseSource.close();
    }
  },

}

</script>
<style scoped>
.wrapper {
  height: 100%;
  padding: 30px 0;
  color: #fff;
  background: linear-gradient(180deg, #FFFFFF0C 0%, #FFFFFF0C 100%);
  box-sizing: border-box;
  border-right: 1px solid #FFFFFF1E;
}

.logo {
  height: 40px;
  margin-bottom: 18px;
  padding-left: 16px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.logo img {
  width: auto;
  height: 40px;
  margin-right: 16px;
}

.logo-collapse {
  justify-content: center;
}

.logo-collapse img {
  height: 28px;
}

.page-left-menu {
  height: calc(100% - 120px);
  overflow-x: hidden;
  overflow-y: scroll;
}

/*滚动条样式*/
.page-left-menu::-webkit-scrollbar {
  width: 5px
}

/*滚动条滑块*/
.page-left-menu::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background: #BCEDFD3D;
  -webkit-box-shadow: inset006pxrgba(0, 0, 0, 0.5);
}

/*滚动槽*/
.page-left-menu::-webkit-scrollbar-track-piece {
  width: 2px;
  background: #121212;
}

.user-box {
  display: flex !important;
  align-items: center;
  justify-content: space-around;
  position: absolute;
  left: 0;
  bottom: 0;
  width: 256px;
  height: 56px;
  background-color: #BCEDFD3D;
  transition: all 0.3s ease-in-out;
}

.portrait {
  width: 36px;
  height: 36px;
  background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAYAAACM/rhtAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAANuSURBVHgBzZnNTttAFIWvnYCyADVUILFrkgUSbAhdIBYg3LBk1xeo+gTQPkCj9AHawgsUXqDdIxEMYsWKCokFSFWyQwIVV4BA/PYc1474cZyMJyT+JMN4xsMc3Zm5d+5g3N3dGRKB1dVVK5FIjN7c3FiGYeTxvMDfSrMNZQe/Kre3t1WUt1G/PjMzY0sEDBWBm5ub6cvLyzkMOueLaXogw6jgsVOpVGliYqLadL9mBHrCiijOSQswTXOpWaENBZbLZVqsqGqxhgP/t2jJsqzl0O/CBELcV2mR1eoBa36DyI/12gMFelP6E8VpaQPcSF1dXYXJyUnncZsZ1OHq6qosbRJHYKQ8xvwR1PZEIKeVHaTNYEzLtu0vj+sfTDE+eAff9V06y4dCobDgv9QEbmxsZOB0y3jPSGdxuru7c/56rE0xxBVjII6kr6+vP/kvrgVpPVT+lhgBK76kFZN8ofVEA0QFGRkZkZ6eHkkmk3J0dCR7e3tycXEhUWFIxa+SP8WRXQrFjY+PSzqddsWR/v5+t45tGrgBIjE1NfVGNKLF8PCw9Pb2PqlHhHAtenBwIBFJVatV24QX13LIAwMDdduChKsAl5enQC2njM1Vt82fcg2muQZfiQanp6d12w4PD0WTvKnr+3Z3dwOtyLr9/X3RJE0Lap3z6Eq2trZq1qKw4+Njt07HzfgCtRcJoZCdnR15DmhBR+KLG0n+SsRp7uvrcx003Ql9nu+YadHz83NxHMeNKicnJxIF7I9K0ksLlXYyhWWzWVdcEBTKx/+Oa5IbJmzHB8G8hbvYVuk0ODgoY2NjdcUFQaEMfeyryC9EJHNbpUcul5OoDA0NKX0PbbaJjGpdFDaKzgFAMbJUqM0/zSxI/LD5wz2wemnmH4kRmN4sLFh1LciTKxNoiQ9LFMdCLSfB+vgs8XDaFRir5L/UBHpZVEk6T8m3HnmQuDMf7eRUc2xoeHCZFHg3s7a2xvzYkjaC8bZxyfn6cX3g3Qwuct4yBEqb4MUm4nchsC3s+o13JcgL5uUZaXT9ZoZ19jq+x1OR1kPXNh8mjoQKJFy0OCUXYOllaRG0Go5jOYhbbPSt0iX6yspKBjf7RS9VzYgabjA4OztbnJ2dbdrfKgm8D9bnNNanheIoBGe85Ms/gzn8VwR3JsrrPDF5hxJl/gHqmono6pFANgAAAABJRU5ErkJggg==') no-repeat center;
  background-size: 100%
}

.user-collapse {
  flex-direction: column;
  width: 69px;
  height: 120px;
}

.user-collapse .txt {
  display: none;
}
</style>
<style>
.el-menu {
  width: 240px;
  margin: 0 8px;
  border-right: 0;
}

.el-menu--collapse {
  width: 50px;
  margin-right: 0;
}

.el-menu--collapse .el-tooltip,
.el-menu--collapse .el-submenu__title {
  display: flex !important;
  align-items: center;
  justify-content: center;
  padding: 0 !important;
}

.el-menu-item.is-active {
  background-color: #BCEDFD3D !important;
}

.el-menu-item,
.el-submenu__title {
  height: 48px;
  line-height: 48px;
}

.el-menu--popup-right-start {
  background-color: #121212 !important;
}
</style>
