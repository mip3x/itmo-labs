<template>
  <div class="main-view">
    <div class="container">
      <PlotArea />
      <div class="panel-gap"></div>
      <PointForm />
      <ResultsTable />
    </div>
  </div>
</template>

<script>
import PlotArea from '../components/PlotArea.vue';
import PointForm from '../components/PointForm.vue';
import ResultsTable from '../components/ResultsTable.vue';

export default {
  components: { PlotArea, PointForm, ResultsTable },
  beforeRouteEnter(to, from, next) {
    document.body.classList.add('main-view-body');
    next();
  },
  beforeRouteLeave(to, from, next) {
    document.body.classList.remove('main-view-body');
    next();
  },
};
</script>

<style scoped>
@import '../styles/main.css';
</style>
