import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { Button, Form, Input } from 'antd';
import axios from 'axios';
import React, { useContext, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import AuthContext from '../../store/auth-context';
import MainHeading from '../layout/MainHeading';

const Login = () => {
  const authCtx = useContext(AuthContext);
  const { currentUser, setCurrentUser } = authCtx;

  const navigate = useNavigate();
  const location = useLocation();
  const locationFrom = location.state && location.state.from ? location.state.from : null;

  useEffect(() => {
    if (currentUser) {
      navigate(locationFrom ? locationFrom : '/', { replace: true, state: location.state });
    }
  }, [currentUser, navigate, locationFrom]);

  const onSubmitHandler = async (values) => {
    axios
      .post('http://localhost:8080/api/v1/auth/login', values)
      .then((res) => {
        console.log(res);
        setCurrentUser(() => res.data);
        navigate(locationFrom ? locationFrom : '/', { replace: true });
      })
      .catch(console.log);
  };

  return (
    <>
      <MainHeading title="Login" />
      <Form
        labelCol={{ span: 5 }}
        wrapperCol={{ span: 15 }}
        name="normal_login"
        className="login-form"
        initialValues={{ remember: true }}
        onFinish={onSubmitHandler}>
        <Form.Item
          label="Email"
          name="username"
          rules={[{ required: true, message: 'Please input your Email!' }]}>
          <Input prefix={<UserOutlined className="site-form-item-icon" />} placeholder="Email" />
        </Form.Item>
        <Form.Item
          label="Password"
          name="password"
          rules={[{ required: true, message: 'Please input your Password!' }]}>
          <Input
            prefix={<LockOutlined className="site-form-item-icon" />}
            type="password"
            placeholder="Password"
          />
        </Form.Item>

        <div className="text-center mt-2">
          <Button type="primary" htmlType="submit" className="login-form-button width-responsive">
            Log in
          </Button>
          <p className="mt-2">
            Don&apos;t have an account yet? <Link to={'/register'}>Register</Link>
          </p>
        </div>
      </Form>
    </>
  );
};

export default Login;
