<template>
  <div>
    <h4 class="mb-3" style="font-weight: bold; font-size: 1.3rem">
      미완료 과제 목록
    </h4>
    <div v-for="homework in onHomeworkList" :key="homework.id">
      <p>{{ homework.homework.title }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "MainUndoHomework",
  props: {
    onHomeworkList: {
      type: Array,
    },
  },
  data() {
    return {};
  },
  methods: {},
  computed: {},
};
</script>
