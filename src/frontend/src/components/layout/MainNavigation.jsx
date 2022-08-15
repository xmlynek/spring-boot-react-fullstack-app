import { Menu } from 'antd';
import { useContext } from 'react';
import { Link } from 'react-router-dom';
import AuthContext from '../../store/auth-context';
import classes from './MainNavigation.module.css';

const MainNavigation = () => {
  const authCtx = useContext(AuthContext);
  const { currentUser } = authCtx;

  return (
    <Menu theme="dark" mode="horizontal">
      <Menu.Item key={'homepage'} defaultChecked={true}>
        <Link to={'/home'} className="text-decoration-none">
          <span className="nav-text h6">Homepage</span>
        </Link>
      </Menu.Item>

      <Menu.Item key={'login'} className={classes.separateMenuItems}>
        <Link to={currentUser ? 'profile' : 'login'} className="text-decoration-none">
          <span className="nav-text h6">{currentUser ? 'Profile' : 'Login'}</span>
        </Link>
      </Menu.Item>
      <Menu.Item key={'register'} className={classes.marginFromRight}>
        <Link to={'register'} className="text-decoration-none">
          <span className="nav-text h6">{currentUser ? 'Logout' : 'Register'}</span>
        </Link>
      </Menu.Item>
    </Menu>
  );
};

export default MainNavigation;
