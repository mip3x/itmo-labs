<template>
  <div class="home-view">
    <div class="background">
      <div class="title">
        <h1>Веб №4</h1>
      </div>
      <div class="menu">
        <div class="button disabled">ФИО: Иван Иванов</div>
        <div class="button disabled">Номер группы: 12345</div>
        <div class="button disabled">Вариант: 4</div>
        <LoginForm />
      </div>
    </div>
  </div>
</template>

<script>
import LoginForm from '../components/LoginForm.vue';

export default {
  components: { LoginForm },
  beforeRouteEnter(to, from, next) {
    document.body.classList.add('home-view-body');
    next();
  },
  beforeRouteLeave(to, from, next) {
    document.body.classList.remove('home-view-body');
    next();
  },
};
</script>

<style scoped>
@import '../styles/index.css';
</style>
