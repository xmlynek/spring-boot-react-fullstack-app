import { Navigate, Route, Routes } from 'react-router-dom';
import React, { Suspense, useContext, useEffect, useState } from 'react';

import PageLayout from './components/layout/PageLayout';
import Spinner from './components/UI/Spinner';
import AuthContext from './store/auth-context';
import axios from 'axios';
import ProtectedRoute from './components/auth/ProtectedRoute';

const Homepage = React.lazy(() => import('./components/pages/Homepage'));
const Login = React.lazy(() => import('./components/pages/Login'));
const Register = React.lazy(() => import('./components/pages/Register'));
const UserProfile = React.lazy(() => import('./components/pages/UserProfile'));
const UserListPage = React.lazy(() => import('./components/pages/UserListPage'));
const UserByIdPage = React.lazy(() => import('./components/pages/UserByIdPage'));
const ProductListPage = React.lazy(() => import('./components/pages/ProductListPage'));
const ProductByIdPage = React.lazy(() => import('./components/pages/ProductByIdPage'));

function App() {
  const [isLoading, setIsLoading] = useState(true);
  const { setCurrentUser } = useContext(AuthContext);
  useEffect(() => {
    const fetchData = async () => {
      await axios
        .get('/api/v1/users/current-user')
        .then((res) => setCurrentUser(() => res.data))
        .catch(() => {});
      setIsLoading(false);
    };
    fetchData();
  }, []);

  return isLoading ? (
    <Spinner />
  ) : (
    <PageLayout>
      <Suspense fallback={<Spinner />}>
        <Routes>
          <Route path="/" element={<Navigate to={'/home'} />} />
          <Route path="/home" element={<Homepage />} />
          <Route path="/products" element={<ProductListPage />}>
            <Route path=":productId" element={<ProductByIdPage />} />
          </Route>
          <Route
            path="/users"
            element={
              <ProtectedRoute allowedRoles={['ROLE_ADMIN']}>
                <UserListPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/users/:userId"
            element={
              <ProtectedRoute allowedRoles={['ROLE_ADMIN']}>
                <UserByIdPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <ProtectedRoute allowedRoles={['ROLE_USER']}>
                <UserProfile />
              </ProtectedRoute>
            }
          />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="*" element={<Navigate to={'/home'} />} />
        </Routes>
      </Suspense>
    </PageLayout>
  );
}

export default App;
