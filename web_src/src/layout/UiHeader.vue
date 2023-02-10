<template>
  <header>
    <div class="layout-header">
      <div class="logobox">
        <img class="logo" src="../../static/images/logo.png" />
        <div class="title">中科曙光视频云</div>
      </div>

      <div class="user-box">
        <el-dropdown>
          <div class="portrait"></div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click="changePassword">修改密码</el-dropdown-item>
            <el-dropdown-item @click="loginout">注销</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <span class="txt">欢迎，{{ this.$cookies.get("session").username }}</span>
      </div>
    </div>

    <changePasswordDialog ref="changePasswordDialog"></changePasswordDialog>
  </header>
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
.layout-header {
  width: 100%;
  height: 60px;
  padding: 0 22px;
  background: #404040;
  box-shadow: 0px 1px 3px 0px #0000001E, 0px 1px 1px 0px #00000023, 0px 2px 1px -1px #00000033;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: space-between;

}

.logobox {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logobox .logo {
  width: auto;
  height: 40px;
}

.logobox .title {
  margin-left: 14px;
  color: #FFFFFF;
  text-shadow: 1px 2px 2px 0px #D540383D;
  font-family: PingFang SC;
  font-weight: bold;
  font-size: 20px;
}

.user-box {
  display: flex !important;
  align-items: center;
  justify-content: flex-end;
}

.user-box .portrait {
  width: 40px;
  height: 40px;
  background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAYAAACM/rhtAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAANuSURBVHgBzZnNTttAFIWvnYCyADVUILFrkgUSbAhdIBYg3LBk1xeo+gTQPkCj9AHawgsUXqDdIxEMYsWKCokFSFWyQwIVV4BA/PYc1474cZyMJyT+JMN4xsMc3Zm5d+5g3N3dGRKB1dVVK5FIjN7c3FiGYeTxvMDfSrMNZQe/Kre3t1WUt1G/PjMzY0sEDBWBm5ub6cvLyzkMOueLaXogw6jgsVOpVGliYqLadL9mBHrCiijOSQswTXOpWaENBZbLZVqsqGqxhgP/t2jJsqzl0O/CBELcV2mR1eoBa36DyI/12gMFelP6E8VpaQPcSF1dXYXJyUnncZsZ1OHq6qosbRJHYKQ8xvwR1PZEIKeVHaTNYEzLtu0vj+sfTDE+eAff9V06y4dCobDgv9QEbmxsZOB0y3jPSGdxuru7c/56rE0xxBVjII6kr6+vP/kvrgVpPVT+lhgBK76kFZN8ofVEA0QFGRkZkZ6eHkkmk3J0dCR7e3tycXEhUWFIxa+SP8WRXQrFjY+PSzqddsWR/v5+t45tGrgBIjE1NfVGNKLF8PCw9Pb2PqlHhHAtenBwIBFJVatV24QX13LIAwMDdduChKsAl5enQC2njM1Vt82fcg2muQZfiQanp6d12w4PD0WTvKnr+3Z3dwOtyLr9/X3RJE0Lap3z6Eq2trZq1qKw4+Njt07HzfgCtRcJoZCdnR15DmhBR+KLG0n+SsRp7uvrcx003Ql9nu+YadHz83NxHMeNKicnJxIF7I9K0ksLlXYyhWWzWVdcEBTKx/+Oa5IbJmzHB8G8hbvYVuk0ODgoY2NjdcUFQaEMfeyryC9EJHNbpUcul5OoDA0NKX0PbbaJjGpdFDaKzgFAMbJUqM0/zSxI/LD5wz2wemnmH4kRmN4sLFh1LciTKxNoiQ9LFMdCLSfB+vgs8XDaFRir5L/UBHpZVEk6T8m3HnmQuDMf7eRUc2xoeHCZFHg3s7a2xvzYkjaC8bZxyfn6cX3g3Qwuct4yBEqb4MUm4nchsC3s+o13JcgL5uUZaXT9ZoZ19jq+x1OR1kPXNh8mjoQKJFy0OCUXYOllaRG0Go5jOYhbbPSt0iX6yspKBjf7RS9VzYgabjA4OztbnJ2dbdrfKgm8D9bnNNanheIoBGe85Ms/gzn8VwR3JsrrPDF5hxJl/gHqmono6pFANgAAAABJRU5ErkJggg==') no-repeat center;
  background-size: 100%;
  cursor: pointer;
}

.user-box .txt {
  margin-left: 20px;
  color: #FFFFFF;
  font-family: Roboto;
  font-size: 16px;
  line-height: 1.5;
  letter-spacing: 0.15px;
  text-align: left;

}
</style>
