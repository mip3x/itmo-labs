<template>
  <form @submit.prevent="login">
    <input v-model="username" placeholder="Логин" required />
    <input v-model="password" type="password" placeholder="Пароль" required />
    <button type="submit" class="button active">Войти</button>
  </form>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      password: '',
    };
  },
  methods: {
    login() {
      this.$router.push('/main');
    },
  },
};
</script>
