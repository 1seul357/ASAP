<template>
  <div class="homeworkList">
    <h5 class="d-flex justify-content-start mb-1">
      <strong>과제 목록</strong>
    </h5>
    <p class="d-flex justify-content-start mb-0">
      미완료 과제 : {{ ingHomework }}개
    </p>
    <p class="d-flex justify-content-start mb-3">
      완료한 과제 : {{ endHomework }}개
    </p>
    <b-table
      id="my-table"
      :items="items"
      :per-page="perPage"
      :current-page="currentPage"
      small
    >
    </b-table>

    <b-pagination
      class="justify-content-center"
      v-model="currentPage"
      :total-rows="rows"
      :per-page="perPage"
      aria-controls="my-table"
    ></b-pagination>
  </div>
</template>

<script>
import moment from "moment";

export default {
  name: "UserInfoTable",
  data() {
    return {
      perPage: 5,
      currentPage: 1,
      items: [],
      endHomework: 0,
      ingHomework: 0,
    };
  },
  props: {
    homeworkList: {
      type: Array,
    },
    endHomeworkList: {
      type: Array,
    },
  },
  methods: {
    dateTime(value) {
      return moment(value).format("YYYY-MM-DD");
    },
  },
  computed: {
    rows() {
      return this.homeworkList.length + this.endHomeworkList.length;
    },
  },
  created() {
    for (var i = 0; i < this.homeworkList.length; i++) {
      this.items.push({
        스터디: this.homeworkList[j].studyname,
        과제: this.homeworkList[i].title,
        제출기한: this.dateTime(this.homeworkList[i].endDate),
        진행상태: "진행",
      });
      this.ingHomework += 1;
    }
    for (var j = 0; j < this.endHomeworkList.length; j++) {
      this.items.push({
        스터디: this.endHomeworkList[j].studyname,
        과제: this.endHomeworkList[j].title,
        제출기한: this.dateTime(this.endHomeworkList[j].endDate),
        진행상태: "완료",
      });
      this.endHomework += 1;
    }
  },
};
</script>

<style scoped>
#th {
  background-color: #e2e0e6;
}
h5 {
  margin: 0px;
}
.homeworkList {
  display: flex;
  flex-direction: column;
  margin-top: 7rem;
}
</style>
