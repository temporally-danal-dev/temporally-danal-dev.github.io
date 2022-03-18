<template>
  <div>
    <div class="title-row">
      <div class="title-box" style="background-color: yellow">M</div>
      <div class="title-box" style="background-color: yellow">A</div>
      <div class="title-box" style="background-color: green">T</div>
      <div class="title-box" style="background-color: green">C</div>
      <div class="title-box" style="background-color: yellow">H</div>
    </div>
    <div id="game-board">
      <h2>ANSWERS</h2>
    </div>
    <div
      v-if="myTurn === true"
      class="turn-box"
      style="background-color: green"
    >
      <strong> Your Turn </strong>
    </div>
    <div v-else class="turn-box" style="background-color: grey">
      <strong> Opponent Turn </strong>
    </div>
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
  name: "Match",
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
    onError(res) {
      const body = JSON.parse(res.body);
      alert(body.ErrorMsg);
      if (body.ErrorType === "TURN") {
        this.myTurn = false;
      } else {
        while (this.currentGuess.length > 0) {
          this.deleteLetter();
        }
      }
    },
    onStart(res) {
      const body = JSON.parse(res.body);
      this.answerLength = body.answerLength;
      if (body.nickname === this.me) {
        this.myTurn = true;
      } else {
        this.myTurn = false;
      }
      const who = this.me !== body.nickname ? "opponent's" : "your";
      this.insertBox(who);
    },
    onSubmit(res) {
      const body = JSON.parse(res.body);
      let row = document.getElementsByClassName("letter-row")[this.guessCount];
      const who = this.me === body.nickname ? "your" : "opponent's";
      this.insertBox(who);
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
            alert(
              `The answer was ${body.word.toUpperCase()}, winner is ${
                body.nickname === this.me ? "You" : "Opponent"
              }`
            );
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
      this.stompClient.subscribe(`/sub/${this.me}/error`, this.onError);
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

      if (guessString.length !== this.answerLength) {
        console.log(guessString);
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

      let row = document.getElementsByClassName("letter-row")[this.gCount];
      if (row) {
        let box = row.children[this.nextLetter];
        box.textContent = pressedKey;
        box.classList.add("filled-box");
        this.currentGuess.push(pressedKey);
        this.nextLetter += 1;
      }
    },
    deleteLetter() {
      let row = document.getElementsByClassName("letter-row")[this.gCount];
      if (row) {
        let box = row.children[this.nextLetter - 1];
        box.textContent = "";
        box.classList.remove("filled-box");
        this.currentGuess.pop();
        this.nextLetter -= 1;
      }
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

        if (
          pressedKey.length === 1 &&
          pressedKey.charCodeAt(0) > 96 &&
          pressedKey.charCodeAt(0) < 123
        ) {
          this.insertLetter(pressedKey);
        } else {
          return;
        }
      }
    },
    insertBox(who) {
      let board = document.getElementById("game-board");
      let nameRow = document.createElement("div");
      nameRow.textContent = `${who} answer`;
      let row = document.createElement("div");
      row.className = "letter-row";

      for (let j = 0; j < this.answerLength; j++) {
        let box = document.createElement("div");
        box.className = "letter-box";
        row.appendChild(box);
      }
      board.appendChild(nameRow);
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
      { roomId: this.roomId, nickname: this.me },
      this.onConnected,
      this.onFailed
    );
    window.addEventListener("beforeunload", this.unLoadEvent);
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
        if (
          pressedKey.length === 1 &&
          pressedKey.charCodeAt(0) > 96 &&
          pressedKey.charCodeAt(0) < 123
        ) {
          this.insertLetter(pressedKey);
        } else {
          return;
        }
      }
    });
  },
  beforeRouteLeave(to, from, next) {
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
    next();
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

.turn-box {
  border: 2px solid gray;
  border-radius: 3px;
  margin: 2px;
  font-size: 2.5rem;
  font-weight: 700;
  height: auto;
  width: auto;
  display: flex;
  justify-content: center;
  align-items: center;
  text-transform: uppercase;
}

#game-board {
  display: flex;
  align-items: center;
  flex-direction: column;
  border: solid;
  height: 600px;
  margin-top: 20px;
}
</style>
