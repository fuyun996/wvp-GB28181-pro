<template>
  <aside class="layout-aside">
    <div class="page-left-menu scrollbarDiv" :class="isCollapse ? 'page-left-menu-collapse' : ''">
      <el-menu router :default-active="activeIndex" background-color="transparent" text-color="#fff"
        active-text-color="#fff" @open="handleOpen" @close="handleClose" :collapse="isCollapse">
        <template v-for="(item, index) in menuList">
          <template v-if="item.child">
            <el-submenu :index="item.url" :key="index">
              <template slot="title">
                <i class="spyfont" :class="item.icon"></i>
                <span class="mtitle">{{ item.name }}</span>
              </template>
              <template v-for="(subItem, inx) in item.child">
                <el-submenu v-if="subItem.child" :index="subItem.url" :key="inx">
                  <template slot="title"><i class="spyfont" :class="subItem.icon"></i><span class="mtitle">{{
                    subItem.title
                  }}</span></template>
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
              <i class="spyfont" :class="item.icon"></i>
              <span class="mtitle">{{ item.name }}</span>
            </el-menu-item>
          </template>
        </template>
        <!-- <el-switch v-model="alarmNotify" active-text="报警信息推送" @change="alarmNotifyChannge"></el-switch>-->
      </el-menu>
    </div>
    <div class="collbtn">
      <i v-if="isCollapse" @click="isCollapse = false" class="el-icon-s-unfold"
        style="font-size: 24px;cursor: pointer;"></i>
      <i v-else @click="isCollapse = true" class="el-icon-s-fold" style="font-size: 24px;cursor: pointer;"></i>
    </div>
  </aside>
</template>

<script>

export default {
  name: "UiHeader",
  components: { Notification },
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
.layout-aside {
  position: relative;
  height: calc(100% - 60px);
  background: #121212, linear-gradient(180deg, #FFFFFF0C 0%, #FFFFFF0C 100%);
}

.collbtn {
  position: absolute;
  right: 0;
  bottom: 0;
  color: #fff;
}

.mtitle {
  margin-left: 6px;
}

.page-left-menu {
  width: 208px;
  height: 100%;
  padding: 8px;
  box-sizing: border-box;
  overflow-x: hidden;
  overflow-y: scroll;
}

.page-left-menu-collapse {
  width: auto;
}
</style>
<style>
.el-menu {
  border-right: 0;
}

.el-menu-item.is-active,
.el-menu-item:focus,
.el-menu-item:hover,
.el-submenu__title:focus,
.el-submenu__title:hover {
  background: #E45252 !important;
}

.el-menu-item,
.el-submenu__title {
  height: 48px;
  line-height: 48px;
}

.el-menu-item i,
.el-submenu__title i {
  color: #fff;
}

.el-menu--collapse {
  width: 42px;
}

.el-menu--collapse .el-menu-item,
.el-menu--collapse .el-submenu__title {
  padding-left: 12px !important
}

.el-tooltip {
  width: 51px !important;
  padding: 0 !important;
  text-align: center;
}
</style>
