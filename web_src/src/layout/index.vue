<template>
  <el-container style="height: 100%">
    <div class="layout-aside">
      <ui-header></ui-header>
    </div>
    <el-main>
      <el-container>
        <transition name="fade">
          <router-view></router-view>
        </transition>
      </el-container>
    </el-main>
  </el-container>
</template>

<script>
import uiHeader from "./UiHeader.vue";

export default {
  name: "index",
  components: {
    uiHeader
  },
}
</script>
<style scoped>
.layout-aside {
  background: #121212;
}

.fade-enter {
  visibility: hidden;
  opacity: 0;
}

.fade-leave-to {
  display: none;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity .5s ease;
}

.fade-enter-to,
.fade-leave {
  visibility: visible;
  opacity: 1;
}
</style>
