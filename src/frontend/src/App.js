import { Navigate, Route, Routes } from 'react-router-dom';
import React, { Suspense } from 'react';

import PageLayout from './components/layout/PageLayout';
import Spinner from './components/UI/Spinner';

const Homepage = React.lazy(() => import('./components/pages/Homepage'));
const Login = React.lazy(() => import('./components/pages/Login'));
const Register = React.lazy(() => import('./components/pages/Register'));
const UserProfile = React.lazy(() => import('./components/pages/UserProfile'));

function App() {
  return (
    <PageLayout>
      <Suspense fallback={<Spinner />}>
        <Routes>
          <Route path="/" element={<Navigate to={'/home'} />} />
          <Route path="/home" element={<Homepage />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/profile" element={<UserProfile />} />
          <Route path="*" element={<Navigate to={'/home'} />} />
        </Routes>
      </Suspense>
    </PageLayout>
  );
}

export default App;
