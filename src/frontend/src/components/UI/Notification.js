import { notification } from 'antd';

const openNotification = (type, message, description) => {
  notification[type]({ message, description, placement: 'top' });
};

export const successNotification = (message, description) => {
  openNotification('success', message, description);
};

export const errorNotification = (message, description) => {
  openNotification('error', message, description);
};
