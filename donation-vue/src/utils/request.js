import axios from 'axios';
import {ElMessage, ElMessageBox} from "element-plus";

const service = axios.create({
    baseURL: 'http://localhost:8080', // 后端地址
    timeout: 5000,
});

service.interceptors.request.use(config => {
    // 如果有 token，可以加在 header
    const token = sessionStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

service.interceptors.response.use(
    response => {
        // 后端统一返回 Result 对象，都在 response.data 里
        const res = response.data;

        // 1. 如果 code 是 200，说明业务成功，直接返回 res 本身（包含 code 和 data）
        if (res.code === "200") {
            return res;
        }

        // 2. 如果 code 是 401（未登录或过期），跳转登录
        if (res.code === "401") {
            sessionStorage.removeItem('token');
            ElMessage.error('登录已过期，请重新登录');
            window.location.href = '/login';
            return Promise.reject(new Error('Unauthorized'));
        }

        // 3. 处理业务错误（如 400 用户被禁用，403 无权限）
        const errorMsg = res.msg || res.message || '系统异常';

        if (res.code === "400" && errorMsg.includes('禁用')) {
            ElMessageBox.alert(errorMsg, '账号状态异常', {
                confirmButtonText: '确定',
                type: 'warning'
            });
        } else {
            ElMessage.error(errorMsg);
        }

        // 返回一个 reject，这样页面里的 try-catch 就能捕获并停止后续逻辑
        return Promise.reject(new Error(errorMsg));
    },
    error => {
        // 这里处理的是 HTTP 级别的错误（比如 500 服务器崩溃或网络断开）
        console.error('HTTP Error:', error);
        ElMessage.error(error.response?.data?.message || '网络连接失败，请检查服务器');
        return Promise.reject(error);
    }
);

export default service;
