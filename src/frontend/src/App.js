import Login from './components/pages/Login';
import { Register } from './components/pages/Register';
import { Navigate, Route, Routes } from 'react-router-dom';

import React from 'react';
import PageLayout from './components/layout/PageLayout';
import Homepage from './components/pages/Homepage';

function App() {
  return (
    <PageLayout>
      <Routes>
        <Route path="/" element={<Navigate to={'/home'} />} />
        <Route path="/home" element={<Homepage />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="*" element={<Navigate to={'/home'} />} />
      </Routes>
    </PageLayout>
  );
}

export default App;
