<template>
  <div>
    <form @submit.prevent="login" class="login-form">
      <input v-model="username" class="input-field" placeholder="Логин" required />
      <input v-model="password" type="password" class="input-field" placeholder="Пароль" required />
      <button type="submit" class="button active">Войти</button>
      <button type="button" @click="register" class="button active">Регистрация</button>
    </form>

    <div v-if="notification.visible" :class="['toast', notification.type]">
      {{ notification.message }}
    </div>
  </div>
</template>

<script>
import axios from "axios";
export default {
  data() {
    return {
      username: '',
      password: '',
      errorMessage: '',
      notification: {
        visible: false,
        message: '',
        type: '',
      },
    };
  },
  methods: {
    showNotification(message, type) {
      this.notification.message = message;
      this.notification.type = type;
      this.notification.visible = true;

      // 3 sec timeout for notification
      setTimeout(() => {
        this.notification.visible = false;
      }, 3000);
    },
    async login() {
      try {
        const response = await axios.post('/api/auth/login', {
          username: this.username,
          password: this.password,
        });
        this.showNotification(response.data.message, 'success');
        this.$router.push('/main');
      } catch (error) {
        this.errorMessage =
            error.response?.data?.message || 'Ошибка входа. Проверьте данные.';
        this.showNotification(this.errorMessage, 'error');
      }
    },
    async register() {
      try {
        const response = await axios.post('/api/auth/register', {
          username: this.username,
          password: this.password,
        });
        this.showNotification(response.data.message, 'success');
        this.$router.push('/main');
      } catch (error) {
        this.errorMessage =
            error.response?.data?.message ||
            'Ошибка регистрации. Пользователь уже существует.';
        this.showNotification(this.errorMessage, 'error');
      }
    },
  },
};
</script>

<style scoped>
.toast {
  position: fixed;
  bottom: 20px;
  right: 20px;
  padding: 15px 20px;
  border-radius: 8px;
  font-size: 1rem;
  color: #fff;
  animation: fade-in-out 3s;
}

.toast.success {
  background-color: #4caf50;
}

.toast.error {
  background-color: #f44336;
}

@keyframes fade-in-out {
  0% {
    opacity: 0;
    transform: translateY(20px);
  }
  10% {
    opacity: 1;
    transform: translateY(0);
  }
  90% {
    opacity: 1;
    transform: translateY(0);
  }
  100% {
    opacity: 0;
    transform: translateY(20px);
  }
}
</style>
