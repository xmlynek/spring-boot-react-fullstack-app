import { LockOutlined, UserOutlined, MailOutlined } from '@ant-design/icons';
import { Button, DatePicker, Form, Input, Select } from 'antd';
import React, { useContext, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import useAxiosRequest, { State } from '../../hooks/useAxiosRequest';
import AuthContext from '../../store/auth-context';
import MainHeading from '../layout/MainHeading';
import { errorNotification, successNotification } from '../UI/Notification';

const Register = () => {
  const authCtx = useContext(AuthContext);
  const { currentUser } = authCtx;
  const [state, applyRequest] = useAxiosRequest();

  const navigate = useNavigate();
  const location = useLocation();
  const locationFrom = location.state && location.state.from ? location.state.from : null;

  useEffect(() => {
    if (state.status === State.SUCCESS) {
      successNotification(
        'Registration successful',
        'You have been successfully registered. Now Log in!'
      );
      navigate('/login', { replace: true });
    } else if (state.status == State.ERROR) {
      errorNotification('Registration failed', state.errMsg);
    }
  }, [state]);

  useEffect(() => {
    if (currentUser) {
      navigate(locationFrom ? locationFrom : '/', { replace: true, state: location.state });
    }
  }, [currentUser, navigate, locationFrom]);

  const onSubmitHandler = async (values) => {
    applyRequest({
      url: 'api/v1/auth/register',
      withCredentials: true,
      method: 'POST',
      data: values
    });
  };

  return (
    <div>
      <MainHeading title="Registration" />
      <Form
        labelCol={{ span: 5 }}
        wrapperCol={{ span: 15 }}
        name="normal_registration"
        className="login-form"
        initialValues={{ remember: true }}
        onFinish={onSubmitHandler}>
        <Form.Item
          label="First name"
          name="firstName"
          rules={[{ required: true, message: 'Please input your first name!' }]}>
          <Input
            prefix={<UserOutlined className="site-form-item-icon" />}
            placeholder="Firstname"
          />
        </Form.Item>
        <Form.Item
          label="Last name"
          name="lastName"
          rules={[{ required: true, message: 'Please input your last name!' }]}>
          <Input prefix={<UserOutlined className="site-form-item-icon" />} placeholder="Lastname" />
        </Form.Item>
        <Form.Item
          label="Email"
          name="email"
          rules={[
            {
              required: true,
              message: 'Please input your email!',
              type: 'email'
            }
          ]}>
          <Input
            prefix={<MailOutlined className="site-form-item-icon" />}
            placeholder="Email"
            type="email"
          />
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
        <Form.Item
          name="confirmPassword"
          label="Confirm Password"
          dependencies={['password']}
          hasFeedback
          rules={[
            {
              required: true,
              message: 'Please confirm your password!'
            },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || getFieldValue('password') === value) {
                  return Promise.resolve();
                }
                return Promise.reject(
                  new Error('The two passwords that you entered do not match!')
                );
              }
            })
          ]}>
          <Input.Password
            prefix={<LockOutlined className="site-form-item-icon" />}
            placeholder="Confirm password"
          />
        </Form.Item>
        <Form.Item
          label="Gender"
          name="gender"
          rules={[{ required: true, message: 'Please input your gender!' }]}>
          <Select placeholder="Gender">
            <Select.Option value="MALE">Male</Select.Option>
            <Select.Option value="FEMALE">Female</Select.Option>
            <Select.Option value="OTHER">Other</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item
          label="Birth date"
          required={true}
          name="birthDate"
          rules={[{ required: true, message: 'Please input your birth date!' }]}>
          <DatePicker style={{ width: '100%' }} />
        </Form.Item>
        <div className="text-center mt-2">
          <Button
            type="primary"
            htmlType="submit"
            className="login-form-button width-responsive"
            loading={state.isLoading}>
            Register
          </Button>
          <p className="mt-2">
            Already have an account? <Link to={'/login'}>Login</Link>
          </p>
        </div>
      </Form>
    </div>
  );
};

export default Register;
