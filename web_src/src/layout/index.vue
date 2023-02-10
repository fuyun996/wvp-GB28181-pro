<template>
  <div style="height: 100%">
    <ui-header></ui-header>
    <el-container>
      <ui-aside></ui-aside>
      <transition name="fade">
        <router-view></router-view>
      </transition>
    </el-container>

  </div>
</template>

<script>
import uiHeader from "./UiHeader.vue";
import UiAside from "./UiAside.vue";

export default {
  name: "index",
  components: {
    uiHeader, UiAside
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
