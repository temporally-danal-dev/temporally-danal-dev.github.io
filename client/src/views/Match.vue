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
      <div class="header">
        <h2>ANSWERS</h2>
        <div
          style="
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
          "
        >
          <div>
            <strong>Timer: {{ timer }}</strong>
          </div>
        </div>
      </div>
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
      <div class="error-row" v-show="error">{{ errorMsg }}</div>
      <div class="hint-row" v-show="myTurn && hint"></div>
      <div class="first-row">
        <button class="keyboard-button" @click="onKeyboardClick">q</button>
        <button class="keyboard-button" @click="onKeyboardClick">w</button>
        <button class="keyboard-button" @click="onKeyboardClick">e</button>
        <button class="keyboard-button" @click="onKeyboardClick">r</button>
        <button class="keyboard-button" @click="onKeyboardClick">t</button>
        <button class="keyboard-button" @click="onKeyboardClick">y</button>
        <button class="keyboard-button" @click="onKeyboardClick">u</button>
        <button class="keyboard-button" @click="onKeyboardClick">i</button>
        <button class="keyboard-button" @click="onKeyboardClick">o</button>
        <button class="keyboard-button" @click="onKeyboardClick">p</button>
      </div>
      <div class="second-row">
        <button class="keyboard-button" @click="onKeyboardClick">a</button>
        <button class="keyboard-button" @click="onKeyboardClick">s</button>
        <button class="keyboard-button" @click="onKeyboardClick">d</button>
        <button class="keyboard-button" @click="onKeyboardClick">f</button>
        <button class="keyboard-button" @click="onKeyboardClick">g</button>
        <button class="keyboard-button" @click="onKeyboardClick">h</button>
        <button class="keyboard-button" @click="onKeyboardClick">j</button>
        <button class="keyboard-button" @click="onKeyboardClick">k</button>
        <button class="keyboard-button" @click="onKeyboardClick">l</button>
      </div>
      <div class="third-row">
        <button class="keyboard-button" @click="onKeyboardClick">Del</button>
        <button class="keyboard-button" @click="onKeyboardClick">z</button>
        <button class="keyboard-button" @click="onKeyboardClick">x</button>
        <button class="keyboard-button" @click="onKeyboardClick">c</button>
        <button class="keyboard-button" @click="onKeyboardClick">v</button>
        <button class="keyboard-button" @click="onKeyboardClick">b</button>
        <button class="keyboard-button" @click="onKeyboardClick">n</button>
        <button class="keyboard-button" @click="onKeyboardClick">m</button>
        <button class="keyboard-button" @click="onKeyboardClick">Enter</button>
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
      hint: true,
      timer: 0,
      timerId: null,
      error: false,
      errorMsg: "",
    };
  },
  methods: {
    onError(res) {
      const body = JSON.parse(res.body);
      this.erorr = true;
      this.erorrMsg = body.ErrorMsg;
      if (body.ErrorType === "TURN") {
        this.myTurn = false;
      } else if (body.ErrorType === "HINT") {
        this.hint = false;
      } else {
        while (this.currentGuess.length > 0) {
          this.deleteLetter();
        }
        this.myTurn = true;
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
      const who = this.me !== body.nickname ? "Opponent's" : "Your";
      this.timer = 90;
      this.insertBox(who);
      this.insertHint();
    },
    onSubmit(res) {
      const body = JSON.parse(res.body);
      let row = document.getElementsByClassName("letter-row")[this.guessCount];
      const who = this.me === body.nickname ? "Your" : "Opponent's";
      const event = body.timeOut === true ? "timeOut" : "submit";
      this.insertBox(who, event);
      this.timer = 90;
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
    onHint(res) {
      const body = JSON.parse(res.body);
      let row = document.getElementsByClassName("letter-row")[this.guessCount];
      const who = this.me === body.nickname ? "Your" : "Opponent's";
      if (who === "Your") {
        this.hint = false;
      }
      this.insertBox(who, "hint");
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
    },
    onEnd(res) {
      const body = JSON.parse(res.body);
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
            let winner;
            if (body.nickname === this.me || body.nickname === "") {
              winner = "You";
            } else {
              winner = "Opponent";
            }
            alert(
              `The answer was ${body.word.toUpperCase()}, winner is ${winner}`
            );
            clearInterval(this.timerId);
            this.timerId = null;
            this.guessCount += 1;
            this.currentGuess = [];
            this.nextLetter = 0;
            this.stompClient.disconnect();
            this.stompClient = null;
            this.$router.push({ name: "Home" });
          }, delay);
        }
      }
    },
    onConnected() {
      this.stompClient.subscribe(`/sub/${this.roomId}/start`, this.onStart);
      this.stompClient.subscribe(`/sub/${this.roomId}/submit`, this.onSubmit);
      this.stompClient.subscribe(`/sub/${this.roomId}/end`, this.onEnd);
      this.stompClient.subscribe(`/sub/${this.me}/error`, this.onError);
      this.stompClient.subscribe(`/sub/${this.roomId}/hint`, this.onHint);
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
        this.error = true;
        this.errorMsg = "Not enough letters!";
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
      if (row) {
        let box = row.children[this.nextLetter];
        box.textContent = pressedKey;
        box.classList.add("filled-box");
        this.currentGuess.push(pressedKey);
        this.nextLetter += 1;
      }
    },
    deleteLetter() {
      let row = document.getElementsByClassName("letter-row")[this.guessCount];
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
    onKeyboardClick(e) {
      if (this.myTurn === true) {
        let pressedKey = String(e.target.textContent);
        this.error = false;
        this.errorMsg = "";
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
    onHintClick(e) {
      if (this.myTurn === true && this.hint === true) {
        const location = e.target.id;
        const msg = {
          nickname: this.me,
          location: location,
        };
        this.stompClient.send(`/pub/${this.roomId}/hint`, JSON.stringify(msg));
      } else {
        this.error = true;
        this.errorMsg = "you can't use hint";
      }
    },
    insertBox(who, event = "submit") {
      let board = document.getElementById("game-board");
      let nameRow = document.createElement("div");
      if (event === "hint") {
        nameRow.textContent = `${who} hint`;
      } else if (event === "timeout") {
        nameRow.textContent = `${who} time out`;
      } else {
        nameRow.textContent = `${who} answer`;
      }
      let row = document.createElement("div");
      row.className = "letter-row";

      for (let j = 0; j < this.answerLength; j++) {
        let box = document.createElement("div");
        box.className = "letter-box";
        row.appendChild(box);
      }
      board.appendChild(nameRow);
      board.appendChild(row);
      row.scrollIntoView();
    },
    insertHint() {
      const hintRow = document.querySelector(".hint-row");
      for (let i = 0; i < this.answerLength; i++) {
        let button = document.createElement("button");
        button.className = "keyboard-button";
        button.textContent = "hint";
        button.id = i;
        button.addEventListener("click", this.onHintClick);
        hintRow.appendChild(button);
      }
    },
    onKeyup(e) {
      if (this.myTurn === true) {
        let pressedKey = String(e.key);
        this.error = false;
        this.errorMsg = "";
        if (pressedKey === "Backspace" && this.nLetter !== 0) {
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
        }
        if (
          pressedKey.length === 1 &&
          pressedKey.charCodeAt(0) > 64 &&
          pressedKey.charCodeAt(0) < 91
        ) {
          this.insertLetter(pressedKey);
        }
      }
    },
  },
  mounted() {
    this.roomId = sessionStorage.getItem("roomId");
    this.me = sessionStorage.getItem("me");
    this.opponent = sessionStorage.getItem("opponent");
    const socket = new SockJs(
      "https://mighty-basin-66401.herokuapp.com/socket"
    );
    this.stompClient = stomp.over(socket);
    this.stompClient.connect(
      { roomId: this.roomId, nickname: this.me },
      this.onConnected,
      this.onFailed
    );
    window.addEventListener("beforeunload", this.unLoadEvent);
    this.timerId = setInterval(() => {
      if (this.timer > 0) {
        this.timer--;
      }
    }, 1000);
    document.addEventListener("keyup", this.onKeyup);
  },
  beforeDestroy() {
    document.removeEventListener("keyup", this.onKeyup);
  },
  beforeRouteLeave(to, from, next) {
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
    sessionStorage.clear();
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
  height: 530px;
  margin-top: 20px;
  overflow: auto;
}

.hint-row {
  margin: 0.5rem 0;
}

.header {
  display: flex;
  justify-content: space-around;
  width: 100%;
  position: sticky;
  top: 0;
  background-color: white;
  border: solid;
}
</style>
