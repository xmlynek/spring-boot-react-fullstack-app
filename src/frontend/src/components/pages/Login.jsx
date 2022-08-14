import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { Button, Form, Input } from 'antd';
import React from 'react';
import { Link } from 'react-router-dom';
import MainHeading from '../layout/MainHeading';

const Login = () => {
  const onFinish = (values) => {
    console.log('Success:', values);
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
        onFinish={onFinish}>
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
