<template>
  <div>
    <div><span></span></div>
    <label for="word"> word : </label>
    <input v-model="word" type="text" id="word" name="word" disabled />
    <button @click="submit">submit</button>
    <div id="game"></div>
  </div>
</template>

<script>
import stomp from "webstomp-client";
import SockJs from "sockjs-client";

// @ is an alias to /src
export default {
  name: "Game",
  data() {
    return {
      answerLength: null,
      word: "",
      myNickname: "",
      opponentNickname: "",
      stompClient: null,
      roomId: "",
    };
  },
  methods: {
    onStart(res) {
      const body = JSON.parse(res.body);
      this.answerLength = body.answerLength;
      const wordInput = document.querySelector("#word");
      if (body.nickname === this.me) {
        wordInput.disabled = false;
        console.log("your turn");
      } else {
        wordInput.disabled = true;
        console.log("opponent turn");
      }
    },
    onSubmit(res) {
      const body = JSON.parse(res.body);
      this.injectInputBox();
      this.setColor(body);
      if (body.nickname === this.me) {
        wordInput.disabled = true;
        console.log("opponent turn");
      } else {
        wordInput.disabled = false;
        console.log("your turn");
      }
    },
    onEnd(res) {
      const body = JSON.parse(res.body);
      console.log(`answer was ${body.word}`);
      console.log(`winner is ${body.nickname}`);
      this.stompClient.disconnect();
      this.stompClient = null;
    },
    onConnected() {
      this.stompClient.subscribe(`/sub/${this.roomId}/start`, this.onStart);
      this.stompClient.subscribe(`/sub/${this.roomId}/submit`, this.onSubmit);
      this.stompClient.subscribe(`/sub/${this.roomId}/end`, this.onEnd);
      const msg = {
        nickname: this.me,
      };
      this.stompClient.send(
        `/pub/${this.roomId}/join`,
        JSON.stringify(msg),
        {}
      );
    },
    onFailed(error) {
      console.log("socket connection failed");
      console.log(error);
    },
    injectInputBox() {
      let template;
      template =
        "<div>" +
        "<br>" +
        '<input class="input boxInput" disabled>'.repeat(this.answerLength) +
        "</div>";
      document.querySelector("#game").insertAdjacentHTML("beforeend", template);
    },
    setColor(body) {
      const inputs = document.querySelectorAll(".input");
      for (let i = 0; i < matchStatus.length; i++) {
        inputs[i].value = body.word[i];
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
    submit() {
      const msg = {
        nickname: this.me,
        word: this.word,
      };
      this.stompClient.send(`/pub/${roomId}/submit`, JSON.stringify(msg));
    },
  },
  beforeMount() {
    this.roomId = sessionStorage.getItem("roomId");
    this.me = sessionStorage.getItem("me");
    this.opponent = sessionStorage.getItem("opponent");
    const socket = new SockJs("socketUrl");
    this.stompClient = stomp.over(socket);
    this.stompClient.connect({}, this.onConnected, this.onFailed);
  },
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
