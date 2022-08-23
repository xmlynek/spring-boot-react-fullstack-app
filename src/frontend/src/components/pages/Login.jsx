import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { Button, Form, Input } from 'antd';
import React, { useContext, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import useAxiosRequest, { State } from '../../hooks/useAxiosRequest';
import AuthContext from '../../store/auth-context';
import MainHeading from '../layout/MainHeading';
import { errorNotification, successNotification } from '../UI/Notification';

const Login = () => {
  const authCtx = useContext(AuthContext);
  const { currentUser, setCurrentUser } = authCtx;

  const [state, applyRequest] = useAxiosRequest();

  const navigate = useNavigate();
  const location = useLocation();
  const locationFrom = location.state && location.state.from ? location.state.from : null;

  useEffect(() => {
    if (currentUser) {
      navigate(locationFrom ? locationFrom : '/', { replace: true, state: location.state });
    }
  }, [currentUser, navigate, locationFrom]);

  useEffect(() => {
    if (state.status === State.SUCCESS) {
      successNotification('Login successful', 'Now you can browse and enjoy this app');
    } else if (state.status == State.ERROR) {
      errorNotification('Login failed', state.errMsg);
    }
  }, [state]);

  const onSubmitHandler = async (values) => {
    applyRequest(
      {
        url: 'api/v1/auth/login',
        withCredentials: true,
        method: 'POST',
        data: values
      },
      setCurrentUser
    );
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
          <Input.Password
            prefix={<LockOutlined className="site-form-item-icon" />}
            type="password"
            placeholder="Password"
          />
        </Form.Item>

        <div className="text-center mt-2">
          <Button
            type="primary"
            htmlType="submit"
            className="login-form-button width-responsive"
            loading={state.isLoading}>
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
