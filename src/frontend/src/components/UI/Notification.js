import { notification } from 'antd';

const openNotification = (type, message, description, duration) => {
  notification[type]({ message, description, placement: 'top', duration });
};

export const successNotification = (message, description) => {
  openNotification('success', message, description, 3.0);
};

export const errorNotification = (message, description) => {
  openNotification('error', message, description, 6.0);
};
