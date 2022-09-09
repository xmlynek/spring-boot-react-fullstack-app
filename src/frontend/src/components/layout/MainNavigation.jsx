import { Menu } from 'antd';
import { useContext, useEffect } from 'react';
import { Link } from 'react-router-dom';
import useAxiosRequest, { State } from '../../hooks/useAxiosRequest';
import AuthContext from '../../store/auth-context';
import ProtectedComponent from '../auth/ProtectedComponent';
import { errorNotification, successNotification } from '../UI/Notification';
import classes from './MainNavigation.module.css';

const MainNavigation = () => {
  const authCtx = useContext(AuthContext);
  const { currentUser, setCurrentUser } = authCtx;
  const [state, applyRequest] = useAxiosRequest();

  useEffect(() => {
    if (state.status === State.SUCCESS) {
      successNotification('Logout successful', 'Logout was successful');
      setCurrentUser(() => null);
    } else if (state.status === State.ERROR) {
      errorNotification('Logout error', 'Error occurred while logging out. Try it later');
    }
  }, [state]);

  const logoutHandler = () => {
    applyRequest({ url: '/api/v1/auth/logout', method: 'POST' });
  };

  return (
    <Menu theme="dark" mode="horizontal">
      <Menu.Item key={'homepage'} defaultChecked={true}>
        <Link to={'/home'} className="text-decoration-none">
          <span className="nav-text h6">Homepage</span>
        </Link>
      </Menu.Item>

      <ProtectedComponent allowedRoles={['ROLE_ADMIN']}>
        <Menu.Item key={'users'}>
          <Link to={'/users'} className="text-decoration-none">
            <span className="nav-text h6">Users</span>
          </Link>
        </Menu.Item>
      </ProtectedComponent>

      <Menu.Item key={'login'} className={classes.separateMenuItems}>
        <Link to={currentUser ? 'profile' : 'login'} className="text-decoration-none">
          <span className="nav-text h6">{currentUser ? 'Profile' : 'Login'}</span>
        </Link>
      </Menu.Item>
      <Menu.Item
        key={'register'}
        className={classes.marginFromRight}
        onClick={currentUser ? logoutHandler : () => {}}>
        <Link to={currentUser ? '/' : 'register'} className="text-decoration-none">
          <span className="nav-text h6">{currentUser ? 'Logout' : 'Register'}</span>
        </Link>
      </Menu.Item>
    </Menu>
  );
};

export default MainNavigation;
