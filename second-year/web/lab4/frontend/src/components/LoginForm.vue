<template>
  <form @submit.prevent="login" class="login-form">
    <input v-model="username" class="input-field" placeholder="Логин" required />
    <input v-model="password" type="password" class="input-field" placeholder="Пароль" required />
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