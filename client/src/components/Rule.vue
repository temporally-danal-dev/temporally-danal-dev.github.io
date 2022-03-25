<template>
  <div class="nav">
    <button type="button" @click="onClick">Rule</button>
    <div class="modal" v-if="show">
      <div class="modalBody" style="text-align: left; padding: 0.5rem">
        <button @click="onClick" class="closeBtn">X</button>
        <div>
          <h1>Rule</h1>
        </div>
        <hr />
        <div class="body">
          <h2>Matching Rule</h2>
          <ul>
            <li>Basically matching is <strong>Random</strong>.</li>
            <li>
              Decision about who is the first is also
              <strong>Random</strong> too.
            </li>
            <li>
              If you send correct answer earlier,<br />
              you win.
            </li>
          </ul>
          <h2>Ingame Rule</h2>
          <ul>
            <li>Each turn has 90 seconds</li>
            <li>
              If you don't submit your answer in time, <br />your answer will be
              generated <strong>randomly</strong>
            </li>
            <li>
              Letter fited perfect will be painted
              <span style="color: green">Green</span>
            </li>
            <li>
              Letter included in answer will be painted
              <span style="color: yellow">Yellow</span>
            </li>
            <li>
              But if the number of letter in your submit is over<br />the number
              of letter included in answer, <br />
              Over letters will be painted <span style="color: grey">Grey</span>
            </li>
            <li>
              Else, letter not included in answer will be painted
              <span style="color: grey">Grey</span>
            </li>
          </ul>
          <h2>Hint</h2>
          <ul>
            <li>You can use your Hint one time in a match</li>
            <li>
              If you hava a chance to use hint, <br />then hint buttons will be
              displayed
            </li>
            <li>
              When you click hint button,<br />you can see which letter is in
              your choice
            </li>
            <li>This hint will be opened to both players</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Rule",
  data() {
    return {
      show: false,
    };
  },
  methods: {
    onClick() {
      if (this.show === true) {
        this.show = false;
      } else {
        this.show = true;
      }
    },
  },
};
</script>

<style>
.nav {
  display: flex;
  top: 20px;
  left: 20px;
}

.modal {
  position: absolute;
  top: auto;
  right: auto;
  width: auto;
  height: auto;
  z-index: 1;
}

.modalBody {
  background-color: white;
  border: solid;
}

.closeBtn {
  position: fixed;
  display: inline;
  border: 0;
  outline: 0;
  background-color: white;
}
</style>
