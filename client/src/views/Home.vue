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
      <button @click="goTutorial" class="redirectBtn btn btn-secondary">
        Tutorial
      </button>
    </div>
    <div style="margin-top: 20px">
      <button @click="fetchMatch" class="redirectBtn btn btn-secondary">
        Match
      </button>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import axios from "axios";

export default {
  name: "Home",
  data() {
    return {
      key: "",
      matched: false,
    };
  },
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
      this.matched = true;
      axios({
        method: "GET",
        url: `http://localhost:8080/matching/${this.key}`,
      })
        .then((response) => {
          const waiting = document.querySelector("h2");
          if (response.data.responseResult === "TIMEOUT") {
            alert("timed out");
            document.querySelector("#home").removeChild(waiting);
          } else if (response.data.responseResult === "CANCEL") {
            alert("canceled");
            document.querySelector("#home").removeChild(waiting);
          } else {
            sessionStorage.setItem("me", response.data.me);
            sessionStorage.setItem("roomId", response.data.roomId);
            sessionStorage.setItem("opponent", response.data.opponent);
            this.$router.push({ name: "Match" });
          }
        })
        .catch((error) => {
          console.log(error);
          alert("can not make it");
          const waiting = document.querySelector("h2");
          document.querySelector("#home").removeChild(waiting);
        });
      for (let i = 0; i < 60; i++) {
        let delay = 500 * i;
        const waiting = document.querySelector("h2");
        setTimeout(() => {
          waiting.textContent = "matching progressing" + ".".repeat(i % 4);
        }, delay);
      }
    },
    unLoadEvent(event) {
      event.preventDefault();
      event.returnValue = "";
      if (this.matched) {
        axios({
          method: "GET",
          url: `http://localhost:8080/delete/${this.key}`,
        })
          .then((response) => {
            console.log(response);
          })
          .catch((error) => {
            console.log(error);
          });
      }
    },
  },
  beforeMount() {
    this.key = Math.floor(Math.random() * (999999 - 100000)) + 100000;
    window.addEventListener("beforeunload", this.unLoadEvent);
  },
  beforeDestroy() {
    window.removeEventListener("beforeunload", this.unLoadEvent);
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

.btn {
  display: inline-block;
  font-weight: 400;
  line-height: 1.5;
  color: #212529;
  text-align: center;
  text-decoration: none;
  vertical-align: middle;
  cursor: pointer;
  -webkit-user-select: none;
  -moz-user-select: none;
  user-select: none;
  background-color: transparent;
  border: 1px solid transparent;
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  border-radius: 0.25rem;
  transition: color 0.15s ease-in-out, background-color 0.15s ease-in-out,
    border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}

.btn-secondary {
  color: #fff;
  background-color: #6c757d;
  border-color: #6c757d;
}
.btn-secondary:hover {
  color: #fff;
  background-color: #5c636a;
  border-color: #565e64;
}
.btn-check:focus + .btn-secondary,
.btn-secondary:focus {
  color: #fff;
  background-color: #5c636a;
  border-color: #565e64;
  box-shadow: 0 0 0 0.25rem rgba(130, 138, 145, 0.5);
}
.btn-check:checked + .btn-secondary,
.btn-check:active + .btn-secondary,
.btn-secondary:active,
.btn-secondary.active,
.show > .btn-secondary.dropdown-toggle {
  color: #fff;
  background-color: #565e64;
  border-color: #51585e;
}
.btn-check:checked + .btn-secondary:focus,
.btn-check:active + .btn-secondary:focus,
.btn-secondary:active:focus,
.btn-secondary.active:focus,
.show > .btn-secondary.dropdown-toggle:focus {
  box-shadow: 0 0 0 0.25rem rgba(130, 138, 145, 0.5);
}
.btn-secondary:disabled,
.btn-secondary.disabled {
  color: #fff;
  background-color: #6c757d;
  border-color: #6c757d;
}
</style>
