<template>
  <div class="main-view">
    <div class="header">
      <span v-if="username" class="username">{{ username }}</span>
      <button class="logout-button" @click="logout">Выйти</button>
    </div>
    <div class="container">
      <PlotArea :radius="radius" :points="points" @add-point="addPoint" />
      <div class="panel-gap"></div>
      <div>
        <PointForm @add-point="addPoint" @update-radius="updateRadius" />
      </div>
    </div>
    <ResultsTable :points="points" />
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
      username: null,
      radius: 100,
      points: [],
    };
  },

  mounted() {
    this.fetchUsername();
    this.fetchPoints();
  },

  methods: {
    logout() {
      localStorage.removeItem('authToken');
      this.$router.push('/');
    },

    async fetchPoints() {
      const token = localStorage.getItem("authToken");
      if (!token) {
        alert("Вы не авторизованы!");
        return;
      }

      try {
        const response = await fetch("/api/plot/points", {
          method: "GET",
          headers: {
            Authorization: token,
            "Content-Type": "application/json",
          },
        });

        if (response.ok) {
          const data = await response.json();
          this.points = data.map((point) => ({
            x: point.x,
            y: point.y,
            radius: point.r,
            result: point.result,
            color: point.result ? "green" : "red",
          }));
        } else {
          console.error("Ошибка загрузки точек:", await response.text());
        }
      } catch (error) {
        console.error("Ошибка подключения к серверу:", error);
      }
    },

    async addPoint(point) {
      const token = localStorage.getItem("authToken");
      if (!token) {
        alert("Вы не авторизованы!");
        return;
      }

      try {
        const response = await fetch("/api/plot/check", {
          method: "POST",
          headers: {
            Authorization: token,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            x: point.x,
            y: point.y,
            radius: this.radius,
          }),
        });

        const data = await response.json();

        if (response.ok) {
          this.points.push({
            x: point.x,
            y: point.y,
            radius: this.radius,
            result: data.result,
            color: data.result ? "green" : "red",
          });
        } else {
          console.error("Ошибка проверки точки:", data.message);
          this.points.push({
            x: point.x,
            y: point.y,
            radius: this.radius,
            color: "orange",
          });
        }
      } catch (error) {
        console.error("Ошибка подключения к серверу:", error);
        this.points.push({
          x: point.x,
          y: point.y,
          radius: this.radius,
          color: "orange",
        });
      }
    },

    updateRadius(newRadius) {
      this.radius = newRadius;
    },

    async fetchUsername() {
      const token = localStorage.getItem('authToken');
      if (!token) {
        this.username = 'Гость';
        return;
      }

      try {
        const response = await fetch('/api/auth/session', {
          method: 'GET',
          headers: {
            Authorization: token,
            'Content-Type': 'application/json',
          },
        });

        if (!response.ok) {
          this.username = 'Гость';
          return;
        }

        const data = await response.json();
        this.username = data.username;
      } catch (error) {
        console.error('Ошибка при загрузке имени пользователя: ', error);
        this.username = 'Гость';
      }
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

.header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 0.8rem;
  position: fixed;
  top: 0;
  right: 0;
  z-index: 1000;
}

.username {
  margin-right: 0.8rem;
  font-size: 1rem;
  color: white;
}

.logout-button {
  padding: 0.8rem 1rem;
  font-size: 1rem;
  border: none;
  border-radius: 0.4rem;
  background-color: #444;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.logout-button:hover {
  background-color: #666;
}
</style>
