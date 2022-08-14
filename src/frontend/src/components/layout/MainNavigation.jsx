import { Menu } from 'antd';
import { Link } from 'react-router-dom';
import classes from './MainNavigation.module.css';

const MainNavigation = () => {
  return (
    <Menu theme="dark" mode="horizontal">
      <Menu.Item key={'homepage'} defaultChecked={true}>
        <Link to={'/home'} className="text-decoration-none">
          <span className="nav-text h6">Homepage</span>
        </Link>
      </Menu.Item>

      <Menu.Item key={'login'} className={classes.separateMenuItems}>
        <Link to={'login'} className="text-decoration-none">
          <span className="nav-text h6">Login</span>
        </Link>
      </Menu.Item>
      <Menu.Item key={'register'} className={classes.marginFromRight}>
        <Link to={'register'} className="text-decoration-none">
          <span className="nav-text h6">Register</span>
        </Link>
      </Menu.Item>
    </Menu>
  );
};

export default MainNavigation;
