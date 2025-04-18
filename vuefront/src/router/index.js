import { createRouter, createWebHistory } from 'vue-router';

const routes = [
    { path: '/init',component: ()=>import("@/pages/Init.vue") },
    {
        path: '/login', // 注意这里没有 '/'
        component: () => import('@/pages/login.vue')
    },

    {
        path: '/head',
        component: () => import('@/pages/head.vue'),
        children: [

            {
                path: '/gitHeader', // 注意这里没有 '/'
                component: () => import('@/pages/gitHeader.vue'),
                children: [
                    {
                        path: '/project', // 子路由
                        component: () => import('@/pages/project.vue')
                    },
                    {
                        path: '/houseSetting', // 子路由
                        component: () => import('@/pages/houseSetting.vue')
                    },
                    {
                        path: '/history', // 子路由
                        component: () => import('@/pages/history.vue')
                    },
                    {
                        path: '/wiki', // 子路由
                        component: () => import('@/pages/wiki.vue')
                    },
                    {
                        path: '/pull', // 子路由
                        component: () => import('@/pages/pullr.vue')
                    },
                    {
                        path: '/issues', // 子路由
                        component: () => import('@/pages/issues.vue')
                    },
                    {
                        path: '/commit', // 子路由
                        component: () => import('@/pages/commit.vue')
                    },
                ]
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
                path: '/new', // 子路由
                component: () => import('@/pages/createRpo.vue')
            },
            {
                path: '/createOri', // 子路由
                component: () => import('@/pages/createOri.vue')
            },
            {
                path: '/search', // 子路由
                component: () => import('@/pages/search.vue')
            },
            {
                path: '/slider', // 子路由
                component: () => import('@/pages/Slider.vue'),
                children:[
                    {
                        path: "/account",
                        component: () => import('@/pages/account.vue')
                    },
                    {
                        path: "/info",
                        component: () => import('@/pages/info.vue')
                    },
                    {
                        path: "/myOri",
                        component: () => import('@/pages/MyOri.vue')
                    },
                    {
                        path: "/myRep",
                        component: () => import('@/pages/myRep.vue')
                    },
                    {
                        path: "/ssh",
                        component: () => import('@/pages/ssh.vue')
                    },
                    {
                        path: "/user",
                        component: () => import('@/pages/users.vue')
                    }
                ]
            }
        ]
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

const  whiteList = ['/login', '/init',"/index","/"];
router.beforeEach((to, from, next) => {
    const  user= localStorage.getItem("user");
    var isLogin=false
    if(JSON.parse(user)) isLogin=true
    if (whiteList.includes(to.path)) {
        // 如果目标路由在白名单内，直接放行
        next();
    } else {
        // 如果不在白名单内但已登录，继续导航
        if (isLogin) {
            next();
        } else {
            // 否则重定向到登录页面
            next('/login');
        }
    }
});
export default router;
