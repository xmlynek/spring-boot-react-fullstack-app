import { Navigate, Route, Routes } from 'react-router-dom';
import React, { Suspense, useContext, useEffect } from 'react';

import PageLayout from './components/layout/PageLayout';
import Spinner from './components/UI/Spinner';
import AuthContext from './store/auth-context';
import axios from 'axios';

const Homepage = React.lazy(() => import('./components/pages/Homepage'));
const Login = React.lazy(() => import('./components/pages/Login'));
const Register = React.lazy(() => import('./components/pages/Register'));
const UserProfile = React.lazy(() => import('./components/pages/UserProfile'));

function App() {
  const { setCurrentUser } = useContext(AuthContext);
  useEffect(() => {
    axios
      .get('/api/v1/users/current-user')
      .then((res) => setCurrentUser(() => res.data))
      .catch(() => {});
  }, []);

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
