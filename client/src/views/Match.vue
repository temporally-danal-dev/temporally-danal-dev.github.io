<template>
  <div id="game-board">
    <div><h1>Match</h1></div>
    <div id="keyboard-cont">
      <div class="first-row">
        <button class="keyboard-button" @click="onClick">q</button>
        <button class="keyboard-button" @click="onClick">w</button>
        <button class="keyboard-button" @click="onClick">e</button>
        <button class="keyboard-button" @click="onClick">r</button>
        <button class="keyboard-button" @click="onClick">t</button>
        <button class="keyboard-button" @click="onClick">y</button>
        <button class="keyboard-button" @click="onClick">u</button>
        <button class="keyboard-button" @click="onClick">i</button>
        <button class="keyboard-button" @click="onClick">o</button>
        <button class="keyboard-button" @click="onClick">p</button>
      </div>
      <div class="second-row">
        <button class="keyboard-button" @click="onClick">a</button>
        <button class="keyboard-button" @click="onClick">s</button>
        <button class="keyboard-button" @click="onClick">d</button>
        <button class="keyboard-button" @click="onClick">f</button>
        <button class="keyboard-button" @click="onClick">g</button>
        <button class="keyboard-button" @click="onClick">h</button>
        <button class="keyboard-button" @click="onClick">j</button>
        <button class="keyboard-button" @click="onClick">k</button>
        <button class="keyboard-button" @click="onClick">l</button>
      </div>
      <div class="third-row">
        <button class="keyboard-button" @click="onClick">Del</button>
        <button class="keyboard-button" @click="onClick">z</button>
        <button class="keyboard-button" @click="onClick">x</button>
        <button class="keyboard-button" @click="onClick">c</button>
        <button class="keyboard-button" @click="onClick">v</button>
        <button class="keyboard-button" @click="onClick">b</button>
        <button class="keyboard-button" @click="onClick">n</button>
        <button class="keyboard-button" @click="onClick">m</button>
        <button class="keyboard-button" @click="onClick">Enter</button>
      </div>
    </div>
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
      myNickname: "",
      opponentNickname: "",
      stompClient: null,
      roomId: "",
      currentGuess: [],
      nextLetter: 0,
      guessCount: 0,
      myTurn: false,
    };
  },
  methods: {
    onStart(res) {
      const body = JSON.parse(res.body);
      this.answerLength = body.answerLength;
      this.insertBox();
      if (body.nickname === this.me) {
        this.myTurn = true;
      } else {
        this.myTurn = false;
      }
    },
    onSubmit(res) {
      const body = JSON.parse(res.body);
      console.log(res.body);
      this.insertBox();
      let row = document.getElementsByClassName("letter-row")[this.guessCount];
      for (let i = 0; i < this.answerLength; i++) {
        let letterColor = "";
        let box = row.children[i];
        let letter = body.word[i];

        // is letter in the correct guess
        if (body.matchStatus[i] === "0") {
          letterColor = "grey";
        } else if (body.matchStatus[i] === "1") {
          letterColor = "yellow";
        } else {
          letterColor = "green";
        }

        let delay = 250 * i;
        setTimeout(() => {
          //shade box
          box.style.backgroundColor = letterColor;
          box.textContent = letter;
          this.shadeKeyBoard(letter, letterColor);
        }, delay);
      }

      this.guessCount += 1;
      this.currentGuess = [];
      this.nextLetter = 0;

      if (body.nickname === this.me) {
        this.myTurn = true;
      } else {
        this.myTurn = false;
      }
    },
    onEnd(res) {
      const body = JSON.parse(res.body);
      this.insertBox();
      let row = document.getElementsByClassName("letter-row")[this.guessCount];
      for (let i = 0; i < this.answerLength + 1; i++) {
        if (i < this.answerLength) {
          let letterColor = "green";
          let box = row.children[i];
          let letter = body.word[i];

          let delay = 250 * i;
          setTimeout(() => {
            //shade box
            box.style.backgroundColor = letterColor;
            box.textContent = letter;
            this.shadeKeyBoard(letter, letterColor);
          }, delay);
        } else {
          let delay = 250 * i;
          setTimeout(() => {
            //shade box
            alert(`The answer was ${body.word}, winner is ${body.nickname}`);
          }, delay);
        }
      }

      this.guessCount += 1;
      this.currentGuess = [];
      this.nextLetter = 0;
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
    submit() {
      let guessString = "";

      for (const val of this.currentGuess) {
        guessString += val;
      }
      console.log(guessString);
      console.log(guessString.length);
      console.log(this.answerLength);
      if (guessString.length !== parseInt(this.answerLength)) {
        alert("Not enough letters!");
        return;
      }

      const msg = {
        nickname: this.me,
        word: guessString,
      };
      this.stompClient.send(`/pub/${this.roomId}/submit`, JSON.stringify(msg));

      this.myTurn = false;
    },
    insertLetter(pressedKey) {
      if (this.nextLetter === this.answerLength) {
        return;
      }
      pressedKey = pressedKey.toLowerCase();

      let row = document.getElementsByClassName("letter-row")[this.guessCount];
      let box = row.children[this.nextLetter];
      box.textContent = pressedKey;
      box.classList.add("filled-box");
      this.currentGuess.push(pressedKey);
      this.nextLetter += 1;
    },
    deleteLetter() {
      let row = document.getElementsByClassName("letter-row")[this.guessCount];
      let box = row.children[this.nextLetter - 1];
      box.textContent = "";
      box.classList.remove("filled-box");
      this.currentGuess.pop();
      this.nextLetter -= 1;
    },
    shadeKeyBoard(letter, color) {
      for (const elem of document.getElementsByClassName("keyboard-button")) {
        if (elem.textContent === letter) {
          let oldColor = elem.style.backgroundColor;
          if (oldColor === "green") {
            return;
          }

          if (oldColor === "yellow" && color !== "green") {
            return;
          }

          elem.style.backgroundColor = color;
          break;
        }
      }
    },
    onClick(e) {
      if (this.myTurn === true) {
        let pressedKey = String(e.target.textContent);
        if (pressedKey === "Del" && this.nextLetter !== 0) {
          this.deleteLetter();
          return;
        }

        if (pressedKey === "Enter") {
          this.submit();
          return;
        }

        let found = pressedKey.match(/[a-z]/gi);
        if (!found || found.length > 1) {
          return;
        } else {
          this.insertLetter(pressedKey);
        }
      }
    },
    insertBox() {
      let board = document.getElementById("game-board");
      let row = document.createElement("div");
      row.className = "letter-row";

      for (let j = 0; j < this.answerLength; j++) {
        let box = document.createElement("div");
        box.className = "letter-box";
        row.appendChild(box);
      }

      board.appendChild(row);
    },
  },
  mounted() {
    this.roomId = sessionStorage.getItem("roomId");
    this.me = sessionStorage.getItem("me");
    this.opponent = sessionStorage.getItem("opponent");
    const socket = new SockJs("http://localhost:8080/socket");
    this.stompClient = stomp.over(socket);
    this.stompClient.connect(
      { roomId: this.roomId },
      this.onConnected,
      this.onFailed
    );

    document.addEventListener("keyup", (e) => {
      if (this.myTurn === true) {
        let pressedKey = String(e.key);
        if (pressedKey === "Backspace" && this.nextLetter !== 0) {
          this.deleteLetter();
          return;
        }

        if (pressedKey === "Enter") {
          this.submit();
          return;
        }

        let found = pressedKey.match(/[a-z]/gi);
        if (!found || found.length > 1) {
          return;
        } else {
          this.insertLetter(pressedKey);
        }
      }
    });
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
