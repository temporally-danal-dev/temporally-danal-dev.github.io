<template>
  <div>
    <div><span></span></div>
    <label for="word"> word : </label>
    <input v-model="word" type="text" id="word" name="word" />
    <button @click="submit">submit</button>
    <div id="game"></div>
  </div>
</template>

<script>
// import stomp from "webstomp-client";
// import SockJs from "sockjs-client";

// @ is an alias to /src
export default {
  name: "Game",
  data() {
    return {
      answerLength: null,
      word: "",
    };
  },
  computed: {
    lowerWord() {
      return this.word.toLowerCase().trim();
    },
  },
  methods: {
    injectInputBox() {
      let template;
      template =
        "<div>" +
        "<br>" +
        '<input class="input boxInput" disabled>'.repeat(this.answerLength) +
        "</div>";
      document.querySelector("#game").insertAdjacentHTML("beforeend", template);
    },
    setColor(matchStatus) {
      const inputs = document.querySelectorAll(".input");
      for (let i = 0; i < matchStatus.length; i++) {
        if (matchStatus[i] === "2") {
          inputs[i].style.background = "green";
        } else if (matchStatus[i] === "1") {
          inputs[i].style.background = "yellow";
        } else {
          inputs[i].style.background = "lightgrey";
        }
        inputs[i].classList.remove("input");
      }
    },
    submit() {},
  },
  beforeMount() {},
};
</script>
<style>
.boxInput {
  width: 50px;
  height: 50px;
  font-size: 40px;
  text-align: center;
}
</style>
