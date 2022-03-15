<template>
  <div id="game-board">
    <div><h1>Tutorial</h1></div>
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
// @ is an alias to /src
export default {
  name: "Tutorial",
  data() {
    return {
      answer: "abcde",
      count: 0,
      currentGuess: [],
      nextLetter: 0,
      guessCount: 0,
    };
  },
  computed: {
    lowerSubmission() {
      return this.submission.toLowerCase().trim();
    },
  },
  methods: {
    insertLetter(pressedKey) {
      if (this.nextLetter === 5) {
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
    checkGuess() {
      this.insertInput();
      console.log(this.guessCount);
      let row = document.getElementsByClassName("letter-row")[this.guessCount];
      let guessString = "";
      let rightGuess = Array.from(this.answer);

      for (const val of this.currentGuess) {
        guessString += val;
      }

      if (guessString.length != 5) {
        alert("Not enough letters!");
        return;
      }

      for (let i = 0; i < 5; i++) {
        let letterColor = "";
        let box = row.children[i];
        let letter = this.currentGuess[i];

        let letterPosition = rightGuess.indexOf(this.currentGuess[i]);
        // is letter in the correct guess
        if (letterPosition === -1) {
          letterColor = "grey";
        } else {
          // now, letter is definitely in word
          // if letter index and right guess index are the same
          // letter is in the right position
          if (this.currentGuess[i] === rightGuess[i]) {
            // shade green
            letterColor = "green";
          } else {
            // shade box yellow
            letterColor = "yellow";
          }

          rightGuess[letterPosition] = "#";
        }

        let delay = 250 * i;
        setTimeout(() => {
          //shade box
          box.style.backgroundColor = letterColor;
          this.shadeKeyBoard(letter, letterColor);
        }, delay);
      }

      if (guessString === this.answer) {
        alert("You guessed right! Game over!");
        this.guessCount = 0;
        return;
      } else {
        this.guessCount += 1;
        this.currentGuess = [];
        this.nextLetter = 0;
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
      let pressedKey = String(e.target.textContent);
      if (pressedKey === "Del" && this.nextLetter !== 0) {
        this.deleteLetter();
        return;
      }

      if (pressedKey === "Enter") {
        this.checkGuess();
        return;
      }

      let found = pressedKey.match(/[a-z]/gi);
      if (!found || found.length > 1) {
        return;
      } else {
        this.insertLetter(pressedKey);
      }
    },
    insertInput() {
      let board = document.getElementById("game-board");
      let row = document.createElement("div");
      row.className = "letter-row";

      for (let j = 0; j < 5; j++) {
        let box = document.createElement("div");
        box.className = "letter-box";
        row.appendChild(box);
      }

      board.appendChild(row);
    },
  },
  mounted() {
    this.insertInput();
    document.addEventListener("keyup", (e) => {
      let pressedKey = String(e.key);
      if (pressedKey === "Backspace" && this.nextLetter !== 0) {
        this.deleteLetter();
        return;
      }

      if (pressedKey === "Enter") {
        this.checkGuess();
        return;
      }

      let found = pressedKey.match(/[a-z]/gi);
      if (!found || found.length > 1) {
        return;
      } else {
        this.insertLetter(pressedKey);
      }
    });
  },
};
</script>
<style>
h1 {
  text-align: center;
}

#game-board {
  display: flex;
  align-items: center;
  flex-direction: column;
}

.letter-box {
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

.filled-box {
  border: 2px solid black;
}

.letter-row {
  display: flex;
}

#keyboard-cont {
  margin: 1rem 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

#keyboard-cont div {
  display: flex;
}

.second-row {
  margin: 0.5rem 0;
}

.keyboard-button {
  font-size: 1rem;
  font-weight: 700;
  padding: 0.5rem;
  margin: 0 2px;
  cursor: pointer;
  text-transform: uppercase;
}
</style>
