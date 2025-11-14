/* Copyright 2024 Kyocera Communication Systems Co., Ltd All rights reserved.*/

import axios, { AxiosResponse } from "axios";
import axiosRetry from 'axios-retry';

const MAX_REQUESTS_COUNT = 10
const INTERVAL_MS = 10
let PENDING_REQUESTS = 0
export const http = axios.create({
  baseURL: "",
  withCredentials: false,
});
axiosRetry(http, {
  retries: 5,              
  retryDelay: axiosRetry.exponentialDelay,
  retryCondition: (error) => {
    return axiosRetry.isNetworkOrIdempotentRequestError(error) || error.response?.status === 500 || error.response?.status === 504;
  },
});
http.interceptors.response.use(function (response) {
  PENDING_REQUESTS = Math.max(0, PENDING_REQUESTS - 1)
  return Promise.resolve(response)
}, function (error) {
  PENDING_REQUESTS = Math.max(0, PENDING_REQUESTS - 1)
  return Promise.reject(error)
})
http.interceptors.request.use(function (config) {
  return new Promise((resolve, reject) => {
    let interval = setInterval(() => {
      if (PENDING_REQUESTS < MAX_REQUESTS_COUNT) {
        PENDING_REQUESTS++
        clearInterval(interval)
        resolve(config)
      } 
    }, INTERVAL_MS)
  })
})
export const setupInterceptors = () => {
  http.interceptors.response.use(
    (response: AxiosResponse): AxiosResponse => {
      return response;
    },
    async (error) => {
      return Promise.reject(error);
    },
  );
};
