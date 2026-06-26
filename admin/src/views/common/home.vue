<template>
  <div>
    <div
      style="
        text-align: center;
        font-size: 50px;
        font-weight: bold;
        line-height: 500px;
      ">
      矢历运动管理系统
    </div>
  </div>
</template>

<script>
import * as echarts from "echarts";
export default {
  name: "home",
  data() {
    return {
      
    };
  },
  computed: {
    role() {
      return localStorage.role;
    },
  },
  created() {
    
  },
  mounted() {},
  methods: {
    
  },
};
</script>

<style scoped lang="scss">

</style>
