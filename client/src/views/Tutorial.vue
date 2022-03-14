<template>
  <div>
    <div><span></span></div>
    <label for="submission"> submission : </label>
    <input v-model="submission" type="text" id="submission" name="submission" />
    <button @click="submit">submit</button>
    <div id="game"></div>
  </div>
</template>

<script>
// @ is an alias to /src
export default {
  name: "Game",
  data() {
    return {
      answer: "abcde",
      submission: "",
      count: 0,
    };
  },
  computed: {
    lowerSubmission() {
      return this.submission.toLowerCase().trim();
    },
  },
  methods: {
    injectInputBox() {
      let template;
      template =
        "<div>" +
        "<br>" +
        '<input class="input boxInput" disabled>'.repeat(this.answer.length) +
        "</div>";
      document.querySelector("#game").insertAdjacentHTML("beforeend", template);
    },
    setColor() {
      const inputs = document.querySelectorAll(".input");
      for (let i = 0; i < this.answer.length; i++) {
        inputs[i].value = this.lowerSubmission[i];
        if (inputs[i].value === this.answer[i]) {
          inputs[i].style.background = "green";
        } else if (this.answer.includes(inputs[i].value)) {
          inputs[i].style.background = "yellow";
        } else {
          inputs[i].style.background = "lightgrey";
        }
        inputs[i].classList.remove("input");
      }
    },
    equalToAnswer() {
      if (this.answer === this.lowerSubmission) {
        alert("you win");
        const gameDiv = document.querySelector("#game");
        while (gameDiv.hasChildNodes()) {
          gameDiv.removeChild(gameDiv.firstChild);
        }
      }
    },
    submit() {
      this.injectInputBox();
      this.setColor();
      this.equalToAnswer();
      this.submission = "";
      this.count++;
    },
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
