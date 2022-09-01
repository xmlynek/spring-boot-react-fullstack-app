import { LockOutlined, UserOutlined, MailOutlined } from '@ant-design/icons';
import { Button, Checkbox, DatePicker, Form, Input, Select } from 'antd';
import moment from 'moment';
import { useEffect } from 'react';

const UserForm = (props) => {
  const [form] = Form.useForm();
  let initialValues = {};

  if (props.userData) {
    initialValues = {
      firstName: props.userData.firstName,
      lastName: props.userData.lastName,
      email: props.userData.email,
      gender: props.userData.gender,
      birthDate: moment(props.userData.birthDate, 'YYYY-MM-DD'),
      isEnabled: props.userData.isEnabled,
      roles: props.userData.roles
    };
  }

  if (props.userRolesField && !props.userData) {
    initialValues.roles = ['ROLE_USER'];
  }

  useEffect(() => {
    form.setFieldsValue(initialValues);
  }, [form, initialValues]);

  return (
    <>
      <Form
        form={form}
        labelCol={{ span: 5 }}
        wrapperCol={{ span: 15 }}
        initialValues={initialValues}
        className="login-form"
        onFinish={(values) => {
          props.onSubmit(values);
        }}>
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
        {props.passwordField && (
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
        )}
        {props.confirmPasswordField && (
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
        )}
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
          <DatePicker style={{ width: '100%' }} name="birthDate" />
        </Form.Item>
        {props.isEnabledField && (
          <Form.Item label="isEnabled" name="isEnabled">
            <Checkbox
              name="isEnabled"
              checked={initialValues.isEnabled ? initialValues.isEnabled : false}
            />
          </Form.Item>
        )}
        {props.userRolesField && (
          <Form.Item
            label="Roles"
            name="roles"
            rules={[{ required: true, message: 'Please, select roles' }]}>
            <Select mode="multiple" placeholder="Roles" name={'roles'}>
              <Select.Option value="ROLE_USER" selected disabled>
                ROLE_USER
              </Select.Option>
              <Select.Option value="ROLE_ADMIN">ROLE_ADMIN</Select.Option>
            </Select>
          </Form.Item>
        )}

        <div className="text-center mt-2">
          <Button
            type="primary"
            htmlType="submit"
            className="login-form-button width-responsive"
            loading={false}>
            {props.submitBtnTitle ? props.submitBtnTitle : 'Save'}
          </Button>
        </div>
      </Form>
    </>
  );
};

export default UserForm;
