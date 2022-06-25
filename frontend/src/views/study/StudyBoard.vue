<template>
  <section>
    <div class="header">
      <div class="d-flex justify-content-center">
        <font-awesome-icon
          icon="comments"
          class="icon fa-5x"
          style="color: rgb(231, 223, 223)"
        />
        <div>
          <h5 id="massage">
            {{ this.nickname }}님, 관심 있는 스터디를 찾아보세요! <br />
            찾고 있는 스터디가 없다면 직접 모집해보세요!
          </h5>
        </div>
      </div>
    </div>
    <div class="container">
      <h2 style="color: #394e79">스터디 모집 및 찾기</h2>
      <div class="d-flex justify-content-center mb-5">
        <b-form-select
          class="col-2 mt-3 mr-2"
          v-model="selected"
          :options="options"
        ></b-form-select>
        <input
          class="form-control col-7 mr-2"
          @keyup.enter="search"
          v-model="searchWord"
        />
        <b-button
          type="button"
          class="btn mt-3"
          style="border-radius: 0.3rem"
          @click="search"
        >
          search
        </b-button>
      </div>
      <span class="row d-flex justify-content-center">
        <div class="col-9" v-for="study in studies" v-bind:key="study.id">
          <router-link
            :to="{
              name: 'StudyBoardDetail',
              params: { boardno: study.boardno },
            }"
            style="color: black; text-decoration: none"
          >
            <div class="card mb-3 col-12">
              <div class="d-flex">
                <div class="div col-8">
                  <h4 class="mt-3 mb-3">
                    <strong>{{ study.boardname }}</strong>
                  </h4>
                  <p
                    class="category mb-3"
                    :style="{ width: study.category.length + 150 + 'px' }"
                  >
                    {{ study.category }}
                  </p>
                  <div>
                    <b-badge
                      v-for="topic in study.topics"
                      class="mr-2 mb-3"
                      variant="info"
                      :key="topic.id"
                      >{{ topic }}</b-badge
                    >
                  </div>
                </div>
                <div class="col-4">
                  <div class="d-flex">
                    <p v-if="study.userImage" class="mt-2">
                      작성자 :
                      <img :src="study.userImage" alt="default-img" />
                    </p>
                    <p v-else class="mt-2">
                      작성자 :
                      <img src="./../accounts/assets/default.png" />
                    </p>
                    <p class="name">{{ study.nickname }}</p>
                  </div>
                  <p class="mt-2 mb-2">
                    스터디 모집 인원 : {{ study.maxmember }}명
                  </p>
                  <p class="mb-2">스터디 현재 인원 : {{ study.membercnt }}명</p>
                </div>
              </div>
            </div>
          </router-link>
        </div>
      </span>
      <div class="row d-flex justify-content-around overflow-auto mt-5">
        <b-pagination
          :currentPage="currentPage"
          :total-rows="rows"
          @page-click="pageClick"
          :number-of-pages="10"
        ></b-pagination>
      </div>
    </div>
  </section>
</template>

<script>
import axios from "axios";
import jwt_decode from "jwt-decode";

export default {
  name: "StudyBoard",
  data() {
    return {
      selected: "boardname",
      interestList: null,
      rows: 30,
      searchWord: null,
      studies: [],
      currentPage: 1,
      nickname: null,
      options: [
        { value: "boardname", text: "스터디 이름" },
        { value: "nickname", text: "작성자" },
        { value: "category", text: "스터디 카테고리" },
        //{ value: "interests", text: '스터디 주제'},
      ],
    };
  },
  methods: {
    setToken: function () {
      const token = localStorage.getItem("jwt"); // 수정
      const config = {
        Authorization: `JWT ${token}`,
      };
      return config;
    },
    search() {
      //검색합니다.
      var searchword = this.searchWord;
      console.log(searchword);
      axios({
        method: "get",
        url: `${process.env.VUE_APP_SERVER_URL}board/list/search?keyword=${searchword}&type=${this.selected}`,
      })
        .then((res) => {
          console.log(res.data.content);
          console.log(res.data);
          this.studies = res.data;
          for (var i = 0; i < this.studies.length; i++) {
            this.studies[i].topics = this.studies[i].interests.split("#");
            this.studies[i].topics.shift();
          }
          this.currentPage = 1;
        })
        .catch((err) => {
          console.log(this.title, err);
        });
    },
    pageClick(button, pageNum) {
      console.log(pageNum);
      if (this.searchWord) {
        var searchword = this.searchWord;
        console.log(searchword);
        axios({
          method: "get",
          url: `${process.env.VUE_APP_SERVER_URL}board/list/search?keyword=${searchword}&page=${pageNum}&type=${this.selected}`,
        })
          .then((res) => {
            this.studies = res.data;
            for (var i = 0; i < this.studies.length; i++) {
              this.studies[i].topics = this.studies[i].interests.split("#");
              this.studies[i].topics.shift();
            }
          })
          .catch((err) => {
            console.log(this.title, err);
          });
      } else {
        axios({
          method: "get",
          url: `${process.env.VUE_APP_SERVER_URL}board/list?page=${pageNum}&size=8`,
          headers: this.setToken(),
        })
          .then((res) => {
            console.log(res.data.content);

            this.studies = res.data.content;
            for (var i = 0; i < this.studies.length; i++) {
              this.studies[i].topics = this.studies[i].interests.split("#");
              this.studies[i].topics.shift();
              console.log(this.studies);
            }
          })
          .catch((err) => {
            console.log(this.title, err);
          });
      }
    },
  },
  created() {
    axios({
      method: "get",
      url: `${process.env.VUE_APP_SERVER_URL}board/list?page=${1}&size=8`,
    })
      .then((res) => {
        console.log(res.data.content);
        this.studies = res.data.content;
        for (var i = 0; i < this.studies.length; i++) {
          this.studies[i].topics = this.studies[i].interests.split("#");
          this.studies[i].topics.shift();
        }
      })
      .catch((err) => {
        console.log(this.title, err);
      });
    if (localStorage.getItem("jwt")) {
      // 수정
      const token = localStorage.getItem("jwt"); // 수정
      const decoded = jwt_decode(token);
      this.nickname = decoded.nickname;
    } else {
      this.$router.push({ name: "Login" });
    }
  },
};
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Nanum+Gothic&family=Ubuntu:wght@500&display=swap");
.container {
  margin-bottom: 10rem;
}
.header {
  width: 100%;
  height: 150px;
  background-color: #394e79;
}
.div {
  display: flex;
  flex-direction: column;
  text-align: start;
}
h5 {
  color: rgb(231, 223, 223);
  font-family: "Ubuntu", sans-serif;
  margin-top: 3.5rem;
  margin-bottom: 1rem;
}
.icon {
  margin-top: 2rem;
  margin-right: 2rem;
}
h2 {
  color: rgb(231, 223, 223);
  font-family: "Black Han Sans", sans-serif;
  margin-top: 3rem;
  margin-bottom: 1rem;
}
input {
  margin-top: 16px;
}
.card:hover {
  box-shadow: 0 0 50px #c2d2e9;
}
.category {
  text-align: center;
  background-color: rgb(248, 239, 228);
}
img {
  width: 50px;
  height: 50px;
  border-radius: 75%;
}
p {
  margin: 0px;
  text-align: start;
}
.name {
  margin-top: 20px;
  margin-left: 7px;
}
.divv {
  margin-left: 11rem;
}
</style>
