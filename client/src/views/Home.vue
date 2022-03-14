<template>
  <div id="home">
    <h1>wordle battle</h1>
    <div>
      <router-link to="/tutorial">Tutorial</router-link>
    </div>
    <div>
      <button @click="fetchMatch">Match</button>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import axios from "axios";

export default {
  name: "Home",
  methods: {
    injectWaiting() {
      const waiting = document.querySelector("h2");
      if (waiting === null) {
        let template;
        template = "<h2>matching progressing...</h2>";
        document
          .querySelector("#home")
          .insertAdjacentHTML("beforeend", template);
      }
    },
    fetchMatch() {
      this.injectWaiting();
      axios({
        method: "POST",
        url: "mainUrl/matching",
      })
        .then((response) => {
          sessionStorage.setItem("me", response.data.me);
          sessionStorage.setItem("roomId", response.data.roomId);
          sessionStorage.setItem("opponent", response.data.opponent);
          this.$router.push({ name: "Match" });
        })
        .catch((error) => {
          console.log(error);
          alert("can not make it");
        });
    },
  },
};
</script>
