import axios from 'axios';
import {ElMessage} from "element-plus";

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

service.interceptors.response.use(response => {
    return response.data;
}, error => {
    if (error.response && error.response.status === 401) {
        sessionStorage.removeItem('token'); // 清理脏数据
        window.location.href = '/login';    // 强制跳转
        return Promise.reject(new Error('登录已过期，请重新登录'));
    }
    return Promise.reject(error);
});

export default service;
