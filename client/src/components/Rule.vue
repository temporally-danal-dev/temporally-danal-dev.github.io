<template>
  <div class="nav">
    <button type="button" @click="onClick">Rule</button>
    <div class="modal" v-if="show">
      <div class="modalBody" style="text-align: left">
        <button @click="onClick" class="closeBtn">X</button>
        <div class="header">
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
            <li>If you send answer earlier then your opponent, you win.</li>
          </ul>
          <h2>Ingame Rule</h2>
          <ul>
            <li>
              Letter fited perfect will be painted
              <span style="color: green">Green</span>
            </li>
            <li>
              Letter included in answer will be painted
              <span style="color: yellow">Yellow</span>
            </li>
            <li>
              Else, letter will be painted <span style="color: grey">Grey</span>
            </li>
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
