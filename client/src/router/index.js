import Vue from "vue";
import VueRouter from "vue-router";
import Home from "@/views/Home.vue";
import Tutorial from "@/views/Tutorial.vue";
import Match from "@/views/Match.vue";

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    name: "Home",
    component: Home,
  },
  {
    path: "/tutorial",
    name: "Tutorial",
    component: Tutorial,
  },
  {
    path: "/match",
    name: "Match",
    component: Match,
  },
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
});

router.beforeEach((to, from, next) => {
  if (to.name === "Match") {
    if (from.name === "Home") {
      next();
    } else {
      next({ name: "Home" });
    }
  } else {
    next();
  }
});
export default router;
