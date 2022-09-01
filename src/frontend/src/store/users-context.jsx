import axios from 'axios';
import React, { useState } from 'react';
import { errorNotification, successNotification } from '../components/UI/Notification';

const UsersContext = React.createContext({
  userList: [],
  fetchUsers: () => {},
  deleteUser: () => {},
  updateUser: () => {},
  createUser: () => {},
  getUserById: () => {}
});

export const UsersContextProvider = (props) => {
  const [userList, setUserList] = useState([]);

  const fetchUsersHandler = async () => {
    await axios
      .get('/api/v1/users')
      .then((res) => setUserList(() => res.data))
      .catch((err) =>
        errorNotification(
          'Loading users',
          err.response.data.message ? err.response.data.message : err.message
        )
      );
  };

  const getUserByIdHandler = async (userId) => {
    await axios
      .get(`/api/v1/users/${userId}`)
      .then((res) => setUserList(() => [res.data]))
      .catch((err) =>
        errorNotification(
          `Loading user with id ${userId}`,
          err.response.data.message ? err.response.data.message : err.message
        )
      );
  };

  const deleteUserHandler = async (userId) => {
    await axios
      .delete(`/api/v1/users/${userId}`)
      .then(() => {
        successNotification('Delete success', 'User was successfully deleted');
      })
      .catch((err) =>
        errorNotification(
          'Delete error',
          err.response.data.message ? err.response.data.message : err.message
        )
      );
  };

  const createUserHandler = async (userData) => {
    await axios
      .post(`/api/v1/users`, userData)
      .then(() => successNotification('Create user success', 'User was successfully created'))
      .catch((err) =>
        errorNotification(
          'Create user error',
          err.response.data.message ? err.response.data.message : err.message
        )
      );
  };

  const updateUserHandler = async (userId, userData) => {
    await axios
      .put(`/api/v1/users/${userId}`, userData)
      .then(() => successNotification('Update user success', 'User was successfully updated'))
      .catch((err) =>
        errorNotification(
          'Update user error',
          err.response.data.message ? err.response.data.message : err.message
        )
      );
  };

  return (
    <UsersContext.Provider
      value={{
        userList: userList,
        createUser: createUserHandler,
        deleteUser: deleteUserHandler,
        fetchUsers: fetchUsersHandler,
        updateUser: updateUserHandler,
        getUserById: getUserByIdHandler
      }}>
      {props.children}
    </UsersContext.Provider>
  );
};

export default UsersContext;
