<template>
  <div>
    <div id="container">
      <div
        class="content"
        :style="{ height: assignment.homework.content.length + 500 + 'px' }"
      >
        <h5>
          <strong>작성자 : {{ assignment.nickname }}</strong>
        </h5>
        <div class="overflow" style="width: 100%; white-space: pre-line">
          {{ assignment.homework.content }}
        </div>
        <p class="p">
          <b-form-file
            ref="file"
            method="post"
            enctype="multipart/form-data"
            class="mt-3"
          >
          </b-form-file>
          <b-button
            class="mt-2"
            @click="uploadFile()"
            style="font-size: 8px; background-color: skyblue; border: none"
            >Add</b-button
          >
        </p>
        <h5><strong>과제 제출 리스트</strong></h5>
        <p>
          <font-awesome-icon icon="fa-solid fa-clock" class="fa-xl" /> 제출 기한
          : {{ assignment.homework.endDate.split(" ")[0] }}
        </p>
        <div v-for="userHomework in userHomeworkList" :key="userHomework.id">
          <div v-if="userHomework.userHomewrok.filepath">
            <a
              style="width: 100%; white-space: normal"
              type="button"
              @click="downloadFile(userHomework.userHomewrok)"
            >
              <p>
                {{ userHomework.nickname }} -
                {{ userHomework.userHomewrok.ogfilename }}
              </p>
            </a>
            <a
              type="button"
              v-if="
                userHomework.userHomewrok.filepath &&
                userHomework.userHomewrok.userno == userno
              "
              @click="deleteFile(userHomework.userHomewrok.userhomeworkno)"
              style="color: red"
              >X</a
            >
          </div>
        </div>
      </div>
      <div class="mt-2 mb-3">
        <b-button
          v-if="userno == homeworkUserno"
          @click="deleteHomework(assignment.homework.homeworkno)"
          >삭제</b-button
        >
        <b-button
          v-if="userno == homeworkUserno"
          id="show-btn"
          @click="showModal(assignment.homework)"
          >수정</b-button
        >
      </div>
      <b-modal ref="my-modal" ok-only title="Update Homework" hide-footer>
        <form>
          <b-form-group label="Title" label-for="title">
            <input
              id="title"
              type="text"
              v-model="homework.title"
              required
              class="form-control"
            />
          </b-form-group>
          <b-form-group label="Content" label-for="content">
            <b-form-textarea
              id="content"
              type="text"
              v-model="homework.content"
              maxlength="100"
              rows="6"
              max-rows="6"
              required
            >
            </b-form-textarea>
          </b-form-group>
          <b-form-group label="Date" label-for="content">
            <b-form-input id="content" type="date" v-model="homework.endDate">
            </b-form-input>
          </b-form-group>
          <b-button
            type="submit"
            style="float-right; font-size: 15px; height: 40px;"
            @click="updateHomework()"
            >확인</b-button
          >
        </form>
      </b-modal>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import jwt_decode from "jwt-decode";

