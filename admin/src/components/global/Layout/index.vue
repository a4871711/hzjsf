<template>
  <div>
    <el-row
      :gutter="gutter|| 20"
      :justify="justify"
      type="flex"
      v-for="(res,idx) in layoutCols"
      :key="idx"
      class="ces-layout"
    >
      <el-col
        v-for="(item,index) in res"
        :key="index"
        :span="item.span|| 3"
        :offset="item.offset || 0"
        v-html="item.html(that)"
      ></el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  props: {
    that: { type: Object, default: this },
    //start, center, end, space-between, space-around
    justify: {
      type: String,
      default: "start",
    },
    gutter: {
      type: Number,
    },
    layoutCols: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {};
  },
  name: "Layout",
  created() {},
  mounted() {},
  methods: {},
};
</script>

<style scoped>
.ces-layout {
  margin-top: 40px;
  padding: 0 30px;
}
</style>
