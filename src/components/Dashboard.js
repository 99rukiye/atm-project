import React from 'react';

const Dashboard = () => {
  const user = JSON.parse(localStorage.getItem('user'));

  return (
    <div className="container mt-5">
      <h2>Merhaba, {user?.fullName}</h2>
      <p>Email: {user?.email}</p>
      <p>Rol: {user?.role}</p>

      <button className="btn btn-success mt-3">Para Yatır</button>
      <button className="btn btn-warning mt-3 ms-2">Para Çek</button>
      <button className="btn btn-info mt-3 ms-2">Havale Yap</button>
    </div>
  );
};

export default Dashboard;
