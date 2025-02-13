import { createRouter, createWebHistory } from 'vue-router';

const routes = [
    { path: '/init',component: ()=>import("@/pages/Init.vue") },
    { path: '/login',component: ()=>import("@/pages/login.vue") },
    { path: '/index',component: ()=>import("@/pages/index.vue") },
    { path: '/',component: ()=>import("@/pages/index.vue") },
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;
