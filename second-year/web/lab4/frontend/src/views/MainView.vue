<template>
  <div class="main-view">
    <button class="logout-button" @click="logout">Выйти</button>
    <div class="container">
      <PlotArea :radius="radius" :points="points" @add-point="addPoint" />
      <div class="panel-gap"></div>
      <div>
        <PointForm @add-point="addPoint" @update-radius="updateRadius" />
        <ResultsTable :points="points" />
      </div>
    </div>
  </div>
</template>

<script>
import PlotArea from '../components/PlotArea.vue';
import PointForm from '../components/PointForm.vue';
import ResultsTable from '../components/ResultsTable.vue';

export default {
  components: { PlotArea, PointForm, ResultsTable },

  data() {
    return {
      radius: 100,
      points: [],
    };
  },

  methods: {
    logout() {
      localStorage.removeItem('authToken');
      this.$router.push('/');
    },

    addPoint(point) {
      this.points.push(point);
    },

    updateRadius(newRadius) {
      this.radius = newRadius;
    },
  },

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

<style>
@import '../styles/main.css';

.logout-button {
  position: absolute;
  top: 10px;
  right: 20px;
  padding: 10px 15px;
  font-size: 1rem;
  border: none;
  border-radius: 5px;
  background-color: #444;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.logout-button:hover {
  background-color: #666;
}
</style>
