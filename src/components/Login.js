import React, { useState } from 'react';
import axios from 'axios';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', {
        email,
        password,
      });
      setMessage('Giriş başarili: ' + response.data.fullName);
    } catch (error) {
      setMessage('Giriş başarisiz: ' + (error.response?.data?.message || 'Sunucu hatasi'));
    }
  };

  return (
    <div className="container mt-5">
      <h2>Giriş Yap</h2>
      <form onSubmit={handleLogin}>
        <div className="form-group">
          <label>Email:</label>
          <input
            type="email"
            className="form-control"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="form-group mt-2">
          <label>Şifre:</label>
          <input
            type="password"
            className="form-control"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        <button className="btn btn-primary mt-3" type="submit">Giriş</button>
      </form>
      <p className="mt-3">{message}</p>
    </div>
  );
};

export default Login;
