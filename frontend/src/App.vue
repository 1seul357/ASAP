<template>
  <div id="app">
    <div id="nav">
      <span v-if="isLogin">
        <ul class="nav-container">
          <div>
            <li class="nav-title">ASAP</li>
          </div>
          <div class="d-flex">
            <li class="nav-item">
              <router-link :to="{ name: 'Main' }">MYSTUDY</router-link>
            </li>
            <li class="nav-item">
              <router-link :to="{ name: 'MyPage' }">MYPAGE</router-link>
            </li>
            <li class="nav-item">
              <router-link :to="{ name: 'StudyBoard' }">STUDYBOARD</router-link>
            </li>
            <li class="nav-item">
              <router-link :to="{ name: 'Admin' }">ADMIN</router-link>
            </li>
            <li class="nav-item">
              <router-link @click.native="logout" to="#">LOGOUT</router-link>
            </li>
          </div>
        </ul>
      </span>
      <span v-else>
        <ul class="nav-container">
          <div>
            <li class="nav-title">ASAP</li>
          </div>
          <div class="d-flex">
            <li class="nav-item">
              <router-link :to="{ name: 'Register' }">SIGNUP</router-link>
            </li>
            <li class="nav-item">
              <router-link :to="{ name: 'Login' }">LOGIN</router-link>
            </li>
          </div>
        </ul>
      </span>
    </div>
    <router-view @login="isLogin = true" />
  </div>
</template>

<script>
export default {
  name: "App",
  data: function () {
    return {
      isLogin: false,
    };
  },
  methods: {
    logout: function () {
      this.isLogin = false;
      localStorage.removeItem("jwt"); // 토큰 값 삭제
      this.$router.push({ name: "Login" });
    },
  },
  created: function () {
    const token = localStorage.getItem("jwt");
    if (token) {
      this.isLogin = true;
    }
  },
};
</script>

<style>
@import url("https://fonts.googleapis.com/css2?family=Black+Han+Sans&family=Do+Hyeon&family=Nanum+Gothic:wght@400;700;800&display=swap");
@import url("https://fonts.googleapis.com/css2?family=Nanum+Pen+Script&display=swap");

#app {
  font-family: "Nanum Gothic", sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  min-height: 100vh;
}
a {
  color: #c2d2e9;
}
#asap {
  font-size: 30px;
  font-family: "Black Han Sans", sans-serif;
  background-color: rgb(rgba(143, 140, 140, 0.932), green, blue);
}
.nav-container {
  display: flex;
  justify-content: space-between;
  flex-direction: row;
  width: 100%;
  margin: 0;
  padding: 0;
  background-color: white;
  list-style-type: none;
  top: 0;
  box-shadow: 2px 2px 3px #bdbdbd;
}
.nav-title {
  font-family: "Black Han Sans", sans-serif;
  font-size: 25px;
  font-weight: 600;
  padding: 15px 30px;
}
.nav-item {
  padding: 20px 30px 0 0;
}
.nav-item a {
  color: black;
}
</style>
