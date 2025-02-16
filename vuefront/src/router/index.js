import { createRouter, createWebHistory } from 'vue-router';

const routes = [
    { path: '/init',component: ()=>import("@/pages/Init.vue") },

    {
        path: '/head',
        component: () => import('@/pages/head.vue'),
        children: [
            {
                path: '/login', // 注意这里没有 '/'
                component: () => import('@/pages/login.vue')
            },
            {
                path: '/index', // 同样，这里没有 '/'
                component: () => import('@/pages/index.vue')
            },
            {
                path: '/', // 默认子路由，当访问 '/head' 时加载此组件
                component: () => import('@/pages/index.vue')
            },
            {
                path: '/project', // 子路由
                component: () => import('@/pages/project.vue')
            },
            {
                path: '/new', // 子路由
                component: () => import('@/pages/createRpo.vue')
            },
            {
                path: '/createOri', // 子路由
                component: () => import('@/pages/createOri.vue')
            }
        ]
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;
