import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth';

export const loginUser = (userData) => {
  return axios.post(`${API_URL}/login`, userData);
};
