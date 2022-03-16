<template>
  <div id="app">
    <Rule />
    <div style="display: flex; justify-content: center; margin-top: 50px">
      <router-view />
    </div>
  </div>
</template>

<script>
import Rule from "@/components/Rule.vue";

export default {
  name: "App",
  components: {
    Rule,
  },
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
</style>