export default {
  name: "HomeworkItem",
  data: function () {
    return {
      homework: {
        homeworkno: this.assignment.homework.homeworkno,
        title: null,
        content: null,
        endDate: null,
      },
      userno: 0,
      homeworkUserno: this.assignment.homework.userno,
      userHomeworkList: null,
    };
  },
  props: {
    assignment: {
      type: Object,
    },
  },
  methods: {
    setToken: function () {
      const token = sessionStorage.getItem("jwt"); // 수정
      const config = {
        Authorization: `JWT ${token}`,
      };
      return config;
    },
    showModal(homework) {
      (this.homework.title = homework.title),
        (this.homework.content = homework.content),
        (this.homework.endDate = homework.endDate);
      this.$refs["my-modal"].show();
    },
    uploadFile: function () {
      var file = this.$refs["file"].files[0];
      const formData = new FormData();
      formData.append("files", file);
      formData.append("homeworkno", this.homework.homeworkno);
      formData.append("userno", this.userno);
      console.log(formData);
      axios({
        method: "post",
        url: `${process.env.VUE_APP_SERVER_URL}homework/upload`,
        headers: this.setToken(),
        data: formData,
      })
        .then((res) => {
          console.log(res);
          window.location.reload();
        })
        .catch((err) => {
          console.log(err);
        });
    },
    downloadFile: function (file) {
      axios({
        method: "get",
        url: `${process.env.VUE_APP_SERVER_URL}homework/download/${file.userhomeworkno}`,
        headers: this.setToken(),
        responseType: "blob",
      })
        .then((res) => {
          const url = window.URL.createObjectURL(new Blob([res.data]));
          const link = document.createElement("a");
          link.href = url;
          link.setAttribute("download", `${file.ogfilename}`);
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
        })
        .catch((err) => {
          console.log(err);
        });
    },
    deleteFile: function (fileno) {
      axios({
        method: "get",
        url: `${process.env.VUE_APP_SERVER_URL}homework/filedelete/${fileno}`,
      })
        .then((res) => {
          console.log(res.data);
          alert("해당 과제를 삭제하였습니다.");
          window.location.reload();
        })
        .catch((err) => {
          console.log(err);
        });
    },
    deleteHomework: function (homeworkno) {
      axios({
        method: "delete",
        url: `${process.env.VUE_APP_SERVER_URL}homework/delete/${homeworkno}`,
        headers: this.setToken(),
      })
        .then((res) => {
          console.log(res.data);
          alert("게시글이 삭제되었습니다.");
          window.location.reload();
        })
        .catch((err) => {
          console.log(err);
        });
    },
    updateHomework: function () {
      var flagTitle = false;
      var flagContent = false;
      for (var i = 0; i < this.homework.title.length; i++) {
        if (this.homework.title[i] != " ") {
          flagTitle = true;
          break;
        }
      }
      for (var j = 0; j < this.homework.content.length; j++) {
        if (this.homework.content[j] != " ") {
          flagContent = true;
          break;
        }
      }
      if (
        this.homework.title == null ||
        this.homework.content == null ||
        this.homework.title == "" ||
        this.homework.content == "" ||
        this.homework.endDate == null ||
        !flagTitle ||
        !flagContent
      ) {
        alert("모든 입력 칸을 입력해주세요.");
      } else {
        axios({
          method: "put",
          url: `${process.env.VUE_APP_SERVER_URL}homework/modify`,
          data: this.homework,
          headers: this.setToken(),
        })
          .then((res) => {
            console.log(res.data);
            window.location.reload();
          })
          .catch((err) => {
            console.log(err);
          });
      }
    },
    getUserHomeworkList: function () {
      axios({
        method: "get",
        url: `${process.env.VUE_APP_SERVER_URL}homework/uploadList/${this.homework.homeworkno}`,
        headers: this.setToken(),
      })
        .then((res) => {
          console.log(res.data, 1012310);
          this.userHomeworkList = res.data.userHomeworkInfoList;
          console.log(this.userHomeworkList, 111333);
        })
        .catch((err) => {
          console.log(err);
        });
    },
  },
  created() {
    const token = sessionStorage.getItem("jwt"); // 수정
    const decoded = jwt_decode(token);
    const userno = decoded.userno;
    this.userno = userno;
    this.getUserHomeworkList();
    console.log(this.assignment);
  },
};
</script>

<style scoped>
#container {
  display: flex;
  flex-direction: column;
}
.content {
  border: 1px solid #0000004d;
  position: relative;
  background-color: white;
  box-shadow: 5px 5px 5px 0px gray;
  /* word-break: break-all; */
  white-space: pre;
  padding: 20px;
  display: flex;
  flex-direction: column;
  word-break: keep-all;
  word-wrap: break-word;
}
.content:before {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  border-top: 15px solid palevioletred;
  border-left: 15px solid rgba(0, 0, 0, 0);
  width: 0;
}
button {
  float: right;
  margin-right: 2px;
  font-size: 11px;
  height: 34px;
  background-color: rgb(130, 163, 209);
}
.overflow {
  font-size: 15px;
  white-space: pre;
  white-space: normal;
}
button:hover {
  background-color: rgb(79, 138, 216);
}
.p {
  margin: 0px;
}
p {
  font-size: 15px;
}
h5 {
  font-size: 16px;
}
a {
  color: black;
  font-size: 15px;
}
</style>
