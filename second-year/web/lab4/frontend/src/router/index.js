import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import MainView from '../views/MainView.vue';

const routes = [
    { path: '/', name: 'Home', component: HomeView },
    { path: '/main', name: 'Main', component: MainView },
];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes,
});

export default router;