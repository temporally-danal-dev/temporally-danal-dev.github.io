<template>
  <div id="home">
    <div class="title-row">
      <div class="title-box" style="background-color: yellow">W</div>
      <div class="title-box" style="background-color: grey">O</div>
      <div class="title-box" style="background-color: green">R</div>
      <div class="title-box" style="background-color: yellow">D</div>
      <div class="title-box" style="background-color: yellow">L</div>
      <div class="title-box" style="background-color: grey">E</div>
    </div>
    <div class="title-row">
      <div class="title-box" style="background-color: grey">B</div>
      <div class="title-box" style="background-color: yellow">A</div>
      <div class="title-box" style="background-color: grey">T</div>
      <div class="title-box" style="background-color: grey">T</div>
      <div class="title-box" style="background-color: green">L</div>
      <div class="title-box" style="background-color: green">E</div>
    </div>
    <div style="margin-top: 100px">
      <button @click="goTutorial" class="redirectBtn">Tutorial</button>
    </div>
    <div style="margin-top: 20px">
      <button @click="fetchMatch" class="redirectBtn">Match</button>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import axios from "axios";

export default {
  name: "Home",
  methods: {
    insertWaiting() {
      const waiting = document.querySelector("h2");
      if (waiting === null) {
        let template;
        template = "<h2>matching progressing...</h2>";
        document
          .querySelector("#home")
          .insertAdjacentHTML("beforeend", template);
      }
    },
    goTutorial() {
      this.$router.push({ name: "Tutorial" });
    },
    fetchMatch() {
      this.insertWaiting();
      axios({
        method: "GET",
        url: "http://10.10.1.84:8080/matching",
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
<style>
.redirectBtn {
  width: 100px;
  height: 50px;
  font-size: 20px;
}

.title-row {
  display: flex;
}

.title-box {
  border: 2px solid gray;
  border-radius: 3px;
  margin: 2px;
  font-size: 2.5rem;
  font-weight: 700;
  height: 3rem;
  width: 3rem;
  display: flex;
  justify-content: center;
  align-items: center;
  text-transform: uppercase;
}
</style>
